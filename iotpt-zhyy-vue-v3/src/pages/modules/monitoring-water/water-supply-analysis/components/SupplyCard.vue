<template>
	<div class="supply-card">
		<div class="card-header">
			<div class="card-title">{{ title }}</div>
			<div class="card-extra">
				<slot name="extra"></slot>
			</div>
		</div>
		<div class="card-body">
			<slot></slot>
		</div>
	</div>
</template>

<script setup lang="ts">
	defineProps({
		title: {
			type: String,
			default: ""
		}
	});
</script>

<style scoped>
	.supply-card {
		background: rgba(255, 255, 255, 0);
		box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		display: flex;
		flex-direction: column;
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 12px;
		padding-bottom: 0;
	}

	.card-title {
		font-weight: normal;
		font-size: 16px;
		color: rgba(0, 0, 0, 0.85);
		line-height: 24px;
		font-weight: 500;
	}

	.card-extra {
		flex-shrink: 0;
	}

	.card-body {
		flex: 1;
		min-height: 0;

		padding: 12px;
		overflow-y: auto;
	}
</style>
