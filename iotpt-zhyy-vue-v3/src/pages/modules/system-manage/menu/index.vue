<template>
	<div class="h-full flex gap-12px p-12px">
		<div class="flex-1 custom-table h-full flex flex-col w-full relative">
			<ElTabs v-model="activeTab" class="demo-tabs">
				<ElTabPane
					v-for="system in systemOptions"
					:key="system.value"
					:label="system.label"
					:name="system.value"
				/>
			</ElTabs>
			<div class="absolute top-8px right-12px">
				<ElButton type="primary" @click="handleAdd" :icon="Plus">新增</ElButton>
			</div>
			<div class="p-12px">
				<div class="filter-container">
					<ElSelect v-model="filterType" placeholder="菜单类型" clearable class="filter-item">
						<ElOption label="菜单" :value="1" />
						<ElOption label="按钮" :value="2" />
					</ElSelect>
					<ElInput
						v-model="filterText"
						placeholder="菜单名称"
						clearable
						class="filter-item"
						@keyup.enter="handleFilter"
					/>
					<ElButton type="primary" @click="handleFilter" :icon="Search">查询</ElButton>
					<ElButton @click="handleReset" :icon="Refresh">重置</ElButton>
				</div>
				<ElTable
					:data="tableData"
					border
					style="width: 100%"
					row-key="id"
					:tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
				>
					<ElTableColumn prop="title" label="菜单名称" align="center" />
					<ElTableColumn prop="routerName" label="路由名称" align="center" />
					<ElTableColumn prop="routerUrl" label="路由链接" align="center" />
					<ElTableColumn prop="vueUrl" label="vue文件路径" align="center" />
					<ElTableColumn prop="menuType" label="菜单类型" align="center" width="90">
						<template #default="{ row }">
							<span>{{ row.menuType === 1 ? "菜单" : "按钮" }}</span>
						</template>
					</ElTableColumn>
					<ElTableColumn prop="icon" label="图标" align="center" width="180" />
					<ElTableColumn prop="isShow" label="是否显示" align="center" width="90">
						<template #default="{ row }">
							<ElTag :type="row.isShow === 1 ? 'success' : 'info'">
								{{ row.isShow === 1 ? "显示" : "隐藏" }}
							</ElTag>
						</template>
					</ElTableColumn>
					<ElTableColumn prop="sort" label="排序" align="center" width="60" />
					<ElTableColumn label="操作" align="center" width="150">
						<template #default="{ row }">
							<ElButton type="primary" size="small" plain @click="handleEdit(row)">编辑</ElButton>
							<ElButton type="danger" size="small" plain @click="handleDelete(row)">删除</ElButton>
						</template>
					</ElTableColumn>
				</ElTable>
			</div>
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { ref, onMounted, watch } from "vue";
	import {
		ElMessage,
		ElMessageBox,
		ElTag,
		ElSelect,
		ElOption,
		ElInput,
		ElButton,
		ElTable,
		ElTableColumn,
		ElTabs,
		ElTabPane
	} from "element-plus";
	import { Search, Refresh, Plus } from "@element-plus/icons-vue";
	import { getMenuTree, delMenu } from "@/api/setting/menu";
	import Detail from "./dialog/detail.vue";
	import { createModelAsync } from "@/core/dialog";

	const tableData = ref([]);
	const filterText = ref("");
	const filterType = ref("");
	const activeTab = ref("system1");
	const systemOptions = [
		{ label: "系统1", value: "system1" },
		{ label: "系统2", value: "system2" },
		{ label: "系统3", value: "system3" }
	];

	// 获取菜单列表
	const getList = async () => {
		try {
			const response = await getMenuTree({
				systemId: activeTab.value
			});
			tableData.value = Array.isArray(response.data) ? response.data : [];
		} catch (error) {
			ElMessage.error("获取菜单列表失败");
		}
	};

	// 筛选处理
	const handleFilter = () => {
		const filteredData = tableData.value.filter((item) => {
			const matchType = !filterType.value || item.menuType === filterType.value;
			const matchText = !filterText.value || item.title.includes(filterText.value);
			return matchType && matchText;
		});
		tableData.value = filteredData;
	};
	// 重置
	const handleReset = () => {
		filterText.value = "";
		filterType.value = "";
		getList();
	};

	// 新增
	const handleAdd = () => {
		createModelAsync(
			{
				title: "新增菜单",
				showNext: false,
				width: "720px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<Detail />
		).then((res) => {
			if (res) {
				ElMessage.success("新增成功");
				getList();
			}
		});
	};

	// 编辑
	const handleEdit = (row) => {
		createModelAsync(
			{
				title: "编辑菜单",
				showNext: false,
				width: "720px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<Detail rowInfo={row} />
		).then((res) => {
			if (res) {
				ElMessage.success("编辑成功");
				getList();
			}
		});
	};

	// 删除
	const handleDelete = (row) => {
		ElMessageBox.confirm("确认删除该菜单吗？", "提示", {
			type: "warning"
		}).then(async () => {
			try {
				await delMenu({ id: row.id });
				ElMessage.success("删除成功");
				getList();
			} catch (error) {
				ElMessage.error("删除失败");
			}
		});
	};

	// 初始化获取数据
	onMounted(() => {
		getList();
	});
	watch(activeTab, () => {
		handleReset();
	});
</script>

<style scoped>
	.filter-container {
		display: flex;
		padding-bottom: 10px;
	}
	.filter-item {
		width: 150px;
		margin-right: 10px;
	}
	:deep(.el-tabs__item) {
		font-size: 16px;
		height: 48px;
		padding: 0 20px !important;
		border-right: 1px solid rgba(0, 0, 0, 0.06);
	}
	:deep(.is-active) {
		background: rgba(52, 91, 173, 0.1);
		border-bottom: 3px solid #345bad;
		color: #345bad;
	}
	:deep(.el-tabs__nav-wrap::after) {
		height: 1px;
	}
	:deep(.el-tabs__header) {
		margin-bottom: 0;
	}
</style>
