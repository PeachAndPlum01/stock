<template>
  <div class="vip-register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>VIP 会员注册</h2>
        <p>尊享高级投资分析服务</p>
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

        <el-form-item prop="vipLevel">
          <el-select v-model="registerForm.vipLevel" placeholder="请选择VIP等级" size="large" style="width: 100%">
            <el-option label="白银会员 (¥99/月)" value="silver" />
            <el-option label="黄金会员 (¥199/月)" value="gold" />
            <el-option label="钻石会员 (¥299/月)" value="diamond" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="warning"
            size="large"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            立即开通 VIP
          </el-button>
        </el-form-item>

        <div class="form-footer">
          <router-link to="/login">已有账号？去登录</router-link>
          <span class="divider">|</span>
          <router-link to="/register">普通注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerVip } from '@/api'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  vipLevel: 'silver'
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
  vipLevel: [
    { required: true, message: '请选择VIP等级', trigger: 'change' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { confirmPassword, ...submitData } = registerForm
        await registerVip(submitData)
        ElMessage.success('VIP开通成功，请登录')
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
.vip-register-container {
  width: 100%;
  height: 100vh;
  background-color: var(--bg-color);
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-box {
  width: 460px;
  padding: 40px;
  background: var(--card-bg-color);
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(255, 215, 0, 0.1);
  border: 1px solid #ffd700; /* 保持金色边框 */
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  font-size: 24px;
  color: var(--text-primary);
  margin-bottom: 10px;
  font-weight: 600;
}

.register-header p {
  font-size: 14px;
  color: #ffd700; /* 金色文字 */
  font-weight: bold;
}

.register-form {
  margin-top: 20px;
}

.register-button {
  width: 100%;
  margin-top: 10px;
  background-color: #b8860b;
  border-color: #b8860b;
  color: #fff;
  font-weight: 600;
}

.register-button:hover {
  background-color: #daa520;
  border-color: #daa520;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.form-footer a {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
}

.form-footer a:hover {
  color: #ffd700;
  text-decoration: underline;
}

.divider {
  margin: 0 10px;
  color: var(--border-color);
}
</style>
