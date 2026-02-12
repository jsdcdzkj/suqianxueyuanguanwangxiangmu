<template>
	<view class="nav-bar" :style="Style.nav2">
		<view class="nav-bar-container" :style="Style.nav">
			<view class="status" :style="Style.status"></view>
			<view class="nav content" :style="Style.content">
				<view class="left">
					<uni-icons type="arrow-left" v-if="showBack" :color="backColor" :size="20"
						style="position: absolute; left: 10px" @click="$TOOL.back()"></uni-icons>
					<template v-else>
						<slot name="left"></slot>
					</template>
				</view>
				<view class="nav-title" :style="{ color: titleColor }" v-if="$slots.default">
					<slot></slot>
				</view>
				<view class="nav-title" :style="{ color: titleColor }" v-else>{{ title }}</view>
				<view class="right">
					<slot name="right"></slot>
				</view>
			</view>
		</view>
		<image :src="image" mode="aspectFill" v-if="image" class="image" :style="{ height: `${maxHeight}px` }"></image>
	</view>
</template>

<script>
	/**
	 * @property {String} statusBackgroundColor 状态栏样式
	 * @property {String} background  背景颜色
	 * @property {String} title  标题
	 * @property {String} showBack  显示返回按钮
	 * @property {String} backColor  返回图标颜色
	 * @property {String} statusTheme 状态栏主题
	 * @value black 黑色主题
	 * @value white 白色主题
	 * @property {String} titleColor 标题颜色
	 */
	import {
		computed,
		reactive
	} from 'vue';

	export default {
		props: {
			statusBackgroundColor: {
				type: String,
				default: 'transparent'
			},
			statusTheme: {
				type: String,
				default: ''
			},
			background: {
				type: String,
				default: 'transparent'
			},
			title: {
				type: String,
				default: '标题'
			},
			showBack: {
				type: Boolean,
				default: false
			},
			backColor: {
				type: String,
				default: '#fff'
			},
			titleColor: {
				type: String,
				default: '#fff'
			},
			image: {
				type: String,
				default: ''
			}
		},
		data() {
			return {
				img: '',
				maxHeight: 0,
				height: 0,
				statusBarHeight: 0
			};
		},
		computed: {
			Style() {
				return {
					nav: {
						height: this.maxHeight + 'px',
						backgroundColor: this.background
					},
					nav2: {
						height: this.maxHeight + 'px',
						// backgroundColor: this.background
					},
					status: {
						height: this.statusBarHeight + 'px',
						backgroundColor: this.statusBackgroundColor
					},
					content: {
						height: this.height + 'px'
					}
				};
			}
		},
		created() {
			if (this.image) {
				this.im = this.image.startsWith('http') ? this.image : import(this.image);
			}
			const statusBarHeight = uni.getSystemInfoSync().statusBarHeight
			const navigationBarHeight = 44;
			this.maxHeight = navigationBarHeight + statusBarHeight;
			this.height = navigationBarHeight;
			this.statusBarHeight = statusBarHeight;
		},
		mounted() {
			// 设置状态栏背景和文本颜色
			if (this.statusTheme === 'black') {
				uni.setNavigationBarColor({
					backgroundColor: '#000000',
					frontColor: '#ffffff'
				});
			} else if (this.statusTheme === 'white') {
				uni.setNavigationBarColor({
					backgroundColor: '#ffffff',
					frontColor: '#000000'
				});
			}
		}
	};
</script>

<style lang="scss" scoped>
	.nav-title {
		font-weight: bold;
		font-size: 34rpx;
	}

	.left {
		position: absolute;
		left: 0;
		top: 0;
		width: 100px;
		height: 100%;
		display: flex;
		align-items: center;
		padding-left: 20rpx;
	}

	.right {
		position: absolute;
		right: 0;
		top: 0;
		width: 100px;
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		padding-right: 20rpx;
	}

	.nav-bar {
		width: 100%;

		.image {
			height: 100%;
			width: 100%;
			position: fixed;
			top: 0;
			left: 0;
			z-index: 888;
		}

		&>.nav {
			&.content {
				position: absolute;
				top: 0;
			}
		}

		.nav-bar-container {
			position: fixed;
			top: 0;
			width: 100%;
			z-index: 999;

			.status {
				width: 100%;
			}

			.content {
				padding: 0 100rpx;
				text-align: center;
				display: flex;
				align-items: center;
				justify-content: center;
				position: relative;
			}
		}
	}
</style>