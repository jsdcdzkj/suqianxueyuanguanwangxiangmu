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
							ref="treeRef"
							:data="dataDeptTree"
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
										<ElButton
											type="success"
											link
											@click.stop="
												() => {
													treeRef.value?.setCurrentKey(node.data.id);
													plus(node, data);
												}
											"
										>
											<ElIcon><Plus /></ElIcon>
										</ElButton>
										<ElButton
											type="primary"
											link
											@click.stop="
												() => {
													treeRef.value?.setCurrentKey(node.data.id);
													editTree(data);
												}
											"
										>
											<ElIcon><Edit /></ElIcon>
										</ElButton>
										<ElButton
											type="danger"
											link
											@click.stop="
												() => {
													treeRef.value?.setCurrentKey(node.data.id);
													remove(node, data);
												}
											"
										>
											<ElIcon><Delete /></ElIcon>
										</ElButton>
									</span>
									<span class="right-edit-con" v-else>
										<ElButton
											type="success"
											link
											@click.stop="
												() => {
													treeRef.value?.setCurrentKey(node.data.id);
													plus(node, data);
												}
											"
										>
											<ElIcon><Plus /></ElIcon>
										</ElButton>
										<ElButton
											type="primary"
											link
											@click.stop="
												() => {
													treeRef.value?.setCurrentKey(node.data.id);
													editTree(data);
												}
											"
										>
											<ElIcon><Edit /></ElIcon>
										</ElButton>
										<ElButton
											type="danger"
											link
											@click.stop="
												() => {
													treeRef.value?.setCurrentKey(node.data.id);
													remove(node, data);
												}
											"
										>
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
								:data="listTable"
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
								<ElTableColumn label="操作" width="140" align="center">
									<template #default="{ row }">
										<ElButton type="primary" text size="small" @click="handleEdit(row)">
											编辑
										</ElButton>
										<ElButton type="danger" text size="small" @click="handleDelete(row)">
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
								<ElForm ref="formInstance" :model="form" :rules="rules" label-width="88px">
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
									<ElFormItem label="邮箱地址" prop="email">
										<ElInput
											v-model.trim="form.email"
											:disabled="msgStatus"
											:placeholder="msgStatus ? '' : '请输入邮箱地址'"
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
											:disabled="loading"
											:icon="Check"
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
	const dataDeptTree = ref([]);
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
		email: "",
		memo: "",
		parentId: "",
		id: ""
	});
	const currentNodeKey = ref("");

	const rules = reactive({
		orgName: [{ required: true, trigger: "blur", message: "请输入单位名称" }]
	});

	const activeType = ref("0");
	const msgStatus = ref(false);

	const activeName = ref("first");
	const iconType = ref("0");

	const listTable = ref(null);
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

	// const editTree = (treeData) => {
	// 	createModelAsync(
	// 		{
	// 			title: "编辑单位",
	// 			showNext: false,
	// 			width: "720px",
	// 			closeOnClickModal: false,
	// 			closeOnPressEscape: false
	// 		},
	// 		{},
	// 		<Detail rowInfo={treeData} />
	// 	).then((res) => {
	// 		if (res) {
	// 			ElMessage.success("编辑成功");
	// 			treeData();
	// 		}
	// 	});
	// };

	const editTree = async (node) => {
		formInstance.value?.clearValidate();
		activeType.value = "0";
		activeName.value = "second";
		msgStatus.value = false;

		try {
			const res = await info(node.id);
			Object.assign(form, {
				orgName: res.orgName,
				isItATenant: res.isItATenant || "0",
				address: res.address,
				manager: res.manager,
				phone: res.phone,
				email: res.email || "",
				memo: res.memo,
				parentId: res.parentId,
				id: res.id
			});
		} catch (error) {
			ElMessage.error("获取信息失败");
		}
	};

	// const plus = (node, treeData) => {
	// 	createModelAsync(
	// 		{
	// 			title: "新增下级单位",
	// 			showNext: false,
	// 			width: "720px",
	// 			closeOnClickModal: false,
	// 			closeOnPressEscape: false
	// 		},
	// 		{},
	// 		<Detail rowInfo={{ parentId: treeData.id }} />
	// 	).then((res) => {
	// 		if (res) {
	// 			ElMessage.success("新增成功");
	// 			treeData();
	// 		}
	// 	});
	// };
	const plus = (node, data) => {
		// 完全重置表单
		formInstance.value?.resetFields();
		Object.assign(form, {
			orgName: "",
			isItATenant: "0",
			address: "",
			manager: "",
			phone: "",
			email: "",
			memo: "",
			parentId: "", // 先清空
			id: "" // 清空ID
		});

		// 设置父级ID
		form.parentId = "";
		if (data != undefined) {
			form.parentId = data.id;
		}

		activeType.value = "1"; // 设置为新增模式
		activeName.value = "second";
		iconType.value = "1";
		msgStatus.value = false; // 设置为可编辑状态
	};

	const plus2 = () => {
		// 完全重置表单
		formInstance.value?.resetFields();
		Object.assign(form, {
			orgName: "",
			isItATenant: "0",
			address: "",
			manager: "",
			phone: "",
			email: "",
			memo: "",
			parentId: "",
			id: "" // 清空ID
		});

		activeType.value = "1"; // 设置为新增模式
		activeName.value = "second";
		iconType.value = "0";
		msgStatus.value = false; // 设置为可编辑状态
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

	const nodeClick = async (item, treeData) => {
		try {
			queryForm.deptName = "";
			reseat3();
			heightTable.value = "calc(100vh - 522px)";
			msgStatus.value = true; // 设置为只读状态
			activeType.value = "0";
			activeName.value = "first";
			form.parentId = item.id;

			// 确保树形组件选中当前节点
			if (treeRef.value) {
				treeRef.value.setCurrentKey(item.id);
			}

			asyncObj.orgId = item.id;
			asyncObj.iccId = item.iccId;
			iconType.value = item.parentId !== 0 ? "1" : "0";

			const res = await info(item.id);
			Object.assign(form, res);
			await fetchData();
		} catch (error) {
			ElMessage.error("获取信息失败");
		}
	};

	const handleEdit = (row) => {
		if (asyncObj.orgId) {
			// 有 form.id 时，传递给弹窗进行编辑
			createModelAsync(
				{
					title: row?.id ? "编辑部门" : "新增部门",
					showNext: false,
					width: "720px",
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<Detail rowInfo={row || {}} orgId={asyncObj.orgId} />
			).then((res) => {
				fetchData();
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
				await delOrgDept(row.id);
				fetchData();
			});
		}
	};

	const fetchData = async () => {
		if (asyncObj.orgId) {
			listLoading.value = true;
			queryForm.orgId = asyncObj.orgId;
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

			const data = await selectOrgDeptList(queryForm);
			console.log(data);
			listTable.value = data.records;
			total.value = data.total;
			listLoading.value = false;
		}
	};

	const save = async () => {
		if (loading.value) return;

		try {
			await formInstance.value?.validate();
			loading.value = true;

			if (activeType.value === "1") {
				// 新增模式
				if (!form.id) {
					// 新增顶级单位
					const res = await addOrgManage(form);
					await treeData();
					form.id = res.id;
					msgStatus.value = true;
					activeType.value = "0";
					await queryData();
				} else {
					// 新增下级单位
					const res = await addOrgManage(form);
					await treeData();
					form.id = res.id;
					msgStatus.value = true;
					activeType.value = "0";
					await queryData();
				}
			} else {
				// 编辑模式
				const res = await updateOrgManage(form);
				await treeData();
				msgStatus.value = true;
				activeType.value = "0";
				await queryData();
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
		const data = await findOrg();
		dataDeptTree.value = data;
		return data; // 添加返回值
	};

	const treeData2 = async () => {
		const data = await areaTreeList();
		options.value = data;
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
	onMounted(async () => {
		try {
			await Promise.all([treeData(), treeData2()]);

			// 等待多次更新确保 DOM 完全渲染
			await nextTick();
			await nextTick();

			if (dataDeptTree.value.length > 0) {
				const firstNode = dataDeptTree.value[0];

				// 使用 treeRef 来设置当前选中节点
				if (treeRef.value) {
					treeRef.value.setCurrentKey(firstNode.id);
				}

				form.id = firstNode.id;
				currentNodeKey.value = firstNode.id;

				try {
					const res = await info(firstNode.id);
					Object.assign(form, res);
					asyncObj.orgId = res.id;
					asyncObj.iccId = res.iccId;
					msgStatus.value = true;
					activeType.value = "0";
				} catch (error) {
					ElMessage.error("获取信息失败");
				}

				await fetchData();
			}
		} catch (error) {
			ElMessage.error("初始化失败");
		}
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
			padding: 3px 6px;
			justify-content: flex-end;
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
	:deep(.el-button + .el-button) {
		margin-left: 2px;
	}
</style>
