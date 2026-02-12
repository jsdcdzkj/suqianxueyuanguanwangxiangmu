<template>
	<view class="search-box">
		<view class="search-input-wrapper">
			<uni-icons type="search" size="16" color="#999" class="search-icon"></uni-icons>
			<input class="search-input" type="text" v-model="searchValue" :placeholder="placeholder"
				@confirm="handleSearch" />
		</view>
		<view class="search-btn" @click="handleSearch">
			<text class="btn-text">搜索</text>
		</view>
	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue'

	// 定义props
	const props = defineProps({
		placeholder: {
			type: String,
			default: '请输入搜索内容'
		}
	})

	// 定义emits
	const emit = defineEmits(['search'])

	// 搜索值
	const searchValue = ref('')

	// 搜索处理
	const handleSearch = () => {
		if (!searchValue.value.trim()) {
			uni.showToast({
				title: '请输入搜索内容',
				icon: 'none'
			})
			return
		}
		emit('search', searchValue.value)
	}
</script>

<style lang="scss" scoped>
	.search-box {
		width: 100%;
		height: 100rpx;
		background: #FFFFFF;
		padding: 16rpx 24rpx;
		display: flex;
		align-items: center;


		.search-input-wrapper {
			flex: 1;
			height: 67rpx;
			background: #F4F4F4;
			border-radius: 17rpx;
			display: flex;
			align-items: center;
			padding: 0 20rpx;
			margin-right: 24rpx;

			.search-icon {
				margin-right: 10rpx;
			}

			.search-input {
				flex: 1;
				height: 100%;
				font-size: 28rpx;

				&::placeholder {
					color: #999;
				}
			}
		}

		.search-btn {
			width: 108rpx;
			height: 67rpx;
			background: #4378E7;
			border-radius: 17rpx;
			display: flex;
			align-items: center;
			justify-content: center;

			.btn-text {
				color: #FFFFFF;
				font-size: 28rpx;
			}
		}
	}
</style>