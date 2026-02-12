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
					<ElTableColumn prop="title" label="菜单名称" />
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
							<ElLink @click="changeHidden(row)" class="status-link">
								<span class="status-dot" :class="row.isShow === 0 ? 'danger' : 'success'"></span>
								{{ row.isShow === 0 ? "隐藏" : "显示" }}
							</ElLink>
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
	import { Search, Refresh, Plus, Warning, SuccessFilled } from "@element-plus/icons-vue";
	import { getMenuTree, delMenu, doChangeStatus } from "@/api/setting/menu";
	import Detail from "./dialog/detail.vue";
	import { createModelAsync } from "@/core/dialog";
	import { getDictByKey } from "@/api/setting/sysDict";
	// import { useStore } from "vuex";
	// const store = useStore();

	const tableData = ref([]);
	const filterText = ref("");
	const filterType = ref("");
	const activeTab = ref("");
	const systemOptions = ref([]);

	const getSystematicType = async () => {
		try {
			const res = await getDictByKey();
			const systematicData = res?.systematic_type;

			systemOptions.value = systematicData
				? Object.values(systematicData).map((item) => ({
						value: item.dictValue,
						label: item.dictLabel
					}))
				: [];

			// 设置默认选中第一个系统类型
			if (systemOptions.value.length > 0) {
				activeTab.value = systemOptions.value[0].value;
			}
		} catch (error) {
			console.error("获取系统类型失败:", error);
			systemOptions.value = [];
		}
	};

	// 初始化获取数据
	onMounted(async () => {
		await getSystematicType();
		getList();
	});

	watch(activeTab, () => {
		getList();
	});

	// 获取菜单列表
	const getList = async () => {
		const response = await getMenuTree({
			systemId: activeTab.value,
			menuType: filterType.value || undefined,
			title: filterText.value || undefined
		});
		tableData.value = Array.isArray(response) ? response : [];
	};
	// 切换显示状态
	const changeHidden = async (row) => {
		try {
			const newStatus = row.isShow === 0 ? 1 : 0;
			await doChangeStatus({ id: row.id, isShow: newStatus });
			row.isShow = newStatus;

			// 更新用户信息和路由
			// const permissions = await store.dispatch("user/getUserInfo");
			// await store.dispatch("routes/setRoutes", permissions);

			// 刷新列表数据
			getList();
		} catch (error) {
			// 恢复原状态
			row.isShow = row.isShow === 0 ? 1 : 0;
		}
	};

	// 筛选处理
	const handleFilter = () => {
		getList();
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
			<Detail systemOptions={systemOptions.value} systemId={activeTab.value} />
		).then((res) => {
			if (res) {
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
			<Detail rowInfo={row} systemOptions={systemOptions.value} systemId={activeTab.value} />
		).then((res) => {
			if (res) {
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
				getList();
			} catch (error) {
				ElMessage.error("删除失败");
			}
		});
	};
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
	.status-link {
		display: inline-flex;
		align-items: center;
		gap: 4px;

		.status-dot {
			margin-right: 6px;
			display: inline-block;
			width: 12px;
			height: 12px;
			border-radius: 50%;

			&.danger {
				background-color: #f56c6c;
			}

			&.success {
				background-color: #67c23a;
			}
		}
	}
</style>
