<template>
	<view class="container">
		<map name="" latitude="34.216052" longitude="117.239753" :markers="markersList" :enable-zoom="true"
			:enable-scroll="true" :scale="mapScale" :min-scale="3" :max-scale="20"></map>
		<view class="search_box">
			<u-search shape="rounded" v-model="name" bg-color="rgba(0,0,0,0.04)"
				:input-style="{background:'transparent'}" placeholder="请输入设备名称"
				:action-style="{background:'#4378E7',color:'#fff',borderRadius:'16rpx',height:'60rpx',width:'108rpx',lineHeight:'60rpx',boxSizing:'border-box'}"></u-search>
		</view>
		<!-- 右侧缩放按钮 -->
		<view class="right_control">
			<view class="u-icon-wrap" @click="addScale">
				<u-icon name="plus" size="32rpx" color="#fff"></u-icon>
			</view>
			<view class="u-icon-wrap u-m-t-16" @click="subtractScale">
				<u-icon name="minus" size="32rpx" color="#fff"></u-icon>
			</view>
		</view>
		<!-- 底部统计弹窗 -->
		<view class="bottom_box" v-if="!showDetail">
			<view class="title">
				<text>设备统计</text>
				<image src="/static/bg_map_title.png"></image>
			</view>
			<view class="content_box">
				<view class="item u-flex u-row-between u-font-28 u-p-16">
					<view>设备总数</view>
					<view class="val">300台</view>
				</view>
				<view class="u-m-t-16 u-flex">
					<view class="u-flex-1 item u-p-16">
						<view class="u-flex u-row-between u-font-28">
							<view>智能水表设备</view>
							<view class="val">300台</view>
						</view>
						<view class="u-m-t-16 u-flex">
							<view class="u-flex-1 u-flex u-flex-col u-row-center status_item u-font-24">
								<view class="danger u-font-weight u-font-50">3</view>
								<view>故障</view>
							</view>
							<view class="u-flex-1 u-flex u-flex-col u-row-center status_item u-font-24 u-m-l-8">
								<view class="u-font-weight u-font-50">3</view>
								<view>离线</view>
							</view>
						</view>
					</view>
					<view class="u-flex-1 item u-m-l-16 u-p-16">
						<view class="u-flex u-row-between u-font-28">
							<view>智能水表设备</view>
							<view class="val">300台</view>
						</view>
						<view class="u-m-t-16 u-flex">
							<view class="u-flex-1 u-flex u-flex-col u-row-center status_item u-font-24">
								<view class="danger u-font-weight u-font-50">3</view>
								<view>故障</view>
							</view>
							<view class="u-flex-1 u-flex u-flex-col u-row-center status_item u-font-24 u-m-l-8">
								<view class="u-font-weight u-font-50">3</view>
								<view>离线</view>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<!-- 设备详情弹窗 -->
		<view class="bottom_box" v-if="showDetail">
			<view class="title">
				<text>设备详情</text>
				<image src="/static/bg_map_title.png"></image>
			</view>
			<view class="content_box">
				<view class="item u-flex u-row-between u-font-28 u-p-16">
					<view>设备类型：</view>
					<view>智能水表</view>
				</view>
				<view class="item u-flex u-row-between u-font-28 u-p-16 u-m-t-16">
					<view>所属位置：</view>
					<view>东南校区/体育学院/宿舍楼</view>
				</view>
				<view class="item u-flex u-row-between u-font-28 u-p-16 u-m-t-16">
					<view>设备状态：</view>
					<view>正常</view>
				</view>
				<view class="navigator_btn u-m-t-16 u-p-16 u-flex u-row-center">
					<u-icon name="map" size="32rpx" color="#fff"></u-icon>
					<text>导航去这里</text>
				</view>
			</view>
		</view>
		<!-- 详情弹窗 -->
		<view class="window_box">
			<view class="title">
				<image src="/static/bg_window_title.png" class="title_bg"></image>
				<view class="u-flex u-p-24 u-rela">
					<image src="/static/icon_water.png" class="icon"></image>
					<text class="u-font-32 u-font-weight u-m-l-20">东南校区0093水表</text>
				</view>
			</view>
			<view class="item u-flex u-row-between">
				<view>年供水量</view>
				<view class="unit">
					<text class="val">12345</text>m³
				</view>
			</view>
			<view class="item u-flex u-row-between u-m-t-18">
				<view>瞬时供水量</view>
				<view class="unit">
					<text class="val">12345</text>m³
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				name: '',
				mapScale: 16,
				showDetail: true,
				markersList: []
			}
		},
		onLoad() {
			this.markersList.push({
				id: 1,
				latitude: '34.216052',
				longitude: '117.239753',
				iconPath: '/static/icon_device1.png',
				width: 44,
				height: 60,
				callout: {
					content: '商学院2022745水表',
					color: 'red',
					fontSize: 12,
					display: 'ALWAYS'
				}
			})
		},
		methods: {
			addScale() {
				this.mapScale++;
				if (this.mapScale >= 20) {
					this.mapScale = 20;
					this.$TOOL.toast('已经最大了');
				}
			},
			subtractScale() {
				this.mapScale--;
				if (this.mapScale <= 3) {
					this.mapScale = 3;
					this.$TOOL.toast('已经最小了');
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	page {
		height: 100%;
	}

	.container {
		width: 100vw;
		height: 100%;
		overflow: hidden;
		position: relative;

		map {
			width: 750rpx;
			height: 100%;
		}

		.search_box {
			width: 700rpx;
			height: 100rpx;
			background: rgba(255, 255, 255, 0.5);
			box-shadow: 0rpx 8rpx 8rpx 0rpx rgba(0, 0, 0, 0.21);
			border-radius: 25rpx 25rpx 25rpx 25rpx;
			border: 2rpx solid rgba(255, 255, 255, 0.3);
			padding: 16rpx;
			box-sizing: border-box;
			position: absolute;
			top: 24rpx;
			left: 25rpx;
			z-index: 3;
			display: flex;
			align-items: center;
			justify-content: space-between;
		}

		.right_control {
			position: fixed;
			right: 25rpx;
			bottom: 450rpx;
			z-index: 3;

			.u-icon-wrap {
				width: 82rpx;
				height: 82rpx;
				background: rgba(15, 35, 64, 0.35);
				box-shadow: 0rpx 8rpx 8rpx 0rpx rgba(0, 0, 0, 0.21);
				border-radius: 25rpx 25rpx 25rpx 25rpx;
				justify-content: center;
			}
		}

		.bottom_box {
			width: 700rpx;
			background: rgba(15, 35, 64, 0.75);
			box-shadow: 0rpx 8rpx 8rpx 0rpx rgba(0, 0, 0, 0.21);
			border-radius: 25rpx 25rpx 25rpx 25rpx;
			border: 2rpx solid rgba(255, 255, 255, 0.3);
			position: fixed;
			bottom: 25rpx;
			left: 25rpx;
			z-index: 3;

			.title {
				position: relative;
				z-index: 1;
				width: 100%;
				height: 58rpx;
				text-align: center;
				line-height: 48rpx;
				color: #fff;
				font-size: 32rpx;
				font-weight: bold;

				image {
					position: absolute;
					top: 0;
					left: 0;
					width: 100%;
					height: 100%;
					z-index: 2;
				}

				text {
					position: relative;
					z-index: 3;
				}
			}

			.content_box {
				padding: 4rpx 16rpx 16rpx;

				.item {
					color: rgba(255, 255, 255, 0.85);
					background: rgba(255, 255, 255, 0.12);
					border-radius: 16rpx;
				}

				.val {
					color: #CDE2FF;
				}

				.danger {
					color: #ED6A5E;
				}

				.status_item {
					background: rgba(0, 0, 0, 0.06);
					border-radius: 8rpx 8rpx 8rpx 8rpx;
					border: 2rpx solid rgba(255, 255, 255, 0.12);
					padding: 12rpx 0;
					line-height: 1.6;
				}

				.navigator_btn {
					background: #4378E7;
					border-radius: 16rpx;

					text {
						margin-left: 8rpx;
						color: rgba(255, 255, 255, 0.85);
						font-size: 28rpx;
					}
				}
			}
		}

		.window_box {
			width: 624rpx;
			background: rgba(15, 35, 64, 0.75);
			box-shadow: 0rpx 8rpx 8rpx 0rpx rgba(0, 0, 0, 0.21);
			border: 2rpx solid rgba(255, 255, 255, 0.3);
			position: fixed;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
			padding: 48rpx 24rpx 24rpx;
			border-radius: 16rpx;

			.title {
				position: absolute;
				top: -48rpx;
				left: -2rpx;
				z-index: 1;
				width: 624rpx;
				height: 90rpx;
				text-align: center;
				color: #fff;
				font-size: 32rpx;
				font-weight: bold;
			}

			.title_bg {
				position: absolute;
				top: 0;
				left: 0;
				width: 100%;
				height: 100%;
			}

			.icon {
				width: 48rpx;
				height: 48rpx;
			}

			.item {
				color: #fff;
				font-weight: bold;
				font-size: 28rpx;

				.unit {
					color: rgba(255, 255, 255, 0.65);
					font-weight: normal;
				}

				.val {
					font-weight: bold;
					color: #fff;
					font-size: 36rpx;
					margin-right: 12rpx;
				}
			}
		}
	}
</style>