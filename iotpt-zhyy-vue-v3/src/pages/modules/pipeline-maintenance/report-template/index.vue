<template>
	<BasePage v-bind="page">
		<template #pageBody>
			<div class="card-list">
				<div
					v-if="page.table?.dataSource.length == 0"
					class="el-table__empty-text"
					style="text-align: center; width: 100%; font-size: 12px"
				>
					<ElEmpty description="暂无数据"></ElEmpty>
				</div>
				<template v-else>
					<div class="card-box" v-for="item in page.table?.dataSource" :key="item">
						<div class="card-head">
							<img src="@/assets/images/report-template/report-icon.png" alt="">
							<div>{{ item.tempName }}</div>
						</div>
						<div class="card-content">
							<div class="content-item">
								<div class="item-label">
									<i class="ri-user-line icon"></i>
									<span>创建人：</span>
								</div>
								<div class="item-value">{{ item.createUserName }}</div>
							</div>
							<div class="content-item">
								<div class="item-label">
									<i class="ri-time-line icon"></i>
									<span>创建时间：</span>
								</div>
								<div class="item-value">{{ item.createTime }}</div>
							</div>
							<div class="content-item">
								<div class="item-label">
									<i class="ri-bookmark-3-line icon"></i>
									<span>创建类型：</span>
								</div>
								<div class="item-value">{{ item.tempType == 1 ? "单区域" : "多区域" }}</div>
							</div>
						</div>
						<div class="card-food">
							<div class="item-btn item-edit" @click="handleEdit(item)">编辑</div>
							<div class="item-btn" @click="handleDelete(item)">删除</div>
						</div>
					</div>
				</template>
			</div>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import { usePage } from "@/core/struct/page";
	import { Plus } from "@element-plus/icons-vue";
	import { markRaw } from "vue";
	import { ElMessageBox, type Action } from "element-plus";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import DealListDialog from "@/pages/modules/alarm-center/history-alarm/dialog/dealListDialog.vue";
	import DetailDialog from "@/pages/modules/alarm-center/history-alarm/dialog/detailDialog.vue";
	import AddDialog from "@/pages/modules/pipeline-maintenance/report-template/dialog/addDialog.vue";
	import { selectPageList, deleteTemplate } from "@/api/pipeline-maintenance/report-template";

	const formConfig: FormConfig = {
		inline: false,
		span: 4,
		expandSpan: 6,
		labelWidth:0,
		formItems: [
			{
				type: "ElInput",
				value: "",
				prop: "tempName",
				attrs: {
					placeholder: "模板名称",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							pageApi.pageList();
						}
					}
				}
			},
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		title: "报告管理",
		toolbar: [
			{
				label: "创建模板",
				size: "default",
				type:'success',
				icon: markRaw(Plus),
				onClick: async () => {
					createDrawerAsync(
						{
							title: "创建模板",
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
		listApi: async (data) => {
			return await selectPageList({ ...data });
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
		}
	});

	// 编辑
	const handleEdit = (row) => {
		createDrawerAsync(
			{
				title: "创建模板",
				showNext: false,
				width: "1200px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<AddDialog row={row} />
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
					deleteTemplate({ id: row.id }).then(() => {
						pageApi.pageList();
					});
				}
			}
		});
	}
</script>
<style scoped lang="scss">
	.card-list {
		display: flex;
		flex-wrap:wrap;

		.card-box {
			width: calc(20% - 12px);
			margin-right:12px;
			// height: 190px;
			display: flex;
			flex-direction: column;
			background: #FFFFFF;
			border-radius: 8px 8px 8px 8px;
			border: 1px solid rgba(0,0,0,0.15);
			margin-bottom:12px;

			.card-head {
				display: flex;
				align-items: center;
				padding: 12px 16px;
				background: #F7F8FA;
				border-radius: 8px 8px 0px 0px;
				font-size: 16px;
				color: rgba(0,0,0,0.85);
				height: 44px;
				border-top: 3px solid #345BAD;
				background: #E0EDFA;

				img {
					width: 18px;
					height:20px;
					margin-right:8px;
				}
			}
			.card-content {
				padding:12px 12px 4px;
				flex:1;
				font-size: 14px;
				color: rgba(0,0,0,0.65);
				line-height: 20px;
				.content-item {
					display: flex;
					align-items: center;
					margin-bottom:8px;
					.icon {
						margin-right: 6px;
					}
					.item-label {
						width: 100px;
					}
					.item-value {
						color: rgba(0,0,0,0.85);
					}
				}
			}
			.card-food {
				display: flex;
				align-items: center;
				border-top: 1px solid rgba(0,0,0,0.06);
				height: 40px;
				line-height: 40px;
				text-align: center;

				.item-btn {
					width: 50%;
					cursor: pointer;
				}
				.item-edit {
					border-right: 1px solid rgba(0,0,0,0.06);
				}


			}
		}
		
	}
</style>
