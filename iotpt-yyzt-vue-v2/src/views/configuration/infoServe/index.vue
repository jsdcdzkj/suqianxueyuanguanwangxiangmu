<template>
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
            <el-input v-model.trim="queryForm.username" placeholder="请输入设备名称" clearable />
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
        <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">添加</el-button>
        <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
        <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
        <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
        <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
      </vab-query-form-right-panel>
    </vab-query-form>

    <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText" @selection-change="setSelectRows" height="calc(100vh - 240px)" border>
      <el-table-column show-overflow-tooltip prop="username" label="设备名称" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="email" label="所属区域" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="datatime" label="供应商" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="datatime" label="设备型号" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="datatime" label="设备品牌" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="datatime" label="设备描述" align="center"></el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template #default="{ row }">
          <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
          <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>
          <el-dropdown trigger="click">
            <el-link :underline="false" type="text">更多<i class="el-icon-arrow-down el-icon--right"></i></el-link>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <el-link :underline="false" type="primary" @click="handleView(row)" style="width: 100%;">详情</el-link>
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-link :underline="false" type="primary" @click="handleUpload(row)" style="width: 100%;">上传配置模板</el-link>
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-link :underline="false" type="primary" @click="handleDown(row)" style="width: 100%;">生成配置文件</el-link>
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-link :underline="false" type="primary" @click="handleDeviceView(row)" style="width: 100%;">设备信息</el-link>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
    <wgsbView ref="wgsbView" @fetch-data="fetchData"></wgsbView>
    <glView ref="glView" @fetch-data="fetchData"></glView>
    <uploadView ref="uploadView" @fetch-data="fetchData"></uploadView>
  </div>
</template>

<script>
import { getList, doDelete } from '@/api/userManagement'
import Edit from './components/WgsbEdit'
import WgsbView from './components/WgsbView'
import glView from './components/WgsbGlView'
import uploadView from './components/uploadView'

export default {
  name: 'UserManagement',
  components: { Edit,WgsbView, glView, uploadView },
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
    handleEdit(row) {
      if (row.id) {
        this.$refs['edit'].showEdit(row)
      } else {
        this.$refs['edit'].showEdit()
      }
    },
    handleView(row) {
      this.$refs['wgsbView'].showEdit(row)
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
    handleUpload(row) {
      this.$refs['uploadView'].showEdit(row)
    },
    handleDown() {},
    handleDeviceView(row) {
      this.$refs['glView'].showEdit(row)
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
<style scoped lang="scss">


  .el-dropdown {
    margin-left: 10px;
    ::v-deep{
      .el-button--text{
        width: 100% !important;
      }
    }
  }

  
</style>