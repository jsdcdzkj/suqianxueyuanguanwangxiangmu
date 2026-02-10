<template>
	<div>
		<div class="base-title m-t-12px">基础信息</div>
		<BaseForm v-bind="form" />
		<div class="base-title m-t-12px">
			<span>组员信息</span>
			<ElButton type="success" :icon="Plus" size="mini" @click="handleAdd">新增组员</ElButton>
		</div>
		<ElTable
			border
			:data="list"
			class="m-t-12px"
		>
			<ElTableColumn
				label="序号"
				type="index"
				width="80px"
				align="center"
			></ElTableColumn>
			<ElTableColumn label="组员名称" align="center" prop="realName" width="120px"></ElTableColumn>
			<ElTableColumn label="所属单位" align="center" prop="deptName" show-overflow-tooltip>
			</ElTableColumn>
			<ElTableColumn label="执行标准" align="center" prop="roleNamesStr" show-overflow-tooltip>
			</ElTableColumn>
			<ElTableColumn label="手机号" align="center" prop="phone" show-overflow-tooltip width="120px">
			</ElTableColumn>
			<ElTableColumn label="组角色" align="center" prop="roleId" show-overflow-tooltip width="120px">
				<template #default="scope">
					<template v-if="scope.row.isOK">
						<ElSelect v-model="scope.row.roleId" placeholder="请选择组角色" style="width: 100%;" @change="changeRole(scope.row,scope.$index)">
							<ElOption v-for="item in roleList" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue"></ElOption>
						</ElSelect>
					</template>
					<template v-else>
						<ElTag v-if="scope.row.roleId === '1'" type="danger">组长</ElTag>
						<ElTag v-else type="success" >组员</ElTag>
					</template>
				</template>
			</ElTableColumn>
			<ElTableColumn label="操作" align="center" prop="roleId" show-overflow-tooltip width="120px">
				<template #default="scope">
					<ElButton type="primary" link v-if="!scope.row.isOK" @click="editVersion(scope.row,scope.$index)" plain >编辑</ElButton>
					<ElButton type="success" link v-else @click="saveVersion(scope.row,scope.$index)" plain>保存</ElButton>
					<ElButton type="warning" link plain @click="delVersion(scope.row,scope.$index)">删除</ElButton>
				</template>
			</ElTableColumn>
		</ElTable>
	</div>
</template>

<script setup lang="tsx">
import { ref } from "vue";
import { number } from "vue-types";
import BaseForm from "@/core/struct/form/base-form";
import {
	addTeam,
	getroleDicts,
	detailTeam
} from "@/api/pipeline-maintenance/team-management";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import { ElMessageBox, ElMessage, ElTable, ElTableColumn, ElButton, ElSelect, ElOption, ElTag, ElTreeSelect } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { createDrawerAsync, createModelAsync } from "@/core/dialog";
import AddGroupDialog from "./addGroupDialog.vue";

const props = defineProps({
	id: number().def(),
});
// 获取角色列表
const roleList = ref([])
const getRole = async () => {
	const res = await getroleDicts({})
	roleList.value = res
}
getRole()

const changeRole = (row,index) => {
	var id = row.roleId;
	for (var i = 0 ; i < roleList.value.length ; i++){
		if (roleList.value[i].dictValue === id){
			list.value[index].infoD = roleList.value[i].dictLabel
		}
	}
}
const list = ref([]);
const handleAdd = () => {
	createModelAsync({ title: "人员信息", width: "960px", showNext: false }, {},
		<AddGroupDialog selectArr={list.value} />
	).then(
		(res) => {
			if(res) {
				list.value = res
			}
		}
	);
};

const { form, registerFormDone } = useDialogStructForm({
	labelWidth: 110,
	span: 24,
	expandSpan: 6,
	notMargn: false,
	inline: false,
	showExpand: false,
	showMessage: true,
	labelPosition: "top",
	formItems: [
		{
			label: "班组名称",
			prop: "name",
			value: "",
			type: "ElInput",
			span: 24,
			attrs: {
				placeholder: "请输入班组名称",
			}
		},
		{
			label: "班组说明",
			prop: "groupDesc",
			value: "",
			type: "ElInput",
			span: 24,
			attrs: {
				type:'textarea',
				placeholder: "请输入班组说明",
				row:4,
			}
		},
	],
	rules: {
		name: [
			{
				required: true,
				message: "请输入班组名称",
				trigger: "blur"
			}
		],
		groupDesc: [
			{
				required: true,
				message: "请输入班组说明",
				trigger: "blur"
			}
		],
	},
});


registerFormDone(async() => {
	if (list.value.length === 0){
        ElMessage({
          type: 'error',
          message: '请选择班组成员!'
        });
		throw '请选择班组成员!'
      }

      var count = -1;
      var num = -1;
      var tempList = [];
      for (var i = 0 ; i < list.value.length ; i++){
        if (list.value[i].roleId === null || list.value[i].roleId === '' || list.value[i].roleId === undefined){
          count++;
        }
        if (list.value[i].roleId === '1' ){
          num++;
        }
        var z = {};
        z.userId = list.value[i].id;
        z.teamRole = list.value[i].roleId;
        tempList.push(z);
      }
      if (count !== -1){
        ElMessage({
          type: 'error',
          message: '请选择班组成员角色!'
        });
		throw '请选择班组成员角色!'
      }
      if (num === -1){
        ElMessage({
          type: 'error',
          message: '请选择组长!'
        });
		throw '请选择组长!'
      }
      if (num > 0){
        ElMessage({
          type: 'error',
          message: '有且只能有一位组长!'
        });
        throw '有且只能有一位组长!'
      }

	const res = await addTeam({ ...form.value,list:tempList, id: props.id });
	return res;
});

const editVersion = (row, index) => {
	list.value[index].isOK = true;
}
const saveVersion = (row,index) => {
	list.value[index].isOK = false;
}
const delVersion = (row,index) => {
	ElMessageBox.confirm('此操作将删除该信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        list.value.splice(index,1)
        ElMessage({
          type: 'success',
          message: '删除成功!'
        });
      }).catch(() => {
        ElMessage({
          type: 'info',
          message: '已取消删除'
        });
      });
}

if (props.id) {
	detailTeam({id: props.id}).then((res) => {
        form.value = res;

          var listValue = res.list ;
          var tempList= [];

          for (var i = 0 ; i< listValue.length ; i++){
            listValue[i].isOK = false;
            listValue[i].roleId = listValue[i].teamRole;
            listValue[i].infoD =listValue[i].teamRoleName;
            listValue[i].id =listValue[i].userId;
            tempList.push(listValue[i]);
          }
          list.value = tempList
      })
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
