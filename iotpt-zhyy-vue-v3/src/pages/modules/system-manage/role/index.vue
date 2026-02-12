<template>
	<BasePage v-bind="page">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'systemId'">
				<span v-text="selectDictLabel(systematicType, row.systemId)"></span>
			</template>
		</template>

		<template #pageSearch></template>
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
	import { Plus } from "@element-plus/icons-vue";
	import { getPageList, doDelete, getRoleUser } from "@/api/setting/role";
	import { getDictByKey } from "@/api/setting/sysDict";
	import { ElMessageBox, ElTooltip, type Action, ElMessage } from "element-plus";

	const systematicType = ref([]);

	const getSystematicType = async () => {
		try {
			const res = await getDictByKey();
			const systematicData = res?.systematic_type;

			systematicType.value = systematicData
				? Object.values(systematicData).map((item) => ({
						value: item.dictValue,
						label: item.dictLabel
					}))
				: [];
		} catch (error) {
			console.error("获取系统类型失败:", error);
		}
	};

	// 在组件挂载时调用
	onMounted(() => {
		getSystematicType();
	});

	const selectDictLabel = (dictList: any[], value: string) => {
		if (!dictList?.length || !value) return value;
		const item = dictList.find((item) => String(item.value) === String(value));
		return item ? item.label : value;
	};

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
				prop: "systemId",
				attrs: {
					placeholder: "所属系统",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "value",
					list: systematicType
				}
			},
			{
				type: "ElInput",
				value: "",
				label: "",
				prop: "roleName",
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
				prop: "roleFlag",
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
		listApi: getPageList,
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
						<Detail systematicType={systematicType.value} />
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
					columnKey: "roleName",
					label: "角色名称",
					prop: "roleName",
					align: "center"
				},
				{
					columnKey: "roleFlag",
					label: "角色标志",
					prop: "roleFlag",
					align: "center"
				},
				{
					columnKey: "systemId",
					label: "所属系统",
					prop: "systemId",
					align: "center"
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
					onClick: async (row: any) => {
						createModelAsync(
							{
								title: "编辑",
								showNext: false,
								width: "720px",
								closeOnClickModal: false,
								closeOnPressEscape: false
							},
							{},
							<Detail rowInfo={row} systematicType={systematicType.value} />
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
					show: (row: any) => !["zhyyadmin", "yyadmin"].includes(row.roleFlag),
					onClick: (row: any) => {
						handleDelete(row);
					}
				}
			]
		}
	});
	const handleDelete = async (row: any) => {
		if (!row.id) {
			ElMessage.warning("请选择要删除的项");
			return;
		}

		try {
			// 查询角色下面的用户
			const res = await getRoleUser({ role_id: row.id });
			if (res.data?.length > 0) {
				const userNames = res.data.map((item) => item.realName).join("、");
				ElMessage.error(`该角色下面有用户(${userNames})，不能删除！`);
				return;
			}

			// 确认删除
			await ElMessageBox.confirm("你确定要删除当前项吗", "提示", {
				confirmButtonText: "确认",
				cancelButtonText: "取消",
				type: "warning"
			});

			// 执行删除
			await doDelete({ id: row.id });

			// 如果当前页只有一条数据且不是第一页，则回到上一页
			if (page.pagination?.total === 1 && page.pagination.currentPage > 1) {
				page.pagination.currentPage--;
			}

			pageApi.pageList();
		} catch (error) {
			if (error !== "cancel") {
				ElMessage.error("删除失败");
			}
		}
	};
</script>
