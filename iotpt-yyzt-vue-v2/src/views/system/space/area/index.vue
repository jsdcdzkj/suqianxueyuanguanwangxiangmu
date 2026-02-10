<template>
  <div class="index-container">

    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-select v-model="queryForm.buildFloorId" filterable placeholder="所属楼宇" class="w" clearable @change="getFloor">
              <el-option
              v-for="item in options2"
              :key="item.id"
              :label="item.floolName"
              :value="item.id">
            </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryForm.floorId" filterable placeholder="所属楼层" class="w" clearable @change="queryData">
              <el-option
              v-for="item in options"
              :key="item.id"
              :label="item.floolName"
              :value="item.id">
            </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input v-model.trim="queryForm.areaName" placeholder="区域名称" clearable @keyup.enter.native="queryData"/>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="primary" @click="queryData">
              查询
            </el-button>
            <el-button icon="el-icon-refresh" @click="reseat()">
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
      <el-table-column show-overflow-tooltip prop="areaName" label="区域名称" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="code" label="区域编码" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="floolName" label="所属楼层" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="memo" label="描述" minwidth="220" align="center"></el-table-column>
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
import { allBuildFloolList,selectBuildAreaList,delBuildArea } from '@/api/area'
  import { allBuildList } from '@/api/floor'
import Edit from './components/edit'

export default {
  name: 'areaSpace',
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
        floorId: '',
        buildFloorId:'',
        areaName:'',
      },
      options: [],
      options2: [],
    }
  },
  created() {
    this.queryData()
    this.getBuils()
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
    handleDelete(row) {
      if (row.id) {
        this.$baseConfirm('你确定要删除当前项吗', null, async () => {
          delBuildArea(row.id).then((res) => {
            if(res.code == 0){
              this.$baseMessage("删除成功", 'success')
              this.queryData()
            }
          })


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
      if(this.queryForm.floorId == "" && this.queryForm.buildFloorId !=""){
        this.$baseMessage('请先选择所属楼层', 'error');
        this.listLoading = false
        return ;

      }
      selectBuildAreaList(this.queryForm).then((res) => {
        if(res.code == 0){
          this.list = res.data.records ;
          this.total =res.data.total
          this.listLoading = false
        }
      })
    },
    getFloor(){
      //楼层下拉
      this.queryForm.floorId = ""
      if(this.queryForm.buildFloorId !=""){
        allBuildFloolList(this.queryForm.buildFloorId).then((res) => {
          if(res.code == 0){
            this.options = res.data ;
          }
        })
      }

    },
    getBuils(){
      //楼宇下拉
      allBuildList().then((res) => {
        if(res.code == 0){
          this.options2 = res.data ;
        }
      })
    },
    reseat(){
      this.queryForm.buildFloorId = ""
      this.queryForm.floorId= ""
      this.queryForm.areaName= ""
      this.queryData() ;
    }
  },
}
</script>
