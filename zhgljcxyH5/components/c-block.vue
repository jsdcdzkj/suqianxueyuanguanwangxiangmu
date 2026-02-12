<template>
	<view :class="'box-block ' + customClass">
		<slot></slot>
	</view>	
</template>

<script>
	export default {
		name: "boxblock",
		props: {
			customClass: {
				type: String,
				value: ""
			},
			isFillet: {
				type: Boolean,
				value: true
			}
		},
	}
</script>

<style lang="scss">
	.box-block {
		padding: 24rpx;
		background: #fff;
		margin-bottom:24rpx;
	}
	.box-block-y {
		padding: 24rpx;
		background: #fff;
		margin-bottom:24rpx;
		border-radius:10rpx;
	}
</style>