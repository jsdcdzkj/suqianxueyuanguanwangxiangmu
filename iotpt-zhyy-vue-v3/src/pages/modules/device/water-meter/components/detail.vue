<template>
    <div>
        <div
            class="flex border-1 border-solid items-center border-[#0000000F] rounded-4px text-16px text-[#000000D9] overflow-hidden">
            <div @click="activeTab = 1"
                class="flex-1 cursor-pointer height-40px text-center line-height-40px border-r-1 border-r-solid border-r-[#0000000F]"
                :class="{ 'bg-#345BADFF text-white': activeTab === 1 }">
                基本信息</div>
            <div @click="activeTab = 2"
                class="flex-1 cursor-pointer height-40px text-center line-height-40px border-r-1 border-r-solid border-r-[#0000000F]"
                :class="{ 'bg-#345BADFF text-white': activeTab === 2 }">
                计量信息</div>
            <div @click="activeTab = 3"
                class="flex-1 cursor-pointer height-40px text-center line-height-40px border-r-1 border-r-solid border-r-[#0000000F]"
                :class="{ 'bg-#345BADFF text-white': activeTab === 3 }">
                维保记录</div>
            <div @click="activeTab = 4" class="flex-1 cursor-pointer height-40px text-center line-height-40px"
                :class="{ 'bg-#345BADFF text-white': activeTab === 4 }">告警信息</div>
        </div>
        <template v-if="activeTab === 1">
            <div class="h-40px mt-12px px-12px text-16px text-[#000000D9] font-bold line-height-40px bg-[#F8F8F8FF]">
                基本信息
            </div>
            <el-descriptions :column="2" border class="mt-12px">
                <el-descriptions-item label-width="110" label="设备类型">
                    kooriookami
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="设备编码">
                    18100000000
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="设备状态">
                    Suzhou
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="所属分项">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="所属网关">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="安装位置">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="所属区域">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="使用日期">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="管理员电话">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="安装日期">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="设备管理员">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="设备型号">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="供应商名称">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="维保公司">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="年检日期">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="采购日期">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="过保日期">
                </el-descriptions-item>
                <el-descriptions-item label-width="110" label="设备描述">
                </el-descriptions-item>
            </el-descriptions>
            <div class="h-40px mt-12px px-12px text-16px text-[#000000D9] font-bold line-height-40px bg-[#F8F8F8FF]">
                关联模型
            </div>
            <ElForm class="mt-12px" label-position="top">
                <el-row :gutter="12">
                    <el-col :span="8">
                        <ElFormItem label-width="110" label="漏水模型" required>
                            <el-select placeholder="请选择" class="w-full">
                                <el-option label="全部" value=""></el-option>
                                <el-option label="在线" value="1"></el-option>
                                <el-option label="告警" value="2"></el-option>
                                <el-option label="离线" value="3"></el-option>
                            </el-select>
                        </ElFormItem>
                    </el-col>
                    <el-col :span="8">
                        <ElFormItem label-width="110" label="参数范围" required>
                            <el-select placeholder="请选择" class="w-full">
                                <el-option label="全部" value=""></el-option>
                                <el-option label="在线" value="1"></el-option>
                                <el-option label="告警" value="2"></el-option>
                                <el-option label="离线" value="3"></el-option>
                            </el-select>
                        </ElFormItem>
                    </el-col>
                    <el-col :span="8">
                        <ElFormItem label-width="110" label="历史数据周期" required>
                            <el-select placeholder="请选择" class="w-full">
                                <el-option label="全部" value=""></el-option>
                                <el-option label="在线" value="1"></el-option>
                                <el-option label="告警" value="2"></el-option>
                                <el-option label="离线" value="3"></el-option>
                            </el-select>
                        </ElFormItem>
                    </el-col>
                </el-row>
            </ElForm>
            <div class="h-40px px-12px text-16px text-[#000000D9] font-bold line-height-40px bg-[#F8F8F8FF]">坐标信息
            </div>
        </template>
        <template v-if="activeTab === 2">
            <div class="h-40px mt-12px px-12px text-16px text-[#000000D9] font-bold line-height-40px bg-[#F8F8F8FF]">
                属性
            </div>
            <div class="flex gap-12px mt-12px">
                <div class="flex-1 border-1 border-solid border-[#00000026]">
                    <div class="flex justify-between items-center p-12px">
                        <div class="text-14px">
                            <div class="text-[#000000A6]">累计用量</div>
                            <div class="text-[#00000073] mt-2px"><span
                                    class="font-bold text-[#000000D9] text-24px mr-2px">0.00</span>万m³</div>
                        </div>
                        <div class="size-56px rounded-8px flex items-center justify-center bg-[#E4EDFB]">
                            <i class="ri-contrast-drop-line text-40px text-[#4378E7]"></i>
                        </div>
                    </div>
                    <div class="mt-4px h-32px bg-[#345BAD0D] px-12px line-height-32px text-14px text-[#000000D9]">
                        2026-08-12 10:00:00
                    </div>
                </div>
                <div class="flex-1 border-1 border-solid border-[#00000026]">
                    <div class="flex justify-between items-center p-12px">
                        <div class="text-14px">
                            <div class="text-[#000000A6]">压力值</div>
                            <div class="text-[#00000073] mt-2px"><span
                                    class="font-bold text-[#000000D9] text-24px mr-2px">0.00</span>MPa</div>
                        </div>
                        <div class="size-56px rounded-8px flex items-center justify-center bg-[#E4EDFB]">
                            <i class="ri-dashboard-3-fill text-40px text-[#4378E7]"></i>
                        </div>
                    </div>
                    <div class="mt-4px h-32px bg-[#345BAD0D] px-12px line-height-32px text-14px text-[#000000D9]">
                        2026-08-12 10:00:00
                    </div>
                </div>
                <div class="flex-1 border-1 border-solid border-[#00000026]">
                    <div class="flex justify-between items-center p-12px">
                        <div class="text-14px">
                            <div class="text-[#000000A6]">瞬时流量</div>
                            <div class="text-[#00000073] mt-2px"><span
                                    class="font-bold text-[#000000D9] text-24px mr-2px">0.00</span>m³/h</div>
                        </div>
                        <div class="size-56px rounded-8px flex items-center justify-center bg-[#E4EDFB]">
                            <i class="ri-water-percent-fill text-40px text-[#4378E7]"></i>
                        </div>
                    </div>
                    <div class="mt-4px h-32px bg-[#345BAD0D] px-12px line-height-32px text-14px text-[#000000D9]">
                        2026-08-12 10:00:00
                    </div>
                </div>
            </div>
            <div class="mt-12px">
                <ElTabs type="border-card" @tab-change="handleTabChange1" v-model="tabs1">
                    <el-tab-pane label="用水量" name="1">
                        <div clss="p-12px">
                            <div class="flex items-center justify-between">
                                <div class="text-14px text-[#000000A6]">累计用量(万m³)：<span
                                        class="text-20px text-[#000000A6] font-bold">123456</span>
                                </div>
                                <div class="flex items-center gap-8px">
                                    <el-radio-group v-model="dateType">
                                        <el-radio-button label="日" :value="1" />
                                        <el-radio-button label="月" :value="2" />
                                        <el-radio-button label="年" :value="3" />
                                    </el-radio-group>
                                    <el-date-picker v-model="date" type="daterange" value-format="YYYY-MM-DD"
                                        placeholder="选择日期" class="!w-220px" start-placeholder="开始日期"
                                        end-placeholder="结束日期" v-if="dateType === 1" />
                                    <el-date-picker v-model="date" type="month" value-format="YYYY-MM"
                                        placeholder="选择日期" class="!w-220px" v-if="dateType === 2" />
                                    <el-date-picker v-model="date" type="year" value-format="YYYY" placeholder="选择日期"
                                        class="!w-220px" v-if="dateType === 3" />
                                    <el-button type="primary" :icon="Search">查询</el-button>
                                    <el-button type="default" :icon="Refresh" class="!ml0">重置</el-button>
                                </div>
                            </div>
                            <div class="w-full h-600px">
                                <ChartItem idName="waterMeterChart" :chartData="chartData" />
                            </div>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="压力值" name="2">压力值</el-tab-pane>
                    <el-tab-pane label="瞬时流量" name="3">瞬时流量</el-tab-pane>
                </ElTabs>
            </div>
        </template>
        <template v-if="activeTab === 3">
            <div class="flex mt-12px gap-8px">
                <el-input placeholder="内容关键字" class="w-200px"></el-input>
                <el-select placeholder="选择处理班组" class="!w-200px">
                    <el-option label="选项1" value="1"></el-option>
                </el-select>
                <el-date-picker type="daterange" value-format="YYYY-MM-DD" class="!w-220px" start-placeholder="开始日期"
                    end-placeholder="结束日期" />
                <el-button type="primary" :icon="Search">查询</el-button>
                <el-button type="default" :icon="Refresh" class="!ml0">重置</el-button>
                <el-button type="default" :icon="Upload" class="!ml0">导出</el-button>
            </div>
            <el-table border class="mt-12px" :data="tableData" height="calc(100vh - 240px)">
                <el-table-column align="center" label="完成时间" prop="time"></el-table-column>
                <el-table-column align="center" label="类型" prop="time"></el-table-column>
                <el-table-column align="center" label="内容" prop="time"></el-table-column>
                <el-table-column align="center" label="所属区域" prop="time"></el-table-column>
                <el-table-column align="center" label="操作" width="100">
                    <template #default="{ row, $index }">
                        <el-link :underline="false" type="primary">查看</el-link>
                        <el-link :underline="false" type="danger" class="ml-8px">删除</el-link>
                    </template>
                </el-table-column>
            </el-table>
            <div class="mt-12px flex items-center justify-end">
                <el-pagination background layout="total,sizes,prev, pager, next, jumper, ->" :total="100" />
            </div>
        </template>
        <template v-if="activeTab === 4">
            <div class="flex mt-12px gap-8px">
                <el-select placeholder="告警类别" class="!w-200px">
                    <el-option label="选项1" value="1"></el-option>
                </el-select>
                <el-date-picker type="daterange" value-format="YYYY-MM-DD" class="!w-220px" start-placeholder="开始日期"
                    end-placeholder="结束日期" />
                <el-button type="primary" :icon="Search">查询</el-button>
                <el-button type="default" :icon="Refresh" class="!ml0">重置</el-button>
            </div>
            <el-table border class="mt-12px" :data="tableData" height="calc(100vh - 240px)">
                <el-table-column align="center" label="时间" prop="time"></el-table-column>
                <el-table-column align="center" label="类型" prop="time"></el-table-column>
                <el-table-column align="center" label="内容" prop="time"></el-table-column>
                <el-table-column align="center" label="所属区域" prop="time"></el-table-column>
                <el-table-column align="center" label="操作" width="100">
                    <template #default="{ row, $index }">
                        <el-link :underline="false" type="danger">上报</el-link>
                        <el-link :underline="false" type="primary" class="ml-8px">忽略</el-link>
                    </template>
                </el-table-column>
            </el-table>
            <div class="mt-12px flex items-center justify-end">
                <el-pagination background layout="total,sizes,prev, pager, next, jumper, ->" :total="100" />
            </div>
        </template>
    </div>
</template>
<script lang="tsx" setup>
import { ElDescriptions, ElDescriptionsItem, ElForm, ElFormItem, ElSelect, ElOption, ElRow, ElCol, ElTabs, ElTabPane, ElRadioGroup, ElRadioButton, ElDatePicker, ElButton, ElInput, ElTable, ElTableColumn, ElLink, ElPagination, ElMessage } from 'element-plus';
import { Search, Refresh, Upload } from '@element-plus/icons-vue';
import { nextTick, ref } from 'vue'
import ChartItem from '@/pages/modules/dma-data/components/chartItem.vue'
const activeTab = ref(1);
const dateType = ref(1);
const date = ref('');
const tabs1 = ref('1');
let base = +new Date(1988, 9, 3);
let oneDay = 24 * 3600 * 1000;
let data = [[base, Math.random() * 300]];
const tableData = ref([{}, {}]);
for (let i = 1; i < 1000; i++) {
    let now = new Date((base += oneDay));
    data.push([+now, Math.round((Math.random() - 0.5) * 20 + data[i - 1][1])]);
}
const chartData = ref({});
const handleTabChange1 = (tab) => {
    console.log(tab)
    if (tab == '1') {
        nextTick(() => {
            chartData.value = {
                color: ['#345BAD'],
                tooltip: {
                    trigger: 'axis',
                    position: function (pt) {
                        return [pt[0], '10%'];
                    }
                },
                xAxis: {
                    type: 'time',
                    boundaryGap: false
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: [0, '100%']
                },
                dataZoom: [
                    {
                        type: 'inside',
                        start: 0,
                        end: 20
                    },
                    {
                        start: 0,
                        end: 20
                    }
                ],
                series: [
                    {
                        name: 'Fake Data',
                        type: 'line',
                        smooth: true,
                        symbol: 'none',
                        data: data
                    }
                ]
            };
        })
    }
}
</script>
<style scoped></style>