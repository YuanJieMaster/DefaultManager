import { defineStore } from 'pinia'

interface AuthState {
  token: string | null
  username: string | null
  isAuthenticated: boolean
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
    username: localStorage.getItem('username'),
    isAuthenticated: !!localStorage.getItem('token') || !!localStorage.getItem('username'),
  }),
  actions: {
    setLogin(token: string | null, username: string | null) {
      this.token = token
      this.username = username
      this.isAuthenticated = !!token || !!username
      if (token) localStorage.setItem('token', token); else localStorage.removeItem('token')
      if (username) localStorage.setItem('username', username); else localStorage.removeItem('username')
    },
    logout() {
      this.setLogin(null, null)
    }
  }
})
