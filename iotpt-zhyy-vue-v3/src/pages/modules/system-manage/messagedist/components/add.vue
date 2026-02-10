<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="100px" label-position="top">
		<ElRow :gutter="20">
			<ElCol :span="12">
				<ElFormItem label="标题" prop="title">
					<ElInput v-model="form.title" placeholder="请输入标题" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="类型" prop="type">
					<ElSelect v-model="form.type" placeholder="请选择类型">
						<ElOption
							v-for="item in typeList"
							:key="item.dictValue"
							:label="item.dictLabel"
							:value="item.dictValue"
						/>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="关键字" prop="keyword">
					<ElInput v-model="form.keyword" placeholder="请输入关键字" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="模板Code" prop="templateCode">
					<ElInput v-model="form.templateCode" placeholder="请输入模板Code" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="跳转类型" prop="jumpType">
					<ElSelect v-model="form.jumpType" placeholder="请选择跳转类型">
						<ElOption label="navigate" :value="1" />
						<ElOption label="switch" :value="2" />
						<ElOption label="relaunch" :value="3" />
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="跳转链接" prop="link">
					<ElInput v-model="form.link" placeholder="请输入跳转链接" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="内容" prop="content">
					<ElInput type="textarea" v-model="form.content" :rows="4" placeholder="请输入内容" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="备注" prop="remark">
					<ElInput type="textarea" v-model="form.remark" :rows="3" placeholder="请输入备注" />
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, reactive } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElRow, ElCol, ElMessage } from "element-plus";
	import { save } from "@/api/setting/messagedist";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		typeList: {
			type: Array,
			default: () => []
		},
		viewStatus: {
			type: Boolean,
			default: false
		}
	});

	const form = reactive({
		title: "",
		type: "",
		keyword: "",
		templateCode: "",
		jumpType: "",
		link: "",
		content: "",
		remark: "",
		isEnable: 1
	});

	const rules = reactive({
		title: [{ required: true, message: "请输入标题", trigger: "blur" }],
		type: [{ required: true, message: "请选择类型", trigger: "blur" }],
		keyword: [{ required: true, message: "请输入关键字", trigger: "blur" }],
		templateCode: [{ required: true, message: "请输入模板Code", trigger: "blur" }],
		jumpType: [{ required: true, message: "请选择跳转类型", trigger: "blur" }],
		link: [{ required: true, message: "请输入跳转链接", trigger: "blur" }],
		content: [{ required: true, message: "请输入内容", trigger: "blur" }]
	});

	const { registerFormDone, formInstance } = useDialogForm();

	// 初始化数据
	console.log(props.rowInfo);
	if (props.rowInfo.id) {
		Object.assign(form, props.rowInfo);
	}

	registerFormDone(async () => {
		try {
			await formInstance.value?.validate();
			const submitData = { ...form };
			if (props.rowInfo.id) {
				submitData.id = props.rowInfo.id;
			}
			await save(submitData);
			ElMessage.success("保存成功");
			return true;
		} catch (error) {
			console.error("保存失败:", error);
			ElMessage.error("保存失败，请检查输入信息");
			return false;
		}
	});
</script>

<style scoped lang="scss">
	:deep(.el-textarea__inner) {
		min-height: 80px;
	}
</style>
