<template>
	<div ref="chartContainer" style="width: 100%; height: 100%"></div>
</template>

<script setup>
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
			tooltip: {
				trigger: "item",
				formatter: "{a} <br/>{b}: {c} ({d}%)"
			},
			legend: {
				orient: "vertical",
				right: 0,
				top: "center",
				data: [
					"Direct",
					"Marketing",
					"Search Engine",
					"Email",
					"Union Ads",
					"Video Ads",
					"Baidu",
					"Google",
					"Bing",
					"Others"
				]
			},
			series: [
				// {
				// 	name: "Access From",
				// 	type: "pie",
				// 	selectedMode: "single",
				// 	radius: [0, "30%"],
				// 	label: {
				// 		position: "inner",
				// 		fontSize: 14
				// 	},
				// 	labelLine: {
				// 		show: false
				// 	},
				// 	data: [
				// 		{ value: 1548, name: "Search Engine" },
				// 		{ value: 775, name: "Direct" },
				// 		{ value: 679, name: "Marketing", selected: true }
				// 	]
				// },
				{
					name: "Access From",
					type: "pie",
					radius: ["40%", "70%"],
					labelLine: {
						length: 100
					},
					label: {
						formatter: "{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ",
						backgroundColor: "#fff",
						borderColor: "rgba(0,0,0,0.12)",
						borderWidth: 1,
						borderRadius: 4,
						rich: {
							a: {
								color: "#6E7079",
								lineHeight: 22,
								align: "center"
							},
							hr: {
								borderColor: "#8C8D8E",
								width: "100%",
								borderWidth: 1,
								height: 0
							},
							b: {
								color: "#4C5058",
								fontSize: 14,
								fontWeight: "bold",
								lineHeight: 33
							},
							per: {
								color: "#fff",
								backgroundColor: "#4C5058",
								padding: [3, 4],
								borderRadius: 4
							}
						}
					},
					data: [
						{ value: 1048, name: "Baidu" },
						{ value: 335, name: "Direct" },
						{ value: 310, name: "Email" },
						{ value: 251, name: "Google" },
						{ value: 234, name: "Union Ads" },
						{ value: 147, name: "Bing" },
						{ value: 135, name: "Video Ads" },
						{ value: 102, name: "Others" }
					]
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
