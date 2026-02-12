<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="100px" label-position="top">
		<ElRow :gutter="20">
			<ElCol :span="8">
				<ElFormItem label="用户名称" prop="realName">
					<ElInput v-model="form.realName" placeholder="请输入用户名称" />
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
				<ElFormItem label="用户密码" prop="password">
					<ElInput v-model="form.password" type="password" placeholder="请输入用户密码" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="用户状态" prop="isEnable">
					<ElRadioGroup v-model="form.isEnable">
						<ElRadioButton :value="1">启用</ElRadioButton>
						<ElRadioButton :value="0">停用</ElRadioButton>
					</ElRadioGroup>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="所属单位" prop="unitId">
					<ElTreeSelect
						v-model="form.unitId"
						placeholder="请选择所属单位"
						:data="treeData"
						:props="{
							children: 'children',
							label: 'label',
							value: 'id'
						}"
						filterable
						clearable
						:check-strictly="true"
						:render-after-expand="false"
						@change="handleUnitChange"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="所属部门" prop="deptId">
					<ElSelect v-model="form.deptId" placeholder="请选择所属部门" :disabled="isDeptDisabled" clearable>
						<template v-for="option in deptList" :key="option.id">
							<ElOption :label="option.deptName" :value="option.id"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="16">
				<ElFormItem label="用户角色" prop="roleIds">
					<ElSelect v-model="form.roleIds" placeholder="请选择用户角色" multiple>
						<template v-for="item in roleOptions" :key="item.id">
							<ElOption :label="item.roleName" :value="item.id"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>

			<ElCol :span="24">
				<ElFormItem label="备注" prop="memo">
					<ElInput type="textarea" v-model="form.memo" :rows="3" placeholder="请输入备注" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="设置头像" prop="avatar">
					<div class="flex">
						<ElUpload
							class="avatar-uploader"
							action="/api/minio/importFile"
							:show-file-list="false"
							:on-success="handleAvatarSuccess"
							:before-upload="beforeAvatarUpload"
						>
							<img
								v-if="form.avatar"
								:src="
									form.avatar.includes('http')
										? form.avatar
										: `/api/minio/previewFile?fileName=${form.avatar}`
								"
								class="avatar"
							/>

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
	import { ref, onMounted, reactive, computed, watch } from "vue";
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
		ElIcon,
		ElTreeSelect
	} from "element-plus";
	import { Plus, InfoFilled } from "@element-plus/icons-vue";
	import { findOrg, findAllDeptByUnit } from "@/api/setting/org";
	import { getList } from "@/api/setting/role";
	import { saveUser } from "@/api/setting/user";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		orgTreeData: {
			type: Array,
			default: () => []
		},
		roleList: {
			type: Array,
			default: () => []
		}
	});
	const form = reactive({
		realName: "",
		phone: "",
		loginName: "",
		roleIds: [],
		password: "",
		isEnable: 1,
		unitId: "",
		deptId: "",
		memo: "",
		avatar: ""
	});
	const deptList = ref([]);
	const isDeptDisabled = ref(true);

	const treeData = computed(() => props.orgTreeData);
	const roleOptions = ref([]);

	onMounted(async () => {
		const roleRes = await getList({});
		roleOptions.value = roleRes || [];
	});
	const rules = reactive({
		realName: [{ required: true, message: "请输入用户名称", trigger: "blur" }],
		loginName: [{ required: true, message: "请输入登录名称", trigger: "blur" }],
		roleIds: [{ required: true, message: "请选择用户角色", trigger: "blur" }],
		password: [{ required: true, message: "请输入用户密码", trigger: "blur" }],
		isEnable: [{ required: true, message: "请选择用户状态", trigger: "blur" }],
		unitId: [{ required: true, message: "请选择所属单位", trigger: "blur" }],
		deptId: [{ required: true, message: "请选择所属部门", trigger: "blur" }]
	});
	const handleUnitChange = (value) => {
		form.deptId = "";
		if (value) {
			isDeptDisabled.value = false;
			findAllDeptByUnit({ orgId: value })
				.then((res) => {
					// 确保返回的数据格式正确
					deptList.value = Array.isArray(res) ? res : [];

					// 如果是编辑模式且有部门ID，需要等待部门列表加载完成后再设置deptId
					if (props.rowInfo?.id && props.rowInfo.deptId) {
						// 确保部门ID在部门列表中存在
						const deptExists = deptList.value.some((dept) => dept.id === props.rowInfo.deptId);
						if (deptExists) {
							form.deptId = props.rowInfo.deptId;
						}
					}
				})
				.catch((error) => {
					console.error("获取部门列表失败:", error);
					deptList.value = [];
					form.deptId = "";
				});
		} else {
			isDeptDisabled.value = true;
			deptList.value = [];
		}
	};

	const { registerFormDone, formInstance } = useDialogForm();

	const handleAvatarSuccess = (response: any, uploadFile: any) => {
		// 保存文件名用于提交
		form.avatar = response.data.filename;
		// 创建临时URL用于预览
		const previewUrl = URL.createObjectURL(uploadFile.raw);
		// 更新预览图片的src
		const imgElement = document.querySelector(".avatar-uploader img");
		if (imgElement) {
			imgElement.src = previewUrl;
		}
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

	// 监听rowInfo的变化
	watch(
		() => props.rowInfo,
		(newVal) => {
			if (newVal?.id) {
				// 使用展开运算符和默认值简化赋值
				Object.assign(form, {
					...newVal,
					roleIds: Array.isArray(newVal.roleIds) ? newVal.roleIds : [],
					avatar: newVal.avatar || newVal.headPortrait || "",
					deptId: "" // 先置空，等部门列表加载完成后再设置
				});

				// 如果有单位ID，触发部门列表加载
				if (newVal.unitId) {
					handleUnitChange(newVal.unitId);
				}
			}
		},
		{ immediate: true }
	);

	registerFormDone(async () => {
		const submitData = { ...form };
		if (props.rowInfo.id) {
			submitData.id = props.rowInfo.id;
		}
		await saveUser(submitData);
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
