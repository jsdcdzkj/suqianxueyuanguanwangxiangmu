<template>
	<div style="height: 700px">
		<ElSteps :active="2" align-center direction="vertical" type="circle">
			<ElStep>
				<template #title>
					<div class="timeline-item">
						<div class="timeline-icon">
							<img src="@/assets/images/alarm-center/alarm-deal.png" alt="" />
						</div>
						<div>
							<div class="title">告警已上报</div>
							<div class="desc">
								<span class="name">上报时间：</span>
								<span>{{ time1 }}</span>
							</div>
							<div class="desc">
								<span class="name">告警内容：</span>
								<span>{{ span1 }}</span>
							</div>
							<div class="desc" v-if="imageSize > 0">
								<span class="name">告警内容：</span>
								<div class="work-img-box">
									<div class="work-img-item" v-for="item in imageUrls" :key="item.id">
										<ElImage
											style="width: 80px; height: 80px"
											:src="item"
											:preview-src-list="imageUrls"
										></ElImage>
									</div>
								</div>
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
							<div class="title">任务已指派</div>
							<div class="desc">
								<span class="name">指派时间：</span>
								<span>{{ time2 }}</span>
							</div>
							<div class="desc" v-if="nowTwo">
								<span class="name">处理班组：</span>
								<span>{{ teamGroupsName }}</span>
							</div>
							<div class="desc" v-if="nowTwo">
								<span class="name">任务类型：</span>
								<span>{{ taskTypeName }}</span>
							</div>
							<div class="desc" v-if="nowTwo">
								<span class="name">紧急程度：</span>
								<span>{{ urgencyName }}</span>
							</div>
							<div class="desc" v-if="nowTwo">
								<span class="name">备注：</span>
								<span>{{ notes }}</span>
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
							<div class="title">任务已接单</div>
							<div class="desc">
								<span class="name">处理时间：</span>
								<span>{{ time3 }}</span>
							</div>
							<div class="desc" v-if="nowThree">
								<span class="name">处理人：</span>
								<span>{{ span2 }}</span>
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
							<div class="title">任务处理完成</div>
							<div class="desc">
								<span class="name">处理时间：</span>
								<span>{{ handleDate }}</span>
							</div>
							<div class="desc" v-if="nowFour">
								<span class="name">处理人：</span>
								<span>{{ handleName }}</span>
							</div>
							<div class="desc" v-if="nowFour">
								<span class="name">处理信息：</span>
								<span>{{ crunch }}</span>
							</div>
							<div class="desc" v-if="handlesSize > 0">
								<span class="name">附件：</span>
								<div class="work-img-box">
									<div class="work-img-item" v-for="item in handlesUrls" :key="item.id">
										<ElImage
											style="width: 80px; height: 80px"
											:src="item"
											:preview-src-list="handlesUrls"
										></ElImage>
									</div>
								</div>
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
import { ElTimelineItem, ElTimeline, ElStep, ElSteps, ElImage } from "element-plus";
import { getDetailsByDeviceIdNew, getHistoryPage, getWarnTypeDict, getHistoryList, getAlarmList } from "@/api/alarm-center/warnInfo";

const props = defineProps({
	id: number().def(),
	row: {
		type: Object,
		default: {}
	}
});

const step = ref(0);
const nowOne = ref(false);
const time1 = ref("");
const span1 = ref("");
const nowTwo = ref(false);
const time2 = ref("");
const teamGroupsName = ref("");
const taskTypeName = ref("");
const urgencyName = ref("");
const notes = ref("");
const nowThree = ref(false);
const time3 = ref("");
const span2 = ref("");
const nowFour = ref(false);
const handleDate = ref("");
const handleName = ref("");
const crunch = ref("");
const handlesSize = ref(0);
const imageUrls = ref([]);
const imageSize = ref(0);
const handlesUrls = ref([]);
const commonImgUrl = ref(import.meta.env.VITE_BASE_URL + "/minio/previewFile?fileName=");

// 详情页数据
if (props.id) {
	getDetailsByDeviceIdNew({ id: props.id }).then(res => {
		let workData = res;
	// 上报信息
	let sbData = workData.bean;
	if (sbData) {
		nowOne.value = true;
		step.value = 1
		time1.value = sbData.createTime;
		span1.value = sbData.notes;
		// 图片
		let fileData = props.row.fileList;

		var files = sbData.fileList;
		imageUrls.value = [];
		for (const key in files) {
			//文件数据渲染
			if (Object.hasOwnProperty.call(files, key)) {
				const element = files[key];
				imageUrls.value.push(commonImgUrl.value + element.fileUrl);
			}
		}
		imageSize.value = imageUrls.value.length;

		var handles = sbData.handles;
		handlesUrls.value = [];
		for (const key in handles) {
			//文件数据渲染
			if (Object.hasOwnProperty.call(handles, key)) {
				const element = handles[key];
				handlesUrls.value.push(commonImgUrl.value + element.fileUrl);
			}
		}
		handlesSize.value = handlesUrls.value.length;
	}

	// 指派信息
	let zpData = workData.assigns;
	if (zpData && zpData.length > 0) {
		zpData = zpData[0];

		nowOne.value = true;
		nowTwo.value = true;
		step.value = 2
		teamGroupsName.value = zpData.teamGroupsName;
		taskTypeName.value = zpData.taskTypeName;
		urgencyName.value = zpData.urgencyName;
		notes.value = zpData.notes;
		time2.value = zpData.deadline;

		if (workData.assignUser) {
			nowThree.value = true;
			step.value = 3
			time3.value = zpData.createTime;
			// 去除最后一个.
			let str = workData.assignUser;
			str = str.substring(0, str.length - 1);
			span2.value = str;
		}
	}

	// 处理信息
	let clData = workData.bean;
	if (clData && clData.handleDate) {
		nowOne.value = true;
		nowTwo.value = true;
		nowThree.value = true;
		nowFour.value = true;
		step.value = 4

		crunch.value = clData.crunch;
		handleName.value = clData.handleName;
		handleDate.value = clData.handleDate;
	}

	})
}
</script>
<style lang="scss" scoped>
.custom-icon {
	width: 16px;
	height: 16px;
	border-radius: 16px;
	background: #f8f8f8;
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
		display: flex;

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
		position: absolute;
		top: -4px;
		left: 4px;
	}

	.el-step__head.is-wait {
		color: #f8f8f8;
	}

	.el-step__head.is-process {
		color: #f8f8f8;
	}
	.el-step__line {
		background-color: rgba(0, 0, 0, 0.1);
	}
}
</style>
