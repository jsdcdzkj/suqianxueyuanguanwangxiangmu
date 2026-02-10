<template>
	<StructureCard title="告警趋势">
		<div class="alarm-trend-box">
			<!-- 告警类型统计的图表 -->
			<AlarmTrendLine />
		</div>
	</StructureCard>
</template>

<script setup lang="tsx">
import StructureCard from "@/components/card/StructureCard";
import AlarmTrendLine from "../echarts/alarmTrendLine.vue";
</script>
<style lang="scss" scoped>
.alarm-trend-box {
	width:100%;
	height: 100%;
}
</style>