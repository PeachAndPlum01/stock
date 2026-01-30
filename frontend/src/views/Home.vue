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
          router
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
          <el-menu-item index="star-view" disabled>
            <el-icon><Star /></el-icon>
            <span>观星（开发中）</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 主内容区 -->
      <div class="main-content" :class="{ 'content-expanded': isCollapsed }">
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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, TrendCharts, ChatDotRound, Expand, Fold, Star, Loading } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getMapData, getInvestmentByProvince, logout } from '@/api'
import { useUserStore } from '@/store/user'
import chinaJson from '@/assets/china.json'

const router = useRouter()
const userStore = useUserStore()

const mapRef = ref(null)
let chartInstance = null

const selectedProvince = ref('')
const investmentList = ref([])
const relatedProvinces = ref([])
const mapData = ref([])
const activeMenu = ref('region-analysis') // 默认选中地区选相关度
const isCollapsed = ref(false) // 导航栏是否收缩

// 切换导航栏展开收缩
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

// 菜单选择处理
const handleMenuSelect = (index) => {
  // 如果点击的是禁用菜单项，不改变当前选中状态
  if (['concept-analysis', 'discussion', 'star-view'].includes(index)) {
    return
  }
  
  activeMenu.value = index
  // 根据选择的菜单项处理不同的功能
  switch (index) {
    case 'region-analysis':
      // 地区选相关度功能 - 重置地图状态并显示中国地图
      selectedProvince.value = ''
      investmentList.value = []
      relatedProvinces.value = []
      
      // 重置地图显示
      if (chartInstance && mapData.value.length > 0) {
        chartInstance.setOption({
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
      
      ElMessage.info('已切换到地区选相关度功能，请点击地图上的省份查看投资信息')
      break
  }
}

// 初始化地图
const initMap = () => {
  if (!mapRef.value) return

  // 注册中国地图
  echarts.registerMap('china', chinaJson)

  chartInstance = echarts.init(mapRef.value)

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

  // 地图点击事件
  chartInstance.on('click', async (params) => {
    if (params.name) {
      await handleProvinceClick(params.name)
    }
  })
}

// 加载地图数据
const loadMapData = async () => {
  try {
    const res = await getMapData()
    mapData.value = res.data.mapData
    
    if (chartInstance) {
      chartInstance.setOption({
        series: [{
          data: mapData.value
        }]
      })
    }
  } catch (error) {
    ElMessage.error('加载地图数据失败')
  }
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
    const res = await getInvestmentByProvince(normalizedName, 10)
    console.log('📊 API返回数据:', res.data)
    console.log('📋 投资列表:', res.data.investmentList)
    console.log('📋 投资列表长度:', res.data.investmentList?.length)
    
    // 修复数据绑定：使用正确的字段名
    investmentList.value = res.data.investmentList || []
    relatedProvinces.value = res.data.relatedProvinces || []
    
    console.log('📋 关联省份数据:', relatedProvinces.value)
    
    // 高亮关联性最强的三个省份
    if (chartInstance && relatedProvinces.value.length > 0) {
      console.log('🔍 关联省份列表:', relatedProvinces.value)
      console.log('🔍 当前选中省份:', provinceName)
      
      // 首先重置所有省份的颜色
      const resetData = mapData.value.map(item => ({
        ...item,
        itemStyle: {
          areaColor: '#e0f3f8',
          borderColor: '#fff',
          borderWidth: 1
        }
      }))

      // 标准化关联省份名称以匹配地图数据中的简称格式
      const normalizedRelatedProvinces = relatedProvinces.value.map(province => {
        // API返回的简称直接使用，无需转换
        return province
      })
      console.log('📊 标准化后的关联省份:', normalizedRelatedProvinces)
      
      // 高亮当前选中的省份和关联省份
      const highlightData = resetData.map(item => {
        if (item.name === provinceName) {
          console.log('🔴 高亮当前省份:', item.name)
          return {
            ...item,
            itemStyle: {
              areaColor: '#ff6b6b',
              borderColor: '#fff',
              borderWidth: 2
            }
          }
        }
        // 高亮关联性最强的三个省份
        if (normalizedRelatedProvinces.includes(item.name)) {
          console.log('🟡 高亮关联省份:', item.name)
          return {
            ...item,
            itemStyle: {
              areaColor: '#ffeb3b',
              borderColor: '#fff',
              borderWidth: 2
            }
          }
        }
        return item
      })

      chartInstance.setOption({
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
            itemStyle: {
              areaColor: '#ff6b6b',
              borderColor: '#fff',
              borderWidth: 2
            }
          }
        }
        return item
      })

      chartInstance.setOption({
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
  try {
    await loadMapData()
    initMap()
    window.addEventListener('resize', handleResize)
    
    // 页面加载完成后显示欢迎信息
    setTimeout(() => {
      ElMessage.success('欢迎使用股票投资信息展示系统！请点击地图上的省份查看投资信息')
    }, 500)
  } catch (error) {
    ElMessage.error('地图初始化失败，请刷新页面重试')
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
  background: linear-gradient(135deg, #007AFF 0%, #0051D5 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
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
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  position: relative;
  z-index: 100;
}

/* 导航栏收缩状态 */
.sidebar-collapsed {
  width: 64px;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
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
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  min-width: 0;
  transition: all 0.3s ease;
}

.info-section {
  width: 450px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 450px;
  transition: all 0.3s ease;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #007AFF;
}

.china-map {
  flex: 1;
  width: 100%;
  min-height: 0;
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

.investment-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}

.investment-card {
  margin-bottom: 15px;
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
</style>