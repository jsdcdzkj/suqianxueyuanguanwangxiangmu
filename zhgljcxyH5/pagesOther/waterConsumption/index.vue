<template>
	<view class="container">
		<AlarmTabs :tabsList="['日用水量','月用水量']" :defaultIndex="tabs" @change="changeTabs"></AlarmTabs>
		<view class="filter_box u-flex">
			<picker class="picker_box u-flex-1">
				<view>2025年12月<u-icon color="rgba(0, 0, 0, 0.25)" size="32rpx" class="u-m-l-8"
						name="arrow-down"></u-icon></view>
			</picker>
			<u-line direction="col" color="rgba(0, 0, 0, 0.06)" length="90rpx"></u-line>
			<picker class="picker_box u-flex-1">
				<view>全部区域<u-icon color="rgba(0, 0, 0, 0.25)" size="32rpx" class="u-m-l-8" name="arrow-down"></u-icon>
				</view>
			</picker>
		</view>
		<scroll-view scroll-y>
			<view class="chartItem"></view>
			<view class="total_box">
				<view class="total u-p-24 u-flex">
					<image src="/static/icon_water1.png" mode=""></image>
					<view class="u-m-l-24">
						<view class="label">本月累计用水(m³)</view>
						<view class="val">436.3</view>
					</view>
				</view>
				<view class="u-p-24">
					<template v-if="tabs==0">
						<view class="th u-flex u-text-center">
							<view class="u-flex-1">
								时间
							</view>
							<view class="u-flex-1">
								水量(立方米)
							</view>
						</view>
						<view>
							<view class="tr u-flex u-text-center">
								<view class="u-flex-1">
									2025-12-01
								</view>
								<view class="u-flex-1">
									117
								</view>
							</view>
							<view class="tr u-flex u-text-center">
								<view class="u-flex-1">
									2025-12-02
								</view>
								<view class="u-flex-1">
									785
								</view>
							</view>
							<view class="tr u-flex u-text-center">
								<view class="u-flex-1">
									2025-12-01
								</view>
								<view class="u-flex-1">
									117
								</view>
							</view>
							<view class="tr u-flex u-text-center">
								<view class="u-flex-1">
									2025-12-02
								</view>
								<view class="u-flex-1">
									785
								</view>
							</view>
							<view class="tr u-flex u-text-center">
								<view class="u-flex-1">
									2025-12-01
								</view>
								<view class="u-flex-1">
									117
								</view>
							</view>
							<view class="tr u-flex u-text-center">
								<view class="u-flex-1">
									2025-12-02
								</view>
								<view class="u-flex-1">
									785
								</view>
							</view>
						</view>
					</template>
					<template v-if="tabs==1">
						<view class="month_card">
							<view class="title u-flex u-row-between">
								<view class="month">2025年12月</view>
								<view class="day">2025-12-1～2025-12-31</view>
							</view>
							<view class="u-flex">
								<view class="u-flex-1 u-flex-col u-row-center u-col-center u-p-t-20 u-p-b-20">
									<view class="val">1300</view>
									<view class="label">本期用水(万m³)</view>
								</view>
								<view class="u-flex-1 u-flex-col u-row-center u-col-center u-p-t-20 u-p-b-20">
									<view class="val red">+13%</view>
									<view class="label">往年同比(%)</view>
								</view>
							</view>
						</view>
						<view class="month_card">
							<view class="title u-flex u-row-between">
								<view class="month">2025年11月</view>
								<view class="day">2025-11-1～2025-11-30</view>
							</view>
							<view class="u-flex">
								<view class="u-flex-1 u-flex-col u-row-center u-col-center u-p-t-20 u-p-b-20">
									<view class="val">1300</view>
									<view class="label">本期用水(万m³)</view>
								</view>
								<view class="u-flex-1 u-flex-col u-row-center u-col-center u-p-t-20 u-p-b-20">
									<view class="val green">-13%</view>
									<view class="label">往年同比(%)</view>
								</view>
							</view>
						</view>
					</template>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import AlarmTabs from '/pagesOther/alarm/components/AlarmTabs.vue'
	export default {
		data() {
			return {
				tabs: 1
			}
		},
		components: {
			AlarmTabs
		},
		onLoad() {

		},
		methods: {
			changeTabs(index) {
				this.tabs = index;
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
		display: flex;
		flex-direction: column;
		overflow: hidden;
		background: #F8F8F8;

		.filter_box {
			text-align: center;
			background: #fff;
			color: rgba(0, 0, 0, 0.85);
			font-size: 28rpx;
		}

		scroll-view {
			flex: 1;
			overflow: auto;
			box-sizing: border-box;
			padding: 24rpx;
			scrollbar-width: none;
			-ms-overflow-style: none;

			&::-webkit-scrollbar {
				display: none;
				width: 0;
				height: 0;
			}
		}

		.chartItem {
			width: 100%;
			height: 458rpx;
			background: #fff;
			border-radius: 24rpx;
		}

		.total_box {
			background: #fff;
			border-radius: 24rpx;
			margin-top: 24rpx;

			.total {
				border-bottom: 1px solid rgba(0, 0, 0, 0.06);

				image {
					width: 128rpx;
					height: 128rpx;
				}

				.label {
					color: rgba(0, 0, 0, 0.65);
					font-size: 28rpx;
				}

				.val {
					color: rgba(0, 0, 0, 0.85);
					font-size: 58rpx;
					font-weight: bold;
				}
			}

			.th,
			.tr {
				background: #E7F0FA;
				color: #4378E7;
				font-size: 28rpx;
				height: 74rpx;
				border-radius: 24rpx;
			}

			.tr {
				background: #fff;

				&:nth-child(even) {
					background: #F8F8F8;
				}
			}

			.month_card {
				margin-bottom: 24rpx;
				border-radius: 24rpx;
				border: 1px solid rgba(0, 0, 0, 0.06);
				overflow: hidden;

				.title {
					padding: 18rpx 24rpx;
					background: #F8F8F8;

					.month {
						color: rgba(0, 0, 0, 0.85);
						font-size: 28rpx;
						font-weight: bold;
					}

					.day {
						color: rgba(0, 0, 0, 0.45);
						font-size: 24rpx;
					}
				}

				.val {
					font-weight: bold;
					color: rgba(0, 0, 0, 0.85);
					font-size: 40rpx;
					line-height: 66rpx;

					&.green {
						color: #57BD94;
					}

					&.red {
						color: #ED777A;
					}
				}

				.label {
					color: rgba(0, 0, 0, 0.65);
					font-size: 24rpx;
				}
			}
		}
	}
</style>