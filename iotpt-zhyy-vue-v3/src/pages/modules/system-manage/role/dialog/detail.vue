<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="100px" label-position="top">
		<ElRow :gutter="20">
			<ElCol :span="8">
				<ElFormItem label="角色名称" prop="userName">
					<ElInput v-model="form.userName" placeholder="请输入角色名称" />
				</ElFormItem>
			</ElCol>

			<ElCol :span="8">
				<ElFormItem label="角色标志" prop="loginName">
					<ElInput v-model="form.loginName" placeholder="请输入角色标志" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="所属系统" prop="roleId">
					<ElSelect v-model="form.roleId" placeholder="请选择所属系统">
						<template v-for="option in roleOptions" :key="option.value">
							<ElOption :label="option.label" :value="option.value"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="菜单权限" prop="unitId">
					<div class="tree-container">
						<ElTree
							ref="treeRef"
							:data="unitOptions"
							:props="treeProps"
							show-checkbox
							node-key="value"
							:default-expanded-keys="defaultExpandedKeys"
							:default-checked-keys="defaultCheckedKeys"
						/>
					</div>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="备注" prop="remark">
					<ElInput type="textarea" v-model="form.remark" :rows="3" placeholder="请输入备注" />
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElTree, ElRow, ElCol, ElMessage } from "element-plus";

	const form = reactive({
		userName: "",
		phone: "",
		loginName: "",
		roleId: "",
		password: "",
		status: 1,
		unitId: "",
		deptId: "",
		remark: "",
		avatar: ""
	});

	// 添加树形组件引用
	const treeRef = ref(null);

	// 默认展开的节点
	const defaultExpandedKeys = ref([]);

	// 默认选中的节点
	const defaultCheckedKeys = ref([]);

	const roleOptions = [
		{ label: "管理员", value: "1" },
		{ label: "普通用户", value: "2" }
	];

	const unitOptions = [
		{
			label: "系统管理",
			value: "1",
			children: [
				{
					label: "用户管理",
					value: "1-1",
					children: [
						{ label: "用户查询", value: "1-1-1" },
						{ label: "用户新增", value: "1-1-2" },
						{ label: "用户修改", value: "1-1-3" }
					]
				},
				{
					label: "角色管理",
					value: "1-2",
					children: [
						{ label: "角色查询", value: "1-2-1" },
						{ label: "角色新增", value: "1-2-2" }
					]
				}
			]
		},
		{
			label: "业务管理",
			value: "2",
			children: [
				{
					label: "订单管理",
					value: "2-1",
					children: [
						{ label: "订单查询", value: "2-1-1" },
						{ label: "订单创建", value: "2-1-2" }
					]
				}
			]
		}
	];

	const deptOptions = [
		{ label: "部门1", value: "1" },
		{ label: "部门2", value: "2" }
	];

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const rules = reactive({
		userName: [{ required: true, message: "请输入用户名称", trigger: "blur" }],
		loginName: [{ required: true, message: "请输入登录名称", trigger: "blur" }],
		roleId: [{ required: true, message: "请选择用户角色", trigger: "blur" }],
		password: [{ required: true, message: "请输入用户密码", trigger: "blur" }],
		status: [{ required: true, message: "请选择用户状态", trigger: "blur" }],
		unitId: [{ required: true, message: "请选择所属单位", trigger: "blur" }],
		deptId: [{ required: true, message: "请选择所属部门", trigger: "blur" }]
	});

	const { registerFormDone, formInstance } = useDialogForm();

	if (props.rowInfo.id) {
		for (const key in form) {
			if (key in form && key in props.rowInfo) {
				form[key] = props.rowInfo[key];
			}
		}
		// 设置默认选中的菜单权限
		if (props.rowInfo.unitId) {
			defaultCheckedKeys.value = Array.isArray(props.rowInfo.unitId)
				? props.rowInfo.unitId
				: [props.rowInfo.unitId];
		}
	}

	registerFormDone(async () => {
		const submitData = { ...form };
		if (props.rowInfo.id) {
			submitData.id = props.rowInfo.id;
		}
		// 获取选中的节点
		if (treeRef.value) {
			submitData.unitId = treeRef.value.getCheckedKeys();
		}
		// TODO: 调用保存用户信息的API
		return true;
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
