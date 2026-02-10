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
                    barData: [],
                    barData1: []
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
            this.chart = echarts.init(this.$el);

            this.chart.setOption({
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    formatter: function (params) {
                        var tar = params[1];
                        return tar.name + '<br/>' + tar.seriesName + ' : ' + tar.value;
                    }
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
                        data: this.valdata.xLabel,
                        splitLine: { show: false },
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
                        name: "数量",
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
                        type: 'bar',
                        stack: 'Total',
                        itemStyle: {
                            borderColor: 'transparent',
                            color: 'transparent'
                        },
                        emphasis: {
                            itemStyle: {
                                borderColor: 'transparent',
                                color: 'transparent'
                            }
                        },
                        data: this.valdata.barData
                    },
                    {
                        name: '告警数量',
                        type: 'bar',
                        stack: 'Total',
                        label: {
                            show: true,
                            position: 'inside'
                        },
                        data: this.valdata.barData1
                    }
                ]
            });
        }
    }
};
</script>
