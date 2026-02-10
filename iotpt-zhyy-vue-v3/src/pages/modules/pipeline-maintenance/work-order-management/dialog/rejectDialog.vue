<template>
	<div>
		<BaseForm v-bind="form" />
	</div>
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { missionRejection } from "@/api/pipeline-maintenance/work-order";
	import { number } from "vue-types";

	const props = defineProps({
		id: number().def(),
	});

	const { form, registerFormDone } = useDialogStructForm({
		labelWidth: 60,
		showExpand: false,
		showMessage: true,
		labelPosition: "top",
		formItems: [
			{
				label: "驳回原因",
				prop: "reason",
				value: "",
				type: "ElInput",
				span: 24,
				attrs: {
					type: 'textarea',
					maxlength: 200,
					rows: 4,
				}
			},
		],
		rules: {
			reason: [
				{
					required: true,
					message: "请输入驳回原因",
					trigger: "blur"
				}
			],
		},
	});
	registerFormDone(async() => {
		console.log('66666666', form.value)
		const res = await missionRejection({ ...form.value, id: props.id });
		return res;
	});
</script>
<style lang="scss" scoped>
	.title {
		width: 100%;
		height: 24px;
		line-height: 24px;
		background: linear-gradient(
			90deg,
			rgba(27, 134, 255, 0.1) 0%,
			rgba(27, 134, 255, 0) 100%
		);
		border-radius: 0px 0px 0px 0px;
		border-left: 4px solid #1b86ff;
		font-size: 16px;
		font-weight: bold;
		padding-left: 10px;
	}
</style>
