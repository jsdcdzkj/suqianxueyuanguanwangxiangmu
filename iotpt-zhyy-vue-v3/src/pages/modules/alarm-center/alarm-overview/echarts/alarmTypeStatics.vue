<template>
	<div ref="chartRef" style="width: 100%; height: 100%"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watchEffect } from "vue";
import * as echarts from "echarts";

const props = defineProps({
	pieData: {
		type: Array,
		default: () => [{
			value: 13,
			name: "电气火灾告警"
		},
		{
			value: 2,
			name: "手动告警"
		},
		{
			value: 4,
			name: "环境告警"
		},
		{
			value: 8,
			name: "其它告警"
		}]
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

		// 配置项
		const option = {
                tooltip: {
                    trigger: "item"
                },
                series: [
                    {
                        name: "类型统计",
                        type: "pie",
                        radius: ["35%", "65%"],
                        data: props.pieData,
                        label: {
                            show: true,
                            position: "outside",
                            formatter: function (param) {
                                return "{hr|}{a|" + param.percent + "%}\n{b|" + param.name + "}";
                            },
                            rich: {
                                a: {
                                    color: "#333",
                                    align: "center",
                                    padding: [5, 0, 5, 0],
                                    fontWeight: "bold",
                                    fontSize: 13
                                },
                                b: {
                                    color: "#666",
                                    fontSize: 12,
                                    padding: [6, 4, 4, 4],
                                    // 背景色
                                    backgroundColor: "#f3f3f3",
                                    borderRadius: 3
                                }
                            }
                        },
                        labelLine: {
                            show: true,
                            normal: {
                                length: 20,
                                length2: 50
                            }
                        },
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: "rgba(0, 0, 0, 0.5)"
                            }
                        }
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
