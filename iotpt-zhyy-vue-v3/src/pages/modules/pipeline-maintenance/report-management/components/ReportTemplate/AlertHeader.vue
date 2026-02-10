<template>
    <div class="module-header" :style="`width:${A4Origin.width}px;height:${A4Origin.height}px`">
      <!-- 静态图片改用import导入，符合Vue3+TS规范 -->
      <img :src="templateBg" class="img" alt="报表背景" />
      <div class="center">
        <h1 class="header-title">
          <div class="rect"></div>
          <span class="title">{{ title }}</span>
        </h1>
        <h4 class="header-timer">{{ timer }}</h4>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted, watch } from 'vue'
  import { jsPDF } from 'jspdf'
  // 导入静态图片资源（Vue3+TS推荐写法，自动识别类型）
  import templateBg from '../../../../../../assets/images/report-template/template_bg.jpg'
  
  // ===================== 定义组件Props（父组件传入属性）=====================
  // 严格定义Props类型，指定默认值，和原逻辑完全一致
  const props = defineProps({
    title: {
      type: String,
      default: ''
    },
    timer: {
      type: String,
      default: 'XXXX-XX-XX'
    }
  })
  // ===================== 定义TS接口 & 响应式数据 =====================
  // A4尺寸对象类型接口
  interface A4Size {
    width: number
    height: number
  }
  // 响应式数据：A4尺寸，初始化值和原代码一致，指定严格类型A4Size
  const A4Origin = ref<A4Size>({
    width: 0,
    height: 0
  })
  
  // 暴露图片资源供模板使用
  defineExpose({
    templateBg
  })
  
  // ===================== 生命周期 & 核心逻辑 =====================
  onMounted(() => {
    // 初始化jsPDF实例，和原A4尺寸计算逻辑完全一致
    const pdf = new jsPDF({
      unit: 'px',
      format: 'a4'
    })
    // 对jsPDF内部尺寸对象做类型断言，避免TS类型报错
    const pageSize = pdf.internal.pageSize as {
      getWidth: () => number
      getHeight: () => number
    }
    // 计算A4尺寸（预留边距40px，放大2倍适配渲染）
    A4Origin.value = {
      width: (pageSize.getWidth() - 40) * 2,
      height: (pageSize.getHeight() - 40) * 2
    }
  })
  </script>
  
  <style lang="scss" scoped>
  // 修复原样式titlle拼写错误为title，其他样式逻辑完全不变
  .img {
    width: 100%;
    height: 100%;
    position: absolute;
    z-index: 1;
    object-fit: cover;
  }
  
  .module-header {
    width: 100%;
    height: 500px;
    position: relative;
  
    .center {
      width: 100%;
      position: absolute;
      z-index: 2;
      top: 30%;
      height: 274px;
      background: rgba(0, 0, 0, 0.45);
    }
  }
  
  .header-title {
    margin: 0;
    color: #eee;
    font-weight: bold;
    font-size: 35px;
    position: absolute;
    top: 62px;
    z-index: 2;
    right: 50px;
  
    .title {
      position: relative;
      font-family: Source Han Sans CN, Source Han Sans CN;
      font-weight: bold;
      font-size: 48px;
      color: #ffffff;
      line-height: 64px;
      text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
      text-align: left;
    }
  
    .rect {
      position: absolute;
      content: "";
      left: -10px;
      bottom: 0px;
      display: inline-block;
      width: calc(100% + 20px);
      height: 30px;
      background: #4096ff;
    }
  }
  
  .header-timer {
    position: absolute;
    top: 130px;
    z-index: 3;
    right: 50px;
    font-family: Source Han Sans CN, Source Han Sans CN;
    font-weight: 500;
    font-size: 32px;
    color: #ffffff;
    line-height: 32px;
    text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  }
  </style>