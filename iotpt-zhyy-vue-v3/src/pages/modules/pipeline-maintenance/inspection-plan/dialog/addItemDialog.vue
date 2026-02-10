<template>
	<div>
		<BaseForm v-bind="form" />
		<div class="base-title">
			<span>检查项</span>
			<ElButton type="success" :icon="Plus" @click="addItem">添加</ElButton>
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
			<ElTableColumn label="设备类型" align="center" prop="type">
				<template #default="scope">
					<ElSelect :disabled="!scope.row.editState" v-model="scope.row.deviceId" placeholder="请选择设备类型">
						<ElOption v-for="item in deviceList" :key="item.id" :label="item.deviceTypeName" :value="item.id" />
					</ElSelect>
					<span v-show="!scope.row.isOK">{{ scope.row.infoD }}</span>
				</template>
			</ElTableColumn>
			<ElTableColumn label="检查项名称" align="center" prop="amount2">
				<template #default="scope">
					<ElInput v-model="scope.row.subContent" size="mini" style="margin: 5px 0px" autocomplete="off" :disabled="!scope.row.editState" placeholder="请输入检查项名称" />
				</template>
			</ElTableColumn>
			<ElTableColumn label="操作" align="center">
				<template #default="scope">
					<ElButton type="primary" link @click="saveEdit(scope.row)" v-if="scope.row.editState">保存</ElButton>
					<ElButton type="primary" link @click="beginEdit(scope.row)" v-else>编辑</ElButton>
					<ElButton type="danger" link @click="delItem(scope.row)">删除</ElButton>
				</template>
			</ElTableColumn>
		</ElTable>
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import BaseForm from "@/core/struct/form/base-form";
import {delCheckTem, getList, getTypeList, setIsAble,copyTemplate,getOneDetail, getAllDeviceType, addCheck} from "@/api/pipeline-maintenance/inspection-plan";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import { Plus } from "@element-plus/icons-vue";
import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
import { ElDescriptions, ElDescriptionsItem,ElPagination, ElTable, ElTableColumn, ElButton, ElCard, ElTree, ElSelect, ElOption, ElMessage, ElInput } from "element-plus";

const props = defineProps({
	id: number().def(),
});

const deviceList = ref([]);
const getDeviceType = () => {
	getAllDeviceType({}).then((res) => {
		deviceList.value = res;
	})
}
getDeviceType()

const { form,registerFormDone } = useDialogStructForm({
	labelWidth: 110,
	span: 12,
	expandSpan: 6,
	notMargn: false,
	inline: false,
	showExpand: false,
	showMessage: true,
	labelPosition: "left",
	formItems: [
		{
			label: "名称",
			prop: "checkName",
			value: "",
			type: "ElInput",
			span: 8,
			attrs: {
				placeholder: "请输入名称",
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "type",
			label: "所属类型",
			span: 8,
			attrs: {
				placeholder: "所属类型",
				clearable: true
			},
			select: {
				type: "ElOption",
				label: "dictLabel",
				value: "dictValue",
				listApi: async () => {
					const data = await getTypeList();
					return data;
				},
				list: []
			}
		},
		{
			type: "ElRadioGroup",
			value: "",
			prop: "status",
			label: "状态",
			span: 8,
			attrs: {
				placeholder: "状态",
				clearable: true
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
		{
			label: "描述",
			prop: "checkDesc",
			value: "",
			type: "ElInput",
			span: 24,
			attrs: {
				placeholder: "请输入描述",
				type: 'textarea',
				row:4
			}
		},
	],
	rules: {
		checkDesc: [
			{
				required: true,
				message: "请输入描述",
				trigger: "blur"
			}
		],
		status: [
			{
				required: true,
				message: "请选择状态",
				trigger: "change"
			}
		],
		type: [
			{
				required: true,
				message: "请选择所属类型",
				trigger: "change"
			}
		],
		planStatus: [
			{
				required: true,
				message: "请选择计划状态",
				trigger: "change"
			}
		],
		checkName: [
			{
				required: true,
				message: "请输入名称",
				trigger: "blur"
			}
		],
	},
});


registerFormDone(async() => {
	console.log('66666666', form.value)
	const tempList = list.value;
      if (tempList.length === 0){
        ElMessage.error('请添加检查项!');
		throw '请添加检查项!';
        return;
      }
      var listValue = [];
      for (var i = 0 ; i < tempList.length ; i++){
        if (tempList[i].deviceId ===  null || tempList[i].deviceId ===  undefined|| tempList[i].deviceId ===  '' ){
			ElMessage.error('请选择设备类型!');
			throw '请选择设备类型!';
        }
        if (tempList[i].subContent ===  null || tempList[i].subContent ===  undefined|| tempList[i].subContent ===  '' ){
			ElMessage.error('请填写检查项内容!');
			throw '请填写检查项内容!';
        }
        var z = {};
        z.deviceType = tempList[i].deviceId;
        z.subContent = tempList[i].subContent;
        listValue.push(z);
      }
	const res = await addCheck({ ...form.value, list:listValue, id: props.id });
	return res;
});

const list = ref([]);
// 新增
const addItem = () => {
	list.value.push({
		editState: true,
		deviceId: "",
		subContent: "",
	});
}
// 新增编辑
const beginEdit = (row) => {
	row.editState = true;
}
//删除
const delItem = (row) => {
	list.value.splice(row.index,1);
}
// 编辑保存
const saveEdit = (row) => {
	row.editState = false;
}

if (props.id) {
	getOneDetail({id: props.id}).then((res) => {
		form.value = res
		form.value.status = res.status.toString();
		list.value = res.list.map((item,index) => ({
			editState: false ,deviceId : item.deviceType,subContent: item.subContent
		}))
	});
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
