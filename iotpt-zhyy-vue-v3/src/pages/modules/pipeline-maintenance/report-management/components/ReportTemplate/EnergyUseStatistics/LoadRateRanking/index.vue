<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc" style="margin-bottom: 10px">
            本监测周期内，负载率超过85%的区域有
            <span class="num">{{ detailInfo.excellentCount }}</span>
            户，负载率70-85%的区域有
            <span class="num">{{ detailInfo.good }}</span>
            户。
        </div>
        <ElTable
            :data="tableData" 
            border 
            stripe
            :row-class-name="tableRowClassName"
            style="width: 100%"
            @row-click="handleRowClick"
        >
            <ElTableColumn 
                type="index" 
                label="排序" 
                width="70" 
                align="center"
                :index="indexMethod"
            >
                <template #default="scope">
                    <span :class="getRankClass($index)">
                        {{ $index + 1 }}
                    </span>
                </template>
            </ElTableColumn>
            <ElTableColumn 
                label="设备名称" 
                prop="name" 
                min-width="180"
                show-overflow-tooltip
            >
                <template #default="scope">
                    <div class="device-name">
                        <ElIcon v-if="scope.row.value >= 85" class="warning-icon">
                            <Warning />
                        </ElIcon>
                        {{ scope.row.name }}
                    </div>
                </template>
            </ElTableColumn>
            <ElTableColumn 
                label="区域名称" 
                prop="areaName" 
                min-width="120"
                show-overflow-tooltip
            ></ElTableColumn>
            <ElTableColumn 
                label="所属街道" 
                prop="floorName" 
                min-width="100"
                show-overflow-tooltip
            ></ElTableColumn>
            <ElTableColumn 
                label="负载率" 
                prop="value" 
                width="120"
                sortable
                :sort-method="sortByLoadRate"
            >
                <template #default="scope">
                    <span :class="getLoadRateClass(scope.row.value)">
                        {{ formatLoadRate(scope.row.value) }}
                    </span>
                </template>
            </ElTableColumn>
            <ElTableColumn 
                label="负载等级" 
                width="100"
            >
                <template #default="scope">
                    <ElTag 
                        :type="getLoadLevelTagType(scope.row.value)" 
                        size="small"
                        effect="light"
                    >
                        {{ getLoadLevel(scope.row.value) }}
                    </ElTag>
                </template>
            </ElTableColumn>
            <ElTableColumn 
                label="操作" 
                width="80"
                align="center"
            >
                <template #default="scope">
                    <ElButton
                        type="primary" 
                        link
                        @click.stop="handleDetailClick(scope.row)"
                    >
                        详情
                    </ElButton>
                </template>
            </ElTableColumn>
        </ElTable>
        
        <!-- 分页器 -->
        <div class="pagination-wrapper" v-if="showPagination">
            <ElPagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="totalItems"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, getCurrentInstance } from "vue";
import type { PropType } from "vue";
import { Warning } from '@element-plus/icons-vue';
import Common from "../../common.js";
import { getDeviceCollectTop } from "@/api/pipeline-maintenance/report-management";
import type { ElTable, ElTableColumn, ElPagination, ElTag, ElButton, ElIcon  } from 'element-plus';
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";

// 定义数据类型
interface DeviceLoadItem {
    areaName: string;
    name: string;
    floorName: string;
    value: string | number;
    [key: string]: any;
}

interface DetailInfo {
    good: number;
    excellentCount: number;
    data: DeviceLoadItem[];
}

interface DeviceCollectTopResponse {
    code: number;
    message: string;
    data: {
        goodCount: number;
        excellentCount: number;
        data: DeviceLoadItem[];
        total?: number;
        [key: string]: any;
    };
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
    cname: "负载率排名TOP10",
    name: "LoadRateRanking",
    mixins: [Common] as any,
    components: {
        Warning,
        Title
    },
    props: {
        // 是否显示分页器
        showPagination: {
            type: Boolean as PropType<boolean>,
            default: false
        },
        // 默认显示条数
        defaultPageSize: {
            type: Number as PropType<number>,
            default: 10
        },
        // 是否显示负载等级列
        showLoadLevel: {
            type: Boolean as PropType<boolean>,
            default: true
        },
        // 是否显示详情操作列
        showAction: {
            type: Boolean as PropType<boolean>,
            default: true
        }
    },
    setup(props, { emit }) {
        // Refs
        const currentPage = ref(1);
        const pageSize = ref(props.defaultPageSize);
        const totalItems = ref(0);
        
        // 响应式数据
        const detailInfo = ref<DetailInfo>({
            good: 10,
            excellentCount: 34,
            data: [
                {
                    areaName: "驿城达明五金杂品",
                    name: "驿城达明五金杂品-智能微断001号",
                    floorName: "三堡街道",
                    value: "14.2"
                },
                {
                    areaName: "驿城慈佑烟酒店",
                    name: "驿城慈佑烟酒店-智能微断001号",
                    floorName: "泰山街道",
                    value: "12.3"
                },
                {
                    areaName: "小马哥熟食",
                    name: "小马哥熟食-智能微断001号",
                    floorName: "新区街道",
                    value: "11.7"
                },
                {
                    areaName: "驿城甜蜜蜜",
                    name: "驿城甜蜜蜜-智能微断001号",
                    floorName: "泰山街道",
                    value: "10.2"
                },
                {
                    areaName: "驿城蔷薇衣橱",
                    name: "驿城蔷薇衣橱-智能微断001号",
                    floorName: "新区街道",
                    value: "8.3"
                },
                {
                    areaName: "驿城黄老邪烧饼",
                    name: "驿城黄老邪烧饼-智能微断001号",
                    floorName: "泰山街道",
                    value: "5.1"
                },
                {
                    areaName: "隆庆批发超市",
                    name: "隆庆批发超市-智能微断001号",
                    floorName: "三堡街道",
                    value: "2.3"
                },
                {
                    areaName: "欧派",
                    name: "欧派-智能微断001号",
                    floorName: "新区街道",
                    value: "2.1"
                },
                {
                    areaName: "千艺发型",
                    name: "千艺发型-智能微断001号",
                    floorName: "二堡街道",
                    value: "1.3"
                },
                {
                    areaName: "药店",
                    name: "药店-智能微断001号",
                    floorName: "新区街道",
                    value: "0.4"
                }
            ]
        });

        // 计算属性
        const tableData = computed(() => {
            const data = detailInfo.value.data || [];
            if (!props.showPagination) {
                return data.slice(0, props.defaultPageSize);
            }
            
            const start = (currentPage.value - 1) * pageSize.value;
            const end = start + pageSize.value;
            console.log('tableDatatableDatatableData', data);
            return data.slice(start, end);
        });
        

        // 格式化负载率
        const formatLoadRate = (value: string | number): string => {
            const num = typeof value === 'string' ? parseFloat(value) : value;
            return isNaN(num) ? '0%' : `${num.toFixed(2)}%`;
        };

        // 获取负载率数值
        const getLoadRateValue = (value: string | number): number => {
            const num = typeof value === 'string' ? parseFloat(value) : value;
            return isNaN(num) ? 0 : num;
        };

        // 获取负载率等级
        const getLoadLevel = (value: string | number): string => {
            const rate = getLoadRateValue(value);
            if (rate >= 85) return '过高';
            if (rate >= 70) return '偏高';
            if (rate >= 40) return '正常';
            if (rate >= 20) return '偏低';
            return '过低';
        };

        // 获取负载率等级对应的标签类型
        const getLoadLevelTagType = (value: string | number): string => {
            const rate = getLoadRateValue(value);
            if (rate >= 85) return 'danger';
            if (rate >= 70) return 'warning';
            if (rate >= 40) return 'success';
            if (rate >= 20) return 'info';
            return '';
        };

        // 获取负载率对应的CSS类名
        const getLoadRateClass = (value: string | number): string => {
            const rate = getLoadRateValue(value);
            if (rate >= 85) return 'load-rate-high';
            if (rate >= 70) return 'load-rate-medium';
            if (rate >= 40) return 'load-rate-normal';
            if (rate >= 20) return 'load-rate-low';
            return 'load-rate-very-low';
        };

        // 获取排名CSS类名
        const getRankClass = (index: number): string => {
            if (index === 0) return 'rank-first';
            if (index === 1) return 'rank-second';
            if (index === 2) return 'rank-third';
            return 'rank-normal';
        };

        // 表格行类名
        const tableRowClassName = ({ row, rowIndex }: { row: DeviceLoadItem; rowIndex: number }) => {
            const rate = getLoadRateValue(row.value);
            if (rate >= 85) return 'warning-row';
            if (rate >= 70) return 'info-row';
            return '';
        };

        // 自定义排序方法
        const sortByLoadRate = (a: DeviceLoadItem, b: DeviceLoadItem): number => {
            return getLoadRateValue(a.value) - getLoadRateValue(b.value);
        };

        // 自定义索引方法
        const indexMethod = (index: number): number => {
            if (!props.showPagination) return index + 1;
            return (currentPage.value - 1) * pageSize.value + index + 1;
        };

        // 事件处理
        const handleRowClick = (row: DeviceLoadItem, column: TableColumnCtx<DeviceLoadItem>, event: Event) => {
            emit('row-click', row, column, event);
        };

        const handleDetailClick = (row: DeviceLoadItem) => {
            emit('detail-click', row);
        };

        const handleSizeChange = (val: number) => {
            pageSize.value = val;
            currentPage.value = 1;
            emit('page-size-change', val);
        };

        const handleCurrentChange = (val: number) => {
            currentPage.value = val;
            emit('current-page-change', val);
        };

        // 加载数据
        const loadData = async (): Promise<void> => {
            try {
                const mixinData = (getCurrentInstance()?.proxy as any) as CommonMixinData;
                
                if (!mixinData.areaIds || mixinData.areaIds.length === 0) {
                    return;
                }
                
                const response = await getDeviceCollectTop({
                    areaIds: mixinData.areaIds,
                    startTime: mixinData.startTime,
                    endTime: mixinData.endTime,
                    timeType: mixinData.timeTypes,
                    splType: mixinData.type
                }) as DeviceCollectTopResponse;
                console.log('9999999999999detailInfo', response)
                if (response.data) {
                    detailInfo.value = {
                        good: response.data.goodCount || 0,
                        excellentCount: response.data.excellentCount || 0,
                        data: response.data.data || []
                    };
                    totalItems.value = response.data.total || response.data.data.length || 0;
                }
            } catch (error) {
                console.error('Failed to load device collect top data:', error);
            }
        };

        // 刷新数据
        const refreshData = async (): Promise<void> => {
            await loadData();
        };

        // 获取统计数据
        const getStatistics = () => {
            const data = detailInfo.value.data || [];
            const total = data.length;
            const highLoadCount = data.filter(item => getLoadRateValue(item.value) >= 85).length;
            const mediumLoadCount = data.filter(item => 
                getLoadRateValue(item.value) >= 70 && getLoadRateValue(item.value) < 85
            ).length;
            const averageLoad = data.reduce((sum, item) => 
                sum + getLoadRateValue(item.value), 0) / Math.max(1, total);
            
            return {
                total,
                highLoadCount,
                mediumLoadCount,
                lowLoadCount: total - highLoadCount - mediumLoadCount,
                averageLoad: averageLoad.toFixed(2) + '%',
                maxLoad: Math.max(...data.map(item => getLoadRateValue(item.value))).toFixed(2) + '%',
                minLoad: Math.min(...data.map(item => getLoadRateValue(item.value))).toFixed(2) + '%'
            };
        };

        // 组件挂载
        onMounted(async () => {
            await loadData();
        });

        // 暴露给父组件的方法
        return {
            detailInfo,
            tableData,
            currentPage,
            pageSize,
            totalItems,
            formatLoadRate,
            getLoadLevel,
            getLoadLevelTagType,
            getLoadRateClass,
            getRankClass,
            tableRowClassName,
            sortByLoadRate,
            indexMethod,
            handleRowClick,
            handleDetailClick,
            handleSizeChange,
            handleCurrentChange,
            loadData,
            refreshData,
            getStatistics
        };
    },
    
    // 计算属性
    computed: {
        // 负载率统计
        loadRateStats() {
            const total = detailInfo.value.data?.length || 0;
            const highLoad = detailInfo.value.excellentCount || 0;
            const mediumLoad = detailInfo.value.good || 0;
            const lowLoad = total - highLoad - mediumLoad;
            
            return {
                total,
                highLoad,
                mediumLoad,
                lowLoad,
                highLoadPercent: total > 0 ? ((highLoad / total) * 100).toFixed(1) + '%' : '0%',
                mediumLoadPercent: total > 0 ? ((mediumLoad / total) * 100).toFixed(1) + '%' : '0%',
                lowLoadPercent: total > 0 ? ((lowLoad / total) * 100).toFixed(1) + '%' : '0%'
            };
        },
        
        // 街道分布
        streetDistribution() {
            const data = detailInfo.value.data || [];
            const distribution: Record<string, number> = {};
            
            data.forEach(item => {
                const street = item.floorName || '未知街道';
                distribution[street] = (distribution[street] || 0) + 1;
            });
            
            return Object.entries(distribution)
                .map(([street, count]) => ({ street, count }))
                .sort((a, b) => b.count - a.count);
        }
    }
});
</script>

<style scoped>
.module-item-desc {
    margin-bottom: 20px;
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 8px;
    font-size: 14px;
    line-height: 1.6;
    color: #555;
    border-left: 4px solid #027dc5;
}

.num {
    font-weight: bold;
    color: #027dc5;
    font-size: 16px;
    margin: 0 4px;
    transition: color 0.3s ease;
}

.num:hover {
    color: #025a99;
}

/* 表格样式 */
.device-name {
    display: flex;
    align-items: center;
    gap: 8px;
}

.warning-icon {
    color: #e6a23c;
    font-size: 16px;
}

/* 负载率颜色 */
.load-rate-high {
    color: #f56c6c;
    font-weight: bold;
}

.load-rate-medium {
    color: #e6a23c;
    font-weight: bold;
}

.load-rate-normal {
    color: #67c23a;
    font-weight: bold;
}

.load-rate-low {
    color: #909399;
}

.load-rate-very-low {
    color: #c0c4cc;
}

/* 排名样式 */
.rank-first {
    color: #ffd700;
    font-weight: bold;
    font-size: 16px;
}

.rank-second {
    color: #c0c0c0;
    font-weight: bold;
    font-size: 15px;
}

.rank-third {
    color: #cd7f32;
    font-weight: bold;
    font-size: 14px;
}

.rank-normal {
    color: #666;
}

/* 表格行样式 */
:deep(.warning-row) {
    background-color: #fff2f2 !important;
}

:deep(.info-row) {
    background-color: #f9f2ff !important;
}

/* 分页器样式 */
.pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .module-item-desc {
        padding: 12px;
        font-size: 13px;
    }
    
    .num {
        font-size: 14px;
    }
    
    :deep(.el-table) {
        font-size: 12px;
    }
    
    :deep(.el-table__cell) {
        padding: 8px 0;
    }
}
</style>