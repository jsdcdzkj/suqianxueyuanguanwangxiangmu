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

<script lang="ts">
import { 
    defineComponent, 
    PropType, 
    ref, 
    onMounted, 
    onBeforeUnmount, 
    nextTick,
    watch 
} from "vue";
import * as echarts from "echarts";
import type { ECharts, EChartsOption } from "echarts";
import Common from "../../common.js";
import { getWarningAreaPie100 } from "@/api/pipeline-maintenance/report-management";
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";

// 定义数据类型
interface WarningAreaData {
    x: string[];
    y: number[];
}

interface ApiResponse<T = any> {
    code: number;
    message: string;
    data: T;
}

interface CommonMixinData {
    areaIds: string[] | number[];
    startTime: string;
    endTime: string;
    valdata?: any;
    [key: string]: any;
}

export default defineComponent({
    cname: "信号告警排行TOP100",
    name:'RankingChart100',
    mixins: [Common] as any,
    props: {
        className: {
            type: String as PropType<string>,
            default: "chart",
        },
        width: {
            type: String as PropType<string>,
            default: "100%",
        },
        height: {
            type: String as PropType<string>,
            default: "300px",
        },
        // 如果需要从父组件传递额外的数据
        externalData: {
            type: Object as PropType<Record<string, any>>,
            default: () => ({}),
        },
    },
    setup(props, { expose }) {
        // Refs
        const chartEl = ref<HTMLDivElement | null>(null);
        const chartInstance = ref<ECharts | null>(null);
        
        // 响应式数据
        const rankName = ref<string[]>([
            "电气火灾告警",
            "手动告警",
            "环境告警",
            "其它告警",
            "其它告警",
        ]);
        
        const rankNum = ref<number[]>([3, 2, 4, 8, 8]);

        // 监听 mixin 中的 valdata 变化
        // 注意：这里需要获取 mixin 的数据
        const watchValdata = () => {
            // 由于 mixin 数据不能直接在 setup 中访问，
            // 我们可以在 mounted 后通过实例访问
            // 或者在组件选项中处理
        };

        // 初始化图表
        const initChart = (): void => {
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
            
            // 配置项
            const option: EChartsOption = {
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        type: "shadow",
                    },
                    backgroundColor: 'rgba(50, 50, 50, 0.7)',
                    borderColor: 'rgba(255, 255, 255, 0.2)',
                    textStyle: {
                        color: '#fff',
                    },
                    borderWidth: 1,
                    formatter: (params: any) => {
                        const data = Array.isArray(params) ? params[0] : params;
                        return `${data.name}<br/>数量: ${data.value}`;
                    }
                },
                grid: {
                    top: "20px",
                    right: "20px",
                    left: "60px",
                    bottom: "85px",
                },
                xAxis: {
                    type: "category",
                    data: rankName.value,
                    axisLine: {
                        lineStyle: {
                            color: "#666",
                        },
                    },
                    axisLabel: {
                        margin: 10,
                        color: "#333",
                        rotate: 35,
                        fontSize: 12,
                        interval: 0, // 强制显示所有标签
                    },
                    axisTick: {
                        show: false,
                    },
                },
                yAxis: {
                    type: "value",
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
                            type: 'dashed'
                        },
                    },
                },
                series: [
                    {
                        type: "bar",
                        data: rankNum.value,
                        barWidth: "40%",
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#1890ff' },
                                { offset: 1, color: '#0050b3' }
                            ]),
                            borderRadius: [4, 4, 0, 0],
                            shadowColor: 'rgba(0, 0, 0, 0.1)',
                            shadowBlur: 4,
                            shadowOffsetY: 2
                        },
                        label: {
                            show: true,
                            position: 'top',
                            formatter: '{c}',
                            rotate: -90,
                            color: '#fff',
                            fontSize: 12,
                            fontWeight: 'bold',
                            textBorderColor: 'rgba(0, 0, 0, 0.3)',
                            textBorderWidth: 1
                        },
                        emphasis: {
                            itemStyle: {
                                shadowColor: 'rgba(0, 0, 0, 0.3)',
                                shadowBlur: 8,
                                shadowOffsetY: 4
                            }
                        }
                    },
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
                // 在实际使用中，这里需要获取 mixin 的数据
                // 由于 mixin 在 setup 中不可直接访问，我们假设这些数据通过 props 或其他方式传递
                // 或者我们可以在组件选项中处理
                
                // 这里暂时使用模拟数据，实际项目中应该从 mixin 获取
                const mockMixinData: CommonMixinData = {
                    areaIds: [],
                    startTime: '',
                    endTime: ''
                };
                
                const { areaIds } = mockMixinData;
                
                if (areaIds && areaIds.length > 0) {
                    const response = await getWarningAreaPie100({
                        startTime: mockMixinData.startTime,
                        endTime: mockMixinData.endTime,
                        areaIds,
                    }) as ApiResponse<WarningAreaData>;
                    
                    if (response.data && response.data.x && response.data.y) {
                        rankName.value = response.data.x;
                        rankNum.value = response.data.y;
                    }
                }
                
                initChart();
            } catch (error) {
                console.error('Failed to load warning area data:', error);
                // 使用默认数据
                initChart();
            }
        };

        // 重新加载数据
        const refreshData = async (): Promise<void> => {
            await loadData();
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
            updateData: (names: string[], values: number[]) => {
                rankName.value = names;
                rankNum.value = values;
                if (chartInstance.value) {
                    chartInstance.value.setOption({
                        xAxis: {
                            data: names
                        },
                        series: [{
                            data: values
                        }]
                    });
                }
            }
        });

        return {
            chartEl,
            rankName,
            rankNum,
            initChart,
            loadData,
            refreshData
        };
    },
    
    // Vue 2 兼容选项
    watch: {
        // 监听 mixin 中的 valdata 变化
        '$props.valdata': {
            handler(newVal: any) {
                if (newVal) {
                    this.initChart();
                }
            },
            deep: true
        }
    },
    
    mounted() {
        // 兼容 Vue 2 的 mounted 钩子
        this.$nextTick(() => {
            if ((this as any).areaIds?.length > 0) {
                getWarningAreaPie100({
                    startTime: (this as any).startTime,
                    endTime: (this as any).endTime,
                    areaIds: (this as any).areaIds,
                }).then((res: ApiResponse<WarningAreaData>) => {
                    this.rankName = res.data.x;
                    this.rankNum = res.data.y;
                    this.initChart();
                });
            } else {
                this.initChart();
            }
        });
    },
    
    beforeDestroy() {
        if (this.chartInstance) {
            this.chartInstance.dispose();
            this.chartInstance = null;
        }
    },
    
    methods: {
        // Vue 2 方法
        initChart() {
            // 这里调用 setup 中的 initChart
            // 或者直接在这里实现
            if (!this.$refs.chartEl) return;
            
            this.chartInstance = echarts.init(this.$refs.chartEl as HTMLDivElement);
            
            const option = {
                // ... 配置项
            };
            
            this.chartInstance.setOption(option);
        }
    },
    
    // 计算属性
    computed: {
        // 如果需要计算属性
        chartData() {
            return {
                names: this.rankName,
                values: this.rankNum
            };
        }
    }
});
</script>

<style scoped>
.chart {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 16px;
    transition: all 0.3s ease;
}

.chart:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}
</style>