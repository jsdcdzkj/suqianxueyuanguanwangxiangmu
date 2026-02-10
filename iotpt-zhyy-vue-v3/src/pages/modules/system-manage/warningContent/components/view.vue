<template>
	<ElDescriptions :column="2" border>
		<ElDescriptionsItem label="告警内容">
			{{ entityInfo.alarmContent }}
		</ElDescriptionsItem>
		<ElDescriptionsItem label="告警编号">
			{{ entityInfo.alarmCode }}
		</ElDescriptionsItem>
		<ElDescriptionsItem label="告警等级">
			{{ entityInfo.alarmLevelName }}
		</ElDescriptionsItem>
		<ElDescriptionsItem label="告警类型">
			{{ entityInfo.alarmCategoryName }}
		</ElDescriptionsItem>
		<ElDescriptionsItem label="告警分组">
			{{ entityInfo.alarmGroupLabel }}
		</ElDescriptionsItem>
		<ElDescriptionsItem label="告警种类">
			{{ entityInfo.alarmTypeName }}
		</ElDescriptionsItem>
		<ElDescriptionsItem label="归属对象">
			{{ entityInfo.deviceTypeName }}
		</ElDescriptionsItem>
		<ElDescriptionsItem label="是否启用">
			{{ entityInfo.enable == 1 ? "启用" : "不启用" }}
		</ElDescriptionsItem>
		<ElDescriptionsItem label="备注" :span="2">
			{{ entityInfo.remark }}
		</ElDescriptionsItem>
	</ElDescriptions>
</template>

<script setup lang="ts">
	import { ref } from "vue";
	import { ElDescriptions, ElDescriptionsItem } from "element-plus";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const entityInfo = ref({ ...props.rowInfo });
</script>

<style scoped lang="scss">
	:deep(.el-descriptions-item__label) {
		width: 140px;
		text-align: right !important;
	}

	:deep(.el-descriptions-item__content) {
		width: 300px;
	}
</style>
