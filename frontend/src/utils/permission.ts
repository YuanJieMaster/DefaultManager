import { useAuthStore } from '@/store/auth'

// 用户角色定义
export enum UserRole {
  ADMIN = 'admin',           // 管理员 - 拥有所有权限
  RISK_REVIEWER = 'risk_reviewer', // 风控审核员 - 审核相关权限
  RISK_CONTROL = 'risk_control',   // 风控专员 - 申请相关权限
  VIEWER = 'viewer'          // 只读用户 - 仅查看权限
}

// 权限定义
export enum Permission {
  // 客户管理
  VIEW_CUSTOMER_LIST = 'view:customer:list',
  VIEW_BREACHED_CUSTOMERS = 'view:customer:breached',
  
  // 违约管理
  VIEW_BREACH_RECORDS = 'view:breach:records',
  APPLY_BREACH = 'apply:breach',
  REVIEW_BREACH = 'review:breach',
  MANAGE_BREACH_REASONS = 'manage:breach:reasons',
  
  // 重生管理
  VIEW_REBIRTH_RECORDS = 'view:rebirth:records',
  APPLY_REBIRTH = 'apply:rebirth',
  REVIEW_REBIRTH = 'review:rebirth',
  MANAGE_REBIRTH_REASONS = 'manage:rebirth:reasons',
  
  // 统计分析
  VIEW_STATISTICS_OVERVIEW = 'view:statistics:overview',
  VIEW_INDUSTRY_ANALYSIS = 'view:statistics:industry',
  VIEW_REGION_ANALYSIS = 'view:statistics:region',
  
  // 用户管理
  VIEW_USER_LIST = 'view:user:list',
  MANAGE_USERS = 'manage:users',
  
  // 系统设置
  MANAGE_SYSTEM = 'manage:system'
}

// 角色权限映射
export const rolePermissions: Record<UserRole, Permission[]> = {
  [UserRole.ADMIN]: Object.values(Permission), // 管理员拥有所有权限
  
  [UserRole.RISK_REVIEWER]: [
    Permission.VIEW_CUSTOMER_LIST,
    Permission.VIEW_BREACHED_CUSTOMERS,
    Permission.VIEW_BREACH_RECORDS,
    Permission.REVIEW_BREACH,
    Permission.VIEW_REBIRTH_RECORDS,
    Permission.REVIEW_REBIRTH,
    Permission.VIEW_STATISTICS_OVERVIEW,
    Permission.VIEW_INDUSTRY_ANALYSIS,
    Permission.VIEW_REGION_ANALYSIS
  ],
  
  [UserRole.RISK_CONTROL]: [
    Permission.VIEW_CUSTOMER_LIST,
    Permission.VIEW_BREACHED_CUSTOMERS,
    Permission.VIEW_BREACH_RECORDS,
    Permission.APPLY_BREACH,
    Permission.VIEW_REBIRTH_RECORDS,
    Permission.APPLY_REBIRTH
  ]
}

// 路由权限映射
export const routePermissions: Record<string, Permission[]> = {
  '/customer/list': [Permission.VIEW_CUSTOMER_LIST],
  '/customer/breached': [Permission.VIEW_BREACHED_CUSTOMERS],
  '/breach/records': [Permission.VIEW_BREACH_RECORDS],
  '/breach/apply': [Permission.APPLY_BREACH],
  '/breach/review': [Permission.REVIEW_BREACH],
  '/breach/reasons': [Permission.MANAGE_BREACH_REASONS],
  '/rebirth/records': [Permission.VIEW_REBIRTH_RECORDS],
  '/rebirth/apply': [Permission.APPLY_REBIRTH],
  '/rebirth/review': [Permission.REVIEW_REBIRTH],
  '/rebirth/reasons': [Permission.MANAGE_REBIRTH_REASONS],
  '/statistics/overview': [Permission.VIEW_STATISTICS_OVERVIEW],
  '/statistics/industry': [Permission.VIEW_INDUSTRY_ANALYSIS],
  '/statistics/region': [Permission.VIEW_REGION_ANALYSIS],
  '/user/list': [Permission.VIEW_USER_LIST],
  '/user/manage': [Permission.MANAGE_USERS],
  '/system': [Permission.MANAGE_SYSTEM]
}

// 获取用户角色
const getUserRole = (username: string | null): UserRole | null => {
  if (!username) return null
  
  const lowerUsername = username.toLowerCase()
  
  if (lowerUsername.includes('admin')) return UserRole.ADMIN
  if (lowerUsername.includes('review')) return UserRole.RISK_REVIEWER
  if (lowerUsername.includes('control')) return UserRole.RISK_CONTROL
  return UserRole.VIEWER
}

// 检查是否是管理员
const checkIsAdmin = (username: string | null): boolean => {
  if (!username) return false
  return getUserRole(username) === UserRole.ADMIN
}

// 权限检查函数
export const hasPermission = (permission: Permission): boolean => {
  const authStore = useAuthStore()
  if (!authStore.isAuthenticated || !authStore.username) return false
  
  // 管理员拥有所有权限
  if (checkIsAdmin(authStore.username)) {
    return true
  }
  
  const userRole = getUserRole(authStore.username)
  if (!userRole) return false
  
  const userPermissions = rolePermissions[userRole] || []
  return userPermissions.includes(permission)
}

// 路由权限检查 - 修复版本
export const hasRoutePermission = (path: string): boolean => {
  const authStore = useAuthStore()
  
  // 未认证用户无权限
  if (!authStore.isAuthenticated || !authStore.username) return false
  
  // 管理员可以访问所有路由
  if (checkIsAdmin(authStore.username)) {
    return true
  }
  
  const requiredPermissions = routePermissions[path]
  
  // 没有设置权限的路由默认允许访问
  if (!requiredPermissions || requiredPermissions.length === 0) {
    return true
  }
  
  // 检查是否拥有任一所需权限
  return requiredPermissions.some(permission => hasPermission(permission))
}

// 获取当前用户角色
export const getCurrentUserRole = (): UserRole | null => {
  const authStore = useAuthStore()
  if (!authStore.isAuthenticated || !authStore.username) return null
  return getUserRole(authStore.username)
}

// 检查是否是管理员
export const isAdmin = (): boolean => {
  const authStore = useAuthStore()
  return authStore.isAuthenticated && authStore.username ? checkIsAdmin(authStore.username) : false
}
