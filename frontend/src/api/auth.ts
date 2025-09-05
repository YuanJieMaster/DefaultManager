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
  token?: string // JWT令牌（可选）
  username?: string // 用户名
}

// JSON格式登录（适用于返回JWT令牌的认证方式）
export const loginJson = (data: LoginPayload) => {
  return request.post<LoginResult>('/auth/login', data)
}

// 表单格式登录（适用于Spring Security基于Session的认证方式）
export const loginForm = async (data: LoginPayload) => {
  // 构建表单数据
  const form = new URLSearchParams()
  form.append('username', data.username)
  form.append('password', data.password)
  
  // 使用fetch API发送表单数据，包含credentials确保Cookie被正确处理
  const res = await fetch('/api/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: form.toString(),
    credentials: 'include' // 包含Cookie，支持Session认证
  })
  
  // 处理错误响应
  if (!res.ok) {
    throw new Error('登录失败')
  }
  
  // 表单登录通常通过Session维护状态，返回用户名作为占位
  return { username: data.username } as LoginResult
}
