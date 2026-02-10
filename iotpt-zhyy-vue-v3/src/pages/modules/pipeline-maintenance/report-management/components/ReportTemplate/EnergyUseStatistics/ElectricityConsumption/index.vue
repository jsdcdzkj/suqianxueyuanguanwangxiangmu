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
            <div ref="chartEl" class="charts-height" />
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onBeforeUnmount, nextTick, getCurrentInstance } from "vue";
import * as echarts from "echarts";
import type { ECharts, EChartsOption, BarSeriesOption } from "echarts";
import Common from "../../common.js";
import { energyInfo } from "@/api/pipeline-maintenance/report-management";
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";

// 定义数据类型
interface TotalData {
    COSum: string | number;
    TceSum: string | number;
    thisSum: string | number;
    thisTime?: string[];
    thisData?: number[];
    [key: string]: any;
}

interface EnergyInfoResponse {
    code: number;
    message: string;
    data: TotalData;
}

interface CommonMixinData {
    areaIds: string[] | number[];
    startTime: string;
    endTime: string;
    timeTypes: string;
    type: string | number;
    [key: string]: any;
}

export default defineComponent({
    cname: "电力消耗统计",
    name: "ElectricityConsumption",
    mixins: [Common] as any,
    setup(props, { expose }) {
        // Refs
        const chartEl = ref<HTMLDivElement | null>(null);
        const chartInstance = ref<ECharts | null>(null);
        
        // 响应式数据
        const totalData = ref<TotalData>({
            COSum: "9.97",
            TceSum: "1.34",
            thisSum: "5947.6"
        });
        
        const xAxis = ref<string[]>([
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
        ]);
        
        const series = ref<number[]>([0, 0, 0, 0, 0, 6000, 0, 8000, 10000, 0]);

        // 格式化数字显示
        const formatNumber = (value: string | number): string => {
            const num = typeof value === 'string' ? parseFloat(value) : value;
            if (isNaN(num)) return '0';
            
            if (num >= 10000) {
                return (num / 10000).toFixed(2) + '万';
            } else if (num >= 1000) {
                return (num / 1000).toFixed(2) + '千';
            }
            return num.toString();
        };

        // 格式化日期
        const formatDateAxis = (dates: string[]): string[] => {
            return dates.map(date => {
                try {
                    const d = new Date(date);
                    return d.toLocaleDateString('zh-CN', { 
                        month: '2-digit', 
                        day: '2-digit' 
                    });
                } catch {
                    return date;
                }
            });
        };

        // 初始化图表
        const initDispatchLineChart = (): void => {
            if (!chartEl.value) {
                console.warn('Chart element not found');
                return;
            }
            
            // 如果已有图表实例，先销毁
            if (chartInstance.value) {
                chartInstance.value.dispose();
            }
            
            // 创建新的图表实例
            chartInstance.value = echarts.init(chartEl.value);
            
            // 过滤有效数据
            const validData = series.value
                .filter(item => Number(item) > 0)
                .map(item => Number(item));
            
            // 格式化日期轴
            const formattedXAxis = formatDateAxis(xAxis.value);
            
            const option: EChartsOption = {
                title: {
                    text: '用电量统计',
                    left: 'center',
                    textStyle: {
                        fontSize: 16,
                        fontWeight: 'bold',
                        color: '#333'
                    }
                },
                color: ["#4A74FF"],
                legend: {
                    right: 0,
                    show: false
                },
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        type: "shadow"
                    },
                    backgroundColor: 'rgba(50, 50, 50, 0.9)',
                    borderColor: 'rgba(255, 255, 255, 0.2)',
                    textStyle: {
                        color: '#fff',
                        fontSize: 14
                    },
                    formatter: (params: any) => {
                        if (Array.isArray(params)) {
                            return params.map(p => `${p.name}<br/>用电量: ${p.value} kW·h`).join('<br/>');
                        }
                        return `${params.name}<br/>用电量: ${params.value} kW·h`;
                    }
                },
                xAxis: {
                    type: "category",
                    data: formattedXAxis,
                    axisLine: {
                        lineStyle: {
                            color: '#ccc'
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        color: '#666',
                        fontSize: 12
                    }
                },
                yAxis: {
                    type: "value",
                    minInterval: 1,
                    name: "kW·h",
                    nameTextStyle: {
                        color: '#666',
                        fontSize: 14,
                        padding: [0, 0, 0, 10]
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#ccc'
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        color: '#666',
                        fontSize: 12
                    },
                    splitLine: {
                        lineStyle: {
                            color: '#f0f0f0',
                            type: 'dashed'
                        }
                    }
                },
                grid: {
                    top: "60px",
                    right: "20px",
                    left: "60px",
                    bottom: "30px"
                },
                series: [
                    {
                        name: '用电量',
                        data: validData,
                        type: "bar",
                        barMaxWidth: 30,
                        itemStyle: {
                            borderRadius: [4, 4, 0, 0],
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#4A74FF' },
                                { offset: 1, color: '#2A54CC' }
                            ])
                        },
                        label: {
                            show: true,
                            position: 'top',
                            formatter: (params: any) => {
                                return formatNumber(params.value);
                            },
                            color: '#333',
                            fontSize: 12,
                            fontWeight: 'bold'
                        },
                        emphasis: {
                            itemStyle: {
                                shadowColor: 'rgba(74, 116, 255, 0.5)',
                                shadowBlur: 10,
                                shadowOffsetY: 3
                            }
                        }
                    } as BarSeriesOption
                ],
                animation: true,
                animationDuration: 1000,
                animationEasing: 'cubicOut'
            };

            chartInstance.value.setOption(option);
            
            // 添加窗口大小变化监听
            const handleResize = () => {
                if (chartInstance.value) {
                    chartInstance.value.resize();
                }
            };
            
            window.addEventListener('resize', handleResize);
            
            // 返回清理函数
            return () => {
                window.removeEventListener('resize', handleResize);
            };
        };

        // 加载数据
        const loadData = async (): Promise<void> => {
            try {
                const mixinData = (getCurrentInstance()?.proxy as any) as CommonMixinData;
                
                if (!mixinData.areaIds || mixinData.areaIds.length === 0) {
                    initDispatchLineChart();
                    return;
                }
                
                const response = await energyInfo({
                    areaIds: mixinData.areaIds,
                    startTime: mixinData.startTime,
                    endTime: mixinData.endTime,
                    timeType: mixinData.timeTypes,
                    splType: mixinData.type
                }) as EnergyInfoResponse;
                
                if (response.data) {
                    xAxis.value = response.data.thisTime || [];
                    series.value = response.data.thisData || [];
                    
                    // 更新统计数据
                    totalData.value = {
                        COSum: response.data.COSum || "0",
                        TceSum: response.data.TceSum || "0",
                        thisSum: response.data.thisSum || "0",
                        ...response.data
                    };
                }
                
                initDispatchLineChart();
            } catch (error) {
                console.error('Failed to load energy information data:', error);
                // 使用默认数据
                initDispatchLineChart();
            }
        };

        // 刷新数据
        const refreshData = async (): Promise<void> => {
            await loadData();
        };

        // 更新图表数据
        const updateChartData = (newTotalData: TotalData, newXAxis?: string[], newSeries?: number[]): void => {
            totalData.value = newTotalData;
            if (newXAxis) xAxis.value = newXAxis;
            if (newSeries) series.value = newSeries;
            initDispatchLineChart();
        };

        // 组件挂载
        onMounted(async () => {
            await nextTick();
            await loadData();
        });

        // 组件卸载
        onBeforeUnmount(() => {
            if (chartInstance.value) {
                chartInstance.value.dispose();
                chartInstance.value = null;
            }
        });

        // 暴露给父组件的方法
        expose({
            refreshChart: refreshData,
            getChart: () => chartInstance.value,
            updateChartData,
            getData: () => ({
                totalData: totalData.value,
                xAxis: xAxis.value,
                series: series.value
            })
        });

        return {
            chartEl,
            totalData,
            xAxis,
            series,
            initDispatchLineChart,
            loadData,
            refreshData
        };
    },
    
    // 计算属性
    computed: {
        // 格式化后的统计数据
        formattedTotalData() {
            return {
                thisSum: formatNumber(this.totalData.thisSum),
                TceSum: formatNumber(this.totalData.TceSum),
                COSum: formatNumber(this.totalData.COSum)
            };
        },
        
        // 统计摘要
        statisticsSummary() {
            const validData = this.series.filter(item => Number(item) > 0);
            const sum = validData.reduce((acc, curr) => acc + Number(curr), 0);
            const avg = validData.length > 0 ? sum / validData.length : 0;
            const max = validData.length > 0 ? Math.max(...validData) : 0;
            const min = validData.length > 0 ? Math.min(...validData) : 0;
            
            return {
                sum: formatNumber(sum),
                avg: formatNumber(avg),
                max: formatNumber(max),
                min: formatNumber(min),
                count: validData.length
            };
        },
        
        // 能源转换系数
        energyConversion() {
            const electricity = Number(this.totalData.thisSum) || 0;
            const coal = Number(this.totalData.TceSum) || 0;
            const co2 = Number(this.totalData.COSum) || 0;
            
            return {
                electricityToCoal: electricity > 0 ? (coal / electricity * 1000).toFixed(4) : '0',
                electricityToCO2: electricity > 0 ? (co2 / electricity * 1000).toFixed(4) : '0',
                coalToCO2: coal > 0 ? (co2 / coal).toFixed(2) : '0'
            };
        }
    }
});
</script>

<style lang="scss" scoped>
.charts-height {
    height: 320px;
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: box-shadow 0.3s ease;
    
    &:hover {
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
    }
}

.module-item-desc {
    margin-bottom: 24px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;
    font-size: 15px;
    line-height: 1.6;
    color: #555;
    border-left: 4px solid #4A74FF;
    
    .num {
        font-weight: bold;
        color: #4A74FF;
        font-size: 18px;
        margin: 0 4px;
        transition: color 0.3s ease;
        
        &:hover {
            color: #2A54CC;
        }
    }
}

// 响应式设计
@media (max-width: 768px) {
    .charts-height {
        height: 280px;
        padding: 15px;
    }
    
    .module-item-desc {
        padding: 16px;
        font-size: 14px;
        
        .num {
            font-size: 16px;
        }
    }
}
</style>