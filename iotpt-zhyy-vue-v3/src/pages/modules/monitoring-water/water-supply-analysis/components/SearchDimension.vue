<template>
	<SupplyCard title="选择类型：">
		<div class="flex flex-col h-100% gap-12px">
			<el-radio-group v-model="currentDimension" @change="handleDimensionChange">
				<el-radio-button label="partition" class="flex-1">分区</el-radio-button>
				<el-radio-button label="department" class="flex-1">部门</el-radio-button>
				<el-radio-button label="building" class="flex-1">楼栋</el-radio-button>
				<el-radio-button label="area" class="flex-1">区域</el-radio-button>
			</el-radio-group>
			<div class="tree-container">
				<el-tree
					ref="treeRef"
					:data="treeData"
					:props="defaultProps"
					:expand-on-click-node="false"
					:highlight-current="true"
					node-key="id"
					show-checkbox
					@check-change="handleCheckChange"
				>
					<template #default="{ node, data }">
						<span class="custom-tree-node">
							<span class="node-label">{{ node.label }}</span>
						</span>
					</template>
				</el-tree>
			</div>
		</div>
	</SupplyCard>
</template>

<script setup lang="ts">
	import { ref, watch } from "vue";
	import { ElMessage } from "element-plus";
	import SupplyCard from "./SupplyCard.vue";

	// 当前选中的维度
	const currentDimension = ref("partition");

	// 树形数据
	const treeData = ref([]);

	// 树组件引用
	const treeRef = ref();

	// 树形结构的配置项
	const defaultProps = {
		children: "children",
		label: "label"
	};

	// 选中的节点
	const checkedNodes = ref([]);

	// 模拟数据
	const getMockData = (dimension: string) => {
		const mockData: any = {
			partition: [
				{
					id: "1",
					label: "一区",
					children: [
						{ id: "1-1", label: "A栋" },
						{ id: "1-2", label: "B栋" }
					]
				},
				{
					id: "2",
					label: "二区",
					children: [
						{ id: "2-1", label: "C栋" },
						{ id: "2-2", label: "D栋" }
					]
				}
			],
			department: [
				{
					id: "1",
					label: "技术部",
					children: [
						{ id: "1-1", label: "前端组" },
						{ id: "1-2", label: "后端组" }
					]
				},
				{
					id: "2",
					label: "运营部",
					children: [
						{ id: "2-1", label: "市场组" },
						{ id: "2-2", label: "客服组" }
					]
				}
			],
			building: [
				{
					id: "1",
					label: "A栋",
					children: [
						{ id: "1-1", label: "1层" },
						{ id: "1-2", label: "2层" }
					]
				},
				{
					id: "2",
					label: "B栋",
					children: [
						{ id: "2-1", label: "1层" },
						{ id: "2-2", label: "2层" }
					]
				}
			],
			area: [
				{
					id: "1",
					label: "北区",
					children: [
						{ id: "1-1", label: "办公区" },
						{ id: "1-2", label: "休息区" }
					]
				},
				{
					id: "2",
					label: "南区",
					children: [
						{ id: "2-1", label: "会议区" },
						{ id: "2-2", label: "展示区" }
					]
				}
			]
		};

		return mockData[dimension] || [];
	};

	// 加载树形数据
	const loadTreeData = async (dimension: string) => {
		try {
			// 这里应该调用实际API获取数据
			// const response = await getTreeData(dimension)
			// treeData.value = response.data

			// 示例数据
			treeData.value = getMockData(dimension);
		} catch (error) {
			ElMessage.error("加载数据失败");
			console.error(error);
		}
	};

	// 监听维度变化，加载对应数据
	watch(
		currentDimension,
		(newVal) => {
			loadTreeData(newVal);
		},
		{ immediate: true }
	);

	// 维度变化处理
	const handleDimensionChange = (value: string) => {
		console.log("维度切换:", value);
	};

	// 节点选中状态变化处理
	const handleCheckChange = (data: any, checked: boolean, indeterminate: boolean) => {
		// 获取所有选中的节点
		if (treeRef.value) {
			checkedNodes.value = treeRef.value.getCheckedNodes();
		}
		console.log("选中节点:", checkedNodes.value);
		// 可以触发emit事件通知父组件
		// emit('node-select', { dimension: currentDimension.value, nodes: checkedNodes.value })
	};
</script>

<style scoped lang="scss">
	::v-deep(.el-radio-button__inner) {
		width: 100%;
	}
	.tree-container {
		flex: 1;
		overflow: auto;

		min-height: 0;

		:deep(.el-tree) {
			background: transparent;

			.el-tree-node__content {
				height: 32px;

				&:hover {
					background-color: #f5f7fa;
				}
			}

			.is-current > .el-tree-node__content {
				background-color: #ecf5ff;
				color: #409eff;
			}
		}
	}

	.custom-tree-node {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-right: 8px;

		.node-label {
			font-size: 14px;
		}
	}
</style>
