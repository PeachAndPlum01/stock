<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <h1>股票投资信息展示系统</h1>
      </div>
      <div class="header-right">
        <span class="username">欢迎，{{ userStore.userInfo.nickname || userStore.userInfo.username }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </div>

    <!-- 主体布局 -->
    <div class="main-layout">
      <!-- 左侧导航栏 -->
      <div class="sidebar" :class="{ 'sidebar-collapsed': isCollapsed }">
        <div class="sidebar-header">
          <h3 v-show="!isCollapsed">功能导航</h3>
          <el-button 
            class="collapse-btn" 
            :icon="isCollapsed ? 'Expand' : 'Fold'" 
            @click="toggleSidebar"
            circle
            size="small"
          />
        </div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="#f8f9fa"
          text-color="#333"
          active-text-color="#007AFF"
          @select="handleMenuSelect"
          :collapse="isCollapsed"
        >
          <el-menu-item index="region-analysis">
            <el-icon><Location /></el-icon>
            <span>地区选相关度</span>
          </el-menu-item>
          <el-menu-item index="concept-analysis" disabled>
            <el-icon><TrendCharts /></el-icon>
            <span>概念选相关度（开发中）</span>
          </el-menu-item>
          <el-menu-item index="discussion" disabled>
            <el-icon><ChatDotRound /></el-icon>
            <span>讨论区（开发中）</span>
          </el-menu-item>
          <el-menu-item index="star-view">
            <el-icon><Star /></el-icon>
            <span>观星</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 主内容区 -->
      <div class="main-content" :class="{ 'content-expanded': isCollapsed }">
        <!-- 地区选相关度视图 -->
        <div v-if="currentView === 'region-analysis'" class="view-container">
          <!-- 左侧地图 -->
          <div class="map-section">
            <div class="section-title">中国投资地图</div>
            <div v-if="mapData.length === 0" class="map-loading">
              <el-icon class="loading-icon"><Loading /></el-icon>
              <span>地图加载中...</span>
            </div>
            <div ref="mapRef" class="china-map" v-else></div>
          </div>

          <!-- 右侧信息面板 -->
          <div class="info-section">
            <div class="section-title">
              {{ selectedProvince ? `${selectedProvince} - 投资信息` : '请点击地图选择省份' }}
            </div>
            
            <div v-if="selectedProvince" class="info-content">
              <!-- 关联省份提示 -->
              <div v-if="relatedProvinces.length > 0" class="related-provinces">
                <el-tag type="warning" size="small">关联省份</el-tag>
                <el-tag
                  v-for="province in relatedProvinces"
                  :key="province"
                  type="info"
                  size="small"
                  style="margin-left: 8px"
                >
                  {{ province }}
                </el-tag>
              </div>
              
              <!-- 关联原因说明 -->
              <div v-if="relatedReasons && Object.keys(relatedReasons).length > 0" class="related-reasons">
                <div class="reason-title">
                  关联原因
                  <span class="reason-count">({{ Object.keys(relatedReasons).length }})</span>
                </div>
                <div v-for="(reasonInfo, province) in relatedReasons" :key="province" class="reason-item">
                  <div 
                    class="reason-header" 
                    @click="toggleReasonExpand(province)"
                  >
                    <span class="reason-province">{{ province }}</span>
                    <span class="reason-summary">{{ reasonInfo.description }}</span>
                    <el-icon 
                      class="expand-icon" 
                      :class="{ 'expanded': expandedReasons[province] }"
                    >
                      <arrow-down />
                    </el-icon>
                  </div>
                  <el-collapse-transition>
                    <div v-show="expandedReasons[province]" class="reason-projects">
                      <div v-if="reasonInfo.projects && reasonInfo.projects.length > 0" class="projects-list">
                        <div v-for="(project, index) in reasonInfo.projects" :key="index" class="project-item">
                          <div class="project-name">{{ project.title }}</div>
                          <div class="project-info">
                            <span class="project-industry">{{ project.industry }}</span>
                            <span class="project-amount">{{ project.amount }} 万元</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-collapse-transition>
                </div>
              </div>

              <!-- 投资信息列表 -->
              <div class="investment-list">
                <el-card
                  v-for="item in investmentList"
                  :key="item.id"
                  class="investment-card"
                  shadow="hover"
                >
                  <template #header>
                    <div class="card-header">
                      <span class="card-title">{{ item.title }}</span>
                      <el-tag type="success" size="small">{{ item.investmentType }}</el-tag>
                    </div>
                  </template>
                  
                  <div class="card-content">
                    <p><strong>公司：</strong>{{ item.companyName }}</p>
                    <p><strong>行业：</strong>{{ item.industry }}</p>
                    <p><strong>城市：</strong>{{ item.city }}</p>
                    <p><strong>金额：</strong><span class="amount">{{ item.investmentAmount }} 万元</span></p>
                    <p><strong>日期：</strong>{{ item.investmentDate }}</p>
                    <p class="description"><strong>描述：</strong>{{ item.description }}</p>
                  </div>
                </el-card>

                <el-empty v-if="investmentList.length === 0" description="暂无投资信息" />
              </div>
            </div>

            <el-empty v-else description="请点击地图上的省份查看投资信息" />
          </div>
        </div>

        <!-- 观星视图 -->
        <div v-if="currentView === 'star-view'" class="view-container">
          <div class="star-section">
        <div class="section-title">观星模块</div>
            
            <!-- 功能入口网格 -->
            <div v-if="!activeStarFeature" class="star-grid">
              <div
                v-for="feature in starFeatures"
                :key="feature.id"
                class="star-feature-card"
                :class="feature.colorClass"
                @click="handleFeatureClick(feature)"
              >
                <h3>{{ feature.title }}</h3>
                <p>{{ feature.description }}</p>
              </div>
            </div>

            <!-- 功能内容展示区 -->
            <div v-else class="feature-content">
              <div class="feature-header">
                <el-button
                  type="primary"
                  :icon="ArrowLeft"
                  @click="handleBackToGrid"
                  size="default"
                >
                  返回功能列表
                </el-button>
                <h2>{{ currentFeatureInfo.title }}</h2>
              </div>
              <div class="feature-body">
                <el-empty description="该功能正在开发中，敬请期待..." />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, TrendCharts, ChatDotRound, Expand, Fold, Star, Loading, ArrowLeft, ArrowDown, Connection, Document, Briefcase } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getMapData, getInvestmentByProvince, logout } from '@/api'
import { useUserStore } from '@/store/user'
import chinaJson from '@/assets/china.json'

// 验证地图数据是否正确加载
if (!chinaJson || !chinaJson.features) {
  console.error('❌ 中国地图JSON数据加载失败！')
  console.error('请检查：')
  console.error('  1. @/assets/china.json 文件是否存在')
  console.error('  2. 文件内容是否完整')
  console.error('  3. 文件格式是否正确')
  // 在开发环境中显示警告
  if (import.meta.env.DEV) {
    alert('地图数据加载失败，请检查浏览器控制台获取详细信息')
  }
} else {
  console.log('✅ 中国地图JSON数据加载成功，包含', chinaJson.features.length, '个省份/地区')
}

const router = useRouter()
const userStore = useUserStore()

const mapRef = ref(null)
let chartInstance = null

const selectedProvince = ref('')
const investmentList = ref([])
const relatedProvinces = ref([])
const relatedReasons = ref({})
const expandedReasons = ref({}) // 关联原因展开状态
const mapData = ref([])
const activeMenu = ref('region-analysis') // 默认选中地区选相关度
const isCollapsed = ref(false) // 导航栏是否收缩
const currentView = ref('region-analysis') // 当前显示的视图
const activeStarFeature = ref('') // 当前激活的观星功能
const currentFeatureInfo = ref({}) // 当前功能信息

// 观星功能入口列表
const starFeatures = ref([
  {
    id: 'market-trend',
    title: '市场行情',
    icon: '📈',
    description: '实时查看A股市场行情，掌握最新动态',
    colorClass: 'feature-blue'
  },
  {
    id: 'stock-analysis',
    title: '股票分析',
    icon: '🔍',
    description: '深度分析股票数据，发现投资机会',
    colorClass: 'feature-green'
  },
  {
    id: 'my-watchlist',
    title: '我的自选',
    icon: '⭐',
    description: '管理自选股票，个性化投资组合',
    colorClass: 'feature-purple'
  },
  {
    id: 'hot-stocks',
    title: '热门股票',
    icon: '🔥',
    description: '查看市场热门股票，追踪投资热点',
    colorClass: 'feature-orange'
  },
  {
    id: 'portfolio-analysis',
    title: '投资组合',
    icon: '💼',
    description: '分析投资组合表现，优化配置策略',
    colorClass: 'feature-cyan'
  },
  {
    id: 'risk-assessment',
    title: '风险评估',
    icon: '⚠️',
    description: '评估投资风险，制定风控方案',
    colorClass: 'feature-red'
  },
  {
    id: 'market-news',
    title: '市场资讯',
    icon: '📰',
    description: '获取最新市场资讯，把握投资机会',
    colorClass: 'feature-indigo'
  },
  {
    id: 'financial-report',
    title: '财务报表',
    icon: '📊',
    description: '查看公司财务报表，分析经营状况',
    colorClass: 'feature-teal'
  }
])

// 切换导航栏展开收缩
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

// 切换关联原因展开状态
const toggleReasonExpand = (province) => {
  expandedReasons.value[province] = !expandedReasons.value[province]
}

// 菜单选择处理
const handleMenuSelect = async (index) => {
  console.log('🎯 菜单切换请求：', index, '当前视图：', currentView.value)
  
  // 如果点击的是禁用菜单项，不改变当前选中状态
  if (['concept-analysis', 'discussion'].includes(index)) {
    ElMessage.warning('该功能正在开发中，敬请期待！')
    return
  }
  
  // 如果点击的是当前已经激活的菜单，不执行任何操作
  if (index === activeMenu.value && index === currentView.value) {
    console.log('⏭️ 已经是该菜单，无需切换')
    return
  }
  
  // 更新当前选中的菜单和视图
  activeMenu.value = index
  currentView.value = index
  
  // 根据选择的菜单项处理不同的功能
  switch (index) {
    case 'region-analysis':
      // 地区选相关度功能 - 等待DOM更新后重新初始化地图
      console.log('📍 切换到地区选相关度功能')
      
      // 重置地图状态
      resetMapState()
      
      // 等待DOM更新
      await new Promise(resolve => {
        requestAnimationFrame(() => {
          requestAnimationFrame(resolve)
        })
      })
      
      // 重新加载地图数据
      await loadMapData()
      
      // 重新初始化地图
      let retryCount = 0
      const maxRetries = 3
      let mapInitialized = false
      
      while (retryCount < maxRetries && !mapInitialized) {
        mapInitialized = initMap()
        if (!mapInitialized) {
          retryCount++
          await new Promise(resolve => setTimeout(resolve, 200))
        }
      }
      
      if (mapInitialized) {
        ElMessage.info('已切换到地区选相关度功能，请点击地图上的省份查看投资信息')
        console.log('✅ 地区相关度视图切换成功')
      } else {
        ElMessage.error('地图加载失败，请刷新页面重试')
        console.error('❌ 地图加载失败')
      }
      break
      
    case 'star-view':
      console.log('🌟 切换到观星模块')
      // 观星功能不需要特殊处理
      ElMessage.success('已切换到观星模块')
      break
      
    default:
      console.log('ℹ️ 其他菜单项：', index)
  }
}

// 重置地图状态
const resetMapState = () => {
  selectedProvince.value = ''
  investmentList.value = []
  relatedProvinces.value = []
  relatedReasons.value = {}
  
  // 重置地图显示
  if (chartInstance && mapData.value.length > 0) {
    chartInstance.setOption({
      visualMap: {
        show: true,
        min: 0,
        max: 10,
        text: ['高', '低'],
        realtime: false,
        calculable: true,
        inRange: {
          color: ['#e0f3f8', '#abd9e9', '#74add1', '#4575b4', '#313695']
        },
        left: 'left',
        bottom: '20px'
      },
      series: [{
        data: mapData.value.map(item => ({
          ...item,
          itemStyle: {
            areaColor: '#e0f3f8',
            borderColor: '#fff',
            borderWidth: 1
          }
        }))
      }]
    })
  }
}

// 处理观星功能点击
const handleFeatureClick = (feature) => {
  activeStarFeature.value = feature.id
  currentFeatureInfo.value = feature
  ElMessage.success(`已进入${feature.title}模块`)
}

// 返回观星功能网格
const handleBackToGrid = () => {
  activeStarFeature.value = ''
  currentFeatureInfo.value = {}
}

// 初始化地图
const initMap = () => {
  console.log('🗺️ 开始初始化地图...')
  
  // 检查DOM元素是否存在
  if (!mapRef.value) {
    console.error('❌ 地图容器DOM未准备好')
    return false
  }
  
  // 检查DOM宽高是否有效
  const width = mapRef.value.clientWidth
  const height = mapRef.value.clientHeight
  console.log(`📐 DOM尺寸检测: 宽=${width}px, 高=${height}px`)
  
  if (width === 0 || height === 0) {
    console.error('❌ 地图容器DOM宽高为0，无法初始化地图')
    console.error('可能原因：')
    console.error('  1. CSS未正确设置宽高')
    console.error('  2. 父容器布局未完成')
    console.error('  3. DOM还未完全渲染')
    return false
  }
  
  try {
    // 注册中国地图
    if (!chinaJson || !chinaJson.features) {
      console.error('❌ 中国地图JSON数据无效')
      return false
    }
    echarts.registerMap('china', chinaJson)
    console.log('✅ 中国地图JSON注册成功')

    // 如果已有实例，先销毁
    if (chartInstance) {
      console.log('♻️ 销毁旧的图表实例')
      chartInstance.dispose()
    }

    // 创建新的图表实例
    chartInstance = echarts.init(mapRef.value)
    console.log('✅ ECharts实例创建成功')

    const option = {
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          if (params.data && params.data.amount !== undefined) {
            return `${params.name}<br/>投资项目：${params.data.value} 个<br/>投资总额：${params.data.amount.toFixed(2)} 万元`
          }
          return params.name
        }
      },
      visualMap: {
        min: 0,
        max: 10,
        text: ['高', '低'],
        realtime: false,
        calculable: true,
        inRange: {
          color: ['#e0f3f8', '#abd9e9', '#74add1', '#4575b4', '#313695']
        },
        left: 'left',
        bottom: '20px'
      },
      series: [
        {
          name: '投资信息',
          type: 'map',
          map: 'china',
          roam: false,
          zoom: 1.2, // 直接在地图series中设置缩放
          center: [105, 36], // 直接在地图series中设置中心点
          selectedMode: 'single', // 允许选中单个省份
          silent: false, // 确保地图可以交互
          label: {
            show: true,
            fontSize: (params) => {
              // 根据省份名称动态调整字体大小
              const smallAreas = ['澳', '港', '台', '沪', '京', '津', '渝'];
              const mediumAreas = ['琼', '宁', '青', '甘'];
              
              // 直接使用省份名称
              const normalizedName = params.name;
              
              if (smallAreas.includes(normalizedName)) {
                return 8; // 小区域使用更小的字体
              } else if (mediumAreas.includes(normalizedName)) {
                return 9; // 中等区域使用中等字体
              } else {
                return 10; // 大区域使用正常字体
              }
            },
            color: '#333',
            fontWeight: 'normal',
            formatter: (params) => {
              // 直接使用省份简称显示在地图上
              return params.name;
            }
          },
          emphasis: {
            label: {
              show: true,
              color: '#fff',
              fontSize: (params) => {
                // 高亮状态下也保持相对大小比例
                const smallAreas = ['澳', '港', '台', '沪', '京', '津', '渝'];
                const mediumAreas = ['琼', '宁', '青', '甘'];
                
                // 直接使用省份名称
                const normalizedName = params.name;
                
                if (smallAreas.includes(normalizedName)) {
                  return 9;
                } else if (mediumAreas.includes(normalizedName)) {
                  return 10;
                } else {
                  return 12;
                }
              },
              formatter: (params) => {
                // 直接使用省份简称显示在高亮状态
                return params.name;
              }
            },
            itemStyle: {
              areaColor: '#ffd700',
              borderColor: '#fff',
              borderWidth: 2,
              shadowBlur: 20,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          select: {
            label: {
              show: true,
              color: '#fff',
              fontSize: (params) => {
                const smallAreas = ['澳', '港', '台', '沪', '京', '津', '渝'];
                const mediumAreas = ['琼', '宁', '青', '甘'];
                
                // 直接使用省份名称
                const normalizedName = params.name;
                
                if (smallAreas.includes(normalizedName)) {
                  return 9;
                } else if (mediumAreas.includes(normalizedName)) {
                  return 10;
                } else {
                  return 12;
                }
              },
              formatter: (params) => {
                // 直接使用省份简称显示在选中状态
                return params.name;
              }
            },
            itemStyle: {
              areaColor: '#ff6b6b',
              borderColor: '#fff',
              borderWidth: 2
            }
          },
          itemStyle: {
            areaColor: '#e0f3f8',
            borderColor: '#fff',
            borderWidth: 1
          },
          data: mapData.value || []
        }
      ]
    }

    chartInstance.setOption(option)
    console.log('✅ 地图配置设置成功，数据项数量：', mapData.value?.length || 0)

    // 地图点击事件
    chartInstance.off('click') // 移除之前的事件监听
    chartInstance.on('click', async (params) => {
      if (params.name) {
        await handleProvinceClick(params.name)
      }
    })
    console.log('✅ 地图点击事件绑定成功')
    
    return true
  } catch (error) {
    console.error('❌ 地图初始化失败：', error)
    return false
  }
}

// 加载地图数据
const loadMapData = async (retryCount = 3) => {
  console.log('📊 开始加载地图数据，剩余重试次数：', retryCount)
  
  for (let attempt = 1; attempt <= retryCount; attempt++) {
    try {
      console.log(`🔄 第 ${attempt} 次尝试加载地图数据...`)
      const res = await getMapData()
      
      console.log('✅ API请求成功，返回数据：', res.data)
      
      if (!res.data || !res.data.mapData) {
        console.error('❌ API返回数据格式不正确：', res.data)
        throw new Error('地图数据格式错误')
      }
      
      mapData.value = res.data.mapData
      console.log('✅ 地图数据加载成功，省份数量：', mapData.value.length)
      
      // 如果图表实例已存在，更新数据
      if (chartInstance) {
        chartInstance.setOption({
          series: [{
            data: mapData.value
          }]
        })
        console.log('✅ 图表数据更新成功')
      }
      
      return true
      
    } catch (error) {
      console.error(`❌ 第 ${attempt} 次加载失败：`, error)
      
      if (attempt === retryCount) {
        console.error('❌ 所有重试均失败，放弃加载')
        ElMessage.error('加载地图数据失败，请刷新页面重试')
        return false
      }
      
      // 等待一段时间后重试（指数退避）
      const waitTime = 1000 * Math.pow(2, attempt - 1)
      console.log(`⏳ 等待 ${waitTime}ms 后重试...`)
      await new Promise(resolve => setTimeout(resolve, waitTime))
    }
  }
  
  return false
}



// 处理省份点击
const handleProvinceClick = async (provinceName) => {
  // 使用标准化后的省份简称作为显示名称
        selectedProvince.value = provinceName
  
  // 标准化省份名称用于查询
        const normalizedName = provinceName
  
  console.log('🔍 点击省份:', provinceName)
  console.log('🔍 标准化后:', normalizedName)
  
  try {
    const res = await getInvestmentByProvince(normalizedName, 100)
    console.log('📊 API返回数据:', res.data)
    console.log('📋 投资列表:', res.data.investmentList)
    console.log('📋 投资列表长度:', res.data.investmentList?.length)
    
    // 修复数据绑定：使用正确的字段名
    investmentList.value = res.data.investmentList || []
    relatedProvinces.value = res.data.relatedProvinces || []
    relatedReasons.value = res.data.relatedReasons || {}
    
    // 重置关联原因展开状态
    expandedReasons.value = {}
    
    console.log('📋 关联省份数据:', relatedProvinces.value)
    console.log('📋 关联原因数据:', relatedReasons.value)
    
    // 高亮关联性最强的三个省份
    if (chartInstance && relatedProvinces.value.length > 0) {
      console.log('🔍 关联省份列表:', relatedProvinces.value)
      console.log('🔍 当前选中省份:', provinceName)
      console.log('🗺️ 地图数据所有省份名称:', mapData.value.map(item => item.name))
      
      // 标准化关联省份名称以匹配地图数据中的简称格式
      const normalizedRelatedProvinces = relatedProvinces.value.map(province => {
        // API返回的简称直接使用，无需转换
        return province
      })
      console.log('📊 标准化后的关联省份:', normalizedRelatedProvinces)
      
      // 创建高亮数据，使用自定义的value值
      const highlightData = mapData.value.map(item => {
        if (item.name === provinceName) {
          console.log('🔴 高亮当前省份:', item.name)
          return {
            ...item,
            value: 100, // 使用特殊值标记当前选中省份
            _type: 'selected'
          }
        }
        // 高亮关联性最强的三个省份
        if (normalizedRelatedProvinces.includes(item.name)) {
          console.log('🟡 高亮关联省份:', item.name, '匹配成功')
          return {
            ...item,
            value: 50, // 使用特殊值标记关联省份
            _type: 'related'
          }
        }
        return {
          ...item,
          value: 1, // 普通省份使用小值
          _type: 'normal'
        }
      })
      
      console.log('🎨 最终高亮数据:', highlightData.filter(item => item._type !== 'normal'))

      // 重新设置visualMap，使其根据自定义的value值显示不同颜色
      chartInstance.setOption({
        visualMap: {
          show: false, // 隐藏visualMap图例
          min: 0,
          max: 100,
          pieces: [
            { value: 100, label: '选中省份', color: '#ff6b6b' },
            { value: 50, label: '关联省份', color: '#ffd700' },
            { value: 1, label: '其他省份', color: '#e0f3f8' }
          ]
        },
        series: [{
          data: highlightData
        }]
      })
    } else {
      console.log('⚠️ 没有关联省份，只高亮当前省份')
      // 如果没有关联省份，只高亮当前选中的省份
      const highlightData = mapData.value.map(item => {
        if (item.name === provinceName) {
          return {
            ...item,
            value: 100,
            _type: 'selected'
          }
        }
        return {
          ...item,
          value: 1,
          _type: 'normal'
        }
      })
      
      chartInstance.setOption({
        visualMap: {
          show: false,
          min: 0,
          max: 100,
          pieces: [
            { value: 100, label: '选中省份', color: '#ff6b6b' },
            { value: 1, label: '其他省份', color: '#e0f3f8' }
          ]
        },
        series: [{
          data: highlightData
        }]
      })
    }
  } catch (error) {
    console.error('❌ 获取投资数据失败:', error)
    ElMessage.error('加载投资信息失败')
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await logout()
    userStore.clearUser()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户取消操作
  }
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(async () => {
  console.log('🚀 Home组件开始挂载...')
  
  try {
    // 检查用户登录状态
    if (!userStore.token) {
      console.error('❌ 用户未登录，跳转到登录页')
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }
    console.log('✅ 用户登录状态正常：', userStore.userInfo.username || userStore.userInfo.nickname)
    
    // 等待DOM完全渲染（确保mapRef已经准备好）
    await new Promise(resolve => setTimeout(resolve, 200))
    console.log('✅ DOM渲染完成')
    
    // 先加载地图数据
    const dataLoaded = await loadMapData()
    if (!dataLoaded) {
      console.error('❌ 地图数据加载失败，停止初始化')
      ElMessage.error('地图数据加载失败，请刷新页面重试')
      return
    }
    
    // 确保当前视图是地区相关度，并且DOM已经渲染
    await new Promise(resolve => {
      requestAnimationFrame(() => {
        requestAnimationFrame(() => {
          requestAnimationFrame(resolve)
        })
      })
    })
    
    console.log(`📋 当前视图状态: currentView=${currentView.value}, activeMenu=${activeMenu.value}`)
    
    // 确保DOM元素存在后再初始化地图
    let retryCount = 0
    const maxRetries = 10  // 增加重试次数
    let mapInitialized = false
    
    while (retryCount < maxRetries && !mapInitialized) {
      console.log(`🔄 尝试初始化地图，第 ${retryCount + 1} 次...`)
      
      // 检查DOM尺寸
      if (mapRef.value) {
        const width = mapRef.value.clientWidth
        const height = mapRef.value.clientHeight
        console.log(`📐 当前DOM尺寸: 宽=${width}px, 高=${height}px`)
      } else {
        console.log('📐 mapRef.value 为 null')
      }
      
      // 增加等待时间，确保DOM完全更新
      const waitTime = 300 + (retryCount * 100)  // 递增等待时间
      console.log(`⏳ 等待 ${waitTime}ms...`)
      await new Promise(resolve => setTimeout(resolve, waitTime))
      
      // 强制触发多次DOM更新
      await new Promise(resolve => {
        requestAnimationFrame(() => {
          requestAnimationFrame(() => {
            requestAnimationFrame(resolve)
          })
        })
      })
      
      mapInitialized = initMap()
      
      if (!mapInitialized) {
        retryCount++
        console.log(`⏳ 第 ${retryCount} 次初始化失败，准备下一次重试...`)
      }
    }
    
    if (!mapInitialized) {
      console.error('❌ 地图初始化失败，已达最大重试次数')
      ElMessage.error('地图初始化失败，请刷新页面重试')
      return
    }
    
    // 监听窗口大小变化
    window.addEventListener('resize', handleResize)
    console.log('✅ 窗口大小变化监听器已添加')
    
    // 确保默认选中地区选相关度
    activeMenu.value = 'region-analysis'
    currentView.value = 'region-analysis'
    console.log('✅ 默认视图已设置为地区选相关度')
    
    // 页面加载完成后显示欢迎信息
    setTimeout(() => {
      ElMessage.success('欢迎使用股票投资信息展示系统！请点击地图上的省份查看投资信息')
      console.log('✅ 欢迎消息已显示')
    }, 500)
    
    console.log('🎉 Home组件挂载完成！')
    
  } catch (error) {
    console.error('❌ Home组件挂载过程中发生错误：', error)
    ElMessage.error('页面初始化失败，请刷新页面重试')
  }
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.header {
  height: 60px;
  background: #007AFF;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  border-bottom: 2px solid #0066cc;
}

.header-left h1 {
  font-size: 22px;
  color: #fff;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #fff;
  font-size: 14px;
}

.main-layout {
  flex: 1;
  display: flex;
  height: calc(100vh - 60px);
  overflow: hidden;
}

/* 左侧导航栏样式 */
.sidebar {
  width: 240px;
  background: #fff;
  border-right: 2px solid #e5e5e5;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 100;
}

/* 导航栏收缩状态 */
.sidebar-collapsed {
  width: 64px;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 2px solid #f0f0f0;
  background: #f5f7fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 60px;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  transition: opacity 0.3s ease;
}

.sidebar-collapsed .sidebar-header h3 {
  opacity: 0;
  pointer-events: none;
}

.collapse-btn {
  background: #007AFF;
  color: white;
  border: none;
  transition: all 0.3s ease;
}

.collapse-btn:hover {
  background: #0051D5;
  transform: scale(1.1);
}

.sidebar-collapsed .collapse-btn {
  margin: 0 auto;
}

.sidebar-menu {
  flex: 1;
  border: none;
  padding: 10px 0;
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 10px;
  border-radius: 6px;
  transition: all 0.3s ease;
  white-space: nowrap;
  overflow: hidden;
}

.sidebar-collapsed .sidebar-menu .el-menu-item {
  margin: 4px 5px;
  padding: 0 12px !important;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #e6f7ff;
  color: #007AFF;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #007AFF;
  color: #fff;
  font-weight: 500;
}

.sidebar-menu .el-icon {
  font-size: 18px;
  margin-right: 8px;
  transition: margin-right 0.3s ease;
}

.sidebar-collapsed .sidebar-menu .el-icon {
  margin-right: 0;
}

.sidebar-menu .el-menu-item span {
  transition: opacity 0.3s ease;
}

.sidebar-collapsed .sidebar-menu .el-menu-item span {
  opacity: 0;
  width: 0;
  height: 0;
  overflow: hidden;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
  overflow: hidden;
  background: #f5f7fa;
  transition: all 0.3s ease;
}

.map-section {
  flex: 1;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-width: 0;
  border: 2px solid #e5e5e5;
}

.info-section {
  width: 450px;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 450px;
  border: 2px solid #e5e5e5;
}

.section-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e5e5e5;
}

.china-map {
  flex: 1;
  width: 100%;
  height: 100%;  /* 明确设置高度 */
  min-height: 0;
  min-width: 0;  /* 添加最小宽度 */
  pointer-events: auto;
  cursor: pointer;
  position: relative;
  z-index: 1;
}

.map-loading {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #666;
  font-size: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 2px dashed #ddd;
}

.loading-icon {
  font-size: 32px;
  margin-bottom: 16px;
  color: #007AFF;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.info-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.related-provinces {
  padding: 15px;
  background: #fff9e6;
  border-radius: 6px;
  margin-bottom: 15px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

/* 关联原因说明样式 */
.related-reasons {
  padding: 0;
  background: #ffffff;
  margin-bottom: 20px;
}

.reason-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.reason-count {
  font-size: 13px;
  color: #999;
  font-weight: 400;
}

.reason-item {
  background: #fafafa;
  border-radius: 0;
  margin-bottom: 1px;
  transition: all 0.2s ease;
}

.reason-item:first-child {
  border-radius: 8px 8px 0 0;
}

.reason-item:last-child {
  border-radius: 0 0 8px 8px;
  margin-bottom: 0;
}

.reason-item:only-child {
  border-radius: 8px;
}

.reason-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s ease;
}

.reason-header:hover {
  background-color: #f0f0f0;
}

.reason-province {
  color: #007AFF;
  font-weight: 600;
  font-size: 14px;
  min-width: 40px;
}

.reason-summary {
  color: #333;
  font-size: 13px;
  flex: 1;
  line-height: 1.5;
}

.expand-icon {
  font-size: 12px;
  color: #999;
  transition: transform 0.3s ease;
}

.expand-icon.expanded {
  transform: rotate(180deg);
}

.reason-projects {
  background: #ffffff;
  border-top: 1px solid #e5e5e5;
}

.projects-list {
  display: flex;
  flex-direction: column;
}

.project-item {
  padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s ease;
}

.project-item:last-child {
  border-bottom: none;
}

.project-item:hover {
  background-color: #fafafa;
}

.project-name {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.project-info {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
}

.project-industry {
  color: #666;
  padding: 2px 8px;
  background: #f0f0f0;
  border-radius: 3px;
  font-size: 12px;
}

.project-amount {
  color: #ff6b35;
  font-weight: 500;
}

.reason-text {
  color: #666;
}

.investment-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}

.investment-card {
  margin-bottom: 15px;
  border: 2px solid #e5e5e5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.card-content p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
  font-size: 15px;
}

.description {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #eee;
  color: #999;
  font-size: 13px;
  line-height: 1.8;
}

/* 滚动条样式 */
.investment-list::-webkit-scrollbar {
  width: 6px;
}

.investment-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.investment-list::-webkit-scrollbar-thumb:hover {
  background: #bbb;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .star-grid {
    grid-template-columns: repeat(3, 1fr);
    grid-template-rows: repeat(3, 1fr);
  }
}

@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
    padding: 15px;
  }
  
  .info-section {
    width: 100%;
    min-width: auto;
    height: 400px;
  }
  
  .map-section {
    height: 500px;
  }
  
  .sidebar {
    width: 200px;
  }
  
  .sidebar-collapsed {
    width: 64px;
  }

  .star-grid {
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 180px;
    position: absolute;
    left: 0;
    top: 60px;
    height: calc(100vh - 60px);
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .sidebar-collapsed {
    width: 180px;
    transform: translateX(0);
  }
  
  .main-content {
    padding: 10px;
    margin-left: 0 !important;
    width: 100% !important;
  }
  
  .header {
    padding: 0 15px;
  }
  
  .header-left h1 {
    font-size: 18px;
  }
  
  .info-section {
    width: 100%;
    min-width: auto;
  }

  .star-grid {
    grid-template-columns: 1fr;
    grid-template-rows: repeat(8, auto);
    gap: 15px;
    padding: 10px 0;
  }

  .star-feature-card {
    min-height: 120px;
    padding: 20px 15px;
  }

  .star-feature-card .feature-icon {
    font-size: 40px;
    margin-bottom: 10px;
  }

  .star-feature-card h3 {
    font-size: 18px;
  }

  .star-feature-card p {
    font-size: 13px;
  }

  .feature-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .feature-header h2 {
    font-size: 20px;
  }
}

/* 移动端菜单遮罩 */
@media (max-width: 768px) {
  .sidebar-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 999;
    display: none;
  }
  
  .sidebar-collapsed + .sidebar-overlay {
    display: block;
  }
}

/* 视图容器样式 */
.view-container {
  width: 100%;
  height: 100%;
  display: flex;
  gap: 20px;
}

/* 观星模块样式 */
.star-section {
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 2px solid #e5e5e5;
}

/* 功能入口网格布局 */
.star-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 20px;
  padding: 20px 0;
  overflow-y: auto;
}

/* 功能卡片样式 - 平面化设计 */
.star-feature-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid #e5e5e5;
  box-shadow: none;
  min-height: 180px;
  background: #f5f7fa;
}

.star-feature-card:hover {
  transform: translateY(-4px);
  border-color: #007AFF;
  background: #e8f0fe;
}

.star-feature-card h3 {
  margin: 10px 0 8px;
  font-size: 20px;
  color: #333;
  font-weight: 500;
  transition: color 0.2s ease;
}

.star-feature-card p {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  text-align: center;
  margin: 0;
  transition: color 0.2s ease;
}

/* 不同颜色的功能卡片 - 平面化设计 */
.star-feature-card.feature-blue {
  background: #e8f4fd;
  border-color: #d1e8fc;
  color: #333;
}

.star-feature-card.feature-blue:hover {
  background: #d1e8fc;
  border-color: #007AFF;
}

.star-feature-card.feature-blue h3,
.star-feature-card.feature-blue p {
  color: #333;
}

.star-feature-card.feature-green {
  background: #e8f5e9;
  border-color: #d4edda;
  color: #333;
}

.star-feature-card.feature-green:hover {
  background: #d4edda;
  border-color: #28a745;
}

.star-feature-card.feature-green h3,
.star-feature-card.feature-green p {
  color: #333;
}

.star-feature-card.feature-purple {
  background: #f3e5f5;
  border-color: #e1d4f5;
  color: #333;
}

.star-feature-card.feature-purple:hover {
  background: #e1d4f5;
  border-color: #9c27b0;
}

.star-feature-card.feature-purple h3,
.star-feature-card.feature-purple p {
  color: #333;
}

.star-feature-card.feature-orange {
  background: #fff3e0;
  border-color: #ffe0b2;
  color: #333;
}

.star-feature-card.feature-orange:hover {
  background: #ffe0b2;
  border-color: #ff9800;
}

.star-feature-card.feature-orange h3,
.star-feature-card.feature-orange p {
  color: #333;
}

.star-feature-card.feature-cyan {
  background: #e0f7fa;
  border-color: #b2ebf2;
  color: #333;
}

.star-feature-card.feature-cyan:hover {
  background: #b2ebf2;
  border-color: #00bcd4;
}

.star-feature-card.feature-cyan h3,
.star-feature-card.feature-cyan p {
  color: #333;
}

.star-feature-card.feature-red {
  background: #ffebee;
  border-color: #ffcdd2;
  color: #333;
}

.star-feature-card.feature-red:hover {
  background: #ffcdd2;
  border-color: #f44336;
}

.star-feature-card.feature-red h3,
.star-feature-card.feature-red p {
  color: #333;
}

.star-feature-card.feature-indigo {
  background: #e8eaf6;
  border-color: #c5cae9;
  color: #333;
}

.star-feature-card.feature-indigo:hover {
  background: #c5cae9;
  border-color: #3f51b5;
}

.star-feature-card.feature-indigo h3,
.star-feature-card.feature-indigo p {
  color: #333;
}

.star-feature-card.feature-teal {
  background: #e0f2f1;
  border-color: #b2dfdb;
  color: #333;
}

.star-feature-card.feature-teal:hover {
  background: #b2dfdb;
  border-color: #009688;
}

.star-feature-card.feature-teal h3,
.star-feature-card.feature-teal p {
  color: #333;
}

/* 功能内容展示区 */
.feature-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.feature-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
  border: 2px solid #e5e5e5;
}

.feature-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
  font-weight: 500;
}

.feature-body {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 4px;
  padding: 40px;
  border: 2px solid #e5e5e5;
}
</style>