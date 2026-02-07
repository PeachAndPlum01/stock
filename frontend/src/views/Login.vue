<template>
  <div class="login-container">
    <SharkBackground />
    
    <div class="login-wrapper">
      <div class="brand-section">
        <div class="logo-box-lg">
          <div class="logo-symbol-lg">T</div>
        </div>
        <h1 class="brand-title">TIANWEN PRO</h1>
        <p class="brand-slogan">下一代专业数字资产交易终端</p>
      </div>

      <div class="login-card">
        <el-tabs v-model="activeTab" class="custom-tabs" stretch>
          <el-tab-pane label="密码登录" name="password">
            <el-form
              v-if="activeTab === 'password'"
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              class="login-form"
              label-position="top"
            >
              <el-form-item prop="username">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><User /></el-icon>
                  <input 
                    v-model="loginForm.username" 
                    type="text" 
                    placeholder="请输入用户名 / 邮箱"
                    class="custom-input"
                    autocomplete="username"
                  />
                </div>
              </el-form-item>
              
              <el-form-item prop="password">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><Lock /></el-icon>
                  <input 
                    v-model="loginForm.password" 
                    :type="passwordVisible ? 'text' : 'password'"
                    placeholder="请输入密码"
                    class="custom-input"
                    autocomplete="current-password"
                    @keyup.enter="handleLogin"
                  />
                  <el-icon 
                    class="password-icon" 
                    @click="passwordVisible = !passwordVisible"
                  >
                    <View v-if="passwordVisible" />
                    <Hide v-else />
                  </el-icon>
                </div>
              </el-form-item>
              
              <el-form-item>
                <button
                  class="submit-btn"
                  :disabled="loading"
                  @click="handleLogin"
                >
                  <span v-if="!loading">登 录</span>
                  <span v-else>登录中...</span>
                </button>
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
                <div class="input-wrapper">
                  <el-icon class="input-icon"><Iphone /></el-icon>
                  <input 
                    v-model="phoneForm.phone" 
                    type="text" 
                    placeholder="请输入手机号"
                    class="custom-input"
                  />
                </div>
              </el-form-item>
              
              <el-form-item prop="code">
                <div class="code-group">
                  <div class="input-wrapper flex-1">
                    <el-icon class="input-icon"><Key /></el-icon>
                    <input 
                      v-model="phoneForm.code" 
                      type="text" 
                      placeholder="6位验证码"
                      class="custom-input"
                      @keyup.enter="handlePhoneLogin"
                    />
                  </div>
                  <button 
                    class="code-btn"
                    :disabled="!!timer || loading"
                    @click="handleSendCode"
                  >
                    {{ timer ? `${count}s` : '获取验证码' }}
                  </button>
                </div>
              </el-form-item>
              
              <el-form-item>
                <button
                  class="submit-btn"
                  :disabled="loading"
                  @click="handlePhoneLogin"
                >
                  <span v-if="!loading">登 录</span>
                  <span v-else>登录中...</span>
                </button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="login-footer">
          <router-link to="/register" class="footer-link">注册新账号</router-link>
          <span class="divider">|</span>
          <a href="#" class="footer-link">忘记密码?</a>
        </div>
      </div>
      
      <div class="copyright">
        © 2024 Tianwen Pro. All rights reserved.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Key, View, Hide } from '@element-plus/icons-vue'
import { login, loginByPhone, sendCaptcha } from '@/api'
import { useUserStore } from '@/store/user'
import SharkBackground from '@/components/SharkBackground.vue'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('password')
const passwordVisible = ref(false)
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
/* --- Variables from Home.vue --- */
:root {
  --okx-bg: #0B0E11;
  --okx-panel-bg: #15181C;
  --okx-border: rgba(255, 255, 255, 0.08);
  --okx-text-primary: #EAECEF;
  --okx-text-secondary: #848E9C;
  --okx-accent: #2962FF;
  --okx-accent-hover: #1E4BD8;
  --okx-glow: rgba(41, 98, 255, 0.4);
}

.login-container {
  width: 100%;
  height: 100vh;
  background-color: #0B0E11;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  font-family: 'DINPro', 'Roboto', sans-serif;
}

.grid-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  background-position: center;
  z-index: 0;
}

.grid-background::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, transparent 30%, rgba(11, 14, 17, 0.6) 100%);
  pointer-events: none;
}

.login-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
  width: 100%;
  max-width: 400px;
  padding: 0 20px;
}

.brand-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.logo-box-lg {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #2962FF, #0039CB);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(41, 98, 255, 0.3);
  margin-bottom: 8px;
}

.logo-symbol-lg {
  font-size: 36px;
  font-weight: 900;
  color: #fff;
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  color: #EAECEF;
  margin: 0;
  letter-spacing: 1px;
}

.brand-slogan {
  font-size: 14px;
  color: #848E9C;
  margin: 0;
}

.login-card {
  width: 100%;
  background: #15181C;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(20px);
}

/* Custom Tabs */
:deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(255, 255, 255, 0.05);
  height: 1px;
}

:deep(.el-tabs__item) {
  color: #848E9C;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
}

:deep(.el-tabs__item.is-active) {
  color: #EAECEF;
}

:deep(.el-tabs__active-bar) {
  background-color: #2962FF;
  height: 3px;
  border-radius: 3px 3px 0 0;
}

/* Form Styles */
.login-form {
  margin-top: 24px;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid transparent;
  border-radius: 8px;
  padding: 0 16px;
  height: 48px;
  width: 100%;
  transition: all 0.2s;
  box-sizing: border-box;
  overflow: hidden;
}

.input-wrapper:focus-within {
  background: rgba(255, 255, 255, 0.08);
  border-color: #2962FF;
  box-shadow: 0 0 0 2px rgba(41, 98, 255, 0.2);
}

.input-icon {
  font-size: 18px;
  color: #848E9C;
  margin-right: 12px;
  flex-shrink: 0;
}

.password-icon {
  font-size: 18px;
  color: #848E9C;
  cursor: pointer;
  margin-left: 12px;
  transition: color 0.2s;
}

.password-icon:hover {
  color: #EAECEF;
}

.custom-input {
  flex: 1;
  width: 100%;
  min-width: 0;
  background: transparent;
  border: none;
  color: #EAECEF;
  font-size: 14px;
  height: 100%;
  outline: none;
  padding: 0;
  margin: 0;
}

/* 修复浏览器自动填充导致的白色背景 */
.custom-input:-webkit-autofill,
.custom-input:-webkit-autofill:hover,
.custom-input:-webkit-autofill:active {
  -webkit-box-shadow: 0 0 0 1000px #1E2125 inset !important;
  -webkit-text-fill-color: #EAECEF !important;
  caret-color: #EAECEF;
  transition: background-color 5000s ease-in-out 0s;
}

.custom-input:-webkit-autofill:focus {
  -webkit-box-shadow: 0 0 0 1000px #272A2E inset !important;
  -webkit-text-fill-color: #EAECEF !important;
  caret-color: #EAECEF;
  transition: background-color 5000s ease-in-out 0s;
}

.custom-input::placeholder {
  color: #5E6673;
}

.code-group {
  display: flex;
  gap: 12px;
}

.flex-1 {
  flex: 1;
}

.code-btn {
  height: 48px;
  padding: 0 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  color: #EAECEF;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.code-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.08);
  border-color: #848E9C;
}

.code-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.submit-btn {
  width: 100%;
  height: 48px;
  background: #2962FF;
  border: none;
  border-radius: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 8px;
}

.submit-btn:hover:not(:disabled) {
  background: #1E4BD8;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(41, 98, 255, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-footer {
  margin-top: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  font-size: 14px;
}

.footer-link {
  color: #848E9C;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-link:hover {
  color: #2962FF;
}

.divider {
  color: #333;
}

.copyright {
  color: #5E6673;
  font-size: 12px;
}

/* Element Plus Overrides */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__content) {
  width: 100%;
}

:deep(.el-form-item__error) {
  padding-top: 4px;
  color: #F6465D;
}
</style>