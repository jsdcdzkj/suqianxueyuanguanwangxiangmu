<template>
	<div class="h-full flex gap-12px p-12px">
		<div class="flex flex-1 w-full relative" v-loading="loading">
			<div class="w-320px h-full mr-12px">
				<StructureCard title="单位信息">
					<template #actions>
						<ElButton type="success" @click="plus2" :icon="Plus">新增</ElButton>
					</template>
					<div class="block">
						<ElTree
							:data="data"
							:props="treeProps"
							node-key="id"
							default-expand-all
							:current-node-key="4"
							:highlight-current="true"
							@node-click="nodeClick"
							:expand-on-click-node="false"
						>
							<template #default="{ node, data }">
								<span class="custom-tree-node">
									<div class="left-des-con">
										<!-- 第一级固定一个图标 -->
										<i class="icon" v-if="node.level === 1">
											<svg
												v-show="!node.isCurrent"
												t="1689305765399"
												viewBox="0 0 1024 1024"
												version="1.1"
												xmlns="http://www.w3.org/2000/svg"
												p-id="17458"
												width="14"
												height="14"
											>
												<path
													d="M931.781 870.036h-27.708V580.229c0-15.531-8.386-36.494-28.219-44.26L624.146 382.442V141.963l-2.771-5.396C610 110.39 585.062 97.228 554.292 102.442l-394.771 109.63c-22.458 7.766-39.083 28.693-39.083 52.172v605.792H92.219C75.083 870.036 64 883.198 64 896.359c0 16.005 11.375 26.323 28.219 26.323h839.563c17.136 0 28.219-10.609 28.219-26.323-0.147-16.005-11.522-26.323-28.22-26.323z m-84-292.651v292.651H624.146V442.234l223.635 135.151z m-672.073-313.14l391.635-109.594v715.385H175.708V264.245z m0 0"
													fill="#222222"
													p-id="17459"
												></path>
											</svg>
											<svg
												v-show="node.isCurrent"
												t="1689306005892"
												viewBox="0 0 1024 1024"
												version="1.1"
												xmlns="http://www.w3.org/2000/svg"
												p-id="17573"
												id="mx_n_1689306005896"
												width="14"
												height="14"
											>
												<path
													d="M931.781 870.036h-27.708V580.229c0-15.531-8.386-36.494-28.219-44.26L624.146 382.442V141.963l-2.771-5.396C610 110.39 585.062 97.228 554.292 102.442l-394.771 109.63c-22.458 7.766-39.083 28.693-39.083 52.172v605.792H92.219C75.083 870.036 64 883.198 64 896.359c0 16.005 11.375 26.323 28.219 26.323h839.563c17.136 0 28.219-10.609 28.219-26.323-0.147-16.005-11.522-26.323-28.22-26.323z m-84-292.651v292.651H624.146V442.234l223.635 135.151z m-672.073-313.14l391.635-109.594v715.385H175.708V264.245z m0 0"
													fill="#334c97"
													p-id="17574"
												></path>
											</svg>
										</i>
										<!-- 非第一级且有子元素是个文件夹图标 -->
										<i class="icon" v-else>
											<svg
												v-show="!node.isCurrent"
												t="1689227089690"
												viewBox="0 0 1024 1024"
												version="1.1"
												xmlns="http://www.w3.org/2000/svg"
												p-id="6393"
												width="14"
												height="14"
											>
												<path
													d="M896 912H128a16 16 0 0 1 0-32h768a16 16 0 0 1 0 32z"
													p-id="6394"
													fill="#333333"
												></path>
												<path
													d="M756 912H268a28 28 0 0 1-28-28V140a28 28 0 0 1 28-28h488a28 28 0 0 1 28 28v744a28 28 0 0 1-28 28z m-484-32h480V144H272z"
													p-id="6395"
													fill="#333333"
												></path>
												<!-- 其他path保持不变 -->
											</svg>
										</i>
										<span
											:class="[
												node.childNodes.length ? 'bold' : '',
												node.isCurrent ? 'blue' : ''
											]"
										>
											{{ node.label }}
										</span>
									</div>
									<span class="right-edit-con" v-if="node.data.type === '0'">
										<ElButton type="success" link @click.stop="() => plus(node, data)">
											<ElIcon><Plus /></ElIcon>
										</ElButton>
										<ElButton type="primary" link @click.stop="() => editTree(data)">
											<ElIcon><Edit /></ElIcon>
										</ElButton>
										<ElButton type="danger" link @click.stop="() => remove(node, data)">
											<ElIcon><Delete /></ElIcon>
										</ElButton>
									</span>
									<span class="right-edit-con" v-else>
										<ElButton type="success" link @click.stop="() => plus(node, data)">
											<ElIcon><Plus /></ElIcon>
										</ElButton>
										<ElButton type="primary" link @click.stop="() => editTree(data)">
											<ElIcon><Edit /></ElIcon>
										</ElButton>
										<ElButton type="danger" link @click.stop="() => remove(node, data)">
											<ElIcon><Delete /></ElIcon>
										</ElButton>
									</span>
								</span>
							</template>
						</ElTree>
					</div>
				</StructureCard>
			</div>
			<!-- 右侧区域 -->
			<div class="flex-1 h-full">
				<div class="custom2-table w-full relative">
					<ElTabs v-model="activeName">
						<!-- 区域信息添加 -->
						<ElTabPane name="first">
							<template #label>
								<ElIcon><Collection /></ElIcon>
								部门信息
							</template>

							<!-- 查询表单 -->
							<div class="filter-container mb-12px">
								<ElCascader
									:options="options"
									style="width: 150px; margin-right: 10px"
									:show-all-levels="false"
									placeholder="请选择楼层区域"
									collapse-tags
									ref="cascaderArr"
									v-model="as"
									@change="choseTheme"
									clearable
								/>
								<ElInput
									v-model.trim="queryForm.deptName"
									placeholder="部门名称"
									clearable
									@keyup.enter="queryData"
									style="width: 150px; margin-right: 10px"
								/>
								<ElButton type="primary" @click="queryData" :icon="Search"> 查询 </ElButton>
								<ElButton @click="reseat3" :icon="Refresh"> 重置 </ElButton>
							</div>

							<!-- 操作按钮 -->
							<div class="operation-container" style="margin-bottom: 12px">
								<ElButton :icon="Plus" type="success" @click="handleEdit"> 添加 </ElButton>
							</div>

							<!-- 表格 -->
							<ElTable
								v-loading="listLoading"
								:data="list"
								:element-loading-text="elementLoadingText"
								height="calc(100vh - 278px)"
								border
							>
								<ElTableColumn
									show-overflow-tooltip
									prop="deptName"
									label="部门名称"
									width="300"
									align="center"
								/>
								<ElTableColumn
									show-overflow-tooltip
									prop="orgName"
									label="包含区域"
									min-width="120"
									align="center"
								/>
								<ElTableColumn
									show-overflow-tooltip
									prop="memo"
									label="备注"
									min-width="120"
									align="center"
								/>
								<ElTableColumn label="操作" width="160" align="center">
									<template #default="{ row }">
										<ElButton
											icon="Edit"
											type="success"
											size="small"
											plain
											@click="handleEdit(row)"
										>
											编辑
										</ElButton>
										<ElButton
											icon="Delete"
											type="danger"
											size="small"
											plain
											@click="handleDelete(row)"
										>
											删除
										</ElButton>
									</template>
								</ElTableColumn>
							</ElTable>

							<!-- 分页 -->
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

						<ElTabPane name="second">
							<template #label>
								<ElIcon v-if="iconType == '0'"><OfficeBuilding /></ElIcon>
								<ElIcon v-else><Files /></ElIcon>
								单位信息
							</template>
							<div class="tab-second-box">
								<ElForm ref="form" :model="form" :rules="rules" label-width="88px">
									<ElFormItem label="单位名称" prop="orgName">
										<ElInput
											v-model.trim="form.orgName"
											:disabled="msgStatus"
											:placeholder="msgStatus ? '' : '请输入单位名称'"
											autocomplete="off"
											style="width: 400px"
										/>
									</ElFormItem>
									<ElFormItem label="是否是租户" prop="isItATenant">
										<ElRadioGroup v-model="form.isItATenant" :disabled="msgStatus">
											<ElRadio :label="1">是</ElRadio>
											<ElRadio :label="0">否</ElRadio>
										</ElRadioGroup>
									</ElFormItem>
									<ElFormItem label="地址" prop="address">
										<ElInput
											v-model.trim="form.address"
											:disabled="msgStatus"
											:placeholder="msgStatus ? '' : '请输入地址'"
											autocomplete="off"
											style="width: 400px"
										/>
									</ElFormItem>
									<ElFormItem label="联系人" prop="manager">
										<ElInput
											v-model.trim="form.manager"
											:disabled="msgStatus"
											:placeholder="msgStatus ? '' : '请输入联系人'"
											autocomplete="off"
											style="width: 400px"
										/>
									</ElFormItem>
									<ElFormItem label="电话" prop="phone">
										<ElInput
											v-model.trim="form.phone"
											:disabled="msgStatus"
											:placeholder="msgStatus ? '' : '请输入电话'"
											autocomplete="off"
											style="width: 400px"
										/>
									</ElFormItem>
									<ElFormItem label="备注信息">
										<ElInput
											v-model.trim="form.memo"
											:disabled="msgStatus"
											type="textarea"
											:placeholder="msgStatus ? '' : '请输入备注信息'"
											autocomplete="off"
											style="width: 400px"
										/>
									</ElFormItem>
									<div v-if="activeType == '0'" style="margin-left: 80px">
										<ElButton
											type="primary"
											style="margin-left: 10px"
											@click="save"
											:loading="loading"
											v-show="!msgStatus"
											:disabled="msgStatus"
											:icon="Check"
										>
											{{ loading ? "保存中 ..." : "保 存" }}
										</ElButton>
										<ElButton
											@click="cancelEdit"
											:loading="loading"
											:disabled="msgStatus"
											v-show="!msgStatus"
											:icon="Refresh"
										>
											{{ loading ? "取消中 ..." : "取 消" }}
										</ElButton>
									</div>
									<div v-else style="margin-left: 80px">
										<ElButton
											type="primary"
											style="margin-left: 10px"
											@click="save"
											:loading="loading"
											:disabled="disabled"
											:icon="Plus"
										>
											{{ loading ? "确定中 ..." : "添 加" }}
										</ElButton>
										<ElButton @click="reseat" :icon="Refresh">重 置</ElButton>
									</div>
								</ElForm>
							</div>
						</ElTabPane>
					</ElTabs>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="tsx">
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
		ElSelect,
		ElOption,
		ElTable,
		ElTableColumn,
		ElTabs,
		ElTabPane,
		ElRadioGroup,
		ElRadio
	} from "element-plus";
	import {
		Plus,
		Edit,
		Delete,
		Collection,
		OfficeBuilding,
		Files,
		Search,
		Refresh,
		Check
	} from "@element-plus/icons-vue";
	import {
		addOrgManage,
		orgTreeList,
		info,
		updateOrgManage,
		delOrgManage,
		findOrg,
		areaTreeList
	} from "@/api/setting/org";
	import { selectOrgDeptList, delOrgDept, visitorRegion, visitorRegion2 } from "@/api/setting/dept";
	import Detail from "./components/edit.vue";
	import { createModelAsync } from "@/core/dialog";
	import StructureCard from "@/components/card/StructureCard";
	// 响应式数据
	const loading = ref(false);
	const data = ref([]);
	const treeProps = {
		label: "orgName",
		value: "id"
	};

	const form = reactive({
		orgName: "",
		isItATenant: "0",
		address: "",
		manager: "",
		phone: "",
		memo: "",
		parentId: "",
		id: ""
	});

	const rules = reactive({
		orgName: [{ required: true, trigger: "blur", message: "请输入单位名称" }]
	});

	const activeType = ref("0");
	const msgStatus = ref(true);
	const activeName = ref("first");
	const iconType = ref("0");

	const list = ref(null);
	const listLoading = ref(false);
	const total = ref(0);
	const elementLoadingText = ref("正在加载...");
	const layout = ref("total, sizes, prev, pager, next, jumper");

	const queryForm = reactive({
		pageNo: 1,
		pageSize: 10,
		deptName: "",
		ids: ""
	});

	const options = ref([]);
	const checkDatas = ref([]);
	const heightTable = ref("calc(100vh - 522px)");
	const asyncObj = reactive({
		iccId: "",
		orgId: ""
	});

	// 表单引用
	const formInstance = ref(null);
	const treeRef = ref(null);
	const cascaderArr = ref(null);

	// 方法定义

	const editTree = (treeData) => {
		createModelAsync(
			{
				title: "编辑单位",
				showNext: false,
				width: "720px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<Detail rowInfo={treeData} />
		).then((res) => {
			if (res) {
				ElMessage.success("编辑成功");
				treeData();
			}
		});
	};

	const plus = (node, treeData) => {
		createModelAsync(
			{
				title: "新增下级单位",
				showNext: false,
				width: "720px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<Detail rowInfo={{ parentId: treeData.id }} />
		).then((res) => {
			if (res) {
				ElMessage.success("新增成功");
				treeData();
			}
		});
	};

	const plus2 = () => {
		reseat();
		form.parentId = "";
		console.log(form.parentId);
		activeType.value = "1";
		activeName.value = "second";
		iconType.value = "0";
		msgStatus.value = false;
	};

	const remove = (node, treeData) => {
		ElMessageBox.confirm("删除操作会一并删除下级所有数据，确定删除吗？", "删除警告", {
			confirmButtonText: "确定",
			cancelButtonText: "取消",
			type: "warning"
		}).then(async () => {
			const res = await delOrgManage(treeData.id);
			if (res.code == 0) {
				ElMessage.success("删除成功");
				treeData();
			}
		});
	};

	const nodeClick = (item, treeData) => {
		queryForm.deptName = "";
		reseat3();
		heightTable.value = "calc(100vh - 522px)";
		msgStatus.value = true;
		activeType.value = "0";
		activeName.value = "first";
		form.parentId = item.id;

		asyncObj.orgId = item.id;
		asyncObj.iccId = item.iccId;
		if (item.parentId != 0) {
			iconType.value = "1";
		} else {
			iconType.value = "0";
		}
		info(item.id).then((res) => {
			if (res.code == 0) {
				Object.assign(form, res.data);
				fetchData();
			}
		});
	};

	const handleEdit = (row) => {
		if (form.id) {
			createModelAsync(
				{
					title: "编辑部门",
					showNext: false,
					width: "720px",
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<Detail rowInfo={row} />
			).then((res) => {
				if (res) {
					ElMessage.success("编辑成功");
					fetchData();
				}
			});
		} else {
			ElMessage.error("请先在左侧选中单位！");
		}
	};

	const handleDelete = (row) => {
		if (row.id) {
			ElMessageBox.confirm("你确定要删除当前项吗", "提示", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				type: "warning"
			}).then(async () => {
				const res = await delOrgDept(row.id);
				if (res.code == 0) {
					ElMessage.success("删除成功");
					fetchData();
				}
			});
		}
	};

	const fetchData = async () => {
		if (form.id) {
			listLoading.value = true;
			queryForm.orgId = form.id;
			checkDatas.value = [];
			const checkedNode = cascaderArr.value.getCheckedNodes();

			if (checkedNode != null) {
				for (let i = 0; i < checkedNode.length; i++) {
					if (checkedNode[i].level == 3) {
						checkDatas.value.push(checkedNode[i].data.value);
					}
				}
			}
			queryForm.ids = checkDatas.value.toString();

			const res = await selectOrgDeptList(queryForm);
			if (res.code == 0) {
				list.value = res.data.records;
				total.value = res.data.total;
				listLoading.value = false;
			}
		}
	};

	const save = async () => {
		if (loading.value) return;

		try {
			await formInstance.value.validate();
			loading.value = true;

			if (activeType.value == "1") {
				if (form.id == "") {
					const res = await addOrgManage(form);
					if (res.code == 0) {
						ElMessage.success("操作成功");
						treeData();
						form.id = res.data.id;
						msgStatus.value = true;
						activeType.value = "0";
					}
				} else {
					const res = await updateOrgManage(form);
					if (res.code == 0) {
						treeData();
						msgStatus.value = true;
						activeType.value = "0";
					}
				}
			}
		} catch (error) {
			ElMessage.error("操作失败");
		} finally {
			loading.value = false;
		}
	};

	const cancelEdit = () => {
		msgStatus.value = true;
		heightTable.value = "calc(100vh - 522px)";
		info(form.id).then((res) => {
			if (res.code == 0) {
				Object.assign(form, res.data);
			}
		});
	};

	const reseat = () => {
		formInstance.value?.resetFields();
		form.memo = "";
		form.id = "";
	};

	const reseat3 = () => {
		queryForm.deptName = "";
		checkDatas.value = [];
		queryForm.ids = "";

		setTimeout(() => {
			fetchData();
		}, 100);
	};

	const handleSizeChange = (val) => {
		queryForm.pageSize = val;
		fetchData();
	};

	const handleCurrentChange = (val) => {
		queryForm.pageNo = val;
		fetchData();
	};

	const queryData = () => {
		queryForm.pageNo = 1;
		fetchData();
	};

	const treeData = async () => {
		const res = await findOrg();
		if (res.code == 0) {
			data.value = res.data;
		}
	};

	const treeData2 = async () => {
		const res = await areaTreeList();
		if (res.code == 0) {
			options.value = res.data;
		}
	};

	const choseTheme = (subjectValue) => {
		if (subjectValue.length == 0) {
			checkDatas.value = [];
			fetchData();
		} else {
			fetchData();
		}
	};

	// 生命周期钩子
	onMounted(() => {
		treeData();
		treeData2();

		nextTick(() => {
			setTimeout(() => {
				if (data.value.length > 0) {
					form.id = data.value[0].id;
					info(form.id).then((res) => {
						if (res.code == 0) {
							Object.assign(form, res.data);
							asyncObj.orgId = res.data.id;
							asyncObj.iccId = res.data.iccId;
						}
					});
				}
				fetchData();
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
		height: calc(100vh - 195px);
		overflow-y: auto;
	}

	::v-deep {
		.el-tree-node__content {
			position: relative;
			overflow: hidden;
		}
		.el-textarea__inner {
			height: 84px !important;
		}
	}
	.tab-second-box {
		height: calc(100vh - 190px);
		overflow-y: auto;
	}
	.operation-container {
		position: absolute;
		top: -39px;
		right: 12px;
		z-index: 9;
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
		overflow: visible;
	}
	:deep(.el-form-item) {
		margin-bottom: 12px;
	}
	:deep(.module-card__header) {
		padding: 8px 0;
	}
	.custom2-table {
		background: var(--app-card-color);
		overflow: hidden;
	}
</style>
