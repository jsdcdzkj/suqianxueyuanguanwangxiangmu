<template>
	<div>
		<ElForm ref="formInstance" :model="form">
			<ElRow :gutter="12">
				<ElCol :span="6">
					<ElTreeSelect
						v-model="form.unitId"
						placeholder="请选择所属单位"
						:data="treeData"
						:props="treeProps"
						filterable
						:filter-node-method="filterNode"
						clearable
						:check-strictly="true"
						:render-after-expand="false"
					/>
				</ElCol>
				<ElCol :span="6">
					<ElFormItem prop="groupLeader">
						<ElSelect v-model="form.roleId" placeholder="角色名称">
							<ElOption v-for="item in roleList" :key="item.id" :label="item.roleName" :value="item.id" />
						</ElSelect>
					</ElFormItem>
				</ElCol>
				<ElCol :span="6">
					<ElFormItem prop="name">
						<ElInput v-model="form.realName" placeholder="用户名" />
					</ElFormItem>
				</ElCol>
				<ElCol :span="6">
					<ElButton type="primary" :icon="Search" @click="handleSearch">查询</ElButton>
					<ElButton type="primary" :icon="Refresh" plain @click="handleReset">重置</ElButton>
				</ElCol>
			</ElRow>
		</ElForm>
		<ElTable
			ref="tableRef"
			border
			:data="list"
			class="m-t-12px"
			multiple-selection
			row-key="id"
			@selection-change="handleSelectionChange"
		>
			<!-- 选择列 -->
			<ElTableColumn
            align="center"
              type="selection"
            :reserve-selection="true"
              width="55">
            </ElTableColumn>
			<ElTableColumn
				label="序号"
				type="index"
				width="80px"
				align="center"
			></ElTableColumn>
			<ElTableColumn label="用户名称" align="center" prop="realName"></ElTableColumn>
			<ElTableColumn label="所属单位" align="center" prop="deptName" show-overflow-tooltip>
			</ElTableColumn>
			<ElTableColumn label="角色" align="center" prop="roleNamesStr" show-overflow-tooltip>
			</ElTableColumn>
			<ElTableColumn label="手机号" align="center" prop="phone" show-overflow-tooltip>
			</ElTableColumn>
		</ElTable>
		<ElPagination
			style="display: flex; justify-content: center; margin-top: 16px"
			background
			small
			layout="total, sizes, prev, pager, next, jumper"
			:total="total"
			:page-size.sync="form.pageSize"
			@size-change="handleSizeChange"
			@current-change="handleCurrentChange"
		/>
	</div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { number } from "vue-types";
import {
	getUserPage,orgTreeList,getRoleList
} from "@/api/pipeline-maintenance/team-management";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
import { useDialogForm } from "@/core/dialog/dialog-container";
import { Refresh, Search } from "@element-plus/icons-vue";
import { ElPagination, ElTable, ElTableColumn, ElButton, ElSelect, ElOption, ElInput, ElForm, ElRow, ElCol, ElFormItem, ElTreeSelect } from "element-plus";

// 选中的用户
const selectedUsers = ref([]);
const props = defineProps({
	id: number().def(),
	selectArr: [],
});
const treeProps = {
	children: "children",
	label: "label",
	value: "id",
	disabled: (data: any) => data.functions?.length > 0 // 新增disabled判断
};
watch(() => props.selectArr, (newVal) => {
	selectedUsers.value = newVal || []
}, { immediate: true })

const form = ref({
	name: '',
	groupLeader: '',
	phone: '',
	pageSize: 10,
	pageIndex: 1,
});
const list = ref([]);
const total = ref(0);


const { registerFormDone, formInstance } = useDialogForm();
// 单位树
const treeData = ref([]);
const getTreeData = () => {
	orgTreeList({}).then((res) => {
		treeData.value = res;
		console.log('单位树', treeData.value)
	})
}

getTreeData()

// 角色列表
const roleList = ref([]);
const getRoleListData = () => {
	getRoleList({}).then((res) => {
		roleList.value = res.map((item) => {
			if(!item.children) {
				item.children = []
			}
			return item
		});
	})
}

getRoleListData()

// 如果存在已选人员回显表示
const tableRef = ref(null);

registerFormDone(async() => {
	console.log('66666666', selectedUsers.value)
	return selectedUsers.value;
});

const handleSearch = () => {
	getData()
}
const handleReset = () => {
	form.value = {
		unitId: '',
		roleId: '',
		realName: '',
		pageSize: 10,
		pageIndex: 1,
	}
	getData()
}

const handleSizeChange = (val) => {
	form.value.pageSize = val
	getData()
}
const handleCurrentChange = (val) => {
	form.value.pageIndex = val
	getData()
}

const getData = () => {
	const ids = selectedUsers.value.map(item => item.id)
	console.log('8888888888888888ids', ids)
	getUserPage(form.value).then((res) => {
		list.value = res.records
		total.value = res.total
		list.value.forEach((item) => {
			if(ids.includes(item.id)) {
				tableRef.value?.toggleRowSelection(item, true)
			}
		})
	});
}
getData()
const handleSelectionChange = (val1) => {
	selectedUsers.value = val1
}
</script>
<style lang="scss" scoped>
:deep(.my-label) {
	font-size: 14px;
	font-weight:500;
	color: rgba(0,0,0,0.85);
	background: #F8F8F8!important;
}
.base-title {
	height: 40px;
	line-height: 40px;
	margin-bottom: 12px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
	padding:0 12px;
	font-size: 16px;
	color: rgba(0,0,0,0.85);
	margin-bottom: 12px;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.detail-type {
	font-size: 14px;
	font-weight:500;
	color: rgba(0,0,0,0.85);
	height: 40px;
	border: 1px solid rgba(0,0,0,0.15);
	text-align: center;
	cursor: pointer;
	border-radius:4px;
	margin-bottom: 12px;

	.type {
		flex:1;
		line-height: 40px;
	}
	.activeType {
		color: #fff;
		background:#345BAD;
	}
}

::v-deep{
	.el-card__header {
		padding: 9px 12px;
		background: rgba(0,0,0,0.06);
	}
}
</style>
