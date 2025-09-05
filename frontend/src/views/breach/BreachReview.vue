<template>
  <div class="breach-review-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>违约审核</h2>
      <p class="subtitle">查看和处理违约记录审核请求</p>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-filter-card">
      <div class="search-filter-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="客户名称">
            <el-input v-model="searchForm.customerName" placeholder="请输入客户名称" style="width: 200px" />
          </el-form-item>
          <el-form-item label="严重程度">
            <el-select v-model="searchForm.severity" placeholder="请选择严重程度" style="width: 150px">
              <el-option label="全部" value="" />
              <el-option label="高" value="HIGH" />
              <el-option label="中" value="MEDIUM" />
              <el-option label="低" value="LOW" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchBreachRecords">查询</el-button>
            <el-button @click="resetSearchForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 违约记录表格 -->
    <el-card class="breach-records-table-card">
      <el-table v-loading="loading" :data="breachRecordsData" style="width: 100%">
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="customerName" label="客户名称" width="180" />
        <el-table-column prop="reason" label="违约原因" min-width="200">
          <template #default="scope">
            <el-popover
              trigger="hover"
              placement="top"
              :width="400"
            >
              <template #reference>
                <span>{{ truncateText(scope.row.reason, 50) }}</span>
              </template>
              <p>{{ scope.row.reason }}</p>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="severity" label="严重程度" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.severity === 'HIGH' ? 'danger' : scope.row.severity === 'MEDIUM' ? 'warning' : 'success'"
            >
              {{ scope.row.severity === 'HIGH' ? '高' : scope.row.severity === 'MEDIUM' ? '中' : '低' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'PENDING' ? 'info' : scope.row.status === 'APPROVED' ? 'success' : 'danger'">
              {{ scope.row.status === 'PENDING' ? '待审核' : scope.row.status === 'APPROVED' ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="showBreachDetails(scope.row.id)">详情</el-button>
            <template v-if="scope.row.status === 'PENDING'">
              <el-button type="success" size="small" @click="approveBreachRecord(scope.row.id)">通过</el-button>
              <el-button type="danger" size="small" @click="rejectBreachRecord(scope.row.id)">拒绝</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalRecords"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 违约记录详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="违约记录详情"
      width="600px"
      :before-close="handleClose"
    >
      <div v-if="currentBreachRecord" class="breach-detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="记录ID">{{ currentBreachRecord.id }}</el-descriptions-item>
          <el-descriptions-item label="客户名称">{{ currentBreachRecord.customerName }}</el-descriptions-item>
          <el-descriptions-item label="违约原因">{{ currentBreachRecord.reason }}</el-descriptions-item>
          <el-descriptions-item label="严重程度">
            <el-tag
              :type="currentBreachRecord.severity === 'HIGH' ? 'danger' : currentBreachRecord.severity === 'MEDIUM' ? 'warning' : 'success'"
            >
              {{ currentBreachRecord.severity === 'HIGH' ? '高' : currentBreachRecord.severity === 'MEDIUM' ? '中' : '低' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请人ID">{{ currentBreachRecord.applicantId }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ currentBreachRecord.createTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentBreachRecord.status === 'PENDING' ? 'info' : currentBreachRecord.status === 'APPROVED' ? 'success' : 'danger'">
              {{ currentBreachRecord.status === 'PENDING' ? '待审核' : currentBreachRecord.status === 'APPROVED' ? '已通过' : '已拒绝' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审核人ID" v-if="currentBreachRecord.reviewerId">{{ currentBreachRecord.reviewerId }}</el-descriptions-item>
          <el-descriptions-item label="审核时间" v-if="currentBreachRecord.reviewTime">{{ currentBreachRecord.reviewTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      :title="`${reviewAction === 'approve' ? '通过' : '拒绝'}违约申请`"
      width="500px"
    >
      <el-form ref="reviewFormRef" :model="reviewForm" label-width="80px">
        <el-form-item label="审核意见" prop="reviewComments">
          <el-input
            v-model="reviewForm.reviewComments"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="reviewLoading"
          @click="confirmReview"
        >
          确认{{ reviewAction === 'approve' ? '通过' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { breachApi } from '@/api/breach'
import type { BreachRecordResponseDTO, PaginatedResponse } from '@/types'

// 状态和数据
const loading = ref(false)
const reviewLoading = ref(false)
const breachRecords = ref<BreachRecordResponseDTO[]>([])
const totalRecords = ref(0)
const currentBreachRecord = ref<BreachRecordResponseDTO | null>(null)

// 对话框状态
const detailDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const reviewAction = ref<'approve' | 'reject'>('approve')
const currentReviewId = ref(0)

// 搜索表单
const searchForm = reactive({
  customerName: '',
  severity: ''
})

// 审核表单
const reviewFormRef = ref()
const reviewForm = reactive({
  reviewComments: ''
})

// 分页参数
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 计算表格数据（模拟分页）
const breachRecordsData = ref<BreachRecordResponseDTO[]>([])

// 初始化加载数据
onMounted(() => {
  fetchBreachRecords()
})

// 获取违约记录列表
const fetchBreachRecords = async () => {
  loading.value = true
  try {
    // 根据搜索条件调用相应的API
    let records: BreachRecordResponseDTO[] = []
    
    if (searchForm.severity) {
      // 根据严重程度查询
      records = await breachApi.getBreachRecordsBySeverity(searchForm.severity as 'HIGH' | 'MEDIUM' | 'LOW')
    } else {
      // 默认获取所有待审核的记录
      records = await breachApi.getBreachRecordsByStatus('PENDING')
      // 如果需要获取所有状态的记录，可以合并不同状态的结果
      // const approvedRecords = await breachApi.getBreachRecordsByStatus('APPROVED')
      // const rejectedRecords = await breachApi.getBreachRecordsByStatus('REJECTED')
      // records = [...pendingRecords, ...approvedRecords, ...rejectedRecords]
    }
    
    // 如果有客户名称搜索条件，在前端进行过滤
    if (searchForm.customerName) {
      records = records.filter(item => 
        item.customerName.includes(searchForm.customerName)
      )
    }
    
    breachRecords.value = records
    totalRecords.value = records.length
    updatePaginationData()
  } catch (error) {
    ElMessage.error('获取违约记录失败')
    console.error('获取违约记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索违约记录
const searchBreachRecords = () => {
  pagination.currentPage = 1
  fetchBreachRecords()
}

// 重置搜索表单
const resetSearchForm = () => {
  searchForm.customerName = ''
  searchForm.severity = ''
  pagination.currentPage = 1
  fetchBreachRecords()
}

// 分页处理
const updatePaginationData = () => {
  const startIndex = (pagination.currentPage - 1) * pagination.pageSize
  const endIndex = startIndex + pagination.pageSize
  
  // 应用搜索和筛选条件
  let filteredData = [...breachRecords.value]
  
  if (searchForm.customerName) {
    filteredData = filteredData.filter(item => 
      item.customerName.includes(searchForm.customerName)
    )
  }
  
  if (searchForm.severity) {
    filteredData = filteredData.filter(item => 
      item.severity === searchForm.severity
    )
  }
  
  totalRecords.value = filteredData.length
  breachRecordsData.value = filteredData.slice(startIndex, endIndex)
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  updatePaginationData()
}

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  updatePaginationData()
}

// 显示违约记录详情
const showBreachDetails = async (id: number) => {
  try {
    // 调用API获取详情
    const record = await breachApi.getBreachRecordById(id)
    if (record) {
      currentBreachRecord.value = record
      detailDialogVisible.value = true
    } else {
      ElMessage.error('未找到该违约记录')
    }
  } catch (error) {
    ElMessage.error('获取违约记录详情失败')
    console.error('获取违约记录详情失败:', error)
  }
}

// 审核通过违约记录
const approveBreachRecord = (id: number) => {
  currentReviewId.value = id
  reviewAction.value = 'approve'
  reviewForm.reviewComments = ''
  reviewDialogVisible.value = true
}

// 拒绝违约记录
const rejectBreachRecord = (id: number) => {
  currentReviewId.value = id
  reviewAction.value = 'reject'
  reviewForm.reviewComments = ''
  reviewDialogVisible.value = true
}

// 确认审核
const confirmReview = async () => {
  reviewLoading.value = true
  try {
    // 调用API进行审核
    const status = reviewAction.value === 'approve' ? 'APPROVED' : 'REJECTED'
    // 假设当前审核人ID为1，实际项目中应该从登录状态获取
    await breachApi.reviewBreachRecord(currentReviewId.value, status, 1)
    
    // 重新获取数据以更新列表
    await fetchBreachRecords()
    
    reviewDialogVisible.value = false
    ElMessage.success(`违约申请${reviewAction.value === 'approve' ? '已通过' : '已拒绝'}`)
  } catch (error) {
    ElMessage.error(`审核失败，请重试`)
    console.error('审核失败:', error)
  } finally {
    reviewLoading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  currentBreachRecord.value = null
  detailDialogVisible.value = false
}

// 截断文本
const truncateText = (text: string, maxLength: number) => {
  if (text.length <= maxLength) {
    return text
  }
  return text.substring(0, maxLength) + '...'
}

// 生成模拟数据的函数已移除，现在使用实际API调用
</script>

<style scoped>
.breach-review-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin-bottom: 8px;
  color: #303133;
}

.page-header .subtitle {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.search-filter-card {
  margin-bottom: 20px;
}

.search-filter-form {
  padding: 10px 0;
}

.breach-records-table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.breach-detail-content {
  max-height: 400px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .breach-review-container {
    padding: 10px;
  }
  
  .search-filter-form {
    overflow-x: auto;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .el-table .el-table__column {
    padding: 0 5px;
  }
}
</style>
