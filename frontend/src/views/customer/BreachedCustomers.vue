<template>
  <div class="breached-customers-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>违约客户管理</h2>
      <p class="subtitle">查看和管理有违约记录的客户信息</p>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-filter-card">
      <div class="search-filter-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="客户名称">
            <el-input v-model="searchForm.customerName" placeholder="请输入客户名称" style="width: 200px" />
          </el-form-item>
          <el-form-item label="所属行业">
            <el-select v-model="searchForm.industry" placeholder="请选择行业" style="width: 150px">
              <el-option label="全部" value="" />
              <el-option label="科技" value="科技" />
              <el-option label="贸易" value="贸易" />
              <el-option label="物流" value="物流" />
              <el-option label="金融" value="金融" />
              <el-option label="制造" value="制造" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属地区">
            <el-select v-model="searchForm.region" placeholder="请选择地区" style="width: 150px">
              <el-option label="全部" value="" />
              <el-option label="北京" value="北京" />
              <el-option label="上海" value="上海" />
              <el-option label="广州" value="广州" />
              <el-option label="深圳" value="深圳" />
              <el-option label="杭州" value="杭州" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchBreachedCustomers">查询</el-button>
            <el-button @click="resetSearchForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <div class="statistics-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ totalBreachedCustomers }}</div>
          <div class="stat-label">总违约客户数</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ highSeverityCount }}</div>
          <div class="stat-label">高风险客户</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ mediumSeverityCount }}</div>
          <div class="stat-label">中风险客户</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ lowSeverityCount }}</div>
          <div class="stat-label">低风险客户</div>
        </div>
      </el-card>
    </div>

    <!-- 违约客户表格 -->
    <el-card class="breached-customers-table-card">
      <el-table v-loading="loading" :data="breachedCustomersData" style="width: 100%">
        <el-table-column prop="id" label="客户ID" width="80" />
        <el-table-column prop="name" label="客户名称" width="180" />
        <el-table-column prop="industry" label="所属行业" width="100" />
        <el-table-column prop="region" label="所属地区" width="100" />
        <el-table-column prop="externalRating" label="外部评级" width="100" />
        <el-table-column prop="latestBreachSeverity" label="最新违约严重程度" width="150">
          <template #default="scope">
            <el-tag
              :type="scope.row.latestBreachSeverity === 'HIGH' ? 'danger' : scope.row.latestBreachSeverity === 'MEDIUM' ? 'warning' : 'success'"
            >
              {{ scope.row.latestBreachSeverity === 'HIGH' ? '高' : scope.row.latestBreachSeverity === 'MEDIUM' ? '中' : '低' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalBreachCount" label="违约次数" width="100" />
        <el-table-column prop="firstBreachTime" label="首次违约时间" width="180" />
        <el-table-column prop="latestBreachTime" label="最新违约时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="showCustomerDetails(scope.row.id)">详情</el-button>
            <el-button size="small" @click="viewBreachRecords(scope.row.id)">违约记录</el-button>
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
          :total="totalBreachedCustomers"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 客户详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="客户详情"
      width="700px"
      :before-close="handleClose"
    >
      <div v-if="currentCustomer" class="customer-detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="客户ID">{{ currentCustomer.id }}</el-descriptions-item>
          <el-descriptions-item label="客户名称">{{ currentCustomer.name }}</el-descriptions-item>
          <el-descriptions-item label="所属行业">{{ currentCustomer.industry }}</el-descriptions-item>
          <el-descriptions-item label="所属地区">{{ currentCustomer.region }}</el-descriptions-item>
          <el-descriptions-item label="外部评级">{{ currentCustomer.externalRating }}</el-descriptions-item>
          <el-descriptions-item label="违约状态">
            <el-tag type="danger">已违约</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="违约次数">{{ currentCustomer.totalBreachCount }}</el-descriptions-item>
          <el-descriptions-item label="首次违约时间">{{ currentCustomer.firstBreachTime }}</el-descriptions-item>
          <el-descriptions-item label="最新违约时间">{{ currentCustomer.latestBreachTime }}</el-descriptions-item>
          <el-descriptions-item label="最新违约严重程度">
            <el-tag
              :type="currentCustomer.latestBreachSeverity === 'HIGH' ? 'danger' : currentCustomer.latestBreachSeverity === 'MEDIUM' ? 'warning' : 'success'"
            >
              {{ currentCustomer.latestBreachSeverity === 'HIGH' ? '高' : currentCustomer.latestBreachSeverity === 'MEDIUM' ? '中' : '低' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentCustomer.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { customerApi } from '@/api/customer'
import { breachApi } from '@/api/breach'
import type { CustomerResponseDTO, BreachRecordResponseDTO } from '@/types'

// 扩展客户类型，添加违约相关信息
interface BreachedCustomer extends CustomerResponseDTO {
  totalBreachCount: number
  firstBreachTime: string
  latestBreachTime: string
  latestBreachSeverity: 'HIGH' | 'MEDIUM' | 'LOW'
}

// 状态和数据
const loading = ref(false)
const breachedCustomers = ref<BreachedCustomer[]>([])
const totalBreachedCustomers = ref(0)
const currentCustomer = ref<BreachedCustomer | null>(null)

// 对话框状态
const detailDialogVisible = ref(false)

// 搜索表单
const searchForm = reactive({
  customerName: '',
  industry: '',
  region: ''
})

// 分页参数
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 计算表格数据（模拟分页）
const breachedCustomersData = ref<BreachedCustomer[]>([])

// 统计数据
const highSeverityCount = computed(() => {
  return breachedCustomers.value.filter(customer => customer.latestBreachSeverity === 'HIGH').length
})

const mediumSeverityCount = computed(() => {
  return breachedCustomers.value.filter(customer => customer.latestBreachSeverity === 'MEDIUM').length
})

const lowSeverityCount = computed(() => {
  return breachedCustomers.value.filter(customer => customer.latestBreachSeverity === 'LOW').length
})

// 初始化加载数据
onMounted(() => {
  fetchBreachedCustomers()
})

// 获取违约客户列表
const fetchBreachedCustomers = async () => {
  loading.value = true
  try {
    // 在实际项目中，这里应该调用API获取数据
    // 由于没有真实API，使用模拟数据
    const mockData: BreachedCustomer[] = generateMockBreachedCustomers()
    breachedCustomers.value = mockData
    totalBreachedCustomers.value = mockData.length
    updatePaginationData()
  } catch (error) {
    ElMessage.error('获取违约客户失败')
    console.error('获取违约客户失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索违约客户
const searchBreachedCustomers = () => {
  pagination.currentPage = 1
  fetchBreachedCustomers()
}

// 重置搜索表单
const resetSearchForm = () => {
  searchForm.customerName = ''
  searchForm.industry = ''
  searchForm.region = ''
  pagination.currentPage = 1
  fetchBreachedCustomers()
}

// 分页处理
const updatePaginationData = () => {
  const startIndex = (pagination.currentPage - 1) * pagination.pageSize
  const endIndex = startIndex + pagination.pageSize
  
  // 应用搜索和筛选条件
  let filteredData = [...breachedCustomers.value]
  
  if (searchForm.customerName) {
    filteredData = filteredData.filter(item => 
      item.name.includes(searchForm.customerName)
    )
  }
  
  if (searchForm.industry) {
    filteredData = filteredData.filter(item => 
      item.industry === searchForm.industry
    )
  }
  
  if (searchForm.region) {
    filteredData = filteredData.filter(item => 
      item.region === searchForm.region
    )
  }
  
  totalBreachedCustomers.value = filteredData.length
  breachedCustomersData.value = filteredData.slice(startIndex, endIndex)
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

// 显示客户详情
const showCustomerDetails = async (id: number) => {
  try {
    // 在实际项目中，这里应该调用API获取详情
    const customer = breachedCustomers.value.find(item => item.id === id)
    if (customer) {
      currentCustomer.value = customer
      detailDialogVisible.value = true
    } else {
      ElMessage.error('未找到该客户')
    }
  } catch (error) {
    ElMessage.error('获取客户详情失败')
    console.error('获取客户详情失败:', error)
  }
}

// 查看违约记录
const viewBreachRecords = (customerId: number) => {
  // 在实际项目中，这里应该跳转到违约记录页面或打开违约记录对话框
  ElMessage.info(`查看客户ID: ${customerId} 的违约记录`)
}

// 关闭对话框
const handleClose = () => {
  currentCustomer.value = null
  detailDialogVisible.value = false
}

// 生成模拟数据
const generateMockBreachedCustomers = (): BreachedCustomer[] => {
  const industries = ['科技', '贸易', '物流', '金融', '制造']
  const regions = ['北京', '上海', '广州', '深圳', '杭州']
  const ratings = ['AAA', 'AA', 'A', 'BBB', 'BB']
  const severities: ('HIGH' | 'MEDIUM' | 'LOW')[] = ['HIGH', 'MEDIUM', 'LOW']
  
  const customers: BreachedCustomer[] = []
  
  for (let i = 1; i <= 20; i++) {
    const industry = industries[Math.floor(Math.random() * industries.length)]
    const region = regions[Math.floor(Math.random() * regions.length)]
    const rating = ratings[Math.floor(Math.random() * ratings.length)]
    const totalBreachCount = Math.floor(Math.random() * 5) + 1
    const latestBreachSeverity = severities[Math.floor(Math.random() * severities.length)]
    
    // 生成过去180天内的随机日期作为首次违约时间
    const now = new Date()
    const firstBreachDays = Math.floor(Math.random() * 180) + 30
    const firstBreachDate = new Date(now.getTime() - firstBreachDays * 24 * 60 * 60 * 1000)
    
    // 生成首次违约时间之后、现在之前的随机日期作为最新违约时间
    const latestBreachDays = Math.floor(Math.random() * (firstBreachDays - 1)) + 1
    const latestBreachDate = new Date(now.getTime() - latestBreachDays * 24 * 60 * 60 * 1000)
    
    // 生成创建时间（在首次违约之前）
    const createDays = firstBreachDays + Math.floor(Math.random() * 365) + 30
    const createDate = new Date(now.getTime() - createDays * 24 * 60 * 60 * 1000)
    
    // 格式化日期
    const formatDate = (date: Date) => {
      return date.toISOString().slice(0, 19).replace('T', ' ')
    }
    
    customers.push({
      id: i,
      name: `${region}${industry}有限公司${i}`,
      industry,
      region,
      externalRating: rating,
      isBreached: true,
      createTime: formatDate(createDate),
      totalBreachCount,
      firstBreachTime: formatDate(firstBreachDate),
      latestBreachTime: formatDate(latestBreachDate),
      latestBreachSeverity
    })
  }
  
  return customers
}
</script>

<style scoped>
.breached-customers-container {
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

.statistics-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.stat-card {
  flex: 1;
  min-width: 200px;
  background: #f8f9fa;
}

.stat-content {
  text-align: center;
  padding: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.breached-customers-table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.customer-detail-content {
  max-height: 500px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .statistics-cards {
    flex-direction: column;
  }
  
  .stat-card {
    min-width: auto;
  }
}

@media (max-width: 768px) {
  .breached-customers-container {
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
