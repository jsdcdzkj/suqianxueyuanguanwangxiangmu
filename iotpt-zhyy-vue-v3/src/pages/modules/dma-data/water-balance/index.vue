<template>
    <BasePage v-bind="page" :aside-left-width="280">
        <template #pageLeft>
            <areaChoose></areaChoose>
        </template>
        <template #pageTitle>
            <div>水平衡分析</div>
        </template>
        <template #pageToolbar>
            <div class="flex gap8px">
                <el-date-picker type="year" v-model="dateYear" value-format="YYYY" start-placeholder="开始日期"
                    end-placeholder="结束日期"></el-date-picker>
                <el-button type="primary" :icon="Search">查询</el-button>
                <el-button type="default" :icon="Refresh" class="!ml0">重置</el-button>
            </div>
        </template>
        <template #pageBody>
            <div class="w-full h-full overflow-hidden flex gap-12px">
                <div class="flex-[1] h-full overflow-hidden flex flex-col gap-12px">
                    <div class="flex items-center justify-between h-48px bg-[#F8F8F8] text-[#000000D9] px-12px">
                        <span class="font-bold">图表分析</span>
                    </div>
                    <div class="flex-1">
                        <ChartItem idName="barChart" :chartData="barOptions" />
                    </div>
                    <div class="flex-1">
                        <ChartItem idName="mapChart" :chartData="mapOptions" />
                    </div>
                </div>
                <div class="flex-[2] h-full overflow-hidden flex flex-col">
                    <div class="flex items-center justify-between h-48px bg-[#F8F8F8] text-[#000000D9] px-12px">
                        <span class="font-bold">2026年水平衡分析</span>
                    </div>
                    <div class="flex-1 overflow-hidden mt-12px">
                        <el-table :data="tableData" max-height="100%" border>
                            <el-table-column prop="date" label="月份" width="80" align="center" />
                            <el-table-column prop="date" label="供水量(万m³)" align="center" />
                            <el-table-column prop="date" label="计费水量(万m³)" align="center" />
                            <el-table-column prop="date" label="免费水量(万m³)" align="center" />
                            <el-table-column prop="date" label="漏损水量(万m³)" align="center" />
                            <el-table-column prop="date" label="产销差(%)" align="center">
                                <el-table-column prop="date" label="天数" align="center" />
                                <el-table-column prop="date" label="占比" align="center" />
                                <el-table-column prop="date" label="占比" align="center" />
                            </el-table-column>
                        </el-table>
                    </div>
                </div>
            </div>
        </template>
    </BasePage>
</template>

<script setup lang="tsx">
import BasePage from "@/core/struct/page/base-page";
import { ref } from 'vue'
import { Search, Refresh } from "@element-plus/icons-vue";
import { usePage } from "@/core/struct/page";
import ChartItem from "../components/chartItem.vue";
import areaChoose from "@/components/area/index.vue";
const { page } = usePage({
    title: "水平衡分析"
});
defineOptions({
    name: 'water-balance'
})
const barOptions = ref({})
const mapOptions = ref({})
const dateYear = ref(String(new Date().getFullYear()));
const tableData = ref([]);
const getData = () => {

}
barOptions.value = {
    tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
    },
    series: [
        {
            color: ['#5C90F7', '#456CB9'],
            type: 'pie',
            selectedMode: 'single',
            radius: [0, '30%'],
            label: {
                position: 'inner',
                fontSize: 12,
                color: '#fff'
            },
            labelLine: {
                show: false
            },
            data: [
                { value: 1548, name: '收益水量' },
                { value: 775, name: '产销差' }
            ]
        },
        {
            color: ['#ED777A', '#F09054', '#57BD94'],
            type: 'pie',
            radius: ['40%', '60%'],
            label: {
                formatter: function (params: any) {
                    return `{a|${params.name}}\n{b|${params.value}m³(${params.percent}%)}`;
                },
                show: true,
                avoidLabelOverlap: true,
                rich: {
                    a: {
                        color: 'rgba(0, 0, 0, 0.85)',
                        fontSize: 14,
                        lineHeight: 24,
                    },
                    b: {
                        lineHeight: 24,
                        color: 'rgba(0, 0, 0, 0.65)',
                        fontSize: 12,
                    }
                }
            },
            data: [
                { value: 1048, name: '计费水量' },
                { value: 335, name: '免费水量' },
                { value: 310, name: '漏损水量' },
            ]
        }
    ]
};
mapOptions.value = {
    series: [
        {
            type: 'treemap',
            width: '100%',
            height: '100%',
            roam: false,
            nodeClick: false,
            breadcrumb: { show: false },
            itemStyle: {
                borderWidth: 4,
                borderColor: '#fff'
            },
            data: [
                {
                    name: 'nodeA',
                    value: 10,
                },
                {
                    name: 'nodeB',
                    value: 20,
                },
                {
                    name: 'nodeC',
                    value: 30,
                },
                {
                    name: 'nodeD',
                    value: 40,
                },
                {
                    name: 'nodeE',
                    value: 50,
                },
                {
                    name: 'nodeF',
                    value: 60,
                }
            ]
        }
    ]
}
</script>

<style scoped lang="scss"></style>