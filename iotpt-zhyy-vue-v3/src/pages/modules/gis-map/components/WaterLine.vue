<template>
	<div ref="chartRef" style="width: 100%; height: 100%; background: rgba(255, 255, 255, 0.05)"></div>
</template>

<script setup lang="ts">
	import { ref, onMounted, onUnmounted } from "vue";
	import * as echarts from "echarts";

	// 定义 DOM 引用
	const chartRef = ref(null);
	let myChart = null;

	onMounted(() => {
		if (chartRef.value) {
			// 初始化 echarts 实例
			myChart = echarts.init(chartRef.value);

			// 配置项
			const option = {
				color: ["#009688", "#ff9800", "#e91e63"],
				// 网格布局，防止标签溢出
				grid: {
					top: "10",
					left: "10",
					right: "10",
					bottom: "10"
				},
				tooltip: {
					show: true,
					trigger: "axis", //axis , item
					backgroundColor: "RGBA(0, 49, 85, 1)",
					borderColor: "rgba(0, 151, 251, 1)",
					borderWidth: 1,
					borderRadius: 0,
					textStyle: {
						color: "#BCE9FC",
						fontSize: 16,
						align: "left"
					}
				},

				// X 轴配置
				xAxis: {
					show: false,
					type: "category",
					boundaryGap: false, // 让折线从轴头开始
					data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"], // 示例数据
					axisLine: {
						lineStyle: {
							color: "#ccc" // 坐标轴颜色
						}
					},
					axisLabel: {
						color: "#666" // 坐标轴文字颜色
					}
				},
				// Y 轴配置
				yAxis: {
					type: "value",
					show: false,
					splitLine: {
						lineStyle: {
							type: "dashed", // 虚线网格
							color: "#eee"
						}
					},
					axisLabel: {
						color: "#666"
					}
				},
				// 核心系列配置
				series: [
					{
						name: "数据趋势1",
						type: "line",
						lineStyle: {
							color: "#009688", // 第一条线的颜色
							width: 2
						},
						data: [120, 23, 101, 134, 340, 34, 210]
					},
					{
						name: "数据趋势2",
						type: "line",
						lineStyle: {
							color: "#ff9800", // 第二条线的颜色
							width: 2
						},
						data: [22, 92, 101, 134, 90, 30, 90]
					},
					{
						name: "数据趋势3",
						type: "line",
						lineStyle: {
							color: "#e91e63", // 第三条线的颜色
							width: 2
						},
						data: [33, 45, 12, 134, 90, 10, 130]
					}
				]
			};

			// 设置配置项
			myChart.setOption(option);

			// 监听窗口大小变化，自适应重绘
			window.addEventListener("resize", () => {
				myChart.resize();
			});
		}
	});

	onUnmounted(() => {
		if (myChart) {
			// 组件销毁时移除监听和销毁实例
			window.removeEventListener("resize", myChart.resize);
			myChart.dispose();
		}
	});
</script>

<style scoped>
	/* 可以在这里添加额外的样式 */
</style>
