<template>
	<div style="height: 600px">
		<div class="process-wrap">
                    <div class="process-item">
                        <div class="dot" :class="{ active: missionData.title }"></div>
                        <div class="process-info">
                            <div class="process-title" :class="{ active: missionData.title }">
                                <span>{{ missionData.title }}</span>
                                <span class="tip">上报时间： {{ missionData.reportingTime }}</span>
                            </div>
                            <div>上报人：{{ missionData.userName }}</div>
                        </div>
                    </div>
                    <div class="line"></div>
                    <div class="process-item">
                        <div class="dot" :class="{ active: assionUserName }"></div>
                        <div class="process-info">
                            <div class="process-title" :class="{ active: assionUserName }">
                                <span>智慧运维指派人员</span>
                            </div>
                            <div>指派人：{{ assionUserName }}</div>
                        </div>
                    </div>
                    <div style="display: flex; width: 300px; justify-content: space-between; margin-top: 20px">
                        <div class="line1"></div>
                        <div class="line2"></div>
                    </div>
                    <div class="process-box">
                        <div class="process-left">
                            <div class="process-item">
                                <div class="dot" :class="{ active: missionData.serviceType == 1 }"></div>
                                <div class="process-info">
                                    <div class="process-title" :class="{ active: missionData.serviceType == 1 }">
                                        <span>公区服务</span>
                                    </div>
                                    <div>服务类型</div>
                                </div>
                            </div>
                            <div class="line"></div>
                            <div class="process-item">
                                <div
                                    class="dot"
                                    :class="{
                                        active: missionData.serviceType == 1 && assigns[0] && assigns[0].teamGroupsName
                                    }"
                                ></div>
                                <div class="process-info">
                                    <div
                                        class="process-title"
                                        :class="{
                                            active:
                                                missionData.serviceType == 1 && assigns[0] && assigns[0].teamGroupsName
                                        }"
                                    >
                                        <span>工单任务组</span>
                                    </div>
                                    <div>
                                        指派信息：{{
                                            missionData.serviceType == 1 && assigns[0] ? assigns[0].teamGroupsName : ""
                                        }}
                                    </div>
                                </div>
                            </div>
                            <div class="line"></div>
                            <div class="process-item">
                                <div
                                    class="dot"
                                    :class="{
                                        active:
                                            missionData.serviceType == 1 &&
                                            missionData.states == 3 &&
                                            missionData.handleName
                                    }"
                                ></div>
                                <div class="process-info">
                                    <div
                                        class="process-title"
                                        :class="{
                                            active:
                                                missionData.serviceType == 1 &&
                                                missionData.states == 3 &&
                                                missionData.handleName
                                        }"
                                    >
                                        <span>处理信息</span>
                                        <el-tag
                                            v-if="missionData.serviceType == 1 && missionData.pendingType"
                                            style="margin-left: 12px"
                                            type="warning"
                                            effect="dark"
                                        >
                                            挂 单
                                        </el-tag>
                                    </div>
                                    <div>
                                        处理人：{{
                                            missionData.serviceType == 1 && missionData.states == 3
                                                ? missionData.handleName
                                                : ""
                                        }}
                                    </div>
                                </div>
                            </div>
                            <div class="line"></div>
                            <div class="process-item">
                                <div
                                    class="dot"
                                    :class="{ active: missionData.serviceType == 1 && missionData.states == 3 }"
                                ></div>
                                <div class="process-info">
                                    <div
                                        class="process-title"
                                        :class="{ active: missionData.serviceType == 1 && missionData.states == 3 }"
                                    >
                                        <span>完成信息</span>
                                    </div>
                                    <div>
                                        结果：{{
                                            missionData.serviceType == 1 && missionData.states == 3 ? "已处理" : ""
                                        }}
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="process-right">
                            <div class="process-item">
                                <div class="dot" :class="{ active: missionData.serviceType == 2 }"></div>
                                <div class="process-info">
                                    <div class="process-title" :class="{ active: missionData.serviceType == 2 }">
                                        <span>有偿服务</span>
                                    </div>
                                    <div>服务类型</div>
                                </div>
                            </div>
                            <div class="line"></div>
                            <div class="process-item">
                                <div
                                    class="dot"
                                    :class="{
                                        active: missionData.serviceType == 2 && assigns[0] && assigns[0].teamGroupsName
                                    }"
                                ></div>
                                <div class="process-info">
                                    <div
                                        class="process-title"
                                        :class="{
                                            active:
                                                missionData.serviceType == 2 && assigns[0] && assigns[0].teamGroupsName
                                        }"
                                    >
                                        <span>工单任务组</span>
                                    </div>
                                    <div>
                                        指派信息：{{
                                            missionData.serviceType == 2 && assigns[0] ? assigns[0].teamGroupsName : ""
                                        }}
                                    </div>
                                </div>
                            </div>
                            <div class="line"></div>
                            <div class="process-item">
                                <div
                                    class="dot"
                                    :class="{
                                        active:
                                            missionData.serviceType == 2 &&
                                            missionData.consumables &&
                                            missionData.consumables.length > 0
                                    }"
                                ></div>
                                <div class="process-info">
                                    <div
                                        class="process-title"
                                        :class="{
                                            active:
                                                missionData.serviceType == 2 &&
                                                missionData.consumables &&
                                                missionData.consumables.length > 0
                                        }"
                                    >
                                        <span>提交耗材</span>
                                    </div>
                                    <div>
                                        处理人：{{
                                            missionData.serviceType == 2 &&
                                            missionData.consumables &&
                                            missionData.consumables.length > 0
                                                ? assionUserName
                                                : ""
                                        }}
                                    </div>
                                </div>
                            </div>
                            <div class="line"></div>
                            <div class="process-item">
                                <div
                                    class="dot"
                                    :class="{
                                        active:
                                            missionData.serviceType == 2 && [1, 2, 3].includes(missionData.handleStatus)
                                    }"
                                ></div>
                                <div class="process-info">
                                    <div
                                        class="process-title"
                                        :class="{
                                            active:
                                                missionData.serviceType == 2 &&
                                                [1, 2, 3].includes(missionData.handleStatus)
                                        }"
                                    >
                                        <span>发起人确认</span>
                                    </div>
                                    <div>
                                        发起人：{{
                                            missionData.serviceType == 2 && [1, 2, 3].includes(missionData.handleStatus)
                                                ? assionUserName
                                                : ""
                                        }}
                                    </div>
                                </div>
                            </div>
                            <div style="display: flex; width: 240px; justify-content: space-between; margin-top: 20px">
                                <div class="line1"></div>
                                <div class="line2"></div>
                            </div>
                            <div class="process-box">
                                <div class="process-left">
                                    <div class="process-item">
                                        <div
                                            class="dot"
                                            :class="{
                                                active: missionData.serviceType == 2 && missionData.handleStatus == 2
                                            }"
                                        ></div>
                                        <div class="process-info">
                                            <div
                                                class="process-title"
                                                :class="{
                                                    active:
                                                        missionData.serviceType == 2 && missionData.handleStatus == 2
                                                }"
                                            >
                                                <span>不同意</span>
                                            </div>
                                            <div>
                                                确认结果：{{
                                                    missionData.serviceType == 2 && missionData.handleStatus == 2
                                                        ? "不同意"
                                                        : ""
                                                }}
                                            </div>
                                        </div>
                                    </div>
                                    <div class="line"></div>
                                    <div class="process-item">
                                        <div
                                            class="dot"
                                            :class="{
                                                active: missionData.serviceType == 2 && missionData.handleStatus == 2
                                            }"
                                        ></div>
                                        <div class="process-info">
                                            <div
                                                class="process-title"
                                                :class="{
                                                    active:
                                                        missionData.serviceType == 2 && missionData.handleStatus == 2
                                                }"
                                            >
                                                <span>关闭工单</span>
                                            </div>
                                            <div>
                                                结果：{{
                                                    missionData.serviceType == 2 && missionData.handleStatus == 2
                                                        ? "已关闭"
                                                        : ""
                                                }}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="process-right">
                                    <div class="process-item">
                                        <div
                                            class="dot"
                                            :class="{
                                                active: missionData.serviceType == 2 && missionData.handleStatus == 1
                                            }"
                                        ></div>
                                        <div class="process-info">
                                            <div
                                                class="process-title"
                                                :class="{
                                                    active:
                                                        missionData.serviceType == 2 && missionData.handleStatus == 1
                                                }"
                                            >
                                                <span>同意</span>
                                            </div>
                                            <div>
                                                确认结果：{{
                                                    missionData.serviceType == 2 && missionData.handleStatus == 1
                                                        ? "同意"
                                                        : ""
                                                }}
                                            </div>
                                        </div>
                                    </div>
                                    <div class="line"></div>
                                    <div class="process-item">
                                        <div
                                            class="dot"
                                            :class="{
                                                active:
                                                    missionData.serviceType == 2 &&
                                                    missionData.states == 3 &&
                                                    missionData.handleName
                                            }"
                                        ></div>
                                        <div class="process-info">
                                            <div
                                                class="process-title"
                                                :class="{
                                                    active:
                                                        missionData.serviceType == 2 &&
                                                        missionData.states == 3 &&
                                                        missionData.handleName
                                                }"
                                            >
                                                <span>处理信息</span>
                                                <el-tag
                                                    v-if="missionData.serviceType == 2 && missionData.pendingType"
                                                    style="margin-left: 12px"
                                                    type="warning"
                                                    effect="dark"
                                                >
                                                    挂 单
                                                </el-tag>
                                            </div>
                                            <div>
                                                处理人：{{
                                                    missionData.serviceType == 2 && missionData.states == 3
                                                        ? missionData.handleName
                                                        : ""
                                                }}
                                            </div>
                                        </div>
                                    </div>
                                    <div class="line"></div>
                                    <div class="process-item">
                                        <div
                                            class="dot"
                                            :class="{ active: missionData.serviceType == 2 && missionData.states == 3 }"
                                        ></div>
                                        <div class="process-info">
                                            <div
                                                class="process-title"
                                                :class="{
                                                    active: missionData.serviceType == 2 && missionData.states == 3
                                                }"
                                            >
                                                <span>完成信息</span>
                                            </div>
                                            <div>
                                                结果：{{
                                                    missionData.serviceType == 2 && missionData.states == 3
                                                        ? "已处理"
                                                        : ""
                                                }}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
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
const missionData = ref({});
const assionUserName = ref('');
const assigns = ref([]); //指派信息
// 详情页数据
if (props.id) {
	inspectionData({missionId: props.id}).then((res) => {
		console.log('详情页数据', res)
		missionData.value = res.bean;
        assionUserName.value = missionData.value.assionUserName;
        assigns.value = res.assigns; //指派信息
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
// 流程图
.process-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 20px;
    padding-bottom: 60px;
    width: 90%;
    margin-left: -50px;

    .dot {
        width: 16px;
        height: 16px;
        border-radius: 20px;
        border: 1px solid rgba(0, 0, 0, 0.15);
        background: #fff;
        &.active {
            background: #409eff;
        }
    }
    .line {
        width: 20px;
        height: 60px;
        margin-top: 12px;
        margin-bottom: 20px;
        background: url("../../../../../assets/images/report-template/line-icon.png") no-repeat;
        background-size: contain;
    }
    .line1 {
        width: 20px;
        height: 80px;
        margin-top: 12px;
        transform: rotate(60deg);
        background: url("../../../../../assets/images/report-template/line-icon.png") no-repeat;
        background-size: contain;
    }
    .line2 {
        width: 20px;
        height: 80px;
        margin-top: 12px;
        transform: rotate(-60deg);
        background: url("../../../../../assets/images/report-template/line-icon.png") no-repeat;
        background-size: contain;
    }

    .process-item {
        // width: 200px;
        display: flex;
        position: relative;

        .process-info {
            position: absolute;
            width: 450px;
            left: 40px;

            .process-title {
                font-size: 20px;
                font-weight: 500;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .tip {
                    font-size: 14px;
                    font-weight: normal;
                    margin-left: 8px;
                }

                &.active {
                    color: #409eff;
                }
            }
        }
    }

    .process-box {
        display: flex;
        width: 100%;
    }

    .process-left,
    .process-right {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
    }
}
</style>
