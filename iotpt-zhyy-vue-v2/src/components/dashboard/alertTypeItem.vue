<template>
    <div class="alertTypeItem">
        <div class="img-box">
            <img :src="imgIcon" alt="" />
        </div>

        <div style="flex-shrink: 0">
            <div class="type_name">{{ this.dataInfo.name }}</div>
            <div>
                <span class="success">{{ this.dataInfo.processed }}</span>
                /
                <span class="error">{{ this.dataInfo.all }}</span>
            </div>
        </div>
        <div class="chart-right">
            <!-- <el-empty
                :image="require('@/assets/img/empty.png')"
                description="数据为空"
                :image-size="40"
            ></el-empty> -->
            <div v-if="empty" class="empty-box">
                <span></span>
                <p>该项零告警</br>系统运行良好</p>
            </div>
            <div class="chart_box" :id="classId" v-else></div>
        </div>
    </div>
</template>
<script>
import * as echarts from "echarts";

export default {
    name: "alertTypeItem",
    props: {
        imgIcon: {
            type: String,
            default: ""
        },
        classId: {
            type: String,
            default: "chart_box"
        },
        dataInfo: {
            type: Object,
            default: () => {
                return {
                    all: 0,
                    processed: 0,
                    name: "",
                    chart: []
                };
            }
        }
    },
    data() {
        return {
            empty: false,
            lineColors: [
                {
                    color: "rgba(255, 118, 118, 1)",
                    areaColor: "rgba(255, 118, 118, 0.16)"
                },
                {
                    color: "rgba(112, 202, 238, 1)",
                    areaColor: "rgba(112, 202, 238, 0.16)"
                },
                {
                    color: "rgba(103, 194, 58, 1)",
                    areaColor: "rgba(103, 194, 58, 0.16)"
                },
                {
                    color: "rgba(118, 118, 255, 1)",
                    areaColor: "rgba(118, 118, 255, 0.16)"
                }
            ],
            imgUrl: process.env.VUE_APP_BASE_API + "/minio/previewFile?fileName="
        };
    },
    mounted() {
        this.$nextTick(() => {
            // 生成 0 到 1 之间的随机数
            const randomNum = Math.random();
            if (randomNum < 0.5) {
                // 若随机数小于 0.5，调用 lineChart 方法
                this.lineChart();
            } else {
                // 若随机数大于等于 0.5，调用 barChart 方法
                this.barChart();
            }
        });
    },
    methods: {
        // 柱形图
        barChart() {
            const xType = this.dataInfo.chart.map((item) => item.name);
            const sType = this.dataInfo.chart.map((item) => Number(item.value));
            if (sType.every((item) => item === 0)) {
                this.empty = true;
                return;
            }
            let myChart = echarts.init(document.getElementById(this.classId));
            let option = {
                grid: {
                    left: 0,
                    right: 0,
                    top: 0,
                    bottom: 0,
                    containLabel: false
                },
                tooltip: {
                    show: false
                },
                xAxis: {
                    type: "category",
                    data: xType,
                    axisLabel: {
                        //x轴文字的配置
                        show: false
                    },
                    axisTick: {
                        //x轴刻度线的配置
                        show: false
                    },
                    axisLine: {
                        //x轴的配置
                        show: false
                    }

                    // boundaryGap: false
                },
                yAxis: {
                    type: "value",
                    axisLabel: {
                        show: false
                    },
                    splitLine: {
                        show: false
                    }
                },
                series: [
                    {
                        name: "",
                        type: "bar",
                        barWidth: 9,
                        itemStyle: {
                            // shadowOffsetX: 200,
                            // shadowOffsetY: 200,
                            normal: {
                                color: {
                                    x: 0,
                                    y: 0,
                                    x2: 1,
                                    y2: 0,
                                    type: "linear",
                                    global: false,
                                    colorStops: [
                                        {
                                            //第一节下面
                                            offset: 0,
                                            color: "#198ED9"
                                        },
                                        {
                                            offset: 0.5,
                                            color: "#45B6FF"
                                        },
                                        {
                                            offset: 1,
                                            color: "#198ED9"
                                        }
                                    ]
                                }
                            }
                        },
                        data: sType
                    },
                    {
                        data: sType.map((value) => ({
                            value: value,
                            symbol: value > 0 ? "path://M10,5 a1,1 0 1,0 20,0 a1,1 0 1,0 -20,0" : "none" // 如果数据为0，则不显示图形
                        })),
                        type: "pictorialBar",
                        symbolSize: [9, 3], //调整截面形状
                        symbolOffset: [0, -1],
                        z: 12,
                        symbolPosition: "end",
                        itemStyle: {
                            normal: {
                                color: new this.$echarts.graphic.LinearGradient(
                                    0,
                                    0,
                                    0,
                                    1,
                                    [
                                        {
                                            offset: 0,
                                            color: "#41D1FF"
                                        },
                                        {
                                            offset: 0.1,
                                            color: "#50A7FF"
                                        },
                                        {
                                            offset: 1,
                                            color: "#02D6EA"
                                        }
                                    ],
                                    false
                                )
                            }
                        }
                    }
                ]
            };
            myChart.setOption(option);
        },
        // 折线图
        lineChart() {
            const xLabel = this.dataInfo.chart.map((item) => item.name);
            const yLabel = this.dataInfo.chart.map((item) => Number(item.value));
            // 如果yLabel全为0，就显示空
            if (yLabel.every((item) => item === 0)) {
                this.empty = true;
                return;
            }
            let myChart = echarts.init(document.getElementById(this.classId));
            // 随机选取颜色
            const randomIndex = Math.floor(Math.random() * this.lineColors.length);
            const randomColor = this.lineColors[randomIndex];
            let option = {
                grid: {
                    left: 0,
                    right: 0,
                    top: 0,
                    bottom: 0,
                    containLabel: false
                },
                tooltip: {
                    show: false
                },
                xAxis: {
                    show: false,
                    type: "category",
                    boundaryGap: false,
                    data: xLabel
                },
                yAxis: {
                    show: false,
                    type: "value",
                    boundaryGap: [0, "0%"]
                },
                series: [
                    {
                        type: "line",
                        smooth: true,
                        data: yLabel,
                        showSymbol: false,
                        // 使用随机选取的线条颜色
                        lineStyle: {
                            color: randomColor.color
                        },
                        // 使用随机选取的区域颜色
                        areaStyle: {
                            color: randomColor.areaColor
                        }
                    }
                ]
            };
            myChart.setOption(option);
        }
    }
};
</script>
<style lang="scss" scoped>
.alertTypeItem {
    box-sizing: border-box;
    height: 88px;
    padding: 0 24px;
    color: rgba(0, 0, 0, 0.65);
    font-size: 12px;
    display: flex;
    align-items: center;
    .type_name {
        margin-bottom: 4px;
        font-weight: 500;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.85);
        line-height: 22px;
    }
    .img-box {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 40px;
        height: 40px;
        margin-right: 12px;
        text-align: center;
        line-height: 40px;
        background: url("../../assets/img/alarm/back.png") no-repeat center center;
        background-size: 100% 100%;
        img {
            width: 22px;
            height: 22px;
        }
    }

    .success {
        color: #97c48b;
        font-weight: bold;
        font-size: 18px;
    }
    .error {
        color: #d17178;
        font-weight: bold;
        font-size: 18px;
    }
    .chart-right {
        display: flex;
        justify-content: flex-end;
        flex: 1;
        .chart_box {
            width: 125px;
            width: 80%;
            height: 48px;
        }
    }
    ::v-deep .el-empty {
        padding: 0;
        margin-right: 50px;
        .el-empty__description {
            margin-top: 8px;
        }
    }
    .empty-box{
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        span{
            display: inline-block;
            width: 38px;
            height: 38px;
            margin-right: 8px;
            background: url("../../assets/img/alarm/no-data.png") no-repeat center center;
            background-size: 100% 100%;
        }
        p{
            margin: 0;
            font-size: 12px;
            color: #6A9BD3;
            line-height: 16px;
            text-align: left;
        }
    }
}
</style>
