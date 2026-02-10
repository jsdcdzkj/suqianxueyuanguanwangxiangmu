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
                    pieData: [
                        {
                            name: "办公",
                            value: 20
                        },
                        {
                            name: "照类",
                            value: 10
                        },
                        {
                            name: "空调",
                            value: 10
                        },
                        {
                            name: "其他",
                            value: 10
                        }
                    ]
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
            var colorList = ["#DE6E6A", "#5A6FC0", "#9ECA7F", "#F2CA6B"];
            this.chart = echarts.init(this.$el);
            var scaleData = this.valdata.pieData;
            var scaleTop = [
                {
                    top: "14%"
                },
                {
                    top: "34%"
                },
                {
                    top: "54%"
                },
                {
                    top: "74%"
                }
            ].reverse();

            var placeHolderStyle = {
                normal: {
                    label: {
                        show: false
                    },
                    labelLine: {
                        show: false
                    },
                    color: "rgba(0, 0, 0, 0)"
                }
            };

            var data = [];
            for (var i = 0; i < scaleData.length; i++) {
                data.push(
                    {
                        data: scaleData[i].count,
                        value: scaleData[i].count,
                        name: scaleData[i].name,
                        itemStyle: {
                            normal: {
                                color: colorList[i]
                            }
                        }
                    },
                    {
                        value: 2,
                        name: "",
                        itemStyle: placeHolderStyle
                    }
                );
            }
            const legendData = [];
            scaleData.forEach((item, i) => {
                legendData.push({
                    orient: "vertical",
                    top: scaleTop[i].top,
                    right: "8%",
                    padding: [8, 14],
                    icon: "circle",
                    backgroundColor: new echarts.graphic.LinearGradient(
                        0,
                        0,
                        0,
                        1, // 起始和结束位置，这里是从左上角到右下角
                        [
                            { offset: 0, color: "#EBF0F2" }, // 起始颜色为红色，偏移量为0
                            { offset: 1, color: "#F5F7F9" } // 结束颜色为蓝色，偏移量为1
                        ]
                    ),
                    borderRadius: 15,
                    borderWidth: 1, // 设置边框宽度
                    borderColor: "#eee", // 设置边框颜色
                    data: [{ name: item.name }],
                    textStyle: {
                        color: "#5E707E",
                        rich: {
                            uname: {
                                width: 40,
                                align: "center"
                            },
                            unum: {
                                width: 50,
                                fontSize: 12,
                                color: "#333",
                                fontWeight: "bold",
                                align: "right"
                            }
                        }
                    },

                    formatter: (name) => {
                        var total = 0;
                        var target;
                        for (var i = 0; i < scaleData.length; i++) {
                            total += scaleData[i].value;
                            if (scaleData[i].name == name) {
                                target = scaleData[i].value;
                            }
                        }

                        var percent = ((target / total) * 100).toFixed(2) + "%";
                        return `{uname|${name}} {unum|${percent}}`;
                    }
                });
            });

            this.chart.setOption({
                color: colorList,
                legend: legendData,
                tooltip: {
                    show: true,
                    formatter: "{b}{c}个 \t{d}%"
                },
                series: [
                    // 外边设置
                    {
                        type: "pie",
                        center: ["30%", "50%"],
                        radius: ["55%", "62%"],
                        itemStyle: {
                            color: "#E5ECF0"
                        },
                        label: {
                            show: false
                        },
                        data: [0]
                    },
                    // 展示层
                    {
                        type: "pie",
                        center: ["30%", "50%"],
                        radius: ["25%", "60%"],
                        itemStyle: {
                            borderWidth: 4, //描边线宽
                            borderColor: "#E5ECF0"
                        },
                        label: {
                            show: false
                        },
                        labelLine: {
                            show: false,
                            normal: {
                                length: 40,
                                length2: 85,
                                align: "right",
                                lineStyle: {
                                    width: 1
                                }
                            }
                        },
                        data: scaleData
                    },
                    // 外边框虚线
                    {
                        type: "pie",
                        zlevel: 4,
                        silent: true,
                        center: ["30%", "50%"],
                        radius: ["67%", "72%"],
                        label: {
                            show: false
                        },
                        labelLine: {
                            show: false
                        },
                        data: this.createData()
                    }
                ]
            });
        },
        createData() {
            let dataArr = [];
            for (var i = 0; i < 200; i++) {
                if (i % 2 === 0) {
                    dataArr.push({
                        name: (i + 1).toString(),
                        value: 25,
                        itemStyle: {
                            normal: {
                                color: "#E5ECF0",
                                borderWidth: 0,
                                borderColor: "rgba(0,0,0,0)"
                            }
                        }
                    });
                } else {
                    dataArr.push({
                        name: (i + 1).toString(),
                        value: 20,
                        itemStyle: {
                            normal: {
                                color: "rgba(0,0,0,0)",
                                borderWidth: 0,
                                borderColor: "rgba(0,0,0,0)"
                            }
                        }
                    });
                }
            }
            return dataArr;
        }
    }
};
</script>
