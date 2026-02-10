<template>
  <div>
    <Title
      :index="index"
      :title="title"
      :subTitle="subTitle"
      :subIndex="subIndex"
    />
    <div class="modules-item-desc">本报表监测周期为{{ startTime }} - {{ endTime }}。</div>
    <!-- 单区域信息 -->
    <ElDescriptions
      class="margin-top"
      :label-style="{ width: '100px' }"
      :column="1"
      border
      v-if="type == 1"
    >
      <ElDescriptionsItem>
        <template #label>区域名称</template>
        {{ dataInfo.area?.areaName || '-' }}
      </ElDescriptionsItem>
      <ElDescriptionsItem>
        <template #label>区域地址</template>
        {{ dataInfo.area?.detailAddress || '-' }}
      </ElDescriptionsItem>
      <ElDescriptionsItem>
        <template #label>区域联系电话</template>
        {{ dataInfo.area?.phones || '-' }}
      </ElDescriptionsItem>
      <ElDescriptionsItem>
        <template #label>区域设备数</template>
        <ElTag size="small">{{ dataInfo.deviceCount || 0 }}个</ElTag>
      </ElDescriptionsItem>
    </ElDescriptions>
    <!-- 多区域信息 -->
    <ElDescriptions
      class="margin-top"
      :column="1"
      border
      v-if="type == 2"
      :label-style="{ width: '100px' }"
    >
      <ElDescriptionsItem>
        <template #label>项目名称</template>
        {{ projectName || '-' }}
      </ElDescriptionsItem>
      <ElDescriptionsItem>
        <template #label>包含区域数</template>
        {{ dataInfo.areaCount || 0 }}户
      </ElDescriptionsItem>
      <ElDescriptionsItem>
        <template #label>区域设备数</template>
        {{ dataInfo.deviceCount || 0 }}个（{{ dataInfo.deviceCountOn || 0 }}个正常，{{
          dataInfo.deviceCountOff || 0
        }}个离线，{{ dataInfo.deviceCountError || 0 }}个异常）
      </ElDescriptionsItem>
      <ElDescriptionsItem>
        <template #label>区域名称</template>
        {{ dataInfo.areaNames || '-' }}
      </ElDescriptionsItem>
    </ElDescriptions>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
// 导入Element Plus组件
import { ElDescriptions, ElDescriptionsItem, ElTag } from 'element-plus'
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";
// 导入业务API
import { baseInfo } from '@/api/pipeline-maintenance/report-management'

// ===================== 定义组件名称（Vue3.3+ 原生支持，带TS类型）=====================
defineOptions({
  name: 'BaseInfo', // 组件名，可根据项目规范修改，如BasicInfo/ReportBaseInfo
  cname: '基本信息' // 组件显示名称，可根据项目规范修改，如单区域/多区域基本信息
})

// ===================== 定义组件Props（严格TS类型约束）=====================
const props = defineProps<{
  index: string | number // 索引
  title: string // 主标题
  subTitle: string // 子标题
  subIndex: string | number // 子索引
  areaIds: (string | number)[] // 区域ID数组
  startTime: string // 开始时间
  endTime: string // 结束时间
  timeType: '日' | '月' | '年' // 时间类型
  type: 1 | 2 // 1-单区域 2-多区域
  projectName?: string // 项目名称（多区域用，可选）
}>()

// ===================== 定义TS接口（数据类型约束）=====================
// 单区域信息类型
interface AreaInfo {
  areaName: string // 区域名称
  detailAddress: string // 区域地址
  phones: string // 联系电话
}
// 接口返回的基础信息数据类型（包含单/多区域所有字段）
interface BaseInfoData {
  area?: AreaInfo // 单区域信息（可选，多区域时无）
  deviceCount: number // 总设备数
  areaCount?: number // 多区域-包含区域数（可选）
  deviceCountOn?: number // 多区域-正常设备数（可选）
  deviceCountOff?: number // 多区域-离线设备数（可选）
  deviceCountError?: number // 多区域-异常设备数（可选）
  areaNames?: string // 多区域-区域名称拼接（可选）
}

const dataInfo = ref<BaseInfoData>({
  area: {
    areaName: "隆庆批发超市",
    detailAddress: "江苏省徐州市泉山区泰山路88号",
    phones: "18888881234"
  },
  deviceCount: 0
})

// ===================== 生命周期 & 核心业务逻辑 =====================
onMounted(async () => {
  await nextTick()
  // 区域ID为空时不发起请求
  if (props.areaIds.length === 0) return

  try {
    // 发起基础信息请求
    const res = await baseInfo({
      areaIds: props.areaIds,
      startTime: props.startTime,
      endTime: props.endTime,
      timeType: props.timeType,
      splType: props.type
    })
    // 更新数据（接口返回值兜底，避免空值）
    dataInfo.value = res.data || { deviceCount: 0 }
  } catch (error) {
    ElMessage.error('基础信息数据请求失败，请重试')
    console.error('基本信息组件请求异常：', error)
  }
})
</script>

<style lang="scss" scoped>
.margin-top {
  margin-top: 10px;
}

.modules-item-desc {
  line-height: 1.6;
  color: #666;
  margin-bottom: 8px;
}

// 微调描述项样式，提升排版一致性
:deep(.el-descriptions__content) {
  word-break: break-all;
}
</style>