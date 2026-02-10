<template>
	<main class="main-container flex flex-col h-full w-full">
		<BaseHeader />
		<section w="full" class="flex flex-1 overflow-hidden">
			<BaseSide />
			<div w="full" class="body-container" v-loading="isRefreshView">
				<BreadCrumbs v-if="showBreadCrumbs" />
				<div class="body-content">
					<CacheView :includes="['MapAnalysis']" />
				</div>
			</div>
		</section>
	</main>
</template>

<script setup lang="ts">
	import { computed } from "vue";
	import { getAppContext } from "@/core";
	const appInstance = getAppContext();
	import CacheView from "@/core/cache/cache-view";
	import BreadCrumbs from "@/layouts/BreadCrumbs.vue";
	import BaseSide from "@/layouts/BaseSide.vue";
	import BaseHeader from "@/layouts/BaseHeader.vue";
	import { useRoute } from "vue-router";

	const route = useRoute();
	const isRefreshView = computed(() => {
		return appInstance.isRefreshView;
	});
	const showBreadCrumbs = computed(() => {
		return !route.fullPath.includes("gis-map");
	});
</script>

<style scoped>
	#app {
		text-align: center;
		color: var(--el-text-color-primary);
	}

	.main-container {
		height: 100vh;
	}
	.body-container {
		display: flex;
		flex-direction: column;

		.body-content {
			width: 100%;
			flex: 1;
			min-height: 0;
			background-color: var(--app-main-bg);
		}
	}
</style>
