<template>
  <div class="user-list-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>用户管理</h1>
      <el-button type="primary" @click="handleAddUser">添加用户</el-button>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-filter-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" clearable>
            <el-option label="管理员" value="ADMIN" />
            <el-option label="操作员" value="RISK_CONTROL" />
            <el-option label="审核员" value="RISK_REVIEWER" />
            <el-option label="只读用户" value="VIEWER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 用户列表表格 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="usersData"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="firstName" label="名字" />
        <el-table-column prop="lastName" label="姓氏" />
        <el-table-column prop="role" label="角色">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.role)">
              {{ getRoleLabel(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEditUser(scope.row)">
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDeleteUser(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页控件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalElements"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!userForm.id">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!userForm.id">
          <el-input v-model="userForm.confirmPassword" type="password" placeholder="请确认密码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="名字" prop="firstName">
          <el-input v-model="userForm.firstName" placeholder="请输入名字" />
        </el-form-item>
        <el-form-item label="姓氏" prop="lastName">
          <el-input v-model="userForm.lastName" placeholder="请输入姓氏" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="操作员" value="RISK_CONTROL" />
            <el-option label="审核员" value="RISK_REVIEWER" />
            <el-option label="只读用户" value="VIEWER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import dayjs from 'dayjs'
import axios from 'axios'
import request from '@/utils/request'
import type { User } from '@/types'

// 扩展User类型，添加密码等字段
interface UserForm extends User {
  password?: string
  confirmPassword?: string
}

// 搜索表单
const searchForm = reactive({
  username: '',
  email: '',
  role: ''
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 10
})

// 表格数据
const users = ref<User[]>([])
const loading = ref(false)
const totalElements = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const userFormRef = ref<FormInstance>()
const userForm = reactive<UserForm>({
  id: 0,
  username: '',
  email: '',
  firstName: '',
  lastName: '',
  role: '',
  createTime: ''
})

// 表单验证规则
const userFormRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== userForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      type: 'email',
      message: '请输入有效的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  firstName: [
    { required: true, message: '请输入名字', trigger: 'blur' }
  ],
  lastName: [
    { required: true, message: '请输入姓氏', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
})

// 计算表格显示数据
const usersData = computed(() => {
  return users.value
})

// 格式化日期
const formatDate = (dateString: string) => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
}

// 获取角色标签类型
const getRoleTagType = (role: string) => {
  const roleTypes: Record<string, string> = {
    ADMIN: 'danger',
    RISK_CONTROL: 'primary',
    RISK_REVIEWER: 'warning',
    VIEWER: 'success'
  }
  return roleTypes[role] || 'default'
}

// 获取角色显示标签
const getRoleLabel = (role: string) => {
  const roleLabels: Record<string, string> = {
    ADMIN: '管理员',
    RISK_CONTROL: '操作员',
    RISK_REVIEWER: '审核员',
    VIEWER: '只读用户'
  }
  return roleLabels[role] || role
}

// 获取用户列表
const getUsers = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.page - 1,
      size: pagination.size
    }

    const response = await request.get('/users', { params })
    // users.value = response.data.content || generateMockUsers()
    users.value = response
    // totalElements.value = response.data.totalElements || 50
  } catch (error) {
    console.error('获取用户列表失败:', error)
    // 如果API调用失败，使用模拟数据
    users.value = generateMockUsers()
    totalElements.value = 50
    ElMessage.error('获取用户列表失败，使用模拟数据')
  } finally {
    loading.value = false
  }
}

// 生成模拟用户数据
const generateMockUsers = (): User[] => {
  const roles = ['ADMIN', 'RISK_CONTROL', 'RISK_REVIEWER', 'VIEWER']
  const mockUsers: User[] = []
  
  for (let i = 1; i <= 50; i++) {
    const role = roles[Math.floor(Math.random() * roles.length)]
    mockUsers.push({
      id: i,
      username: `user${i}`,
      email: `user${i}@example.com`,
      firstName: `First${i}`,
      lastName: `Last${i}`,
      role: role,
      createTime: dayjs().subtract(Math.floor(Math.random() * 30), 'day').format('YYYY-MM-DDTHH:mm:ss')
    })
  }
  
  // 应用搜索和分页
  let filtered = [...mockUsers]
  
  if (searchForm.username) {
    filtered = filtered.filter(user => user.username.includes(searchForm.username))
  }
  
  if (searchForm.email) {
    filtered = filtered.filter(user => user.email.includes(searchForm.email))
  }
  
  if (searchForm.role) {
    filtered = filtered.filter(user => user.role === searchForm.role)
  }
  
  totalElements.value = filtered.length
  
  // 分页
  const start = (pagination.page - 1) * pagination.size
  const end = start + pagination.size
  return filtered.slice(start, end)
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getUsers()
}

// 重置搜索条件
const handleReset = () => {
  searchForm.username = ''
  searchForm.email = ''
  searchForm.role = ''
  pagination.page = 1
  getUsers()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  getUsers()
}

// 分页页码变化
const handleCurrentChange = (current: number) => {
  pagination.page = current
  getUsers()
}

// 打开添加用户对话框
const handleAddUser = () => {
  dialogTitle.value = '添加用户'
  Object.assign(userForm, {
    id: 0,
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    role: '',
    createTime: '',
    password: '',
    confirmPassword: ''
  })
  dialogVisible.value = true
}

// 打开编辑用户对话框
const handleEditUser = (user: User) => {
  dialogTitle.value = '编辑用户'
  Object.assign(userForm, {
    ...user,
    password: '',
    confirmPassword: ''
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    const userData = {
      ...userForm
    }
    // 删除不需要的字段
    delete (userData as any).confirmPassword
    delete (userData as any).createTime
    
    if (userForm.id) {
      // 编辑用户
      await request.put(`/users/${userForm.id}`, userData)
      ElMessage.success('用户更新成功')
    } else {
      // 添加用户
      await request.post('/users', userData)
      ElMessage.success('用户添加成功')
    }
    
    dialogVisible.value = false
    getUsers()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(userForm.id ? '用户更新失败' : '用户添加失败')
  }
}

// 删除用户
const handleDeleteUser = async (userId: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该用户吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await request.delete(`/users/${userId}`)
    ElMessage.success('用户删除成功')
    getUsers()
  } catch (error) {
    console.error('删除失败:', error)
    // 如果用户取消删除，不显示错误信息
    if (error !== 'cancel') {
      ElMessage.error('用户删除失败')
    }
  }
}

// 初始化加载数据
onMounted(() => {
  getUsers()
})
</script>

<style scoped>
.user-list-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
}

.search-filter-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search-form {
  display: flex;
  align-items: center;
  gap: 16px;
}

.table-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.pagination-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  text-align: right;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-form {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .user-list-container {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>