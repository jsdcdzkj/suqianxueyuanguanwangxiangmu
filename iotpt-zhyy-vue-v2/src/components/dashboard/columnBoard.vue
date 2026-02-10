<template>
    <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from "echarts";
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
                    barList: []
                };
            }
        },
        colors: {
            type: Array,
            default: () => {
                return ["#ebbf46", "#0067df", "#2fcf97", "#73c0de", "#fc8452"];
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
                color: ["#5A6FC0", "#F2CA6B"],
                tooltip: {
                    trigger: "axis",
                    textStyle: {
                        color: "#666"
                    },
                    borderWidth: 0
                },
                legend: {
                    icon: "circle",
                    textStyle: {
                        color: "#333",
                        fontSize: 12
                    },
                    top: "10px",
                    itemWidth: 10,
                    itemHeight: 10,
                    borderRadius: 2,
                    backgroundColor: "rgba(255,255,255, .05)",
                    itemGap: 30,
                    padding: [4, 8]
                },
                grid: {
                    x: 0,
                    y: 0,
                    x2: 0,
                    y2: 0,
                    top: "60px",
                    left: "30px",
                    right: "30px",
                    bottom: "30px",
                    containLabel: true
                },
                xAxis: {
                    data: this.valdata.xLabel,
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        //坐标轴刻度标签的相关设置
                        textStyle: {
                            color: "#333",
                            fontSize: 12
                        },
                        formatter: function (data) {
                            return data;
                        }
                    }
                },
                yAxis: {
                    name: "kWh",
                    nameTextStyle: {
                        color: "#999",
                        padding: [0, 10, 0, 10]
                    },
                    splitLine: {
                        show: true,
                        lineStyle: {
                            color: "rgba(0, 0, 0, 0.06)"
                        }
                    },
                    minInterval: 1,
                    axisLine: {
                        symbol: ["none", "arrow"],
                        symbolSize: [5, 12],
                        symbolOffset: [0, 10],
                        lineStyle: {
                            color: "rgba(0, 0, 0, 0.06)"
                        }
                    },
                    axisLabel: {
                        formatter: "{value}",
                        textStyle: {
                            color: "#333"
                        }
                    },
                    axisTick: {
                        show: false
                    }
                },

                series: [
                    {
                        name: this.valdata.barList[0].name,
                        type: "bar",
                        data: this.valdata.barList[0].data
                    },
                    {
                        name: this.valdata.barList[1].name,
                        type: "bar",
                        data: this.valdata.barList[1].data
                    }
                ]
            });
        }
    }
};
</script>
