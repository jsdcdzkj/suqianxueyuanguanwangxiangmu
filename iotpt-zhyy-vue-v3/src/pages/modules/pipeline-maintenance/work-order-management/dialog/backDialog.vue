<template>
	<div>
		<BaseForm v-bind="form" />
	</div>
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { backOut } from "@/api/pipeline-maintenance/work-order";
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
				label: "备注",
				prop: "notes",
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
	});
	registerFormDone(async() => {
		console.log('66666666', form.value)
		const res = await backOut({ ...form.value, id: props.id });
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
