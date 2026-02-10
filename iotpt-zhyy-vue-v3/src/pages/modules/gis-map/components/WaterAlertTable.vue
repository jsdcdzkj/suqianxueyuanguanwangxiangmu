<template>
	<div class="water-alert-table">
		<table>
			<thead>
				<tr>
					<th>时间</th>
					<th>来源</th>
					<th>状态</th>
					<th>查看</th>
				</tr>
			</thead>
			<tbody>
				<tr v-for="(item, index) in alertList" :key="index" :class="{ 'even-row': index % 2 !== 0 }">
					<td>{{ item.time }}</td>
					<td>{{ item.source }}</td>
					<td>{{ item.status }}</td>
					<td>
						<span class="view-btn">查看</span>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</template>

<script setup lang="ts">
	interface AlertItem {
		time: string;
		source: string;
		status: string;
	}

	const alertList: AlertItem[] = [
		// 这里可以添加实际的数据
		{
			time: "2023-10-01 12:00:00",
			source: "气象站气象站气象站气象站",
			status: "预警"
		},
		{
			time: "2023-10-01 12:00:00",
			source: "气象站气象站气象站气象站",
			status: "预警"
		},
		{
			time: "2023-10-01 12:00:00",
			source: "气象站气象站气象站气象站",
			status: "预警"
		},
		{
			time: "2023-10-01 12:00:00",
			source: "气象站气象站气象站气象站",
			status: "预警"
		}
	];
</script>

<style scoped>
	.water-alert-table {
		width: 100%;
	}

	table {
		width: 100%;
		border-collapse: collapse;
	}

	thead {
		background: rgba(255, 255, 255, 0.05);
		box-shadow: inset 0px 0px 12px 0px rgba(29, 146, 255, 0.46);
		border-radius: 8px;
	}

	thead th {
		height: 24px;

		font-weight: normal;
		font-size: 12px;
		color: rgba(255, 255, 255, 0.65);
		line-height: 20px;
		padding: 0 12px;
		text-align: center;
	}

	tbody tr {
		height: 24px;
	}

	tbody tr.even-row {
		background: rgba(255, 255, 255, 0.08);
	}

	tbody tr td {
		padding: 0 12px;
		font-size: 12px;
		color: rgba(255, 255, 255, 0.85);
		text-align: center;
	}

	.view-btn {
		color: #1d92ff;
		cursor: pointer;
		border-radius: 4px 4px 4px 4px;
		border: 1px solid #3ca6ff;
		padding: 0px 4px;
		font-size: 12px;
	}

	.view-btn:hover {
		color: #40a9ff;
	}
</style>
