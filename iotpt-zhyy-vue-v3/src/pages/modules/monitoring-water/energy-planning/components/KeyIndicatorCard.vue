<template>
	<div class="KeyIndicatorCard">
		<div class="KeyIndicatorCard__header">
			<span class="header__title" v-if="!slots.header">
				{{ title }}
			</span>
			<slot name="header" v-else></slot>
			<div class="header__actions">
				<slot name="actions"></slot>
			</div>
		</div>
		<div class="KeyIndicatorCard__body" :style="{ padding: padding }">
			<slot></slot>
		</div>
	</div>
</template>

<script setup lang="ts">
	const props = defineProps({
		title: {
			type: String,
			default: ""
		},
		padding: {
			type: String,
			default: "0px"
		}
	});
	const slots = defineSlots();
</script>

<style lang="scss" scoped>
	.KeyIndicatorCard {
		display: flex;
		flex-direction: column;
		width: 100%;
		&__header {
			height: 40px;
			background: #f8f8f8;
			border-radius: 4px 4px 4px 4px;
			padding: 0 12px;
			display: flex;
			align-items: center;
			justify-content: space-between;
			.header__title {
				font-size: 16px;
				color: rgba(0, 0, 0, 0.85);
				line-height: 24px;
				font-weight: bold;
			}
			.header__actions {
			}
		}
		&__body {
			flex: 1;
			min-height: 0;
			width: 100%;
		}
	}
</style>
