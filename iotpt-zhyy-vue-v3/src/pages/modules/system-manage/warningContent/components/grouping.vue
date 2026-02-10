<template>
	<div class="search-wrap">
		<div>
			<ElInput
				v-model="queryForm.name"
				:suffix-icon="Search"
				placeholder="搜索分组名称"
				@clear="debouncedGetAlarmGroupPage"
				@input="debouncedGetAlarmGroupPage"
				@keyup.enter="getAlarmGroupPage"
			/>
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
		<ElTableColumn label="序号" width="60" align="center">
			<template #default="scope">
				{{ scope.$index + 1 }}
			</template>
		</ElTableColumn>
		<ElTableColumn show-overflow-tooltip prop="code" label="分组编号" align="center" width="160" />
		<ElTableColumn show-overflow-tooltip prop="name" label="分组名称" align="center" min-width="160" />
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
		ElButton,
		ElTable,
		ElTableColumn
	} from "element-plus";
	import { Search, Plus } from "@element-plus/icons-vue";
	import { debounce } from "lodash-es";
	import { createModelAsync } from "@/core/dialog";
	import { alarmGroupPage, saveAlarmGroup } from "@/api/setting/warnInfo";
	import AddGrouping from "./addGrouping.vue";

	const listLoading = ref(false);
	const elementLoadingText = ref("正在加载...");
	const list = ref([]);
	const queryForm = reactive({
		name: ""
	});

	const getAlarmGroupPage = async () => {
		listLoading.value = true;
		try {
			const data = await alarmGroupPage({
				pageNo: 1,
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
				title: row ? "编辑分组" : "新增分组",
				width: "480px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<AddGrouping rowInfo={row} />
		).then(() => {
			getAlarmGroupPage();
		});
	};

	const handleDelete = (id: number) => {
		ElMessageBox.confirm("此操作将删除该条数据, 是否继续?", "提示", {
			confirmButtonText: "确定",
			cancelButtonText: "取消",
			type: "warning"
		}).then(async () => {
			await saveAlarmGroup({ id, isDel: 1 });
			ElMessage.success("删除成功");
			getAlarmGroupPage();
		});
	};

	// 防抖函数
	const debouncedGetAlarmGroupPage = debounce(getAlarmGroupPage, 500);

	onMounted(() => {
		getAlarmGroupPage();
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
