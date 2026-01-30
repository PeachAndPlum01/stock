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
    url: '/auth/userInfo',
    method: 'get'
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
