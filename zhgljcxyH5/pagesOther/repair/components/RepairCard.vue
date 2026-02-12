<template>
	<view class="card-box">
		<!-- 第一行内容 -->
		<view class="card-header">
			<text class="title">{{ title }}</text>
			<view class="repair-tag">维修</view>
			<view class="emergency-tag">
				<view class="emergency-dot"></view>
				<text class="emergency-text">紧急情况</text>
			</view>
		</view>

		<!-- 中间内容 -->
		<view class="card-content">
			<view class="info-item">
				<text class="info-label">预计完成时间：</text>
				<text class="info-value">{{ estimatedTime }}</text>
			</view>
			<view class="info-item">
				<text class="info-label">负责人：</text>
				<text class="info-value">{{ manager }}</text>
			</view>
		</view>

		<!-- 状态标签 -->
		<view class="status-tag" :class="statusClass">
			{{ statusText }}
		</view>
	</view>
</template>

<script setup>
	import {
		computed
	} from 'vue'

	// 定义props
	const props = defineProps({
		title: {
			type: String,
			default: '维修任务标题维修任务标题维修任务标题维修任务标题维修任务标题维修任务标题'
		},
		estimatedTime: {
			type: String,
			default: '2025-08-13 12:00:00'
		},
		manager: {
			type: String,
			default: '老王'
		},
		status: {
			type: String,
			default: 'pending', // pending: 待安排, processing: 维修中, completed: 已完成
			validator: (value) => ['pending', 'processing', 'completed'].includes(value)
		}
	})

	// 计算状态文字
	const statusText = computed(() => {
		const statusMap = {
			pending: '待安排',
			processing: '维修中',
			completed: '已完成'
		}
		return statusMap[props.status]
	})

	// 计算状态样式类
	const statusClass = computed(() => {
		return {
			'status-pending': props.status === 'pending',
			'status-processing': props.status === 'processing',
			'status-completed': props.status === 'completed'
		}
	})
</script>

<style lang="scss" scoped>
	.card-box {
		width: 700rpx;

		background: rgb(255, 255, 255);
		border-radius: 20rpx;
		position: relative;
		padding: 20rpx;
		box-sizing: border-box;
		margin: 25rpx;
		overflow: hidden;

		.card-header {
			display: flex;
			align-items: flex-start;
			gap: 20rpx;
			margin-bottom: 30rpx;

			.title {
				font-weight: normal;
				font-size: 29rpx;
				color: rgba(0, 0, 0, 0.85);
				line-height: 46rpx;
				flex: 1;
				font-weight: bold;
			}

			.repair-tag {
				width: 108rpx;
				height: 50rpx;
				background: #FDECDC;
				border-radius: 8rpx;
				border: 2rpx solid #F7B573;
				font-size: 29rpx;
				color: #F5A250;
				display: flex;
				align-items: center;
				justify-content: center;
			}

			.emergency-tag {
				display: flex;
				align-items: center;
				gap: 10rpx;

				.emergency-dot {
					width: 17rpx;
					height: 17rpx;
					background: #5187FF;
					border-radius: 50%;
				}

				.emergency-text {
					font-weight: normal;
					font-size: 29rpx;
					color: #5187FF;
					line-height: 46rpx;
				}
			}
		}

		.card-content {
			.info-item {
				margin-bottom: 20rpx;
				font-size: 29rpx;
				line-height: 46rpx;

				.info-label {
					color: rgba(0, 0, 0, 0.65);
				}

				.info-value {
					color: rgba(0, 0, 0, 0.85);
				}
			}
		}

		.status-tag {
			position: absolute;
			right: 0;
			bottom: 0;
			width: 138rpx;
			height: 50rpx;
			border-radius: 25rpx 0rpx 20rpx 0rpx;
			font-weight: normal;
			font-size: 29rpx;
			color: #FFFFFF;
			display: flex;
			align-items: center;
			justify-content: center;

			&.status-pending {
				background: #FFA500;
			}

			&.status-processing {
				background: #5187FF;
			}

			&.status-completed {
				background: #61C554;
			}
		}
	}
</style>