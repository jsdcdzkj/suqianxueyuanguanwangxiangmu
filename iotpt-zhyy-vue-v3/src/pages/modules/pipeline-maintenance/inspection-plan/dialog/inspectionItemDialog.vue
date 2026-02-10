<template>
	<div class="inspection-item-dialog">
		<ElButton class="add-btn" type="success" :icon="Plus" @click="handleAdd()">添加</ElButton>
		<BasePage v-bind="page">
			<template #pageTableCell="{ column, row }">
				<!-- 严重程度 -->
				<div v-if="column.columnKey === 'levelsName'" class="cell-content">
					<span>{{ row.levelsName }}</span>
				</div>
				<!-- 状态 -->
				<div v-if="column.columnKey === 'status'" class="cell-content">
					<ElTag type="danger" v-if="row.status == 0">禁用</ElTag>
					<ElTag type="success" v-if="row.status == 1">启用</ElTag>
				</div>
			</template>
			<template #tableActions="scope">
				<ElButton type="primary" link @click="handleEdit(scope.row)">编辑</ElButton>
				<ElButton type="primary" link @click="handleCopy(scope.row)">复制</ElButton>
				<ElButton type="danger"  :disabled="scope.row.status !== 0" link @click="handleDelete(scope.row)">删除</ElButton>
			</template>
		</BasePage>
	</div>
	
</template>

<script setup lang="tsx">
import BasePage from "@/core/struct/page/base-page";
import { usePage } from "@/core/struct/page";
import { Plus } from "@element-plus/icons-vue";
import { markRaw } from "vue";
import { type FormConfig } from "@/core/struct/form/use-base-form";
import { createDrawerAsync, createModelAsync } from "@/core/dialog";
import {delCheckTem, getList, getTypeList, setIsAble,copyTemplate,getOneDetail} from "@/api/pipeline-maintenance/inspection-plan";
import { ElMessageBox, type Action, ElTag, ElButton } from "element-plus";
import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
import DetailDialog from "@/pages/modules/pipeline-maintenance/work-order-management/dialog/detailDialog.vue";
import AssignDialog from "@/pages/modules/pipeline-maintenance/work-order-management/dialog/assignDialog.vue";
import AddItemDialog from "@/pages/modules/pipeline-maintenance/inspection-plan/dialog/addItemDialog.vue";

const formConfig: FormConfig = {
	span: 5,
	expandSpan: 6,
	labelWidth: 0,
	showExpand: true,
	notMargn: true,
	isExpand: true,
	formItems: [
		{
			type: "ElInput",
			value: "",
			prop: "checkName",
			attrs: {
				placeholder: "检查项名称",
				clearable: true,
				onKeyup: (e: KeyboardEvent) => {
					if (e.key === "Enter") {
						pageApi.pageList();
					}
				}
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "type",
			attrs: {
				placeholder: "类型",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "dictLabel",
				value: "dictValue",
				listApi: async () => {
					const data = await getTypeList({ dictType: "warnLevel" });
					return data;
				},
				list: []
			}
		},
		{
			type: "ElRadioGroup",
			value: "",
			prop: "status",
			attrs: {
				placeholder: "状态",
			},
			select: {
				type: "ElRadio",
				list: [
					{
						label: "启用",
						value: "1"
					},
					{
						label: "禁用",
						value: "0"
					}
				]
			}
		},
	]
};
const { page, pageApi } = usePage({
	formConfig,
	listApi: async (data) => {
		const [startTimeStatistics1, endTimeStatistics1] = data.reporTime || [];
		const params = {
			...data,
			startTimeStatistics1,
			endTimeStatistics1,
		};
		delete params.time;
		return await getList({ ...params });
	},
	title:'',
	pagination: {
		currentPage: 1,
		pageSize: 20,
		total: 15,
		background: true,
		layout: "prev, pager, next, jumper, ->, total",
		onChange(pageNo: number, pageSize: number) {
			if (page.pagination) {
				page.pagination.currentPage = pageNo;
				page.pagination.pageSize = pageSize;
				pageApi.pageList();
			}
		}
	},
	tableConfig: {
		// 生成20条数据
		dataSource: [{}],
		columns: [
			{
				columnKey: "index",
				label: "序号",
				prop: "label",
				width: "80px",
				align: "center"
			},
			{
				label: "检查项名称",
				prop: "checkName",
				columnKey: "checkName",
				minWidth: 160,
				showOverflowTooltip: true
			},
			{
				label: "所属类型",
				prop: "typeName",
				columnKey: "typeName",
				width: 160
			},
			{
				label: "描述",
				prop: "checkDesc",
				columnKey: "checkDesc",
				minWidth: 200,
				showOverflowTooltip: true
			},
			{
				label: "状态",
				prop: "status",
				columnKey: "status",
				width: 100
			},
			{
				label: "操作",
				columnKey: "actions",
				prop: "actions",
				width: 160
			}
		]
	}
});

// 编辑
const handleEdit = (row) => {
	createDrawerAsync({ title: "编辑", width: "960px", showNext: false }, {},
		<AddItemDialog id={row.id} />
	).then(
		() => {
			pageApi.pageList();
		}
	);
};

// 添加
const handleAdd = () => {
	createDrawerAsync({ title: "添加", width: "960px", showNext: false }, {},
		<AddItemDialog />
	).then(
		() => {
			pageApi.pageList();
		}
	);
};

const handleCopy = (row) => {
	ElMessageBox.alert("确认复制此条数据?", "提示", {
		confirmButtonText: "确认",
		callback: (action: Action) => {
			if (action == "confirm") {
				copyTemplate({ id: row.id }).then(() => {
					pageApi.pageList();
				});
			}
		}
	});
	
};

// 删除
const handleDelete = (row) => {
	ElMessageBox.alert("此操作将删除该信息, 是否继续?", "提示", {
		confirmButtonText: "确认",
		callback: (action: Action) => {
			if (action == "confirm") {
				delCheckTem({ id: row.id }).then(() => {
					pageApi.pageList();
				});
			}
		}
	});
}

</script>
<style scoped lang="scss">
.inspection-item-dialog {
	position:relative;
	.add-btn {
		position:absolute;
		top: 22px;
		right: 20px;
		z-index:10;
	}


}
</style>
