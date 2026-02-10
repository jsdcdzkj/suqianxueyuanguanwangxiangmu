import type { DictSelfMap } from "@/core";
import { defineStore } from "pinia";
// 导出一个名为useAppStore的函数，用于定义一个名为app的store
export const useAppStore = defineStore("app", {
	// 定义state，返回一个对象
	state: () => {
		return {
			// 菜单列表
			menusList: [],
			// 折叠状态
			collapse: false,
			// 面包屑
			breadcrumb: "",
			// 字典映射
			mapDict: {} as DictSelfMap
		};
	},
	// 定义actions，包含三个方法
	actions: {
		// 改变折叠状态
		changeCollapse() {
			this.collapse = !this.collapse;
		},
		// 改变面包屑
		changeBreadcrumb(breadcrumb: string) {
			this.breadcrumb = breadcrumb;
		},
		// 异步获取字典映射
		async getMapDict() {}
	},
	// 定义getters，为空
	getters: {}
});
