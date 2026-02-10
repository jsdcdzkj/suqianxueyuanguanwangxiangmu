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
                    legendData: [],
                    xLabel: [],
                    barData1: [],
                    barData2: [],
                    barData3: [],
                    lineData1: []
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
        createData() {
            let arr = [];
            for (let index = 0; index < this.valdata.barList.length; index++) {
                const element = this.valdata.barList[index];
                element.type = "bar";

                element.itemStyle = {
                    color: this.colors[index],
                    barWidth: this.valdata.barList.length - 10 + "%"
                };

                arr.push(element);
            }
            return arr;
        },
        initChart() {
            this.chart = echarts.init(this.$el);
            this.chart.setOption({
                tooltip: {
                    trigger: "axis",
                    textStyle: {
                        color: "#666"
                    },
                    borderWidth: 0,
                    formatter: (params) => {
                        let str = "";
                        params.forEach((item, idx) => {
                            str += `${item.marker}${item.seriesName}: ${item.data}`;
                            switch (idx) {
                                case 0:
                                    str += "%";
                                    break;
                                default:
                            }
                            str += idx === params.length - 1 ? "" : "<br/>";
                        });
                        return str;
                    }
                },
                legend: {
                    data: this.valdata.legendData,
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
                    left: "10px",
                    right: "10px",
                    bottom: "0px",
                    containLabel: true
                },
                xAxis: {
                    data: this.valdata.xLabel,
                    axisLine: {
                        lineStyle: {
                            color: "rgba(33, 57, 93,.9)"
                        }
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
                yAxis: [
                    {
                        name: "告警数量",
                        nameTextStyle: {
                            color: "#999",
                            padding: [0, 10, 0, 10]
                        },
                        splitLine: {
                            show: true,
                            lineStyle: {
                                type: "dashed",
                                color: "rgba(33, 57, 93,.19)"
                            }
                        },
                        minInterval: 1,
                        axisLine: {
                            symbol: ["none", "arrow"],
                            symbolSize: [5, 12],
                            symbolOffset: [0, 10],
                            lineStyle: {
                                color: "#22BF65"
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
                    {
                        name: "完成率",
                        nameTextStyle: {
                            color: "#999"
                        },
                        splitLine: { show: false },
                        axisLine: {
                            symbol: ["none", "arrow"],
                            symbolSize: [5, 12],
                            symbolOffset: [0, 10],
                            lineStyle: {
                                color: "#22BF65"
                            }
                        },
                        axisLabel: {
                            formatter: "{value}%",
                            textStyle: {
                                color: "#333"
                            }
                        },
                        axisTick: {
                            show: false
                        }
                    }
                ],
                series: [
                    {
                        name: this.valdata.legendData[0],
                        type: "line",
                        smooth: true,
                        showSymbol: false,
                        // showAllSymbol: true,
                        // symbol: 'emptyCircle',
                        // symbolSize: 6,
                        yAxisIndex: 1,
                        itemStyle: {
                            normal: {
                                color: "#ffe312"
                            }
                        },

                        barWidth: 16,
                        itemStyle: {
                            normal: {
                                barBorderRadius: [50, 50, 0, 0],
                                color: "#ffe312"
                            }
                        },
                        data: this.valdata.lineData1
                    },
                    ...this.createData()
                ]
            });
        }
    }
};
</script>
