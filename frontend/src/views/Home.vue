<template>
  <div class="okx-container">
    <!-- 1. 顶部导航栏 (Global Header) -->
    <header class="okx-header">
      <div class="header-left">
        <div class="logo-box">
          <div class="logo-symbol">T</div>
          <span class="logo-text">TIANWEN</span>
        </div>
        <div class="header-divider"></div>
        <nav class="main-nav">
          <a href="#" class="nav-link" :class="{ active: currentView === 'region-analysis' }" @click.prevent="handleMenuSelect('region-analysis')">地区透视</a>
          <a href="#" class="nav-link" :class="{ active: currentView === 'star-view' }" @click.prevent="handleMenuSelect('star-view')">观星台</a>
          <a href="#" class="nav-link" :class="{ active: currentView === 'discussion' }" @click.prevent="handleMenuSelect('discussion')">股吧</a>
          <a href="#" class="nav-link disabled">市场 <span class="badge-new">New</span></a>
          <a href="#" class="nav-link disabled">金融</a>
          <a href="#" class="nav-link disabled">Web3</a>
        </nav>
      </div>
      <div class="header-right">
        <div class="search-box">
          <el-icon><Search /></el-icon>
          <input type="text" placeholder="搜索币种、合约、理财" />
        </div>
        <div class="action-icons">
          <el-icon class="icon-btn"><Bell /></el-icon>
          <el-icon class="icon-btn"><Download /></el-icon>
        </div>
        <div class="user-menu" @click="handleLogout">
          <div class="avatar-circle">
            {{ (userStore.userInfo.nickname || userStore.userInfo.username || 'U').charAt(0).toUpperCase() }}
          </div>
        </div>
      </div>
    </header>

    <!-- 2. 次级行情条 (Ticker Bar) -->
    <div class="ticker-bar">
      <div class="ticker-track">
        <!-- Group 1 -->
        <div class="ticker-item">
          <span class="pair">BTC/USDT</span>
          <span class="price up">43,250.50</span>
          <span class="change up">+2.45%</span>
        </div>
        <div class="ticker-item">
          <span class="pair">ETH/USDT</span>
          <span class="price down">2,280.15</span>
          <span class="change down">-1.12%</span>
        </div>
        <div class="ticker-item">
          <span class="pair">SOL/USDT</span>
          <span class="price up">98.45</span>
          <span class="change up">+5.67%</span>
        </div>
        <div class="ticker-item highlight">
          <span class="pair">上证指数</span>
          <span class="price down">2,850.30</span>
          <span class="change down">-0.85%</span>
        </div>
        <div class="ticker-item nasdaq">
          <span class="pair">NASDAQ</span>
          <span class="price up">15,628.90</span>
          <span class="change up">+1.25%</span>
        </div>
        <div class="ticker-item">
          <span class="pair">GOLD</span>
          <span class="price up">2,035.40</span>
          <span class="change up">+0.45%</span>
        </div>

        <!-- Group 2 (Duplicate for seamless scroll) -->
        <div class="ticker-item">
          <span class="pair">BTC/USDT</span>
          <span class="price up">43,250.50</span>
          <span class="change up">+2.45%</span>
        </div>
        <div class="ticker-item">
          <span class="pair">ETH/USDT</span>
          <span class="price down">2,280.15</span>
          <span class="change down">-1.12%</span>
        </div>
        <div class="ticker-item">
          <span class="pair">SOL/USDT</span>
          <span class="price up">98.45</span>
          <span class="change up">+5.67%</span>
        </div>
        <div class="ticker-item highlight">
          <span class="pair">上证指数</span>
          <span class="price down">2,850.30</span>
          <span class="change down">-0.85%</span>
        </div>
        <div class="ticker-item nasdaq">
          <span class="pair">NASDAQ</span>
          <span class="price up">15,628.90</span>
          <span class="change up">+1.25%</span>
        </div>
        <div class="ticker-item">
          <span class="pair">GOLD</span>
          <span class="price up">2,035.40</span>
          <span class="change up">+0.45%</span>
        </div>
      </div>
    </div>

    <!-- 3. 主体交易界面 (Trading Interface) -->
    <main class="trading-main">
      
      <!-- 股吧视图 -->
      <StockBar v-if="currentView === 'discussion'" />

      <!-- 观星台视图 -->
      <StarObservatory v-else-if="currentView === 'star-view'" />

      <!-- 原有视图容器 -->
      <template v-else>
      <!-- 左侧：市场列表 (Market List) -->
      <aside class="panel-left">
        <!-- 新增：市场情绪分布 -->
        <div class="market-sentiment">
          <div class="sentiment-header">
            <div class="index-info">
              <span class="index-name">上证指数</span>
              <span class="index-value text-down">2,850.30</span>
            </div>
            <span class="index-change text-down">-0.85%</span>
          </div>
          <div class="sentiment-chart">
            <div class="bar-labels">
              <span class="label-up">涨 1,205</span>
              <span class="label-flat">平 158</span>
              <span class="label-down">跌 3,850</span>
            </div>
            <div class="distribution-bar">
              <div class="bar-segment up" style="width: 23%"></div>
              <div class="bar-segment flat" style="width: 3%"></div>
              <div class="bar-segment down" style="width: 74%"></div>
            </div>
          </div>
        </div>

        <div class="panel-divider"></div>

        <div class="market-card-container">
          <div class="panel-header">
            <span class="tab active">热门区域</span>
            <span class="tab">自选</span>
          </div>

          <div class="market-list-header">
            <span>区域</span>
            <span class="text-right" style="display: flex; align-items: center; gap: 4px;">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                <path d="M13.5 3C13.5 3 13.5 3 13.5 3C10.5 5 8.5 8 8.5 11.5C8.5 12 8.5 12.5 8.5 13C6.5 14 5.5 16 5.5 18C5.5 21 8 23.5 11 23.5C14 23.5 16.5 21 16.5 18C16.5 16 15.5 14 13.5 13C13.5 12.5 13.5 12 13.5 11.5C13.5 8 11.5 5 8.5 3" fill="#FFC107"/>
              </svg>
              热度
            </span>
          </div>
          <div class="market-list-body scrollbar-hide">
            <div 
              v-for="(item, index) in (mapData.length ? mapData : mockProvinces)" 
              :key="index"
              class="market-item"
              :class="{ active: selectedProvince === item.name }"
              @click="handleProvinceClick(item.name)"
            >
              <div class="item-name">
                <span class="star-icon"><el-icon><Star /></el-icon></span>
                {{ item.name }}
              </div>
              <div class="item-price" :class="item.value > 5 ? 'text-up' : ''">
                {{ item.value ? item.value.toFixed(2) : '-' }}
              </div>
            </div>
          </div>
        </div>
      </aside>

      <!-- 中间：K线/地图区域 (Chart Area) -->
      <section class="panel-center">
        <!-- 顶部工具栏 -->
        <div class="chart-toolbar">
          <div class="pair-info">
            <h1 class="pair-title">{{ selectedProvince || 'China Market' }} <span class="currency">/ CNY</span></h1>
            <div class="pair-stats">
              <div class="stat-box">
                <span class="label">最新价</span>
                <span class="value text-up">12,450.00</span>
              </div>
              <div class="stat-box">
                <span class="label">24h涨跌</span>
                <span class="value text-up">+3.25%</span>
              </div>
              <div class="stat-box">
                <span class="label">24h最高</span>
                <span class="value">12,800.00</span>
              </div>
            </div>
          </div>
          <div class="time-intervals">
            <span class="interval active">地图</span>
            <span class="interval">1m</span>
            <span class="interval">15m</span>
            <span class="interval">1h</span>
            <span class="interval">4h</span>
            <span class="interval">1D</span>
            <el-icon class="setting-icon"><Setting /></el-icon>
          </div>
        </div>

        <!-- 地图容器 -->
        <div class="chart-container">
          <div v-if="currentView === 'region-analysis'" class="full-map-wrapper">
            <div ref="mapRef" class="china-map"></div>
          </div>
        </div>
      </section>

      <!-- 右侧：订单簿/详情 (Order Book) -->
      <aside class="panel-right">
        <!-- 上半部分：深度/关联 -->
        <div class="order-book-section">
          <div class="panel-header-sm">
            <span>关联深度</span>
            <span class="more-link">更多</span>
          </div>
          <div class="order-book-header">
            <span>价格(CNY)</span>
            <span>数量(手)</span>
            <span>累计</span>
          </div>
          <div class="order-book-body">
            <!-- 卖盘 (红) -->
            <div class="asks">
              <div v-for="n in 5" :key="'ask'+n" class="order-row">
                <span class="price text-down">{{ (12450 + n * 2.5).toFixed(2) }}</span>
                <span class="amount">{{ (Math.random() * 100).toFixed(3) }}</span>
                <span class="total">{{ (Math.random() * 500).toFixed(3) }}</span>
                <div class="depth-bar down" :style="{ width: Math.random() * 100 + '%' }"></div>
              </div>
            </div>
            
            <!-- 最新价 -->
            <div class="current-price-row">
              <span class="current-price text-up">12,450.00</span>
              <span class="mark-price">≈ $1,729.16</span>
            </div>

            <!-- 买盘 (绿) -->
            <div class="bids">
              <div v-for="n in 8" :key="'bid'+n" class="order-row">
                <span class="price text-up">{{ (12450 - n * 2.5).toFixed(2) }}</span>
                <span class="amount">{{ (Math.random() * 100).toFixed(3) }}</span>
                <span class="total">{{ (Math.random() * 500).toFixed(3) }}</span>
                <div class="depth-bar up" :style="{ width: Math.random() * 100 + '%' }"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 下半部分：最新成交/企业列表 -->
        <div class="trades-section">
          <div class="panel-header-sm">
            <span>重点企业 ({{ investmentList.length }})</span>
          </div>
          <div class="order-book-header">
            <span>时间</span>
            <span style="text-align: left; padding-left: 12px;">名称</span>
            <span>涨跌幅</span>
          </div>
          <div class="trades-list scrollbar-hide">
            <div v-for="item in investmentList" :key="item.id" class="trade-item">
              <span class="time">{{ new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit', second:'2-digit'}) }}</span>
              <span class="name">{{ item.companyName }}</span>
              <span class="price" :class="getChangeClass(item.changePercent)">
                {{ item.changePercent }}%
              </span>
            </div>
            <div v-if="investmentList.length === 0" class="empty-state">
              <el-icon :size="32" color="#373A40"><Files /></el-icon>
              <span>暂无数据</span>
            </div>
          </div>
        </div>
      </aside>
      </template>
    </main>

    <!-- 底部状态栏 -->
    <footer class="okx-footer">
      <div class="footer-left">
        <span class="status-dot online"></span>
        <span>系统正常</span>
        <span class="divider">|</span>
        <span>延迟: 24ms</span>
      </div>
      <div class="footer-right">
        <span>Cookie 偏好</span>
        <span>下载客户端</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Bell, Download, Star, Setting, Files } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getMapData, getInvestmentByProvince, logout } from '@/api'
import { useUserStore } from '@/store/user'
import chinaJson from '@/assets/china.json'
import StockBar from '@/components/StockBar.vue'
import StarObservatory from '@/components/StarObservatory.vue'

// 验证地图数据是否正确加载
if (!chinaJson || !chinaJson.features) {
  console.error('❌ 中国地图JSON数据加载失败！')
}

const router = useRouter()
const userStore = useUserStore()

const mapRef = ref(null)
let chartInstance = null

const selectedProvince = ref('')
const investmentList = ref([])
const mapData = ref([])
const activeMenu = ref('region-analysis')
const currentView = ref('region-analysis')

// 颜色辅助函数
const getChangeClass = (val) => {
  const num = Number(val)
  if (num > 0) return 'text-up'
  if (num < 0) return 'text-down'
  return ''
}

// 模拟省份数据（用于左侧列表兜底）
const mockProvinces = ref([
  { name: '广东', value: 8.5 },
  { name: '江苏', value: 7.2 },
  { name: '浙江', value: 6.8 },
  { name: '山东', value: 5.4 },
  { name: '北京', value: 4.9 },
  { name: '上海', value: 4.5 },
  { name: '四川', value: 3.2 },
  { name: '湖北', value: 2.8 }
])



const handleMenuSelect = async (index) => {
  if (['concept-analysis'].includes(index)) {
    ElMessage.warning('该功能正在开发中')
    return
  }
  
  if (index === activeMenu.value && index === currentView.value) return
  
  activeMenu.value = index
  currentView.value = index
  
  if (index === 'region-analysis') {
    resetMapState()
    await new Promise(resolve => setTimeout(resolve, 100))
    await loadMapData()
    initMap()
  }
}

const resetMapState = () => {
  selectedProvince.value = ''
  investmentList.value = []
}



const initMap = () => {
  if (!mapRef.value) return false
  
  try {
    echarts.registerMap('china', chinaJson)
    if (chartInstance) chartInstance.dispose()
    chartInstance = echarts.init(mapRef.value)

    const option = {
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(22, 23, 26, 0.9)',
        borderColor: '#363C45',
        borderWidth: 1,
        padding: [12, 16],
        textStyle: { color: '#EAECEF' },
        extraCssText: 'box-shadow: 0 8px 24px rgba(0,0,0,0.5); backdrop-filter: blur(10px); border-radius: 4px;',
        formatter: (params) => {
          if (params.data && params.data.value !== undefined) {
            return `<div style="font-family: 'DINPro', sans-serif; min-width: 120px;">
              <div style="font-size: 12px; color: #848E9C; margin-bottom: 8px; display: flex; justify-content: space-between; align-items: center;">
                <span>${params.name}</span>
                <span style="width: 6px; height: 6px; border-radius: 50%; background: #2962FF; box-shadow: 0 0 8px #2962FF;"></span>
              </div>
              <div style="display: flex; align-items: baseline; gap: 8px;">
                <span style="font-size: 24px; font-weight: 600; color: #fff; font-family: 'JetBrains Mono'; letter-spacing: -0.5px;">${params.data.value}</span>
                <span style="font-size: 12px; color: #2962FF; background: rgba(41, 98, 255, 0.15); padding: 2px 6px; border-radius: 4px; font-weight: 500; display: flex; align-items: center; gap: 3px;">
                  <svg width="10" height="10" viewBox="0 0 24 24" fill="#FFC107" style="display: block;">
                    <path d="M13.5 3C13.5 3 13.5 3 13.5 3C10.5 5 8.5 8 8.5 11.5C8.5 12 8.5 12.5 8.5 13C6.5 14 5.5 16 5.5 18C5.5 21 8 23.5 11 23.5C14 23.5 16.5 21 16.5 18C16.5 16 15.5 14 13.5 13C13.5 12.5 13.5 12 13.5 11.5C13.5 8 11.5 5 8.5 3" fill="#FFC107"/>
                    <path d="M12 23c4.97 0 9-4.03 9-9 0-4.97-9-13-9-13S3 9.03 3 14c0 4.97 4.03 9 9 9z" fill="#FFC107"/>
                  </svg>
                  热度
                </span>
              </div>
            </div>`
          }
          return `<div style="font-size: 13px; color: #fff; padding: 4px 8px;">${params.name}</div>`
        }
      },
      visualMap: {
        show: false,
        min: 0,
        max: 10,
        inRange: {
          color: ['#1A1D21', '#23272E', '#2C323D'] // 极简深色阶，不突兀
        }
      },
      geo: {
        map: 'china',
        roam: true,
        zoom: 1.2,
        center: [105, 36],
        label: {
          show: false, // 默认隐藏文字，保持画面干净
          color: 'rgba(255, 255, 255, 0.4)',
          fontSize: 10
        },
        itemStyle: {
          areaColor: '#16171A', // 接近背景色
          borderColor: '#373A40', // 细腻的深灰边框
          borderWidth: 0.8, // 极细边框
          shadowColor: 'rgba(0, 0, 0, 0.2)',
          shadowBlur: 0
        },
        emphasis: {
          label: { 
            show: true, 
            color: '#fff',
            fontSize: 12
          },
          itemStyle: {
            areaColor: '#2962FF', // 鲜艳的品牌蓝
            borderColor: '#fff',
            borderWidth: 1,
            shadowColor: 'rgba(41, 98, 255, 0.4)',
            shadowBlur: 15 // 柔和辉光
          }
        },
        select: {
           label: { show: true, color: '#fff' },
           itemStyle: {
              areaColor: '#2962FF',
              borderColor: '#fff',
              shadowColor: 'rgba(41, 98, 255, 0.4)',
              shadowBlur: 15
           }
        }
      },
      series: [
        {
          name: '投资信息',
          type: 'map',
          geoIndex: 0,
          data: mapData.value || []
        },
        {
          type: 'effectScatter',
          coordinateSystem: 'geo',
          data: [], 
          symbolSize: 6,
          showEffectOn: 'render',
          rippleEffect: { 
            brushType: 'fill', // 实心涟漪
            scale: 4,
            period: 3
          },
          itemStyle: {
            color: '#00F0FF', // 赛博青
            shadowBlur: 10,
            shadowColor: '#00F0FF'
          },
          zlevel: 1
        }
      ]
    }

    chartInstance.setOption(option)
    
    chartInstance.on('click', async (params) => {
      if (params.name) {
        await handleProvinceClick(params.name)
      }
    })
    
    return true
  } catch (error) {
    console.error('Map init failed:', error)
    return false
  }
}

const loadMapData = async () => {
  try {
    const res = await getMapData()
    if (res.data && res.data.mapData) {
      mapData.value = res.data.mapData
      if (chartInstance) {
        chartInstance.setOption({ series: [{ data: mapData.value }] })
      }
    }
  } catch (error) {
    console.error('Load map data failed:', error)
    mapData.value = [] // Fallback
  }
}

const handleProvinceClick = async (provinceName) => {
  selectedProvince.value = provinceName
  try {
    const res = await getInvestmentByProvince(provinceName, 100)
    
    // 更新股票列表
    investmentList.value = (res.data.topStocks || []).map(item => ({
      ...item,
      changePercent: item.tenDayChange ? item.tenDayChange.toFixed(2) : '0.00'
    }))
    
    // 高亮处理
    if (chartInstance) {
      const highlightData = mapData.value.map(item => {
        if (item.name === provinceName) {
          return { ...item, itemStyle: { areaColor: '#2962FF', borderColor: '#fff' } } // 选中蓝
        }
        return item
      })
      
      chartInstance.setOption({
        series: [{ data: highlightData }]
      })
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      customClass: 'dark-dialog'
    })
    
    try {
      await logout()
    } catch (error) {
      console.warn('Logout API failed, forcing local logout:', error)
    }
    
    userStore.clearUser()
    router.push('/login')
  } catch (e) {
    // User cancelled or other error
  }
}

const handleResize = () => {
  if (chartInstance) chartInstance.resize()
}

onMounted(async () => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  
  await new Promise(resolve => setTimeout(resolve, 200))
  await loadMapData()
  initMap()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (chartInstance) chartInstance.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* --- OKX Style Variables --- */
:root {
  --okx-bg: #0B0E11; /* 更深邃的背景 */
  --okx-panel-bg: #15181C; /* 略微提升的面板色 */
  --okx-border: rgba(255, 255, 255, 0.08);
  --okx-text-primary: #EAECEF;
  --okx-text-secondary: #848E9C;
  --okx-up: #0ECB81;
  --okx-down: #F6465D;
  --okx-accent: #2962FF;
  --okx-hover: rgba(255, 255, 255, 0.06);
  --okx-glow: rgba(41, 98, 255, 0.4); /* 品牌色辉光 */
}

/* --- Global Layout --- */
.okx-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--okx-bg);
  color: var(--okx-text-primary);
  font-family: 'DINPro', 'Roboto', -apple-system, BlinkMacSystemFont, sans-serif;
  overflow: hidden;
  background-image: radial-gradient(circle at top center, #1F2329 0%, #0B0E11 40%); /* 顶部微光 */
}

/* --- Header --- */
.okx-header {
  height: 64px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  background-color: rgba(24, 26, 32, 0.8); /* 半透明 */
  backdrop-filter: blur(20px); /* 磨砂玻璃 */
  border-bottom: 1px solid var(--okx-border);
  flex-shrink: 0;
  z-index: 100;
}

.header-left, .header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo-box {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  font-size: 18px;
  color: #fff;
  letter-spacing: 0.5px;
}

.logo-symbol {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #2962FF, #0039CB); /* 渐变Logo */
  color: #fff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  box-shadow: 0 4px 12px rgba(41, 98, 255, 0.3);
}

.header-divider {
  width: 1px;
  height: 20px;
  background-color: var(--okx-border);
}

.main-nav {
  display: flex;
  gap: 32px;
}

.nav-link {
  color: var(--okx-text-secondary);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  position: relative;
  padding: 20px 0;
}

.nav-link:hover {
  color: #fff;
}

.nav-link.active {
  color: #fff;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background-color: var(--okx-accent);
  border-radius: 3px 3px 0 0;
  box-shadow: 0 -2px 8px var(--okx-glow);
}

.nav-link.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.badge-new {
  font-size: 9px;
  background: linear-gradient(90deg, #F6465D, #FF6B81);
  color: #fff;
  padding: 1px 5px;
  border-radius: 3px;
  margin-left: 4px;
  font-weight: 700;
  vertical-align: super;
}

.search-box {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 8px;
  padding: 8px 16px;
  width: 240px;
  border: 1px solid transparent;
  transition: all 0.3s;
}

.search-box:focus-within {
  background: rgba(255, 255, 255, 0.08);
  border-color: var(--okx-accent);
  box-shadow: 0 0 0 2px rgba(41, 98, 255, 0.2);
}

.search-box input {
  background: transparent;
  border: none;
  color: #fff;
  margin-left: 10px;
  font-size: 13px;
  width: 100%;
}

.search-box input:focus {
  outline: none;
}

.action-icons {
  display: flex;
  gap: 20px;
  color: var(--okx-text-secondary);
}

.icon-btn {
  cursor: pointer;
  font-size: 20px;
  transition: all 0.2s;
}

.icon-btn:hover {
  color: #fff;
  transform: translateY(-1px);
}

.avatar-circle {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #474D57, #2B3139);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid rgba(255,255,255,0.1);
}

.avatar-circle:hover {
  transform: scale(1.05);
  border-color: rgba(255,255,255,0.3);
}

/* --- Ticker Bar --- */
.ticker-bar {
  height: 40px;
  background: rgba(0, 247, 210, 0.1); /* 纳斯达克青背景 */
  display: flex;
  align-items: center;
  border-bottom: 1px solid rgba(0, 247, 210, 0.2);
  font-size: 12px;
  flex-shrink: 0;
  overflow: hidden;
  position: relative;
  mask-image: linear-gradient(to right, transparent, black 5%, black 95%, transparent); /* 两侧渐隐 */
}

.ticker-track {
  display: flex;
  gap: 60px; /* 增加间距 */
  padding-left: 24px;
  animation: ticker-scroll 30s linear infinite;
  white-space: nowrap;
}

.ticker-bar:hover .ticker-track {
  animation-play-state: paused;
}

@keyframes ticker-scroll {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); } /* 假设两组内容完全一样，移动50%即可无缝衔接 */
}

.ticker-item {
  display: flex;
  gap: 8px;
  align-items: center;
  opacity: 0.8;
  transition: all 0.2s;
  cursor: pointer;
  flex-shrink: 0;
}

.ticker-item:hover {
  opacity: 1;
  transform: scale(1.05);
  z-index: 10;
  position: relative;
}

.ticker-item.highlight {
  color: #fff;
  opacity: 1;
}

.ticker-item.nasdaq {
  opacity: 1;
}

.ticker-item.nasdaq .pair {
  color: #00F7D2;
  font-weight: 700;
}

.ticker-item .pair {
  color: var(--okx-text-secondary);
  font-weight: 500;
}

.ticker-item .price {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 600;
}

/* --- Main Trading Area --- */
.trading-main {
  flex: 1;
  display: flex;
  overflow: hidden;
  gap: 1px; /* 极细分割线 */
  background-color: var(--okx-border); /* 利用gap做边框 */
}

/* Left Panel: Market List */
.panel-left {
  width: 300px;
  display: flex;
  flex-direction: column;
  background: var(--okx-bg);
  padding: 12px;
}

.panel-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.08), transparent);
  margin: 12px 8px;
  flex-shrink: 0;
}

.panel-header {
  height: 48px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid var(--okx-border);
  background: transparent;
  gap: 24px;
}

.market-sentiment {
  padding: 16px;
  background: var(--okx-panel-bg);
  border-radius: 8px;
  border: 1px solid var(--okx-border);
  /* A股特有颜色定义：红涨绿跌 */
  --sentiment-up: #F6465D;
  --sentiment-down: #0ECB81;
}

.market-card-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: var(--okx-panel-bg);
  border-radius: 8px;
  border: 1px solid var(--okx-border);
  overflow: hidden;
}

.market-sentiment .text-up { color: var(--sentiment-up) !important; }
.market-sentiment .text-down { color: var(--sentiment-down) !important; }

.sentiment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 13px;
}

.index-info {
  display: flex;
  gap: 8px;
  align-items: center;
}

.index-name {
  color: var(--okx-text-secondary);
  font-weight: 500;
}

.index-value {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 600;
}

.index-change {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 500;
}

.sentiment-chart {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.bar-labels {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  font-family: 'JetBrains Mono', monospace;
}

.label-up { color: var(--sentiment-up); }
.label-flat { color: var(--okx-text-secondary); }
.label-down { color: var(--sentiment-down); }

.distribution-bar {
  height: 10px; /* 稍微加高 */
  background: #2C323D;
  border-radius: 2px; /* 减小圆角，更像柱子 */
  display: flex;
  overflow: hidden;
  width: 100%;
  gap: 2px; /* 增加间隔，形成独立的柱子感 */
  padding: 2px; /* 内边距 */
}

.bar-segment {
  height: 100%;
  transition: width 0.5s ease;
  border-radius: 1px;
}

.bar-segment.up { background: var(--sentiment-up); }
.bar-segment.flat { background: var(--okx-text-secondary); opacity: 0.5; }
.bar-segment.down { background: var(--sentiment-down); }

.tab {
  font-size: 14px;
  color: var(--okx-text-secondary);
  cursor: pointer;
  height: 100%;
  display: flex;
  align-items: center;
  border-bottom: 2px solid transparent;
  font-weight: 500;
  transition: all 0.2s;
}

.tab:hover {
  color: #fff;
}

.tab.active {
  color: #fff;
  border-bottom-color: var(--okx-accent);
}

.market-list-header {
  display: flex;
  justify-content: space-between;
  padding: 10px 16px;
  font-size: 12px;
  color: var(--okx-text-secondary);
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}

.market-list-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0; /* 移除左右 padding，由 item 自身控制 */
}

.market-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 2px;
  border-left: 3px solid transparent; /* 移除了 border-radius 以保持整洁 */
}

.market-item:hover {
  background: var(--okx-hover);
}

.market-item.active {
  background: rgba(41, 98, 255, 0.08);
  border-left-color: var(--okx-accent);
}

.item-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--okx-text-primary);
  font-weight: 500;
}

.star-icon {
  color: var(--okx-text-secondary);
  font-size: 12px;
}

.market-item.active .star-icon {
  color: #FCD535;
}

.item-price {
  font-family: 'JetBrains Mono', monospace;
  font-size: 14px;
  font-weight: 500;
}

/* Center Panel: Chart */
.panel-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: var(--okx-bg);
  position: relative;
}

.chart-toolbar {
  height: 56px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  border-bottom: 1px solid var(--okx-border);
  background: var(--okx-panel-bg);
}

.pair-info {
  display: flex;
  align-items: center;
  gap: 32px; /* 增加标题与数据的间距 */
}

.pair-title {
  font-size: 22px; /* 稍微加大标题 */
  margin: 0;
  color: #fff;
  font-weight: 600;
  display: flex;
  align-items: baseline;
}

.pair-title .currency {
  font-size: 12px;
  color: var(--okx-text-secondary);
  font-weight: 400;
  margin-left: 8px;
  background: rgba(255,255,255,0.08);
  padding: 2px 6px;
  border-radius: 4px;
}

.pair-stats {
  display: flex;
  gap: 32px; /* 增加数据项之间的间距 */
}

.stat-box {
  display: flex;
  flex-direction: column;
  font-size: 12px;
  gap: 4px;
}

.stat-box .label {
  color: var(--okx-text-secondary);
  font-size: 11px;
}

.stat-box .value {
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
}

.time-intervals {
  display: flex;
  gap: 4px;
  align-items: center;
  background: rgba(0, 0, 0, 0.2);
  padding: 3px;
  border-radius: 6px;
  border: 1px solid var(--okx-border);
}

.interval {
  padding: 4px 10px;
  font-size: 12px;
  color: var(--okx-text-secondary);
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
  font-weight: 500;
}

.interval:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
}

.interval.active {
  color: #fff;
  background: #2B3139;
  box-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

.setting-icon {
  margin-left: 12px;
  color: var(--okx-text-secondary);
  cursor: pointer;
  font-size: 16px;
  transition: color 0.2s;
}

.setting-icon:hover {
  color: #fff;
}

.chart-container {
  flex: 1;
  position: relative;
  overflow: hidden;
  background-color: #0B0E11;
  /* 优化网格背景：更细腻的科技感 */
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  background-position: center;
}

/* 添加暗角效果，增加深邃感 */
.chart-container::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, transparent 30%, rgba(11, 14, 17, 0.6) 100%);
  pointer-events: none;
}

.full-map-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
  z-index: 1;
}

.china-map {
  width: 100%;
  height: 100%;
}

/* Right Panel: Order Book */
.panel-right {
  width: 320px;
  display: flex;
  flex-direction: column;
  background: var(--okx-panel-bg);
}

.order-book-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-bottom: 1px solid var(--okx-border);
}

.panel-header-sm {
  padding: 12px 16px;
  font-size: 13px;
  color: var(--okx-text-primary);
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255,255,255,0.01);
}

.more-link {
  color: var(--okx-text-secondary);
  font-weight: 400;
  cursor: pointer;
  font-size: 12px;
  transition: color 0.2s;
}

.more-link:hover {
  color: var(--okx-accent);
}

.order-book-header {
  display: flex;
  justify-content: space-between;
  padding: 8px 16px;
  font-size: 11px;
  color: var(--okx-text-secondary);
}

.order-book-header span:nth-child(1) { width: 30%; text-align: left; }
.order-book-header span:nth-child(2) { width: 30%; text-align: right; }
.order-book-header span:nth-child(3) { width: 40%; text-align: right; }

.order-book-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 4px 0;
}

.order-row {
  display: flex;
  justify-content: space-between;
  padding: 3px 16px;
  font-size: 12px;
  font-family: 'JetBrains Mono', monospace;
  position: relative;
  cursor: pointer;
  margin-bottom: 1px;
}

.order-row:hover {
  background: var(--okx-hover);
}

.order-row span {
  z-index: 1;
}

.order-row .price { width: 30%; text-align: left; font-weight: 500; }
.order-row .amount { width: 30%; text-align: right; color: var(--okx-text-primary); }
.order-row .total { width: 40%; text-align: right; color: var(--okx-text-secondary); }

.depth-bar {
  position: absolute;
  top: 0;
  right: 0;
  height: 100%;
  opacity: 0.15;
  z-index: 0;
  transition: width 0.3s ease-out;
}

.depth-bar.up { background: linear-gradient(270deg, rgba(14, 203, 129, 0.5) 0%, rgba(14, 203, 129, 0) 100%); }
.depth-bar.down { background: linear-gradient(270deg, rgba(246, 70, 93, 0.5) 0%, rgba(246, 70, 93, 0) 100%); }

.current-price-row {
  padding: 12px 16px;
  border-top: 1px solid var(--okx-border);
  border-bottom: 1px solid var(--okx-border);
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.02);
}

.current-price {
  font-size: 20px;
  font-weight: 700;
  font-family: 'JetBrains Mono', monospace;
}

.mark-price {
  font-size: 13px;
  color: var(--okx-text-secondary);
}

.trades-section {
  height: 300px;
  display: flex;
  flex-direction: column;
}

.trades-list {
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;
}

.trade-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 16px;
  font-size: 12px;
  font-family: 'JetBrains Mono', monospace;
  transition: background 0.1s;
}

.trade-item:hover {
  background: var(--okx-hover);
}

.trade-item .time { color: var(--okx-text-secondary); width: 30%; }
.trade-item .name { color: var(--okx-text-primary); width: 40%; text-align: left; font-weight: 500; }
.trade-item .price { width: 30%; text-align: right; font-weight: 500; }

/* --- Footer --- */
.okx-footer {
  height: 32px;
  background: #15181C;
  border-top: 1px solid var(--okx-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  font-size: 11px;
  color: var(--okx-text-secondary);
  flex-shrink: 0;
}

.footer-left, .footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  box-shadow: 0 0 6px rgba(14, 203, 129, 0.6);
}

.status-dot.online { background: var(--okx-up); }

.divider { color: #333; }

/* --- Utilities --- */
.text-up { color: var(--okx-up) !important; }
.text-down { color: var(--okx-down) !important; }
.text-right { text-align: right; }

.scrollbar-hide::-webkit-scrollbar {
  width: 4px;
}
.scrollbar-hide::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}
.scrollbar-hide:hover::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
}

/* Star View Styles */
.star-view-wrapper {
  width: 100%;
  height: 100%;
  padding: 0;
  overflow: hidden;
  z-index: 2;
  position: relative;
}



.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: var(--okx-text-secondary);
  font-size: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
</style>