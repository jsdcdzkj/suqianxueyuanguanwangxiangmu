<template>
	<u-popup :modelValue="modelValue" mode="bottom" border-radius="20" :closeOnClickOverlay="false"
		@update:show="updateShow" :safe-area-inset-bottom="true">
		<view class="detail-popup">
			<!-- 弹窗标题栏 -->
			<view class="popup-header">
				<text class="popup-title">告警详情</text>
				<view class="close-btn" @click="closePopup">
					<u-icon name="close" size="20" color="#999"></u-icon>
				</view>
			</view>

			<view class="" style="padding: 25rpx;">
				<!-- 详情内容 -->
				<view class="popup-content">
					<view class="detail-item">
						<text class="detail-label">告警对象：</text>
						<text class="detail-value">{{ detailInfo.object || '漏水探漏器/038989721' }}</text>
					</view>
					<view class="detail-item">
						<text class="detail-label">告警位置：</text>
						<text class="detail-value">{{ detailInfo.location || '东南校区/体育学院' }}</text>
					</view>
					<view class="detail-item">
						<text class="detail-label">告警内容：</text>
						<text class="detail-value">{{ detailInfo.content || '漏水事件' }}</text>
					</view>
					<view class="detail-item">
						<text class="detail-label">告警来源：</text>
						<text class="detail-value">{{ detailInfo.source || '采集设备' }}</text>
					</view>
					<view class="detail-item">
						<text class="detail-label">告警等级：</text>
						<text class="detail-value level-important">{{ detailInfo.level || '重要' }}</text>
					</view>
					<view class="detail-item">
						<text class="detail-label">告警类型：</text>
						<text class="detail-value">{{ detailInfo.type || '漏损报警' }}</text>
					</view>
					<view class="detail-item">
						<text class="detail-label">告警状态：</text>
						<text class="detail-value">{{ detailInfo.status || '告警中' }}</text>
					</view>
					<view class="detail-item">
						<text class="detail-label">处理状态：</text>
						<text class="detail-value">{{ detailInfo.handleStatus || '上报' }}</text>
					</view>
				</view>

				<!-- 处理进度时间轴 -->
				<view class="timeline-section">
					<view class="timeline-title">处理进度</view>
					<view class="timeline-list">
						<view class="timeline-item active">
							<view class="timeline-dot"></view>
							<view class="timeline-line"></view>
							<view class="timeline-content">
								<view class="timeline-card">
									<view class="card-title">告警已上报</view>
									<view class="card-info">上报时间：2025-10-12 10:00:00</view>
									<view class="card-info">指派人员：张三</view>
									<view class="card-info">告警内容：用能超阈值</view>
									<view class="card-info">处理班组：秩序组</view>
								</view>
							</view>
						</view>
						<!-- 可以添加更多时间轴节点 -->
						<view class="timeline-item active">
							<view class="timeline-dot"></view>
							<view class="timeline-line"></view>
							<view class="timeline-content">
								<view class="timeline-card">
									<view class="card-title">处理中</view>
									<view class="card-info">处理时间：2025-10-12 11:00:00</view>
									<view class="card-info">处理人员：李四</view>
									<view class="card-info">处理说明：正在处理中</view>
								</view>
							</view>
						</view>
					</view>
				</view>

				<!-- 分割线 -->
				<view class="divider"></view>

				<!-- 操作按钮组 -->
				<view class="action-buttons">
					<view class="action-btn" @click="handleReport">上报</view>
					<view class="action-btn" @click="handleIgnore">忽略</view>
					<view class="action-btn" @click="handleMistake">误报</view>
					<view class="action-btn delete-btn" @click="handleDelete">删除</view>
				</view>

				<!-- 查看地图按钮 -->
				<view class="map-btn" @click="handleMap">
					<u-icon name="map" size="20" color="#fff"></u-icon>
					<text>查看地图定位</text>
				</view>
			</view>
		</view>
	</u-popup>
</template>

<script setup>
	import {
		defineProps,
		defineEmits
	} from 'vue'

	const props = defineProps({
		modelValue: {
			type: Boolean,
			default: false
		},
		detailInfo: {
			type: Object,
			default: () => ({})
		}
	})

	const emit = defineEmits(['update:modelValue', 'report', 'ignore', 'mistake', 'delete', 'map'])

	// 更新显示状态
	const updateShow = (value) => {
		emit('update:modelValue', value)
	}

	// 关闭弹窗
	const closePopup = () => {
		emit('update:modelValue', false)
	}

	// 处理上报
	const handleReport = () => {
		emit('report')
		closePopup()
	}

	// 处理忽略
	const handleIgnore = () => {
		emit('ignore')
		closePopup()
	}

	// 处理误报
	const handleMistake = () => {
		emit('mistake')
		closePopup()
	}

	// 处理删除
	const handleDelete = () => {
		uni.showModal({
			title: '提示',
			content: '确定要删除这条告警吗？',
			success: (res) => {
				if (res.confirm) {
					emit('delete')
					closePopup()
				}
			}
		})
	}

	// 查看地图
	const handleMap = () => {
		emit('map')
		closePopup()
	}
</script>

<style lang="scss" scoped>
	.detail-popup {

		background: #FFFFFF;
		border-radius: 20rpx;

		border-top-right-radius: 25rpx;
		border-top-left-radius: 25rpx;

	}

	.popup-header {
		display: flex;
		justify-content: space-between;
		align-items: center;

		border-bottom: 2rpx solid rgba(0, 0, 0, 0.06);
		padding: 0 25rpx;
		height: 100rpx;

		.popup-title {
			font-size: 32rpx;
			font-weight: 500;
			color: rgba(0, 0, 0, 0.85);
		}

		.close-btn {
			padding: 8rpx;
		}
	}

	.popup-content {}

	.detail-item {
		display: flex;
		margin-bottom: 24rpx;


		.detail-label {
			font-size: 29rpx;
			color: rgba(0, 0, 0, 0.65);
			line-height: 46rpx;
			width: 160rpx;
		}

		.detail-value {
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

	.divider {
		height: 2rpx;
		background: rgba(0, 0, 0, 0.06);
		margin: 32rpx 0;
	}

	.action-buttons {
		display: flex;
		justify-content: space-between;
		margin-bottom: 32rpx;
	}

	.action-btn {
		height: 83rpx;
		background: #FFFFFF;
		border-radius: 17rpx;
		border: 2rpx solid rgba(0, 0, 0, 0.15);
		font-size: 29rpx;
		color: rgba(0, 0, 0, 0.85);
		line-height: 46rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		flex: 1;
		margin: 0 8rpx;

		&.delete-btn {
			background: #FBE1DF;
			color: #ED6A5E;
			border: none;
		}

		&:active {
			opacity: 0.8;
		}
	}

	.map-btn {
		width: 700rpx;
		height: 83rpx;
		background: #4378E7;
		border-radius: 17rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 16rpx;

		text {
			font-size: 29rpx;
			color: #FFFFFF;
			line-height: 46rpx;
		}

		&:active {
			opacity: 0.8;
		}
	}


	.timeline-section {
		margin-bottom: 32rpx;
	}

	.timeline-title {
		font-size: 29rpx;
		color: rgba(0, 0, 0, 0.85);
		line-height: 46rpx;
		margin-bottom: 24rpx;
	}

	.timeline-list {
		padding-left: 24rpx;
		position: relative;

		// 添加垂直线容器
		&::before {
			content: '';
			position: absolute;
			left: 0;
			top: 16rpx;
			bottom: 0;
			width: 2rpx;
			background: #E8E8E8;
		}
	}

	.timeline-item {
		position: relative;
		padding-left: 40rpx;
		padding-bottom: 32rpx;

		&.active {
			.timeline-dot {
				background: #4378E7;
				border-color: #4378E7;
			}

			.timeline-line {
				background: #4378E7;
			}
		}

		// 最后一个节点隐藏垂直线
		&:last-child {
			.timeline-line {
				display: none;
			}
		}
	}

	.timeline-dot {
		position: absolute;
		left: -8rpx;
		top: 8rpx;
		width: 16rpx;
		height: 16rpx;
		border-radius: 50%;
		background: #fff;
		border: 2rpx solid #ddd;
		z-index: 1;
	}

	.timeline-line {
		position: absolute;
		left: -1rpx;
		top: 32rpx;
		bottom: 0;
		width: 2rpx;
		background: #E8E8E8;
	}

	.timeline-content {
		padding-bottom: 0;
	}

	.timeline-card {
		// height: 179rpx;
		border-radius: 25rpx;
		border: 2rpx solid rgba(0, 0, 0, 0.06);
		padding: 24rpx;
		background: #fff;

		.card-title {
			font-size: 29rpx;
			color: #4378E7;
			margin-bottom: 16rpx;
		}

		.card-info {
			font-size: 25rpx;
			color: rgba(0, 0, 0, 0.65);
			line-height: 40rpx;
		}
	}
</style>