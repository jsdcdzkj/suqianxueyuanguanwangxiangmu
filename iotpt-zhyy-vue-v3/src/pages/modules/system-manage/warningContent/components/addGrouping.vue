<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-position="top">
		<ElFormItem label="分组名称：" prop="name">
			<ElInput v-model="form.name" placeholder="请输入分组名称" />
		</ElFormItem>
		<ElFormItem label="告警编号：" prop="code">
			<ElInput v-model="form.code" placeholder="请输入告警编号" />
		</ElFormItem>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, reactive } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElMessage, ElForm, ElFormItem, ElInput } from "element-plus";
	import { saveAlarmGroup } from "@/api/setting/warnInfo";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const form = reactive({
		name: "",
		code: ""
	});

	const rules = reactive({
		name: [{ required: true, message: "请输入分组名称", trigger: "blur" }],
		code: [{ required: true, message: "请输入告警编号", trigger: "blur" }]
	});

	const { registerFormDone, formInstance } = useDialogForm();

	// 初始化数据
	if (props.rowInfo.id) {
		Object.assign(form, props.rowInfo);
	}

	registerFormDone(async () => {
		try {
			await formInstance.value?.validate();
			await saveAlarmGroup(form);
			ElMessage.success("保存成功");
			return true;
		} catch (error) {
			console.error("保存失败:", error);
			ElMessage.error("保存失败，请检查输入信息");
			return false;
		}
	});
</script>
