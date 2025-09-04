<template>
  <div class="statistics-overview-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>统计概览</h2>
      <p class="subtitle">查看系统关键指标和数据统计</p>
    </div>

    <!-- 时间筛选器 -->
    <div class="time-filter-container">
      <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
        <el-radio-button label="today">今日</el-radio-button>
        <el-radio-button label="week">本周</el-radio-button>
        <el-radio-button label="month">本月</el-radio-button>
        <el-radio-button label="quarter">本季度</el-radio-button>
        <el-radio-button label="year">本年</el-radio-button>
        <el-radio-button label="custom">自定义</el-radio-button>
      </el-radio-group>
      
      <div class="custom-date-picker" v-if="timeRange === 'custom'">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="small"
          @change="handleDateRangeChange"
        />
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-card-content">
          <div class="stat-title">总客户数</div>
          <div class="stat-value">{{ statistics.totalCustomers }}</div>
          <div class="stat-change" :class="{ positive: statistics.customerGrowth > 0, negative: statistics.customerGrowth < 0 }">
            <span v-if="statistics.customerGrowth > 0">↑</span>
            <span v-else-if="statistics.customerGrowth < 0">↓</span>
            {{ Math.abs(statistics.customerGrowth) }}%
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-card-content">
          <div class="stat-title">违约记录数</div>
          <div class="stat-value">{{ statistics.totalBreaches }}</div>
          <div class="stat-change" :class="{ positive: statistics.breachGrowth > 0, negative: statistics.breachGrowth < 0 }">
            <span v-if="statistics.breachGrowth > 0">↑</span>
            <span v-else-if="statistics.breachGrowth < 0">↓</span>
            {{ Math.abs(statistics.breachGrowth) }}%
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-card-content">
          <div class="stat-title">重生申请数</div>
          <div class="stat-value">{{ statistics.totalRebirths }}</div>
          <div class="stat-change" :class="{ positive: statistics.rebirthGrowth > 0, negative: statistics.rebirthGrowth < 0 }">
            <span v-if="statistics.rebirthGrowth > 0">↑</span>
            <span v-else-if="statistics.rebirthGrowth < 0">↓</span>
            {{ Math.abs(statistics.rebirthGrowth) }}%
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-card-content">
          <div class="stat-title">预警客户数</div>
          <div class="stat-value">{{ statistics.warningCustomers }}</div>
          <div class="stat-change" :class="{ positive: statistics.warningGrowth > 0, negative: statistics.warningGrowth < 0 }">
            <span v-if="statistics.warningGrowth > 0">↑</span>
            <span v-else-if="statistics.warningGrowth < 0">↓</span>
            {{ Math.abs(statistics.warningGrowth) }}%
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-card-content">
          <div class="stat-title">冻结客户数</div>
          <div class="stat-value">{{ statistics.blockedCustomers }}</div>
          <div class="stat-change" :class="{ positive: statistics.blockedGrowth > 0, negative: statistics.blockedGrowth < 0 }">
            <span v-if="statistics.blockedGrowth > 0">↑</span>
            <span v-else-if="statistics.blockedGrowth < 0">↓</span>
            {{ Math.abs(statistics.blockedGrowth) }}%
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-card-content">
          <div class="stat-title">平均违约次数</div>
          <div class="stat-value">{{ statistics.avgBreaches.toFixed(2) }}</div>
          <div class="stat-change" :class="{ positive: statistics.avgBreachChange > 0, negative: statistics.avgBreachChange < 0 }">
            <span v-if="statistics.avgBreachChange > 0">↑</span>
            <span v-else-if="statistics.avgBreachChange < 0">↓</span>
            {{ Math.abs(statistics.avgBreachChange).toFixed(2) }}%
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>客户增长趋势</span>
            <el-select v-model="chartTimeUnit" size="small" @change="updateCharts">
              <el-option label="日" value="day" />
              <el-option label="周" value="week" />
              <el-option label="月" value="month" />
            </el-select>
          </div>
        </template>
        <div class="chart-content">
          <div ref="customerTrendChart" class="chart" />
        </div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>违约记录趋势</span>
            <el-select v-model="chartTimeUnit" size="small" @change="updateCharts" style="display: none">
              <el-option label="日" value="day" />
              <el-option label="周" value="week" />
              <el-option label="月" value="month" />
            </el-select>
          </div>
        </template>
        <div class="chart-content">
          <div ref="breachTrendChart" class="chart" />
        </div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>客户行业分布</span>
            <el-button type="text" size="small" @click="handleRefresh">
              <Refresh /> 刷新
            </el-button>
          </div>
        </template>
        <div class="chart-content">
          <div ref="industryDistributionChart" class="chart" />
        </div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>客户状态分布</span>
            <el-button type="text" size="small" @click="handleRefresh" style="display: none">
              <Refresh /> 刷新
            </el-button>
          </div>
        </template>
        <div class="chart-content">
          <div ref="statusDistributionChart" class="chart" />
        </div>
      </el-card>
    </div>

    <!-- 最新数据表格 -->
    <el-card class="latest-data-card">
      <template #header>
        <div class="table-header">
          <span>最近违约记录</span>
          <el-button type="primary" size="small" @click="viewAllBreaches">查看全部</el-button>
        </div>
      </template>
      
      <el-table
        :data="latestBreaches"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="记录ID" width="80" align="center" />
        <el-table-column prop="customerName" label="客户名称" width="180" />
        <el-table-column prop="breachReason" label="违约原因" width="250" />
        <el-table-column prop="severity" label="严重程度" width="100" align="center">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.severity === 'HIGH'">高</el-tag>
            <el-tag type="warning" v-else-if="scope.row.severity === 'MEDIUM'">中</el-tag>
            <el-tag type="info" v-else-if="scope.row.severity === 'LOW'">低</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="occurTime" label="发生时间" width="180" align="center" />
        <el-table-column prop="handler" label="处理人" width="100" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="text" @click="viewBreachDetail(scope.row.id)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import type { BreachStatistics, BreachRecordDTO } from '@/types'

// 状态数据
const loading = ref(false)
const timeRange = ref('month')
const dateRange = ref<[string, string] | null>(null)
const chartTimeUnit = ref('day')

// 统计数据
const statistics = reactive<BreachStatistics>({
  totalCustomers: 0,
  totalBreaches: 0,
  totalRebirths: 0,
  warningCustomers: 0,
  blockedCustomers: 0,
  avgBreaches: 0,
  customerGrowth: 0,
  breachGrowth: 0,
  rebirthGrowth: 0,
  warningGrowth: 0,
  blockedGrowth: 0,
  avgBreachChange: 0
})

// 最新违约记录
const latestBreaches = ref<BreachRecordDTO[]>([])

// 图表实例
const customerTrendChart = ref<HTMLElement>()
const breachTrendChart = ref<HTMLElement>()
const industryDistributionChart = ref<HTMLElement>()
const statusDistributionChart = ref<HTMLElement>()

let customerTrendInstance: echarts.ECharts | null = null
let breachTrendInstance: echarts.ECharts | null = null
let industryDistributionInstance: echarts.ECharts | null = null
let statusDistributionInstance: echarts.ECharts | null = null

// 模拟获取统计数据
const fetchStatistics = async () => {
  loading.value = true
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 在实际项目中，这里应该调用API获取统计数据
    // const response = await statisticsApi.getOverviewStatistics(timeRange.value, dateRange.value)
    
    // 使用模拟数据
    statistics.totalCustomers = 128
    statistics.totalBreaches = 56
    statistics.totalRebirths = 12
    statistics.warningCustomers = 23
    statistics.blockedCustomers = 8
    statistics.avgBreaches = 1.35
    statistics.customerGrowth = 15.2
    statistics.breachGrowth = -8.7
    statistics.rebirthGrowth = 25.0
    statistics.warningGrowth = 10.8
    statistics.blockedGrowth = -4.2
    statistics.avgBreachChange = -2.5
    
    // 获取最新违约记录
    // const breachesResponse = await breachApi.getLatestBreaches(10)
    // 使用模拟数据
    latestBreaches.value = [
      { id: 101, customerName: '北京星辰科技有限公司', breachReason: '合同交付延迟', severity: 'HIGH', occurTime: '2023-07-20 14:30:00', handler: '张三' },
      { id: 102, customerName: '上海远景贸易有限公司', breachReason: '付款不及时', severity: 'MEDIUM', occurTime: '2023-07-18 10:15:00', handler: '李四' },
      { id: 103, customerName: '广州恒远物流有限公司', breachReason: '服务质量不达标', severity: 'MEDIUM', occurTime: '2023-07-15 16:45:00', handler: '王五' },
      { id: 104, customerName: '南京智慧城市建设有限公司', breachReason: '项目变更未及时通知', severity: 'LOW', occurTime: '2023-07-12 09:30:00', handler: '赵六' },
      { id: 105, customerName: '武汉长江环保科技有限公司', breachReason: '合同条款执行不力', severity: 'HIGH', occurTime: '2023-07-10 15:20:00', handler: '钱七' }
    ]
  } catch (error) {
    ElMessage.error('获取统计数据失败')
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化图表
const initCharts = () => {
  // 客户增长趋势图
  if (customerTrendChart.value) {
    customerTrendInstance = echarts.init(customerTrendChart.value)
  }
  
  // 违约记录趋势图
  if (breachTrendChart.value) {
    breachTrendInstance = echarts.init(breachTrendChart.value)
  }
  
  // 客户行业分布图
  if (industryDistributionChart.value) {
    industryDistributionInstance = echarts.init(industryDistributionChart.value)
  }
  
  // 客户状态分布图
  if (statusDistributionChart.value) {
    statusDistributionInstance = echarts.init(statusDistributionChart.value)
  }
  
  // 更新图表数据
  updateCharts()
  
  // 监听窗口大小变化，自适应图表
  window.addEventListener('resize', () => {
    customerTrendInstance?.resize()
    breachTrendInstance?.resize()
    industryDistributionInstance?.resize()
    statusDistributionInstance?.resize()
  })
}

// 更新图表数据
const updateCharts = () => {
  // 客户增长趋势图配置
  const customerTrendOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['新增客户', '流失客户']
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
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增客户',
        type: 'line',
        stack: '总量',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [15, 23, 18, 25, 20, 16, 22],
        smooth: true
      },
      {
        name: '流失客户',
        type: 'line',
        stack: '总量',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [3, 5, 2, 4, 3, 2, 1],
        smooth: true
      }
    ]
  }
  
  // 违约记录趋势图配置
  const breachTrendOption = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['高风险', '中风险', '低风险']
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
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '高风险',
        type: 'line',
        data: [8, 6, 9, 7, 5, 4, 3],
        smooth: true,
        lineStyle: {
          color: '#f56c6c'
        },
        areaStyle: {
          color: 'rgba(245, 108, 108, 0.1)'
        }
      },
      {
        name: '中风险',
        type: 'line',
        data: [12, 15, 10, 13, 11, 9, 8],
        smooth: true,
        lineStyle: {
          color: '#e6a23c'
        },
        areaStyle: {
          color: 'rgba(230, 162, 60, 0.1)'
        }
      },
      {
        name: '低风险',
        type: 'line',
        data: [5, 8, 6, 4, 7, 6, 5],
        smooth: true,
        lineStyle: {
          color: '#67c23a'
        },
        areaStyle: {
          color: 'rgba(103, 194, 58, 0.1)'
        }
      }
    ]
  }
  
  // 客户行业分布图配置
  const industryDistributionOption = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '客户行业分布',
        type: 'pie',
        radius: '60%',
        data: [
          { value: 35, name: '科技' },
          { value: 25, name: '贸易' },
          { value: 20, name: '物流' },
          { value: 15, name: '金融' },
          { value: 10, name: '医疗' },
          { value: 8, name: '环保' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  // 客户状态分布图配置
  const statusDistributionOption = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '客户状态分布',
        type: 'pie',
        radius: '60%',
        data: [
          { value: 97, name: '正常', itemStyle: { color: '#67c23a' } },
          { value: 23, name: '预警', itemStyle: { color: '#e6a23c' } },
          { value: 8, name: '冻结', itemStyle: { color: '#f56c6c' } }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  // 设置图表配置
  customerTrendInstance?.setOption(customerTrendOption)
  breachTrendInstance?.setOption(breachTrendOption)
  industryDistributionInstance?.setOption(industryDistributionOption)
  statusDistributionInstance?.setOption(statusDistributionOption)
}

// 时间范围变化处理
const handleTimeRangeChange = () => {
  fetchStatistics()
  // 如果不是自定义时间范围，清空日期范围
  if (timeRange.value !== 'custom') {
    dateRange.value = null
  }
}

// 自定义日期范围变化处理
const handleDateRangeChange = () => {
  if (dateRange.value) {
    fetchStatistics()
  }
}

// 刷新数据
const handleRefresh = () => {
  fetchStatistics()
  updateCharts()
}

// 查看全部违约记录
const viewAllBreaches = () => {
  ElMessage.info('跳转到违约记录列表')
  // 在实际项目中，这里应该跳转到违约记录列表页面
  // router.push('/breach/records')
}

// 查看违约详情
const viewBreachDetail = (breachId: number) => {
  ElMessage.info(`查看违约记录ID: ${breachId} 的详情`)
  // 在实际项目中，这里应该跳转到违约记录详情页面
  // router.push(`/breach/detail/${breachId}`)
}

// 监听组件挂载
onMounted(() => {
  fetchStatistics()
  // 延迟初始化图表，确保DOM已经渲染完成
  setTimeout(() => {
    initCharts()
  }, 100)
})

// 监听统计数据变化，更新图表
watch(statistics, () => {
  updateCharts()
}, { deep: true })
</script>

<style scoped>
.statistics-overview-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin-bottom: 8px;
  color: #303133;
}

.page-header .subtitle {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.time-filter-container {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.custom-date-picker {
  display: inline-flex;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.stat-title {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-change {
  font-size: 12px;
}

.stat-change.positive {
  color: #67c23a;
}

.stat-change.negative {
  color: #f56c6c;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  height: 300px;
  display: flex;
  flex-direction: column;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.chart-content {
  flex: 1;
  padding: 10px;
  position: relative;
}

.chart {
  width: 100%;
  height: 100%;
}

.latest-data-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-container {
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  }
}

@media (max-width: 768px) {
  .statistics-overview-container {
    padding: 10px;
  }
  
  .time-filter-container {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .charts-container {
    grid-template-columns: 1fr;
  }
  
  .chart-card {
    height: 250px;
  }
  
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
