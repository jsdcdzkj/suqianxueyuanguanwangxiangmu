<template>
	<div>
		<div class="base-title">上报信息</div>
		<ElDescriptions :column="2" border label-width="120px">
			<ElDescriptionsItem label="标题" label-class-name="my-label">{{ detail.title || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="来源" label-class-name="my-label">{{
				detail.sourceName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="严重程度" label-class-name="my-label">{{
				detail.levelsName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="上报人" label-class-name="my-label">{{
				detail.userName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="上报时间" label-class-name="my-label">{{
				detail.reportingTime || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="区域名称" v-if="4 != detail.source" label-class-name="my-label">{{
				detail.areaName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="异常设备" v-if="4 != detail.source" label-class-name="my-label">
				<span v-if="detail.deviceName == '' || detail.deviceName == null">-</span>
				<span v-else>{{ detail.deviceName }}</span>
			</ElDescriptionsItem>
			<ElDescriptionsItem label="内容" label-class-name="my-label">{{ detail.notes || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="异常图片" v-if="4 != detail.source" label-class-name="my-label">
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
		<template v-if="detail.serviceType">
			<div class="base-title m-t-12px">服务类型</div>
			<ElDescriptions :column="2" border label-width="120px">
				<ElDescriptionsItem label="任务类型" label-class-name="my-label">{{
					detail.serviceType == 2 ? "有偿服务" : "公区服务" || "--"
				}}</ElDescriptionsItem>
			</ElDescriptions>
		</template>
		<div class="base-title m-t-12px" v-if="assignLength > 0">指派信息</div>
		<ElDescriptions :column="2" border label-width="120px" v-for="(assign, index) in assigns" :key="index">
			<ElDescriptionsItem label="任务类型" label-class-name="my-label">{{
				assign.taskTypeName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="紧急程度" label-class-name="my-label">{{
				assign.urgencyName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="截止时间" label-class-name="my-label">{{
				assign.deadline || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="任务组" label-class-name="my-label">{{
				assign.teamGroupsName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="备注" label-class-name="my-label">{{ assign.notes || "--" }}</ElDescriptionsItem>
		</ElDescriptions>
		<!-- 耗材类目 -->
		<template v-if="detail.consumables && detail.consumables.length">
			<div class="base-title m-t-12px">耗材类目</div>
			<ElTable border :data="detail.consumables">
				<ElTableColumn label="序号" type="index" width="80px" align="center"></ElTableColumn>
				<ElTableColumn label="耗材名称" align="center" prop="consumableName"></ElTableColumn>
				<ElTableColumn label="金额（元）" align="center" prop="money"></ElTableColumn>
			</ElTable>
		</template>
		<div class="base-title m-t-12px">处理信息</div>
		<BaseForm v-bind="form" />
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import BaseForm from "@/core/struct/form/base-form";
import { tobeAssigned, teamGroupsAllList, inspectionData } from "@/api/pipeline-maintenance/work-order";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
import { ElDescriptions, ElDescriptionsItem, ElPagination, ElTable, ElTableColumn, ElButton } from "element-plus";

const props = defineProps({
	id: number().def()
});

// 班组数据
const groupsList = ref([]);
const getAllgroundList = () => {
	teamGroupsAllList().then((res) => {
		groupsList.value = [];
		// 先处理一下班组数据
		for (let key in res.data) {
			res.data[key] = res.data[key].filter((val) => val.realName);
			if (res.data[key].length == 0) delete res.data[key];
		}
		for (let key in res.data) {
			groupsList.value.push({
				label: key,
				options: res.data[key]
			});
		}
	});
};
getAllgroundList();

// 详情页数据
const assignLength = ref(0);
const imageUrls = ref([]);
const detail = ref({});
const assigns = ref([]);
const assignUser = ref("");
const groupUsers = ref([]);
const imageSize = ref([]);

if (props.id) {
	inspectionData({ missionId: row }).then((res) => {
		var data = res.data;
		detail.value = data.bean; //上报信息
		assigns.value = data.assigns; //指派信息
		if (data.assignUser.length > 0) {
			//指派人员
			assignUser.value = data.assignUser.substring(0, data.assignUser.lastIndexOf("、"));
		}
		groupUsers.value = data.groupUsers; //获取处理人员
		var files = detail.value.fileList;
		imageUrls.value = [];
		for (const key in files) {
			//文件数据渲染
			if (Object.hasOwnProperty.call(files, key)) {
				const element = files[key];
				// imageUrls.value.push(process.env.VUE_APP_FILE_URL + element.fileUrl)
				imageUrls.value.push("/minio/downMinio?fileName=" + element.fileUrl);
			}
		}
		imageSize.value = imageUrls.value;
	});
}
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
			label: "指派处理人",
			prop: "assignUser",
			value: "",
			type: "ElInput",
			span: 24,
			attrs: {
				placeholder: "请输入指派处理人",
			}
		},
		{
			label: "处理信息",
			prop: "crunch",
			value: "",
			type: "ElInput",
			span: 24,
			attrs: {
				type: "textarea",
				placeholder: "请输入处理信息",
				maxlength: 200,
				row:4
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "handleId",
			label: "处理人",
			attrs: {
				placeholder: "请选择处理人",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "realName",
				value: "userId",
				list: groupUsers.value
			}
		},
		{
			type: "ElDatePicker",
			value: "",
			label: "截止日期",
			prop: "startTime",
			attrs: {
				type: "datetime",
				placeholder: "截止日期",
				valueFormat: "YYYY-MM-DD HH:mm:ss"
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "assistHandleIds",
			label: "协助人",
			attrs: {
				placeholder: "请选择协助人",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "realName",
				value: "userId", // 这点原来逻辑是拼上了groupId
				list: groupsList.value
			}
		}
	],
	rules: {
		handleId: [
			{
				required: true,
				message: "请选择处理人",
				trigger: "change"
			}
		],
		crunch: [
			{
				required: true,
				message: "请输入处理信息",
				trigger: "blur"
			}
		],
		startTime: [
			{
				required: true,
				message: "请选择截止时间",
				trigger: "blur"
			}
		],
		taskGroup: [
			{
				required: true,
				message: "请选择任务组",
				trigger: "blur"
			}
		]
	}
});
registerFormDone(async () => {
	console.log("66666666", form.value);
	const res = await tobeAssigned({ ...form.value, id: props.id });
	return res;
});
</script>
<style lang="scss" scoped>
:deep(.my-label) {
	font-size: 14px;
	font-weight: 500;
	color: rgba(0, 0, 0, 0.85);
	background: #f8f8f8 !important;
}
.base-title {
	height: 40px;
	line-height: 40px;
	margin-bottom: 12px;
	background: #f8f8f8;
	border-radius: 4px 4px 4px 4px;
	padding: 0 12px;
	font-size: 16px;
	color: rgba(0, 0, 0, 0.85);
	margin-bottom: 12px;
}
.detail-type {
	font-size: 14px;
	font-weight: 500;
	color: rgba(0, 0, 0, 0.85);
	height: 40px;
	border: 1px solid rgba(0, 0, 0, 0.15);
	text-align: center;
	cursor: pointer;
	border-radius: 4px;
	margin-bottom: 12px;

	.type {
		flex: 1;
		line-height: 40px;
	}
	.activeType {
		color: #fff;
		background: #345bad;
	}
}
</style>
