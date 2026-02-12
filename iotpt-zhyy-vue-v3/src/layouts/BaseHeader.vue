<script lang="tsx" setup>
	import { useAppStore } from "@/store/app";
	import { ref, watch, onMounted, onUnmounted } from "vue";
	import { useRoute, useRouter } from "vue-router";
	import BaseParentMenus from "./BaseParentMenus";
	import { eventBus, getAppContext } from "@/core";
	import Logo from "./logo.vue";
	import { useUserStore } from "@/store/user";
	import { UapiClient } from "uapi-sdk-typescript";
	import { changePassword } from "@/api/setting/user";
	const client = new UapiClient("https://uapis.cn");

	const appContext = getAppContext();
	const app = useAppStore();
	const route = useRoute();
	const cRoutes = ref([]);
	const roueter = useRouter();
	const defaultActive = ref("");
	const user = useUserStore();
	const defaultOpend = ref("");
	const loginName = ref("");
	const realName = ref("");
	const Phone = ref("");
	const weatherInfo = ref({
		temperature: "",
		weather: "",
		windDirection: "",
		windLevel: "",
		icon: ""
	});

	// 添加时间相关变量
	const currentTime = ref(new Date());
	let timer = null;

	const handleLogout = () => {
		user.actionLogout();
		roueter.replace("/login");
	};

	// 弱密码检测函数
	const isWeakPassword = (password: string) => {
		// 密码规则：小写字母+大写字母+数字+特殊符号+长度为8-16位
		const strongRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/;
		return !strongRegex.test(password);
	};
	// 标记用户是否关闭过弱密码提示弹窗
	const hasClosedWeakPasswordDialog = ref(false);
	// 标记是否因弱密码弹出弹窗
	const isWeakPasswordDialog = ref(false);

	const dialogVisible = ref(false);
	// 使用对象来存储表单数据
	const form = ref({
		oldPassWord: "",
		password: "",
		confirmPassword: ""
	});

	const rules = {
		oldPassWord: [{ required: true, message: "请输入旧密码", trigger: "blur" }],
		password: [
			{ required: true, message: "请输入新密码", trigger: "blur" },
			{ min: 8, message: "密码长度不能小于8位", trigger: "blur" },
			{
				pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/,
				message: "密码需包含小写字母、大写字母、数字、特殊符号(@,$,!,%,*,?,&)，且长度为8-16位",
				trigger: "blur"
			}
		],
		confirmPassword: [
			{ required: true, message: "请确认新密码", trigger: "blur" },
			{
				validator: (rule, value, callback) => {
					if (value !== form.value.password) {
						callback(new Error("两次输入的密码不一致"));
					} else {
						callback();
					}
				},
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
	// 更新时间的函数
	const updateTime = () => {
		currentTime.value = new Date();
	};

	// 格式化时间
	const formatTime = (date) => {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, "0");
		const day = String(date.getDate()).padStart(2, "0");
		const hours = String(date.getHours()).padStart(2, "0");
		const minutes = String(date.getMinutes()).padStart(2, "0");
		const seconds = String(date.getSeconds()).padStart(2, "0");
		return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
	};
	onMounted(async () => {
		console.log(appContext?.userStore);
		loginName.value = appContext?.userStore.userInfo?.userName;
		realName.value = appContext?.userStore.userInfo?.realName;
		Phone.value = appContext?.userStore.userInfo?.phone;
		// await userStore.getUserInfo();
		// if (userStore.userInfo) {
		// 	loginName.value = userStore.userInfo.username;
		// 	realName.value = userStore.userInfo.sysUser.realName;
		// 	Phone.value = userStore.userInfo.sysUser.phone;
		// }
		// 获取存储的密码
		const lastLoginPassword = localStorage.getItem("lastLoginPassword");
		if (lastLoginPassword && isWeakPassword(lastLoginPassword)) {
			dialogVisible.value = true;
			isWeakPasswordDialog.value = true;
		}
		// else if (!appContext?.userStore.userInfo.customerId) {
		// 	customerVisible.value = true;
		// }
		// 添加定时器
		timer = setInterval(updateTime, 1000);

		// 获取天气信息
		getWeather();
		// 每30分钟更新一次天气
		setInterval(getWeather, 30 * 60 * 1000);
	});
	onUnmounted(() => {
		// 清除定时器
		if (timer) {
			clearInterval(timer);
			timer = null;
		}
	});
	const submitPassword = async () => {
		if (!formRef.value) return;
		try {
			await formRef.value.validate();
			await changePassword({
				// id: appContext?.userStore.userId,
				oldPassWord: form.value.oldPassWord,
				password: form.value.password
			});
			// 密码修改成功，清除存储的密码
			localStorage.removeItem("lastLoginPassword");
			// 清除关闭标记
			hasClosedWeakPasswordDialog.value = false;
			isWeakPasswordDialog.value = false;
			dialogVisible.value = false;
			handleLogout();
		} catch (error) {
			console.error("密码修改失败", error);
		}
	};

	// 关闭弹窗时记录标记
	const handleDialogClose = () => {
		hasClosedWeakPasswordDialog.value = true;
	};

	watch(
		() => route.fullPath,
		(r) => {
			if (route.matched.length <= 1) return;

			cRoutes.value = appContext?.userStore.menus.find((item) => item.fullPath == route.matched[1].path).children;

			defaultActive.value = route.matched[1].path;
		},
		{ immediate: true, deep: true }
	);

	// // 请求关联客户
	// const customerList = ref({});
	// const getCustomer = async () => {
	// 	customerList.value = await selectListAll({});
	// };
	// getCustomer();

	const formCustomer = ref({});
	const formCustomerRef = ref();
	const customerVisible = ref(false);
	const contact = async () => {
		if (!formCustomerRef.value) return;
		try {
			await formCustomerRef.value.validate();
			// await editUserCustomer({
			//   id: appContext?.userStore.userId,
			//   customerId: formCustomer.value.customerId,
			// });
			customerVisible.value = false;
		} catch (error) {
			console.error("客户关联失败", error);
		}
	};
	// console.log(appContext);

	const getWeather = async () => {
		try {
			const payload = {
				city: "宿城区",
				adcode: "321302", // 宿迁市宿城区行政区划代码
				extended: true,
				indices: false,
				forecast: false
			};
			const response = await (client as any).misc.getMiscWeather(payload);
			if (response) {
				weatherInfo.value = {
					temperature: response.temperature + "℃",
					weather: response.weather,
					windDirection: response.wind_direction,
					windLevel: response.wind_power + "级",
					icon: getWeatherIcon(response.weather)
				};
			}
		} catch (error) {
			console.error("获取天气信息失败:", error);
		}
	};

	const getWeatherIcon = (weather: string) => {
		const iconMap: { [key: string]: string } = {
			晴: "☀️",
			多云: "⛅",
			阴: "☁️",
			小雨: "🌦️",
			中雨: "🌧️",
			大雨: "⛈️",
			雷阵雨: "🌩️",
			小雪: "🌨️",
			中雪: "❄️",
			大雪: "🌨️",
			雾: "🌫️",
			霾: "😷"
		};
		return iconMap[weather] || "❓";
	};
</script>

<template>
	<section class="layout-header">
		<div class="flex items-center justify-between h-100% layout-header__content">
			<Logo />
			<div class="flex-1 flex justify-center h-100%">
				<!-- <BaseParentMenus
					:raw-list="appContext?.userStore.menus"
					:default-active="defaultActive"
					:collapse="app.collapse"
					:ch-list="appContext?.userStore.menus"
					:ch-active="route.fullPath"
					/> -->
			</div>
			<div class="header-tips-item">安全运行：<span class="text-primary">345</span>天</div>
			<div class="header-tips-item">{{ formatTime(currentTime) }}</div>
			<div class="header-tips-item">
				<span class="mr-10px">{{ weatherInfo.icon }}</span>
				<span class="mr-10px" style="color: #40bfa8">{{ weatherInfo.temperature }}</span>
				<span class="mr-10px" style="color: #40bfa8">{{ weatherInfo.weather }}</span>
				<span class="mr-10px">{{ weatherInfo.windDirection }}</span>
				<span>{{ weatherInfo.windLevel }}</span>
			</div>

			<div class="flex items-center p-r-12px">
				<el-dropdown>
					<span style="color: #fff">
						{{ appContext?.userStore.userInfo?.realName }}
						<i class="ri-arrow-down-s-fill"></i>
					</span>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item @click="handlePassword">修改密码</el-dropdown-item>
							<el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
			</div>
		</div>

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
				<ElFormItem label="旧密码" prop="oldPassWord">
					<ElInput v-model="form.oldPassWord" type="password" placeholder="请输入旧密码" />
				</ElFormItem>
				<ElFormItem label="新密码" prop="password">
					<ElInput v-model="form.password" type="password" placeholder="请输入新密码" />
				</ElFormItem>
				<ElFormItem label="确认密码" prop="confirmPassword">
					<ElInput v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" />
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
	</section>
</template>

<style lang="scss" scoped>
	.layout-header {
		height: var(--app-page-header-height);
		background: var(--app-page-header-bg);
		display: flex;
		width: 100%;
		flex-direction: column;
		// cursor: pointer;
		position: relative;
		z-index: 10;

		&__tag {
			padding: 0 var(--content-padding-size);
			height: 38px;
			line-height: 38px;
			text-align: left;
			font-weight: normal;
			font-size: 14px;
			color: rgba(0, 0, 0, 0.85);
		}
	}
	.company-name {
	}
	::deep(.el-dropdown) {
		color: #ffffff !important;
	}

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
	.header-tips-item {
		padding-right: 24px;
		margin-right: 24px;
		font-size: 14px;
		color: #c3c7cd;
		line-height: 20px;
		border-right: 1px solid rgba(255, 255, 255, 0.3);
		.text-primary {
			color: #40bfa8;
		}
	}
</style>
