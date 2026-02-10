<template>
    <BasePage v-bind="page" :aside-left-width="280">
        <template #pageLeft>
            <areaChoose></areaChoose>
        </template>
        <template #pageTitle>
            <div>流量压力对比分析</div>
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
                <span class="font-bold">2025-12-01 至 2025-12-15 流量压力对比分析</span>
            </div>
            <div class="flex-1 my-12px">
                <div class="w-full h-full">
                    <ChartItem idName="trafficLine" :chartData="trafficOptions" />
                </div>
            </div>
            <div class="flex-1 mt-12px">
                <div class="w-full h-full">
                    <ChartItem idName="presureChart" :chartData="pressureOptions" />
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
import ChartItem from "../components/chartItem.vue";
import areaChoose from "@/components/area/index.vue";

const { page } = usePage({
    title: "流量压力对比分析"
});
defineOptions({
    name: 'traffic-pressure'
})
const trafficOptions = ref({})
const pressureOptions = ref({})
const dateRange = ref([
    dayjs().subtract(14, 'day').format('YYYY-MM-DD'),
    dayjs().format('YYYY-MM-DD')
]);
const getData = () => {

}
trafficOptions.value = {
    color: ['#57BD94', '#5C90F7'],
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
        name: '入口流量计',
        data: [150, 123, 244, 278, 235, 47, 60],
        type: 'line',
        showSymbol: false,
        symbol: 'diamond',
        symbolSize: 12
    }, {
        name: '夜间最小流量',
        data: [100, 230, 224, 218, 135, 147, 260],
        type: 'line',
        showSymbol: false,
        symbol: 'diamond',
        symbolSize: 12
    }]
}
pressureOptions.value = {
    color: ['#57BD94', '#5C90F7'],
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
        name: '压力(m)',
        type: 'value',
        splitLine: {
            lineStyle: {
                color: 'rgba(0, 0, 0, 0.15)',
                type: 'dashed'
            }
        }
    },
    series: [{
        name: '入口压力计',
        data: [150, 123, 244, 278, 235, 47, 60],
        type: 'line',
        showSymbol: false,
        symbol: 'diamond',
        symbolSize: 12
    }, {
        name: '最不利点压力',
        data: [100, 230, 224, 218, 135, 147, 260],
        type: 'line',
        showSymbol: false,
        symbol: 'diamond',
        symbolSize: 12
    }]
}
</script>

<style scoped lang="scss"></style>