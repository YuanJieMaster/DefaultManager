<template>
  <div class="dashboard-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">仪表盘</h1>
        <p class="subtitle">欢迎使用违约管理系统，这里是系统运行的关键指标概览</p>
      </div>
      <div class="header-actions">
        <div class="welcome-message">
          <span>欢迎回来，{{ userName }}</span>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card" shadow="hover" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
        <div class="stat-content">
          <div class="stat-header">
            <span class="stat-number">{{ totalCustomers }}</span>
            <span class="stat-change">
              <el-icon class="change-icon"><TrendingUp /></el-icon>
              <span>+12%</span>
            </span>
          </div>
          <div class="stat-label">总客户数</div>
        </div>
        <div class="stat-icon">
          <User class="icon-large" />
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
        <div class="stat-content">
          <div class="stat-header">
            <span class="stat-number">{{ totalBreaches }}</span>
            <span class="stat-change danger">
              <el-icon class="change-icon"><TrendingUp /></el-icon>
              <span>+8%</span>
            </span>
          </div>
          <div class="stat-label">违约记录</div>
        </div>
        <div class="stat-icon">
          <Warning class="icon-large" />
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
        <div class="stat-content">
          <div class="stat-header">
            <span class="stat-number">{{ pendingReviews }}</span>
            <span class="stat-change warning">
              <el-icon class="change-icon"><Clock /></el-icon>
              <span>待处理</span>
            </span>
          </div>
          <div class="stat-label">待审核</div>
        </div>
        <div class="stat-icon">
          <Clock class="icon-large" />
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
        <div class="stat-content">
          <div class="stat-header">
            <span class="stat-number">{{ rebirthSuccess }}</span>
            <span class="stat-change success">
              <el-icon class="change-icon"><TrendingUp /></el-icon>
              <span>+24%</span>
            </span>
          </div>
          <div class="stat-label">重生成功</div>
        </div>
        <div class="stat-icon">
          <Refresh class="icon-large" />
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 违约趋势图 -->
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="chart-title">违约趋势</span>
            <el-select v-model="chartTimeRange" size="small" class="time-range-select">
              <el-option label="近6个月" value="6months" />
              <el-option label="近12个月" value="12months" />
              <el-option label="全部" value="all" />
            </el-select>
          </div>
        </template>
        <div class="chart-wrapper">
          <div id="breachTrendChart" class="chart"></div>
        </div>
      </el-card>

      <!-- 行业分布 -->
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="chart-title">客户行业分布</span>
            <el-select v-model="chartType" size="small" class="chart-type-select">
              <el-option label="饼图" value="pie" />
              <el-option label="柱状图" value="bar" />
            </el-select>
          </div>
        </template>
        <div class="chart-wrapper">
          <div id="industryChart" class="chart"></div>
        </div>
      </el-card>
    </div>

    <!-- 第二行图表 -->
    <div class="charts-container">
      <!-- 客户风险评分分布图 -->
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="chart-title">客户风险评分分布</span>
          </div>
        </template>
        <div class="chart-wrapper">
          <div id="riskScoreChart" class="chart"></div>
        </div>
      </el-card>

      <!-- 月度重生申请统计 -->
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="chart-title">月度重生申请统计</span>
          </div>
        </template>
        <div class="chart-wrapper">
          <div id="rebirthChart" class="chart"></div>
        </div>
      </el-card>
    </div>

    <!-- 最近记录表格 -->
    <el-card class="recent-records-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="table-title">最近记录</span>
          <div class="table-actions">
            <el-select v-model="recordType" size="small" class="record-type-select">
              <el-option label="全部记录" value="all" />
              <el-option label="违约记录" value="breach" />
              <el-option label="重生记录" value="rebirth" />
            </el-select>
            <el-link type="primary" :underline="false" @click="viewAllRecords" class="view-all-link">
              查看全部
              <el-icon><ArrowRight /></el-icon>
            </el-link>
          </div>
        </div>
      </template>
      <el-table :data="filteredRecords" style="width: 100%" v-loading="tableLoading" element-loading-text="加载中" border>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="customerName" label="客户名称" width="180">
          <template #default="scope">
            <el-popover trigger="hover" placement="top">
              <div class="customer-popover">
                <p>客户ID: {{ scope.row.id }}</p>
                <p>行业: {{ scope.row.industry || '未知' }}</p>
                <p>注册时间: {{ scope.row.registerTime || '未知' }}</p>
              </div>
              <template #reference>
                <span class="customer-name" @click="viewCustomerDetail(scope.row.id)">{{ scope.row.customerName }}</span>
              </template>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.type === 'breach'" type="danger" class="tag-hover">违约</el-tag>
            <el-tag v-else-if="scope.row.type === 'rebirth'" type="success" class="tag-hover">重生</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因">
          <template #default="scope">
            <el-popover trigger="hover" placement="top">
              <p>{{ scope.row.reason }}</p>
              <template #reference>
                <span class="reason-text">{{ scope.row.reason.length > 20 ? scope.row.reason.substring(0, 20) + '...' : scope.row.reason }}</span>
              </template>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'PENDING'" type="warning" class="tag-hover">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 'APPROVED'" type="success" class="tag-hover">已通过</el-tag>
            <el-tag v-else-if="scope.row.status === 'REJECTED'" type="danger" class="tag-hover">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewRecordDetail(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 底部操作区域 -->
    <div class="dashboard-footer">
      <el-button type="primary" @click="refreshDashboard" class="refresh-btn">
        <el-icon><Refresh /></el-icon>
        刷新数据
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { User, Warning, Clock, Refresh, ArrowRight, TrendingUp } from '@element-plus/icons-vue'
import { customerApi } from '@/api/customer'
import { breachApi } from '@/api/breach'
import { rebirthApi } from '@/api/rebirth'
import { useAuthStore } from '@/store/auth'
import type { BreachRecordResponseDTO, RebirthRecordResponseDTO } from '@/types'

const router = useRouter()
const auth = useAuthStore()

// 统计数据
const totalCustomers = ref(0)
const totalBreaches = ref(0)
const pendingReviews = ref(0)
const rebirthSuccess = ref(0)

// 图表控制
const chartTimeRange = ref('6months')
const chartType = ref('pie')
const recordType = ref('all')
const tableLoading = ref(false)

// 获取当前用户名
const userName = ref('管理员') // 实际项目中应从auth store获取

// 最近记录数据
const recentRecords = ref<Array<{
  id: number
  customerName: string
  type: 'breach' | 'rebirth'
  reason: string
  status: string
  createTime: string
  industry?: string
  registerTime?: string
}>>([])

// 计算属性：根据选择的记录类型过滤表格数据
const filteredRecords = computed(() => {
  if (recordType.value === 'all') {
    return recentRecords.value
  }
  return recentRecords.value.filter(record => record.type === recordType.value)
})

// 模拟数据生成函数
const generateMockData = () => {
  // 统计数据
  totalCustomers.value = 156
  totalBreaches.value = 28
  pendingReviews.value = 12
  rebirthSuccess.value = 8

  // 最近记录数据
  recentRecords.value = [
    {
      id: 102, 
      customerName: '北京星辰科技有限公司', 
      type: 'breach', 
      reason: '未按时还款超过30天', 
      status: 'PENDING', 
      createTime: '2023-07-12 14:30:22',
      industry: '科技',
      registerTime: '2022-05-15'
    },
    {
      id: 101, 
      customerName: '上海远景贸易有限公司', 
      type: 'rebirth', 
      reason: '已完成全部还款计划，并提供了详细的整改方案，财务状况有所改善', 
      status: 'APPROVED', 
      createTime: '2023-07-11 10:15:08',
      industry: '贸易',
      registerTime: '2021-08-22'
    },
    {
      id: 100, 
      customerName: '广州恒远物流有限公司', 
      type: 'breach', 
      reason: '违反合同约定条款，未按规定路线配送导致货物延误', 
      status: 'REJECTED', 
      createTime: '2023-07-10 16:42:35',
      industry: '物流',
      registerTime: '2022-01-10'
    },
    {
      id: 99, 
      customerName: '深圳科技创新有限公司', 
      type: 'breach', 
      reason: '财务状况异常，连续三个月亏损，存在资金链断裂风险', 
      status: 'APPROVED', 
      createTime: '2023-07-09 09:28:16',
      industry: '科技',
      registerTime: '2021-11-30'
    },
    {
      id: 98, 
      customerName: '杭州未来金融服务有限公司', 
      type: 'rebirth', 
      reason: '成功重组债务结构，引入战略投资者，业务模式转型成功', 
      status: 'APPROVED', 
      createTime: '2023-07-08 15:10:42',
      industry: '金融',
      registerTime: '2020-12-05'
    }
  ]
}

// 初始化图表
const initCharts = () => {
  // 违约趋势图
  const breachTrendChart = echarts.init(document.getElementById('breachTrendChart'))
  const breachTrendOption = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 0, 0, 0.7)',
      borderColor: '#333',
      textStyle: { color: '#fff' },
      axisPointer: {
        type: 'cross',
        label: { backgroundColor: '#6a7985' }
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    legend: {
      data: ['违约记录'],
      textStyle: { color: '#606266' }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisLabel: { color: '#606266' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisLabel: { color: '#606266' },
      splitLine: { lineStyle: { color: '#f0f2f5' } }
    },
    series: [
      {
        name: '违约记录',
        type: 'line',
        smooth: true,
        data: [12, 19, 15, 22, 18, 28],
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245, 108, 108, 0.5)' },
            { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
          ])
        },
        lineStyle: {
          width: 3,
          color: '#f56c6c'
        },
        itemStyle: {
          color: '#f56c6c',
          borderColor: '#fff',
          borderWidth: 2,
          shadowBlur: 4,
          shadowColor: 'rgba(0, 0, 0, 0.2)'
        },
        emphasis: {
          focus: 'series',
          itemStyle: {
            borderWidth: 3,
            shadowBlur: 8
          }
        }
      }
    ]
  }
  breachTrendChart.setOption(breachTrendOption)

  // 行业分布图表
  updateIndustryChart()

  // 风险评分分布图
  const riskScoreChart = echarts.init(document.getElementById('riskScoreChart'))
  riskScoreChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 0, 0, 0.7)',
      borderColor: '#333',
      textStyle: { color: '#fff' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['低风险', '中低风险', '中风险', '中高风险', '高风险'],
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisLabel: { color: '#606266' }
    },
    yAxis: {
      type: 'value',
      name: '客户数',
      nameTextStyle: { color: '#606266' },
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisLabel: { color: '#606266' },
      splitLine: { lineStyle: { color: '#f0f2f5' } }
    },
    series: [
      {
        name: '客户数',
        type: 'bar',
        data: [45, 58, 32, 15, 6],
        itemStyle: {
          color: function(params) {
            const colorList = ['#67c23a', '#e6a23c', '#f56c6c', '#d93f3f', '#722ed1']
            return colorList[params.dataIndex]
          },
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.3)'
          }
        }
      }
    ]
  })

  // 重生申请统计图表
  const rebirthChart = echarts.init(document.getElementById('rebirthChart'))
  rebirthChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 0, 0, 0.7)',
      borderColor: '#333',
      textStyle: { color: '#fff' }
    },
    legend: {
      data: ['申请数量', '成功数量'],
      textStyle: { color: '#606266' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisLabel: { color: '#606266' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisLabel: { color: '#606266' },
      splitLine: { lineStyle: { color: '#f0f2f5' } }
    },
    series: [
      {
        name: '申请数量',
        type: 'line',
        smooth: true,
        data: [5, 7, 9, 12, 15, 18],
        lineStyle: {
          width: 2,
          color: '#667eea'
        },
        itemStyle: {
          color: '#667eea'
        }
      },
      {
        name: '成功数量',
        type: 'line',
        smooth: true,
        data: [2, 3, 5, 8, 10, 12],
        lineStyle: {
          width: 2,
          color: '#67c23a'
        },
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  })

  // 响应式图表
  const handleResize = () => {
    breachTrendChart.resize()
    const industryChart = echarts.getInstanceByDom(document.getElementById('industryChart'))
    if (industryChart) industryChart.resize()
    riskScoreChart.resize()
    rebirthChart.resize()
  }

  window.addEventListener('resize', handleResize)
  
  // 清理函数
  const cleanup = () => {
    window.removeEventListener('resize', handleResize)
  }
  
  // 返回清理函数以便在组件卸载时调用
  return cleanup
}

// 更新行业分布图
const updateIndustryChart = () => {
  const industryChart = echarts.getInstanceByDom(document.getElementById('industryChart'))
  if (!industryChart) return
  
  const chartData = [
    { value: 35, name: '科技' },
    { value: 25, name: '贸易' },
    { value: 20, name: '金融' },
    { value: 15, name: '物流' },
    { value: 5, name: '其他' }
  ]
  
  if (chartType.value === 'pie') {
    industryChart.setOption({
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(0, 0, 0, 0.7)',
        borderColor: '#333',
        textStyle: { color: '#fff' },
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        textStyle: { color: '#606266' }
      },
      series: [
        {
          name: '行业分布',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            },
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          labelLine: {
            show: false
          },
          data: chartData
        }
      ]
    })
  } else {
    industryChart.setOption({
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(0, 0, 0, 0.7)',
        borderColor: '#333',
        textStyle: { color: '#fff' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: chartData.map(item => item.name),
        axisLine: { lineStyle: { color: '#dcdfe6' } },
        axisLabel: { color: '#606266' }
      },
      yAxis: {
        type: 'value',
        name: '客户数',
        nameTextStyle: { color: '#606266' },
        axisLine: { lineStyle: { color: '#dcdfe6' } },
        axisLabel: { color: '#606266' },
        splitLine: { lineStyle: { color: '#f0f2f5' } }
      },
      series: [
        {
          name: '客户数',
          type: 'bar',
          data: chartData.map(item => item.value),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.3)'
            }
          }
        }
      ]
    })
  }
}

// 查看全部记录
const viewAllRecords = () => {
  router.push('/breach/records')
}

// 查看客户详情
const viewCustomerDetail = (id: number) => {
  router.push(`/customer/detail?id=${id}`)
}

// 查看记录详情
const viewRecordDetail = (record: any) => {
  if (record.type === 'breach') {
    router.push(`/breach/detail?id=${record.id}`)
  } else {
    router.push(`/rebirth/detail?id=${record.id}`)
  }
}

// 刷新仪表盘数据
const refreshDashboard = () => {
  tableLoading.value = true
  // 模拟数据加载延迟
  setTimeout(() => {
    generateMockData()
    updateIndustryChart()
    tableLoading.value = false
  }, 800)
}

// 卡片悬停效果
const hoverCard = (event: Event) => {
  const card = event.currentTarget as HTMLElement
  card.style.transform = 'translateY(-5px)'
  card.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.1)'
}

const leaveCard = (event: Event) => {
  const card = event.currentTarget as HTMLElement
  card.style.transform = 'translateY(0)'
  card.style.boxShadow = '0 2px 12px 0 rgba(0, 0, 0, 0.1)'
}

// 监听图表类型变化
watch(chartType, () => {
  updateIndustryChart()
})

// 监听时间范围变化
watch(chartTimeRange, () => {
  // 实际项目中应根据时间范围重新获取数据并更新图表
  refreshDashboard()
})

// 生命周期钩子
let cleanupFunction: (() => void) | undefined

onMounted(() => {
  // 在实际项目中，这里应该调用API获取真实数据
  generateMockData()
  cleanupFunction = initCharts()
})

// 清理函数（在组件卸载时调用）
const onUnmounted = () => {
  if (cleanupFunction) {
    cleanupFunction()
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  min-height: 100vh;
  background-color: #f5f7fa;
  transition: all 0.3s ease;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding: 16px 0;
}

.header-content .page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.header-content .subtitle {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.welcome-message {
  font-size: 14px;
  color: #606266;
  padding: 8px 16px;
  background-color: #f0f2f5;
  border-radius: 20px;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 28px;
}

.stat-card {
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  border-radius: 12px !important;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid #e4e7ed;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1) !important;
}

.stat-content {
  padding: 24px 20px;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 12px;
}

.stat-number {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.stat-change {
  font-size: 12px;
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-change.danger {
  color: #f56c6c;
}

.stat-change.warning {
  color: #e6a23c;
}

.stat-change.success {
  color: #67c23a;
}

.change-icon {
  width: 14px;
  height: 14px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.stat-icon {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 48px;
  height: 48px;
  background-color: #f0f2f5;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-large {
  width: 24px;
  height: 24px;
  color: #667eea;
}

/* 图表容器 */
.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 20px;
  margin-bottom: 28px;
}

.chart-card {
  height: 360px;
  transition: all 0.3s ease;
  border-radius: 12px !important;
  background: #ffffff;
  border: 1px solid #e4e7ed;
}

.chart-card:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1) !important;
}

.chart-wrapper {
  height: calc(100% - 56px);
  padding: 0 16px 16px 16px;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.time-range-select,
.chart-type-select,
.record-type-select {
  width: 120px;
}

/* 最近记录表格 */
.recent-records-card {
  transition: all 0.3s ease;
  border-radius: 12px !important;
  background: #ffffff;
  border: 1px solid #e4e7ed;
  margin-bottom: 28px;
}

.recent-records-card:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1) !important;
}

.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.table-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.view-all-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #667eea !important;
}

.view-all-link:hover {
  color: #5a67d8 !important;
}

/* 表格样式 */
:deep(.el-table) {
  border-radius: 0 0 12px 12px !important;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  background-color: #fafafa;
}

:deep(.el-table__header th) {
  background-color: #fafafa !important;
  font-weight: 600 !important;
  color: #303133 !important;
  border-bottom: 1px solid #f0f2f5 !important;
}

:deep(.el-table__body tr:hover > td) {
  background-color: #f5f7fa !important;
}

:deep(.el-table__body tr.current-row > td) {
  background-color: #e6f4ff !important;
}

/* 标签样式 */
:deep(.tag-hover) {
  transition: all 0.3s ease;
  cursor: pointer;
}

:deep(.tag-hover:hover) {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 客户名称和原因文本 */
.customer-name {
  color: #667eea;
  cursor: pointer;
  text-decoration: underline;
  transition: all 0.3s ease;
}

.customer-name:hover {
  color: #5a67d8;
  text-decoration: none;
}

.reason-text {
  color: #606266;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reason-text:hover {
  color: #303133;
  text-decoration: underline;
}

/* 客户信息弹窗 */
.customer-popover {
  padding: 8px 0;
}

.customer-popover p {
  margin: 4px 0;
  font-size: 12px;
  color: #606266;
}

/* 底部操作区域 */
.dashboard-footer {
  margin-top: 24px;
  text-align: center;
}

.refresh-btn {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  background-color: #667eea;
  border-color: #667eea;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  background-color: #5a67d8;
  border-color: #5a67d8;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .header-content .page-title {
    font-size: 24px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .stat-card {
    border-radius: 8px !important;
  }
  
  .chart-card {
    height: 320px;
    border-radius: 8px !important;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    padding: 12px 16px;
  }
  
  .table-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    width: 100%;
  }
  
  .time-range-select,
  .chart-type-select,
  .record-type-select {
    width: 100%;
  }
  
  .view-all-link {
    align-self: flex-end;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .dashboard-container {
    background-color: #1a1a1a;
  }
  
  .header-content .page-title {
    color: #ffffff;
  }
  
  .header-content .subtitle {
    color: #cccccc;
  }
  
  .welcome-message {
    color: #cccccc;
    background-color: #333333;
  }
  
  .stat-card {
    background: linear-gradient(135deg, #2d2d2d 0%, #252525 100%);
    border: 1px solid #444444;
  }
  
  .stat-number {
    color: #ffffff;
  }
  
  .stat-label {
    color: #cccccc;
  }
  
  .stat-icon {
    background-color: #444444;
  }
  
  .chart-card {
    background: #2d2d2d;
    border: 1px solid #444444;
  }
  
  .chart-title {
    color: #ffffff;
  }
  
  .table-title {
    color: #ffffff;
  }
  
  .recent-records-card {
    background: #2d2d2d;
    border: 1px solid #444444;
  }
  
  :deep(.el-table__header-wrapper) {
    background-color: #333333;
  }
  
  :deep(.el-table__header th) {
    background-color: #333333 !important;
    color: #ffffff !important;
    border-bottom: 1px solid #444444 !important;
  }
  
  :deep(.el-table__body tr:hover > td) {
    background-color: #333333 !important;
  }
  
  :deep(.el-table__body tr.current-row > td) {
    background-color: #1e40af !important;
  }
  
  .reason-text {
    color: #cccccc;
  }
  
  .reason-text:hover {
    color: #ffffff;
  }
}
</style>
