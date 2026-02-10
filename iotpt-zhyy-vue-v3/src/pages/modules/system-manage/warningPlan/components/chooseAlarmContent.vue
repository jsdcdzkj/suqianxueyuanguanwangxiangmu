<template>
	<ElForm ref="formInstance" :model="queryForm" :rules="rules">
		<div>
			<ElFormItem prop="deviceName">
				<ElInput
					v-model="queryForm.deviceName"
					placeholder="搜索告警内容"
					:suffix-icon="Search"
					@input="handleSearch"
				/>
			</ElFormItem>
			<div class="alarm-wrap">
				<div class="alarm-title">
					<div class="alarm-items fixed-width-column">
						<div class="title">告警分组</div>
					</div>
					<div class="alarm-items fixed-width-column">
						<div class="title">告警分类</div>
					</div>
					<div class="alarm-items fixed-width-column">
						<div class="title">告警内容</div>
					</div>
				</div>
				<div class="check-con">
					<ElCascaderPanel
						ref="cascaderPanel"
						:options="filteredOptions"
						v-model="checkList"
						:props="cascaderProps"
						:default-expanded-keys="defaultExpandedKeys"
						@change="handleChange"
					>
						<template #default="{ node, data }">
							<span>{{ data.name || "未找到名称" }}</span>
							<span v-if="!node.isLeaf">({{ data.children?.length }})</span>
						</template>
					</ElCascaderPanel>
				</div>
			</div>
		</div>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, reactive, watch, nextTick } from "vue";
	import { Search } from "@element-plus/icons-vue";
	import { ElMessage, ElForm, ElFormItem, ElInput, ElCascaderPanel } from "element-plus";
	import { alarmContentTree } from "@/api/setting/warnInfo";
	import { useDialogForm } from "@/core/dialog/dialog-container";

	const props = defineProps({
		preSelectedIds: {
			type: Array,
			default: () => []
		},
		editId: {
			type: [String, Number],
			default: null
		}
	});

	const { registerFormDone, formInstance } = useDialogForm();

	const loading = ref(false);
	const disabled = ref(false);
	const title = ref("选择告警内容");
	const entityInfo = ref({});
	const total = ref(10);
	const listLoading = ref(false);
	const elementLoadingText = ref("正在加载...");
	const list = ref([]);
	const checkList = ref([]);
	const queryForm = reactive({
		deviceName: ""
	});
	const rules = reactive({
		deviceName: [{ required: false, message: "请输入搜索内容", trigger: "blur" }]
	});
	const defaultExpandedKeys = ref([]);
	const cascaderProps = {
		multiple: true,
		expandTrigger: "hover",
		value: "id",
		label: "name",
		children: "children",
		disabled: "disable",
		checkStrictly: false
	};

	const options = ref([]);
	const filteredOptions = ref([]);
	const lastLevelIds = ref([]);
	const cascaderPanel = ref(null);

	const getAlarmGroupTree = async () => {
		try {
			let res;
			if (props.editId) {
				res = await alarmContentTree({ planId: props.editId, disable: 1 });
			} else {
				res = await alarmContentTree({ disable: 1 });
			}
			options.value = res;
			filteredOptions.value = [...options.value];
			nextTick(() => {
				expandSecondAndThirdLevel();
			});
		} catch (error) {
			console.error("获取告警分组树失败:", error);
		}
	};

	const setCascaderValue = (selectedIds) => {
		if (!options.value.length) return;
		const paths = [];
		const findPath = (nodes, targetId, currentPath = []) => {
			if (!nodes || !Array.isArray(nodes)) return null;
			for (const node of nodes) {
				const newPath = [...currentPath, node[cascaderProps.value]];
				if (node[cascaderProps.value] === targetId) {
					return newPath;
				}
				if (node.children && node.children.length > 0) {
					const result = findPath(node.children, targetId, newPath);
					if (result) return result;
				}
			}
			return null;
		};

		selectedIds.forEach((id) => {
			const path = findPath(options.value, id);
			if (path) {
				paths.push(path);
			}
		});

		nextTick(() => {
			checkList.value = [...paths];
		});
	};

	const handleChange = (values) => {
		const ids = values
			.map((item) => {
				if (item && item.length > 0) {
					return item[item.length - 1];
				}
				return null;
			})
			.filter((item) => item !== null);
		lastLevelIds.value = ids;
	};

	const handleSearch = () => {
		const keyword = queryForm.deviceName.toLowerCase();
		if (!keyword) {
			filteredOptions.value = [...options.value];
			defaultExpandedKeys.value = [];
			expandSecondAndThirdLevel();
			return;
		}

		const matchedKeys = [];
		const filterOptions = (nodes) => {
			if (!nodes || !Array.isArray(nodes)) return [];
			return nodes.reduce((result, node) => {
				if (!node.children || node.children.length === 0) {
					const hasMatch = node[cascaderProps.label].toLowerCase().includes(keyword);
					if (hasMatch) {
						matchedKeys.push(node[cascaderProps.value]);
						result.push(node);
					}
				} else {
					const children = filterOptions(node.children);
					if (children.length > 0) {
						const newNode = { ...node, children };
						result.push(newNode);
					}
				}
				return result;
			}, []);
		};

		filteredOptions.value = filterOptions(options.value);
		findParentPathsAndExpand(options.value, matchedKeys);
	};

	const findParentPathsAndExpand = (nodes, matchedKeys, parentKeys = []) => {
		if (!nodes || !Array.isArray(nodes)) return;
		nodes.forEach((node) => {
			const currentKeys = [...parentKeys, node[cascaderProps.value]];
			if (!node.children || node.children.length === 0) {
				if (matchedKeys.includes(node[cascaderProps.value])) {
					defaultExpandedKeys.value = [...new Set([...defaultExpandedKeys.value, ...currentKeys])];
				}
			} else {
				findParentPathsAndExpand(node.children, matchedKeys, currentKeys);
			}
		});
	};

	const expandSecondAndThirdLevel = () => {
		const expandedKeys = [];
		const traverseNodes = (nodes, level = 1) => {
			if (!nodes || !Array.isArray(nodes)) return;
			nodes.forEach((node) => {
				if (level >= 1 && node.children && node.children.length > 0) {
					expandedKeys.push(node[cascaderProps.value]);
				}
				if (node.children && node.children.length > 0) {
					traverseNodes(node.children, level + 1);
				}
			});
		};

		traverseNodes(filteredOptions.value);
		defaultExpandedKeys.value = [...new Set([...defaultExpandedKeys.value, ...expandedKeys])];
	};

	// 初始化数据
	if (props.preSelectedIds && props.preSelectedIds.length > 0) {
		nextTick(() => {
			setCascaderValue(props.preSelectedIds);
		});
	}

	registerFormDone(async () => {
		try {
			if (!lastLevelIds.value || lastLevelIds.value.length === 0) {
				ElMessage.warning("请选择告警内容");
				return false;
			}

			// 构建完整的数据结构
			const selectedItems = [];
			const findItem = (nodes, id) => {
				for (const node of nodes) {
					if (node.id === id) {
						return node;
					}
					if (node.children) {
						const found = findItem(node.children, id);
						if (found) return found;
					}
				}
				return null;
			};

			lastLevelIds.value.forEach((id) => {
				const item = findItem(options.value, id);
				if (item) {
					selectedItems.push({
						id: item.id,
						label: item.name
						// 可以根据需要添加其他字段
					});
				}
			});

			return selectedItems;
		} catch (error) {
			console.error("提交失败:", error);
			ElMessage.error("提交失败，请检查输入信息");
			return false;
		}
	});

	watch(
		() => props.preSelectedIds,
		(newIds) => {
			setCascaderValue(newIds);
		},
		{ immediate: true }
	);

	watch(
		options,
		() => {
			nextTick(() => {
				setCascaderValue(props.preSelectedIds);
			});
		},
		{ immediate: true }
	);

	// 初始化
	getAlarmGroupTree();
</script>

<style scoped lang="scss">
	:deep(.el-cascader-menu) {
		width: 280px !important;
	}

	.alarm-wrap {
		border: 1px solid rgba(0, 0, 0, 0.15);
		margin-top: 20px;

		.alarm-title {
			display: flex;

			.fixed-width-column {
				width: 280px;
			}

			.alarm-items {
				.title {
					height: 40px;
					border-right: 1px solid rgba(0, 0, 0, 0.06);
					border-bottom: 1px solid rgba(0, 0, 0, 0.06);
					position: relative;
					padding-left: 28px;
					display: flex;
					align-items: center;

					&::before {
						content: "";
						position: absolute;
						width: 4px;
						height: 16px;
						background: #1e95f4;
						top: 12px;
						left: 16px;
					}
				}
			}
		}

		.check-con {
			width: 100%;
			height: 500px;
			overflow-y: auto;
		}
	}

	.checkboxList {
		display: flex;
		flex-direction: column;
		padding: 16px;

		:deep(.el-checkbox__label) {
			line-height: 32px;
		}
	}
</style>
