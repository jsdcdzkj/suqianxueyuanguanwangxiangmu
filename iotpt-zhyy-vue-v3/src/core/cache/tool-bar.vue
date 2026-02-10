<template>
	<div class="page-bar-container">
		<div class="page-bar" id="toolBar">
			<div class="tool-bar-scroll" id="toolBarScroll">
				<template v-for="item in cachedList">
					<router-link :to="item.fullPath" class="tool-bar-item">
						{{ item.menuName }}
					</router-link>
				</template>
			</div>
		</div>
	</div>
</template>

<script lang="tsx" setup>
	import { array, number } from "vue-types";
	import type { CacheItem } from "@/core/cache/useToolBar";
	defineProps({
		cachedList: array<CacheItem>().def([]),
		cacheCurrent: number().def(-1)
	});

	// console.log(cachedList);
</script>

<style lang="scss" scoped>
	.page-bar-container {
		height: 36px;
		display: flex;
		border-bottom: 1ox solid #eee;
		overflow: hidden;
		.page-bar {
			flex: 1 1;
			display: flex;
			padding-top: 8px;
			overflow: hidden;
			.tool-bar-scroll {
				position: relative;
				display: flex;
				overflow-y: hidden;
				overflow-x: auto;
				width: 100%;
				scrollbar-width: none;
				padding-inline: 20px;
				align-items: flex-end;
				.tool-bar-item {
					padding: 0px 15px;
					text-align: center;
					line-height: 30px;
					height: 30px;
					color: #000;
					position: relative;
					transform: all linear 400ms;
					font-size: 14px;
				}
			}
		}
	}
</style>
