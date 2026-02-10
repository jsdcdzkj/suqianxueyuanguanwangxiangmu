<template>
    <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from "echarts";
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
            this.chart = echarts.init(this.$el);
            this.chart.setOption({
                tooltip: {
                    trigger: "item"
                },
                series: [
                    {
                        name: "类型统计",
                        type: "pie",
                        radius: ["40%", "60%"],
                        data: this.valdata.pieData,
                        label: {
                            show: true,
                            position: "outside",
                            formatter: function (param) {
                                return "{hr|}{a|" + param.percent + "%}\n{b|" + param.name + "}";
                            },
                            rich: {
                                a: {
                                    color: "#333",
                                    align: "center",
                                    padding: [5, 0, 5, 0],
                                    fontWeight: "bold",
                                    fontSize: 13
                                },
                                b: {
                                    color: "#666",
                                    fontSize: 12,
                                    padding: [6, 4, 4, 4],
                                    // 背景色
                                    backgroundColor: "#f3f3f3",
                                    borderRadius: 3
                                }
                            }
                        },
                        labelLine: {
                            show: true,
                            normal: {
                                length: 20,
                                length2: 50
                            }
                        },
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: "rgba(0, 0, 0, 0.5)"
                            }
                        }
                    }
                ]
            });
        }
    }
};
</script>
