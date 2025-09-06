<template>
  <div class="rebirth-records-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h2>重生记录</h2>
        <p class="subtitle">查看和管理所有客户重生申请记录</p>
      </div>
      <div class="header-actions">
        <el-button 
          v-if="hasSelection" 
          type="danger" 
          @click="batchDeleteRecords"
          class="batch-delete-button"
          :disabled="!hasSelection"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <!-- 工具栏 -->
    <el-card class="toolbar-card">
      <div class="toolbar-content">
        <!-- 搜索框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="搜索客户名称或重生原因"
          prefix-icon="Search"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        
        <!-- 状态筛选 -->
        <el-select v-model="statusFilter" placeholder="筛选状态" class="status-select">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        
        <!-- 创建时间筛选 -->
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
      <div class="stat-card">
        <div class="stat-number">{{ stats.totalRecords }}</div>
        <div class="stat-label">总记录数</div>
        <div class="stat-icon total-icon">
          <el-icon><Document /></el-icon>
        </div>
      </div>
      <div class="stat-card stat-card-pending">
        <div class="stat-number">{{ stats.pendingRecords }}</div>
        <div class="stat-label">待审核</div>
        <div class="stat-icon pending-icon">
          <el-icon><Clock /></el-icon>
        </div>
      </div>
      <div class="stat-card stat-card-approved">
        <div class="stat-number">{{ stats.approvedRecords }}</div>
        <div class="stat-label">已通过</div>
        <div class="stat-icon approved-icon">
          <el-icon><Check /></el-icon>
        </div>
      </div>
      <div class="stat-card stat-card-rejected">
        <div class="stat-number">{{ stats.rejectedRecords }}</div>
        <div class="stat-label">已拒绝</div>
        <div class="stat-icon rejected-icon">
          <el-icon><Close /></el-icon>
        </div>
      </div>
    </div>

    <!-- 表格内容 -->
    <el-card class="records-table-card">
      <el-table
        v-loading="loading"
        :data="rebirthRecords"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
        class="records-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="记录ID" width="80" align="center" />
        <el-table-column prop="customerName" label="客户名称" width="180">
          <template #default="scope">
            <el-link type="primary" @click="viewCustomerDetail(scope.row.customerId)">{{ scope.row.customerName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="rebirthReasonContent" label="重生原因">
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
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag 
              v-if="scope.row.status === 'PENDING'" 
              type="warning" 
              class="status-tag"
            >
              待审核
            </el-tag>
            <el-tag 
              v-else-if="scope.row.status === 'APPROVED'" 
              type="success" 
              class="status-tag"
            >
              已通过
            </el-tag>
            <el-tag 
              v-else-if="scope.row.status === 'REJECTED'" 
              type="danger" 
              class="status-tag"
            >
              已拒绝
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180" align="center" />
        <el-table-column prop="reviewTime" label="审核时间" width="180" align="center">
          <template #default="scope">
            <span v-if="scope.row.reviewTime">{{ scope.row.reviewTime }}</span>
            <span v-else class="not-reviewed">未审核</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <el-button 
              type="text" 
              @click="viewRecordDetail(scope.row.id)"
              class="action-button view-button"
              size="small"
            >
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button 
              type="text" 
              v-if="scope.row.status === 'PENDING'" 
              @click="reviewRecord(scope.row.id)"
              class="action-button review-button"
              size="small"
            >
              <el-icon><Check /></el-icon>
              审核
            </el-button>
            <el-button 
              type="text" 
              danger 
              @click="deleteRecord(scope.row.id)"
              class="action-button delete-button"
              size="small"
            >
              <el-icon><Delete /></el-icon>
              删除
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

    <!-- 审核对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="审核重生申请"
      width="700px"
      append-to-body
      destroy-on-close
    >
      <div v-if="currentRecord" class="review-dialog-content">
        <h4>申请信息</h4>
        <el-descriptions border column="1" class="review-descriptions">
          <el-descriptions-item label="申请ID">{{ currentRecord.id }}</el-descriptions-item>
          <el-descriptions-item label="客户名称">{{ currentRecord.customerName }}</el-descriptions-item>
          <el-descriptions-item label="重生原因">
            <div class="description-content">{{ currentRecord.rebirthReasonContent }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="整改措施">
            <div class="description-content">{{ currentRecord.rectificationMeasures }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="预期效果">
            <div class="description-content">{{ currentRecord.expectedEffects }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ currentRecord.applyTime }}</el-descriptions-item>
          <el-descriptions-item label="附件数量">{{ currentRecord.attachmentCount || 0 }} 个</el-descriptions-item>
        </el-descriptions>
        
        <h4 class="mt-4">审核意见</h4>
        <el-form :model="reviewForm" label-width="80px">
          <el-form-item label="审核结果" prop="status">
            <el-radio-group v-model="reviewForm.status">
              <el-radio label="APPROVED">通过</el-radio>
              <el-radio label="REJECTED">拒绝</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核意见" prop="comment">
            <el-input
              v-model="reviewForm.comment"
              type="textarea"
              rows="4"
              placeholder="请输入审核意见（可选）"
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
import { Search, Delete, View, Check, Close, Clock, Document, Loading } from '@element-plus/icons-vue'
import { rebirthApi } from '@/api/rebirth'
import type { RebirthRecordResponseDTO } from '@/types'

const router = useRouter()

// 状态数据
const loading = ref(false)
const submitting = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const dateRange = ref<[string, string] | null>(null)
const selectedRecords = ref<RebirthRecordResponseDTO[]>([])

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 统计数据
const stats = reactive({
  totalRecords: 0,
  pendingRecords: 0,
  approvedRecords: 0,
  rejectedRecords: 0
})

// 重生记录数据
const rebirthRecords = ref<RebirthRecordResponseDTO[]>([])

// 审核对话框相关
const dialogVisible = ref(false)
const currentRecord = ref<RebirthRecordResponseDTO | null>(null)
const reviewForm = reactive({
  status: 'APPROVED' as 'APPROVED' | 'REJECTED',
  comment: ''
})

// 计算属性：是否有选中记录
const hasSelection = computed(() => {
  return selectedRecords.value.length > 0
})

// 模拟获取重生记录数据
const fetchRebirthRecords = async () => {
  loading.value = true
  try {
    // 根据状态筛选获取重生记录
    let response
    if (statusFilter.value) {
      response = await rebirthApi.getRebirthRecordsByStatus(statusFilter.value as 'PENDING' | 'APPROVED' | 'REJECTED')
    } else {
      // 如果没有状态筛选，获取所有记录（通过分别获取不同状态的记录并合并）
      const pendingRecords = await rebirthApi.getRebirthRecordsByStatus('PENDING')
      const approvedRecords = await rebirthApi.getRebirthRecordsByStatus('APPROVED')
      const rejectedRecords = await rebirthApi.getRebirthRecordsByStatus('REJECTED')
      response = [...pendingRecords, ...approvedRecords, ...rejectedRecords]
    }
    
    // 根据筛选条件过滤数据
    let filteredData = [...response]
    
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredData = filteredData.filter(record => 
        record.customerName.toLowerCase().includes(keyword) || 
        record.rebirthReasonContent.toLowerCase().includes(keyword)
      )
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
    rebirthRecords.value = paginatedData
    pagination.total = total
    
    // 更新统计信息
    stats.totalRecords = filteredData.length
    stats.pendingRecords = filteredData.filter(r => r.status === 'PENDING').length
    stats.approvedRecords = filteredData.filter(r => r.status === 'APPROVED').length
    stats.rejectedRecords = filteredData.filter(r => r.status === 'REJECTED').length
  } catch (error) {
    ElMessage.error('获取重生记录失败')
    console.error('获取重生记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  selectedRecords.value = []
  fetchRebirthRecords()
}

// 重置筛选条件
const resetFilters = () => {
  searchKeyword.value = ''
  statusFilter.value = ''
  dateRange.value = null
  pagination.currentPage = 1
  selectedRecords.value = []
  fetchRebirthRecords()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  selectedRecords.value = []
  fetchRebirthRecords()
}

// 当前页码变化
const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  selectedRecords.value = []
  fetchRebirthRecords()
}

// 选择变化处理
const handleSelectionChange = (selection: RebirthRecordResponseDTO[]) => {
  selectedRecords.value = selection
}

// 查看客户详情
const viewCustomerDetail = (customerId: number) => {
  ElMessage.info(`查看客户ID: ${customerId} 的详情`)
  // 在实际项目中，这里应该跳转到客户详情页面
}

// 查看记录详情
const viewRecordDetail = (recordId: number) => {
  ElMessage.info(`查看记录ID: ${recordId} 的详情`)
  // 在实际项目中，这里应该跳转到记录详情页面
}

// 打开审核对话框
const reviewRecord = (recordId: number) => {
  const record = rebirthRecords.value.find(r => r.id === recordId)
  if (record) {
    currentRecord.value = { ...record }
    reviewForm.status = 'APPROVED'
    reviewForm.comment = ''
    dialogVisible.value = true
  }
}

// 提交审核结果
const submitReview = async () => {
  if (!currentRecord) return
  
  submitting.value = true
  try {
    // 获取当前用户ID（实际项目中应该从登录状态中获取）
    const reviewerId = 1; // 这里使用模拟的审核人ID
    
    // 调用API提交审核结果
    await rebirthApi.reviewRebirthRecord(currentRecord.value.id, reviewForm.status, reviewerId)
    
    dialogVisible.value = false
    ElMessage.success('审核成功')
    fetchRebirthRecords()
  } catch (error) {
    ElMessage.error('审核失败，请重试')
    console.error('提交审核结果失败:', error)
  } finally {
    submitting.value = false
  }
}

// 删除记录
const deleteRecord = (recordId: number) => {
  ElMessageBox.confirm(
    `确定要删除此重生申请记录吗？此操作不可撤销。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    }
  ).then(async () => {
    try {
      // 显示加载状态
      const loadingInstance = ElLoading.service({
        lock: true,
        text: '正在删除记录，请稍候...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      
      // 调用API删除记录
      await rebirthApi.deleteRebirthRecord(recordId)
      
      // 关闭加载状态
      loadingInstance.close()
      
      // 显示成功消息
      ElMessage.success('记录删除成功')
      
      // 刷新数据
      fetchRebirthRecords()
    } catch (error) {
      ElMessage.error('删除记录失败')
      console.error('删除记录失败:', error)
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 批量删除记录
const batchDeleteRecords = () => {
  if (selectedRecords.value.length === 0) {
    ElMessage.warning('请先选择要删除的记录')
    return
  }
  
  const recordIds = selectedRecords.value.map(r => r.id).join(', ')
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRecords.value.length} 条重生申请记录吗？此操作不可撤销。\n\n选中的记录ID: ${recordIds}`,
    '批量删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'danger',
      center: true,
      dangerouslyUseHTMLString: true
    }
  ).then(async () => {
    try {
      // 显示加载状态
      const loadingInstance = ElLoading.service({
        lock: true,
        text: `正在删除 ${selectedRecords.value.length} 条记录，请稍候...`,
        background: 'rgba(0, 0, 0, 0.7)'
      })
      
      // 批量删除记录（由于API不支持批量删除，这里逐个删除）
      const deletePromises = selectedRecords.value.map(record => 
        rebirthApi.deleteRebirthRecord(record.id)
      )
      await Promise.all(deletePromises)
      
      // 关闭加载状态
      loadingInstance.close()
      
      // 显示成功消息
      ElMessage.success(`成功删除 ${selectedRecords.value.length} 条记录`)
      
      // 清空选择并刷新数据
      selectedRecords.value = []
      fetchRebirthRecords()
    } catch (error) {
      ElMessage.error('批量删除失败')
      console.error('批量删除记录失败:', error)
    }
  }).catch(() => {
    // 用户取消删除
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
  fetchRebirthRecords()
})
</script>

<style scoped>
/* 基础容器样式 */
.rebirth-records-container {
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

.bg-circle-3 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
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

.status-select {
  width: 120px;
  transition: all 0.3s ease;
}

.status-select:hover .el-input__wrapper {
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
.reset-button {
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

.batch-delete-button {
  transition: all 0.3s ease;
}

.batch-delete-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
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

.stat-card-approved {
  border-left: 4px solid #67c23a;
}

.stat-card-rejected {
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

.total-icon {
  background-color: #409eff;
  color: white;
}

.pending-icon {
  background-color: #e6a23c;
  color: white;
}

.approved-icon {
  background-color: #67c23a;
  color: white;
}

.rejected-icon {
  background-color: #f56c6c;
  color: white;
}

/* 表格卡片 */
.records-table-card {
  margin-bottom: 20px;
  position: relative;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  overflow: hidden;
}

/* 表格样式 */
.records-table {
  transition: all 0.3s ease;
}

.records-table .el-table__body-wrapper {
  overflow-y: auto;
}

.records-table .el-table__row:hover > td {
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

/* 状态标签 */
.status-tag {
  transition: all 0.3s ease;
  font-size: 12px;
}

.status-tag:hover {
  transform: scale(1.05);
}

/* 未审核状态 */
.not-reviewed {
  color: #909399;
  font-style: italic;
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

.review-button:hover {
  color: #67c23a;
  background-color: #f0f9eb;
}

.delete-button:hover {
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
  
  .bg-circle-3 {
    width: 100px;
    height: 100px;
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
}

@media (max-width: 768px) {
  .rebirth-records-container {
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
  .status-select,
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
  .rebirth-records-container {
    padding: 5px;
  }
  
  .toolbar-card,
  .records-table-card {
    border-radius: 6px;
  }
  
  .stat-card {
    padding: 12px;
  }
  
  .stat-number {
    font-size: 20px;
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
.records-table .el-table__body-wrapper::-webkit-scrollbar {
  width: 6px;
}

.records-table .el-table__body-wrapper::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.records-table .el-table__body-wrapper::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.records-table .el-table__body-wrapper::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>
