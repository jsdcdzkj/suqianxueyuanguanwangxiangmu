import {
	createRouter,
	createWebHistory,
	type NavigationGuardNext,
	type RouteLocationNormalized,
	type Router,
	type RouteRecordRaw,
	type RouterHistory
} from "vue-router";
import { createApp, inject, type App, type Component } from "vue";
import { coverPathRouter, reconstructionRouter } from "./router";
import { createPinia } from "pinia";

// @ts-expect-error "类型推断问题"
import NProgress from "nprogress";
import ElementPlus from "element-plus";
import mitt from "mitt";

import TlbsMap from "tlbs-map-vue";
import { useUserStore } from "@/store/user";
import { useAppStore } from "@/store/app";
import type { AppContext } from "@/components/config/context-config";
import { textEllipsis } from "./directive/text-ellipsis";

interface ProjectMangerOptions {
	app: Component;
	title?: string;
	routes: RouteRecordRaw[];
	routerHistory?: RouterHistory;
	callback?: (app: App, router: Router) => void;
}

type DictItemType = {
	dictLabel: string;
	dictValue: string | number;
};

export type DictMap = {
	degree_of_difficulty?: DictItemType[];
	priority?: DictItemType[];
	quality_evaluation?: DictItemType[];
	task_attribute?: DictItemType[];
	urgency?: DictItemType[];
	work_item_type?: DictItemType[];
	task_attribute_plan?: DictItemType[];
};

type DictItemSelfType = {
	label: string;
	value: string | number;
	minValue?: number;
};

// 导出类型DictSelfMap
export type DictSelfMap = {
	// 关联类型
	associationType?: DictItemSelfType[];
	// 设备类型
	deviceType?: DictItemSelfType[];
	// 行业
	industry?: DictItemSelfType[];
	// 内部工作处理状态
	internal_work_handle_status?: DictItemSelfType[];
	// 内部工作来源
	internal_work_source?: DictItemSelfType[];
	// 内部工作状态
	internal_work_status?: DictItemSelfType[];
	// 内部工作类型
	internal_work_type?: DictItemSelfType[];
	// 优先级别
	priorityLevel?: DictItemSelfType[];
	// 岗位
	position?: DictItemSelfType[];
	// 内场工作类型
	internal_work_content?: DictItemSelfType[];
	useList?: DictItemSelfType[];
	sysArea?: DictItemSelfType[];
	customerList?: DictItemSelfType[];
	carType?: DictItemSelfType[];
	// 固定资产
	assetsTypeList?: DictItemSelfType[];
	brandList?: DictItemSelfType[];
	statusList?: DictItemSelfType[];
	assetsUserList?: DictItemSelfType[];
};

/***
 *
 */
export const genPList = (element: any, list: any, fullPath: string, isRoot: boolean = false) => {
	list.push({
		...element,
		isRoot,
		children: element.children
			.filter((item) => item.children.length == 0)
			.map((item) => {
				return {
					...item,
					children: []
				};
			})
	});

	const pList = element.children.filter((item) => item.children.length > 0);

	for (let i = 0; i < pList.length; i++) {
		const item = pList[i];
		genPList(item, list, `${fullPath}/${item.path}`);
	}
};
export const dynamicRouter = (router: Router, menuTrees: any[]) => {
	// 获取跟路由
	const rootPath = router.getRoutes().find((item) => item.path == "/");

	let menus = coverPathRouter(menuTrees, "/", router, "Root", []);

	for (let i = 0; i < menus.length; i++) {
		const element = menus[i];
		const list: any[] = [];
		genPList(element, list, element.path, true);
		element.popoverList = list;
	}

	// 跟节点重定向到第一个菜单
	if (rootPath) {
		rootPath.redirect = menus[0].fullPath;
	}
	return menus;
};
// export const dynamicRouter = (router: Router, menuTrees: any[]) => {
// 	// 获取跟路由
// 	const rootPath = router.getRoutes().find((item) => item.path == "/");

// 	// 添加路由并绑定页面
// 	let menus = reconstructionRouter(menuTrees);
// 	menus = coverPathRouter(menus, "/", router, "Root", []);
// 	// 跟节点重定向到第一个菜单
// 	if (rootPath) {
// 		rootPath.redirect = menus[0].fullPath;
// 	}
// 	return menus;
// };

export const getAppContext = (): AppContext => {
	// 使用inject函数获取AppContext
	return inject<AppContext>("config") as AppContext;
};

export const eventBus = mitt();

// 导出一个异步函数，用于创建项目应用
export const createProjectApp = async (options: ProjectMangerOptions) => {
	try {
		// 清理现有实例（开发环境）
		cleanupExistingInstance();

		// 创建应用核心实例
		const app = createApp(options.app);

		// 初始化路由和状态管理
		const router = initializeRouter(options.routes);
		initializeStore(app);

		// 初始化全局事件监听
		initializeEventListeners(router);

		// 加载初始数据
		await loadInitialData(router);

		// 设置路由导航守卫
		setupRouterGuards(router);
		app.directive("textEllipsis", textEllipsis);
		app.use(router);
		// 挂载应用
		mountApplication(app);
	} catch (err) {
		console.log(err);
	}
};

// Helper functions

// 清理现有的Vue实例
function cleanupExistingInstance() {
	if (import.meta.env.DEV && window.vueInstance?._instance) {
		// 卸载现有的Vue实例
		window.vueInstance.unmount();
		// 将window.vueInstance置为null
		window.vueInstance = null;
		// 在控制台输出清理信息
		console.log("Cleaned up existing Vue instance in development mode");
	}
}

// 初始化路由
function initializeRouter(routes: RouteRecordRaw[]) {
	// 创建路由实例
	const router = createRouter({
		// 使用HTML5历史模式
		history: createWebHistory(),
		// 路由配置
		routes,
		// 严格模式，不允许使用尾斜杠
		strict: true,
		// 滚动行为，滚动到顶部
		scrollBehavior: () => ({ top: 0 })
	});

	// 返回路由实例
	return router;
}

// 初始化Pinia存储
function initializeStore(app: App) {
	// 创建Pinia存储
	const store = createPinia();

	// 将Pinia存储添加到Vue应用中
	app.use(store);
	// 将ElementPlus组件库添加到Vue应用中
	app.use(ElementPlus);
	// 将TlbsMap地图组件库添加到Vue应用中
	app.use(TlbsMap);

	// 返回Pinia存储
	return store;
}

function initializeEventListeners(router: Router) {
	eventBus.on("logout", () => {
		const userStore = useUserStore();
		userStore.actionLogout();
		router.replace("/login");
	});
}

// 异步函数，用于加载初始数据
async function loadInitialData(router: Router) {
	// 使用用户存储
	const userStore = useUserStore();
	// 使用应用存储
	const appStore = useAppStore();
	// 如果用户已经登录
	if (userStore.isLogin) {
		// 等待用户已经登录和获取地图字典的Promise都完成
		await Promise.all([userStore.alreadyLogin(router), appStore.getMapDict()]);
	}
}

// 设置路由守卫
function setupRouterGuards(router: Router) {
	// 获取应用存储
	const appStore = useAppStore();

	// 在路由跳转前执行
	router.beforeEach(async (to, _, next) => {
		// 开始加载动画
		NProgress.start();

		// 更新面包屑
		updateBreadcrumb(to, appStore);

		// 处理导航守卫
		handleNavigationGuard(to, next);
	});

	// 在路由跳转后执行
	router.afterEach(() => {
		// 结束加载动画
		NProgress.done();
	});
}

function updateBreadcrumb(to: RouteLocationNormalized, appStore: any) {
	const breadcrumb = to.matched.reduce((str, item) => {
		if (item.meta?.title) {
			str += `${item.meta.title} > `;
		}
		return str;
	}, "");

	if (breadcrumb.length > -3) {
		appStore.changeBreadcrumb(breadcrumb.slice(0, -3));
	}
}

// 导航守卫函数，用于处理导航守卫
function handleNavigationGuard(to: RouteLocationNormalized, next: NavigationGuardNext) {
	// 获取用户信息存储
	const userStore = useUserStore();
	// 如果用户已登录
	if (userStore.isLogin) {
		// 如果用户信息不存在
		if (!userStore.userInfo) {
			// 打印正在获取用户信息
			console.log("Fetching user info...");
		}
		// 继续导航
		next();
		// 如果用户未登录
	} else {
		// 如果当前路径不是登录页面
		to.path !== "/login" ? next({ path: "/login" }) : next();
	}
}

// 函数：挂载应用程序
// 参数：app：应用程序实例

function mountApplication(app: App) {
	// 挂载应用程序到id为app的元素上
	app.mount("#app", true);
	// 如果当前环境为开发环境且window.vueInstance不存在，则将app赋值给window.vueInstance
	if (import.meta.env.DEV && !window.vueInstance) {
		window.vueInstance = app;
		// 在控制台输出Vue实例已注册到window上
		console.log("Vue instance registered on window for development");
	}
}
