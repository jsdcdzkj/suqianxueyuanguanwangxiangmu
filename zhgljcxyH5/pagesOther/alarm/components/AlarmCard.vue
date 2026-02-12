<template>
	<view class="alarm-card">
		<!-- 卡片主体内容 -->
		<view class="card-content">
			<!-- 标题区域 -->
			<view class="title-section">
				<image class="title-icon" src="/static/alarm/alert-fill.png" mode="aspectFit"></image>
				<text class="title-text">告警信息</text>
			</view>

			<!-- 告警信息列表 -->
			<view class="info-list">
				<view class="info-item">
					<text class="info-label">告警内容：</text>
					<text class="info-value">{{ content || '探测到漏水事件' }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">告警等级：</text>
					<text class="info-value level-important">{{ level || '重要' }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">告警时间：</text>
					<text class="info-value">{{ time || '2025-08-13 12:00:00' }}</text>
				</view>
			</view>
		</view>

		<!-- 底部详情按钮 -->
		<view class="detail-button" @click="handleDetail">
			<text>查看详情</text>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { defineProps, defineEmits } from 'vue'

	const props = defineProps({
		content: {
			type: String,
			default: ''
		},
		level: {
			type: String,
			default: ''
		},
		time: {
			type: String,
			default: ''
		}
	})

	const emit = defineEmits(['detail'])

	const handleDetail = () => {
		emit('detail')
	}
</script>

<style lang="scss" scoped>
	.alarm-card {
		width: 700rpx;

		background: #FFFFFF;
		border-radius: 25rpx;
		display: flex;
		flex-direction: column;

		margin-left: 25rpx;
		margin-bottom: 25rpx;
		margin-top: 25rpx;
	}

	.card-content {
		flex: 1;
		padding: 32rpx;
	}

	.title-section {
		display: flex;
		align-items: center;
		gap: 16rpx;
		margin-bottom: 32rpx;

		.title-icon {
			width: 32rpx;
			height: 32rpx;
		}

		.title-text {
			font-weight: normal;
			font-size: 29rpx;
			color: rgba(0, 0, 0, 0.85);
			line-height: 46rpx;
		}
	}

	.info-list {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.info-item {
		display: flex;
		align-items: flex-start;

		.info-label {
			font-size: 29rpx;
			color: rgba(0, 0, 0, 0.65);
			line-height: 46rpx;
		}

		.info-value {
			font-size: 29rpx;
			color: rgba(0, 0, 0, 0.85);
			line-height: 46rpx;
			flex: 1;

			&.level-important {
				color: #FF4D4F;
				font-weight: 500;
			}
		}
	}

	.detail-button {
		width: 700rpx;
		height: 83rpx;
		background: rgba(255, 255, 255, 0);
		border-radius: 0rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-top: 2rpx solid rgba(0, 0, 0, 0.06);
		border-bottom-left-radius: 25rpx;
		border-bottom-right-radius: 25rpx;
		overflow: hidden;


		text {
			font-size: 29rpx;
			color: #4378E7;
			line-height: 46rpx;
		}

		&:active {
			background: rgba(67, 120, 231, 0.1);
		}
	}
</style>