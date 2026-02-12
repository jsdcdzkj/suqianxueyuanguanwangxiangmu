<template>
	<div class="page-login">
		<div class="login-bg">
			<div class="login-container">
				<BorderBox1>
					<div class="p-72px">
						<div class="login-title">
							欢迎来到<br />
							宿迁学院给水管网监测系统
						</div>

						<BaseForm v-bind="form" v-model:value="form.value" style="width: 100%" />

						<!-- <div class="flex w-100%">
				<ElCheckboxGroup v-model="form.value.rememberMe">
					<ElCheckbox label="记住我" />
				</ElCheckboxGroup>
			</div> -->
						<div w="100%" h="48px">
							<ElButton :loading="isLoading" type="primary" w="100%" h="48px" @click="handleLogin"
								>登录</ElButton
							>
						</div>
					</div>
				</BorderBox1>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import BaseForm from "@/core/struct/form/base-form";
	import { useForm } from "@/core/struct/form/use-base-form";
	import { useUserStore } from "@/store/user";
	import { useAppStore } from "@/store/app";
	import type { FormInstance } from "element-plus";
	import { ElCheckboxGroup, ElCheckbox } from "element-plus";
	import { BorderBox1 } from "@kjgl77/datav-vue3";
	import { markRaw, ref } from "vue";
	import { useRouter } from "vue-router";
	import { User, Lock } from "@element-plus/icons-vue";
	let formInstance = ref<FormInstance>();
	const userStore = useUserStore();
	const appStore = useAppStore();
	const router = useRouter();
	const isLoading = ref(false);
	const { form } = useForm({
		span: 24,

		inline: false,
		size: "large",
		inlineMessage: false,
		showMessage: true,
		labelWidth: 0,
		showExpand: false,
		formItems: [
			{
				prop: "username",
				value: "",
				type: "ElInput",
				span: 24,
				attrs: {
					placeholder: "Username",
					prefixIcon: markRaw(User),
					onKeydown: (event: any) => {
						if (event.keyCode == 13) {
							handleLogin();
						}
					}
				}
			},
			{
				prop: "password",
				value: "",
				type: "ElInput",
				span: 24,
				attrs: {
					placeholder: "Password",
					type: "password",
					prefixIcon: markRaw(Lock),
					showPassword: true,
					onKeydown: (event: any) => {
						if (event.keyCode == 13) {
							handleLogin();
						}
					}
				}
			}
		],
		rules: {
			username: [{ required: true, message: "请输入", trigger: "blur" }],
			password: [{ required: true, message: "请输入", trigger: "blur" }]
		},
		onInstance(instance) {
			formInstance.value = instance;
		}
	});

	const handleLogin = () => {
		const formData = {
			...form.value,
			type: 2
		};
		// 存储密码到 localStorage
		localStorage.setItem("lastLoginPassword", formData.password);
		formInstance.value?.validate().then(async (res) => {
			if (res) {
				try {
					isLoading.value = true;
					await userStore.actionLogin(formData, router);
					await appStore.getMapDict();
					// console.log(router.getRoutes());

					router.replace("/");
					isLoading.value = false;
				} catch (e) {
					isLoading.value = false;
				}
			}
		});
	};
</script>

<style lang="scss" scoped>
	.page-login {
		width: 100vw;
		height: 100vh;
		overflow: hidden;
		background-image: url(../../assets/images/back.jpg);
		background-size: cover;
		position: relative;
	}
	.login_top_bg {
		position: fixed;
		width: 946px;
		height: 772px;
		margin: auto 0;
		top: 0;
		bottom: 0;
		left: 338px;
	}
	::v-deep(.el-form-item) {
		margin-bottom: 20px;
		&.is-error {
		}
	}
	::v-deep(.is-guttered) {
		padding: 0 !important;
	}
	::v-deep(.el-checkbox) {
		height: inherit;
	}
	::v-deep(.el-input__wrapper) {
		background-color: transparent;
		border: 1px solid #409eff;
	}
	::v-deep(.el-input__inner) {
		color: #fff;
	}
	::v-deep(.el-input__icon) {
		color: #e0e1e1;
	}
	.login-bg {
		position: absolute;
		top: 0;
		right: 0;
		bottom: 0;
		width: 640px;
		height: 100%;
		background: rgba(0, 0, 0, 0.65);
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}
	.login-container {
		width: 560px;
		height: 456px;
		padding: 0;
		margin: auto 0;
		.login-title {
			margin-bottom: 48px;
			font-family:
				Alimama ShuHeiTi,
				Alimama ShuHeiTi;
			font-size: 32px;
			color: #fff;
			line-height: 44px;
		}
	}
</style>
