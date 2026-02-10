<template>
	<div ref="chartContainer" style="width: 100%; height: 100%"></div>
</template>

<script setup lang="ts">
	import { onMounted, ref } from "vue";
	import * as echarts from "echarts";

	const chartContainer = ref(null);

	onMounted(() => {
		initChart();
	});

	const initChart = () => {
		const myChart = echarts.init(chartContainer.value);

		// 生成24小时的时间点
		const hours = Array.from({ length: 24 }, (_, i) => `${i}时`);

		// 模拟数据 - 昨天用水量
		const yesterdayData = [
			12, 15, 10, 8, 6, 5, 7, 12, 18, 22, 25, 28, 30, 32, 30, 28, 25, 22, 20, 18, 16, 14, 13, 12
		];

		// 模拟数据 - 今天用水量
		const todayData = [14, 16, 12, 9, 7, 6, 8, 14, 20, 24, 28, 32, 35, 38, 36, 33, 30, 26, 23, 20, 18, 16, 15, 14];
		const colors = ["#F1975F", "#57BD94"];

		const option = {
			color: colors,
			// 增加图例组件

			tooltip: {
				trigger: "axis",
				axisPointer: {
					type: "cross"
				}
			},
			grid: {
				left: "12",
				right: "0",
				bottom: "0",
				top: "0", // 为图例留出空间
				containLabel: true
			},
			xAxis: {
				type: "category",
				boundaryGap: false,
				data: hours,
				axisLine: {
					show: true,
					lineStyle: {
						color: "#ECECEC"
					}
				},
				axisTick: {
					show: false
				},
				axisLabel: {
					color: "rgba(0,0,0,0.45)"
				}
			},
			yAxis: {
				type: "value",
				splitLine: {
					show: true,
					lineStyle: {
						type: "dashed",
						color: "rgba(0,0,0,0.1)"
					}
				},
				axisTick: {
					show: false
				},
				axisLabel: {
					show: false,
					color: "rgba(0,0,0,0.45)"
				}
			},
			series: [
				{
					name: "昨天",
					type: "line",
					smooth: true,
					symbol: "none",
					data: yesterdayData,
					lineStyle: {
						width: 4,
						color: "#57BD94"
					},
					areaStyle: {
						color: {
							type: "linear",
							x: 0,
							y: 0,
							x2: 0,
							y2: 1,
							colorStops: [
								{
									offset: 0,
									color: "#CFEDE0"
								},
								{
									offset: 0.9565,
									color: "#EBF7F2"
								}
							],
							global: false
						}
					},
					showSymbol: false
				}
			]
		};

		myChart.setOption(option);

		// 响应式调整大小
		window.addEventListener("resize", () => {
			myChart.resize();
		});
	};
</script>

<style scoped>
	/* 可以在这里添加组件特定的样式 */
</style>
