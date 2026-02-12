<template>
	<PageContainer :safeArea="true" :screen="true" background="#F8F8F8">
		<FlexScroll ref="flexContainer" :loading="loading" :load-status="loadStatus" @scrolltolower="loadMore"
			@refresh="onRefresh">
			<template #header>
				<NavBar title="告警中心" show-back background="#4378E7" />
				<AlarmTabs :tabsList="['实时告警', '离线告警', '历史告警']" @change="handleTabChange" />
				<SearchBox />
			</template>
			<AlarmCard v-for="item in list" :key="item.id" @detail="handleShowDetail">

			</AlarmCard>
			<AlarmDetailPopup v-model="showDetail" :detail-info="detailInfo" @report="handleReport"
				@ignore="handleIgnore" @mistake="handleMistake" @delete="handleDelete" @map="handleMap" />
		</FlexScroll>
	</PageContainer>
</template>

<script setup>
	import PageContainer from "@/components/hh-page-container/hh-page-container.vue";
	import NavBar from "@/components/hh-nav-bar/hh-nav-bar.vue"
	import FlexScroll from "@/components/hh-flex-scroll.vue"
	import AlarmTabs from './components/AlarmTabs.vue'
	import SearchBox from "./components/SearchBox.vue";
	import AlarmCard from "./components/AlarmCard.vue";
	import AlarmDetailPopup from "./components/AlarmDetailPopup.vue";
	import {
		ref
	} from "vue"

	// console.log(ref);
	const handleTabChange = (index) => {
		console.log('当前选中的索引：', index)
	}
	const showDetail = ref(false)
	const detailInfo = ref({})
	const list = ref([])
	const loading = ref(false)
	const loadStatus = ref('loadmore')
	const flexContainer = ref()

	const handleShowDetail = () => {

		showDetail.value = !showDetail.value
		console.log(showDetail.value);
	}

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

	// 处理上报
	const handleReport = () => {
		console.log('上报告警')
	}

	// 处理忽略
	const handleIgnore = () => {
		console.log('忽略告警')
	}

	// 处理误报
	const handleMistake = () => {
		console.log('标记为误报')
	}

	// 处理删除
	const handleDelete = () => {
		console.log('删除告警')
	}

	// 查看地图
	const handleMap = () => {
		console.log('查看地图定位')
	}


	fetchData().then(res => {
		list.value = res
	})
</script>

<style lang="scss" scoped>

</style>