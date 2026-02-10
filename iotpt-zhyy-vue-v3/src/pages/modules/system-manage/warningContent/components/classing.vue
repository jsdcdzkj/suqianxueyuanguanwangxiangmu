<template>
	<div class="search-wrap">
		<div>
			<ElInput
				v-model="queryForm.name"
				:suffix-icon="Search"
				placeholder="搜索告警分类"
				@clear="debouncedGetAlarmCategoryPage"
				@input="debouncedGetAlarmCategoryPage"
				@keyup.enter="getAlarmCategoryPage"
			/>
			<ElSelect
				v-model="queryForm.alarmGroup"
				placeholder="全部告警分组"
				@change="getAlarmCategoryPage"
				filterable
				clearable
				@clear="getAlarmCategoryPage"
			>
				<ElOption v-for="item in warnGroupList" :key="item.id" :label="item.name" :value="item.id" />
			</ElSelect>
		</div>
		<ElButton :icon="Plus" type="success" @click="handleAdd">新增</ElButton>
	</div>
	<ElTable
		v-loading="listLoading"
		:data="list"
		:element-loading-text="elementLoadingText"
		height="calc(100vh - 190px)"
		border
	>
		<ElTableColumn show-overflow-tooltip prop="code" label="分类编号" align="center" width="160" />
		<ElTableColumn show-overflow-tooltip prop="name" label="分类名称" align="center" min-width="160" />
		<ElTableColumn show-overflow-tooltip prop="alarmTypeName" label="警种" align="center" min-width="160" />
		<ElTableColumn show-overflow-tooltip prop="alarmGroupName" label="告警分组" align="center" width="160" />
		<ElTableColumn label="操作" align="center" width="180">
			<template #default="scope">
				<ElButton type="primary" plain size="small" @click="handleAdd(scope.row)">编辑</ElButton>
				<ElButton type="danger" plain size="small" @click="handleDelete(scope.row.id)">删除</ElButton>
			</template>
		</ElTableColumn>
	</ElTable>
</template>

<script setup lang="tsx">
	import { ref, reactive, onMounted } from "vue";
	import {
		ElMessage,
		ElMessageBox,
		ElForm,
		ElFormItem,
		ElInput,
		ElSelect,
		ElOption,
		ElButton,
		ElTable,
		ElTableColumn
	} from "element-plus";
	import { Search, Plus } from "@element-plus/icons-vue";
	import { debounce } from "lodash-es";
	import { createModelAsync } from "@/core/dialog";
	import { alarmCategoryPage, saveAlarmCategory } from "@/api/setting/warnInfo";
	import AddClassing from "./addClassing.vue";

	const props = defineProps({
		warnGroupList: {
			type: Array,
			default: () => []
		},
		alarmTypeList: {
			type: Array,
			default: () => []
		}
	});

	const listLoading = ref(false);
	const elementLoadingText = ref("正在加载...");
	const list = ref([]);
	const queryForm = reactive({
		name: "",
		alarmGroup: ""
	});

	const getAlarmCategoryPage = async () => {
		listLoading.value = true;
		try {
			const data = await alarmCategoryPage({
				pageNum: 1,
				pageSize: 1000,
				...queryForm
			});
			list.value = data.records;
		} finally {
			listLoading.value = false;
		}
	};

	const handleAdd = (row?: any) => {
		createModelAsync(
			{
				title: row ? "编辑分类" : "新增分类",
				width: "480px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<AddClassing rowInfo={row} warnGroupList={props.warnGroupList} alarmTypeList={props.alarmTypeList} />
		).then(() => {
			getAlarmCategoryPage();
		});
	};

	const handleDelete = (id: number) => {
		ElMessageBox.confirm("此操作将删除该条数据, 是否继续?", "提示", {
			confirmButtonText: "确定",
			cancelButtonText: "取消",
			type: "warning"
		}).then(async () => {
			await saveAlarmCategory({ id, isDel: 1 });
			ElMessage.success("删除成功");
			getAlarmCategoryPage();
		});
	};

	// 防抖函数
	const debouncedGetAlarmCategoryPage = debounce(getAlarmCategoryPage, 500);

	onMounted(() => {
		getAlarmCategoryPage();
	});
</script>

<style scoped lang="scss">
	.search-wrap {
		display: flex;
		margin-bottom: 16px;
		justify-content: space-between;
		align-items: center;
		gap: 16px;

		> div {
			display: flex;
			gap: 16px;
		}
	}
</style>
