<template>
	<div>
		<div class="base-title m-t-12px">告警信息</div>
		<ElDescriptions :column="2" border>
			<ElDescriptionsItem label="告警设备" content-class-name="my-content">
				{{ entityInfo.deviceName }}
			</ElDescriptionsItem>
			<ElDescriptionsItem label="告警位置" content-class-name="my-content">
				{{ entityInfo.areaName }}
			</ElDescriptionsItem>
			<ElDescriptionsItem label="告警时间" content-class-name="my-content">
				{{ formatDateStr(entityInfo.alarmTime) }}
			</ElDescriptionsItem>
			<ElDescriptionsItem label="告警等级" content-class-name="my-content">
				{{ entityInfo.alarmLevelName }}
			</ElDescriptionsItem>
			<ElDescriptionsItem label="告警类型" content-class-name="my-content">
				{{ entityInfo.alarmCategoryName }}
			</ElDescriptionsItem>
			<ElDescriptionsItem label="处理状态" content-class-name="my-content" v-if="entityInfo.handleStatus == 1">
				误报
			</ElDescriptionsItem>
			<ElDescriptionsItem label="处理状态" content-class-name="my-content" v-if="entityInfo.handleStatus == 2">
				上报
			</ElDescriptionsItem>
			<ElDescriptionsItem label="处理状态" content-class-name="my-content" v-if="entityInfo.handleStatus == 3">
				忽略
			</ElDescriptionsItem>

			<ElDescriptionsItem label="告警内容" content-class-name="my-content">
				{{ entityInfo.alarmContentStr }}
			</ElDescriptionsItem>
		</ElDescriptions>
		<div class="base-title m-t-12px">告警记录</div>
		<ElTable
			v-loading="listLoading"
			:data="list"
			:element-loading-text="elementLoadingText"
			height="calc(100vh - 500px)"
			border
		>
			<ElTableColumn label="序号" width="60" align="center">
				<template #default="scope">
					{{ del(recordForm, scope.$index, (recordForm.pageNo - 1) * recordForm.pageSize + scope.$index + 1) }}
				</template>
			</ElTableColumn>
			<ElTableColumn
				show-overflow-tooltip
				prop="deviceName"
				label="告警设备"
				align="center"
				width="160"
			></ElTableColumn>
			<ElTableColumn
				show-overflow-tooltip
				prop="areaName"
				label="告警区域"
				align="center"
				min-width="160"
			></ElTableColumn>
			<ElTableColumn
				show-overflow-tooltip
				prop="alarmContentStr"
				label="告警内容"
				align="center"
				min-width="160"
			></ElTableColumn>
			<ElTableColumn
				show-overflow-tooltip
				prop="alarmTime"
				label="告警时间"
				:formatter="formatDate"
				align="center"
				width="160"
			></ElTableColumn>
			<ElTableColumn show-overflow-tooltip prop="alarmLevelName" label="告警等级" width="100" align="center">
				<template #default="scope">
					<ElTag type="warning" v-if="scope.row.alarmLevel == 1">{{ scope.row.alarmLevelName }}</ElTag>
					<ElTag type="warning" v-else-if="scope.row.alarmLevel == 2">
						{{ scope.row.alarmLevelName }}
					</ElTag>
					<ElTag type="warning" v-else-if="scope.row.alarmLevel == 3">
						{{ scope.row.alarmLevelName }}
					</ElTag>
					<ElTag type="danger" v-else>{{ scope.row.alarmLevelName }}</ElTag>
				</template>
			</ElTableColumn>
			<ElTableColumn show-overflow-tooltip prop="handleStatus" label="处理状态" align="center" width="120">
				<template #default="scope">
					<ElTag size="small" type="warning" v-if="scope.row.handleStatus == 0">未处理</ElTag>
					<ElTag size="small" type="warning" v-if="scope.row.handleStatus == 1">误报</ElTag>
					<ElTag size="small" type="warning" v-if="scope.row.handleStatus == 2">上报</ElTag>
					<ElTag size="small" type="warning" v-if="scope.row.handleStatus == 3">忽略</ElTag>
				</template>
			</ElTableColumn>
		</ElTable>
		<ElPagination
            style="margin-top: 12px;display: flex;justify-content: center;"
			background
			:current-page="recordForm.pageNo"
			:page-size="recordForm.pageSize"
			layout="total, sizes, prev, pager, next, jumper"
			@size-change="handleSizeChange"
			@current-change="pageCurrentChange"
			:total="total"
		></ElPagination>
	</div>
</template>

<script setup lang="ts">
import { getRealTimeWarnView, getHistoryList } from "@/api/alarm-center/warnInfo";
import { ElDescriptionsItem, ElDescriptions, ElTable, ElTableColumn, ElPagination, ElTag } from "element-plus";
import { ref } from "vue";

const props = defineProps({
	id: {
		type: Number,
		default: 0
	},
	row: {
		type: Object,
		default: () => {}
	}
});

const loading = ref(false);
const disabled = ref(false);
const title = ref("");
const dialogFormVisible = ref(false);
const recordForm = ref({
	pageNo: 1,
	pageSize: 20,
	deviceId: 0,
	deviceType: 0,
	deviceSource: 0
});
const total = ref(10);
const listLoading = ref(false);
const elementLoadingText = ref("正在加载...");
const entityInfo = ref({});
const list = ref([]);

// 删除序号中的0
const del = (val1, val2, val) => {
    console.log('val1', val1);
	console.log('val2', val2);
	console.log('val', val);
	return val;
}

const getWarnRecordsData = async () => {
	listLoading.value = true;
	const data = await getHistoryList(recordForm.value);
    console.log('333333333333333', data);
	list.value = data.records;
	total.value = data.total;
	listLoading.value = false;
};
if (props.row) {
	title.value = "查看详情";
	entityInfo.value = props.row;
	recordForm.value.deviceId = props.row.deviceId;
	recordForm.value.deviceType = props.row.deviceType;
	recordForm.value.deviceSource = props.row.deviceSource;
	getWarnRecordsData();
}
// 切换页面显示个数操作
const handleSizeChange = (val) => {
	recordForm.value.pageNo = 1;
	recordForm.value.pageSize = val;
	getWarnRecordsData();
};
// 切换页面页码
const pageCurrentChange = (val) => {
	recordForm.value.pageNo = val;
	getWarnRecordsData();
};
// 格式化日期
const formatDate = (row, column) => {
	// 获取单元格数据
	let data = row[column.property];
	if (data == null) {
		return null;
	}
	let dt = new Date(data);

	const year = dt.getFullYear();
	const month = ("0" + (dt.getMonth() + 1)).slice(-2);
	const day = ("0" + dt.getDate()).slice(-2);
	const hour = ("0" + dt.getHours()).slice(-2);
	const minute = ("0" + dt.getMinutes()).slice(-2);
	const second = ("0" + dt.getSeconds()).slice(-2);
	return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
	// return dt.getFullYear() + '-' + (dt.getMonth() + 1) + '-' + dt.getDate() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds()
};
// 格式化日期字符串
const formatDateStr = (data) => {
	// 获取单元格数据
	if (data == null) {
		return "";
	}
	let dt = new Date(data);

	const year = dt.getFullYear();
	const month = ("0" + (dt.getMonth() + 1)).slice(-2);
	const day = ("0" + dt.getDate()).slice(-2);
	const hour = ("0" + dt.getHours()).slice(-2);
	const minute = ("0" + dt.getMinutes()).slice(-2);
	const second = ("0" + dt.getSeconds()).slice(-2);
	return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
	// return dt.getFullYear() + '-' + (dt.getMonth() + 1) + '-' + dt.getDate() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds()
};
</script>
<style scoped lang="scss">
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
	display: flex;
	align-items: center;
	justify-content: space-between;
}
</style>
