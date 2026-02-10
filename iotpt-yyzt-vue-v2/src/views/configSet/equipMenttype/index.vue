<template>
  <div class="index-container">

    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-input v-model.trim="queryForm.deviceTypeName" placeholder="请输入设备类型名称" clearable @keyup.enter.native="queryData"/>
          </el-form-item>
          <el-form-item>
            <el-input v-model.trim="queryForm.deviceTypeCode" placeholder="请输入设备类型编码" clearable @keyup.enter.native="queryData"/>
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
        <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">添加</el-button>
        <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
        <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
        <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
        <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
      </vab-query-form-right-panel>
    </vab-query-form>

    <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
      @selection-change="setSelectRows" height="calc(100vh - 240px)" border>
      <el-table-column prop="index" label="序号" width="80" align="center">
        <template #default="{ row, $index }">
          <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip prop="deviceTypeName" label="设备类型" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="deviceTypeCode" label="设备类型编码" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="deviceTypeDesc" label="描述"  align="center"></el-table-column>
      <el-table-column label="操作" width="300" align="center">
        <template #default="{ row }">
          <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
          <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>
          <el-link :underline="false" type="primary" @click="handleView(row)">信号类型</el-link> -->
          <el-button icon="el-icon-edit"  type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
          <el-button icon="el-icon-delete" type="danger" size="mini"  @click="handleDelete(row)">删除</el-button>
          <el-button icon="el-icon-info" type="primary" size="mini"  @click="handleView(row)">信号类型</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
      :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
    <signal ref="signal" @fetch-data="fetchData"></signal>
  </div>
</template>

<script>
import { delConfigDeviceType, getPageList } from '@/api/configDeviceType'
import { doDelete } from '@/api/userManagement'
import Edit from './components/EquipMenttypeEdit'
import Signal from './components/SignalTpye'

export default {
  name: 'EquipMenttype',
  components: { Edit, Signal },
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
        deviceTypeName: '',
        deviceTypeCode: '',
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
    handleEdit(row) {
      if (row.id) {
        this.$refs['edit'].showEdit(row)
      } else {
        this.$refs['edit'].showEdit()
      }
    },
    handleView(row) {
        this.$refs['signal'].showEdit(row)
    },
    handleDelete(row) {
      if (row.id) {
        this.$baseConfirm('你确定要删除当前项吗', null, async () => {
          const { msg } = await delConfigDeviceType({ id: row.id })
          this.$baseMessage(msg, 'success')
          this.fetchData()
        })
      } else {
        if (this.selectRows.length > 0) {
          const ids = this.selectRows.map((item) => item.id).join()
          this.$baseConfirm('你确定要删除选中项吗', null, async () => {
            const { msg } = await doDelete({ id })
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
    resetQuery() {
      this.queryForm.deviceTypeName = ''
      this.queryForm.deviceTypeCode = ''
      this.queryForm.pageNo = 1
      this.queryForm.pageSize = 10
      this.fetchData()
    },
    async fetchData() {
      this.listLoading = true
      const {data} = await getPageList(this.queryForm)
      this.list = data.records;
      this.total = data.total;
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
