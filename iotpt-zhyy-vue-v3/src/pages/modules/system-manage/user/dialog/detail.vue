<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="100px" label-position="top">
		<ElRow :gutter="20">
			<ElCol :span="8">
				<ElFormItem label="用户名称" prop="userName">
					<ElInput v-model="form.userName" placeholder="请输入用户名称" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="联系方式" prop="phone">
					<ElInput v-model="form.phone" placeholder="请输入联系方式" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="登录名称" prop="loginName">
					<ElInput v-model="form.loginName" placeholder="请输入登录名称" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="用户角色" prop="roleId">
					<ElSelect v-model="form.roleId" placeholder="请选择用户角色">
						<template v-for="option in roleOptions" :key="option.value">
							<ElOption :label="option.label" :value="option.value"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="用户密码" prop="password">
					<ElInput v-model="form.password" type="password" placeholder="请输入用户密码" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="用户状态" prop="status">
					<ElRadioGroup v-model="form.status">
						<ElRadioButton :value="1">启用</ElRadioButton>
						<ElRadioButton :value="0">停用</ElRadioButton>
					</ElRadioGroup>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="所属单位" prop="unitId">
					<ElSelect v-model="form.unitId" placeholder="请选择所属单位">
						<template v-for="option in unitOptions" :key="option.value">
							<ElOption :label="option.label" :value="option.value"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="所属部门" prop="deptId">
					<ElSelect v-model="form.deptId" placeholder="请选择所属部门">
						<template v-for="option in deptOptions" :key="option.value">
							<ElOption :label="option.label" :value="option.value"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="备注" prop="remark">
					<ElInput type="textarea" v-model="form.remark" :rows="3" placeholder="请输入备注" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="设置头像" prop="avatar">
					<div class="flex">
						<ElUpload
							class="avatar-uploader"
							action="/api/upload"
							:show-file-list="false"
							:on-success="handleAvatarSuccess"
							:before-upload="beforeAvatarUpload"
						>
							<img v-if="form.avatar" :src="form.avatar" class="avatar" />
							<ElIcon class="avatar-uploader-icon">
								<Plus />
							</ElIcon>
						</ElUpload>
						<div class="flex1 ml-12px">
							<div class="avatar-uploader-desc flex">
								<ElIcon class="avatar-uploader-desc-icon"><InfoFilled /></ElIcon>
								<div class="avatar-uploader-desc-text">
									1.请上传或采集正面冠照，确保图片中只存在一个人脸，人脸在图片中的占比不超过整张图片的2/3，整张图片的宽:高之比不超过1:2。
									2.脸部保持清洁，胡子变化不要太大，人脸需双眼睁开、表情自然、建议头发不要遮挡额头。3.使用常规光线色彩(非发白、泛黄、背光等)，人脸无遮挡(如戴帽子、口罩、眼镜等)、无修图。4.图片格式为jpg、jpeg、png，大小小于4M，100K左右最佳，像素范围处于300x300分辨率600x600之间，建议图片像素在50x500左右。
								</div>
							</div>
							<div class="flex">
								<div class="example mr-21px">
									<div class="example-title">正确照片示例</div>
									<div class="example-img">
										<img
											src="@/assets/images/setting/example1.png"
											alt="正确照片示例"
											class="avatar-uploader-desc-img"
										/>
									</div>
								</div>
								<div class="example">
									<div class="example-title">错误照片示例</div>
									<div class="example-img">
										<img
											src="@/assets/images/setting/example2.png"
											alt="错误照片示例"
											class="avatar-uploader-desc-img mr-12px"
										/>
										<img
											src="@/assets/images/setting/example3.png"
											alt="错误照片示例"
											class="avatar-uploader-desc-img mr-12px"
										/>
										<img
											src="@/assets/images/setting/example4.png"
											alt="错误照片示例"
											class="avatar-uploader-desc-img"
										/>
									</div>
								</div>
							</div>
						</div>
					</div>
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import {
		ElForm,
		ElFormItem,
		ElInput,
		ElRadioGroup,
		ElRadioButton,
		ElSelect,
		ElOption,
		ElRow,
		ElCol,
		ElUpload,
		ElMessage,
		ElIcon
	} from "element-plus";
	import { Plus, InfoFilled } from "@element-plus/icons-vue";

	const form = reactive({
		userName: "",
		phone: "",
		loginName: "",
		roleId: "",
		password: "",
		status: 1,
		unitId: "",
		deptId: "",
		remark: "",
		avatar: ""
	});

	const roleOptions = [
		{ label: "管理员", value: "1" },
		{ label: "普通用户", value: "2" }
	];

	const unitOptions = [
		{ label: "单位1", value: "1" },
		{ label: "单位2", value: "2" }
	];

	const deptOptions = [
		{ label: "部门1", value: "1" },
		{ label: "部门2", value: "2" }
	];

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const rules = reactive({
		userName: [{ required: true, message: "请输入用户名称", trigger: "blur" }],
		loginName: [{ required: true, message: "请输入登录名称", trigger: "blur" }],
		roleId: [{ required: true, message: "请选择用户角色", trigger: "blur" }],
		password: [{ required: true, message: "请输入用户密码", trigger: "blur" }],
		status: [{ required: true, message: "请选择用户状态", trigger: "blur" }],
		unitId: [{ required: true, message: "请选择所属单位", trigger: "blur" }],
		deptId: [{ required: true, message: "请选择所属部门", trigger: "blur" }]
	});

	const { registerFormDone, formInstance } = useDialogForm();

	const handleAvatarSuccess = (response: any, uploadFile: any) => {
		form.avatar = URL.createObjectURL(uploadFile.raw);
	};

	const beforeAvatarUpload = (rawFile: any) => {
		if (rawFile.type !== "image/jpeg" && rawFile.type !== "image/png") {
			ElMessage.error("头像必须是JPG或PNG格式!");
			return false;
		} else if (rawFile.size / 1024 / 1024 > 2) {
			ElMessage.error("头像大小不能超过2MB!");
			return false;
		}
		return true;
	};

	if (props.rowInfo.id) {
		for (const key in form) {
			if (key in form && key in props.rowInfo) {
				form[key] = props.rowInfo[key];
			}
		}
	}

	registerFormDone(async () => {
		const submitData = { ...form };
		if (props.rowInfo.id) {
			submitData.id = props.rowInfo.id;
		}
		// TODO: 调用保存用户信息的API
		return true;
	});
</script>

<style scoped lang="scss">
	::v-deep {
		.avatar-uploader .el-upload {
			border-radius: 6px;
			cursor: pointer;
			position: relative;
			overflow: hidden;
			transition: var(--el-transition-duration-fast);
			width: 96px;
			height: 96px;
			border-radius: 4px;
			border: 1px solid rgba(0, 0, 0, 0.15);
		}

		.avatar-uploader .el-upload:hover {
			border-color: var(--el-color-primary);
		}
	}
	.avatar {
		width: 178px;
		height: 178px;
		display: block;
	}
	.avatar-uploader-icon {
		font-size: 18px;
		color: #8c939d;
	}
	.avatar-uploader-desc {
		margin-bottom: 12px;
		padding: 10px 12px;
		font-size: 14px;
		color: #333;
		line-height: 22px;
		background: #f2f8ff;
		border-radius: 4px 4px 4px 4px;
		.avatar-uploader-desc-icon {
			margin-right: 6px;
			margin-top: 2px;
			font-size: 18px;
			color: #345bad;
		}
	}
	.example {
		.example-title {
			margin-bottom: 8px;
			font-size: 14px;
			color: #333;
			line-height: 22px;
		}
	}
</style>
