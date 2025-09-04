import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '仪表盘' }
  },
  {
    path: '/customer',
    name: 'Customer',
    redirect: '/customer/list',
    meta: { title: '客户管理' },
    children: [
      {
        path: 'list',
        name: 'CustomerList',
        component: () => import('@/views/customer/CustomerList.vue'),
        meta: { title: '客户列表' }
      },
      {
        path: 'breached',
        name: 'BreachedCustomers',
        component: () => import('@/views/customer/BreachedCustomers.vue'),
        meta: { title: '违约客户' }
      }
    ]
  },
  {
    path: '/breach',
    name: 'Breach',
    redirect: '/breach/records',
    meta: { title: '违约管理' },
    children: [
      {
        path: 'records',
        name: 'BreachRecords',
        component: () => import('@/views/breach/BreachRecords.vue'),
        meta: { title: '违约记录' }
      },
      {
        path: 'apply',
        name: 'BreachApply',
        component: () => import('@/views/breach/BreachApply.vue'),
        meta: { title: '违约申请' }
      },
      {
        path: 'review',
        name: 'BreachReview',
        component: () => import('@/views/breach/BreachReview.vue'),
        meta: { title: '违约审核' }
      }
    ]
  },
  {
    path: '/rebirth',
    name: 'Rebirth',
    redirect: '/rebirth/records',
    meta: { title: '重生管理' },
    children: [
      {
        path: 'records',
        name: 'RebirthRecords',
        component: () => import('@/views/rebirth/RebirthRecords.vue'),
        meta: { title: '重生记录' }
      },
      {
        path: 'apply',
        name: 'RebirthApply',
        component: () => import('@/views/rebirth/RebirthApply.vue'),
        meta: { title: '重生申请' }
      },
      {
        path: 'review',
        name: 'RebirthReview',
        component: () => import('@/views/rebirth/RebirthReview.vue'),
        meta: { title: '重生审核' }
      }
    ]
  },
  {
    path: '/statistics',
    name: 'Statistics',
    redirect: '/statistics/overview',
    meta: { title: '统计分析' },
    children: [
      {
        path: 'overview',
        name: 'StatisticsOverview',
        component: () => import('@/views/statistics/StatisticsOverview.vue'),
        meta: { title: '概览统计' }
      },
      {
        path: 'industry',
        name: 'IndustryAnalysis',
        component: () => import('@/views/statistics/IndustryAnalysis.vue'),
        meta: { title: '行业分析' }
      },
      {
        path: 'region',
        name: 'RegionAnalysis',
        component: () => import('@/views/statistics/RegionAnalysis.vue'),
        meta: { title: '区域分析' }
      }
    ]
  },
  {
    path: '/system',
    name: 'System',
    component: () => import('@/views/System.vue'),
    meta: { title: '系统设置' }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()
  if (to.meta.public) {
    // 已登录访问登录页，跳转首页
    if (to.path === '/login' && auth.isAuthenticated) return next('/dashboard')
    return next()
  }
  if (!auth.isAuthenticated) return next({ path: '/login', query: { redirect: to.fullPath } })
  next()
})

export default router
