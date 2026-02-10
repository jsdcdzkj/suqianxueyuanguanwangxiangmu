<template>
	<view class="container">
		<map name="" latitude="34.216052" longitude="117.239753" :markers="markersList" :enable-zoom="true"
			:enable-scroll="true"></map>
		<view class="search_box">
			<u-search shape="rounded" v-model="name" bg-color="rgba(0,0,0,0.04)"
				:input-style="{background:'transparent'}" placeholder="请输入设备名称"
				:action-style="{background:'#4378E7',color:'#fff',borderRadius:'16rpx',height:'60rpx',width:'108rpx',lineHeight:'60rpx',boxSizing:'border-box'}"></u-search>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				name: '',
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
				height: 60
			})
		},
		methods: {

		}
	}
</script>

<style lang="scss" scoped>
	.container {
		width: 100vw;
		height: 100vh;
		overflow: hidden;

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
			position: fixed;
			top: 24rpx;
			left: 25rpx;
			z-index: 3;
			display: flex;
			align-items: center;
			justify-content: space-between;
		}
	}
</style>