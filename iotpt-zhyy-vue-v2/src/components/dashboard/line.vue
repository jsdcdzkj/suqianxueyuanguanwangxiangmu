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
            let legendData = ["总告警数", "处理告警数"];
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
                    left: "24px",
                    right: "40px",
                    bottom: "0",
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
                        margin: 10,
                        color: "#A1A7B3",
                        textStyle: {
                            fontSize: 14
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
                                color: "#A1A7B3",
                                fontSize: 14
                            }
                        },
                        axisTick: {
                            show: false
                        },
                        min: 1,
                        minInterval: 1
                    }
                ],
                series: [
                    {
                        name: legendData[0],
                        type: "line",
                        symbol: "circle", //设定为实心点
                        showAllSymbol: true,
                        symbolSize: 0,
                        smooth: true,
                        itemStyle: {
                            normal: {
                                color: `#80A4DB`,
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
                                                color: "rgba(128, 164, 219, 0.2)"
                                            },
                                            {
                                                offset: 1,
                                                color: "rgba(128, 164, 219, 0.1)"
                                            }
                                        ]
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
                        symbolSize: 0,
                        smooth: true,
                        itemStyle: {
                            normal: {
                                color: `#67C23A`,
                                lineStyle: {
                                    //线的颜色
                                    color: `#67C23A`,
                                    width: 1.5
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
