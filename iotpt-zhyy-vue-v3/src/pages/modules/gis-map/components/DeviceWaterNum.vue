<template>
	<div class="device-water-num">
		<div class="device-water-num__header">{{ title }}</div>
		<div class="device-water-num__body">
			<div class="num-content">
				<span>共</span>
				<span class="value">80</span>
				<span>台</span>
			</div>
			<div class="pies">
				<div class="pie-item online">
					<span class="pie-item-num">100 </span>
					<span class="pie-item-label">离线</span>
					<img class="pie-item-bg" src="/icon/gis-map/pie-bg.png" alt="" srcset="" />
				</div>
				<div class="pie-item">
					<span class="pie-item-num"> 20</span>
					<span class="pie-item-label">故障</span>
					<img class="pie-item-bg" src="/icon/gis-map/pie-bg-active.png" alt="" srcset="" />
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	defineProps({
		title: {
			type: String,
			default: "智能水表设备"
		}
	});
</script>

<style lang="scss" scoped>
	.device-water-num {
		flex: 1;
		background: rgba(255, 255, 255, 0.05);
		display: flex;
		height: 120px;
		flex-direction: column;
		&__header {
			height: 28px;
			background-image: url(/icon/gis-map/child-top-bg.png);
			background-repeat: no-repeat;
			background-size: 100% 100%;
			line-height: 28px;
			text-align: center;
			font-size: 12px;
			font-weight: bold;
		}
		&__body {
			flex: 1;
			min-height: 0;
			display: flex;
			flex-direction: column;
			.num-content {
				text-align: center;
				span {
					vertical-align: text-bottom;
				}
			}
			.value {
				color: #40bfa8;
				font-size: 26px;
				font-weight: bold;
				line-height: 26px;
				margin: 0 4px;
			}
			.pies {
				flex: 1;
				display: flex;
				gap: 4px;
				.pie-item {
					position: relative;
					flex: 1;
					display: flex;
					flex-direction: column;
					align-items: center;
					gap: 4px;

					.pie-item-num {
						font-family: PangMenZhengDao, PangMenZhengDao;
						font-weight: bold;
						font-size: 20px;
						height: 21px;
						text-shadow: 0px 2px 2px rgba(0, 0, 0, 0.25);
						text-align: center;
						font-style: normal;
						text-transform: none;
						background: linear-gradient(180deg, #ffffff 0%, #5099ff 100%);
						-webkit-background-clip: text;
						background-clip: text;
						-webkit-text-fill-color: transparent;
						color: transparent;
						margin-top: 10px;
					}
					.pie-item-label {
						font-family:
							Alibaba PuHuiTi 3,
							Alibaba PuHuiTi 30;
						font-weight: normal;
						font-size: 12px;
						color: rgba(255, 255, 255, 0.85);
						height: 14px;
						transform: scale(0.8);
					}
					.pie-item-bg {
						position: absolute;
						width: 100%;
						height: 28px;
						bottom: 0;
						left: 0;
					}
					&.online {
						.pie-item-num {
							-webkit-background-clip: inherit;
							background-clip: inherit;
							-webkit-text-fill-color: inherit;
							color: #fff;
							background: none;
						}
					}
				}
			}
		}
	}
</style>
