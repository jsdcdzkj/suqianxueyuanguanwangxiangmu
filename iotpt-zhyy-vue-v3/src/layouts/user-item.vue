<script setup lang="ts">
	import { ElAvatar, ElDialog, ElInput } from "element-plus";
	import { SwitchButton, Lock } from "@element-plus/icons-vue";
	import { eventBus, getAppContext } from "@/core";
	const appInstance = getAppContext();
	import { ref, onMounted } from "vue";
	import header from "@/assets/header.png";
	// import { pass } from "@/api/setting/user";
	const loginName = ref("");
	const realName = ref("");
	const Phone = ref("");

	// 弱密码检测函数
	const isWeakPassword = (password: string) => {
		// 密码规则：小写字母+大写字母+数字+特殊符号+长度为8-16位
		const strongRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/;
		return !strongRegex.test(password);
	};

	const handleLogout = () => {
		eventBus.emit("logout");
	};

	// 标记用户是否关闭过弱密码提示弹窗
	const hasClosedWeakPasswordDialog = ref(false);
	// 标记是否因弱密码弹出弹窗
	const isWeakPasswordDialog = ref(false);

	const dialogVisible = ref(false);
	// 使用对象来存储表单数据
	const form = ref({
		oldPassword: "",
		newPassword: ""
	});

	const rules = {
		oldPassword: [{ required: true, message: "请输入旧密码", trigger: "blur" }],
		newPassword: [
			{ required: true, message: "请输入新密码", trigger: "blur" },
			{ min: 8, message: "密码长度不能小于8位", trigger: "blur" },
			{
				pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/,
				message: "密码需包含小写字母、大写字母、数字、特殊符号(@,$,!,%,*,?,&)，且长度为8-16位",
				trigger: "blur"
			}
		]
	};

	const formRef = ref(null);

	const handlePassword = () => {
		dialogVisible.value = true;
		// 重置表单
		if (formRef.value) {
			formRef.value.resetFields();
		}
	};
	onMounted(async () => {
		// await userStore.getUserInfo();
		// if (userStore.userInfo) {
		loginName.value = appInstance.userStore?.userInfo?.userName;
		realName.value = appInstance.userStore?.userInfo?.realName;
		Phone.value = appInstance.userStore?.userInfo?.phone;
		// }
		// 获取存储的密码
		const lastLoginPassword = localStorage.getItem("lastLoginPassword");
		if (lastLoginPassword && isWeakPassword(lastLoginPassword)) {
			dialogVisible.value = true;
			isWeakPasswordDialog.value = true;
		}
	});
	const submitPassword = async () => {
		if (!formRef.value) return;
		try {
			await formRef.value.validate();
			// await pass({
			// 	tempPass: form.value.oldPassword,
			// 	password: form.value.newPassword,
			// 	loginName: loginName.value
			// });
			// 密码修改成功，清除存储的密码
			localStorage.removeItem("lastLoginPassword");
			// 清除关闭标记
			hasClosedWeakPasswordDialog.value = false;
			isWeakPasswordDialog.value = false;
			dialogVisible.value = false;
		} catch (error) {
			console.error("密码修改失败", error);
		}
	};

	// 关闭弹窗时记录标记
	const handleDialogClose = () => {
		hasClosedWeakPasswordDialog.value = true;
	};
</script>

<template>
	<ElPopover placement="top" width="232px">
		<template #reference>
			<ElAvatar :src="header" class="cursor-pointer"></ElAvatar>
		</template>
		<div class="flex flex-col gap-4px">
			<div class="flex flex-justify-between flex-items-center">
				<div class="font-size-16px font-weight-bold">
					{{ realName }}
				</div>
				<div>
					<ElTag type="primary" @click="handlePassword"
						><ElIcon size="20px" style="vertical-align: middle"><Lock /></ElIcon
					></ElTag>
					<ElTag type="danger" @click="handleLogout" style="margin-left: 10px"
						><ElIcon size="20px" style="vertical-align: middle"><SwitchButton /></ElIcon
					></ElTag>
				</div>
			</div>
			<!-- <div>{{ appInstance._config.workId }}</div> -->
			<div>{{ Phone }}</div>
			<!-- <ElDivider direction="horizontal" class="m-t-6px m-b-6px" /> -->
			<!-- <div class="flex gap-6px"> -->
			<!-- <ElTag>{{ appInstance._config.workId }}</ElTag> -->
			<!-- <ElTag>前端小组</ElTag> -->
			<!-- </div> -->
		</div>
	</ElPopover>
	<ElDialog
		v-model="dialogVisible"
		width="590"
		title="修改密码"
		style="padding-bottom: 20px"
		@close="handleDialogClose"
		:show-close="!isWeakPasswordDialog"
		:close-on-click-modal="!isWeakPasswordDialog"
		:close-on-press-escape="!isWeakPasswordDialog"
	>
		<!-- 修改密码 -->
		<template #header>
			<div class="dialog-title" style="text-align: left">
				{{ isWeakPasswordDialog ? "弱密码修改" : "修改密码" }}
			</div>
		</template>
		<!-- 确保使用 #content 插槽 -->
		<ElForm :model="form" :rules="rules" ref="formRef" label-width="80px" style="padding: 20px 30px 20px 20px">
			<ElFormItem label="旧密码" prop="oldPassword">
				<ElInput v-model="form.oldPassword" type="password" placeholder="请输入旧密码" />
			</ElFormItem>
			<ElFormItem label="新密码" prop="newPassword">
				<ElInput v-model="form.newPassword" type="password" placeholder="请输入新密码" />
				<div class="pwd-tips">
					密码规则说明：<br />小写字母+大写字母+数字+特殊符号(@,$,!,%,*,?,&)+长度为8-16位！
				</div>
			</ElFormItem>
		</ElForm>
		<!-- 密码规则说明：小写字母+大写字母+数字+特殊符号+长度为8-16位！ -->

		<template #footer>
			<span class="dialog-footer" style="padding: 20px">
				<ElButton v-if="!isWeakPasswordDialog" @click="dialogVisible = false">取消</ElButton>
				<ElButton type="primary" @click="submitPassword">确定</ElButton>
			</span>
		</template>
	</ElDialog>
</template>

<style scoped>
	.pwd-tips {
		width: 100%;
		margin-top: 10px;
		padding: 10px;
		border-radius: 6px;
		font-size: 14px;
		line-height: 24px;
		color: rgba(0, 0, 0, 0.65);
		background-color: #f8f8f8;
		text-align: left;
	}
</style>
