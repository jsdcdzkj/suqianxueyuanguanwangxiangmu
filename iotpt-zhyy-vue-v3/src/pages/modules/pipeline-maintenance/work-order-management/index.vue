<template>
	<div class="h-full flex gap-12px p-12px">
		<div class="flex-1 custom-table h-full flex flex-col w-full relative">
			<div class="task-btn-wrap">
				<!-- 导出按钮 -->
				<ElButton v-if="activeTab == 'yclCount'" :icon="Upload" @click="handleExport">导出</ElButton>
				<!-- 上报按钮 -->
				<ElButton v-if="activeTab == 'wsbCount'" :icon="DocumentAdd" @click="handleReport">上报</ElButton>
			</div>
			<ElTabs v-model="activeTab" class="demo-tabs" @tab-click="getCount">
				<ElTabPane
					v-for="work in workOptions"
					:key="work.value"
					:label="work.label"
					:name="work.value"
				>
					<template #label>
						<span class="flex items-center">
							{{ work.label }}
							<span class="span">({{ work.count }})</span>
						</span>
					</template>
				</ElTabPane>
			</ElTabs>
			<div class="flex-1" style="margin: -10px">
				<PendingAssigned ref="pendingAssignedEl" v-if="activeTab == 'dzpCount'" @refreshCount="getCount" />
				<PendingDeal ref="pendingDealEl" v-if="activeTab == 'dclCount'" @refreshCount="getCount" />
				<FinishTask ref="finishTaskEl" v-if="activeTab == 'yclCount'" @refreshCount="getCount" />
				<TaskList ref="taskListEl" v-if="activeTab == 'wsbCount'" @refreshCount="getCount" />
			</div>
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { ref } from "vue";
	import {
		ElTabs,
		ElTabPane
	} from "element-plus";
	import { DocumentAdd, Upload } from "@element-plus/icons-vue";
	import {
		statusCount,
	} from "@/api/pipeline-maintenance/work-order";
	import PendingAssigned from "@/pages/modules/pipeline-maintenance/work-order-management/components/pendingAssigned.vue";
	import TaskList from "@/pages/modules/pipeline-maintenance/work-order-management/components/taskList.vue";
	import PendingDeal from "@/pages/modules/pipeline-maintenance/work-order-management/components/pendingDeal.vue";
	import FinishTask from "@/pages/modules/pipeline-maintenance/work-order-management/components/finishTask.vue";
	import TaskEditDialog from "@/pages/modules/pipeline-maintenance/work-order-management/dialog/taskEditDialog.vue";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";

	const activeTab = ref("dzpCount");
	const workOptions = ref([
		{ label: "待指派", count: 0, value: "dzpCount" },
		{ label: "待处理", count: 0, value: "dclCount" },
		{ label: "已完成", count: 0, value: "yclCount" },
		{ label: "任务库", count: 0, value: "wsbCount" }
	]);

	// 获取工单得数量
	const getCount = async () => {
		const res = await statusCount({});
		console.log('99999999999res', res)
		workOptions.value.forEach((item) => {
			item.count = res[item.value] || 0;
		});
	}
	getCount()
	// 导出
	const finishTaskEl = ref()
	const handleExport = () => {
		finishTaskEl.value.handleExport()
	}
	// 上报
	const taskListEl = ref(null)
	const handleReport = () => {
		createDrawerAsync(
			{ title: "上报", width: '960px', showNext: false, showZanCun: true },
			{},
			<TaskEditDialog />
		).then(() => {
			getCount();
			// 刷新任务库
			taskListEl.value.refreshPageData()
		});
	}
</script>

<style scoped>
	:deep(.el-tabs__item) {
		font-size: 16px;
		height: 48px;
		padding: 0 32px !important;
		border-right: 1px solid rgba(0, 0, 0, 0.06);
	}
	:deep(.is-active) {
		background: rgba(52, 91, 173, 0.1);
		border-bottom: 3px solid #345bad;
		color: #345bad;
	}
	:deep(.el-tabs__nav-wrap::after) {
		height: 1px;
	}
	:deep(.el-tabs__header) {
		margin-bottom: 0;
	}
	.task-btn-wrap {
		position: absolute;
		top:10px;
		right: 10px;
		z-index: 10;
	}
</style>
