<template>
	<ContextConfig>
		<ElConfigProvider :locale="zhCn"> <RouterView /></ElConfigProvider>
	</ContextConfig>
</template>

<script setup lang="ts">
	import zhCn from "element-plus/es/locale/lang/zh-cn";
	import ContextConfig from "@/components/config/context-config";
</script>

<style>
	#app {
		color: var(--el-text-color-primary);
	}

	.main-container {
		height: 100vh;
	}
	.body-container {
		height: 100%;
		overflow-y: hidden;
		background-color: var(--el-body-bg);
	}

	.textEllipsis {
		text-overflow: ellipsis;
		overflow: hidden;
		white-space: nowrap;
	}
</style>
