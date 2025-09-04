<template>
  <div class="industry-analysis-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>行业分析</h2>
      <p class="subtitle">分析不同行业客户的违约情况和风险特征</p>
    </div>

    <!-- 时间筛选器 -->
    <div class="time-filter-container">
      <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
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

    <!-- 行业统计概览卡片 -->
    <div class="industry-summary-cards">
      <el-card class="summary-card">
        <div class="summary-card-content">
          <div class="summary-title">行业数量</div>
          <div class="summary-value">{{ industryStats.totalIndustries }}</div>
        </div>
      </el-card>
      
      <el-card class="summary-card">
        <div class="summary-card-content">
          <div class="summary-title">最高违约率行业</div>
          <div class="summary-value">{{ industryStats.highestBreachIndustry }}</div>
          <div class="summary-subtitle">{{ industryStats.highestBreachRate }}%</div>
        </div>
      </el-card>
      
      <el-card class="summary-card">
        <div class="summary-card-content">
          <div class="summary-title">平均行业违约率</div>
          <div class="summary-value">{{ industryStats.avgBreachRate }}%</div>
          <div class="summary-subtitle">{{ industryStats.avgBreachRateChange > 0 ? '↑' : '↓' }} {{ Math.abs(industryStats.avgBreachRateChange) }}%</div>
        </div>
      </el-card>
      
      <el-card class="summary-card">
        <div class="summary-card-content">
          <div class="summary-title">最高风险行业</div>
          <div class="summary-value">{{ industryStats.highestRiskIndustry }}</div>
          <div class="summary-subtitle">{{ industryStats.highestRiskScore }}</div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 行业客户分布图表 -->
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>行业客户分布</span>
            <el-select v-model="chartType" size="small" @change="updateCharts">
              <el-option label="饼图" value="pie" />
              <el-option label="柱状图" value="bar" />
            </el-select>
          </div>
        </template>
        <div class="chart-content">
          <div ref="industryDistributionChart" class="chart" />
        </div>
      </el-card>

      <!-- 行业违约率图表 -->
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>行业违约率对比</span>
            <el-button type="text" size="small" @click="handleRefresh">
              <Refresh /> 刷新
            </el-button>
          </div>
        </template>
        <div class="chart-content">
          <div ref="industryBreachRateChart" class="chart" />
        </div>
      </el-card>

      <!-- 行业风险趋势图表 -->
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>行业风险趋势</span>
            <el-select v-model="selectedIndustry" size="small" @change="updateTrendChart">
              <el-option label="全部" value="all" />
              <el-option label="科技" value="TECH" />
              <el-option label="贸易" value="TRADE" />
              <el-option label="物流" value="LOGISTICS" />
              <el-option label="金融" value="FINANCE" />
              <el-option label="医疗" value="MEDICAL" />
            </el-select>
          </div>
        </template>
        <div class="chart-content">
          <div ref="industryTrendChart" class="chart" />
        </div>
      </el-card>

      <!-- 行业风险评分雷达图 -->
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>行业风险评分</span>
            <el-button type="text" size="small" @click="handleRefresh" style="display: none">
              <Refresh /> 刷新
            </el-button>
          </div>
        </template>
        <div class="chart-content">
          <div ref="industryRiskRadarChart" class="chart" />
        </div>
      </el-card>
    </div>

    <!-- 行业详情表格 -->
    <el-card class="industry-details-card">
      <template #header>
        <div class="table-header">
          <span>行业详情</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索行业名称"
            prefix-icon="Search"
            size="small"
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="industryDetails"
        style="width: 100%"
        border
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="industry" label="行业名称" width="120">
          <template #default="scope">
            <el-tag>{{ getIndustryLabel(scope.row.industry) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="customerCount" label="客户数量" width="120" align="center" />
        <el-table-column prop="breachCount" label="违约记录" width="120" align="center" />
        <el-table-column prop="breachRate" label="违约率" width="120" align="center">
          <template #default="scope">
            <span :class="{ 'text-danger': scope.row.breachRate > 20 }">
              {{ scope.row.breachRate }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="warningCustomers" label="预警客户" width="120" align="center" />
        <el-table-column prop="blockedCustomers" label="冻结客户" width="120" align="center" />
        <el-table-column prop="avgBreachSeverity" label="平均违约严重度" width="150" align="center">
          <template #default="scope">
            <div class="severity-bar-container">
              <div class="severity-bar" :style="{ width: scope.row.avgBreachSeverity * 10 + '%' }" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="riskScore" label="风险评分" width="120" align="center">
          <template #default="scope">
            <div class="risk-score">
              <el-rate
                v-model="scope.row.riskScore"
                :max="100"
                disabled
                show-score
                score-template="{{ score }}"
                style="font-size: 12px"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="text" @click="viewIndustryAnalysis(scope.row.industry)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 状态数据
const loading = ref(false)
const timeRange = ref('quarter')
const dateRange = ref<[string, string] | null>(null)
const chartType = ref('pie')
const selectedIndustry = ref('all')
const searchKeyword = ref('')

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 行业统计数据
const industryStats = reactive({
  totalIndustries: 6,
  highestBreachIndustry: '物流',
  highestBreachRate: 28.5,
  avgBreachRate: 15.2,
  avgBreachRateChange: -2.3,
  highestRiskIndustry: '贸易',
  highestRiskScore: 78
})

// 行业详情数据
const industryDetails = ref<IndustryDetail[]>([])

// 图表实例
const industryDistributionChart = ref<HTMLElement>()
const industryBreachRateChart = ref<HTMLElement>()
const industryTrendChart = ref<HTMLElement>()
const industryRiskRadarChart = ref<HTMLElement>()

let industryDistributionInstance: echarts.ECharts | null = null
let industryBreachRateInstance: echarts.ECharts | null = null
let industryTrendInstance: echarts.ECharts | null = null
let industryRiskRadarInstance: echarts.ECharts | null = null

// 行业详情类型定义
interface IndustryDetail {
  industry: string
  customerCount: number
  breachCount: number
  breachRate: number
  warningCustomers: number
  blockedCustomers: number
  avgBreachSeverity: number
  riskScore: number
}

// 模拟获取行业详情数据
const fetchIndustryDetails = async () => {
  loading.value = true
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 在实际项目中，这里应该调用API获取数据
    // const response = await statisticsApi.getIndustryDetails(pagination.currentPage, pagination.pageSize, searchKeyword.value)
    
    // 使用模拟数据
    const mockData: IndustryDetail[] = [
      { industry: 'TECH', customerCount: 35, breachCount: 5, breachRate: 14.3, warningCustomers: 4, blockedCustomers: 1, avgBreachSeverity: 5.2, riskScore: 45 },
      { industry: 'TRADE', customerCount: 25, breachCount: 6, breachRate: 24.0, warningCustomers: 5, blockedCustomers: 2, avgBreachSeverity: 6.8, riskScore: 78 },
      { industry: 'LOGISTICS', customerCount: 20, breachCount: 6, breachRate: 28.5, warningCustomers: 4, blockedCustomers: 3, avgBreachSeverity: 7.5, riskScore: 72 },
      { industry: 'FINANCE', customerCount: 15, breachCount: 2, breachRate: 13.3, warningCustomers: 2, blockedCustomers: 0, avgBreachSeverity: 4.5, riskScore: 42 },
      { industry: 'MEDICAL', customerCount: 10, breachCount: 1, breachRate: 10.0, warningCustomers: 1, blockedCustomers: 0, avgBreachSeverity: 3.8, riskScore: 30 },
      { industry: 'ENVIRONMENT', customerCount: 8, breachCount: 1, breachRate: 12.5, warningCustomers: 1, blockedCustomers: 0, avgBreachSeverity: 4.2, riskScore: 35 }
    ]
    
    // 根据搜索关键词过滤数据
    let filteredData = [...mockData]
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      const industryMap: Record<string, string> = {
        'TECH': '科技',
        'TRADE': '贸易',
        'LOGISTICS': '物流',
        'FINANCE': '金融',
        'MEDICAL': '医疗',
        'ENVIRONMENT': '环保'
      }
      filteredData = filteredData.filter(item => 
        industryMap[item.industry]?.toLowerCase().includes(keyword)
      )
    }
    
    // 计算分页数据
    const total = filteredData.length
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    const paginatedData = filteredData.slice(start, end)
    
    // 更新表格数据和分页信息
    industryDetails.value = paginatedData
    pagination.total = total
  } catch (error) {
    ElMessage.error('获取行业详情失败')
    console.error('获取行业详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取行业标签
const getIndustryLabel = (industry: string): string => {
  const industryMap: Record<string, string> = {
    'TECH': '科技',
    'TRADE': '贸易',
    'LOGISTICS': '物流',
    'FINANCE': '金融',
    'MEDICAL': '医疗',
    'ENVIRONMENT': '环保'
  }
  return industryMap[industry] || industry
}

// 初始化图表
const initCharts = () => {
  // 行业分布图表
  if (industryDistributionChart.value) {
    industryDistributionInstance = echarts.init(industryDistributionChart.value)
  }
  
  // 行业违约率图表
  if (industryBreachRateChart.value) {
    industryBreachRateInstance = echarts.init(industryBreachRateChart.value)
  }
  
  // 行业风险趋势图表
  if (industryTrendChart.value) {
    industryTrendInstance = echarts.init(industryTrendChart.value)
  }
  
  // 行业风险评分雷达图
  if (industryRiskRadarChart.value) {
    industryRiskRadarInstance = echarts.init(industryRiskRadarChart.value)
  }
  
  // 更新图表数据
  updateCharts()
  
  // 监听窗口大小变化，自适应图表
  window.addEventListener('resize', () => {
    industryDistributionInstance?.resize()
    industryBreachRateInstance?.resize()
    industryTrendInstance?.resize()
    industryRiskRadarInstance?.resize()
  })
}

// 更新图表数据
const updateCharts = () => {
  updateDistributionChart()
  updateBreachRateChart()
  updateTrendChart()
  updateRiskRadarChart()
}

// 更新行业分布图表
const updateDistributionChart = () => {
  const industryData = [
    { value: 35, name: '科技' },
    { value: 25, name: '贸易' },
    { value: 20, name: '物流' },
    { value: 15, name: '金融' },
    { value: 10, name: '医疗' },
    { value: 8, name: '环保' }
  ]
  
  if (chartType.value === 'pie') {
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 10,
        data: industryData.map(item => item.name)
      },
      series: [
        {
          name: '客户数量',
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
            }
          },
          labelLine: {
            show: false
          },
          data: industryData
        }
      ]
    }
    industryDistributionInstance?.setOption(option)
  } else {
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: industryData.map(item => item.name),
        axisTick: {
          alignWithLabel: true
        }
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '客户数量',
          type: 'bar',
          barWidth: '60%',
          data: industryData
        }
      ]
    }
    industryDistributionInstance?.setOption(option)
  }
}

// 更新行业违约率图表
const updateBreachRateChart = () => {
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['违约率', '预警率']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    yAxis: {
      type: 'category',
      data: ['科技', '贸易', '物流', '金融', '医疗', '环保']
    },
    series: [
      {
        name: '违约率',
        type: 'bar',
        data: [14.3, 24.0, 28.5, 13.3, 10.0, 12.5],
        itemStyle: {
          color: '#f56c6c'
        }
      },
      {
        name: '预警率',
        type: 'bar',
        data: [11.4, 20.0, 20.0, 13.3, 10.0, 12.5],
        itemStyle: {
          color: '#e6a23c'
        }
      }
    ]
  }
  industryBreachRateInstance?.setOption(option)
}

// 更新行业风险趋势图表
const updateTrendChart = () => {
  const monthData = ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
  
  if (selectedIndustry.value === 'all' || !selectedIndustry.value) {
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['科技', '贸易', '物流', '金融']
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
        data: monthData
      },
      yAxis: {
        type: 'value',
        max: 100
      },
      series: [
        {
          name: '科技',
          type: 'line',
          stack: '总量',
          data: [35, 38, 42, 45, 43, 46, 45],
          smooth: true
        },
        {
          name: '贸易',
          type: 'line',
          stack: '总量',
          data: [65, 68, 72, 75, 78, 76, 78],
          smooth: true
        },
        {
          name: '物流',
          type: 'line',
          stack: '总量',
          data: [60, 65, 70, 72, 70, 72, 72],
          smooth: true
        },
        {
          name: '金融',
          type: 'line',
          stack: '总量',
          data: [30, 32, 35, 38, 36, 40, 42],
          smooth: true
        }
      ]
    }
    industryTrendInstance?.setOption(option)
  } else {
    const industryMap: Record<string, number[]> = {
      'TECH': [35, 38, 42, 45, 43, 46, 45],
      'TRADE': [65, 68, 72, 75, 78, 76, 78],
      'LOGISTICS': [60, 65, 70, 72, 70, 72, 72],
      'FINANCE': [30, 32, 35, 38, 36, 40, 42],
      'MEDICAL': [25, 28, 30, 32, 30, 32, 30],
      'ENVIRONMENT': [30, 32, 35, 38, 36, 35, 35]
    }
    
    const option = {
      tooltip: {
        trigger: 'axis'
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
        data: monthData
      },
      yAxis: {
        type: 'value',
        max: 100
      },
      series: [
        {
          name: getIndustryLabel(selectedIndustry.value),
          type: 'line',
          stack: '总量',
          data: industryMap[selectedIndustry.value] || [],
          smooth: true,
          areaStyle: {},
          emphasis: {
            focus: 'series'
          }
        }
      ]
    }
    industryTrendInstance?.setOption(option)
  }
}

// 更新行业风险评分雷达图
const updateRiskRadarChart = () => {
  const option = {
    tooltip: {},
    legend: {
      data: ['科技', '贸易', '物流']
    },
    radar: {
      indicator: [
        { name: '违约概率', max: 100 },
        { name: '严重程度', max: 100 },
        { name: '违约频率', max: 100 },
        { name: '恢复难度', max: 100 },
        { name: '损失规模', max: 100 },
        { name: '市场影响', max: 100 }
      ]
    },
    series: [
      {
        name: '行业风险评分',
        type: 'radar',
        data: [
          {
            value: [45, 40, 35, 30, 35, 40],
            name: '科技',
            areaStyle: {
              color: 'rgba(103, 194, 58, 0.2)'
            },
            lineStyle: {
              color: '#67c23a'
            }
          },
          {
            value: [75, 80, 85, 70, 75, 65],
            name: '贸易',
            areaStyle: {
              color: 'rgba(245, 108, 108, 0.2)'
            },
            lineStyle: {
              color: '#f56c6c'
            }
          },
          {
            value: [70, 75, 80, 75, 65, 60],
            name: '物流',
            areaStyle: {
              color: 'rgba(230, 162, 60, 0.2)'
            },
            lineStyle: {
              color: '#e6a23c'
            }
          }
        ]
      }
    ]
  }
  industryRiskRadarInstance?.setOption(option)
}

// 时间范围变化处理
const handleTimeRangeChange = () => {
  fetchIndustryDetails()
  updateCharts()
  // 如果不是自定义时间范围，清空日期范围
  if (timeRange.value !== 'custom') {
    dateRange.value = null
  }
}

// 自定义日期范围变化处理
const handleDateRangeChange = () => {
  if (dateRange.value) {
    fetchIndustryDetails()
    updateCharts()
  }
}

// 刷新数据
const handleRefresh = () => {
  fetchIndustryDetails()
  updateCharts()
}

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  fetchIndustryDetails()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  fetchIndustryDetails()
}

// 当前页码变化
const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  fetchIndustryDetails()
}

// 查看行业详情分析
const viewIndustryAnalysis = (industry: string) => {
  ElMessage.info(`查看${getIndustryLabel(industry)}行业的详细分析`)
  // 在实际项目中，这里应该跳转到更详细的行业分析页面
  // router.push(`/statistics/industry/${industry}`)
}

// 监听组件挂载
onMounted(() => {
  fetchIndustryDetails()
  // 延迟初始化图表，确保DOM已经渲染完成
  setTimeout(() => {
    initCharts()
  }, 100)
})
</script>

<style scoped>
.industry-analysis-container {
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

.industry-summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.summary-card {
  transition: all 0.3s ease;
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.summary-card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.summary-title {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.summary-subtitle {
  font-size: 12px;
  color: #909399;
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

.industry-details-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
  margin-bottom: 10px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 严重度进度条样式 */
.severity-bar-container {
  width: 100%;
  height: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}

.severity-bar {
  height: 100%;
  background-color: #e6a23c;
  transition: width 0.3s ease;
}

/* 风险评分样式 */
.risk-score {
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-container {
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  }
}

@media (max-width: 768px) {
  .industry-analysis-container {
    padding: 10px;
  }
  
  .time-filter-container {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .industry-summary-cards {
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
