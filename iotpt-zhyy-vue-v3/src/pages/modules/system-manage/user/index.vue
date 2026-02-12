<template>
	<BasePage v-bind="page">
		<!-- "status": "", //状态 0启用1禁用 -->
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'isEnable'">
				<ElSwitch :model-value="row.isEnable === 1" @change="(value) => handleStatusChange(row, value)" />
			</template>
			<template v-if="column.columnKey === 'roleNamesStr'">
				<template v-for="(role, index) in row.roleNamesStr?.split(',')" :key="index">
					<ElTag v-if="role" class="mr-1 mt-2px mb-2px">{{ role }}</ElTag>
				</template>
			</template>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { ref, onMounted } from "vue";

	import { createModelAsync, createDrawerAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./dialog/detail.vue";
	import { markRaw } from "vue";
	import { Plus, Upload } from "@element-plus/icons-vue";
	import { getListUser, delUser, doEnable, syncAccountInfo, toExportTemplate, importUser } from "@/api/setting/user";
	import { findOrg, findAllDeptByUnit } from "@/api/setting/org";
	import { getList } from "@/api/setting/role";
	import { ElMessageBox, ElMessage, type Action, ElSwitch, ElTag } from "element-plus";
	import { downBlobFile, createFileInput } from "@/core/utils";
	const isDeptDisabled = ref(true);
	const orgTreeData = ref<any[]>([]);
	const deptList = ref<any[]>([]);
	const roleList = ref<any[]>([]);

	onMounted(async () => {
		const res = await findOrg();
		orgTreeData.value = res;
		const roleRes = await getList({});
		roleList.value = roleRes;
	});
	const handleUnitChange = async (value) => {
		// 清空部门选择值
		if (page.form?.value) {
			page.form.value.deptId = "";
		}

		if (value) {
			isDeptDisabled.value = false;
			try {
				const res = await findAllDeptByUnit({ orgId: value });
				deptList.value = res || [];
			} catch (error) {
				console.error("获取部门列表失败:", error);
				deptList.value = [];
			}
		} else {
			isDeptDisabled.value = true;
			deptList.value = [];
		}
	};

	const formConfig: FormConfig = {
		span: 3,
		labelWidth: 100,
		labelPosition: "right",
		expandSpan: 4,
		notMargn: true,
		formItems: [
			{
				type: "ElTreeSelect",
				value: "",
				prop: "unitId",
				attrs: {
					placeholder: "选择所属单位",
					clearable: true,
					checkStrictly: true,
					renderAfterExpand: false,
					onChange: handleUnitChange,
					props: {
						label: "label",
						value: "id",
						children: "children"
					},
					data: orgTreeData
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "deptId",
				attrs: {
					placeholder: "选择所属部门",
					clearable: true,
					disabled: isDeptDisabled
				},
				select: {
					type: "ElOption",
					label: "deptName",
					value: "id",
					list: deptList
				}
			},

			{
				type: "ElSelect",
				value: "",
				prop: "roleId",
				label: "角色名称",
				attrs: {
					placeholder: "请选择角色名称",
					filterable: true,
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "roleName",
					value: "id",
					list: roleList
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
				prop: "realName",
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
				prop: "loginName",
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
		listApi: getListUser,
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
						<Detail orgTreeData={orgTreeData.value} />
					).then(() => {
						pageApi.pageList();
					});
				}
			},
			// 下载模板
			{
				label: "下载模板",
				type: "warning",
				size: "default",
				onClick: async () => {
					toExportTemplate().then((file) => {
						downBlobFile(file, "用户导入模板.xlsx");
					});
				}
			},
			// 批量导入
			{
				label: "批量导入",
				type: "primary",
				size: "default",
				onClick: async () => {
					createFileInput(".xlsx").then((file) => {
						const formData = new FormData();
						formData.append("file", file);
						importUser(formData).then(() => {
							pageApi.pageList();
						});
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
				height: "100%"
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
					columnKey: "dahuaCode",
					label: "员工编号",
					prop: "dahuaCode",
					align: "center",
					width: "160px"
				},
				{
					columnKey: "realName",
					label: "用户姓名",
					prop: "realName",
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
					columnKey: "roleNamesStr",
					label: "角色名称",
					prop: "roleNamesStr",
					align: "center"
				},
				{
					columnKey: "deptName",
					label: "所属组织",
					prop: "deptName",
					align: "center"
				},

				{
					columnKey: "isEnable",
					label: "是否启用",
					prop: "isEnable",
					align: "center",
					width: "100px"
				},
				{
					columnKey: "updateUserName",
					label: "修改人",
					prop: "updateUserName",
					align: "center",
					width: "120px"
				},
				{
					columnKey: "updateTime",
					label: "修改时间",
					prop: "updateTime",
					align: "center",
					width: "180px"
				},
				{
					columnKey: "memo",
					label: "备注",
					prop: "memo",
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
					onClick: async (row: any) => {
						createDrawerAsync(
							{
								title: "新增用户",
								showNext: false,
								width: "960px",
								closeOnClickModal: false,
								closeOnPressEscape: false
							},
							{},
							<Detail rowInfo={row} orgTreeData={orgTreeData.value} />
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
					onClick: (row: any) => {
						ElMessageBox.alert("是否删除该条数据", "提示", {
							confirmButtonText: "确认",
							callback: (action: Action) => {
								if (action == "confirm") {
									delUser({ id: row.id }).then(() => {
										pageApi.pageList();
									});
								}
							}
						});
					}
				}
				// {
				// 	text: "重置密码",
				// 	size: "small",
				// 	plain: true,
				// 	type: "warning",
				// 	onClick: ({ row }) => {
				// 		ElMessageBox.alert("是否重置该条数据", "提示", {
				// 			confirmButtonText: "确认",
				// 			callback: (action: Action) => {
				// 				if (action == "confirm") {
				// 					resetPass(row.id).then(() => {
				// 						pageApi.pageList();
				// 					});
				// 				}
				// 			}
				// 		});
				// 	}
				// }
			]
		}
	});
	const handleStatusChange = async (row: any, value: boolean) => {
		try {
			await doEnable({ id: row.id, isEnable: value ? 1 : 0 });
			ElMessage.success(value ? "启用成功" : "禁用成功");
			pageApi.pageList();
		} catch (error) {
			ElMessage.error("操作失败");
		}
	};
</script>
