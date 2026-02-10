<template>
	<BasePage v-bind="page">
		<!-- "status": "", //状态 0启用1禁用 -->
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'status'">
				<ElTag :type="row.status == 0 ? 'success' : 'info'">
					{{ row.status == 0 ? "启用" : "禁用" }}
				</ElTag>
			</template>
		</template>
		<!-- <template #pageHeader></template> -->
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
	import { Plus, Upload } from "@element-plus/icons-vue";
	import { getList, delUser, resetPass, syncAccountInfo } from "@/api/setting/user";
	import { ElMessageBox, ElMessage, type Action } from "element-plus";

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
					placeholder: "选择所属单位",
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
				type: "ElSelect",
				value: "",
				prop: "markResult2",
				attrs: {
					placeholder: "选择所属部门",
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
					placeholder: "手机号",
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
				prop: "username3",
				attrs: {
					placeholder: "用户名称",
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
				prop: "username4",
				attrs: {
					placeholder: "登录名称",
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
		title: "用户列表",
		listApi: getList,
		toolbar: [
			{
				label: "新增",
				type: "success",
				size: "default",
				icon: markRaw(Plus),
				onClick: async () => {
					createDrawerAsync(
						{
							title: "新增用户",
							showNext: false,
							width: "960px",
							closeOnClickModal: false,
							closeOnPressEscape: false
						},
						{},
						<Detail />
					).then(() => {
						pageApi.pageList();
					});
				}
			},
			// 同步
			{
				label: "批量导入",
				type: "primary",
				size: "default",
				icon: markRaw(Upload),
				onClick: async () => {
					// ElMessageBox.alert("是否删除该条数据", "提示", {
					// 		confirmButtonText: "确认",
					// 		callback: (action: Action) => {
					// 			if (action == "confirm") {
					// 				delUser(row.id).then(() => {
					// 					pageApi.pageList();
					// 				});
					// 			}
					// 		}
					// 	});

					ElMessageBox.confirm("确定同步用户数据？", "提示", {
						type: "warning"
					}).then(async () => {
						await syncAccountInfo({}).then(() => {
							ElMessage.success("同步完成");
						});
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
			dataSource: [{ label: 1 }],

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
					label: "员工编号",
					prop: "username5",
					align: "center"
				},
				{
					columnKey: "username",
					label: "用户姓名",
					prop: "username",
					align: "center",
					width: "120px"
				},
				{
					columnKey: "phone",
					label: "手机号",
					prop: "phone",
					align: "center",
					width: "120px"
				},
				{
					columnKey: "realName",
					label: "角色名称",
					prop: "realName",
					align: "center"
				},
				{
					columnKey: "workId",
					label: "所属组织",
					prop: "workId",
					align: "center"
				},

				{
					columnKey: "status",
					label: "是否启用",
					prop: "status",
					align: "center"
				},
				{
					columnKey: "workId",
					label: "修改人",
					prop: "workId",
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
					width: "240px",
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
						createDrawerAsync(
							{
								title: "新增用户",
								showNext: false,
								width: "960px",
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
				},
				{
					text: "重置密码",
					size: "small",
					plain: true,
					type: "warning",
					onClick: ({ row }) => {
						ElMessageBox.alert("是否重置该条数据", "提示", {
							confirmButtonText: "确认",
							callback: (action: Action) => {
								if (action == "confirm") {
									resetPass(row.id).then(() => {
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
