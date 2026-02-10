<template>
	<PageLayout :show-page-top="false" class="p-0">
		<template #pageContent>
			<div id="gis-map">
				<BreadCrumbs theme="dark" />
				<div class="gis-map-bg"></div>
				<GisMapChangeType />
				<div
					class="gis-map-slider flex flex-col"
					:style="{
						transform: `translateX(0) scaleY(${scale}) scaleX(${scale})`,
						top: `calc(var(--app-page-toolbar-height) + ${10}px)`,
						gap: `${8}px`
					}"
				>
					<GisModuleCard title="分区用水" :height="252.852" :style="{ animationDelay: '0.1s' }">
						<template #actions>
							<ModuleTabs :tabs="moduleTabs" v-model:module-value="currentTabs" />
						</template>
						<div class="flex flex-col gap-9px h-100%">
							<WaterLine class="flex-1 min-h-0" />
							<div class="flex-1 flex gap-9px min-h-0" style="background: rgba(255, 255, 255, 0.05)">
								<SchoolAreaSort :items="items" />
								<SchoolAreaSortBar v-bind="barChart.data" />
							</div>
						</div>
					</GisModuleCard>
					<GisModuleCard title="设备统计" :height="174.85" :style="{ animationDelay: '0.2s' }">
						<div class="flex h-100% gap-9px">
							<DeviceNum />
							<DeviceWaterNum />
							<DeviceWaterNum />
						</div>
					</GisModuleCard>
					<GisModuleCard title="区域漏水排行" :height="159.85" :style="{ animationDelay: '0.3s' }">
						<template #actions>
							<ModuleTabs :tabs="moduleTabs2" v-model:module-value="currentTabs" />
						</template>
						<ProgressGroup />
					</GisModuleCard>
					<GisModuleCard title="漏水告警记录" :height="178.85" :style="{ animationDelay: '0.4s' }">
						<WaterAlertTable />
					</GisModuleCard>
					<GisModuleCard title="漏水区域分布" :height="164.85" :style="{ animationDelay: '0.5s' }">
						<WaterPictorialBar />
					</GisModuleCard>
				</div>
			</div>
		</template>
	</PageLayout>
</template>

<script setup lang="tsx">
	import { ref, computed, onMounted, onUnmounted, reactive } from "vue";
	import GisModuleCard from "./components/GisModuleCard.vue";
	import ModuleTabs from "./components/ModuleTabs.vue";
	import PageLayout from "@/layouts/PageStructure/PageLayout";
	import BreadCrumbs from "@/layouts/BreadCrumbs.vue";
	import WaterLine from "./components/WaterLine.vue";
	import SchoolAreaSort from "./components/SchoolAreaSort.vue";
	import SchoolAreaSortBar from "./components/SchoolAreaSortBar.vue";
	import DeviceNum from "./components/DeviceNum.vue";
	import DeviceWaterNum from "./components/DeviceWaterNum.vue";
	import ProgressGroup from "./components/ProgressGroup.vue";
	import WaterAlertTable from "./components/WaterAlertTable.vue";
	import WaterPictorialBar from "./components/WaterPictorialBar.vue";
	import GisMapChangeType from "./components/GisMapChangeType.vue";

	const barChart = reactive({
		data: {
			xdata: ["学校1", "学校2", "学校3", "学校4", "学校5", "学校6", "学校7", "学校8"],
			ydata: [100, 200, 300, 400, 500, 300, 400, 500],
			ydata1: [100, 200, 300, 400, 500, 300, 400, 500]
		}
	});

	const items = ref([
		{ name: "学校1", value: "school1" },
		{ name: "学校2", value: "school2" },
		{ name: "学校3", value: "school3" },
		{ name: "学校4", value: "school4" },
		{ name: "学校5", value: "school5" }
	]);

	// 当前缩放比例
	const scale = ref(1);

	// 计算缩放比例
	const updateScale = () => {
		const height = window.innerHeight - 50 - 44 - 20;

		const scaleY = height / 966;

		scale.value = scaleY;
	};

	// 标签页数据
	const moduleTabs = ref([
		{ label: "日", value: "monitor" },
		{ label: "月", value: "history" },
		{ label: "年", value: "report" }
	]);

	// 标签页数据
	const moduleTabs2 = ref([{ label: "查看全部", value: "monitor" }]);

	// 当前选中的标签页
	const currentTabs = ref("monitor");

	// 组件挂载时初始化并监听窗口大小变化
	onMounted(() => {
		updateScale();
		window.addEventListener("resize", updateScale);
	});

	// 组件卸载时移除监听
	onUnmounted(() => {
		window.removeEventListener("resize", updateScale);
	});
</script>

<style lang="scss" scoped>
	// 添加滑入动画
	@keyframes slideIn {
		from {
			transform: translateX(100%);
			opacity: 0;
		}
		to {
			transform: translateX(0);
			opacity: 1;
		}
	}
	#gis-map {
		width: 100%;
		height: 100%;
		background-image: url(/gis-bg.jpg);
		background-repeat: no-repeat;
		background-size: 100% 100%;
		position: relative;
		overflow: hidden;
	}

	.gis-map-slider {
		position: absolute;
		right: 20px;

		z-index: 199;
		transition: transform 0.3s ease; // 添加过渡效果使缩放更平滑

		animation: slideIn 0.5s ease-out;
		transform: translateX(0);
		gap: 8px;
		transform-origin: top right;
	}

	.gis-map-bg {
		width: 691px;
		height: 100%;
		background: linear-gradient(270deg, #112f6e 0%, rgba(52, 91, 173, 0) 100%);
		position: absolute;
		z-index: 99;
		right: 0;
		top: 0;
	}
</style>
