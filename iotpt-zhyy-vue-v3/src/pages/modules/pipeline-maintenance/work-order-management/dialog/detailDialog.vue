<template>
	<div class="detail-type flex align-center">
		<div class="type" :class="{activeType: activeType == 'baseInfo'}" @click="activeType = 'baseInfo'">详细信息</div>
		<div class="type" :class="{activeType: activeType == 'processInfo'}" @click="activeType = 'processInfo'">流程节点</div>
	</div>
	<div class="detail-content">
		<BaseInfo v-if="activeType === 'baseInfo'" :row="row" />
		<ProcessInfo v-else :row="row" />
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import ProcessInfo from "./processInfo.vue";
import BaseInfo from "./baseInfo.vue";
import { ElDescriptions, ElDescriptionsItem,ElPagination, ElTable, ElTableColumn, ElButton } from "element-plus";

const props = defineProps({
	id: number().def(),
	projectId: {
		type: Number,
		default: 0
	},
	row: {
		type: Object,
		default: {}
	}
});

const activeType = ref('baseInfo');
const list = ref([{}]);
// 分页相关数据
const currentPage = ref(1); // 当前页码
const pageSize = ref(10); // 每页显示数量
const total = ref(0); // 总数据量

const fetchData = () => {
	// 调用获取数据的接口，将结果赋值给 list.value
};
// 页码改变事件处理函数
const handleCurrentChange = (page: number) => {
	currentPage.value = page;
	fetchData();
};

// 每页显示数量改变事件处理函数
const handleSizeChange = (size: number) => {
	pageSize.value = size;
	currentPage.value = 1; // 重置页码为第一页
	fetchData();
};

const detail = ref({});
// 详情页数据
if (props.id) {
	// getEntityOperation(props.id).then((res) => {
	// 	detail.value = res
	// });
}
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
