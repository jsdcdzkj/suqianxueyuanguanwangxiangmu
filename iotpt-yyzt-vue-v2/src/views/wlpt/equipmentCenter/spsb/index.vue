<template>
  <div class="index-container">
    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-select
              v-model="queryForm.areaId"
              placeholder="选择所属区域"
              clearable
              @keyup.enter.native="queryData"
            >
              <el-option
                v-for="item in this.areaList"
                :key="item.id"
                :label="item.areaName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model.trim="queryForm.name"
              placeholder="请输入设备名称"
              clearable
              @keyup.enter.native="queryData"
            />
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="primary" @click="queryData">
              查询
            </el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </vab-query-form-left-panel>
      <vab-query-form-right-panel :span="4">
        <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">
          添加
        </el-button>
        <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
        <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
        <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
        <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
      </vab-query-form-right-panel>
    </vab-query-form>

    <el-table
      v-loading="listLoading"
      :data="list"
      :element-loading-text="elementLoadingText"
      @selection-change="setSelectRows"
      height="calc(100vh - 240px)"
      border
    >
      <el-table-column prop="index" label="序号" width="80" align="center">
        <template #default="{ row, $index }">
          <span>
            {{ $index + 1 + (queryForm.pageNo - 1) * queryForm.pageSize }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="name"
        label="设备名称"
        width="200"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="deviceCode"
        label="设备编码"
        width="200"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="areaName"
        label="所属区域"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="supplierName"
        label="供应商"
        width="200"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="modelName"
        label="设备型号"
        width="200"
        align="center"
      ></el-table-column>
      <el-table-column
        label="设备状态"
        width="100"
        align="center"
        :key="Math.random()"
        prop="status"
      >
        <template slot-scope="scope">
          <el-link
            :icon="
              scope.row.status == 0 ? 'el-icon-warning' : 'el-icon-success'
            "
            :type="scope.row.status == 0 ? 'danger' : 'success'"
            @click="changeHidden(scope.row)"
          >
            {{ scope.row.status == 0 ? '禁用' : '启用' }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="deviceDesc"
        label="设备描述"
        align="center"
      ></el-table-column>
      <el-table-column
        label="在线状态"
        width="100"
        align="center"
        prop="onLineStatus"
      >
        <template slot-scope="{ row }">
          <el-link
            :underline="false"
            icon="el-icon-success"
            type="success"
            v-if="row.onLineStatus == 1"
          >
            正常
          </el-link>
          <el-link
            :underline="false"
            icon="el-icon-error"
            type="danger"
            v-if="row.onLineStatus == 2"
          >
            离线
          </el-link>
          <el-link
            :underline="false"
            icon="el-icon-warning"
            type="warning"
            v-if="row.onLineStatus == 3"
          >
            异常
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" align="center">
        <template #default="{ row }">
          <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
          <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>
          <el-link :underline="false" type="primary" @click="handleView(row)">详情</el-link> -->
          <el-button
            icon="el-icon-edit"
            type="success"
            size="mini"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            icon="el-icon-delete"
            type="danger"
            size="mini"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
          <el-button
            icon="el-icon-info"
            type="primary"
            size="mini"
            @click="handleView(row)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      :current-page="queryForm.pageNo"
      :page-size="queryForm.pageSize"
      :layout="layout"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    ></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
    <spsbView ref="spsbView" @fetch-data="fetchData"></spsbView>
  </div>
</template>

<script>
  import { getPage, saveOrUpdate, del } from '@/api/spsb'
  import { getBuildAreaList } from '@/api/sysBuildArea'
  import Edit from './components/SpsbEdit'
  import SpsbView from './components/SpsbView'

  export default {
    name: 'UserManagement',
    components: { Edit, SpsbView },
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
          pageSize: 10,
          name: '',
          areaId: '',
        },
        areaList: [],
      }
    },
    created() {
      this.fetchData()
      this.areaListData()
    },
    methods: {
      ShowSearch() {
        this.searchShow = !this.searchShow
      },
      async areaListData() {
        const { data } = await getBuildAreaList()
        console.log('所属区域', data)
        this.areaList = data
      },
      refresh() {
        location.reload()
      },
      setSelectRows(val) {
        this.selectRows = val
      },
      changeHidden(row) {
        var status = row.status
        // 弹窗是否确定求改显示
        this.$baseConfirm(
          '确定' + (status == 1 ? '禁用' : '启用') + '当前视频设备吗',
          null,
          async () => {
            row.status = row.status ^ 1
            saveOrUpdate(row).then((res) => {
              if (0 == res.code) {
                this.$baseMessage(
                  '已' + (status == 1 ? '禁用' : '启用'),
                  'success'
                )
                this.fetchData()
              } else {
                this.$baseMessage(
                  (status == 1 ? '禁用' : '启用') + '失败',
                  'error'
                )
              }
            })
          }
        )
      },
      handleEdit(row) {
        if (row.id) {
          this.$refs['edit'].showEdit(row)
        } else {
          this.$refs['edit'].showEdit()
        }
      },
      handleView(row) {
        this.$refs['spsbView'].showEdit(row)
      },
      handleDelete(row) {
        if (row.id) {
          this.$baseConfirm('确定删除当前项吗', null, async () => {
            del({ id: row.id }).then((res) => {
              if (0 == res.code) {
                this.$baseMessage('删除成功', 'success')
                this.fetchData()
              } else {
                this.$baseMessage(res.msg, 'success')
              }
            })
          })
        } else {
          if (this.selectRows.length > 0) {
            const ids = this.selectRows.map((item) => item.id).join()
            this.$baseConfirm('确定删除选中项吗', null, async () => {
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
      async fetchData() {
        this.listLoading = true
        getPage(this.queryForm).then((res) => {
          if (0 == res.code) {
            this.list = res.data.records
            this.total = res.data.total
            if (res.data.records.length == 0 && this.queryForm.pageNo > 1) {
              this.queryForm.pageNo = this.queryForm.pageNo - 1
              this.fetchData()
            }
          }
        })

        setTimeout(() => {
          this.listLoading = false
        }, 300)
      },
      resetQuery() {
        this.queryForm.name = ''
        this.queryForm.areaId = ''
        this.queryForm.pageNo = 1
        this.queryForm.pageSize = 10
        this.queryData()
      },
      async refreshRoute() {
        this.$baseEventBus.$emit('reload-router-view')
        this.pulse = true
        setTimeout(() => {
          this.pulse = false
        }, 1000)
      },
    },
  }
</script>
