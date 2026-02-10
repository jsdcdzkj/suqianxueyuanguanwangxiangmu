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
                    xlabel: [],
                    firstList: [],
                    secondList: []
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
            let legendData = ["排除安全隐患", "处理安全事件"];
            let xAxisData = this.valdata.xlabel;
            let seriesData = this.valdata.firstList;
            let seriesData1 = this.valdata.secondList;
            this.chart = echarts.init(this.$el);

            this.chart.setOption({
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        type: "shadow"
                    }
                },
                grid: {
                    top: "40px",
                    left: "20px",
                    right: "30px",
                    bottom: "20px",
                    containLabel: true
                },
                legend: {
                    top: "2%",
                    itemGap: 15, // 设置间距
                    textStyle: {
                        //图例文字的样式
                        color: "#5E707E",
                        fontSize: 12
                    }
                },
                xAxis: {
                    boundaryGap: false,
                    data: xAxisData,
                    axisLine: {
                        lineStyle: {
                            color: "#96A2AB"
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        color: "#96A2AB",
                        fontSize: 12,
                        textStyle: {
                            fontWeight: "bold" // 加粗文本
                        }
                    }
                },
                yAxis: [
                    {
                        name: "",
                        type: "value",
                        inverse: false,
                        splitLine: {
                            show: true,
                            lineStyle: {
                                color: "#E8E8E8",
                                type: "dashed"
                            }
                        },
                        axisLine: {
                            show: false,
                            lineStyle: {
                                color: "#0A5C95",
                                fontSize: 12,
                                fontWeight: "bold"
                            }
                        },
                        axisTick: {
                            show: false
                        },
                        min: 0,
                        minInterval: 1
                    }
                ],
                series: [
                    {
                        name: legendData[0],
                        type: "line",
                        symbol: "circle", //设定为实心点
                        showAllSymbol: true,
                        symbolSize: 6,
                        smooth: true,
                        tooltip: {
                            show: true
                        },
                        itemStyle: {
                            normal: {
                                color: `#80A4DB`,
                                borderColor: "#6c98d9",
                                borderWidth: 1,
                                shadowColor: "#80A4DB",
                                shadowBlur: 14,
                                lineStyle: {
                                    //线的颜色
                                    color: `#80A4DB`,
                                    width: 1.5
                                },
                                areaStyle: {
                                    //type: 'default',
                                    //旧版渐变色实现
                                    // color: new echarts.graphic.LinearGradient(0, 0, 0, 1, //变化度
                                    //    //三种由深及浅的颜色
                                    //    [{
                                    //       offset: 0,
                                    //       color: 'rgba(1, 180, 255, 0.2)'
                                    //    }, {
                                    //       offset: 1,
                                    //       color: 'rgba(1, 180, 255, 0.1)'
                                    //    }])

                                    //新版渐变色实现
                                    color: {
                                        type: "linear",
                                        x: 0,
                                        y: 0,
                                        x2: 0,
                                        y2: 1,
                                        colorStops: [
                                            {
                                                offset: 0,
                                                color: "rgba(128, 164, 219, 0.4)"
                                            },
                                            {
                                                offset: 1,
                                                color: "rgba(128, 164, 219, 0.1)"
                                            }
                                        ],
                                        shadowColor: "rgba(25,163,223, 0.5)", //阴影颜色
                                        shadowBlur: 20 //shadowBlur设图形阴影的模糊大小。配合shadowColor,shadowOffsetX/Y, 设置图形的阴影效果。
                                    }
                                }
                            }
                        },
                        data: seriesData
                    },
                    {
                        name: legendData[1],
                        type: "line",
                        symbol: "circle", //设定为实心点
                        showAllSymbol: true,
                        symbolSize: 6,
                        smooth: true,
                        tooltip: {
                            show: true
                        },
                        itemStyle: {
                            normal: {
                                color: `#67C23A`,
                                borderColor: "#67c13b",
                                borderWidth: 1,
                                shadowColor: "#67C23A",
                                shadowBlur: 14,
                                lineStyle: {
                                    //线的颜色
                                    color: `#67C23A`,
                                    width: 1.5
                                },
                                areaStyle: {
                                    //新版渐变色实现
                                    color: {
                                        type: "linear",
                                        x: 0,
                                        y: 0,
                                        x2: 0,
                                        y2: 1,
                                        colorStops: [
                                            {
                                                offset: 0,
                                                color: "rgba(103, 194, 58, 0.4)"
                                            },
                                            {
                                                offset: 1,
                                                color: "rgba(103, 194, 58, 0)"
                                            }
                                        ],
                                        shadowColor: "rgba(25,163,223, 0.5)", //阴影颜色
                                        shadowBlur: 20 //shadowBlur设图形阴影的模糊大小。配合shadowColor,shadowOffsetX/Y, 设置图形的阴影效果。
                                    }
                                }
                            }
                        },

                        data: seriesData1
                    }
                ]
            });
        }
    }
};
</script>
