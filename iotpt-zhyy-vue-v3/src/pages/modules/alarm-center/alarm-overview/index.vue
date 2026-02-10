<template>
	<div class="alarm-overview">
		<div class="search-box">
			<ElRadioGroupButton>
				<ElRadioButton label="1">日</ElRadioButton>
				<ElRadioButton label="2">月</ElRadioButton>
				<ElRadioButton label="3">年</ElRadioButton>
			</ElRadioGroupButton>
			<ElDatePicker
				type="daterange"
				style="width: 240px;flex:none;margin:0 12px;"
				value-format="yyyy-MM-dd"
				placeholder="选择日期"
				range-separator="至"
				start-placeholder="开始日期"
				end-placeholder="结束日期"
			/>
			<div>
				<ElButton type="primary" :icon="Search">查询</ElButton>
				<ElButton :icon="Refresh">重置</ElButton>
			</div>
		</div>
		<ElRow :gutter="12">
			<ElCol :span="24">
				<alarm-type></alarm-type>
			</ElCol>
			<ElCol :span="8"  class="card-height" style="margin-bottom:12px;">
				<alarm-type-statics></alarm-type-statics>
			</ElCol>
			<ElCol :span="16"  class="card-height" style="margin-bottom:12px;">
				<alarm-trend></alarm-trend>
			</ElCol>
			<ElCol :span="8" class="card-height">
				<alarm-safe></alarm-safe>
			</ElCol>
			<ElCol :span="8" class="card-height">
				<alarm-deal></alarm-deal>
			</ElCol>
			<ElCol :span="8" class="card-height">
				<alarm-order></alarm-order>
			</ElCol>
		</ElRow>
	</div>
</template>

<script setup lang="tsx">
import { ref } from "vue";
import {
  Refresh,
  Search,
} from '@element-plus/icons-vue'
import { ElRadio, ElRadioGroup, ElDatePicker, ElButton, ElRow, ElCol } from "element-plus";
import alarmTypeStatics from "./components/alarm-type-statics.vue";
import alarmTrend from "./components/alarm-trend.vue";
import alarmSafe from "./components/alarm-safe.vue";
import alarmDeal from "./components/alarm-deal.vue";
import alarmOrder from "./components/alarm-order.vue";
import alarmType from "./components/alarm-type.vue";

const time = ref("1");
const handleChange = (val: string) => {
	time.value = val;
};

const value = ref([]);
</script>
<style lang="scss" scoped>
.alarm-overview {
	padding: 12px;
}
.search-box {
	display: flex;
	justify-content: flex-start;
	align-items: center;
	background: #fff;
	height: 56px;
	margin-bottom:12px;
	padding:12px;
}

.card-height {
	height: calc((100vh - 400px) / 2);
}
</style>
