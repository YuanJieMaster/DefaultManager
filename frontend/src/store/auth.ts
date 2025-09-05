// 认证状态管理
import { defineStore } from 'pinia'

// 认证状态接口定义
interface AuthState {
  token: string | null // 用户认证令牌
  username: string | null // 用户名
  isAuthenticated: boolean // 是否已认证
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    // 从本地存储获取认证信息，实现页面刷新后保持登录状态
    token: localStorage.getItem('token') || 'dev-token',
    username: localStorage.getItem('username') || 'dev-user',
    isAuthenticated: true || !!localStorage.getItem('token') || !!localStorage.getItem('username'),
  }),
  actions: {
    // 设置登录状态并保存到本地存储
    setLogin(token: string | null, username: string | null) {
      this.token = token
      this.username = username
      this.isAuthenticated = !!token || !!username
      
      // 保存或移除本地存储中的认证信息
      if (token) localStorage.setItem('token', token); else localStorage.removeItem('token')
      if (username) localStorage.setItem('username', username); else localStorage.removeItem('username')
    },
    
    // 退出登录，清除认证状态
    logout() {
      this.setLogin(null, null)
    }
  }
})
