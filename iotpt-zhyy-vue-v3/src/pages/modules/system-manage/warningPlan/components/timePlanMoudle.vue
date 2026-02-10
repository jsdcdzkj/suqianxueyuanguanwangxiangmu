<template>
	<el-table
		v-loading="listLoading"
		:data="list"
		:element-loading-text="elementLoadingText"
		height="calc(100vh - 120px)"
		border
	>
		<el-table-column label="序号" width="60" align="center">
			<template #default="scope">
				{{ (recordForm.pageNum - 1) * recordForm.pageSize + scope.$index + 1 }}
			</template>
		</el-table-column>

		<el-table-column
			show-overflow-tooltip
			prop="name"
			label="模板名称"
			align="center"
			min-width="160"
		></el-table-column>
		<el-table-column prop="areaName" label="操作" align="center" width="160">
			<template #default="scope">
				<el-button type="success" plain size="small" @click="handleEdit(scope.row)">编辑</el-button>
				<el-button type="danger" plain size="small" @click="handleDel(scope.row.id)">删除</el-button>
			</template>
		</el-table-column>
	</el-table>
	<el-pagination
		background
		:current-page="recordForm.pageNum"
		:page-size="recordForm.pageSize"
		layout="total, sizes, prev, pager, next, jumper"
		@size-change="handleSizeChange"
		@current-change="pageCurrentChange"
		:total="total"
	></el-pagination>
</template>

<script setup lang="tsx">
	import { ref, onMounted } from "vue";
	import { ElMessage, ElMessageBox, ElTable, ElTableColumn, ElButton } from "element-plus";
	import { alarmPlanTimePage, delTime } from "@/api/setting/warnInfo";
	import AddTimeMoudle from "./addTimeMoudle.vue";
	import { createModelAsync } from "@/core/dialog";

	const list = ref([]);
	const total = ref(0);
	const listLoading = ref(false);
	const elementLoadingText = ref("正在加载...");
	const recordForm = ref({
		pageNum: 1,
		pageSize: 20
	});

	const getWarnRecordsData = async () => {
		listLoading.value = true;
		try {
			const data = await alarmPlanTimePage({
				pageNum: recordForm.value.pageNum,
				pageSize: recordForm.value.pageSize
			});
			console.log("data:", data);
			list.value = data.records;
			total.value = data.total;
		} finally {
			listLoading.value = false;
		}
	};

	const handleEdit = (row) => {
		createModelAsync(
			{
				title: "编辑时间计划模板",
				width: "1150px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<AddTimeMoudle
				rowInfo={row}
				onSave={() => {
					getWarnRecordsData();
				}}
			/>
		);
	};

	const handleSizeChange = (val) => {
		recordForm.value.pageNum = 1;
		recordForm.value.pageSize = val;
		getWarnRecordsData();
	};

	const pageCurrentChange = (val) => {
		recordForm.value.pageNum = val;
		getWarnRecordsData();
	};

	const handleDel = (id) => {
		ElMessageBox.confirm("此操作将删除该条数据, 是否继续?", "提示", {
			confirmButtonText: "确定",
			cancelButtonText: "取消",
			type: "warning"
		}).then(() => {
			delTime({ id }).then(() => {
				ElMessage.success("删除成功");
				getWarnRecordsData();
			});
		});
	};

	onMounted(() => {
		getWarnRecordsData();
	});
</script>
