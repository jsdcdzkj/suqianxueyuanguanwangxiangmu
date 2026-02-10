<template>
	<div class="contrastdetail flex">
		<Icon :name="iconName" iconSize="62px" moduleName="realtime_water" />
		<div class="contrastdetail__content flex">
			<div class="contrastdetail__label">
				<span>{{ label }}</span>
				<slot name="label"></slot>
			</div>
			<div class="contrastdetail__value">
				<span>{{ value }}</span>
				<span class="contrastdetail__unit">{{ unit }}</span>
			</div>
		</div>
		<div class="contrastdetail__operation">
			<slot name="operation"></slot>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { Icon } from "@/components/icon/icon";

	// 定义 props
	interface Props {
		iconName?: string;
		label?: string;
		value?: string | number;
		unit?: string;
	}

	// 设置默认值
	const props = withDefaults(defineProps<Props>(), {
		iconName: "guan_blue.png",
		label: "今日用水",
		value: "456.3",
		unit: "m3"
	});
	console.log(props);
</script>

<style lang="scss" scoped>
	.contrastdetail {
		background: #ffffff;
		border-radius: 0px 0px 0px 0px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		flex: 1;
		padding: 12px;
		gap: 12px;
		align-items: center;
		&__content {
			flex: 1;
			flex-direction: column;
			justify-content: space-around;
			.contrastdetail__label {
				display: flex;
				gap: 12px;
				align-items: center;
				span {
					font-size: 14px;
					color: rgba(0, 0, 0, 0.65);
				}
			}
			.contrastdetail__value {
				span {
					font-size: 32px;
					font-weight: bold;
					color: #000;
				}
				.contrastdetail__unit {
					font-size: 14px;
					font-weight: 400;

					margin-left: 4px;
					color: rgba(0, 0, 0, 0.45);
				}
			}
		}
		&__operation {
		}
	}
</style>
