<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">
            本周期内，共有 
            <span class="num">{{dataInfo.errorTaskTotal}}</span>
            条异常记录，其中
            <span class="num">{{dataInfo.yErrorTaskTotal}}</span>
            条已处理完成，
            <span class="num">{{dataInfo.unErrorTaskTotal}}</span>
            条未处理，请分析未处理的原因并及时处理。
        </div>
        
        <!-- 统计卡片 -->
        <div class="statistics-cards" v-if="showStatistics">
            <el-row :gutter="20">
                <el-col :span="8">
                    <div class="stat-card total-card">
                        <div class="stat-icon">
                            <el-icon><Warning /></el-icon>
                        </div>
                        <div class="stat-content">
                            <div class="stat-value">{{ dataInfo.errorTaskTotal }}</div>
                            <div class="stat-label">异常总数</div>
                        </div>
                    </div>
                </el-col>
                <el-col :span="8">
                    <div class="stat-card processed-card">
                        <div class="stat-icon">
                            <el-icon><CircleCheck /></el-icon>
                        </div>
                        <div class="stat-content">
                            <div class="stat-value">{{ dataInfo.yErrorTaskTotal }}</div>
                            <div class="stat-label">已处理</div>
                        </div>
                    </div>
                </el-col>
                <el-col :span="8">
                    <div class="stat-card pending-card">
                        <div class="stat-icon">
                            <el-icon><Clock /></el-icon>
                        </div>
                        <div class="stat-content">
                            <div class="stat-value">{{ dataInfo.unErrorTaskTotal }}</div>
                            <div class="stat-label">未处理</div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 表格 -->
        <el-table 
            :data="tableData" 
            border 
            stripe
            :row-class-name="tableRowClassName"
            style="width: 100%; margin-top: 20px;"
            @row-click="handleRowClick"
            @sort-change="handleSortChange"
        >
            <el-table-column 
                label="序号" 
                width="70" 
                align="center"
            >
                <template #default="scope">
                    {{ (currentPage - 1) * pageSize + $index + 1 }}
                </template>
            </el-table-column>
            <el-table-column 
                label="任务名称" 
                prop="title"
                min-width="150"
                show-overflow-tooltip
            >
                <template #default="scope">
                    <div class="task-title">
                        <el-tag 
                            v-if="scope.row.priority" 
                            :type="getPriorityTagType(scope.row.priority)"
                            size="small"
                            effect="plain"
                        >
                            {{ getPriorityText(scope.row.priority) }}
                        </el-tag>
                        {{ scope.row.title }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column 
                label="巡检人" 
                prop="createUserName"
                width="100"
            ></el-table-column>
            <el-table-column 
                label="异常类型" 
                prop="notes"
                width="120"
            >
                <template #default="scope">
                    <el-tag 
                        :type="getErrorTypeTagType(scope.row.notes)"
                        size="small"
                    >
                        {{ scope.row.notes }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column 
                label="异常内容" 
                prop="description"
                min-width="180"
                show-overflow-tooltip
            >
                <template #default="scope">
                    <div class="error-content">
                        <span>{{ scope.row.description || scope.row.notes }}</span>
                        <el-button 
                            v-if="scope.row.attachmentCount && scope.row.attachmentCount > 0"
                            type="text"
                            size="small"
                            @click.stop="handleViewAttachments(scope.row)"
                        >
                            查看附件({{ scope.row.attachmentCount }})
                        </el-button>
                    </div>
                </template>
            </el-table-column>
            <el-table-column 
                label="上报时间" 
                prop="createTime"
                width="160"
                sortable
                :sort-orders="['ascending', 'descending']"
            >
                <template #default="scope">
                    <div class="create-time">
                        <el-icon><Calendar /></el-icon>
                        {{ formatDateTime(scope.row.createTime) }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column 
                label="处理时限" 
                prop="deadline"
                width="160"
            >
                <template #default="scope">
                    <div :class="getDeadlineClass(scope.row)">
                        <el-icon><Timer /></el-icon>
                        {{ formatDateTime(scope.row.deadline) || '--' }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column 
                label="异常状态" 
                prop="statesName"
                width="100"
            >
                <template #default="scope">
                    <el-tag 
                        :type="getStatusTagType(scope.row.statesName)"
                        effect="light"
                        size="small"
                    >
                        {{ scope.row.statesName }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column 
                label="操作" 
                width="120"
                align="center"
                v-if="showActions"
            >
                <template #default="scope">
                    <div class="action-buttons">
                        <el-button 
                            type="primary" 
                            link
                            size="small"
                            @click.stop="handleViewDetail(scope.row)"
                        >
                            详情
                        </el-button>
                        <el-button 
                            v-if="scope.row.statesName === '未处理'"
                            type="success" 
                            link
                            size="small"
                            @click.stop="handleProcess(scope.row)"
                        >
                            处理
                        </el-button>
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页器 -->
        <div class="pagination-wrapper" v-if="showPagination">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="totalItems"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>

        <!-- 异常处理统计 -->
        <div class="analysis-section" v-if="showAnalysis">
            <h3>异常处理分析</h3>
            <el-row :gutter="20">
                <el-col :span="12">
                    <div class="analysis-item">
                        <div class="analysis-label">处理率</div>
                        <div class="analysis-value">
                            <el-progress 
                                :percentage="processingRate" 
                                :color="getProgressColor(processingRate)"
                                :show-text="false"
                            />
                            <span class="percentage">{{ processingRate }}%</span>
                        </div>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="analysis-item">
                        <div class="analysis-label">平均响应时间</div>
                        <div class="analysis-value">{{ averageResponseTime }}</div>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script lang="ts">
import { 
    defineComponent, 
    ref, 
    computed, 
    onMounted, 
    onBeforeUnmount,
    getCurrentInstance,
    type PropType 
} from "vue";
import type { 
    ComponentInternalInstance, 
    ComputedRef,
    Ref 
} from "vue";
import type { 
    TableColumnCtx, 
    TableRowClassName,
    SortProps 
} from "element-plus";
import { 
    Warning, 
    CircleCheck, 
    Clock, 
    Calendar, 
    Timer 
} from '@element-plus/icons-vue';
import Common from "../../common.js";
import { taskErrorInfo } from "@/api/pipeline-maintenance/report-management";
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";

// 定义数据类型
interface ErrorTask {
    id?: string | number;
    title: string;
    createUserName: string;
    notes: string;
    createTime: string;
    statesName: string;
    description?: string;
    priority?: 1 | 2 | 3; // 1: 高, 2: 中, 3: 低
    deadline?: string;
    attachmentCount?: number;
    processTime?: string;
    [key: string]: any;
}

interface DataInfo {
    errorTaskTotal: number;
    yErrorTaskTotal: number;
    unErrorTaskTotal: number;
    allErrorTask: ErrorTask[];
    [key: string]: any;
}

interface TaskErrorInfoResponse {
    code: number;
    message: string;
    data: DataInfo;
}

interface CommonMixinData {
    areaIds: string[] | number[];
    startTime: string;
    endTime: string;
    timeTypes: string;
    type: string | number;
    [key: string]: any;
}

interface SortChangeParams {
    column: TableColumnCtx<ErrorTask>;
    prop: string;
    order: 'ascending' | 'descending' | null;
}

interface Statistics {
    total: number;
    processed: number;
    pending: number;
    processingRate: number;
    userStats: Array<{ user: string; count: number }>;
    typeStats: Array<{ type: string; count: number }>;
    timeStats: Array<{ period: string; count: number }>;
    overdueCount: number;
}

export default defineComponent({
    cname: "巡检记录",
    name: "Records",
    mixins: [Common] as any,
    components: {
        Warning,
        CircleCheck,
        Clock,
        Calendar,
        Timer,
        Title
    },
    props: {
        // 是否显示统计卡片
        showStatistics: {
            type: Boolean as PropType<boolean>,
            default: true
        },
        // 是否显示分页器
        showPagination: {
            type: Boolean as PropType<boolean>,
            default: false
        },
        // 是否显示操作列
        showActions: {
            type: Boolean as PropType<boolean>,
            default: true
        },
        // 是否显示分析部分
        showAnalysis: {
            type: Boolean as PropType<boolean>,
            default: true
        },
        // 默认显示条数
        defaultPageSize: {
            type: Number as PropType<number>,
            default: 10
        },
        // 是否自动刷新
        autoRefresh: {
            type: Boolean as PropType<boolean>,
            default: false
        },
        // 刷新间隔（秒）
        refreshInterval: {
            type: Number as PropType<number>,
            default: 300
        }
    },
    emits: [
        'row-click',
        'sort-change',
        'view-detail',
        'process-task',
        'view-attachments',
        'page-size-change',
        'current-page-change'
    ],
    setup(props, { emit }) {
        // Refs
        const currentPage = ref<number>(1);
        const pageSize = ref<number>(props.defaultPageSize);
        const totalItems = ref<number>(0);
        const refreshTimer = ref<NodeJS.Timeout | null>(null);
        const dataInfo = ref<DataInfo>({
            errorTaskTotal: 2,
            yErrorTaskTotal: 1,
            unErrorTaskTotal: 1,
            allErrorTask: [
                {
                    title: "巡检计划2023",
                    createUserName: "张建设",
                    notes: "电气火灾告警",
                    createTime: "2024-02-21 14:50",
                    statesName: "已处理",
                    priority: 1,
                    deadline: "2024-02-22 14:50",
                    attachmentCount: 2
                },
                {
                    title: "巡检计划2023",
                    createUserName: "张建设",
                    notes: "电气火灾告警",
                    createTime: "2024-02-21 14:50",
                    statesName: "未处理",
                    priority: 2,
                    deadline: "2024-02-23 14:50",
                    attachmentCount: 1
                }
            ]
        });

        // 计算属性
        const tableData: ComputedRef<ErrorTask[]> = computed(() => {
            const data = dataInfo.value.allErrorTask || [];
            if (!props.showPagination) {
                return data.slice(0, props.defaultPageSize);
            }
            
            const start = (currentPage.value - 1) * pageSize.value;
            const end = start + pageSize.value;
            return data.slice(start, end);
        });

        const processingRate: ComputedRef<number> = computed(() => {
            const total = dataInfo.value.errorTaskTotal;
            const processed = dataInfo.value.yErrorTaskTotal;
            return total > 0 ? Math.round((processed / total) * 100) : 0;
        });

        const averageResponseTime: ComputedRef<string> = computed(() => {
            const processedTasks = dataInfo.value.allErrorTask?.filter(task => 
                task.statesName === '已处理' && task.createTime && task.processTime
            ) || [];
            
            if (processedTasks.length === 0) return '--';
            
            const totalHours = processedTasks.reduce((sum, task) => {
                const createTime = new Date(task.createTime).getTime();
                const processTime = new Date(task.processTime!).getTime();
                const hours = (processTime - createTime) / (1000 * 60 * 60);
                return sum + hours;
            }, 0);
            
            const avgHours = totalHours / processedTasks.length;
            if (avgHours < 1) {
                return `${Math.round(avgHours * 60)}分钟`;
            } else if (avgHours < 24) {
                return `${avgHours.toFixed(1)}小时`;
            } else {
                return `${(avgHours / 24).toFixed(1)}天`;
            }
        });

        // 处理建议
        const processingSuggestion: ComputedRef<string> = computed(() => {
            if (processingRate.value >= 90) return '处理率良好，继续保持！';
            if (processingRate.value >= 70) return '处理率正常，建议关注未处理异常';
            if (processingRate.value >= 50) return '处理率偏低，请及时处理未处理异常';
            return '处理率过低，请立即处理积压异常！';
        });

        // 紧急异常列表
        const urgentTasks: ComputedRef<ErrorTask[]> = computed(() => {
            const tasks = dataInfo.value.allErrorTask || [];
            return tasks
                .filter(task => task.statesName === '未处理' && task.priority === 1)
                .slice(0, 5);
        });

        // 即将超时的任务
        const nearlyOverdueTasks: ComputedRef<ErrorTask[]> = computed(() => {
            const tasks = dataInfo.value.allErrorTask || [];
            const now = new Date().getTime();
            
            return tasks
                .filter(task => {
                    if (task.statesName !== '未处理' || !task.deadline) return false;
                    
                    const deadline = new Date(task.deadline).getTime();
                    const hoursLeft = (deadline - now) / (1000 * 60 * 60);
                    return hoursLeft > 0 && hoursLeft < 24;
                })
                .slice(0, 5);
        });

        // 格式化日期时间
        const formatDateTime = (dateTime: string): string => {
            if (!dateTime) return '--';
            try {
                const date = new Date(dateTime);
                if (isNaN(date.getTime())) return dateTime;
                
                return date.toLocaleString('zh-CN', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false
                }).replace(/\//g, '-');
            } catch {
                return dateTime;
            }
        };

        // 获取优先级标签类型
        const getPriorityTagType = (priority?: number): string => {
            switch (priority) {
                case 1: return 'danger';
                case 2: return 'warning';
                case 3: return 'info';
                default: return '';
            }
        };

        const getPriorityText = (priority?: number): string => {
            switch (priority) {
                case 1: return '高';
                case 2: return '中';
                case 3: return '低';
                default: return '--';
            }
        };

        // 获取异常类型标签类型
        const getErrorTypeTagType = (notes: string): string => {
            const typeMap: Record<string, string> = {
                '电气火灾告警': 'danger',
                '手动告警': 'warning',
                '环境告警': 'info',
                '其它告警': ''
            };
            return typeMap[notes] || '';
        };

        // 获取状态标签类型
        const getStatusTagType = (status: string): string => {
            return status === '已处理' ? 'success' : 'danger';
        };

        // 获取进度条颜色
        const getProgressColor = (percentage: number): string => {
            if (percentage >= 80) return '#67c23a';
            if (percentage >= 60) return '#e6a23c';
            return '#f56c6c';
        };

        // 获取处理时限样式类
        const getDeadlineClass = (task: ErrorTask): string => {
            if (task.statesName === '已处理') return 'deadline-normal';
            
            if (!task.deadline) return 'deadline-normal';
            
            const now = new Date().getTime();
            const deadline = new Date(task.deadline).getTime();
            
            if (isNaN(deadline)) return 'deadline-normal';
            
            const hoursLeft = (deadline - now) / (1000 * 60 * 60);
            
            if (hoursLeft < 0) return 'deadline-overdue';
            if (hoursLeft < 24) return 'deadline-urgent';
            return 'deadline-normal';
        };

        // 表格行类名
        const tableRowClassName: TableRowClassName<ErrorTask> = ({ row }: { row: ErrorTask }) => {
            if (row.statesName === '未处理') {
                if (row.priority === 1) return 'priority-high-row';
                if (row.priority === 2) return 'priority-medium-row';
            }
            return '';
        };

        // 事件处理
        const handleRowClick = (row: ErrorTask): void => {
            emit('row-click', row);
        };

        const handleSortChange = ({ column, prop, order }: SortChangeParams): void => {
            emit('sort-change', { column, prop, order });
        };

        const handleViewDetail = (row: ErrorTask): void => {
            emit('view-detail', row);
        };

        const handleProcess = (row: ErrorTask): void => {
            emit('process-task', row);
        };

        const handleViewAttachments = (row: ErrorTask): void => {
            emit('view-attachments', row);
        };

        const handleSizeChange = (val: number): void => {
            pageSize.value = val;
            currentPage.value = 1;
            emit('page-size-change', val);
        };

        const handleCurrentChange = (val: number): void => {
            currentPage.value = val;
            emit('current-page-change', val);
        };

        // 加载数据
        const loadData = async (): Promise<void> => {
            try {
                const instance = getCurrentInstance() as ComponentInternalInstance;
                const mixinData = instance?.proxy as unknown as CommonMixinData;
                
                if (!mixinData.areaIds?.length) {
                    return;
                }
                
                const response = await taskErrorInfo({
                    areaIds: mixinData.areaIds,
                    startTime: mixinData.startTime,
                    endTime: mixinData.endTime,
                    timeType: mixinData.timeTypes,
                    splType: mixinData.type
                }) as TaskErrorInfoResponse;
                
                if (response) {
                    dataInfo.value = response;
                    totalItems.value = response.allErrorTask?.length || 0;
                }
            } catch (error) {
                console.error('Failed to load task error info:', error);
            }
        };

        // 刷新数据
        const refreshData = async (): Promise<void> => {
            await loadData();
        };

        // 获取统计分析
        const getStatistics = (): Statistics => {
            const allTasks = dataInfo.value.allErrorTask || [];
            const processedTasks = allTasks.filter(task => task.statesName === '已处理');
            const pendingTasks = allTasks.filter(task => task.statesName === '未处理');
            
            // 按巡检人统计
            const userStats: Record<string, number> = {};
            allTasks.forEach(task => {
                const user = task.createUserName || '未知';
                userStats[user] = (userStats[user] || 0) + 1;
            });
            
            // 按异常类型统计
            const typeStats: Record<string, number> = {};
            allTasks.forEach(task => {
                const type = task.notes || '未知';
                typeStats[type] = (typeStats[type] || 0) + 1;
            });
            
            // 按时间段统计
            const timeStats: Record<string, number> = {};
            allTasks.forEach(task => {
                if (task.createTime) {
                    try {
                        const hour = new Date(task.createTime).getHours();
                        const period = hour < 6 ? '凌晨(0-6点)' : 
                                    hour < 12 ? '上午(6-12点)' :
                                    hour < 18 ? '下午(12-18点)' : '晚上(18-24点)';
                        timeStats[period] = (timeStats[period] || 0) + 1;
                    } catch {
                        // 忽略无效日期
                    }
                }
            });
            
            return {
                total: dataInfo.value.errorTaskTotal,
                processed: dataInfo.value.yErrorTaskTotal,
                pending: dataInfo.value.unErrorTaskTotal,
                processingRate: processingRate.value,
                userStats: Object.entries(userStats)
                    .map(([user, count]) => ({ user, count }))
                    .sort((a, b) => b.count - a.count),
                typeStats: Object.entries(typeStats)
                    .map(([type, count]) => ({ type, count }))
                    .sort((a, b) => b.count - a.count),
                timeStats: Object.entries(timeStats)
                    .map(([period, count]) => ({ period, count }))
                    .sort((a, b) => b.count - a.count),
                overdueCount: pendingTasks.filter(task => {
                    if (!task.deadline) return false;
                    try {
                        return new Date(task.deadline).getTime() < new Date().getTime();
                    } catch {
                        return false;
                    }
                }).length
            };
        };

        // 启动自动刷新
        const startAutoRefresh = (): void => {
            if (props.autoRefresh && props.refreshInterval > 0) {
                refreshTimer.value = setInterval(async () => {
                    await loadData();
                }, props.refreshInterval * 1000);
            }
        };

        // 停止自动刷新
        const stopAutoRefresh = (): void => {
            if (refreshTimer.value) {
                clearInterval(refreshTimer.value);
                refreshTimer.value = null;
            }
        };

        // 组件挂载
        onMounted(async () => {
            await loadData();
            startAutoRefresh();
        });

        // 组件卸载
        onBeforeUnmount(() => {
            stopAutoRefresh();
        });

        // 暴露给父组件的方法
        return {
            dataInfo,
            tableData,
            currentPage,
            pageSize,
            totalItems,
            processingRate,
            averageResponseTime,
            processingSuggestion,
            urgentTasks,
            nearlyOverdueTasks,
            formatDateTime,
            getPriorityTagType,
            getPriorityText,
            getErrorTypeTagType,
            getStatusTagType,
            getProgressColor,
            getDeadlineClass,
            tableRowClassName,
            handleRowClick,
            handleSortChange,
            handleViewDetail,
            handleProcess,
            handleViewAttachments,
            handleSizeChange,
            handleCurrentChange,
            loadData,
            refreshData,
            getStatistics,
            startAutoRefresh,
            stopAutoRefresh
        };
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
    border-left: 4px solid #e6a23c;
}

.num {
    font-weight: bold;
    color: #e6a23c;
    font-size: 16px;
    margin: 0 4px;
    transition: color 0.3s ease;
}

.num:hover {
    color: #cf9236;
}

/* 统计卡片样式 */
.statistics-cards {
    margin-bottom: 20px;
}

.stat-card {
    display: flex;
    align-items: center;
    padding: 20px;
    border-radius: 8px;
    color: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-icon {
    font-size: 36px;
    margin-right: 20px;
    opacity: 0.8;
}

.stat-content {
    flex: 1;
}

.stat-value {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 8px;
}

.stat-label {
    font-size: 14px;
    opacity: 0.9;
}

.total-card {
    background: linear-gradient(135deg, #f56c6c, #e84b4b);
}

.processed-card {
    background: linear-gradient(135deg, #67c23a, #529b2e);
}

.pending-card {
    background: linear-gradient(135deg, #e6a23c, #d4882a);
}

/* 表格样式 */
.task-title {
    display: flex;
    align-items: center;
    gap: 8px;
}

.error-content {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.create-time, .deadline-normal, .deadline-urgent, .deadline-overdue {
    display: flex;
    align-items: center;
    gap: 6px;
}

.deadline-urgent {
    color: #e6a23c;
    font-weight: bold;
}

.deadline-overdue {
    color: #f56c6c;
    font-weight: bold;
}

.action-buttons {
    display: flex;
    gap: 8px;
    justify-content: center;
}

/* 表格行样式 */
:deep(.priority-high-row) {
    background-color: #fff2f2 !important;
}

:deep(.priority-medium-row) {
    background-color: #fdf6ec !important;
}

/* 分页器样式 */
.pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

/* 分析部分样式 */
.analysis-section {
    margin-top: 30px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;
}

.analysis-section h3 {
    margin-top: 0;
    margin-bottom: 20px;
    color: #333;
    font-size: 16px;
}

.analysis-item {
    margin-bottom: 15px;
}

.analysis-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
}

.analysis-value {
    display: flex;
    align-items: center;
    gap: 12px;
}

.analysis-value .percentage {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    min-width: 50px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .statistics-cards .el-col {
        margin-bottom: 15px;
    }
    
    .stat-card {
        padding: 15px;
    }
    
    .stat-icon {
        font-size: 28px;
        margin-right: 15px;
    }
    
    .stat-value {
        font-size: 22px;
    }
    
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
}
</style>