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
				tooltip: {
					trigger: "item"
				},
				grid: {
					left: 80,
					bottom: 0,
					top: 0
				},
				xAxis: {
					axisTick: {
						show: false
					},
					axisLine: {
						show: false
					},
					axisLabel: {
						interval: 0,
						textStyle: {
							color: "#fff",
							fontSize: 20
						}
						// margin: 20, //刻度标签与轴线之间的距离。
					},
					splitLine: {
						show: true,
						lineStyle: {
							type: "dashed",
							color: "rgba(255,255,255,0.2)"
						}
					}
				},
				yAxis: {
					data: xData,
					splitLine: {
						show: false
					},
					axisTick: {
						show: false
					},
					axisLine: {
						show: false
					},
					axisLabel: {
						textStyle: {
							color: "rgba(0,0,0,0.85)",
							fontSize: 14
						}
					}
				},
				series: [
					{
						//三个最低下的圆片
						name: "",
						type: "pictorialBar",
						symbolSize: [12, 20],
						symbolOffset: [-5, 0],
						z: 10,
						itemStyle: {
							opacity: 1,
							color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
								{ offset: 0, color: "#198ED9" },
								{ offset: 0.4651, color: "#45B6FF" },
								{ offset: 1, color: "#2386C5" }
							])
						},
						data: [1, 1, 1, 1, 1]
					},
					{
						name: "", //头部
						type: "pictorialBar",
						symbolSize: [12, 20],
						symbolOffset: [4, 0],
						z: 18,
						symbolPosition: "end",
						itemStyle: {
							color: "#2386C5",
							opacity: 1
						},
						data: yData1
					},

					{
						name: "2019",
						type: "bar",
						barWidth: 20,
						barGap: "-100%",
						z: 11,
						itemStyle: {
							color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
								{ offset: 0, color: "#198ED9" },
								{ offset: 0.4651, color: "#45B6FF" },
								{ offset: 1, color: "#2386C5" }
							]),
							opacity: 1
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

<style scoped></style>
