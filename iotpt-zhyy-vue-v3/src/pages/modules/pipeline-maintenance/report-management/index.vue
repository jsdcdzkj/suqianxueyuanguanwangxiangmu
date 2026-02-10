<template>
	<BasePage v-bind="page">
		<template #tableActions="scope">
			<ElButton type="primary" link @click="handleView(scope.row)">查看</ElButton>
			<!-- <ElButton type="primary" link size="small" @click="handleView(scope.row)">分享</ElButton> //产品说去掉这个按钮-->
			<ElButton type="primary" link @click="handleDownload(scope.row)">下载</ElButton>
			<ElButton type="danger" link @click="handleDelete(scope.row)">删除</ElButton>
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
	import { ElMessageBox, type Action } from "element-plus";
	import { downFile } from "@/core/utils";
	import {reportManageGetPage, deleteReport, areaTreeList} from "@/api/pipeline-maintenance/report-management";
	import AddDialog from "@/pages/modules/pipeline-maintenance/report-management/dialog/addDialog.vue";

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
				prop: "reportName",
				attrs: {
					placeholder: "报告名称",
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
				prop: "createName",
				attrs: {
					placeholder: "创建人",
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
		title: "报告管理",
		listApi: async (data) => {
			return await reportManageGetPage({ ...data });
		},
		toolbar: [
			{
				label: "创建报告",
				size: "default",
				type:'success',
				icon: markRaw(Plus),
				onClick: async () => {
					createDrawerAsync(
						{
							title: "创建报告",
							showNext: false,
							width: "1200px",
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
					label: "报表名称",
					prop: "reportName",
					columnKey: "reportName",
					minWidth: 200,
					showOverflowTooltip: true
				},
				{
					label: "报表类型",
					prop: "chooseTempleName",
					columnKey: "chooseTempleName",
					width: 200
				},
				{
					label: "日期范围",
					prop: "reportStarTime",
					columnKey: "reportStarTime",
					width: 180
				},
				{
					label: "制作人",
					prop: "createName",
					columnKey: "createName",
					width: 120,
				},
				{
					label: "制作时间",
					prop: "createTime",
					columnKey: "createTime",
					width: 180
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

	// 预览
	const handleView = ({ minioFile, reportName }) => {
		window.open("/minio/previewCheck?fileName=" + minioFile,reportName + ".pdf");
	}
	//下载
	const handleDownload = ({ minioFile, reportName }) => {
		downFile("/minio/downloadCheck?fileName=" + minioFile,reportName + ".pdf");
	}
	// 删除
	const handleDelete = (row) => {
		ElMessageBox.alert("此操作将删除该信息, 是否继续?", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					deleteReport({ id: row.id }).then(() => {
						pageApi.pageList();
					});
				}
			}
		});
	}
</script>
<style>
</style>
