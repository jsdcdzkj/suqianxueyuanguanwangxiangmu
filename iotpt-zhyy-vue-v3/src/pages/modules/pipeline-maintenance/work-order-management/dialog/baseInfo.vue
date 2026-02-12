<template>
	<div>
		<div class="base-title">上报信息</div>
		<ElDescriptions :column="2" border label-width="120px">
			<ElDescriptionsItem label="标题" label-class-name="my-label">{{ missionData.title || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="来源" label-class-name="my-label">{{ missionData.sourceName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="严重程度" label-class-name="my-label">{{ missionData.levelsName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="上报人" label-class-name="my-label">{{ missionData.userName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="上报时间" label-class-name="my-label">{{ missionData.reportingTime || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="区域名称" v-if="4 != missionData.source" label-class-name="my-label">{{
				missionData.areaName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="异常设备" v-if="4 != missionData.source" label-class-name="my-label">
				<span v-if="missionData.deviceName == '' || missionData.deviceName == null">-</span>
                <span v-else>{{ missionData.deviceName }}</span>
			</ElDescriptionsItem>
			<ElDescriptionsItem label="内容" label-class-name="my-label">{{ missionData.notes || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="异常图片" v-if="4 != missionData.source" label-class-name="my-label">
				<span v-if="imageUrls.length == 0">-</span>
				<div v-else>
					<ElImage
						v-for="item in imageUrls"
						:key="item.id"
						:src="item"
						:preview-src-list="imageUrls"
						style="width: 100px; height: 100px; margin-right: 10px"
					></ElImage>
				</div>
			</ElDescriptionsItem>
		</ElDescriptions>
		<template  v-if="missionData.serviceType">
			<div class="base-title m-t-12px">服务类型</div>
			<ElDescriptions :column="2" border label-width="120px">
				<ElDescriptionsItem label="任务类型" label-class-name="my-label">{{ missionData.serviceType == 2 ? "有偿服务" : "公区服务" || "--" }}</ElDescriptionsItem>
			</ElDescriptions>
		</template>
		<div class="base-title m-t-12px" v-if="assignLength > 0">指派信息</div>
		<ElDescriptions :column="2" border label-width="120px" v-for="(assign, index) in assigns" :key="index">
			<ElDescriptionsItem label="任务类型" label-class-name="my-label">{{ assign.taskTypeName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="紧急程度" label-class-name="my-label">{{ assign.urgencyName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="截止时间" label-class-name="my-label">{{ assign.deadline || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="任务组" label-class-name="my-label">{{ assign.teamGroupsName || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="备注" label-class-name="my-label">{{ assign.notes || "--" }}</ElDescriptionsItem>
		</ElDescriptions>
		<template v-if="missionData.isPending">
			<div class="base-title m-t-12px">挂起信息</div>
			<ElDescriptions :column="2" border label-width="120px">
				<ElDescriptionsItem label="挂起时间" label-class-name="my-label">{{ missionData.pendingTime || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="挂起原因" label-class-name="my-label">{{ missionData.pendingReason || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="处理人" label-class-name="my-label">{{ missionData.createTime || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="处理时间" label-class-name="my-label">{{ missionData.sourceName || "--" }}</ElDescriptionsItem>
			</ElDescriptions>
		</template>
		<!-- 耗材类目 -->
		<template v-if="missionData.consumables && missionData.consumables.length">
			<div class="base-title m-t-12px">耗材类目</div>
			<ElTable
				border
				:data="list"
			>
				<ElTableColumn
					label="序号"
					type="index"
					width="80px"
					align="center"
				></ElTableColumn>
				<ElTableColumn label="耗材名称" align="center" prop="consumableName"></ElTableColumn>
				<ElTableColumn label="金额（元）" align="center" prop="money"></ElTableColumn>
			</ElTable>
		</template>
		<template  v-if="(missionData.states == 2 || missionData.states == 3) && null != missionData.handleName">
			<div class="base-title m-t-12px">处理信息</div>
			<ElDescriptions :column="2" border label-width="120px">
				<ElDescriptionsItem :span="2" label="指派人" label-class-name="my-label">{{ assionUserName || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem :span="2" label="处理信息" label-class-name="my-label">{{ missionData.crunch || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="处理人" label-class-name="my-label">{{ missionData.handleName || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="处理时间" label-class-name="my-label">{{ missionData.handleDate || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="协助人" v-if="missionData.assistHandleNames" label-class-name="my-label">{{ missionData.assistHandleNames || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="协助人数" v-if="missionData.assistHandleNames" label-class-name="my-label">{{ missionData.assistHandleNames.split(",").length || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="处理图片" v-if="handlesSize > 0" label-class-name="my-label">
					<ElImage
						v-for="item in handlesUrls"
						:key="item.id"
						:src="item"
						:preview-src-list="handlesUrls"
						style="width: 100px; height: 100px; margin-right: 10px"
					></ElImage>
				</ElDescriptionsItem>
			</ElDescriptions>
		</template>
		<!-- 驳回信息 -->
		<template v-if="missionData.states == 8">
			<div class="base-title m-t-12px">驳回信息</div>
			<ElDescriptions :column="2" border label-width="120px">
				<ElDescriptionsItem label="操作人" label-class-name="my-label">{{ missionData.rejectUsername || "--" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="驳回原因" label-class-name="my-label">{{ missionData.reason || "--" }}</ElDescriptionsItem>
			</ElDescriptions>
		</template>
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import { ElDescriptions, ElImage, ElDescriptionsItem, ElTable, ElTableColumn } from "element-plus";
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
const missionId = ref(0);
const missionData = ref({})
const assignUser = ref([])
const assigns = ref([])
const assionUserName = ref([])
const assignLength = ref(0)
const imageUrls = ref([])
const handlesUrls = ref([])
const imageSize = ref(0)
const handlesSize = ref(0)
if (props.id) {
	inspectionData({missionId: props.id, isHandle: 1}).then((res) => {
		var data = res;
		missionId.value = props.id;
		missionData.value = data.bean; //上报信息
		if (data.assignUser.length > 0) {
			//指派人员
			assignUser.value = data.assignUser.substring(0, data.assignUser.lastIndexOf("、"));
		}
		assigns.value = data.assigns; //指派信息
		assionUserName.value = missionData.value.assionUserName;
		assignLength.value = data.assigns.length;
		var files = missionData.value.fileList;
		var handles = data.handles;
		imageUrls.value = [];
		handlesUrls.value = [];
		for (const key in files) {
			//文件数据渲染
			if (Object.hasOwnProperty.call(files, key)) {
				const element = files[key];
				imageUrls.value.push( import.meta.env.VITE_APP_BASE_API + `/minio/downMinio?fileName=${element.fileUrl}`);
			}
		}
		imageSize.value = imageUrls.value.length;
		for (const key in handles) {
			//文件数据渲染
			if (Object.hasOwnProperty.call(handles, key)) {
				const element = handles[key];
				handlesUrls.value.push( import.meta.env.VITE_APP_BASE_API + `/minio/downMinio?fileName=${element.fileUrl}`);
			}
		}
		handlesSize.value = handlesUrls.value.length;
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
.detail-type {
	font-size: 14px;
	font-weight:500;
	color: rgba(0,0,0,0.85);
	height: 40px;
	border: 1px solid rgba(0,0,0,0.15);
	text-align: center;
	cursor: pointer;
	margin-bottom: 12px;

	.type {
		flex:1;
		line-height: 40px;
	}
	.activeType {
		color: #fff;
		background:#345BAD;
	}
}
</style>
