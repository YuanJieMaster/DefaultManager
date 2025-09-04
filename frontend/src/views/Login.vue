<template>
  <div class="login-container">
    <!-- 背景动画效果 -->
    <div class="bg-particles">
      <div class="particle" v-for="n in 20" :key="n"></div>
    </div>
    
    <!-- 登录卡片 -->
    <div class="login-wrapper">
      <el-card class="login-card" shadow="hover">
        <!-- 系统Logo和标题 -->
        <div class="login-header">
          <div class="logo-container">
            <div class="logo-icon">
              <ShieldCheckBold class="logo-svg" />
            </div>
          </div>
          <h1 class="system-title">违约管理系统</h1>
          <p class="system-subtitle">违约风险管理与决策支持平台</p>
        </div>
        
        <!-- 登录表单 -->
        <el-form 
          :model="form" 
          :rules="rules" 
          ref="formRef" 
          label-width="0" 
          class="login-form"
          @keyup.enter.prevent="onSubmit"
        >
          <!-- 用户名输入 -->
          <el-form-item prop="username" class="form-item">
            <el-input 
              v-model="form.username" 
              placeholder="请输入用户名"
              :prefix-icon="User"
              :validate-event="false"
              :class="{ 'input-focus': usernameFocus }"
              @focus="usernameFocus = true"
              @blur="usernameFocus = false"
              autocomplete="username"
            />
          </el-form-item>
          
          <!-- 密码输入 -->
          <el-form-item prop="password" class="form-item">
            <el-input 
              v-model="form.password" 
              placeholder="请输入密码"
              show-password
              :prefix-icon="Lock"
              :validate-event="false"
              :class="{ 'input-focus': passwordFocus }"
              @focus="passwordFocus = true"
              @blur="passwordFocus = false"
              autocomplete="current-password"
            />
          </el-form-item>
          
          <!-- 额外选项 -->
          <div class="login-extra">
            <el-checkbox v-model="form.rememberMe" class="remember-checkbox">记住我</el-checkbox>
            <el-link type="primary" @click="handleForgotPassword" class="forgot-password">忘记密码？</el-link>
          </div>
          
          <!-- 登录按钮 -->
          <el-button 
            type="primary" 
            class="login-btn"
            :loading="loading"
            @click="onSubmit"
            :class="{ 'btn-loading': loading }"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form>
        
        <!-- 底部版权信息 -->
        <div class="login-footer">
          <p class="copyright">© 2023 违约管理系统. All rights reserved.</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, ShieldCheckBold } from '@element-plus/icons-vue'
import { loginJson, loginForm, type LoginPayload } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const auth = useAuthStore()

// 状态管理
const loading = ref(false)
const formRef = ref()
const usernameFocus = ref(false)
const passwordFocus = ref(false)
const form = ref<LoginPayload>({
  username: '', 
  password: '', 
  rememberMe: true 
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 登录处理函数
const onSubmit = async () => {
  try {
    // 表单验证
    await formRef.value?.validate()
    
    loading.value = true
    
    // 优先尝试 JSON 登录（若后端未提供可自动回退到表单登录）
    let result
    try {
      result = await loginJson(form.value)
    } catch (_) {
      result = await loginForm(form.value)
    }
    
    // 存储登录信息
    auth.setLogin(result.token || null, result.username || form.value.username)
    
    // 显示成功消息并跳转
    ElMessage.success('登录成功，正在跳转...')
    
    // 延迟跳转，让用户看到成功提示
    setTimeout(() => {
      router.push('/dashboard')
    }, 1000)
    
  } catch (e: any) {
    // 捕获验证错误
    if (typeof e === 'string') {
      ElMessage.error(e)
    } else {
      ElMessage.error('登录失败，请检查用户名或密码')
      console.error('Login error:', e)
    }
  } finally {
    loading.value = false
  }
}

// 忘记密码处理
const handleForgotPassword = () => {
  ElMessage.info('忘记密码功能暂未实现')
  // 在实际项目中，这里应该跳转到找回密码页面
  // router.push('/forgot-password')
}

// 自动填充上次记住的用户名
const initForm = () => {
  try {
    const savedUsername = localStorage.getItem('savedUsername')
    if (savedUsername) {
      form.value.username = savedUsername
      form.value.rememberMe = true
    }
  } catch (error) {
    console.error('Failed to load saved username:', error)
  }
}

// 组件挂载时初始化
initForm()
</script>

<style scoped>
/* 登录容器 */
.login-container {
  position: relative;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 背景粒子动画 */
.bg-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 15s infinite ease-in-out;
}

/* 粒子动画 */
@keyframes float {
  0% { transform: translate(0, 0) rotate(0deg); }
  25% { transform: translate(10%, 10%) rotate(90deg); }
  50% { transform: translate(0, 20%) rotate(180deg); }
  75% { transform: translate(-10%, 10%) rotate(270deg); }
  100% { transform: translate(0, 0) rotate(360deg); }
}

/* 随机生成粒子大小和位置 */
.particle:nth-child(1) { width: 100px; height: 100px; top: 10%; left: 20%; animation-delay: 0s; }
.particle:nth-child(2) { width: 150px; height: 150px; top: 30%; left: 70%; animation-delay: 2s; }
.particle:nth-child(3) { width: 80px; height: 80px; top: 60%; left: 10%; animation-delay: 4s; }
.particle:nth-child(4) { width: 120px; height: 120px; top: 80%; left: 50%; animation-delay: 6s; }
.particle:nth-child(5) { width: 60px; height: 60px; top: 20%; left: 80%; animation-delay: 8s; }
.particle:nth-child(6) { width: 90px; height: 90px; top: 50%; left: 30%; animation-delay: 10s; }
.particle:nth-child(7) { width: 70px; height: 70px; top: 70%; left: 90%; animation-delay: 12s; }
.particle:nth-child(8) { width: 110px; height: 110px; top: 15%; left: 40%; animation-delay: 14s; }
.particle:nth-child(9) { width: 130px; height: 130px; top: 40%; left: 60%; animation-delay: 1s; }
.particle:nth-child(10) { width: 85px; height: 85px; top: 65%; left: 25%; animation-delay: 3s; }
.particle:nth-child(11) { width: 140px; height: 140px; top: 85%; left: 5%; animation-delay: 5s; }
.particle:nth-child(12) { width: 95px; height: 95px; top: 25%; left: 75%; animation-delay: 7s; }
.particle:nth-child(13) { width: 75px; height: 75px; top: 55%; left: 15%; animation-delay: 9s; }
.particle:nth-child(14) { width: 105px; height: 105px; top: 90%; left: 45%; animation-delay: 11s; }
.particle:nth-child(15) { width: 125px; height: 125px; top: 5%; left: 55%; animation-delay: 13s; }
.particle:nth-child(16) { width: 80px; height: 80px; top: 35%; left: 15%; animation-delay: 0.5s; }
.particle:nth-child(17) { width: 110px; height: 110px; top: 75%; left: 65%; animation-delay: 2.5s; }
.particle:nth-child(18) { width: 90px; height: 90px; top: 45%; left: 85%; animation-delay: 4.5s; }
.particle:nth-child(19) { width: 130px; height: 130px; top: 10%; left: 30%; animation-delay: 6.5s; }
.particle:nth-child(20) { width: 70px; height: 70px; top: 50%; left: 50%; animation-delay: 8.5s; }

/* 登录包装器 */
.login-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  position: relative;
  z-index: 1;
}

/* 登录卡片 */
.login-card {
  width: 380px;
  padding: 30px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-card:hover {
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.2);
  transform: translateY(-5px);
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 30px;
}

/* Logo容器 */
.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.logo-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.logo-svg {
  width: 40px;
  height: 40px;
  color: white;
}

/* 系统标题 */
.system-title {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.system-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 登录表单 */
.login-form {
  width: 100%;
}

/* 表单项 */
.form-item {
  margin-bottom: 20px;
}

/* 输入框样式 */
:deep(.el-input__wrapper) {
  border-radius: 8px !important;
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
  background-color: rgba(255, 255, 255, 0.8);
}

:deep(.el-input__wrapper:hover) {
  border-color: #c0c4cc !important;
}

:deep(.el-input__wrapper.input-focus) {
  border-color: #667eea !important;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) !important;
}

:deep(.el-input__inner) {
  font-size: 14px;
  border: none !important;
  background-color: transparent !important;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  letter-spacing: 1px;
}

.login-btn:hover {
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.login-btn.btn-loading {
  transform: scale(0.98);
}

/* 登录额外选项 */
.login-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  font-size: 13px;
}

/* 记住我复选框 */
:deep(.remember-checkbox .el-checkbox__label) {
  color: #606266;
}

:deep(.remember-checkbox .el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #667eea;
  border-color: #667eea;
}

/* 忘记密码链接 */
.forgot-password {
  color: #667eea !important;
}

.forgot-password:hover {
  color: #5a67d8 !important;
}

/* 登录底部 */
.login-footer {
  margin-top: 24px;
  text-align: center;
}

.copyright {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    width: 90%;
    padding: 24px;
    margin: 0 10px;
  }
  
  .system-title {
    font-size: 20px;
  }
  
  .login-extra {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .forgot-password {
    align-self: flex-end;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .login-card {
    background: rgba(30, 30, 30, 0.95);
    border: 1px solid rgba(60, 60, 60, 0.5);
  }
  
  .system-title {
    color: #ffffff;
  }
  
  .system-subtitle {
    color: #cccccc;
  }
  
  :deep(.el-input__wrapper) {
    background-color: rgba(40, 40, 40, 0.8);
    border: 1px solid #444444;
  }
  
  :deep(.el-input__inner) {
    color: #ffffff;
  }
  
  :deep(.remember-checkbox .el-checkbox__label) {
    color: #cccccc;
  }
  
  .copyright {
    color: #999999;
  }
}
</style>
