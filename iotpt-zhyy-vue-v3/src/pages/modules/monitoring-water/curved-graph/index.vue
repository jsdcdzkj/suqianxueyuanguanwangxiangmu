<template>
	<PageLayout :show-page-top="false">
		<template #pageContent>
			<div class="h-100% w-100% overflow-hidden flex flex-col">
				<TopTabbar />
				<KeyIndicatorCard class="p-12px flex-1 overflow-hidden">
					<template #header>
						<div class="flex items-center gap-12px">
							<el-checkbox-group v-model="checkList">
								<el-checkbox label="99">宿迁学院</el-checkbox>
								<el-checkbox label="1">一级分区</el-checkbox>
								<el-checkbox label="2">二级分区</el-checkbox>
								<el-checkbox label="3">三级分区</el-checkbox>
								<el-checkbox label="4">核减大用户水量</el-checkbox>
							</el-checkbox-group>
							<div class="title-unit">单位：m³/h</div>
						</div>
					</template>
					<template #actions>
						<div class="flex items-center gap-12px">
							<span class="line-item">平衡</span>
							<span class="line-item warning">预警</span>
							<span class="line-item danger">告警</span>
						</div>
					</template>

					<!-- 卡片列表容器 -->
					<div class="card-list-container">
						<div class="card-list">
							<WaterTrendCard
								v-for="item in currentPageData"
								:key="item.id"
								:title="item.title"
								:tags="item.tags"
							/>
						</div>

						<!-- 分页器 -->
						<div class="pagination-container">
							<el-pagination
								v-model:current-page="currentPage"
								v-model:page-size="pageSize"
								:page-sizes="[12, 24, 36, 48]"
								:total="total"
								background
								layout="total, sizes, prev, pager, next, jumper"
								@size-change="handleSizeChange"
								@current-change="handleCurrentChange"
							/>
						</div>
					</div>
				</KeyIndicatorCard>
			</div>
		</template>
	</PageLayout>
</template>

<script setup>
	import { ref, computed } from "vue";
	import PageLayout from "@/layouts/PageStructure/PageLayout";
	import TopTabbar from "../traffic-topology-diagram/components/TopTabbar.vue";
	import KeyIndicatorCard from "../energy-planning/components/KeyIndicatorCard.vue";
	import WaterTrendCard from "./components/WaterTrendCard.vue";

	const checkList = ref([]);
	const currentPage = ref(1);
	const pageSize = ref(12);
	const total = ref(100);

	// 模拟数据
	const mockData = Array.from({ length: 100 }, (_, index) => ({
		id: index + 1,
		title: `监测点 ${index + 1}`,
		tags: ["一级分区"]
	}));

	// 计算当前页显示的数据
	const currentPageData = computed(() => {
		const start = (currentPage.value - 1) * pageSize.value;
		const end = start + pageSize.value;
		return mockData.slice(start, end);
	});

	const handleSizeChange = (val) => {
		pageSize.value = val;
		currentPage.value = 1;
	};

	const handleCurrentChange = (val) => {
		currentPage.value = val;
	};
</script>

<style scoped lang="scss">
	.card-list-container {
		display: flex;
		flex-direction: column;
		height: 100%;
		padding: 12px;
		gap: 16px;
	}

	.card-list {
		flex: 1;
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
		grid-template-rows: repeat(auto-fill, 246px);
		gap: 12px;
		overflow-y: auto;
	}

	.pagination-container {
		display: flex;
		justify-content: flex-end;
		padding: 0;
	}

	// 保留原有的样式
	#container {
		width: 100%;
		height: 100%;
		min-height: calc(100vh - 64px);
		background: #f5f5f5;
	}

	::v-deep(.el-checkbox-group .el-checkbox) {
		margin-right: 12px;
	}

	.title-unit {
		display: flex;
		align-items: center;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		&::before {
			content: "";
			display: inline-block;
			width: 1px;
			height: 16px;
			background: rgba(0, 0, 0, 0.15);
			margin-right: 12px;
		}
	}

	.line-item {
		display: inline-block;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		&::before {
			content: "";
			display: inline-block;
			width: 16px;
			height: 4px;
			background: #57bd94;
			vertical-align: middle;
			margin-right: 6px;
		}
		&.warning {
			&::before {
				background: #f5a623;
			}
		}
		&.danger {
			&::before {
				background: #f52323;
			}
		}
	}
</style>
