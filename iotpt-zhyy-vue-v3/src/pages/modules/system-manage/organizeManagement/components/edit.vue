<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="116px">
		<ElFormItem label="部门名称" prop="deptName">
			<ElInput v-model.trim="form.deptName" placeholder="请输入部门名称" />
		</ElFormItem>
		<ElFormItem label="所属区域" prop="defaultCascaderValue">
			<ElCascader
				v-model="form.defaultCascaderValue"
				:options="options"
				:props="cascaderProps"
				placeholder="请选择所属区域"
				clearable
				@change="handleCascaderChange"
			/>
		</ElFormItem>
		<ElFormItem label="部门描述">
			<ElInput v-model.trim="form.memo" type="textarea" rows="4" placeholder="请输入部门描述" />
		</ElFormItem>
		<ElFormItem label="第三方访客区域配置">
			<ElSelect v-model="form.visitorAreaIds" multiple placeholder="请选择访客区域" class="w-full">
				<ElOption v-for="item in visitorList" :key="item.id" :label="item.areaName" :value="item.id" />
			</ElSelect>
		</ElFormItem>
	</ElForm>
</template>

<script setup lang="tsx">
	import { ref, reactive, watch } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElCascader, ElMessage } from "element-plus";
	import { addOrgDept, info, updateOrgDept } from "@/api/setting/dept";
	import { areaTreeList } from "@/api/setting/org";
	import { visitorRegionList } from "@/api/setting/dept";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const form = reactive({
		deptName: "",
		memo: "",
		ids: "",
		orgId: "",
		id: "",
		defaultCascaderValue: [],
		visitorAreaIds: [],
		dahuaDeptId: ""
	});

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
	const visitorList = ref([]);
	const cascaderProps = {
		multiple: true
	};

	const { registerFormDone, formInstance } = useDialogForm();

	// 获取区域树
	const getAreaTree = async () => {
		try {
			const response = await areaTreeList();
			options.value = Array.isArray(response.data) ? response.data : [];
		} catch (error) {
			ElMessage.error("获取区域树失败");
		}
	};

	// 获取访客区域列表
	const getVisitorList = async () => {
		try {
			const response = await visitorRegionList();
			visitorList.value = Array.isArray(response.data) ? response.data : [];
		} catch (error) {
			ElMessage.error("获取访客区域列表失败");
		}
	};

	// 处理级联选择器变化
	const handleCascaderChange = () => {
		form.ids = "";
	};

	// 监听rowInfo变化
	watch(
		() => props.rowInfo,
		async (newVal) => {
			await getVisitorList();
			if (newVal.id) {
				try {
					const response = await info(newVal.id);
					if (response.code == 0) {
						Object.assign(form, response.data);
					}
				} catch (error) {
					ElMessage.error("获取部门信息失败");
				}
			} else {
				Object.assign(form, {
					deptName: "",
					memo: "",
					ids: "",
					orgId: "",
					id: "",
					defaultCascaderValue: [],
					visitorAreaIds: [],
					dahuaDeptId: ""
				});
			}
		},
		{ immediate: true }
	);

	// 初始化数据
	const initData = async () => {
		await getAreaTree();
	};

	// 初始化时调用
	initData();

	// 表单提交处理
	registerFormDone(async () => {
		try {
			await formInstance.value.validate();

			// 获取选中的区域ID
			const checkedNodes = formInstance.value.$refs.cascader.getCheckedNodes(true);
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

			let response;
			if (props.rowInfo.id) {
				response = await updateOrgDept(submitData);
			} else {
				response = await addOrgDept(submitData);
			}

			if (response.code == 0) {
				ElMessage.success("操作成功");
				return true;
			}
		} catch (error) {
			ElMessage.error("操作失败");
			return false;
		}
	});
</script>

<style scoped>
	.w-full {
		width: 100%;
	}

	:deep(.el-cascader-panel) {
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
