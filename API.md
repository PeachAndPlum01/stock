# API接口文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **请求格式**: JSON
- **响应格式**: JSON

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1706601234567
}
```

### 响应码说明
- `200`: 成功
- `401`: 未授权
- `500`: 服务器错误

---

## 1. 认证接口

### 1.1 用户登录

**接口地址**: `POST /auth/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "email": "admin@example.com",
      "phone": null
    }
  },
  "timestamp": 1706601234567
}
```

**错误响应**:
```json
{
  "code": 500,
  "message": "用户不存在",
  "data": null,
  "timestamp": 1706601234567
}
```

---

### 1.2 获取用户信息

**接口地址**: `GET /auth/userInfo`

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@example.com",
    "phone": null
  },
  "timestamp": 1706601234567
}
```

---

### 1.3 用户登出

**接口地址**: `POST /auth/logout`

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1706601234567
}
```

---

## 2. 投资信息接口

### 2.1 根据省份获取投资信息

**接口地址**: `GET /investment/province/{province}`

**路径参数**:
- `province`: 省份名称（如：京、沪）

**查询参数**:
- `limit`: 返回数量，默认10，范围1-50

**请求示例**:
```
GET /investment/province/京?limit=10
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "province": "京",
    "total": 2,
    "investmentList": [
      {
        "id": 1,
        "title": "北京科技园区AI项目",
        "province": "京",
        "provinceCode": "110000",
        "city": "北京市",
        "companyName": "智能科技有限公司",
        "industry": "人工智能",
        "investmentAmount": 50000.00,
        "investmentType": "股权投资",
        "description": "投资AI芯片研发项目，预计3年内实现量产",
        "relatedProvinces": "沪,粤,浙",
        "investmentDate": "2026-01-15",
        "status": 1,
        "createTime": "2026-01-30 10:00:00",
        "updateTime": "2026-01-30 10:00:00"
      },
      {
        "id": 16,
        "title": "北京量子通信研发中心",
        "province": "京",
        "provinceCode": "110000",
        "city": "北京市",
        "companyName": "量子科技研究院",
        "industry": "量子科技",
        "investmentAmount": 85000.00,
        "investmentType": "研发投资",
        "description": "建设量子通信研发中心，抢占科技制高点",
        "relatedProvinces": "沪,皖,粤",
        "investmentDate": "2026-01-11",
        "status": 1,
        "createTime": "2026-01-30 10:00:00",
        "updateTime": "2026-01-30 10:00:00"
      }
    ],
    "relatedProvinces": ["沪", "粤", "浙", "皖"]
  },
  "timestamp": 1706601234567
}
```

**字段说明**:
- `province`: 查询的省份
- `total`: 返回的投资信息数量
- `investmentList`: 投资信息列表
  - `id`: 投资信息ID
  - `title`: 投资标题
  - `province`: 省份
  - `provinceCode`: 省份编码
  - `city`: 城市
  - `companyName`: 公司名称
  - `industry`: 行业
  - `investmentAmount`: 投资金额（万元）
  - `investmentType`: 投资类型
  - `description`: 投资描述
  - `relatedProvinces`: 关联省份（逗号分隔）
  - `investmentDate`: 投资日期
  - `status`: 状态（0-无效，1-有效）
  - `createTime`: 创建时间
  - `updateTime`: 更新时间
- `relatedProvinces`: 关联省份列表（去重后）

---

### 2.2 获取地图数据

**接口地址**: `GET /investment/map/data`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "mapData": [
      {
        "name": "北京",
        "value": 2,
        "amount": 135000.00
      },
      {
        "name": "上海",
        "value": 2,
        "amount": 175000.00
      },
      {
        "name": "广东",
        "value": 3,
        "amount": 244000.00
      }
    ],
    "totalInvestments": 20,
    "totalProvinces": 15
  },
  "timestamp": 1706601234567
}
```

**字段说明**:
- `mapData`: 地图数据数组
  - `name`: 省份名称
  - `value`: 投资项目数量
  - `amount`: 投资总额（万元）
- `totalInvestments`: 总投资项目数
- `totalProvinces`: 涉及省份数

---

### 2.3 添加投资信息

**接口地址**: `POST /investment/add`

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:
```json
{
  "title": "测试投资项目",
  "province": "北京",
  "provinceCode": "110000",
  "city": "北京市",
  "companyName": "测试公司",
  "industry": "科技",
  "investmentAmount": 10000.00,
  "investmentType": "股权投资",
  "description": "这是一个测试投资项目",
  "relatedProvinces": "上海,广东",
  "investmentDate": "2026-01-30"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "添加成功",
  "data": null,
  "timestamp": 1706601234567
}
```

---

## 3. 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 200 | 成功 | - |
| 401 | 未授权 | 检查token是否有效 |
| 500 | 服务器错误 | 查看错误信息 |

---

## 4. 使用示例

### 4.1 cURL示例

#### 登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

#### 获取地图数据
```bash
curl -X GET http://localhost:8080/api/investment/map/data
```

#### 获取省份投资信息
```bash
curl -X GET "http://localhost:8080/api/investment/province/北京?limit=10"
```

#### 添加投资信息（需要token）
```bash
curl -X POST http://localhost:8080/api/investment/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "测试投资项目",
    "province": "北京",
    "provinceCode": "110000",
    "city": "北京市",
    "companyName": "测试公司",
    "industry": "科技",
    "investmentAmount": 10000.00,
    "investmentType": "股权投资",
    "description": "这是一个测试投资项目",
    "relatedProvinces": "上海,广东",
    "investmentDate": "2026-01-30"
  }'
```

---

### 4.2 JavaScript示例

#### 使用Axios

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

// 登录
async function login() {
  const response = await api.post('/auth/login', {
    username: 'admin',
    password: '123456'
  })
  const token = response.data.data.token
  // 保存token
  localStorage.setItem('token', token)
  return token
}

// 获取地图数据
async function getMapData() {
  const response = await api.get('/investment/map/data')
  return response.data.data
}

// 获取省份投资信息
async function getProvinceData(province, limit = 10) {
  const response = await api.get(`/investment/province/${province}`, {
    params: { limit }
  })
  return response.data.data
}

// 添加投资信息（需要先登录获取token）
async function addInvestment(data) {
  const token = localStorage.getItem('token')
  const response = await api.post('/investment/add', data, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  return response.data
}
```

---

## 5. 注意事项

1. **认证**: 除了登录接口，其他接口都需要在请求头中携带JWT token
2. **跨域**: 后端已配置CORS，允许跨域访问
3. **缓存**: 投资信息和地图数据有10分钟缓存，用户信息有30分钟缓存
4. **省份名称**: 使用中文省份名称，如"北京"、"上海"等
5. **日期格式**: 使用ISO 8601格式，如"2026-01-30"
6. **金额单位**: 所有金额单位为万元

---

## 6. 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| test | 123456 | 测试用户 |

---

## 7. 联系方式

如有问题，请查看：
- 项目README.md
- 部署文档DEPLOY.md
- 项目结构说明PROJECT_STRUCTURE.md
