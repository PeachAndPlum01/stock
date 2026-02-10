<template>
  <div class="star-observatory">
    <!-- 左侧：关系图谱 -->
    <div class="graph-panel">
      <div class="star-panel-header">
        <span class="star-title">产业链关系图谱</span>
        <div class="star-legend">
          <span class="dot core"></span>核心企业
          <span class="dot related"></span>关联企业
        </div>
      </div>
      <div ref="chartRef" class="chart-container"></div>
    </div>

    <!-- 右侧：分析与对话 -->
    <div class="analysis-panel">
      <div class="star-panel-header">
        <span class="star-title">智能分析</span>
      </div>
      
      <div class="chat-container" ref="chatContainerRef">
        <!-- 初始分析内容 -->
        <div class="message ai-message" v-if="initialAnalysis">
          <div class="avatar">AI</div>
          <div class="content" v-html="formatText(initialAnalysis)"></div>
        </div>

        <!-- 对话记录 -->
        <div v-for="(msg, index) in chatHistory" :key="index" 
             class="message" :class="msg.type === 'user' ? 'user-message' : 'ai-message'">
          <div class="avatar">{{ msg.type === 'user' ? 'Me' : 'AI' }}</div>
          <div class="content" v-html="formatText(msg.content)"></div>
        </div>
        
        <div v-if="loading" class="message ai-message">
          <div class="avatar">AI</div>
          <div class="content typing">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>

      <div class="input-area">
        <input 
          v-model="inputQuestion" 
          @keyup.enter="handleSend"
          placeholder="输入您的问题，例如：宁德时代和特斯拉是什么关系？"
          :disabled="loading"
        />
        <button @click="handleSend" :disabled="loading || !inputQuestion.trim()">
          <el-icon><Position /></el-icon>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getStarGraph, getStarAnalysis, chatWithStar } from '@/api'
import { Position } from '@element-plus/icons-vue'

const chartRef = ref(null)
const chatContainerRef = ref(null)
let chartInstance = null

const initialAnalysis = ref('')
const chatHistory = ref([])
const inputQuestion = ref('')
const loading = ref(false)

// 格式化文本（简单 Markdown 处理）
const formatText = (text) => {
  if (!text) return ''
  // 简单处理换行和加粗
  return text
    .replace(/\n/g, '<br/>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
}

const initChart = (data) => {
  if (!chartRef.value) return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(chartRef.value)
  
  const idToName = {}
  data.nodes.forEach(node => {
    if (node.id && node.name) {
      idToName[node.id] = node.name
    }
  })

  const option = {
    backgroundColor: '#0B0E11',
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        if (params.dataType === 'node') {
          return `${params.data.name} (${params.data.stockCode})<br/>市值权重: ${params.data.value}`
        } else {
          return `${params.data.source} -> ${params.data.target}<br/>关系: ${params.data.relation}`
        }
      }
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        data: data.nodes.map(node => ({
          // 移除 id 属性，强制使用 name 进行连接匹配
          name: node.name,
          stockCode: node.symbol, // 存储股票代码供 tooltip 使用
          value: node.value,
          symbolSize: node.category === 0 ? 70 : 45,
          itemStyle: {
            // 扁平化：使用纯色，移除径向渐变和阴影
            color: node.category === 0 ? '#2962FF' : '#00C853',
            borderColor: '#0B0E11',
            borderWidth: 2
          },
          label: {
            show: true,
            color: '#EAECEF',
            fontSize: node.category === 0 ? 14 : 12,
            fontWeight: node.category === 0 ? '600' : 'normal'
          }
        })),
        links: data.links.map(link => {
          // 确保 source 和 target 转换为对应的中文名称
          const sourceName = idToName[link.source] || link.source
          const targetName = idToName[link.target] || link.target
          return {
            source: sourceName,
            target: targetName,
            relation: link.relation,
            lineStyle: {
              width: 1,
              curveness: 0,
              color: '#474D57',
              opacity: 0.4
            }
          }
        }),
        roam: true,
        zoom: 0.7, // 初始缩放比例，避免节点太大占满屏幕
        label: {
          position: 'right',
          distance: 8
        },
        force: {
          repulsion: 2000,
          edgeLength: [150, 300],
          gravity: 0.1
        },
        edgeSymbol: ['none', 'arrow'],
        edgeSymbolSize: [4, 8],
        lineStyle: {
          color: '#474D57',
          opacity: 0.4,
          curveness: 0
        },
        emphasis: {
          focus: 'adjacency',
          lineStyle: {
            width: 2,
            opacity: 1
          },
          itemStyle: {
            borderColor: '#fff',
            borderWidth: 2
          }
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
  
  // 窗口大小改变时重绘
  window.addEventListener('resize', () => chartInstance.resize())
}

const loadData = async () => {
  try {
    const [graphRes, analysisRes] = await Promise.all([
      getStarGraph(),
      getStarAnalysis()
    ])
    
    if (graphRes && graphRes.data) {
      // 兼容 Result 包装结构
      const graphData = graphRes.data.data || graphRes.data
      if (graphData && graphData.nodes) {
        initChart(graphData)
      }
    }
    
    if (analysisRes && analysisRes.data) {
      // 兼容 Result 包装结构
      const analysisData = analysisRes.data.data || analysisRes.data
      if (analysisData && analysisData.content) {
        initialAnalysis.value = analysisData.content
      }
    }
  } catch (error) {
    console.error('Failed to load star data:', error)
  }
}

const handleSend = async () => {
  const q = inputQuestion.value.trim()
  if (!q || loading.value) return
  
  // 添加用户消息
  chatHistory.value.push({ type: 'user', content: q })
  inputQuestion.value = ''
  loading.value = true
  scrollToBottom()
  
  try {
    const res = await chatWithStar(q)
    // 兼容 Result 包装结构
    const data = res.data && (res.data.data || res.data)
    
    if (data && data.answer) {
      chatHistory.value.push({ type: 'ai', content: data.answer })
    } else {
      // 如果没有获取到有效答案
      chatHistory.value.push({ type: 'ai', content: '抱歉，我没有理解您的问题，请换个方式提问。' })
    }
  } catch (error) {
    chatHistory.value.push({ type: 'ai', content: '抱歉，我暂时无法回答这个问题。' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainerRef.value) {
      chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight
    }
  })
}

onMounted(() => {
  loadData()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<style scoped>
.star-observatory {
  flex: 1;
  display: flex;
  height: 100%;
  width: 100%;
  background: #0B0E11;
  color: #EAECEF;
}

.graph-panel {
  flex: 3;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #1E2329;
}

.analysis-panel {
  flex: 2;
  display: flex;
  flex-direction: column;
  background: #0B0E11;
  min-width: 350px;
}

.star-panel-header {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border-bottom: 1px solid #1E2329;
  background: #0B0E11;
}

.star-title {
  font-size: 15px;
  font-weight: 600;
  color: #EAECEF;
}

.star-legend {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #848E9C;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
  display: inline-block;
  margin-right: 6px;
}

.dot.core { background: #2962FF; }
.dot.related { background: #00C853; }

.chart-container {
  flex: 1;
  width: 100%;
  height: 100%;
  min-height: 400px;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 90%;
}

.message.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 4px;
  background: #1E2329;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  flex-shrink: 0;
  color: #848E9C;
  border: none;
}

.user-message .avatar {
  background: #2962FF;
  color: #fff;
}

.content {
  background: #1E2329;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  color: #EAECEF;
}

.user-message .content {
  background: #2962FF;
  color: #fff;
  border-radius: 8px;
}

.input-area {
  padding: 16px;
  border-top: 1px solid #1E2329;
  display: flex;
  gap: 10px;
  background: #0B0E11;
}

input {
  flex: 1;
  background: #15181C;
  border: 1px solid #2B3139;
  border-radius: 4px;
  padding: 10px 14px;
  color: #fff;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

input:focus {
  border-color: #2962FF;
  background: #0B0E11;
}

button {
  width: 42px;
  height: 42px;
  border-radius: 4px;
  background: #2962FF;
  border: none;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

button:hover {
  background: #1E4BD8;
}

button:disabled {
  background: #2B3139;
  color: #5E6673;
  cursor: not-allowed;
}

.typing span {
  display: inline-block;
  width: 6px;
  height: 6px;
  background: #848E9C;
  border-radius: 50%;
  margin: 0 2px;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing span:nth-child(1) { animation-delay: -0.32s; }
.typing span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}
</style>
