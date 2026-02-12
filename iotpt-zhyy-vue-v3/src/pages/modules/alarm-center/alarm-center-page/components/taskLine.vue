<template>
    <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from "echarts";
require("echarts/theme/macarons");

const animationDuration = 6000;

export default {
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
            default: "300px"
        },
        valdata: {
            type: Object,
            default: () => {
                return {
                    legendData: [],
                    label: [],
                    yData: []
                };
            }
        },
    },
    data() {
        return {
            chart: null
        };
    },
    watch: {
        valdata:{
            handler(val) {
                this.initChart();
            },
            deep:true
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
            console.log('2222222222222', this.valdata)
            const that = this
            let option = {
                grid: {
                    left: "10",
                    right: "10",
                    top: "10",
                    bottom: "10",
                    containLabel: true
                },
                tooltip: {
                    show: true,
                    trigger: "axis"
                },
                xAxis: [
                    {
                        type: "category",
                        axisLine: {
                            show: false,
                        },
                        axisTick: {
                            show: false
                        },
                        axisLabel: {
                            show: false
                        },
                        splitLine: {
                            show: false
                        },
                        data: that.valdata.label
                    }
                ],
                yAxis: [
                    {
                        type: "value",
                        axisLabel: {
                            show: false
                        },
                        splitLine: {
                            show:false,
                        }
                    }
                ],
                series: [
                    {
                        name: "",
                        type: "line",
                        lineStyle: {
                            normal: {
                                width: 2,
                                color: "#48CBB9" // 线条颜色
                            }
                        },
                        itemStyle: {
                            color: "#48CBB9"
                        },
                        areaStyle: {
                            normal: {
                                //线性渐变，前4个参数分别是x0,y0,x2,y2(范围0~1);相当于图形包围盒中的百分比。如果最后一个参数是‘true’，则该四个值是绝对像素位置。
                                color: new echarts.graphic.LinearGradient(
                                    0,
                                    0,
                                    0,
                                    1,
                                    [
                                        {
                                            offset: 0,
                                            color: "rgba(84,176,157,0.4)"
                                        },
                                        {
                                            offset: 1,
                                            color: "rgba(84,176,157, 0)"
                                        }
                                    ],
                                    false
                                )
                            }
                        },
                        data: that.valdata.yData
                    }
                ]
            };
            this.chart.setOption(option);
        }
    }
};
</script>
