<template>
	<div class="device-card">
		<div class="card-header">
			<div class="energy-container">
				<span class="energy-value">{{ formatEnergyValue }}</span>
				<span class="energy-unit">万m³</span>
			</div>
			<el-tag :type="device.onlineStatus === '在线' ? 'success' : 'danger'">
				{{ device.onlineStatus }}
			</el-tag>
		</div>
		<div class="card-body">
			<Icon name="kudu.png" moduleName="realtime_water" iconSize="75px"></Icon>
			<div class="flex-1 flex flex-col">
				<div class="card-item">
					<span class="item-label">设备名称:</span>
					<span class="item-value">{{ device.deviceName }}</span>
				</div>
				<div class="card-item">
					<span class="item-label">设备类型:</span>
					<span class="item-value">{{ device.deviceType }}</span>
				</div>
				<div class="card-item">
					<span class="item-label">设备点位描述:</span>
					<span class="item-value">{{ device.deviceLocation }}</span>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { Icon } from "@/components/icon/icon";
	import { computed } from "vue";

	// 定义设备接口
	interface Device {
		id: number;
		deviceName: string;
		deviceType: string;
		deviceLocation: string;
		energyConsumption: string;
		onlineStatus: string;
	}

	// 定义组件props
	interface Props {
		device: Device;
	}

	// 使用defineProps
	const props = defineProps<Props>();

	// 格式化能耗值，转换为万m3
	const formatEnergyValue = computed(() => {
		const value = parseFloat(props.device.energyConsumption);
		return (value / 10000).toFixed(4);
	});
</script>

<style lang="scss" scoped>
	.device-card {
		background-color: #fff;
		border-radius: 4px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		transition: all 0.3s;
		display: flex;
		flex-direction: column;
		height: 220px;
		cursor: pointer;

		&:hover {
			box-shadow: 0 2px 16px 0 rgba(0, 0, 0, 0.15);
			transform: translateY(-2px);
		}

		.card-header {
			background: #f0f5ff;
			border-radius: 0px 0px 0px 0px;
			border-top: 2px solid #345bad;
			padding: 12px 16px;
			display: flex;
			justify-content: space-between;
			align-items: center;

			.energy-container {
				display: flex;
				align-items: baseline;

				.energy-value {
					font-size: 20px;
					font-weight: bold;
					color: #345bad;
				}

				.energy-unit {
					font-size: 14px;
					color: #999;
					margin-left: 4px;
				}
			}
		}

		.card-body {
			padding: 12px;
			flex: 1;
			display: flex;
			gap: 12px;
			align-items: center;
			width: 100%;

			.card-item {
				margin-bottom: 12px;
				display: flex;
				width: 100%;
				gap: 12px;

				&:last-child {
					margin-bottom: 0;
				}

				.item-label {
					min-width: 100px;
					color: #606266;
					font-size: 14px;
				}

				.item-value {
					font-size: 14px;
					color: #303133;
					font-weight: 500;
				}
			}
		}
	}
</style>
