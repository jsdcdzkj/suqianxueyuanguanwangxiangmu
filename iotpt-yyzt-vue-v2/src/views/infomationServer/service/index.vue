<template>
  <div class="roleManagement-container">
    <!-- <el-divider content-position="left">
      演示环境仅做基础功能展示，若想实现不同角色的真实菜单配置，需将settings.js路由加载模式改为all模式，由后端全面接管路由渲染与权限控制
    </el-divider> -->
    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-input v-model.trim="queryForm.msgName" placeholder="请输入配置名称" @keyup.enter.native="queryData" clearable />
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryForm.msgType" filterable placeholder="请选择服务类型" class="w" @keyup.enter.native="queryData" clearable>
<!--                <el-option value="1" label="邮件"></el-option>-->
<!--                <el-option value="2" label="短信"></el-option>-->

                <el-option v-for="item in supplierTypes" :key="item.id" :label="item.dictLabel" :value="item.dictValue"></el-option>
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
      <vab-query-form-right-panel :span="4">
        <el-button icon="el-icon-plus" type="success" plain @click="handleEdit">添加</el-button>
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
      height="calc(100vh - 240px)"
      border
    >
        <el-table-column prop="index" label="序号" width="80" align="center">
            <template #default="{ row, $index }">
                <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
            </template>
        </el-table-column>
          <el-table-column
            show-overflow-tooltip
            prop="msgName"
            label="配置名称"
            align="center"
            minwidth="160"
          ></el-table-column>
          <el-table-column
            show-overflow-tooltip
            prop="msgName"
            label="通知方式"
            align="center"
            minwidth="160"
          ></el-table-column>
          <el-table-column
            show-overflow-tooltip
            prop="msgName"
            label="说明"
            align="center"
            minwidth="160"
          ></el-table-column>
          <!-- <el-table-column label="状态" :key="Math.random()" prop="status" width="100" align="center">
            <template slot-scope="scope">
              <el-link :icon="scope.row.status === 0 ? 'el-icon-warning' : 'el-icon-success'" :type="scope.row.status === 0 ? 'danger' : 'success'" @click="changeHidden(scope.row)">{{scope.row.status === 0 ? "禁用" : "启用"}}</el-link>
            </template>
          </el-table-column> -->

      <el-table-column show-overflow-tooltip label="操作" width="160" align="center">
        <template #default="{ row }">
          <!-- <el-link type="success" :underline="false" @click="handleEdit(row)">编辑</el-link>
          <el-link type="danger" :underline="false" @click="handleDelete(row)">删除</el-link> -->
           <el-button icon="el-icon-edit"  type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
           <el-button icon="el-icon-delete" type="danger" size="mini"  @click="handleDelete(row)">删除</el-button>
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
  </div>
</template>

<script>

  import Edit from './components/edit'
  import {changeMsg, getMsgPage, delOne} from '@/api/msgInfo'

  export default {
    name: 'service',
    components: { Edit },
    data() {
      return {
        list: null,
        listLoading: true,
        layout: 'total, sizes, prev, pager, next, jumper',
        total: 0,
        selectRows: '',
        elementLoadingText: '正在加载...',
        queryForm: {
          pageNo: 1,
          pageSize: 20,
          msgName: '',
          msgType: '',
        },
          supplierTypes: [],
      }
    },
    created() {
        this.getDictByKey().then(res => {
            this.supplierTypes = res.data['msg_type']
        }).then(() => {
            this.fetchData()
        })
    },
    methods: {
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
      changeHidden(row){
          this.$baseConfirm('确定要更改服务状态吗', null, async () => {
              if (row.status === 1) {
                  var x = {};
                  x.id = row.id;
                  x.status = 0 ;
                  changeMsg(x).then((res) => {
                      if (res.code === 0){
                          // row.status = 0;
                          this.fetchData();
                      }else {
                          this.$baseMessage("状态更改失败", 'error')
                      }
                  })
              } else {
                  var x = {};
                  x.id = row.id;
                  x.status = 1 ;
                  changeMsg(x).then((res) => {
                      if (res.code === 0){
                          // row.status = 1
                          this.fetchData();
                      }else {
                          this.$baseMessage("状态更改失败", 'error')
                      }
                  })
              }
              this.$forceUpdate()
          });


      },
      handleDelete(row) {
        if (row.id) {
          this.$baseConfirm('你确定要删除当前项吗', null, async () => {
              var x = {};
              x.id = row.id;
              delOne(x).then((res) => {
                  if (res.code === 0){
                      this.$baseMessage("删除成功", 'success')
                      // 为了在删除最后一页的最后一条数据时能成功跳转回最后一页的上一页
                      const totalPage = Math.ceil((this.total - 1) / this.queryForm.pageSize) // 总页数
                      this.queryForm.pageNo = this.queryForm.pageNo > totalPage ? totalPage : this.queryForm.pageNo
                      this.queryForm.pageNo = this.queryForm.pageNo < 1 ? 1 : this.queryForm.pageNo

                      this.fetchData()
                  }else {
                      this.$baseMessage("删除失败", 'error')
                  }
              })
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
        const { data, totalCount } = await getMsgPage(this.queryForm)
        this.list = data.records;
        this.total = data.total
          console.log('11111',this.supplierTypes);

        setTimeout(() => {
          this.listLoading = false
        }, 300)
      },
        resetQuery() {
            this.queryForm.msgName = ''
            this.queryForm.msgType = ''
            this.queryForm.pageNo = 1
            this.queryForm.pageSize = 20
            this.queryData()
        },
    },
  }
</script>
