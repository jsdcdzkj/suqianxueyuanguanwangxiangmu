<template>
	<SupplyCard title="选择部门：">
		<div class="selected-info">
			<span class="label">已选：</span>
			<span class="value">{{ selectedDimensionText }} - {{ selectedNodeText }}</span>
		</div>
		<div class="tree-container">
			<el-tree
				ref="deptTreeRef"
				:data="deptTreeData"
				:props="defaultProps"
				:expand-on-click-node="false"
				:highlight-current="true"
				node-key="id"
				show-checkbox
				@check-change="handleDeptCheckChange"
			>
				<template #default="{ node, data }">
					<span class="custom-tree-node">
						<span class="node-label">{{ node.label }}</span>
					</span>
				</template>
			</el-tree>
		</div>
	</SupplyCard>
</template>

<script setup lang="ts">
	import { ref, computed, watch } from "vue";
	import { ElMessage } from "element-plus";
	import SupplyCard from "./SupplyCard.vue";

	// 接收来自 SearchDimension 的选中信息
	const selectedDimension = ref("partition");
	const selectedNode = ref<any>(null);

	// 维度类型映射
	const dimensionMap: any = {
		partition: "分区",
		department: "部门",
		building: "楼栋",
		area: "区域"
	};

	// 计算属性：选中的维度文本
	const selectedDimensionText = computed(() => {
		return dimensionMap[selectedDimension.value] || "";
	});

	// 计算属性：选中的节点文本
	const selectedNodeText = computed(() => {
		return selectedNode.value?.label || "";
	});

	// 部门树数据
	const deptTreeData = ref([]);

	// 部门树组件引用
	const deptTreeRef = ref();

	// 部门树形结构的配置项
	const defaultProps = {
		children: "children",
		label: "label"
	};

	// 选中的部门节点
	const checkedDeptNodes = ref([]);

	// 监听选中的节点变化，更新部门树数据
	watch(
		() => selectedNode.value,
		(newVal) => {
			if (newVal) {
				loadDeptTreeData();
			}
		},
		{ immediate: true, deep: true }
	);

	// 加载部门树数据（基于选中的节点）
	const loadDeptTreeData = async () => {
		try {
			// 这里应该调用实际API获取数据
			// const response = await getDeptTreeData(selectedDimension.value, selectedNode.value.id)
			// deptTreeData.value = response.data

			// 示例数据：使用选中节点的子节点
			if (selectedNode.value && selectedNode.value.children) {
				deptTreeData.value = JSON.parse(JSON.stringify(selectedNode.value.children));
			} else {
				deptTreeData.value = [];
			}
		} catch (error) {
			ElMessage.error("加载部门数据失败");
			console.error(error);
		}
	};

	// 部门节点选中状态变化处理
	const handleDeptCheckChange = (data: any, checked: boolean, indeterminate: boolean) => {
		// 获取所有选中的部门节点
		if (deptTreeRef.value) {
			checkedDeptNodes.value = deptTreeRef.value.getCheckedNodes();
		}
		console.log("选中的部门节点:", checkedDeptNodes.value);
		// 可以触发emit事件通知父组件
		// emit('dept-select', { dimension: selectedDimension.value, parentNode: selectedNode.value, nodes: checkedDeptNodes.value })
	};

	// 暴露方法给父组件，用于更新选中的维度和节点
	const updateSelection = (dimension: string, node: any) => {
		selectedDimension.value = dimension;
		selectedNode.value = node;
	};

	// 暴露方法给父组件
	defineExpose({
		updateSelection
	});
</script>

<style scoped lang="scss">
	.selected-info {
		display: flex;
		align-items: center;
		gap: 8px;
		padding: 8px 0;

		.label {
			font-weight: 500;
			color: #606266;
		}

		.value {
			flex: 1;
			color: #303133;
		}
	}

	.tree-container {
		flex: 1;
		overflow: auto;
		padding: 12px;

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
