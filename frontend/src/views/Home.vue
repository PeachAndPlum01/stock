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
          <el-menu-item index="concept-analysis">
            <el-icon><TrendCharts /></el-icon>
            <span>概念选相关度</span>
          </el-menu-item>
          <el-menu-item index="discussion">
            <el-icon><ChatDotRound /></el-icon>
            <span>讨论区</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 主内容区 -->
      <div class="main-content" :class="{ 'content-expanded': isCollapsed }">
        <!-- 左侧地图 -->
        <div class="map-section">
          <div class="section-title">中国投资地图</div>
          <div ref="mapRef" class="china-map"></div>
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
import { Location, TrendCharts, ChatDotRound, Expand, Fold } from '@element-plus/icons-vue'
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
  activeMenu.value = index
  // 根据选择的菜单项处理不同的功能
  switch (index) {
    case 'region-analysis':
      // 地区选相关度功能（当前已有）
      ElMessage.info('已切换到地区选相关度功能')
      break
    case 'concept-analysis':
      // 概念选相关度功能（未来扩展）
      ElMessage.info('概念选相关度功能开发中...')
      break
    case 'discussion':
      // 讨论区功能（未来扩展）
      ElMessage.info('讨论区功能开发中...')
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
        selectedMode: 'single', // 允许选中单个省份
        silent: false, // 确保地图可以交互
        label: {
          show: true,
          fontSize: function(params) {
            // 根据省份名称动态调整字体大小
            const smallAreas = ['澳门特别行政区', '香港特别行政区', '台湾', '上海', '北京', '天津', '重庆'];
            const mediumAreas = ['海南', '宁夏', '青海', '甘肃'];
            
            if (smallAreas.includes(params.name)) {
              return 8; // 小区域使用更小的字体
            } else if (mediumAreas.includes(params.name)) {
              return 9; // 中等区域使用中等字体
            } else {
              return 10; // 大区域使用正常字体
            }
          },
          color: '#333',
          fontWeight: 'normal'
        },
        emphasis: {
          label: {
            show: true,
            color: '#fff',
            fontSize: function(params) {
              // 高亮状态下也保持相对大小比例
              const smallAreas = ['澳门特别行政区', '香港特别行政区', '台湾', '上海', '北京', '天津', '重庆'];
              const mediumAreas = ['海南', '宁夏', '青海', '甘肃'];
              
              if (smallAreas.includes(params.name)) {
                return 9;
              } else if (mediumAreas.includes(params.name)) {
                return 10;
              } else {
                return 12;
              }
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
            fontSize: function(params) {
              const smallAreas = ['澳门特别行政区', '香港特别行政区', '台湾', '上海', '北京', '天津', '重庆'];
              const mediumAreas = ['海南', '宁夏', '青海', '甘肃'];
              
              if (smallAreas.includes(params.name)) {
                return 9;
              } else if (mediumAreas.includes(params.name)) {
                return 10;
              } else {
                return 12;
              }
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

// 标准化省份名称(去除"省"、"市"、"自治区"等后缀)
const normalizeProvinceName = (name) => {
  // 先去除民族名称和行政区划后缀，如"维吾尔自治区" -> ""
  return name.replace(/(壮族|回族|维吾尔|藏族|蒙古|朝鲜族)?(自治区|省|市|特别行政区)/g, '')
}

// 处理省份点击
const handleProvinceClick = async (provinceName) => {
  selectedProvince.value = provinceName
  
  // 标准化省份名称用于查询
  const normalizedName = normalizeProvinceName(provinceName)
  
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

    // 高亮关联性最强的三个省份
    if (chartInstance && relatedProvinces.value.length > 0) {
      // 首先重置所有省份的颜色
      const resetData = mapData.value.map(item => ({
        ...item,
        itemStyle: {
          areaColor: '#e0f3f8',
          borderColor: '#fff',
          borderWidth: 1
        }
      }))

      // 高亮当前选中的省份
      const highlightData = resetData.map(item => {
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
        // 高亮关联性最强的三个省份
        if (relatedProvinces.value.includes(item.name)) {
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
  await loadMapData()
  initMap()
  window.addEventListener('resize', handleResize)
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