<template>
	<div ref="chartRef" style="width: 100%; height: 100%"></div>
</template>

<script setup>
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
				// 网格布局，防止标签溢出
				grid: {
					top: "0%",
					left: "-20",
					right: "0%",
					bottom: "0%",
					containLabel: true
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
						name: "数据趋势",
						type: "line",
						smooth: false, // 开启平滑曲线
						symbol: "none", // 不显示折线上的小圆点
						// 线条样式
						lineStyle: {
							color: "#3BA8DF",
							width: 3
						},
						// 区域填充样式（渐变）
						areaStyle: {
							// 定义线性渐变
							color: new echarts.graphic.LinearGradient(
								0,
								0, // (x0, y0) - 渐变起点
								0,
								1, // (x1, y1) - 渐变终点，0,1 代表从上到下
								[
									{ offset: 0, color: "rgba(125, 214, 245, 0.50)" }, // 0% 处的颜色
									{ offset: 1, color: "rgba(125, 214, 245, 0)" } // 100% 处的颜色
								]
							)
						},
						// 数据
						data: [120, 132, 101, 134, 90, 230, 210] // 示例数据
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
