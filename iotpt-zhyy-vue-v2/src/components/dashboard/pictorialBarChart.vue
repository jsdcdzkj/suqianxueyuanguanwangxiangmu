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
                backgroundColor: "rgba(0,0,0,0)",

                grid: {
                    left: "50px",
                    top: "11%",
                    bottom: "20px",
                    right: "5%"
                },
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        type: "shadow"
                    }
                },
                xAxis: {
                    data: this.valdata.xLabel,
                    // name: 'X',
                    nameTextStyle: {
                        color: "#FFFFFF",
                        padding: [0, 0, 0, 0]
                    },
                    show: true,
                    axisLine: {
                        show: true
                    },
                    splitLine: {
                        show: false,
                        lineStyle: {
                            color: "rgba(255,255,255,0.2)"
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        show: true,
                        // rotate: -1,
                        fontSize: 12,
                        textStyle: {
                            fontSize: 12,
                            color: "#333",
                            lineHeight: 14
                        },
                        interval: 0,
                        formatter: function (value) {
                            //x轴的文字改为竖版显示
                            // var str = value.split("");
                            var result = "";
                            for (let i = 0; i < value.length; i++) {
                                result += value[i];
                                if ((i + 1) % 6 == 0) result += "\n";
                                // 一行显示几个
                            }

                            return result;
                        }
                    }
                },
                yAxis: [
                    {
                        name: "",
                        nameTextStyle: {
                            color: "#666",
                            padding: [0, 0, 0, 20]
                        },
                        show: true,
                        axisTick: {
                            show: false
                        },
                        axisLine: {
                            show: true,
                            symbol: ["none", "arrow"],
                            symbolOffset: [0, 10],
                            lineStyle: {
                                // color: 'rgba(255, 129, 109, 0.1)',
                                width: 1, //这里是为了突出显示加上的
                                color: "rgba(91,100,134,1)",
                                shadowColor: "rgba(91,100,134,1)"
                            }
                        },
                        axisLabel: {
                            show: true,
                            // rotate: -1,
                            fontSize: 12,
                            textStyle: {
                                fontSize: 12,
                                // fontFamily: PangMenZhengDao,
                                // fontWeight: 400,
                                color: "#333"
                                // lineHeight: 45,
                            }
                        },
                        splitArea: {
                            areaStyle: {
                                color: "rgba(255,255,255,.5)"
                            }
                        },
                        minInterval: 1,
                        splitLine: {
                            show: true,
                            lineStyle: {
                                type: "dashed",
                                color: "rgba(33, 57, 93,.19)"
                            }
                        }
                    }
                ],
                series: [
                    {
                        type: "pictorialBar",
                        barCategoryGap: "5%",
                        symbol: "path://M0,10 L10,10 C5.5,10 6.5,5 5,0 C3.5,5 4.5,10 0,10 z",
                        label: {
                            show: true,
                            position: "top",
                            distance: 4,
                            color: "#fff",
                            fontSize: 14
                        },
                        itemStyle: {
                            normal: {
                                color: {
                                    type: "linear",
                                    x: 0,
                                    y: 0,
                                    x2: 0,
                                    y2: 1,
                                    colorStops: [
                                        {
                                            offset: 0,
                                            color: "#9A11FF"
                                        },
                                        {
                                            offset: 1,
                                            color: "#08DFFE"
                                        }
                                    ],
                                    global: false //  缺省为  false
                                }
                            },
                            emphasis: {
                                opacity: 1
                            }
                        },
                        data: this.valdata.barData1
                    }
                ]
            });
        }
    }
};
</script>
