<template>
	<div>
		<div class="base-title m-b-12px">基础信息</div>
		<ElDescriptions :column="1" border label-width="120px">
			<ElDescriptionsItem label="班组名称" label-class-name="my-label">{{ detail.name || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="班组说明" label-class-name="my-label">{{ detail.groupDesc || "--" }}</ElDescriptionsItem>
		</ElDescriptions>
		<div class="base-title m-t-12px">
			<span>组员信息</span>
		</div>
		<ElTable
			border
			:data="list"
			class="m-t-12px"
		>
			<ElTableColumn
				label="序号"
				type="index"
				width="55px"
				align="center"
			></ElTableColumn>
			<ElTableColumn label="组员名称" align="center" prop="realName" width="120px"></ElTableColumn>
			<ElTableColumn label="所属单位" align="center" prop="deptName"  width="150px" show-overflow-tooltip>
			</ElTableColumn>
			<ElTableColumn label="执行标准" align="center" prop="roleNamesStr" show-overflow-tooltip>
			</ElTableColumn>
			<ElTableColumn label="手机号" align="center" prop="phone" width="120px" show-overflow-tooltip>
			</ElTableColumn>
			<ElTableColumn label="组角色" align="center" prop="recovery_time"  width="120px" show-overflow-tooltip>
				<template #default="scope">
					<ElTag v-if="scope.row.roleId === '1'" type="danger">{{ scope.row.infoD }}</ElTag>
					<ElTag v-else type="success" >{{ scope.row.infoD }}</ElTag>
				</template>
			</ElTableColumn>
		</ElTable>
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import { ElDescriptions, ElDescriptionsItem,ElPagination, ElTable, ElTableColumn, ElTag } from "element-plus";
import { detailTeam } from "@/api/pipeline-maintenance/team-management";

const props = defineProps({
	id: number().def(),
	projectId: {
		type: Number,
		default: 0
	},
	row: {
		type: Object,
		default: {}
	}
});

const list = ref([{}]);
const detail = ref({});

if (props.id) {
	detailTeam({id: props.id}).then((res) => {
        detail.value = res;

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
}
</style>
