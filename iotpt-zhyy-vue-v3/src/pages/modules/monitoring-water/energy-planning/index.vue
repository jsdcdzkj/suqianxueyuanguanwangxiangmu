<template>
	<PageLayout :show-page-top="false" gap="12px" sidebar-width="60%">
		<template #pageLeft>
			<div class="flex flex-col gap-12px h-100% overflow-hidden">
				<StructureCard show-title-popup class="h-492px" title="关键指标">
					<div class="flex h-100% w-100% gap-12px">
						<KeyIndicatorCard title="大额用水" class="flex-1">
							<KeyIndicatorsBar v-bind="barChartData" />
						</KeyIndicatorCard>
						<KeyIndicatorCard title="近7日用水趋势" class="flex-1">
							<WeekDayTrendLine />
						</KeyIndicatorCard>
					</div>
				</StructureCard>
				<StructureCard show-title-popup title="部门定额监测">
					<template #actions>
						<el-button type="primary" :icon="Setting">设置分项月定额</el-button>
					</template>
					<DepartmentQuotaMonitoring />
				</StructureCard>
			</div>
		</template>
		<template #pageContent>
			<div class="flex flex-col gap-12px h-100% overflow-hidden">
				<StructureCard show-title-popup title="用能异常" class="h-313px flex-shrink-0">
					<template #actions>
						<div class="m-r-12px flex items-center gap-8px">
							<span class="tag-item"> 正常 </span>
							<span class="tag-item warning"> 异常 </span>
							<span class="tag-item danger"> 警告 </span>
						</div>
						<el-button type="primary">用能规划</el-button>
					</template>
					<KeyIndicatorCard title="近7日用水趋势" class="flex-1">
						<template #actions>
							<div class="card-actions">10%</div>
						</template>
						<div class="flex flex-col h-100% gap-12px">
							<div class="flex gap-12px m-t-12px flex-1">
								<WaterMonCard title="999" description="实际用水(m³)" />
								<WaterMonCard name="water-2.png" description="年总定额(m³)" title="888" />
							</div>
							<div class="flex flex-col gap-12px">
								<ProgressBar :progress="80" title="已过时间占比" />
								<ProgressBar :progress="80" title="实际能耗占年总定额比" />
							</div>
						</div>
					</KeyIndicatorCard>
				</StructureCard>
				<StructureCard title="夜间最小流量均值（MNF）" body-padding="12px">
					<template #actions>
						<el-radio-group v-model="radio1">
							<el-radio-button label="1">近30天</el-radio-button>
							<el-radio-button label="2">近12月</el-radio-button>
						</el-radio-group>
					</template>
					<div class="w-100% h-100% overflow-hidden">
						<WaterMNFLine />
					</div>
				</StructureCard>
				<StructureCard title="全校人均用水趋势">
					<template #actions>
						<el-radio-group v-model="radio1">
							<el-radio-button label="1">近30天</el-radio-button>
							<el-radio-button label="2">近12月</el-radio-button>
						</el-radio-group>
					</template>
					<div class="w-100% h-100% overflow-hidden">
						<PerCapitaWaterLine />
					</div>
				</StructureCard>
			</div>
		</template>
	</PageLayout>
</template>

<script setup lang="tsx">
	import PageLayout from "@/layouts/PageStructure/PageLayout";
	import StructureCard from "@/components/card/StructureCard";
	import KeyIndicatorsBar from "./echarts/KeyIndicatorsBar.vue";
	import KeyIndicatorCard from "./components/KeyIndicatorCard.vue";
	import WeekDayTrendLine from "./echarts/WeekDayTrendLine.vue";
	import DepartmentQuotaMonitoring from "./components/DepartmentQuotaMonitoring.vue";
	import { reactive, ref } from "vue";
	import { Setting } from "@element-plus/icons-vue";
	import WaterMonCard from "./components/WaterMonCard.vue";
	import ProgressBar from "./components/ProgressBar.vue";
	import WaterMNFLine from "./echarts/WaterMNFLine.vue";
	import PerCapitaWaterLine from "./echarts/PerCapitaWaterLine.vue";
	const radio1 = ref("1");
	const barChartData = reactive({
		xdata: ["容城谷庄", "雄县七间房", "安新三台", "雄县张岗", "安新寨里"],
		ydata: [200, 100, 200, 200, 100],
		ydata1: [300, 200, 300, 300, 400]
	});
</script>
<style lang="scss" scoped>
	::v-deep(.page-layout__aside) {
		background-color: transparent;
	}
	::v-deep(.page-layout__main) {
		background-color: transparent;
	}

	.card-actions {
		font-weight: bold;
		font-size: 16px;
		color: rgba(0, 0, 0, 0.85);
		// 向下三角形 绿色
		&::after {
			content: "";
			display: inline-block;
			width: 0;
			height: 0;
			border-left: 5px solid transparent;
			border-right: 5px solid transparent;
			border-top: 8px solid #52c41a;
			margin-left: 8px;
		}
	}
	.tag-item {
		display: flex;
		gap: 8px;
		align-items: center;
		font-size: 14px;
		&::before {
			content: "";
			display: inline-block;
			width: 12px;
			height: 12px;
			background: #57bd94;
			border-radius: 50%;
		}
		&.warning {
			&::before {
				background: #f5a250;
			}
		}
		&.danger {
			&::before {
				background: #ed6a5e;
			}
		}
	}
</style>
