<template>
	<StructureCard title="告警排名">
		<template #actions>
			<ElRadioGroup>
				<ElRadioButton label="1">区域排名</ElRadioButton>
				<ElRadioButton label="2">类型排名</ElRadioButton>
				<ElRadioButton label="2">等级排名</ElRadioButton>
			</ElRadioGroup>
		</template>
		<div class="table-box-separate">
			<ElTable :data="tableData" style="width: 100%" height="370" stripe ref="tableRef">
				<ElTableColumn
					prop="projectName"
					show-overflow-tooltip
					label="排名"
					min-width="80"
					align="center"
				/>
				<ElTableColumn prop="projectStatus" label="区域名称" min-width="120" align="center">
				</ElTableColumn>
				<ElTableColumn prop="projectSchedule" label="次数" align="center">
				</ElTableColumn>
				<ElTableColumn prop="projectManagerName" label="操作" width="140" align="center">
					<template #default="scope">
						<ElButton type="primary" size="mini">查看详情</ElButton>
					</template>
				</ElTableColumn>
			</ElTable>
		</div>
	</StructureCard>
</template>

<script setup lang="tsx">
import StructureCard from "@/components/card/StructureCard";
import { ElRadioGroup, ElRadioButton, ElTable, ElTableColumn } from "element-plus";
import { ref } from "vue";
const orderType = ref("1");
const handleChange = (val: string) => {
	console.log(val);
};
const tableData = ref([])
</script>
<style lang="scss" scoped>
::v-deep {
		.el-card__header {
			font-weight: bold;
		}

		// 工时对比表的表格
		.work-time-table {
			border-radius: 12px;
			overflow: hidden;
			background: linear-gradient(180deg, #ffffff 51%, #f2f8ff 100%);

			.cell {
				line-height: 32px;
			}

			.el-table__header {
			}

			th.el-table__cell {
				background: linear-gradient(180deg, #e8f4fe 0%, #d7edff 100%) !important;
			}
		}
	}
	// 表格样式
	.table-box-separate {
		overflow: hidden;
		:deep(.el-table) {
			border: none;
			background-color: transparent;
			tr {
				background-color: transparent;
			}
			th.el-table__cell {
				padding: 0;
				background: #e6f1ff !important;
				font-size: 14px;
				color: #275798;
				.cell {
					line-height: 44px;
					border-left: 2px solid #fff;
				}
				&:first-child .cell {
					border-left: none;
				}
				&:first-child {
					border-top-left-radius: 20px;
					border-bottom-left-radius: 20px;
				}
				&:last-child {
					border-top-right-radius: 20px;
					border-bottom-right-radius: 20px;
				}
			}
			td.el-table__cell {
				border-bottom: 1px solid #fff;
				border-left: 2px solid #fff;
				&:first-child {
					border-left: none;
				}
			}
		}
		:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
			background-color: #f4f7ff;
			&:first-child {
				border-top-left-radius: 20px;
				border-bottom-left-radius: 20px;
			}
			&:last-child {
				border-top-right-radius: 20px;
				border-bottom-right-radius: 20px;
			}
		}
		:deep(.el-table__column-filter-trigger i) {
			color: #275798;
		}
	}
</style>
