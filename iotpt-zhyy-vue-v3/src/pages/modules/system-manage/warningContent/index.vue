<template>
	<div class="h-full flex gap-12px p-12px">
		<div class="flex-1 h-full flex flex-col w-full relative">
			<div class="mb-12px p-12px bg-white">
				<div class="warning-wrap">
					<div>
						<ElButton :icon="Plus" type="success" @click="handleAdd">新建</ElButton>
						<ElButton :icon="Upload" type="primary" @click="exportAlarm">批量导出</ElButton>
						<ElButton :icon="Download" type="warning" @click="handleDownload">模板导入</ElButton>
					</div>
					<div class="flex">
						<ElInput
							v-model="queryForm.keyword"
							placeholder="搜索告警内容/告警对象"
							:suffix-icon="Search"
							class="w-240px mr-8px"
							@keyup.enter="queryData"
							clearable
						/>
						<ElButton type="default" class="filter-btn" @click="handleFilter">
							<span>{{ show ? "收起过滤" : "展开过滤" }}</span>
							<ElIcon class="ml-8px"><CaretBottom /></ElIcon>
						</ElButton>
						<ElButton type="success" @click="handleClass">
							<span class="flex items-center"> 分类设置 </span>
						</ElButton>
						<ElButton type="primary" @click="handleGroup">
							<span class="flex items-center"> 分组设置 </span>
						</ElButton>
					</div>
				</div>
				<div class="search-info" v-if="show">
					<ElForm
						:inline="false"
						class="demo-form-inline"
						:model="queryForm"
						@submit.prevent
						ref="queryFormRef"
					>
						<ElRow :gutter="0">
							<ElCol :span="4">
								<ElFormItem>
									<ElSelect v-model="queryForm.alarmCategory" placeholder="告警类型" clearable>
										<ElOption
											v-for="item in warnLevelList"
											:key="item.id"
											:label="item.name"
											:value="item.id"
										/>
									</ElSelect>
								</ElFormItem>
							</ElCol>
							<ElCol :span="4">
								<ElFormItem>
									<ElSelect v-model="queryForm.alarmLevel" placeholder="告警等级" clearable>
										<ElOption
											v-for="item in warnLevelOptions"
											:key="item.id"
											:label="item.dictLabel"
											:value="item.dictValue"
										/>
									</ElSelect>
								</ElFormItem>
							</ElCol>
							<ElCol :span="4">
								<ElFormItem>
									<ElSelect v-model="queryForm.alarmGroup" placeholder="告警分组" clearable>
										<ElOption
											v-for="item in warnGroupList"
											:key="item.id"
											:label="item.name"
											:value="item.id"
										/>
									</ElSelect>
								</ElFormItem>
							</ElCol>
							<ElCol :span="4">
								<ElFormItem>
									<ElSelect v-model="queryForm.deviceType" placeholder="归属对象" clearable>
										<ElOption
											v-for="item in deviceTypeList"
											:key="item.id"
											:label="item.deviceTypeName"
											:value="item.id"
										/>
									</ElSelect>
								</ElFormItem>
							</ElCol>
							<ElCol :span="4">
								<ElFormItem>
									<ElSelect v-model="queryForm.enable" placeholder="启用状态" clearable>
										<ElOption
											v-for="item in enableStatus"
											:key="item.value"
											:label="item.label"
											:value="item.value"
										/>
									</ElSelect>
								</ElFormItem>
							</ElCol>
							<ElCol :span="4">
								<ElFormItem>
									<ElButton :icon="Search" type="primary" @click="queryData">查询</ElButton>
									<ElButton :icon="Refresh" @click="clearData">重置</ElButton>
								</ElFormItem>
							</ElCol>
						</ElRow>
					</ElForm>
				</div>
			</div>

			<StructureCard title="告警内容配置列表">
				<div class="relative">
					<ElTable
						v-loading="listLoading"
						:data="list"
						:element-loading-text="elementLoadingText"
						:height="tableHeight"
						border
						:row-class-name="tableRowClassName"
					>
						<template #empty>
							<ElEmpty :image-size="160" />
						</template>
						<ElTableColumn label="序号" width="60" align="center">
							<template #default="scope">
								{{ (queryForm.pageNo - 1) * queryForm.pageSize + scope.$index + 1 }}
							</template>
						</ElTableColumn>
						<ElTableColumn
							show-overflow-tooltip
							prop="alarmCode"
							label="内容编号"
							width="160"
							align="center"
						/>
						<ElTableColumn show-overflow-tooltip prop="alarmContent" label="告警内容" align="center" />
						<ElTableColumn
							show-overflow-tooltip
							prop="alarmCategoryName"
							label="告警类别"
							width="140"
							align="center"
						/>
						<ElTableColumn
							show-overflow-tooltip
							prop="alarmLevelName"
							label="告警等级"
							width="100"
							align="center"
						>
							<template #default="scope">
								<ElTag type="warning" v-if="scope.row.alarmLevel == 1">{{
									scope.row.alarmLevelName
								}}</ElTag>
								<ElTag type="warning" v-else-if="scope.row.alarmLevel == 2">
									{{ scope.row.alarmLevelName }}
								</ElTag>
								<ElTag type="warning" v-else-if="scope.row.alarmLevel == 3">
									{{ scope.row.alarmLevelName }}
								</ElTag>
								<ElTag type="danger" v-else>{{ scope.row.alarmLevelName }}</ElTag>
							</template>
						</ElTableColumn>
						<ElTableColumn
							show-overflow-tooltip
							prop="alarmGroupLabel"
							label="告警分组"
							width="160"
							align="center"
						/>
						<ElTableColumn
							show-overflow-tooltip
							prop="deviceTypeName"
							label="归属对象"
							width="160"
							align="center"
						/>
						<ElTableColumn show-overflow-tooltip prop="enable" label="启用状态" align="center" width="120">
							<template #default="scope">
								<ElSwitch
									v-model="scope.row.enable"
									:active-value="1"
									:inactive-value="0"
									active-color="#409EFF"
									inactive-color="#C0CCDA"
									@change="(val) => handleEnableChange(val, scope.row.id)"
								/>
							</template>
						</ElTableColumn>
						<ElTableColumn show-overflow-tooltip prop="remark" label="备注" align="center" />
						<ElTableColumn show-overflow-tooltip label="操作" width="210" align="center">
							<template #default="scope">
								<ElButton type="success" plain size="small" @click="handleView(scope.row)">
									详情
								</ElButton>
								<ElButton type="primary" plain size="small" @click="handleAdd(scope.row)"
									>编辑</ElButton
								>
								<ElButton type="danger" plain size="small" @click="handleDelete(scope.row.id)"
									>删除</ElButton
								>
							</template>
						</ElTableColumn>
					</ElTable>
					<div class="pagination-box">
						<ElPagination
							v-model:current-page="queryForm.pageNo"
							v-model:page-size="queryForm.pageSize"
							:background="true"
							:total="total"
							layout="total, sizes, prev, pager, next, jumper "
							@size-change="handleSizeChange"
							@current-change="pageCurrentChange"
						/>
					</div>
				</div>
			</StructureCard>
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { ref, reactive, computed, onMounted } from "vue";
	import StructureCard from "@/components/card/StructureCard";
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
	import { Plus, Upload, Download, Search, CaretBottom, Refresh } from "@element-plus/icons-vue";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import BasePage from "@/core/struct/page/base-page";
	import TableHeader from "@/core/struct/table/table-header";
	import { getRedisDictList } from "@/api/setting/sysDict";
	import {
		alarmContentPage,
		getWarnTypeDict,
		categoryList,
		alarmGroupList,
		deviceTypeList,
		saveAlarmContent,
		exportTemplate,
		exportAlarmContent
	} from "@/api/setting/warnInfo";
	import Detail from "./components/view.vue";
	import Grouping from "./components/grouping.vue";
	import Classing from "./components/classing.vue";
	import Model from "./components/model.vue";
	import AddWarningContent from "./components/addWarningContent.vue";

	// 响应式数据
	const queryForm = reactive({
		pageNo: 1,
		pageSize: 20,
		keyword: "",
		alarmCategory: "",
		alarmLevel: "",
		alarmGroup: "",
		deviceType: "",
		enable: ""
	});

	const enableStatus = ref([
		{
			value: 0,
			label: "未启用"
		},
		{
			value: 1,
			label: "启用"
		}
	]);

	const total = ref(10);
	const listLoading = ref(false);
	const elementLoadingText = ref("正在加载...");
	const list = ref([]);
	const show = ref(false);
	const warnLevelList = ref([]);
	const warnLevelOptions = ref([]);
	const warnGroupList = ref([]);
	const deviceTypeListSel = ref([]);
	const alarmTypeList = ref([]);

	// refs
	const queryFormRef = ref(null);

	// 计算属性
	const tableHeight = computed(() => {
		return show.value ? "calc(100vh - 372px)" : "calc(100vh - 304px)";
	});

	// 方法
	const handleEnableChange = async (value: number, id: number) => {
		try {
			await saveAlarmContent({ id, enable: value });
			ElMessage.success("状态更新成功");
		} catch (error) {
			console.error("更新启用状态失败:", error);
			ElMessage.error("更新启用状态失败，请稍后重试");
			const row = list.value.find((item) => item.id === id);
			if (row) {
				row.enable = !value;
			}
		}
	};

	const handleFilter = () => {
		show.value = !show.value;
	};

	const tableRowClassName = ({ row }: { row: any }) => {
		if (row.isNewData) {
			return "warning-row";
		}
		return "";
	};

	const handleAdd = (row?: any) => {
		createModelAsync(
			{
				title: row ? "编辑告警内容" : "新增告警内容",
				width: "800px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<AddWarningContent
				rowInfo={row}
				warnLevelOptions={warnLevelOptions.value}
				warnLevelList={warnLevelList.value}
				warnGroupList={warnGroupList.value}
				deviceTypeList={deviceTypeListSel.value}
				alarmTypeList={alarmTypeList.value}
			/>
		).then(() => {
			fetchData();
		});
	};

	const handleSizeChange = (val: number) => {
		queryForm.pageNo = 1;
		fetchData();
	};

	const pageCurrentChange = (val: number) => {
		fetchData();
	};

	const handleView = (row: any) => {
		createDrawerAsync(
			{
				title: "告警内容详情",
				width: "1000px",
				showConfirm: false
			},
			{},
			<Detail rowInfo={row} />
		);
	};

	const handleDelete = (id: number) => {
		ElMessageBox.confirm("此操作将删除该条数据, 是否继续?", "提示", {
			confirmButtonText: "确定",
			cancelButtonText: "取消",
			type: "warning"
		}).then(async () => {
			await saveAlarmContent({ id, isDel: 1 });
			ElMessage.success("删除成功");
			fetchData();
		});
	};

	const handleClass = () => {
		createDrawerAsync(
			{
				title: "分类设置",
				width: "960px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<Classing warnGroupList={warnGroupList.value} alarmTypeList={alarmTypeList.value} />
		).then(() => {
			fetchData();
		});
	};

	const handleGroup = () => {
		createDrawerAsync(
			{
				title: "分组设置",
				width: "960px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<Grouping />
		).then(() => {
			fetchData();
		});
	};

	const getWarnLevels = async () => {
		const [categoryRes, groupRes, deviceTypeRes] = await Promise.all([
			categoryList({}),
			alarmGroupList({}),
			deviceTypeList({})
		]);

		warnLevelList.value = categoryRes.data;
		warnGroupList.value = groupRes.data;
		deviceTypeListSel.value = deviceTypeRes.data;
	};

	const queryData = () => {
		queryForm.pageNo = 1;
		fetchData();
	};

	const fetchData = async () => {
		listLoading.value = true;
		try {
			const data = await alarmContentPage(queryForm);
			console.log("data:", data);
			list.value = data.records;
			total.value = data.total;
		} finally {
			setTimeout(() => {
				listLoading.value = false;
			}, 300);
		}
	};

	const clearData = () => {
		queryFormRef.value?.resetFields();
		Object.assign(queryForm, {
			pageNo: 1,
			pageSize: 20,
			keyword: "",
			alarmCategory: "",
			alarmLevel: "",
			alarmGroup: "",
			deviceType: "",
			enable: ""
		});
		queryData();
	};

	const handleDownload = () => {
		createModelAsync(
			{
				title: "模板导入",
				width: "400px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<Model />
		).then(() => {
			fetchData();
		});
	};

	const exportAlarm = () => {
		ElMessageBox.confirm("确认要导出吗？", null, {
			type: "warning"
		}).then(async () => {
			listLoading.value = true;
			try {
				const res = await exportAlarmContent(queryForm);
				let fileName = "告警内容.xls";
				let objectUrl = URL.createObjectURL(new Blob([res.data], { type: "application/vnd.ms-excel" }));
				const link = document.createElement("a");
				link.download = decodeURI(fileName);
				link.href = objectUrl;
				link.click();
				ElMessage.success("导出成功！");
			} finally {
				listLoading.value = false;
			}
		});
	};

	// 生命周期
	onMounted(async () => {
		const { data } = await getRedisDictList({ dictType: "alarmLevel" });
		warnLevelOptions.value = data;

		const { data: res } = await getRedisDictList({ dictType: "alarmType" });
		alarmTypeList.value = res;

		fetchData();
		getWarnLevels();
	});
</script>

<style scoped lang="scss">
	.search-info {
		padding: 12px;
		background: var(--el-page-color);
		border-radius: 8px;
		background: #f8f8f8;
		margin-top: 12px;
	}

	.pagination-box {
		display: flex;
		justify-content: end;
		margin-top: 12px;
	}

	.warning-wrap {
		display: flex;
		justify-content: space-between;
	}

	:deep(.el-form-item) {
		margin-bottom: 16px;
	}

	:deep(.el-form-item) {
		margin-right: 12px;
		margin-bottom: 0px;
	}
</style>
