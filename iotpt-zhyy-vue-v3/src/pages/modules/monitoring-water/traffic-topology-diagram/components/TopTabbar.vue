<template>
	<div class="tab-bar">
		<div
			v-for="(item, index) in tabs"
			:key="item.value"
			class="tab-item"
			:class="{ active: activeTab === item.value }"
			@click="handleTabClick(item.value)"
		>
			<div v-if="item.value !== 'all'" class="status-dot" :style="{ backgroundColor: item.color }"></div>
			<span>{{ item.label }}</span>
		</div>
	</div>
</template>

<script setup>
	import { ref } from "vue";

	const activeTab = ref("all");

	const tabs = [
		{ label: "全部(999)", value: "all" },
		{ label: "流量正常(111)", value: "normal", color: "#57BD94" },
		{ label: "用水正常(332)", value: "water-normal", color: "#5C90F7" },
		{ label: "告警(344)", value: "alarm", color: "#ED777A" },
		{ label: "未知(53)", value: "unknown", color: "#F09054" }
	];

	const handleTabClick = (value) => {
		activeTab.value = value;
	};
</script>

<style scoped>
	.tab-bar {
		display: flex;
		align-items: center;

		background: #fff;
		flex-shrink: 0;
		height: 48px;
		border-bottom: 1px solid rgba(0, 0, 0, 0.06);
	}

	.tab-item {
		display: flex;
		align-items: center;
		padding: 8px 16px;
		cursor: pointer;
		min-width: 152px;
		height: 100%;
		position: relative;
		display: flex;
		justify-content: center;
		font-size: 16px;
		color: rgba(0, 0, 0, 0.85);
	}

	.tab-item:not(:last-child)::before {
		content: "";
		position: absolute;
		right: 0;
		top: 50%;
		transform: translateY(-50%);
		width: 1px;
		height: 100%;
		background: rgba(0, 0, 0, 0.06);
	}

	.tab-item.active {
		background: rgba(52, 91, 173, 0.1);
		border-radius: 0px;
		color: #345bad;
		font-weight: bold;
		font-size: 16px;
		&::after {
			width: 100%;
			height: 3px;
			background-color: rgba(52, 91, 173, 1);
			content: "";
			display: inline-block;
			position: absolute;
			left: 0;
			bottom: 0;
		}
	}

	.status-dot {
		width: 12px;
		height: 12px;
		border-radius: 50%;
		margin-right: 8px;
	}
</style>
