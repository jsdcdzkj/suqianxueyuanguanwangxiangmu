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
                    show: true,
                    trigger: "item"
                },
                radar: {
                    // shape: 'circle',
                    center: ["50%", "50%"],
                    // 设置雷达图半径，可调整为百分比或像素值，这里缩小半径留出边距
                    radius: "70%",
                    splitNumber: 6,
                    // nameGap: "10",
                    name: {
                        textStyle: {
                            color: "#646464"
                        }
                    },
                    axisLine: {
                        show: true,
                        lineStyle: {
                            color: "#DBDDDF",
                            width: 1
                        }
                    },
                    splitLine: {
                        lineStyle: {
                            color: "#DBDDDF",
                            // opacity: 0.5,
                            width: 1
                        }
                    },
                    splitArea: {
                        areaStyle: {
                            color: new echarts.graphic.RadialGradient(0.5, 0.5, 0.5, [
                                { offset: 0, color: "rgba(255, 255, 255, 0)" },
                                { offset: 0.8, color: "rgba(31, 158, 248, 0.1)" },
                                { offset: 1, color: "rgba(31, 158, 248, 0.3)" }
                            ])
                        }
                    },
                    name: {
                        textStyle: {
                            color: "#666",
                            backgroundColor: "#F3F3F3",
                            borderRadius: 4,
                            padding: [5, 5]
                        }
                    },
                    indicator: [
                        {
                            name: "功能完整性",
                            max: 5
                        },
                        {
                            name: "设备在线率",
                            max: 5
                        },
                        {
                            name: "处置及时率",
                            max: 5
                        },
                        {
                            name: "报警处置率",
                            max: 5
                        },
                        {
                            name: "有效报警率",
                            max: 5
                        },
                        {
                            name: "点位覆盖率",
                            max: 5
                        }
                    ]
                },
                series: [
                    {
                        name: "安全评估",
                        type: "radar",
                        symbolSize: 0,
                        areaStyle: {
                            normal: {
                                width: 1,
                                opacity: 0.65,
                                // color linear-gradient( 180deg, #50A0FF 0%, #547FF7 100%);
                                color: new echarts.graphic.LinearGradient(
                                    0,
                                    0,
                                    0,
                                    1,
                                    [
                                        {
                                            offset: 0,
                                            color: "#50A0FF"
                                        },
                                        {
                                            offset: 1,
                                            color: "#547FF7"
                                        }
                                    ],
                                    false
                                )
                            }
                        },
                        lineStyle: {
                            normal: {
                                width: 0
                            }
                        },
                        data: [
                            {
                                itemStyle: {
                                    normal: {
                                        color: "#67abff",
                                        lineStyle: {
                                            color: "#67abff"
                                        }
                                    }
                                },
                                value: [4.5, 4.3, 3.9, 4.1, 4.2, 4.6]
                            }
                        ]
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
