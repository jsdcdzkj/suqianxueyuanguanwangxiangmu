<template>
  <div class="index-container">

    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-input v-model.trim="queryForm.username" placeholder="请输入设备名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryForm.username" placeholder="请选择设备类型">
              <el-option key="1" label="角色一" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryForm.username" placeholder="请选择设备所属分项">
              <el-option key="1" label="角色一" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryForm.username" placeholder="请选择设备所属区域">
              <el-option key="1" label="角色一" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryForm.username" placeholder="请选择设备状态">
              <el-option key="1" label="角色一" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="primary" @click="queryData">
              查询
            </el-button>
            <el-button icon="el-icon-refresh">
              重置
            </el-button>
          </el-form-item>
        </el-form>

      </vab-query-form-left-panel>
      <vab-query-form-right-panel :span="4">       
        <el-button icon="el-icon-edit" plain type="warning" @click="handlePlEdit">批量编辑</el-button>
        <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">添加</el-button>
        <el-button icon="el-icon-download" plain @click="handleDelete">导出</el-button>
        <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
        <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
      </vab-query-form-right-panel>
    </vab-query-form>

    <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
      @selection-change="setSelectRows" height="calc(100vh - 240px)" border>
      <el-table-column show-overflow-tooltip type="selection" width="60" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="username" label="设备名称" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="username" label="类型" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="id" label="所属分项" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="email" label="所属楼层/区域"   align="center"></el-table-column>
      <el-table-column label="状态" width="100" align="center" :key="Math.random()" prop="hidden">
        <template slot-scope="scope">
          <el-link :underline="false" :icon="scope.row.hidden == 0 ? 'el-icon-warning' : 'el-icon-success'" :type="scope.row.hidden == 0 ? 'danger' : 'success'" @click="changeHidden(scope.row)">{{scope.row.hidden == 0 ? "禁用" : "启用"}}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="{ row }">
          <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
          <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>
          <el-link :underline="false" type="primary" @click="handleView(row)">详情</el-link>
          <el-link :underline="false" type="primary" @click="handleSignal(row)">信号信息</el-link>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
      :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
    <plEdit ref="plEdit" @fetch-data="fetchData"></plEdit>
    <cjzdView ref="cjzdView" @fetch-data="fetchData"></cjzdView>
    <cjzdSignalView ref="cjzdSignalView" @fetch-data="fetchData"></cjzdSignalView>
  </div>
</template>

<script>
import { getList, doDelete } from '@/api/userManagement'
import Edit from './components/CjzdEdit'
import PlEdit from './components/CjzdPlEdit'
import CjzdView from './components/CjzdView'
import CjzdSignalView from './components/CjzdSignalView'

export default {
  name: 'UserManagement',
  components: { Edit, PlEdit, CjzdView, CjzdSignalView },
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
        username: '',
      },
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    ShowSearch() {
      this.searchShow = !this.searchShow
    },
    refresh() {
      location.reload();
    },
    setSelectRows(val) {
      this.selectRows = val
    },
    changeHidden(row){
      console.log(row.hidden)
      if (row.hidden) {
        row.hidden = 0
      } else {
        row.hidden = 1
      }
      
    },
    handleEdit(row) {
      if (row.id) {
        this.$refs['edit'].showEdit(row)
      } else {
        this.$refs['edit'].showEdit()
      }
    },
    handlePlEdit() {
      this.$refs['plEdit'].showEdit(this.selectRows)
    },
    handleView(row) {
      this.$refs['cjzdView'].showEdit(row)
    },
    handleSignal(row) {
      this.$refs['cjzdSignalView'].showEdit(row)
    },
    handleDelete(row) {
      if (row.id) {
        this.$baseConfirm('你确定要删除当前项吗', null, async () => {
          const { msg } = await doDelete({ ids: row.id })
          this.$baseMessage(msg, 'success')
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
    async fetchData() {
      this.listLoading = true
      const { data, totalCount } = await getList(this.queryForm)
      this.list = data
      this.total = totalCount
      setTimeout(() => {
        this.listLoading = false
      }, 300)
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
