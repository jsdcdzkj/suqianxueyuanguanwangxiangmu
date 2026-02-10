<template>
	<!-- 左侧选择区域 -->
	<div class="left-panel">
		<!-- 头部操作区 (v-if 控制显示) -->
		<div v-if="showHeader" class="panel-header">
			<div class="header-actions">
				<!-- 全选 (v-if 控制显示) -->
				<el-checkbox
					v-if="showCheckAll"
					v-model="checkAll"
					:indeterminate="isIndeterminate"
					@change="handleCheckAll"
					>全选</el-checkbox
				>
				<!-- 查看区域开关 (v-if 控制显示) -->
				<el-switch v-if="showViewSwitch" v-model="isViewEnabled" active-text="查看区域" />
			</div>
		</div>

		<!-- 搜索区 (v-if 控制显示) -->
		<div v-if="showSearch" class="panel-search">
			<el-input v-model="filterText" placeholder="请输入区域关键词进行搜索" :suffix-icon="Search" clearable />
		</div>

		<!-- 中间区域节点树 (垂直滚动) -->
		<div class="panel-tree">
			<el-tree
				ref="treeRef"
				:data="treeData"
				:props="defaultProps"
				:filter-node-method="filterNode"
				show-checkbox
				node-key="id"
				:default-checked-keys="defaultCheckedKeys"
				:default-expanded-keys="defaultExpandedKeys"
				@check="handleCheck"
			>
				<template #default="{ node, data }">
					<span class="custom-tree-node">
						<span>{{ node.label }}</span>
						<!-- 可以根据 isViewEnabled 控制节点样式或额外图标 -->
						<span v-if="!isViewEnabled" style="color: #ccc; font-size: 12px; margin-left: 8px"
							>(不可用)</span
						>
					</span>
				</template>
			</el-tree>
		</div>

		<!-- 底部按钮区 (v-if 控制显示) -->
		<div v-if="showQueryBtn" class="panel-footer">
			<el-button type="primary" style="width: 100%" @click="handleQuery">查询 </el-button>
		</div>
	</div>
</template>

<script setup>
	import { ref, watch } from "vue";
	import { ElMessage } from "element-plus";
	import { Search } from "@element-plus/icons-vue";

	// --- Props 定义 ---
	const props = defineProps({
		// 是否显示头部区域（包含全选和开关）
		showHeader: {
			type: Boolean,
			default: true
		},
		// 是否显示全选复选框
		showCheckAll: {
			type: Boolean,
			default: true
		},
		// 是否显示查看区域开关
		showViewSwitch: {
			type: Boolean,
			default: true
		},
		// 是否显示搜索框
		showSearch: {
			type: Boolean,
			default: true
		},
		// 是否显示查询按钮
		showQueryBtn: {
			type: Boolean,
			default: true
		}
	});

	// --- 数据定义 ---

	// 搜索关键词
	const filterText = ref("");
	// 树组件引用
	const treeRef = ref();
	// 全选状态
	const checkAll = ref(false);
	// 半选状态
	const isIndeterminate = ref(false);
	// 开启/关闭查看状态
	const isViewEnabled = ref(true);
	// 当前选中的节点 keys
	const checkedKeys = ref([]);

	// 模拟树形数据
	const treeData = ref([
		{
			id: "1",
			label: "一级 1",
			children: [
				{
					id: "1-1",
					label: "二级 1-1",
					children: [
						{ id: "1-1-1", label: "三级 1-1-1" },
						{ id: "1-1-2", label: "三级 1-1-2" }
					]
				},
				{
					id: "1-2",
					label: "二级 1-2",
					children: [
						{ id: "1-2-1", label: "三级 1-2-1" },
						{ id: "1-2-2", label: "三级 1-2-2" }
					]
				}
			]
		},
		{
			id: "2",
			label: "一级 2",
			children: [
				{
					id: "2-1",
					label: "二级 2-1",
					children: [
						{ id: "2-1-1", label: "内部节点 A" },
						{ id: "2-2-2", label: "内部节点 B" }
					]
				}
			]
		},
		{
			id: "3",
			label: "一级 3",
			children: [
				{ id: "3-1", label: "二级 3-1" },
				{ id: "3-2", label: "二级 3-2" }
			]
		}
	]);

	// 树组件配置
	const defaultProps = {
		children: "children",
		label: "label"
	};

	// 默认展开的节点
	const defaultExpandedKeys = ref(["1", "2", "3"]);
	// 默认选中的节点
	const defaultCheckedKeys = ref([]);

	// --- 方法 ---

	// 监听搜索框文字变化，过滤树节点
	watch(filterText, (val) => {
		treeRef.value.filter(val);
	});

	// 过滤方法
	const filterNode = (value, data) => {
		if (!value) return true;
		return data.label.includes(value);
	};

	// 处理全选/取消全选
	const handleCheckAll = (val) => {
		if (val) {
			// 获取所有节点的 key (递归获取)
			const allKeys = getAllNodeKeys(treeData.value);
			treeRef.value.setCheckedKeys(allKeys);
			isIndeterminate.value = false;
		} else {
			treeRef.value.setCheckedKeys([]);
			isIndeterminate.value = false;
		}
	};

	// 递归获取所有节点的 key
	const getAllNodeKeys = (nodes) => {
		let keys = [];
		nodes.forEach((node) => {
			keys.push(node.id);
			if (node.children && node.children.length > 0) {
				keys = keys.concat(getAllNodeKeys(node.children));
			}
		});
		return keys;
	};

	// 监听树的选中状态变化，更新全选框状态
	const handleCheck = (data, checkedStatus) => {
		const checkedCount = checkedStatus.checkedKeys.length;
		const allKeys = getAllNodeKeys(treeData.value);
		// 计算实际可勾选的节点数（排除被禁用的节点，如果有）
		// 这里简单处理：比较已选数量和总数
		checkAll.value = checkedCount === allKeys.length;
		isIndeterminate.value = checkedCount > 0 && checkedCount < allKeys.length;

		// 更新选中的 keys
		checkedKeys.value = checkedStatus.checkedKeys;
	};

	// 查询按钮点击事件
	const handleQuery = () => {
		if (!isViewEnabled.value) {
			ElMessage.warning("查看区域已关闭，请先开启查看区域");
			return;
		}
		if (checkedKeys.value.length === 0) {
			ElMessage.warning("请至少选择一个节点");
			return;
		}
		ElMessage.success("查询成功！选中节点：" + JSON.stringify(checkedKeys.value));
		console.log("Querying with keys:", checkedKeys.value);
	};
</script>

<style scoped>
	/* 左侧面板样式 */
	.left-panel {
		width: 100%;
		height: 100%; /* 确保父容器有高度，或者这里写死高度 */
		background-color: #fff;
		display: flex;
		flex-direction: column;
	}

	/* 头部样式 */
	.panel-header {
		flex-shrink: 0; /* 防止头部被压缩 */
		padding: 6px 12px;
		border-bottom: 1px solid #ebeef5;
		background-color: #fafafa;
	}

	.header-actions {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	/* 搜索区样式 */
	.panel-search {
		padding: 10px 15px;
		flex-shrink: 0; /* 防止搜索区被压缩 */
	}

	/* 树形控件区域 (核心：垂直滚动) */
	.panel-tree {
		flex: 1; /* 占据剩余空间 */
		overflow-y: auto; /* 垂直方向溢出滚动 */
		background-color: #fff;
		min-height: 0; /* 关键：解决flex子元素溢出滚动问题 */
	}

	/* 自定义树节点内容样式 */
	.custom-tree-node {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-size: 14px;
		padding-right: 8px;
	}

	/* 底部样式 */
	.panel-footer {
		padding: 12px;
		border-top: 1px solid #ebeef5;
		background-color: #fafafa;
		flex-shrink: 0; /* 防止底部被压缩 */
	}
</style>
