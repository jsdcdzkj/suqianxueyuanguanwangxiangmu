<template>
	<div>
		<ElDescriptions :column="3" border label-width="120px">
			<ElDescriptionsItem label="任务名称" label-class-name="my-label" class-name="my-content">{{ detail.planName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="计划类型" label-class-name="my-label" class-name="my-content">{{ detail.executeType == 1 ? "周期任务" : "一次性任务" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="周期类型" label-class-name="my-label" class-name="my-content">{{ detail.executeCycle || "--" }}</ElDescriptionsItem>
		</ElDescriptions>
		<ElTable
			border
			:data="subs"
			class="m-t-12px"
		>
			<ElTableColumn
				label="序号"
				type="index"
				width="55px"
				align="center"
			></ElTableColumn>
			<ElTableColumn label="所属区域" align="center" prop="regionName"></ElTableColumn>
			<ElTableColumn label="检查项" align="center" prop="amount2">
				<template #default="scope">
                            <div class="table-row-con">
                                <div :key="i" v-for="(d, i) in scope.row.records">
                                    {{ d.deviceName }}
                                </div>
                            </div>
                        </template>
			</ElTableColumn>
			<ElTableColumn label="执行标准" align="center" prop="amount3" show-overflow-tooltip>
				<template #default="scope">
                            <div class="table-row-con">
                                <div :key="i" v-for="(d, i) in scope.row.records">
                                    {{ d.subContent }}
                                </div>
                            </div>
                        </template>
			</ElTableColumn>
			<ElTableColumn label="执行结果" align="center" show-overflow-tooltip>
				<template #default="scope"> 
					<div class="table-row-con">
						<div :key="i" v-for="(d, i) in scope.row.records">
							<div v-if="d.execution == 1">正常</div>
							<div v-if="d.execution == 2">异常</div>
						</div>
					</div>
				</template>
			</ElTableColumn>
			<ElTableColumn label="备注信息" align="center" prop="recovery_time" show-overflow-tooltip>
				<template #default="scope">
                            <div class="table-row-con">
                                <div :key="i" v-for="(d, i) in scope.row.records">
                                    {{ d.description }}
                                </div>
                            </div>
                        </template>
			</ElTableColumn>
			<ElTableColumn label="图片上传" align="center" prop="amount5" width="350">
				<template #default="scope">
					<div class="table-row-con">
						<div :key="i" v-for="(d, i) in scope.row.records">
							<ElImage
								v-for="(file, index) in d.fileList"
								style="width: 81px; height: 81px; margin-right: 12px"
								:src="file"
								:key="index"
								:preview-src-list="d.fileList"
							></ElImage>
						</div>
					</div>
				</template>
			</ElTableColumn>
		</ElTable>
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import { ElDescriptions, ElDescriptionsItem, ElTable, ElTableColumn, ElImage } from "element-plus";
import { inspectionData } from "@/api/pipeline-maintenance/work-order";

const props = defineProps({
	id: number().def(),
	projectId: {
		type: Number,
		default: 0
	},
	row: {
		type: Object,
		default: {}
	}
});
const detail = ref({});
// 详情页数据
const subs = ref([]);
if (props.id) {
	inspectionData({ missionId: props.id, isHandle: 1 }).then((res) => {
                var subsInfo = res.subs;
                for (const key in subsInfo) {
                    if (Object.hasOwnProperty.call(subsInfo, key)) {
                        const element = subsInfo[key];
                        var records = element.records;
                        for (var i = 0; i < records.length; i++) {
                            if (records[i].files.length > 0) {
                                if (null == records.fileList) {
                                    records[i].fileList = [];
                                }
                                var files = records[i].files;
                                for (var j = 0; j < files.length; j++) {
                                    records[i].fileList.push(
                                        "/minio/downMinio?fileName=" + files[j].fileUrl
                                    );
                                }
                            }
                        }
                    }
                }
                subs.value = res.subs;
                detail.value = res.plan || {};
                if (1 == detail.value.planType) {
                    detail.value.planType = "保养";
                } else {
                    detail.value.planType = "巡检";
                }
                if (1 == detail.value.executeCycle) {
                    detail.value.executeCycle = "日";
                } else if (1 == detail.value.executeCycle) {
                    detail.value.executeCycle = "周";
                } else {
                    detail.value.executeCycle = "月";
                }
            });
}
</script>
<style lang="scss" scoped>
:deep(.my-label) {
	font-size: 14px;
	font-weight:500;
	color: rgba(0,0,0,0.85);
	background: #F8F8F8!important;
}
:deep(.my-content) {
	font-size: 14px;
	width: 300px;
	font-weight:400;
	color: rgba(0,0,0,0.65);
}
.base-title {
	height: 40px;
	line-height: 40px;
	margin-bottom: 12px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
	padding:0 12px;
	font-size: 16px;
	color: rgba(0,0,0,0.85);
	margin-bottom: 12px;
}
.table-row-con {
    position: relative;
    // height: 100%;
    width: 100%;

    > div {
        padding: 2px 8px;
        height: 93px;
        display: flex;
        align-items: center;
        justify-content: center;
        word-break: break-all;
        overflow: hidden;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
    }

    > div:not(:last-child) {
        border-bottom: 1px solid #ebeef5;
    }

    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }

    .avatar-uploader .el-upload:hover {
        border-color: #409eff;
    }

    .avatar-uploader-icon {
        font-size: 12px;
        color: #8c939d;
        width: 14px;
        height: 14px;
        line-height: 14px;
        text-align: center;
    }

    .avatar {
        width: 14x;
        height: 14x;
        display: block;
    }
}
</style>
