<template>
	<div>
		<div class="base-title m-t-12px">基础信息</div>
		<BaseForm v-bind="form1" />
		<div class="base-title m-t-12px">执行周期</div>
		<BaseForm v-bind="form2" />
		<div class="base-title m-t-12px">
			<span>执行检查项</span>
			<ElSelect
				v-model="manageId"
				placeholder="请选择检查项"
				filterable
				:disabled="isDetail"
				style="width: 300px;"
			>
				<ElOption v-for="item in options" :key="item.id" :label="item.checkName" :value="item.id" />
			</ElSelect>
		</div>
		<ElCard class="box-card" shadow="never" style="margin-top: 6px;" body-style="height: calc(100vh - 790px);overflow: hidden; overflow-y: auto;">
          <template #header>
            <span class="clearfix">巡检范围</span>
          </template>
			<ElTree :data="treeData" v-model="region" show-checkbox default-expand-all node-key="id"
						ref="tree"
						highlight-current :props="defaultProps">
			</ElTree>
        </ElCard>
	</div>
</template>

<script setup lang="ts">
import { ref, h } from "vue";
import { number } from "vue-types";
import BaseForm from "@/core/struct/form/base-form";
import {
	getGroupList,
	toAdd,
	getTreeBuild,
	getTemplateList,
	getJobPlanById
} from "@/api/pipeline-maintenance/inspection-plan";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
import { ElCard, ElTree, ElSelect, ElOption, ElRadio, ElRadioGroup, ElMessage } from "element-plus";

const props = defineProps({
	id: number().def(),
	isDetail: {
		type: Boolean,
		default: false
	}
});

const treeData = ref([]);
getTreeBuild().then(res => {
  treeData.value = res
})
getTreeBuild()

const dis = ()=> {
	return true
}
const region = ref([]);
const defaultProps = {
  children: 'children',
  label: 'label',
  disabled: dis()
}

const manageId = ref();

// 执行时间显示
const dateList = ref([]);
const getTimeData = (e: string) => {
	if(e == '1') {
		dateList.value = Array(24).fill().map((_, i) => ({
			label: `${i}`,
			value: `${i}`
		}))
	} else if(e == '2') {
		dateList.value = Array(7).fill().map((_, i) => ({
			label: `${i + 1}`,
			value: `${i + 1}`
		}))
	} else {
		dateList.value =Array(31).fill().map((_, i) => ({
			label: `${i}`,
			value: `${i}`
		}))
	}
}

const { form:form1 } = useDialogStructForm({
	labelWidth: 110,
	span: 12,
	expandSpan: 6,
	notMargn: false,
	inline: false,
	showExpand: false,
	showMessage: true,
	labelPosition: "top",
	formItems: [
		{
			label: "计划名称",
			prop: "planName",
			value: "",
			type: "ElInput",
			span: 8,
			attrs: {
				placeholder: "请输入计划名称",
				disabled: props.isDetail
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "planType",
			label: "计划类型",
			span: 8,
			attrs: {
				placeholder: "计划类型",
				clearable: true,
				disabled: props.isDetail
			},
			select: {
				type: "ElOption",
				label: "dictLabel",
				value: "dictValue",
				listApi: async () => {
					const data = await getRedisDictList({ dictType: "planType" });
					console.log('dataplanType', data)
					return data;
				},
				list: []
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "executeType",
			label: "执行类型",
			span: 8,
			attrs: {
				placeholder: "执行类型",
				clearable: true,
				disabled: props.isDetail
			},
			select: {
				type: "ElOption",
				label: "label",
				value: "value",
				list: [
					{
						label: "重复执行",
						value: "1"
					},
					{
						label: "单次执行",
						value: "2"
					}
				]
			}
		},
		{
				label: "计划状态",
				value: "1",
				type: "ElRadioGroup",
				prop: "planStatus",
				attrs: {
					disabled: props.isDetail
				},
				span: 8,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "启用",
							value: "0"
						},
						{
							label: "停用",
							value: "1"
						}
					]
				}
			},
		{
			type: "ElDatePicker",
			value: "",
			label: "计划开始时间",
			prop: "startTime",
			span: 8,
			attrs: {
				type: "datetime",
				placeholder: "请选择日期",
				valueFormat: "YYYY-MM-DD HH:mm:ss",
				disabled: props.isDetail
			}
		},
		{
			type: "ElDatePicker",
			value: "",
			label: "计划结束时间",
			span: 8,
			prop: "endTime",
			attrs: {
				type: "datetime",
				placeholder: "请选择日期",
				valueFormat: "YYYY-MM-DD HH:mm:ss",
				disabled: props.isDetail
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "groupId",
			label: "执行班组",
			span: 8,
			attrs: {
				placeholder: "执行班组",
				clearable: true,
				disabled: props.isDetail
			},
			select: {
				type: "ElOption",
				label: "name",
				value: "id",
				listApi: async () => {
					const data = await getGroupList({});
					return data;
				},
				list: []
			}
		},
		{
			type: "ElSelect",
			value: "",
			prop: "planLevel",
			label: "级别",
			span: 8,
			attrs: {
				placeholder: "级别",
				clearable: true,
				disabled: props.isDetail
			},
			select: {
				type: "ElOption",
				label: "dictLabel",
				value: "dictValue",
				listApi: async () => {
					const data = await getRedisDictList({dictType: "warnLevel"});
					return data;
				},
				list: []
			}
		},
		{
			label: "执行期限",
			prop: "executeTimes",
			value: "",
			span: 8,
			type: "ElInput",
			attrs: {
				placeholder: "请输入执行时长与时长单位",
				disabled: props.isDetail
			},
			append: (formData, item) => h(ElSelect, {
				disabled: props.isDetail,
				modelValue: formData.executeUint, // 绑定到表单数据的executeUint字段
				"onUpdate:modelValue": (value) => {
				formData.executeUint = value; // 更新表单数据
				},
				style: { width: '80px' }
			}, [
				h(ElOption, { label: '时', value: 1 }),
				h(ElOption, { label: '日', value: 2 }),
			])
		},
	],
	rules: {
		planName: [
			{
				required: true,
				message: "请输入计划名称",
				trigger: "blur"
			}
		],
		planType: [
			{
				required: true,
				message: "请选择计划类型",
				trigger: "change"
			}
		],
		executeType: [
			{
				required: true,
				message: "请选择执行类型",
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
		startTime: [
			{
				required: true,
				message: "请选择计划开始时间",
				trigger: "change"
			}
		],
		endTime: [
			{
				required: true,
				message: "请选择计划结束时间",
				trigger: "change"
			}
		],
		groupId: [
			{
				required: true,
				message: "请选择执行班组",
				trigger: "change"
			}
		],
		executeTimes: [
			{
				required: true,
				message: "请输入执行时长与时长单位",
				trigger: "blur"
			}
		],
		planLevel: [
			{
				required: true,
				message: "请选择级别",
				trigger: "change"
			}
		],
	},
});
const { form: form2, registerFormDone } = useDialogStructForm({
	labelWidth: 100,
	span: 12,
	expandSpan: 6,
	notMargn: false,
	inline: false,
	showExpand: false,
	showMessage: true,
	labelPosition: "top",
	formItems: [
		{
			type: "ElSelect",
			value: "1",
			prop: "executeCycle",
			label: "执行周期",
			span: 8,
			attrs: {
				placeholder: "执行周期",
				clearable: true,
				disabled: props.isDetail,
				onChange: (e: string) => {
					console.log('77777777777e', e)
					if(e == '1') {
						form2.formItems[1].select.list = Array(24).fill().map((_, i) => ({
							label: `${i}`,
							value: `${i}`
						}))
					} else if(e == '2') {
						form2.formItems[1].select.list = Array(7).fill().map((_, i) => ({
							label: `${i + 1}`,
							value: `${i + 1}`
						}))
					} else {
						form2.formItems[1].select.list =Array(31).fill().map((_, i) => ({
							label: `${i}`,
							value: `${i}`
						}))
					}
					console.log('dateList.value', dateList.value)	
				}
			},
			select: {
				type: "ElOption",
				label: "label",
				value: "value",
				list: [
					{
						label: "日",
						value: "1"
					},
					{
						label: "周",
						value: "2"
					},
					{
						label: "月",
						value: "3"
					}
				]
			}
		},
		{
			type: "ElCheckboxGroup",
			value: [],
			prop: "cycleTime",
			label: "周期时间",
			span: 24,
			attrs: {
				placeholder: "周期时间",
				clearable: true,
				multiple: true,
				disabled: props.isDetail
			},
			select: {
				type: "ElCheckbox",
				label: "label",
				value: "value",
				list: dateList
			}
		}
	],
	rules: {
		executeCycle: [
			{
				required: true,
				message: "请选择执行周期",
				trigger: "blur"
			}
		],
		selectDays: [
			{
				required: true,
				message: "请选择周期时间",
				trigger: "blur"
			}
		],
	},
});

const tree = ref()
registerFormDone(async() => {
	console.log('66666666', form1.value, form2.value)
	var checkedList = tree.value.getCheckedNodes();
      if (checkedList.length == 0) {
        ElMessage.error("请选择巡检范围");
		throw '请选择巡检范围'
        return;
      }
      var areaList = [];
      for (let i = 0; i < checkedList.length; i++) {
        areaList.push({
          realId: checkedList[i].realId,
          type: checkedList[i].type
        })
      }
	const res = await toAdd({ 
		...form1.value,
		...form2.value,
		cycleTime: form2.value.cycleTime.join(','),
		selectDays: form2.value.cycleTime,
		id: props.id,
		manageId: manageId.value,
		areaList,
	});
	return res;
});

const options = ref([]);
getTemplateList({}).then(res => {
	options.value = res;
})

if (props.id) {
	getJobPlanById({id: props.id}).then((res) => {
		console.log('88888888详情页数据', res)
		Object.keys(form1.value).forEach((key) => {
			form1.value[key] = res[key];
		});
		// 计划开始时间
		form1.value.startTime = res.planStartTime || '';
		// 计划结束时间
		form1.value.endTime = res.planEndTime || '';
		// 执行时长得单位
		form1.value.executeUint = res.executeUint || '';
		//计划类型
		form1.value.planType = res.planType.toString() || '';
		// 执行类型
		form1.value.executeType = res.executeType.toString() || '';
		// 计划状态
		form1.value.planStatus = res.planStatus.toString() || '';
		// 级别
		form1.value.planLevel = res.planLevel.toString() || '';
		console.log('99999999999999999form1', form1	)
		Object.keys(form2.value).forEach((key) => {
			form2.value[key] = res[key];
		});
		// 执行周期
		form2.value.executeCycle = res.executeCycle.toString() || '';
		getTimeData(form2.value.executeCycle)
		// 执行时间
		form2.value.cycleTime = res.cycleTime.split(',') || [];
		manageId.value = res.manageId || '';
		// 树形结构回显
		const areaList = res.areaList || [];
		const areaLists = [];
		for (let i = 0; i < areaList.length; i++) {
			if (areaList[i].type=='areaId'){
				areaLists[areaLists.length]="AAAA"+areaList[i].areaId;
			}
		}
		tree.value.setCheckedKeys(areaLists)
	});
} else {
	getTimeData(`1`)
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
