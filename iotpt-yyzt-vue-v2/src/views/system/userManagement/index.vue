<template>
  <div class="index-container">
    <vab-query-form>
      <vab-query-form-left-panel :span="18" class="left-panel-self">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-input v-model.trim="queryForm.unitId" type="hidden"></el-input>
          </el-form-item>

          <el-form-item>
            <ebs-tree-select
              :options="treeData"
              v-model="deptLabel"
              :accordion="true"
              @node-click="nodeClick"
              placeholder="选择所属单位"
            />
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="queryForm.deptId"
              placeholder="选择所属部门"
              filterable
              clearable
              :disabled="isDeptDisabled"
            >
              <el-option
                v-for="item in deptList"
                :key="item.id"
                :label="item.deptName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="queryForm.roleId"
              placeholder="角色名称"
              filterable
              clearable
            >
              <el-option
                v-for="item in roleList"
                :key="item.id"
                :label="item.roleName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model.trim="queryForm.phone"
              clearable
              placeholder="手机号"
              @keyup.enter.native="queryData"
            />
          </el-form-item>

          <el-form-item>
            <el-input
              v-model.trim="queryForm.realName"
              clearable
              placeholder="用户名称"
              @keyup.enter.native="queryData"
            />
          </el-form-item>
          <el-form-item>
            <el-input
              v-model.trim="queryForm.loginName"
              clearable
              placeholder="登录名称"
              @keyup.enter.native="queryData"
            />
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="primary" @click="queryData">
              查询
            </el-button>
            <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
          </el-form-item>
        </el-form>
      </vab-query-form-left-panel>
      <vab-query-form-right-panel :span="6">
        <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">
          添加
        </el-button>
        <el-button
          icon="el-icon-document-copy"
          type="primary"
          plain
          @click="handleSynchronous"
        >
          同步
        </el-button>
        <!-- <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
        <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button> -->
        <el-button
          icon="el-icon-refresh"
          @click="refreshRoute()"
          :loading="asyncUser"
        >
          统一门户同步
        </el-button>
      </vab-query-form-right-panel>
    </vab-query-form>

    <el-table
      v-loading="listLoading"
      :data="list"
      :element-loading-text="elementLoadingText"
      border
      height="calc(100vh - 240px)"
    >
      <el-table-column prop="index" label="序号" width="80" align="center">
        <template #default="{ row, $index }">
          <span>
            {{ $index + 1 + (queryForm.pageNo - 1) * queryForm.pageSize }}
          </span>
        </template>
      </el-table-column>
      <!-- 员工编号 -->
      <el-table-column
        align="center"
        label="员工编号"
        prop="dahuaCode"
        show-overflow-tooltip
        width="120"
      ></el-table-column>
      <el-table-column
        align="center"
        label="用户姓名"
        prop="realName"
        show-overflow-tooltip
        min-width="120"
      ></el-table-column>
      <!-- 手机号 -->
      <el-table-column
        align="center"
        label="手机号"
        prop="phone"
        width="120"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        align="center"
        label="角色名称"
        prop="roleNamesStr"
        show-overflow-tooltip
        min-width="150"
      >
        <template slot-scope="scope">
          <div class="tag-box">
            <el-tag
              v-for="(item, index) in scope.row.roleNames"
              :key="index"
              size="mini"
            >
              {{ item }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="所属组织机构/部门"
        prop="deptName"
        show-overflow-tooltip
        width="180"
      ></el-table-column>
      <el-table-column
        :key="Math.random()"
        align="center"
        label="是否启用"
        prop="isEnable"
        width="100"
      >
        <template slot-scope="scope">
          <el-link
            :icon="
              scope.row.isEnable == 0 ? 'el-icon-warning' : 'el-icon-success'
            "
            :type="scope.row.isEnable == 0 ? 'danger' : 'success'"
            @click="changeHidden(scope.row)"
          >
            {{ scope.row.isEnable == 0 ? '禁用' : '启用' }}
          </el-link>
        </template>
      </el-table-column>

      <!--            <el-table-column align="center" label="创建人" prop="createUserName" show-overflow-tooltip width="120"></el-table-column>-->
      <!--            <el-table-column align="center" label="创建时间" prop="createTime" show-overflow-tooltip width="180"></el-table-column>-->
      <el-table-column
        align="center"
        label="修改人"
        prop="updateUserName"
        show-overflow-tooltip
        width="120"
      ></el-table-column>
      <el-table-column
        align="center"
        label="修改时间"
        prop="updateTime"
        show-overflow-tooltip
        width="180"
        :formatter="formatDate"
      ></el-table-column>
      <!-- <el-table-column show-overflow-tooltip label="权限">
              <template #default="{ row }">
                <el-tag v-for="(item, index) in row.permissions" :key="index" size="mini">
                  {{ item }}
                </el-tag>
              </template>
            </el-table-column> -->

      <el-table-column
        align="center"
        label="备注"
        minwidth="120"
        prop="memo"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template #default="{ row }">
          <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
                    <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link> -->
          <div style="text-align: left">
            <el-button
              icon="el-icon-edit"
              type="success"
              size="mini"
              v-if="row.loginName != 'admin' && !row.roleIds.includes(40167)"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              icon="el-icon-delete"
              type="danger"
              size="mini"
              v-if="row.loginName != 'admin'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </div>
          <!-- <el-button type="text" @click="handleView(row)">详情</el-button> -->
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="queryForm.pageNo"
      :layout="layout"
      :page-size="queryForm.pageSize"
      :total="total"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    ></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
    <bumen ref="bumen" @fetch-data=""></bumen>

    <!-- <div class="add-edit"></div> -->
  </div>
</template>

<script>
  import {
    doDelete,
    getList,
    doEnable,
    personnelSynchronization,
    syncAccountInfo,
  } from '@/api/userManagement'
  import { getList as getRoleList } from '@/api/roleManagement'

  import Edit from './components/UserManagementEdit'
  import bumen from '@/components/bumen'
  import { findOrg, findAllDeptByUnit } from '@/api/org'

  export default {
    name: 'UserManagement',
    components: { Edit, bumen },
    data() {
      return {
        searchShow: false,
        list: null,
        listLoading: true,
        layout: 'total, sizes, prev, pager, next, jumper',
        total: 0,
        selectRows: '',
        elementLoadingText: '正在加载...',
        queryForm: {
          pageNo: 1,
          pageSize: 20,
          realName: '',
          loginName: '',
          roleId: '',
          deptId: '',
          unitId: undefined,
        },
        deptLabel: '',
        roleList: [],
        treeData: [],
        deptList: [],
        isDeptDisabled: true,
        asyncUser: false,
      }
    },
    created() {
      this.fetchData()
      this.getRoleList()
      this.getTreeData()
    },
    methods: {
      /** 部门数据 */
      nodeClick(node) {
        this.queryForm.unitId = node.data.id
        this.deptLabel = node.data.orgName
        this.queryForm.deptId = ''

        // 查询部门列表
        if (this.deptLabel) {
          this.isDeptDisabled = false
          findAllDeptByUnit({orgId: node.data.id}).then((res) => {
          if (res.code == 0) {
            this.deptList = res.data
          }
        })
        } else {
          this.deptList = []
          this.isDeptDisabled = true
        }
        
      },
      // 单位数据
      getTreeData() {
        findOrg().then((res) => {
          if (res.code == 0) {
            this.treeData = res.data
          }
        })
      },
      ShowSearch() {
        this.searchShow = !this.searchShow
      },
      refresh() {
        location.reload()
      },
      reset() {
        this.queryForm = {
          pageNo: 1,
          pageSize: 20,
          realName: '',
          loginName: '',
          roleId: '',
          deptId: '',
          unitId: undefined,
        }
        this.deptLabel = ''
        this.isDeptDisabled = true
        this.fetchData()
      },
      changeHidden(row) {
        var isEnable = row.isEnable
        // 弹窗是否确定求改显示
        this.$baseConfirm(
          '你确定要' + (isEnable == 1 ? '禁用' : '启用') + '当前用户吗',
          null,
          async () => {
            const { msg } = await doEnable({
              id: row.id,
              isEnable: isEnable == 1 ? 0 : 1,
            })
            this.$baseMessage(
              '已' + (isEnable == 1 ? '禁用' : '启用'),
              'success'
            )
            this.fetchData()
          }
        )
      },
      setSelectRows(val) {
        this.selectRows = val
      },
      handleEdit(row) {
        if (row.id) {
          this.$refs['edit'].showEdit(row)
        } else {
          this.$refs['edit'].showEdit()
        }
      },
      handleSynchronous() {
        personnelSynchronization().then((res) => {
          if (res.code == 0) {
            this.$baseMessage(res.msg, 'success')
            this.fetchData()
          }
        })
      },
      handleDelete(row) {
        if (row.id) {
          this.$baseConfirm('你确定要删除当前项吗', null, async () => {
            let listLength = this.list.length
            const { msg } = await doDelete({ id: row.id })
            this.$baseMessage('删除成功', 'success')
            if (listLength == 1 && this.queryForm.pageNo > 1) {
              this.queryForm.pageNo--
            }
            this.deptLabel = ''
            this.fetchData()
          })
        } else {
          if (this.selectRows.length > 0) {
            const ids = this.selectRows.map((item) => item.id).join()
            this.$baseConfirm('你确定要删除选中项吗', null, async () => {
              const { msg } = await doDelete({ ids })
              this.$baseMessage(msg, 'success')
              this.fetchData()
            })
          } else {
            this.$baseMessage('未选中任何行', 'error')
            return false
          }
        }
      },
      handleSizeChange(val) {
        this.queryForm.pageSize = val
        this.fetchData()
      },
      handleCurrentChange(val) {
        this.queryForm.pageNo = val
        this.fetchData()
      },
      queryData() {
        this.queryForm.pageNo = 1
        this.fetchData()
      },
      openwin() {
        var that = this
        that.$refs['bumen'].showDia()
      },
      async fetchData() {
        if (
          this.deptLabel === '' ||
          this.deptLabel === null ||
          this.deptLabel === undefined
        ) {
          this.queryForm.unitId = undefined
        }
        this.listLoading = true
        const { data, totalCount } = await getList(this.queryForm)
        this.list = data.records
        this.total = data.total
        setTimeout(() => {
          this.listLoading = false
        }, 300)
      },
      async refreshRoute() {
        // this.$baseEventBus.$emit('reload-router-view')
        // this.pulse = true
        // setTimeout(() => {
        //   this.pulse = false
        // }, 1000)
        this.asyncUser = true
        syncAccountInfo()
          .then((res) => {
            this.$message.success('同步成功')
          })
          .finally(() => {
            this.asyncUser = false
          })
      },
      async getRoleList() {
        const { data } = await getRoleList()
        this.roleList = data
      },
    },
  }
</script>
<style scoped lang="scss">
  .left-panel-self .left-panel .el-form,
  .left-panel-self .left-panel .el-form-item__content {
    display: flex;
    flex-wrap: nowrap !important;
  }
  .tag-box {
    display: flex;
    flex-wrap: wrap;
    margin-top: 10px;
    .el-tag {
      margin-bottom: 10px;
      margin-right: 10px;
    }
    .el-tag + .el-tag {
      margin-left: 0;
    }
  }
  .index-container {
    position: relative;
  }
</style>
