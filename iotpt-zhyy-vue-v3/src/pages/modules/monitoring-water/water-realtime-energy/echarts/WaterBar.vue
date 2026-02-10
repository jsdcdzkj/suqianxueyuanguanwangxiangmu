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
					data: ["故障", "急停"],
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
						name: "上部圆",
						type: "pictorialBar",
						silent: true,
						symbolSize: [20, 10],
						symbolOffset: [-11, -6],
						symbolPosition: "end",
						z: 12,
						label: {
							normal: {
								show: false, // 恢复显示数值标签
								position: "top",
								padding: [0, 22, 0, 0],
								fontSize: 15,
								fontWeight: "bold",
								color: "#5BFCF4" // 修改为与渐变主色调一致
							}
						},
						color: "#2386C5", // 使用渐变结束色
						data: yData
					},
					{
						name: "上部圆1",
						type: "pictorialBar",
						silent: true,
						symbolSize: [20, 10],
						symbolOffset: [11, -6],
						symbolPosition: "end",
						z: 12,
						label: {
							normal: {
								show: false, // 恢复显示数值标签
								position: "top",
								fontSize: 15,
								padding: [0, 0, 0, 20],
								fontWeight: "bold",
								color: "#5BFCF4"
							}
						},
						color: "#FFC23D", // 使用渐变结束色
						data: yData1
					},
					{
						name: "底部圆",
						type: "pictorialBar",
						silent: true,
						symbolSize: [20, 10],
						symbolOffset: [-11, 6],
						z: 12,
						color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
							{ offset: 0, color: "#198ED9" },
							{ offset: 0.4651, color: "#45B6FF" },
							{ offset: 1, color: "#2386C5" }
						]), // 使用渐变起始色
						data: yData
					},
					{
						name: "底部圆1",
						type: "pictorialBar",
						silent: true,
						symbolSize: [20, 10],
						symbolOffset: [11, 6],
						z: 12,
						color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
							{ offset: 0, color: "#F5932B" },
							{ offset: 0.4651, color: "#FFC23D" },
							{ offset: 1, color: "#F5932B" }
						]), // 使用渐变起始色
						data: yData1
					},
					{
						name: "故障",
						type: "bar",
						barWidth: "20",
						barGap: "10%", // Make series be overlap
						barCateGoryGap: "10%",
						itemStyle: {
							normal: {
								// 修改渐变
								color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
									{ offset: 0, color: "#198ED9" },
									{ offset: 0.4651, color: "#45B6FF" },
									{ offset: 1, color: "#2386C5" }
								]),
								// 添加阴影效果以模拟垂直渐变
								shadowColor: "rgba(0, 0, 0, 0.5)",
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowOffsetY: 5,
								opacity: 0.9 // 调整透明度
							}
						},
						data: yData
					},
					{
						name: "急停",
						type: "bar",
						barWidth: "20",
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
								// 添加阴影效果以模拟垂直渐变
								shadowColor: "rgba(0, 0, 0, 0.5)",
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowOffsetY: 5,
								opacity: 0.9 // 调整透明度
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
