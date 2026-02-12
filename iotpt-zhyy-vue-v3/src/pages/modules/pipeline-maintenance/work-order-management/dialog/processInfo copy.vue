<template>
	<div style="height: 600px">
		<ElSteps :active="2" align-center direction="vertical" type="circle">
			<ElStep v-for="(item, index) in activities" :key="index">
				<template #title>
					<div class="timeline-item flex">
						<div class="timeline-icon">
							<i :class="['icon', item.icon]"></i>
						</div>
						<div class="flex-1">
							<div class="title flex justify-between align-center">
								<div class="title-label">{{ item.title }}</div>
								<div class="title-time">{{ item.timeLabel }}</div>
							</div>
							<div class="desc  flex justify-between align-center">
								<span class="label">{{ item.description }}</span>
								<span class="value">{{ item.timestamp }}</span>
							</div>
						</div>
					</div>
				</template>
			</ElStep>
		</ElSteps>
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import { inspectionData } from "@/api/pipeline-maintenance/work-order";
import { ElTimelineItem, ElTimeline, ElStep, ElSteps } from "element-plus";

const props = defineProps({
	id: number().def(),
	projectId: {
		type: Number,
		default: 0
	},
	row: {
		type: Object,
		default: {}
	}
});
const activities = ref([
	{
		timestamp: "",
		timeLabel: "上报时间",
		icon: "ri-id-card-fill",
		title: "",
		description: "上报人"
	},
	{
		timestamp: "2023-01-01 10:00:00",
		timeLabel: "指派时间",
		icon: "ri-user-2-fill",
		title: "智慧运维指派人员",
		description: "指派人"
	},
	{
		timestamp: "2023-01-01 10:00:00",
		timeLabel: "上报时间",
		icon: "ri-firebase-fill",
		title: "公共服务",
		description: "服务类型"
	},
	{
		timestamp: "2023-01-01 10:00:00",
		timeLabel: "指派时间",
		icon: "ri-survey-fill",
		title: "工单任务组",
		description: "指派信息"
	},
	{
		timestamp: "2023-01-01 10:00:00",
		timeLabel: "处理时间",
		icon: "ri-user-5-fill",
		title: "处理信息",
		description: "处理人"
	},
	{
		timestamp: "2023-01-01 10:00:00",
		timeLabel: "完成时间",
		icon: "ri-file-list-3-fill",
		title: "完成信息",
		description: "结果"
	}
]);
const detailInfo = ref({});
// 详情页数据
if (props.id) {
	inspectionData({missionId: props.id, isHandle: 1}).then((res) => {
		console.log('详情页数据', res)
		detailInfo.value = res
	})
}
</script>
<style lang="scss" scoped>
.custom-icon {
	width: 16px;
	height: 16px;
	border-radius: 16px;
	background: #F8F8F8;
}
.timeline-item {
	box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
	border-radius: 0px 0px 0px 0px;
	border: 1px solid rgba(0, 0, 0, 0.06);
	padding: 12px;
	display: flex;

	.timeline-icon {
		width: 48px;
		height: 48px;
		background: #e7f0fa;
		border-radius: 8px 8px 8px 8px;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 12px;

		.icon {
			font-size:30px;
			color: #345BAD;
		}
	}

	.title {
		font-weight: normal;
		.title-label {
			font-size: 16px;
			color: rgba(0,0,0,0.85);
			line-height: 24px;
		}
		.title-time {
			font-size: 14px;
			color: rgba(0,0,0,0.65);
			line-height: 22px;
		}
	}

	.desc {
		font-weight: normal;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		line-height: 22px;
		margin-top: 4px;

		.label {
			font-size: 14px;
			color: rgba(0,0,0,0.65);
			line-height: 22px;
		}
		.value {
			font-size: 14px;
			color: rgba(0,0,0,0.85);
			line-height: 22px;
		}
	}
}
::v-deep {
	.el-step__icon.is-text {
		background: currentColor;
	}

	.el-step__icon {
		width: 16px;
		height: 16px;
		position:absolute;
		top:-4px;
		left:4px;
	}

	.el-step__head.is-wait {
		color: #f8f8f8;
	}

	.el-step__head.is-process {
		color: #f8f8f8;
	}
	.el-step__line {
		background-color:rgba(0, 0, 0, 0.1);
	}
}
</style>
