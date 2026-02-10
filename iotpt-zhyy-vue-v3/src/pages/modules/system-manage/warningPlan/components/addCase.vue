<template>
	<div v-loading="loadingView">
		<div class="module-card">
			<header class="module-card__header">
				<div class="module-card__title-wrapper">
					<span class="module-card__title-line"></span>

					<h3 class="module-card__title" @click="handleBack" style="cursor: pointer">
						<i class="ri-arrow-left-line mr-8px" style="font-size: 22px"></i>
						{{ title }}
					</h3>
				</div>

				<div class="module-card__actions">
					<ElButton type="success" @click="handleMoudle" v-if="!isDisabled">
						<span style="display: flex; align-items: center"> 模板管理 </span>
					</ElButton>
					<ElButton type="primary" @click="addModule" v-if="!isDisabled">
						<span style="display: flex; align-items: center"> 新增模板 </span>
					</ElButton>
				</div>
			</header>
			<div class="module-card__divider"></div>
			<div class="module-card__body">
				<ElRow :gutter="16">
					<ElCol :span="8">
						<div class="card-info">
							<div class="title">基础信息</div>
							<ElForm
								:model="queryParamsBase"
								:rules="rules"
								label-position="top"
								ref="queryParamsBaseRef"
							>
								<ElFormItem label="预案名称：" prop="planName">
									<ElInput
										v-model="queryParamsBase.planName"
										style="width: 100%"
										placeholder="请输入预案名称"
										:disabled="isDisabled"
									/>
								</ElFormItem>
								<ElFormItem label="时间计划：" prop="planTimeConfigId">
									<ElSelect
										v-model="queryParamsBase.planTimeConfigId"
										style="width: 100%"
										placeholder="请选择时间计划"
										:disabled="isDisabled"
									>
										<ElOption
											v-for="item in timePlanList"
											:key="item.id"
											:label="item.name"
											:value="item.id"
										/>
									</ElSelect>
								</ElFormItem>
							</ElForm>
						</div>
					</ElCol>
					<ElCol :span="16">
						<div class="card-info">
							<div class="title">触发报警</div>
							<div class="warning-set">
								<div class="set-card border" style="margin-right: 16px">
									<div class="card-title">
										<span>1.选择报警内容</span>
										<ElButton :icon="Plus" @click="chooseContent" v-if="!isDisabled">
											添加
										</ElButton>
									</div>
									<div class="set-tag-con">
										<ElTag
											v-for="item in checkedTags"
											:key="item.id"
											:closable="!isDisabled"
											@close="handleClose(item)"
										>
											{{ item.label }}
										</ElTag>
									</div>
								</div>
								<div class="set-card">
									<ElTable
										v-loading="listLoading"
										:data="linkageRuleList"
										:element-loading-text="elementLoadingText"
										height="200"
										border
									>
										<ElTableColumn
											show-overflow-tooltip
											prop="deviceName"
											label="2.请设置联动规则"
											align="center"
										/>
										<ElTableColumn
											show-overflow-tooltip
											prop="enableStatus"
											label="启用状态"
											align="center"
										>
											<template #default="scope">
												<ElSwitch
													v-model="scope.row.enableStatus"
													active-value="1"
													inactive-value="0"
													:disabled="isDisabled"
													@change="handleEnableStatusChange(scope.row)"
												/>
											</template>
										</ElTableColumn>
									</ElTable>
								</div>
							</div>
						</div>
					</ElCol>
					<ElCol :span="24">
						<div class="card-info" style="margin-bottom: 14px">
							<div class="title">通知设置</div>
							<div class="notice">
								<div class="left-card">
									<div
										v-for="(item, index) in pushList"
										:key="item.value"
										class="push-card"
										:class="{ 'is-checked': pushConfig[item.value] }"
										:style="{ marginTop: index > 0 ? '24px' : '0' }"
										@click="isDisabled ? null : togglePushConfig(item.value)"
									>
										<div class="push-items">
											<img
												v-if="item.label.includes('APP')"
												src="@/assets/images/setting/warningPlan/push-app.png"
												alt=""
											/>
											<img
												v-else-if="item.label.includes('PC')"
												src="@/assets/images/setting/warningPlan/push-pc.png"
												alt=""
											/>
											<img
												v-else-if="item.label.includes('短信')"
												src="@/assets/images/setting/warningPlan/push-mess.png"
												alt=""
											/>
											<img
												v-else
												src="@/assets/images/setting/warningPlan/push-phone.png"
												alt=""
											/>
											{{ item.label }}
										</div>
										<div class="check-box">
											<ElCheckbox
												v-model="pushConfig[item.value]"
												:true-label="item.value"
												:false-label="null"
												:disabled="isDisabled"
												size="large"
											/>
											<div class="checkCover"></div>
										</div>
									</div>
								</div>
								<div style="flex: 1; width: 0">
									<ElForm :model="queryParams" :rules="rules" ref="queryParamsRef">
										<ElRow :gutter="16">
											<ElCol :span="24">
												<ElFormItem label="推送方式：" prop="handleStatus">
													<ElRadioGroup v-model="pushType" :disabled="isDisabled">
														<ElRadio
															:label="item.value"
															v-for="item in alarmPushTypeList"
															:key="item.value"
															:disabled="isDisabled"
														>
															{{ item.label }}
														</ElRadio>
													</ElRadioGroup>
												</ElFormItem>
											</ElCol>
											<template v-if="!isDisabled">
												<ElCol :span="6" v-if="pushType == 1">
													<ElFormItem>
														<ElInput
															v-model="queryParams.deptName"
															style="width: 100%"
															placeholder="请输入部门名称"
															@keyup.enter="handleQuery"
														/>
													</ElFormItem>
												</ElCol>
												<ElCol :span="6" v-if="pushType == 1">
													<ElFormItem>
														<ElInput
															v-model="queryParams.address"
															style="width: 100%"
															placeholder="请选择区域"
															@keyup.enter="handleQuery"
														/>
													</ElFormItem>
												</ElCol>
												<ElCol :span="6" v-if="pushType == 1">
													<ElFormItem>
														<ElButton :icon="Search" type="primary" @click="handleQuery">
															查询
														</ElButton>
														<ElButton :icon="Refresh" @click="handleReset"> 重置 </ElButton>
													</ElFormItem>
												</ElCol>
											</template>
										</ElRow>
									</ElForm>
									<div v-if="pushType == 1">
										<ElTable
											ref="orgTableRef"
											v-loading="listLoading"
											:data="orgListData"
											:element-loading-text="elementLoadingText"
											:height="getTableHeight()"
											style="width: 100%; min-height: 322px"
											@selection-change="handleSelectionChange"
											border
											:row-key="getRowKey"
											:row-selectable="rowSelectable"
											stripe
										>
											<ElTableColumn
												show-overflow-tooltip
												prop="deptName"
												label="部门名称"
												min-width="120"
												align="center"
											/>
											<ElTableColumn
												show-overflow-tooltip
												prop="address"
												min-width="200"
												label="包含区域"
												align="center"
											/>
											<ElTableColumn
												type="selection"
												width="80"
												align="center"
												v-if="!isDisabled"
											/>
										</ElTable>
									</div>
									<div v-if="pushType == 2" key="pushType-2">
										<span style="font-size: 14px">请选择指定用户:</span>
										<div class="alarm-wrap">
											<div class="alarm-con-title">
												<div class="alarm-items">
													<div class="alarm-title">单位名称</div>
												</div>
												<div class="alarm-items">
													<div class="alarm-title">部门名称</div>
												</div>
												<div class="alarm-items">
													<div class="alarm-title">组员名称</div>
												</div>
											</div>
											<div class="check-con" :class="isDisabled ? 'panelDisabled' : ''">
												<ElCascaderPanel
													ref="cascaderPanelUserRef"
													v-model="checkUserList"
													:options="optionsUser"
													:props="propsUser"
													:default-expanded-keys="defaultExpandedKeysUser"
												>
													<template #default="{ node, data }">
														<span>{{ data.label || "未找到名称" }}</span>
														<span v-if="!node.isLeaf">({{ data.children.length }})</span>
													</template>
												</ElCascaderPanel>
											</div>
										</div>
									</div>
									<div v-if="pushType == 3" key="pushType-3">
										<span style="font-size: 14px">请选择指定用户:</span>
										<div class="alarm-wrap">
											<div class="alarm-con-title">
												<div class="alarm-items">
													<div class="alarm-title">班组名称</div>
												</div>
												<div class="alarm-items">
													<div class="alarm-title">组员名称</div>
												</div>
											</div>
											<div class="check-con" :class="isDisabled ? 'panelDisabled' : ''">
												<ElCascaderPanel
													ref="cascaderPanelOperationRef"
													v-model="checkOperationList"
													:options="optionsOperation"
													:props="propsOperation"
													:default-expanded-keys="defaultExpandedKeysOperation"
												>
													<template #default="{ node, data }">
														<span>{{ data.name || "未找到名称" }}</span>
														<span v-if="!node.isLeaf">({{ data.children.length }})</span>
													</template>
												</ElCascaderPanel>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</ElCol>
				</ElRow>
				<div style="text-align: right">
					<ElButton @click="close">取消</ElButton>
					<ElButton
						type="primary"
						@click="handleSave"
						:loading="saveLoading"
						v-if="!isDisabled"
						:disabled="saveLoading"
					>
						保存
					</ElButton>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { ref, onMounted } from "vue";
	// 补全使用的Element Plus组件
	import {
		ElMessage,
		ElMessageBox,
		ElPagination,
		ElTag,
		ElSwitch,
		ElEmpty,
		ElIcon,
		ElButton,
		ElForm,
		ElFormItem,
		ElInput,
		ElSelect,
		ElOption,
		ElUpload,
		ElTable,
		ElTableColumn,
		ElCascaderPanel
	} from "element-plus";

	import { Back, Plus, Search, Refresh } from "@element-plus/icons-vue";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import { getRedisDictList } from "@/api/setting/sysDict";
	import {
		alarmContentList,
		getOrgList,
		getUserTree,
		teamGroupsTree,
		saveAlarmPlan,
		alarmPlanTimePage,
		getPlanById
	} from "@/api/setting/warnInfo";
	import TimePlanMoudle from "./timePlanMoudle.vue";
	import ChooseAlarmContent from "./chooseAlarmContent.vue";
	import AddTimeMoudle from "./addTimeMoudle.vue";

	// Props定义
	const props = defineProps({
		editId: {
			type: [String, Number],
			default: null
		},
		viewStatus: {
			type: Boolean,
			default: false
		}
	});

	// Emits定义
	const emit = defineEmits(["back", "fetch-data"]);

	// Refs
	const queryParamsBaseRef = ref(null);
	const queryParamsRef = ref(null);
	const orgTableRef = ref(null);
	const cascaderPanelUserRef = ref(null);
	const cascaderPanelOperationRef = ref(null);

	// 响应式数据
	const loadingView = ref(false);
	const title = ref("新增");
	const listLoading = ref(false);
	const elementLoadingText = ref("正在加载...");
	const orgListData = ref([]);
	const queryParamsBase = ref({
		planName: "",
		planTimeConfigId: "",
		enable: 1
	});
	const rules = {
		planName: [{ required: true, message: "请输入预案名称", trigger: "blur" }],
		planTimeConfigId: [{ required: true, message: "请选择时间计划", trigger: "blur" }]
	};
	const pushType = ref("1");
	const alarmPushTypeList = ref([]);
	const parentCheckList = ref([]);
	const checkedTags = ref([]);
	const alarmContentData = ref([]);
	const pushConfig = ref({});
	const pushList = ref([]);
	const multipleSelection = ref([]);
	const propsUser = {
		multiple: true,
		expandTrigger: "hover",
		value: "id",
		label: "label",
		children: "children",
		checkStrictly: false,
		emitPath: true
	};
	const optionsUser = ref([]);
	const defaultExpandedKeysUser = ref([]);
	const checkUserList = ref([]);
	const propsOperation = {
		multiple: true,
		expandTrigger: "hover",
		value: "id",
		label: "name",
		children: "children",
		checkStrictly: false
	};
	const optionsOperation = ref([]);
	const defaultExpandedKeysOperation = ref([]);
	const checkOperationList = ref([]);
	const queryParams = ref({
		deptName: "",
		address: ""
	});
	const linkageRule = ref([]);
	const linkageRuleList = ref([]);
	const timePlanList = ref([]);
	const saveLoading = ref(false);
	const isDisabled = ref(false);

	// 方法定义
	const handleMoudle = async () => {
		createDrawerAsync(
			{
				title: "时间计划模板",
				width: "960px",
				showCancel: true,
				showConfirm: false
			},
			{},
			<TimePlanMoudle />
		).then(() => {
			getTimePlanList();
		});
	};

	const addModule = async () => {
		createModelAsync(
			{
				title: "新增模板",
				width: "1150px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<AddTimeMoudle />
		).then(() => {
			getTimePlanList();
		});
	};

	const chooseContent = async () => {
		createModelAsync(
			{
				title: "选择告警内容",
				width: "960px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{
				preSelectedIds: parentCheckList.value,
				editId: props.editId
			},
			<ChooseAlarmContent />
		).then((res) => {
			if (res && Array.isArray(res)) {
				checkedTags.value = res;
				parentCheckList.value = res.map((item) => item.id);
			}
		});
	};

	const handleGetAlarmContent = (checkList) => {
		parentCheckList.value = checkList;
		checkedTags.value = [];
		checkList.forEach((id) => {
			const matchedItem = alarmContentData.value.find((item) => item.id == id); // 使用新名称
			if (matchedItem) {
				checkedTags.value.push({
					id: matchedItem.id,
					label: matchedItem.alarmContent || matchedItem.name
				});
			}
		});
	};

	const handleClose = (item) => {
		checkedTags.value = checkedTags.value.filter((tag) => tag.id !== item.id);
		parentCheckList.value = checkedTags.value.map((tag) => tag.id);
	};

	const handleEnableStatusChange = (row) => {
		if (row.enableStatus === "1") {
			if (!linkageRule.value.includes(row.id)) {
				linkageRule.value.push(row.id);
			}
		} else {
			linkageRule.value = linkageRule.value.filter((id) => id !== row.id);
		}
	};

	const handleQuery = () => {
		const { deptName, address } = queryParams.value;
		orgListData.value = orgListData.value.filter((item) => {
			const deptMatch = deptName ? item.deptName.includes(deptName) : true;
			const addressMatch = address ? item.address.includes(address) : true;
			return deptMatch && addressMatch;
		});
	};

	const handleReset = () => {
		queryParams.value.deptName = "";
		queryParams.value.address = "";
		fetchOrgList();
	};

	const handleSelectionChange = (val) => {
		multipleSelection.value = val;
	};

	const rowSelectable = (row) => {
		return !isDisabled.value;
	};

	const getRowKey = (row) => {
		return String(row.id);
	};

	const getTableHeight = () => {
		return isDisabled.value ? "calc(100vh - 664px)" : "calc(100vh - 714px)";
	};

	const togglePushConfig = (value) => {
		pushConfig.value[value] = !pushConfig.value[value];
	};

	const close = () => {
		handleBack();
	};

	const handleBack = () => {
		emit("back");
	};

	const handleSave = async () => {
		try {
			saveLoading.value = true;
			const valid = await queryParamsBaseRef.value.validate();
			if (!valid) return;

			const selectedAlarmContent = parentCheckList.value;
			if (selectedAlarmContent.length === 0) {
				ElMessage.warning("请选择告警内容");
				return;
			}

			const linkageRuleList = linkageRule.value;
			if (linkageRuleList.length === 0) {
				ElMessage.warning("请选择联动规则");
				return;
			}

			const pushConfigList = Object.keys(pushConfig.value).filter((key) => pushConfig.value[key]);
			const multipleSelection = multipleSelection.value.map((item) => item.id);
			const checkUserList = checkUserList.value
				.map((item) => item[item.length - 1])
				.filter((item) => item !== null);
			const checkOperationList = checkOperationList.value
				.map((item) => item[item.length - 1])
				.filter((item) => item !== null);

			if (linkageRuleList.includes("3")) {
				if (!pushConfigList.length) {
					ElMessage.warning("请选择推送配置");
					return;
				}
				if (pushType.value == 1 && multipleSelection.length == 0) {
					ElMessage.warning("请选择推送对象");
					return;
				} else if (pushType.value == 2 && checkUserList.length == 0) {
					ElMessage.warning("请选择推送对象");
					return;
				} else if (pushType.value == 3 && checkOperationList.length == 0) {
					ElMessage.warning("请选择推送对象");
					return;
				}
			}

			let pushUserList = [];
			if (pushType.value == 1 && multipleSelection.length > 0) {
				pushUserList = multipleSelection;
			} else if (pushType.value == 2 && checkUserList.length > 0) {
				pushUserList = checkUserList;
			} else if (pushType.value == 3 && checkOperationList.length > 0) {
				pushUserList = checkOperationList;
			}

			const params = {
				pushConfigList,
				pushType: pushType.value,
				alarmContentList: selectedAlarmContent,
				linkageRuleList,
				pushUserList,
				...queryParamsBase.value
			};

			const res = await saveAlarmPlan(params);
			if (res && res.code == 0) {
				ElMessage.success("保存成功");
				handleBack();
				emit("fetch-data");
			} else {
				ElMessage.error(res?.msg || "保存失败");
			}
		} catch (error) {
			console.error("保存出错:", error);
			ElMessage.error(error.message || "保存失败");
		} finally {
			saveLoading.value = false;
		}
	};

	// API调用方法
	const getTimePlanList = async () => {
		const data = await alarmPlanTimePage({
			pageNo: 1,
			pageSize: 1000
		});
		if (data && data.records) {
			timePlanList.value = data.records.map((item) => ({
				id: item.id,
				name: item.name
			}));
		}
	};

	const getLinkageRuleList = async () => {
		const data = await getRedisDictList({ dictType: "alarmLinkageRule" });
		linkageRuleList.value = data.map((item) => ({
			id: item.dictValue,
			deviceName: item.dictLabel,
			enableStatus: 0
		}));
	};

	const getWarnRecordsData = async () => {
		const data = await getRedisDictList({ dictType: "alarmPushType" });
		alarmPushTypeList.value = data.map((item) => ({
			label: item.dictLabel,
			value: item.dictValue
		}));
	};

	const getHandlePushConfig = async () => {
		const data = await getRedisDictList({ dictType: "alarmPushConfig" });
		pushList.value = data.map((item) => ({
			label: item.dictLabel,
			value: item.dictValue
		}));
		pushList.value.forEach((item) => {
			pushConfig.value[item.value] = false;
		});
	};

	const fetchOrgList = async () => {
		const res = await getOrgList({ parentId: 0 });
		if (res && Array.isArray(res)) {
			orgListData.value = res;
		}
	};

	const fetchUserTree = async () => {
		const res = await getUserTree();
		if (res && Array.isArray(res)) {
			const parentNode = {
				id: 0,
				label: "鼎驰科技",
				children: res
			};
			optionsUser.value = [parentNode];
		}
	};

	const getTeamGroupsTree = async () => {
		const res = await teamGroupsTree();
		if (res && Array.isArray(res)) {
			const transformedData = res.map((item) => ({
				id: item.id,
				name: item.name,
				children: item.list.map((member) => ({
					id: member.id,
					name: member.realName
				}))
			}));
			optionsOperation.value = transformedData;
		}
	};

	const getAlarmContentList = async () => {
		const res = await alarmContentList();
		if (res && Array.isArray(res)) {
			alarmContentData.value = res;
		}
	};

	// 过滤树结构，只保留选中节点及其所有父节点
	const filterTreeBySelectedIds = (nodes, selectedIds, valueKey = "id") => {
		const selectedNodeIds = new Set(selectedIds);
		const resultNodes = [];

		const findAndKeepPath = (node) => {
			if (!node) return false;

			// 检查当前节点或其子节点是否在选中列表中
			const isSelected = selectedNodeIds.has(String(node[valueKey]));
			let childSelected = false;

			if (node.children && node.children.length > 0) {
				node.children = node.children.filter((child) => findAndKeepPath(child));
				childSelected = node.children.length > 0;
			}

			return isSelected || childSelected;
		};

		for (const node of nodes) {
			if (findAndKeepPath(node)) {
				resultNodes.push(node);
			}
		}

		return resultNodes;
	};
	// 设置级联选择器的值
	const setCascaderValue = (selectedIds) => {
		if (!Array.isArray(selectedIds) || selectedIds.length === 0) {
			console.log("selectedIds 为空或不是数组，无法设置级联选择器值");
			return;
		}
		if (!optionsUser.value.length) {
			console.log("optionsUser 为空，无法设置级联选择器值");
			return;
		}
		const paths = [];
		const findPath = (nodes, targetId, currentPath = []) => {
			if (!nodes || !Array.isArray(nodes)) return null;
			for (const node of nodes) {
				const newPath = [...currentPath, node[propsUser.value]];
				if (String(node[propsUser.value]) === String(targetId)) {
					return newPath;
				}
				if (node.children && node.children.length > 0) {
					const result = findPath(node.children, targetId, newPath);
					if (result) return result;
				}
			}
			return null;
		};

		selectedIds.forEach((id) => {
			const path = findPath(optionsUser.value, id);
			if (path) {
				paths.push(path);
			} else {
				console.log(`未找到 ID 为 ${id} 的路径`);
			}
		});

		checkUserList.value = paths;
	};

	// 设置操作级联选择器的值
	const setOperationCascaderValue = (selectedIds) => {
		if (!Array.isArray(selectedIds) || selectedIds.length === 0) {
			console.log("selectedIds 为空或不是数组，无法设置级联选择器值");
			return;
		}
		if (!optionsOperation.value.length) {
			console.log("optionsOperation 为空，无法设置级联选择器值");
			return;
		}
		const paths = [];
		const findPath = (nodes, targetId, currentPath = []) => {
			if (!nodes || !Array.isArray(nodes)) return null;
			for (const node of nodes) {
				const newPath = [...currentPath, node[propsOperation.value]];
				if (String(node[propsOperation.value]) === String(targetId)) {
					return newPath;
				}
				if (node.children && node.children.length > 0) {
					const result = findPath(node.children, targetId, newPath);
					if (result) return result;
				}
			}
			return null;
		};

		selectedIds.forEach((id) => {
			const path = findPath(optionsOperation.value, id);
			if (path) {
				paths.push(path);
			} else {
				console.log(`未找到 ID 为 ${id} 的路径`);
			}
		});

		checkOperationList.value = paths;
	};

	// 生命周期钩子
	onMounted(async () => {
		if (props.viewStatus) {
			isDisabled.value = true;
		}

		await Promise.all([
			getWarnRecordsData(),
			getAlarmContentList(),
			getHandlePushConfig(),
			fetchOrgList(),
			fetchUserTree(),
			getTeamGroupsTree(),
			getLinkageRuleList(),
			getTimePlanList()
		]);

		if (props.editId) {
			loadingView.value = true;
			try {
				const res = await getPlanById({ id: props.editId });
				console.log("res:", res);
				queryParamsBase.value = {
					id: res.id,
					planName: res.planName,
					planTimeConfigId: res.planTimeConfigId,
					enable: res.enable
				};
				title.value = props.viewStatus ? "详情" : "编辑";

				// 处理告警内容
				parentCheckList.value = res.alarmContentList;
				handleGetAlarmContent(parentCheckList.value);

				// 处理联动规则
				const linkageRuleArr = (res.linkageRule || "").split("，").filter(Boolean);
				const flatLinkageRuleArr = [];
				linkageRuleArr.forEach((item) => {
					flatLinkageRuleArr.push(...item.split(","));
				});
				linkageRuleList.value.forEach((item) => {
					item.enableStatus = flatLinkageRuleArr.includes(item.id.toString()) ? "1" : "0";
				});
				linkageRule.value = flatLinkageRuleArr;

				// 处理推送配置
				const pushConfigList = res.pushConfigList || [];
				pushList.value.forEach((item) => {
					pushConfig.value[item.value] = false;
				});
				pushConfigList.forEach((value) => {
					if (pushConfig.value.hasOwnProperty(value)) {
						pushConfig.value[value] = true;
					}
				});

				// 处理推送方式
				pushType.value = res.pushType.toString();
				if (res.pushType == 1) {
					const pushUserList = res.pushUserList;
					await fetchOrgList();
					if (isDisabled.value) {
						orgListData.value = orgListData.value.filter((item) => pushUserList.includes(String(item.id)));
					}
					await nextTick();
					if (orgTableRef.value) {
						orgTableRef.value.clearSelection();
						orgListData.value.forEach((item) => {
							if (pushUserList.includes(String(item.id))) {
								orgTableRef.value.toggleRowSelection(item, true);
							}
						});
					}
				} else if (res.pushType == 2) {
					const pushUserList = res.pushUserList;
					await fetchUserTree();
					if (isDisabled.value) {
						optionsUser.value = filterTreeBySelectedIds(optionsUser.value, pushUserList);
					}
					setCascaderValue(pushUserList);
				} else if (res.pushType == 3) {
					const pushUserList = res.pushUserList;
					await getTeamGroupsTree();
					if (isDisabled.value) {
						optionsOperation.value = filterTreeBySelectedIds(optionsOperation.value, pushUserList);
					}
					setOperationCascaderValue(pushUserList);
				}
			} catch (error) {
				console.error("获取编辑详情失败", error);
				ElMessage.error("获取编辑详情失败，请稍后重试");
			} finally {
				loadingView.value = false;
			}
		}
	});
</script>

<style scoped lang="scss">
	.module-card__title {
		display: flex;
		align-items: center;
	}
	.card-info {
		background: #ffffff;
		box-shadow: 0px 0px 7px 0px rgba(46, 119, 221, 0.15);
		border-radius: 12px;
		border: 1px solid rgba(0, 0, 0, 0.15);
		padding: 12px;
		min-height: 276px;
		margin-bottom: 12px;

		.title {
			padding-left: 12px;
			font-size: 16px;
			color: #333;
			line-height: 40px;
			margin-bottom: 12px;
			height: 40px;
			background: #f8f8f8;
			border-radius: 4px;
		}

		.notice {
			display: flex;
			.left-card {
				height: calc(100vh - 614px);
				display: flex;
				justify-content: center;
				flex-direction: column;
			}
			.push-card {
				flex: 1;
				width: 272px;
				background: linear-gradient(0deg, #ffffff 0%, #ffffff 100%);
				border-radius: 12px 12px 12px 12px;
				border: 1px solid #f0f0f0;
				display: flex;
				margin-right: 24px;
				display: flex;
				align-items: center;
				padding: 16px;
				justify-content: space-between;
				cursor: pointer;
				img {
					width: 56px;
					height: 56px;
					margin-right: 16px;
				}

				.push-items {
					display: flex;
					align-items: center;
				}
				&.is-checked {
					border-top: 4px solid #345bad;
				}
			}
		}

		.warning-set {
			display: flex;

			.set-card {
				height: 200px;
				flex: 1;
				&.border {
					border: 1px solid #f0f0f0;
					border-radius: 12px;
					overflow: hidden;
				}
				.card-title {
					margin-bottom: 12px;
					height: 48px;
					padding: 0 16px;
					font-size: 14px;
					color: rgba(0, 0, 0, 0.85);
					display: flex;
					justify-content: space-between;
					align-items: center;
					background: #f4f7ff;
				}
			}
		}
	}
	:deep(.el-drawer__header) {
		padding: 10px !important;
		margin-bottom: 14px;
		border-bottom: 1px solid #d1d9e1;

		.drawer-title {
			font-size: 16px;
			line-height: 16px;
			color: rgb(40, 53, 95);
			display: flex;
			align-items: center;
			position: relative;
			&::before {
				content: "";
				position: absolute;
				left: -10px;
				width: 4px;
				height: 45px;
				background: #537cdb;
			}
			b {
				display: block;
				font-size: 16px;
				line-height: 16px;
				margin-top: 2px;
				margin-right: 4px;
				font-weight: normal;
				margin-left: 8px;
			}
		}
	}

	:deep(.el-tag) {
		margin: 0 0px 6px 8px;
	}

	:deep(.el-cascader-panel) {
		display: flex;
		width: 100%;
		height: 100%;
	}
	:deep(.el-cascader-menu) {
		width: 33%;
		height: 100%;
	}
	:deep(.el-cascader-menu__wrap) {
		height: 100%;
	}
	.alarm-wrap {
		border: 1px solid rgba(0, 0, 0, 0.15);
		margin-top: 20px;
		.alarm-con-title {
			display: flex;
			.alarm-items {
				width: 33%;
				.alarm-title {
					height: 40px;
					border-right: 1px solid #f0f0f0;
					border-bottom: 1px solid #f0f0f0;
					position: relative;
					padding-left: 28px;
					display: flex;
					align-items: center;

					&::before {
						content: "";
						position: absolute;
						width: 4px;
						height: 16px;
						background: var(--el-color-primary);
						top: 12px;
						left: 16px;
					}
				}
			}
		}
	}
	.check-con {
		min-height: 292px;
		height: calc(100vh - 746px);
	}

	.check-box {
		height: calc(100% - 40px);
		display: flex;
		flex-direction: column;
		justify-content: center;
	}
	.checkboxList {
		display: flex;
		flex-direction: column;
		padding: 16px;
		height: 100%;
		overflow-y: auto;
		border-right: 1px solid #f0f0f0;

		:deep(.el-checkbox__label) {
			line-height: 32px;
		}
	}
	.set-tag-con {
		height: calc(100% - 64px);
		overflow-y: auto;
	}
	.panelDisabled {
		:deep(.el-checkbox) {
			display: none;
		}
	}
	.check-box {
		position: relative;
		.checkCover {
			position: absolute;
			top: 2px;
			left: 0;
			width: 100%;
			height: 100%;
			z-index: 9;
		}
	}
	:deep(.set-card .el-table th.el-table__cell) {
		background: #f4f7ff !important;
	}
	:deep(.el-table--fit.el-table--border) {
		border-radius: 12px;
		border: 1px solid #efefef;
		border-bottom: none;
	}
	:deep(.el-table--border::before) {
		width: 0;
	}
	:deep(.el-table--border::after) {
		width: 0;
	}
	:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
		background-color: #f4f7ff;
	}
</style>
