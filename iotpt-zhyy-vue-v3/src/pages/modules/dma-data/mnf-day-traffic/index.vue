<template>
    <BasePage v-bind="page" :aside-left-width="280">
        <template #pageLeft>
            <areaChoose></areaChoose>
        </template>
        <template #pageTitle>
            <div>供水量分析</div>
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
                <span class="font-bold">2025-12-01 至 2025-12-15 MNF日均流量比</span>
                <div class="flex gap-8px">
                    <div class="flex items-center">
                        <div class="size-12px bg-[#57BD94]"></div>
                        <span class="text-[rgba(0, 0, 0, 0.85)] text-14px ml-4px">较好</span>
                    </div>
                    <div class="flex items-center">
                        <div class="size-12px bg-[#F09054]"></div>
                        <span class="text-[rgba(0, 0, 0, 0.85)] text-14px ml-4px">一般</span>
                    </div>
                    <div class="flex items-center">
                        <div class="size-12px bg-[#ED6A5E]"></div>
                        <span class="text-[rgba(0, 0, 0, 0.85)] text-14px ml-4px">较差</span>
                    </div>
                </div>
            </div>
            <div class="flex-1 overflow-hidden mt-12px">
                <el-table :data="tableData" border stripe height="100%">
                    <el-table-column align="center" prop="date" label="上级分区" width="180px"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="DMA分区"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="DMA分区"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="MNF流量比(%)"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="总供水量(m³)"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="日均供水量(m³)"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="日均小时供水量(m³/h)"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="日均夜间最小小时流量(m³/h)"></el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="日均夜间最小小时流量·最小值">
                        <el-table-column align="center" prop="trafficRatio" label="流量值(m³/h)"></el-table-column>
                        <el-table-column align="center" prop="trafficRatio" label="出现时间"></el-table-column>
                    </el-table-column>
                    <el-table-column align="center" prop="trafficRatio" label="日均夜间最小小时流量·最大值">
                        <el-table-column align="center" prop="trafficRatio" label="流量值(m³/h)"></el-table-column>
                        <el-table-column align="center" prop="trafficRatio" label="出现时间"></el-table-column>
                    </el-table-column>
                </el-table>
            </div>
            <div class="flex justify-end my-12px">
                <el-pagination background layout="prev, pager, next, jumper, ->, total"
                    :current-page.sync="queryParams.pageNo" :page-sizes="[10, 20, 50, 100]"
                    :page-size.sync="queryParams.pageSize" :total="queryParams.total" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" />
            </div>
            <div class="flex-1 position-relative">
                <div
                    class="flex items-center justify-between h-48px bg-[#F8F8F8] text-[#000000D9] px-12px w-full box-border position-absolute top-0 left-0 z-1">
                    <span class="font-bold">MNF流量比</span>
                </div>
                <div class="w-full h-full position-relative z-2 bg-transparent" id="lineChart">
                    <chartItem :idName="'lineChart'" :chartData="chartOption"></chartItem>
                </div>
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
import chartItem from "../components/chartItem.vue";
import areaChoose from "@/components/area/index.vue";
const { page } = usePage({
    title: "MNF日均流量比"
});
defineOptions({
    name: 'mnf-day-traffic'
})
const dateRange = ref([
    dayjs().subtract(14, 'day').format('YYYY-MM-DD'),
    dayjs().format('YYYY-MM-DD')
]);
const queryParams = ref({
    pageNo: 1,
    pageSize: 10,
    total: 0
})
const tableData = ref([]);
const handleSizeChange = (val: number) => {
    queryParams.value.pageSize = val;
    getData()
}
const handleCurrentChange = (val: number) => {
    queryParams.value.pageNo = val;
    getData()
}
const getData = () => {

}
const chartOption = ref({});
chartOption.value = {
    color: ['#57BD94', '#F09054', '#ED6A5E', '#5C90F7', '#844CDB'],
    tooltip: {
        trigger: 'axis',
        textStyle: {
            align: 'justify'
        }
    },
    legend: {
        top: 12,
        left: 'right',
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
        type: 'value',
        splitLine: {
            lineStyle: {
                color: 'rgba(0, 0, 0, 0.15)',
                type: 'dashed'
            }
        }
    },
    series: [{
        name: '教学楼A',
        data: [150, 123, 244, 278, 235, 47, 60],
        type: 'line',
        showSymbol: false,
        symbol: 'diamond',
        symbolSize: 12
    }, {
        name: '教学楼B0-1',
        data: [100, 230, 224, 218, 135, 147, 260],
        type: 'line',
        showSymbol: false,
        symbol: 'diamond',
        symbolSize: 12
    }, {
        name: '教学楼C',
        data: [70, 160, 104, 28, 35, 47, 160],
        type: 'line',
        showSymbol: false,
        symbol: 'diamond',
        symbolSize: 12
    }]
};
</script>

<style scoped lang="scss"></style>