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
                    barData1: []
                };
            }
        },
        colors: {
            type: Array,
            default: () => {
                return [
                    "#5878EB",
                    "#E7B737",
                    "#589C4E",
                    "#6CB2BC",
                    "#DE6E6A",
                    "#59A076",
                    "#EC8A5D",
                    "#9263AF",
                    "#DC82C8"
                ];
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
            this.chart = echarts.init(this.$el);

            this.chart.setOption({
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        type: "shadow"
                    },
                    formatter: "{b}时: \t{c}次"
                },
                grid: {
                    top: "40px",
                    left: "20px",
                    right: "30px",
                    bottom: "0",
                    containLabel: true
                },
                xAxis: [
                    {
                        type: "category",
                        data: this.x,
                        name: "(时)",
                        nameTextStyle: {
                            color: "#C1C6CF",
                            fontSize: 12,
                            align: "left",
                            verticalAlign: "top",
                            padding: [-4, 0, 0, -10]
                        },
                        axisLine: {
                            lineStyle: {
                                color: "#D1D9EB"
                            }
                        },
                        axisLabel: {
                            margin: 10,
                            color: "#A1A7B3",
                            textStyle: {
                                fontSize: 14
                            }
                        },
                        axisTick: {
                            show: false
                        }
                    }
                ],
                yAxis: [
                    {
                        type: "value",
                        name: "(次数)",
                        nameTextStyle: {
                            color: "#C1C6CF",
                            fontSize: 12,
                            align: "left",
                            padding: [0, 0, 0, -25]
                        },
                        axisLabel: {
                            formatter: "{value}",
                            color: "#A1A7B3",
                            fontSize: 14
                        },
                        axisTick: {
                            show: false
                        },
                        axisLine: {
                            show: false
                        },
                        splitLine: {
                            lineStyle: {
                                color: "#D1D9EB",
                                type: "dashed"
                            }
                        }
                    }
                ],
                series: [
                    {
                        type: "bar",
                        data: this.valdata.barData1,
                        barWidth: "12px",
                        itemStyle: {
                            normal: {
                                color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    {
                                        offset: 0,
                                        color: "#5DBBF2"
                                    },
                                    {
                                        offset: 1,
                                        color: "#4293FD"
                                    }
                                ])
                            },
                            barBorderRadius: [2, 2, 0, 0]
                        }
                    }
                ]
            });
        }
    }
};
</script>
