<template>
	<div class="water-trend-card">
		<div class="card-header">
			<div class="header-title">{{ title }}</div>
			<div class="header-tags">
				<el-tag v-for="tag in tags" :key="tag" size="small">{{ tag }}</el-tag>
			</div>
		</div>
		<div class="card-content">
			<div ref="chartContainer" class="chart-container"></div>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted } from "vue";
	import * as echarts from "echarts";

	const props = defineProps({
		title: {
			type: String,
			default: "用水量趋势"
		},
		tags: {
			type: Array,
			default: () => ["一级分区"]
		}
	});

	const chartContainer = ref(null);
	let chart = null;

	onMounted(() => {
		initChart();
	});

	const initChart = () => {
		chart = echarts.init(chartContainer.value);

		// 生成24小时的时间点
		const hours = Array.from({ length: 24 }, (_, i) => `${i}时`);

		// 模拟数据
		const todayData = Array.from({ length: 24 }, () => Math.floor(Math.random() * 100));
		const yesterdayData = Array.from({ length: 24 }, () => Math.floor(Math.random() * 100));
		const dayBeforeYesterdayData = Array.from({ length: 24 }, () => Math.floor(Math.random() * 100));

		const option = {
			tooltip: {
				trigger: "axis",
				axisPointer: {
					type: "cross"
				}
			},

			grid: {
				left: "0",
				right: "0",
				bottom: "0",
				top: "12",
				containLabel: true
			},
			xAxis: {
				type: "category",
				boundaryGap: false,
				data: hours,
				axisTick: {
					show: false
				},
				axisLine: {
					show: true,
					lineStyle: {
						color: "#ccc" // 坐标轴颜色
					}
				},
				splitLine: {
					show: true,
					lineStyle: {
						type: "dashed"
					}
				},
				axisLabel: {
					color: "#666" // 坐标轴文字颜色
				}
			},
			yAxis: {
				type: "value",
				splitLine: {
					show: true,
					lineStyle: {
						type: "dashed"
					}
				},
				axisLabel: {
					color: "#666" // 坐标轴文字颜色
				}
			},
			series: [
				{
					name: "今日",
					type: "line",
					smooth: true,
					data: todayData,
					itemStyle: {
						color: "#57BD94"
					}
				},
				{
					name: "昨日",
					type: "line",
					smooth: true,
					data: yesterdayData,
					itemStyle: {
						color: "#F5A250"
					}
				},
				{
					name: "前日",
					type: "line",
					smooth: true,
					data: dayBeforeYesterdayData,
					itemStyle: {
						color: "#4C6FB7"
					}
				}
			]
			// 添加预警阈值区间
			// visualMap: {
			// 	show: false,
			// 	pieces: [
			// 		{
			// 			gt: 50,
			// 			lte: 60
			// 		}
			// 	],
			// 	outOfRange: {
			// 		color: "#000"
			// 	}
			// },
			// markArea: {
			// 	silent: true,
			// 	data: [
			// 		[
			// 			{
			// 				yAxis: 60,
			// 				itemStyle: {
			// 					color: "#FDEDEC"
			// 				}
			// 			},
			// 			{
			// 				yAxis: 70
			// 			}
			// 		]
			// 	]
			// }
		};

		chart.setOption(option);

		// 响应式调整
		window.addEventListener("resize", () => {
			chart && chart.resize();
		});
	};
</script>

<style scoped lang="scss">
	.water-trend-card {
		background: #ffffff;
		border-radius: 4px;
		border: 1px solid rgba(0, 0, 0, 0.15);
		padding: 16px;
		height: 246px;
		display: flex;
		flex-direction: column;

		.card-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 16px;

			.header-title {
				font-size: 16px;
				font-weight: 500;
				color: rgba(0, 0, 0, 0.85);
			}

			.header-tags {
				display: flex;
				gap: 8px;
			}
		}

		.card-content {
			flex: 1;
			min-height: 0;

			.chart-container {
				width: 100%;
				height: 100%;
			}
		}
	}
</style>
