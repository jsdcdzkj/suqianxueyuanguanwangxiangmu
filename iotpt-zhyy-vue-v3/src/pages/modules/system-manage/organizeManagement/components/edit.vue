<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-position="top" label-width="116px">
		<ElRow :gutter="20">
			<ElCol :span="8">
				<ElFormItem label="部门名称" prop="deptName">
					<ElInput v-model.trim="form.deptName" placeholder="请输入部门名称" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="负责人" prop="manager">
					<ElInput v-model.trim="form.manager" placeholder="请输入负责人" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="所属区域" prop="defaultCascaderValue">
					<ElCascader
						ref="cascaderRef"
						v-model="form.defaultCascaderValue"
						:options="options"
						:props="cascaderProps"
						placeholder="请选择所属区域"
						clearable
						@change="handleCascaderChange"
						style="width: 100%"
					/>
				</ElFormItem>
			</ElCol>

			<ElCol :span="8">
				<ElFormItem label="电话号码" prop="phone">
					<ElInput v-model.trim="form.phone" placeholder="请输入电话号码" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="手机号码" prop="telephone">
					<ElInput v-model.trim="form.telephone" placeholder="请输入手机号码" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="邮箱地址" prop="email">
					<ElInput v-model.trim="form.email" placeholder="请输入邮箱地址" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="部门描述">
					<ElInput v-model.trim="form.memo" type="textarea" :rows="4" placeholder="请输入部门描述" />
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="tsx">
	import { ref, reactive, watch } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElForm, ElFormItem, ElInput, ElCascader, ElMessage, ElRow, ElCol } from "element-plus";
	import { addOrgDept, info, updateOrgDept } from "@/api/setting/dept";
	import { areaTreeList } from "@/api/setting/org";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		orgId: {
			type: Number,
			default: ""
		}
	});

	const form = reactive({
		deptName: "",
		memo: "",
		ids: "",
		orgId: "",
		id: "",
		defaultCascaderValue: [],
		manager: "",
		phone: "",
		telephone: "",
		email: ""
	});
	const cascaderRef = ref(null);

	const rules = reactive({
		deptName: [{ required: true, trigger: "blur", message: "请输入部门名称" }],
		defaultCascaderValue: [
			{
				required: true,
				trigger: "submit",
				message: "请选择区域",
				type: "array"
			}
		]
	});

	const options = ref([]);
	const cascaderProps = {
		multiple: true,
		checkStrictly: true,
		emitPath: false,
		value: "value",
		label: "label",
		children: "children"
	};

	const { registerFormDone, formInstance } = useDialogForm();

	// 获取区域树
	const getAreaTree = async () => {
		try {
			const response = await areaTreeList();
			options.value = Array.isArray(response) ? response : [];
		} catch (error) {
			ElMessage.error("获取区域树失败");
		}
	};

	// 处理级联选择器变化
	const handleCascaderChange = (value) => {
		form.ids = "";
		// 获取选中的节点
		if (cascaderRef.value) {
			const checkedNodes = cascaderRef.value.getCheckedNodes(true) || [];
			const checkedIds = [];
			if (checkedNodes) {
				for (const node of checkedNodes) {
					if (node.level === 3) {
						checkedIds.push(node.value);
					}
				}
			}
			form.ids = checkedIds.join(",");
		}
	};

	// if (props.rowInfo) {
	// 	const res = await info({ id: props.rowInfo.id });
	// 	Object.assign(form, res);
	// }

	// if (props.orgId) {
	// 	form.orgId = props.orgId;
	// }
	if (props.rowInfo.id) {
		info(props.rowInfo.id).then((res) => {
			Object.assign(form, res);
			if (res.showData) {
				const thirdLevelIds = [];
				res.showData.forEach((item) => {
					if (Array.isArray(item) && item.length === 3) {
						// item[2] 是第三层的值
						thirdLevelIds.push(item[2]);
					}
				});
				form.defaultCascaderValue = thirdLevelIds;
			}
		});
	}
	if (props.orgId) {
		form.orgId = props.orgId;
	}
	// 初始化数据
	const initData = async () => {
		await getAreaTree();
	};

	// 初始化时调用
	initData();

	// 表单提交处理
	registerFormDone(async () => {
		// 获取选中的区域ID
		const checkedNodes = cascaderRef.value?.getCheckedNodes(true);

		const checkedIds = [];
		if (checkedNodes) {
			for (const node of checkedNodes) {
				if (node.level === 3) {
					checkedIds.push(node.value);
				}
			}
		}

		if (checkedIds.length === 0) {
			ElMessage.error("请选择所属区域");
			return false;
		}

		const submitData = {
			...form,
			ids: checkedIds.join(",")
		};

		if (props.rowInfo.id) {
			submitData.id = props.rowInfo.id;
		}

		if (props.rowInfo.id) {
			await updateOrgDept(submitData);
		} else {
			await addOrgDept(submitData);
		}

		return true;
	});
</script>

<style lang="scss">
	.el-cascader-panel {
		.el-scrollbar.el-cascader-menu:first-child {
			.el-checkbox {
				display: none !important;
			}
		}
		.el-scrollbar.el-cascader-menu:nth-child(2) {
			.el-checkbox {
				display: none !important;
			}
		}
	}
</style>
