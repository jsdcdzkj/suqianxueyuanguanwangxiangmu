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
        <div ref="el" style="width: 100%; height: 240px"></div>
    </div>
</template>

<script>
import Common from "@/components/ReportTemplate/common";
import * as echarts from "echarts";
import { getDeviceCollectList, tripartiteImbalanceReport } from "@/api/report";
export default {
    cname: "三相不平衡分析",
    mixins: [Common],
    data() {
        return {
            chart: null,
            source: [
                ["4%以下(正常区间)", 86.5, 92.1],
                ["4%-6%", 41.1, 30.4],
                ["6%-8%", 24.1, 67.2],
                ["8%-10%", 55.2, 67.1],
                ["10%-12%", 55.2, 67.1],
                ["12%以上", 55.2, 67.1]
            ],
            obj: {
                zb: "100%",
                value: "1000"
            },
            obj2: {
                zb: "100%",
                value: "1000"
            }
        };
    },
    methods: {
        async initCharts() {
            this.chart = echarts.init(this.$refs.el);
            this.chart.setOption({
                legend: { top: "10px" },
                tooltip: {},
                dataset: {
                    source: this.source
                },
                series: [
                    {
                        type: "pie",
                        radius: "40%",
                        center: ["25%", "50%"]
                    },
                    {
                        type: "pie",
                        radius: "40%",
                        center: ["75%", "50%"]
                    }
                ]
            });
        }
    },
    async mounted() {
        if (this.areaIds.length > 0) {
            const devList = await getDeviceCollectList({ areaIds: this.areaIds });
            const deviceIds = devList.data.map((item) => item.id);
            const res = await tripartiteImbalanceReport({
                startTime: this.startTime,
                endTime: this.endTime,
                timeType: this.timeTypes,
                deviceType: 1,
                deviceIds
            });

            const res2 = await tripartiteImbalanceReport({
                startTime: this.startTime,
                endTime: this.endTime,
                timeType: this.timeTypes,
                deviceType: 2,
                deviceIds
            });
            res.data.vo.forEach((item, index) => {
                this.source[index][1] = Number(item.value);
            });
            res2.data.vo.forEach((item, index) => {
                this.source[index][2] = Number(item.value);
            });
            this.obj.zb = res.data.vo[0].zb;
            this.obj.value = res.data.vo[0].value;

            this.obj2.zb = res2.data.vo[0].zb;
            this.obj2.value = res2.data.vo[0].value;
        }
        this.initCharts();
    }
};
</script>

<style></style>
