<template>
	<PageLayout :show-page-top="false" sidebar-width="650px" gap="12px">
		<template #pageContent>
			<div style="background-color: var(--app-main-bg)" class="flex flex-col gap-12px h-100%">
				<StructureCard title="水量消耗统计" class="flex-1 min-h-0px">
					<template #actions>
						<div class="flex gap-12px">
							<ElSelect placeholder="用量" class="w-100px" v-model="selectType">
								<el-option label="用水量" value="用水量"></el-option>
								<el-option label="用水量同比" value="用水量同比"></el-option>
								<el-option label="用水量环比" value="用水量环比"></el-option>
							</ElSelect>
							<ElSelect placeholder="小时" class="w-100px" v-model="selectTyp2">
								<el-option label="小时" value="小时"></el-option>
								<el-option label="日" value="日"></el-option>
								<el-option label="月" value="月"></el-option>
								<el-option label="年" value="年"></el-option>
							</ElSelect>
							<ElSelect placeholder="折线图" class="w-100px" v-model="selectType3">
								<el-option label="折线图" value="折线图"></el-option>
								<el-option label="柱状图" value="柱状图"></el-option>
							</ElSelect>
						</div>
					</template>
					<WaterConsumptionLine></WaterConsumptionLine>
				</StructureCard>

				<StructureCard title="水量消耗统计" class="flex-1 min-h-0px">
					<WaterContrastPie />
				</StructureCard>
			</div>
		</template>
		<template #pageLeft>
			<StructureCard title="查询条件">
				<div class="flex gap-12px h-100% overflow-hidden">
					<div class="flex flex-1 flex-col h-100% gap-12px">
						<SelectType></SelectType>
						<SelectTime></SelectTime>
						<SearchDimension class="flex-1 min-h-0px" />
					</div>
					<div class="flex flex-1 flex-col h-100% gap-12px">
						<SearchMutTime class="flex-1 min-h-0px" />
						<SearchDept class="flex-1 min-h-0px" />
					</div>
				</div>
			</StructureCard>
		</template>
	</PageLayout>
</template>

<script setup lang="tsx">
	import PageLayout from "@/layouts/PageStructure/PageLayout";
	import StructureCard from "@/components/card/StructureCard";
	import SelectTime from "./components/SelectTime.vue";
	import SelectType from "./components/SelectType.vue";
	import SearchDimension from "./components/SearchDimension.vue";
	import SearchDept from "./components/SearchDept.vue";
	import SearchMutTime from "./components/SearchMutTime.vue";
	import WaterConsumptionLine from "./echarts/WaterConsumptionLine.vue";
	import WaterContrastPie from "./echarts/WaterContrastPie.vue";
	import { ElSelect, ElOption } from "element-plus";
	import { ref } from "vue";

	const selectType = ref("用水量");
	const selectTyp2 = ref("小时");
	const selectType3 = ref("折线图");
</script>
<style></style>
