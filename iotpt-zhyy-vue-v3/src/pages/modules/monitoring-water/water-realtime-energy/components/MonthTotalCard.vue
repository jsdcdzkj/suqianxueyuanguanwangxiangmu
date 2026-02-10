<template>
	<div class="monthtoalcard">
		<div class="monthtoalcard__icon">
			<Icon :name="iconName" iconSize="56px" moduleName="realtime_water"></Icon>
		</div>
		<div class="monthtoalcard__content">
			<div class="content__title">{{ title }}</div>
			<div class="content__value">
				<span> {{ value }}</span>
				<span class="content__value__unit">{{ unit }}</span>
			</div>
			<div class="content__contrast">
				上月同期
				<span
					class="content__contrast__value"
					:class="{ up: contrast.type === 'up', down: contrast.type === 'down' }"
				>
					{{ contrast.type === "up" ? "+" : "" }}{{ contrast.value }}%
				</span>
				<span class="content__contrast__line"></span>
				{{ contrastTitle }}
				<span class="content__contrast__va">{{ contrastValue }} {{ unit }}</span>
			</div>
		</div>
		<div class="monthtoalcard__echarts">
			<slot name="chart"></slot>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { Icon } from "@/components/icon/icon";

	defineProps({
		title: {
			type: String,
			default: "月总能耗"
		},
		iconName: {
			type: String,
			default: "contrast-drop-2-line.png"
		},
		value: {
			type: Number,
			default: 0
		},
		unit: {
			type: String,
			default: "m3"
		},
		contrast: {
			type: Object,
			default: () => {
				return {
					value: 0,
					type: "down"
				};
			}
		},
		contrastTitle: {
			type: String,
			default: "上月用水"
		},
		contrastValue: {
			type: Number,
			default: 0
		}
	});
</script>

<style lang="scss" scoped>
	.monthtoalcard {
		flex: 1;
		width: 100%;
		height: 116px;
		background: #fcfdff;
		border-radius: 0px 0px 0px 0px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		padding: 12px;
		display: flex;
		align-items: center;
		&__icon {
			width: 88px;
			height: 88px;
			display: flex;
			align-items: center;
			justify-content: center;
			background: linear-gradient(0deg, #e1edfa 0%, #e4edfb 100%);
			border-radius: 8px 8px 8px 8px;
			flex-shrink: 0;
		}
		&__content {
			margin-left: 12px;
			width: 354px;
			flex-shrink: 0;
			.content {
				&__title {
					font-family:
						PingFangSC-Medium,
						PingFang SC;
					font-weight: 500;
					color: rgba(0, 0, 0, 0.85);
					line-height: 20px;
					font-size: 16px;
				}
				&__value {
					font-weight: bold;
					font-size: 32px;
					color: rgba(0, 0, 0, 0.85);
					&__unit {
						font-weight: normal;
						font-size: 14px;
						color: rgba(0, 0, 0, 0.85);
						line-height: 24px;
						margin-left: 6px;
					}
				}
				&__contrast {
					font-size: 16px;
					font-family:
						PingFangSC-Regular,
						PingFang SC;
					font-weight: 400;
					color: rgba(0, 0, 0, 0.65);
					line-height: 17px;
					margin-top: 4px;
					display: flex;
					align-items: center;
					gap: 12px;
					&__value {
						color: #ff6b6b;
						&::after {
							content: "";
							// 三角形
							display: inline-block;
							width: 0;
							height: 0;
							border-width: 4px;
							border-style: solid;
							border-color: transparent transparent #ff6b6b transparent;
							margin-left: 4px;
						}
						&.up {
							color: #52c41a;

							&::after {
								border-color: #52c41a transparent transparent transparent;
							}
						}
						&.down {
							color: #ff6b6b;
							&::after {
								border-color: transparent transparent #ff6b6b transparent;
							}
						}
					}
					&__line {
						width: 1px;
						height: 20px;
						background: #e5e5e5;
						margin: 0 8px;
						display: inline-block;
					}
				}
			}
		}
		&__echarts {
			flex: 1;
			height: 100%;
			margin-left: 12px;
		}
	}
	.content__contrast__va {
		color: rgba(0, 0, 0, 0.85);
	}
</style>
