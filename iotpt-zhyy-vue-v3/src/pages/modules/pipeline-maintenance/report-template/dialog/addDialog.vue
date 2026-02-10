<template>
	<div class="mainbody">
		<ElForm :inline="true" ref="formInstance" :rules="formRules" :model="form" class="demo-form-inline">
			<ElRow :gutter="12">
				<ElCol :span="6">
					<ElFormItem prop="tempName" style="width: 100%">
						<ElInput v-model="form.tempName" placeholder="请输入报告名称" />
					</ElFormItem>
				</ElCol>
				<ElCol :span="6">
					<ElFormItem prop="tempType" style="width: 100%">
						<ElSelect v-model="form.tempType" @change="handlerTemplateChange" placeholder="请选择模板类型">
							<ElOption
								v-for="item in templateOptions"
								:key="item.value"
								:label="item.label"
								:value="item.value"
							/>
						</ElSelect>
					</ElFormItem>
				</ElCol>
			</ElRow>
		</ElForm>

		<div class="video-main-con">
			<!-- 左侧树节点栏 -->
			<ElCard class="video-left-box" shadow="never">
				<div class="video-title">
					<span>报告模块</span>
				</div>
				<div class="tree-con">
					<ElTree
						:data="treeData"
						default-expand-all
						:props="treeDefaultProps"
						show-checkbox
						node-key="id"
						:check-on-click-node="true"
						ref="treeRef"
						:highlight-current="true"
						:default-expanded-keys="['1']"
						:expand-on-click-node="false"
						@node-click="handleNodeClick"
						@check-change="handleCheckChange"
						:filter-node-method="filterNode"
					/>
				</div>
			</ElCard>

			<!-- 右侧预览栏 -->
			<div class="video-right-box">
				<ElCard shadow="never">
					<div class="video-title">
						<span>模块信息预览</span>
					</div>
					<div
						class="mt10"
						ref="templateRef"
						style="height: calc(-269px + 100vh); overflow-y: auto; overflow-x: hidden"
					>
						<AlertHeader :title="form.tempName" />
						<div v-for="item in selectList" :key="item.id">
							<component
								:is="componentMap[child.type]"
								v-for="(child, index) in item.children"
								:key="index"
								:title="child.name"
								:index="`${child._subIndex}.${index + 1}`"
								:subIndex="child.subIndex"
								:subTitle="item.name"
								ref="modulesRef"
								@moduleLoad="moduleLoad"
							/>
						</div>
					</div>
				</ElCard>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, watch, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { ElRow, ElCol, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElCard, ElTree, ElButton } from "element-plus";
import { selectReportTypeTree, saveTemplate, updTemplate } from "@/api/pipeline-maintenance/report-template";
import { getTemplateList } from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/index.js";
import AlertHeader from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/AlertHeader.vue";
import BaseInfo from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/ProjectProfile/BaseInfo/index.vue";
import TerminalSituation from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/ProjectProfile/TerminalSituation/index.vue";
import Abnormal from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/InspectionSituation/Abnormal/index.vue";
import Records from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/InspectionSituation/Records/index.vue";
import ThreeUnbalancedAnalyses from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/ElectricalSafetyAnalysis/ThreeUnbalancedAnalyses/index.vue";
import ElectricityConsumption from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/EnergyUseStatistics/ElectricityConsumption/index.vue";
import LoadRateRanking from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/EnergyUseStatistics/LoadRateRanking/index.vue";
import LoadRateAnalysis from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/EnergyUseStatistics/LoadRateAnalysis/index.vue";
import TypesChart from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/AlarmAnalysis/AlarmType/index.vue";
import DeviceChart from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/AlarmAnalysis/AlarmDevice/index.vue";
import RankingChart from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/AlarmAnalysis/AlarmRank/index.vue";
import RankingChart100 from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/AlarmAnalysis/AlarmRank100/index.vue";
import WorkOrderStatus from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/InspectionSituation/WorkOrderStatus/index.vue";
import { useDialogForm } from "@/core/dialog/dialog-container";

const componentMap = {
  BaseInfo,
  TerminalSituation,
  Abnormal,
  Records,
  ThreeUnbalancedAnalyses,
  ElectricityConsumption,
  LoadRateRanking,
  LoadRateAnalysis,
  TypesChart,
  DeviceChart,
  RankingChart,
  RankingChart100,
  WorkOrderStatus
};

// 模板下拉选项类型
interface TemplateOption {
	label: string;
	value: number;
}
// 表单数据类型
interface FormData {
	tempName: string;
	tempType: number | "";
	id?: number; // 编辑时的主键ID
	reportTypeIds?: string[]; // 编辑时已选的树节点ID
}
// 表单校验规则类型
type FormRules = Record<keyof FormData, { required: boolean; trigger: string; message: string }[]>;
// 树节点基础类型
interface TreeBaseNode {
	id: string;
	name: string;
	label?: string;
	children?: TreeNode[];
	pId?: string;
	type?: string;
	checked: boolean;
	[key: string]: any; // 兼容后端返回的其他字段
}
// 树节点完整类型（继承基础类型，补充预览所需字段）
interface TreeNode extends TreeBaseNode {
	pName?: string;
	subIndex: number;
	_subIndex: number;
}
// 接口返回的树结构数据类型
interface TreeResponse {
	data: TreeBaseNode[];
	code?: number;
	msg?: string;
}
// 保存/编辑接口的请求参数类型
interface TemplateSaveParams extends Omit<FormData, "reportTypeIds"> {
	reportTypeIds: string[];
}
// 预览列表项类型
interface SelectListItem extends TreeBaseNode {
	children: TreeNode[];
}

// 模块DOM列表（接收子组件的moduleLoad事件）
const moduleList = ref<HTMLElement[]>([]);
// 已选的告警类型ID
const alarmType = ref<string[]>([]);
// 模板下拉选项
const templateOptions: TemplateOption[] = [
	{ label: "单区域模板", value: 1 },
	{ label: "多区域模板", value: 2 }
];
// 表单数据
const form = reactive<FormData>({
	tempName: "",
	tempType: ""
});
// 表单校验规则
const formRules: FormRules = {
	tempName: [{ required: true, trigger: "blur", message: "请输入报告名称" }],
	tempType: [{ required: true, trigger: "change", message: "请选择模板类型" }]
};
// 树结构数据
const treeData = ref<TreeBaseNode[]>([]);
// 树组件默认Props
const treeDefaultProps = reactive({
	children: "children",
	label: "name"
});
// 预览列表数据
const selectList = ref<SelectListItem[]>([]);

const treeRef = ref<InstanceType<typeof ElTree> | null>(null);
const templateRef = ref<HTMLDivElement | null>(null);
const modulesRef = ref<HTMLElement[]>([]); // 动态组件Ref

const filterNode = () => {};
const handleNodeClick = (data: TreeBaseNode) => {};

const prop = defineProps({
	row: {
		type: Object,
		default: () => ({
			tempName: "",
			tempType: "",
			id: null,
			reportTypeIds: []
		})
	}
});

// 修复点1：抽离初始化方法，确保树组件挂载后再执行（解决编辑态treeRef为null）
const initEditTemplate = async () => {
	if (prop.row.id && prop.row.tempType) {
		form.tempName = prop.row.tempName;
		form.tempType = prop.row.tempType;
		// 等待DOM挂载完成，treeRef有值
		await nextTick();
		await handlerTemplateChange(prop.row.tempType, prop.row.reportTypeIds || []);
	}
};
// 组件挂载后执行编辑态初始化
onMounted(() => {
	initEditTemplate();
});

const handlerTemplateChange = async (e: number | "", checkedNodeIds: string[] = []) => {
  if (!e) return;
  alarmType.value = [];
  treeData.value = [];
  selectList.value = [];
  try {
    const res = (await selectReportTypeTree({ modelType: Number(e) })) as TreeResponse;
    const treeDataTemp = res || [];
    const templateList = getTemplateList();
    console.log("原始树数据", templateList);

    // 核心：封装递归方法，处理所有层级的节点，挂载checked/type/pId属性
    const handleTreeNodes = (parentNode: TreeBaseNode, childrenNodes: TreeBaseNode[]) => {
      if (!childrenNodes || childrenNodes.length === 0) return;
      childrenNodes.forEach((child) => {
        // 浅拷贝节点，避免原对象只读导致属性赋值失败
        const newChild = { ...child };
        // 挂载父节点ID（关键：所有节点都有pId，方便后续匹配）
        newChild.pId = parentNode.id;
        // 校验ID存在后，赋值checked（是否在已选列表中）
        newChild.checked = newChild.id ? checkedNodeIds.includes(newChild.id) : false;
        // 匹配组件类型（一级子节点处理）
        const findItem = templateList.find((it) => it.cname === newChild.name);
        if (findItem) {
          newChild.type = findItem.name;
        } else {
          // 深层节点默认用name作为type
          newChild.type = newChild.name;
        }
        // 递归处理当前节点的子节点（覆盖二级/三级，比如告警类型节点）
        if (newChild.children) {
          handleTreeNodes(newChild, newChild.children);
        }
        // 替换原节点为新节点（保证属性挂载成功）
        const index = childrenNodes.findIndex(item => item.id === child.id);
        if (index > -1) childrenNodes[index] = newChild;
      });
    };

    // 遍历根节点，处理所有层级的子节点
    treeDataTemp.forEach((rootNode) => {
      if (rootNode.children) {
        handleTreeNodes(rootNode, rootNode.children);
      }
    });

    // 处理已选节点和告警类型（现在用正确的checkedNodeIds）
    if (checkedNodeIds.length > 0 && treeRef.value) {
      alarmType.value = checkedNodeIds.filter((item) => item?.startsWith("dict_"));
      treeRef.value.setCheckedKeys(checkedNodeIds);
    } else if (treeRef.value) {
      treeRef.value.setCheckedKeys([]);
    }

    treeData.value = treeDataTemp;
    // 强制刷新nextTick，确保数据更新后再计算预览列表
    await nextTick();
    computedTemplateList();
    console.log("处理后树数据（带checked）", treeData.value);
  } catch (error) {
    ElMessage.error("模板类型加载失败，请重试");
    console.error("模板类型切换异常：", error);
  }
};

const moduleLoad = (el: HTMLElement) => {
	moduleList.value.push(el);
};

const handleCheckChange = (data: TreeBaseNode, checked: boolean) => {
	// 给节点添加checked属性
	if (Reflect.has(data, "pId")) {
		data.checked = checked;
	}

	// 处理告警类型节点（pId为type_19/type_5）
	const targetPIds = ["type_19", "type_5"];
	if (targetPIds.includes(data.pId || "") && checked) {
		!alarmType.value.includes(data.id) && alarmType.value.push(data.id);
	} else if (targetPIds.includes(data.pId || "") && !checked) {
		const index = alarmType.value.findIndex((it) => it === data.id);
		index > -1 && alarmType.value.splice(index, 1);
	}

	// 重新计算预览列表
	computedTemplateList();
};

const computedTemplateList = () => {
	const list: SelectListItem[] = [];
	let current = 0;

	// 非空校验：树数据为空直接返回
	if (!treeData.value || treeData.value.length === 0) {
		selectList.value = list;
		return;
	}
	console.log('00000000000000treeData.value', treeData.value)
	treeData.value.forEach((item) => {
		const p: TreeNode[] = [];
		// 非空校验：避免item.children为undefined
		if (!item.children || item.children.length === 0) return;

		item.children.forEach((child) => {
			// 情况1：节点本身被选中 → 推入
			if (child.checked) {
				if (p.length === 0) current += 1;
				p.push({
					...child,
					pName: item.name, // 修复点4：用item.name替代item.label（后端返回name）
					subIndex: p.length === 0 ? current : 0,
					_subIndex: current
				});
			}
			// 修复点5：告警类型判断逻辑修正 → 选中告警节点且告警类型有值时推入
			else if (
				(child.pId === "type_19" || child.pId === "type_5") && 
				checked && // 节点被选中
				alarmType.value.length > 0
			) {
				if (p.length === 0) current += 1;
				p.push({
					...child,
					pName: item.name,
					subIndex: p.length === 0 ? current : 0,
					_subIndex: current
				});
			}
		});
		console.log('99999999999pppppppppp', p)
		// 有符合条件的子节点才推入预览列表
		if (p.length > 0) {
			list.push({
				...item,
				children: [...p]
			});
		}
	});

	selectList.value = list;
	console.log('selectList.value', selectList.value); // 现在能拿到正确数据
};

// 核心方法：保存模板
const { registerFormDone, formInstance } = useDialogForm();

registerFormDone(async () => {
	// 表单校验
	formInstance.value.validate(async (valid: boolean) => {
		if (!valid) return;

		// 收集已选的树节点ID
		const selectNodeIds = selectList.value
			.map((item) => item.children)
			.flat()
			.filter((item) => item.checked)
			.map((item) => item.id);
		console.log("选择的模板", selectNodeIds, alarmType.value);
		// 合并已选节点和告警类型
		const allSelectIds = [...new Set([...selectNodeIds, ...alarmType.value])]; // 去重

		// 校验是否选择了模块
		if (allSelectIds.length === 0) {
			ElMessage.error("请选择报告模块");
			return;
		}

		// 构造请求参数（修复点6：补充所有必要字段）
		const saveParams: TemplateSaveParams = {
			tempName: form.tempName,
			tempType: Number(form.tempType),
			reportTypeIds: allSelectIds
		};
		// 编辑：添加ID
		if (prop.row.id) {
			saveParams.id = prop.row.id;
			await updTemplate(saveParams);
			ElMessage.success("修改成功");
		} else {
			// 新增
			await saveTemplate(saveParams);
			ElMessage.success("添加成功");
		}
		return true;
	});
});
</script>

<style scoped lang="scss">
.drawer-manage {
	.se-edit .ql-editor {
		height: 200px;
	}
	.el-drawer__close-btn {
		position: relative;
		top: -3px;
	}
	.el-drawer__header {
		margin-bottom: 0px;
	}
}
</style>

<style lang="scss" scoped>
// 图表高度
.charts-height {
	height: calc(52vh - 140px);
}
.video-main-con {
	display: flex;
}
.video-left-box {
	margin-right: 14px;
	width: 300px;
	height: calc(100vh - 210px);
	.tree-con {
		padding: 14px 0;
		overflow-y: auto;
		height: calc(100vh - 210px);
	}
}
.report-img {
	padding: 20px 0;
	width: 100%;
	height: calc(100vh - 224px);
	overflow-y: auto;
	text-align: center;
	box-sizing: border-box;
	img {
		max-width: 100%;
	}
}
.video-right-box {
	flex: 1;
	height: calc(100vh - 210px);
	display: flex;
	flex-direction: column;
}
.video-title {
	width: 100%;
	height: 40px;
	background: #f8f8f8;
	border-radius: 4px 4px 4px 4px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 12px;
	font-size: 16px;
	color: rgba(0, 0, 0, 0.85);

	.tree-header {
		display: flex;
		align-items: center;
		height: 40px;
		font-size: 14px;
	}
}

.mt10 {
  margin-top: 12px;
}

::v-deep {
	.el-card__body {
		padding: 0;
	}

	.el-card {
		border: 0;
	}
}
</style>
