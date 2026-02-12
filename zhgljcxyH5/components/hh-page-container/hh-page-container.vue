<template>
	<view :class="['page-container', props.screen ? 'full':'min']" :style="pageStyle">
		<slot></slot>
		<view class="fixed-bottom" v-if="slots.bottom&& showBottom">
			<slot name="bottom"></slot>
		</view>
		<view class="safe-area" v-if="safeArea"></view>
	</view>
</template>

<script setup>
	import {
		computed
	} from 'vue';


	/**
	 * @property {Boolean} safeArea 安全区域
	 * @property {String} background 背景颜色
	 * @property {Boolean} screen 高度为100vh
	 */
	const props = defineProps({
		status: {
			type: String,
			default: 'loading'
		},
		safeArea: {
			type: Boolean,
			default: false
		},
		screen: {
			type: Boolean,
			default: false
		},
		showBottom: {
			type: Boolean,
			default: true
		},
		background: {
			type: String,
			default: ''
		}
	})


	const slots = defineSlots()
	const pageStyle = computed(() => {
		return {
			paddingBottom: uni.upx2px(slots.bottom && props.showBottom ? 96 : 0) +
				(props.safeArea ? getApp().globalData.safeAreaBottom : 0) +
				'px',
			background: props.background
		}
	})
</script>

<style lang="scss">
	.page-container {
		width: 100%;


		&.full {
			height: 100vh;
		}

		&.min {
			min-height: 100vh;
		}


	}

	.safe-area {
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		background-color: #fff;
		z-index: 999;
		height: constant(safe-area-inset-bottom);
		/*兼容 IOS<11.2*/
		height: env(safe-area-inset-bottom);
		/*兼容 IOS>11.2*/
	}

	.fixed-bottom {
		position: fixed;
		bottom: constant(safe-area-inset-bottom);
		/*兼容 IOS<11.2*/
		bottom: env(safe-area-inset-bottom);
		/*兼容 IOS>11.2*/
		left: 0;
		background-color: #fff;
		border-top: 1px solid #eee;
		height: 96rpx;
		width: 100%;
		z-index: 98;
		box-shadow: 0rpx -2rpx 8rpx 0rpx rgba(0, 0, 0, 0.05);
	}
</style>