<template>
	<div class="h-full flex gap-12px p-12px">
		<div class="flex-1 custom-table h-full flex flex-col w-full relative">
			<div v-if="showAdd">
				<StructureCard title="告警预案配置">
					<template #actions>
						<ElButton type="success" @click="handleAdd" :icon="Plus">新建</ElButton>
						<ElButton type="danger" @click="handleBatchDel" :icon="Delete">删除</ElButton>
					</template>
					<div class="relative">
						<div class="filter-container mb-12px">
							<ElInput
								v-model="queryForm.keyword"
								placeholder="搜索告警内容"
								@keyup.enter="queryData"
								style="width: 240px; margin-right: 8px"
							>
								<template #suffix>
									<ElIcon><Search /></ElIcon>
								</template>
							</ElInput>
							<ElButton type="primary" @click="queryData" :icon="Search">查询</ElButton>
							<ElButton @click="handleReset" :icon="Refresh">重置</ElButton>
						</div>
						<ElTable
							v-loading="listLoading"
							:data="list"
							:element-loading-text="elementLoadingText"
							height="calc(100vh - 288px)"
							border
							@selection-change="handleSelectionChange"
							tooltip-effect="dark myTooltips"
						>
							<template #empty>
								<ElEmpty :image-size="160"></ElEmpty>
							</template>
							<ElTableColumn type="selection" width="55" align="center"></ElTableColumn>
							<ElTableColumn label="序号" width="60" align="center">
								<template #default="scope">
									{{ (queryForm.pageNo - 1) * queryForm.pageSize + scope.$index + 1 }}
								</template>
							</ElTableColumn>
							<ElTableColumn
								show-overflow-tooltip
								prop="planName"
								label="告警预案名称"
								min-width="100"
								align="center"
							></ElTableColumn>
							<ElTableColumn
								show-overflow-tooltip
								prop="planTimeConfigName"
								label="时间计划"
								width="160"
								align="center"
							></ElTableColumn>
							<ElTableColumn
								show-overflow-tooltip
								prop="alarmContentStr"
								label="告警内容"
								min-width="260"
								align="center"
							></ElTableColumn>
							<ElTableColumn
								show-overflow-tooltip
								prop="createUserName"
								label="创建人"
								width="160"
								align="center"
							></ElTableColumn>
							<ElTableColumn
								show-overflow-tooltip
								prop="enable"
								label="启用状态"
								align="center"
								width="120"
							>
								<template #default="scope">
									<ElSwitch
										v-model="scope.row.enable"
										:active-value="1"
										:inactive-value="0"
										active-color="#409EFF"
										inactive-color="#C0CCDA"
										@change="(val) => handleEnableChange(val, scope.row.id)"
									></ElSwitch>
								</template>
							</ElTableColumn>
							<ElTableColumn show-overflow-tooltip label="操作" width="220" align="center">
								<template #default="scope">
									<ElButton type="primary" plain size="small" @click="handleAdd(scope.row, 1)">
										详情
									</ElButton>
									<ElButton type="success" plain size="small" @click="handleAdd(scope.row)"
										>编辑</ElButton
									>
									<ElButton type="danger" plain size="small" @click="handleDel(scope.row.id)"
										>删除</ElButton
									>
								</template>
							</ElTableColumn>
						</ElTable>
						<div class="pagination-container flex justify-end mt-14px">
							<ElPagination
								background
								:current-page="queryForm.pageNo"
								:page-size="queryForm.pageSize"
								layout="total, sizes, prev, pager, next, jumper"
								@size-change="handleSizeChange"
								@current-change="pageCurrentChange"
								:total="total"
							></ElPagination>
						</div>
					</div>
				</StructureCard>
			</div>
			<AddCase
				v-else
				ref="addCase"
				@back="handleBack"
				@fetch-data="queryData"
				:editId="editId"
				:viewStatus="viewStatus"
			/>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted } from "vue";
	import {
		ElMessage,
		ElMessageBox,
		ElPagination,
		ElTag,
		ElSwitch,
		ElEmpty,
		ElIcon,
		ElButton,
		ElForm,
		ElFormItem,
		ElInput,
		ElSelect,
		ElOption,
		ElUpload,
		ElTable,
		ElTableColumn
	} from "element-plus";
	import { Plus, Delete, Search, Refresh } from "@element-plus/icons-vue";
	import { getRedisDictList } from "@/api/setting/sysDict";
	import { alarmPlanPage, getWarnTypeDict, saveAlarmPlan, batchDel, switchEnable } from "@/api/setting/warnInfo";
	import AddCase from "./components/addCase.vue";
	import StructureCard from "@/components/card/StructureCard";

	// 响应式数据
	const queryForm = ref({
		pageNo: 1,
		pageSize: 20,
		keyword: ""
	});
	const total = ref(10);
	const listLoading = ref(false);
	const elementLoadingText = ref("正在加载...");
	const list = ref([]);
	const warnLevelList = ref([]);
	const allBuildListList = ref([]);
	const warnLevelOptions = ref([]);
	const showAdd = ref(true);
	const multipleSelection = ref([]);
	const viewStatus = ref(false);
	const editId = ref(null);

	// 方法定义
	const handleBatchDel = () => {
		if (multipleSelection.value.length === 0) {
			ElMessage.warning("请至少选择一条数据");
			return;
		}
		ElMessageBox.confirm("此操作将删除选中数据, 是否继续?", "提示", {
			confirmButtonText: "确定",
			cancelButtonText: "取消",
			type: "warning"
		}).then(() => {
			const ids = multipleSelection.value.map((item) => item.id);
			batchDel(ids).then(() => {
				ElMessage.success("删除成功");
				queryData();
			});
		});
	};

	const handleSelectionChange = (val) => {
		multipleSelection.value = val;
	};

	const handleEnableChange = async (value, id) => {
		try {
			await switchEnable({ id, enable: value });
			ElMessage.success("状态更新成功");
		} catch (error) {
			console.error("更新启用状态失败:", error);
			ElMessage.error("更新启用状态失败，请稍后重试");
			// 失败时恢复原值
			const row = list.value.find((item) => item.id === id);
			if (row) {
				row.enable = !value;
			}
		}
	};

	const handleSizeChange = (val) => {
		queryForm.value.pageNo = 1;
		queryForm.value.pageSize = val;
		fetchData();
	};

	const pageCurrentChange = (val) => {
		queryForm.value.pageNo = val;
		fetchData();
	};

	const handleAdd = (row, type) => {
		editId.value = row ? row.id : null;
		viewStatus.value = !!type;
		showAdd.value = false;
	};

	const handleDel = (id) => {
		ElMessageBox.confirm("此操作将删除该条数据, 是否继续?", "提示", {
			confirmButtonText: "确定",
			cancelButtonText: "取消",
			type: "warning"
		}).then(() => {
			batchDel([id]).then(() => {
				ElMessage.success("删除成功");
				queryData();
			});
		});
	};

	const handleBack = () => {
		showAdd.value = true;
	};

	const getWarnLevels = async () => {
		const data = await getWarnTypeDict(["4"]);
		warnLevelList.value = data;
	};

	const queryData = () => {
		queryForm.value.pageNo = 1;
		fetchData();
	};

	const fetchData = async () => {
		listLoading.value = true;
		const data = await alarmPlanPage(queryForm.value);
		list.value = data.records;
		total.value = data.total;
		setTimeout(() => {
			listLoading.value = false;
		}, 300);
	};
	const handleReset = () => {
		queryForm.value = {
			pageNo: 1,
			pageSize: 20,
			keyword: ""
		};
		fetchData();
	};

	// 生命周期钩子
	onMounted(async () => {
		const data = await getRedisDictList({ dictType: "warnLevel" });
		warnLevelOptions.value = data;
		fetchData(true);
		getWarnLevels();
	});
</script>

<style scoped lang="scss">
	:deep(.el-table .warning-row) {
		background: #fdf7f7;
	}
	:deep(.el-table tbody .warning-row:hover > td) {
		background-color: transparent !important;
	}
	.myTooltips {
		max-width: 1000px;
		line-height: 1.5;
		white-space: pre-line;
	}
</style>
