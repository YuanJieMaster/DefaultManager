<template>
  <div class="breach-apply-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>违约申请</h2>
      <p class="subtitle">提交客户违约记录申请</p>
    </div>

    <!-- 申请表单卡片 -->
    <el-card class="apply-form-card">
      <el-form :model="breachForm" :rules="breachRules" ref="breachFormRef" label-width="120px">
        <!-- 客户信息 -->
        <div class="form-section">
          <h4>客户信息</h4>
          <el-form-item label="客户名称" prop="customerId">
            <el-select
              v-model="breachForm.customerId"
              filterable
              clearable
              placeholder="请选择客户"
              remote
              :remote-method="remoteSearchCustomers"
              :loading="searchLoading"
              :popper-class="'customer-select-popup'"
            >
              <el-option
                v-for="customer in customerOptions"
                :key="customer.id"
                :label="`${customer.name} (${customer.industry} - ${customer.region})`"
                :value="customer.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="客户状态">
            <el-tag type="danger" v-if="selectedCustomer?.isBreached">已违约</el-tag>
            <el-tag type="success" v-else-if="selectedCustomer">正常</el-tag>
            <el-tag type="info" v-else>未选择</el-tag>
            <div v-if="selectedCustomer?.isBreached" class="error-tip">
              <i class="el-icon-warning" style="color: #f56c6c;"></i>
              <span style="color: #f56c6c;">已是违约客户禁止发起</span>
            </div>
          </el-form-item>
        </div>

        <!-- 违约详情 -->
        <div class="form-section">
          <h4>违约详情</h4>
          <el-form-item label="违约原因" prop="breachReasonId">
            <el-select
              v-model="breachForm.breachReasonId"
              placeholder="请选择违约原因"
              clearable
            >
              <el-option
                v-for="reason in breachReasons"
                :key="reason.id"
                :label="reason.reasonContent"
                :value="reason.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="严重程度" prop="severity">
            <el-radio-group v-model="breachForm.severity">
              <el-radio-button label="HIGH">高</el-radio-button>
              <el-radio-button label="MEDIUM">中</el-radio-button>
              <el-radio-button label="LOW">低</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="相关文件" prop="files">
            <el-upload
              class="upload-demo"
              :action="uploadUrl"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :file-list="fileList"
              :auto-upload="false"
              multiple
              ref="uploadRef"
            >
              <template #trigger>
                <el-button type="primary">选择文件</el-button>
              </template>
              <el-button type="success" :disabled="fileList.length === 0" @click="submitUpload">上传文件</el-button>
              <div slot="tip" class="el-upload__tip">支持上传PDF、Word、Excel文件，单个文件不超过10MB</div>
            </el-upload>
          </el-form-item>
        </div>

        <!-- 申请人信息 -->
<!--        <div class="form-section">-->
<!--          <h4>申请人信息</h4>-->
<!--          <el-form-item label="申请人姓名">-->
<!--            <el-input v-model="currentUser.name" disabled />-->
<!--          </el-form-item>-->
<!--          <el-form-item label="申请部门">-->
<!--            <el-input v-model="currentUser.department" disabled />-->
<!--          </el-form-item>-->
<!--        </div>-->

        <!-- 表单操作按钮 -->
        <el-form-item>
          <el-button type="primary" :disabled="selectedCustomer?.isBreached" @click="submitBreachRecord">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="info" @click="cancelApply">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 申请指南 -->
    <el-card class="guide-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>申请指南</span>
        </div>
      </template>
      <div class="guide-content">
        <ul>
          <li>1. 请确保填写的客户信息准确无误</li>
          <li>2. 详细描述违约原因，有助于审核人员快速了解情况</li>
          <li>3. 根据违约的实际情况选择适当的严重程度</li>
          <li>4. 上传相关证明文件可以提高申请的审核效率</li>
          <li>5. 提交后，您可以在"违约记录"中查看申请状态</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { breachApi } from '@/api/breach'
import { customerApi } from '@/api/customer'
import { breachReasonApi } from '@/api/breach-reason'
import type { BreachRecordDTO, CustomerResponseDTO } from '@/types'

const router = useRouter()

// 表单引用和数据
const breachFormRef = ref()
const uploadRef = ref()
const breachForm = reactive<BreachRecordDTO>({
  customerId: 0,
  breachReasonId: 0,
  severity: 'MEDIUM',
  applicantId: 6 // 假设当前用户ID为1
})

// 违约原因列表
const breachReasons = ref<any[]>([])

// 表单验证规则
const breachRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'blur' }],
  breachReasonId: [{ required: true, message: '请选择违约原因', trigger: 'change' }],
  severity: [{ required: true, message: '请选择严重程度', trigger: 'change' }]
}

// 客户选择相关
const searchLoading = ref(false)
const customerOptions = ref<CustomerResponseDTO[]>([])
const selectedCustomer = ref<CustomerResponseDTO | null>(null)

// 上传相关
const uploadUrl = '/api/upload'
// 从Element Plus导入正确的UploadFile类型
import type { UploadFile } from 'element-plus'
const fileList = ref<UploadFile[]>([])

// 当前用户信息（模拟数据）
const currentUser = ref({
  name: '管理员',
  department: '风控部门'
})

// 远程搜索客户
const remoteSearchCustomers = async (query: string) => {
  if (!query) {
    customerOptions.value = []
    return
  }
  
  searchLoading.value = true
  try {
    // 调用API获取所有客户，然后在前端进行过滤
    // 注意：响应拦截器已经返回了response.data
    // 添加类型断言确保TypeScript识别返回类型
    const customers = (await customerApi.getAllCustomers()) as CustomerResponseDTO[]
    
    // 过滤匹配的客户
    customerOptions.value = customers.filter(customer =>
      customer.name.includes(query) || customer.industry.includes(query) || customer.region.includes(query)
    )
  } catch (error) {
    ElMessage.error('搜索客户失败')
  } finally {
    searchLoading.value = false
  }
}

// 组件挂载时加载违约原因列表
onMounted(() => {
  loadBreachReasons()
})

// 加载违约原因列表
const loadBreachReasons = async () => {
  try {
    // 调用API获取所有启用的违约原因
    const reasons = await breachReasonApi.getEnabledBreachReasons()
    breachReasons.value = reasons
  } catch (error) {
    ElMessage.error('获取违约原因列表失败')
    console.error('加载违约原因失败:', error)
  }
}

// 监听客户选择变化
watch(() => breachForm.customerId, async (newId) => {
  if (newId > 0) {
    try {
      // 调用API获取客户详情
      // 注意：响应拦截器已经返回了response.data
      selectedCustomer.value = await customerApi.getCustomerById(newId)
    } catch (error) {
      ElMessage.error('获取客户详情失败')
    }
  } else {
    selectedCustomer.value = null
  }
})

// 文件上传处理
const submitUpload = () => {
  uploadRef.value?.submit()
}

const handleUploadSuccess = (response: any, file: any) => {
  ElMessage.success(`${file.name} 文件上传成功`)
  // 将上传成功的文件添加到文件列表
  fileList.value.push({
    name: file.name,
    url: file.url || '',
    uid: file.uid
  })
}

const handleUploadError = (error: any, file: any) => {
  ElMessage.error(`${file.name} 文件上传失败`)
}

// 提交违约记录申请
const submitBreachRecord = async () => {
  try {
    if (!breachFormRef.value) return
    await breachFormRef.value.validate()
    
    // 显示提交确认对话框
    ElMessageBox.confirm(
      '确定要提交此违约申请吗？提交后将进入审核流程。',
      '提交确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      // 调用API提交违约记录
      await breachApi.createBreachRecord(breachForm)
      ElMessage.closeAll()
      
      ElMessage.success('违约申请提交成功')
      
      // 重置表单并跳转到违约记录页面
      resetForm()
      setTimeout(() => {
        router.push('/breach/records')
      }, 1000)
    }).catch(() => {
      // 用户取消提交
    })
  } catch (error) {
    // 表单验证失败或提交出错
    console.error('提交违约申请失败:', error)
    ElMessage.error('提交违约申请失败')
  }
}

// 重置表单
const resetForm = () => {
  if (breachFormRef.value) {
    breachFormRef.value.resetFields()
  }
  fileList.value = []
  selectedCustomer.value = null
}

// 取消申请
const cancelApply = () => {
  if (breachForm.customerId || breachForm.breachReasonId) {
    ElMessageBox.confirm(
      '确定要取消当前编辑的内容吗？未保存的信息将丢失。',
      '取消确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    ).then(() => {
      router.push('/breach/records')
    }).catch(() => {
      // 用户取消操作
    })
  } else {
    router.push('/breach/records')
  }
}
</script>

<style scoped>
.breach-apply-container {
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

.apply-form-card {
  margin-bottom: 20px;
}

.form-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.form-section:last-child {
  border-bottom: none;
}

.form-section h4 {
  margin-bottom: 16px;
  color: #303133;
  font-size: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.guide-content {
  padding: 10px 0;
}

.guide-content ul {
  margin: 0;
  padding-left: 20px;
}

.guide-content li {
  margin-bottom: 8px;
  color: #606266;
  line-height: 1.6;
}

/* 调整客户选择下拉框样式 */
.customer-select-popup {
  max-height: 300px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .breach-apply-container {
    padding: 10px;
  }
  
  .form-section h4 {
    font-size: 14px;
  }
}
</style>
