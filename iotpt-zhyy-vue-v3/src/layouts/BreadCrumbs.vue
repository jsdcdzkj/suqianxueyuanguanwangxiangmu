<template>
	<div :class="['bread-crumbs', theme]">
		<i class="ri-menu-unfold-2-fill" @click="handleChangeCollapse" />
		<el-breadcrumb separator="/">
			<el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="index" :to="{ path: item.path }">
				{{ item.title }}
			</el-breadcrumb-item>
		</el-breadcrumb>
	</div>
</template>

<script setup lang="ts">
	import { useAppStore } from "@/store/app";
	import { computed } from "vue";
	import { useRoute, type RouteMeta } from "vue-router";
	import { string } from "vue-types";

	// 定义面包屑项类型
	type BreadcrumbItem = {
		path: string;
		title: string;
	};

	defineProps({
		theme: string().def("light")
	});

	const route = useRoute();
	const app = useAppStore();

	// 切换侧边栏折叠状态
	const handleChangeCollapse = () => {
		app.changeCollapse();
	};

	// 使用computed计算面包屑列表，比watch更高效
	const breadcrumbList = computed<BreadcrumbItem[]>(() => {
		return route.matched.map((item) => ({
			path: item.path,
			title: (item.meta?.title || "未知页面") as string
		}));
	});
</script>

<style lang="scss" scoped>
	.bread-crumbs.light {
		background: #ffffff;
		box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12);
		height: var(--app-page-toolbar-height);
		display: flex;
		align-items: center;
		gap: 10px;
		padding: 0 16px;

		i {
			font-size: 20px;
			color: #000;
			cursor: pointer;
			transition: color 0.3s;

			&:hover {
				color: var(--el-color-primary);
			}
		}
	}
	.bread-crumbs.dark {
		background: rgba(0, 0, 0, 0.45);
		box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12);
		height: var(--app-page-toolbar-height);
		display: flex;
		align-items: center;
		gap: 10px;
		padding: 0 16px;

		i {
			font-size: 20px;
			color: #fff;
			cursor: pointer;
			transition: color 0.3s;

			&:hover {
				color: #fff;
			}
		}
		::v-deep(.el-breadcrumb__inner.is-link) {
			color: #fff;
		}
	}
</style>
