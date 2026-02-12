<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">
            本监测周期内，最大负载率
            <span class="num">{{ formattedData.maxValue }}</span>
            ，发生于
            <span class="num">{{ formattedData.maxTime }}</span>
            。本监测周期用能概况曲线如下：
        </div>
        <div>
            <div ref="chartEl" class="chartHeight"></div>
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onBeforeUnmount, nextTick, getCurrentInstance, computed } from "vue";
import * as echarts from "echarts";
import type { ECharts, EChartsOption, LineSeriesOption, MarkPointComponentOption } from "echarts";
import Common from "../../common.js";
import { getAreaLoad } from "@/api/pipeline-maintenance/report-management";
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";

// 定义数据类型
interface LoadDataItem {
    name: string;
    value: number;
}

interface LoadResponseData {
    maxValue: string | number;
    maxTime: string;
    data: LoadDataItem[];
    [key: string]: any;
}

interface DataOptions {
    maxValue: string | number;
    maxTime: string;
    date: string[];
    value: number[];
}

interface CommonMixinData {
    areaIds: string[] | number[];
    startTime: string;
    endTime: string;
    timeTypes: string;
    type: string | number;
    [key: string]: any;
}

interface LoadResponse {
    code: number;
    message: string;
    data: LoadResponseData;
}

export default defineComponent({
    cname: "负载率分析",
    name: "LoadRateAnalysis",
    mixins: [Common] as any,
    components: {
        Title
    },

    setup(props, { expose }) {
        // Refs
        const chartEl = ref<HTMLDivElement | null>(null);
        const chartInstance = ref<ECharts | null>(null);
        
        // 响应式数据
        const dataOptions = ref<DataOptions>({
            maxValue: "32.74",
            maxTime: "2024-01-01",
            date: ['2024-02-01','2024-02-02','2024-02-03','2024-02-04','2024-02-05','2024-02-06','2024-02-07'],
            value: [22,13,11,6,4,8,2]
        });

        // 添加图表初始化状态跟踪
        const chartInitialized = ref(false);
        
        // 颜色配置
        const colorList = ["#1BBFC0"];

        // 格式化日期
        const formatDate = (dateStr: string): string => {
            try {
                const date = new Date(dateStr);
                return date.toLocaleDateString('zh-CN', {
                    month: '2-digit',
                    day: '2-digit'
                });
            } catch {
                return dateStr;
            }
        };

        // 格式化数值
        const formatValue = (value: string | number): string => {
            const num = typeof value === 'string' ? parseFloat(value) : value;
            return isNaN(num) ? '0' : num.toFixed(2);
        };

        // 初始化图表
        const initDispatchLineChart = (): (() => void) | void => {
            // 检查DOM元素是否存在
            if (!chartEl.value) {
                console.error('图表容器元素不存在');
                return;
            }
            
            // 检查ECharts是否加载成功
            if (!echarts) {
                console.error('ECharts库未加载');
                return;
            }
            
            // 如果已有图表实例，先销毁
            if (chartInstance.value) {
                chartInstance.value.dispose();
            }
            
            try {
                // 创建新的图表实例
                chartInstance.value = echarts.init(chartEl.value);
                
                // 格式化日期轴
                const formattedDates = dataOptions.value.date.map(formatDate);
                
                const option: EChartsOption = {
                    backgroundColor: "#FFFFFF",
                    title: {
                        text: '负载率分析',
                        left: 'center',
                        textStyle: {
                            fontSize: 16,
                            fontWeight: 'bold',
                            color: '#333'
                        }
                    },
                    legend: {
                        x: "center",
                        y: "top",
                        show: true,
                        right: "5%",
                        top: "5%",
                        itemWidth: 8,
                        itemHeight: 8,
                        itemGap: 20,
                        textStyle: {
                            color: "#556677",
                            fontSize: 12
                        },
                        data: ["负载率"]
                    },
                    grid: {
                        top: "60px",
                        right: "40px",
                        left: "60px",
                        bottom: "50px"
                    },
                    tooltip: {
                        trigger: "axis",
                        axisPointer: {
                            type: 'line',
                            label: {
                                show: true,
                                backgroundColor: "#fff",
                                color: "#556677",
                                borderColor: "rgba(0,0,0,0)",
                                shadowColor: "rgba(0,0,0,0)",
                                shadowOffsetY: 0,
                                formatter: (params: any) => {
                                    return `${params.value}%`;
                                }
                            },
                            lineStyle: {
                                width: 1,
                                type: 'dashed',
                                color: '#ccc'
                            }
                        },
                        backgroundColor: "rgba(255,255,255,0.95)",
                        textStyle: {
                            color: "#5c6c7c",
                            fontSize: 12
                        },
                        padding: [12, 16],
                        extraCssText: "box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1); border-radius: 4px;",
                        formatter: (params: any) => {
                            if (Array.isArray(params)) {
                                return params.map(p => {
                                    const date = formatDate(p.axisValue);
                                    return `${date}<br/>负载率: ${p.value}%`;
                                }).join('<br/>');
                            }
                            const date = formatDate(params.axisValue);
                            return `${date}<br/>负载率: ${params.value}%`;
                        }
                    },
                    xAxis: {
                        type: "category",
                        name: "日期",
                        nameTextStyle: {
                            color: '#666',
                            fontSize: 12,
                            padding: [0, 0, 0, 10]
                        },
                        data: formattedDates,
                        axisLine: {
                            show: true,
                            lineStyle: {
                                color: "#DCE2E8",
                                width: 1
                            }
                        },
                        axisTick: {
                            show: true,
                            alignWithLabel: true,
                            length: 4
                        },
                        axisLabel: {
                            color: "#556677",
                            fontSize: 11,
                            margin: 12,
                            rotate: formattedDates.length > 10 ? 45 : 0
                        },
                        splitLine: {
                            show: true,
                            lineStyle: {
                                type: "dashed",
                                color: "#E6E9ED"
                            }
                        },
                        boundaryGap: false
                    },
                    yAxis: {
                        type: "value",
                        name: "负载率 (%)",
                        nameTextStyle: {
                            color: "#666",
                            fontSize: 12,
                            padding: [0, 0, 10, 0]
                        },
                        axisTick: {
                            show: false
                        },
                        axisLine: {
                            show: true,
                            lineStyle: {
                                color: "#DCE2E8",
                                width: 1
                            }
                        },
                        axisLabel: {
                            color: "#556677",
                            fontSize: 11,
                            formatter: '{value}%'
                        },
                        splitLine: {
                            show: true,
                            lineStyle: {
                                type: "dashed",
                                color: "#E6E9ED"
                            }
                        },
                        min: 0,
                        max: (value: any) => {
                            const max = Math.max(...dataOptions.value.value);
                            return Math.ceil(max / 10) * 10 + 10; // 向上取整到最近的10的倍数
                        }
                    },
                    series: [
                        {
                            name: "负载率",
                            type: "line",
                            data: dataOptions.value.value,
                            symbolSize: 8,
                            symbol: "circle",
                            smooth: true,
                            showSymbol: true,
                            emphasis: {
                                focus: "series",
                                scale: true,
                                scaleSize: 10
                            },
                            lineStyle: {
                                width: 3,
                                shadowColor: 'rgba(27, 191, 192, 0.3)',
                                shadowBlur: 10,
                                shadowOffsetY: 5
                            },
                            itemStyle: {
                                color: colorList[0],
                                borderColor: '#fff',
                                borderWidth: 2,
                                shadowColor: 'rgba(0, 0, 0, 0.1)',
                                shadowBlur: 4
                            },
                            areaStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: 'rgba(27, 191, 192, 0.4)' },
                                    { offset: 1, color: 'rgba(27, 191, 192, 0.1)' }
                                ])
                            },
                            markPoint: {
                                symbol: "circle",
                                symbolSize: 8,
                                itemStyle: {
                                    color: '#FF6B6B',
                                    borderColor: '#fff',
                                    borderWidth: 2
                                },
                                label: {
                                    color: '#fff',
                                    backgroundColor: '#FF6B6B',
                                    padding: [4, 8],
                                    borderRadius: 4,
                                    fontSize: 10
                                },
                                data: [
                                    { 
                                        type: "max", 
                                        name: "峰值",
                                        symbol: 'pin',
                                        symbolSize: 50
                                    },
                                    { 
                                        type: "min", 
                                        name: "谷值",
                                        symbol: 'pin',
                                        symbolSize: 50
                                    }
                                ]
                            } as MarkPointComponentOption
                        } as LineSeriesOption
                    ],
                    animation: true,
                    animationDuration: 1500,
                    animationEasing: 'cubicOut'
                };

                chartInstance.value.setOption(option);
                chartInitialized.value = true;
                console.log('图表初始化成功');
                
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
            } catch (error) {
                console.error('图表初始化失败:', error);
            }
        };

        // 加载数据
        const loadData = async (): Promise<void> => {
            try {
                const mixinData = (getCurrentInstance()?.proxy as any) as CommonMixinData;
                
                if (!mixinData.areaIds || mixinData.areaIds.length === 0) {
                    console.warn('areaIds is empty'); // 添加调试信息
                    initDispatchLineChart();
                    return;
                }
                
                const response = await getAreaLoad({
                    areaIds: mixinData.areaIds,
                    startTime: mixinData.startTime,
                    endTime: mixinData.endTime,
                    timeType: mixinData.timeTypes,
                    splType: mixinData.type
                }) as LoadResponse;
                
                console.log('API response:', response); // 添加调试信息
                
                if (response.data) {
                    dataOptions.value = {
                        maxValue: response.data.maxValue || "0",
                        maxTime: response.data.maxTime || "",
                        date: response.data.data?.map(item => item.name) || [],
                        value: response.data.data?.map(item => Number(item.value) || 0) || []
                    };
                }
                
                initDispatchLineChart();
            } catch (error) {
                console.error('Failed to load area load data:', error);
                // 使用默认数据
                console.log('Using default data'); // 添加调试信息
                initDispatchLineChart();
            }
        };

        // 刷新数据
        const refreshData = async (): Promise<void> => {
            await loadData();
        };

        // 更新图表数据
        const updateChartData = (newData: Partial<DataOptions>): void => {
            dataOptions.value = { ...dataOptions.value, ...newData };
            initDispatchLineChart();
        };

        // 格式化显示的数据
        const formattedData = computed(() => ({
            maxValue: formatValue(dataOptions.value.maxValue) + '%',
            maxTime: formatDate(dataOptions.value.maxTime),
            date: dataOptions.value.date.map(formatDate),
            value: dataOptions.value.value.map(v => formatValue(v) + '%')
        }));
        
        // 负载率趋势分析
        const loadTrend = computed(() => {
            const values = dataOptions.value.value;
            if (values.length < 2) return '数据不足';
            
            const firstAvg = values.slice(0, 3).reduce((a, b) => a + b, 0) / Math.min(3, values.length);
            const lastAvg = values.slice(-3).reduce((a, b) => a + b, 0) / Math.min(3, values.length);
            const trend = ((lastAvg - firstAvg) / firstAvg * 100).toFixed(1);
            
            if (Math.abs(Number(trend)) < 5) return '平稳';
            return Number(trend) > 0 ? `上升${Math.abs(Number(trend))}%` : `下降${Math.abs(Number(trend))}%`;
        });
        
        // 建议措施
        const suggestions = computed(() => {
            const avg = dataOptions.value.value.reduce((a, b) => a + b, 0) / Math.max(1, dataOptions.value.value.length);
            
            if (avg < 30) return '建议优化设备调度，提高负载率，避免资源浪费';
            if (avg < 70) return '负载率在正常范围内，继续保持';
            return '负载率较高，建议增加设备容量或进行负载均衡';
        });

        // 组件挂载
        onMounted(async () => {
            // 增加延迟确保 DOM 已完全渲染
            await nextTick();
            setTimeout(() => {
                loadData();
            }, 100); // 短暂延迟确保元素可用
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
            getData: () => dataOptions.value,
            // 获取统计分析
            getStatistics: () => {
                const values = dataOptions.value.value;
                if (values.length === 0) return null;
                
                const max = Math.max(...values);
                const min = Math.min(...values);
                const avg = values.reduce((a, b) => a + b, 0) / values.length;
                const variance = values.reduce((a, b) => a + Math.pow(b - avg, 2), 0) / values.length;
                
                return {
                    max: { value: max, index: values.indexOf(max) },
                    min: { value: min, index: values.indexOf(min) },
                    average: avg,
                    variance: variance,
                    standardDeviation: Math.sqrt(variance),
                    // 负载率等级
                    level: avg < 30 ? '低负载' : avg < 70 ? '正常负载' : '高负载'
                };
            }
        });

        return {
            chartEl,
            dataOptions,
            formattedData,
            loadTrend,
            suggestions
        };
    }
});
</script>

<style scoped>
.chartHeight {
    height: 340px; /* 确保高度设置正确 */
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: box-shadow 0.3s ease;
    /* 添加调试边框，确认元素是否存在 */
    /* border: 1px solid red; */
}
.chartHeight:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.module-item-desc {
    /* margin-bottom: 24px; */
    padding: 20px;
}

.num {
    font-weight: bold;
    color: #1BBFC0;
    font-size: 18px;
    margin: 0 4px;
    transition: color 0.3s ease;
}

.num:hover {
    color: #159a9b;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .chartHeight {
        height: 280px;
        padding: 15px;
    }
    
    .module-item-desc {
        padding: 16px;
        font-size: 14px;
    }
    
    .num {
        font-size: 16px;
    }
}
</style>