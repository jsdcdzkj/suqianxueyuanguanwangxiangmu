<template>
    <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from "echarts";
import "echarts-liquidfill";
require("echarts/theme/macarons"); // echarts theme
// import resize from "../mixins/resize";

export default {
    // mixins: [resize],
    props: {
        className: {
            type: String,
            default: "chart"
        },
        width: {
            type: String,
            default: "100%"
        },
        height: {
            type: String,
            default: "100%"
        },
        valdata: {
            type: Object,
            default: () => {
                return {
                    xData: [],
                    yData: []
                };
            }
        }
    },
    data() {
        return {
            chart: null
        };
    },
    watch: {
        valdata(val) {
            this.initChart();
        }
    },
    mounted() {
        this.$nextTick(() => {
            this.initChart();
        });
    },
    beforeDestroy() {
        if (!this.chart) {
            return;
        }
        this.chart.dispose();
        this.chart = null;
    },
    methods: {
        initChart() {
            const colors = [
                "#2E93fA",
                "#66DA26",
                "#546570",
                "#E5C22E",
                "#F5C7B8",
                "#6F52CC",
                "#FF9500",
                "#1AA7EC",
                "#4CC790",
                "#FFD166",
                "#A05D56",
                "#D45087",
                "#8D6AB8",
                "#F95D6A",
                "#2F4B7C",
                "#665191"
            ];
            const seriesNames = this.valdata.yData.map((item, index) => item.label);
            const seriesData = this.valdata.yData.map((item) => item.data);
            const series = seriesNames.map((name, index) => ({
                name,
                type: "line",
                showSymbol: false,
                data: seriesData[index],
                // 使用 colors 数组中的颜色
                itemStyle: {
                    color: colors[index % colors.length]
                }
            }));
            this.chart = echarts.init(this.$el);
            this.chart.setOption({
                tooltip: {
                    trigger: "axis",
                    formatter: function (params) {
                        let sum = 0;
                        params.forEach((item) => {
                            sum += item.value;
                        });

                        // 对参数进行排序（从大到小）
                        const sortedParams = [...params].sort((a, b) => b.value - a.value);

                        let tip = `<div style="padding: 5px; background-color: #fff; border-radius: 3px; width: 200px;">`;
                        tip += `<div style="display: flex; justify-content: space-between; margin-bottom: 5px; font-weight: bold;">`;
                        // 直接使用params[0].axisValue获取完整日期
                        tip += `<span>${params[0].axisValue}</span>`;
                        tip += `<span>${sum}</span>`;
                        tip += `</div>`;

                        sortedParams.forEach(function (item) {
                            tip += `<div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 2px;">`;
                            tip += `<div style="display: flex; align-items: center;">`;
                            tip += `<div style="width: 12px; height: 12px; border: 2px solid ${item.color}; border-radius: 50%; margin-right: 5px; margin-top: 2px;"></div>`;
                            tip += `<span>${item.seriesName}</span>`;
                            tip += `</div>`;
                            tip += `<span>${item.value}</span>`;
                            tip += `</div>`;
                        });
                        tip += `</div>`;
                        return tip;
                    }
                },
                legend: {
                    data: seriesNames,
                    top: "0",
                    right: "20",
                    textStyle: {
                        color: "#8C8C8C" // 设置图例文字颜色为灰色
                    }
                },
                grid: {
                    top: "60",
                    left: "20",
                    right: "20",
                    bottom: "20",
                    containLabel: true
                },
                xAxis: {
                    type: "category",
                    boundaryGap: false,
                    data: this.valdata.xData,
                    axisLabel: {
                        show: false, // 仍然不显示x轴标签
                        formatter: function (value) {
                            // 但确保tooltip能获取完整日期
                            return value;
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        show: true,
                        lineStyle: {
                            color: "#e3e3e3" // 设置轴线颜色为灰色
                        }
                    }
                },
                yAxis: {
                    type: "value",
                    // 虚线
                    splitLine: {
                        show: true,
                        lineStyle: {
                            type: "dashed",
                            color: "#CFCFCF" // 设置虚线颜色为灰色
                        }
                    },
                    axisLabel: {
                        show: true,
                        formatter: "{value}",
                        textStyle: {
                            color: "#8C8C8C" // 设置y轴文字颜色为灰色
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        show: false
                    },
                    splitArea: {
                        show: false
                    }
                },
                series
            });
        },
        sortedData(xData, yData) {
            // 创建一个包含索引和xData值的数组
            const indexedData = xData.map((value, index) => ({ index, value }));

            // 根据xData的值进行排序
            indexedData.sort((a, b) => a.value - b.value);

            // 获取排序后的索引顺序
            const sortedIndices = indexedData.map((item) => item.index);

            // 对xData和yData进行排序
            const sortedXData = sortedIndices.map((i) => xData[i]);

            // 对每个yData的数据项也进行相同的排序
            const sortedYData = yData.map((item) => ({
                ...item,
                data: sortedIndices.map((i) => item.data[i])
            }));

            return {
                xData: sortedXData,
                yData: sortedYData
            };
        }
    }
};
</script>
