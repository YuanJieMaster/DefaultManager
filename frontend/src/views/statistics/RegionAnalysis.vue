<template>
  <div class="region-analysis-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>区域分析</h2>
      <p class="subtitle">分析不同地区客户的分布和违约风险情况</p>
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

    <!-- 区域统计概览卡片 -->
    <div class="region-summary-cards">
      <el-card class="summary-card">
        <div class="summary-card-content">
          <div class="summary-title">区域数量</div>
          <div class="summary-value">{{ regionStats.totalRegions }}</div>
        </div>
      </el-card>

      <el-card class="summary-card">
        <div class="summary-card-content">
          <div class="summary-title">客户最多区域</div>
          <div class="summary-value">{{ regionStats.topCustomerRegion }}</div>
          <div class="summary-subtitle">{{ regionStats.topCustomerCount }}家客户</div>
        </div>
      </el-card>

      <el-card class="summary-card">
        <div class="summary-card-content">
          <div class="summary-title">最高违约率区域</div>
          <div class="summary-value">{{ regionStats.highestBreachRegion }}</div>
          <div class="summary-subtitle">{{ regionStats.highestBreachRate }}%</div>
        </div>
      </el-card>

      <el-card class="summary-card">
        <div class="summary-card-content">
          <div class="summary-title">最高风险区域</div>
          <div class="summary-value">{{ regionStats.highestRiskRegion }}</div>
          <div class="summary-subtitle">{{ regionStats.highestRiskScore }}</div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 区域客户分布地图 -->
      <el-card class="chart-card" style="grid-column: 1 / -1;">
        <template #header>
          <div class="chart-header">
            <span>区域客户分布地图</span>
            <div class="map-controls">
              <el-select v-model="mapType" size="small" @change="updateMapChart">
                <el-option label="客户数量" value="customer" />
                <el-option label="违约率" value="breachRate" />
                <el-option label="风险评分" value="riskScore" />
              </el-select>
              <el-button type="text" size="small" @click="handleRefresh">
                <Refresh /> 刷新
              </el-button>
            </div>
          </div>
        </template>
        <div class="chart-content">
          <div ref="regionMapChart" class="map-chart" />
        </div>
      </el-card>

      <!-- 区域客户分布柱状图 -->
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>区域客户数量TOP10</span>
            <el-select v-model="regionDisplayCount" size="small" @change="updateDistributionChart">
              <el-option label="TOP5" value="5" />
              <el-option label="TOP10" value="10" />
              <el-option label="TOP15" value="15" />
            </el-select>
          </div>
        </template>
        <div class="chart-content">
          <div ref="regionDistributionChart" class="chart" />
        </div>
      </el-card>

      <!-- 区域违约率趋势图 -->
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>区域违约率对比</span>
            <el-select v-model="selectedRegions" size="small" multiple collapse-tags @change="updateBreachRateChart">
              <el-option label="北京" value="beijing" />
              <el-option label="上海" value="shanghai" />
              <el-option label="广州" value="guangzhou" />
              <el-option label="深圳" value="shenzhen" />
              <el-option label="杭州" value="hangzhou" />
            </el-select>
          </div>
        </template>
        <div class="chart-content">
          <div ref="regionBreachRateChart" class="chart" />
        </div>
      </el-card>

      <!-- 区域风险评分分布图 -->
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>区域风险评分分布</span>
            <el-button type="text" size="small" @click="handleRefresh">
              <Refresh /> 刷新
            </el-button>
          </div>
        </template>
        <div class="chart-content">
          <div ref="regionRiskDistributionChart" class="chart" />
        </div>
      </el-card>

      <!-- 区域客户增长率图 -->
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>区域客户增长率</span>
            <el-radio-group v-model="growthRatePeriod" size="small" @change="updateGrowthRateChart">
              <el-radio-button label="month">月增长</el-radio-button>
              <el-radio-button label="quarter">季度增长</el-radio-button>
            </el-radio-group>
          </div>
        </template>
        <div class="chart-content">
          <div ref="regionGrowthRateChart" class="chart" />
        </div>
      </el-card>
    </div>

    <!-- 区域详情表格 -->
    <el-card class="region-details-card">
      <template #header>
        <div class="table-header">
          <span>区域详情</span>
          <div class="table-controls">
            <el-select v-model="regionLevel" size="small" @change="handleRegionLevelChange">
              <el-option label="全部级别" value="all" />
              <el-option label="省级" value="province" />
              <el-option label="市级" value="city" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索区域名称"
              prefix-icon="Search"
              size="small"
              style="width: 200px; margin-left: 10px"
              @keyup.enter="handleSearch"
            />
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="regionDetails"
        style="width: 100%"
        border
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="regionName" label="区域名称" width="150">
          <template #default="scope">
            <el-tag>{{ scope.row.regionName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="regionLevel" label="区域级别" width="100" align="center">
          <template #default="scope">
            <el-tag size="small" :type="scope.row.regionLevel === 'province' ? 'primary' : 'success'">
              {{ scope.row.regionLevel === 'province' ? '省级' : '市级' }}
            </el-tag>
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
        <el-table-column prop="customerGrowthRate" label="客户增长率" width="120" align="center">
          <template #default="scope">
            <span :class="{ 'text-success': scope.row.customerGrowthRate > 0, 'text-danger': scope.row.customerGrowthRate < 0 }">
              {{ scope.row.customerGrowthRate > 0 ? '+' : '' }}{{ scope.row.customerGrowthRate }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="text" @click="viewRegionAnalysis(scope.row.regionName)">详情</el-button>
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

// 状态数据
const loading = ref(false)
const timeRange = ref('quarter')
const dateRange = ref<[string, string] | null>(null)
const mapType = ref('customer')
const regionDisplayCount = ref('10')
const selectedRegions = ref(['beijing', 'shanghai', 'guangzhou', 'shenzhen'])
const searchKeyword = ref('')
const regionLevel = ref('all')
const growthRatePeriod = ref('month')

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 添加地图加载方法
const loadChinaMap = async () => {
  try {
    const response = await fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json');
    const chinaJson = await response.json();
    echarts.registerMap('china', chinaJson);
    return true;
  } catch (error) {
    console.error('加载地图失败:', error);
    return false;
  }
}

// 区域统计数据
const regionStats = reactive({
  totalRegions: 34,
  topCustomerRegion: '上海',
  topCustomerCount: 85,
  highestBreachRegion: '广州',
  highestBreachRate: 26.5,
  highestRiskRegion: '深圳',
  highestRiskScore: 75
})

// 区域详情数据
const regionDetails = ref<RegionDetail[]>([])

// 图表实例
const regionMapChart = ref<HTMLElement>()
const regionDistributionChart = ref<HTMLElement>()
const regionBreachRateChart = ref<HTMLElement>()
const regionRiskDistributionChart = ref<HTMLElement>()
const regionGrowthRateChart = ref<HTMLElement>()

let regionMapInstance: echarts.ECharts | null = null
let regionDistributionInstance: echarts.ECharts | null = null
let regionBreachRateInstance: echarts.ECharts | null = null
let regionRiskDistributionInstance: echarts.ECharts | null = null
let regionGrowthRateInstance: echarts.ECharts | null = null

// 区域详情类型定义
interface RegionDetail {
  regionName: string
  regionLevel: string
  customerCount: number
  breachCount: number
  breachRate: number
  warningCustomers: number
  blockedCustomers: number
  avgBreachSeverity: number
  riskScore: number
  customerGrowthRate: number
}

// 模拟获取区域详情数据
const fetchRegionDetails = async () => {
  loading.value = true
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 800))

    // 在实际项目中，这里应该调用API获取数据
    // const response = await statisticsApi.getRegionDetails(pagination.currentPage, pagination.pageSize, searchKeyword.value, regionLevel.value)

    // 使用模拟数据
    const mockData: RegionDetail[] = [
      { regionName: '北京', regionLevel: 'province', customerCount: 78, breachCount: 12, breachRate: 15.4, warningCustomers: 8, blockedCustomers: 2, avgBreachSeverity: 5.8, riskScore: 65, customerGrowthRate: 3.2 },
      { regionName: '上海', regionLevel: 'province', customerCount: 85, breachCount: 14, breachRate: 16.5, warningCustomers: 9, blockedCustomers: 3, avgBreachSeverity: 6.2, riskScore: 68, customerGrowthRate: 4.5 },
      { regionName: '广东', regionLevel: 'province', customerCount: 120, breachCount: 25, breachRate: 20.8, warningCustomers: 15, blockedCustomers: 5, avgBreachSeverity: 6.5, riskScore: 72, customerGrowthRate: 5.8 },
      { regionName: '浙江', regionLevel: 'province', customerCount: 65, breachCount: 9, breachRate: 13.8, warningCustomers: 6, blockedCustomers: 1, avgBreachSeverity: 4.8, riskScore: 58, customerGrowthRate: 4.2 },
      { regionName: '江苏', regionLevel: 'province', customerCount: 72, breachCount: 10, breachRate: 13.9, warningCustomers: 7, blockedCustomers: 2, avgBreachSeverity: 5.2, riskScore: 60, customerGrowthRate: 3.8 },
      { regionName: '北京', regionLevel: 'city', customerCount: 78, breachCount: 12, breachRate: 15.4, warningCustomers: 8, blockedCustomers: 2, avgBreachSeverity: 5.8, riskScore: 65, customerGrowthRate: 3.2 },
      { regionName: '上海', regionLevel: 'city', customerCount: 85, breachCount: 14, breachRate: 16.5, warningCustomers: 9, blockedCustomers: 3, avgBreachSeverity: 6.2, riskScore: 68, customerGrowthRate: 4.5 },
      { regionName: '广州', regionLevel: 'city', customerCount: 65, breachCount: 17, breachRate: 26.5, warningCustomers: 12, blockedCustomers: 4, avgBreachSeverity: 7.2, riskScore: 75, customerGrowthRate: 3.5 },
      { regionName: '深圳', regionLevel: 'city', customerCount: 72, breachCount: 18, breachRate: 25.0, warningCustomers: 13, blockedCustomers: 4, avgBreachSeverity: 7.0, riskScore: 75, customerGrowthRate: 5.2 },
      { regionName: '杭州', regionLevel: 'city', customerCount: 45, breachCount: 6, breachRate: 13.3, warningCustomers: 4, blockedCustomers: 1, avgBreachSeverity: 4.5, riskScore: 55, customerGrowthRate: 6.8 },
      { regionName: '南京', regionLevel: 'city', customerCount: 38, breachCount: 5, breachRate: 13.2, warningCustomers: 3, blockedCustomers: 0, avgBreachSeverity: 4.2, riskScore: 52, customerGrowthRate: 4.1 },
      { regionName: '成都', regionLevel: 'city', customerCount: 32, breachCount: 4, breachRate: 12.5, warningCustomers: 3, blockedCustomers: 0, avgBreachSeverity: 4.0, riskScore: 48, customerGrowthRate: 5.5 },
      { regionName: '武汉', regionLevel: 'city', customerCount: 28, breachCount: 3, breachRate: 10.7, warningCustomers: 2, blockedCustomers: 0, avgBreachSeverity: 3.8, riskScore: 45, customerGrowthRate: 3.9 },
      { regionName: '西安', regionLevel: 'city', customerCount: 22, breachCount: 2, breachRate: 9.1, warningCustomers: 1, blockedCustomers: 0, avgBreachSeverity: 3.5, riskScore: 42, customerGrowthRate: 4.3 },
      { regionName: '重庆', regionLevel: 'city', customerCount: 25, breachCount: 3, breachRate: 12.0, warningCustomers: 2, blockedCustomers: 0, avgBreachSeverity: 3.8, riskScore: 46, customerGrowthRate: 5.1 }
    ]

    // 根据搜索关键词和区域级别过滤数据
    let filteredData = [...mockData]

    if (regionLevel.value !== 'all') {
      filteredData = filteredData.filter(item => item.regionLevel === regionLevel.value)
    }

    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredData = filteredData.filter(item =>
        item.regionName.toLowerCase().includes(keyword)
      )
    }

    // 计算分页数据
    const total = filteredData.length
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    const paginatedData = filteredData.slice(start, end)

    // 更新表格数据和分页信息
    regionDetails.value = paginatedData
    pagination.total = total
  } catch (error) {
    ElMessage.error('获取区域详情失败')
    console.error('获取区域详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化图表
const initCharts = () => {
  // 区域地图图表
  if (regionMapChart.value) {
    regionMapInstance = echarts.init(regionMapChart.value)
  }

  // 区域分布图表
  if (regionDistributionChart.value) {
    regionDistributionInstance = echarts.init(regionDistributionChart.value)
  }

  // 区域违约率图表
  if (regionBreachRateChart.value) {
    regionBreachRateInstance = echarts.init(regionBreachRateChart.value)
  }

  // 区域风险分布图表
  if (regionRiskDistributionChart.value) {
    regionRiskDistributionInstance = echarts.init(regionRiskDistributionChart.value)
  }

  // 区域客户增长率图表
  if (regionGrowthRateChart.value) {
    regionGrowthRateInstance = echarts.init(regionGrowthRateChart.value)
  }

  // 更新图表数据
  updateCharts()

  // 监听窗口大小变化，自适应图表
  window.addEventListener('resize', () => {
    regionMapInstance?.resize()
    regionDistributionInstance?.resize()
    regionBreachRateInstance?.resize()
    regionRiskDistributionInstance?.resize()
    regionGrowthRateInstance?.resize()
  })
}

// 更新所有图表
const updateCharts = () => {
  updateMapChart()
  updateDistributionChart()
  updateBreachRateChart()
  updateRiskDistributionChart()
  updateGrowthRateChart()
}

// 更新地图图表
const updateMapChart = () => {
  // 模拟地图数据
  const mapData = [
    { name: '北京', value: 78, breachRate: 15.4, riskScore: 65 },
    { name: '上海', value: 85, breachRate: 16.5, riskScore: 68 },
    { name: '广东', value: 120, breachRate: 20.8, riskScore: 72 },
    { name: '浙江', value: 65, breachRate: 13.8, riskScore: 58 },
    { name: '江苏', value: 72, breachRate: 13.9, riskScore: 60 },
    { name: '山东', value: 55, breachRate: 12.5, riskScore: 56 },
    { name: '河南', value: 45, breachRate: 11.8, riskScore: 52 },
    { name: '四川', value: 48, breachRate: 12.0, riskScore: 53 },
    { name: '湖北', value: 42, breachRate: 11.5, riskScore: 51 },
    { name: '福建', value: 40, breachRate: 11.2, riskScore: 50 }
  ]

  // 根据选择的地图类型获取对应的数据
  let mapOptionData = mapData.map(item => ({ name: item.name, value: item.value }))
  let tooltipFormatter = '{b}: {c} 家客户'
  let visualMapMin = 0
  let visualMapMax = 120
  let name = '客户数量'

  if (mapType.value === 'breachRate') {
    mapOptionData = mapData.map(item => ({ name: item.name, value: item.breachRate }))
    tooltipFormatter = '{b}: {c}%'
    visualMapMin = 10
    visualMapMax = 25
    name = '违约率'
  } else if (mapType.value === 'riskScore') {
    mapOptionData = mapData.map(item => ({ name: item.name, value: item.riskScore }))
    tooltipFormatter = '{b}: {c}'
    visualMapMin = 45
    visualMapMax = 80
    name = '风险评分'
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: tooltipFormatter
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: [name]
    },
    visualMap: {
      min: visualMapMin,
      max: visualMapMax,
      text: ['高', '低'],
      realtime: false,
      calculable: true,
      inRange: {
        color: ['lightskyblue', 'yellow', 'orangered']
      }
    },
    series: [
      {
        name: name,
        type: 'map',
        map: 'china',
        emphasis: {
          itemStyle: {
            areaColor: '#323c48'
          },
          label: {
            show: true,
            color: '#fff'
          }
        },
        data: mapOptionData
      }
    ]
  }

  regionMapInstance?.setOption(option)
}

// 更新区域分布图表
const updateDistributionChart = () => {
  // 模拟区域客户数量数据
  const regionData = [
    { name: '上海', value: 85 },
    { name: '广东', value: 82 },
    { name: '北京', value: 78 },
    { name: '江苏', value: 72 },
    { name: '浙江', value: 65 },
    { name: '山东', value: 55 },
    { name: '四川', value: 48 },
    { name: '湖北', value: 42 },
    { name: '福建', value: 40 },
    { name: '河南', value: 38 },
    { name: '湖南', value: 36 },
    { name: '河北', value: 34 },
    { name: '安徽', value: 32 },
    { name: '陕西', value: 30 },
    { name: '重庆', value: 28 }
  ]

  // 根据选择的显示数量截取数据
  const displayCount = parseInt(regionDisplayCount.value, 10)
  const displayData = regionData.slice(0, displayCount)

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
      bottom: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: displayData.map(item => item.name),
      axisLabel: {
        interval: 0,
        rotate: 0
      }
    },
    series: [
      {
        name: '客户数量',
        type: 'bar',
        data: displayData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#67c23a' },
            { offset: 1, color: '#409eff' }
          ])
        },
        label: {
          show: true,
          position: 'right',
          formatter: '{c}'
        }
      }
    ]
  }

  regionDistributionInstance?.setOption(option)
}

// 更新区域违约率图表
const updateBreachRateChart = () => {
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月']

  // 模拟各区域违约率数据
  const regionBreachRates: Record<string, number[]> = {
    'beijing': [12.5, 13.2, 14.1, 14.8, 15.2, 15.4, 15.6],
    'shanghai': [13.8, 14.5, 15.2, 15.8, 16.2, 16.5, 16.8],
    'guangzhou': [20.5, 21.8, 22.5, 23.8, 25.2, 26.5, 27.1],
    'shenzhen': [19.8, 20.5, 21.2, 22.8, 24.1, 25.0, 25.6],
    'hangzhou': [11.5, 12.2, 12.8, 13.2, 13.5, 13.3, 13.8]
  }

  const colors: Record<string, string> = {
    'beijing': '#409eff',
    'shanghai': '#67c23a',
    'guangzhou': '#f56c6c',
    'shenzhen': '#e6a23c',
    'hangzhou': '#909399'
  }

  const regionNames: Record<string, string> = {
    'beijing': '北京',
    'shanghai': '上海',
    'guangzhou': '广州',
    'shenzhen': '深圳',
    'hangzhou': '杭州'
  }

  // 创建系列数据
  const series = selectedRegions.value.map(region => ({
    name: regionNames[region],
    type: 'line',
    data: regionBreachRates[region] || [],
    smooth: true,
    lineStyle: {
      color: colors[region]
    },
    itemStyle: {
      color: colors[region]
    }
  }))

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function (params: any) {
        let result = params[0].axisValue + '<br/>'
        params.forEach((item: any) => {
          result += `${item.marker} ${item.seriesName}: ${item.value}%<br/>`
        })
        return result
      }
    },
    legend: {
      data: selectedRegions.value.map(region => regionNames[region])
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
      data: months
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      },
      min: 0,
      max: 30
    },
    series: series
  }

  regionBreachRateInstance?.setOption(option)
}

// 更新区域风险分布图表
const updateRiskDistributionChart = () => {
  const riskRanges = ['低风险(0-40)', '中低风险(41-60)', '中风险(61-75)', '高风险(76-90)', '极高风险(91-100)']

  // 模拟各风险等级的区域数量
  const riskData = [
    { name: '低风险(0-40)', value: 8 },
    { name: '中低风险(41-60)', value: 15 },
    { name: '中风险(61-75)', value: 9 },
    { name: '高风险(76-90)', value: 2 },
    { name: '极高风险(91-100)', value: 0 }
  ]

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 10,
      left: 'center',
      data: riskRanges
    },
    series: [
      {
        name: '风险等级',
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
        data: riskData,
        color: ['#67c23a', '#e6a23c', '#f56c6c', '#d48265', '#909399']
      }
    ]
  }

  regionRiskDistributionInstance?.setOption(option)
}

// 更新区域客户增长率图表
const updateGrowthRateChart = () => {
  // 模拟各区域增长率数据
  const regionGrowthData = [
    { name: '北京', monthGrowth: 3.2, quarterGrowth: 9.5 },
    { name: '上海', monthGrowth: 4.5, quarterGrowth: 13.2 },
    { name: '广州', monthGrowth: 3.5, quarterGrowth: 10.2 },
    { name: '深圳', monthGrowth: 5.2, quarterGrowth: 15.1 },
    { name: '杭州', monthGrowth: 6.8, quarterGrowth: 18.5 },
    { name: '南京', monthGrowth: 4.1, quarterGrowth: 12.1 },
    { name: '成都', monthGrowth: 5.5, quarterGrowth: 16.2 },
    { name: '武汉', monthGrowth: 3.9, quarterGrowth: 11.5 },
    { name: '西安', monthGrowth: 4.3, quarterGrowth: 12.8 },
    { name: '重庆', monthGrowth: 5.1, quarterGrowth: 14.8 }
  ]

  const xAxisData = regionGrowthData.map(item => item.name)
  const growthData = growthRatePeriod.value === 'month'
    ? regionGrowthData.map(item => item.monthGrowth)
    : regionGrowthData.map(item => item.quarterGrowth)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: '{b}: {c}%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLabel: {
        interval: 0,
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: growthRatePeriod.value === 'month' ? '月增长率' : '季度增长率',
        type: 'bar',
        data: growthData,
        itemStyle: {
          color: function(params: any) {
            const colorList = [
              new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#67c23a' },
                { offset: 1, color: '#409eff' }
              ])
            ]
            return colorList[params.dataIndex % colorList.length]
          }
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}%',
          color: '#606266'
        }
      }
    ]
  }

  regionGrowthRateInstance?.setOption(option)
}

// 时间范围变化处理
const handleTimeRangeChange = () => {
  fetchRegionDetails()
  updateCharts()
  // 如果不是自定义时间范围，清空日期范围
  if (timeRange.value !== 'custom') {
    dateRange.value = null
  }
}

// 自定义日期范围变化处理
const handleDateRangeChange = () => {
  if (dateRange.value) {
    fetchRegionDetails()
    updateCharts()
  }
}

// 区域级别变化处理
const handleRegionLevelChange = () => {
  pagination.currentPage = 1
  fetchRegionDetails()
}

// 刷新数据
const handleRefresh = () => {
  fetchRegionDetails()
  updateCharts()
}

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  fetchRegionDetails()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  fetchRegionDetails()
}

// 当前页码变化
const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  fetchRegionDetails()
}

// 查看区域详情分析
const viewRegionAnalysis = (regionName: string) => {
  ElMessage.info(`查看${regionName}区域的详细分析`)
  // 在实际项目中，这里应该跳转到更详细的区域分析页面
  // router.push(`/statistics/region/${regionName}`)
}

// 监听组件挂载
onMounted(() => {
  fetchRegionDetails()
  // 延迟初始化图表，确保DOM已经渲染完成
  setTimeout(() => {
    initCharts()
  }, 100)
})
</script>

<style scoped>
.region-analysis-container {
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

.region-summary-cards {
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

.map-chart {
  width: 100%;
  height: 400px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.map-controls {
  display: flex;
  align-items: center;
  gap: 10px;
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

.region-details-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
  margin-bottom: 10px;
}

.table-controls {
  display: flex;
  align-items: center;
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
  .region-analysis-container {
    padding: 10px;
  }

  .time-filter-container {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .region-summary-cards {
    grid-template-columns: 1fr;
  }

  .charts-container {
    grid-template-columns: 1fr;
  }

  .chart-card {
    height: 250px;
  }

  .map-chart {
    height: 300px;
  }

  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .table-header {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .table-controls {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .map-controls {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
}
</style>
