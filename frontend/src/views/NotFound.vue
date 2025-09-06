<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="not-found-code">404</h1>
      <p class="not-found-message">抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="handleBack">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const handleBack = () => {
  router.push('/customer/list')
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
  padding: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.not-found-code {
  font-size: 100px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 20px;
}

.not-found-message {
  font-size: 18px;
  color: #606266;
  margin-bottom: 30px;
}
</style>