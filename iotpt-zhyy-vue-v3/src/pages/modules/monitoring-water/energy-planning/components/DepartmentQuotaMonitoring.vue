<template>
	<div class="department-quota-monitoring">
		<el-table
			:data="tableData"
			style="width: 100%; height: 100%"
			fit
			:default-sort="{ prop: 'id', order: 'ascending' }"
			@sort-change="handleSortChange"
			border
		>
			<el-table-column prop="id" label="ID" sortable width="80" align="center"></el-table-column>

			<el-table-column prop="departmentName" label="部门名称" sortable align="center"></el-table-column>

			<el-table-column prop="actualEnergy" label="实际能耗" sortable align="center"></el-table-column>

			<el-table-column prop="yearOnYear" label="能耗同比" sortable align="center">
				<template #default="{ row }">
					<span :class="['energy-change', row.yearOnYear > 0 ? 'up' : 'down']">
						{{ Math.abs(row.yearOnYear) }}%
						<span class="arrow" :class="row.yearOnYear > 0 ? 'up' : 'down'"></span>
					</span>
				</template>
			</el-table-column>

			<el-table-column prop="monthOnMonth" label="能耗环比" sortable align="center">
				<template #default="{ row }">
					<span :class="['energy-change', row.monthOnMonth > 0 ? 'up' : 'down']">
						{{ Math.abs(row.monthOnMonth) }}%
						<span class="arrow" :class="row.monthOnMonth > 0 ? 'up' : 'down'"></span>
					</span>
				</template>
			</el-table-column>

			<el-table-column prop="ratio" label="实际能耗占管控值比例" sortable align="center">
				<template #default="{ row }">
					<span :class="['ratio-status', getStatusClass(row.ratio)]">
						{{ getStatusText(row.ratio) }}
					</span>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>

<script setup lang="ts">
	import { ref } from "vue";

	// 表格数据
	const tableData = ref(
		Array.from({ length: 50 }, (n: number, k: number) => {
			return {
				id: k + 1,
				departmentName: `部门${k + 1}`,
				actualEnergy: Math.floor(Math.random() * 1000) + 1000,
				yearOnYear: Math.floor(Math.random() * 20) - 10,
				monthOnMonth: Math.floor(Math.random() * 20) - 10,
				ratio: Math.floor(Math.random() * 30) + 70
			};
		})
	);

	// 排序处理
	const handleSortChange = ({ prop, order }) => {
		if (!order) return;

		tableData.value.sort((a, b) => {
			const valueA = a[prop];
			const valueB = b[prop];

			if (order === "ascending") {
				return valueA > valueB ? 1 : -1;
			} else {
				return valueA < valueB ? 1 : -1;
			}
		});
	};

	// 获取状态样式类
	const getStatusClass = (ratio) => {
		if (ratio < 70) return "normal";
		if (ratio < 85) return "warning";
		if (ratio < 95) return "danger";
		return "none";
	};

	// 获取状态文本
	const getStatusText = (ratio) => {
		if (ratio < 70) return "正常";
		if (ratio < 85) return "告警";
		if (ratio < 95) return "危险";
		return "无";
	};
</script>

<style lang="scss" scoped>
	.department-quota-monitoring {
		width: 100%;
		height: 100%;
	}

	.energy-change {
		display: inline-flex;
		align-items: center;

		&.up {
			color: #f56c6c;
		}

		&.down {
			color: #67c23a;
		}

		.arrow {
			margin-left: 4px;
			width: 0;
			height: 0;
			border-left: 5px solid transparent;
			border-right: 5px solid transparent;

			&.up {
				border-bottom: 5px solid currentColor;
			}

			&.down {
				border-top: 5px solid currentColor;
			}
		}
	}

	.ratio-status {
		padding: 2px 8px;
		border-radius: 4px;
		font-size: 12px;

		&.none {
			background-color: #f5f7fa;
			color: #909399;
		}

		&.normal {
			background-color: #f0f9eb;
			color: #67c23a;
		}

		&.warning {
			background-color: #ecf5ff;
			color: #409eff;
		}

		&.danger {
			background-color: #fef0f0;
			color: #f56c6c;
		}
	}
</style>
