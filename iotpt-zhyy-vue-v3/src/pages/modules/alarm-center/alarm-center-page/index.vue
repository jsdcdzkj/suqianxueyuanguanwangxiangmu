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
									<el-input v-model="queryForm.deviceName" placeholder="告警对象搜索"></el-input>
									<ElButton type="default" class="filter-btn" @click="handleFilter">
										<span>{{ show ? "收起过滤" : "展开过滤" }}</span>
										<el-icon class="el-icon-caret-bottom"></el-icon>
									</ElButton>
								</div>
							</el-col>
						</el-row>
						<div class="search-info" v-if="show">
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
									<ElSelect style="width: 200px" v-model="queryForm.alarmContentId" placeholder="事件内容" clearable>
										<ElOption
											v-for="item in warnContentList"
											:key="item.id"
											:label="item.alarmContent"
											:value="item.id"
										></ElOption>
									</ElSelect>
								</ElFormItem>
								<ElFormItem>
									<ElSelect  style="width: 200px" v-model="queryForm.deviceSource" placeholder="告警来源" clearable>
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
	</BasePage>
</template>

<script setup lang="tsx">
import BasePage from "@/core/struct/page/base-page";
import { usePage } from "@/core/struct/page";
import { Search, Refresh } from "@element-plus/icons-vue";
import { ElButton, ElCascader, ElDatePicker, ElForm, ElFormItem, ElIcon, ElOption, ElSelect, ElTag } from "element-plus";
import taskLine from "@/pages/modules/alarm-center/alarm-center-page/echarts/taskLine.vue";
// import notice from "./components/notice";
import { ref } from "vue";

const queryForm = ref({
	alarmContentId: "",
	deviceSource: "",
	deviceName: "",
	startTime: "",
	endTime: ""
});
const lineData = ref({
	label: [],
	value: []
});
const warnNum = ref([
	{
		num: 0,
		name: "全部",
		color: "warn-num-all"
	},
	{
		num: 0,
		name: "未处理",
		color: "warn-num-unprocessed"
	},
	{
		num: 0,
		name: "已处理",
		color: "warn-num-processed"
	}
]);

const typeIndex = ref(0);

const handleChangeType = (index: number, val: string) => {
	typeIndex.value = index;
	queryForm.value.warnType = val;
	search();
};

const search = () => {
	pageApi.pageList();
};
const ids = ref([]);
const value1 = ref([]);
const clearData = () => {
	queryForm.value = {
		alarmContentId: "",
		deviceSource: "",
		deviceName: "",
		startTime: "",
		endTime: ""
	};
	search();
};
const show = ref(true);

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
const handleExport = () => {
	// exportApi.export(queryForm.value).then(({ data }) => {
	// 	downloadFile(data, "告警信息.xlsx");
	// });
};
const warnContentList = ref([]);
const logicalAreaIds = ref([]);
const logicalAreaList = ref([]);
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
				label: "序号",
				prop: "name"
			},
			{
				label: "告警来源",
				prop: "name"
			},
			{
				label: "告警类型",
				prop: "path",
				width: 200
			},
			{
				label: "告警等级",
				prop: "icon",
				width: 200
			},
			{
				label: "时间",
				prop: "desc",
				width: 180
			},
			{
				label: "区域",
				prop: "icon"
			},
			{
				label: "事件内容",
				prop: "desc"
			},
			{
				label: "告警状态",
				prop: "desc"
			},
			{
				label: "操作",
				prop: "action",
				width: 200
			}
		]
	}
});
const handleFilter = () => {
	if (value1.value.length > 0) {
		queryForm.value.startTime = value1.value[0];
		queryForm.value.endTime = value1.value[1];
	}
	search();
};
const handleBatchDeal = () => {
	show.value = true;
};
const handleAiAnalysis = () => {
	show.value = true;
};
const activeIndex = ref();
const changeWarnType=(index: number) => {
	typeIndex.value = index;
	queryForm.value.warnType = warnTypeList.value[index].id;
	search();
}
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
::v-deep {
	.el-form--inline .el-form-item {
		margin-right: 12px;
	}
}
</style>
