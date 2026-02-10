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
                <span class="font-bold">2025-12-01 至 2025-12-15 供水量分析</span>
                <div class="flex gap-8px">
                    <div class="flex items-center cursor-pointer">
                        <div class="size-12px bg-[#57BD94]"></div>
                        <span class="text-[rgba(0, 0, 0, 0.85)] text-14px ml-4px">夜间最小小时流量</span>
                    </div>
                    <div class="flex items-center cursor-pointer">
                        <div class="size-12px bg-[#F09054]"></div>
                        <span class="text-[rgba(0, 0, 0, 0.85)] text-14px ml-4px">MNF流量比</span>
                    </div>
                </div>
            </div>
            <el-row :gutter="12" class="overflow-auto flex-1 content-start mt-12px">
                <el-col :span="8" class="mb12px" v-for="i in 12">
                    <div class="border-1px border-[#00000026] border-solid rounded-4px p-12px">
                        <div class="flex items-center justify-between">
                            <span class="text-[#000000D9] text-18px font-bold">商学院A-01教学楼</span>
                            <span
                                class="text-[#345BADFF] text-14px border-1px border-solid border-[#345BADFF] px-8px rounded-4px line-height-22px bg-[#F5F8FFFF]">商学院</span>
                        </div>
                        <div class="mt-6px h-202px w-full">
                            <ChartItem :idName="`chart-item-${i}`" :chartData="chartData" />
                        </div>
                    </div>
                </el-col>
            </el-row>
        </template>
    </BasePage>
</template>

<script setup lang="tsx">
import BasePage from "@/core/struct/page/base-page";
import dayjs from 'dayjs'
import { ref } from 'vue'
import { Search, Refresh } from "@element-plus/icons-vue";
import ChartItem from "../components/chartItem.vue";
import { usePage } from "@/core/struct/page";
import areaChoose from "@/components/area/index.vue";
const chartData = ref({
    color: ['#57BD94', '#F09054'],
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '4%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        axisTick: {
            show: false
        }
    },
    yAxis: [{
        type: 'value',
    }, {
        type: 'value',
        name: 'MNF流量比',
        axisLabel: {
            formatter: '{value}%'
        },
        position: 'right'
    }],
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'line'
        },
        textStyle: {
            align: 'justify'
        },
        formatter: function (params: any) {
            let result = params[0].axisValue + '<br/>';
            params.forEach((item: any) => {
                if (item.seriesName === 'MNF流量比') {
                    result += `${item.marker}${item.seriesName}: ${item.value}%<br/>`;
                } else {
                    result += `${item.marker}${item.seriesName}: ${item.value}<br/>`;
                }
            });
            return result;
        }
    },
    series: [
        {
            name: '夜间最小小时流量',
            data: [820, 932, 901, 934, 1290, 1330, 1320],
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 10,
            itemStyle: {
                borderWidth: 2,
                borderColor: '#fff'
            }
        }, {
            name: 'MNF流量比',
            data: [10, 92, 91, 94, 90, 30, 20],
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            symbol: 'circle',
            symbolSize: 10,
            itemStyle: {
                borderWidth: 2,
                borderColor: '#fff'
            }
        }
    ]
})
defineOptions({
    name: 'mnf-day-trend'
})
const dateRange = ref([
    dayjs().subtract(14, 'day').format('YYYY-MM-DD'),
    dayjs().format('YYYY-MM-DD')
]);

const { page, pageApi } = usePage({
    title: "MNF日趋势总览",
    pagination: {
        currentPage: 1,
        pageSize: 20,
        total: 15,
        background: true,
        layout: "prev, pager, next, jumper, ->, total",
        onChange(pageNo: number, pageSize: number) {
            if (page.pagination) {
                page.pagination.currentPage = pageNo;
                page.pagination.pageSize = pageSize;
                pageApi.pageList();
            }
        }
    }
});
</script>

<style scoped lang="scss"></style>