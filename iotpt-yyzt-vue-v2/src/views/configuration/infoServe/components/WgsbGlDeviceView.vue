<template>
  <el-dialog :visible.sync="dialogFormVisible" append-to-body width="1300px" @close="close" title="设备列表">
    <div class="index-container">

      <vab-query-form>
        <vab-query-form-left-panel :span="20">
          <el-form :inline="true" :model="queryForm" @submit.native.prevent>
            <el-form-item>
              <el-select v-model="queryForm.username" placeholder="选择所属区域">
                <el-option key="1" label="角色一" value="1"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryForm.username" placeholder="选择设备类型">
                <el-option key="1" label="角色一" value="1"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model.trim="queryForm.username" placeholder="请输入设备名称" clearable />
            </el-form-item>
            <el-form-item>
              <el-input v-model.trim="queryForm.username" placeholder="请输入设备编码" clearable />
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" type="primary" @click="queryData">
                查询
              </el-button>
              <!-- <el-button icon="el-icon-refresh">
              重置
            </el-button> -->
            </el-form-item>
          </el-form>

        </vab-query-form-left-panel>
        <!-- <vab-query-form-right-panel :span="4">
          <el-button icon="el-icon-plus" type="success" @click="handleEdit">关联</el-button>
        </vab-query-form-right-panel> -->
      </vab-query-form>

      <el-table v-loading="listLoading" :data="list"  :element-loading-text="elementLoadingText" @selection-change="setSelectRows" height="600" border>
        <el-table-column show-overflow-tooltip type="selection" width="60" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="username" label="设备名称" width="120" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="datatime" label="设备类型" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="datatime" label="设备编码" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="datatime" label="所属楼层/区域" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="email" label="所属区域" align="center"></el-table-column>
      </el-table>
      <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>

    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getList, doDelete } from '@/api/userManagement'

export default {
  name: 'UserManagement',
  data() {
    return {
      dialogFormVisible: false,
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
    showEdit(row) {
      this.dialogFormVisible = true
    },
    close() {
      this.dialogFormVisible = false
    },
    ShowSearch() {
      this.searchShow = !this.searchShow
    },
    refresh() {
      location.reload()
    },
    setSelectRows(val) {
      this.selectRows = val
    },
    changeHidden(row) {
      console.log(row.hidden)
      if (row.hidden) {
        row.hidden = 0
      } else {
        row.hidden = 1
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
    handleEdit() {}
  },
}
</script>
