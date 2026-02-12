<template>
	<div style="padding: 0 20px 80px">
		<div class="video-wrap">
			<ElImage
				:src="commonImgUrl + detailInfo.alarmImg"
				v-if="detailInfo.alarmImg"
				fit="contain"
				style="width: 100%; height: 400px"
				:previewSrcList="[commonImgUrl + detailInfo.alarmImg]"
			></ElImage>
		</div>
		<div>
			<ElForm
				class="demo-form-inline"
				:model="queryForm"
				ref="formInstance"
				size="small"
				:rules="rules"
				label-position="top"
			>
				<ElFormItem label="人工判定：" prop="judge">
					<ElSelect v-model="queryForm.judge" style="width: 100%" placeholder="请选择人工判定" clearable>
						<ElOption
							v-for="item in judgmentList"
							:key="item.id"
							:label="item.dictLabel"
							:value="item.dictValue"
						></ElOption>
					</ElSelect>
				</ElFormItem>
				<ElFormItem>
					<ElCheckbox v-model="check1">上传到算法训练平台</ElCheckbox>
				</ElFormItem>
				<ElFormItem>
					<ElCheckbox v-model="check2">删除人工判定为误报的记录</ElCheckbox>
				</ElFormItem>
			</ElForm>
		</div>
	</div>
</template>

<script setup lang="ts">
import { batchProcessing } from "@/api/alarm-center/alarmCenter";
import { getRedisDictList } from "@/api/common/common";
import { useDialogForm } from "@/core/dialog/dialog-container";
import { ElForm, ElFormItem, ElSelect, ElOption, ElCheckbox, ElMessage } from "element-plus";
import { ref } from "vue";

const props = defineProps({
	row: {
		type: Object,
		default: () => {}
	}
});
const queryForm = ref({
	judge: ""
});
const warnLevelList = ref([]);
const check1 = ref("");
const check2 = ref("");
const judgmentList = ref([]);
const detailInfo = ref({});
const rules = ref({
	judge: [{ required: true, message: "请选择", trigger: "blur" }]
});
if (props.row) {
	detailInfo.value = props.row;
}

const init = async () => {
	const judgment = await getRedisDictList({ dictType: "judge" });
	judgmentList.value = judgment;
};
init();
const { registerFormDone, formInstance } = useDialogForm();
registerFormDone(async () => {
	formInstance.value.validate((valid) => {
		if (valid) {
			setTimeout(() => {
				console.log("44444444444444444444this.queryForm", queryForm.value);
				batchProcessing({ ...queryForm.value, ids: detailInfo.value.id, handleStatus: "1" }).then((res) => {
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
::v-deep {
	.el-drawer__header {
		padding: 10px !important;
		margin-bottom: 14px;
		border-bottom: 1px solid #d1d9e1;

		.drawer-title {
			font-size: 16px;
			line-height: 16px;
			color: rgb(40, 53, 95);
			display: flex;
			align-items: center;
			position: relative;
			// &::before {
			//     content: "";
			//     position: absolute;
			//     left: -10px;
			//     width: 4px;
			//     height: 45px;
			//     background: #537cdb;
			// }
			b {
				display: block;
				font-size: 16px;
				line-height: 16px;
				margin-top: 2px;
				margin-right: 4px;
				font-weight: normal;
				margin-left: 8px;
			}
		}
	}

	.footer-btn {
		padding: 12px 20px;
		border-top: 1px solid #efefef;
		position: absolute;
		bottom: 0;
		text-align: right;
		width: 100%;
		background-color: #fff;
		z-index: 9;
	}
	.my-content {
		width: 380px;
	}
	.el-descriptions-item__label {
		text-align: right !important;
	}
}
.video-wrap {
	width: 100%;
	height: 400px;
	border: 1px solid #333;
}
</style>
