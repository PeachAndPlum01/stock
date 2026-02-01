import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  
  // 安全地解析userInfo，避免JSON.parse错误
  const getUserInfo = () => {
    try {
      const stored = localStorage.getItem('userInfo')
      if (!stored || stored === 'undefined' || stored === 'null') {
        return {}
      }
      return JSON.parse(stored)
    } catch (e) {
      console.warn('解析userInfo失败，使用默认值:', e)
      return {}
    }
  }
  
  const userInfo = ref(getUserInfo())

  // 设置token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 清除用户信息
  const clearUser = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    clearUser
  }
})
