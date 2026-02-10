<template>
  <!-- @close="close"  -->
  <el-drawer :visible.sync="dialogFormVisible" size="1200px" :before-close="handleClose">
    <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>数据转换配置</b></span>
    <div style="padding: 0 20px 80px 20px;position: relative;" class="title-manager-view">
      <el-form :inline="true" :model="queryForm" @submit.native.prevent>
        <el-form-item label="请选择模板">
          <el-select v-model="queryForm.username" placeholder="请选择模板">
            <el-option key="1" label="主题一模板" value="1"></el-option>
            <el-option key="2" label="主题二模板" value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-button type="primary" size="mini" class="jiexi">解析测试</el-button>
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="数据" name="shuju">
          <shuju ref="shuju" @fetch-data="fetchData"></shuju>
        </el-tab-pane>
        <el-tab-pane label="告警" name="gaojing">
          <gaojing ref="gaojing" @fetch-data="fetchData"></gaojing>
        </el-tab-pane>
        <el-tab-pane label="心跳" name="xintiao">
          <xintiao ref="xintiao" @fetch-data="fetchData"></xintiao>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="footer-btn">
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">立即提交</el-button>
      <el-button @click="close">取消</el-button>
    </div>
  </el-drawer>
</template>

<script>
import shuju from './shuju'
import gaojing from './gaojing'
import xintiao from './xintiao'

export default {
  name: 'UserManagementEdit',
  components: { shuju, gaojing, xintiao },
  data() {
    return {
      activeName: 'gaojing',
      queryForm: {
        pageNo: 1,
        pageSize: 10,
        username: '',
      },
      loading: false,
      disabled: false,
      sizeForm: {
        name: '默认文字',
      },
      ruleForm: {
        desc: '',
        time: '',
        radio: 1,
        radio2: 1,
        radio3: 1,
      },
      rules: {
        radio: [{ required: true, message: '请选择', trigger: 'change' }],
        radio2: [{ required: true, message: '请选择', trigger: 'change' }],
        radio3: [{ required: true, message: '请选择', trigger: 'change' }],
      },
      title: '',
      dialogFormVisible: false,
      tableData: [
        {
          grade: '一般告警',
          date: '2016-05-03 12:12:00',
          state: '已恢复',
          con: '设备故障报警',
        },
        {
          grade: '一般告警',
          date: '2016-05-03 12:12:00',
          state: '已恢复',
          con: '设备故障报警',
        },
        {
          grade: '一般告警',
          date: '2016-05-03 12:12:00',
          state: '已恢复',
          con: '设备故障报警',
        },
        {
          grade: '一般告警',
          date: '2016-05-03 12:12:00',
          state: '已恢复',
          con: '设备故障报警',
        },
        {
          grade: '一般告警',
          date: '2016-05-03 12:12:00',
          state: '已恢复',
          con: '设备故障报警',
        },
        {
          grade: '一般告警',
          date: '2016-05-03 12:12:00',
          state: '已恢复',
          con: '设备故障报警',
        },
      ],
    }
  },
  created() {},
  methods: {
    showEdit(row) {
      if (!row) {
        this.title = '添加'
      } else {
        this.title = '编辑'
        this.form = Object.assign({}, row)
      }
      this.dialogFormVisible = true
    },
    handleClick(tab, event) {
      console.log(tab, event)
    },
    close() {
      // this.$refs['ruleForm'].resetFields()
      // this.form = this.$options.data().form
      this.dialogFormVisible = false
    },
    handleClose(done) {
      if (this.loading) {
        return
      }
      // this.loading = true;
      this.timer = setTimeout(() => {
        done();
        // 动画关闭需要一定的时间
        // setTimeout(() => {
        //   this.loading = false
        // }, 400)
      }, 500)

      // this.$confirm('确定要提交表单吗？')
      //   .then((_) => {
      //     this.loading = true
      //     this.timer = setTimeout(() => {
      //       done()
      //       // 动画关闭需要一定的时间
      //       setTimeout(() => {
      //         this.loading = false
      //       }, 400)
      //     }, 2000)
      //   })
      //   .catch((_) => {})
    },
    save() {
      this.$refs[this.activeName].save()
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
// .el-form  {
//   margin-top: -23px !important;
// }

// .el-form-item  {
//   margin-bottom: 0px!important;
//   position: relative;
//   top: 12px;
// }
</style>
