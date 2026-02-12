<template>
	<view class="switch-box">
		<view class="switch-btn" :class="{ active: activeTab === 'pending' }" @click="handleTabChange('pending')">
			待处理
		</view>
		<view class="switch-btn" :class="{ active: activeTab === 'history' }" @click="handleTabChange('history')">
			历史上报
		</view>
	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue'

	// 定义当前选中的tab
	const activeTab = ref('pending')

	// 定义emits
	const emit = defineEmits(['change'])

	// 切换处理
	const handleTabChange = (tab) => {
		activeTab.value = tab
		emit('change', tab)
	}
</script>

<style lang="scss" scoped>
	.switch-box {

		background: #FFFFFF;
		border-radius: 0rpx;
		border-bottom: 2rpx solid rgba(0, 0, 0, 0.06);
		display: flex;
		align-items: center;
		gap: 10rpx;
		padding: 0 24rpx;
		padding-bottom: 16rpx;


		.switch-btn {
			width: 163rpx;
			height: 67rpx;
			background: #F4F4F4;
			border-radius: 38rpx;
			font-weight: normal;
			font-size: 29rpx;
			color: rgba(0, 0, 0, 0.65);
			line-height: 67rpx;
			text-align: center;
			transition: all 0.3s ease;

			&.active {
				background: #4378E7;
				color: #FFFFFF;
			}
		}
	}
</style>