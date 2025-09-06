// 认证相关API接口
import request from '@/utils/request'

// 登录请求参数接口
export interface LoginPayload {
  username: string // 用户名
  password: string // 密码
  rememberMe?: boolean // 是否记住登录状态
}

// 登录响应结果接口
export interface LoginResult {
  success: boolean // 登录是否成功
  username: string // 用户名
  role: string // 用户角色 (ADMIN, RISK_REVIEWER, RISK_CONTROL)
}

/**
 * 用户登录接口
 * 实际项目中基于Session的认证方式
 * 前端发送JSON格式数据，后端使用Spring Security认证
 */
export const login = async (data: LoginPayload) => {
  try {
    // 发送登录请求
    const response = await request.post<LoginResult>('/auth/login', data)
    
    // 返回登录结果
    return response
  } catch (error) {
    // 处理登录错误
    console.error('登录请求失败:', error)
    throw new Error('登录失败，请检查用户名和密码')
  }
}
