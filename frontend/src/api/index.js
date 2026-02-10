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

/**
 * 获取股吧帖子列表
 */
export const getPostList = (page = 1, size = 10) => {
  return request({
    url: '/investment/bar/posts',
    method: 'get',
    params: { page, size }
  })
}

/**
 * 发布帖子
 */
export const createPost = (data) => {
  return request({
    url: '/investment/bar/post',
    method: 'post',
    data
  })
}

/**
 * 获取帖子评论
 */
export const getComments = (postId) => {
  return request({
    url: `/investment/bar/post/${postId}/comments`,
    method: 'get'
  })
}

/**
 * 发表评论
 */
export const createComment = (data) => {
  return request({
    url: '/investment/bar/comment',
    method: 'post',
    data
  })
}

/**
 * 获取热门股票
 */
export const getHotStocks = () => {
  return request({
    url: '/investment/bar/hot-stocks',
    method: 'get'
  })
}

/**
 * 获取观星台图谱数据
 */
export const getStarGraph = () => {
  return request({
    url: '/investment/star/graph',
    method: 'get'
  })
}

/**
 * 获取观星台初始分析
 */
export const getStarAnalysis = () => {
  return request({
    url: '/investment/star/analyze',
    method: 'get'
  })
}

/**
 * 观星台提问
 */
export const chatWithStar = (question) => {
  return request({
    url: '/investment/star/chat',
    method: 'post',
    data: { question }
  })
}