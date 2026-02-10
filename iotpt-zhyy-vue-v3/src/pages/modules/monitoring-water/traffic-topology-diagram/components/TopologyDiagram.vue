<template>
	<div class="topology-diagram">
		<div id="container" ref="container"></div>
	</div>
</template>

<script setup>
	import { onMounted, onUnmounted, ref } from "vue";
	import LogicFlow from "@logicflow/core";
	import "@logicflow/core/dist/index.css";

	const props = defineProps({
		graphData: {
			type: Object,
			required: true
		},
		customNodeStyle: {
			type: Object,
			default: () => ({})
		},
		customEdgeStyle: {
			type: Object,
			default: () => ({
				stroke: "#BFBFBF",
				strokeWidth: 1
			})
		}
	});

	let lf = null;
	const container = ref(null);

	// 更新画布大小
	const updateCanvasSize = () => {
		if (!lf || !container.value) return;
		const rect = container.value.getBoundingClientRect();
		lf.updateEditConfig({
			width: rect.width,
			height: rect.height
		});
		lf.resize(rect.width, rect.height);
		lf.fitView();
	};

	onMounted(() => {
		// 获取容器初始大小
		const rect = container.value.getBoundingClientRect();

		// 初始化 LogicFlow
		lf = new LogicFlow({
			container: container.value,
			width: rect.width,
			height: rect.height,
			grid: {
				visible: true,
				type: "mesh",
				size: 20,
				config: {
					color: "#f7f7f7",
					thickness: 1
				}
			},
			background: {
				backgroundColor: "#fff"
			},
			isSilentMode: false,
			stopZoomGraph: false,
			stopScrollGraph: false,
			adjustNodePosition: true
		});

		// 注册自定义节点
		lf.register("custom-rect", ({ RectNode, RectNodeModel, h }) => {
			class CustomRectNode extends RectNode {
				getShape() {
					const { model } = this.props;
					const { x, y, width, height, properties } = model;
					const { titleHeight, titleBg, titleColor, contentColor, text, value } = properties;

					return h("g", {}, [
						// 外层容器
						h("rect", {
							x: x - width / 2,
							y: y - height / 2,
							width,
							height,
							fill: properties.background,
							stroke: "rgba(0,0,0,0.06)",
							strokeWidth: 1,
							style: {
								filter: "drop-shadow(0px 1px 3px rgba(0,0,0,0.12))"
							}
						}),
						// 标题背景
						h("rect", {
							x: x - width / 2,
							y: y - height / 2,
							width,
							height: titleHeight,
							fill: titleBg,
							style: {
								backgroundSize: "cover",
								backgroundPosition: "center"
							}
						}),
						// 标题文字
						h(
							"text",
							{
								x,
								y: y - height / 2 + titleHeight / 2,
								fill: titleColor,
								fontSize: properties.fontSize,
								textAnchor: "middle",
								dominantBaseline: "middle",
								style: {
									fontWeight: "bold"
								}
							},
							text
						),
						// 内容区域
						h("g", {}, [
							h(
								"text",
								{
									x,
									y: y + 5,
									fill: contentColor,
									fontSize: properties.fontSize - 2,
									textAnchor: "middle",
									dominantBaseline: "middle"
								},
								"水流量"
							),
							h(
								"text",
								{
									x,
									y: y + 20,
									fill: contentColor,
									fontSize: properties.fontSize + 2,
									textAnchor: "middle",
									dominantBaseline: "middle",
									style: {
										fontWeight: "bold"
									}
								},
								value || "0"
							)
						])
					]);
				}

				getAnchorShape() {
					return [];
				}
			}

			class CustomRectModel extends RectNodeModel {
				initNodeData(data) {
					super.initNodeData(data);
					const style = data.properties;
					this.width = style.width;
					this.height = style.height;
					this.text = style.text;
					this.value = style.value || Math.floor(Math.random() * 100);
				}

				getNodeStyle() {
					return {
						...super.getNodeStyle(),
						fill: "transparent",
						stroke: "transparent"
					};
				}

				getAnchorStyle() {
					return [];
				}
			}

			return {
				view: CustomRectNode,
				model: CustomRectModel
			};
		});

		// 渲染图形
		lf.render(props.graphData);

		// 初始自适应
		lf.fitView();

		// 添加窗口大小变化监听
		window.addEventListener("resize", updateCanvasSize);
	});

	onUnmounted(() => {
		// 移除事件监听
		window.removeEventListener("resize", updateCanvasSize);
	});
</script>

<style scoped>
	.topology-diagram {
		width: 100%;
		height: 100%;
		background: #f5f5f5;
	}

	#container {
		width: 100%;
		height: 100%;
	}
</style>
