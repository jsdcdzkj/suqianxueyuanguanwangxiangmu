<template>
	<div class="mainbody">
		<ElForm :inline="true" ref="formInstance" :model="form" :rules="rules" class="demo-form-inline">
			<ElRow :gutter="12">
				<ElCol :span="5">
					<ElFormItem prop="chooseTemple" style="width: 100%">
						<ElSelect v-model="form.chooseTemple" @change="handlerSelectTemplate" placeholder="请选择模板">
							<ElOption
								v-for="item in tempList"
								:key="item.id"
								:label="item.tempName"
								:value="item.id"
							></ElOption>
						</ElSelect>
					</ElFormItem>
				</ElCol>
				<ElCol :span="5">
					<ElFormItem prop="reportName" style="width: 100%">
						<ElInput v-model="form.reportName" placeholder="请输入报告名称" style="width: 100%"></ElInput>
					</ElFormItem>
				</ElCol>
				<ElCol :span="12" style="display: flex">
					<div>
						<ElFormItem style="margin-right: 12px">
							<ElRadioGroup v-model="timeType" @change="handlerTimeSelectChange">
								<ElRadioButton label="日"></ElRadioButton>
								<ElRadioButton label="月"></ElRadioButton>
								<ElRadioButton label="年"></ElRadioButton>
							</ElRadioGroup>
						</ElFormItem>
					</div>
					<div>
						<ElFormItem prop="startTime">
							<ElDatePicker
								v-if="timeType === '日'"
								v-model="value1"
								type="daterange"
								:key="daterange"
								range-separator="至"
								@change="handlerDayChange"
								value-format="YYYY-MM-DD"
								start-placeholder="开始日期"
								end-placeholder="结束日期"
							></ElDatePicker>
							<ElDatePicker
								v-if="timeType === '月'"
								v-model="value2"
								:key="month"
								type="month"
								placeholder="选择月"
								value-format="YYYY-MM"
								@change="handlerMonthChange"
							></ElDatePicker>
							<ElDatePicker
								v-if="timeType === '年'"
								v-model="value3"
								:key="year"
								type="year"
								placeholder="选择年"
								value-format="YYYY"
								@change="handlerYearChange"
							></ElDatePicker>
						</ElFormItem>
					</div>
				</ElCol>
			</ElRow>
		</ElForm>

		<div class="video-main-con">
			<!-- 左侧栏 -->
			<ElCard class="video-left-box" shadow="never">
				<div class="video-title">
					<span>区域选择</span>
					<div class="tree-header" v-if="selectTemplate.tempType === 2">
						<ElCheckbox @change="handleCheckAllChange" v-model="checkedAll"></ElCheckbox>
						<span style="margin-left: 10px">全部区域</span>
					</div>
				</div>
				<div class="tree-list">
					<ElCascader
						v-if="selectTemplate.tempType === 1"
						:options="data"
						:props="{ value: 'id' }"
						filterable
						v-model="selectTemplateOne"
						@change="handleCasaderChange"
					></ElCascader>
					<div class="tree-con" v-if="selectTemplate.tempType === 2">
						<div style="padding: 10px">
							<ElInput placeholder="输入关键字进行过滤" v-model="filterText"></ElInput>
						</div>
						<ElTree
							:data="data"
							:props="defaultProps"
							show-checkbox
							node-key="id"
							ref="treeRef"
							:highlight-current="true"
							default-expand-all
							:expand-on-click-node="false"
							:check-on-click-node="true"
							@node-click="handleNodeClick"
							@check="handleCheckChange"
							:default-checked-keys="['1-1']"
							:filter-node-method="filterNode"
						></ElTree>
					</div>
					<div class="btns" style="text-align: center; line-height: 100px" v-show="form.chooseTemple">
						<ElButton
							@click="genTemplateAlert"
							type="primary"
							plain
							:loading="genLoading"
							style="width: 100%; margin-top: 20px; border: 0"
						>
							生成
						</ElButton>
					</div>
				</div>
			</ElCard>

			<!-- 右侧栏 -->
			<div class="video-right-box">
				<ElCard
					shadow="never"
					v-loading="genLoading"
					:element-loading-text="loadText"
					element-loading-spinner="el-icon-loading"
					element-loading-background="rgba(0, 0, 0, 0.85)"
				>
					<div class="video-title">
						<span>模块信息预览</span>
					</div>

					<div class="mt10 alertContainer" id="alertContainer" ref="templateRef">
						<AlertHeader
							v-show="selectList.length > 0"
							:title="form.reportName"
							:timer="`${form.startTime}~${form.endTime}`"
						/>
						<template v-for="item in selectList" :key="item.id">
							<component
								:is="componentMap[child.type]"
								v-for="(child, index) in item.children"
								:key="child.id"
								:title="child.name"
								:index="`${child._subIndex}.${index + 1}`"
								:subIndex="child.subIndex"
								:subTitle="item.name"
								:startTime="form.startTime"
								:endTime="form.endTime"
								:areaIds="areaIds"
								:type="tempType"
								:timeType="timeType"
								:projectName="form.reportName"
								:chooseTemple="form.chooseTemple"
							></component>
						</template>
					</div>
				</ElCard>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, nextTick, defineProps, defineEmits } from "vue";
import type { FormInstance, FormRules, TreeInstance } from "element-plus";
// 导入Element Plus组件（已全局注册可删除，未全局注册保留）
import {
	ElMessageBox,
	ElRow,
	ElCol,
	ElForm,
	ElFormItem,
	ElSelect,
	ElOption,
	ElInput,
	ElRadioGroup,
	ElRadioButton,
	ElDatePicker,
	ElCard,
	ElCascader,
	ElCheckbox,
	ElTree,
	ElButton,
	ElMessage
} from "element-plus";
// 导入业务相关依赖
import HtmlToPdf from "@/components/HtmlToPdf";
import { getMonthLimit, getYearLimit } from "@/core/utils";
import {
	selectPageList,
	selectReportTemplateList,
	selectReportTypeTree,
	areaTreeList,
	addReport
} from "@/api/pipeline-maintenance/report-management";
import { getTemplateList } from "../components/ReportTemplate/index.js";
import { useDialogForm } from "@/core/dialog/dialog-container";
// 导入所有动态组件（根据实际组件路径调整）
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

// 创建组件映射
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


// ===================== 类型定义 =====================
// 模板列表项类型
interface TempListItem {
	id: number | string;
	tempName: string;
	tempType: number;
	reportTypeIds: (number | string)[];
	[key: string]: any;
}
// 树形节点类型（区域/报告类型）
interface TreeDataNode {
	id: number | string;
	label: string;
	name?: string;
	children?: TreeDataNode[];
	type?: string;
	pId?: number | string;
	checked?: boolean;
	subIndex?: number;
	_subIndex?: number;
	pName?: string;
	[key: string]: any;
}
// 表单数据类型
interface FormModel {
	startTime: string;
	endTime: string;
	chooseTemple: number | string;
	reportType: string;
	reportName: string;
}
// 选择的模板类型
interface SelectTemplate {
	tempName: string;
	tempType: number;
	[key: string]: any;
}
// 时间范围返回类型
interface TimeLimit {
	startTime: string;
	endTime: string;
}

// ===================== Props & Emits =====================
const props = defineProps<{
	row?: Record<string, any>; // 编辑时传入的行数据
}>();

const emit = defineEmits<{
	(e: "fetch-data"): void;
}>();

// ===================== 响应式数据定义 =====================
// 基础响应式变量
const filterText = ref("");
const tempList = ref<TempListItem[]>([]);
const timeType = ref<"日" | "月" | "年">("日");
const value1 = ref<string[]>([]); // 日期范围
const value2 = ref(""); // 月份
const value3 = ref(""); // 年份
const selectList = ref<TreeDataNode[]>([]);
const genLoading = ref(false);
const loadText = ref("报告生成中");
const checkedAll = ref(false);
// 修复原代码重复定义type问题，重命名为energyType
const energyType = ref([
	{ value: "电", label: "电" },
	{ value: "水", label: "水" },
	{ value: "气", label: "气" }
]);
const selectTemplateOne = ref<(number | string)[]>([]);
const areaIds = ref<(number | string)[]>([]);
const tempType = ref(1);
const selectTemplate = reactive<SelectTemplate>({ tempName: "", tempType: 0 });
const alarmType = ref<(number | string)[]>([]);
const allCheckedList = ref<(number | string)[]>([]);
const treeList = ref<TreeDataNode[]>([]);

// 表单相关
const form = reactive<FormModel>({
	startTime: "",
	endTime: "",
	chooseTemple: "",
	reportType: "",
	reportName: ""
});
const rules = reactive<FormRules>({
	chooseTemple: [{ required: true, trigger: "change", message: "请选择模板" }],
	startTime: [{ required: true, trigger: "blur", message: "请选择时间" }],
	reportName: [{ required: true, trigger: "blur", message: "请输入名称" }]
});

// 树形组件相关
const treeRef = ref<TreeInstance>();
// 初始树形数据
const data = ref<TreeDataNode[]>([
	{
		id: 1,
		label: "基础信息",
		children: [
			{
				id: 4,
				label: "单位信息",
				children: [
					{ id: 9, label: "空间信息" },
					{ id: 10, label: "设备信息" }
				]
			}
		]
	},
	{
		id: 2,
		label: "安全管理",
		children: [
			{ id: 5, label: "告警趋势" },
			{ id: 6, label: "告警重点区域" }
		]
	},
	{
		id: 3,
		label: "能源管理",
		children: [
			{ id: 7, label: "能耗用量月统计" },
			{ id: 8, label: "能耗用量年度统计" }
		]
	}
]);
const defaultProps = reactive({
	children: "children",
	label: "label"
});
const templateRef = ref<HTMLDivElement>(); // 预览容器ref

// ===================== 监听器 =====================
// 树形过滤
watch(filterText, (val) => {
	treeRef.value?.filter(val);
});

// ===================== 生命周期 =====================
onMounted(() => {
	checkList();
	// 初始化获取模板列表和区域树
	initData();
});

// ===================== 方法定义 =====================
/** 初始化数据：模板列表 + 区域树 */
const initData = async () => {
	try {
		// 并行请求提升性能
		const [tempRes, areaRes] = await Promise.all([
			selectReportTemplateList({ pageIndex: 1, pageSize: 999 }),
			areaTreeList({})
		]);
		tempList.value = tempRes.records;
		data.value = areaRes;
		checkList();
	} catch (err) {
		ElMessage.error("数据初始化失败");
		console.error(err);
	}
};

/** 级联选择器变化（模板类型1） */
const handleCasaderChange = (e: (number | string)[]) => {
	// 处理area_前缀，取最后一个节点
	const lastNode = e[e.length - 1] as string;
	areaIds.value = [lastNode.replace("area_", "")];
};

/** 选择模板变化 */
const handlerSelectTemplate = (e: number | string) => {
	const find = tempList.value.find((item) => item.id === e);
	if (find) {
		Object.assign(selectTemplate, find);
		console.log("selectTemplate11111111111", selectTemplate);
		selectList.value = [];
		handlerTemplateChange(find.tempType, find.reportTypeIds);
	}
	// 重置树形选择、级联选择、全选状态
	treeRef.value?.setCheckedKeys([]);
	checkedAll.value = false;
	selectTemplateOne.value = [];
};

/** 时间类型切换（日/月/年） */
const handlerTimeSelectChange = () => {
	form.startTime = "";
	form.endTime = "";
};

/** 日期范围变化（日） */
const handlerDayChange = (e: string[]) => {
	value2.value = "";
	value3.value = "";
	if (e && e.length === 2) {
		form.startTime = e[0];
		form.endTime = e[1];
	}
};

/** 模板类型变化，获取报告类型树 */
const handlerTemplateChange = async (type: number, select: (number | string)[]) => {
	tempType.value = type;
	try {
		const listRes = await selectReportTypeTree({ modelType: Number(type) });
		console.log("listRes", listRes);
		const templateList = getTemplateList();
		const selectList = select;

		// 处理报告类型树，匹配组件类型
		listRes.forEach((item: TreeDataNode) => {
			if (item.children && item.children.length > 0) {
				item.children.forEach((child: TreeDataNode) => {
					const find = templateList.find((it: any) => it.cname === child.name);
					if (find) {
						child.type = find.name;
						child.pId = child.id;
						child.checked = selectList.includes(child.id);
					}
					// 处理子节点
					if (child.children) {
						child.children.forEach((ch: TreeDataNode) => {
							ch.type = ch.name;
							ch.pId = child.id;
							ch.checked = false;
						});
					}
				});
			}
		});

		// 过滤告警类型
		alarmType.value = selectList.filter((item) => {
			return typeof item === "string" && item.startsWith("dict_");
		});
		treeList.value = listRes;
	} catch (err) {
		ElMessage.error("获取报告类型失败");
		console.error(err);
	}
};

/** 计算要渲染的模板列表 */
const computedTemplateList = (dataList: TreeDataNode[]) => {
	const list: TreeDataNode[] = [];
	let current = 0;

	dataList.forEach((item) => {
		const p: TreeDataNode[] = [];
		if (!item.children) return;
		item.children.forEach((child) => {
			// 选中的节点
			if (child.checked) {
				if (p.length === 0) current += 1;
				p.push({
					...child,
					pName: item.name,
					subIndex: p.length === 0 ? current : 0,
					_subIndex: current
				});
			}
			// 告警类型未选中但有告警的特殊情况
			else if ((child.pId === "type_19" || child.pId === "type_5") && alarmType.value.length > 0) {
				if (p.length === 0) current += 1;
				p.push({
					...child,
					pName: item.label,
					subIndex: p.length === 0 ? current : 0,
					_subIndex: current
				});
			}
		});
		if (p.length > 0) {
			list.push({ ...item, children: [...p] });
		}
	});
	selectList.value = list;
};

/** 树形全选/取消全选 */
const handleCheckAllChange = (e: boolean) => {
	if (!treeRef.value || !data.value) return;
	if (e) {
		data.value.forEach((item) => {
			treeRef.value?.setChecked(item.id, true, true);
		});
	} else {
		treeRef.value?.setCheckedKeys([]);
	}
};

/** 月份选择变化 */
const handlerMonthChange = (e: string) => {
	const d = getMonthLimit(e) as TimeLimit;
	form.startTime = d.startTime;
	form.endTime = d.endTime;
};

/** 年份选择变化 */
const handlerYearChange = (e: string) => {
	const d = getYearLimit(e) as TimeLimit;
	form.startTime = d.startTime;
	form.endTime = d.endTime;
};

/** 生成报告预览 */
const genTemplateAlert = () => {
	return new Promise((resolve) => {
		formInstance.value?.validate(async (valid) => {
			if (valid) {
				// 模板类型2时，获取树形选中的区域ID
				if (selectTemplate.tempType === 2) {
					if (!treeRef.value) return;
					const checkedKeys = treeRef.value.getCheckedKeys(true) as string[];
					areaIds.value = checkedKeys.map((item) => {
						return Number(item.replace("area_", ""));
					});
				}

				// 校验区域选择
				if (areaIds.value.length === 0) {
					ElMessage.error("请选择区域");
					return;
				}

				selectList.value = [];
				nextTick(() => {
					computedTemplateList(treeList.value);
					// 新增调试输出，检查selectList数据
					console.log("生成报告后的selectList:", selectList.value);
					resolve(true);
				});
			}
		});
	});
};

/** 树形节点过滤方法 */
const filterNode = (value: string, data: TreeDataNode) => {
	if (!value) return true;
	return data.label.indexOf(value) !== -1;
};

/** 树形节点点击（预留） */
const handleNodeClick = (data: TreeDataNode) => {};

/** 树形节点勾选变化 */
const handleCheckChange = (data: TreeDataNode, { checkedKeys }: { checkedKeys: (number | string)[] }) => {
	checkedAll.value = checkedKeys.length === allCheckedList.value.length;
};

/** 递归计算树形所有节点ID，用于全选判断 */
const checkList = () => {
	const list: (number | string)[] = [];
	const loop = (nodeList: TreeDataNode[], checkedList: (number | string)[]) => {
		nodeList.forEach((item) => {
			checkedList.push(item.id);
			if (item.children && item.children.length > 0) {
				loop(item.children, checkedList);
			}
		});
	};
	loop(data.value, list);
	allCheckedList.value = list;
};

/** 关闭弹窗/重置组件 */
const close = (done?: () => void) => {
	if (genLoading.value) return;
	emit("fetch-data");
	formInstance.value?.resetFields();
	if (done) {
		done();
	}
};

const { registerFormDone, formInstance } = useDialogForm();

registerFormDone(async () => {
	const valid = await formInstance.value?.validate().catch(() => false);
	if (!valid) return;

	// 校验区域和预览列表
	if (areaIds.value.length === 0) {
		ElMessage.error("请选择区域");
		throw "请选择区域";
	}
	if (selectList.value.length === 0) {
		ElMessage.error("请先生成报告");
		throw "请先生成报告";
	}

	genLoading.value = true;
	loadText.value = `报告生成中 0 %`;

	try {
		await nextTick();
		// 确保容器元素存在
		if (!templateRef.value) {
			ElMessage.error("PDF容器元素不存在");
			throw "PDF容器元素不存在";
		}
		// 使用DOM元素引用而非ID选择器
		const htmlToPdf = new HtmlToPdf(templateRef.value, {
			onProgress: (value: number) => {
				loadText.value = `报告生成中 ${value.toFixed(2)} %`;
			},
			fileName: form.reportName
		});
		await htmlToPdf.drawImage();
		const pdfBlob = await htmlToPdf.genPdf();

		// 转换为File对象
		const myFile = new File([pdfBlob as Blob], `${form.reportName}.pdf`, {
			type: "application/pdf"
		});

		// 上传报告并保存数据
		const data = {
			...form,
			areaIds: areaIds.value.join(",")
		};
		const formData = new FormData();
		formData.append("file", myFile);
		// formData.append("body", `${JSON.stringify(data)}`);
		Reflect.ownKeys(data).forEach((item) => {
			formData.append(item, data[item]);
		});
		const res = await addReport(formData);
		console.log("报告生成res", res);
		if (res.code === 0) {
			ElMessage.success("报告生成成功");
			emit("fetch-data");
			close();
		} else {
			ElMessage.error(res.msg || "报告生成失败");
			throw "报告生成失败，请重试";
		}
	} catch (e) {
    	console.error("报告生成失败", e);
		ElMessage.error("报告生成失败，请重试");
		throw "报告生成失败，请重试";
	} finally {
		genLoading.value = false;
	}
});
</script>

<style scoped lang="scss">
.alertContainer {
	height: calc(100vh - 269px);
	overflow-y: auto;
	width: 814px;
	padding-bottom: 20px;
	padding-right: 15px;
	background: #fff;
	overflow-x: hidden;

	div {
		background: #fff;
	}
}

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

// 图表高度
.charts-height {
	height: calc(52vh - 140px);
}

.video-main-con {
	display: flex;
}

.video-left-box {
	margin-right: 12px;
	width: 300px;
	height: calc(100vh - 216px);

	.tree-list {
		display: flex;
		flex-direction: column;
		height: calc(100vh - 226px);
		padding: 14px 0;

		.tree-con {
			flex: 1;
			overflow-y: auto;
			border: 1px #eee solid;
			border-radius: 6px;
		}

		.btns {
			height: 100px;
		}
	}
}

.video-right-box {
	flex: 1;
	height: calc(100vh - 216px);
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

// Vue3 标准样式穿透语法
:deep(.el-card__body) {
	padding: 0;
}

:deep(.el-card) {
	border: 0;
}
</style>