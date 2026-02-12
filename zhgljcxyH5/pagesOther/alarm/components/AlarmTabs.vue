<template>
	<view class="tabs-container">
		<view class="tabs-box">
			<view v-for="(item, index) in tabsList" :key="index" class="tab-item"
				:class="{ active: currentIndex === index }" @click="handleTabClick(index)">
				{{ item }}
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue'

	const props = defineProps({
		tabsList: {
			type: Array,
			default: () => []
		},
		defaultIndex: {
			type: Number,
			default: 0
		}
	})

	const emit = defineEmits(['change'])

	const currentIndex = ref(props.defaultIndex)

	const handleTabClick = (index) => {
		currentIndex.value = index
		emit('change', index)
	}
</script>

<style lang="scss" scoped>
	.tabs-container {

		background: #4378E7;
		border-radius: 0px;

		padding: 16rpx 25rpx;

		.tabs-box {
			height: 67rpx;
			background: rgba(255, 255, 255, 0.12);
			border-radius: 8rpx;
			border: 2rpx solid rgba(255, 255, 255, 0.3);
			display: flex;
			overflow: hidden;

			.tab-item {
				flex: 1;
				height: 67rpx;
				background: rgba(255, 255, 255, 0);
				border-radius: 0rpx;
				border-right: 2rpx solid rgba(255, 255, 255, 0.2);
				font-weight: normal;
				font-size: 29rpx;
				color: rgba(255, 255, 255, 0.85);
				line-height: 46rpx;
				text-align: center;
				display: flex;
				align-items: center;
				justify-content: center;


				&:last-child {
					border-right: none
				}

				&.active {
					background: #FFFFFF;
					color: #4378E7;
					border-color: transparent;
					position: relative;
					z-index: 1;
				}
			}
		}
	}
</style>