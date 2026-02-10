<template>
	<div ref="chartContainer" :style="{ width: '100%', height: height }"></div>
</template>

<script setup>
	import { onMounted, ref, defineProps } from "vue";
	import * as echarts from "echarts";

	// 接收父组件传入的高度
	const props = defineProps({
		height: {
			type: String,
			default: "285px" // 默认高度
		}
	});

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
		// 模拟数据 - 去年同期用水量
		const lastYearData = [
			14, 16, 12, 9, 7, 6, 8, 14, 20, 24, 28, 32, 35, 38, 36, 33, 30, 26, 23, 20, 18, 16, 15, 14
		];

		// 模拟数据 - 今天用水量
		const todayData = [10, 12, 8, 7, 5, 4, 6, 10, 15, 18, 20, 22, 25, 27, 25, 23, 20, 18, 16, 15, 13, 12, 11, 10];

		const colors = ["#57BD94", "#5C90F7", "#5A6FC0"];

		const option = {
			color: colors,
			// 增加图例组件
			legend: {
				data: ["昨日用水", "去年同期", "实时用水"],
				top: 5
			},
			tooltip: {
				trigger: "axis",
				axisPointer: {
					type: "cross"
				}
			},
			grid: {
				left: "15",
				right: "15",
				bottom: "3%",
				top: "40", // 为图例留出空间
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
					color: "rgba(0,0,0,0.45)"
				}
			},
			series: [
				{
					name: "昨日用水",
					type: "line",
					smooth: true,
					symbol: "none",
					data: yesterdayData,
					lineStyle: {
						width: 3,
						color: "#57BD94"
					},

					showSymbol: false
				},
				{
					name: "去年同期",
					type: "line",
					smooth: true,
					symbol: "none",
					data: lastYearData,
					lineStyle: {
						width: 3,
						color: "#5C90F7"
					},
					showSymbol: false
				},
				{
					name: "实时用水",
					type: "line",
					smooth: true,
					symbol: "none",
					data: todayData,
					lineStyle: {
						width: 0.1,
						color: "#5A6FC0"
					},
					areaStyle: {
						color: "rgba(90, 111, 192, 0.2)"
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
