<template>
	<div class="progress-item">
		<div class="progress-label">{{ label }}</div>
		<div class="progress-bar-container">
			<div class="progress-bar" :style="progressBarStyle"></div>
		</div>
		<div class="progress-value">{{ percentage }}%</div>
	</div>
</template>

<script setup lang="ts">
	import { computed } from "vue";

	interface Props {
		label?: string;
		percentage?: number;
		startColor?: string;
		endColor?: string;
	}

	const props = withDefaults(defineProps<Props>(), {
		label: "",
		percentage: 0,
		startColor: "#407ff4",
		endColor: "#67b3f6"
	});

	const progressBarStyle = computed(() => ({
		width: `${props.percentage}%`,
		background: `linear-gradient(90deg, ${props.startColor} 0%, ${props.endColor} 100%)`
	}));
</script>

<style scoped>
	.progress-item {
		display: flex;
		align-items: center;
		width: 100%;
	}

	.progress-label {
		margin-right: 12px;
		font-size: 14px;
		color: #fff;
		font-family:
			Alibaba PuHuiTi 3,
			Alibaba PuHuiTi 30;
		font-weight: normal;
		font-size: 12px;
		color: rgba(255, 255, 255, 0.85);
		line-height: 20px;
		width: 40px;
	}

	.progress-bar-container {
		flex: 1;
		height: 8px;
		position: relative;
		overflow: hidden;
	}

	.progress-bar {
		height: 100%;
		border: 1px solid rgba(255, 255, 255, 0.2);
		position: relative;
		clip-path: polygon(0 0, 100% 0, calc(100% - 10px) 100%, 0 100%);
		transition: width 0.3s ease;
	}

	.progress-value {
		font-family: PangMenZhengDao, PangMenZhengDao;
		font-weight: 400;
		font-size: 12px;
		color: rgba(255, 255, 255, 0.85);
		line-height: 20px;
	}
</style>
