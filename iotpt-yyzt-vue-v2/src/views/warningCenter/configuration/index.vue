<template>
  <div class="index-container">

    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent ref="queryForm">
          <el-form-item prop="deviceType" >
            <el-select v-model.trim="queryForm.deviceType" filterable placeholder="设备类型" class="w" @keyup.enter.native="queryData" clearable>
                <el-option
                    v-for="item in options"
                    :key="item.id"
                    :label="item.deviceTypeName"
                    :value="item.id">
                </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="configName" clearable>
            <el-input v-model.trim="queryForm.configName" placeholder="配置名称" clearable @keyup.enter.native="queryData" />
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
        <el-button icon="el-icon-plus" type="success" plain @click="handleEdit">添加</el-button>
        <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
        <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
        <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
        <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
      </vab-query-form-right-panel>
    </vab-query-form>

    <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
     height="calc(100vh - 240px)" border>
      <el-table-column show-overflow-tooltip label="序号" width="95" align="center">
        <template #default="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip prop="configName" label="配置名称" minwidth="120" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="deviceTypeName" label="设备类型" minwidth="120" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="configTypeName" label="配置范围" minwidth="120" align="center"></el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template #default="{ row }">
          <!-- <el-link type="success" :underline="false" @click="handleEdit(row)">编辑</el-link>
          <el-link type="danger" :underline="false" @click="handleDelete(row)">删除</el-link> -->

           <el-button icon="el-icon-edit"  type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
           <el-button icon="el-icon-delete" type="danger" size="mini"  @click="handleDelete(row)">删除</el-button>

          <!-- <el-button type="text" @click="handleView(row)">查看</el-button> -->
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
      :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
  </div>
</template>

<script>
import { getList, doDelete } from '@/api/userManagement'
import Edit from './components/edit'

import {getPageList as getWarnConfig, saveOrUpdate,delEntity} from '@/api/warnConfig'
import {getRedisDictList} from '@/api/sysDict'
import {getPageList as getconfigDeviceType } from '@/api/configDeviceType'
export default {
  name: 'department',
  components: { Edit },
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
        deviceType: '',
        configName:'',
      },
      sysdicForm: {
        dictType:'',
      },
      options: [],
    }
  },
  created() {
    this.getOptionData()
    this.fetchData()
  },
  methods: {
    ShowSearch() {
      this.searchShow = !this.searchShow
    },
    refresh() {
      location.reload();
    },
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.queryData();
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
    handleDelete(row) {
      if (row.id) {
        this.$baseConfirm('你确定要删除当前项吗', null, async () => {
          const { msg } = await delEntity({ "id": row.id })
          this.$baseMessage(msg, 'success')
          this.fetchData()
        })
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
      const {data} = await getWarnConfig(this.queryForm)

      this.list = data.records;
      this.total = data.total;
      setTimeout(() => {
        this.listLoading = false
      }, 300)
    },
    async getOptionData() {
      const {data} = await getconfigDeviceType()
      console.log(data)
      this.options = data.records;
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
