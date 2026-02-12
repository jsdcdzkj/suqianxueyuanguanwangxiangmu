<template>
	<PageContainer :safeArea="true" :screen="true" background="#F8F8F8">
		<FlexScroll ref="flexContainer" :loading="loading" :load-status="loadStatus" @scrolltolower="loadMore"
			@refresh="onRefresh">
			<template #header>
				<NavBar title="报事报修" show-back background="#4378E7" />
				<InputSearch />
				<SwitchBox />
			</template>
			<RepairCard v-for="item in list" :key="item.id">

			</RepairCard>
		</FlexScroll>
		<view class="add_slip" @click="handlerAdd">
			<!-- <uni-icons type="plusempty" size="25" color="#fff"></uni-icons> -->
			<image :src="`${$publicImage}/slip/pencil-ai-2-fill.png`" class="slip_icon" />
			<text>创建</text>
		</view>
	</PageContainer>
</template>

<script setup>
	import PageContainer from "@/components/hh-page-container/hh-page-container.vue";
	import NavBar from "@/components/hh-nav-bar/hh-nav-bar.vue"
	import FlexScroll from "@/components/hh-flex-scroll.vue"
	import InputSearch from "./components/InputSearch.vue";
	import SwitchBox from "./components/SwitchBox.vue";
	import RepairCard from "./components/RepairCard.vue";
	import {
		ref
	} from "vue"

	const list = ref([])
	const loading = ref(false)
	const loadStatus = ref('loadmore')
	const flexContainer = ref()


	// 模拟获取数据
	const fetchData = async () => {
		// 返回模拟数据
		return Array.from({
			length: 10
		}, (_, i) => ({
			id: Date.now() + i,
			content: `列表项 ${list.value.length + i + 1}`
		}))
	}

	// 加载更多
	const loadMore = async () => {
		loading.value = true
		loadStatus.value = 'loading'

		try {
			// 模拟API调用
			await new Promise(resolve => setTimeout(resolve, 1000))
			// 添加新数据到列表
			const newData = await fetchData()
			list.value.push(...newData)

			// 判断是否还有更多数据
			loadStatus.value = newData.length < 10 ? 'nomore' : 'loadmore'
		} catch (error) {
			console.error('加载失败:', error)
			loadStatus.value = 'loadmore'
		} finally {
			loading.value = false
		}
	}

	// 下拉刷新
	const onRefresh = async () => {
		try {

			// 模拟API调用
			await new Promise(resolve => setTimeout(resolve, 1000))
			// 重新加载数据
			list.value = await fetchData()
			loadStatus.value = 'loadmore'
			flexContainer.value.stopRefresh()


		} catch (error) {
			console.error('刷新失败:', error)
		} finally {
			// 停止下拉刷新动画
			flexContainer.value.stopRefresh()
		}
	}

	const handlerAdd = () => {
		uni.navigateTo({
			url: "/pagesOther/repair/add"
		})
	}

	fetchData().then(res => {
		list.value = res
	})
</script>

<style lang="scss" scoped>
	.add_slip {
		position: fixed;
		bottom: 168rpx;
		right: 3%;
		width: 112rpx;
		height: 112rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		font-size: 24rpx;
		color: #fff;
		line-height: 40rpx;
		background: #5187ff;
		border-radius: 50%;
		box-shadow: 0px 3px 6px 0px rgba(0, 0, 0, 0.1);

		.slip_icon {
			width: 34rpx;
			height: 34rpx;
		}
	}

	.add_slip {
		position: fixed;
		bottom: 168rpx;
		right: 3%;
		width: 112rpx;
		height: 112rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		font-size: 24rpx;
		color: #fff;
		line-height: 40rpx;
		background: #5187ff;
		border-radius: 50%;
		box-shadow: 0px 3px 6px 0px rgba(0, 0, 0, 0.1);

		.slip_icon {
			width: 34rpx;
			height: 34rpx;
		}
	}

	.add_slip {
		position: fixed;
		bottom: 168rpx;
		right: 3%;
		width: 112rpx;
		height: 112rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		font-size: 24rpx;
		color: #fff;
		line-height: 40rpx;
		background: #5187ff;
		border-radius: 50%;
		box-shadow: 0px 3px 6px 0px rgba(0, 0, 0, 0.1);

		.slip_icon {
			width: 34rpx;
			height: 34rpx;
		}
	}

	.add_slip {
		position: fixed;
		bottom: 168rpx;
		right: 3%;
		width: 112rpx;
		height: 112rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		font-size: 24rpx;
		color: #fff;
		line-height: 40rpx;
		background: #5187ff;
		border-radius: 50%;
		box-shadow: 0px 3px 6px 0px rgba(0, 0, 0, 0.1);

		.slip_icon {
			width: 34rpx;
			height: 34rpx;
		}
	}

	.add_slip {
		position: fixed;
		bottom: 168rpx;
		right: 3%;
		width: 112rpx;
		height: 112rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		font-size: 24rpx;
		color: #fff;
		line-height: 40rpx;
		background: #5187ff;
		border-radius: 50%;
		box-shadow: 0px 3px 6px 0px rgba(0, 0, 0, 0.1);

		.slip_icon {
			width: 34rpx;
			height: 34rpx;
		}
	}
</style>