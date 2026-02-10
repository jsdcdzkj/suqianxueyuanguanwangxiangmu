<template>
	<div>
		<div class="base-title">基本信息</div>
		<ElDescriptions :column="2" border label-width="120px">
			<ElDescriptionsItem label="告警编码" label-class-name="my-label">{{ detail.create_user_name || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="告警来源" label-class-name="my-label">{{ detail.severityName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="告警类型" label-class-name="my-label">{{ detail.createTime || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="告警等级" label-class-name="my-label">{{ detail.sourceName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="告警时间" label-class-name="my-label">{{ detail.reviewName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="告警区域" label-class-name="my-label">{{
				detail.pro_project_name || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="事件内容" label-class-name="my-label">{{ detail.moduleName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="告警状态" label-class-name="my-label">{{ detail.featureName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="处理状态" label-class-name="my-label">{{ detail.statusName || "--" }}</ElDescriptionsItem>
		</ElDescriptions>
		<div class="base-title m-t-12px">告警记录</div>
		<ElTable
			border
			:data="list"
		>
			<ElTableColumn
				label="序号"
				type="index"
				width="80px"
				align="center"
			></ElTableColumn>
			<ElTableColumn label="告警来源" align="center" prop="suspend_user_name"></ElTableColumn>
			<ElTableColumn label="告警类型" align="center" prop="recovery_user_name"></ElTableColumn>
			<ElTableColumn label="告警等级" align="center" prop="suspend_time"></ElTableColumn>
			<ElTableColumn label="时间" align="center">
				<template #default="scope"> 
				</template>
			</ElTableColumn>
			<ElTableColumn label="区域" align="center" prop="recovery_time"
				></ElTableColumn
			>
			<ElTableColumn label="事件内容" align="center" prop="suspendHours"></ElTableColumn>
			<ElTableColumn label="告警状态" align="center" prop="suspendHours"></ElTableColumn>
			<ElTableColumn label="操作" align="center" prop="suspendHours" width="280">
				<template #default="scope"> 
					<ElButton type="primary" link size="mini">查看</ElButton>
					<ElButton type="primary" link size="mini">上报</ElButton>
					<ElButton type="primary" link size="mini">忽略</ElButton>
					<ElButton type="primary" link size="mini">误报</ElButton>
					<ElButton type="delete" link size="mini">删除</ElButton>
				</template>
			</ElTableColumn>
		</ElTable>
		<div class="flex justify-center m-t-12px">
			<ElPagination
				background
				layout="total,prev, pager, next, jumper"
				:total="total"
				@current-change="handleCurrentChange"
				@size-change="handleSizeChange"
				:currentPage="currentPage"
				:pageSize="pageSize"
			></ElPagination>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
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
</style>
