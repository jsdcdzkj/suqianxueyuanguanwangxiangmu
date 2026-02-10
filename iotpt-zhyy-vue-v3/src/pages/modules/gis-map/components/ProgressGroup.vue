<template>
	<div class="progress-group">
		<ProgressItem v-for="(item, index) in progress" v-bind="item" :key="index" />
	</div>
</template>

<script setup lang="ts">
	import { ref } from "vue";
	import ProgressItem from "./ProgressItem.vue";
	const progress = ref([
		{ label: "任务1", percentage: 90, startColor: "#407FF4", endColor: "#67B3F6" },
		{ label: "任务2", percentage: 80, startColor: "#F2CA6B", endColor: "#F2CA6B" },
		{ label: "任务3", percentage: 70, startColor: "#9ECA7F", endColor: "#9ECA7F" },
		{ label: "任务4", percentage: 60, startColor: "#9ECA7F", endColor: "#9ECA7F" },
		{ label: "任务5", percentage: 50, startColor: "#9ECA7F", endColor: "#9ECA7F" }
	]);
</script>

<style scoped></style>
