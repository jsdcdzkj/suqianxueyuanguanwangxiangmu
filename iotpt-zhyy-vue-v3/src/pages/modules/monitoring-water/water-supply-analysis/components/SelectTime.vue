<template>
	<SupplyCard title="选择类型：">
		<div class="flex flex-col gap-12px">
			<el-radio-group v-model="radio" class="w-100%">
				<el-radio-button class="flex-1" label="1">日</el-radio-button>
				<el-radio-button class="flex-1" label="4">周</el-radio-button>
				<el-radio-button class="flex-1" label="2">月</el-radio-button>
				<el-radio-button class="flex-1" label="3">年</el-radio-button>
				<el-radio-button class="flex-1" label="5">任意时间</el-radio-button>
			</el-radio-group>
			<el-button :icon="Plus">添加时间</el-button>
		</div>
	</SupplyCard>
</template>

<script setup lang="ts">
	import { Plus } from "@element-plus/icons-vue";
	import SupplyCard from "./SupplyCard.vue";
	import { ref } from "vue";
	const radio = ref("1");
</script>

<style lang="scss" scoped>
	::v-deep(.el-radio-button__inner) {
		width: 100%;
	}
</style>
