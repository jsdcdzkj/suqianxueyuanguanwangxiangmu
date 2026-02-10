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
			tooltip: {
				trigger: "axis"
			},
			legend: {
				data: [
					"广州西门子变压器公司",
					"耐恒（广州）纸品有限公司",
					"广州斗原钢铁有限公司",
					"广州璨宇光学有限公司",
					"佐登妮丝（广州）有限公司"
				]
			},
			grid: {
				left: "3%",
				right: "4%",
				bottom: "3%",
				containLabel: true
			},
			xAxis: {
				type: "category",
				boundaryGap: false,
				axisLabel: {
					interval: 0,
					textStyle: {
						color: 'rgba(0,0,0,0.45)'
					},
					// 默认x轴字体大小
					fontSize: 12,
					// margin:文字到x轴的距离
					margin: 15,
					align:'center'
				},
				axisLine: {
					show:true,
					lineStyle: {
						color: 'rgba(0,0,0,0.15)'
					}
				},
				data: [
					"00:00",
					"06:00",
					"12:00",
					"18:00",
					"00:00",
					"06:00",
					"12:00",
					"18:00",
					"00:00"
				]
			},
			yAxis: {
				type: "value",
				splitLine: {
					lineStyle: {
						type: "dashed",
						color: "#E9E9E9"
					}
				},
				axisLabel: {
					textStyle: {
						color: 'rgba(0,0,0,0.45)'
					}
				},
			},
			series: [
				{
					name: "广州西门子变压器公司",
					type: "line",
					symbol: 'diamond',
					symbolSize: 14,
					// stack: 'Total',
					data: [130, 105, 135, 120, 175, 205, 185, 195, 208],
					smooth: true
				},
				{
					name: "耐恒（广州）纸品有限公司",
					type: "line",
					symbol: 'diamond',
					symbolSize: 14,
					// stack: 'Total',
					data: [225, 222, 180, 234, 205, 180, 170, 220, 231],
					smooth: true
				},
				{
					name: "广州斗原钢铁有限公司",
					type: "line",
					symbol: 'diamond',
					symbolSize: 14,
					// stack: 'Total',
					data: [180, 230, 201, 150, 230, 230, 240, 210, 240],
					smooth: true
				},
				{
					name: "广州璨宇光学有限公司",
					type: "line",
					symbol: 'diamond',
					symbolSize: 14,
					// stack: 'Total',
					data: [180, 175, 201, 210, 200, 142, 205, 235, 248],
					smooth: true
				},
				{
					name: "佐登妮丝（广州）有限公司",
					type: "line",
					symbol: 'diamond',
					symbolSize: 14,
					// stack: 'Total',
					data: [235, 250, 238, 252, 260, 245, 235, 235, 230],
					smooth: true
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
