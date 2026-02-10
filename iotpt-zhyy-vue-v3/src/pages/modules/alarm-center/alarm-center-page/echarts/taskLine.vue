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
                        name: "",
                        type: "line",
                        lineStyle: {
                            normal: {
                                width: 2,
                                color: "#48CBB9" // 线条颜色
                            }
                        },
                        itemStyle: {
                            color: "#48CBB9"
                        },
                        areaStyle: {
                            normal: {
                                //线性渐变，前4个参数分别是x0,y0,x2,y2(范围0~1);相当于图形包围盒中的百分比。如果最后一个参数是‘true’，则该四个值是绝对像素位置。
                                color: new echarts.graphic.LinearGradient(
                                    0,
                                    0,
                                    0,
                                    1,
                                    [
                                        {
                                            offset: 0,
                                            color: "rgba(84,176,157,0.4)"
                                        },
                                        {
                                            offset: 1,
                                            color: "rgba(84,176,157, 0)"
                                        }
                                    ],
                                    false
                                )
                            }
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
