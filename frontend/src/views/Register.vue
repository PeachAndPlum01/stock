<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>欢迎加入天问投资信息系统</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-width="0"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Iphone"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱（可选）"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            注 册
          </el-button>
        </el-form-item>

        <div class="form-footer">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
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
.register-container {
  width: 100%;
  height: 100vh;
  background-color: #0f172a; /* 深海蓝背景 */
  background-image: linear-gradient(to bottom, #0f172a, #1e293b);
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-box {
  width: 460px;
  padding: 40px;
  background: rgba(15, 23, 42, 0.6); /* 半透明深色背景 */
  backdrop-filter: blur(12px); /* 毛玻璃效果 */
  border-radius: 12px;
  box-shadow: 0 0 30px rgba(0, 243, 255, 0.15), inset 0 0 20px rgba(0, 243, 255, 0.05);
  border: 1px solid rgba(0, 243, 255, 0.3); /* 青色微光边框 */
  transition: all 0.3s ease;
}

.register-box:hover {
  box-shadow: 0 0 40px rgba(0, 243, 255, 0.25), inset 0 0 30px rgba(0, 243, 255, 0.1);
  border-color: rgba(0, 243, 255, 0.5);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  font-size: 32px;
  color: #00f3ff; /* 霓虹青 */
  margin-bottom: 10px;
  font-weight: 700;
  letter-spacing: 4px;
  text-shadow: 0 0 10px rgba(0, 243, 255, 0.6);
  text-transform: uppercase;
}

.register-header p {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 1px;
  text-transform: uppercase;
}

.register-form {
  margin-top: 20px;
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

.register-button {
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
  color: #fff;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 25px rgba(0, 243, 255, 0.5);
  background: linear-gradient(90deg, #00ffff, #0088ff);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.form-footer a {
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.form-footer a:hover {
  color: #00f3ff;
  text-shadow: 0 0 5px rgba(0, 243, 255, 0.5);
  text-decoration: none;
}
</style>
