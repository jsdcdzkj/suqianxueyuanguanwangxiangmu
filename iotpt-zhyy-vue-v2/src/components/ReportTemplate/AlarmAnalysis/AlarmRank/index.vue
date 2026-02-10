<template>
    <div>
        <Title
            :index="index"
            :title="title"
            :subTitle="subTitle"
            :subIndex="subIndex"
        />
        <div
            :class="className"
            ref="chartEl"
            :style="{ height: height, width: width }"
        />
    </div>
</template>

<script>
import * as echarts from "echarts";
import Common from "@/components/ReportTemplate/common";
import { getWarningSignStat } from "@/api/report";
export default {
    cname: "信号告警排行TOP10",
    mixins: [Common],
    props: {
        className: {
            type: String,
            default: "chart",
        },
        width: {
            type: String,
            default: "100%",
        },
        height: {
            type: String,
            default: "300px",
        },
        valdata: {
            type: Object,
            default: () => {
                return {};
            },
        },
    },
    data() {
        return {
            chart: null,
            rankName: [
                "电气火灾告警",
                "手动告警",
                "环境告警",
                "其它告警",
                "其它告警",
            ],
            rankNum: [3, 2, 4, 8, 8],
        };
    },
    watch: {
        valdata(val) {
            this.initChart();
        },
    },
    mounted() {
        this.$nextTick(() => {
            if (this.areaIds.length > 0) {
                getWarningSignStat({
                    startTime: this.startTime,
                    endTime: this.endTime,
                    areaIds: this.areaIds,
                }).then((res) => {
                    this.rankName = res.data.map((item) => item.NAME);
                    this.rankNum = res.data.map((item) => item.VALUE);
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

            this.chart.setOption({
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        type: "shadow",
                    },
                    // backgroundColor: 'rgba(9, 24, 48, 0.5)',
                    // borderColor: 'rgba(75, 253, 238, 0.4)',
                    // textStyle: {
                    //     color: '#CFE3FC',
                    // },
                    // borderWidth: 1,
                },
                grid: {
                    top: "20px",
                    right: "20px",
                    left: "60px",
                    bottom: "60px",
                },
                xAxis: [
                    {
                        name: "",
                        type: "category",
                        data: this.rankName,
                        axisLine: {
                            lineStyle: {
                                color: "#666",
                            },
                        },
                        axisLabel: {
                            margin: 10,
                            color: "#333",
                            rotate: 35,
                            textStyle: {
                                fontSize: 12,
                            },
                        },
                        axisTick: {
                            show: false,
                        },
                    },
                ],
                yAxis: [
                    {
                        name: "",
                        axisLabel: {
                            formatter: "{value}",
                            color: "#333",
                        },
                        axisTick: {
                            show: false,
                        },

                        splitLine: {
                            lineStyle: {
                                color: "#efefef",
                            },
                        },
                    },
                ],
                series: [
                    {
                        type: "bar",
                        data: this.rankNum,
                        barWidth: "20%",
                        label: {
                            normal: {
                                show: true,
                                lineHeight: 10,
                                formatter: "{c}",
                                left: "center",
                                textStyle: {
                                    color: "#fff",
                                    fontSize: 14,
                                },
                            },
                        },
                    },
                ],
            });
        },
    },
};
</script>
