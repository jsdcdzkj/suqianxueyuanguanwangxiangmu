<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">
            本周期内，电流三相不平衡最大占比区间为
            <span class="num">4%以下(正常区间)</span>
            ，占比为
            <span class="num">{{ obj.zb }}</span>
            ，不平衡程度的最大值为
            <span class="num">{{ obj.value }}</span>
            。
        </div>
        <div class="module-item-desc">
            电压三相不平衡最大占比区间为
            <span class="num">4%以下(正常区间)</span>
            ，占比为
            <span class="num">{{ obj2.zb }}</span>
            ，不平衡程度的最大值为
            <span class="num">{{ obj2.value }}</span>
            。
        </div>
        <div ref="chartEl" style="width: 100%; height: 240px"></div>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onBeforeUnmount, nextTick, getCurrentInstance } from "vue";
import * as echarts from "echarts";
import type { ECharts, EChartsOption, DatasetComponentOption, PieSeriesOption } from "echarts";
import Common from "../../common.js";
import { getDeviceCollectList, tripartiteImbalanceReport } from "@/api/pipeline-maintenance/report-management";
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";

// 定义数据类型
interface ImbalanceDataItem {
    zb: string;
    value: string | number;
    name?: string;
}

interface DeviceItem {
    id: string | number;
    [key: string]: any;
}

interface TripartiteResponse {
    code: number;
    message: string;
    data: {
        vo: ImbalanceDataItem[];
    };
}

interface ChartSourceItem extends Array<any> {
    0: string;  // 区间名称
    1: number;  // 电流值
    2: number;  // 电压值
}

interface CommonMixinData {
    areaIds: string[] | number[];
    startTime: string;
    endTime: string;
    timeTypes: string;
    [key: string]: any;
}

export default defineComponent({
    cname: "三相不平衡分析",
    name: "ThreeUnbalancedAnalyses",
    mixins: [Common] as any,
    components: {
        Title
    },

    setup(props, { expose }) {
        // Refs
        const chartEl = ref<HTMLDivElement | null>(null);
        const chartInstance = ref<ECharts | null>(null);
        
        // 响应式数据
        const source = ref<ChartSourceItem[]>([
            ["4%以下(正常区间)", 86.5, 92.1],
            ["4%-6%", 41.1, 30.4],
            ["6%-8%", 24.1, 67.2],
            ["8%-10%", 55.2, 67.1],
            ["10%-12%", 55.2, 67.1],
            ["12%以上", 55.2, 67.1]
        ]);
        
        const obj = ref<ImbalanceDataItem>({
            zb: "100%",
            value: "1000"
        });
        
        const obj2 = ref<ImbalanceDataItem>({
            zb: "100%",
            value: "1000"
        });

        // 颜色配置
        const colorPalette = [
            '#91cc75', // 绿色 - 正常区间
            '#fac858', // 黄色 - 轻微不平衡
            '#ee6666', // 红色 - 中度不平衡
            '#5470c6', // 蓝色
            '#73c0de', // 浅蓝
            '#3ba272'  // 深绿
        ];

        // 初始化图表
        const initCharts = async (): Promise<void> => {
            await nextTick();
            
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
            
            // 计算总数用于百分比显示
            const currentTotal = source.value.reduce((sum, item) => sum + (item[1] || 0), 0);
            const voltageTotal = source.value.reduce((sum, item) => sum + (item[2] || 0), 0);
            
            const option: EChartsOption = {
                backgroundColor: '#fff',
                legend: {
                    top: "10px",
                    textStyle: {
                        fontSize: 12,
                        color: '#666'
                    },
                    data: ['电流不平衡', '电压不平衡']
                },
                tooltip: {
                    trigger: 'item',
                    formatter: (params: any) => {
                        if (params.seriesIndex === 0) {
                            const percent = ((params.value as number) / currentTotal * 100).toFixed(1);
                            return `${params.name}<br/>电流: ${params.value} (${percent}%)`;
                        } else {
                            const percent = ((params.value as number) / voltageTotal * 100).toFixed(1);
                            return `${params.name}<br/>电压: ${params.value} (${percent}%)`;
                        }
                    },
                    backgroundColor: 'rgba(50, 50, 50, 0.9)',
                    borderColor: 'rgba(255, 255, 255, 0.2)',
                    textStyle: {
                        color: '#fff'
                    }
                },
                dataset: {
                    source: source.value
                } as DatasetComponentOption,
                series: [
                    {
                        name: '电流不平衡',
                        type: "pie",
                        radius: ["40%", "55%"],
                        center: ["25%", "50%"],
                        encode: {
                            itemName: 0,
                            value: 1
                        },
                        label: {
                            show: true,
                            formatter: '{b}: {d}%',
                            fontSize: 12
                        },
                        labelLine: {
                            length: 10,
                            length2: 5
                        },
                        itemStyle: {
                            borderColor: '#fff',
                            borderWidth: 2
                        },
                        color: colorPalette,
                        emphasis: {
                            scale: true,
                            scaleSize: 5,
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    } as PieSeriesOption,
                    {
                        name: '电压不平衡',
                        type: "pie",
                        radius: ["40%", "55%"],
                        center: ["75%", "50%"],
                        encode: {
                            itemName: 0,
                            value: 2
                        },
                        label: {
                            show: true,
                            formatter: '{b}: {d}%',
                            fontSize: 12
                        },
                        labelLine: {
                            length: 10,
                            length2: 5
                        },
                        itemStyle: {
                            borderColor: '#fff',
                            borderWidth: 2
                        },
                        color: colorPalette,
                        emphasis: {
                            scale: true,
                            scaleSize: 5,
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    } as PieSeriesOption
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
                    await initCharts();
                    return;
                }
                
                // 获取设备列表
                const devList = await getDeviceCollectList({ areaIds: mixinData.areaIds });
                const deviceIds = devList.map((item: DeviceItem) => item.id);
                
                // 并行请求电流和电压不平衡数据
                const [currentRes, voltageRes] = await Promise.all([
                    tripartiteImbalanceReport({
                        startTime: mixinData.startTime,
                        endTime: mixinData.endTime,
                        timeType: mixinData.timeTypes,
                        deviceType: 1,
                        deviceIds
                    }) as Promise<TripartiteResponse>,
                    tripartiteImbalanceReport({
                        startTime: mixinData.startTime,
                        endTime: mixinData.endTime,
                        timeType: mixinData.timeTypes,
                        deviceType: 2,
                        deviceIds
                    }) as Promise<TripartiteResponse>
                ]);
                
                // 更新数据源
                if (currentRes.data?.vo && voltageRes.data?.vo) {
                    source.value.forEach((item, index) => {
                        if (currentRes.data.vo[index]) {
                            item[1] = Number(currentRes.data.vo[index].value) || 0;
                        }
                        if (voltageRes.data.vo[index]) {
                            item[2] = Number(voltageRes.data.vo[index].value) || 0;
                        }
                    });
                    
                    // 更新统计信息
                    if (currentRes.data.vo[0]) {
                        obj.value = {
                            zb: currentRes.data.vo[0].zb || "0%",
                            value: currentRes.data.vo[0].value || "0"
                        };
                    }
                    
                    if (voltageRes.data.vo[0]) {
                        obj2.value = {
                            zb: voltageRes.data.vo[0].zb || "0%",
                            value: voltageRes.data.vo[0].value || "0"
                        };
                    }
                }
                
                await initCharts();
            } catch (error) {
                console.error('Failed to load three-phase imbalance data:', error);
                // 使用默认数据
                await initCharts();
            }
        };

        // 刷新数据
        const refreshData = async (): Promise<void> => {
            await loadData();
        };

        // 更新图表数据
        const updateChartData = (newSource: ChartSourceItem[], newObj?: ImbalanceDataItem, newObj2?: ImbalanceDataItem): void => {
            source.value = newSource;
            if (newObj) obj.value = newObj;
            if (newObj2) obj2.value = newObj2;
            initCharts();
        };

        // 组件挂载
        onMounted(async () => {
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
                source: source.value,
                obj: obj.value,
                obj2: obj2.value
            })
        });

        return {
            chartEl,
            source,
            obj,
            obj2,
            initCharts,
            loadData,
            refreshData
        };
    },
    
    // 计算属性
    computed: {
        // 格式化显示值
        formattedObj() {
            return {
                zb: this.obj.zb.endsWith('%') ? this.obj.zb : `${this.obj.zb}%`,
                value: this.obj.value
            };
        },
        formattedObj2() {
            return {
                zb: this.obj2.zb.endsWith('%') ? this.obj2.zb : `${this.obj2.zb}%`,
                value: this.obj2.value
            };
        },
        // 电流不平衡数据统计
        currentStats() {
            const total = this.source.reduce((sum, item) => sum + (item[1] || 0), 0);
            const normalValue = this.source[0] ? this.source[0][1] : 0;
            const normalPercent = total > 0 ? (normalValue / total * 100).toFixed(1) : '0';
            return {
                total,
                normalValue,
                normalPercent: `${normalPercent}%`
            };
        },
        // 电压不平衡数据统计
        voltageStats() {
            const total = this.source.reduce((sum, item) => sum + (item[2] || 0), 0);
            const normalValue = this.source[0] ? this.source[0][2] : 0;
            const normalPercent = total > 0 ? (normalValue / total * 100).toFixed(1) : '0';
            return {
                total,
                normalValue,
                normalPercent: `${normalPercent}%`
            };
        }
    }
});
</script>

<style scoped>
/* .module-item-desc {
    margin-bottom: 16px;
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 8px;
    font-size: 14px;
    line-height: 1.6;
    color: #666;
    border-left: 4px solid #1890ff;
} */

.module-item-desc:first-of-type {
    border-left-color: #52c41a; /* 绿色表示电流 */
}

.module-item-desc:last-of-type {
    border-left-color: #fa8c16; /* 橙色表示电压 */
}

.num {
    font-weight: bold;
    color: #1890ff;
    font-size: 16px;
    margin: 0 4px;
}
</style>