<template>
  <div class="index-container">

    <vab-query-form>
      <vab-query-form-left-panel :span="18">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent ref="queryForm">
          <el-form-item prop="modelName">
            <el-input v-model.trim="queryForm.modelName" placeholder="请输入模板名称" clearable
                      @keyup.enter.native="queryData"/>
          </el-form-item>
          <el-form-item prop="modelType">
            <el-select v-model="queryForm.modelType" placeholder="请选择模板类型" clearable>
              <el-option
                v-for="item in this.typeList"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              ></el-option>
            </el-select>
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
      <vab-query-form-right-panel :span="6">
        <!-- <el-button icon="el-icon-plus" type="success" @click="handleEdit">添加</el-button> -->
        <el-button-group>
          <el-button type="success" plain icon="  el-icon-collection-tag" @click="handleEdit">订阅主题模板</el-button>
          <el-button type="success" plain icon="el-icon-s-promotion" @click="handleFbEdit">发布主题模板</el-button>
          <el-button type="success" plain icon="el-icon-connection" @click="handleSysEdit">系统对接模板</el-button>
        </el-button-group>

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
      <el-table-column show-overflow-tooltip prop="modelName" label="模板名称" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="modelCode" label="模板编码" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="modelTypeName" label="模板类型" width="200"
                       align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="modelDesc" label="模板描述" align="center"></el-table-column>
      <el-table-column label="操作" width="300" align="center">
        <template #default="{ row }">
          <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
           <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>
           <el-link :underline="false" type="primary" @click="handleView(row)">详情</el-link>
          -->
          <el-button icon="el-icon-edit"  type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
          <el-button icon="el-icon-delete" type="danger" size="mini"  @click="handleDelete(row)">删除</el-button>
          <el-button icon="el-icon-info"  type="primary" size="mini" @click="handleView(row)">详情</el-button>

        </template>
      </el-table-column>
    </el-table>
    <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
                   :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
    <DataManagerFbEdit ref="DataManagerFbEdit" @fetch-data="fetchData"></DataManagerFbEdit>
    <DataManagerSysEdit ref="DataManagerSysEdit" @fetch-data="fetchData"></DataManagerSysEdit>
    <DataManagerView ref="DataManagerView" @fetch-data="fetchData"></DataManagerView>
    <DataManagerDyView ref="DataManagerDyView" @fetch-data="fetchData"></DataManagerDyView>
  </div>
</template>

<script>
  // 数据转换模板管理

  import {getRedisDictList} from '@/api/sysDict'
  import {getPageList, saveOrUpdate, del} from '@/api/configDataTransfer'
  import Edit from './components/DataManagerEdit/index'
  import DataManagerFbEdit from './components/DataManagerFbEdit'
  import DataManagerSysEdit from './components/DataManagerSysEdit'
  import DataManagerView from './components/DataManagerView'
  import DataManagerDyView from './components/DataManagerDyView/index'

  export default {
    name: 'dataManager',
    components: {Edit, DataManagerView, DataManagerSysEdit, DataManagerFbEdit, DataManagerDyView},
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
          modelName: '',
          modelType: '',
        },
        typeList: [],
      }
    },
    created() {
      //模板类型列表
      this.typeListData();
      this.fetchData()
        console.log("@1312i83y129i83y12iu3y9128")
    },
    methods: {
      ShowSearch() {
        this.searchShow = !this.searchShow
      },
      refresh() {
        location.reload()
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.$refs.queryForm.resetFields();
        this.queryData();
      },
      setSelectRows(val) {
        this.selectRows = val
      },
      handleEdit(row) {
        if (row.id) {
          if (row.modelType == "1") {
            this.$refs['edit'].showEdit(row.id)
          } else if (row.modelType == "2") {
            this.$refs['DataManagerFbEdit'].showEdit(row.id)
          } else if (row.modelType == "3") {
            this.$refs['DataManagerSysEdit'].showEdit(row.id)
          }
        } else {
          this.$refs['edit'].showEdit()
        }
      },
      handleFbEdit(row) {
        if (row.id) {
          this.$refs['DataManagerFbEdit'].showEdit(row.id)
        } else {
          this.$refs['DataManagerFbEdit'].showEdit()
        }
      },
      handleSysEdit(row) {
        if (row.id) {
          this.$refs['DataManagerSysEdit'].showEdit(row.id)
        } else {
          this.$refs['DataManagerSysEdit'].showEdit()
        }
      },
      handleView(row) {
        if (row.modelType == "1") {
          this.$refs['DataManagerDyView'].showEdit(row.id)
        } else {
          this.$refs['DataManagerView'].showEdit(row.id)
        }
      },
      handleDelete(row) {
        if (row.id) {
          this.$baseConfirm('你确定要删除当前项吗', null, async () => {
            const {msg} = await del({id: row.id, isDel: 1})
            this.$baseMessage("删除成功", 'success')
            this.fetchData()
          })
        } else {
          // if (this.selectRows.length > 0) {
          //   const ids = this.selectRows.map((item) => item.id).join()
          //   this.$baseConfirm('你确定要删除选中项吗', null, async () => {
          //     const {msg} = await doDelete({ids})
          //     this.$baseMessage(msg, 'success')
          //     this.fetchData()
          //   })
          // } else {
          //   this.$baseMessage('未选中任何行', 'error')
          //   return false
          // }
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
      async typeListData() {
        const {data} = await getRedisDictList({dictType: "config_data_transfer"})
        console.log(data)
        this.typeList = data;
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
