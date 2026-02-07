<template>
  <div class="login-container">
    <!-- 鲨鱼背景动画 -->
    <div class="ocean-background">
      <div class="shark-wrapper">
        <div class="shark">
          <!-- 修复版极高精度赛博朋克机械鲨鱼 -->
          <svg viewBox="0 0 800 400" xmlns="http://www.w3.org/2000/svg" class="shark-svg">
            <defs>
              <!-- 复杂的光晕滤镜 -->
              <filter id="complex-glow" x="-50%" y="-50%" width="200%" height="200%">
                <feGaussianBlur stdDeviation="2" result="blur"/>
                <feSpecularLighting in="blur" surfaceScale="5" specularConstant=".75" specularExponent="20" lighting-color="#00f3ff" result="specOut">
                  <fePointLight x="-5000" y="-10000" z="20000"/>
                </feSpecularLighting>
                <feComposite in="specOut" in2="SourceAlpha" operator="in" result="specOut"/>
                <feComposite in="SourceGraphic" in2="specOut" operator="arithmetic" k1="0" k2="1" k3="1" k4="0" result="litPaint"/>
                <feGaussianBlur stdDeviation="5" in="SourceGraphic" result="glow"/>
                <feMerge>
                  <feMergeNode in="glow"/>
                  <feMergeNode in="litPaint"/>
                </feMerge>
              </filter>
              
              <!-- 碳纤维纹理 -->
              <pattern id="carbon-fiber" x="0" y="0" width="10" height="10" patternUnits="userSpaceOnUse">
                <rect width="10" height="10" fill="#0a1a2a"/>
                <path d="M0,10 L10,0 M-2,2 L2,-2 M8,12 L12,8" stroke="#1a2a3a" stroke-width="1"/>
              </pattern>

              <!-- 能量核心渐变 -->
              <radialGradient id="core-energy" cx="50%" cy="50%" r="50%" fx="50%" fy="50%">
                <stop offset="0%" stop-color="#fff" stop-opacity="1" />
                <stop offset="40%" stop-color="#00f3ff" stop-opacity="1" />
                <stop offset="100%" stop-color="#00f3ff" stop-opacity="0" />
              </radialGradient>

              <!-- 扫描线 -->
              <linearGradient id="scan-line" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" stop-color="#00f3ff" stop-opacity="0" />
                <stop offset="50%" stop-color="#00f3ff" stop-opacity="0.5" />
                <stop offset="100%" stop-color="#00f3ff" stop-opacity="0" />
              </linearGradient>
            </defs>

            <g class="shark-container" filter="url(#complex-glow)">
              
              <!-- === 尾部推进器系统 === -->
              <!-- 使用嵌套组：外层定位，内层动画 -->
              <g transform="translate(50, 200)">
                <g class="thruster-anim">
                    <!-- 主喷口 -->
                    <path d="M0,-20 L60,-30 L60,30 L0,20 Z" fill="#112233" stroke="#ff00de" stroke-width="2" />
                    <rect x="50" y="-25" width="10" height="50" fill="#223344" stroke="#00f3ff" stroke-width="1" />
                    
                    <!-- 辅助喷口 -->
                    <path d="M20,-35 L50,-45 L50,-25 L20,-25 Z" fill="#112233" stroke="#00f3ff" stroke-width="1" />
                    <path d="M20,25 L50,25 L50,45 L20,35 Z" fill="#112233" stroke="#00f3ff" stroke-width="1" />

                    <!-- 粒子火焰 -->
                    <path d="M-10,0 L-80,-25 L-60,0 L-80,25 Z" fill="url(#core-energy)" opacity="0.8">
                      <animate attributeName="d" values="M-10,0 L-80,-25 L-60,0 L-80,25 Z; M-10,0 L-100,-30 L-70,0 L-100,30 Z; M-10,0 L-80,-25 L-60,0 L-80,25 Z" dur="0.08s" repeatCount="indefinite" />
                    </path>
                </g>
              </g>

              <!-- === 身体主体 === -->
              <g transform="translate(120, 200)">
                <g class="body-anim">
                    <!-- 内部骨架 -->
                    <path d="M20,0 L380,0" stroke="#1a3a4a" stroke-width="10" stroke-linecap="round" />
                    
                    <!-- 主装甲板 -->
                    <path d="M0,0 C60,-60 180,-70 300,-40 C360,-30 420,0 450,20 C420,60 300,80 180,70 C100,60 30,30 0,0 Z" fill="url(#carbon-fiber)" stroke="#00f3ff" stroke-width="2" />
                    
                    <!-- 机械细节：散热片 -->
                    <g transform="translate(100, -50)">
                      <path d="M0,0 L20,-10 L40,0 L20,10 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="1" />
                      <path d="M50,5 L70,-5 L90,5 L70,15 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="1" />
                      <path d="M100,10 L120,0 L140,10 L120,20 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="1" />
                    </g>

                    <!-- 能量核心 -->
                    <circle cx="250" cy="10" r="15" fill="url(#core-energy)">
                      <animate attributeName="r" values="15;18;15" dur="2s" repeatCount="indefinite" />
                      <animate attributeName="opacity" values="0.8;1;0.8" dur="2s" repeatCount="indefinite" />
                    </circle>
                    
                    <!-- 能量管路 -->
                    <path d="M265,10 L350,10 L400,20" fill="none" stroke="#00f3ff" stroke-width="2" stroke-dasharray="5,5">
                       <animate attributeName="stroke-dashoffset" from="100" to="0" dur="1s" repeatCount="indefinite" />
                    </path>
                    <path d="M235,10 L150,10 L100,0" fill="none" stroke="#00f3ff" stroke-width="2" stroke-dasharray="5,5">
                       <animate attributeName="stroke-dashoffset" from="0" to="100" dur="1s" repeatCount="indefinite" />
                    </path>

                    <!-- 侧面装甲接缝 -->
                    <path d="M150,-60 L150,60 M250,-45 L250,65 M350,-25 L350,45" stroke="#00f3ff" stroke-width="1" opacity="0.3" />
                    
                    <!-- 机械鳃 (动态开合) -->
                    <g transform="translate(280, -15)">
                      <path d="M0,0 L10,0" stroke="#00f3ff" stroke-width="4">
                        <animate attributeName="d" values="M0,0 L10,0; M0,0 L10,5; M0,0 L10,0" dur="1s" repeatCount="indefinite" />
                      </path>
                      <path d="M15,0 L25,0" stroke="#00f3ff" stroke-width="4">
                        <animate attributeName="d" values="M15,0 L25,0; M15,0 L25,5; M15,0 L25,0" dur="1s" begin="0.1s" repeatCount="indefinite" />
                      </path>
                      <path d="M30,0 L40,0" stroke="#00f3ff" stroke-width="4">
                        <animate attributeName="d" values="M30,0 L40,0; M30,0 L40,5; M30,0 L40,0" dur="1s" begin="0.2s" repeatCount="indefinite" />
                      </path>
                    </g>

                    <!-- 背鳍 (带天线) -->
                    <g transform="translate(220, -65)">
                      <path d="M0,0 L40,-80 L80,10 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="2" />
                      <line x1="40" y1="-80" x2="20" y2="-100" stroke="#ff00de" stroke-width="1" />
                      <circle cx="20" cy="-100" r="2" fill="#ff00de">
                        <animate attributeName="opacity" values="1;0;1" dur="0.5s" repeatCount="indefinite" />
                      </circle>
                    </g>
                    
                    <!-- 腹鳍 -->
                    <path d="M220,65 L240,110 L280,75 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="2" />
                    
                    <!-- 全息数据投影 -->
                    <g transform="translate(250, -80)" opacity="0.6">
                       <rect x="0" y="0" width="60" height="20" fill="none" stroke="#00f3ff" stroke-width="0.5" />
                       <path d="M5,10 L55,10" stroke="#00f3ff" stroke-width="0.5" stroke-dasharray="2,2" />
                       <text x="5" y="15" fill="#00f3ff" font-size="8" font-family="monospace">SYS:OK</text>
                    </g>
                </g>
              </g>

              <!-- === 头部 === -->
              <!-- 使用嵌套组：外层定位，内层动画 -->
              <g transform="translate(570, 220)">
                <g class="head-anim">
                    <!-- 头部装甲 -->
                    <path d="M0,0 C40,-15 80,-10 120,10 C80,40 40,35 0,25 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="2" />
                    
                    <!-- 机械下颚 -->
                    <path d="M20,25 L100,25 L80,40 L20,35 Z" fill="#112233" stroke="#00f3ff" stroke-width="1" />
                    
                    <!-- 眼睛 (多重结构) -->
                    <g transform="translate(60, 10)">
                      <circle cx="0" cy="0" r="8" fill="#000" stroke="#ff0000" stroke-width="1" />
                      <circle cx="0" cy="0" r="4" fill="#ff0000">
                        <animate attributeName="opacity" values="1;0.5;1" dur="0.2s" repeatCount="indefinite" />
                      </circle>
                      <!-- 扫描光束 -->
                      <path d="M0,0 L150,-60 L150,60 Z" fill="url(#scan-line)" opacity="0.3">
                        <animateTransform attributeName="transform" type="rotate" values="-10 0 0; 10 0 0; -10 0 0" dur="2s" repeatCount="indefinite" />
                      </path>
                    </g>

                    <!-- 牙齿 (激光刃) -->
                    <path d="M30,25 L40,30 L50,25 L60,30 L70,25" fill="none" stroke="#fff" stroke-width="2" filter="url(#complex-glow)" />
                </g>
              </g>
              
              <!-- === 尾鳍 === -->
              <!-- 使用嵌套组：外层定位，内层动画 -->
              <g transform="translate(120, 200)">
                 <g class="tail-anim">
                     <path d="M0,0 C-40,-50 -80,-100 -60,-130 C-30,-100 0,-50 10,-10 C0,30 -30,80 -60,110 C-80,80 -40,30 0,0 Z" fill="url(#carbon-fiber)" stroke="#00f3ff" stroke-width="2" />
                     <!-- 尾鳍加强筋 -->
                     <path d="M-20,-60 L-10,-20 M-20,60 L-10,20" stroke="#00f3ff" stroke-width="1" />
                 </g>
              </g>
            </g>
          </svg>
        </div>
      </div>
      <div class="bubbles">
        <span v-for="n in 10" :key="n" :style="{ '--i': n }"></span>
      </div>
    </div>

    <div class="login-box">
      <div class="login-header">
        <h2>天问</h2>
        <p>Next-Gen Investment Community</p>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs" stretch>
        <el-tab-pane label="密码登录" name="password">
          <el-form
            v-if="activeTab === 'password'"
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名或邮箱"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-button"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="手机登录" name="phone">
          <el-form
            v-if="activeTab === 'phone'"
            ref="phoneFormRef"
            :model="phoneForm"
            :rules="phoneRules"
            class="login-form"
          >
            <el-form-item prop="phone">
              <el-input
                v-model="phoneForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Iphone"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="code">
              <div class="code-input-group">
                <el-input
                  v-model="phoneForm.code"
                  placeholder="验证码"
                  prefix-icon="Key"
                  size="large"
                  @keyup.enter="handlePhoneLogin"
                />
                <el-button 
                  size="large" 
                  :disabled="!!timer || loading"
                  @click="handleSendCode"
                  class="code-btn"
                >
                  {{ timer ? `${count}s后重发` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-button"
                @click="handlePhoneLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="login-footer">
        <router-link to="/register">注册账号</router-link>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, loginByPhone, sendCaptcha } from '@/api'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('password')
const loginFormRef = ref(null)
const phoneFormRef = ref(null)
const loading = ref(false)

// 密码登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 手机登录表单
const phoneForm = reactive({
  phone: '',
  code: ''
})

const phoneRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ]
}

// 验证码倒计时
const timer = ref(null)
const count = ref(60)

const handleSendCode = async () => {
  // 校验手机号
  if (!phoneForm.phone) {
    ElMessage.warning('请先输入手机号')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(phoneForm.phone)) {
    ElMessage.warning('手机号格式不正确')
    return
  }

  try {
    await sendCaptcha({ target: phoneForm.phone, type: 'phone' })
    ElMessage.success('验证码已发送')
    
    // 开始倒计时
    count.value = 60
    timer.value = setInterval(() => {
      count.value--
      if (count.value <= 0) {
        clearInterval(timer.value)
        timer.value = null
      }
    }, 1000)
  } catch (error) {
    console.error('发送验证码失败：', error)
  }
}

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        handleLoginSuccess(res)
      } catch (error) {
        console.error('登录失败：', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handlePhoneLogin = async () => {
  if (!phoneFormRef.value) return
  
  await phoneFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await loginByPhone(phoneForm)
        handleLoginSuccess(res)
      } catch (error) {
        console.error('登录失败：', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleLoginSuccess = (res) => {
  // 保存token和用户信息
  userStore.setToken(res.data.token)
  // 后端返回的数据结构：{ token, userId, username, nickname }
  userStore.setUserInfo({
    userId: res.data.userId,
    username: res.data.username,
    nickname: res.data.nickname
  })
  
  ElMessage.success('登录成功')
  
  // 跳转到首页
  router.push('/home')
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  background-color: #0f172a; /* 深海蓝背景 */
  background-image: linear-gradient(to bottom, #0f172a, #1e293b);
  display: flex;
  justify-content: center; /* 内容居中 */
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* 海洋背景动画层 */
.ocean-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.shark-wrapper {
  position: absolute;
  top: 50%;
  left: -50%; /* 初始位置在屏幕左侧外 */
  width: 65vw;
  height: 32.5vw;
  transform: translateY(-50%);
  animation: swim 30s linear infinite;
  opacity: 0.8;
  z-index: 0;
}

.shark {
  width: 100%;
  height: 100%;
  animation: float 6s ease-in-out infinite;
}

/* 鲨鱼游动动画 */
.shark-svg {
  width: 100%;
  height: 100%;
  overflow: visible;
}

.head-anim {
  transform-origin: 0 0; /* 围绕连接点旋转 */
  animation: head-swing 4s ease-in-out infinite alternate;
}

.tail-anim {
  transform-origin: 0 0; /* 围绕连接点旋转 */
  animation: tail-swing 3s ease-in-out infinite alternate;
}

/* 推进器火焰闪烁 */
.thruster-anim path {
  animation: flicker 0.1s infinite alternate;
}

@keyframes head-swing {
  from { transform: rotate(3deg); }
  to { transform: rotate(-3deg); }
}

@keyframes tail-swing {
  from { transform: rotate(8deg); }
  to { transform: rotate(-8deg); }
}

@keyframes swim {
  0% {
    left: -60%;
    transform: translateY(-50%) scaleX(1); /* 向右游 */
  }
  45% {
    left: 120%;
    transform: translateY(-50%) scaleX(1);
  }
  50% {
    left: 120%;
    transform: translateY(-50%) scaleX(-1); /* 翻转，向左游 */
  }
  95% {
    left: -60%;
    transform: translateY(-50%) scaleX(-1);
  }
  100% {
    left: -60%;
    transform: translateY(-50%) scaleX(1); /* 恢复 */
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-30px); }
}

@keyframes flicker {
  0% { opacity: 0.8; }
  100% { opacity: 0.4; }
}

/* 气泡动画 */
.bubbles {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.bubbles span {
  position: absolute;
  bottom: -20px;
  width: 20px;
  height: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: rise 10s infinite ease-in;
  animation-delay: calc(var(--i) * 1s);
  left: calc(var(--i) * 10%);
}

@keyframes rise {
  0% {
    bottom: -20px;
    transform: translateX(0);
  }
  50% {
    transform: translateX(20px);
  }
  100% {
    bottom: 100%;
    transform: translateX(-20px);
  }
}

.login-box {
  position: relative;
  z-index: 1; /* 确保登录框在鲨鱼上面 */
  width: 420px;
  padding: 40px;
  background: rgba(15, 23, 42, 0.6); /* 半透明深色背景 */
  backdrop-filter: blur(12px); /* 毛玻璃效果 */
  border-radius: 12px;
  box-shadow: 0 0 30px rgba(0, 243, 255, 0.15), inset 0 0 20px rgba(0, 243, 255, 0.05);
  border: 1px solid rgba(0, 243, 255, 0.3); /* 青色微光边框 */
  transition: all 0.3s ease;
}

.login-box:hover {
  box-shadow: 0 0 40px rgba(0, 243, 255, 0.25), inset 0 0 30px rgba(0, 243, 255, 0.1);
  border-color: rgba(0, 243, 255, 0.5);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 32px;
  color: #00f3ff; /* 霓虹青 */
  margin-bottom: 10px;
  font-weight: 700;
  letter-spacing: 4px;
  text-shadow: 0 0 10px rgba(0, 243, 255, 0.6);
  text-transform: uppercase;
}

.login-header p {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 1px;
  text-transform: uppercase;
}

.login-tabs {
  margin-bottom: 20px;
}

/* 覆盖 Element Plus Tabs 样式 */
:deep(.el-tabs__item) {
  color: rgba(255, 255, 255, 0.6);
  font-size: 16px;
  transition: all 0.3s;
}
:deep(.el-tabs__item.is-active) {
  color: #00f3ff;
  text-shadow: 0 0 8px rgba(0, 243, 255, 0.4);
}
:deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(255, 255, 255, 0.1);
}
:deep(.el-tabs__active-bar) {
  background-color: #00f3ff;
  box-shadow: 0 0 10px #00f3ff;
}

/* 覆盖 Element Plus Input 样式 */
:deep(.el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.4) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1) inset !important;
  transition: all 0.3s;
}
:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #00f3ff inset !important;
  background-color: rgba(0, 0, 0, 0.6) !important;
}
:deep(.el-input__inner) {
  color: #fff !important;
  background: transparent !important;
}
:deep(.el-input__prefix-inner) {
  color: rgba(255, 255, 255, 0.5);
}

/* 修复浏览器自动填充导致的白色背景 */
:deep(.el-input__inner:-webkit-autofill),
:deep(.el-input__inner:-webkit-autofill:hover),
:deep(.el-input__inner:-webkit-autofill:focus),
:deep(.el-input__inner:-webkit-autofill:active) {
  -webkit-text-fill-color: #fff !important;
  -webkit-box-shadow: 0 0 0 1000px transparent inset !important;
  transition: background-color 5000s ease-in-out 0s;
  caret-color: #fff;
}

.login-form {
  margin-top: 10px;
}

.login-button {
  width: 100%;
  margin-top: 10px;
  font-weight: 600;
  background: linear-gradient(90deg, #00f3ff, #0066ff);
  border: none;
  height: 45px;
  font-size: 16px;
  letter-spacing: 2px;
  box-shadow: 0 0 15px rgba(0, 243, 255, 0.3);
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 25px rgba(0, 243, 255, 0.5);
  background: linear-gradient(90deg, #00ffff, #0088ff);
}

.code-input-group {
  display: flex;
  gap: 10px;
}

.code-btn {
  width: 120px;
  background-color: rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.8);
}
.code-btn:hover {
  border-color: #00f3ff;
  color: #00f3ff;
  background-color: rgba(0, 243, 255, 0.1);
}

.login-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
}

.login-footer a {
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  transition: color 0.3s;
  position: relative;
}

.login-footer a:hover {
  color: #00f3ff;
  text-shadow: 0 0 5px rgba(0, 243, 255, 0.5);
}

.vip-link {
  color: #ffd700 !important; /* 金色 */
  font-weight: bold;
}

.divider {
  margin: 0 10px;
  color: var(--border-color);
}

</style>