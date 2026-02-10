<template>
	<div class="progress-bar-container">
		<div class="progress-title">{{ title }}</div>
		<div class="progress-wrapper">
			<div class="progress-bg">
				<div class="progress-fill" :style="{ width: progress + '%' }">
					<div class="progress-pattern"></div>
				</div>
			</div>
			<div class="progress-value">{{ progress }}%</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	const props = defineProps({
		title: {
			type: String,
			default: "标题"
		},
		progress: {
			type: Number,
			default: 0,
			validator: (value: number) => value >= 0 && value <= 100
		}
	});
</script>

<style lang="scss" scoped>
	.progress-bar-container {
		display: flex;
		align-items: center;
		width: 100%;

		.progress-title {
			width: 158px;
			margin-right: 12px;
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
		}

		.progress-wrapper {
			flex: 1;
			display: flex;
			align-items: center;
			gap: 12px;

			.progress-bg {
				width: 100%;
				height: 24px;
				background: rgba(0, 0, 0, 0.06);
				border-radius: 4px;
				overflow: hidden;
				position: relative;

				.progress-fill {
					height: 100%;
					background-color: #4378e7;
					border-radius: 4px;
					position: relative;
					transition: width 0.3s ease;

					.progress-pattern {
						position: absolute;
						top: 0;
						left: 0;
						right: 0;
						bottom: 0;
						background-image: repeating-linear-gradient(
							45deg,
							transparent,
							transparent 10px,
							rgba(255, 255, 255, 0.2) 10px,
							rgba(255, 255, 255, 0.2) 20px
						);
					}
				}
			}

			.progress-value {
				font-weight: bold;
				font-size: 16px;
				color: rgba(0, 0, 0, 0.85);
				text-align: right;
				width: 60px;
			}
		}
	}
</style>
