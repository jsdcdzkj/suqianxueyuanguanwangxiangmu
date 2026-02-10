import layout from "@/composables/layout";
// @ts-expect-error "类型推断问题"
import NProgress from "nprogress";
import { type Router } from "vue-router";
// @ts-expect-error "类型推断问题"
import { routes } from "vue-router/auto-routes";

const pagesComponents = import.meta.glob("../pages/modules/**/*.vue");

// 导出一个名为 reconstructionRouter 的函数，该函数接收一个 any 类型的数组 routeRaws 作为参数
export const reconstructionRouter = (routeRaws: any[]) => {
	// 使用 map 方法遍历 routeRaws 数组，对每个元素执行以下操作
	return routeRaws.map((item) => {
		// 在 layout.modules 数组中查找路径与 item.pathName 相匹配的模块
		const meta = layout.modules.find((it) => it.path == "/" + item.pathName);
		// 返回一个新的对象，该对象包含 item 的所有属性，并添加 meta 属性
		return {
			...item,
			meta
		};
	});
};

// 导出一个名为 coverPathRouter 的函数，用于处理菜单路径并生成路由配置
export const coverPathRouter = (menus: any[], parentPath: string, router: Router, pName: string, list: any[]) => {
	// 遍历传入的菜单数组
	for (const item of menus) {
		// 如果当前菜单项有子菜单且子菜单数量大于0
		if (item.children && item.children.length > 0) {
			// 计算当前菜单项的完整路径
			item.fullPath = parentPath + item.routerUrl;

			// 创建一个路由配置对象
			const routerRaw = {
				path: item.routerUrl, // 路由路径
				name: item.routerName, // 路由名称
				component: () => import("@/layouts/empty.vue"), // 路由组件，使用动态导入
				redirect: item.fullPath + "/" + item.children[0].routerUrl, // 重定向路径
				children: [], // 子路由数组
				meta: {
					...item,
					children: undefined,
					title: item.title, // 路由标题

					icon: item.icon, // 路由图标
					isShow: item.isShow === 1 // 是否显示路由
				}
			};

			// 将路由配置添加到路由器中
			router.addRoute(pName, routerRaw);

			// 递归处理子菜单，并更新子菜单的完整路径
			item.children = coverPathRouter(item.children, item.fullPath + "/", router, item.routerName, []);

			// 将当前菜单项及其完整路径添加到列表中
			list.push({ ...item, path: item.fullPath });
		} else {
			// 如果当前菜单项没有子菜单
			// 计算当前菜单项的完整路径
			item.fullPath = parentPath + item.routerUrl;
			// 将路由配置添加到路由器中
			router.addRoute(pName, {
				path: item.routerUrl, // 路由路径
				component: pagesComponents[`../${item.vueUrl}`], // 路由组件
				name: item.routerName, // 路由名称
				meta: {
					...item,
					children: undefined,
					title: item.title, // 路由标题
					icon: item.icon, // 路由图标
					isShow: item.isShow === 1 // 是否显示路由
				}
			});
			// 如果当前菜单项需要显示
			if (item.isShow === 1) {
				// 将当前菜单项及其完整路径添加到列表中
				list.push({ ...item, path: item.fullPath });
			}
		}
	}

	// 返回处理后的菜单列表
	return list;
};
