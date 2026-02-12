import { getMenus } from "@/api/common/common";
import { getUserInfo, login } from "@/api/common/login";
import { dynamicRouter } from "@/core";
import { defineStore } from "pinia";
import type { Router } from "vue-router";

// 导出用户信息类型
export type UserInfo = {
	roles: { id: string; roleName: string; roleCode: string }[];
	id: string; // 用户ID
	name: string; // 用户名
	avatar: string; // 用户头像
	menuTrees: any[]; // 用户菜单树
	realName: string; // 用户真实姓名
	phone: number; // 用户电话
	userName: string; // 用户登录名
};

// 定义用户存储
export const useUserStore = defineStore("user", {
	state: () => {
		return {
			token: localStorage.getItem("accesstoken") || "", // 用户token
			userInfo: null as UserInfo | null, // 用户信息
			loading: false, // 加载状态
			userId: localStorage.getItem("userId") || "", // 用户ID
			menus: [] as any[] // 用户菜单
		};
	},
	actions: {
		// 登录
		async actionLogin(model: any, router: Router) {
			const userInfo = await login(model); // 调用登录接口
			this.token = userInfo.token.tokenValue; // 设置用户token
			this.userId = userInfo.user.id; // 设置用户ID
			this.userInfo = userInfo.user; // 设置用户信息
			// const menus = await getMenus();
			localStorage.setItem("accesstoken", this.token); // 存储用户token
			localStorage.setItem("userId", userInfo.user.id); // 存储用户ID
			this.menus = dynamicRouter(router, userInfo.permissions); // 设置用户菜单
			console.log("menus4444444444:", this.menus);
			// this.menus = dynamicRouter(router, menus);
			// return userInfo; // 返回用户信息
			// return menus; // 返回用户信息
		},
		// 已登录
		async alreadyLogin(router: Router) {
			// if (this.isLogin) {
			const sysUser = await getUserInfo(this.token); // 调用获取用户信息接口
			this.userInfo = sysUser.user; // 设置用户信息
			this.menus = dynamicRouter(router, sysUser.permissions); // 设置用户菜单
			console.log("menus4444444444:", this.menus);
			// const menus = await getMenus();
			// this.menus = dynamicRouter(router, menus); // 设置
		},
		// 登出
		async actionLogout() {
			this.token = ""; // 清空用户token
			this.userId = ""; // 清空用户ID
			this.userInfo = null; // 清空用户信息
			localStorage.removeItem("accesstoken"); // 移除用户token
			localStorage.removeItem("userId"); // 移除用户ID
		}
	},
	getters: {
		// 是否已登录
		isLogin(): boolean {
			return !!this.token;
		},
		roleCodes(): string[] {
			return this.userInfo?.roles.map((item) => item.roleCode) || [];
		}
	}
});
