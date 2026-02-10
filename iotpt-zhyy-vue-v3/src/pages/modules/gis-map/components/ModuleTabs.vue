<template>
	<div class="module-tabs">
		<div
			class="module-tabs-item"
			v-for="(item, index) in tabs"
			@click="handleClickTabs(item)"
			:key="index"
			:class="{ active: moduleValue === item[valueKey] }"
		>
			{{ item[bindkey] }}
		</div>
	</div>
</template>

<script lang="ts" setup>
	import { array, string } from "vue-types";

	const props = defineProps({
		tabs: array<any>().def([]),
		moduleValue: string().def(""),
		bindkey: string().def("label"),
		valueKey: string().def("value")
	});

	const emit = defineEmits(["update:moduleValue"]);

	const handleClickTabs = (item: any) => {
		emit("update:moduleValue", item[props.valueKey]);
	};
</script>

<style lang="scss" scoped>
	.module-tabs {
		display: flex;
		justify-content: space-around;
		align-items: center;
		margin-bottom: 10px;

		.module-tabs-item {
			width: 100%;
			height: 100%;
			display: flex;
			justify-content: center;
			align-items: center;
			font-size: 12px;
			font-weight: 600;
			color: #478edd;
			cursor: pointer;
			background-image: url("/icon/gis-map/tab-item.png");
			//width: 46px;
			padding: 0 16px;
			height: 20px;
			background-size: 100% 100%;

			&.active {
				color: #fff;
				background-image: url("/icon/gis-map/tab-select-item.png");
				background-size: 100% 100%;
			}
		}
	}
</style>
