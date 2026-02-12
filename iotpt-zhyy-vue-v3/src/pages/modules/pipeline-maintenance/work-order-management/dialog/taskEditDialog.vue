<template>
	<BaseForm v-bind="form" />
	<ElForm ref="formStance" :model="form2" label-width="100px" label-position="left">
		<ElFormItem label="图标" prop="icon">
			<ElImage
				class="avatar-uploader-box"
				v-for="(file, index) in files"
				:key="index"
				:src="commomUrl + file.fileUrl"
				fit="fill"
				:preview-src-list="[commomUrl + file.fileUrl]"
			/>
			<ElUpload
				ref="uploadRef"
				action="/api/mission/uploadFile.do"
				:on-success="handleUploadSuccess"
				accept=".jpg,.jpeg,.png"
				:limit="5"
				:headers="{ accessToken: accessToken }"
				:before-upload="beforeUpload"
				class="avatar-uploader-box"
				:show-file-list="false"
			>
				<ElIcon class="avatar-uploader-icon"><Plus /></ElIcon>
			</ElUpload>
		</ElFormItem>
	</ElForm>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import BaseForm from "@/core/struct/form/base-form";
import {
	tobeAssigned,
	teamGroupsAllList,
	selectAreaList,
	selectRegionByDevice,
	saveMission,
	detailsMission
} from "@/api/pipeline-maintenance/work-order";
import { Plus, Delete } from "@element-plus/icons-vue";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
import { ElUpload, ElIcon, ElMessage, ElForm, ElFormItem, ElTag, ElImage } from "element-plus";

const props = defineProps({
	id: number().def()
});
// 文件前置地址
const commomUrl = import.meta.env.VITE_APP_BASE_API + "/minio/downMinio?fileName=";
console.log('commomUrl', commomUrl)

// 获取token的值
const accessToken = localStorage.getItem("accesstoken");
const form2 = ref({});
const formStance = ref(null);
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
				placeholder: "请输入标题"
			}
		},
		{
			label: "上报内容",
			prop: "notes",
			value: "",
			type: "ElInput",
			span: 24,
			attrs: {
				type: "textarea",
				maxlength: 200,
				rows: 4
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "levels",
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
			prop: "areaId",
			label: "区域名称",
			span: 24,
			attrs: {
				placeholder: "请选择区域名称",
				clearable: true,
				async onChange(val) {
					let data = [];
					if (val) {
						data = await selectRegionByDevice({ data: form.value.areaId });
						form.formItems[4].select.list = data;
					}
				}
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
			prop: "pId",
			label: "设备选择",
			span: 24,
			attrs: {
				placeholder: "请选择设备",
				clearable: true,
				onChange(val) {
					console.log("val", val);
					if (val) {
						const target = form.formItems[4].select.list.find((item) => item.pid === val);
						form.value.deviceId = target.id
						form.value.deviceType = target.type;
					} else {
						form.value.deviceId = ""
						form.value.deviceType = "";
					}
				}
			},
			select: {
				type: "ElOption",
				label: "deviceCode",
				value: "pid",
				list: []
			}
		}
	],
	rules: {
		title: [
			{
				required: true,
				message: "请输入标题",
				trigger: "blur"
			}
		],
		notes: [
			{
				required: true,
				message: "请输入上报内容",
				trigger: "blur"
			}
		],
		levels: [
			{
				required: true,
				message: "请选择严重程度",
				trigger: "blur"
			}
		],
		areaId: [
			{
				required: true,
				message: "请选择区域名称",
				trigger: "blur"
			}
		]
	}
});
registerFormDone(async (type: number) => {
	if (form.value.deviceId && files.value.length === 0) {
		ElMessage.error("请上传异常图片");
		throw '请上传异常图片';
	}
	console.log("66666666", form.value);
	const res = await saveMission({ ...form.value, id: props.id, source: 2, states: type == 2? 0 : 1, fileList: files.value });
	return res;
});

const detail = ref({});
// 详情页数据
const assignLength = ref(0);
const imageUrls = ref([]);
if (props.id) {
	detailsMission({ id: props.id }).then((res) => {
		form.value = res.mission
		form.value.pId = res.mission.pid
		form.value.levels = res.mission.levels + ''
		// 如果存在区域名，调用设备列表
		if (res.mission.areaId) {
			selectRegionByDevice({ data: form.value.areaId }).then((data) => {
				form.formItems[4].select.list = data;
			});
		}
		files.value = res.files || []
	});
}
const getImg = (url: string) => {
	return import.meta.env.VITE_APP_BASE_API + "/minio/previewFile?fileName=" + url;
};

const beforeUpload = (file: File) => {
	if (file.size > 1024 * 500) {
		ElMessage.error("图片大于500KB");
		return false;
	}
	return true;
};

const files = ref([]);
const handleUploadSuccess = (response: any, file: any) => {
	console.log("hahahahahaah", response, file);
	if (response.code === 0) {
		files.value.push(response.data);
	} else {
		ElMessage.error(response.logMsg || response.msg || "上传失败");
	}
};
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

.avatar-uploader-box {
	width: 100px;
	height: 100px;
	overflow: hidden;
	display: flex;
	justify-content: center;
	align-items: center;
	cursor: pointer;
	margin-right: 8px;

	:deep(.el-upload) {
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		border-radius: 4px;
		border: 1px solid rgba(0, 0, 0, 0.15);
		&:hover {
			border: 1px solid var(--el-color-primary);
			border-radius: 4px;
			.avatar-uploader-icon {
				color: var(--el-color-primary);
			}
		}
	}

	img {
		width: 100%;
		height: 100%;
	}

	.avatar-uploader-icon {
		font-size: 20px;
		color: #8c8c8c;
	}
}

.el-upload__tip {
	margin-left: 2px;
	color: #999;
	font-size: 14px;
}
</style>
