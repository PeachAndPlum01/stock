import request from './request'

/**
 * 用户登录
 */
export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export const logout = () => {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  return request({
    url: '/auth/userinfo',
    method: 'get'
  })
}

/**
 * 用户注册
 */
export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * VIP注册
 */
export const registerVip = (data) => {
  return request({
    url: '/auth/vip/register',
    method: 'post',
    data
  })
}

/**
 * 发送验证码
 * type: 'phone' | 'email'
 */
export const sendCaptcha = (data) => {
  return request({
    url: '/auth/captcha',
    method: 'post',
    data
  })
}

/**
 * 手机验证码登录
 */
export const loginByPhone = (data) => {
  return request({
    url: '/auth/login/phone',
    method: 'post',
    data
  })
}

/**
 * 根据省份获取投资信息
 */
export const getInvestmentByProvince = (province, limit = 10) => {
  return request({
    url: `/investment/province/${province}`,
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取地图数据
 */
export const getMapData = () => {
  return request({
    url: '/investment/map/data',
    method: 'get'
  })
}
