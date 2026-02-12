<script lang="jsx" setup>
	import { useAppStore } from "@/store/app";
	import { DArrowLeft, DArrowRight } from "@element-plus/icons-vue";
	import { ref, watch, nextTick, reactive } from "vue";
	import { useRoute, useRouter } from "vue-router";
	import BaseChildMenus from "./BaseChildMenus";
	import { getAppContext } from "@/core";

	const appInstance = getAppContext();
	const app = useAppStore();
	const route = useRoute();
	const router = useRouter();

	// 响应式数据
	const cRoutes = ref([]);
	const defaultActive = ref("");
	const defaultOpened = ref([]);
	const currentPath = ref("");

	const parentMeta = reactive({
		icon: "ri-apps-2-add-line",
		title: "应用中心"
	});

	// 菜单选择处理
	const handleSelect = (path) => {
		router.push(path);
	};

	// 折叠切换处理
	const handleToggleCollapse = () => {
		app.changeCollapse();
	};

	// 递归获取默认展开的菜单项
	const getDefaultOpened = (list, paths) => {
		list.forEach((item) => {
			if (item.children?.length) {
				paths.push(item.path);
				getDefaultOpened(item.children, paths);
			}
		});
	};

	// 监听路由变化
	watch(
		() => route.fullPath,
		(newPath) => {
			// 如果匹配的路由少于2个，不处理
			if (route.matched.length <= 1) return;

			// 获取当前一级菜单的子菜单
			const parentRoute = route.matched[1];
			const parentMenu = appInstance.userStore.menus;

			if (!parentMenu) return;
			// 更新子菜单和父级元信息
			cRoutes.value = parentMenu || [];
			parentMeta.icon = parentRoute.meta.icon;
			parentMeta.title = parentRoute.meta.title;

			// 设置当前激活的菜单项
			defaultActive.value = route.matched[route.matched.length - 2].path;

			// 如果一级菜单路径变化，重置展开的菜单项
			if (currentPath.value !== parentRoute.path) {
				defaultOpened.value = [];
				nextTick(() => {
					getDefaultOpened(cRoutes.value, defaultOpened.value);
				});
				currentPath.value = parentRoute.path;
			}
		},
		{ immediate: true, deep: true }
	);
</script>

<template>
	<div
		:class="['h-full slider ep-r-1px flex flex-col z-7 am', app.collapse ? 'w-65px' : 'w-200px']"
		style="flex-shrink: 0"
	>
		<div class="flex-1 overflow-hidden hide-scrollbar w-100%">
			<BaseChildMenus
				v-if="defaultOpened.length > 0"
				:collapse="app.collapse"
				:raw-list="cRoutes"
				:default-active="route.fullPath"
				:default-opened="defaultOpened"
				class="page-slider"
			/>
		</div>
	</div>
</template>

<style lang="scss" scoped>
	.ep-r-1px {
		border-right: solid 1px var(--el-menu-border-color);
	}

	.menu-title {
		font-weight: bold;
		font-size: 20px;
		color: rgba(0, 0, 0, 0.85);
		line-height: 28px;
		text-align: left;
		white-space: nowrap;
	}

	.menu-parent {
		border-bottom: 1px solid rgba(0, 0, 0, 0.15);
		padding: 16px 0;
		display: flex;
		gap: 12px;
		align-items: center;
		width: 194px;
		margin-left: 26px;
		transition: all 0.25s linear;

		&.center {
			justify-content: center;
		}

		&.collapse {
			width: calc(100% - 46px);
		}
	}

	.ep-b-1px {
		border-bottom: solid 1px var(--el-menu-border-color);
	}

	.slider {
		box-shadow: 6px 0px 12px 0px rgba(0, 0, 0, 0.08);
		background-color: var(--app-page-slider-bg);
	}

	.h-full {
		position: relative;

		.collose {
			width: var(--el-slide-size);
			height: var(--el-slide-size);
			background: var(--el-color-white);
			box-shadow: 0px 1px 3px 0px rgba(64, 49, 49, 0.12);
			border-radius: var(--el-slide-size);
			border: 1px solid rgba(0, 0, 0, 0.06);
			box-sizing: border-box;
			position: absolute;
			top: 0;
			bottom: 0;
			margin: auto;
			right: -16px;
			z-index: 888;
			cursor: pointer;
			text-align: center;
			line-height: var(--el-slide-size);
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}

	.dark .h-full .collose {
		background: var(--el-menu-border-color);
	}
</style>
