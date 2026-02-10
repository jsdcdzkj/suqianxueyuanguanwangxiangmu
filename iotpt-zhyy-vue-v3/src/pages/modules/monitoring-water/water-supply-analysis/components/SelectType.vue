<template>
	<SupplyCard title="选择类型：">
		<el-radio-group v-model="radio" class="w-100%">
			<el-radio-button label="1" class="flex-1">多时间</el-radio-button>
			<el-radio-button label="2" class="flex-1">多对象</el-radio-button>
		</el-radio-group>
	</SupplyCard>
</template>

<script setup lang="ts">
	import SupplyCard from "./SupplyCard.vue";
	import { ref } from "vue";
	const radio = ref("1");
</script>

<style lang="scss" scoped>
	::v-deep(.el-radio-button__inner) {
		width: 100%;
	}
</style>
