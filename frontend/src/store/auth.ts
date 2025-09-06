// 认证状态管理
import { defineStore } from 'pinia'

// 认证状态接口定义
interface AuthState {
  username: string | null // 用户名
  isAuthenticated: boolean // 是否已认证
  role: string | null // 用户角色
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    // 从本地存储获取用户名，实现页面刷新后保持用户信息
    username: localStorage.getItem('username') || null,
    // 认证状态基于本地存储中的用户名和会话状态
    isAuthenticated: !!localStorage.getItem('username'),
    // 从本地存储获取用户角色
    role: localStorage.getItem('role') || null,
  }),
  actions: {
    // 设置登录状态并保存用户名和角色到本地存储
    setLogin(username: string | null, role: string | null = null) {
      this.username = username
      this.isAuthenticated = !!username
      this.role = role
      
      // 保存或移除本地存储中的用户名和角色
      if (username && role) {
        localStorage.setItem('username', username)
        localStorage.setItem('role', role)
      } else {
        localStorage.removeItem('username')
        localStorage.removeItem('role')
      }
    },

    // 退出登录，清除认证状态
    logout() {
      // 清除本地存储
      this.setLogin(null)
      
      // 清除会话cookie
      // 实际的会话清除通常由后端处理，但前端可以清除本地存储
      console.log('用户已退出登录，会话将由后端自动管理')
    }
  }
})
