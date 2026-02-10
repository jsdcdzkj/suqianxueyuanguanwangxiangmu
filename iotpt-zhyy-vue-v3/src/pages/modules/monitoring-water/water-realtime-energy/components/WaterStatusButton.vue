<template>
	<div :class="['waterstatusbutton', isUp ? 'style-up' : 'style-down']">
		<span class="text">
			<!-- 使用插槽允许父组件传入具体数值，例如 12.5% -->
			<slot>12.5%</slot>
		</span>
		<!-- 根据状态显示不同的箭头图标 -->
		<span class="icon" v-if="isUp">▲</span>
		<span class="icon" v-else>▼</span>
	</div>
</template>

<script>
	export default {
		name: "WaterStatusButton",
		props: {
			// 接收一个布尔值，true 代表增长（红色），false 代表下降（绿色）
			isUp: {
				type: Boolean,
				default: true
			}
		}
	};
</script>

<style scoped>
	/* 基础布局样式 */
	.waterstatusbutton {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 76px;
		height: 32px;
		border-radius: 4px;
		font-size: 16px;
		line-height: 24px;
		padding: 0 4px; /* 增加一点内边距防止文字和箭头紧贴 */
		box-sizing: border-box;
	}

	.text {
		margin-right: 2px; /* 文字和箭头的间距 */
	}

	.icon {
		font-size: 12px; /* 调整箭头大小 */
		line-height: 1;
	}

	/* --- 绿色样式 (下降) --- */
	.style-down {
		background: #edffec;
		border: 1px solid #81d176;
		color: #61c554;
	}

	/* --- 红色样式 (增长) --- */
	.style-up {
		background: #fff0f0;
		border: 1px solid #ff8e8e;
		color: #ff4d4d;
	}
</style>
