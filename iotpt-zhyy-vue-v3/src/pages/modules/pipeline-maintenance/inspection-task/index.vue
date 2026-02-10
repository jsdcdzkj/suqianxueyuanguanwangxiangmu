<template>
	<BasePage v-bind="page">statesName
		<template #pageTableCell="{ column, row }">
			<!-- 严重程度 -->
			<div v-if="column.columnKey === 'levelsName'" class="cell-content">
				<span class="degree">
					<i :class="row.levelsColour"></i>
					{{ row.levelsName }}
				</span>
			</div>
			<!-- 状态 -->
			<div v-if="column.columnKey === 'statesName'" class="cell-content">
				<ElTag size="mini" type="danger" v-if="row.states == 1">待指派</ElTag>
				<ElTag size="mini" type="warning" v-if="row.states == 2 || row.states == 4"> 待处理 </ElTag>
				<ElTag size="mini" type="success" v-if="row.states == 3">已处理</ElTag>
				<ElTag size="mini" type="primary" v-if="row.states == 0">暂存</ElTag>
			</div>
		</template>
		<template #tableActions="scope">
			<ElButton type="primary" link @click="handleView(scope.row)">查看</ElButton>
			<!-- <ElButton v-if="scope.row.states == 3" type="primary" link @click="handleEdit(scope.row)">查看</ElButton> -->
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import { usePage } from "@/core/struct/page";
	import { getRedisDictList } from "@/api/common/common";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import DealListDialog from "@/pages/modules/alarm-center/history-alarm/dialog/dealListDialog.vue";
	import DetailDialog from "@/pages/modules/pipeline-maintenance/inspection-task/components/detailDialog.vue";
	import { Plus, Upload } from "@element-plus/icons-vue";
	import { markRaw } from "vue";
	import {
		backOut,
		getTaskAuthority,
		ignoreMission,
		openStatus,
		pageListMission,
		pageListMission1,
		statusCount,
		updateIsReads,
		teamGroupsAllList,
		exportAssignExcel,
		missionExcel,
		deleteMission,
		missionRejection,
		exportExcel
	} from "@/api/pipeline-maintenance/work-order";

	const formConfig: FormConfig = {
		inline: false,
		span: 4,
		expandSpan: 6,
		labelWidth:0,
		formItems: [
			{
				type: "ElSelect",
				value: "",
				prop: "assignTaskType",
				attrs: {
					placeholder: "任务类型",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "dictLabel",
					value: "dictValue",
					listApi: async () => {
						const data = await getRedisDictList({ dictType: "planType" });
						return data;
					},
					list: []
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "levels",
				attrs: {
					placeholder: "任务级别",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "dictLabel",
					value: "dictValue",
					listApi: async () => {
						const data = await getRedisDictList({ dictType: "warnLevel" });
						return data;
					},
					list: []
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "处理期限起",
					endPlaceholder: "处理期限止",
					valueFormat: "YYYY-MM-DD"
				}
			},
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		title: "巡检任务",
		listApi: async (data) => {
			const [startTime, endTime] = data.time || [];
			const params = {
				...data,
				startTime,
				endTime,
				source: 4,
				selectSource: 1,
			};
			delete params.time;
			return await pageListMission({ ...params });
		},	
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
			dataSource: [{}],
			columns: [
				{
					columnKey: "index",
					label: "序号",
					prop: "label",
					width: "80px",
					align: "center"
				},
				{
					label: "来源",
					prop: "sourceName",
					columnKey: "sourceName",
				},
				{
					label: "级别",
					prop: "levelsName",
					columnKey: "levelsName",
					width: 120,
				},
				{
					label: "内容",
					prop: "substance",
					columnKey: "substance",
					minWidth: 200,
					showOverflowTooltip: true
				},
				{
					label: "备注",
					prop: "notes",
					columnKey: "notes",
					minWidthidth: 200,
					showOverflowTooltip: true
				},
				{
					label: "上报人",
					prop: "userName",
					columnKey: "userName",
					width: 100,
				},
				{
					label: "上报时间",
					prop: "reportingTime",
					columnKey: "reportingTime",
					width: 180,
				},
				{
					label: "状态",
					prop: "statesName",
					columnKey: "statesName",
				},
				{
					label: "操作",
					columnKey: "actions",
					prop: "actions",
					width: 200
				}
			]
		}
	});

	const handleView = (row) => {
		createDrawerAsync(
			{ title: "查看", width: '1200px', showNext: false, showConfirm: false, showCancel: false },
			{},
			<DetailDialog id={row.id} />
		).then(() => {
			pageApi.pageList();
		});
	}
</script>
<style scoped lang="scss">
.degree {
	display: inline-block;
	width: 88px;
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
		&.warning {
			background: #e6a23c;
		}
		&.success {
			background: #57BD94;
		}

		&.danger {
			background: #ED6A5E;
		}
	}
}
</style>
