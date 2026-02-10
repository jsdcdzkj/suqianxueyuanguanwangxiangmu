<template>
    <div class="title-container">
      <div class="module-sub-title" v-if="subIndex > 0">
        {{ subIndex }}、{{ subTitle }}
      </div>
      <div class="module-item-title">{{ index }}、{{ title }}</div>
    </div>
  </template>
  
  <script setup lang="ts">
  
  // 直接定义Props，和公共组件/报表子组件的属性完全一致，保证全项目属性统一
  const props = defineProps<{
    index?: string | number
    title?: string
    subTitle?: string
    subIndex?: number
  }>()
  
  // 解构并设置默认值，和公共组件保持一致
  const {
    index = '1.1',
    title = '标题',
    subTitle = '标题',
    subIndex = 0
  } = props
  </script>
  
  <style lang="scss" scoped>
  .module-item-title {
    font-size: 14px;
    line-height: 22px;
    margin: 6px 0;
  }
  
  .module-sub-title {
    font-size: 18px;
    font-weight: bold;
    line-height: 30px;
    margin: 10px 0;
  }
  
  .title-container {
    width: 100%;
    box-sizing: border-box;
  }
  </style>