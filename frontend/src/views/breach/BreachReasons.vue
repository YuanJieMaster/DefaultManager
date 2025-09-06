<template>
  <div class="breach-reasons-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>违约原因维护</h2>
      <p class="subtitle">管理系统中的违约原因数据</p>
    </div>

    <!-- 工具栏 -->
    <el-card class="toolbar-card">
      <div class="toolbar-content">
        <!-- 搜索框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="搜索违约原因内容"
          prefix-icon="Search"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        
        <!-- 状态筛选 -->
        <el-select v-model="statusFilter" placeholder="筛选状态" class="status-select">
          <el-option label="全部" value="" />
          <el-option label="启用" value="true" />
          <el-option label="禁用" value="false" />
        </el-select>
        
        <!-- 搜索按钮 -->
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        
        <!-- 重置按钮 -->
        <el-button @click="resetFilters">重置</el-button>
        
        <!-- 新增按钮 -->
        <el-button type="success" @click="handleCreate" style="margin-left: auto">
          <Plus /> 新增违约原因
        </el-button>
      </div>
    </el-card>

    <!-- 表格内容 -->
    <el-card class="reasons-table-card">
      <el-table
        v-loading="loading"
        :data="breachReasons"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="reasonContent" label="违约原因内容" min-width="200">
          <template #default="scope">
            <el-popover
              trigger="hover"
              placement="top"
              :content="scope.row.reasonContent"
              width="400"
            >
              <template #reference>
                <span class="reason-text">{{ truncateText(scope.row.reasonContent, 50) }}</span>
              </template>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序号" width="100" align="center" />
        <el-table-column prop="isEnabled" label="状态" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.isEnabled"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="handleStatusChange(scope.row.id, scope.row.isEnabled)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" danger @click="handleDelete(scope.row.id)">删除</el-button>
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
        <span class="stat-label">总原因数：</span>
        <span class="stat-value">{{ stats.totalReasons }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">启用原因：</span>
        <span class="stat-value enabled">{{ stats.enabledReasons }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">禁用原因：</span>
        <span class="stat-value disabled">{{ stats.disabledReasons }}</span>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        class="reason-form"
      >
        <el-form-item label="违约原因内容" prop="reasonContent">
          <el-input
            v-model="formData.reasonContent"
            type="textarea"
            placeholder="请输入违约原因内容"
            rows="3"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            :max="999"
            placeholder="请输入排序号"
            class="sort-input"
          />
        </el-form-item>
        <el-form-item label="状态" prop="isEnabled">
          <el-switch
            v-model="formData.isEnabled"
            active-color="#13ce66"
            inactive-color="#ff4949"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ submitting ? '提交中...' : '确定' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量删除确认弹窗 -->
    <el-dialog
      v-model="batchDeleteDialogVisible"
      title="确认删除"
      width="400px"
      :before-close="() => (batchDeleteDialogVisible = false)"
    >
      <div class="batch-delete-content">
        <div class="warning-icon">
          <ElWarning size="48" class="warning-svg" />
        </div>
        <div class="warning-text">
          <p>您确定要删除选中的 {{ selectedRowKeys.length }} 条违约原因吗？</p>
          <p class="warning-note">此操作不可撤销，删除后数据将无法恢复。</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="() => (batchDeleteDialogVisible = false)">取消</el-button>
          <el-button type="danger" @click="handleBatchDelete" :loading="batchDeleting">
            {{ batchDeleting ? '删除中...' : '确认删除' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { breachReasonApi } from '@/api/breach-reason'
import type { BreachReasonDTO, BreachReasonResponseDTO } from '@/types'

// 状态数据
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const submitting = ref(false)
const batchDeleting = ref(false)

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 统计数据
const stats = reactive({
  totalReasons: 0,
  enabledReasons: 0,
  disabledReasons: 0
})

// 违约原因数据
const breachReasons = ref<BreachReasonResponseDTO[]>([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增违约原因')
const formRef = ref()
const formData = reactive<BreachReasonDTO>({
  reasonContent: '',
  isEnabled: true,
  sortOrder: 0
})
let currentId: number | null = null

// 批量操作相关
const selectedRowKeys = ref<number[]>([])
const batchDeleteDialogVisible = ref(false)

// 表单验证规则
const formRules = {
  reasonContent: [
    { required: true, message: '请输入违约原因内容', trigger: 'blur' },
    { min: 1, max: 200, message: '原因内容长度在 1 到 200 个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序号', trigger: 'blur' },
    { type: 'number', min: 0, max: 999, message: '排序号必须在 0 到 999 之间', trigger: 'blur' }
  ]
}

// 获取违约原因数据
const fetchBreachReasons = async () => {
  loading.value = true
  try {
    // 获取所有违约原因
    const response = await breachReasonApi.getAllBreachReasons()
    let allReasons = response
    
    // 根据搜索关键词和状态筛选
    let filteredData = [...allReasons]
    
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredData = filteredData.filter(reason => 
        reason.reasonContent && reason.reasonContent.toLowerCase().includes(keyword)
      )
    }
    
    if (statusFilter.value !== '') {
      const isEnabled = statusFilter.value === 'true'
      filteredData = filteredData.filter(reason => reason.isEnabled === isEnabled)
    }
    
    // 按排序号升序排序
    filteredData.sort((a, b) => a.sortOrder - b.sortOrder)
    
    // 计算分页数据
    const total = filteredData.length
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    const paginatedData = filteredData.slice(start, end)
    
    // 更新表格数据和分页信息
    breachReasons.value = paginatedData
    pagination.total = total
    
    // 更新统计信息
    stats.totalReasons = filteredData.length
    stats.enabledReasons = filteredData.filter(r => r.isEnabled).length
    stats.disabledReasons = filteredData.filter(r => !r.isEnabled).length
  } catch (error) {
    ElMessage.error('获取违约原因失败')
    console.error('获取违约原因失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  fetchBreachReasons()
}

// 重置筛选条件
const resetFilters = () => {
  searchKeyword.value = ''
  statusFilter.value = ''
  pagination.currentPage = 1
  fetchBreachReasons()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  fetchBreachReasons()
}

// 当前页码变化
const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  fetchBreachReasons()
}

// 处理表格选中变化
const handleSelectionChange = (selection: BreachReasonResponseDTO[]) => {
  selectedRowKeys.value = selection.map(item => item.id)
}

// 打开新增对话框
const handleCreate = () => {
  dialogTitle.value = '新增违约原因'
  formData.reasonContent = ''
  formData.isEnabled = true
  formData.sortOrder = 0
  currentId = null
  dialogVisible.value = true
}

// 打开编辑对话框
const handleEdit = (row: BreachReasonResponseDTO) => {
  dialogTitle.value = '编辑违约原因'
  formData.reasonContent = row.reasonContent
  formData.isEnabled = row.isEnabled
  formData.sortOrder = row.sortOrder
  currentId = row.id
  dialogVisible.value = true
}

// 关闭对话框
const handleDialogClose = () => {
  dialogVisible.value = false
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (currentId) {
      // 更新违约原因
      await breachReasonApi.updateBreachReason(currentId, formData)
      ElMessage.success('违约原因更新成功')
    } else {
      // 创建违约原因
      await breachReasonApi.createBreachReason(formData)
      ElMessage.success('违约原因创建成功')
    }
    
    dialogVisible.value = false
    fetchBreachReasons()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(currentId ? '违约原因更新失败' : '违约原因创建失败')
      console.error(currentId ? '更新违约原因失败:' : '创建违约原因失败:', error)
    }
  } finally {
    submitting.value = false
  }
}

// 处理状态切换
const handleStatusChange = async (id: number, isEnabled: boolean) => {
  try {
    await breachReasonApi.toggleBreachReasonStatus(id)
    ElMessage.success('状态切换成功')
  } catch (error) {
    // 恢复原状态
    const reason = breachReasons.value.find(r => r.id === id)
    if (reason) {
      reason.isEnabled = !isEnabled
    }
    ElMessage.error('状态切换失败')
    console.error('切换违约原因状态失败:', error)
  }
}

// 删除违约原因
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条违约原因吗？此操作不可撤销。',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await breachReasonApi.deleteBreachReason(id)
    ElMessage.success('违约原因删除成功')
    fetchBreachReasons()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('违约原因删除失败')
      console.error('删除违约原因失败:', error)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRowKeys.value.length === 0) {
    ElMessage.warning('请选择要删除的违约原因')
    return
  }
  
  try {
    batchDeleting.value = true
    
    // 逐条删除（实际项目中可以考虑使用批量删除API）
    for (const id of selectedRowKeys.value) {
      await breachReasonApi.deleteBreachReason(id)
    }
    
    ElMessage.success(`成功删除 ${selectedRowKeys.value.length} 条违约原因`)
    batchDeleteDialogVisible.value = false
    selectedRowKeys.value = []
    fetchBreachReasons()
  } catch (error) {
    ElMessage.error('批量删除失败')
    console.error('批量删除违约原因失败:', error)
  } finally {
    batchDeleting.value = false
  }
}

// 文本截断工具函数
const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

// 组件挂载时获取数据
onMounted(() => {
  fetchBreachReasons()
})
</script>

<style scoped>
.breach-reasons-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
}

.page-header .subtitle {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.toolbar-card {
  margin-bottom: 24px;
}

.toolbar-content {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.search-input {
  width: 300px;
}

.status-select,
.severity-select {
  width: 150px;
}

.date-picker {
  width: 250px;
}

.reasons-table-card {
  margin-bottom: 24px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.stats-summary {
  display: flex;
  gap: 30px;
  padding: 16px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.stat-value.enabled {
  color: #13ce66;
}

.stat-value.disabled {
  color: #ff4949;
}

.reason-form {
  margin-top: 16px;
}

.sort-input {
  width: 200px;
}

.reason-text {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.batch-delete-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.warning-icon {
  margin-bottom: 16px;
  color: #e6a23c;
}

.warning-text {
  text-align: center;
}

.warning-text p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
}

.warning-note {
  color: #909399 !important;
  font-size: 12px !important;
}
</style>