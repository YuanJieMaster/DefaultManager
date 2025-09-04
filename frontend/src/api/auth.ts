import request from '@/utils/request'

export interface LoginPayload {
  username: string
  password: string
  rememberMe?: boolean
}

export interface LoginResult {
  token?: string
  username?: string
}

// JSON 登录（如果后端提供 /api/auth/login 返回 JWT）
export const loginJson = (data: LoginPayload) => {
  return request.post<LoginResult>('/auth/login', data)
}

// 表单登录（Spring Security 默认 /login，基于 Session）
export const loginForm = async (data: LoginPayload) => {
  const form = new URLSearchParams()
  form.append('username', data.username)
  form.append('password', data.password)
  // 使用裸 axios 以便自定义 header
  const res = await fetch('/api/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: form.toString(),
    credentials: 'include'
  })
  if (!res.ok) {
    throw new Error('登录失败')
  }
  // 表单登录通常没有 token，返回占位
  return { username: data.username } as LoginResult
}
