<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="116px">
		<ElFormItem label="部门名称" prop="areaName">
			<ElInput v-model.trim="form.areaName" placeholder="请输入部门名称" />
		</ElFormItem>
		<ElFormItem label="区域类型" prop="areaType">
			<ElSelect v-model="form.areaType" placeholder="请选择区域类型" class="w-full">
				<ElOption
					v-for="item in areaTypeList"
					:key="item.dictValue"
					:label="item.dictLabel"
					:value="item.dictValue"
				/>
			</ElSelect>
		</ElFormItem>
		<ElFormItem label="所属楼宇" prop="buildFloorId">
			<ElSelect
				v-model="form.buildFloorId"
				filterable
				placeholder="请选择所属楼宇"
				class="w-full"
				@change="getFloor"
			>
				<ElOption v-for="item in options2" :key="item.id" :label="item.buildName" :value="item.id" />
			</ElSelect>
		</ElFormItem>
		<ElFormItem label="所属楼层" prop="floorId">
			<ElSelect v-model="form.floorId" filterable placeholder="请选择所属楼层" class="w-full">
				<ElOption v-for="item in options" :key="item.id" :label="item.floorName" :value="item.id" />
			</ElSelect>
		</ElFormItem>
		<ElFormItem label="是否对gis展示" prop="isLargeScreenDisplay">
			<ElRadioGroup v-model="form.isLargeScreenDisplay">
				<ElRadio :label="1">是</ElRadio>
				<ElRadio :label="0">否</ElRadio>
			</ElRadioGroup>
		</ElFormItem>
		<ElFormItem label="区域排序" prop="sort">
			<ElInput v-model.trim="form.sort" placeholder="请输入区域序号" />
		</ElFormItem>
		<ElFormItem label="区域描述">
			<ElInput v-model.trim="form.memo" rows="4" type="textarea" placeholder="请输入区域描述" />
		</ElFormItem>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, reactive, watch } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElRadioGroup, ElRadio, ElMessage } from "element-plus";
	import {
		getRedisDictList,
		allBuildFloolList,
		updateBuildArea,
		addBuildArea,
		getAreaInfo
	} from "@/api/setting/area";
	import { allBuildList } from "@/api/setting/floor";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const form = reactive({
		floorId: "",
		areaName: "",
		buildFloorId: "",
		memo: "",
		areaType: "",
		id: "",
		isLargeScreenDisplay: 0,
		sort: ""
	});

	const rules = reactive({
		areaName: [{ required: true, trigger: "blur", message: "请输入部门名称" }],
		buildFloorId: [{ required: true, trigger: "blur", message: "请选择所属楼宇" }],
		floorId: [{ required: true, trigger: "blur", message: "请选择所属楼层" }],
		areaType: [{ required: true, trigger: "blur", message: "请选择区域类型" }],
		isLargeScreenDisplay: [{ required: true, trigger: "blur", message: "请选择是否对gis展示" }],
		sort: [{ required: true, trigger: "blur", message: "请输入区域排序" }]
	});

	const options = ref([]);
	const options2 = ref([]);
	const areaTypeList = ref([]);

	const { registerFormDone, formInstance } = useDialogForm();

	// 获取区域类型列表
	const getAreaTypeList = async () => {
		try {
			const { data } = await getRedisDictList({ dictType: "area_type" });
			areaTypeList.value = data;
		} catch (error) {
			ElMessage.error("获取区域类型失败");
		}
	};

	// 获取楼层列表
	const getFloor = async () => {
		form.floorId = "";
		try {
			const res = await allBuildFloolList(form.buildFloorId);
			if (res.code == 0) {
				options.value = res.data;
			}
		} catch (error) {
			ElMessage.error("获取楼层列表失败");
		}
	};

	// 获取楼宇列表
	const getBuils = async () => {
		try {
			const res = await allBuildList();
			if (res.code == 0) {
				options2.value = res.data;
			}
		} catch (error) {
			ElMessage.error("获取楼宇列表失败");
		}
	};

	// 监听rowInfo变化
	watch(
		() => props.rowInfo,
		async (newVal) => {
			if (newVal.id) {
				try {
					const res = await getAreaInfo(newVal.id);
					if (res.code == 0) {
						Object.assign(form, res.data);
						await getFloor();
						if (form.isLargeScreenDisplay == null) {
							form.isLargeScreenDisplay = 0;
						}
					}
				} catch (error) {
					ElMessage.error("获取区域信息失败");
				}
			} else {
				Object.assign(form, {
					floorId: "",
					areaName: "",
					buildFloorId: "",
					memo: "",
					areaType: "",
					id: "",
					isLargeScreenDisplay: 0,
					sort: ""
				});
				if (newVal.buildId) {
					form.buildFloorId = newVal.buildId;
					await getFloor();
					form.floorId = newVal.floorId;
				}
			}
		},
		{ immediate: true }
	);

	// 初始化数据
	const initData = async () => {
		await getAreaTypeList();
		options.value = [];
		await getBuils();
	};

	// 初始化时调用
	initData();

	// 表单提交处理
	registerFormDone(async () => {
		const submitData = { ...form };
		if (props.rowInfo.id) {
			submitData.id = props.rowInfo.id;
		}

		try {
			let res;
			if (props.rowInfo.id) {
				res = await updateBuildArea(submitData);
			} else {
				res = await addBuildArea(submitData);
			}

			if (res.code == 0) {
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
</style>
