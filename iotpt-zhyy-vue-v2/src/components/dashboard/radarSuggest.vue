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
                    radarData: []
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
                    show: true,
                    trigger: "item"
                },
                radar: {
                    // shape: 'circle',
                    center: ["50%", "50%"],
                    // 设置雷达图半径，可调整为百分比或像素值，这里缩小半径留出边距
                    radius: "70%",
                    splitNumber: 6,
                    // nameGap: "10",
                    name: {
                        textStyle: {
                            color: "#646464"
                        }
                    },
                    axisLine: {
                        show: true,
                        lineStyle: {
                            color: "#DBDDDF",
                            width: 1
                        }
                    },
                    splitLine: {
                        lineStyle: {
                            color: "#DBDDDF",
                            // opacity: 0.5,
                            width: 1
                        }
                    },
                    // splitArea: {
                    //     areaStyle: {
                    //         color: [
                    //             // "rgb(125,200,242)",
                    //             // "rgb(137,205,244)",
                    //             // "rgb(143,210,246)",
                    //             // "rgb(155,216,248)",
                    //             "rgba(167,220,250)",
                    //             "rgb(186,227,252)",
                    //             "rgb(200,230,253)",
                    //             "rgb(216,235,254)",
                    //             "rgb(233,245,255)",
                    //             "rgba(233,245,255,.6)"
                    //         ]
                    //     }
                    // },
                    splitArea: {
                        areaStyle: {
                            color: new echarts.graphic.RadialGradient(0.5, 0.5, 0.5, [
                                { offset: 0, color: "rgba(255, 255, 255, 0)" },
                                { offset: 0.8, color: "rgba(31, 158, 248, 0.1)" },
                                { offset: 1, color: "rgba(31, 158, 248, 0.3)" }
                            ])
                        }
                    },
                    name: {
                        textStyle: {
                            color: "#666",
                            backgroundColor: "#F3F3F3",
                            borderRadius: 4,
                            padding: [5, 5]
                        }
                    },
                    indicator: [
                        {
                            name: "功能完整性",
                            max: 5
                        },
                        {
                            name: "设备在线率",
                            max: 5
                        },
                        {
                            name: "处置及时率",
                            max: 5
                        },
                        {
                            name: "报警处置率",
                            max: 5
                        },
                        {
                            name: "有效报警率",
                            max: 5
                        },
                        {
                            name: "点位覆盖率",
                            max: 5
                        }
                    ]
                },
                series: [
                    {
                        name: "安全评估",
                        type: "radar",
                        symbolSize: 0,
                        areaStyle: {
                            normal: {
                                width: 1,
                                opacity: 0.65,
                                // color linear-gradient( 180deg, #50A0FF 0%, #547FF7 100%);
                                color: new echarts.graphic.LinearGradient(
                                    0,
                                    0,
                                    0,
                                    1,
                                    [
                                        {
                                            offset: 0,
                                            color: "#50A0FF"
                                        },
                                        {
                                            offset: 1,
                                            color: "#547FF7"
                                        }
                                    ],
                                    false
                                )
                            }
                        },
                        lineStyle: {
                            normal: {
                                width: 0
                            }
                        },
                        data: [
                            {
                                itemStyle: {
                                    normal: {
                                        color: "#67abff",
                                        lineStyle: {
                                            color: "#67abff"
                                        }
                                    }
                                },
                                value: this.valdata.radarData
                            }
                        ]
                    }
                ]
            });
        }
    }
};
</script>
