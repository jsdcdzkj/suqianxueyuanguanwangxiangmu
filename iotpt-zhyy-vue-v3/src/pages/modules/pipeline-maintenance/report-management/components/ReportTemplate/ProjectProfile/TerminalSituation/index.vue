<template>
	<div>
	  <Title
		:index="index"
		:title="title"
		:subTitle="subTitle"
		:subIndex="subIndex"
	  />
	  <!-- 表格添加空数据兜底，列数据空值兜底 -->
	  <ElTable :data="list" border v-loading="loading" empty-text="暂无终端设备数据">
		<ElTableColumn label="设备类型" prop="type" />
		<ElTableColumn label="总数" prop="num">
		  <template #default="scope">{{ scope.row.num || 0 }}</template>
		</ElTableColumn>
		<ElTableColumn label="启用数" prop="open_num">
		  <template #default="scope">{{ scope.row.open_num || 0 }}</template>
		</ElTableColumn>
		<ElTableColumn label="正常" prop="normal">
		  <template #default="scope">{{ scope.row.normal || 0 }}</template>
		</ElTableColumn>
		<ElTableColumn label="失联" prop="unline">
		  <template #default="scope">{{ scope.row.unline || 0 }}</template>
		</ElTableColumn>
	  </ElTable>
	</div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted, nextTick } from 'vue'
  import { ElMessage } from 'element-plus'
  // 按需导入Element Plus组件（Vue3+TS规范）
  import { ElTable, ElTableColumn } from 'element-plus'
  // 导入业务API
  import { cmdInfo } from '@/api/pipeline-maintenance/report-management'
  import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";
  // 原mixins的Common.js，建议解构导入所需方法替代mixins
  // import { 公共方法 } from '../../common.js'
  
  defineOptions({
    name: 'TerminalSituation',
    cname: '终端情况'
  })
  // 严格定义所有入参类型，和原组件this上的属性一一对应
  const props = defineProps<{
	index: string | number // 索引
	title: string // 主标题
	subTitle: string // 子标题
	subIndex: string | number // 子索引
	areaIds: (string | number)[] // 区域ID数组
	startTime: string // 开始时间
	endTime: string // 结束时间
	timeType: '日' | '月' | '年' // 时间类型（修复原代码timeTypes拼写错误）
  }>()
  
  // ===================== 定义TS接口（数据类型约束）=====================
  // 表格行数据类型（和渲染字段严格匹配）
  export interface TerminalItem {
	type: string // 设备类型
	num: number // 总数
	open_num: number // 启用数
	normal: number // 正常数
	unline: number // 失联数
  }
  // 接口返回的result值类型：key为设备类型，value为数字数组[总数,启用数,正常数,失联数]
  interface CmdInfoResult {
	[deviceType: string]: number[]
  }
  // 接口返回的整体数据类型
  interface CmdInfoResponseData {
	result: CmdInfoResult
  }
  // 接口请求参数类型（抽离便于API层复用）
  export interface CmdInfoParams {
	areaIds: (string | number)[]
	startTime: string
	endTime: string
	timeType: '日' | '月' | '年'
  }
  
  // ===================== 响应式数据定义 =====================
  // 表格列表数据，指定严格类型TerminalItem[]，初始化值和原代码一致
  const list = ref<TerminalItem[]>([
	{
	  type: "微断设备",
	  num: 20,
	  open_num: 15,
	  normal: 15,
	  unline: 0
	},
	{
	  type: "烟感设备",
	  num: 13,
	  open_num: 13,
	  normal: 13,
	  unline: 0
	}
  ])
  // 加载状态（可选，为表格添加加载动画，提升交互体验）
  const loading = ref(false)
  
  // 保留原组件的cname标识（父组件<component :is="child.type">动态渲染必备）
  defineExpose({
	cname: '终端情况'
  })
  
  // ===================== 生命周期 & 核心业务逻辑 =====================
  onMounted(async () => {
	// 替代Vue2的this.$nextTick，确保DOM渲染完成后请求数据
	await nextTick()
	// 区域ID数组无值时，不发起请求，直接返回
	if (props.areaIds.length === 0) return
  
	try {
	  loading.value = true
	  // 发起接口请求，修复timeTypes为timeType，和Props保持一致
	  const res = await cmdInfo({
		areaIds: props.areaIds,
		startTime: props.startTime,
		endTime: props.endTime,
		timeType: props.timeType
	  })
	  const resData = res.data as CmdInfoResponseData
	  // 处理接口返回数据：对象转数组，和原逻辑完全一致
	  const tableData: TerminalItem[] = []
	  // TS规范遍历对象key，替代for...in（避免遍历原型链属性）
	  Object.keys(resData.result).forEach(deviceType => {
		const itemValues = resData.result[deviceType]
		tableData.push({
		  type: deviceType,
		  num: itemValues[0] || 0,
		  open_num: itemValues[1] || 0,
		  normal: itemValues[2] || 0,
		  unline: itemValues[3] || 0
		})
	  })
	  // 更新表格数据
	  list.value = tableData
	} catch (error) {
	  // 错误捕获，避免组件崩溃，给出友好提示
	  ElMessage.error('终端设备数据请求失败，请重试')
	  console.error('终端情况组件请求异常：', error)
	} finally {
	  // 无论请求成功/失败，都关闭加载状态
	  loading.value = false
	}
  })
  </script>
  
  <style lang="scss" scoped>
  // 补充基础样式，和原组件无冲突
  :deep(.el-table) {
	width: 100%;
	margin-top: 8px;
  }
  </style>