<template>
  <div class="unauthorized-container">
    <el-result
      icon="warning"
      title="403"
      sub-title="抱歉，您没有权限访问此页面"
    >
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/customer/list')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.unauthorized-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
}
</style>
