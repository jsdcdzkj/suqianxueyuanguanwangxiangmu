<template>
	<BasePage v-bind="page">
		<template #pageTop>
			<div class="alarm-center-box">
				<div class="header-wrap">
					<div class="tabs_box">
						<div class="tabs">
							<div
								:class="{ active: index == activeIndex }"
								v-for="(el, index) in warnTypeList"
								class="tab-item"
								:key="index"
								@click="changeWarnType(index, el.id)"
							>
								<span v-if="el.num" class="tag">{{ el.num }}</span>
								{{ el.name }}
							</div>
						</div>
						<!-- <div class="ai-wrap" v-if="aiWord">
							<div class="tip">
								<notice title="AI分析" :notice="markedParse(aiWord)"></notice>
							</div>
							<img
								@click="handleAiAnalysis()"
								src="@/assets/img/alarmEvent/ai-man.png"
								class="ai-man"
								alt=""
							/>
						</div> -->
					</div>
					<div class="search">
						<el-row :gutter="24">
							<el-col :span="14">
								<div class="serach-num">
									<div
										class="warn-num"
										@click="handleChangeType(index, item.val)"
										:class="{ active: typeIndex == index }"
										v-for="(item, index) in warnNum"
										:key="index"
									>
										<div class="num" :class="[item.color]">{{ item.num }}</div>
										<span class="name">{{ item.name }}</span>
									</div>
								</div>
							</el-col>
							<el-col :span="5">
								<taskLine height="77px" :valdata="lineData"></taskLine>
							</el-col>
							<el-col :span="5">
								<div class="export-wrap">
									<ElButton type="success" @click="handleBatchDeal('', '-1')">批量处理</ElButton>
									<ElButton type="primary" :disabled="ids.length == 0" @click="handleExport"
										>导出</ElButton
									>
								</div>
								<div class="filter-wrap">
									<ElInput v-model="deviceName" placeholder="告警对象搜索"></ElInput>
									<ElButton type="default" class="filter-btn" @click="handleFilter">
										<span>{{ show ? "收起过滤" : "展开过滤" }}</span>
										<ElIcon><CaretBottom /></ElIcon>
									</ElButton>
								</div>
							</el-col>
						</el-row>
						<div class="search-info" v-show="show">
							<ElForm
								:inline="true"
								class="demo-form-inline"
								:model="queryForm"
								@submit.native.prevent
								ref="queryForm"
								label-width="0"
							>
								<ElFormItem>
									<ElCascader
										:options="logicalAreaList"
										placeholder="请选择区域"
										v-model="logicalAreaIds"
										:show-all-levels="false"
										clearable
									></ElCascader>
								</ElFormItem>
								<ElFormItem>
									<ElSelect
										style="width: 200px"
										v-model="queryForm.alarmContentId"
										placeholder="事件内容"
										clearable
									>
										<ElOption
											v-for="item in warnContentList"
											:key="item.id"
											:label="item.alarmContent"
											:value="item.id"
										></ElOption>
									</ElSelect>
								</ElFormItem>
								<ElFormItem>
									<ElSelect
										style="width: 200px"
										v-model="queryForm.deviceSource"
										placeholder="告警来源"
										clearable
									>
										<ElOption
											v-for="item in warnSourceList"
											:key="item.dictValue"
											:label="item.dictLabel"
											:value="item.dictValue"
										></ElOption>
									</ElSelect>
								</ElFormItem>
								<ElFormItem>
									<ElDatePicker
										v-model="value1"
										type="daterange"
										range-separator="至"
										start-placeholder="告警开始日期"
										end-placeholder="告警结束日期"
										value-format="yyyy-MM-dd"
									></ElDatePicker>
								</ElFormItem>
								<ElFormItem style="margin-left: 12px">
									<ElButton :icon="Search" type="primary" @click="search">查询</ElButton>
									<ElButton :icon="Refresh" @click="clearData">重置</ElButton>
								</ElFormItem>
							</ElForm>
						</div>
					</div>
				</div>
			</div>
		</template>
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'alarmLevel'">
				<span class="degree" v-if="row.alarmLevel == 1">
					<i class="degree-1"></i>
					{{ row.alarmLevelName }}
				</span>
				<span class="degree" v-else-if="row.alarmLevel == 2">
					<i class="degree-2"></i>
					{{ row.alarmLevelName }}
				</span>
				<span class="degree" v-else-if="row.alarmLevel == 3">
					<i class="degree-3"></i>
					{{ row.alarmLevelName }}
				</span>
				<span class="degree" v-else-if="row.alarmLevel == 4">
					<i class="degree-4"></i>
					{{ row.alarmLevelName }}
				</span>
				<span class="degree" v-else>
					<i class="degree-5"></i>
					{{ row.alarmLevelName }}
				</span>
			</template>
		</template>
		<template #tableActions="{ row }">
			<ElButton link plain type="primary" @click="handleView(row)">查看</ElButton>
			<ElButton link plain type="primary" v-if="row.handleStatus == 0" @click="handleBatchDeal(row, '2')">
				上报
			</ElButton>
			<ElButton link plain type="primary" v-if="row.handleStatus == 0" @click="handleBatchDeal(row, '3')">
				忽略
			</ElButton>
			<ElButton link plain type="primary" v-if="row.handleStatus == 0" @click="handleManualJudgment(row)">
				误报
			</ElButton>
			<ElButton link plain type="danger" v-if="row.handleStatus == 0" @click="handleDelete(row)">
				删除
			</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
import BasePage from "@/core/struct/page/base-page";
import { usePage } from "@/core/struct/page";
import { Search, Refresh, CaretBottom } from "@element-plus/icons-vue";
import {
	ElButton,
	ElCascader,
	ElDatePicker,
	ElForm,
	ElFormItem,
	ElIcon,
	ElOption,
	ElSelect,
	ElTag,
	ElMessageBox,
	ElMessage,
	ElInput,
	type Action
} from "element-plus";
import taskLine from "@/pages/modules/alarm-center/alarm-center-page/echarts/taskLine.vue";
// import notice from "./components/notice";
import { groupAlarmLevel, batchProcessing, toExportTemplate,getPageList, getAlarmCategory, contentList } from "@/api/alarm-center/alarmCenter";
import { areaTreeList2, getRedisDictList } from "@/api/common/common";
import { createDrawerAsync, createModelAsync } from "@/core/dialog";
import { ref } from "vue";
import BatchDeal from "@/pages/modules/alarm-center/alarm-center-page/components/batchDeal.vue";
import ViewDialog from "@/pages/modules/alarm-center/alarm-center-page/components/view.vue";
import ManualJudgment from "@/pages/modules/alarm-center/alarm-center-page/components/manualJudgment.vue";

const queryForm = ref({
	alarmContentId: "",
	deviceSource: "",
	startTime: "",
	endTime: ""
});
const deviceName = ref("");
const lineData = ref({
	label: [],
	value: []
});
const colorList = ["blue", "green", "organe", "red"]
// 顶部告警等级统计数据
const warnNum = ref([
	{ name: "告警总数", num: 0 },
	{ name: "警告", num: 0, color: "blue" },
	{ name: "次要", num: 0, color: "green" },
	{ name: "重要", num: 0, color: "organe" },
	{ name: "紧急", num: 0, color: "red" }
]);
const getLevelsNum = async () => {
	const data = await groupAlarmLevel(queryForm.value);
	console.log("2222222222222222levelnum", data);
	let total = 0;

	warnNum.value = warnNum.value?.map((res, index) => {
		res.num = 0;
		data.map((val) => {
			if (res.val == val.alarmLevel) {
				total += Number(val.num);
				res.num = val.num;
			}
		});
		return res;
	});
	warnNum.value[0].num = total;
};
getLevelsNum()
const typeIndex = ref(0);

const handleChangeType = (index: number, val: string) => {
	typeIndex.value = index;
	queryForm.value.warnType = val;
	search();
};

const value1 = ref([]);
const clearData = () => {
	queryForm.value = {
		alarmContentId: "",
		deviceSource: "",
		startTime: "",
		endTime: ""
	};
	deviceName.value = "";
	value1.value = [];
	search();
};
const show = ref(false);

const warnTypeList = ref([
	{
		id: "1",
		name: "全部"
	},
	{
		id: "2",
		name: "未处理"
	},
	{
		id: "3",
		name: "已处理"
	}
]);
const warnSourceList = ref([
	{
		dictValue: "1",
		dictLabel: "设备"
	},
	{
		dictValue: "2",
		dictLabel: "系统"
	}
]);
const aiWord = ref(false);
const markedParse = (val: string) => {
	if (val == "1") {
		return "全部";
	} else if (val == "2") {
		return "已处理";
	} else if (val == "3") {
		return "未处理";
	}
};
const warnContentList = ref([]);
const logicalAreaIds = ref([]);
const logicalAreaList = ref([]);
const areaTreeList = async () => {
	const res = await areaTreeList2({});
	for (let index = 0; index < res[0].children.length; index++) {
		delete res[0].children[index].children;
	}
	logicalAreaList.value = res;
}
areaTreeList()
const init = async() => {
	// 告警等级
	const warnLevelOptions = ref([]);
	const data = await getRedisDictList({ dictType: "alarmLevel" });
	warnLevelOptions.value = data;
	warnNum.value = [{ name: "告警总数", num: 0 }];
	data.map((res, index) => {
		warnNum.value.push({ name: res.dictLabel, val: res.dictValue, num: 0, color: colorList[index] });
	});
	// 告警类型
	const warnTypeArr = ref([]);
	const type = await getAlarmCategory({ alarmType: 1 });  // 接口报错
	warnTypeList.value = [{ name: "全部", id: "-1", num: 0 }].concat(type);
	warnTypeList.value.map((val) => {
		warnTypeArr.value[val.id] = val.name;
	});

	// 告警来源
	const source = await getRedisDictList({ dictType: "deviceSource" });
	warnSourceList.value = source;

	// 事件内容
	const content = await contentList();
	warnContentList.value = content;
}
init()

const { page, pageApi } = usePage({
	title: "告警信息列表",
	pagination: {
		currentPage: 1,
		pageSize: 20,
		total: 15,
		background: true,
		layout: "prev, pager, next, jumper, ->, total",
		onChange(pageNo: number, pageSize: number) {
			if (page.pagination) {
				page.pagination.currentPage = pageNo;
				page.pagination.pageSize = pageSize;
				pageApi.pageList();
			}
		}
	},
	tableConfig: {
		// 生成20条数据
		dataSource: [],
		columns: [
			{
				columnKey: "check",
				label: "",
				type: "selection",
				prop: "check",
				width: "55px",
				align: "center"
			},
			{
				columnKey: "index",
				label: "序号",
				prop: "label",
				width: "80px",
				align: "center"
			},
			{
				label: "告警来源",
				prop: "deviceName",
				columnKey: "deviceName",
				width: 200,
				showOverflowTooltip: true
			},
			{
				label: "告警类型",
				prop: "alarmCategoryName",
				columnKey: "alarmCategoryName",
				width: 140
			},
			{
				label: "告警等级",
				prop: "alarmLevel",
				columnKey: "alarmLevel",
				width: 140
			},
			{
				label: "时间",
				prop: "alarmTime",
				columnKey: "alarmTime",
				width: 180
			},
			{
				label: "区域",
				prop: "areaName",
				minWidth: 160,
				showOverflowTooltip: true
			},
			{
				label: "事件内容",
				prop: "alarmContentStr",
				columnKey: "alarmContentStr",
				minWidth: 200,
				showOverflowTooltip: true
			},
			{
				label: "告警状态",
				prop: "alarmStatusName",
				columnKey: "alarmStatusName"
			},
			{
				label: "操作",
				prop: "actions",
				columnKey: "actions",
				width: 260
			}
		]
	}
});
const handleFilter = () => {
	show.value = !show.value;
};
const activeIndex = ref();
const changeWarnType = (index: number) => {
	typeIndex.value = index;
	queryForm.value.warnType = warnTypeList.value[index].id;
	search();
};
const search = async () => {
	if (value1.value?.length > 0) {
		queryForm.value.startTime = value1.value[0];
		queryForm.value.endTime = value1.value[1];
	} else {
		queryForm.value.startTime = null;
		queryForm.value.endTime = null;
	}
	// 选择区域
	console.log("9999999999999logicalAreaIds", logicalAreaIds.value);
	if (logicalAreaIds.value.length > 0) {
		queryForm.value.buildId = logicalAreaIds.value[0];
		queryForm.value.floorId = logicalAreaIds.value[1];
	} else {
		queryForm.value.buildId = "";
		queryForm.value.floorId = "";
	}
	const data = await getPageList({...queryForm.value, deviceName: deviceName.value});
	page.table.dataSource = data.records;
	page.pagination.total = data.total;
};
search()
// 查看
const handleView = (row) => {
	// createModelAsync(
	// 	{ title: "查看", width: '600px', showNext: false, showConfirm:false },
	// 	{},
	// 	<ViewDialog id={row.id} />
	// ).then(() => {
	// 	search()
	// });
};
const handleManualJudgment = (row) => {
	createModelAsync(
		{ title: "人工判定误报", width: '600px', showNext: false },
		{},
		<ManualJudgment row={row} />
	).then(() => {
		search()
	});
};
// 批量处理
const ids = ref([]);
const handleBatchDeal = (row, type) => {
	if (type == -1) {
		if (ids.value.length == 0) {
			return ElMessage.error("请选择一个告警");
		} else {
			createModelAsync(
				{ title: "批量处理", width: '600px', showNext: false },
				{},
				<BatchDeal row={row} ids={ids.value.join(",")} type={type} />
			).then(() => {
				search()
			});
		}
	} else {
		<BatchDeal row={row} ids={row.id} type={type} />
	}
};
// 导出
const handleExport = () => {
	// 导出
	ElMessageBox.alert("确认要导出吗？", '提示',{
		confirmButtonText: "确认",
		callback: (action: Action) => {
			if (action == "confirm") {
				toExportTemplate({ ...queryForm.value, ids: ids.value.join(",") })
					.then((res) => {
						let fileName = "告警事件列表.xls";
						let objectUrl = URL.createObjectURL(new Blob([res.data], { type: "application/vnd.ms-excel" }));
						const link = document.createElement("a");
						link.download = decodeURI(fileName);
						link.href = objectUrl;
						link.click();
						ElMessage.success("导出成功！");
					})
				}
			}
	});
}
// 删除
const handleDelete = (row) => {
	ElMessageBox.alert("此操作将删除该信息, 是否继续?", "提示", {
		confirmButtonText: "确认",
		callback: (action: Action) => {
			if (action == "confirm") {
				batchProcessing({ ids: row.id, handleStatus: 4 }).then((res) => {
					ElMessage.success("删除成功");
					pageApi.pageList();
				});
			}
		}
	});
};

</script>
<style scoped lang="scss">
.header-wrap {
	.tabs_box {
		justify-content: space-between;
		align-items: center;
		display: flex;
		.tabs {
			overflow-x: auto;
			overflow-y: hidden;
			margin-right: 20px;
			display: flex;
			flex-wrap: nowrap;
			// flex: 1;
			padding-top: 8px;

			.tab-item {
				position: relative;
				.tag {
					position: absolute;
					top: 2px;
					z-index: 10;
					right: 20px;
					transform: translateY(-50%) translateX(100%);
					border-radius: 10px;
					color: #fff;
					display: inline-block;
					font-size: 12px;
					height: 20px;
					line-height: 20px;
					padding: 0 6px;
					text-align: center;
					white-space: nowrap;
					border: 1px solid #fff;
					background-color: #f56c6c;
				}
			}

			div {
				height: 48px;
				background: #ffffff;
				border-radius: 8px 8px 0px 0px;
				border: 1px solid rgba(0, 0, 0, 0.06);
				padding: 0 16px;
				margin-right: 12px;
				line-height: 48px;
				cursor: pointer;
				flex-shrink: 0;
				color: rgba(0, 0, 0, 0.65);
				font-size: 16px;
				&.active {
					background: linear-gradient(180deg, #ffffff 0%, #d0e6ff 100%);
					box-shadow: inset 0px 0px 4px 0px #1a81f4;
					border: none;
					border-bottom: 2px solid #1b86ff;
					color: #1b83f8;
				}
			}
		}
	}

	.ai-wrap {
		// position: fixed;
		// right: 0;
		// top: 76px;
		flex: 1;
		overflow: hidden;
		display: flex;
		align-items: center;

		.tip {
			width: calc(100% - 70px);
			height: 38px;
			line-height: 38px;
			background: linear-gradient(180deg, #4cabf6 0%, #1e95f4 100%);
			border-radius: 16px 0px 16px 16px;
			color: #fff;
			padding: 0 16px;
			font-size: 14px;
			overflow: hidden;
		}

		.ai-man {
			width: 76px;
			height: 115px;
			position: fixed;
			right: 0;
			top: 76px;
		}
	}
	.search {
		background: #fff;
		box-shadow: 0px -1px 3px 0px rgba(0, 0, 0, 0.12);
		padding: 24px;

		.serach-num {
			display: flex;
		}

		.warn-num {
			flex: 1;
			text-align: center;
			position: relative;
			height: 76px;
			margin: 0 12px;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;

			&::after {
				content: "";
				width: 1px;
				height: 56px;
				background: rgba(0, 0, 0, 0.15);
				position: absolute;
				right: -12px;
				top: 10px;
			}

			.num {
				font-weight: bold;
				font-size: 24px;
				color: rgba(0, 0, 0, 0.85);
				line-height: 20px;
			}

			.blue {
				color: #1e95f4;
			}

			.green {
				color: #67c23a;
			}

			.organe {
				color: #faab0c;
			}

			.red {
				color: #f56c6c;
			}

			.name {
				font-size: 14px;
				color: rgba(0, 0, 0, 0.65);
				margin-top: 8px;
			}

			&:last-child {
				&::after {
					display: none;
				}
			}
		}

		.active {
			background: #e8f4fe;
			border-radius: 8px 8px 8px 8px;
		}

		.export-wrap {
			text-align: right;
		}
		.filter-wrap {
			display: flex;
			margin-top: 12px;

			.filter-btn {
				margin-left: 12px;
			}
		}
	}

	.search-info {
		padding: 24px 24px 6px;
		background: linear-gradient(180deg, #ffffff 0%, #f3f3f3 100%);
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0, 0, 0, 0.15);
		margin-top: 24px;
	}
}
.degree {
	display: inline-block;
	width: 62px;
	height: 26px;
	font-size: 14px !important;
	color: rgba(0, 0, 0, 0.65);
	line-height: 26px;
	border-radius: 4px 4px 4px 4px;
	box-sizing: border-box;
	text-align: center;
	i {
		display: inline-block;
		margin-right: 6px;
		width: 10px;
		height: 10px;
		border-radius: 50%;
		&.degree-1 {
			background: #409eff;
		}
		&.degree-2 {
			background: #67c23a;
		}
		&.degree-3 {
			background: #faab0c;
		}
		&.degree-4 {
			background: #f56c6c;
		}
		&.degree-5 {
			background: rgba(0, 0, 0, 0.15);
		}
	}
}
::v-deep {
	.el-form--inline .el-form-item {
		margin-right: 12px;
	}
}
</style>
