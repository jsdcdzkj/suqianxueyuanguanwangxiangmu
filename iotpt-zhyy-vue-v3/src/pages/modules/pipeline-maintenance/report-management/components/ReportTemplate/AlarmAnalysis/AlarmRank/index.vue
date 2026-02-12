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
import { defineComponent, PropType, ref, onMounted, onBeforeUnmount, watch, nextTick, getCurrentInstance } from "vue";
import * as echarts from "echarts";
import Common from "../../common.js";
import { getWarningSignStat } from "@/api/pipeline-maintenance/report-management";
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";

// 定义数据类型
interface WarningSignItem {
    NAME: string;
    VALUE: number;
}

interface CommonMixin {
    areaIds: string[] | number[];
    startTime: string;
    endTime: string;
    [key: string]: any;
}

export default defineComponent({
    cname: "信号告警排行TOP10",
    name: "RankingChart",
    mixins: [Common] as any,
    components: {
        Title
    },
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
        valdata: {
            type: Object as PropType<Record<string, any>>,
            default: () => ({}),
        },
    },
    setup(props, { expose }) {
        const chartEl = ref<HTMLDivElement | null>(null);
        const chart = ref<echarts.ECharts | null>(null);
        
        // 响应式数据
        const rankName = ref<string[]>([
            "电气火灾告警",
            "手动告警",
            "环境告警",
            "其它告警",
            "其它告警",
        ]);
        const rankNum = ref<number[]>([3, 2, 4, 8, 8]);

        // 监听 valdata 变化 - 修改此处
        watch(() => props.valdata, (newVal) => {
            if (newVal && Object.keys(newVal).length > 0) {
                nextTick(() => {  // 添加nextTick确保DOM更新完成
                    loadData();  // 改为调用loadData而非直接initChart
                });
            }
        }, { deep: true });

        // 初始化图表 - 修改此处
        const initChart = () => {
            if (!chartEl.value) {
                console.error('图表容器元素不存在');
                return;
            }
            
            // 如果图表已存在，先销毁
            if (chart.value) {
                chart.value.dispose();
            }
            
            chart.value = echarts.init(chartEl.value);
            
            const option: echarts.EChartsOption = {
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        type: "shadow",
                    },
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
                        data: rankNum.value,
                        barWidth: "20%",
                        label: {
                            show: true,
                            position: 'top',
                            formatter: '{c}',
                            textStyle: {
                                color: "#fff",
                                fontSize: 14,
                            },
                        },
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#83bff6' },
                                { offset: 0.5, color: '#188df0' },
                                { offset: 1, color: '#188df0' }
                            ])
                        },
                        emphasis: {
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#2378f7' },
                                    { offset: 0.7, color: '#2378f7' },
                                    { offset: 1, color: '#83bff6' }
                                ])
                            }
                        }
                    },
                ],
            };

            chart.value.setOption(option);
            
            // 添加窗口大小变化时的自适应
            const handleResize = () => {
                if (chart.value) {
                    chart.value.resize();
                }
            };
            
            window.addEventListener('resize', handleResize);
            
            // 清理函数
            return () => {
                window.removeEventListener('resize', handleResize);
            };
        };

        // 加载数据 - 修改此处
        const loadData = async () => {
            try {
                // 从 mixin 中获取数据（通过组件实例）
                const componentInstance = getCurrentInstance();
                if (!componentInstance) return;
                
                const mixinData = componentInstance.proxy as unknown as CommonMixin;
                const { areaIds, startTime, endTime } = mixinData;
                
                // 检查必要参数是否存在
                if (!areaIds || areaIds.length === 0) {
                    console.warn('区域ID为空，无法加载数据');
                    initChart(); // 使用默认数据
                    return;
                }
                
                if (!startTime || !endTime) {
                    console.warn('时间范围不完整，无法加载数据');
                    initChart(); // 使用默认数据
                    return;
                }
                
                const res = await getWarningSignStat({
                    startTime,
                    endTime,
                    areaIds,
                });
                
                if (res.data && Array.isArray(res.data)) {
                    rankName.value = res.data.map((item: WarningSignItem) => item.NAME);
                    rankNum.value = res.data.map((item: WarningSignItem) => Number(item.VALUE));
                }
                
                // 确保在数据更新后初始化图表
                nextTick(() => {
                    initChart();
                });
            } catch (error) {
                console.error('加载信号告警排行数据失败:', error);
                nextTick(() => {
                    initChart(); // 使用默认数据
                });
            }
        };

        // 组件挂载 - 修改此处
        onMounted(() => {
            nextTick(() => {
                loadData();
            });
        });

        // 组件卸载前清理
        onBeforeUnmount(() => {
            if (chart.value) {
                chart.value.dispose();
                chart.value = null;
            }
        });

        // 暴露方法给父组件（如果需要）
        expose({
            refreshChart: loadData,
            getChartInstance: () => chart.value,
        });

        return {
            chartEl,
            rankName,
            rankNum,
            initChart,
            loadData,
        };
    },
    
    // Vue 2 选项式 API 兼容（如果需要）
    mounted() {
        // 这里可以留空，因为已经在 setup 的 onMounted 中处理
    },
    
    beforeDestroy() {
        // 这里可以留空，因为已经在 setup 的 onBeforeUnmount 中处理
    },
    
    methods: {
        // 如果需要暴露给模板的方法
        refreshData() {
            this.loadData();
        }
    }
});
</script>

<style scoped>
.chart {
    min-height: 300px;
}
</style>