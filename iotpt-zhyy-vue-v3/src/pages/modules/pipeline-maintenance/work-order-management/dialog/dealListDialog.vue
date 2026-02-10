<template>
	<div style="height: 600px">
		<ElSteps :active="2" align-center direction="vertical" type="circle">
			<ElStep>
				<template #title>
					<div class="timeline-item">
						<div class="timeline-icon">
							<img src="@/assets/images/alarm-center/alarm-deal.png" alt="" />
						</div>
						<div>
							<div class="title">{{ activities[0].title }}</div>
							<div class="desc">
								<span class="name">上报时间：</span>
								<span>{{ activities[0].description }}</span>
							</div>
							<div class="desc">
								<span class="name">告警内容：</span>
								<span>{{ activities[0].description }}</span>
							</div>
						</div>
					</div>
				</template>
			</ElStep>
			<ElStep>
				<template #title>
					<div class="timeline-item">
						<div class="timeline-icon">
							<img src="@/assets/images/alarm-center/alarm-deal.png" alt="" />
						</div>
						<div>
							<div class="title">{{ activities[1].title }}</div>
							<div class="desc">
								<span class="name">指派时间：</span>
								<span>{{ activities[1].description }}</span>
							</div>
							<div class="desc">
								<span class="name">指派人员：</span>
								<span>{{ activities[1].description }}</span>
							</div>
						</div>
					</div>
				</template>
			</ElStep>

			<ElStep>
				<template #title>
					<div class="timeline-item">
						<div class="timeline-icon">
							<img src="@/assets/images/alarm-center/alarm-deal.png" alt="" />
						</div>
						<div>
							<div class="title">{{ activities[2].title }}</div>
							<div class="desc">
								<span class="name">指派时间：</span>
								<span>{{ activities[2].description }}</span>
							</div>
							<div class="desc">
								<span class="name">处理班组：</span>
								<span>{{ activities[2].description }}</span>
							</div>
						</div>
					</div>
				</template>
			</ElStep>
			<ElStep>
				<template #title>
					<div class="timeline-item">
						<div class="timeline-icon">
							<img src="@/assets/images/alarm-center/alarm-deal.png" alt="" />
						</div>
						<div>
							<div class="title">{{ activities[3].title }}</div>
							<div class="desc">
								<span class="name">指派时间：</span>
								<span>{{ activities[3].description }}</span>
							</div>
							<div class="desc">
								<span class="name">处理人员：</span>
								<span>{{ activities[3].description }}</span>
							</div>
							<div class="desc">
								<span class="name">附件：</span>
								<span>{{ activities[3].description }}</span>
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
		timestamp: "2023-01-01 10:00:00",
		icon: "warning-report.png",
		title: "告警已上报",
		description: "告警已上报，处置人：张三"
	},
	{
		timestamp: "2023-01-02 12:00:00",
		icon: "warning-report.png",
		title: "任务已指派",
		description: "告警已上报，处置人：张三"
	},
	{
		timestamp: "2023-01-02 12:00:00",
		icon: "warning-report.png",
		title: "任务已指派",
		description: "告警已上报，处置人：张三"
	},
	{
		timestamp: "2023-01-02 12:00:00",
		icon: "warning-report.png",
		title: "任务已指派",
		description: "告警已上报，处置人：张三"
	}
]);
const detailInfo = ref({});
// 详情页数据
if (props.id) {
	// getEntityOperation(props.id).then((res) => {
	// 	detailInfo.value = res
	// });
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

		img {
			width: 25px;
			height: 25px;
		}
	}

	.title {
		font-size: 16px;
		color: rgba(0, 0, 0, 0.85);
		line-height: 24px;
	}

	.desc {
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		line-height: 22px;
		margin-top: 4px;

		.name {
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
			line-height: 22px;
			width: 70px;
			display: inline-block;
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
