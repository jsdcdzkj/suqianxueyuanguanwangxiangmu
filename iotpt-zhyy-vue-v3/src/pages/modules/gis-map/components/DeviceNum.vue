<template>
	<div class="device-num" ref="deviceNumRef" :style="{ width: `${width}px` }">
		<span class="label">设备总数</span>
		<span class="value">100</span>
		<span class="unit">(台)</span>
	</div>
</template>

<script lang="ts" setup>
	import { onMounted, ref } from "vue";

	const deviceNumRef = ref<HTMLDivElement>();
	const width = ref(130);

	// onMounted(() => {
	// 	if (deviceNumRef.value) {
	// 		const { height } = deviceNumRef.value.getBoundingClientRect();
	// 		width.value = height;
	// 	}
	// });
</script>

<style lang="scss" scoped>
	.device-num {
		height: 120px;
		background: url("/icon/gis-map/rect-bg.png") no-repeat;
		background-size: 100% 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;

		flex-shrink: 0;
		.label {
			display: block;
			font-size: 12px;
			color: #fff;

			font-weight: bold;
			font-size: 13px;
			color: #daeafd;
		}
		.value {
			font-family: PangMenZhengDao, PangMenZhengDao;
			display: block;
			font-size: 32px;
			color: #fff;
			text-shadow: 0px 2px 2px rgba(0, 0, 0, 0.25);
			font-weight: bold;
			background: linear-gradient(180deg, #ffffff 0%, #5099ff 100%);
			-webkit-background-clip: text;
			background-clip: text;
			-webkit-text-fill-color: transparent;
			color: transparent;
		}
		.unit {
			display: block;
			font-size: 12px;
			color: #fff;
			font-weight: bold;
			font-size: 14px;
			color: rgba(255, 255, 255, 0.85);
		}
	}
</style>
