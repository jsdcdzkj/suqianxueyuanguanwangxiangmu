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
                    warnTypeRatio: []
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
            let title = "告警总数";
            let formatNumber = function (num) {
                let reg = /(?=(\B)(\d{3})+$)/g;
                return num.toString().replace(reg, ",");
            };
            let total = this.valdata.warnTypeRatio.reduce((a, b) => {
                return a + b.value * 1;
            }, 0);

            this.chart.setOption({
                title: [
                    {
                        text: "" + formatNumber(total) + "",
                        subtext: "告警总数",
                        top: "42%",
                        left: "29%",
                        textAlign: "center",
                        itemGap: 4,
                        subtextStyle: {
                            color: "#666",
                            fontSize: 12,
                            align: "center"
                        },
                        textStyle: {
                            color: "#333",
                            fontSize: 24
                        }
                    }
                ],
                tooltip: {
                    trigger: "item"
                },
                legend: {
                    type: "scroll",
                    orient: "vertical",
                    right: "0",
                    top: "center",
                    itemWidth: 12,
                    itemHeight: 12,
                    selectedMode: true,
                    icon: "circle",
                    textStyle: {
                        rich: {
                            a: {
                                color: "#666666",
                                fontSize: 12,
                                fontWeight: 500,
                                padding: [2, 0, 0, 0]
                            },
                            b0: {
                                color: "#666666",
                                fontSize: 12,
                                fontWeight: 500,
                                padding: [2, 0, 0, 0]
                            }
                        }
                    },
                    formatter: (name) => {
                        var target;
                        for (var i = 0, l = this.valdata.warnTypeRatio.length; i < l; i++) {
                            if (this.valdata.warnTypeRatio[i].name == name) {
                                target = this.valdata.warnTypeRatio[i].value;
                            }
                        }
                        return `{a|${name}} {b0|${target}}`;
                    }
                },
                series: [
                    {
                        type: "pie",
                        center: ["30%", "55%"],
                        radius: ["50%", "78%"],
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
                        // 在这里添加 graphic 组件来模拟标题
                        graphic: {
                            type: "text",
                            left: "center", // 水平居中
                            top: "middle", // 垂直居中（但需要根据圆环的大小进行微调）
                            style: {
                                text: "告警总数\n" + formatNumber(total),
                                textAlign: "center",
                                textVerticalAlign: "middle",
                                fontSize: 24, // 字体大小
                                fontWeight: "bold", // 字体粗细
                                fill: "#000" // 字体颜色
                                // 可能还需要添加其他样式属性，如 lineHeight 来调整行高
                            },
                            // 可能需要添加 z 或 zlevel 属性来控制图层顺序
                            z: 100 // 确保标题位于圆环上方（但通常 graphic 默认就在前面）
                        },
                        data: this.valdata.warnTypeRatio
                    }
                ]
            });
        }
    }
};
</script>
