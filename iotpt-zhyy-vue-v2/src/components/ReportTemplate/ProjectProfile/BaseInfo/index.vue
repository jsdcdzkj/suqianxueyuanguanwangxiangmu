<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">本报表监测周期为{{ startTime }} - {{ endTime }}。</div>
        <!-- 单区域信息 -->
        <el-descriptions class="margin-top" :labelStyle="{ width: '100px' }" :column="1" border v-if="type == 1">
            <el-descriptions-item>
                <template slot="label">区域名称</template>
                {{ dataInfo.area.areaName }}
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">区域地址</template>
                {{ dataInfo.area.detailAddress }}
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">区域联系电话</template>
                {{ dataInfo.area.phones }}
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">区域设备数</template>
                <el-tag size="small">{{ dataInfo.deviceCount }}个</el-tag>
            </el-descriptions-item>
        </el-descriptions>
        <!-- 多区域信息 -->
        <el-descriptions class="margin-top" :column="1" border v-if="type == 2" :labelStyle="{ width: '100px' }">
            <el-descriptions-item>
                <template slot="label">项目名称</template>
                {{ projectName }}
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">包含区域数</template>
                {{ dataInfo.areaCount }}户
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">区域设备数</template>
                {{ dataInfo.deviceCount || 0 }}个（{{ dataInfo.deviceCountOn }}个正常，{{
                    dataInfo.deviceCountOff
                }}个离线，{{ dataInfo.deviceCountError }}个异常）
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">区域名称</template>
                {{ dataInfo.areaNames }}
            </el-descriptions-item>
        </el-descriptions>
    </div>
</template>

<script>
import Common from "@/components/ReportTemplate/common";
import { baseInfo } from "@/api/report";
export default {
    cname: "基本信息",
    mixins: [Common],
    data() {
        return {
            dataInfo: {
                area: {
                    areaName: "隆庆批发超市",
                    detailAddress: "江苏省徐州市泉山区泰山路88号",
                    phones: "18888881234"
                },
                deviceCount: 0
            }
        };
    },
    mounted() {
        this.$nextTick(async () => {
            if (this.areaIds.length > 0) {
                baseInfo({
                    areaIds: this.areaIds,
                    startTime: this.startTime,
                    endTime: this.endTime,
                    timeType: this.timeTypes,
                    splType: this.type
                }).then((res) => {
                    this.dataInfo = res.data;
                });
            }
        });
    }
};
</script>

<style lang="scss" scoped>
.margin-top {
    margin-top: 10px;
}
</style>
