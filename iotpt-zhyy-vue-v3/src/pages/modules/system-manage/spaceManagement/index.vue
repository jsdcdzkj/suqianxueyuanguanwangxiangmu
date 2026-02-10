<template>
	<div class="h-full flex gap-12px p-12px">
		<div class="flex-1 w-full relative" v-loading="loading">
			<ElRow :gutter="15">
				<!-- 左侧区域 -->
				<ElCol :lg="5" :xl="4">
					<StructureCard title="楼宇信息">
						<template #actions>
							<ElButton type="success" @click="buildingAdd" :icon="Plus">新增</ElButton>
						</template>

						<div class="block">
							<ElTree
								ref="treeRef"
								:data="data"
								node-key="id"
								default-expand-all
								:current-node-key="1"
								:highlight-current="true"
								@node-click="nodeClick"
								:expand-on-click-node="false"
							>
								<template #default="{ node, data }">
									<span class="custom-tree-node">
										<div class="left-des-con">
											<ElIcon
												v-if="node.level === 1"
												:class="['icon', node.isCurrent ? 'blue' : '']"
											>
												<OfficeBuilding />
											</ElIcon>
											<ElIcon v-else :class="['icon', node.isCurrent ? 'blue' : '']">
												<Files />
											</ElIcon>
											<span
												:class="[
													node.childNodes.length ? 'bold' : '',
													node.isCurrent ? 'blue' : ''
												]"
											>
												{{ node.label }}
											</span>
										</div>
										<span class="right-edit-con">
											<ElButton
												v-if="node.data.type === '0'"
												type="success"
												link
												@click.stop="plus(node, data)"
											>
												<ElIcon><Plus /></ElIcon>
											</ElButton>
											<ElButton type="primary" link @click.stop="editTree(data)">
												<ElIcon><Edit /></ElIcon>
											</ElButton>
											<ElButton type="danger" link @click.stop="remove(node, data)">
												<ElIcon><Delete /></ElIcon>
											</ElButton>
										</span>
									</span>
								</template>
							</ElTree>
						</div>
					</StructureCard>
				</ElCol>

				<!-- 右侧区域 -->
				<ElCol :lg="19" :xl="20">
					<div class="h-full flex gap-12px p-12px">
						<div class="flex-1 custom-table h-full flex flex-col w-full relative">
							<ElTabs v-model="activeName" class="demo-tabs">
								<!-- 区域信息添加 -->
								<ElTabPane name="second">
									<template #label> 区域列表 </template>
									<ElRow :gutter="20">
										<ElCol :span="20">
											<ElForm :inline="true" :model="queryForm" @submit.prevent>
												<ElFormItem>
													<ElInput
														v-model.trim="queryForm.areaName"
														placeholder="请输入区域名称"
														@input="tempInput"
														clearable
														@keyup.enter="queryData"
													/>
												</ElFormItem>
												<ElFormItem>
													<ElButton type="primary" @click="queryData" :icon="Search">
														查询
													</ElButton>
													<ElButton @click="resetForm" :icon="Refresh"> 重置 </ElButton>
												</ElFormItem>
											</ElForm>
										</ElCol>
										<ElCol :span="4">
											<ElButton type="success" plain @click="handleEdit"> 添加 </ElButton>
										</ElCol>
									</ElRow>

									<ElTable
										v-loading="listLoading"
										:data="list"
										:element-loading-text="elementLoadingText"
										height="calc(100vh - 296px)"
										border
									>
										<ElTableColumn
											show-overflow-tooltip
											prop="areaName"
											label="区域名称"
											align="center"
										/>
										<ElTableColumn
											show-overflow-tooltip
											prop="areaType"
											label="区域类型"
											align="center"
										/>
										<ElTableColumn
											show-overflow-tooltip
											prop="buildName"
											label="所属楼宇"
											align="center"
										/>
										<ElTableColumn
											show-overflow-tooltip
											prop="sort"
											label="区域排序"
											width="100"
											align="center"
										/>
										<ElTableColumn
											show-overflow-tooltip
											prop="floorName"
											label="所属楼层"
											width="200"
											align="center"
										/>
										<ElTableColumn
											show-overflow-tooltip
											prop="memo"
											label="区域描述"
											min-width="220"
											align="center"
										/>
										<ElTableColumn
											show-overflow-tooltip
											prop="isLargeScreenDisplay"
											label="是否对gis展示"
											width="130"
											align="center"
										>
											<template #default="{ row }">
												<ElSwitch
													v-model="row.isLargeScreenDisplay"
													@change="gisSwitch(row)"
													active-color="#13ce66"
													:active-value="1"
													:inactive-value="0"
												/>
											</template>
										</ElTableColumn>
										<ElTableColumn label="操作" width="160" align="center">
											<template #default="{ row }">
												<ElButton type="success" size="small" @click="handleEdit(row)">
													编辑
												</ElButton>
												<ElButton type="danger" size="small" @click="handleDelete(row)">
													删除
												</ElButton>
											</template>
										</ElTableColumn>
									</ElTable>
									<div class="flex justify-end mt-12px">
										<ElPagination
											background
											:current-page="queryForm.pageNo"
											:page-size="queryForm.pageSize"
											:layout="layout"
											:total="total"
											@size-change="handleSizeChange"
											@current-change="handleCurrentChange"
										/>
									</div>
								</ElTabPane>

								<!-- 楼宇、楼层添加信息 -->
								<ElTabPane :label="firstName" name="first">
									<template #label>
										{{ firstName }}
									</template>
									<div class="tab-second-box">
										<!-- 楼宇信息表单 -->
										<div v-show="activeType == '0'">
											<div class="build-title-box">
												<div class="build-tit">楼宇信息</div>
												<div class="build-btn-box">
													<div v-if="newsStatus" style="margin-left: 80px">
														<ElButton
															type="primary"
															@click="save"
															v-show="!msgStatus"
															:disabled="disabled"
															:icon="loading ? Loading : Check"
														>
															{{ loading ? "确定中 ..." : "确 定" }}
														</ElButton>
														<ElButton
															@click="resetForm"
															v-show="!msgStatus"
															:icon="Refresh"
														>
															重 置
														</ElButton>
													</div>
													<div v-else style="margin-left: 80px">
														<ElButton
															type="primary"
															@click="save"
															:disabled="msgStatus"
															:icon="loading ? Loading : Check"
															v-show="!msgStatus"
														>
															{{ loading ? "保存中 ..." : "保 存" }}
														</ElButton>
														<ElButton
															@click="cancelEdit"
															:disabled="msgStatus"
															v-show="!msgStatus"
															:icon="loading ? Loading : Refresh"
														>
															{{ loading ? "取消中 ..." : "取 消" }}
														</ElButton>
													</div>
												</div>
											</div>
											<ElForm ref="formRef" :model="form" :rules="rules" class="build-form">
												<table>
													<!-- 楼宇基本信息 -->
													<tr>
														<td rowspan="4" class="al-center">
															<span class="floor-label">楼宇基本信息</span>
														</td>
														<td colspan="2">
															<span class="floor-label">楼宇名称</span>
														</td>
														<td width="50%">
															<ElFormItem prop="buildName">
																<ElInput
																	v-model.trim="form.buildName"
																	:disabled="msgStatus"
																	:placeholder="msgStatus ? '' : '请输入楼宇名称'"
																/>
															</ElFormItem>
														</td>
													</tr>
													<!-- 其他表格行保持不变 -->
												</table>
											</ElForm>
										</div>

										<!-- 楼层信息表单 -->
										<div v-show="activeType == '1'">
											<ElForm
												ref="floorFormRef"
												:model="floorForm"
												:rules="rules2"
												label-width="110px"
											>
												<ElFormItem label="楼层名称" prop="floorName">
													<ElInput
														v-model.trim="floorForm.floorName"
														:disabled="msgStatus"
														:placeholder="msgStatus ? '' : '请输入楼层名称'"
														style="width: 400px"
													/>
												</ElFormItem>
												<ElFormItem label="楼层序号" prop="sort">
													<ElInput
														v-model.number="floorForm.sort"
														:disabled="msgStatus"
														:placeholder="msgStatus ? '' : '请输入楼层序号'"
														style="width: 400px"
													/>
												</ElFormItem>
												<ElFormItem label="是否对gis展示" prop="isLargeScreenDisplay">
													<ElRadioGroup
														v-model="floorForm.isLargeScreenDisplay"
														:disabled="msgStatus"
													>
														<ElRadio :label="1">是</ElRadio>
														<ElRadio :label="0">否</ElRadio>
													</ElRadioGroup>
												</ElFormItem>
												<ElFormItem label="楼层描述">
													<ElInput
														v-model.trim="floorForm.memo"
														:disabled="msgStatus"
														type="textarea"
														:placeholder="msgStatus ? '' : '请输入楼层描述'"
														rows="4"
														style="width: 400px"
													/>
												</ElFormItem>
											</ElForm>
										</div>
									</div>
								</ElTabPane>
							</ElTabs>
						</div>
					</div>
				</ElCol>
			</ElRow>
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { createModelAsync, createDrawerAsync } from "@/core/dialog";
	import { ref, reactive, onMounted, nextTick } from "vue";
	import {
		ElMessage,
		ElMessageBox,
		ElCard,
		ElRow,
		ElCol,
		ElTree,
		ElButton,
		ElIcon,
		ElForm,
		ElFormItem,
		ElInput,
		ElRadioGroup,
		ElRadio,
		ElUpload,
		ElDialog,
		ElImage,
		ElSelect,
		ElOption,
		ElTable,
		ElTableColumn,
		ElSwitch,
		ElPagination,
		ElTabs,
		ElTabPane,
		ElDatePicker,
		ElTag
	} from "element-plus";
	import {
		Plus,
		Edit,
		Delete,
		MapLocation,
		OfficeBuilding,
		Files,
		Loading,
		Check,
		Refresh,
		Search
	} from "@element-plus/icons-vue";
	// import { importData } from "@/api/configDeviceType";
	import { addBuild, bulidTreeList, info, updateBuild, delBuild } from "@/api/setting/build";
	import { getAreaInfo, addBuildFloor, updateBuildFlool, delBuildFloor } from "@/api/setting/floor";
	import Detail from "./components/detail.vue";
	import { getRedisDictList, selectBuildAreaList, delBuildArea, updateBuildArea } from "@/api/setting/area";
	import StructureCard from "@/components/card/StructureCard";
	// import { baseURL } from "@/config";
	const baseURL = import.meta.env.VITE_BASE_URL;

	// 响应式数据
	const loading = ref(false);
	const delPic = ref(false);
	const disabled = ref(false);
	const buildFlag = ref(true);
	const fileList = ref([]);
	const activeName = ref("second");
	const firstName = ref("楼宇信息");
	const activeType = ref("0");
	const newsStatus = ref(true);
	const msgStatus = ref(true);
	const dialogImageUrl = ref("");
	const dialogVisible = ref(false);
	const list = ref(null);
	const listLoading = ref(true);
	const total = ref(0);
	const selectRows = ref("");
	const hideUpload = ref(false);
	const treeRef = ref(null);
	const formRef = ref(null);
	const floorFormRef = ref(null);
	const editRef = ref(null);
	const elementLoadingText = ref("正在加载...");
	const layout = ref("total, sizes, prev, pager, next, jumper");

	// 表单数据
	const form = reactive({
		buildName: "",
		place: "",
		memo: "",
		id: "",
		xxmc: "",
		jsdw: "",
		xmdz: "",
		zjzmj: "",
		dscs: "",
		dxcs: "",
		jsxz: "",
		gclxpzwh: "",
		gclxrq: "",
		sjdwmc: "",
		sgdwmc: "",
		jsyt: "",
		jglx: "",
		jsgd: "",
		cg: "",
		jznhdj: "",
		synx: "",
		rlzljxhl: "",
		nyxlzb: "",
		lsjzxj: "",
		glmj: "",
		glnh: "",
		grmj: "",
		grnh: "",
		hbsspzqk: "",
		rjl: "",
		ldl: "",
		tccpzsl: "",
		tzze: "",
		kgrq: "",
		jgrq: "",
		ysri: "",
		ysjl: "",
		propertyManagement: "",
		propertyManagementFee: "",
		parkingSpaceFee: "",
		airConditioner: "",
		airConditionerFee: "",
		airConditionerTime: "",
		theNumberOfElevators: "",
		network: "",
		money: "",
		personInCharge: "",
		phone: ""
	});

	const floorForm = reactive({
		floorName: "",
		sort: "",
		memo: "",
		id: "",
		dictBuilding: "",
		isLargeScreenDisplay: 0
	});

	const queryForm = reactive({
		pageNo: 1,
		pageSize: 10,
		permission: "",
		username: "",
		desc: "",
		buildId: "",
		floorId: "",
		areaName: ""
	});

	// 树形数据
	const data = ref([]);
	const buildingType = ref([]);
	const structureType = ref([]);

	// 选项数据
	const optionsJsxz = ref([
		{ value: "0", label: "新建" },
		{ value: "1", label: "改建" },
		{ value: "2", label: "扩建" }
	]);

	const optionsRlzljxhl = ref([
		{ value: "0", label: "水能" },
		{ value: "1", label: "电能" },
		{ value: "2", label: "气能" },
		{ value: "3", label: "太阳能" },
		{ value: "4", label: "生物能" }
	]);

	// 验证规则
	const rules = reactive({
		buildName: [{ required: true, trigger: "submit", message: " " }],
		place: [{ required: true, trigger: "submit", message: " " }]
	});

	const rules2 = reactive({
		floorName: [{ required: true, trigger: "submit", message: "请输入楼层名称" }],
		sort: [
			{ required: true, trigger: "submit", message: "请输入楼层序号" },
			{ required: true, type: "number", message: "楼层序号为数字", trigger: "submit" }
		],
		isLargeScreenDisplay: [{ required: true, trigger: "blur", message: "请选择是否对gis展示" }]
	});

	// 方法定义
	const queryData = () => {
		queryForm.pageNo = 1;
		fetchData();
	};

	const fetchData = async () => {
		listLoading.value = true;
		try {
			const res = await selectBuildAreaList(queryForm);
			if (res.code == 0) {
				list.value = res.data.records;
				total.value = res.data.total;
			}
		} finally {
			listLoading.value = false;
		}
	};

	const treeData = async () => {
		try {
			const res = await bulidTreeList();
			if (res.code == 0) {
				data.value = res.data;
			}
		} catch (error) {
			console.error(error);
		}
	};

	const getBuildingType = async () => {
		const { data } = await getRedisDictList({ dictType: "buildingType" });
		buildingType.value = data;
	};

	const getStructureType = async () => {
		const { data } = await getRedisDictList({ dictType: "structureType" });
		structureType.value = data;
	};

	const tempInput = () => {
		console.log("queryForm.areaName", queryForm.areaName);
	};

	const buildingAdd = () => {
		firstName.value = "楼宇信息";
		activeType.value = "0";
		activeName.value = "first";
		newsStatus.value = true;
		msgStatus.value = false;
		buildFlag.value = false;
		hideUpload.value = false;
		reseat();
	};

	const cancelEdit = async () => {
		msgStatus.value = true;
		if (activeType.value == "0") {
			try {
				const res = await info(form.id);
				if (res.code == 0) {
					buildFlag.value = false;
					Object.assign(form, res.data.sysBuild);
					if (form.filename) {
						hideUpload.value = true;
						fileList.value = [
							{
								name: form.filename,
								url: baseURL + "/minio/previewFile?fileName=" + form.filename,
								originalFilename: form.originalFilename
							}
						];
					} else {
						hideUpload.value = false;
					}
				}
			} catch (error) {
				ElMessage.error("获取楼宇信息失败");
			}
		} else if (activeType.value == "1") {
			try {
				const res = await getAreaInfo(floorForm.id);
				if (res.code == 0) {
					Object.assign(floorForm, res.data.data.sysBuildFloor);
					buildFlag.value = true;
					if (floorForm.filename) {
						hideUpload.value = true;
						fileList.value = [
							{
								name: floorForm.filename,
								url: baseURL + "/minio/previewFile?fileName=" + floorForm.filename,
								originalFilename: floorForm.originalFilename
							}
						];
					} else {
						hideUpload.value = false;
					}
				}
			} catch (error) {
				ElMessage.error("获取楼层信息失败");
			}
		}
	};

	const editTree = async (treeData) => {
		fileList.value = [];
		activeName.value = "first";
		newsStatus.value = false;
		msgStatus.value = false;
		delPic.value = false;

		try {
			if (treeData.type === "0") {
				activeType.value = "0";
				firstName.value = "楼宇信息";
				const res = await info(treeData.id);
				if (res.code == 0) {
					buildFlag.value = false;
					Object.assign(form, res.data.sysBuild);
					if (form.filename) {
						hideUpload.value = true;
						fileList.value = [
							{
								name: form.filename,
								url: baseURL + "/minio/previewFile?fileName=" + form.filename,
								originalFilename: form.originalFilename
							}
						];
					} else {
						hideUpload.value = false;
					}
				}
			} else {
				activeType.value = "1";
				firstName.value = "楼层信息";
				const res = await getAreaInfo(treeData.id);
				if (res.code == 0) {
					Object.assign(floorForm, res.data.data.sysBuildFloor);
					buildFlag.value = false;
					if (floorForm.filename) {
						hideUpload.value = true;
						fileList.value = [
							{
								name: floorForm.filename,
								url: baseURL + "/minio/previewFile?fileName=" + floorForm.filename,
								originalFilename: floorForm.originalFilename
							}
						];
					} else {
						hideUpload.value = false;
					}
				}
			}
		} catch (error) {
			ElMessage.error("获取信息失败");
		}
	};

	const plus = async (node, treeData) => {
		activeName.value = "first";
		activeType.value = "1";
		newsStatus.value = true;
		msgStatus.value = false;
		firstName.value = "楼层信息";
		floorForm.floorName = "";
		floorForm.sort = "";
		floorForm.memo = "";
		floorForm.id = "";
		floorForm.isLargeScreenDisplay = 0;
		buildFlag.value = false;
		hideUpload.value = false;

		try {
			const res = await info(treeData.id);
			if (res.code == 0) {
				buildFlag.value = false;
				Object.assign(form, res.data.sysBuild);
			}
		} catch (error) {
			ElMessage.error("获取楼宇信息失败");
		}
	};

	const remove = (node, treeData) => {
		if (treeData.type === "0") {
			activeType.value = "0";
		} else {
			activeType.value = "1";
		}

		ElMessageBox.confirm("删除操作会一并删除下级所有数据，确定删除吗？", "删除警告", {
			confirmButtonText: "确定",
			cancelButtonText: "取消",
			type: "warning"
		}).then(async () => {
			try {
				let res;
				if (activeType.value == "0") {
					res = await delBuild(treeData.id);
				} else {
					res = await delBuildFloor(treeData.id);
				}

				if (res.code == 0) {
					ElMessage.success("删除成功");
					treeData();
				}
			} catch (error) {
				ElMessage.error("删除失败");
			}
		});
	};

	const nodeClick = async (item) => {
		queryForm.buildId = "";
		queryForm.floorId = "";
		queryForm.areaName = "";
		formRef.value?.clearValidate();
		floorFormRef.value?.clearValidate();
		reseat();
		msgStatus.value = true;
		activeName.value = "second";
		newsStatus.value = false;

		try {
			if (item.type === "0") {
				activeType.value = "0";
				firstName.value = "楼宇信息";
				queryForm.buildId = item.id;
				const res = await info(item.id);
				if (res.code == 0) {
					buildFlag.value = true;
					Object.assign(form, res.data.sysBuild);
					if (form.filename) {
						hideUpload.value = true;
						fileList.value = [
							{
								name: form.filename,
								url: baseURL + "/minio/previewFile?fileName=" + form.filename,
								originalFilename: form.originalFilename
							}
						];
					} else {
						hideUpload.value = false;
					}
				}
			} else {
				activeType.value = "1";
				firstName.value = "楼层信息";
				queryForm.floorId = item.id;
				const res = await getAreaInfo(item.id);
				if (res.code == 0) {
					Object.assign(floorForm, res.data.data.sysBuildFloor);
					buildFlag.value = true;
					if (floorForm.filename) {
						hideUpload.value = true;
						fileList.value = [
							{
								name: floorForm.filename,
								url: baseURL + "/minio/previewFile?fileName=" + floorForm.filename,
								originalFilename: floorForm.originalFilename
							}
						];
					} else {
						hideUpload.value = false;
					}
				}
			}
			queryData();
		} catch (error) {
			ElMessage.error("获取信息失败");
		}
	};

	const loadFile = async (file) => {
		const isValidType = [".jpg", ".jpeg", ".png", ".PNG"].includes(file.name.substring(file.name.lastIndexOf(".")));

		if (!isValidType) {
			ElMessage.error("图片格式仅支持.png、.jpg、.jpeg!");
			return false;
		}

		const isLt2M = file.size / 1024 / 1024 < 2;
		if (!isLt2M) {
			ElMessage.error("上传图片大小不能超过 2MB!");
			return false;
		}

		// try {
		// 	const formData = new FormData();
		// 	formData.append("file", file.raw);
		// 	const result = await importData(formData);

		// 	fileList.value = [
		// 		{
		// 			name: result.data.data.filename,
		// 			url: baseURL + "/minio/previewFile?fileName=" + result.data.data.filename,
		// 			originalFilename: result.data.data.originalFilename
		// 		}
		// 	];

		// 	hideUpload.value = true;
		// 	return true;
		// } catch (error) {
		// 	ElMessage.error("上传失败");
		// 	return false;
		// }
	};

	const handleRemove = (file) => {
		delPic.value = true;
		hideUpload.value = fileList.value.length > 1;
	};

	const handlePictureCardPreview = (file) => {
		dialogImageUrl.value = file.url;
		dialogVisible.value = true;
	};

	const handleEdit = (row) => {
		if (row.id) {
			// editRef.value.showEdit(row, "detail");
			createModelAsync(
				{
					title: "编辑",
					showNext: false,
					width: "720px",
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<Detail rowInfo={row} />
			).then((res) => {
				queryData();
			});
		} else {
			const addData = {
				buildId: "",
				floorId: ""
			};

			if (queryForm.buildId) {
				addData.buildId = parseInt(queryForm.buildId);
			}

			if (queryForm.floorId) {
				addData.floorId = parseInt(queryForm.floorId);
				getAreaInfo(queryForm.floorId).then((res) => {
					if (res.code == 0) {
						addData.buildId = res.data.data.sysBuildFloor.dictBuilding;
					}
				});
			}

			// editRef.value.showEdit(addData, "add");
			createModelAsync(
				{
					title: "添加",
					showNext: false,
					width: "720px",
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<Detail rowInfo={addData} />
			).then((res) => {
				queryData();
			});
		}
	};

	const handleDelete = (row) => {
		if (row.id) {
			ElMessageBox.confirm("你确定要删除当前项吗", "提示", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				type: "warning"
			}).then(async () => {
				try {
					const res = await delBuildArea(row.id);
					if (res.code == 0) {
						ElMessage.success("删除成功");
						queryData();
					}
				} catch (error) {
					ElMessage.error("删除失败");
				}
			});
		}
	};

	const save = async () => {
		if (loading.value) return;

		let formData = new FormData();
		if (fileList.value.length > 0 && fileList.value[0].raw) {
			formData.append("file", fileList.value[0].raw);
		}
		formData.append("delPic", delPic.value);

		try {
			if (activeType.value == "0") {
				await formRef.value.validate();
				loading.value = true;
				disabled.value = true;

				// 添加表单数据到formData
				Object.keys(form).forEach((key) => {
					if (form[key] !== null && form[key] !== undefined) {
						formData.append(key, form[key]);
					}
				});

				if (fileList.value.length) {
					formData.append("filename", fileList.value[0].name);
					formData.append("originalFilename", fileList.value[0].originalFilename);
				}

				let res;
				if (form.id) {
					res = await updateBuild(formData);
				} else {
					res = await addBuild(formData);
				}

				if (res.code == 0) {
					ElMessage.success("操作成功");
					treeData();
					msgStatus.value = true;
					newsStatus.value = false;
					buildFlag.value = true;
				}
			} else if (activeType.value == "1") {
				await floorFormRef.value.validate();
				loading.value = true;
				disabled.value = true;

				// 添加表单数据到formData
				Object.keys(floorForm).forEach((key) => {
					if (floorForm[key] !== null && floorForm[key] !== undefined) {
						formData.append(key, floorForm[key]);
					}
				});

				if (fileList.value.length) {
					formData.append("filename", fileList.value[0].name);
					formData.append("originalFilename", fileList.value[0].originalFilename);
				}

				let res;
				if (floorForm.id) {
					res = await updateBuildFlool(formData);
				} else {
					formData.append("dictBuilding", form.id);
					res = await addBuildFloor(formData);
				}

				if (res.code == 0) {
					ElMessage.success("操作成功");
					treeData();
					msgStatus.value = true;
					newsStatus.value = false;
					buildFlag.value = true;
				}
			}
		} catch (error) {
			ElMessage.error("操作失败");
		} finally {
			loading.value = false;
			disabled.value = false;
			hideUpload.value = false;
		}
	};

	const resetForm = () => {
		Object.keys(form).forEach((key) => {
			form[key] = "";
		});
		Object.keys(floorForm).forEach((key) => {
			floorForm[key] = key === "isLargeScreenDisplay" ? 0 : "";
		});
		fileList.value = [];
		hideUpload.value = false;
	};

	const handleSizeChange = (val) => {
		queryForm.pageSize = val;
		fetchData();
	};

	const handleCurrentChange = (val) => {
		queryForm.pageNo = val;
		fetchData();
	};

	const infoLabel = async () => {
		await treeData();
		if (data.value.length > 0) {
			try {
				const res = await info(data.value[0].id);
				if (res.code == 0) {
					buildFlag.value = true;
					Object.assign(form, res.data.sysBuild);
					if (form.filename) {
						hideUpload.value = true;
						fileList.value = [
							{
								name: form.filename,
								url: baseURL + "/minio/previewFile?fileName=" + form.filename,
								originalFilename: form.originalFilename
							}
						];
					} else {
						hideUpload.value = false;
					}
				}
			} catch (error) {
				ElMessage.error("获取楼宇信息失败");
			}
		}
	};

	const reseat = () => {
		Object.keys(form).forEach((key) => {
			form[key] = "";
		});
		Object.keys(floorForm).forEach((key) => {
			floorForm[key] = key === "isLargeScreenDisplay" ? 0 : "";
		});
		fileList.value = [];
	};

	const reseat2 = () => {
		queryForm.areaName = "";
		queryData();
	};

	const gisSwitch = async (row) => {
		try {
			const res = await updateBuildArea(row);
			if (res.code == 0) {
				ElMessage.success("操作成功");
				fetchData();
			}
		} catch (error) {
			ElMessage.error("操作失败");
		}
	};

	// 生命周期钩子
	onMounted(() => {
		queryData();
		treeData();
		getBuildingType();
		getStructureType();

		nextTick(() => {
			setTimeout(() => {
				infoLabel();
			}, 1000);
		});
	});
</script>

<style scoped lang="scss">
	.custom-tree-node {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-size: 14px;
		padding-right: 20px;
		max-width: 100%;
		box-sizing: border-box;

		&:hover {
			.right-edit-con {
				display: flex;
			}
		}

		.left-des-con {
			display: inline-block;
			width: 100%;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}

		.right-edit-con {
			display: none;
			position: absolute;
			right: 0;
			top: 0;
			min-width: 40px;
			padding: 6px 10px;
			justify-content: flex-end;
			background-color: #f5f7fac4;

			.el-link {
				font-size: 14px;
			}
		}

		.icon {
			margin-right: 4px;
			vertical-align: middle;
		}

		.bold {
			font-weight: normal;
		}

		.blue {
			color: #334c97;
		}
	}

	.block {
		height: calc(100vh - 215px);
		overflow-y: auto;
	}

	.small-title {
		margin: 0 0 14px;
		line-height: 16px;
		color: #334c97;
		font-weight: bold;

		&::before {
			content: "";
			display: inline-block;
			width: 4px;
			height: 22px;
			background-color: #334c97;
			margin-right: 14px;
			margin-top: -3px;
			margin-left: -20px;
			position: absolute;
			border-radius: 1px;
		}
	}

	:deep(.el-tree-node__content) {
		position: relative;
		overflow: hidden;
	}

	:deep(.right-box-card) {
		.el-card__body {
			padding-top: 6px;

			.el-tabs__item {
				height: 46px;
			}

			.el-tabs__content {
				padding-top: 12px;
			}
		}
	}

	.tab-second-box {
		height: calc(100vh - 201px);
		overflow-y: auto;
	}

	:deep(.pic-box) {
		.el-dialog__body {
			border-top: none;
		}
	}

	:deep(.el-upload-list__item-thumbnail) {
		object-fit: fill;
	}

	:deep(.hide .el-upload--picture-card) {
		display: none;
	}

	table {
		width: 100%;
	}

	table,
	th,
	td {
		border: 1px solid #dcdfe6;
		border-collapse: collapse;
	}

	td .floor-label {
		display: inline-block;
		width: 100%;
		box-sizing: border-box;
		padding: 6px 12px;

		:deep(.el-upload-list--picture-card .el-upload-list__item) {
			width: 80px;
			height: 80px;
		}

		:deep(.el-upload--picture-card) {
			width: 80px;
			height: 80px;
			line-height: 82px;
		}
	}

	td.al-center .floor-label {
		text-align: center;
		font-weight: 600;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
	}

	.build-title-box {
		width: 100%;
		display: flex;
		justify-content: space-between;
		background-color: #f3f3f3;
		height: 38px;
		line-height: 38px;

		.build-tit {
			font-weight: 600;
			font-size: 14px;
			color: rgba(0, 0, 0, 0.85);

			&::before {
				content: "";
				display: inline-block;
				margin-left: 14px;
				margin-right: 7px;
				width: 8px;
				height: 8px;
				background: #355bad;
				border-radius: 50%;
			}
		}

		.build-btn-box {
			margin-right: 14px;
		}
	}

	.w100 {
		width: 100%;
	}

	.build-form {
		margin-bottom: 20px;

		:deep(.el-form-item--small.el-form-item) {
			margin-bottom: 0;
		}

		:deep(.el-input__inner),
		:deep(.el-textarea__inner) {
			border-color: transparent;
		}

		:deep(.el-form-item.is-error .el-input__inner) {
			border-color: #f34d37;
		}
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
	:deep(.el-tabs__content) {
		padding: 12px;
	}
	:deep(.el-form-item) {
		margin-bottom: 12px;
	}
</style>
