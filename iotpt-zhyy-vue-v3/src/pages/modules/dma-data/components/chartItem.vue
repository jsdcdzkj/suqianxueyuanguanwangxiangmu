<template>
    <div class="h-full w-full" :id="idName"></div>
</template>
<script lang="ts" setup>
import { ref, watch, onUnmounted, onMounted } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
    idName: {
        type: String,
        default: ''
    },
    chartData: {
        type: Object,
        default: () => ({})
    }
})

const chart = ref(null)

// 处理窗口大小变化的函数
const handleResize = () => {
    if (chart.value) {
        chart.value.resize()
    }
}

const initChart = () => {
    // 获取DOM元素
    const chartDom = document.getElementById(props.idName)
    if (!chartDom) return

    // 初始化ECharts实例
    const myChart = echarts.init(chartDom)

    // 准备图表配置
    const defaultOption = {
        color: ['#57BD94', '#F09054', '#5A6FC0', '#9ECA7F', '#F2CA6B', '#DE6E6A', '#85BEDB'],
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '4%',
            containLabel: true
        }
    }

    // 优先使用传入的options或chartData，否则使用默认配置
    const chartOption = { ...defaultOption }
    if (Object.keys(props.chartData).length > 0) {
        Object.assign(chartOption, props.chartData)
    }

    // 设置图表配置
    myChart.setOption(chartOption)
    chart.value = myChart
}

// 监听chartData或options变化
watch(() => props.chartData, () => {
    initChart()
}, { deep: true })

// 组件挂载后初始化图表
onMounted(() => {
    initChart()
    window.addEventListener('resize', handleResize)
})

// 组件卸载前清理资源
onUnmounted(() => {
    if (chart.value) {
        chart.value.dispose()
        chart.value = null
    }
    window.removeEventListener('resize', handleResize)
})
</script>
<style scoped></style>