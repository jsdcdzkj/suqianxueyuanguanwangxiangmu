<template>
	<PageLayout :show-page-top="false">
		<template #pageContent>
			<div class="h-100% w-100% overflow-hidden flex flex-col">
				<TopTabbar />
				<KeyIndicatorCard class="p-12px flex-1 overflow-hidden">
					<template #header>
						<div class="flex items-center gap-12px">
							<el-checkbox-group v-model="checkList">
								<el-checkbox label="99">宿迁学院</el-checkbox>
								<el-checkbox label="1">一级分区</el-checkbox>
								<el-checkbox label="2">二级分区</el-checkbox>
								<el-checkbox label="3">三级分区</el-checkbox>
								<el-checkbox label="4">核减大用户水量</el-checkbox>
							</el-checkbox-group>
							<div class="title-unit">单位：m³/h</div>
						</div>
					</template>
					<template #actions>
						<div class="flex items-center gap-12px">
							<span class="line-item">平衡</span>
							<span class="line-item warning">预警</span>
							<span class="line-item danger">告警</span>
						</div>
					</template>
					<TopologyDiagram
						:graph-data="graphData"
						:custom-node-style="customNodeStyle"
						:custom-edge-style="customEdgeStyle"
					/>
				</KeyIndicatorCard>
			</div>
		</template>
	</PageLayout>
</template>

<script setup>
	import PageLayout from "@/layouts/PageStructure/PageLayout";
	import TopologyDiagram from "./components/TopologyDiagram.vue";
	import TopTabbar from "./components/TopTabbar.vue";
	import KeyIndicatorCard from "../energy-planning/components/KeyIndicatorCard.vue";
	import { ref } from "vue";
	const checkList = ref([]);
	// 自定义节点样式
	const customNodeStyle = {
		// 学院节点样式
		academy: {
			width: 180,
			height: 80,
			titleHeight: 30, // 标题高度
			background: "#FFFFFF",
			boxShadow: "0px 1px 3px 0px rgba(0,0,0,0.12)",
			borderRadius: "0px",
			border: "1px solid rgba(0,0,0,0.06)",
			titleBg: "url('/path/to/your/image.jpg')", // 替换为实际的图片路径
			fontSize: 14,
			titleColor: "#000",
			contentColor: "#333333"
		},
		// 一级分区节点样式
		level1: {
			width: 160,
			height: 70,
			titleHeight: 28,
			background: "#FFFFFF",
			boxShadow: "0px 1px 3px 0px rgba(0,0,0,0.12)",
			borderRadius: "0px",
			border: "1px solid rgba(0,0,0,0.06)",
			titleBg: "url('/path/to/your/image.jpg')",
			fontSize: 13,
			titleColor: "#000",
			contentColor: "#333333"
		},
		// 二级分区节点样式
		level2: {
			width: 140,
			height: 65,
			titleHeight: 26,
			background: "#FFFFFF",
			boxShadow: "0px 1px 3px 0px rgba(0,0,0,0.12)",
			borderRadius: "0px",
			border: "1px solid rgba(0,0,0,0.06)",
			titleBg: "url('/path/to/your/image.jpg')",
			fontSize: 12,
			titleColor: "#000",
			contentColor: "#333333"
		},
		// 三级分区节点样式
		level3: {
			width: 120,
			height: 60,
			titleHeight: 24,
			background: "#FFFFFF",
			boxShadow: "0px 1px 3px 0px rgba(0,0,0,0.12)",
			borderRadius: "0px",
			border: "1px solid rgba(0,0,0,0.06)",
			titleBg: "url('/path/to/your/image.jpg')",
			fontSize: 12,
			titleColor: "#000",
			contentColor: "#333333"
		}
	};

	// 自定义连线样式
	const customEdgeStyle = {
		stroke: "#BFBFBF",
		strokeWidth: 1
	};

	// 添加节点数据
	const graphData = {
		nodes: [
			{
				id: "academy",
				type: "custom-rect",
				x: 100,
				y: 300,
				properties: {
					text: "某学院",
					value: "85",
					...customNodeStyle.academy
				}
			},
			{
				id: "level1-1",
				type: "custom-rect",
				x: 350,
				y: 200,
				properties: {
					text: "一级分区1",
					value: "65",
					...customNodeStyle.level1
				}
			},
			{
				id: "level1-2",
				type: "custom-rect",
				x: 350,
				y: 400,
				properties: {
					text: "一级分区2",
					value: "72",
					...customNodeStyle.level1
				}
			},
			{
				id: "level2-1",
				type: "custom-rect",
				x: 600,
				y: 150,
				properties: {
					text: "二级分区1",
					value: "45",
					...customNodeStyle.level2
				}
			},
			{
				id: "level2-2",
				type: "custom-rect",
				x: 600,
				y: 250,
				properties: {
					text: "二级分区2",
					value: "58",
					...customNodeStyle.level2
				}
			},
			{
				id: "level2-3",
				type: "custom-rect",
				x: 600,
				y: 350,
				properties: {
					text: "二级分区3",
					value: "52",
					...customNodeStyle.level2
				}
			},
			{
				id: "level2-4",
				type: "custom-rect",
				x: 600,
				y: 450,
				properties: {
					text: "二级分区4",
					value: "48",
					...customNodeStyle.level2
				}
			},
			{
				id: "level3-1",
				type: "custom-rect",
				x: 850,
				y: 100,
				properties: {
					text: "三级分区1",
					value: "25",
					...customNodeStyle.level3
				}
			},
			{
				id: "level3-2",
				type: "custom-rect",
				x: 850,
				y: 200,
				properties: {
					text: "三级分区2",
					value: "32",
					...customNodeStyle.level3
				}
			},
			{
				id: "level3-3",
				type: "custom-rect",
				x: 850,
				y: 300,
				properties: {
					text: "三级分区3",
					value: "28",
					...customNodeStyle.level3
				}
			},
			{
				id: "level3-4",
				type: "custom-rect",
				x: 850,
				y: 400,
				properties: {
					text: "三级分区4",
					value: "35",
					...customNodeStyle.level3
				}
			},
			{
				id: "level3-5",
				type: "custom-rect",
				x: 850,
				y: 500,
				properties: {
					text: "三级分区5",
					value: "30",
					...customNodeStyle.level3
				}
			}
		],
		edges: [
			// 学院到一级分区的连线
			{
				id: "e1",
				sourceNodeId: "academy",
				targetNodeId: "level1-1",
				type: "polyline",
				propertie: {
					style: {
						stroke: "#fff",
						lineWidth: 2,
						endArrow: true
					}
				}
			},
			{
				id: "e2",
				sourceNodeId: "academy",
				targetNodeId: "level1-2",
				type: "polyline",
				...customEdgeStyle
			},
			// 一级分区到二级分区的连线
			{
				id: "e3",
				sourceNodeId: "level1-1",
				targetNodeId: "level2-1",
				type: "polyline",
				...customEdgeStyle
			},
			{
				id: "e4",
				sourceNodeId: "level1-1",
				targetNodeId: "level2-2",
				type: "polyline",
				...customEdgeStyle
			},
			{
				id: "e5",
				sourceNodeId: "level1-2",
				targetNodeId: "level2-3",
				type: "polyline",
				...customEdgeStyle
			},
			{
				id: "e6",
				sourceNodeId: "level1-2",
				targetNodeId: "level2-4",
				type: "polyline",
				...customEdgeStyle
			},
			// 二级分区到三级分区的连线
			{
				id: "e7",
				sourceNodeId: "level2-1",
				targetNodeId: "level3-1",
				type: "polyline",
				...customEdgeStyle
			},
			{
				id: "e8",
				sourceNodeId: "level2-1",
				targetNodeId: "level3-2",
				type: "polyline",
				...customEdgeStyle
			},
			{
				id: "e9",
				sourceNodeId: "level2-2",
				targetNodeId: "level3-3",
				type: "polyline",
				...customEdgeStyle
			},
			{
				id: "e10",
				sourceNodeId: "level2-3",
				targetNodeId: "level3-4",
				type: "polyline",
				...customEdgeStyle
			},
			{
				id: "e11",
				sourceNodeId: "level2-4",
				targetNodeId: "level3-5",
				type: "polyline",
				...customEdgeStyle
			}
		]
	};
</script>

<style scoped lang="scss">
	#container {
		width: 100%;
		height: 100%;
		min-height: calc(100vh - 64px);
		background: #f5f5f5;
	}
	::v-deep(.el-checkbox-group .el-checkbox) {
		margin-right: 12px;
	}
	.title-unit {
		display: flex;
		align-items: center;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		&::before {
			content: "";
			display: inline-block;
			width: 1px;
			height: 16px;
			background: rgba(0, 0, 0, 0.15);
			margin-right: 12px;
		}
	}
	.line-item {
		display: inline-block;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		&::before {
			content: "";
			display: inline-block;
			width: 16px;
			height: 4px;
			background: #57bd94;
			vertical-align: middle;
			margin-right: 6px;
		}
		&.warning {
			&::before {
				background: #f5a623;
			}
		}
		&.danger {
			&::before {
				background: #f52323;
			}
		}
	}
</style>
