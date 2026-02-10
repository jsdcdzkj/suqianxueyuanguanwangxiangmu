<template>
	<div class="h-full flex gap-12px p-12px">
		<div class="flex-1 custom-table h-full flex flex-col w-full relative">
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
				<PendingAssigned v-if="activeTab == 'dzpCount'" />
				<PendingDeal v-if="activeTab == 'dclCount'" />
				<FinishTask v-if="activeTab == 'yclCount'" />
				<TaskList v-if="activeTab == 'wsbCount'" />
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
	import {
		statusCount,
	} from "@/api/pipeline-maintenance/work-order";
	import PendingAssigned from "@/pages/modules/pipeline-maintenance/work-order-management/components/pendingAssigned.vue";
	import TaskList from "@/pages/modules/pipeline-maintenance/work-order-management/components/taskList.vue";
	import PendingDeal from "@/pages/modules/pipeline-maintenance/work-order-management/components/pendingDeal.vue";
	import FinishTask from "@/pages/modules/pipeline-maintenance/work-order-management/components/finishTask.vue";

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
</style>
