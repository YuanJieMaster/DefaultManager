<template>
  <div id="app">
    <template v-if="$route.path === '/login'">
      <router-view />
    </template>
    <template v-else>
      <el-container class="layout-container">
        <el-aside width="250px" class="sidebar">
          <div class="logo"><h2>违约管理系统</h2></div>
          <el-menu :default-active="$route.path" class="sidebar-menu" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
            <!-- 仪表盘 - 只有ADMIN可以访问 -->
<!--            <el-menu-item v-if="auth.role === 'ADMIN'" router index="/dashboard"><el-icon><DataBoard /></el-icon><span>仪表盘</span></el-menu-item>-->
            
            <!-- 客户管理 - 所有角色都可以访问 -->
            <el-sub-menu index="/customer">
              <template #title><el-icon><User /></el-icon><span>客户管理</span></template>
              <el-menu-item index="/customer/list">客户列表</el-menu-item>
              <el-menu-item index="/customer/breached">违约客户</el-menu-item>
            </el-sub-menu>
            
            <!-- 违约管理 -->
            <el-sub-menu index="/breach">
              <template #title><el-icon><Warning /></el-icon><span>违约管理</span></template>
              <el-menu-item index="/breach/records">违约记录</el-menu-item>
              <el-menu-item v-if="auth.role === 'ADMIN' || auth.role === 'RISK_CONTROL'" index="/breach/apply">违约申请</el-menu-item>
              <el-menu-item v-if="auth.role === 'ADMIN' || auth.role === 'RISK_REVIEWER'" index="/breach/review">违约审核</el-menu-item>
              <el-menu-item v-if="auth.role === 'ADMIN'" index="/breach/reasons">违约原因维护</el-menu-item>
            </el-sub-menu>
            
            <!-- 重生管理 -->
            <el-sub-menu index="/rebirth">
              <template #title><el-icon><Refresh /></el-icon><span>重生管理</span></template>
              <el-menu-item index="/rebirth/records">重生记录</el-menu-item>
              <el-menu-item v-if="auth.role === 'ADMIN' || auth.role === 'RISK_CONTROL'" index="/rebirth/apply">重生申请</el-menu-item>
              <el-menu-item v-if="auth.role === 'ADMIN' || auth.role === 'RISK_REVIEWER'" index="/rebirth/review">重生审核</el-menu-item>
              <el-menu-item v-if="auth.role === 'ADMIN'" index="/rebirth/reasons">重生原因维护</el-menu-item>
            </el-sub-menu>
            
            <!-- 统计分析 - 只有ADMIN和RISK_REVIEWER可以访问 -->
            <el-sub-menu v-if="auth.role === 'ADMIN' || auth.role === 'RISK_REVIEWER'" index="/statistics">
              <template #title><el-icon><TrendCharts /></el-icon><span>统计分析</span></template>
              <el-menu-item index="/statistics/overview">概览统计</el-menu-item>
            </el-sub-menu>
            
            <!-- 用户管理 - 只有ADMIN可以访问 -->
            <el-sub-menu v-if="auth.role === 'ADMIN'" index="/user">
              <template #title><el-icon><UserFilled /></el-icon><span>用户管理</span></template>
              <el-menu-item index="/user/list">用户列表</el-menu-item>
            </el-sub-menu>
            
            <!-- 系统设置 - 只有ADMIN可以访问 -->
            <el-menu-item v-if="auth.role === 'ADMIN'" index="/system"><el-icon><Setting /></el-icon><span>系统设置</span></el-menu-item>
          </el-menu>
        </el-aside>
        <el-container>
          <el-header class="header">
            <div class="header-left">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">{{ item.name }}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            <div class="header-right">
              <el-dropdown>
                <span class="user-info">
                  <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                  <span class="username">{{ auth.username }}</span>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>个人信息</el-dropdown-item>
                    <el-dropdown-item>修改密码</el-dropdown-item>
                    <el-dropdown-item divided @click="onLogout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </el-header>
          <el-main class="main-content">
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataBoard, User, Warning, Refresh, TrendCharts, Setting, UserFilled } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched.map(item => ({ name: item.meta.title as string, path: item.path }))
})

const onLogout = () => {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.sidebar { background-color: #304156; color: #bfcbd9; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; border-bottom: 1px solid #435266; }
.logo h2 { margin: 0; font-size: 18px; }
.sidebar-menu { border: none; }
.header { background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; }
.header-left { flex: 1; }
.header-right { display: flex; align-items: center; }
.user-info { display: flex; align-items: center; cursor: pointer; }
.username { margin-left: 8px; color: #606266; }
.main-content { background-color: #f0f2f5; padding: 20px; }
</style>
