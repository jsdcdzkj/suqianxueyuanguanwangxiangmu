<template>
	<StructureCard title="告警类型统计">
		<div class="alarm-type-statics">
			<!-- 告警类型统计的图表 -->
			<AlarmTypeStatics ref="alarmTypeStaticsRef" />
		</div>
	</StructureCard>
</template>

<script setup lang="tsx">
import StructureCard from "@/components/card/StructureCard";
import AlarmTypeStatics from "../echarts/alarmTypeStatics.vue";

</script>
<style lang="scss" scoped>
.alarm-type-statics {
	width: 100%;
	height: 100%;
}
</style>
