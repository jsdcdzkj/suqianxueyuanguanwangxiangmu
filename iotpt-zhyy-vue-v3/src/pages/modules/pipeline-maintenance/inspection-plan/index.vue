<template>
	<BasePage v-bind="page">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'executeType'">
				<span class="degree" v-if="row.executeType == 1"
					><i class="degree-1"></i>重复执行</span
				>
				<span class="degree" v-if="row.executeType== 2"
					><i class="degree-2"></i>单次执行</span
				>
			</template>
			<template v-if="column.columnKey === 'executeCycle'">
				<span v-if="row.executeCycle == 1">日</span>
				<span v-if="row.executeCycle == 2">周</span>
				<span v-if="row.executeCycle == 3">月</span>
			</template>
			<template v-if="column.columnKey === 'planStatus'">
				<ElTag type="success" v-if="row.planStatus == 0">启用</ElTag>
                <ElTag type="danger" v-if="row.planStatus == 1">停用</ElTag>
			</template>
			<template v-if="column.columnKey === 'urgency_name'">
				<span class="degree" v-if="row.urgency == 1"
					><i class="degree-3"></i>普通</span
				>
				<span class="degree" v-if="row.urgency== 2"
					><i class="degree-4"></i>紧急</span
				>
				<span class="degree" v-if="row.urgency == 3"
					><i class="degree-5"></i>非常紧急</span
				>
			</template>
		</template>
		<template #tableActions="scope">
			<ElButton type="danger" link @click="handleStop(scope.row)">停用</ElButton>
			<ElButton type="primary" link @click="handleView(scope.row)">详情</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import { usePage } from "@/core/struct/page";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import { markRaw } from "vue";
	import { Plus } from "@element-plus/icons-vue";
	import { ElMessageBox, type Action, ElTag } from "element-plus";
	import {getPageList, getGroupList, isEnablePlan, toDel} from "@/api/pipeline-maintenance/inspection-plan";
	import { getRedisDictList } from "@/api/common/common"
	import AddDialog from "@/pages/modules/pipeline-maintenance/inspection-plan/dialog/addDialog.vue";
	import InspectionItemDialog from "@/pages/modules/pipeline-maintenance/inspection-plan/dialog/inspectionItemDialog.vue";

	const formConfig: FormConfig = {
		inline: false,
		span: 4,
		expandSpan: 6,
		labelWidth:0,
		formItems: [
			{
				type: "ElInput",
				value: "",
				prop: "planName",
				attrs: {
					placeholder: "计划名称",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							pageApi.pageList();
						}
					}
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "groupId",
				attrs: {
					placeholder: "执行班组",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "name",
					value: "id",
					listApi: async () => {
						const data = await getGroupList({});
						return data;
					},
					list: []
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "planType",
				attrs: {
					placeholder: "计划类型",
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
				prop: "executeCycle",
				attrs: {
					placeholder: "周期类型",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "日",
							id: "1"
						},
						{
							label: "周",
							id: "2"
						},
						{
							label: "月",
							id: "3"
						}
					]
				}
			},
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		title: "巡检计划",
		listApi: async (data) => {
			const [startTime, endTime] = data.time || [];
			const params = {
				...data,
				startTime,
				endTime,
			};
			delete params.time;
			return await getPageList({ ...params });
		},
		toolbar: [
			{
				label: "添加",
				size: "default",
				type:'success',
				icon: markRaw(Plus),
				onClick: async () => {
					createDrawerAsync(
						{
							title: "添加",
							showNext: false,
							width: "960px",
							closeOnClickModal: false,
							closeOnPressEscape: false
						},
						{},
						<AddDialog />
					).then(() => {
						pageApi.pageList();
					});
				}
			},
			{
				label: "检查项模板管理",
				type: "primary",
				size: "default",
				onClick: async () => {
					createDrawerAsync(
						{
							title: "检查项模板管理",
							showNext: false,
							width: "1200px",
							closeOnClickModal: false,
							closeOnPressEscape: false
						},
						{},
						<InspectionItemDialog />
					).then(() => {
						pageApi.pageList();
					});
				}
			}
		],
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
					label: "计划名称",
					prop: "planName",
					columnKey: "planName",
					minWidth: 200,
					showOverflowTooltip: true
				},
				{
					label: "执行班组",
					prop: "groupName",
					columnKey: "groupName",
					width: 200,
				},
				{
					label: "计划开始时间",
					prop: "planStartTime",
					columnKey: "planStartTime",
					width: 180
				},
				{
					label: "计划结束时间",
					prop: "planEndTime",
					columnKey: "planEndTime",
					width: 180
				},
				{
					label: "计划类型",
					prop: "typeName",
					columnKey: "typeName",
				},
				{
					label: "执行类型",
					prop: "executeType",
					columnKey: "executeType",
					width: 120,
				},
				{
					label: "周期类型",
					prop: "executeCycle",
					columnKey: "executeCycle",
				},
				{
					label: "计划状态",
					prop: "planStatus",
					columnKey: "planStatus",
					width: 100,
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

	// 查看
	const handleView = (row) => {
		createDrawerAsync(
			{ title: "详情", width: '960px', showNext: false },
			{},
			<AddDialog isDetail={true} id={row.id} />
		).then(() => {
			pageApi.pageList();
		});
	}
	const handleStop = (row) => {
		ElMessageBox.alert("此操作将停用该计划, 是否继续?", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					isEnablePlan({ id: row.id, planStatus:1 }).then(() => {
						pageApi.pageList();
					});
				}
			}
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
		&.degree-1 {
			background: #ED6A5E;
		}
		&.degree-2 {
			background: #57BD94;
		}
	}
}
</style>
