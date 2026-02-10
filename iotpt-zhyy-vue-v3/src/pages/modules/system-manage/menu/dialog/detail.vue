<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="100px" label-position="top">
		<ElRow :gutter="20">
			<ElCol :span="8">
				<ElFormItem label="菜单名称" prop="title">
					<ElInput v-model="form.title" placeholder="请输入菜单名称" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- 单类型 1:菜单 2:按钮 -->
				<ElFormItem label="菜单类型" prop="menuType">
					<ElSelect v-model="form.menuType" placeholder="请选择菜单类型">
						<template v-for="option in menuTypeOptions" :key="option.value">
							<ElOption :label="option.label" :value="`${option.value}`"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- 所属系统 -->
				<ElFormItem label="所属系统" prop="systemId">
					<ElSelect v-model="form.systemId" placeholder="请选择所属系统">
						<template v-for="option in systemOptions" :key="option.value">
							<ElOption :label="option.label" :value="`${option.value}`"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- 父级菜单 el-tree-select -->
				<ElFormItem label="父级菜单" prop="parentId">
					<ElTreeSelect
						v-model="form.parentId"
						placeholder="请选择父级菜单"
						:data="treeData"
						:props="treeProps"
						filterable
						:filter-node-method="filterNode"
						clearable
						:check-strictly="true"
						:render-after-expand="false"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- 路由名称 -->
				<ElFormItem label="路由名称" prop="routerUrl">
					<ElInput v-model="form.routerUrl" placeholder="请输入路由名称" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- 路由链接 -->
				<ElFormItem label="路由链接" prop="routerUrl">
					<ElInput v-model="form.routerUrl" placeholder="请输入路由链接" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- VUR文件路径 -->
				<ElFormItem label="VUR文件路径" prop="vueUrl">
					<ElInput v-model="form.vueUrl" placeholder="请输入VUR文件路径" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- 重定向类型 -->
				<ElFormItem label="重定向类型" prop="redirectType">
					<ElInput v-model="form.redirectType" placeholder="请输入重定向类型" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- 图标 -->
				<ElFormItem label="图标" prop="icon" v-if="form.menuType == 1">
					<IconSelect v-model:model-value="form.icon" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- 排序 -->
				<ElFormItem label="排序" prop="sort">
					<ElInput v-model="form.sort" placeholder="请输入排序" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<!-- 是否显示 0：显示 1：不显示 -->
				<ElFormItem label="是否显示" prop="isFlag">
					<!-- 使用radio -->
					<ElRadioGroup v-model="form.isFlag">
						<ElRadioButton :value="0">显示</ElRadioButton>
						<ElRadioButton :value="1">隐藏</ElRadioButton>
					</ElRadioGroup>
				</ElFormItem>
			</ElCol>
		</ElRow>

		<!-- <ElFormItem label="是否外链" prop="isOutsidechain">
			<ElRadioGroup v-model="form.isOutsidechain">
				<ElRadioButton value="1">是</ElRadioButton>
				<ElRadioButton value="0">否</ElRadioButton>
			</ElRadioGroup>
		</ElFormItem>

		<ElFormItem label="权限标识" prop="menuCode">
			<ElInput v-model="form.menuCode" placeholder="请输入权限标识" />
		</ElFormItem>

		<ElFormItem label="是否缓存" prop="isCache">
			<ElRadioGroup v-model="form.isCache">
				<ElRadioButton :value="1">缓存</ElRadioButton>
				<ElRadioButton :value="0">不缓存</ElRadioButton>
			</ElRadioGroup>
		</ElFormItem> -->
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import {
		ElForm,
		ElFormItem,
		ElInput,
		ElTree,
		ElTreeSelect,
		ElRadioGroup,
		ElRadioButton,
		ElSelect,
		ElOption,
		ElRow,
		ElCol
	} from "element-plus";

	import { number } from "vue-types";
	import { toRaw } from "vue";
	import { getMenuTree, saveMenu } from "@/api/setting/menu";
	import IconSelect from "@/components/icon/IconSelect";

	const form = reactive({
		parentId: "",
		menuType: "1",
		systemId: "1",
		parentId: "",
		icon: "",
		title: "",
		sort: "",
		isOutsidechain: "0",
		routerUrl: "",
		vueUrl: "",
		redirectType: "",
		menuCode: "",
		isFlag: 0,
		status: 0,
		isCache: 0
	});
	const menuTypeOptions = [
		{ label: "菜单", value: 1 },
		{ label: "按钮", value: 2 }
	];
	const systemOptions = [
		{ label: "系统1", value: 1 },
		{ label: "系统2", value: 2 }
	];

	const props = defineProps({
		// id: number().def()
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const rules = reactive({
		menuType: [
			{
				required: true,
				message: "请选择菜单类型",
				trigger: "blur"
			}
		],
		systemId: [
			{
				required: true,
				message: "请选择系统",
				trigger: "blur"
			}
		],
		icon: [
			{
				required: true,
				message: "请选择图标",
				trigger: "blur"
			}
		],
		title: [
			{
				required: true,
				message: "请输入菜单名称",
				trigger: "blur"
			}
		],
		sort: [
			{
				required: true,
				message: "请输入排序",
				trigger: "blur"
			}
		],
		routerUrl: [
			{
				required: true,
				message: "请输入路径名称",
				trigger: "blur"
			}
		],
		vueUrl: [
			{
				required: true,
				message: "请输入菜单路径",
				trigger: "blur"
			}
		],
		menuCode: [
			{
				required: true,
				message: "请输入权限标识",
				trigger: "blur"
			}
		],
		isFlag: [
			{
				required: true,
				message: "请选择状态",
				trigger: "blur"
			}
		]
	});

	const { registerFormDone, formInstance } = useDialogForm();
	const tree = ref<InstanceType<typeof ElTree>>();

	const treeData = ref<any[]>([]);
	const treeProps = {
		children: "children",
		label: "title",
		value: "id"
	};
	const filterNode = (value: string, data: any) => {
		if (!value) return true;
		// 检查当前节点的 title 是否包含搜索值
		if (data.title && data.title.includes(value)) {
			return true;
		}
		// 递归检查子节点
		if (data.children && data.children.length > 0) {
			for (let i = 0; i < data.children.length; i++) {
				if (filterNode(value, data.children[i])) {
					return true;
				}
			}
		}
		return false;
	};
	if (props.rowInfo.id) {
		for (const key in form) {
			// 修改部分
			if (key in form && key in props.rowInfo) {
				if (key === "parentId" && props.rowInfo[key] === "-1") {
					// 若 key 是 parentId 且值为 "-1"，则不赋值
					continue;
				}
				form[key] = props.rowInfo[key];
			}
		}
	} else if (props.rowInfo.parentId && props.rowInfo.parentId != "-1") {
		form.parentId = props.rowInfo.parentId;
	}
	onMounted(async () => {
		const res = await getMenuTree({});
		treeData.value = res;
	});
	// registerFormDone(async () => {
	// 	await saveMenu(Object.assign({}, toRaw(form), props.rowInfo ? { id: props.rowInfo.id } : {}));
	// 	return true;
	// });
	registerFormDone(async () => {
		let submitData = { ...toRaw(form) };

		if (submitData.menuType === 1) {
			// 当菜单类型为 1（菜单）时，清除按钮标识
			submitData.buttonSign = "";
		} else if (submitData.menuType === 2) {
			// 当菜单类型为 2（按钮）时，清除与菜单相关的字段
			const menuFields = ["title", "routerUrl", "vueUrl", "redirectType", "title"];
			menuFields.forEach((field) => {
				submitData[field] = "";
			});
		}
		console.log("9999999999999submitData", submitData);
		submitData = {
			...submitData,
			parentId: submitData.parentId ? submitData.parentId : "-1",
			id: props.rowInfo.id
		};
		submitData.parentId = submitData.parentId;

		await saveMenu(submitData);
		return true;
	});
</script>
