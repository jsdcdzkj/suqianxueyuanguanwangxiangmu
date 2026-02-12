<template>
	<div class="detail-type flex align-center">
		<div class="type" :class="{activeType: activeType == 'baseInfo'}" @click="activeType = 'baseInfo'">详细信息</div>
		<div class="type" :class="{activeType: activeType == 'processInfo'}" @click="activeType = 'processInfo'">流程节点</div>
	</div>
	<div class="detail-content">
		<BaseInfo v-if="activeType === 'baseInfo'" :id="props.id" />
		<ProcessInfo v-else  :id="props.id" />
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import ProcessInfo from "./processInfo.vue";
import BaseInfo from "./baseInfo.vue";
import { inspectionData } from "@/api/pipeline-maintenance/work-order";
import { ElDescriptions, ElDescriptionsItem,ElPagination, ElTable, ElTableColumn, ElButton } from "element-plus";

const props = defineProps({
	id: number().def(),
});

const activeType = ref('baseInfo');

const detail = ref({});
// 详情页数据
// if (props.id) {
// 	inspectionData({missionId: props.id, isHandle: 1}).then((res) => {
// 		console.log('详情页数据', res)
// 		detail.value = res
// 	});
// }
</script>
<style lang="scss" scoped>
:deep(.my-label) {
	font-size: 14px;
	font-weight:500;
	color: rgba(0,0,0,0.85);
	background: #F8F8F8!important;
}
.base-title {
	height: 40px;
	line-height: 40px;
	margin-bottom: 12px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
	padding:0 12px;
	font-size: 16px;
	color: rgba(0,0,0,0.85);
	margin-bottom: 12px;
}
.detail-type {
	font-size: 14px;
	font-weight:500;
	color: rgba(0,0,0,0.85);
	height: 40px;
	border: 1px solid rgba(0,0,0,0.15);
	text-align: center;
	cursor: pointer;
	border-radius:4px;
	margin-bottom: 12px;

	.type {
		flex:1;
		line-height: 40px;
	}
	.activeType {
		color: #fff;
		background:#345BAD;
	}
}
</style>
