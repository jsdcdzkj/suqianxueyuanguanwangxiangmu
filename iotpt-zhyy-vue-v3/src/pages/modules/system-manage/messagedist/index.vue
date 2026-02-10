<template>
	<BasePage v-bind="page">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'jumpType'">
				<ElLink
					:type="row.jumpType == 1 ? 'danger' : row.jumpType == 2 ? 'success' : 'warning'"
					:underline="false"
				>
					<span v-if="row.jumpType == 1">navigate</span>
					<span v-else-if="row.jumpType == 2">switch</span>
					<span v-else-if="row.jumpType == 3">relaunch</span>
				</ElLink>
			</template>
			<template v-if="column.columnKey === 'status'">
				<ElSwitch
					v-model="row.isEnable"
					:active-value="1"
					:inactive-value="0"
					active-color="#13ce66"
					:before-change="() => handleStatusChange(row)"
				/>
			</template>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createModelAsync, createDrawerAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./components/add.vue";
	import { markRaw } from "vue";
	import { Plus } from "@element-plus/icons-vue";
	import { messagedistList, del, getType, save } from "@/api/setting/messagedist";
	import { ElMessageBox, ElMessage, ElLink, ElTag } from "element-plus";

	const formConfig: FormConfig = {
		span: 3,
		labelWidth: 100,
		labelPosition: "right",
		expandSpan: 4,
		notMargn: true,
		formItems: [
			{
				type: "ElInput",
				value: "",
				prop: "title",
				attrs: {
					placeholder: "输入标题",
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
				prop: "type",
				attrs: {
					placeholder: "请选择类型",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "dictLabel",
					value: "dictValue",
					list: []
				}
			},
			{
				type: "ElInput",
				value: "",
				prop: "keyword",
				attrs: {
					placeholder: "输入关键字",
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
				prop: "templateCode",
				attrs: {
					placeholder: "输入模板Code",
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
				prop: "jumpType",
				attrs: {
					placeholder: "选择跳转类型",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "value",
					list: [
						{ label: "navigate", value: 1 },
						{ label: "switch", value: 2 },
						{ label: "relaunch", value: 3 }
					]
				}
			}
		]
	};

	const { page, pageApi } = usePage({
		formConfig,
		title: "消息分发",
		listApi: messagedistList,
		toolbar: [
			{
				label: "新增",
				type: "success",
				size: "default",
				icon: markRaw(Plus),
				onClick: async () => {
					createModelAsync(
						{
							title: "新增消息分发",
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
			}
		],
		pagination: {
			currentPage: 1,
			pageSize: 20,
			total: 0,
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
					width: "80px",
					align: "center"
				},
				{
					columnKey: "title",
					label: "标题",
					prop: "title",
					align: "center"
				},
				{
					columnKey: "typeName",
					label: "类型",
					prop: "typeName",
					align: "center"
				},
				{
					columnKey: "keyword",
					label: "关键字",
					prop: "keyword",
					align: "center",
					width: "160"
				},
				{
					columnKey: "link",
					label: "跳转链接",
					prop: "link",
					align: "center"
				},
				{
					columnKey: "jumpType",
					label: "跳转类型",
					align: "center",
					width: "200"
				},
				{
					columnKey: "status",
					label: "是否启用",
					align: "center",
					width: "200"
				},
				{
					columnKey: "actions",
					label: "操作",
					prop: "label",
					width: "200px",
					align: "center"
				}
			],
			actions: [
				{
					text: "查看",
					type: "primary",
					size: "small",
					plain: true,
					onClick: async ({ row }) => {
						// 这边需要把({ row })改成 ( row )才能打印出内容，很奇怪！
						console.log("row:", row);
						createModelAsync(
							{
								title: "查看消息分发",
								showNext: false,
								width: "960px",
								closeOnClickModal: false,
								closeOnPressEscape: false
							},
							{},
							<Detail rowInfo={row} viewStatus={true} />
						);
					}
				},
				{
					text: "编辑",
					type: "primary",
					size: "small",
					plain: true,
					onClick: async ({ row }) => {
						createModelAsync(
							{
								title: "编辑消息分发",
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
						ElMessageBox.confirm("此操作将删除该信息, 是否继续?", "提示", {
							confirmButtonText: "确定",
							cancelButtonText: "取消",
							type: "warning"
						}).then(() => {
							del({ id: row.id, isDel: 1 }).then(() => {
								ElMessage.success("删除成功");
								pageApi.pageList();
							});
						});
					}
				}
			]
		}
	});

	// 获取类型列表
	getType({}).then((res) => {
		const typeSelect = formConfig.formItems.find((item) => item.prop === "type");
		if (typeSelect) {
			typeSelect.select.list = res.data;
		}
	});
	// 处理状态切换
	const handleStatusChange = async (row: any) => {
		try {
			const statusText = row.isEnable === 1 ? "启用" : "禁用";
			await ElMessageBox.confirm(`确定要${statusText}该条数据吗？`, "提示", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				type: "warning"
			});

			await save({
				id: row.id,
				isEnable: row.isEnable
			});

			ElMessage.success(`${statusText}成功`);
		} catch (error) {
			// 恢复原状态
			row.isEnable = row.isEnable === 1 ? 0 : 1;
			if (error !== "cancel") {
				ElMessage.error("操作失败");
			}
		}
	};
</script>

<style scoped lang="scss">
	:deep(.input-with-select .el-select .el-input) {
		width: 80px;
	}

	.ignore-time-fl {
		display: flex;
		align-items: center;
		.time-lf {
			width: 180px;
			margin-right: 30px;
		}
	}

	.pop-msg-box {
		background-color: #ecf5ff;
		padding: 10px 14px;

		h4 {
			height: 26px;
			text-align: left;
			color: #409eff;
			font-size: 14px;
			margin: 0;
			font-weight: 500;
			position: relative;
			border-bottom: 1px solid #d5e4f7;
			margin-bottom: 6px;

			&:before {
				content: "";
				display: inline-block;
				width: 4px;
				height: 14px;
				position: absolute;
				left: -12px;
				top: 2px;
				background: #409eff;
				margin-right: 10px;
			}
		}

		p {
			margin: 0;
			line-height: 24px;
			font-size: 14px;
			color: #666;

			span {
				display: inline-block;
				text-align: right;
			}
		}
	}
</style>
