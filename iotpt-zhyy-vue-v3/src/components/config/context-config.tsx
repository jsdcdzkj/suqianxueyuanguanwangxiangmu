import type { DictSelfMap } from "@/core";
import { defineComponent, Fragment, provide, reactive, watch } from "vue";
import { useUserStore, type UserInfo } from "@/store/user";
import type { Store } from "pinia";
import { useAppStore } from "@/store/app";
import { type Router } from "vue-router";

export interface AppContext {
	mapDict: DictSelfMap;
	userInfo: UserInfo;
	userStore: Store<
		"user",
		{
			token: string;
			userInfo: UserInfo | null;
			loading: boolean;
			userId: string;
			menus: any[];
		},
		{
			isLogin(): boolean;
		},
		{
			actionLogin(model: any, router: Router): Promise<any>;
			alreadyLogin(router: Router): Promise<any>;
			actionLogout(router: Router): void;
		}
	>;
	appStore: Store<
		"app",
		{
			menusList: never[];
			collapse: boolean;
			breadcrumb: string;
			mapDict: DictSelfMap;
		},
		{},
		{
			changeCollapse(): void;
			changeBreadcrumb(breadcrumb: string): void;
			getMapDict(): Promise<any>;
		}
	>;
}

// 导出一个函数，用于获取AppContext
export default defineComponent({
	setup(_, { slots }) {
		const useStore = useUserStore();
		const appStore = useAppStore();

		const contenxt = reactive({
			appStore,
			userStore: useStore,
			mapDict: appStore.mapDict,
			userInfo: useStore.userInfo
		});

		watch(
			() => appStore.mapDict,
			() => {
				contenxt.mapDict = appStore.mapDict;
			}
		);

		watch(
			() => useStore.userInfo,
			() => {
				contenxt.userInfo = useStore.userInfo;
			}
		);

		provide("config", contenxt as AppContext);

		return () => <Fragment>{slots.default?.()}</Fragment>;
	}
});
