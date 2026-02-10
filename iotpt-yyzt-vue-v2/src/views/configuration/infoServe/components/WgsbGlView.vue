<template>
  <div>
    <el-dialog :visible.sync="dialogFormVisible" custom-class="gl-view" append-to-body size="800px" @close="close" title="设备信息">

      <!-- <span slot="title" class="el-dialog-title"><i class="el-icon-menu"></i><b>设备信息</b></span> -->
      <div style="text-align: right;  margin: 10px 0;">
        <el-button icon="el-icon-link" type="primary" @click="handleDeviceTableView">
          关联设备
        </el-button>
      </div>
      <el-table ref="multipleTable" :data="tableData" v-loading="loading" :element-loading-text="elementLoadingText" height="400px" border>
        <el-table-column label="信号名称" show-overflow-tooltip align="center">
          <template slot-scope="scope">{{ scope.row.date }}</template>
        </el-table-column>
        <el-table-column prop="name" label="信号值" show-overflow-tooltip align="center">
        </el-table-column>
        <el-table-column prop="address" label="单位" show-overflow-tooltip align="center">
        </el-table-column>
        <el-table-column label="操作" width="360" align="center">
          <template #default="{ row }">
            <el-button type="text" @click="handleDelete(row)">取消关联</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
    <wgsbGlDeviceView ref="wgsbGlDeviceView" @fetch-data="fetchData"></wgsbGlDeviceView>
  </div>

</template>

<script>
import { doEdit } from '@/api/userManagement'
import WgsbGlDeviceView from './WgsbGlDeviceView'

export default {
  name: 'WgsbGlView',
  components: { WgsbGlDeviceView },
  data() {
    return {
      queryForm: {
        pageNo: 1,
        pageSize: 10,
        data1: '',
        data: '',
      },
      listLoading: false,
      loading: false,
      elementLoadingText: '正在加载...',
      disabled: false,
      title: '',
      dialogFormVisible: false,
      tableData: [
        {
          date: '2016-05-03',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄',
        },
        {
          date: '2016-05-02',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄',
        },
        {
          date: '2016-05-04',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄',
        },
        {
          date: '2016-05-01',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄',
        },
        {
          date: '2016-05-08',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄',
        },
        {
          date: '2016-05-06',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄',
        },
        {
          date: '2016-05-07',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄',
        },
      ],
      multipleSelection: [],
    }
  },
  created() {},
  methods: {
    showEdit(row) {
      this.dialogFormVisible = true
    },
    close() {
      this.dialogFormVisible = false
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
    handleDeviceTableView(row) {
      this.$refs['wgsbGlDeviceView'].showEdit(row)
    },
  },
}
</script>
<style scoped lang="scss">
.title {
  display: flex;
  margin-top: 20px;

  div:first-child {
    margin-right: 40px;
  }
}

::v-deep {
  .el-dialog__header {
    font-size: 14px;
    line-height: 16px;
    color: #334c97;

    i {
      font-size: 16px;
      line-height: 16px;
      margin-right: 4px;
      vertical-align: middle;
    }

    b {
      font-size: 14px;
      line-height: 16px;
      margin-right: 4px;
      vertical-align: middle;
    }
  }

  .tips-title {
    font-size: 14px;
    margin-bottom: 8px;
    font-weight: bold;
    color: #334c97;

    i {
      margin-right: 4px;
    }
  }
}
</style>

<style lang="scss">
.gl-view {
  .el-dialog__body {
    padding: 0 20px;
  }
}
</style>
