<template>
	<view class="flex-container">
		<view class="flex-container__header">
			<slot name="header"></slot>
		</view>
		<scroll-view scroll-y class="scroll-list" @scrolltolower="handleScrollToLower" :lower-threshold="lowerThreshold"
			:refresher-enabled="refresherEnabled" @refresherrefresh="handleRefresh" :refresher-triggered="isRefreshing"
			refresher-background="#f8f8f8">
			<slot></slot>
			<!-- 底部加载状态 -->
			<view v-if="loading" class="loading-more">
				<!-- <uni-load-more :status="loadStatus"></uni-load-more> -->
				<u-loadmore :status="loadStatus"></u-loadmore>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import {
		ref,
		defineProps,
		defineEmits
	} from 'vue'

	const props = defineProps({
		// 距离底部多少距离时触发
		lowerThreshold: {
			type: [Number, String],
			default: 50
		},
		refresherEnabled: {
			type: Boolean,
			default: true
		},
		// 是否正在加载
		loading: {
			type: Boolean,
			default: false
		},
		// 加载状态：more-加载前，loading-加载中，noMore-没有更多了
		loadStatus: {
			type: String,
			default: 'more'
		}
	})

	const emit = defineEmits(['scrolltolower', 'refresh'])

	// 下拉刷新状态
	const isRefreshing = ref(false)

	// 滚动到底部事件处理
	const handleScrollToLower = () => {
		if (!props.loading) {
			emit('scrolltolower')
		}
	}

	// 下拉刷新处理
	const handleRefresh = async () => {
		isRefreshing.value = true
		emit('refresh')
		// 注意：父组件需要在刷新完成后重置刷新状态
	}

	// 暴露方法给父组件，用于控制刷新状态
	const stopRefresh = () => {
		isRefreshing.value = false
	}

	defineExpose({
		stopRefresh
	})
</script>

<style lang="scss" scoped>
	.flex-container {
		width: 100%;
		height: 100%;
		display: flex;
		flex-direction: column;

		&__header {
			display: flex;
			flex-direction: column;
		}

		.scroll-list {
			flex: 1;
			overflow: hidden;

			&:last-child {
				margin-bottom: 0;
			}
		}

		.loading-more {
			padding-bottom: 25rpx;
		}
	}
</style>