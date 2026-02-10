<template>
	<div class="school-area-sort">
		<div
			v-for="(item, index) in items"
			:key="index"
			class="sort-item"
			:class="{ active: activeIndex === index }"
			@click="handleClick(index)"
		>
			<div class="rect" :style="{ backgroundColor: getRandomColor() }"></div>
			<div class="sort-name">{{ index + 1 }}.{{ item.name }}</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { ref } from "vue";

	interface Props {
		items?: any[];
		defaultIndex?: number;
	}

	const props = withDefaults(defineProps<Props>(), {
		items: () => [],
		defaultIndex: 0
	});

	const emit = defineEmits<{
		(e: "change", index: number): void;
	}>();

	const activeIndex = ref(props.defaultIndex);

	const handleClick = (index: number) => {
		activeIndex.value = index;
		emit("change", index);
	};

	// 生成随机颜色
	const getRandomColor = () => {
		const letters = "0123456789ABCDEF";
		let color = "#";
		for (let i = 0; i < 6; i++) {
			color += letters[Math.floor(Math.random() * 16)];
		}
		return color;
	};
</script>

<style scoped>
	.school-area-sort {
		display: flex;
		flex-direction: column;
		width: 86px;
	}

	/* 未选中效果 */
	.sort-item {
		width: 86px;
		flex: 1;
		border-radius: 0px;
		border: 1px solid rgba(255, 255, 255, 0.12);
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
		transition: all 0.3s ease;
		color: #fff;
		font-size: 12px;
	}

	/* 选中效果 */
	.sort-item.active {
		width: 86px;
		height: 24px;
		background: #085cbe;
		box-shadow: inset 0px 0px 7px 0px #1ce1ff;
		border-radius: 0px;
		border: none;
	}

	/* 正方形样式 */
	.rect {
		width: 8px;
		height: 8px;
		margin-right: 4px;
	}
</style>
