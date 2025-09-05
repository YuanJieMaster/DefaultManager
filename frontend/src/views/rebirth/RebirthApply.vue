<template>
  <div class="rebirth-apply-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h2>重生申请</h2>
        <p class="subtitle">提交客户违约重生申请，帮助客户恢复正常合作状态</p>
      </div>
      <div class="header-actions">
        <el-button type="info" size="small" icon="DocumentCopy" @click="showExample">
          查看示例
        </el-button>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <!-- 申请表单卡片 -->
    <el-card class="form-card">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        class="rebirth-form"
      >
        <!-- 客户选择 -->
        <el-form-item label="客户名称" prop="customerId" required>
          <el-select
            v-model="formData.customerId"
            filterable
            remote
            reserve-keyword
            placeholder="搜索客户"
            :remote-method="searchCustomers"
            :loading="customerLoading"
            @change="onCustomerChange"
            class="select-animation"
          >
            <el-option
              v-for="customer in customerOptions"
              :key="customer.id"
              :label="customer.name"
              :value="customer.id"
            />
          </el-select>
        </el-form-item>

        <!-- 客户信息展示 -->
        <template v-if="selectedCustomer">
          <div class="customer-info-section">
            <el-form-item label="联系电话">
              <el-input v-model="selectedCustomer.phone" disabled class="info-input" :prefix-icon="Phone" />
            </el-form-item>
            <el-form-item label="所属行业">
              <el-input v-model="selectedCustomer.industry" disabled class="info-input" :prefix-icon="Briefcase" />
            </el-form-item>
            <el-form-item label="合作状态">
              <el-tag :type="getStatusTagType(selectedCustomer.status)" size="large" class="status-tag">
                {{ getStatusLabel(selectedCustomer.status) }}
              </el-tag>
            </el-form-item>
          </div>
        </template>

        <!-- 违约记录选择 -->
        <el-form-item label="违约记录" prop="breachRecordIds" required>
          <el-select
            v-model="formData.breachRecordIds"
            placeholder="选择需要重生的违约记录"
            multiple
            collapse-tags
            :loading="breachRecordsLoading"
            :disabled="!formData.customerId"
            class="select-animation"
          >
            <el-option
              v-for="record in breachRecordOptions"
              :key="record.id"
              :label="`[${getSeverityLabel(record.severity)}] ${truncateText(record.reason, 30)} (${record.createTime})`"
              :value="record.id"
            >
              <div class="option-content">
                <div class="option-title">{{ record.reason }}</div>
                <div class="option-meta">
                  <el-tag size="small" :type="getSeverityTagType(record.severity)">{{ getSeverityLabel(record.severity) }}</el-tag>
                  <el-tag size="small" :type="getStatusTagType(record.status)">{{ getStatusLabel(record.status) }}</el-tag>
                  <span class="create-time">{{ record.createTime }}</span>
                </div>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 申请理由 -->
        <el-form-item label="申请理由" prop="reason" required>
          <el-input
            v-model="formData.reason"
            type="textarea"
            rows="4"
            placeholder="请详细说明客户申请重生的理由和依据"
            class="textarea-animation"
          />
          <div class="char-count">{{ formData.reason.length }}/500</div>
        </el-form-item>

        <!-- 整改措施 -->
        <el-form-item label="整改措施" prop="rectificationMeasures">
          <el-input
            v-model="formData.rectificationMeasures"
            type="textarea"
            rows="3"
            placeholder="请描述客户针对违约问题采取的整改措施"
            class="textarea-animation"
          />
          <div class="char-count">{{ formData.rectificationMeasures.length }}/300</div>
        </el-form-item>

        <!-- 预期效果 -->
        <el-form-item label="预期效果" prop="expectedEffects">
          <el-input
            v-model="formData.expectedEffects"
            type="textarea"
            rows="3"
            placeholder="请描述实施整改措施后的预期效果"
            class="textarea-animation"
          />
          <div class="char-count">{{ formData.expectedEffects.length }}/300</div>
        </el-form-item>

        <!-- 申请时间 -->
        <el-form-item label="申请时间">
          <el-date-picker
            v-model="formData.applyTime"
            type="datetime"
            placeholder="选择申请时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            class="date-picker"
          />
        </el-form-item>

        <!-- 附件上传 -->
        <el-form-item label="支持材料" prop="attachments">
          <el-upload
            v-model:file-list="formData.attachments"
            class="upload-demo"
            multiple
            action=""
            :show-file-list="true"
            :before-upload="beforeUpload"
            :on-remove="handleRemove"
            :auto-upload="false"
          >
            <el-button type="primary" class="upload-button">
              <el-icon><Upload /></el-icon>
              选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip text-muted">
                支持上传PDF、Word、Excel等格式文件，单个文件不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <!-- 申请指南 -->
        <el-divider />
        <div class="guide-section">
          <h4><el-icon><Help /></el-icon> 重生申请指南</h4>
          <ul class="guide-list">
            <li class="guide-item"><span class="step-number">1</span> 请确保客户信息准确无误，选择正确的客户。</li>
            <li class="guide-item"><span class="step-number">2</span> 选择需要申请重生的违约记录，可多选。</li>
            <li class="guide-item"><span class="step-number">3</span> 详细填写申请理由，包括客户违约情况的说明和改进措施。</li>
            <li class="guide-item"><span class="step-number">4</span> 上传相关支持材料，如客户整改报告、承诺书等。</li>
            <li class="guide-item"><span class="step-number">5</span> 提交申请后，系统将进行审核，审核结果将通过系统通知告知您。</li>
          </ul>
        </div>

        <!-- 表单操作按钮 -->
        <div class="form-actions">
          <el-button @click="resetForm" class="reset-button">重置</el-button>
          <el-button type="primary" @click="submitForm" class="submit-button" :loading="submitting">
            <el-icon v-if="submitting"><Loading /></el-icon>
            <span>提交申请</span>
          </el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 示例对话框 -->
    <el-dialog
      v-model="exampleDialogVisible"
      title="重生申请示例"
      width="600px"
      append-to-body
    >
      <div class="example-content">
        <h4>申请理由示例：</h4>
        <p class="example-text">
          客户北京星辰科技有限公司因资金周转困难，未能按时履行合同付款义务，违约记录编号为102和101。经过与客户沟通协商，客户已制定详细的还款计划，并提供了相应的财务担保。客户表示将严格按照新的还款计划执行，确保未来不再发生类似违约情况。考虑到该客户与我方有长期合作关系，且此次违约为首次发生，建议给予该客户重生机会。
        </p>
        
        <h4 class="mt-4">整改措施示例：</h4>
        <p class="example-text">
          1. 客户已制定详细的月度还款计划，并提供了加盖公章的书面承诺；<br/>
          2. 客户已提供第三方担保，确保还款计划的执行；<br/>
          3. 客户已调整内部财务流程，加强资金管理和风险控制；<br/>
          4. 客户同意在还清所有欠款前，提高预付款比例。
        </p>
        
        <h4 class="mt-4">预期效果示例：</h4>
        <p class="example-text">
          通过此次重生申请，预计客户将恢复正常的合作状态，继续履行合同义务。客户承诺将严格遵守我方的合作规范，按时付款，并保持良好的沟通。我方将对客户的还款情况进行持续监控，确保各项措施得到有效执行。
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElLoading, ElForm, ElDialog, ElMessageBox } from 'element-plus'
import { Help, Upload, Phone, Briefcase, Loading, DocumentCopy } from '@element-plus/icons-vue'
import { customerApi } from '@/api/customer'
import { breachApi } from '@/api/breach'
import { rebirthApi } from '@/api/rebirth'
import type { CustomerResponseDTO } from '@/types'
import type { BreachRecordResponseDTO } from '@/types'
import type { RebirthApplyDTO } from '@/types'

// 表单引用
const formRef = ref<InstanceType<typeof ElForm>>()

// 加载状态
const customerLoading = ref(false)
const breachRecordsLoading = ref(false)
const submitting = ref(false)

// 示例对话框
const exampleDialogVisible = ref(false)

// 客户选项
const customerOptions = ref<{ id: number; name: string }[]>([])
const selectedCustomer = ref<Partial<CustomerResponseDTO> | null>(null)

// 违约记录选项
const breachRecordOptions = ref<BreachRecordResponseDTO[]>([])

// 表单数据
const formData = reactive<RebirthApplyDTO>({
  customerId: null,
  customerName: '',
  breachRecordIds: [],
  reason: '',
  rectificationMeasures: '',
  expectedEffects: '',
  applyTime: new Date().toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).replace(/\//g, '-'),
  attachments: []
})

// 表单验证规则
const formRules = reactive({
  customerId: [
    { required: true, message: '请选择客户', trigger: 'change' }
  ],
  breachRecordIds: [
    { required: true, message: '请选择违约记录', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请填写申请理由', trigger: 'blur' },
    { min: 20, message: '申请理由至少20个字符', trigger: 'blur' },
    { max: 500, message: '申请理由最多500个字符', trigger: 'blur' }
  ],
  rectificationMeasures: [
    { max: 300, message: '整改措施最多300个字符', trigger: 'blur' }
  ],
  expectedEffects: [
    { max: 300, message: '预期效果最多300个字符', trigger: 'blur' }
  ]
})

// 监听表单数据变化，添加自动保存逻辑（实际项目中可以实现）
watch(
  () => ({ ...formData }),
  () => {
    // 可以添加自动保存逻辑
  },
  { deep: true, debounce: 1000 }
)

// 搜索客户
const searchCustomers = async (keyword: string) => {
  if (!keyword.trim()) {
    customerOptions.value = []
    return
  }

  customerLoading.value = true
  try {
    // 调用API获取所有客户，然后在前端进行模糊过滤
    const allCustomers = await customerApi.getAllCustomers()
    // 在前端进行模糊搜索过滤
    const filteredCustomers = allCustomers.filter((customer: CustomerResponseDTO) => 
      customer.name.toLowerCase().includes(keyword.toLowerCase())
    )
    customerOptions.value = filteredCustomers.map((customer: CustomerResponseDTO) => ({
      id: customer.id,
      name: customer.name
    }))
  } catch (error) {
    ElMessage.error('搜索客户失败')
    console.error('搜索客户失败:', error)
  } finally {
    customerLoading.value = false
  }
}

// 客户选择变化
const onCustomerChange = async (customerId: number) => {
  if (!customerId) {
    selectedCustomer.value = null
    breachRecordOptions.value = []
    formData.customerName = ''
    return
  }

  try {
    // 检查客户是否可以申请重生
    const canRebirth = await rebirthApi.canCustomerRebirth(customerId)
    if (!canRebirth) {
      ElMessage.warning('该客户当前不能申请重生，请检查客户状态')
      formData.customerId = null
      selectedCustomer.value = null
      breachRecordOptions.value = []
      return
    }

    // 调用API获取客户详情
    const customerDetail = await customerApi.getCustomerById(customerId)
    selectedCustomer.value = customerDetail
    formData.customerName = customerDetail.name
    
    // 获取客户相关的违约记录
    await loadCustomerBreachRecords(customerId)
  } catch (error) {
    ElMessage.error('获取客户信息失败')
    console.error('获取客户信息失败:', error)
  }
}

// 加载客户违约记录
const loadCustomerBreachRecords = async (customerId: number) => {
  breachRecordsLoading.value = true
  try {
    // 调用API获取客户违约记录
    breachRecordOptions.value = await breachApi.getBreachRecordsByCustomerId(customerId)
  } catch (error) {
    ElMessage.error('获取违约记录失败')
    console.error('获取违约记录失败:', error)
  } finally {
    breachRecordsLoading.value = false
  }
}

// 获取严重程度标签
const getSeverityLabel = (severity: string): string => {
  const severityMap: Record<string, string> = {
    'HIGH': '高',
    'MEDIUM': '中',
    'LOW': '低'
  }
  return severityMap[severity] || severity
}

// 获取严重程度标签类型
const getSeverityTagType = (severity: string): string => {
  const typeMap: Record<string, string> = {
    'HIGH': 'danger',
    'MEDIUM': 'warning',
    'LOW': 'success'
  }
  return typeMap[severity] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string): string => {
  const statusMap: Record<string, string> = {
    'PENDING': '待处理',
    'APPROVED': '已批准',
    'REJECTED': '已拒绝',
    'BLOCKED': '已冻结',
    'WARNING': '预警中',
    'NORMAL': '正常'
  }
  return statusMap[status] || status
}

// 获取状态标签类型
const getStatusTagType = (status: string): string => {
  const typeMap: Record<string, string> = {
    'PENDING': 'info',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'BLOCKED': 'danger',
    'WARNING': 'warning',
    'NORMAL': 'success'
  }
  return typeMap[status] || 'info'
}

// 文本截断
const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) {
    return text
  }
  return text.substring(0, maxLength) + '...'
}

// 文件上传前处理
const beforeUpload = (file: File) => {
  // 检查文件类型和大小
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB!')
    return false
  }
  
  // 检查文件类型
  const validTypes = ['application/pdf', 'application/msword', 
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet']
  
  if (!validTypes.includes(file.type)) {
    ElMessage.error('仅支持PDF、Word、Excel格式文件!')
    return false
  }
  
  return true
}

// 文件移除处理
const handleRemove = (uploadFile: any, uploadFiles: any) => {
  formData.attachments = uploadFiles
  ElMessage.success('文件已移除')
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    ElMessageBox.confirm('确定要重置表单吗？当前填写的内容将会丢失。', '确认重置', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      formRef.value?.resetFields()
      selectedCustomer.value = null
      breachRecordOptions.value = []
      formData.attachments = []
      formData.applyTime = new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      }).replace(/\//g, '-')
      ElMessage.success('表单已重置')
    }).catch(() => {
      // 用户取消重置
    })
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitting.value = true
    
    try {
      // 构建提交数据
      const submitData = {
        customerId: formData.customerId,
        breachId: formData.breachRecordIds[0], // 后端目前只支持单个违约记录
        reason: formData.reason,
        applicantId: 1 // 实际项目中应从登录状态获取当前用户ID
      }
      
      // 调用API提交申请
      await rebirthApi.createRebirthRecord(submitData)
      
      ElMessage.success('重生申请提交成功')
      
      // 显示提交成功后的操作建议
      ElMessageBox.alert(
        '申请已成功提交，请等待系统审核。审核结果将通过系统通知告知您。',
        '提交成功',
        {
          confirmButtonText: '确定',
          type: 'success',
          callback: () => {
            resetForm()
          }
        }
      )
    } catch (error) {
      ElMessage.error('提交申请失败，请重试')
      console.error('提交重生申请失败:', error)
    } finally {
      submitting.value = false
    }
  } catch (error) {
    // 表单验证失败
    console.log('表单验证失败:', error)
  }
}

// 显示示例
const showExample = () => {
  exampleDialogVisible.value = true
}
</script>

<style scoped>
/* 基础容器样式 */
.rebirth-apply-container {
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

/* 表单卡片 */
.form-card {
  max-width: 900px;
  margin: 0 auto;
  position: relative;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.form-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.rebirth-form {
  margin-top: 20px;
}

/* 选择框动画 */
.select-animation {
  transition: all 0.3s ease;
}

.select-animation:hover .el-input__wrapper {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 文本域动画 */
.textarea-animation {
  transition: all 0.3s ease;
}

.textarea-animation:hover .el-textarea__wrapper {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 字符计数 */
.char-count {
  text-align: right;
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}

/* 客户信息卡片 */
.customer-info-section {
  background-color: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.info-input {
  background-color: #ffffff;
}

.status-tag {
  font-size: 14px;
  padding: 8px 16px;
  height: auto;
}

/* 选项内容样式 */
.option-content {
  padding: 8px;
}

.option-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #303133;
}

.option-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

.create-time {
  margin-left: auto;
}

/* 日期选择器 */
.date-picker .el-input__wrapper {
  transition: all 0.3s ease;
}

.date-picker:hover .el-input__wrapper {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 上传按钮 */
.upload-button {
  transition: all 0.3s ease;
}

.upload-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 申请指南 */
.guide-section {
  margin-top: 20px;
  padding: 20px;
  background-color: #f0f2f5;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.guide-section h4 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.guide-list {
  margin: 0;
  padding-left: 0;
  list-style: none;
}

.guide-item {
  margin-bottom: 12px;
  line-height: 1.6;
  color: #606266;
  position: relative;
  padding-left: 30px;
  display: flex;
  align-items: flex-start;
}

.step-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  margin-right: 10px;
  position: absolute;
  left: 0;
  top: 2px;
}

/* 表单操作按钮 */
.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
}

.reset-button {
  transition: all 0.3s ease;
}

.reset-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.submit-button {
  transition: all 0.3s ease;
  min-width: 120px;
}

.submit-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 示例对话框 */
.example-content {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;
}

.example-content h4 {
  color: #303133;
  font-size: 16px;
  margin-bottom: 10px;
}

.example-text {
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
  background-color: #f8f9fa;
  padding: 12px;
  border-radius: 4px;
  white-space: pre-wrap;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .form-card {
    max-width: 100%;
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

@media (max-width: 768px) {
  .rebirth-apply-container {
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
  
  .customer-info-section {
    padding: 10px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
  
  .guide-item {
    padding-left: 25px;
  }
}

@media (max-width: 480px) {
  .rebirth-apply-container {
    padding: 5px;
  }
  
  .form-card {
    border-radius: 8px;
  }
  
  .rebirth-form {
    margin-top: 10px;
  }
  
  .guide-section {
    padding: 12px;
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

:deep(.el-form-item__label.is-required:not(.is-asterisk))::before,
:deep(.el-form-item__label.is-required.is-asterisk)::after {
  color: #f56c6c;
}

:deep(.el-form-item__error) {
  color: #f56c6c;
}

:deep(.el-dialog__body) {
  color: #303133;
}

/* 滚动条样式 */
.example-content::-webkit-scrollbar {
  width: 6px;
}

.example-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.example-content::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.example-content::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>
