<template>
	<div class="chart-height">
		<ElForm :model="queryParams" :rules="rules" ref="queryParams">
			<ElFormItem label="处理类型：" prop="handleStatus">
				<ElSelect
					v-model="queryParams.handleStatus"
					@change="changeStatus"
					:disabled="statusType == 2 || statusType == 3"
					style="width: 100%"
					placeholder="请选择处理类型"
					clearable
				>
					<ElOption
						v-for="item in dealTypeList"
						:key="item.id"
						:label="item.dictLabel"
						:value="item.dictValue"
					></ElOption>
				</ElSelect>
			</ElFormItem>
			<ElFormItem label="人工判定：" prop="judge" v-if="queryParams.handleStatus == 1">
				<ElSelect v-model="queryParams.judge" style="width: 100%" placeholder="请选择人工判定" clearable>
					<ElOption
						v-for="item in judgmentList"
						:key="item.id"
						:label="item.dictLabel"
						:value="item.dictValue"
					></ElOption>
				</ElSelect>
			</ElFormItem>
			<ElFormItem v-if="queryParams.handleStatus == 1">
				<ElCheckbox v-model="check1">上传到算法训练平台</ElCheckbox><br />
				<ElCheckbox v-model="check2">删除人工判定为误报的记录</ElCheckbox>
			</ElFormItem>
			<ElFormItem label="原因：" prop="reason" v-if="queryParams.handleStatus != 1">
				<ElInput v-model="queryParams.reason" style="width: 100%" placeholder="请输入原因"></ElInput>
			</ElFormItem>
			<ElFormItem label="备注：" prop="remark" v-if="queryParams.handleStatus != 1">
				<ElInput v-model="queryParams.remark" style="width: 100%" placeholder="请输入备注"></ElInput>
			</ElFormItem>
		</ElForm>
	</div>
</template>

<script setup lang="ts">
import { getRedisDictList } from "@/api/common/common";
import { batchProcessing } from "@/api/alarm-center/alarmCenter";
import { useDialogForm } from "@/core/dialog/dialog-container";
import { ElMessage } from "element-plus";
import { ref } from "vue";

const props = defineProps({
	row: {
		type: Object,
		default: () => {}
	},
	ids: {
		type: String,
		default: ""
	},
	type: {
		type: Number,
		default: -1
	}
});

const queryParams = ref({
	ids: "",
	handleStatus: ""
});
const rules = ref({
	handleStatus: [{ required: true, message: "请选择", trigger: "blur" }],
	judge: [{ required: true, message: "请选择", trigger: "blur" }]
});
const saveDisabled = ref(false);
//0=未处理 1=误报 2=上报 3=忽略 4=删除
const dealTypeList = ref([
	{
		dictLabel: "误报",
		dictValue: "1"
	},
	{
		dictLabel: "上报",
		dictValue: "2"
	},
	{
		dictLabel: "忽略",
		dictValue: "3"
	},
	{
		dictLabel: "删除",
		dictValue: "4"
	}
]);
const judgmentList = ref([]);
const statusType = ref("");
const check1 = ref("");
const check2 = ref("");

const initJudgmentList = async () => {
	const judgment = await getRedisDictList({ dictType: "judge" });
	judgmentList.value = judgment.data;
};

if (props.ids) {
	queryParams.value.ids = props.ids;
	if (props.type != -1) {
		statusType.value = props.type.toString();
		queryParams.value.handleStatus = props.type.toString();
	} else {
		statusType.value = null;
		queryParams.value.handleStatus = "";
	}
}
initJudgmentList();
const changeStatus = (val) => {
	queryParams.value.handleStatus = `${val}`;
};
const { registerFormDone, formInstance } = useDialogForm();
registerFormDone(async () => {
	formInstance.value.validate((valid) => {
		if (valid) {
			setTimeout(() => {
				batchProcessing(queryParams.value).then((res) => {
					if (res.code == 0) {
						ElMessage({
							type: "success",
							message: "保存成功"
						});
					}
				});
			}, 1000);
		} else {
			return false;
		}
	});
});
</script>
<style scoped lang="scss">
::v-deep .el-dialog__body {
	border-top: 1px solid #dcdfe6;
}

.chart-height {
	// height: 600px;
}

.all-num {
	display: flex;
	height: 100px;
	border: 1px solid rgba(0, 0, 0, 0.15);
	.num-wrap {
		flex: 1;
		display: flex;
		border-right: 1px solid rgba(0, 0, 0, 0.15);
		height: 100%;
		align-items: center;

		img {
			width: 56px;
			height: 56px;
			margin-left: 24px;
			margin-right: 16px;
		}

		.num {
			font-weight: bold;
			font-size: 20px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 20px;
		}

		.info {
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
			line-height: 22px;
			margin-top: 14px;
		}
	}
}

.list-wrap {
	display: flex;
	flex-wrap: wrap;
	height: calc(100% - 80px);
	overflow: auto;
	align-content: flex-start;

	.list {
		width: 100%;
		background: #ffffff;
		box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
		border-radius: 0px 0px 0px 0px;
		border: 1px solid rgba(0, 0, 0, 0.15);
		margin-top: 16px;
		margin-right: 16px;
	}
	.header-title {
		height: 56px;
		font-weight: bold;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		display: flex;
		align-items: center;
		background: rgba(235, 239, 247, 0.5);
		border-top: 2px solid #355bad;

		img {
			width: 21px;
			height: 21px;
			margin-left: 8px;
			margin-right: 8px;
		}
	}

	.content-list {
		padding: 8px 16px;
		.item {
			height: 40px;
			display: flex;
			align-items: center;

			.name {
				color: rgba(0, 0, 0, 0.65);
				width: 72px;
			}

			.tip {
				// color: #355bad;
				margin-left: 20px;
				cursor: pointer;
			}
		}

		.meeting-time {
			margin-bottom: 12px;
			border-bottom: 1px solid rgba(0, 0, 0, 0.15);
			padding: 16px 0 24px;

			.time-name {
				font-weight: bold;
				font-size: 14px;
				color: rgba(0, 0, 0, 0.85);
				line-height: 22px;
				margin-bottom: 12px;
				text-align: center;
			}

			.time-solt-wrap {
				display: flex;
				border-top: 1px solid rgba(0, 0, 0, 0.15);
				border-right: 1px solid rgba(0, 0, 0, 0.15);
				border-bottom: 1px solid rgba(0, 0, 0, 0.15);
				margin-bottom: 4px;
			}
			.time-solt {
				flex: 1;
				height: 16px;
				background: #ffffff;
				box-shadow: 0px 0px 2px 0px rgba(32, 144, 225, 0.18);
				border-left: 1px solid rgba(0, 0, 0, 0.15);
			}

			.time-num {
				font-size: 10px;
				color: rgba(0, 0, 0, 0.65);
				line-height: 18px;
			}
		}

		.sence-wrap {
			display: flex;

			.sence-item {
				flex: 1;
				padding: 12px 0 8px 32px;
				border-right: 1px solid rgba(0, 0, 0, 0.15);
			}
		}
	}
}
</style>
