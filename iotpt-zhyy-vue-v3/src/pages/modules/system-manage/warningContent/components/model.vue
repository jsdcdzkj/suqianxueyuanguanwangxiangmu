<template>
	<ElForm ref="formInstance" :model="form">
		<ElFormItem>
			<ElUpload
				class="upload-dragger"
				drag
				:action="actionUrl"
				accept=".xlsx, .xls"
				:limit="1"
				ref="uploadRef"
				name="file"
				:show-file-list="false"
				:headers="{ accessToken: token }"
				:on-success="handleUploadSuccess"
			>
				<i class="ri-upload-cloud-fill"></i>
				<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
				<template #tip>
					<div class="el-upload__tip">
						<ElLink type="primary" @click="handleDownload">点击下载模板</ElLink>
						，只能上传excel文件
					</div>
				</template>
			</ElUpload>
		</ElFormItem>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref } from "vue";
	import { ElMessage, ElUpload, ElForm, ElFormItem, ElButton, ElLink } from "element-plus";
	import { UploadFilled } from "@element-plus/icons-vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { exportTemplate } from "@/api/setting/warnInfo";
	import { useUserStore } from "@/store/user";

	const userStore = useUserStore();
	const token = userStore.token;
	const actionUrl = import.meta.env.VITE_APP_BASE_API + "/alarm/content/upload/data";
	const uploadRef = ref();

	const form = ref({});

	const { registerFormDone, formInstance } = useDialogForm();

	const handleDownload = async () => {
		try {
			const res = await exportTemplate();
			let fileName = "告警模板.xls";
			let objectUrl = URL.createObjectURL(new Blob([res.data], { type: "application/vnd.ms-excel" }));
			const link = document.createElement("a");
			link.download = decodeURI(fileName);
			link.href = objectUrl;
			link.click();
			ElMessage.success("下载成功");
		} catch (error) {
			console.error("下载失败:", error);
			ElMessage.error("下载失败，请重试");
		}
	};

	const handleUploadSuccess = (response: any, file: any, fileList: any[]) => {
		if (response && response.code === 0) {
			ElMessage.success("上传成功");
			uploadRef.value?.clearFiles();
			return true;
		} else {
			ElMessage.error(response.msg || "上传失败");
			uploadRef.value?.clearFiles();
			return false;
		}
	};

	registerFormDone(() => {
		return true;
	});
</script>

<style scoped lang="scss">
	.upload-dragger {
		width: 100%;
	}
	.el-upload__tip {
		text-align: center;
		margin-top: 8px;
	}
	.ri-upload-cloud-fill {
		font-size: 64px;
		color: #c0c4cc;
	}
</style>
