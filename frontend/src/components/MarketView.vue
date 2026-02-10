<template>
  <div class="market-view-container">
    <!-- 顶部：主要指数 -->
    <div class="indices-section">
      <div v-for="index in indices" :key="index.code" class="index-card" :class="index.change >= 0 ? 'bg-up' : 'bg-down'">
        <div class="index-header">
          <span class="index-name">{{ index.name }}</span>
          <span class="index-code">{{ index.code }}</span>
        </div>
        <div class="index-content">
          <span class="index-value" :class="getChangeClass(index.change)">{{ index.value.toFixed(2) }}</span>
          <div class="index-changes">
            <span :class="getChangeClass(index.change)">{{ index.change >= 0 ? '+' : '' }}{{ index.changeAmount.toFixed(2) }}</span>
            <span :class="getChangeClass(index.change)">{{ index.change >= 0 ? '+' : '' }}{{ index.change }}%</span>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <!-- 左侧：排行榜 -->
      <div class="left-column">
        <!-- 个股排行 -->
        <div class="panel stock-ranking">
          <div class="panel-header">
            <div class="tabs">
              <span 
                v-for="tab in stockTabs" 
                :key="tab.key" 
                class="tab-item" 
                :class="{ active: currentStockTab === tab.key }"
                @click="currentStockTab = tab.key"
              >
                {{ tab.name }}
              </span>
            </div>
            <span class="more-link">更多 ></span>
          </div>
          <div class="ranking-list">
            <div class="list-header">
              <span class="col-rank">排名</span>
              <span class="col-name">名称</span>
              <span class="col-price">最新价</span>
              <span class="col-change">涨跌幅</span>
              <span class="col-amount">成交额</span>
            </div>
            <div v-for="(stock, index) in currentStockList" :key="stock.code" class="list-item">
              <span class="col-rank" :class="{ 'top-3': index < 3 }">{{ index + 1 }}</span>
              <span class="col-name">
                <div class="name">{{ stock.name }}</div>
                <div class="code">{{ stock.code }}</div>
              </span>
              <span class="col-price" :class="getChangeClass(stock.change)">{{ stock.price.toFixed(2) }}</span>
              <span class="col-change" :class="getChangeClass(stock.change)">{{ stock.change >= 0 ? '+' : '' }}{{ stock.change }}%</span>
              <span class="col-amount">{{ stock.amount }}</span>
            </div>
          </div>
        </div>

        <!-- 板块排行 -->
        <div class="panel sector-ranking">
          <div class="panel-header">
            <div class="tabs">
              <span 
                class="tab-item" 
                :class="{ active: currentSectorTab === 'industry' }"
                @click="currentSectorTab = 'industry'"
              >
                行业板块
              </span>
              <span 
                class="tab-item" 
                :class="{ active: currentSectorTab === 'concept' }"
                @click="currentSectorTab = 'concept'"
              >
                概念板块
              </span>
            </div>
            <span class="more-link">更多 ></span>
          </div>
          <div class="sector-grid">
            <div v-for="sector in currentSectorList" :key="sector.name" class="sector-box">
              <div class="sector-title">
                <span>{{ sector.name }}</span>
                <span :class="getChangeClass(sector.change)">{{ sector.change >= 0 ? '+' : '' }}{{ sector.change }}%</span>
              </div>
              <div class="sector-leader">
                <span class="leader-name">{{ sector.topStock.name }}</span>
                <span class="leader-price" :class="getChangeClass(sector.topStock.change)">{{ sector.topStock.price }}</span>
                <span class="leader-change" :class="getChangeClass(sector.topStock.change)">+{{ sector.topStock.change }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：市场数据 -->
      <div class="right-column">
        <!-- 市场情绪 -->
        <div class="panel sentiment-panel">
          <div class="panel-header">
            <span>市场情绪</span>
          </div>
          <div class="sentiment-content">
            <div class="up-down-dist">
              <div class="dist-item up">
                <span class="label">涨</span>
                <div class="bar-wrapper">
                  <div class="bar" :style="{ height: (sentiment.upCount / 5000 * 100) + '%' }"></div>
                  <span class="count">{{ sentiment.upCount }}</span>
                </div>
              </div>
              <div class="dist-item flat">
                <span class="label">平</span>
                <div class="bar-wrapper">
                  <div class="bar" :style="{ height: (sentiment.flatCount / 5000 * 100) + '%' }"></div>
                  <span class="count">{{ sentiment.flatCount }}</span>
                </div>
              </div>
              <div class="dist-item down">
                <span class="label">跌</span>
                <div class="bar-wrapper">
                  <div class="bar" :style="{ height: (sentiment.downCount / 5000 * 100) + '%' }"></div>
                  <span class="count">{{ sentiment.downCount }}</span>
                </div>
              </div>
            </div>
            <div class="limit-info">
              <div class="limit-item">
                <span class="label">涨停</span>
                <span class="value text-up">{{ sentiment.limitUp }}家</span>
              </div>
              <div class="limit-item">
                <span class="label">跌停</span>
                <span class="value text-down">{{ sentiment.limitDown }}家</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 资金流向 -->
        <div class="panel money-flow-panel">
          <div class="panel-header">
            <span>资金流向</span>
          </div>
          <div class="flow-list">
            <div class="flow-item">
              <span class="label">北向资金</span>
              <span class="value" :class="getChangeClass(moneyFlow.north)">{{ moneyFlow.north > 0 ? '+' : '' }}{{ moneyFlow.north }}亿</span>
            </div>
            <div class="flow-item">
              <span class="label">主力净流</span>
              <span class="value" :class="getChangeClass(moneyFlow.main)">{{ moneyFlow.main > 0 ? '+' : '' }}{{ moneyFlow.main }}亿</span>
            </div>
            <div class="flow-item">
              <span class="label">南向资金</span>
              <span class="value" :class="getChangeClass(moneyFlow.south)">{{ moneyFlow.south > 0 ? '+' : '' }}{{ moneyFlow.south }}亿</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { TrendCharts, DataAnalysis, PieChart } from '@element-plus/icons-vue'

// 辅助函数：获取涨跌颜色类名 (红涨绿跌)
const getChangeClass = (val) => {
  if (val > 0) return 'text-up'
  if (val < 0) return 'text-down'
  return ''
}

const indices = ref([
  { name: '上证指数', code: '000001', value: 3050.12, changeAmount: 15.23, change: 0.50 },
  { name: '深证成指', code: '399001', value: 9850.45, changeAmount: -23.12, change: -0.23 },
  { name: '创业板指', code: '399006', value: 1950.33, changeAmount: 10.50, change: 0.54 },
  { name: '科创50', code: '000688', value: 850.20, changeAmount: 8.12, change: 0.96 }
])

const stockTabs = [
  { name: '涨幅榜', key: 'up' },
  { name: '跌幅榜', key: 'down' },
  { name: '成交额', key: 'amount' }
]
const currentStockTab = ref('up')

const stockData = {
  up: [
    { name: '中科曙光', code: '603019', price: 45.23, change: 10.01, amount: '56.2亿' },
    { name: '浪潮信息', code: '000977', price: 38.50, change: 9.98, amount: '42.1亿' },
    { name: '工业富联', code: '601138', price: 22.15, change: 8.50, amount: '35.8亿' },
    { name: '紫光股份', code: '000938', price: 25.60, change: 7.20, amount: '28.5亿' },
    { name: '中兴通讯', code: '000063', price: 32.40, change: 6.80, amount: '31.2亿' }
  ],
  down: [
    { name: 'ST左江', code: '300799', price: 18.50, change: -20.00, amount: '1.2亿' },
    { name: '宁德时代', code: '300750', price: 180.20, change: -3.50, amount: '45.6亿' },
    { name: '贵州茅台', code: '600519', price: 1650.00, change: -1.20, amount: '68.2亿' },
    { name: '五粮液', code: '000858', price: 145.30, change: -1.50, amount: '25.3亿' },
    { name: '迈瑞医疗', code: '300760', price: 280.50, change: -2.10, amount: '15.8亿' }
  ],
  amount: [
    { name: '贵州茅台', code: '600519', price: 1650.00, change: -1.20, amount: '68.2亿' },
    { name: '中科曙光', code: '603019', price: 45.23, change: 10.01, amount: '56.2亿' },
    { name: '宁德时代', code: '300750', price: 180.20, change: -3.50, amount: '45.6亿' },
    { name: '浪潮信息', code: '000977', price: 38.50, change: 9.98, amount: '42.1亿' },
    { name: '赛力斯', code: '601127', price: 88.50, change: 4.50, amount: '38.9亿' }
  ]
}

const currentStockList = computed(() => stockData[currentStockTab.value])

const currentSectorTab = ref('industry')

const industrySectors = [
  { name: '半导体', change: 2.8, topStock: { name: '中芯国际', price: 48.5, change: 5.6 } },
  { name: '通信设备', change: 2.5, topStock: { name: '中兴通讯', price: 32.4, change: 6.8 } },
  { name: '互联网', change: 2.1, topStock: { name: '三六零', price: 12.6, change: 4.5 } },
  { name: '软件服务', change: 1.9, topStock: { name: '金山办公', price: 320.5, change: 3.2 } },
  { name: '汽车整车', change: 1.5, topStock: { name: '赛力斯', price: 88.5, change: 4.5 } },
  { name: '消费电子', change: 1.2, topStock: { name: '立讯精密', price: 32.1, change: 3.5 } }
]

const conceptSectors = [
  { name: '人工智能', change: 3.5, topStock: { name: '科大讯飞', price: 52.3, change: 10.0 } },
  { name: 'CPO概念', change: 3.2, topStock: { name: '中际旭创', price: 120.5, change: 8.2 } },
  { name: '算力租赁', change: 2.9, topStock: { name: '鸿博股份', price: 35.6, change: 6.8 } },
  { name: '华为概念', change: 2.4, topStock: { name: '软通动力', price: 45.2, change: 5.6 } },
  { name: '数据要素', change: 2.1, topStock: { name: '人民网', price: 28.5, change: 4.2 } },
  { name: '信创', change: 1.8, topStock: { name: '中国软件', price: 38.9, change: 3.5 } }
]

const currentSectorList = computed(() => {
  return currentSectorTab.value === 'industry' ? industrySectors : conceptSectors
})

const sentiment = ref({
  upCount: 3200,
  flatCount: 200,
  downCount: 1600,
  limitUp: 85,
  limitDown: 12
})

const moneyFlow = ref({
  north: 45.2,
  main: -120.5,
  south: 23.8
})
</script>

<style scoped>
.market-view-container {
  height: 100%;
  width: 100%;
  background: #0B0E11; /* 回归纯黑背景 */
  color: #FFFFFF;
  padding: 24px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 24px;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* 滚动条 Web3 风格 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.02);
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 通用样式 */
.text-up { color: #FF2E63 !important; text-shadow: 0 0 10px rgba(255, 46, 99, 0.3); } /* 荧光红 */
.text-down { color: #00E096 !important; text-shadow: 0 0 10px rgba(0, 224, 150, 0.3); } /* 荧光绿 */
.bg-up { border-left: 3px solid #FF2E63; }
.bg-down { border-left: 3px solid #00E096; }

.panel {
  background: rgba(20, 20, 30, 0.6); /* 玻璃拟态 */
  backdrop-filter: blur(20px);
  border-radius: 16px; /* 大圆角 */
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
  transition: transform 0.3s, box-shadow 0.3s;
}

.panel:hover {
  box-shadow: 0 12px 40px rgba(108, 93, 211, 0.1); /* 紫色微光 */
  border-color: rgba(108, 93, 211, 0.3);
}

.panel-header {
  height: 56px;
  padding: 0 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.02);
}

.panel-header span {
  font-weight: 600;
  font-size: 16px;
  color: #FFFFFF;
  letter-spacing: 0.5px;
}

.more-link {
  font-size: 13px;
  color: #808191;
  cursor: pointer;
  transition: all 0.3s;
  padding: 6px 12px;
  border-radius: 8px;
}

.more-link:hover { 
  color: #FFFFFF;
  background: rgba(108, 93, 211, 0.2); /* 紫色背景 */
}

/* 顶部指数 */
.indices-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.index-card {
  background: rgba(20, 20, 30, 0.6);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 110px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.index-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.03), transparent);
  transform: translateX(-100%);
  transition: 0.5s;
}

.index-card:hover::before {
  transform: translateX(100%);
}

.index-card:hover {
  transform: translateY(-4px);
  border-color: rgba(108, 93, 211, 0.5);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.index-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.index-name { font-size: 15px; font-weight: 600; color: #FFFFFF; }
.index-code { font-size: 12px; color: #808191; background: rgba(255, 255, 255, 0.1); padding: 2px 6px; border-radius: 4px; }

.index-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.index-value {
  font-size: 28px;
  font-weight: 700;
  font-family: 'JetBrains Mono', monospace;
  line-height: 1;
  letter-spacing: -1px;
}

.index-changes {
  display: flex;
  gap: 12px;
  font-size: 14px;
  font-weight: 500;
  font-family: 'JetBrains Mono', monospace;
}

/* 主体布局 */
.main-content {
  display: flex;
  gap: 24px;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.left-column {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.right-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-width: 320px;
}

/* 排行榜 */
.stock-ranking {
  flex: 1;
}

.tabs {
  display: flex;
  gap: 32px;
}

.tab-item {
  font-size: 15px;
  color: #808191;
  cursor: pointer;
  padding-bottom: 16px;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  position: relative;
  top: 1px;
}

.tab-item:hover {
  color: #FFFFFF;
}

.tab-item.active {
  color: #FFFFFF;
  border-bottom-color: #6C5DD3; /* 电光紫 */
  font-weight: 600;
  text-shadow: 0 0 10px rgba(108, 93, 211, 0.5);
}

.ranking-list {
  flex: 1;
  padding: 0;
  overflow-y: auto;
}

.list-header, .list-item {
  display: grid;
  grid-template-columns: 70px 1.5fr 1fr 1fr 1fr;
  padding: 0 24px;
  height: 52px;
  align-items: center;
  font-size: 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
}

.list-header { 
  color: #808191; 
  font-size: 13px; 
  background: rgba(20, 20, 30, 0.8);
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 1;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.list-item {
  transition: background 0.2s;
}

.list-item:hover { background: rgba(255, 255, 255, 0.03); }

.col-rank { color: #808191; text-align: center; font-family: 'JetBrains Mono', monospace; }
.col-rank.top-3 { color: #FFD166; font-weight: 700; font-size: 16px; text-shadow: 0 0 10px rgba(255, 209, 102, 0.4); } /* 金色前三 */
.col-name .name { color: #FFFFFF; font-weight: 500; }
.col-name .code { color: #808191; font-size: 12px; margin-left: 8px; display: inline-block; background: rgba(255, 255, 255, 0.05); padding: 1px 4px; border-radius: 4px; }
.col-price, .col-change, .col-amount { text-align: right; font-family: 'JetBrains Mono', monospace; font-weight: 500; }

/* 板块排行 */
.sector-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding: 20px;
  background: transparent;
  border: none;
}

.sector-box {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.sector-box:hover { 
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(108, 93, 211, 0.4);
  transform: translateY(-2px);
}

.sector-title {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  font-weight: 600;
  color: #FFFFFF;
}

.sector-leader {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #808191;
  padding-top: 0;
  border: none;
}

/* 市场情绪 */
.sentiment-panel {
  flex: 1;
}

.sentiment-content {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 32px;
  flex: 1;
  justify-content: center;
}

.up-down-dist {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 160px;
  gap: 24px;
}

.dist-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  height: 100%;
  justify-content: flex-end;
}

.bar-wrapper {
  width: 100%;
  height: 120px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  overflow: hidden;
}

.bar {
  width: 100%;
  border-radius: 8px 8px 0 0;
  min-height: 2px;
  transition: height 0.5s ease-out;
  opacity: 1;
}

.dist-item.up .bar { background: linear-gradient(to top, #FF2E63, #FF6B81); box-shadow: 0 0 15px rgba(255, 46, 99, 0.4); }
.dist-item.flat .bar { background: linear-gradient(to top, #808191, #A0A1B2); }
.dist-item.down .bar { background: linear-gradient(to top, #00E096, #26E8A8); box-shadow: 0 0 15px rgba(0, 224, 150, 0.4); }

.count {
  position: absolute;
  top: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #FFFFFF;
  z-index: 1;
  text-shadow: 0 1px 2px rgba(0,0,0,0.8);
}

.label { font-size: 13px; color: #808191; font-weight: 500; }

.limit-info {
  display: flex;
  justify-content: space-between;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 24px;
}

.limit-item {
  display: flex;
  gap: 12px;
  font-size: 14px;
  align-items: center;
}

.limit-item .label { color: #808191; }
.limit-item .value { font-weight: 600; font-family: 'JetBrains Mono', monospace; font-size: 16px; }

/* 资金流向 */
.flow-list {
  padding: 0;
  display: flex;
  flex-direction: column;
}

.flow-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  padding: 18px 24px;
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  align-items: center;
  transition: background 0.2s;
}

.flow-item:hover { background: rgba(255, 255, 255, 0.02); }
.flow-item:last-child { border-bottom: none; }

.flow-item .label { color: #808191; }
.flow-item .value { font-weight: 600; font-family: 'JetBrains Mono', monospace; font-size: 15px; }

</style>