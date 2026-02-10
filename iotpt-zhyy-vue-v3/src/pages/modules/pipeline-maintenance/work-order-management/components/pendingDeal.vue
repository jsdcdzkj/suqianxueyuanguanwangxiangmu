<template>
	<BasePage v-bind="page">
		<!-- 来源 -->
		<template #pageTableHeader="{ column }">
			<template v-if="column.columnKey == 'sourceName'">
				<ElTooltip effect="dark" placement="top-start">
					<template #content>
						<div>
							<span style="line-height: 20px; display: inline-block; margin-bottom: 4px">来源说明：</span>
						</div>
						<div>
							<span style="line-height: 20px; display: inline-block">1、系统工单：告警上报</span>
						</div>
						<div>
							<span style="line-height: 20px; display: inline-block">2、人员上报：PC端上报</span>
						</div>
						<div>
							<span style="line-height: 20px; display: inline-block">3、巡检工单：巡检异常上报</span>
						</div>
						<div>
							<span style="line-height: 20px; display: inline-block">4、巡检计划生成</span>
						</div>
						<div>
							<span style="line-height: 20px; display: inline-block">5、服务工单：报事报修</span>
						</div>
						<div>
							<span style="line-height: 20px; display: inline-block">6、投诉工单：投诉申请</span>
						</div>
					</template>
					<div>
						<span>来源</span>
						<i class="ri-question-line" style="cursor: pointer; margin-left: 2px;color:rgba(0,0,0,0.45)"></i>
					</div>
				</ElTooltip>
			</template>
		</template>
		<template #tableActions="scope">
			<ElButton type="primary" link size="small" @click="handleDeal(scope.row)">处理</ElButton>
			<ElButton type="primary" link size="small" @click="handleView(scope.row)">查看</ElButton>
			<ElButton type="danger" link size="small" @click="handleDelete(scope.row)">删除</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import { usePage } from "@/core/struct/page";
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
	import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import { ElMessageBox, type Action } from "element-plus";
	import DetailDialog from "@/pages/modules/pipeline-maintenance/work-order-management/dialog/detailDialog.vue";
	import DealDialog from "@/pages/modules/pipeline-maintenance/work-order-management/dialog/dealDialog.vue";

	const formConfig: FormConfig = {
		inline: false,
		span: 4,
		expandSpan: 4,
		labelWidth:0,
		notMargn: true,
		showExpand: true,
		showMessage: false,
		onSubmit: (e) => {
			e.preventDefault();
		},
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
						const data = await selectMissionDictAll({ dictType: "warnLevel" });
						return data;
					},
					list: []
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "urgency",
				attrs: {
					placeholder: "紧急程度",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "dictLabel",
					value: "dictValue",
					listApi: async () => {
						const data = await getRedisDictList({ dictType: "missionUrgency" });
						return data;
					},
					list: []
				}
			},
			{
				type: "ElInput",
				value: "",
				prop: "title",
				attrs: {
					placeholder: "标题关键字",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							page.form.onSearch(page.form.value);
						}
					}
				}
			},
			{
				type: "ElInput",
				value: "",
				prop: "notes",
				attrs: {
					placeholder: "内容关键字",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							page.form.onSearch(page.form.value);
						}
					}
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "teamGroupsId",
				attrs: {
					placeholder: "处理班组",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "name",
					value: "id",
					listApi: async () => {
						const data = await teamGroupsAllList();
						return data;
					},
					list: []
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "isPending",
				attrs: {
					placeholder: "是否挂起",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "是",
							id: "1"
						},
						{
							label: "否",
							id: "0"
						}
					]
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "reporTime",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "上报时间起",
					endPlaceholder: "上报时间止",
					valueFormat: "YYYY-MM-DD"
				}
			},
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		listApi: async (data) => {
			const [startTimeStatistics1, endTimeStatistics1] = data.reporTime || [];
			const params = {
				...data,
				states: 2,
				isHandle: 2,
				startTimeStatistics1,
				endTimeStatistics1,
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
					label: "紧急程度",
					prop: "urgencyName",
					columnKey: "urgencyName",
					width: 120
				},
				{
					label: "来源",
					prop: "sourceName",
					columnKey: "sourceName",
					width: 200
				},
				{
					label: "任务类型",
					prop: "taskTypeName",
					columnKey: "taskTypeName",
					width: 150
				},
				{
					label: "标题",
					prop: "title",
					columnKey: "title",
					minWidth: 200,
					showOverflowTooltip: true
				},
				{
					label: "内容",
					prop: "notes",
					columnKey: "notes",
					minWidth: 200,
					showOverflowTooltip: true
				},
				{
					label: "上报人",
					prop: "userName",
					columnKey: "userName",
					width: 120
				},
				{
					label: "上报时间",
					prop: "reportingTime",
					columnKey: "reportingTime",
					width: 180
				},
				{
					label: "处理期限",
					prop: "deadline",
					columnKey: "deadline",
					width: 180
				},
				{
					label: "处理班组",
					prop: "teamGroupsName",
					columnKey: "teamGroupsName",
					width: 180
				},
				{
					label: "状态",
					prop: "statesName",
					columnKey: "statesName",
					width: 100,
				},
				{
					label: "是否挂起",
					prop: "isPending",
					columnKey: "isPending",
					width: 100,
				},
				{
					label: "操作",
					columnKey: "actions",
					prop: "actions",
					width: 150
				}
			]
		}
	});

	// 处理
	const handleDeal = (row) => {
		createDrawerAsync(
			{ title: "处理", width: '960px', showNext: false },
			{},
			<DealDialog id={row.id} />
		).then(() => {
			pageApi.pageList();
		});
	}
	const handleView = (row) => {
		createDrawerAsync(
			{ title: "详情", width: '960px', showNext: false },
			{},
			<DetailDialog id={row.id} />
		).then(() => {
			pageApi.pageList();
		});
	}
	// 删除
	const handleDelete = (row) => {
		ElMessageBox.alert("此操作将删除该信息, 是否继续?", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					deleteMission({ id: row.id }).then(() => {
						pageApi.pageList();
					});
				}
			}
		});
	}
</script>
<style>
</style>
