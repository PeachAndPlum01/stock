<template>
  <div class="register-container">
    <div class="grid-background"></div>
    
    <div class="register-wrapper">
      <div class="brand-section">
        <div class="logo-box-lg">
          <div class="logo-symbol-lg">T</div>
        </div>
        <h1 class="brand-title">TIANWEN PRO</h1>
        <p class="brand-slogan">创建您的账户，开启专业交易之旅</p>
      </div>

      <div class="register-card">
        <div class="card-header">
          <h2>注册账号</h2>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          label-width="0"
        >
          <el-form-item prop="username">
            <div class="input-wrapper">
              <el-icon class="input-icon"><User /></el-icon>
              <input 
                v-model="registerForm.username" 
                type="text" 
                placeholder="请输入用户名"
                class="custom-input"
                autocomplete="username"
              />
            </div>
          </el-form-item>
          
          <el-form-item prop="nickname">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Avatar /></el-icon>
              <input 
                v-model="registerForm.nickname" 
                type="text" 
                placeholder="请输入昵称"
                class="custom-input"
                autocomplete="nickname"
              />
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <input 
                v-model="registerForm.password" 
                :type="passwordVisible ? 'text' : 'password'"
                placeholder="设置密码 (不少于6位)"
                class="custom-input"
                autocomplete="new-password"
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

          <el-form-item prop="confirmPassword">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <input 
                v-model="registerForm.confirmPassword" 
                :type="confirmPasswordVisible ? 'text' : 'password'"
                placeholder="确认密码"
                class="custom-input"
                autocomplete="new-password"
              />
              <el-icon 
                class="password-icon" 
                @click="confirmPasswordVisible = !confirmPasswordVisible"
              >
                <View v-if="confirmPasswordVisible" />
                <Hide v-else />
              </el-icon>
            </div>
          </el-form-item>

          <el-form-item prop="phone">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Iphone /></el-icon>
              <input 
                v-model="registerForm.phone" 
                type="text" 
                placeholder="请输入手机号"
                class="custom-input"
                autocomplete="tel"
              />
            </div>
          </el-form-item>

          <el-form-item prop="email">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Message /></el-icon>
              <input 
                v-model="registerForm.email" 
                type="text" 
                placeholder="请输入邮箱（可选）"
                class="custom-input"
                autocomplete="email"
              />
            </div>
          </el-form-item>
          
          <el-form-item>
            <button
              class="submit-btn"
              :disabled="loading"
              @click.prevent="handleRegister"
            >
              <span v-if="!loading">立即注册</span>
              <span v-else>注册中...</span>
            </button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="footer-link">去登录</router-link>
        </div>
      </div>
      
      <div class="copyright">
        © 2024 Tianwen Pro. All rights reserved.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Avatar, Lock, Iphone, Message, View, Hide } from '@element-plus/icons-vue'
import { register } from '@/api'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const passwordVisible = ref(false)
const confirmPasswordVisible = ref(false)

const registerForm = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 移除 confirmPassword 字段再提交
        const { confirmPassword, ...submitData } = registerForm
        await register(submitData)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        console.error('注册失败：', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* --- Variables from Home.vue/Login.vue --- */
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

.register-container {
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

.register-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  width: 100%;
  max-width: 440px; /* 稍微宽一点以容纳更多字段 */
  padding: 0 20px;
  height: 100vh;
  justify-content: center;
  overflow-y: auto; /* 防止小屏幕溢出 */
}

.brand-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
}

.logo-box-lg {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #2962FF, #0039CB);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(41, 98, 255, 0.3);
  margin-bottom: 4px;
}

.logo-symbol-lg {
  font-size: 32px;
  font-weight: 900;
  color: #fff;
}

.brand-title {
  font-size: 24px;
  font-weight: 700;
  color: #EAECEF;
  margin: 0;
  letter-spacing: 1px;
}

.brand-slogan {
  font-size: 13px;
  color: #848E9C;
  margin: 0;
}

.register-card {
  width: 100%;
  background: #15181C;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(20px);
}

.card-header {
  margin-bottom: 24px;
  text-align: center;
}

.card-header h2 {
  font-size: 20px;
  color: #EAECEF;
  margin: 0;
  font-weight: 600;
}

/* Form Styles */
.register-form {
  margin-top: 16px;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid transparent;
  border-radius: 8px;
  padding: 0 16px;
  height: 44px; /* 稍微调小高度以适应更多字段 */
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
  font-size: 16px;
  color: #848E9C;
  margin-right: 12px;
  flex-shrink: 0;
}

.password-icon {
  font-size: 16px;
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

.submit-btn {
  width: 100%;
  height: 44px;
  background: #2962FF;
  border: none;
  border-radius: 8px;
  color: #fff;
  font-size: 15px;
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

.register-footer {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #848E9C;
}

.footer-link {
  color: #2962FF;
  text-decoration: none;
  transition: color 0.2s;
  font-weight: 500;
}

.footer-link:hover {
  color: #1E4BD8;
}

.copyright {
  color: #5E6673;
  font-size: 12px;
  margin-bottom: 20px;
}

/* Element Plus Overrides */
:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__content) {
  width: 100%;
}

:deep(.el-form-item__error) {
  padding-top: 4px;
  color: #F6465D;
}
</style>
