<template>
	<BaseForm v-bind="form" />
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import BaseForm from "@/core/struct/form/base-form";
import {
	tobeAssigned,
	teamGroupsAllList,
	selectAreaList,
	selectRegionByDevice
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
			label: "标题",
			prop: "title",
			value: "",
			type: "ElInput",
			span: 24,
			attrs: {
				placeholder: "请输入标题",
			}
		},
		{
			label: "上报内容",
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
		{
			type: "ElSelect",
			value: "",
			prop: "taskType",
			label: "严重程度",
			span: 24,
			attrs: {
				placeholder: "请选择严重程度",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "dictLabel",
				value: "dictValue",
				listApi: async () => {
					const data = await getRedisDictList({ dictType: "warnLevel" });
					return data;
				},
				list: []
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "urgency",
			label: "区域名称",
			span: 24,
			attrs: {
				placeholder: "请选择区域名称",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "areaName",
				value: "id",
				listApi: async () => {
					const data = await selectAreaList({});
					return data;
				},
				list: []
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "taskType",
			label: "设备选择",
			span: 24,
			attrs: {
				placeholder: "请选择设备",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "dictLabel",
				value: "dictValue",
				listApi: async () => {
					const data = await selectRegionByDevice(form.value.areaId);
					return data;
				},
				list: []
			}
		},
		{
			type: "ElUpload",
			value: "",
			label: "图片上传",
			prop: "reporTime",
			span: 24,
			attrs: {
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
		reporTime: [
			{
				required: true,
				message: "请选择上报时间",
				trigger: "blur"
			}
		],
		taskGroup: [
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
	const res = await tobeAssigned({ ...form.value, id: props.id });
	return res;
});

const detail = ref({});
// 详情页数据
const assignLength = ref(0);
const imageUrls = ref([]);
if (props.id) {
	// getEntityOperation(props.id).then((res) => {
	// 	detail.value = res
	// });
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
