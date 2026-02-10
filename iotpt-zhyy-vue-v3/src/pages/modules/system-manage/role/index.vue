<template>
	<BasePage v-bind="page">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'status'">
				<ElTag :type="row.status == 0 ? 'success' : 'info'">
					{{ row.status == 0 ? "启用" : "禁用" }}
				</ElTag>
			</template>
		</template>
		<template #pageTableHeader="{ column }">
			<template v-if="column.columnKey === 'username5'">
				<div class="color-red">
					<el-tooltip class="box-item" effect="dark" content="111" placement="top">
						{{ column.label }}
					</el-tooltip>
				</div>
			</template>
		</template>
		<template #pageSearch></template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createModelAsync, createDrawerAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./dialog/detail.vue";
	import { markRaw } from "vue";
	import { Plus } from "@element-plus/icons-vue";
	import { getList, delUser } from "@/api/setting/user";
	import { ElMessageBox, ElTooltip, type Action } from "element-plus";

	const formConfig: FormConfig = {
		span: 3,
		labelWidth: 100,
		labelPosition: "right",
		expandSpan: 4,
		notMargn: true,
		formItems: [
			{
				type: "ElSelect",
				value: "",
				prop: "markResult",
				attrs: {
					placeholder: "所属系统",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "区域1",
							id: "0"
						},
						{
							label: "区域2",
							id: "1"
						}
					]
				}
			},
			{
				type: "ElInput",
				value: "",
				label: "",
				prop: "username",
				attrs: {
					placeholder: "角色名称",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							pageApi.pageList();
						}
					}
				}
			},
			{
				type: "ElInput",
				value: "",
				label: "",
				prop: "phone",
				attrs: {
					placeholder: "角色标志",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							pageApi.pageList();
						}
					}
				}
			}
		]
	};

	const { page, pageApi } = usePage({
		formConfig,
		title: "角色管理",
		// listApi: getList,
		toolbar: [
			{
				label: "新增",
				type: "success",
				size: "default",
				icon: markRaw(Plus),
				onClick: async () => {
					createModelAsync(
						{
							title: "新增用户",
							showNext: false,
							width: "720px",
							closeOnClickModal: false,
							closeOnPressEscape: false
						},
						{},
						<Detail />
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
			dataSource: [{ label: 1 }, { label: 1 }, { label: 1 }],

			attrs: {
				fit: true,
				border: true,
				height: "100%",
				rowClassName: ({ rowIndex }) => {
					if (rowIndex % 2 != 0) {
						return "warning-row";
					} else {
						return "success-row";
					}
				}
			},
			columns: [
				{
					columnKey: "index",
					label: "序号",
					prop: "label",
					width: "80px",
					align: "center"
				},
				{
					columnKey: "username5",
					label: "角色名称",
					prop: "label",
					align: "center"
					// renderHeader(header) {
					// 	return (
					// <div class={"color-red"}>
					// 	<el-tooltip
					// 		class="box-item"
					// 		effect="dark"
					// 		content="Top Center prompts info"
					// 		placement="top"
					// 	>
					// 		{header.column.label}
					// 	</el-tooltip>
					// </div>
					// 	);
					// }
				},
				{
					columnKey: "username",
					label: "角色标志",
					prop: "username",
					align: "center",
					width: "120px"
				},
				{
					columnKey: "phone",
					label: "所属系统",
					prop: "phone",
					align: "center"
				},
				{
					columnKey: "realName",
					label: "修改人",
					prop: "realName",
					align: "center",
					width: "120px"
				},
				{
					columnKey: "workId",
					label: "修改时间",
					prop: "workId",
					align: "center",
					width: "180px"
				},

				{
					columnKey: "workId",
					label: "备注",
					prop: "workId",
					align: "center"
				},
				{
					columnKey: "actions",
					label: "操作",
					prop: "label",
					width: "180px",
					align: "center"
				}
			],
			actions: [
				{
					text: "编辑",
					type: "primary",
					size: "small",
					plain: true,
					onClick: async ({ row }) => {
						createModelAsync(
							{
								title: "编辑",
								showNext: false,
								width: "720px",
								closeOnClickModal: false,
								closeOnPressEscape: false
							},
							{},
							<Detail rowInfo={row} />
						).then(() => {
							pageApi.pageList();
						});
					}
				},
				{
					text: "删除",
					size: "small",
					plain: true,
					danger: true,
					type: "danger",
					onClick: ({ row }) => {
						ElMessageBox.alert("是否删除该条数据", "提示", {
							confirmButtonText: "确认",
							callback: (action: Action) => {
								if (action == "confirm") {
									delUser(row.id).then(() => {
										pageApi.pageList();
									});
								}
							}
						});
					}
				}
			]
		}
	});
</script>
