<template>
	<BasePage v-bind="page">
		<template #pageTableCell="{ column, row }">
			<!-- 严重程度 -->
			<div v-if="column.columnKey === 'levelsName'" class="cell-content">
				<span>{{ row.levelsName }}</span>
			</div>
			<!-- 状态 -->
			<div v-if="column.columnKey === 'statesName'" class="cell-content">
				<ElTag size="mini" type="danger" v-if="row.states == 1">待指派</ElTag>
				<ElTag size="mini" type="warning" v-if="row.states == 2 || row.states == 4" > 待处理 </ElTag>
				<ElTag size="mini" type="success" v-if="row.states == 3">已处理</ElTag>
				<ElTag size="mini" type="primary" v-if="row.states == 0">暂存</ElTag>
			</div>
		</template>
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
			<ElButton type="primary" link size="small" @click="handleView(scope.row)">查看</ElButton>
			<ElButton type="primary" link size="small" @click="handleEdit(scope.row)">编辑</ElButton>
			<ElButton type="primary" link size="small" @click="handleBack(scope.row)">撤销</ElButton>
			<ElButton type="danger" link size="small" @click="handleDelete(scope.row)">删除</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import { usePage } from "@/core/struct/page";
	import { Plus } from "@element-plus/icons-vue";
	import { markRaw } from "vue";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import { ElTag } from "element-plus";
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
	import { ElMessageBox, type Action } from "element-plus";
	import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
	import DetailDialog from "@/pages/modules/pipeline-maintenance/work-order-management/dialog/detailDialog.vue";
	import BackDialog from "@/pages/modules/pipeline-maintenance/work-order-management/dialog/backDialog.vue";
	import TaskEditDialog from "@/pages/modules/pipeline-maintenance/work-order-management/dialog/taskEditDialog.vue";

	const formConfig: FormConfig = {
		inline: false,
		span: 4,
		expandSpan: 4,
		labelWidth:0,
		notMargn: true,
		isExpand: false,
		showExpand: true,
		showMessage: false,
		onSubmit: (e) => {
			e.preventDefault();
		},
		formItems: [
			{
				type: "ElSelect",
				value: "",
				prop: "levels",
				attrs: {
					placeholder: "严重程度",
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
			{
				type: "ElSelect",
				value: "",
				prop: "isPending",
				attrs: {
					placeholder: "状态",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "name",
					value: "id",
					list: [
						{
							name: "暂存",
							id: "0"
						},
						{
							name: "待指派",
							id: "1"
						},
						{
							name: "待处理",
							id: "2"
						},
						{
							name: "已处理",
							id: "3"
						},
						{
							name: "开启",
							id: "4"
						},
						{
							name: "撤销",
							id: "5"
						},
						{
							name: "忽略",
							id: "6"
						},
						{
							name: "已关闭",
							id: "7"
						},
						{
							name: "已驳回",
							id: "8"
						}
					]
				},
			},
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		listApi: async (data) => {
			const [startTimeStatistics1, endTimeStatistics1] = data.reporTime || [];
			const params = {
				...data,
				isHandle: 1,
				startTimeStatistics1,
				endTimeStatistics1,
			};
			delete params.time;
			return await pageListMission1({ ...params });
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
					label: "严重程度",
					prop: "levelsName",
					columnKey: "levelsName",
					width: 120
				},
				{
					label: "标题",
					prop: "title",
					columnKey: "title",
					minWidth: 200
				},
				{
					label: "内容",
					prop: "notes",
					columnKey: "notes",
					minWidth: 200
				},
				{
					label: "来源",
					prop: "sourceName",
					columnKey: "sourceName",
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
					width: 180
				},
				{
					label: "状态",
					prop: "statesName",
					columnKey: "statesName",
					width: 120
				},
				{
					label: "操作",
					columnKey: "actions",
					prop: "actions",
					width: 260
				}
			]
		}
	});

	// 编辑
	const handleEdit = (row) => {
		createModelAsync(
			{ title: "编辑", width: '600px', showNext: false },
			{},
			<TaskEditDialog id={row.id} />
		).then(() => {
			pageApi.pageList();
		});
	}
	// 撤销
	const handleBack = (row) => {
		createModelAsync({ title: "撤销", width: "600px", showNext: false }, {}, <BackDialog id={row.id} />).then(
			() => {
				pageApi.pageList();
			}
		);
	}
	// 查看
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
