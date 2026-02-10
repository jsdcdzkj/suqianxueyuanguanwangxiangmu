<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc" style="margin-bottom: 10px">
            本本监测周期内，负载率超过85%的区域有
            <span class="num">{{ detailInfo.excellentCount }}</span>
            户，负载率70-85%的区域有
            <span class="num">{{ detailInfo.good }}</span>
            户。
        </div>
        <el-table :data="detailInfo.data" border>
            <el-table-column type="index" label="排序" width="50" align="center"></el-table-column>
            <el-table-column label="设备名称" prop="name"></el-table-column>
            <el-table-column label="区域名称" prop="areaName"></el-table-column>
            <el-table-column label="所属街道" prop="floorName"></el-table-column>
            <el-table-column label="负载率" prop="value"></el-table-column>
        </el-table>
    </div>
</template>

<script>
import Common from "@/components/ReportTemplate/common";
import { getDeviceCollectTop } from "@/api/report";
export default {
    cname: "负载率排名TOP10",
    mixins: [Common],
    data() {
        return {
            detailInfo: {
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
            }
        };
    },
    mounted() {
        this.$nextTick(async () => {
            if (this.areaIds.length > 0) {
                getDeviceCollectTop({
                    areaIds: this.areaIds,
                    startTime: this.startTime,
                    endTime: this.endTime,
                    timeType: this.timeTypes,
                    splType: this.type
                }).then(({ data }) => {
                    this.detailInfo = {
                        good: data.goodCount,
                        excellentCount: data.excellentCount,
                        data: data.data.splice(0, 10)
                    };
                });
            }
        });
    }
};
</script>

<style>
.num {
    color: #027dc5;
}
</style>
