<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-position="top">
		<ElRow :gutter="16">
			<ElCol :span="12">
				<ElFormItem label="告警内容：" prop="alarmContent">
					<ElInput v-model="form.alarmContent" placeholder="请输入告警内容" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="告警编号：" prop="alarmCode">
					<ElInput v-model="form.alarmCode" placeholder="请输入编号" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="告警等级：" prop="alarmLevel">
					<ElSelect v-model="form.alarmLevel" placeholder="告警等级" clearable>
						<ElOption
							v-for="item in warnLevelOptions"
							:key="item.id"
							:label="item.dictLabel"
							:value="item.dictValue"
						/>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="告警类型：" prop="alarmCategory">
					<ElSelect v-model="form.alarmCategory" placeholder="告警类型" clearable>
						<ElOption v-for="item in warnLevelList" :key="item.id" :label="item.name" :value="item.id" />
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="告警分组：" prop="alarmGroup">
					<ElSelect v-model="form.alarmGroup" placeholder="告警分组" clearable>
						<ElOption v-for="item in warnGroupList" :key="item.id" :label="item.name" :value="item.id" />
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="归属对象：" prop="deviceType">
					<ElSelect
						v-model="form.deviceType"
						placeholder="归属对象"
						clearable
						@change="handleDeviceTypeChange"
					>
						<ElOption
							v-for="item in deviceTypeList"
							:key="item.id"
							:label="item.deviceTypeName"
							:value="item.id"
						/>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="备注：" prop="remark">
					<ElInput type="textarea" v-model="form.remark" placeholder="请输入备注" :rows="3" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="是否启用：" prop="enable">
					<ElSwitch
						v-model="form.enable"
						:active-value="1"
						:inactive-value="0"
						active-color="#409EFF"
						inactive-color="#C0CCDA"
					/>
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, reactive } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElMessage, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElSwitch, ElRow, ElCol } from "element-plus";
	import { saveAlarmContent } from "@/api/setting/warnInfo";

	const props = defineProps({
		warnLevelOptions: {
			type: Array,
			default: () => []
		},
		warnLevelList: {
			type: Array,
			default: () => []
		},
		warnGroupList: {
			type: Array,
			default: () => []
		},
		deviceTypeList: {
			type: Array,
			default: () => []
		},
		alarmTypeList: {
			type: Array,
			default: () => []
		},
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const form = reactive({
		alarmContent: "",
		alarmCode: "",
		alarmLevel: "",
		alarmCategory: "",
		alarmGroup: "",
		deviceType: "",
		remark: "",
		enable: 1,
		deviceTypeName: ""
	});

	const rules = reactive({
		alarmContent: [{ required: true, message: "请输入告警内容", trigger: "blur" }],
		alarmCode: [{ required: true, message: "请输入告警编号", trigger: "blur" }],
		alarmLevel: [{ required: true, message: "请选择告警等级", trigger: "change" }],
		alarmCategory: [{ required: true, message: "请选择告警类型", trigger: "change" }],
		alarmGroup: [{ required: true, message: "请选择告警分组", trigger: "change" }],
		deviceType: [{ required: true, message: "请选择归属对象", trigger: "change" }]
	});

	const handleDeviceTypeChange = (value: string) => {
		const selectedItem = props.deviceTypeList.find((item: any) => item.id === value);
		if (selectedItem) {
			form.deviceTypeName = selectedItem.deviceTypeName;
		} else {
			form.deviceTypeName = "";
		}
	};

	const { registerFormDone, formInstance } = useDialogForm();

	// 初始化数据
	if (props.rowInfo.id) {
		const newRow = { ...props.rowInfo };
		if (typeof newRow.alarmLevel === "number") {
			newRow.alarmLevel = String(newRow.alarmLevel);
		}
		if (typeof newRow.alarmType === "number") {
			newRow.alarmType = String(newRow.alarmType);
		}
		Object.assign(form, newRow);
	}

	registerFormDone(async () => {
		try {
			await formInstance.value?.validate();
			const categoryItem = props.warnLevelList.find((item: any) => item.id === form.alarmCategory);
			if (categoryItem) {
				form.alarmType = categoryItem.alarmType;
			}
			await saveAlarmContent(form);
			ElMessage.success("保存成功");
			return true;
		} catch (error) {
			console.error("保存失败:", error);
			ElMessage.error("保存失败，请检查输入信息");
			return false;
		}
	});
</script>
