<template>
  <div class="default-statistics-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>违约统计</h2>
      <p class="subtitle">按行业和区域统计违约及违约重生主体情况</p>
    </div>

    <!-- 时间筛选器 -->
    <div class="time-filter-container">
      <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
        <el-radio-button label="month">本月</el-radio-button>
        <el-radio-button label="quarter">本季度</el-radio-button>
        <el-radio-button label="year">本年</el-radio-button>
        <el-radio-button label="custom">自定义</el-radio-button>
      </el-radio-group>

      <el-date-picker
        v-if="timeRange === 'custom'"
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        class="custom-date-picker"
        @change="handleDateRangeChange"
      />

      <el-button type="primary" icon="Refresh" @click="handleRefresh">刷新</el-button>
    </div>

    <!-- 统计维度切换 -->
    <div class="dimension-switch">
      <el-radio-group v-model="activeDimension" @change="handleDimensionChange">
        <el-radio-button label="industry">行业统计</el-radio-button>
        <el-radio-button label="region">区域统计</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 行业统计部分 -->
    <div v-if="activeDimension === 'industry'">
      <!-- 行业概览卡片 -->
      <div class="summary-cards">
        <el-card class="summary-card">
          <div class="summary-card-content">
            <div class="summary-title">行业违约主体总数</div>
            <div class="summary-value">{{ industryStats.totalDefault }}</div>
            <div class="summary-subtitle">同比 <span :class="industryStats.defaultGrowth >= 0 ? 'positive' : 'negative'">
              {{ industryStats.defaultGrowth }}%
            </span></div>
          </div>
        </el-card>

        <el-card class="summary-card">
          <div class="summary-card-content">
            <div class="summary-title">行业违约重生主体总数</div>
            <div class="summary-value">{{ industryStats.totalRebirth }}</div>
            <div class="summary-subtitle">同比 <span :class="industryStats.rebirthGrowth >= 0 ? 'positive' : 'negative'">
              {{ industryStats.rebirthGrowth }}%
            </span></div>
          </div>
        </el-card>

        <el-card class="summary-card">
          <div class="summary-card-content">
            <div class="summary-title">平均行业违约率</div>
            <div class="summary-value">{{ industryStats.avgBreachRate }}%</div>
            <div class="summary-subtitle">同比变化 <span :class="industryStats.breachRateChange >= 0 ? 'positive' : 'negative'">
              {{ industryStats.breachRateChange }}%</span></div>
          </div>
        </el-card>

        <el-card class="summary-card">
          <div class="summary-card-content">
            <div class="summary-title">行业重生率</div>
            <div class="summary-value">{{ industryStats.rebirthRate }}%</div>
            <div class="summary-subtitle">同比变化 <span :class="industryStats.rebirthRateChange >= 0 ? 'positive' : 'negative'">
              {{ industryStats.rebirthRateChange }}%</span></div>
          </div>
        </el-card>
      </div>

      <!-- 行业图表区域 -->
      <div class="charts-section">
        <!-- 行业分布饼图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>行业分布</span>
              <el-select v-model="chartType" @change="handleChartTypeChange" class="chart-type-select">
                <el-option label="违约主体数" value="defaultCount" />
                <el-option label="重生主体数" value="rebirthCount" />
                <el-option label="违约率" value="breachRate" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <div ref="industryDistributionChart" class="chart" id="industryDistributionChart"></div>
          </div>
        </el-card>

        <!-- 行业增长趋势图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>行业增长趋势</span>
              <el-select v-model="selectedIndustries" multiple collapse-tags @change="handleIndustryChange" class="industry-select">
                <el-option v-for="industry in industryOptions" :key="industry.value" :label="industry.label" :value="industry.value" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <div ref="industryTrendChart" class="chart" id="industryTrendChart"></div>
          </div>
        </el-card>

        <!-- 行业对比柱状图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>行业对比</span>
              <el-select v-model="comparisonType" @change="handleChartTypeChange" class="chart-type-select">
                <el-option label="违约主体数" value="defaultCount" />
                <el-option label="重生主体数" value="rebirthCount" />
                <el-option label="违约率" value="breachRate" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <div ref="industryComparisonChart" class="chart" id="industryComparisonChart"></div>
          </div>
        </el-card>

        <!-- 行业风险雷达图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>行业风险雷达图</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="industryRiskRadarChart" class="chart" id="industryRiskRadarChart"></div>
          </div>
        </el-card>
      </div>

      <!-- 行业详情表格 -->
      <el-card class="table-card">
        <template #header>
          <div class="card-header">
            <span>行业详情</span>
            <el-button type="primary" icon="Download" @click="exportData">导出数据</el-button>
          </div>
        </template>
        <el-table :data="industryData.slice((pagination.currentPage - 1) * pagination.pageSize, pagination.currentPage * pagination.pageSize)" border>
          <el-table-column prop="industry" label="行业名称" width="120">
            <template #default="scope">
              {{ getIndustryLabel(scope.row.industry) }}
            </template>
          </el-table-column>
          <el-table-column prop="totalCustomers" label="客户总数" width="100" sortable />
          <el-table-column prop="defaultCount" label="违约主体数" width="100" sortable />
          <el-table-column prop="rebirthCount" label="重生主体数" width="100" sortable />
          <el-table-column prop="breachRate" label="违约率" width="100" sortable>
            <template #default="scope">
              {{ scope.row.breachRate.toFixed(2) }}%
            </template>
          </el-table-column>
          <el-table-column prop="rebirthRate" label="重生率" width="100" sortable>
            <template #default="scope">
              {{ scope.row.rebirthRate.toFixed(2) }}%
            </template>
          </el-table-column>
          <el-table-column prop="defaultGrowth" label="违约增长率" width="120" sortable>
            <template #default="scope">
              <span :class="scope.row.defaultGrowth >= 0 ? 'text-success' : 'text-danger'">
                {{ scope.row.defaultGrowth >= 0 ? '+' : '' }}{{ scope.row.defaultGrowth.toFixed(2) }}%
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="rebirthGrowth" label="重生增长率" width="120" sortable>
            <template #default="scope">
              <span :class="scope.row.rebirthGrowth >= 0 ? 'text-success' : 'text-danger'">
                {{ scope.row.rebirthGrowth >= 0 ? '+' : '' }}{{ scope.row.rebirthGrowth.toFixed(2) }}%
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="riskScore" label="风险评分" width="100" sortable />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewIndustryAnalysis(scope.row.industry)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="industryData.length"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 区域统计部分 -->
    <div v-if="activeDimension === 'region'">
      <!-- 区域概览卡片 -->
      <div class="summary-cards">
        <el-card class="summary-card">
          <div class="summary-card-content">
            <div class="summary-title">区域违约主体总数</div>
            <div class="summary-value">{{ regionStats.totalDefault }}</div>
            <div class="summary-subtitle">同比 <span :class="regionStats.defaultGrowth >= 0 ? 'positive' : 'negative'">
              {{ regionStats.defaultGrowth }}%
            </span></div>
          </div>
        </el-card>

        <el-card class="summary-card">
          <div class="summary-card-content">
            <div class="summary-title">区域违约重生主体总数</div>
            <div class="summary-value">{{ regionStats.totalRebirth }}</div>
            <div class="summary-subtitle">同比 <span :class="regionStats.rebirthGrowth >= 0 ? 'positive' : 'negative'">
              {{ regionStats.rebirthGrowth }}%
            </span></div>
          </div>
        </el-card>

        <el-card class="summary-card">
          <div class="summary-card-content">
            <div class="summary-title">平均区域违约率</div>
            <div class="summary-value">{{ regionStats.avgBreachRate }}%</div>
            <div class="summary-subtitle">同比变化 <span :class="regionStats.breachRateChange >= 0 ? 'positive' : 'negative'">
              {{ regionStats.breachRateChange }}%</span></div>
          </div>
        </el-card>

        <el-card class="summary-card">
          <div class="summary-card-content">
            <div class="summary-title">区域重生率</div>
            <div class="summary-value">{{ regionStats.rebirthRate }}%</div>
            <div class="summary-subtitle">同比变化 <span :class="regionStats.rebirthRateChange >= 0 ? 'positive' : 'negative'">
              {{ regionStats.rebirthRateChange }}%</span></div>
          </div>
        </el-card>
      </div>

      <!-- 区域图表区域 -->
      <div class="charts-section">
        <!-- 区域地图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>区域地图</span>
              <el-select v-model="mapDataType" @change="handleMapDataTypeChange" class="chart-type-select">
                <el-option label="违约主体数" value="defaultCount" />
                <el-option label="重生主体数" value="rebirthCount" />
                <el-option label="违约率" value="breachRate" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <div ref="regionMapChart" class="chart" id="regionMapChart"></div>
          </div>
        </el-card>

        <!-- 区域违约率趋势图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>区域违约率趋势</span>
              <el-select v-model="selectedRegions" multiple collapse-tags @change="handleRegionChange" class="region-select">
                <el-option v-for="region in regionOptions" :key="region.value" :label="region.label" :value="region.value" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <div ref="regionBreachRateChart" class="chart" id="regionBreachRateChart"></div>
          </div>
        </el-card>

        <!-- 区域风险分布饼图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>区域风险分布</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="regionRiskDistributionChart" class="chart" id="regionRiskDistributionChart"></div>
          </div>
        </el-card>

        <!-- 区域增长率柱状图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>区域增长率</span>
              <el-select v-model="growthRatePeriod" @change="handleGrowthRatePeriodChange" class="chart-type-select">
                <el-option label="月度增长" value="month" />
                <el-option label="季度增长" value="quarter" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <div ref="regionGrowthRateChart" class="chart" id="regionGrowthRateChart"></div>
          </div>
        </el-card>
      </div>

      <!-- 区域详情表格 -->
      <el-card class="table-card">
        <template #header>
          <div class="card-header">
            <span>区域详情</span>
            <el-button type="primary" icon="Download" @click="exportData">导出数据</el-button>
          </div>
        </template>
        <el-table :data="regionData.slice((pagination.currentPage - 1) * pagination.pageSize, pagination.currentPage * pagination.pageSize)" border>
          <el-table-column prop="regionName" label="区域名称" width="120" />
          <el-table-column prop="totalCustomers" label="客户总数" width="100" sortable />
          <el-table-column prop="defaultCount" label="违约主体数" width="100" sortable />
          <el-table-column prop="rebirthCount" label="重生主体数" width="100" sortable />
          <el-table-column prop="breachRate" label="违约率" width="100" sortable>
            <template #default="scope">
              {{ scope.row.breachRate.toFixed(2) }}%
            </template>
          </el-table-column>
          <el-table-column prop="rebirthRate" label="重生率" width="100" sortable>
            <template #default="scope">
              {{ scope.row.rebirthRate.toFixed(2) }}%
            </template>
          </el-table-column>
          <el-table-column prop="defaultGrowth" label="违约增长率" width="120" sortable>
            <template #default="scope">
              <span :class="scope.row.defaultGrowth >= 0 ? 'text-success' : 'text-danger'">
                {{ scope.row.defaultGrowth >= 0 ? '+' : '' }}{{ scope.row.defaultGrowth.toFixed(2) }}%
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="rebirthGrowth" label="重生增长率" width="120" sortable>
            <template #default="scope">
              <span :class="scope.row.rebirthGrowth >= 0 ? 'text-success' : 'text-danger'">
                {{ scope.row.rebirthGrowth >= 0 ? '+' : '' }}{{ scope.row.rebirthGrowth.toFixed(2) }}%
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="riskScore" label="风险评分" width="100" sortable />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewRegionAnalysis(scope.row.regionName)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="regionData.length"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

// 时间范围选项
const timeRange = ref<'month' | 'quarter' | 'year' | 'custom'>('year')
const dateRange = ref<string[]>([])

// 统计维度
const activeDimension = ref<'industry' | 'region'>('industry')

// 图表类型选择
const chartType = ref<string>('defaultCount')
const comparisonType = ref<string>('defaultCount')
const mapDataType = ref<string>('defaultCount')
const growthRatePeriod = ref<'month' | 'quarter'>('month')

// 行业选择
const industryOptions = ref([
  { label: '科技', value: 'tech' },
  { label: '金融', value: 'finance' },
  { label: '制造业', value: 'manufacturing' },
  { label: '医疗', value: 'medical' },
  { label: '教育', value: 'education' },
  { label: '房地产', value: 'realestate' },
  { label: '零售', value: 'retail' },
  { label: '物流', value: 'logistics' },
  { label: '贸易', value: 'trade' },
  { label: '能源', value: 'energy' }
])
const selectedIndustries = ref<string[]>(['tech', 'finance', 'manufacturing', 'medical'])

// 区域选择
const regionOptions = ref([
  { label: '北京', value: 'beijing' },
  { label: '上海', value: 'shanghai' },
  { label: '广州', value: 'guangzhou' },
  { label: '深圳', value: 'shenzhen' },
  { label: '杭州', value: 'hangzhou' },
  { label: '南京', value: 'nanjing' },
  { label: '成都', value: 'chengdu' },
  { label: '武汉', value: 'wuhan' },
  { label: '重庆', value: 'chongqing' },
  { label: '西安', value: 'xian' }
])
const selectedRegions = ref<string[]>(['beijing', 'shanghai', 'guangzhou', 'shenzhen'])

// 分页配置
const pagination = ref({
  currentPage: 1,
  pageSize: 10
})

// 图表实例引用
const industryDistributionChart = ref<HTMLElement | null>(null)
const industryTrendChart = ref<HTMLElement | null>(null)
const industryComparisonChart = ref<HTMLElement | null>(null)
const industryRiskRadarChart = ref<HTMLElement | null>(null)
const regionMapChart = ref<HTMLElement | null>(null)
const regionBreachRateChart = ref<HTMLElement | null>(null)
const regionRiskDistributionChart = ref<HTMLElement | null>(null)
const regionGrowthRateChart = ref<HTMLElement | null>(null)

// 行业统计数据
const industryStats = ref({
  totalDefault: 1258,
  totalRebirth: 386,
  avgBreachRate: 15.2,
  rebirthRate: 30.7,
  defaultGrowth: 12.5,
  rebirthGrowth: 8.3,
  breachRateChange: 2.1,
  rebirthRateChange: -1.2
})

const industryData = ref([
  { industry: 'tech', totalCustomers: 3256, defaultCount: 485, rebirthCount: 156, breachRate: 14.9, rebirthRate: 32.2, defaultGrowth: 15.8, rebirthGrowth: 10.2, riskScore: 75 },
  { industry: 'finance', totalCustomers: 2845, defaultCount: 425, rebirthCount: 125, breachRate: 14.9, rebirthRate: 29.4, defaultGrowth: 8.7, rebirthGrowth: 5.8, riskScore: 82 },
  { industry: 'manufacturing', totalCustomers: 4123, defaultCount: 687, rebirthCount: 198, breachRate: 16.7, rebirthRate: 28.8, defaultGrowth: 14.3, rebirthGrowth: 9.5, riskScore: 88 },
  { industry: 'medical', totalCustomers: 1876, defaultCount: 215, rebirthCount: 68, breachRate: 11.5, rebirthRate: 31.6, defaultGrowth: 7.2, rebirthGrowth: 4.3, riskScore: 65 },
  { industry: 'education', totalCustomers: 1567, defaultCount: 189, rebirthCount: 45, breachRate: 12.0, rebirthRate: 23.8, defaultGrowth: 5.4, rebirthGrowth: 3.2, riskScore: 60 },
  { industry: 'realestate', totalCustomers: 2345, defaultCount: 567, rebirthCount: 89, breachRate: 24.2, rebirthRate: 15.7, defaultGrowth: 25.6, rebirthGrowth: 12.8, riskScore: 95 },
  { industry: 'retail', totalCustomers: 3467, defaultCount: 432, rebirthCount: 112, breachRate: 12.5, rebirthRate: 25.9, defaultGrowth: 9.8, rebirthGrowth: 6.4, riskScore: 70 },
  { industry: 'logistics', totalCustomers: 1987, defaultCount: 215, rebirthCount: 56, breachRate: 10.8, rebirthRate: 26.0, defaultGrowth: 6.5, rebirthGrowth: 4.8, riskScore: 68 },
  { industry: 'trade', totalCustomers: 2765, defaultCount: 412, rebirthCount: 98, breachRate: 14.9, rebirthRate: 23.8, defaultGrowth: 11.2, rebirthGrowth: 7.6, riskScore: 78 },
  { industry: 'energy', totalCustomers: 1654, defaultCount: 218, rebirthCount: 45, breachRate: 13.2, rebirthRate: 20.6, defaultGrowth: 8.3, rebirthGrowth: 3.9, riskScore: 72 }
])

// 区域统计数据
const regionStats = ref({
  totalDefault: 1892,
  totalRebirth: 548,
  avgBreachRate: 14.8,
  rebirthRate: 28.9,
  defaultGrowth: 11.2,
  rebirthGrowth: 9.5,
  breachRateChange: 1.8,
  rebirthRateChange: -0.9
})

const regionData = ref([
  { regionName: '北京', totalCustomers: 3456, defaultCount: 512, rebirthCount: 156, breachRate: 14.8, rebirthRate: 30.5, defaultGrowth: 12.5, rebirthGrowth: 8.3, riskScore: 82, monthGrowth: 3.5, quarterGrowth: 10.2 },
  { regionName: '上海', totalCustomers: 3890, defaultCount: 587, rebirthCount: 178, breachRate: 15.1, rebirthRate: 30.3, defaultGrowth: 13.2, rebirthGrowth: 9.1, riskScore: 85, monthGrowth: 4.2, quarterGrowth: 11.5 },
  { regionName: '广州', totalCustomers: 2765, defaultCount: 432, rebirthCount: 125, breachRate: 15.6, rebirthRate: 28.9, defaultGrowth: 11.8, rebirthGrowth: 7.6, riskScore: 80, monthGrowth: 2.8, quarterGrowth: 9.4 },
  { regionName: '深圳', totalCustomers: 3123, defaultCount: 489, rebirthCount: 143, breachRate: 15.7, rebirthRate: 29.2, defaultGrowth: 12.8, rebirthGrowth: 8.7, riskScore: 83, monthGrowth: 3.9, quarterGrowth: 10.8 },
  { regionName: '杭州', totalCustomers: 2156, defaultCount: 312, rebirthCount: 98, breachRate: 14.5, rebirthRate: 31.4, defaultGrowth: 10.5, rebirthGrowth: 8.9, riskScore: 78, monthGrowth: 5.2, quarterGrowth: 12.6 },
  { regionName: '南京', totalCustomers: 1890, defaultCount: 278, rebirthCount: 85, breachRate: 14.7, rebirthRate: 30.6, defaultGrowth: 9.8, rebirthGrowth: 7.2, riskScore: 75, monthGrowth: 3.6, quarterGrowth: 9.9 },
  { regionName: '成都', totalCustomers: 2345, defaultCount: 345, rebirthCount: 102, breachRate: 14.7, rebirthRate: 29.6, defaultGrowth: 11.2, rebirthGrowth: 8.5, riskScore: 77, monthGrowth: 4.5, quarterGrowth: 11.2 },
  { regionName: '武汉', totalCustomers: 2012, defaultCount: 305, rebirthCount: 89, breachRate: 15.2, rebirthRate: 29.2, defaultGrowth: 12.1, rebirthGrowth: 8.3, riskScore: 79, monthGrowth: 3.8, quarterGrowth: 10.5 },
  { regionName: '重庆', totalCustomers: 1789, defaultCount: 256, rebirthCount: 78, breachRate: 14.3, rebirthRate: 30.5, defaultGrowth: 8.9, rebirthGrowth: 6.7, riskScore: 74, monthGrowth: 3.2, quarterGrowth: 9.1 },
  { regionName: '西安', totalCustomers: 1567, defaultCount: 218, rebirthCount: 65, breachRate: 13.9, rebirthRate: 29.8, defaultGrowth: 7.8, rebirthGrowth: 5.9, riskScore: 72, monthGrowth: 2.9, quarterGrowth: 8.7 }
])

// 计算属性：获取年份
const currentYear = new Date().getFullYear()

// 行业标签映射
const getIndustryLabel = (industryCode: string): string => {
  const industry = industryOptions.value.find(item => item.value === industryCode)
  return industry ? industry.label : industryCode
}

// 初始化图表
const initCharts = () => {
  // 行业分布饼图
  const industryDistributionChartInstance = echarts.init(industryDistributionChart.value!)
  industryDistributionChartInstance.setOption(getIndustryDistributionOption())

  // 行业增长趋势图
  const industryTrendChartInstance = echarts.init(industryTrendChart.value!)
  industryTrendChartInstance.setOption(getIndustryTrendOption())

  // 行业对比柱状图
  const industryComparisonChartInstance = echarts.init(industryComparisonChart.value!)
  industryComparisonChartInstance.setOption(getIndustryComparisonOption())

  // 行业风险雷达图
  const industryRiskRadarChartInstance = echarts.init(industryRiskRadarChart.value!)
  industryRiskRadarChartInstance.setOption(getIndustryRiskRadarOption())

  // 区域地图
  const regionMapChartInstance = echarts.init(regionMapChart.value!)
  regionMapChartInstance.setOption(getRegionMapOption())

  // 区域违约率趋势图
  const regionBreachRateChartInstance = echarts.init(regionBreachRateChart.value!)
  regionBreachRateChartInstance.setOption(getRegionBreachRateOption())

  // 区域风险分布饼图
  const regionRiskDistributionChartInstance = echarts.init(regionRiskDistributionChart.value!)
  regionRiskDistributionChartInstance.setOption(getRegionRiskDistributionOption())

  // 区域增长率柱状图
  const regionGrowthRateChartInstance = echarts.init(regionGrowthRateChart.value!)
  regionGrowthRateChartInstance.setOption(getRegionGrowthRateOption())
}

// 更新所有图表
const updateCharts = () => {
  // 更新行业相关图表
  if (activeDimension.value === 'industry') {
    updateIndustryDistributionChart()
    updateIndustryTrendChart()
    updateIndustryComparisonChart()
    updateIndustryRiskRadarChart()
  }
  // 更新区域相关图表
  else if (activeDimension.value === 'region') {
    updateRegionMapChart()
    updateRegionBreachRateChart()
    updateRegionRiskDistributionChart()
    updateRegionGrowthRateChart()
  }
}

// 行业分布饼图选项
const getIndustryDistributionOption = () => {
  const data = industryData.value.map(item => ({
    name: getIndustryLabel(item.industry),
    value: chartType.value === 'defaultCount' ? item.defaultCount : 
           chartType.value === 'rebirthCount' ? item.rebirthCount : item.breachRate
  }))

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: data.map(item => item.name)
    },
    series: [{
      name: chartType.value === 'defaultCount' ? '违约主体数' : 
           chartType.value === 'rebirthCount' ? '重生主体数' : '违约率',
      type: 'pie',
      radius: '60%',
      center: ['50%', '50%'],
      data: data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
}

// 行业增长趋势图选项
const getIndustryTrendOption = () => {
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  const selectedIndustriesData = selectedIndustries.value.map(industry => {
    const industryName = getIndustryLabel(industry)
    // 生成模拟趋势数据
    const baseValue = industryData.value.find(item => item.industry === industry)?.defaultCount || 100
    const data = months.map(() => Math.floor(baseValue * (0.8 + Math.random() * 0.4)))
    return {
      name: industryName,
      type: 'line',
      data: data
    }
  })

  return {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: selectedIndustriesData.map(item => item.name)
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
      type: 'value'
    },
    series: selectedIndustriesData
  }
}

// 行业对比柱状图选项
const getIndustryComparisonOption = () => {
  const data = industryData.value.map(item => ({
    name: getIndustryLabel(item.industry),
    value: comparisonType.value === 'defaultCount' ? item.defaultCount : 
           comparisonType.value === 'rebirthCount' ? item.rebirthCount : item.breachRate
  }))

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: comparisonType.value === 'breachRate' ? '{value}%' : '{value}'
      }
    },
    series: [{
      name: comparisonType.value === 'defaultCount' ? '违约主体数' : 
           comparisonType.value === 'rebirthCount' ? '重生主体数' : '违约率',
      type: 'bar',
      data: data.map(item => item.value),
      itemStyle: {
        color: '#409eff'
      }
    }]
  }
}

// 行业风险雷达图选项
const getIndustryRiskRadarOption = () => {
  const industries = ['科技', '金融', '制造业', '医疗', '教育']
  return {
    tooltip: {},
    radar: {
      indicator: [
        { name: '违约率', max: 30 },
        { name: '客户规模', max: 100 },
        { name: '风险评分', max: 100 },
        { name: '增长率', max: 30 },
        { name: '重生率', max: 50 }
      ]
    },
    series: [{
      name: '行业风险对比',
      type: 'radar',
      data: [
        {
          value: [15, 90, 75, 16, 32],
          name: '科技',
          areaStyle: { color: 'rgba(103, 194, 58, 0.2)' },
          lineStyle: { color: '#67c23a' }
        },
        {
          value: [15, 85, 82, 9, 29],
          name: '金融',
          areaStyle: { color: 'rgba(102, 194, 255, 0.2)' },
          lineStyle: { color: '#66c2ff' }
        },
        {
          value: [17, 95, 88, 14, 29],
          name: '制造业',
          areaStyle: { color: 'rgba(245, 108, 108, 0.2)' },
          lineStyle: { color: '#f56c6c' }
        },
        {
          value: [12, 70, 65, 7, 32],
          name: '医疗',
          areaStyle: { color: 'rgba(194, 185, 103, 0.2)' },
          lineStyle: { color: '#c2b967' }
        },
        {
          value: [12, 60, 60, 5, 24],
          name: '教育',
          areaStyle: { color: 'rgba(144, 103, 194, 0.2)' },
          lineStyle: { color: '#9067c2' }
        }
      ]
    }]
  }
}

// 区域地图选项
const getRegionMapOption = () => {
  const data = regionData.value.map(item => ({
    name: item.regionName,
    value: mapDataType.value === 'defaultCount' ? item.defaultCount : 
           mapDataType.value === 'rebirthCount' ? item.rebirthCount : item.breachRate
  }))

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}'
    },
    visualMap: {
      min: 0,
      max: Math.max(...data.map(item => item.value)),
      text: ['高', '低'],
      realtime: false,
      calculable: true,
      inRange: {
        color: ['#67c23a', '#e6a23c', '#f56c6c']
      }
    },
    series: [{
      name: mapDataType.value === 'defaultCount' ? '违约主体数' : 
           mapDataType.value === 'rebirthCount' ? '重生主体数' : '违约率',
      type: 'map',
      map: 'china',
      emphasis: {
        label: {
          show: true
        }
      },
      data: data
    }]
  }
}

// 区域违约率趋势图选项
const getRegionBreachRateOption = () => {
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  const selectedRegionsData = selectedRegions.value.map(region => {
    const regionItem = regionOptions.value.find(r => r.value === region)
    const regionName = regionItem ? regionItem.label : region
    const baseRate = regionData.value.find(item => item.regionName === regionName)?.breachRate || 10
    // 生成模拟趋势数据
    const data = months.map(() => Number((baseRate * (0.9 + Math.random() * 0.2)).toFixed(2)))
    return {
      name: regionName,
      type: 'line',
      data: data
    }
  })

  return {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}%'
    },
    legend: {
      data: selectedRegionsData.map(item => item.name)
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
      }
    },
    series: selectedRegionsData
  }
}

// 区域风险分布饼图选项
const getRegionRiskDistributionOption = () => {
  const data = regionData.value.map(item => ({
    name: item.regionName,
    value: item.riskScore
  }))

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 'right',
      data: data.map(item => item.name)
    },
    series: [{
      name: '区域风险评分',
      type: 'pie',
      radius: '60%',
      center: ['40%', '50%'],
      data: data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
}

// 区域增长率柱状图选项
const getRegionGrowthRateOption = () => {
  const data = regionData.value.map(item => ({
    name: item.regionName,
    value: growthRatePeriod.value === 'month' ? item.monthGrowth : item.quarterGrowth
  }))

  return {
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
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [{
      name: growthRatePeriod.value === 'month' ? '月度增长率' : '季度增长率',
      type: 'bar',
      data: data.map(item => item.value),
      itemStyle: {
        color: function(params: any) {
          const value = params.data
          return value >= 0 ? '#67c23a' : '#f56c6c'
        }
      }
    }]
  }
}

// 更新行业分布饼图
const updateIndustryDistributionChart = () => {
  const chart = echarts.getInstanceByDom(industryDistributionChart.value!)
  chart?.setOption(getIndustryDistributionOption())
}

// 更新行业增长趋势图
const updateIndustryTrendChart = () => {
  const chart = echarts.getInstanceByDom(industryTrendChart.value!)
  chart?.setOption(getIndustryTrendOption())
}

// 更新行业对比柱状图
const updateIndustryComparisonChart = () => {
  const chart = echarts.getInstanceByDom(industryComparisonChart.value!)
  chart?.setOption(getIndustryComparisonOption())
}

// 更新行业风险雷达图
const updateIndustryRiskRadarChart = () => {
  const chart = echarts.getInstanceByDom(industryRiskRadarChart.value!)
  chart?.setOption(getIndustryRiskRadarOption())
}

// 更新区域地图
const updateRegionMapChart = () => {
  const chart = echarts.getInstanceByDom(regionMapChart.value!)
  chart?.setOption(getRegionMapOption())
}

// 更新区域违约率趋势图
const updateRegionBreachRateChart = () => {
  const chart = echarts.getInstanceByDom(regionBreachRateChart.value!)
  chart?.setOption(getRegionBreachRateOption())
}

// 更新区域风险分布饼图
const updateRegionRiskDistributionChart = () => {
  const chart = echarts.getInstanceByDom(regionRiskDistributionChart.value!)
  chart?.setOption(getRegionRiskDistributionOption())
}

// 更新区域增长率柱状图
const updateRegionGrowthRateChart = () => {
  const chart = echarts.getInstanceByDom(regionGrowthRateChart.value!)
  chart?.setOption(getRegionGrowthRateOption())
}

// 事件处理函数
const handleTimeRangeChange = () => {
  ElMessage.info('时间范围已更新')
  // 在实际应用中，这里应该根据时间范围重新获取数据
  updateCharts()
}

const handleDateRangeChange = () => {
  ElMessage.info('自定义日期范围已更新')
  // 在实际应用中，这里应该根据自定义日期范围重新获取数据
  updateCharts()
}

const handleRefresh = () => {
  ElMessage.success('数据已刷新')
  // 在实际应用中，这里应该重新从服务器获取数据
  updateCharts()
}

const handleDimensionChange = () => {
  nextTick(() => {
    updateCharts()
  })
}

const handleChartTypeChange = () => {
  updateCharts()
}

const handleIndustryChange = () => {
  updateCharts()
}

const handleRegionChange = () => {
  updateCharts()
}

const handleMapDataTypeChange = () => {
  updateCharts()
}

const handleGrowthRatePeriodChange = () => {
  updateCharts()
}

const viewIndustryAnalysis = (industry: string) => {
  ElMessage.info(`查看${getIndustryLabel(industry)}行业详情`)
  // 在实际应用中，这里应该跳转到行业详情页面
}

const viewRegionAnalysis = (regionName: string) => {
  ElMessage.info(`查看${regionName}区域详情`)
  // 在实际应用中，这里应该跳转到区域详情页面
}

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.currentPage = 1
}

const handleCurrentChange = (page: number) => {
  pagination.value.currentPage = page
}

const exportData = () => {
  ElMessage.success('数据导出成功')
  // 在实际应用中，这里应该实现数据导出功能
}

// 生命周期钩子
onMounted(() => {
  nextTick(() => {
    initCharts()
  })
})

// 监听窗口大小变化，自适应图表
window.addEventListener('resize', () => {
  const charts = [
    industryDistributionChart.value,
    industryTrendChart.value,
    industryComparisonChart.value,
    industryRiskRadarChart.value,
    regionMapChart.value,
    regionBreachRateChart.value,
    regionRiskDistributionChart.value,
    regionGrowthRateChart.value
  ]

  charts.forEach(chart => {
    if (chart) {
      const instance = echarts.getInstanceByDom(chart)
      instance?.resize()
    }
  })
})
</script>

<style scoped>
.default-statistics-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.page-header .subtitle {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.time-filter-container {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.custom-date-picker {
  width: 240px;
}

.dimension-switch {
  margin-bottom: 20px;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.summary-card {
  transition: transform 0.3s ease;
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.summary-card-content {
  text-align: center;
}

.summary-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.summary-subtitle {
  font-size: 12px;
  color: #909399;
}

.summary-subtitle .positive {
  color: #67c23a;
}

.summary-subtitle .negative {
  color: #f56c6c;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-type-select,
.industry-select,
.region-select {
  width: auto;
}

.chart-container {
  height: 350px;
  padding-top: 10px;
}

.chart {
  width: 100%;
  height: 100%;
}

.table-card {
  margin-bottom: 24px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .chart-container {
    height: 300px;
  }
}

@media (max-width: 768px) {
  .default-statistics-container {
    padding: 10px;
  }
  
  .time-filter-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .custom-date-picker {
    width: 100%;
  }
  
  .summary-cards {
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }
  
  .summary-value {
    font-size: 24px;
  }
  
  .chart-container {
    height: 250px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .chart-type-select,
  .industry-select,
  .region-select {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .summary-cards {
    grid-template-columns: 1fr;
  }
}

/* 表格样式 */
:deep(.el-table) {
  margin-top: 10px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

:deep(.el-table .text-success) {
  color: #67c23a;
}

:deep(.el-table .text-danger) {
  color: #f56c6c;
}
</style>