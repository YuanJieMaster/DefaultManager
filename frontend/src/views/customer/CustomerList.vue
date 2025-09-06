<template>
  <div class="customer-list-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>客户列表</h2>
      <p class="subtitle">管理所有合作客户信息</p>
    </div>

    <!-- 工具栏 -->
    <el-card class="toolbar-card">
      <div class="toolbar-content">
        <!-- 搜索框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="搜索客户名称"
          :prefix-icon="Search"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        
        <!-- 行业筛选 -->
        <el-select v-model="industryFilter" placeholder="筛选行业" class="industry-select">
          <el-option label="全部" value="" />
          <el-option label="科技" value="TECH" />
          <el-option label="贸易" value="TRADE" />
          <el-option label="物流" value="LOGISTICS" />
          <el-option label="金融" value="FINANCE" />
          <el-option label="医疗" value="MEDICAL" />
          <el-option label="环保" value="ENVIRONMENT" />
        </el-select>
        
        <!-- 状态筛选 -->
        <el-select v-model="statusFilter" placeholder="筛选状态" class="status-select">
          <el-option label="全部" value="" />
          <el-option label="正常" value="NORMAL" />
          <el-option label="预警" value="WARNING" />
          <el-option label="冻结" value="BLOCKED" />
        </el-select>
        
        <!-- 搜索按钮 -->
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        
        <!-- 重置按钮 -->
        <el-button @click="resetFilters">重置</el-button>
        
        <!-- 新增按钮 -->
        <el-button type="success" @click="handleCreate" style="margin-left: auto">
          <Plus /> 新增客户
        </el-button>
      </div>
    </el-card>

    <!-- 表格内容 -->
    <el-card class="records-table-card">
      <el-table
        v-loading="loading"
        :data="customers"
        style="width: 100%"
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="客户ID" width="80" align="center" />
        <el-table-column prop="name" label="客户名称" width="180">
          <template #default="scope">
            <el-link type="primary" @click="viewCustomerDetail(scope.row.id)">{{ scope.row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="industry" label="所属行业" width="100" align="center">
          <template #default="scope">
            <el-tag>{{ getIndustryLabel(scope.row.industry) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="region" label="地区" width="100" align="center" />
        <el-table-column prop="externalRating" label="评级" width="100" align="center" />
        <el-table-column prop="isBreached" label="是否违约" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isBreached ? 'danger' : 'success'">
              {{ scope.row.isBreached ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="text" @click="viewCustomerDetail(scope.row.id)">查看</el-button>
            <el-button type="text" @click="editCustomer(scope.row.id)">编辑</el-button>
            <el-button 
              type="text" 
              :danger="scope.row.status !== 'BLOCKED'"
              :text="scope.row.status === 'BLOCKED'"
              @click="toggleCustomerStatus(scope.row.id, scope.row.status)"
            >
              {{ scope.row.status === 'BLOCKED' ? '解冻' : '冻结' }}
            </el-button>
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
        <span class="stat-label">总客户数：</span>
        <span class="stat-value">{{ stats.totalCustomers }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">正常：</span>
        <span class="stat-value normal">{{ stats.normalCustomers }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">预警：</span>
        <span class="stat-value warning">{{ stats.warningCustomers }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">冻结：</span>
        <span class="stat-value blocked">{{ stats.blockedCustomers }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已违约：</span>
        <span class="stat-value breached">{{ stats.breachedCustomers }}</span>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑客户' : '新增客户'"
      width="800px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        class="customer-form"
      >
        <el-form-item label="客户名称" prop="name" required>
          <el-input v-model="formData.name" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson" required>
          <el-input v-model="formData.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone" required>
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="所属行业" prop="industry" required>
          <el-select v-model="formData.industry" placeholder="请选择所属行业">
            <el-option label="科技" value="TECH" />
            <el-option label="贸易" value="TRADE" />
            <el-option label="物流" value="LOGISTICS" />
            <el-option label="金融" value="FINANCE" />
            <el-option label="医疗" value="MEDICAL" />
            <el-option label="环保" value="ENVIRONMENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户地址" prop="address">
          <el-input v-model="formData.address" type="textarea" rows="2" placeholder="请输入客户地址" />
        </el-form-item>
        <el-form-item label="客户邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入客户邮箱" />
        </el-form-item>
        <el-form-item label="客户状态" prop="status" required>
          <el-select v-model="formData.status" placeholder="请选择客户状态">
            <el-option label="正常" value="NORMAL" />
            <el-option label="预警" value="WARNING" />
            <el-option label="冻结" value="BLOCKED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息" prop="remark">
          <el-input v-model="formData.remark" type="textarea" rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { customerApi } from '@/api/customer'
import type { CustomerResponseDTO, CustomerDTO } from '@/types'

const router = useRouter()

// 状态数据
const loading = ref(false)
const searchKeyword = ref('')
const industryFilter = ref('')
const statusFilter = ref('')

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 统计数据
const stats = reactive({
  totalCustomers: 0,
  normalCustomers: 0,
  warningCustomers: 0,
  blockedCustomers: 0,
  breachedCustomers: 0
})

// 客户数据
const customers = ref<CustomerResponseDTO[]>([])

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<InstanceType<typeof ElForm>>()
const formData = reactive<CustomerDTO>({
  id: null,
  name: '',
  contactPerson: '',
  phone: '',
  industry: '',
  address: '',
  email: '',
  status: 'NORMAL',
  remark: ''
})

// 表单验证规则
const formRules = reactive({
  name: [
    { required: true, message: '请输入客户名称', trigger: 'blur' },
    { min: 2, max: 50, message: '客户名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '联系人姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  industry: [
    { required: true, message: '请选择所属行业', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择客户状态', trigger: 'change' }
  ]
})

// 获取客户列表数据
const fetchCustomers = async () => {
  loading.value = true
  try {
    // 调用API获取数据，传递分页和筛选参数
    const response = await customerApi.getAllCustomers()
    
    // 根据筛选条件过滤数据
    let filteredData = [...response]
    
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredData = filteredData.filter(customer => 
        customer.name.toLowerCase().includes(keyword)
      )
    }
    
    if (industryFilter.value) {
      filteredData = filteredData.filter(customer => customer.industry === industryFilter.value)
    }
    
    if (statusFilter.value) {
      filteredData = filteredData.filter(customer => customer.status === statusFilter.value)
    }
    
    // 计算分页数据
    const total = filteredData.length
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    const paginatedData = filteredData.slice(start, end)
    
    // 更新表格数据和分页信息
    customers.value = paginatedData
    pagination.total = total
    
    // 更新统计信息
    stats.totalCustomers = filteredData.length
    stats.normalCustomers = filteredData.filter(c => c.status === 'NORMAL').length
    stats.warningCustomers = filteredData.filter(c => c.status === 'WARNING').length
    stats.blockedCustomers = filteredData.filter(c => c.status === 'BLOCKED').length
    stats.breachedCustomers = filteredData.filter(c => c.isBreached).length
  } catch (error) {
    ElMessage.error('获取客户列表失败')
    console.error('获取客户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 调用API保存数据
    if (isEdit.value) {
      await customerApi.updateCustomer(formData.id!, formData)
    } else {
      await customerApi.createCustomer(formData)
    }
    
    dialogVisible.value = false
    ElMessage.success(isEdit.value ? '客户信息更新成功' : '客户新增成功')
    fetchCustomers()
  } catch (error) {
    ElMessage.error(isEdit.value ? '客户信息更新失败' : '客户新增失败')
    console.error(isEdit.value ? '更新客户失败:' : '新增客户失败:', error)
  }
}

// 获取行业标签
const getIndustryLabel = (industry: string): string => {
  const industryMap: Record<string, string> = {
    'TECH': '科技',
    'TRADE': '贸易',
    'LOGISTICS': '物流',
    'FINANCE': '金融',
    'MEDICAL': '医疗',
    'ENVIRONMENT': '环保'
  }
  return industryMap[industry] || industry
}

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  fetchCustomers()
}

// 重置筛选条件
const resetFilters = () => {
  searchKeyword.value = ''
  industryFilter.value = ''
  statusFilter.value = ''
  pagination.currentPage = 1
  fetchCustomers()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  fetchCustomers()
}

// 当前页码变化
const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  fetchCustomers()
}

// 查看客户详情
const viewCustomerDetail = (customerId: number) => {
  // 由于CustomerDetail.vue文件不存在，这里暂时用提示代替
  ElMessage.info(`查看客户ID: ${customerId} 的详情`)
  // 在实际项目中，这里应该跳转到客户详情页面
  // router.push(`/customer/detail/${customerId}`)
}

// 打开新增对话框
const handleCreate = () => {
  isEdit.value = false
  // 重置表单数据
  Object.assign(formData, {
    id: null,
    name: '',
    contactPerson: '',
    phone: '',
    industry: '',
    address: '',
    email: '',
    status: 'NORMAL',
    remark: ''
  })
  dialogVisible.value = true
}

// 打开编辑对话框
const editCustomer = (customerId: number) => {
  const customer = customers.value.find(c => c.id === customerId)
  if (customer) {
    isEdit.value = true
    // 填充表单数据
    Object.assign(formData, {
      id: customer.id,
      name: customer.name,
      contactPerson: customer.contactPerson,
      phone: customer.phone,
      industry: customer.industry,
      address: customer.address || '',
      email: customer.email || '',
      status: customer.status,
      remark: customer.remark || ''
    })
    dialogVisible.value = true
  }
}

// 切换客户状态
const toggleCustomerStatus = (customerId: number, currentStatus: string) => {
  const newStatus = currentStatus === 'BLOCKED' ? 'NORMAL' : 'BLOCKED'
  const statusText = newStatus === 'NORMAL' ? '解冻' : '冻结'
  
  ElMessageBox.confirm(
    `确定要${statusText}此客户吗？${statusText}后可能影响与该客户的业务合作。`,
    `${statusText}确认`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 创建一个只包含状态字段的更新对象
      const updateData = { ...formData }
      updateData.status = newStatus
      
      // 调用API更新客户状态
      await customerApi.updateCustomer(customerId, updateData)
      
      ElMessage.success(`${statusText}成功`)
      fetchCustomers()
    } catch (error) {
      ElMessage.error(`${statusText}失败`)
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 生命周期钩子
onMounted(() => {
  fetchCustomers()
})
</script>

<style scoped>
.customer-list-container {
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

.industry-select,
.status-select {
  width: 120px;
}

.records-table-card {
  margin-bottom: 20px;
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

.stat-value.normal {
  color: #67c23a;
}

.stat-value.warning {
  color: #e6a23c;
}

.stat-value.blocked {
  color: #f56c6c;
}

.stat-value.breached {
  color: #f56c6c;
}

.customer-form {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-input {
    width: 200px;
  }
}

@media (max-width: 768px) {
  .customer-list-container {
    padding: 10px;
  }
  
  .toolbar-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input,
  .industry-select,
  .status-select {
    width: 100%;
  }
  
  .stats-summary {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
