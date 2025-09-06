import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { hasRoutePermission } from '@/utils/permission'
import { ElMessage } from 'element-plus'

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
      },
      {
        path: 'reasons',
        name: 'BreachReasons',
        component: () => import('@/views/breach/BreachReasons.vue'),
        meta: { title: '违约原因维护' }
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
      },
      {
        path: 'reasons',
        name: 'RebirthReasons',
        component: () => import('@/views/rebirth/RebirthReasons.vue'),
        meta: { title: '重生原因维护' }
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
        component: () => import('@/views/statistics/DefaultStatistics.vue'),
        meta: { title: '概览统计' }
      }
    ]
  },
  {
    path: '/user',
    name: 'User',
    redirect: '/user/list',
    meta: { title: '用户管理' },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户列表' }
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

// 路由全局前置守卫 - 处理认证和权限控制
router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()
  
  // 处理公开路由（无需登录即可访问）
  if (to.meta.public) {
    // 已登录用户访问登录页时，自动重定向到仪表盘
    if (to.path === '/login' && auth.isAuthenticated) return next('/dashboard')
    return next()
  }
  
  // 处理需要认证的路由
  if (!auth.isAuthenticated) {
    // 未登录用户访问受保护路由时，重定向到登录页，并保存当前路由用于登录后跳转
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }
  
  // 已登录用户访问受保护路由，检查权限
  if (hasRoutePermission(to.path)) {
    // 有权限，允许访问
    next()
  } else {
    // 无权限，提示用户并跳转到仪表盘
    ElMessage.warning('您没有权限访问此页面')
    next('/dashboard')
  }
})

export default router
