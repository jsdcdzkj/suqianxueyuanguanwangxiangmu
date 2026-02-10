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
                    xLabel: [],
                    yLabel: []
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
            console.log(this.valdata);
            this.chart = echarts.init(this.$el);
            this.chart.setOption({
                color: ["#1E95F4"],
                grid: {
                    left: "10px",
                    right: "10px",
                    top: "20px",
                    bottom: "0",
                    containLabel: true
                },
                tooltip: {
                    show: true,
                    trigger: "axis",
                    axisPointer: {
                        type: "line"
                    }
                },
                xAxis: {
                    show: true,
                    axisLine: {
                        lineStyle: {
                            color: "#efefef"
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        color: "#666",
                        textStyle: {
                            fontSize: 12
                        }
                    },
                    type: "category",
                    // boundaryGap: false,
                    data: this.valdata.xLabel
                },
                yAxis: {
                    show: true,
                    type: "value",
                    boundaryGap: [0, "0%"],
                    axisLine: {
                        show: false
                    },
                    axisLabel: {
                        color: "#999",
                        textStyle: {
                            fontSize: 12
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        lineStyle: {
                            color: "#D1D9EB",
                            type: "dashed"
                        }
                    },
                    splitNumber: 3
                },
                series: [
                    {
                        type: "line",
                        smooth: true,
                        data: this.valdata.yLabel,
                        showSymbol: true,
                        symbolSize: 14,
                        lineStyle: {
                            color: "#1E95F4"
                        },
                        areaStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                {
                                    offset: 0,
                                    color: "rgba(30,149,244,0.32)"
                                },
                                {
                                    offset: 1,
                                    color: "rgba(30,149,244,0)"
                                }
                            ])
                        }
                    }
                ]
            });
        }
    }
};
</script>
