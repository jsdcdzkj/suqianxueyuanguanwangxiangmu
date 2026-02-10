<template>
	<el-dialog
		:close-on-click-modal="false"
		:title="title"
		:visible.sync="dialogFormVisible"
		top="6vh"
		width="800px"
		@close="close"
	>
		<el-form
			v-loading.lock="fullscreenLoading"
			ref="form"
			:model="form"
			:rules="rules"
			label-position="top"
			label-width="80px"
		>
			<el-form-item label="组名" prop="groupName">
				<el-input
					v-model="form.groupName"
					autocomplete="off"
					placeholder="请输入"
				></el-input>
			</el-form-item>
			<el-form-item label="分组人员">
				<div class="group-list">
					<p class="sel-user-num">
						（
						<span>{{ selectNum }}</span>
						/ 300）
					</p>
					<div class="unit-list">
						<span class="title">单位名称</span>
						<el-tree
							:data="unitData"
							ref="tree"
							show-checkbox
							default-expand-all
							:check-strictly="true"
							node-key="id"
							:default-checked-keys="checkedKeys"
							@check="checkGroupNode"
							:render-after-expand="false"
							:props="defaultProps"
						>
							<span
								class="custom-tree-node"
								slot-scope="{ node, data }"
							>
								<span @click.stop="handleNodeClick(data, node)">
									{{ data.label }}
									（{{ data.deptCheckedOptions.length || 0 }}/{{
										data.deptList.length
									}}）
								</span>
							</span>
						</el-tree>
					</div>
					<div class="dept-list">
						<span class="title">部门名称</span>
						<el-checkbox-group
							v-model="deptCheckedOptions"
							@change="handleDeptCheckedOptions"
						>
							<el-checkbox
								v-for="item in deptListArry"
								:label="item.id"
								:key="item.id"
								:disabled="item.sysUserList.length == 0"
								style="width: 100%"
							>
								<span @click.stop.prevent="justToShow(item.id)">
									{{ item.deptName }}
									（{{ item.choose || 0 }}/{{
										item.sysUserList.length
									}}）
								</span>
							</el-checkbox>
						</el-checkbox-group>
					</div>
					<div class="user-list">
						<span class="title">人员名称</span>

						<el-checkbox-group
							v-model="userCheckedOptions"
							@change="handleUserCheckedOptions"
						>
							<el-checkbox
								v-for="item in userListArry"
								:label="item.id"
								:key="item.id"
								style="width: 100%"
							>
								<span>
									{{ item.realName }}
								</span>
							</el-checkbox>
						</el-checkbox-group>
					</div>
				</div>
			</el-form-item>

			<el-form-item label="备注">
				<el-input
					v-model.trim="form.remark"
					type="textarea"
					placeholder="请输入"
					autocomplete="off"
				></el-input>
			</el-form-item>
		</el-form>

		<div slot="footer" class="dialog-footer">
			<el-button @click="close">取 消</el-button>
			<el-button
				type="primary"
				@click="save"
				:loading="loading"
				:disabled="disabled"
			>
				{{ loading ? '确定中 ...' : '确定' }}
			</el-button>
		</div>
	</el-dialog>
</template>

<script>
	import { doEdit, getManageDeptUserTree } from '@/api/groupManagement'

	export default {
		name: 'groupManagementEdit',
		components: {},
		data() {
			return {
				fullscreenLoading: false,
				loading: false,
				disabled: false,
				selectNum: 0,
				form: {
					id: '',
					groupName: '',
					itemList: [
						{
							orgId: '',
							deptId: '',
							userId: '',
						},
					],
					remark: '',
				},
				radio: 0,
				rules: {
					groupName: [
						{
							required: true,
							trigger: 'blur',
							message: '请输入分组名称',
						},
					],
					unitId: [
						{
							required: true,
							trigger: 'change',
							message: '请选择所属单位',
						},
					],
					deptId: [
						{
							required: true,
							trigger: 'change',
							message: '请选择所属部门',
							type: 'number',
						},
					],
					userId: [
						{
							required: true,
							trigger: 'change',
							message: '请选择人员',
						},
					],
				},
				title: '',
				dialogFormVisible: false,
				options: [],
				treeData: [],
				defaultProps: {
					children: 'children',
					label: 'label',
					// unitData: [],
				},
				unitData: [],
				deptChecked: [],
				userChecked: [],
				dataList: [],
				deptListArry: [],
				userListArry: [],
				deptCheckedOptions: [],
				userCheckedOptions: [],
				checkedKeys: [],
				checkedselect: true,
				companyId: '',
				deptId: '',
				selectNode: null,
				isUpdateInit: false,
			}
		},
		created() {
			this.userListData()
		},
		methods: {
			// 更新部门和用户数据
			updateDeptUsers(selectNode) {
				if (selectNode) {
					this.deptListArry = selectNode.deptList
					let sysUserListN = []
					selectNode.deptList.forEach((item) => {
						sysUserListN = sysUserListN.concat(item.sysUserList)
					})

					this.userListArry = Array.from(new Set(sysUserListN))
				} else {
					this.deptListArry = []
					this.userListArry = []
				}
			},

			// 设置默认选中
			setDefaultSelect(list, depts, users) {
				list.forEach((org) => {
					const deptList = org.deptList
					const deptCheckedOptions = deptList
						.filter((item) => depts.includes(item.id))
						.map((item) => item.id)
					const userCheckedOptions = deptList
						.map((item) => {
							return item.sysUserList
								.filter((item) => users.includes(item.id))
								.map((item) => item.id)
						})
						.flat()
					org.deptCheckedOptions = deptCheckedOptions
					org.userCheckedOptions = userCheckedOptions
					org.choose = deptCheckedOptions.length

					if (org.children && org.children.length > 0) {
						this.setDefaultSelect(org.children, depts, users)
					}
				})
			},
			// 更新选中数量
			updateSelectNum() {
				const loop = (list) => {
					return list.reduce((value, item) => {
						if (item.children && item.children.length > 0) {
							value += loop(item.children, value)
						}
						if (
							item.userCheckedOptions &&
							item.userCheckedOptions.length > 0
						) {
							value += item.userCheckedOptions.length
						}
						return value
					}, 0)
				}
				this.selectNum = loop(this.unitData)
				this.selectNode.deptList.forEach((item) => {
					this.$set(
						item,
						'choose',
						item.sysUserList.filter((it) =>
							this.userCheckedOptions.includes(it.id)
						).length
					)
				})
			},
			showEdit(row) {
				if (!row) {
					this.title = '添加分组'
					this.dialogFormVisible = true
					this.setDefaultSelect(this.unitData, [], [])
					this.selectNum = 0
				} else {
					this.isUpdateInit = true
					this.title = '编辑分组'
					this.form = Object.assign({}, row)
					const checkedKeys = Array.from(
						new Set(row.itemList.map((it) => it.orgId))
					)
					const depts = Array.from(
						new Set(row.itemList.map((it) => it.deptId))
					)
					const users = Array.from(
						new Set(row.itemList.map((it) => it.userId))
					)
					this.selectNum = users.length
					this.setDefaultSelect(this.unitData, depts, users)
					this.dialogFormVisible = true
					this.checkedKeys = checkedKeys
					setTimeout(() => {
						this.selectNode = this.unitData.find(
							(item) => item.id == checkedKeys[0]
						)
						this.updateDeptUsers(this.selectNode)

						this.userCheckedOptions = [
							...this.selectNode.userCheckedOptions,
						]

						this.deptCheckedOptions = [
							...this.selectNode.deptCheckedOptions,
						]
						this.updateSelectNum()
					}, 300)
				}
			},
			close() {
				this.$refs['form'].resetFields()
				this.deptCheckedOptions = []
				this.userCheckedOptions = []
				this.checkedKeys = []
				this.deptListArry = []
				this.userListArry = []
				this.$refs.tree.setCheckedKeys([], true)
				this.form = this.$options.data().form
				this.dialogFormVisible = false
				this.initChooes(this.unitData)
				this.isUpdateInit = false
			},
			// 获取选中的数据，用户最终提交
			getSubmitItemList(arr, list) {
				list.forEach((item) => {
					const { deptCheckedOptions, userCheckedOptions } = item
					item.deptList.forEach((i) => {
						if (
							deptCheckedOptions &&
							deptCheckedOptions.includes(i.id)
						) {
							i.sysUserList.forEach((j) => {
								if (
									userCheckedOptions &&
									userCheckedOptions.includes(j.id)
								) {
									arr.push({
										orgId: item.id,
										deptId: i.id,
										userId: j.id,
									})
								}
							})
						}
					})
					if (item.children && item.children.length > 0) {
						this.getSubmitItemList(arr, item.children)
					}
				})
			},
			save() {
				if (this.loading) {
					return
				}
				this.$refs['form'].validate(async (valid) => {
					if (valid) {
						this.loading = true
						this.disabled = true
						setTimeout(() => {
							this.loading = false
							this.disabled = false
						}, 1000)
						let arr = []
						this.getSubmitItemList(arr, this.unitData)
						this.form.itemList = Array.from(new Set(arr))
						const { msg } = await doEdit(this.form)
						this.$baseMessage(msg, 'success')
						this.$emit('fetch-data')
						this.close()
					} else {
						return false
					}
				})
			},
			// 给树结构添加默认参数
			initChooes(list) {
				list.forEach((item) => {
					item.deptCheckedOptions = []
					item.userCheckedOptions = []
					item.choose = 0
					item.disabled =
						item.deptList.length == 0 ||
						!item.deptList.some(
							(item) => item.sysUserList.length > 0
						)
					if (item.children && item.children.length > 0) {
						this.initChooes(item.children)
					}
				})
			},

			async userListData() {
				this.fullscreenLoading = true
				const { data } = await getManageDeptUserTree()
				this.fullscreenLoading = false
				this.initChooes(data)
				this.unitData = data
			},
			// 选中部门事件改变
			handleDeptCheckedOptions(value) {
				if (this.selectNode) {
					const deptList = this.selectNode.deptList
					let sysUserListN = []
					value.forEach((item) => {
						const findItem = deptList.find((it) => it.id == item)
						if (findItem) {
							sysUserListN = sysUserListN.concat(
								findItem.sysUserList
							)
						}
					})
					this.userCheckedOptions = Array.from(
						new Set(sysUserListN)
					).map((item) => item.id)
					this.checkComList()
				}
			},
			// 选中改变，更新列表
			checkComList() {
				if (this.deptCheckedOptions.length == 0) {
					this.$refs.tree.setChecked(this.selectNode.id, false)
					this.selectNode.choose = 0
				} else {
					if (
						!this.$refs.tree
							.getCheckedKeys()
							.includes(this.selectNode.id)
					) {
						this.$refs.tree.setChecked(this.selectNode.id, true)
					}
				}
				this.selectNode.deptCheckedOptions = [
					...this.deptCheckedOptions,
				]
				this.selectNode.userCheckedOptions = [
					...this.userCheckedOptions,
				]
				this.selectNode.choose = this.deptCheckedOptions.length
				this.updateSelectNum()
			},
			// 选中用户事件改变
			handleUserCheckedOptions(value) {
				if (this.selectNode) {
					const deptList = this.selectNode.deptList
					const list = value.map((id) => {
						const item = deptList.find((item) =>
							item.sysUserList.map((it) => it.id).includes(id)
						)
						return item
					})
					this.deptCheckedOptions = Array.from(
						new Set(list.map((item) => item.id))
					)
					this.checkComList()
				}
			},

			// 选择会触发checkGroupNode方法
			checkGroupNode(checkedNodes, { checkedKeys }) {
				if (this.checkedselect) {
					this.updateDeptUsers(checkedNodes)
					if (checkedKeys.includes(checkedNodes.id)) {
						this.$nextTick(() => {
							this.selectNode = checkedNodes
							if (checkedNodes.deptList.length > 0) {
								checkedNodes.deptList.forEach((item) => {
									item.choose = item.sysUserList.length
								})
							}
							this.deptCheckedOptions = checkedNodes.deptList
								.map((item) => {
									if (item.sysUserList.length > 0)
										return item.id
								})
								.filter((item) => item > 0)
							this.userCheckedOptions = this.userListArry.map(
								(item) => item.id
							)
							this.selectNode.deptCheckedOptions = [
								...this.deptCheckedOptions,
							]
							this.selectNode.userCheckedOptions = [
								...this.userCheckedOptions,
							]
							this.selectNode.choose =
								this.deptCheckedOptions.length
							this.updateSelectNum()
						})
					} else {
						this.selectNode = checkedNodes
						this.selectNode.deptCheckedOptions = []
						this.selectNode.userCheckedOptions = []
						this.deptCheckedOptions = []
						this.userCheckedOptions = []
						this.selectNode.choose = this.deptCheckedOptions.length
						this.updateSelectNum()
					}
				}
			},
			handleNodeClick(data, node) {
				this.selectNode = node.data
				this.deptListArry = this.selectNode.deptList
				let sysUserListN = []
				this.selectNode.deptList.forEach((item) => {
					sysUserListN = sysUserListN.concat(item.sysUserList)
				})
				this.userListArry = Array.from(new Set(sysUserListN))
				if (
					this.selectNode.deptCheckedOptions &&
					this.selectNode.userCheckedOptions
				) {
					this.deptCheckedOptions = this.selectNode.deptCheckedOptions
					this.userCheckedOptions = this.selectNode.userCheckedOptions
				}
				this.updateSelectNum()
			},

			// 点击部门名称 只显示人员
			justToShow(id) {
				this.userListArry = this.deptListArry.find(
					(item) => item.id === id
				).sysUserList
				this.deptId = id
			},
		},
	}
</script>
<style lang="scss" scoped>
	.group-list {
		display: flex;
		padding: 2px 14px 14px;
		border-radius: 4px;
		border: 1px solid #ebeef5;
		position: relative;
		.sel-user-num {
			position: absolute;
			top: -50px;
			right: 0;
			color: #909399;
			span {
				color: #409eff;
			}
		}
		.unit-list {
			flex: 1;
			margin-right: 20px;
			.title {
				display: block;
				margin-bottom: 10px;
				&::after {
					content: '';
					display: block;
					width: 100%;
					height: 1px;
					background-color: #ebeef5;
					margin-bottom: 10px;
				}
				&::before {
					content: '';
					display: inline-block;
					margin-right: 8px;
					margin-top: -2px;
					vertical-align: middle;
					width: 4px;
					height: 14px;
					background-color: #cbd9f8;
				}
			}
			.el-tree {
				height: calc(100vh - 12vh - 530px);
				overflow-y: auto;
			}
		}
		.dept-list {
			flex: 1;
			margin-right: 20px;
			.title {
				display: block;
				margin-bottom: 10px;
				position: relative;
				&::after {
					content: '';
					display: block;
					width: 100%;
					height: 1px;
					background-color: #ebeef5;
					margin-bottom: 10px;
				}
				&::before {
					content: '';
					display: inline-block;
					margin-right: 8px;
					margin-top: -2px;
					vertical-align: middle;
					width: 4px;
					height: 14px;
					background-color: #cbd9f8;
				}
			}
			.el-checkbox-group {
				height: calc(100vh - 12vh - 530px);
				overflow-y: auto;
				.el-checkbox{
					margin-right: 0;
				}
			}
		}
		.user-list {
			width: 140px;
			.title {
				display: block;
				margin-bottom: 10px;
				&::after {
					content: '';
					display: block;
					width: 100%;
					height: 1px;
					background-color: #ebeef5;
					margin-bottom: 10px;
				}
				&::before {
					content: '';
					display: inline-block;
					margin-right: 8px;
					margin-top: -2px;
					vertical-align: middle;
					width: 4px;
					height: 14px;
					background-color: #cbd9f8;
				}
			}
			.el-checkbox-group {
				height: calc(100vh - 12vh - 530px);
				overflow-y: auto;
				.el-checkbox{
					margin-right: 0;
				}
			}
		}
	}
</style>
