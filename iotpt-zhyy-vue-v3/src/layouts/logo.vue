<template>
	<div class="h-72px ep-b-1px flex flex-items-center flex-justify-center m-l-16px">
		<img class="w-340px" alt="" srcset="@/assets/images/logo.png" />
		<!-- <span :class="['project-logo ', collapse ? 'size-0 m-l-0' : ' m-l-16px']">宿迁学院给水管网监测系统</span> -->
	</div>
</template>

<script lang="ts" setup>
	import { bool } from "vue-types";

	defineProps({
		collapse: bool().def(false)
	});
</script>

<style lang="scss" scoped>
	.project-logo {
		font-weight: bold;
		font-size: 22px;

		color: var(--app-logo-color);
		line-height: 22px;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;

		display: inline-block;
	}
</style>
