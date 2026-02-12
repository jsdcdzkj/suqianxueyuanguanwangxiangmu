<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="100px" label-position="top">
		<ElRow :gutter="20">
			<ElCol :span="8">
				<ElFormItem label="角色名称" prop="roleName">
					<ElInput v-model="form.roleName" placeholder="请输入角色名称" />
				</ElFormItem>
			</ElCol>

			<ElCol :span="8">
				<ElFormItem label="角色标志" prop="roleFlag">
					<ElInput v-model="form.roleFlag" placeholder="请输入角色标志" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="所属系统" prop="systemId">
					<ElSelect v-model="form.systemId" placeholder="请选择所属系统" @change="handleSystemChange">
						<template v-for="option in sysOptions" :key="option.value">
							<ElOption :label="option.label" :value="option.value"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="菜单权限" prop="menuIds">
					<div class="tree-container">
						<ElTree
							ref="treeRef"
							:data="treeData"
							:props="treeProps"
							show-checkbox
							node-key="id"
							:default-expanded-keys="defaultExpandedKeys"
							:default-checked-keys="defaultCheckedKeys"
						/>
					</div>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="备注" prop="memo">
					<ElInput type="textarea" v-model="form.memo" :rows="3" placeholder="请输入备注" />
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive, computed, watch } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElTree, ElRow, ElCol, ElMessage } from "element-plus";
	import { getMenuTree } from "@/api/setting/menu";
	import { doEdit } from "@/api/setting/role";
	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		systematicType: {
			type: Array,
			default: () => []
		}
	});
	const form = reactive({
		roleName: "",
		roleFlag: "",
		systemId: "",
		menuIds: "",
		memo: ""
	});

	// 添加树形组件引用
	const treeRef = ref(null);

	// 默认展开的节点
	const defaultExpandedKeys = ref([]);

	// 默认选中的节点
	const defaultCheckedKeys = ref([]);

	const sysOptions = computed(() => props.systematicType);

	// 添加树形数据
	const treeData = ref([]);
	const treeProps = {
		children: "children",
		label: "title",
		value: "id"
	};

	const handleSystemChange = async (value: string) => {
		if (value) {
			try {
				const res = await getMenuTree({
					systemId: value
				});
				treeData.value = res;
				// 清空已选中的菜单权限
				defaultCheckedKeys.value = [];
			} catch (error) {
				console.error("获取菜单树失败:", error);
				treeData.value = [];
				defaultCheckedKeys.value = [];
			}
		} else {
			treeData.value = [];
			defaultCheckedKeys.value = [];
		}
	};
	const rules = reactive({
		roleName: [{ required: true, message: "请输入角色名称", trigger: "blur" }],
		roleFlag: [{ required: true, message: "请输入角色标志", trigger: "blur" }],
		systemId: [{ required: true, message: "请选择所属系统", trigger: "blur" }],
		menuIds: [
			{
				required: true,
				message: "请选择菜单权限",
				trigger: "change", // 改为change触发
				validator: (rule, value, callback) => {
					if (!treeRef.value) {
						callback(new Error("请选择菜单权限"));
						return;
					}
					const checkedKeys = treeRef.value.getCheckedKeys();
					const halfCheckedKeys = treeRef.value.getHalfCheckedKeys();
					if (checkedKeys.length === 0 && halfCheckedKeys.length === 0) {
						callback(new Error("请选择菜单权限"));
					} else {
						callback();
					}
				}
			}
		],
		memo: [{ required: true, message: "请输入备注", trigger: "blur" }]
	});

	const { registerFormDone, formInstance } = useDialogForm();

	// 监听systemId的变化，确保在系统切换后重新加载菜单
	watch(
		() => form.systemId,
		async (newVal) => {
			if (newVal) {
				await handleSystemChange(newVal);
				// 如果是编辑模式，设置已选中的菜单权限
				if (props.rowInfo?.id && props.rowInfo.menuIds) {
					defaultCheckedKeys.value = Array.isArray(props.rowInfo.menuIds)
						? props.rowInfo.menuIds
						: [props.rowInfo.menuIds];
				}
			}
		}
	);

	// 编辑时的数据初始化
	if (props.rowInfo.id) {
		Object.assign(form, {
			...props.rowInfo,
			systemId: String(props.rowInfo.systemId), // 确保是字符串类型
			menuIds: Array.isArray(props.rowInfo.menuIds)
				? props.rowInfo.menuIds
				: props.rowInfo.menuIds
					? [props.rowInfo.menuIds]
					: []
		});
	}

	registerFormDone(async () => {
		// 先进行表单验证
		try {
			await formInstance.value.validate();
		} catch (error) {
			return false;
		}

		const submitData = { ...form };
		if (props.rowInfo.id) {
			submitData.id = props.rowInfo.id;
		}

		// 获取选中的节点
		if (treeRef.value) {
			const checkedKeys = treeRef.value.getCheckedKeys();
			const halfCheckedKeys = treeRef.value.getHalfCheckedKeys();
			submitData.menuIds = [...checkedKeys, ...halfCheckedKeys];
		}

		try {
			await doEdit(submitData);
			return true;
		} catch (error) {
			console.error("保存角色失败:", error);
			return false;
		}
	});
</script>

<style scoped lang="scss">
	.tree-container {
		width: 100%;
		padding: 10px;
		border: 1px solid var(--el-border-color);
		border-radius: 4px;
		height: 336px;
		overflow-y: auto;
	}
</style>
