<template>
    <BasePage v-bind="page" :aside-left-width="280">
        <template #pageLeft>
            <areaChoose></areaChoose>
        </template>
        <template #pageTitle>
            <div>漏水评估</div>
        </template>
        <template #pageToolbar>
            <div class="flex gap8px">
                <el-checkbox>核减大用户水量</el-checkbox>
                <el-date-picker type="daterange" v-model="dateRange" value-format="YYYY-MM-DD" start-placeholder="开始日期"
                    end-placeholder="结束日期"></el-date-picker>
                <el-button type="primary" :icon="Search">查询</el-button>
                <el-button type="default" :icon="Refresh" class="!ml0">重置</el-button>
            </div>
        </template>
        <template #pageBody>
            <div class="flex items-center justify-between h-48px bg-[#F8F8F8] text-[#000000D9] px-12px">
                <span class="font-bold">2025年-12-01 至 2025年-12-15 DMA漏损预警评估</span>
                <div class="flex gap-8px">
                    <div class="flex items-center cursor-pointer">
                        <div class="size-12px bg-[#57BD94]"></div>
                        <span class="text-[rgba(0, 0, 0, 0.85)] text-14px ml-4px">正常</span>
                    </div>
                    <div class="flex items-center cursor-pointer">
                        <div class="size-12px bg-[#F09054]"></div>
                        <span class="text-[rgba(0, 0, 0, 0.85)] text-14px ml-4px">黄色预警</span>
                    </div>
                    <div class="flex items-center cursor-pointer">
                        <div class="size-12px bg-[#ED6A5E]"></div>
                        <span class="text-[rgba(0, 0, 0, 0.85)] text-14px ml-4px">红色预警</span>
                    </div>
                    <el-select v-model="selectedStatus" placeholder="请选择" size="small" class="w-100px">
                        <el-option label="正常" value="1"></el-option>
                        <el-option label="黄色预警" value="2"></el-option>
                        <el-option label="红色预警" value="3"></el-option>
                    </el-select>
                </div>
            </div>
            <div class="flex-1 my-12px">
                <div class="w-full h-full">
                    <ChartItem idName="warnBar" :chartData="warnBarOptions" />
                </div>
            </div>
            <div class="flex-1 mt-12px">
                <el-table :data="tableData" border stripe height="100%">
                    <el-table-column align="center" type="index" label="序号" width="80px"></el-table-column>
                    <el-table-column align="center" prop="date" label="上级分区" width="180px"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="DMA分区"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="红色预警" label-class-name="red_label">
                        <el-table-column align="center" prop="trafficRatio" label-class-name="red_label"
                            label="天数"></el-table-column>
                        <el-table-column align="center" prop="trafficRatio" label-class-name="red_label"
                            label="占比"></el-table-column>
                    </el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label-class-name="yellow_label" label="黄色预警">
                        <el-table-column align="center" prop="trafficRatio" label-class-name="yellow_label"
                            label="天数"></el-table-column>
                        <el-table-column align="center" prop="trafficRatio" label-class-name="yellow_label"
                            label="占比"></el-table-column>
                    </el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label-class-name="green_label" label="正常">
                        <el-table-column align="center" prop="trafficRatio" label-class-name="green_label"
                            label="天数"></el-table-column>
                        <el-table-column align="center" prop="trafficRatio" label-class-name="green_label"
                            label="占比"></el-table-column>
                    </el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="基准值(m³/h)"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="黄色预警值(m³/h)"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="红色预警值(m³/h)"></el-table-column>
                </el-table>
            </div>
        </template>
    </BasePage>
</template>

<script setup lang="tsx">
import BasePage from "@/core/struct/page/base-page";
import dayjs from 'dayjs'
import { ref } from 'vue'
import { Search, Refresh } from "@element-plus/icons-vue";
import { usePage } from "@/core/struct/page";
import ChartItem from "../components/chartItem.vue";
import areaChoose from "@/components/area/index.vue";
const selectedStatus = ref('1');
const { page } = usePage({
    title: "漏水评估",
});
defineOptions({
    name: 'water-leakage'
})
const warnBarOptions = ref({})
const dateRange = ref([
    dayjs().subtract(14, 'day').format('YYYY-MM-DD'),
    dayjs().format('YYYY-MM-DD')
]);
const tableData = ref([]);
const getData = () => {

}
warnBarOptions.value = {
    color: ['#57BD94', '#F09054', '#ED6A5E'],
    tooltip: {
        trigger: 'axis',
        textStyle: {
            align: 'justify'
        }
    },
    grid: {
        top: 60,
        bottom: 10,
        left: 10,
        right: 20,
        containLabel: true
    },
    xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        axisTick: {
            show: false
        }
    },
    yAxis: {
        name: '流量(L/s)',
        type: 'value',
        splitLine: {
            lineStyle: {
                color: 'rgba(0, 0, 0, 0.15)',
                type: 'dashed'
            }
        }
    },
    series: [{
        name: '正常',
        type: 'bar',
        stack: 'Ad',
        emphasis: {
            focus: 'series'
        },
        data: [120, 132, 101, 134, 90, 230, 210]
    },
    {
        name: '黄色预警',
        type: 'bar',
        stack: 'Ad',
        emphasis: {
            focus: 'series'
        },
        data: [220, 182, 191, 234, 290, 330, 310]
    },
    {
        name: '红色预警',
        type: 'bar',
        stack: 'Ad',
        emphasis: {
            focus: 'series'
        },
        data: [150, 232, 201, 154, 190, 330, 410]
    }]
}
</script>

<style scoped lang="scss">
:deep(.el-table th.red_label) {
    background-color: #ED6A5E !important;
    color: #fff;
    border: 1px solid rgba(0, 0, 0, 0.06);
}

:deep(.el-table th.yellow_label) {
    background-color: #FBBE56 !important;
    color: #fff;
    border: 1px solid rgba(0, 0, 0, 0.06);
}

:deep(.el-table th.green_label) {
    background-color: #57BD94 !important;
    color: #fff;
    border: 1px solid rgba(0, 0, 0, 0.06);
}
</style>