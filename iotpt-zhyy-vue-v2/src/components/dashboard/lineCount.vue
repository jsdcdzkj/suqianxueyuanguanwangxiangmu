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
            this.chart = echarts.init(this.$el);
            this.chart.setOption({
                grid: {
                    left: 0,
                    right: 0,
                    top: 0,
                    bottom: 0,
                    containLabel: false
                },
                tooltip: {
                    show: false
                },
                xAxis: {
                    show: false,
                    type: "category",
                    boundaryGap: false,
                    data: this.valdata.xLabel
                },
                yAxis: {
                    show: false,
                    type: "value",
                    boundaryGap: [0, "0%"]
                },
                series: [
                    {
                        type: "line",
                        smooth: true,
                        data: this.valdata.yLabel,
                        showSymbol: false,

                        lineStyle: {
                            color: "#3B9FE1"
                        },
                        areaStyle: {
                            color: "rgba(112, 202, 238, 0.16)"
                        }
                    }
                ]
            });
        }
    }
};
</script>
