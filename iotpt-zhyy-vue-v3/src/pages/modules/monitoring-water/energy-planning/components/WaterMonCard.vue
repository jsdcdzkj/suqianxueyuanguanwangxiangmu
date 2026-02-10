<template>
	<div class="water-consumption-card">
		<div class="card-left">
			<Icon :name="name" moduleName="enery-planning" iconSize="81px" />
		</div>
		<div class="card-right">
			<div class="card-title">{{ title }}</div>
			<div class="card-description">{{ description }}</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { Icon } from "@/components/icon/icon";

	const props = defineProps({
		title: {
			type: String,
			default: "当月用水实际能耗比管控值"
		},
		description: {
			type: String,
			default: "本月用水量 1200m³ / 管控值 1500m³"
		},
		name: {
			type: String,
			default: "water-1.png" // 请替换为实际图片路径
		}
	});
</script>

<style lang="scss" scoped>
	.water-consumption-card {
		height: 100%;
		border: 1px solid rgba(0, 0, 0, 0.06);
		background: #fcfdff;
		display: flex;
		align-items: center;
		padding: 12px;
		box-sizing: border-box;
		flex: 1;

		.card-left {
			flex-shrink: 0;
			width: 81px;
			height: 100%;

			img {
				width: 100%;
				height: 100%;
				object-fit: contain;
			}
		}

		.card-right {
			flex: 1;
			display: flex;
			flex-direction: column;
			justify-content: center;

			height: 100%;
			padding-left: 12px;

			.card-title {
				font-weight: bold;
				font-size: 28px;
				color: rgba(0, 0, 0, 0.85);
				margin-bottom: 8px;
				text-align: left;
			}

			.card-description {
				font-weight: normal;
				font-size: 14px;
				color: rgba(0, 0, 0, 0.65);
			}
		}
	}
</style>
