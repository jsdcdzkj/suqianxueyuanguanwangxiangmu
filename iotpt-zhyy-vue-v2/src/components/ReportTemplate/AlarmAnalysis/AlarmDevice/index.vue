<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div :class="className" ref="chartEl" :style="{ height: height, width: width }" />
    </div>
</template>

<script>
import * as echarts from "echarts";
import Common from "@/components/ReportTemplate/common";
import { getWarningDeviceTypePie } from "@/api/report";
export default {
    cname: "设备告警占比",
    mixins: [Common],
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
            default: "160px"
        },
        chooseTemple: {
            type: Number | String,
            default: 0
        }
    },
    data() {
        return {
            chart: null,
            warnDevice: [
                { value: 3, name: "电气告警" },
                { value: 2, name: "手动告警" },
                { value: 4, name: "环境告警" },
                { value: 8, name: "其它告警" }
            ]
        };
    },
    watch: {
        valdata(val) {
            this.initChart();
        }
    },
    mounted() {
        this.$nextTick(() => {
            if (this.areaIds.length > 0) {
                getWarningDeviceTypePie({
                    startTime: this.startTime,
                    endTime: this.endTime,
                    areaIds: this.areaIds,
                    tempId: this.chooseTemple
                }).then((res) => {
                    this.warnDevice = res.data;
                    this.initChart();
                });
            } else {
                this.initChart();
            }
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
            this.chart = echarts.init(this.$refs.chartEl);
            const dataSource = this.warnDevice.map((item) => {
                item.value = Number(item.value);
                return item;
            });
            let dataSourcemax = 0;
            dataSource.forEach((item) => {
                dataSourcemax += item.value;
            });
            const color = [
                "#5470c6",
                "#91cc75",
                "#fac858",
                "#ee6666",
                "#73c0de",
                "#3ba272",
                "#fc8452",
                "#9a60b4",
                "#ea7ccc"
            ];
            let salvProMax = [
                dataSourcemax,
                dataSourcemax,
                dataSourcemax,
                dataSourcemax,
                dataSourcemax,
                dataSourcemax,
                dataSourcemax,
                dataSourcemax
            ];

            this.chart.setOption({
                grid: [
                    {
                        left: 40,
                        top: 0,
                        right: 50,
                        bottom: 0,
                        containLabel: true
                    }
                ],
                xAxis: [
                    {
                        gridIndex: 0,
                        type: "value",
                        axisLabel: { show: false },
                        axisLine: { show: false },
                        splitLine: { show: false },
                        axisTick: { show: false }
                    },
                    {
                        gridIndex: 0,
                        type: "value",
                        max: 100,
                        axisLabel: { show: false },
                        axisLine: { show: false },
                        splitLine: { show: false },
                        axisTick: { show: false }
                    }
                ],
                yAxis: [
                    {
                        gridIndex: 0,
                        type: "category",
                        inverse: true,
                        data: dataSource.map((item) => item.name),
                        axisTick: { show: false },
                        axisLine: { show: false },
                        splitLine: { show: false },
                        position: "left",
                        axisLabel: {
                            width: 140,
                            padding: [0, 0, 0, -80],
                            align: "left",
                            formatter: (name, index) => {
                                return `{value|${name}}`;
                            },
                            rich: {
                                value: {
                                    color: "#333",
                                    fontSize: 14,
                                    fontWeight: 500
                                }
                            }
                        }
                    },
                    {
                        gridIndex: 0,
                        type: "category",
                        position: "right",
                        inverse: true,
                        margin: 20,
                        data: dataSource.map((item) => item.name),
                        axisTick: { show: false },
                        axisLine: { show: false },
                        splitLine: { show: false },
                        axisLabel: {
                            align: "right",
                            padding: [0, -40, 0, 0],
                            formatter: (_, index) => {
                                return `{value|${((dataSource[index].value / dataSourcemax) * 100).toFixed(2)}%}`;
                            },
                            rich: {
                                value: {
                                    color: "#333",
                                    fontSize: 14,
                                    fontWeight: 500
                                }
                            }
                        }
                    }
                ],
                series: [
                    {
                        z: 1,
                        type: "bar",
                        xAxisIndex: 0,
                        yAxisIndex: 0,
                        barWidth: 14,
                        data: dataSource.map((item) => item.value),
                        silent: true,
                        itemStyle: {
                            emphasis: {
                                barBorderRadius: [0, 20, 20, 0]
                            },
                            normal: {
                                // barBorderRadius: [0, 20, 20, 0],
                                barBorderRadius: [30, 30, 30, 30],
                                color: function (params) {
                                    return color[params.dataIndex];
                                },
                                // 柱状图上显示数字
                                label: {
                                    show: true, // 开启显示
                                    // position: 'insideRight', // 在上方显示
                                    offset: [0, 0], //  5以上用水平垂直
                                    textStyle: {
                                        // 数值样式
                                        fontSize: 12,
                                        color: "#fff",
                                        fontWeight: 500,
                                        padding: [0, 0, 40, 0] // 5以上版本无效
                                    }
                                }
                            }
                        }
                    },
                    {
                        z: 3,
                        name: "背景",
                        type: "bar",
                        barWidth: 14,
                        barGap: "-100%",
                        data: salvProMax,
                        itemStyle: {
                            normal: {
                                color: function (params) {
                                    return color[params.dataIndex];
                                },
                                opacity: 0.3,
                                barBorderRadius: [30, 30, 30, 30]
                            }
                        }
                    }
                ]
            });
        }
    }
};
</script>
