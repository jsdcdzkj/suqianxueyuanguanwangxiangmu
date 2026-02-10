<template>
  <el-drawer :visible.sync="dialogFormVisible" title="信号信息" size="1000px" @close="close">
    <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>信号信息</b></span>
    <div style="padding: 0 20px">
      <div class="title">
        <!-- <div class="tips-title"> <i class="el-icon-s-promotion"></i>设备名称：xx设备</div> -->
        <div class="tips-title"><i class="el-icon-s-promotion"></i> 所属区域：鼎驰大厦/10楼/开敞办公区</div>
      </div>
      <el-table ref="multipleTable" :data="tableData" v-loading="listLoading" :element-loading-text="elementLoadingText" height="355px" border>
        <el-table-column type="selection" align="center">
        </el-table-column>
        <el-table-column label="信号名称" show-overflow-tooltip align="center">
          <template slot-scope="scope">{{ scope.row.date }}</template>
        </el-table-column>
        <el-table-column prop="name" label="信号值" show-overflow-tooltip align="center">
        </el-table-column>
        <el-table-column prop="address" label="单位" show-overflow-tooltip align="center">
        </el-table-column>
      </el-table>
      <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>

      <div class="title" style="margin-top: 36px;">
        <div class="tips-title"> <i class="el-icon-s-promotion"></i>历史记录</div>
      </div>
      <vab-query-form>
        <vab-query-form-left-panel :span="16">
          <el-form :inline="true" :model="queryForm2" @submit.native.prevent>
            <el-form-item label="采集时间起" prop="org_code">
              <el-input v-model.trim="queryForm2.data" placeholder="采集时间起" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="采集时间止" prop="org_code">
              <el-input v-model.trim="queryForm2.data1" placeholder="采集时间止" autocomplete="off"></el-input>
            </el-form-item>
          </el-form>
        </vab-query-form-left-panel>
        <vab-query-form-right-panel :span="8">
          <el-button icon="el-icon-search" type="primary" @click="queryData">
            查询
          </el-button>
          <el-button icon="el-icon-refresh">
            重置
          </el-button>
        </vab-query-form-right-panel>
      </vab-query-form>
      <el-table ref="multipleTable" :data="tableData" v-loading="loading" :element-loading-text="elementLoadingText" height="355px" border>
        <el-table-column type="selection" align="center">
        </el-table-column>
        <el-table-column label="信号名称" show-overflow-tooltip align="center">
          <template slot-scope="scope">{{ scope.row.date }}</template>
        </el-table-column>
        <el-table-column prop="name" label="信号值" show-overflow-tooltip align="center">
        </el-table-column>
        <el-table-column prop="address" label="单位" show-overflow-tooltip align="center">
        </el-table-column>
      </el-table>
      <el-pagination background :current-page="queryForm2.pageNo" :page-size="queryForm2.pageSize" :layout="layout" :total="total2" @size-change="handleSizeChange2" @current-change="handleCurrentChange2"></el-pagination>
    </div>

  </el-drawer>
</template>

<script>
import { doEdit } from '@/api/userManagement'
import bumen from '@/components/bumen'

export default {
  name: 'UserManagementEdit',
  components: { bumen },
  data() {
    return {
      total: 0,
      total2: 0,
      queryForm: {
        pageNo: 1,
        pageSize: 10,
        data1: '',
        data: '',
      },
      queryForm2: {
        pageNo: 1,
        pageSize: 10,
        username: '',
      },
      layout: 'total, sizes, prev, pager, next, jumper',
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
        {
          date: '2016-05-07',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄',
        },
        {
          date: '2016-05-07',
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
      // this.$refs['ruleForm'].resetFields()
      // this.form = this.$options.data().form
      this.dialogFormVisible = false
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
    handleSizeChange(val) {
      this.queryForm.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.queryForm.pageNo = val
      this.fetchData()
    },
    handleSizeChange2(val) {
      this.queryForm.pageSize = val
      this.fetchData()
    },
    handleCurrentChange2(val) {
      this.queryForm.pageNo = val
      this.fetchData()
    },
  },
}
</script>
<style scoped lang="scss">
.jiexi {
  position: absolute;
  right: 21px;
  top: 100px;
  z-index: 1;
  padding: 5px 15px;
}
::v-deep {
  .el-drawer__header {
    padding: 10px !important;
    margin-bottom: 14px;
    border-bottom: 1px solid #d1d9e1;

    .drawer-title {
      font-size: 16px;
      line-height: 16px;
      color: #334c97;
      display: flex;
      align-items: center;

      i {
        display: block;
        font-size: 18px;
        line-height: 18px;
        margin-right: 4px;
      }

      b {
        display: block;
        font-size: 16px;
        line-height: 16px;
        margin-top: 2px;
        margin-right: 4px;
      }
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

  .el-timeline {
    padding-left: 0 !important;
  }

  .timeline-con {
    padding: 10px 4px;
    height: calc(100vh - 598px);
    overflow-y: auto;
  }

  .time-con {
    margin-left: -6px;
    color: #333;
    // box-shadow: 0 0px 4px 0 rgba(0, 0, 0, 0.1);
    border-top: 4px solid #ccd2e6;
    background-color: #f5f6fb;

    .time-top {
      padding: 10px;

      span.time-name {
        margin-right: 10px;
        color: #666;
        font-weight: bold;

        i {
          color: #ccd2e6;
          margin-right: 4px;
        }

        b {
          font-weight: normal;
          color: #999;
          margin-left: 12px;
          font-size: 13px;
        }
      }

      .el-tag {
        float: right;
        margin-top: -2px;
      }
    }

    .time-bot {
      display: flex;
      padding: 0px 10px 6px;
      align-items: flex-start;
      line-height: 24px;
      font-size: 14px;

      i {
        display: inline-block;
        width: 16px;
        height: 24px;
        margin-right: 4px;
        line-height: 24px;
        color: #ccd2e6;
      }

      p {
        margin: 0;
        color: #666;
      }
    }
  }

  .el-radio {
    padding: 6px 12px;
    background-color: #f5fbff;
    margin-bottom: 6px;
    margin-right: 6px;
  }

  .my-label {
    background: #e1f3d8;
  }

  .my-content {
    width: 280px;
  }
}
.footer-btn {
  padding: 12px 20px;
  border-top: 1px solid #efefef;
  position: absolute;
  bottom: 0;
  text-align: right;
  width: 100%;
  background-color: #fff;
  z-index: 999;
}
</style>
<style lang="scss">
// 控制的dialog样式需要穿透到底层，所以这里不能写scoped属性
// 如果不想影响全局，可以给el-dialog的父级定义一个class,再把.center-dialog置于这个class的嵌套里面
// .center-dialog {
//     top: 50%;
//     transform: translateY(-50%)
// }
</style>