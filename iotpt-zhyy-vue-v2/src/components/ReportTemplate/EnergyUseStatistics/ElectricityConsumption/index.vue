<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">
            本周期内，共计使用电力
            <span class="num">{{ totalData.thisSum }}kW·h</span>
            ，累计转标煤
            <span class="num">{{ totalData.TceSum }}tce</span>
            ，累计转二氧化碳
            <span class="num">{{ totalData.COSum }}t</span>
            。
        </div>
        <div>
            <div id="powerStatistic" class="charts-height" />
        </div>
    </div>
</template>

<script>
import Common from "@/components/ReportTemplate/common";
import { energyInfo } from "@/api/report";
export default {
    cname: "用电量统计",
    mixins: [Common],
    data() {
        return {
            totalData: {
                COSum: "9.97",
                TceSum: "1.34",
                thisSum: "5947.6"
            },
            xAxis: [
                "2024-01-01",
                "2024-01-04",
                "2024-01-07",
                "2024-01-11",
                "2024-01-14",
                "2024-01-17",
                "2024-01-21",
                "2024-01-24",
                "2024-01-27",
                "2024-01-31"
            ],
            series: [0, 0, 0, 0, 0, 6000, 0, 8000, 10000, 0]
        };
    },
    mounted() {
        this.$nextTick(() => {
            if (this.areaIds.length > 0) {
                energyInfo({
                    areaIds: this.areaIds,
                    startTime: this.startTime,
                    endTime: this.endTime,
                    timeType: this.timeTypes,
                    splType: this.type
                }).then(({ data }) => {
                    this.xAxis = data.thisTime;
                    this.series = data.thisData;
                    this.totalData = { ...data };
                    this.initDispatchLineChart();
                });
            } else {
                this.initDispatchLineChart();
            }
        });
    },
    methods: {
        initDispatchLineChart() {
            var chartDom = document.getElementById("powerStatistic");
            var myChart = this.$echarts.init(chartDom);
            var option;
            option = {
                title: {},
                color: ["#4A74FF"],
                legend: {
                    right: 0
                },
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        type: "shadow"
                    }
                },
                xAxis: {
                    type: "category",
                    data: this.xAxis
                },
                yAxis: {
                    type: "value",
                    minInterval: 1,
                    name: "kw-h"
                },
                grid: {
                    top: "40px",
                    right: "20px",
                    left: "60px",
                    bottom: "20px"
                },
                series: [
                    {
                        data: this.series.filter((item) => Number(item) > 0),
                        type: "bar",
                        barMaxWidth: 30,
                        label: {
                            normal: {
                                show: true,
                                lineHeight: 10,
                                formatter: "{c}",
                                rotate: -90,
                                textStyle: {
                                    color: "#fff",
                                    fontSize: 14
                                }
                            }
                        }
                    }
                ]
            };
            option && myChart.setOption(option);
        }
    }
};
</script>

<style lang="scss" scoped>
.charts-height {
    height: 290px;
}
</style>
