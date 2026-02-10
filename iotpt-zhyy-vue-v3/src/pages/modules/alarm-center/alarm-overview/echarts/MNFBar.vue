<template>
	<div ref="chartRef" style="width: 100%; height: 100%"></div>
</template>

<script setup>
	import { ref, onMounted, onUnmounted, watchEffect } from "vue";
	import * as echarts from "echarts";

	const props = defineProps({
		xdata: {
			type: Array,
			default: () => []
		},
		ydata: {
			type: Array,
			default: () => []
		},
		ydata1: {
			type: Array,
			default: () => []
		}
	});

	// 定义图表DOM的ref
	const chartRef = ref(null);
	// 定义图表实例的变量
	let myChart = null;

	const updateEcharts = () => {
		if (chartRef.value) {
			// 初始化echarts实例
			window.removeEventListener("resize", handleResize);
			myChart = echarts.init(chartRef.value);

			// 定义数据
			const xData = props.xdata || [];
			const yData = props.ydata || [];
			const yData1 = props.ydata1 || [];

			// 配置项
			const option = {
				legend: {
					data: ["故障"],
					show: false
				},

				grid: {
					top: "6",
					left: "-20",
					bottom: "0%", // 调整了bottom值，给x轴标签留出空间
					right: "0%",
					containLabel: true
				},

				animation: false,
				xAxis: [
					{
						show: false, // 恢复x轴显示以查看标签
						type: "category",
						data: xData,
						axisTick: {
							alignWithLabel: true,
							show: false // 隐藏刻度
						},
						axisLine: {
							show: false
						},
						axisLabel: {
							textStyle: {
								color: "#ddd"
							},
							margin: 0, // 标签与轴线的距离
							interval: 0, // 强制显示所有标签
							fontSize: 12 // 调整字体大小以适应
						}
					}
				],
				yAxis: [
					{
						show: false,
						type: "value"
					}
				],
				series: [
					{
						name: "急停",
						type: "bar",
						barWidth: "12",
						barGap: "10%", // Make series be overlap
						barCateGoryGap: "10%",
						itemStyle: {
							normal: {
								// 修改渐变
								color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
									{ offset: 0, color: "#F5932B" },
									{ offset: 0.4651, color: "#FFC23D" },
									{ offset: 1, color: "#F5932B" }
								]),
							}
						},
						data: yData1
					}
				]
			};

			// 设置配置项
			myChart.setOption(option);

			// 监听窗口大小变化，重绘图表
			window.addEventListener("resize", handleResize);
		}
	};

	// 窗口大小变化处理函数
	const handleResize = () => {
		if (myChart) {
			myChart.resize();
		}
	};

	// 组件卸载时销毁实例和移除监听
	onUnmounted(() => {
		if (myChart) {
			myChart.dispose();
			myChart = null;
		}
		window.removeEventListener("resize", handleResize);
	});

	watchEffect(() => {
		updateEcharts();
	});
</script>

<style scoped>
	/* 如果需要特定的容器高度，可以在这里设置，否则由父级决定 */
</style>
