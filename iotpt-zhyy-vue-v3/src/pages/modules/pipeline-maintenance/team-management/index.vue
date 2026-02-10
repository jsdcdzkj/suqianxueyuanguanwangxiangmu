<template>
	<BasePage v-bind="page">
		<template #tableActions="scope">
			<ElButton type="primary" link @click="handleAdd(scope.row)">编辑</ElButton>
			<ElButton type="primary" link @click="handleView(scope.row)">详情</ElButton>
			<ElButton type="danger" link @click="handleDelete(scope.row)">删除</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import { usePage } from "@/core/struct/page";
	import { ElMessageBox, type Action } from "element-plus";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import { Plus } from "@element-plus/icons-vue";
	import { markRaw } from "vue";
	import { addTeam,getList,delTeam } from "@/api/pipeline-maintenance/team-management";
	import DetailDialog from "@/pages/modules/pipeline-maintenance/team-management/dialog/detailDialog.vue";
	import AddDialog from "@/pages/modules/pipeline-maintenance/team-management/dialog/addDialog.vue";

	const formConfig: FormConfig = {
		inline: false,
		span: 4,
		expandSpan: 6,
		labelWidth:0,
		onSubmit: (e) => {
			e.preventDefault();
		},
		formItems: [
			{
				type: "ElInput",
				value: "",
				prop: "name",
				attrs: {
					placeholder: "班组名称",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							page.form.onSearch(page.form.value);
						}
					}
				}
			},
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		title: "班组管理",
		listApi: async (data) => {
			return await getList({ ...data });
		},
		toolbar: [
			{
				label: "新增",
				size: "default",
				type:'success',
				icon: markRaw(Plus),
				onClick: async () => {
					createDrawerAsync(
						{
							title: "新增",
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
					label: "班组名称",
					prop: "name",
					columnKey: "name",
					minWidth: 200,
					showOverflowTooltip: true
				},
				{
					label: "组长",
					prop: "groupLeader",
					columnKey: "groupLeader",
					width: 120
				},
				{
					label: "人员数量",
					prop: "peopleNum",
					columnKey: "peopleNum",
					width: 100
				},
				{
					label: "班组说明",
					prop: "groupDesc",
					columnKey: "groupDesc",
					showOverflowTooltip: true
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

	// 编辑
	const handleAdd = (row) => {
		createDrawerAsync(
			{ title: "新增", width: '960px', showNext: false },
			{},
			<AddDialog id={row.id} />
		).then(() => {
			pageApi.pageList();
		});
	}
	const handleView = (row) => {
		createDrawerAsync(
			{ title: "详情", width: '960px', showNext: false, showCancel: false, showConfirm: false },
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
					delTeam({ id: row.id }).then(() => {
						pageApi.pageList();
					});
				}
			}
		});
	}
</script>
<style>
</style>
