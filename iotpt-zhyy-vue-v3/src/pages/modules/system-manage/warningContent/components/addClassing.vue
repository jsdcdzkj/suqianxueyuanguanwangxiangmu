<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-position="top">
		<ElFormItem label="分类名称：" prop="name">
			<ElInput v-model="form.name" placeholder="请输入分类名称" />
		</ElFormItem>
		<ElFormItem label="分类编号：" prop="code">
			<ElInput v-model="form.code" placeholder="请输入分类编号" />
		</ElFormItem>
		<ElFormItem label="告警分组：" prop="alarmGroup">
			<ElSelect v-model="form.alarmGroup" placeholder="请选择告警分组" clearable>
				<ElOption v-for="item in warnGroupList" :key="item.id" :label="item.name" :value="item.id" />
			</ElSelect>
		</ElFormItem>
		<ElFormItem label="警种：" prop="alarmType">
			<ElSelect v-model="form.alarmType" placeholder="请选择警种" clearable>
				<ElOption
					v-for="item in alarmTypeList"
					:key="item.id"
					:label="item.dictLabel"
					:value="item.dictValue"
				/>
			</ElSelect>
		</ElFormItem>
		<ElFormItem label="图片上传" prop="icon">
			<template #label>
				<span class="">
					图片上传
					<span class="el-upload__tip">(只能上传一张jpg/png文件，且不超过500kb）</span>
				</span>
			</template>
			<ElUpload
				ref="uploadRef"
				:action="actionUrl"
				:on-success="handleUploadSuccess"
				accept=".jpg,.jpeg,.png"
				:before-upload="beforeUpload"
				class="avatar-uploader-box"
				:show-file-list="false"
			>
				<img v-if="imageUrl" :src="imageUrl" class="avatar" />
				<ElIcon v-else class="avatar-uploader-icon"><Plus /></ElIcon>
			</ElUpload>
		</ElFormItem>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, reactive } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElMessage, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElUpload, ElIcon } from "element-plus";
	import { Plus } from "@element-plus/icons-vue";
	import { saveAlarmCategory } from "@/api/setting/warnInfo";

	const props = defineProps({
		warnGroupList: {
			type: Array,
			default: () => []
		},
		alarmTypeList: {
			type: Array,
			default: () => []
		},
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const actionUrl = import.meta.env.VITE_APP_BASE_API + "/minio/importFile";
	const imageUrl = ref("");
	const uploadRef = ref();

	const form = reactive({
		name: "",
		code: "",
		alarmGroup: "",
		alarmType: "",
		icon: ""
	});

	const rules = reactive({
		name: [{ required: true, message: "请输入分类名称", trigger: "blur" }],
		code: [{ required: true, message: "请输入分类编号", trigger: "blur" }],
		alarmGroup: [{ required: true, message: "请选择告警分组", trigger: "change" }],
		alarmType: [{ required: true, message: "请选择警种", trigger: "change" }],
		icon: [{ required: true, message: "请上传图片", trigger: "change" }]
	});

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

	const handleUploadSuccess = (response: any, file: any) => {
		imageUrl.value = URL.createObjectURL(file.raw);
		if (response.code === 0) {
			form.icon = response.data.filename;
		} else {
			ElMessage.error(response.logMsg || response.msg || "上传失败");
			uploadRef.value?.clearFiles();
		}
	};

	const { registerFormDone, formInstance } = useDialogForm();

	// 初始化数据
	if (props.rowInfo.id) {
		Object.assign(form, props.rowInfo);
		if (form.alarmType) {
			form.alarmType = form.alarmType.toString();
		}
		if (form.icon) {
			imageUrl.value = getImg(form.icon);
		}
	}

	registerFormDone(async () => {
		try {
			await formInstance.value?.validate();
			await saveAlarmCategory(form);
			ElMessage.success("保存成功");
			return true;
		} catch (error) {
			console.error("保存失败:", error);
			ElMessage.error("保存失败，请检查输入信息");
			return false;
		}
	});
</script>

<style scoped lang="scss">
	.avatar-uploader-box {
		width: 100px;
		height: 100px;
		overflow: hidden;
		display: flex;
		justify-content: center;
		align-items: center;
		cursor: pointer;

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
