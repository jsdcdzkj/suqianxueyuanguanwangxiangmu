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
		<div class="base-title m-t-12px">指派信息</div>
		<BaseForm v-bind="form" />
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import BaseForm from "@/core/struct/form/base-form";
import {
	tobeAssigned,
	teamGroupsAllList,
	inspectionData
} from "@/api/pipeline-maintenance/work-order";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
import { ElDescriptions, ElDescriptionsItem,ElPagination, ElTable, ElTableColumn, ElButton } from "element-plus";

const props = defineProps({
	id: number().def(),
});

const { form, registerFormDone } = useDialogStructForm({
	labelWidth: 100,
	span: 12,
	expandSpan: 6,
	notMargn: false,
	inline: false,
	showExpand: false,
	showMessage: true,
	labelPosition: "left",
	formItems: [
		{
			type: "ElSelect",
			value: "",
			prop: "taskType",
			label: "任务类型",
			attrs: {
				placeholder: "任务类型",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "dictLabel",
				value: "dictValue",
				listApi: async () => {
					const data = await getRedisDictList({ dictType: "taskTypes" });
					return data;
				},
				list: []
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "urgency",
			label: "紧急程度",
			attrs: {
				placeholder: "紧急程度",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "dictLabel",
				value: "dictValue",
				listApi: async () => {
					const data = await getRedisDictList({ dictType: "missionUrgency" });
					return data;
				},
				list: []
			}
		},
		{
			type: "ElDatePicker",
			value: "",
			label: "截止时间",
			prop: "deadlineTime",
			attrs: {
				type: "datetime",
				placeholder: "截止时间",
				valueFormat: "YYYY-MM-DD HH:mm:ss"
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "teamGroupsId",
			label: "任务组",
			attrs: {
				placeholder: "任务组",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "name",
				value: "id",
				listApi: async () => {
					const data = await teamGroupsAllList({});
					return data;
				},
				list: []
			}
		},
		{
			label: "备注",
			prop: "notes",
			value: "",
			type: "ElInput",
			span: 24,
			attrs: {
				type: 'textarea',
				maxlength: 200,
				rows: 4,
			}
		},
	],
	rules: {
		taskType: [
			{
				required: true,
				message: "请选择任务类型",
				trigger: "blur"
			}
		],
		urgency: [
			{
				required: true,
				message: "请选择紧急程度",
				trigger: "blur"
			}
		],
		deadlineTime: [
			{
				required: true,
				message: "请选择截止时间",
				trigger: "blur"
			}
		],
		teamGroupsId: [
			{
				required: true,
				message: "请选择任务组",
				trigger: "blur"
			}
		],
	},
});
registerFormDone(async() => {
	console.log('66666666', form.value)
	const res = await tobeAssigned({ ...form.value, missionId: props.id, id: localStorage.getItem("userId") });
	return res;
});

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
				imageUrls.value.push( `/minio/downMinio?fileName=${element.fileUrl}`);
			}
		}
		imageSize.value = imageUrls.value.length;
		for (const key in handles) {
			//文件数据渲染
			if (Object.hasOwnProperty.call(handles, key)) {
				const element = handles[key];
				handlesUrls.value.push( `/minio/downMinio?fileName=${element.fileUrl}`);
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
	border-radius:4px;
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
