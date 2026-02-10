<template>
	<PageLayout gap="12px" header-height="140px" sidebar-width="320px">
		<template #pageTop>
			<div class="flex gap-12px w-100%">
				<MonthTotalCard title="本月用水" unit="m3" contrastTitle="上月用水" v-bind="headerData.water">
					<template #chart>
						<WaterBar v-bind="headerData.water.chart"></WaterBar>
					</template>
				</MonthTotalCard>
				<MonthTotalCard
					icon-name="money-cny-box-fill.png"
					title="本月费用"
					unit="元"
					contrastTitle="上月费用"
					v-bind="headerData.cost"
				>
					<template #chart>
						<WaterCostLine v-bind="headerData.cost.chart"></WaterCostLine>
					</template>
				</MonthTotalCard>
			</div>
		</template>
		<template #pageContent>
			<StructureCard title="分区用水排行">
				<template #actions>
					<div class="filter-form">
						<!-- 年月日单选按钮组 -->
						<el-radio-group v-model="dateType" size="default">
							<el-radio-button label="year">年</el-radio-button>
							<el-radio-button label="month">月</el-radio-button>
							<el-radio-button label="date">日</el-radio-button>
						</el-radio-group>

						<!-- 设备名称输入框 -->
						<el-input
							v-model="queryParams.deviceName"
							placeholder="请输入设备名称"
							clearable
							style="width: 200px"
						/>

						<!-- 设备状态下拉框 -->
						<el-select
							v-model="queryParams.status"
							placeholder="请选择设备状态"
							clearable
							style="width: 150px"
						>
							<el-option label="在线" value="online" />
							<el-option label="离线" value="offline" />
							<el-option label="故障" value="error" />
						</el-select>

						<div>
							<!-- 查询按钮 -->
							<el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>

							<!-- 重置按钮 -->
							<el-button :icon="Refresh" @click="handleReset">重置</el-button>
						</div>
					</div>
				</template>
				<div class="flex gap-12px flex-col h-100%">
					<div class="flex gap-12px">
						<ContrastDetail />
						<ContrastDetail icon-name="guan_red_1.png">
							<template #operation>
								<WaterStatusButton :isUp="false">-15.2%</WaterStatusButton>
							</template>
							<template #label>
								<el-select
									v-model="queryParams.status"
									placeholder="请选择设备状态"
									clearable
									style="width: 150px"
								>
									<el-option label="在线" value="online" />
									<el-option label="离线" value="offline" />
									<el-option label="故障" value="error" />
								</el-select>
							</template>
						</ContrastDetail>
					</div>
					<WaterContrastLine />
					<ToDayRealTimeEnergy />
				</div>
			</StructureCard>
		</template>
		<template #pageLeft>
			<StructureCard title="请选择区域">
				<LeftAreaTreeSelect />
			</StructureCard>
		</template>
	</PageLayout>
</template>

<script setup lang="tsx">
	import PageLayout from "@/layouts/PageStructure/PageLayout";
	import StructureCard from "@/components/card/StructureCard";
	import { getRealtimeTop } from "@/api/monitoring-water/realtime";
	import MonthTotalCard from "./components/MonthTotalCard.vue";
	import WaterBar from "./echarts/WaterBar.vue";
	import WaterCostLine from "./echarts/WaterCostLine.vue";
	import LeftAreaTreeSelect from "./components/LeftAreaTreeSelect.vue";
	import { reactive, ref } from "vue";
	import { Search, Refresh } from "@element-plus/icons-vue";
	import ContrastDetail from "./components/ContrastDetail.vue";
	import WaterStatusButton from "./components/WaterStatusButton.vue";
	import WaterContrastLine from "./echarts/WaterContrastLine.vue";
	import ToDayRealTimeEnergy from "./components/ToDayRealTimeEnergy.vue";

	// --- 数据定义 ---

	// 日期类型：年/月/日
	const dateType = ref("date");

	// 查询参数对象
	const queryParams = reactive({
		deviceName: "",
		status: ""
	});

	// --- 方法 ---

	// 查询方法
	const handleQuery = () => {
		console.log("查询参数:", {
			dateType: dateType.value,
			...queryParams
		});
	};

	// 重置方法
	const handleReset = () => {
		dateType.value = "date";
		queryParams.deviceName = "";
		queryParams.status = "";
	};

	const headerData = reactive({
		water: {
			value: 0,
			contrastValue: 0,
			contrast: {
				value: 0,
				type: "down"
			},
			chart: {
				xdata: [],
				ydata: [],
				ydata1: []
			}
		},
		cost: {
			value: 0,
			contrastValue: 0,
			contrast: {
				value: 0,
				type: "down"
			},
			chart: {
				xdata: [],
				ydata: []
			}
		}
	});

	getRealtimeTop().then(({ data }) => {
		console.log(data);

		// 用水
		headerData.water.value = data.currentMonth.totalWater;
		headerData.water.contrastValue = data.lastMonth.totalWater;
		headerData.water.contrast.value = data.comparison.waterChangePercent;
		headerData.water.contrast.type = data.comparison.waterType;
		headerData.water.chart.xdata = data.dateLabels.slice(0, 7);
		headerData.water.chart.ydata = data.currentMonth.dailyWaterUsage.slice(0, 7);
		headerData.water.chart.ydata1 = data.lastMonth.dailyWaterUsage.slice(0, 7);

		// 费用
		headerData.cost.value = data.currentMonth.totalCost;
		headerData.cost.contrastValue = data.lastMonth.totalCost;
		headerData.cost.contrast.value = data.comparison.costChangePercent;
		headerData.cost.contrast.type = data.comparison.costType;
	});
</script>
<style lang="scss" scoped>
	.items {
		height: 36px;
		line-height: 36px;
		text-align: left;
		border-radius: 4px 4px 4px 4px;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		padding-left: 16px;
	}

	.active {
		background: #e8f3ff;
	}

	/* 筛选表单容器样式 */
	.filter-form {
		display: flex;
		align-items: center;
		gap: 12px; /* 控制子元素之间的间距 */
	}

	/* 如果需要在小屏幕下自动换行，可以添加媒体查询 */
	@media (max-width: 768px) {
		.filter-form {
			flex-wrap: wrap;
		}
	}
</style>
