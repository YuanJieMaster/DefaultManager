<template>
  <div class="rebirth-review-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h2>重生审核</h2>
        <p class="subtitle">审核客户提交的重生申请</p>
      </div>
      <div class="header-actions">
        <el-button 
          type="primary" 
          @click="refreshData"
          class="refresh-button"
          :loading="refreshing"
        >
          <el-icon v-if="refreshing"><Refresh /></el-icon>
          <el-icon v-else><RefreshRight /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
    </div>

    <!-- 工具栏 -->
    <el-card class="toolbar-card">
      <div class="toolbar-content">
        <!-- 搜索框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="搜索客户名称或重生原因内容"
          prefix-icon="Search"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        
        <!-- 客户行业筛选 -->
        <el-select v-model="industryFilter" placeholder="筛选客户行业" class="industry-select">
          <el-option label="全部" value="" />
          <el-option label="科技" value="TECH" />
          <el-option label="物流" value="LOGISTICS" />
          <el-option label="贸易" value="TRADE" />
          <el-option label="金融" value="FINANCE" />
          <el-option label="医疗" value="MEDICAL" />
          <el-option label="环保" value="ENVIRONMENT" />
        </el-select>
        
        <!-- 申请时间筛选 -->
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          class="date-picker"
        />
        
        <!-- 搜索按钮 -->
        <el-button type="primary" @click="handleSearch" class="search-button">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        
        <!-- 重置按钮 -->
        <el-button @click="resetFilters" class="reset-button">重置</el-button>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card stat-card-pending">
        <div class="stat-number">{{ pendingCount }}</div>
        <div class="stat-label">待审核</div>
        <div class="stat-icon pending-icon">
          <el-icon><Clock /></el-icon>
        </div>
      </div>
      <div class="stat-card stat-card-today">
        <div class="stat-number">{{ todayCount }}</div>
        <div class="stat-label">今日申请</div>
        <div class="stat-icon today-icon">
          <el-icon><Calendar /></el-icon>
        </div>
      </div>
      <div class="stat-card stat-card-urgent">
        <div class="stat-number">{{ urgentCount }}</div>
        <div class="stat-label">紧急申请</div>
        <div class="stat-icon urgent-icon">
          <el-icon><Warning /></el-icon>
        </div>
      </div>
    </div>

    <!-- 表格内容 -->
    <el-card class="review-table-card">
      <el-table
        v-loading="loading"
        :data="rebirthReviews"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
        class="review-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="申请ID" width="80" align="center" />
        <el-table-column prop="customerName" label="客户名称" width="180">
          <template #default="scope">
            <el-link type="primary" @click="viewCustomerDetail(scope.row.customerId)">{{ scope.row.customerName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="industry" label="客户行业" width="100" align="center">
            <template #default="scope">
              <el-tag size="small">{{ industryMap[scope.row.industry] || scope.row.industry }}</el-tag>
            </template>
          </el-table-column>
        <el-table-column prop="rebirthReasonContent" label="申请理由">
          <template #default="scope">
            <el-popover
              trigger="hover"
              placement="top"
              :content="scope.row.rebirthReasonContent"
              width="400"
            >
              <template #reference>
                <span class="reason-text">{{ truncateText(scope.row.rebirthReasonContent, 50) }}</span>
              </template>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="breachRecordCount" label="关联违约记录" width="120" align="center">
          <template #default="scope">
            <el-badge :value="scope.row.breachRecordCount" type="primary" class="breach-record-badge" />
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180" align="center" sortable />
        <el-table-column prop="isUrgent" label="紧急程度" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.isUrgent" type="danger" class="urgent-tag">紧急</el-tag>
            <el-tag v-else type="info" class="normal-tag">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="scope">
            <el-button 
              type="text" 
              @click="viewReviewDetail(scope.row.id)"
              class="action-button view-button"
              size="small"
            >
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button 
              type="success" 
              @click="approveReview(scope.row.id)"
              class="action-button approve-button"
              size="small"
            >
              <el-icon><Check /></el-icon>
              通过
            </el-button>
            <el-button 
              type="danger" 
              @click="rejectReview(scope.row.id)"
              class="action-button reject-button"
              size="small"
            >
              <el-icon><Close /></el-icon>
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <div class="pagination-info">
          共 {{ pagination.total }} 条记录，当前第 {{ pagination.currentPage }} / {{ Math.ceil(pagination.total / pagination.pageSize) }} 页
        </div>
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="pagination"
        />
      </div>
    </el-card>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="hasSelection">
      <el-button 
        type="success" 
        @click="batchApprove"
        class="batch-action-button approve-button"
      >
        <el-icon><Check /></el-icon>
        批量通过 ({{ selectedRows.length }})
      </el-button>
      <el-button 
        type="danger" 
        @click="batchReject"
        class="batch-action-button reject-button"
      >
        <el-icon><Close /></el-icon>
        批量拒绝 ({{ selectedRows.length }})
      </el-button>
    </div>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      append-to-body
      destroy-on-close
    >
      <div v-if="currentReview" class="review-dialog-content">
        <h4>申请信息</h4>
        <el-descriptions border column="1" class="review-descriptions">
          <el-descriptions-item label="申请ID">{{ currentReview.id }}</el-descriptions-item>
          <el-descriptions-item label="客户名称">{{ currentReview.customerName }}</el-descriptions-item>
          <el-descriptions-item label="客户行业">{{ currentReview && industryMap[currentReview.industry] ? industryMap[currentReview.industry] : (currentReview ? currentReview.industry : '') }}</el-descriptions-item>
          <el-descriptions-item label="申请理由">
            <div class="description-content">{{ currentReview.rebirthReasonContent }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="整改措施">
            <div class="description-content">{{ currentReview.rectificationMeasures }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="预期效果">
            <div class="description-content">{{ currentReview.expectedEffects }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ currentReview.applyTime }}</el-descriptions-item>
          <el-descriptions-item label="附件数量">{{ currentReview.attachmentCount || 0 }} 个</el-descriptions-item>
        </el-descriptions>
        
        <h4 class="mt-4">审核意见</h4>
        <el-form :model="reviewForm" label-width="80px">
          <el-form-item label="审核结果" prop="status">
            <el-radio-group v-model="reviewForm.status">
              <el-radio label="APPROVED">通过</el-radio>
              <el-radio label="REJECTED">拒绝</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item 
            label="审核意见" 
            prop="comment"
            :rules="[{ required: reviewForm.status === 'REJECTED', message: '拒绝时必须填写审核意见', trigger: 'change' }]"
          >
            <el-input
              v-model="reviewForm.comment"
              type="textarea"
              rows="4"
              placeholder="请输入审核意见（拒绝时必须填写）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="dialogVisible = false" class="dialog-button">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="submitting" class="dialog-button primary-button">
          <el-icon v-if="submitting"><Loading /></el-icon>
          <span>提交审核</span>
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Search, Check, Close, View, Refresh, RefreshRight, Calendar, Clock, Warning, Loading } from '@element-plus/icons-vue'
import { rebirthApi } from '@/api/rebirth'

// 定义基本接口
export interface RebirthReviewResponseDTO {
  id: number
  customerId: number
  customerName: string
  industry: string
  rebirthReasonContent: string
  breachRecordCount: number
  isUrgent: boolean
  applyTime: string
  rectificationMeasures: string
  expectedEffects: string
  attachmentCount?: number
}

const router = useRouter()

// 状态数据
const loading = ref(false)
const refreshing = ref(false)
const submitting = ref(false)
const searchKeyword = ref('')
const industryFilter = ref('')
const dateRange = ref<any>(null)
const selectedRows = ref<any[]>([])

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 统计数据
const pendingCount = ref(0)
const todayCount = ref(0)
const urgentCount = ref(0)

// 重生审核数据
const rebirthReviews = ref<RebirthReviewResponseDTO[]>([])

// 审核对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('审核重生申请')
const currentReview = ref<any>(null)
const reviewForm = reactive({
  status: 'APPROVED',
  comment: ''
})

// 计算属性：是否有选中记录
const hasSelection = computed(() => {
  return selectedRows.value.length > 0
})

// 行业映射
const industryMap = {
  'TECH': '科技',
  'LOGISTICS': '物流',
  'TRADE': '贸易',
  'FINANCE': '金融',
  'MEDICAL': '医疗',
  'ENVIRONMENT': '环保',
  'FOOD': '食品',
  'TOURISM': '旅游',
  'CULTURE': '文化',
  'MANUFACTURING': '制造业'
};

// 为模板提供简单的数据格式
defineExpose({
  industryMap
})

// 获取重生审核数据
const fetchRebirthReviews = async () => {
  loading.value = true
  try {
    // 调用真实API获取待审核的重生记录
    const response = await rebirthApi.getRebirthRecordsByStatus('PENDING')
    
    // 转换数据格式以适应前端表格
        const formattedData = response.map(record => ({
          id: record.id,
          customerId: record.customerId,
          customerName: record.customerName,
          industry: 'TECH', // 暂时使用默认值，实际项目中应该从客户信息获取
          rebirthReasonContent: record.rebirthReasonContent, // 使用新的字段名称
          breachRecordCount: 1, // 暂时使用默认值，实际项目中应该调用API获取
          isUrgent: false, // 暂时使用默认值，实际项目中应该根据申请时间判断
          applyTime: record.createTime, // 假设后端返回的createTime格式已经符合前端要求
          rectificationMeasures: record.rectificationMeasures || '',
          expectedEffects: record.expectedEffects || '',
          attachmentCount: 0 // 暂时使用默认值，实际项目中应该调用API获取
        }))
    
    // 根据筛选条件过滤数据
    let filteredData = [...formattedData]
    
    if (searchKeyword.value) {
          const keyword = searchKeyword.value.toLowerCase()
          filteredData = filteredData.filter(record => 
            record.customerName.toLowerCase().includes(keyword) || 
            record.rebirthReasonContent.toLowerCase().includes(keyword)
          )
        }
    
    if (industryFilter.value) {
      filteredData = filteredData.filter(record => record.industry === industryFilter.value)
    }
    
    if (dateRange.value) {
      // 实际项目中应该根据dateRange过滤数据
      console.log('根据日期范围过滤数据:', dateRange.value)
    }
    
    // 计算分页数据
    const total = filteredData.length
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    const paginatedData = filteredData.slice(start, end)
    
    // 更新表格数据和分页信息
    rebirthReviews.value = paginatedData
    pagination.total = total
    
    // 更新统计信息
    pendingCount.value = filteredData.length
    todayCount.value = filteredData.filter(r => {
      const today = new Date().toISOString().split('T')[0]
      return r.applyTime.includes(today)
    }).length
    urgentCount.value = filteredData.filter(r => r.isUrgent).length
  } catch (error) {
    ElMessage.error('获取重生审核数据失败')
    console.error('获取重生审核数据失败:', error)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 刷新数据
const refreshData = () => {
  refreshing.value = true
  pagination.currentPage = 1
  selectedRows.value = []
  fetchRebirthReviews()
}

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  selectedRows.value = []
  fetchRebirthReviews()
}

// 重置筛选条件
const resetFilters = () => {
  searchKeyword.value = ''
  industryFilter.value = ''
  dateRange.value = null
  pagination.currentPage = 1
  selectedRows.value = []
  fetchRebirthReviews()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  selectedRows.value = []
  fetchRebirthReviews()
}

// 当前页码变化
const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  selectedRows.value = []
  fetchRebirthReviews()
}

// 选择变化处理
const handleSelectionChange = (selection: RebirthReviewResponseDTO[]) => {
  selectedRows.value = selection
}

// 查看客户详情
const viewCustomerDetail = (customerId: number) => {
  ElMessage.info(`查看客户ID: ${customerId} 的详情`)
  // 在实际项目中，这里应该跳转到客户详情页面
}

// 查看审核详情
const viewReviewDetail = (reviewId: number) => {
  const review = rebirthReviews.value.find(r => r.id === reviewId)
  if (review) {
    currentReview.value = { ...review }
    reviewForm.status = 'APPROVED'
    reviewForm.comment = ''
    dialogTitle.value = '审核重生申请'
    dialogVisible.value = true
  }
}

// 直接通过审核
const approveReview = (reviewId: number) => {
  const review = rebirthReviews.value.find(r => r.id === reviewId)
  if (!review) return
  
  ElMessageBox.confirm(
    `确定要通过客户「${review.customerName}」的重生申请吗？`,
    '通过确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success',
      center: true
    }
  ).then(async () => {
    submitReviewAction(reviewId, 'APPROVED', '')
  }).catch(() => {
    // 用户取消操作
  })
}

// 直接拒绝审核
const rejectReview = (reviewId: number) => {
  const review = rebirthReviews.value.find(r => r.id === reviewId)
  if (!review) return

  ElMessageBox.confirm(
    `确定要拒绝客户「${review.customerName}」的重生申请吗？`,
    '拒绝确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger',
      center: true
    }
  ).then(() => {
    submitReviewAction(reviewId, 'REJECTED', '')
  }).catch(() => {
    // 用户取消操作
  })
}

// 提交审核操作
const submitReviewAction = async (reviewId: number, status: 'APPROVED' | 'REJECTED', comment: string) => {
  try {
    const loadingInstance = ElLoading.service({
      lock: true,
      text: status === 'APPROVED' ? '正在处理通过申请...' : '正在处理拒绝申请...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 调用实际的API提交审核结果
    await rebirthApi.reviewRebirthRecord(reviewId, status, 1) // 实际项目中应从登录状态获取当前用户ID
    loadingInstance.close()
    
    ElMessage.success(status === 'APPROVED' ? '申请已通过' : '申请已拒绝')
    fetchRebirthReviews()
  } catch (error) {
    ElMessage.error('审核操作失败，请重试')
    console.error('提交审核结果失败:', error)
  }
}

// 提交审核对话框中的审核结果
const submitReview = async () => {
  if (!currentReview || (reviewForm.status === 'REJECTED' && !reviewForm.comment.trim())) {
    return
  }
  
  submitting.value = true
  try {
    await submitReviewAction(currentReview.id, reviewForm.status, reviewForm.comment)
    dialogVisible.value = false
  } catch (error) {
    // 错误处理已在submitReviewAction中完成
  } finally {
    submitting.value = false
  }
}

// 批量通过
const batchApprove = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要通过的申请')
    return
  }
  
  const reviewIds = selectedRows.value.map(r => r.id).join(', ')
  const customerNames = selectedRows.value.map(r => r.customerName).join('、')
  
  ElMessageBox.confirm(
    `确定要批量通过选中的 ${selectedRows.value.length} 个重生申请吗？\n\n涉及客户：${customerNames}\n申请ID：${reviewIds}`,
    '批量通过确认',
    {
      confirmButtonText: '确定通过',
      cancelButtonText: '取消',
      type: 'success',
      center: true,
      dangerouslyUseHTMLString: true
    }
  ).then(async () => {
    try {
      const loadingInstance = ElLoading.service({
        lock: true,
        text: `正在批量处理 ${selectedRows.value.length} 个申请...`,
        background: 'rgba(0, 0, 0, 0.7)'
      })
      
      // 遍历选中的记录，逐一调用审核API
      const promises = selectedRows.value.map(row => 
        rebirthApi.reviewRebirthRecord(row.id, 'APPROVED', 1) // 实际项目中应从登录状态获取当前用户ID
      )
      
      await Promise.all(promises)
      loadingInstance.close()
      
      ElMessage.success(`成功通过 ${selectedRows.value.length} 个申请`)
      selectedRows.value = []
      fetchRebirthReviews()
    } catch (error) {
      ElMessage.error('批量通过失败')
      console.error('批量通过失败:', error)
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 批量拒绝
const batchReject = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要拒绝的申请')
    return
  }
  
  ElMessageBox.prompt(
    `请输入批量拒绝 ${selectedRows.value.length} 个重生申请的理由（将应用于所有选中的申请）：`,
    '批量拒绝确认',
    {
      confirmButtonText: '确定拒绝',
      cancelButtonText: '取消',
      type: 'danger',
      inputType: 'textarea',
      inputPlaceholder: '请输入拒绝理由',
      inputValidator: (value: string) => {
        if (!value || value.trim().length === 0) {
          return '拒绝理由不能为空'
        }
        return true
      },
      center: true
    }
  ).then(async ({ value }) => {
    try {
      const loadingInstance = ElLoading.service({
        lock: true,
        text: `正在批量拒绝 ${selectedRows.value.length} 个申请...`,
        background: 'rgba(0, 0, 0, 0.7)'
      })
      
      // 遍历选中的记录，逐一调用审核API
      const promises = selectedRows.value.map(row => 
        rebirthApi.reviewRebirthRecord(row.id, 'REJECTED', 1) // 实际项目中应从登录状态获取当前用户ID
      )
      
      await Promise.all(promises)
      loadingInstance.close()
      
      ElMessage.success(`成功拒绝 ${selectedRows.value.length} 个申请`)
      selectedRows.value = []
      fetchRebirthReviews()
    } catch (error) {
      ElMessage.error('批量拒绝失败')
      console.error('批量拒绝失败:', error)
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 文本截断函数
const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) {
    return text
  }
  return text.substring(0, maxLength) + '...'
}

// 生命周期钩子
onMounted(() => {
  fetchRebirthReviews()
})
</script>

<style scoped>
/* 基础容器样式 */
.rebirth-review-container {
  padding: 20px;
  position: relative;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
  position: relative;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin-bottom: 8px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.header-content .subtitle {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(147, 197, 253, 0.15), rgba(165, 180, 252, 0.15));
}

.bg-circle-1 {
  width: 300px;
  height: 300px;
  top: 10%;
  right: 5%;
}

.bg-circle-2 {
  width: 200px;
  height: 200px;
  bottom: 20%;
  left: 5%;
}

/* 工具栏 */
.toolbar-card {
  margin-bottom: 20px;
  position: relative;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.toolbar-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.toolbar-content {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding: 16px;
}

.search-input {
  width: 300px;
  transition: all 0.3s ease;
}

.search-input:hover .el-input__wrapper {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.industry-select {
  width: 120px;
  transition: all 0.3s ease;
}

.industry-select:hover .el-input__wrapper {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.date-picker {
  width: 250px;
  transition: all 0.3s ease;
}

.date-picker:hover .el-input__wrapper {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.search-button,
.reset-button,
.refresh-button {
  transition: all 0.3s ease;
  min-width: 80px;
}

.search-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.reset-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.refresh-button:hover {
  transform: translateY(-1px) rotate(10deg);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
  position: relative;
  z-index: 10;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.stat-card-pending {
  border-left: 4px solid #e6a23c;
}

.stat-card-today {
  border-left: 4px solid #409eff;
}

.stat-card-urgent {
  border-left: 4px solid #f56c6c;
}

.stat-number {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.stat-icon {
  position: absolute;
  right: 16px;
  top: 16px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  opacity: 0.3;
}

.pending-icon {
  background-color: #e6a23c;
  color: white;
}

.today-icon {
  background-color: #409eff;
  color: white;
}

.urgent-icon {
  background-color: #f56c6c;
  color: white;
}

/* 表格卡片 */
.review-table-card {
  margin-bottom: 20px;
  position: relative;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  overflow: hidden;
}

/* 表格样式 */
.review-table {
  transition: all 0.3s ease;
}

.review-table .el-table__body-wrapper {
  overflow-y: auto;
}

.review-table .el-table__row:hover > td {
  background-color: #f0f7ff !important;
}

.reason-text {
  display: inline-block;
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 关联违约记录徽章 */
.breach-record-badge {
  font-size: 12px;
  padding: 4px 8px;
  min-width: 24px;
  height: 24px;
}

/* 紧急程度标签 */
.urgent-tag,
.normal-tag {
  transition: all 0.3s ease;
  font-size: 12px;
}

.urgent-tag:hover {
  transform: scale(1.05);
}

.normal-tag:hover {
  transform: scale(1.05);
}

/* 操作按钮 */
.action-button {
  transition: all 0.3s ease;
  padding: 8px 12px;
  margin: 0 4px;
}

.view-button:hover {
  color: #409eff;
  background-color: #ecf5ff;
}

.approve-button:hover {
  color: #67c23a;
  background-color: #f0f9eb;
}

.reject-button:hover {
  color: #f56c6c;
  background-color: #fef0f0;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 16px 0 0 0;
  border-top: 1px solid #ebeef5;
}

.pagination-info {
  color: #606266;
  font-size: 14px;
}

.pagination {
  display: flex;
  align-items: center;
}

/* 批量操作 */
.batch-actions {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
  z-index: 100;
}

.batch-action-button {
  transition: all 0.3s ease;
  min-width: 120px;
}

.batch-action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 审核对话框 */
.review-dialog-content h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.review-descriptions {
  margin-bottom: 20px;
}

.description-content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}

/* 对话框按钮 */
.dialog-button {
  transition: all 0.3s ease;
  min-width: 80px;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-input {
    width: 200px;
  }
  
  .stats-cards {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  }
  
  .bg-circle-1 {
    width: 200px;
    height: 200px;
  }
  
  .bg-circle-2 {
    width: 150px;
    height: 150px;
  }
}

@media (max-width: 992px) {
  .toolbar-content {
    justify-content: center;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .pagination-info {
    order: 2;
    align-self: flex-end;
  }
  
  .pagination {
    order: 1;
    align-self: flex-end;
  }
  
  .batch-actions {
    bottom: 10px;
    right: 10px;
    flex-direction: column;
  }
  
  .batch-action-button {
    min-width: 100px;
  }
}

@media (max-width: 768px) {
  .rebirth-review-container {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .header-content h2 {
    font-size: 20px;
  }
  
  .toolbar-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input,
  .industry-select,
  .date-picker {
    width: 100%;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-number {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .rebirth-review-container {
    padding: 5px;
  }
  
  .toolbar-card,
  .review-table-card {
    border-radius: 6px;
  }
  
  .stat-card {
    padding: 12px;
  }
  
  .stat-number {
    font-size: 20px;
  }
  
  .batch-actions {
    position: relative;
    bottom: auto;
    right: auto;
    margin-top: 10px;
    justify-content: center;
  }
}

/* 深色模式适配 */
:deep(.el-card.is-dark) {
  background-color: #1f2937;
  border-color: #374151;
}

:deep(.el-card.is-dark) .el-card__body {
  color: #e5e7eb;
}

:deep(.el-table__header-wrapper) th {
  background-color: #f8f9fa !important;
  font-weight: 600;
}

:deep(.el-table.is-dark) .el-table__header-wrapper th {
  background-color: #1f2937 !important;
}

:deep(.el-dialog__title) {
  color: #303133;
  font-weight: 600;
}

:deep(.el-dialog.is-dark .el-dialog__title) {
  color: #e5e7eb;
}

/* 滚动条样式 */
.review-table .el-table__body-wrapper::-webkit-scrollbar {
  width: 6px;
}

.review-table .el-table__body-wrapper::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.review-table .el-table__body-wrapper::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.review-table .el-table__body-wrapper::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>
