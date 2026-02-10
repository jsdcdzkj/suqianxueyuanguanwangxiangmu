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
                    warnTypeRatio: [],
                    warnTypeSum: 0
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
            let value = this.valdata.liquildData;
            let title = "收入";
            this.chart.setOption({
                title: {
                    text: "{val|" + "¥" + this.valdata.warnTypeSum + "}\n{name|" + title + "}",
                    x: "29.6%",
                    y: "43%",
                    textAlign: "center",
                    textStyle: {
                        rich: {
                            name: {
                                fontSize: 14,
                                fontWeight: "normal",
                                color: "#5E707E",
                                padding: [10, 0]
                            },
                            val: {
                                fontSize: 20,
                                color: "#333"
                            }
                        }
                    }
                },

                tooltip: {
                    trigger: "item"
                },
                legend: {
                    type: "scroll",
                    orient: "vertical",
                    right: "2%",
                    top: "center",
                    itemWidth: 14,
                    itemHeight: 14,
                    selectedMode: true,
                    icon: "circle",
                    textStyle: {
                        rich: {
                            a: {
                                color: "#666666",
                                fontSize: 12,
                                fontWeight: 500,
                                padding: [29, 0, 15, 2]
                            },
                            b0: {
                                color: "#666666",
                                fontSize: 12,
                                fontWeight: 500,
                                padding: [-10, 0, 0, 4]
                            },
                            b1: {
                                color: "#666666",
                                fontSize: 12,
                                fontWeight: 500,
                                padding: [29, 0, 15, 4]
                            }
                        }
                    },
                    formatter: (name) => {
                        let target = 0;
                        let percent = 0;
                        for (let i = 0, l = this.valdata.warnTypeRatio.length; i < l; i++) {
                            if (this.valdata.warnTypeRatio[i].name === name) {
                                target = this.valdata.warnTypeRatio[i].value;
                                percent = this.valdata.warnTypeSum > 0 ? (target / this.valdata.warnTypeSum) * 100 : 0;
                                break;
                            }
                        }
                        return `{a| ${name}}    {b1|占比 ${percent.toFixed(2)}%\n} {b0|${target}元}`;
                    }
                },
                series: [
                    {
                        type: "pie",
                        center: ["30%", "50%"],
                        radius: ["40%", "70%"],
                        clockwise: true,
                        avoidLabelOverlap: true,
                        hoverOffset: 2,
                        tooltip: {
                            trigger: "item",
                            formatter: function (params) {
                                return (
                                    params.name + "：" + params.value + "个<br>占比：" + params.percent.toFixed(2) + "%"
                                );
                            }
                        },
                        emphasis: {
                            itemStyle: {
                                borderColor: "#f3f3f3",
                                borderWidth: 0
                            }
                        },
                        itemStyle: {
                            borderRadius: 8,
                            borderColor: "#fff",
                            borderWidth: 6
                        },
                        label: {
                            show: false,
                            position: "outside",
                            formatter: "{a|{b}：{d}%}\n{hr|}",
                            rich: {
                                hr: {
                                    backgroundColor: "t",
                                    borderRadius: 1,
                                    width: 1,
                                    height: 1,
                                    padding: [1, 1, 0, -4]
                                },
                                a: {
                                    padding: [-15, 7, -10, 7]
                                }
                            }
                        },
                        labelLine: {
                            normal: {
                                length: 10,
                                length2: 15,
                                lineStyle: {
                                    width: 1
                                }
                            }
                        },
                        data: this.valdata.warnTypeRatio
                    }
                ]
            });
        }
    }
};
</script>
