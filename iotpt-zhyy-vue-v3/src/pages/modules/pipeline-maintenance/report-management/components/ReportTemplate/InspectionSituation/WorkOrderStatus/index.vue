<template>
    <div>
      <Title
        :index="index"
        :title="title"
        :subTitle="subTitle"
        :subIndex="subIndex"
      />
      <div class="module-item-desc">
        本周期内，共产生
        <span class="num">{{ dataInfo.allMissionTotal }}</span>
        条工单，其中
        <span class="num">{{ dataInfo.missionTotal }}</span>
        条已处理完成，当前存在
        <span class="num">{{ dataInfo.unMissionTotal }}</span>
        条工单未处理。 工单平均响应时间
        <span class="num">{{ dataInfo.responseMin }}</span>
        分钟，平均处理完成时间
        <span class="num">{{ dataInfo.finishMin }}</span>
        分钟。
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted, nextTick } from 'vue'
  import { ElMessage } from 'element-plus'
  // 导入API和公共方法（Common中的方法可按需解构导入，替代mixins）
  import { missionInfo } from '@/api/pipeline-maintenance/report-management'
  import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";
  // 原mixins的Common.js，建议解构导入所需方法，替代mixins（TS推荐组合式而非mixins）
  // import { 所需方法 } from '../../common.js'

  defineOptions({
    name: 'WorkOrderStatus',
    cname: '工单情况'
  })
  
  // ===================== 定义组件Props（父组件传入的属性）=====================
  // 严格定义所有父组件传入的参数类型，和原组件this上的属性一一对应
  const props = defineProps<{
    index: string | number // 索引
    title: string // 主标题
    subTitle: string // 子标题
    subIndex: number | string // 子索引
    areaIds: (number | string)[] // 区域ID数组
    startTime: string // 开始时间
    endTime: string // 结束时间
    timeType: '日' | '月' | '年' // 时间类型（和父组件保持一致）
    type: number // 模板类型 splType
  }>()
  
  // ===================== 定义接口（TS数据类型）=====================
  // 工单接口返回的数据类型，和后端返回的res.data结构严格一致
  interface MissionInfoData {
    allMissionTotal: number // 总工单数
    missionTotal: number // 已处理工单数
    unMissionTotal: number // 未处理工单数
    responseMin: number // 平均响应时间(分钟)
    finishMin: number // 平均处理完成时间(分钟)
  }
  
  // ===================== 响应式数据 =====================
  // 工单数据，初始化值和原代码保持一致，指定类型为MissionInfoData
  const dataInfo = ref<MissionInfoData>({
    allMissionTotal: 114,
    missionTotal: 102,
    unMissionTotal: 12,
    responseMin: 65,
    finishMin: 164
  })
  
  // 保留原组件的cname标识（父组件动态渲染component时需要）
  defineExpose({
    cname: '工单情况'
  })
  
  // ===================== 生命周期 & 业务逻辑 =====================
  onMounted(async () => {
    // 替代原Vue2的this.$nextTick，确保DOM渲染完成后请求数据
    await nextTick()
    // 区域ID数组有值时才发起请求，和原逻辑一致
    if (props.areaIds.length > 0) {
      try {
        const res = await missionInfo({
          areaIds: props.areaIds,
          startTime: props.startTime,
          endTime: props.endTime,
          timeType: props.timeType, // 修复原代码timeTypes拼写错误，和Props保持一致
          splType: props.type
        })
        // 接口请求成功，更新数据
        dataInfo.value = res.data
      } catch (error) {
        // 错误捕获，避免组件崩溃，给出友好提示
        ElMessage.error('工单数据请求失败，请重试')
        console.error('工单情况组件请求异常：', error)
      }
    }
  })
  </script>
  
  <style lang="scss" scoped>
  // 保留原样式，可根据需要补充样式
  .module-item-desc {
    line-height: 1.8;
    color: #333;
  }
  .num {
    color: #1890ff; // 可根据项目主题色调整，突出数字
    font-weight: 600;
    margin: 0 4px;
  }
  </style>