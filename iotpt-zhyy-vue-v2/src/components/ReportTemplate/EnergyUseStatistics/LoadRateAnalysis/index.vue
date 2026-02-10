<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">
            本监测周期内，最大负载率
            <span class="num">{{dataOptions.maxValue}}</span>
            ，发生于
            <span class="num">{{dataOptions.maxTime}}</span>
            。本监测周期用能概况曲线如下：
        </div>
        <div>
            <div id="curveChart" class="chartHeight"></div>
        </div>
    </div>
</template>

<script>
import Common from "@/components/ReportTemplate/common";
import { getAreaLoad } from "@/api/report";
export default {
    cname: "负载率分析",
    mixins: [Common],
	data(){
		return {
			dataOptions:{
				maxValue:"32.74",
				maxTime:"2024-01-01",
				date:['2024-02-01','2024-02-02','2024-02-03','2024-02-04','2024-02-05','2024-02-06','2024-02-07'],
				value:[22,13,11,6,4,8,2]
			}
		}
	},
    mounted() {
		this.$nextTick(() => {
		    if (this.areaIds.length > 0) {
		       getAreaLoad({
		       	"areaIds":this.areaIds,
		       	"startTime":this.startTime,
		       	"endTime":this.endTime,
		       	"timeType":this.timeTypes,
		       	"splType":this.type
		       }).then(({data})=>{
					this.dataOptions = {
						maxValue:data.maxValue,
						maxTime:data.maxTime,
						date:data.data.map(el=>el.name),
						value:data.data.map(el=>el.value),
					}
					this.initDispatchLineChart();
		       })
		    }else{
				this.initDispatchLineChart();
			}
		});
    },
    methods: {
        initDispatchLineChart() {
            var chartDom = document.getElementById("curveChart");
            var myChart = this.$echarts.init(chartDom);
            const colorList = ["#1BBFC0"];
            var option = {
                backgroundColor: "#FFFFFF",
                legend: {
                    x: "center",
                    y: "top",
                    show: true,
                    right: "5%",
                    top: "5%",
                    itemWidth: 6,
                    itemGap: 20,
                    textStyle: {
                        color: "#556677"
                    },
                    data: ["最大负载率"]
                },
                grid: {
                    top: "20px",
                    right: "60px",
                    left: "60px",
                    bottom: "40px"
                },
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        label: {
                            show: true,
                            backgroundColor: "#fff",
                            color: "#556677",
                            borderColor: "rgba(0,0,0,0)",
                            shadowColor: "rgba(0,0,0,0)",
                            shadowOffsetY: 0
                        },
                        lineStyle: {
                            width: 0
                        }
                    },
                    backgroundColor: "#fff",
                    textStyle: {
                        color: "#5c6c7c"
                    },
                    padding: [10, 10],
                    extraCssText: "box-shadow: 1px 0 2px 0 rgba(163,163,163,0.5)"
                },
                xAxis: [
                    {
                        type: "category",
                        name: "日期",
                        data: this.dataOptions.date,
                        axisLine: {
                            show: true,
                            lineStyle: {
                                color: "#000000"
                            }
                        },
                        axisTick: {
                            show: true
                        },
                        axisLabel: {
                            textStyle: {
                                color: "#556677"
                            },
                            // 默认x轴字体大小
                            fontSize: 12,
                            // margin:文字到x轴的距离
                            margin: 15
                        },
                        splitLine: {
                            show: true,
                            lineStyle: {
                                type: "dashed"
                            }
                        },
                        boundaryGap: false
                    }
                ],
                yAxis: [
                    {
                        type: "value",
                        nameTextStyle: {
                            color: "#000000"
                        },
                        axisTick: {
                            show: false
                        },
                        axisLine: {
                            show: true,
                            lineStyle: {
                                color: "#DCE2E8"
                            }
                        },
                        axisLabel: {
                            textStyle: {
                                color: "#556677"
                            }
                        },
                        splitLine: {
                            show: true,
                            lineStyle: {
                                type: "dashed"
                            }
                        }
                    }
                ],
                series: [
                    {
                        name: "最大负载率",
                        type: "line",
                        data: this.dataOptions.value,
                        symbolSize: 1,
                        symbol: "circle",
                        smooth: true,
                        yAxisIndex: 0,
                        showSymbol: false,
                        emphasis: {
                            focus: "series"
                        },
                        lineStyle: {
                            width: 2
                            // shadowColor: '#1BBFC0',
                        },
                        itemStyle: {
                            normal: {
                                color: colorList[0],
                                borderColor: colorList[0]
                            }
                        },
                        markPoint: {
                            symbol: "pin", //标记(气泡)的图形
                            symbolSize: 50, //标记(气泡)的大小
                            itemStyle: {
                                // color: '#4587E7', //图形的颜色。
                                borderColor: "#000", //图形的描边颜色。支持的颜色格式同 color，不支持回调函数。
                                borderWidth: 0, //描边线宽。为 0 时无描边。
                                borderType: "solid" //柱条的描边类型，默认为实线，支持 ‘solid’, ‘dashed’, ‘dotted’。
                            },
                            data: [
                                { type: "max", name: "最大值" },
                                { type: "min", name: "最小值" }
                            ]
                        }
                    }
                ]
            };

            option && myChart.setOption(option);
        }
    }
};
</script>

<style>
.chartHeight {
    height: 290px;
}
</style>
