<template>
	<div class="gis-module-card">
		<svg
			class="card-background"
			width="467.5"
			:height="height"
			:view-box="`0 0 467.5 ${height}`"
			fill="none"
			xmlns="http://www.w3.org/2000/svg"
		>
			<g id="Frame 113306">
				<foreignObject x="0" y="0" width="0" height="0"
					><div
						xmlns="http://www.w3.org/1999/xhtml"
						style="
							backdrop-filter: blur(2.5px);
							clip-path: url(#bgblur_0_4130_13299_clip_path);
							height: 100%;
							width: 100%;
						"
					></div
				></foreignObject>
				<!--  -->
				<path
					id="Subtract"
					data-figma-bg-blur-radius="5"
					:d="path"
					fill="#0F2340"
					fill-opacity="0.35"
					stroke="url(#paint0_linear_4130_13299)"
				/>
			</g>
			<defs>
				<clipPath id="bgblur_0_4130_13299_clip_path" transform="translate(0 0)">
					<path :d="path" />
				</clipPath>
				<linearGradient
					id="paint0_linear_4130_13299"
					x1="244"
					y1="0"
					x2="244"
					y2="227.852"
					gradientUnits="userSpaceOnUse"
				>
					<stop stop-color="white" stop-opacity="0" />
					<stop offset="0.279182" stop-color="white" stop-opacity="0.31" />
					<stop offset="0.502496" stop-color="white" stop-opacity="0.55" />
					<stop offset="0.733716" stop-color="white" stop-opacity="0.31" />
					<stop offset="1" stop-color="white" stop-opacity="0.24" />
				</linearGradient>
			</defs>
		</svg>

		<!-- 卡片内容 -->
		<div class="card-content" :style="{ height: `${height}px` }">
			<div class="card-title flex items-center justify-between">
				<div class="title-left flex gap-12px items-center j">
					<Icon moduleName="gis-map" iconSize="15px" name="bookmark-3-fill.png" />
					<span class="title-name">{{ title }}</span>
				</div>
				<div class="title-actions">
					<slot name="actions"></slot>
				</div>
			</div>
			<div class="card-body">
				<slot></slot>
			</div>
		</div>
		<img class="rangle-b" src="/icon/gis-map/rangle-b-left.png" alt="" srcset="" />
		<img class="rangle-b r" src="/icon/gis-map/rangle-b-right.png" alt="" srcset="" />
	</div>
</template>

<script setup lang="ts">
	import { Icon } from "@/components/icon/icon";
	import { computed } from "vue";

	// 定义组件属性
	const props = defineProps({
		title: {
			type: String,
			default: "模块标题"
		},
		width: {
			type: Number,
			default: 467.5
		},
		height: {
			type: Number,
			default: 252.852
		}
	});

	const path = computed(() => {
		return `M187.158 0.5L200.38 13.7217H${props.width}V${props.height}H0.602539L0.5 0.5H187.158Z`;
		// return `M${props.width / 2} 0.5 L${props.width} ${props.height / 2} H${props.width} V${props.height} H0.5 L0.5 0.5 H${props.width / 2}Z`;
	});
</script>

<style lang="scss" scoped>
	@keyframes slideIn {
		from {
			transform: translateX(100%);
			opacity: 0;
		}
		to {
			transform: translateX(0);
			opacity: 1;
		}
	}
	.gis-module-card {
		position: relative;
		width: 100%;
		animation: slideIn 0.5s ease-out backwards;

		.card-background {
			position: absolute;
			top: 0;
			left: 0;

			z-index: 0;
		}

		.card-content {
			position: relative;
			z-index: 1;
			width: 467.5px;
			display: flex;
			flex-direction: column;
			box-sizing: border-box;

			.card-title {
				font-size: 18px;
				font-weight: bold;
				color: #fff;
				background-image: url(/icon/gis-map/card-header-bg.png);
				width: 100%;
				height: 35px;
				background-size: 100% 100%;
				display: flex;
				align-items: center;
				padding: 0 12px;
				padding-right: 0;
			}
			.title-name {
				font-weight: bold;
				font-size: 16px;
				letter-spacing: 1px;
				text-shadow: 0px 2px 2px rgba(0, 0, 0, 0.25);
				text-align: left;
				font-style: normal;
				text-transform: none;
				background: linear-gradient(180deg, #ffffff 0%, #5099ff 100%);
				-webkit-background-clip: text;
				background-clip: text;
				-webkit-text-fill-color: transparent;
				color: transparent;
			}

			.card-body {
				flex: 1;
				min-height: 0;
				color: #fff;
				padding: 9px;
			}
		}
		.rangle-b {
			position: absolute;
			width: 13px;
			height: 13px;
			left: -1px;
			bottom: -2px;
			&.r {
				right: -1px;
				left: auto;
			}
		}
	}
</style>
