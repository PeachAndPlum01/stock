<template>
  <div class="stock-bar-container">
    <!-- 左侧：热门股票 -->
    <aside class="hot-stocks-panel">
      <div class="panel-header">
        <el-icon><Histogram /></el-icon>
        <span>热门股票</span>
      </div>
      <div class="stock-list scrollbar-hide">
        <div v-for="(stock, index) in hotStocks" :key="index" class="stock-item">
          <div class="stock-info">
            <span class="stock-name">{{ stock.name }}</span>
            <span class="stock-code">{{ stock.code }}</span>
          </div>
          <div class="stock-price" :class="stock.change >= 0 ? 'text-up' : 'text-down'">
            <span class="price">{{ stock.price.toFixed(2) }}</span>
            <span class="change">{{ stock.change >= 0 ? '+' : '' }}{{ stock.change }}%</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- 右侧：帖子列表 -->
    <main class="posts-panel">
      <div class="posts-header">
        <div class="header-title">
          <el-icon><ChatLineSquare /></el-icon>
          <span>股吧讨论</span>
        </div>
        <el-button type="primary" size="small" @click="showPostDialog = true">
          <el-icon><Edit /></el-icon> 发帖
        </el-button>
      </div>

      <div class="posts-list scrollbar-hide">
        <div v-for="post in posts" :key="post.id" class="post-card">
          <div class="post-header">
            <div class="user-info">
              <div class="avatar">{{ (post.username || 'U').charAt(0).toUpperCase() }}</div>
              <span class="username">{{ post.username || '匿名用户' }}</span>
              <span class="time">{{ formatTime(post.createTime) }}</span>
            </div>
            <div class="post-tags" v-if="post.tags">
              <el-tag size="small" effect="dark" v-for="tag in parseTags(post.tags)" :key="tag">{{ tag }}</el-tag>
            </div>
          </div>
          
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-content">{{ post.content }}</p>
          
          <div class="post-actions">
            <div class="action-item" @click="handleLike(post)">
              <el-icon><Pointer /></el-icon>
              <span>{{ post.likeCount || 0 }}</span>
            </div>
            <div class="action-item" @click="toggleComments(post)">
              <el-icon><ChatDotRound /></el-icon>
              <span>{{ post.commentCount || '评论' }}</span>
            </div>
            <div class="action-item">
              <el-icon><Share /></el-icon>
              <span>分享</span>
            </div>
          </div>

          <!-- 评论区 -->
          <div v-if="post.showComments" class="comments-section">
            <div class="comment-list">
              <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
                <span class="comment-user">{{ comment.username }}:</span>
                <span class="comment-content">{{ comment.content }}</span>
              </div>
              <div v-if="!post.comments || post.comments.length === 0" class="no-comments">暂无评论</div>
            </div>
            <div class="comment-input">
              <el-input v-model="post.newComment" placeholder="写下你的评论..." size="small">
                <template #append>
                  <el-button @click="submitComment(post)">发送</el-button>
                </template>
              </el-input>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 发帖弹窗 -->
    <el-dialog v-model="showPostDialog" title="发布新帖" width="500px" custom-class="dark-dialog">
      <el-form :model="newPost" label-width="60px">
        <el-form-item label="标题">
          <el-input v-model="newPost.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="newPost.content" type="textarea" rows="4" placeholder="分享你的观点..." />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="newPost.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPostDialog = false">取消</el-button>
          <el-button type="primary" @click="handleCreatePost">发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Histogram, ChatLineSquare, Edit, Pointer, ChatDotRound, Share } from '@element-plus/icons-vue'
import { getPostList, createPost, getComments, createComment, getHotStocks } from '@/api'

const hotStocks = ref([])
const posts = ref([])
const showPostDialog = ref(false)
const newPost = ref({ title: '', content: '', tags: '' })

const loadData = async () => {
  try {
    const [stocksRes, postsRes] = await Promise.all([
      getHotStocks(),
      getPostList(1, 20)
    ])
    hotStocks.value = stocksRes.data || []
    posts.value = (postsRes.data.records || []).map(p => ({
      ...p,
      showComments: false,
      comments: [],
      newComment: ''
    }))
  } catch (error) {
    console.error('Load data failed:', error)
  }

  // 如果没有数据，使用样例数据
  if (hotStocks.value.length === 0) {
    hotStocks.value = [
      { name: '贵州茅台', code: '600519', price: 1788.50, change: 1.25 },
      { name: '宁德时代', code: '300750', price: 215.60, change: -0.85 },
      { name: '招商银行', code: '600036', price: 32.45, change: 0.62 },
      { name: '腾讯控股', code: '00700', price: 385.20, change: 2.10 },
      { name: '比亚迪', code: '002594', price: 245.80, change: -1.15 },
      { name: '五粮液', code: '000858', price: 156.30, change: 0.95 },
      { name: '中国平安', code: '601318', price: 48.20, change: 0.45 },
      { name: '美团-W', code: '03690', price: 98.50, change: -2.30 }
    ]
  }

  if (posts.value.length === 0) {
    posts.value = [
      {
        id: 1,
        username: '股海老韭菜',
        createTime: new Date().toISOString(),
        title: '关于近期大盘走势的几点看法',
        content: '最近市场波动较大，建议大家控制仓位，不要盲目追高。特别是新能源板块，短期回调压力较大，可以关注一下低估值的蓝筹股。',
        tags: '大盘,策略,蓝筹',
        likeCount: 128,
        commentCount: 32,
        showComments: false,
        comments: [
          { id: 101, username: '新手小白', content: '大佬说得对，我已经空仓观望了。' },
          { id: 102, username: '价值投资', content: '长期看好中国经济，现在是布局的好时机。' }
        ],
        newComment: ''
      },
      {
        id: 2,
        username: '短线小王子',
        createTime: new Date(Date.now() - 3600000).toISOString(),
        title: '宁德时代这波回调到位了吗？',
        content: '技术面上看，宁德时代已经回踩到了半年线支撑位，成交量也有所萎缩，感觉可以尝试博一个反弹。大家怎么看？',
        tags: '宁德时代,技术分析,抄底',
        likeCount: 45,
        commentCount: 18,
        showComments: false,
        comments: [],
        newComment: ''
      },
      {
        id: 3,
        username: '基本面分析师',
        createTime: new Date(Date.now() - 7200000).toISOString(),
        title: '深度解析：人工智能产业链的投资机会',
        content: '随着ChatGPT的爆火，人工智能产业链迎来了新的爆发期。从算力芯片到光模块，再到应用端，都有巨大的增长空间。重点推荐关注...',
        tags: 'AI,ChatGPT,产业链',
        likeCount: 256,
        commentCount: 64,
        showComments: false,
        comments: [],
        newComment: ''
      }
    ]
  }
}

const parseTags = (tagsStr) => {
  if (!tagsStr) return []
  return tagsStr.split(/[,，]/).filter(t => t.trim())
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return new Date(timeStr).toLocaleString()
}

const handleCreatePost = async () => {
  if (!newPost.value.title || !newPost.value.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }
  try {
    await createPost(newPost.value)
    ElMessage.success('发布成功')
    showPostDialog.value = false
    newPost.value = { title: '', content: '', tags: '' }
    loadData() // Reload list
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const toggleComments = async (post) => {
  post.showComments = !post.showComments
  if (post.showComments && (!post.comments || post.comments.length === 0)) {
    try {
      const res = await getComments(post.id)
      post.comments = res.data || []
    } catch (error) {
      console.error('Load comments failed:', error)
    }
  }
}

const submitComment = async (post) => {
  if (!post.newComment) return
  try {
    await createComment({
      postId: post.id,
      content: post.newComment
    })
    ElMessage.success('评论成功')
    post.newComment = ''
    // Reload comments
    const res = await getComments(post.id)
    post.comments = res.data || []
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

const handleLike = (post) => {
  // Mock like
  post.likeCount = (post.likeCount || 0) + 1
  ElMessage.success('点赞成功')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.stock-bar-container {
  display: flex;
  height: 100%;
  width: 100%;
  background: #0B0E11;
  color: #EAECEF;
}

.hot-stocks-panel {
  width: 280px;
  background: #0B0E11;
  border-right: 1px solid #1E2329;
  display: flex;
  flex-direction: column;
}

.panel-header {
  height: 56px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  border-bottom: 1px solid #1E2329;
  font-weight: 600;
  font-size: 15px;
  color: #EAECEF;
  background: #0B0E11;
}

.stock-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.stock-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 2px solid transparent;
}

.stock-item:hover {
  background: #15181C;
  border-left-color: #EB4436;
}

.stock-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stock-name {
  color: #EAECEF;
  font-size: 15px;
  font-weight: 500;
}

.stock-code {
  color: #848E9C;
  font-size: 13px;
}

.stock-price {
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.price {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 600;
  font-size: 16px;
}

.change {
  font-size: 13px;
  font-weight: 500;
}

.posts-panel {
  flex: 1;
  background: #0B0E11;
  display: flex;
  flex-direction: column;
}

.posts-header {
  height: 56px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  border-bottom: 1px solid #1E2329;
  background: #0B0E11;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 16px;
  color: #EAECEF;
}

.posts-list {
  flex: 1;
  overflow-y: auto;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.post-card {
  background: #0B0E11;
  border-radius: 0;
  padding: 24px;
  border: none;
  border-bottom: 1px solid #1E2329;
  transition: background 0.2s;
}

.post-card:hover {
  transform: none;
  box-shadow: none;
  border-color: #1E2329;
  background: #101316;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #1E2329;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #848E9C;
  font-size: 14px;
}

.username {
  color: #EAECEF;
  font-weight: 600;
  font-size: 14px;
}

.time {
  color: #5E6673;
  font-size: 12px;
}

.post-tags {
  display: flex;
  gap: 6px;
  margin-top: 4px;
}

.post-title {
  margin: 0 0 8px 0;
  color: #EAECEF;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.5;
}

.post-content {
  margin: 0 0 16px 0;
  color: #848E9C;
  font-size: 14px;
  line-height: 1.6;
}

.post-actions {
  display: flex;
  gap: 24px;
  border-top: none;
  padding-top: 0;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #5E6673;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
  padding: 4px 8px;
  border-radius: 4px;
  margin-left: -8px;
}

.action-item:hover {
  color: #EB4436;
  background: rgba(235, 68, 54, 0.08);
}

.comments-section {
  margin-top: 16px;
  background: #15181C;
  border-radius: 4px;
  padding: 16px;
  border: none;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.comment-item {
  font-size: 14px;
  line-height: 1.6;
  display: flex;
  gap: 8px;
}

.comment-user {
  color: #447EFF;
  font-weight: 500;
  white-space: nowrap;
}

.comment-content {
  color: #B7BDC6;
}

.no-comments {
  color: #5E6673;
  font-size: 13px;
  text-align: center;
  padding: 12px 0;
}

.text-up { color: #EB4436; }
.text-down { color: #00AA3B; }

/* 自定义滚动条 */
.scrollbar-hide::-webkit-scrollbar {
  width: 4px;
  height: 4px;
}
.scrollbar-hide::-webkit-scrollbar-track {
  background: transparent;
}
.scrollbar-hide::-webkit-scrollbar-thumb {
  background: #2B3139;
  border-radius: 2px;
}
.scrollbar-hide::-webkit-scrollbar-thumb:hover {
  background: #474D57;
}
</style>
