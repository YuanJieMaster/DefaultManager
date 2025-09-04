<template>
  <div class="system-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">系统设置</h1>
      <p class="subtitle">管理用户信息和系统配置</p>
    </div>

    <!-- 用户信息卡片 -->
    <el-card class="user-info-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户信息</span>
          <el-badge v-if="auth.isAdmin" type="primary" text="管理员" class="admin-badge" />
        </div>
      </template>
      <div class="user-info-content">
        <div class="avatar-section">
          <div class="avatar-container">
            <el-avatar :size="100" :src="avatarUrl" class="user-avatar" />
            <div class="avatar-upload-overlay">
              <el-icon class="upload-icon"><Upload /></el-icon>
              <span>更换头像</span>
            </div>
            <input type="file" ref="avatarInput" accept="image/*" class="avatar-input" @change="handleAvatarUpload" />
          </div>
          <el-button type="primary" size="small" style="margin-top: 16px" @click="uploadAvatar">
            <el-icon><Upload /></el-icon>
            更换头像
          </el-button>
        </div>
        <div class="user-details">
          <div class="detail-item">
            <span class="detail-label">用户名：</span>
            <span class="detail-value">{{ auth.username || '管理员' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">角色：</span>
            <el-tag type="primary" class="role-tag">管理员</el-tag>
          </div>
          <div class="detail-item">
            <span class="detail-label">上次登录：</span>
            <span class="detail-value">{{ lastLoginTime }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">登录IP：</span>
            <span class="detail-value">{{ lastLoginIp }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">系统版本：</span>
            <el-tag type="info" class="version-tag">{{ systemVersion }}</el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 设置选项卡 -->
    <el-card class="settings-card" shadow="hover" style="margin-top: 24px">
      <template #header>
        <div class="card-header">
          <span class="card-title">系统设置</span>
        </div>
      </template>
      <el-tabs v-model="activeTab" class="settings-tabs" type="card">
        <!-- 密码设置 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px" class="setting-form">
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input v-model="passwordForm.currentPassword" type="password" placeholder="请输入当前密码" show-password class="form-input" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password class="form-input" @input="checkPasswordStrength" />
              <!-- 密码强度指示器 -->
              <div v-if="passwordForm.newPassword" class="password-strength" style="margin-top: 8px">
                <div class="strength-labels">
                  <span>密码强度：</span>
                  <span :class="['strength-text', `strength-${passwordStrength}`]">{{ passwordStrengthText }}</span>
                </div>
                <div class="strength-bar">
                  <div :class="['strength-indicator', `strength-${passwordStrength}`]" :style="{ width: passwordStrengthWidth }"></div>
                </div>
                <div class="strength-tips">{{ passwordTips }}</div>
              </div>
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password class="form-input" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updatePassword" :loading="passwordUpdating" class="form-button">
                <el-icon v-if="passwordUpdating"><Loading /></el-icon>
                {{ passwordUpdating ? '处理中...' : '确认修改' }}
              </el-button>
              <el-button @click="resetPasswordForm" class="form-button">取消</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 通知设置 -->
        <el-tab-pane label="通知设置" name="notification">
          <el-form :model="notificationForm" ref="notificationFormRef" label-width="120px" class="setting-form">
            <el-form-item label="违约提醒" prop="breachNotification">
              <el-switch v-model="notificationForm.breachNotification" class="custom-switch" size="large" />
              <span class="switch-desc">接收新的违约记录通知</span>
            </el-form-item>
            <el-form-item label="审核通知" prop="reviewNotification">
              <el-switch v-model="notificationForm.reviewNotification" class="custom-switch" size="large" />
              <span class="switch-desc">接收待审核申请的通知</span>
            </el-form-item>
            <el-form-item label="系统更新通知" prop="systemNotification">
              <el-switch v-model="notificationForm.systemNotification" class="custom-switch" size="large" />
              <span class="switch-desc">接收系统更新和维护通知</span>
            </el-form-item>
            <el-form-item label="邮件通知" prop="email" v-if="notificationForm.breachNotification || notificationForm.reviewNotification || notificationForm.systemNotification">
              <el-input v-model="notificationForm.email" placeholder="请输入接收通知的邮箱地址" class="form-input" />
              <div class="form-tip">* 开启通知时请填写有效的邮箱地址</div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveNotificationSettings" :loading="notificationSaving" class="form-button">
                <el-icon v-if="notificationSaving"><Loading /></el-icon>
                {{ notificationSaving ? '保存中...' : '保存设置' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 数据备份 -->
        <el-tab-pane label="数据备份" name="backup">
          <div class="backup-section">
            <div class="backup-actions">
              <el-button type="primary" @click="backupData" :loading="backupLoading" :disabled="backupLoading" class="backup-button">
                <el-icon v-if="backupLoading"><Loading /></el-icon>
                {{ backupLoading ? '备份中...' : '立即备份' }}
              </el-button>
              <el-button type="default" @click="restoreData" class="restore-button" :disabled="!canRestore">
                <el-icon><Refresh /></el-icon>
                恢复数据
              </el-button>
            </div>
            
            <!-- 备份进度条 -->
            <div v-if="backupProgress > 0 && backupProgress < 100" class="backup-progress-container">
              <div class="progress-label">备份进度：{{ backupProgress }}%</div>
              <el-progress :percentage="backupProgress" :status="backupProgress === 100 ? 'success' : 'normal'" />
              <div class="progress-detail">{{ backupStatusText }}</div>
            </div>
            
            <div class="backup-info-card">
              <div class="info-item">
                <span class="info-label">上次备份时间：</span>
                <span class="info-value">{{ lastBackupTime || '暂无备份记录' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">备份存储位置：</span>
                <span class="info-value">本地服务器</span>
              </div>
              <div class="info-item">
                <span class="info-label">备份文件数：</span>
                <span class="info-value">{{ backupHistory.length }}</span>
              </div>
            </div>
            
            <p class="backup-tips">定期备份数据可以防止数据丢失，请确保备份存储在安全位置。建议每周至少备份一次。</p>
            
            <!-- 备份策略设置 -->
            <div class="backup-strategy">
              <h4>自动备份策略</h4>
              <el-form :model="backupStrategyForm" inline class="strategy-form">
                <el-form-item label="备份频率：">
                  <el-select v-model="backupStrategyForm.frequency" placeholder="选择备份频率">
                    <el-option label="每天" value="daily" />
                    <el-option label="每周" value="weekly" />
                    <el-option label="每月" value="monthly" />
                    <el-option label="关闭自动备份" value="none" />
                  </el-select>
                </el-form-item>
                <el-form-item label="备份时间：" v-if="backupStrategyForm.frequency !== 'none'">
                  <el-time-select v-model="backupStrategyForm.time" placeholder="选择时间" :picker-options="{ start: '00:00', step: '01:00', end: '23:00' }" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveBackupStrategy">保存策略</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <!-- 备份历史 -->
            <div class="backup-history" v-if="backupHistory.length > 0">
              <div class="history-header">
                <h4>备份历史</h4>
                <el-button type="text" @click="clearAllBackups" :disabled="backupHistory.length === 0 || backupLoading" class="clear-button">
                  <el-icon><Delete /></el-icon>
                  清空历史
                </el-button>
              </div>
              <el-table :data="backupHistory" style="width: 100%" v-loading="loadingBackupHistory" border class="backup-table">
                <el-table-column prop="filename" label="备份文件名" show-overflow-tooltip />
                <el-table-column prop="time" label="备份时间" width="180" />
                <el-table-column prop="size" label="文件大小" width="100" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.status === 'success' ? 'success' : 'warning'">{{ scope.row.status === 'success' ? '成功' : '失败' }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                  <template #default="scope">
                    <el-button type="text" @click="downloadBackup(scope.row.filename)" :disabled="backupLoading">下载</el-button>
                    <el-button type="text" danger @click="deleteBackup(scope.row.filename)" :disabled="backupLoading">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            
            <!-- 空状态提示 -->
            <div v-else class="empty-backup">
              <el-empty description="暂无备份记录" />
              <el-button type="primary" @click="backupData" style="margin-top: 20px">立即创建备份</el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 系统信息 -->
    <el-card class="system-info-card" shadow="hover" style="margin-top: 24px">
      <template #header>
        <div class="card-header">
          <span class="card-title">系统信息</span>
          <el-button type="text" @click="refreshSystemInfo">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <div class="system-info-content">
        <div class="info-grid">
          <div class="info-card">
            <div class="info-icon">
              <el-icon class="icon-lg"><Monitor /></el-icon>
            </div>
            <div class="info-text">
              <div class="info-value">{{ systemInfo.os }}</div>
              <div class="info-label">操作系统</div>
            </div>
          </div>
          <div class="info-card">
            <div class="info-icon">
              <el-icon class="icon-lg"><Server /></el-icon>
            </div>
            <div class="info-text">
              <div class="info-value">{{ systemInfo.serverIp }}</div>
              <div class="info-label">服务器IP</div>
            </div>
          </div>
          <div class="info-card">
            <div class="info-icon">
              <el-icon class="icon-lg"><Database /></el-icon>
            </div>
            <div class="info-text">
              <div class="info-value">{{ systemInfo.dbType }}</div>
              <div class="info-label">数据库</div>
            </div>
          </div>
          <div class="info-card">
            <div class="info-icon">
              <el-icon class="icon-lg"><Clock /></el-icon>
            </div>
            <div class="info-text">
              <div class="info-value">{{ systemInfo.uptime }}</div>
              <div class="info-label">运行时间</div>
            </div>
          </div>
          <div class="info-card">
            <div class="info-icon">
              <el-icon class="icon-lg"><User /></el-icon>
            </div>
            <div class="info-text">
              <div class="info-value">{{ systemInfo.onlineUsers }}</div>
              <div class="info-label">当前在线</div>
            </div>
          </div>
          <div class="info-card">
            <div class="info-icon">
              <el-icon class="icon-lg"><Cpu /></el-icon>
            </div>
            <div class="info-text">
              <div class="info-value">{{ systemInfo.cpuUsage }}%</div>
              <div class="info-label">CPU使用率</div>
            </div>
          </div>
        </div>
        
        <!-- 详细系统信息表格 -->
        <div class="detailed-info">
          <h4>详细信息</h4>
          <el-table :data="detailedSystemInfo" style="width: 100%" class="info-table">
            <el-table-column prop="name" label="项目" width="180" />
            <el-table-column prop="value" label="信息" show-overflow-tooltip />
          </el-table>
        </div>
      </div>
    </el-card>

    <!-- 页脚信息 -->
    <div class="system-footer">
      <p>© 2023 违约管理系统 - 技术部版权所有</p>
      <p>版本号: {{ systemVersion }} | 最后更新: 2023-07-01</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Upload, Loading, Delete, Refresh, Monitor, Server, Database, Clock, User, Cpu } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'

const auth = useAuthStore()

// 状态数据
const activeTab = ref('password')
const lastLoginTime = ref('2023-07-12 14:30:22')
const lastLoginIp = ref('192.168.1.10')
const systemVersion = ref('v1.0.0')
const avatarUrl = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')

// 密码表单状态
const passwordFormRef = ref()
const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordUpdating = ref(false)
const passwordStrength = ref('weak') // weak, medium, strong
const passwordStrengthWidth = computed(() => {
  switch (passwordStrength.value) {
    case 'weak': return '33%'
    case 'medium': return '66%'
    case 'strong': return '100%'
    default: return '0%'
  }
})
const passwordStrengthText = computed(() => {
  switch (passwordStrength.value) {
    case 'weak': return '弱'
    case 'medium': return '中'
    case 'strong': return '强'
    default: return ''
  }
})
const passwordTips = ref('')

// 密码验证规则
const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度应在8-20位之间', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
          callback(new Error('密码必须包含大小写字母和数字'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 通知设置表单状态
const notificationFormRef = ref()
const notificationForm = ref({
  breachNotification: true,
  reviewNotification: true,
  systemNotification: true,
  email: 'admin@example.com'
})
const notificationSaving = ref(false)

// 备份相关状态
const backupHistory = ref([
  {
    filename: 'backup_20230710.sql',
    time: '2023-07-10 23:00:00',
    size: '12.5MB',
    status: 'success'
  },
  {
    filename: 'backup_20230705.sql',
    time: '2023-07-05 23:00:00',
    size: '11.8MB',
    status: 'success'
  }
])
const backupLoading = ref(false)
const loadingBackupHistory = ref(false)
const backupProgress = ref(0)
const backupStatusText = ref('')
const lastBackupTime = computed(() => backupHistory.value.length > 0 ? backupHistory.value[0].time : '')
const canRestore = computed(() => backupHistory.value.length > 0)

// 备份策略表单
const backupStrategyForm = ref({
  frequency: 'weekly',
  time: '23:00'
})

// 系统信息数据
const systemInfo = ref({
  os: 'Windows Server 2019',
  serverIp: '192.168.1.100',
  dbType: 'MySQL 8.0',
  uptime: '15天 8小时 32分钟',
  onlineUsers: '3人',
  cpuUsage: '12'
})

const detailedSystemInfo = ref([
  { name: '系统名称', value: '违约管理系统' },
  { name: '版本号', value: 'v1.0.0' },
  { name: '开发团队', value: '技术部' },
  { name: '服务器IP', value: '192.168.1.100' },
  { name: '数据库类型', value: 'MySQL 8.0' },
  { name: '运行时间', value: '15天 8小时 32分钟' },
  { name: '当前在线', value: '3人' },
  { name: 'CPU型号', value: 'Intel(R) Xeon(R) CPU E5-2690 v4 @ 2.60GHz' },
  { name: '内存容量', value: '32GB' },
  { name: '磁盘空间', value: '480GB / 1TB' },
  { name: '前端框架', value: 'Vue 3 + TypeScript' },
  { name: '后端框架', value: 'Spring Boot' }
])

// 头像上传相关
const avatarInput = ref<HTMLInputElement>()

// 方法

// 密码强度检测
const checkPasswordStrength = (value: string) => {
  let strength = 0
  let tips = []
  
  if (value.length >= 8) {
    strength++
    tips.push('✓ 长度符合要求')
  } else {
    tips.push('✗ 密码长度至少为8位')
  }
  
  if (/[a-z]/.test(value) && /[A-Z]/.test(value)) {
    strength++
    tips.push('✓ 包含大小写字母')
  } else {
    tips.push('✗ 建议包含大小写字母')
  }
  
  if (/\d/.test(value)) {
    strength++
    tips.push('✓ 包含数字')
  } else {
    tips.push('✗ 建议包含数字')
  }
  
  if (/[^a-zA-Z0-9]/.test(value)) {
    strength++
    tips.push('✓ 包含特殊字符')
  } else {
    tips.push('✗ 建议包含特殊字符')
  }
  
  if (strength <= 2) {
    passwordStrength.value = 'weak'
  } else if (strength <= 3) {
    passwordStrength.value = 'medium'
  } else {
    passwordStrength.value = 'strong'
  }
  
  passwordTips.value = tips.join(' | ')
}

// 上传头像
const uploadAvatar = () => {
  avatarInput.value?.click()
}

// 处理头像上传
const handleAvatarUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (file) {
    // 检查文件大小和类型
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.error('文件大小不能超过5MB')
      return
    }
    
    if (!file.type.startsWith('image/')) {
      ElMessage.error('请上传图片文件')
      return
    }
    
    // 在实际项目中，这里应该上传文件到服务器
    // 这里仅做前端预览
    const reader = new FileReader()
    reader.onload = (e) => {
      avatarUrl.value = e.target?.result as string
      ElMessage.success('头像上传成功')
    }
    reader.readAsDataURL(file)
  }
  
  // 清空input值，以便可以重复上传同一文件
  if (target) {
    target.value = ''
  }
}

// 更新密码
const updatePassword = async () => {
  try {
    if (!passwordFormRef.value) return
    await passwordFormRef.value.validate()
    
    passwordUpdating.value = true
    
    // 模拟API请求延迟
    setTimeout(() => {
      // 在实际项目中，这里应该调用API更新密码
      ElMessage.success('密码修改成功')
      resetPasswordForm()
      passwordUpdating.value = false
    }, 1500)
  } catch (error) {
    passwordUpdating.value = false
    ElMessage.error('密码修改失败')
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  passwordStrength.value = 'weak'
  passwordTips.value = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

// 保存通知设置
const saveNotificationSettings = () => {
  notificationSaving.value = true
  
  // 模拟API请求延迟
  setTimeout(() => {
    // 在实际项目中，这里应该调用API保存设置
    ElMessage.success('通知设置保存成功')
    notificationSaving.value = false
  }, 1000)
}

// 备份数据
const backupData = () => {
  backupLoading.value = true
  backupProgress.value = 0
  backupStatusText.value = '正在准备备份...'
  
  // 模拟备份过程
  const interval = setInterval(() => {
    backupProgress.value += Math.floor(Math.random() * 10) + 5
    
    if (backupProgress.value < 30) {
      backupStatusText.value = '正在收集数据...'
    } else if (backupProgress.value < 70) {
      backupStatusText.value = '正在写入备份文件...'
    } else if (backupProgress.value < 90) {
      backupStatusText.value = '正在验证备份完整性...'
    } else {
      backupStatusText.value = '备份完成，正在清理临时文件...'
    }
    
    if (backupProgress.value >= 100) {
      clearInterval(interval)
      backupProgress.value = 100
      
      setTimeout(() => {
        ElMessage.success('数据备份成功')
        
        // 添加新的备份记录
        const now = new Date()
        const timestamp = now.toISOString().replace(/[-:]/g, '').replace(/\.\d+Z/, '')
        backupHistory.value.unshift({
          filename: `backup_${timestamp}.sql`,
          time: now.toLocaleString('zh-CN'),
          size: `${(Math.random() * 5 + 10).toFixed(1)}MB`,
          status: 'success'
        })
        
        // 重置状态
        backupLoading.value = false
        backupProgress.value = 0
        backupStatusText.value = ''
      }, 1000)
    }
  }, 300)
}

// 恢复数据
const restoreData = () => {
  if (backupHistory.value.length === 0) {
    ElMessage.warning('暂无备份记录')
    return
  }
  
  ElMessageBox.confirm(
    '恢复数据将覆盖当前所有数据，是否继续？',
    '恢复确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const loading = ElLoading.service({
      lock: true,
      text: '正在恢复数据，请稍候...',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 模拟恢复过程
    setTimeout(() => {
      loading.close()
      ElMessage.success('数据恢复成功')
    }, 3000)
  }).catch(() => {
    // 用户取消操作
  })
}

// 下载备份
const downloadBackup = (filename: string) => {
  // 在实际项目中，这里应该调用API下载备份文件
  ElMessage.info(`正在下载备份文件: ${filename}`)
  
  // 模拟下载进度
  const loading = ElMessage.loading('下载中...', { duration: 2000 })
  setTimeout(() => {
    loading.close()
    ElMessage.success(`备份文件 ${filename} 下载完成`)
  }, 2000)
}

// 删除备份
const deleteBackup = (filename: string) => {
  ElMessageBox.confirm(
    `确定要删除备份文件 ${filename} 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    backupHistory.value = backupHistory.value.filter(item => item.filename !== filename)
    ElMessage.success('备份文件已删除')
  }).catch(() => {
    // 用户取消操作
  })
}

// 清空所有备份
const clearAllBackups = () => {
  ElMessageBox.confirm(
    '确定要删除所有备份文件吗？此操作不可恢复！',
    '危险操作',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(() => {
    backupHistory.value = []
    ElMessage.success('所有备份文件已删除')
  }).catch(() => {
    // 用户取消操作
  })
}

// 保存备份策略
const saveBackupStrategy = () => {
  ElMessage.success('自动备份策略已保存')
}

// 刷新系统信息
const refreshSystemInfo = () => {
  const loading = ElMessage.loading('正在刷新系统信息...', { duration: 1000 })
  
  // 模拟刷新过程
  setTimeout(() => {
    loading.close()
    ElMessage.success('系统信息已刷新')
  }, 1000)
}

// 生命周期钩子
onMounted(() => {
  // 在实际项目中，这里应该调用API获取用户信息和系统设置
  console.log('系统设置页面加载完成')
  
  // 模拟加载备份历史
  loadingBackupHistory.value = true
  setTimeout(() => {
    loadingBackupHistory.value = false
  }, 800)
})
</script>

<style scoped>
.system-container {
  padding: 24px;
  min-height: 100vh;
  background-color: #f5f7fa;
  transition: all 0.3s ease;
}

/* 页面标题 */
.page-header {
  margin-bottom: 28px;
  padding: 16px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.subtitle {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

/* 卡片样式 */
.user-info-card,
.settings-card,
.system-info-card {
  transition: all 0.3s ease;
  border-radius: 12px !important;
  background: #ffffff;
  border: 1px solid #e4e7ed;
}

.user-info-card:hover,
.settings-card:hover,
.system-info-card:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.admin-badge {
  margin-left: 12px;
}

/* 用户信息卡片 */
.user-info-content {
  display: flex;
  align-items: flex-start;
  padding: 24px;
}

.avatar-section {
  text-align: center;
  margin-right: 40px;
}

.avatar-container {
  position: relative;
  display: inline-block;
}

.user-avatar {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 3px solid #f0f2f5;
}

.user-avatar:hover {
  transform: scale(1.05);
  border-color: #667eea;
}

.avatar-upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
  cursor: pointer;
}

.avatar-container:hover .avatar-upload-overlay {
  opacity: 1;
}

.upload-icon {
  margin-bottom: 4px;
}

.avatar-input {
  display: none;
}

.user-details {
  flex: 1;
}

.detail-item {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.detail-label {
  color: #606266;
  margin-right: 16px;
  min-width: 80px;
  font-weight: 500;
}

.detail-value {
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

.role-tag,
.version-tag {
  margin-left: 0;
}

/* 设置选项卡 */
.settings-tabs {
  padding: 0;
}

:deep(.el-tabs__header) {
  padding: 0;
  margin: 0;
  border-bottom: 1px solid #f0f2f5;
}

:deep(.el-tabs__nav) {
  padding: 0 20px;
}

:deep(.el-tabs__content) {
  padding: 20px;
}

.setting-form {
  max-width: 600px;
}

.form-input {
  transition: all 0.3s ease;
}

.form-input:focus-within {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.form-button {
  margin-right: 12px;
  transition: all 0.3s ease;
}

.form-button:hover {
  transform: translateY(-1px);
}

.form-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 6px;
}

/* 密码强度指示器 */
.password-strength {
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.strength-labels {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.strength-text {
  font-size: 12px;
  font-weight: 500;
}

.strength-weak {
  color: #f56c6c;
}

.strength-medium {
  color: #e6a23c;
}

.strength-strong {
  color: #67c23a;
}

.strength-bar {
  height: 6px;
  background-color: #e4e7ed;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 6px;
}

.strength-indicator {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 3px;
}

.strength-indicator.strength-weak {
  background-color: #f56c6c;
}

.strength-indicator.strength-medium {
  background-color: #e6a23c;
}

.strength-indicator.strength-strong {
  background-color: #67c23a;
}

.strength-tips {
  font-size: 11px;
  color: #909399;
  line-height: 1.4;
}

/* 开关样式 */
.custom-switch {
  margin-right: 12px;
}

.switch-desc {
  color: #606266;
  font-size: 14px;
}

/* 备份相关样式 */
.backup-section {
  padding: 8px 0;
}

.backup-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.backup-button,
.restore-button {
  transition: all 0.3s ease;
}

.backup-button:hover:not(:disabled),
.restore-button:hover:not(:disabled) {
  transform: translateY(-1px);
}

.backup-progress-container {
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.progress-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.progress-detail {
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
}

.backup-info-card {
  background-color: #f0f2f5;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  color: #606266;
  margin-right: 12px;
  min-width: 120px;
  font-size: 13px;
}

.info-value {
  color: #303133;
  font-size: 13px;
  font-weight: 500;
}

.backup-tips {
  margin: 16px 0 24px;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.backup-strategy {
  background-color: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
}

.backup-strategy h4 {
  margin-bottom: 16px;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.strategy-form {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.history-header h4 {
  margin: 0;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.clear-button {
  display: flex;
  align-items: center;
  gap: 4px;
}

.backup-table {
  border-radius: 8px !important;
  overflow: hidden;
}

.empty-backup {
  text-align: center;
  padding: 40px 20px;
}

/* 系统信息样式 */
.system-info-content {
  padding: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.info-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.info-card:hover {
  background-color: #e9ecef;
  transform: translateY(-2px);
}

.info-icon {
  margin-right: 16px;
  color: #667eea;
  background-color: rgba(102, 126, 234, 0.1);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-lg {
  width: 20px;
  height: 20px;
}

.info-text {
  flex: 1;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.info-label {
  font-size: 12px;
  color: #606266;
}

.detailed-info {
  margin-top: 32px;
}

.detailed-info h4 {
  margin-bottom: 16px;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.info-table {
  border-radius: 8px !important;
  overflow: hidden;
}

/* 页脚信息 */
.system-footer {
  margin-top: 40px;
  text-align: center;
  color: #909399;
  font-size: 12px;
  padding: 20px 0;
  border-top: 1px solid #f0f2f5;
}

.system-footer p {
  margin: 4px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .info-grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }
}

@media (max-width: 768px) {
  .system-container {
    padding: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .user-info-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .avatar-section {
    margin-right: 0;
    margin-bottom: 24px;
  }
  
  .detail-item {
    justify-content: center;
  }
  
  .detail-label {
    min-width: auto;
  }
  
  .setting-form {
    max-width: 100%;
  }
  
  .backup-actions {
    flex-direction: column;
  }
  
  .strategy-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
  
  :deep(.el-table__column--fixed-right) {
    right: 0 !important;
    width: 100px !important;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .system-container {
    background-color: #1a1a1a;
  }
  
  .page-title {
    color: #ffffff;
  }
  
  .subtitle {
    color: #cccccc;
  }
  
  .user-info-card,
  .settings-card,
  .system-info-card {
    background: #2d2d2d;
    border: 1px solid #444444;
  }
  
  .card-title {
    color: #ffffff;
  }
  
  .detail-label {
    color: #cccccc;
  }
  
  .detail-value {
    color: #ffffff;
  }
  
  .switch-desc {
    color: #cccccc;
  }
  
  .backup-tips {
    color: #cccccc;
  }
  
  .progress-label {
    color: #ffffff;
  }
  
  .progress-detail {
    color: #cccccc;
  }
  
  .backup-info-card {
    background-color: #333333;
  }
  
  .info-label {
    color: #cccccc;
  }
  
  .info-value {
    color: #ffffff;
  }
  
  .backup-strategy {
    background-color: #333333;
  }
  
  .history-header h4 {
    color: #ffffff;
  }
  
  .detailed-info h4 {
    color: #ffffff;
  }
  
  .info-card {
    background-color: #333333;
  }
  
  .info-card:hover {
    background-color: #444444;
  }
  
  .system-footer {
    color: #999999;
    border-top: 1px solid #444444;
  }
  
  .strength-tips {
    color: #999999;
  }
}
</style>
