<template>
  <div class="breach-records-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>违约记录</h2>
      <p class="subtitle">查看和管理所有客户违约记录</p>
    </div>

    <!-- 工具栏 -->
    <el-card class="toolbar-card">
      <div class="toolbar-content">
        <!-- 搜索框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="搜索客户名称或违约原因"
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
        
        <!-- 严重程度筛选 -->
        <el-select v-model="severityFilter" placeholder="筛选严重程度" class="severity-select">
          <el-option label="全部" value="" />
          <el-option label="高" value="HIGH" />
          <el-option label="中" value="MEDIUM" />
          <el-option label="低" value="LOW" />
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
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        
        <!-- 重置按钮 -->
        <el-button @click="resetFilters">重置</el-button>
        
        <!-- 新增按钮 -->
        <el-button type="success" @click="handleCreate" style="margin-left: auto">
          <Plus /> 新增违约记录
        </el-button>
      </div>
    </el-card>

    <!-- 表格内容 -->
    <el-card class="records-table-card">
      <el-table
        v-loading="loading"
        :data="breachRecords"
        style="width: 100%"
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="记录ID" width="80" align="center" />
        <el-table-column prop="customerName" label="客户名称" width="180">
          <template #default="scope">
            <el-link type="primary" @click="viewCustomerDetail(scope.row.customerId)">{{ scope.row.customerName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="违约原因">
          <template #default="scope">
            <el-popover
              trigger="hover"
              placement="top"
              :content="scope.row.reason"
              width="400"
            >
              <template #reference>
                <span class="reason-text">{{ truncateText(scope.row.reason, 50) }}</span>
              </template>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="severity" label="严重程度" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.severity === 'HIGH'" type="danger">高</el-tag>
            <el-tag v-else-if="scope.row.severity === 'MEDIUM'" type="warning">中</el-tag>
            <el-tag v-else-if="scope.row.severity === 'LOW'" type="info">低</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'PENDING'" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 'APPROVED'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column prop="reviewTime" label="审核时间" width="180" align="center" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="text" @click="viewRecordDetail(scope.row.id)">查看</el-button>
            <el-button type="text" v-if="scope.row.status === 'PENDING'" @click="editRecord(scope.row.id)">编辑</el-button>
            <el-button type="text" danger @click="deleteRecord(scope.row.id)">删除</el-button>
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
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 操作统计 -->
    <div class="stats-summary">
      <div class="stat-item">
        <span class="stat-label">总记录数：</span>
        <span class="stat-value">{{ stats.totalRecords }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">待审核：</span>
        <span class="stat-value pending">{{ stats.pendingRecords }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已通过：</span>
        <span class="stat-value approved">{{ stats.approvedRecords }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已拒绝：</span>
        <span class="stat-value rejected">{{ stats.rejectedRecords }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { breachApi } from '@/api/breach'
import type { BreachRecordResponseDTO } from '@/types'

const router = useRouter()

// 状态数据
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const severityFilter = ref('')
const dateRange = ref<[string, string] | null>(null)

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

// 违约记录数据
const breachRecords = ref<BreachRecordResponseDTO[]>([])

// 模拟获取违约记录数据
const fetchBreachRecords = async () => {
  loading.value = true
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 在实际项目中，这里应该调用API获取数据
    // const response = await breachApi.getBreachRecordsByStatus(statusFilter.value as any || undefined)
    
    // 使用模拟数据
    const mockData: BreachRecordResponseDTO[] = [
      { id: 102, customerId: 1, customerName: '北京星辰科技有限公司', reason: '未按时还款超过30天，经多次催促仍未履行合同义务', severity: 'HIGH', applicantId: 1, status: 'PENDING', createTime: '2023-07-12 14:30:22' },
      { id: 101, customerId: 2, customerName: '上海远景贸易有限公司', reason: '违反合同约定的付款条款，延迟付款达到20天', severity: 'MEDIUM', applicantId: 2, status: 'APPROVED', reviewerId: 3, reviewTime: '2023-07-10 16:45:12', createTime: '2023-07-08 09:15:36' },
      { id: 100, customerId: 3, customerName: '广州恒远物流有限公司', reason: '运输过程中货物损坏，未按合同约定进行赔偿', severity: 'HIGH', applicantId: 1, status: 'REJECTED', reviewerId: 3, reviewTime: '2023-07-05 11:20:48', createTime: '2023-07-01 15:30:00' },
      { id: 99, customerId: 4, customerName: '深圳科技创新有限公司', reason: '技术服务未达到合同约定标准', severity: 'MEDIUM', applicantId: 2, status: 'APPROVED', reviewerId: 3, reviewTime: '2023-06-28 14:10:25', createTime: '2023-06-25 10:05:18' },
      { id: 98, customerId: 5, customerName: '杭州未来金融服务有限公司', reason: '财务报表与实际情况不符，存在虚假记录', severity: 'HIGH', applicantId: 1, status: 'APPROVED', reviewerId: 3, reviewTime: '2023-06-20 09:35:40', createTime: '2023-06-15 16:20:32' },
      { id: 97, customerId: 1, customerName: '北京星辰科技有限公司', reason: '项目交付延迟，影响我方业务进度', severity: 'LOW', applicantId: 2, status: 'PENDING', createTime: '2023-06-10 11:45:15' },
      { id: 96, customerId: 2, customerName: '上海远景贸易有限公司', reason: '产品质量不符合合同约定标准', severity: 'MEDIUM', applicantId: 1, status: 'APPROVED', reviewerId: 3, reviewTime: '2023-06-05 15:25:30', createTime: '2023-05-30 08:40:12' },
      { id: 95, customerId: 6, customerName: '南京智慧城市建设有限公司', reason: '未按约定时间提供技术支持', severity: 'LOW', applicantId: 2, status: 'REJECTED', reviewerId: 3, reviewTime: '2023-05-25 10:50:45', createTime: '2023-05-20 13:15:20' },
      { id: 94, customerId: 7, customerName: '成都华西医疗科技有限公司', reason: '医疗设备交付延迟，影响医院正常运营', severity: 'HIGH', applicantId: 1, status: 'APPROVED', reviewerId: 3, reviewTime: '2023-05-15 14:20:35', createTime: '2023-05-10 09:30:18' },
      { id: 93, customerId: 8, customerName: '武汉长江环保科技有限公司', reason: '环保设备未达到国家规定标准', severity: 'MEDIUM', applicantId: 2, status: 'PENDING', createTime: '2023-05-05 11:15:40' }
    ]
    
    // 根据筛选条件过滤数据
    let filteredData = [...mockData]
    
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredData = filteredData.filter(record => 
        record.customerName.toLowerCase().includes(keyword) || 
        record.reason.toLowerCase().includes(keyword)
      )
    }
    
    if (statusFilter.value) {
      filteredData = filteredData.filter(record => record.status === statusFilter.value)
    }
    
    if (severityFilter.value) {
      filteredData = filteredData.filter(record => record.severity === severityFilter.value)
    }
    
    // 计算分页数据
    const total = filteredData.length
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    const paginatedData = filteredData.slice(start, end)
    
    // 更新表格数据和分页信息
    breachRecords.value = paginatedData
    pagination.total = total
    
    // 更新统计信息
    stats.totalRecords = filteredData.length
    stats.pendingRecords = filteredData.filter(r => r.status === 'PENDING').length
    stats.approvedRecords = filteredData.filter(r => r.status === 'APPROVED').length
    stats.rejectedRecords = filteredData.filter(r => r.status === 'REJECTED').length
  } catch (error) {
    ElMessage.error('获取违约记录失败')
    console.error('获取违约记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  fetchBreachRecords()
}

// 重置筛选条件
const resetFilters = () => {
  searchKeyword.value = ''
  statusFilter.value = ''
  severityFilter.value = ''
  dateRange.value = null
  pagination.currentPage = 1
  fetchBreachRecords()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  fetchBreachRecords()
}

// 当前页码变化
const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  fetchBreachRecords()
}

// 新增违约记录
const handleCreate = () => {
  router.push('/breach/apply')
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

// 编辑记录
const editRecord = (recordId: number) => {
  ElMessage.info(`编辑记录ID: ${recordId}`)
  // 在实际项目中，这里应该跳转到编辑页面或打开编辑对话框
}

// 删除记录
const deleteRecord = (recordId: number) => {
  ElMessageBox.confirm(
    `确定要删除此违约记录吗？此操作不可撤销。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 在实际项目中，这里应该调用API删除记录
      // await breachApi.deleteBreachRecord(recordId)
      
      // 模拟删除操作
      ElMessage.loading('正在删除记录，请稍候...', 0)
      await new Promise(resolve => setTimeout(resolve, 1000))
      ElMessage.closeAll()
      
      ElMessage.success('记录删除成功')
      fetchBreachRecords()
    } catch (error) {
      ElMessage.error('记录删除失败')
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
  fetchBreachRecords()
})
</script>

<style scoped>
.breach-records-container {
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

.toolbar-card {
  margin-bottom: 20px;
}

.toolbar-content {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.search-input {
  width: 300px;
}

.status-select,
.severity-select {
  width: 120px;
}

.date-picker {
  width: 250px;
}

.records-table-card {
  margin-bottom: 20px;
}

.reason-text {
  display: inline-block;
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.stats-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-label {
  color: #606266;
  margin-right: 8px;
}

.stat-value {
  font-weight: 600;
  color: #303133;
}

.stat-value.pending {
  color: #e6a23c;
}

.stat-value.approved {
  color: #67c23a;
}

.stat-value.rejected {
  color: #f56c6c;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-input {
    width: 200px;
  }
}

@media (max-width: 768px) {
  .breach-records-container {
    padding: 10px;
  }
  
  .toolbar-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input,
  .status-select,
  .severity-select,
  .date-picker {
    width: 100%;
  }
  
  .stats-summary {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
