<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">
            本周期内，共发生告警
            <span class="num">{{ typeNums.totalNum }}</span>
            次，其中严重告警
            <span class="num">{{ typeNums.seriousNum }}</span>
            次，一般告警
            <span class="num">{{ typeNums.sameNum }}</span>
            次，轻微告警
            <span class="num">{{ typeNums.slightNum }}</span>
            次。
        </div>
        <div :class="className" ref="chartEl" :style="{ height: height, width: width }" />
    </div>
</template>

<script>
import * as echarts from "echarts";
import Common from "@/components/ReportTemplate/common";
import { getWarningType, warningDataForNum } from "@/api/report";
export default {
    cname: "告警类型统计",
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
            default: "300px"
        },
        chooseTemple: {
            type: Number | String,
            default: 0
        }
    },
    data() {
        return {
            chart: null,
            typeNums: {
                sameNum: 100,
                seriousNum: 20,
                slightNum: 30,
                totalNum: 40
            },
            warnTypeSum: 17,
            warnTypeRatio: [
                { value: 13, name: "电气火灾告警" },
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
        this.$nextTick(async () => {
            if (this.areaIds.length > 0) {
                const pieData = await getWarningType({
                    startTime: this.startTime,
                    endTime: this.endTime,
                    areaIds: this.areaIds,

                    tempId: this.chooseTemple
                });
                const numsData = await warningDataForNum({
                    startTime: this.startTime,
                    endTime: this.endTime,
                    areaIds: this.areaIds
                });
                this.warnTypeRatio = pieData.data;
                this.warnTypeSum = this.warnTypeRatio.reduce((value, item) => {
                    value += Number(item.value);
                    return value;
                }, 0);
                this.typeNums = numsData.data;
                this.initChart();
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
        initChart(list) {
            this.chart = echarts.init(this.$refs.chartEl);
            this.chart.setOption({
                tooltip: {
                    trigger: "item"
                },
                legend: {
                    type: "scroll",
                    orient: "vertical",
                    right: "8%",
                    top: "center",
                    selectedMode: true,
                    icon: "pin",
                    textStyle: {
                        rich: {
                            a: {
                                color: "#666666",
                                fontSize: 14,
                                fontWeight: 500,
                                padding: [25, 0, 15, 2]
                            },
                            b0: {
                                color: "#666666",
                                fontSize: 14,
                                fontWeight: 500,
                                padding: [-10, 0, 0, 4]
                            },
                            b1: {
                                color: "#666666",
                                fontSize: 14,
                                fontWeight: 500,
                                padding: [25, 0, 15, 4]
                            }
                        }
                    },
                    formatter: (name) => {
                        var target;
                        var percent;
                        for (var i = 0, l = this.warnTypeRatio.length; i < l; i++) {
                            if (this.warnTypeRatio[i].name == name) {
                                target = this.warnTypeRatio[i].value;
                                percent = (this.warnTypeRatio[i].value / this.warnTypeSum) * 100;
                            }
                        }
                        return ` ${name}   占比 ${percent.toFixed(0)}%   ${target}个`;
                    }
                },
                series: [
                    {
                        type: "pie",
                        center: ["25%", "50%"],
                        radius: ["30%", "50%"],
                        clockwise: true,
                        avoidLabelOverlap: true,
                        hoverOffset: 2,
                        tooltip: {
                            trigger: "item",
                            formatter: function (params) {
                                return (
                                    params.name + "：" + params.value + "个<br>占比：" + params.percent.toFixed(1) + "%"
                                );
                            }
                        },
                        emphasis: {
                            itemStyle: {
                                borderColor: "#f3f3f3",
                                borderWidth: 5
                            }
                        },
                        label: {
                            show: true,
                            position: "outside"
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
                        data: this.warnTypeRatio
                    }
                ]
            });
        }
    }
};
</script>
