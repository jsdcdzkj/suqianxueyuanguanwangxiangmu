<template>
	<div class="gis-map-change-type">
		<div
			class="gis-map-change-type__item"
			:class="{ active: index == currentIndex }"
			v-for="(item, index) in types"
			@click="currentIndex = index"
			:key="index"
		>
			{{ item.name }}
		</div>
	</div>
</template>

<script setup lang="ts">
	import { ref } from "vue";

	const types = ref([
		{
			name: "卫星影像",
			icon: ""
		},
		{
			name: "矢量地图",
			icon: ""
		},
		{
			name: "地形图",
			icon: ""
		},
		{
			name: "三维地图",
			icon: ""
		}
	]);
	const currentIndex = ref(0);
</script>

<style lang="scss" scoped>
	.gis-map-change-type {
		width: 360px;
		height: 40px;
		background: rgba(15, 35, 64, 0.35);
		box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.21);
		border-radius: 0px 0px 0px 0px;
		border: 1px solid;
		position: absolute;
		left: 10px;
		top: calc(var(--app-page-toolbar-height) + 10px);
		border: 1px solid;
		border-image: linear-gradient(
				360deg,
				rgba(255, 255, 255, 0),
				rgba(255, 255, 255, 0.31),
				rgba(255, 255, 255, 0.55),
				rgba(255, 255, 255, 0.31),
				rgba(255, 255, 255, 0.24)
			)
			1 1;
		display: flex;
		align-items: center;

		&__item {
			width: 120px;
			height: 40px;
			background: rgba(255, 255, 255, 0);
			border-radius: 0px 0px 0px 0px;
			border-right: 1px solid rgba(255, 255, 255, 0.2);
			border-top: 1px solid rgba(255, 255, 255, 0.2);
			backdrop-filter: blur(2px);
			font-weight: bold;
			font-size: 16px;
			letter-spacing: 1px;
			text-shadow: 0px 2px 2px rgba(0, 0, 0, 0.25);
			text-align: center;
			color: #fff;
			line-height: 40px;
			cursor: pointer;
			&:last-child {
				border-right: 0px;
			}
			&.active {
				background: linear-gradient(90deg, #0861c7 0%, #3491fc 50%, #0861c7 100%);
				border-radius: 0px 0px 0px 0px;
				border: 1px solid;
				border-image: linear-gradient(90deg, rgba(164, 203, 255, 1), rgba(50, 117, 206, 0)) 1 1;
			}
		}
	}
</style>
