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
                    pieData: []
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
            var colorList = ["#5A6FC0", "#9ECA7F", "#F2CA6B ", "#DE6E6A", "#85BEDB", "#59A076", "#EC8A5D"];
            this.chart = echarts.init(this.$el);
            var scaleData = this.valdata.pieData;

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
                        data: scaleData[i].value,
                        value: scaleData[i].value,
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
            this.chart.setOption({
                color: colorList,
                legend: {
                    orient: "vertical",
                    right: "5%",
                    bottom: "center",
                    type:'scroll',
                    // padding: [8, 14],
                    icon: "circle",
                    data: scaleData,
                    textStyle: {
                        color: "#5E707E",
                        rich: {
                            uname: {
                                width: 60,
                                align: "left"
                            },
                            unum: {
                                width: 56,
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
                        var percent;
                        for (var i = 0; i < scaleData.length; i++) {
                            total += scaleData[i].value;
                            if (scaleData[i].name == name) {
                                target = scaleData[i].value;
                            }
                        }
                        if (total == 0) {
                            return `{uname|${name}} {unum|0}`;
                        } else {
                            percent = ((target / total) * 100).toFixed(2) + "%";
                            return `{uname|${name}} {unum|${percent}}`;
                        }
                    }
                },
                tooltip: {
                    show: true,
                    formatter: "{b}{c}个 \t{d}%"
                },
                series: [
                    // 展示层
                    {
                        type: "pie",
                        center: ["35%", "45%"],
                        radius: ["35%", "66%"],
                        itemStyle: {
                            borderWidth: 4, //描边线宽
                            borderColor: "#fff"
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
                    {
                        name: "阴影圈",
                        type: "pie",
                        center: ["35%", "45%"],
                        radius: ["35%", "58%"],
                        hoverAnimation: false,
                        tooltip: {
                            show: false
                        },
                        itemStyle: {
                            normal: {
                                color: "rgba(255, 255, 255, 0.6)"
                            }
                        },
                        zlevel: 4,
                        labelLine: {
                            show: false
                        },
                        data: [100]
                    },
                    // 外边框虚线
                    {
                        type: "pie",
                        zlevel: 4,
                        silent: true,
                        center: ["35%", "45%"],
                        radius: ["70%", "72%"],
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
