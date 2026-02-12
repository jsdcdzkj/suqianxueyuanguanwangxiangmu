<template>
	<view class="search-box">
		<!-- 告警时间范围选择 -->
		<view class="time-range">
			<view class="time-item" @click="showStartTimePicker">
				<text class="time-text">{{ startTime || '告警时间起' }}</text>
				<u-icon color="rgba(0,0,0,0.25)" size="18" name="arrow-down"></u-icon>

			</view>
			<view class="time-item" @click="showEndTimePicker">
				<text class="time-text">{{ endTime || '告警时间止' }}</text>
				<u-icon color="rgba(0,0,0,0.25)" size="18" name="arrow-down"></u-icon>
			</view>
		</view>

		<!-- 操作按钮组 -->
		<view class="action-buttons">
			<view class="reset-btn" @click="handleReset">
				<text>重置</text>
			</view>
			<view class="filter-btn" @click="handleFilter">
				<text>筛选</text>
				<image class="filter-icon" src="/static/alarm/filter-line.png" mode="aspectFit"></image>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref } from 'vue'

	const startTime = ref('')
	const endTime = ref('')

	const showStartTimePicker = () => {
		// TODO: 实现开始时间选择器
		uni.showToast({
			title: '选择开始时间',
			icon: 'none'
		})
	}

	const showEndTimePicker = () => {
		// TODO: 实现结束时间选择器
		uni.showToast({
			title: '选择结束时间',
			icon: 'none'
		})
	}

	const handleReset = () => {
		startTime.value = ''
		endTime.value = ''
		// TODO: 触发重置事件
	}

	const handleFilter = () => {
		// TODO: 触发筛选事件
	}
</script>

<style lang="scss" scoped>
	.search-box {
		height: 92rpx;
		background: #FFFFFF;
		border-radius: 0rpx;
		border: 2rpx solid rgba(0, 0, 0, 0.06);
		display: flex;
		align-items: center;
		padding: 0 32rpx;
		gap: 16rpx;
	}

	.time-range {
		display: flex;
		gap: 16rpx;
		flex: 1;
	}

	.time-item {
		height: 58rpx;
		background: #F8F8F8;
		border-radius: 38rpx;
		display: flex;
		align-items: center;
		padding: 0 14rpx;
		gap: 8rpx;
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: space-around;


		.time-text {
			font-size: 24rpx;
			color: rgba(0, 0, 0, 0.65);
		}

		.dropdown-icon {
			width: 24rpx;
			height: 24rpx;
		}
	}

	.action-buttons {
		display: flex;
		gap: 16rpx;
	}

	.reset-btn {
		height: 58rpx;
		background: #E7EFFA;
		border-radius: 38rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 32rpx;

		text {
			font-size: 24rpx;
			color: #4378E7;
			line-height: 42rpx;
		}
	}

	.filter-btn {
		height: 58rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 32rpx;
		background: #F8F8F8;
		gap: 8rpx;
		border-radius: 38rpx;
		display: flex;
		align-items: center;
		justify-content: space-around;

		text {
			font-size: 24rpx;
			color: rgba(0, 0, 0, 0.65);
			line-height: 42rpx;
		}

		.filter-icon {
			width: 24rpx;
			height: 24rpx;
		}
	}
</style>