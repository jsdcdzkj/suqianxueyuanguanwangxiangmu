<template>
  <el-drawer :visible.sync="dialogFormVisible" title="信号信息" size="1000px" @close="close" :before-close="handleClose">
    <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>信号信息</b></span>
    <div style="padding: 0 20px">
      <div class="title">
        <div class="tips-title">
          <vab-icon :icon="['fas', 'hdd']"></vab-icon>&nbsp;设备名称：{{ form.name }}
        </div>
        <div class="tips-title">
          <vab-icon :icon="['fas', 'map-marked-alt']"></vab-icon>&nbsp;
          物理位置：{{ form.areaNames }}
        </div>
      </div>
      <el-table ref="multipleTable" :data="tableData" v-loading="listLoading" :element-loading-text="elementLoadingText"
                height="355px" border stripe @selection-change="setSelectRows">
        <el-table-column show-overflow-tooltip type="selection" align="center">
        </el-table-column>
        <el-table-column prop="signalTypeName" label="信号名称" show-overflow-tooltip align="center">
          <!--<template slot-scope="scope">{{ scope.row.signalTypeName }}</template>-->
        </el-table-column>
        <el-table-column prop="signalTypeCode" label="信号编码" show-overflow-tooltip align="center">
        </el-table-column>
        <!--<el-table-column prop="address" label="单位" show-overflow-tooltip align="center">-->
        <!--</el-table-column>-->
      </el-table>
      <!--<el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"-->
      <!--:total="total" @size-change="handleSizeChange"-->
      <!--@current-change="handleCurrentChange"></el-pagination>-->

      <div class="title" style="margin-top: 36px;">
        <div class="tips-title">
          <vab-icon :icon="['fas', 'history']"></vab-icon>&nbsp;历史记录
        </div>
      </div>
      <vab-query-form>
        <vab-query-form-left-panel :span="16">
          <el-form :inline="true" :model="queryForm2" ref="queryForm2" @submit.native.prevent>
            <el-form-item label="采集时间起" prop="startTime">
              <el-date-picker type="date" placeholder="采集时间起" value-format="yyyy-MM-dd"
                              v-model="queryForm2.startTime"></el-date-picker>
              <!--<el-input v-model.trim="queryForm2.startTime" type="date" placeholder="采集时间起"-->
              <!--autocomplete="off"></el-input>-->
            </el-form-item>
            <el-form-item label="采集时间止" prop="endTime">
              <el-date-picker type="date" placeholder="采集时间止" value-format="yyyy-MM-dd"
                              v-model="queryForm2.endTime"></el-date-picker>
              <!--<el-input v-model.trim="queryForm2.endTime" type="date" placeholder="采集时间止" autocomplete="off"></el-input>-->
            </el-form-item>
          </el-form>
        </vab-query-form-left-panel>
        <vab-query-form-right-panel :span="8">
          <el-button icon="el-icon-search" type="primary" @click="queryData">
            查询
          </el-button>
          <!--<el-button icon="el-icon-refresh" @click="resetQuery">-->
          <!--重置-->
          <!--</el-button>-->
        </vab-query-form-right-panel>
      </vab-query-form>
      <el-table ref="multipleTable" :data="tableDataList" v-loading="loading" :element-loading-text="elementLoadingText"
                height="355px" border>
          <el-table-column prop="index" label="序号" width="80" align="center">
              <template #default="{ row, $index }">
                  <span>{{ ($index + 1) + (queryForm2.pageNo - 1) * queryForm2.pageSize }}</span>
              </template>
          </el-table-column>
        <el-table-column prop="signalTypeName" label="信号名称" show-overflow-tooltip align="center">
          <!--<template slot-scope="scope">{{ scope.row.signalTypeName }}</template>-->
        </el-table-column>
        <el-table-column prop="val" label="信号值" show-overflow-tooltip align="center">
        </el-table-column>
        <!--<el-table-column prop="address" label="单位" show-overflow-tooltip align="center">-->
        <!--</el-table-column>-->
        <el-table-column prop="dataTime" label="采集时间" show-overflow-tooltip align="center">
        </el-table-column>
      </el-table>
      <el-pagination background :current-page="queryForm2.pageNo" :page-size="queryForm2.pageSize" :layout="layout"
                     :total="total2" @size-change="handleSizeChange2"
                     @current-change="handleCurrentChange2"></el-pagination>
    </div>

  </el-drawer>
</template>

<script>
  import {getPageList, getDataSheetList, getLastOne} from '@/api/dataSheet'
  import {getListByDeviceTypeCode} from '@/api/configSignalType'

  export default {
    name: 'CjzdSignalView',
    components: {},
    data() {
      return {
        total: 0,
        total2: 0,
        queryForm: {
          pageNo: 1,
          pageSize: 10,
        },
        queryForm2: {
          pageNo: 1,
          pageSize: 10,
          startTime: '',
          endTime: '',
          deviceId: '',
          // gateWayId: '',
          codes: [],
        },
        layout: 'total, sizes, prev, pager, next, jumper',
        listLoading: false,
        loading: false,
        elementLoadingText: '正在加载...',
        disabled: false,
        title: '',
        dialogFormVisible: false,
        form: {
          id: '',
        },
        tableData: [],
        tableDataList: [],
        multipleSelection: [],
        selectRows: [],
        ids: [],// 选中数组
      }
    },
    mounted() {
      this.queryForm2.startTime = this.getCurrentDate();
      this.queryForm2.endTime = this.getCurrentDate();
    },
    created() {
    },
    methods: {
      showEdit(row) {
        this.form = Object.assign({}, row);
        this.getData(row);

        this.queryForm2.deviceId = row.id;
        // this.queryForm2.gateWayId = row.gatewayId;
        //历史记录
        // this.getDataList(row);
        this.dialogFormVisible = true
      },
      getCurrentDate() {
        let now = new Date();
        let year = now.getFullYear();
        let month = (now.getMonth() + 1) < 10 ? '0' + (now.getMonth() + 1) : (now.getMonth() + 1)
        let day = new Date().getDate() < 10 ? '0' + now.getDate() : now.getDate()
        return year + "-" + month + "-" + day;
      },
      setSelectRows(selection) {
        console.log("selection")
        console.log(selection)
        this.selectRows = selection;
        console.log(this.selectRows)
        this.ids = selection.map(item => item.signalTypeCode);
        console.log("ids")
        console.log(this.ids)
        // this.multiple = !selection.length;
      },
      close() {
        // this.$refs['ruleForm'].resetFields()
        // this.form = this.$options.data().form

        this.queryForm2.startTime = this.getCurrentDate();
        this.queryForm2.endTime = this.getCurrentDate();
        this.tableDataList = [];

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
        //   .catch((_) => {
        //   })
      },

      /** 重置按钮操作 */
      resetQuery() {
        this.$refs.queryForm2.resetFields();
        this.queryData();
      },
      queryData() {
        if (this.queryForm2.startTime == "" || this.queryForm2.startTime == null || this.queryForm2.startTime == undefined) {
          this.$baseMessage("请选择采集时间起", 'error');
          return
        }
        if (this.queryForm2.endTime == "" || this.queryForm2.endTime == null || this.queryForm2.endTime == undefined) {
          this.$baseMessage("请选择采集时间止", 'error');
          return
        }
        if (this.selectRows.length == 0) {
          this.$baseMessage("请选择信号", 'error');
          return
        }
        this.queryForm2.pageNo = 1
        this.getDataList()
      },
      // async fetchData() {
      //   this.listLoading = true
      //   const {data, totalCount} = await getList(this.queryForm)
      //   this.list = data
      //   this.total = totalCount
      //   setTimeout(() => {
      //     this.listLoading = false
      //   }, 300)
      // },
      handleSizeChange(val) {
        this.queryForm.pageSize = val
        this.getDataList()
      },
      handleCurrentChange(val) {
        this.queryForm.pageNo = val
        this.getDataList()
      },
      handleSizeChange2(val) {
        this.queryForm2.pageSize = val
        this.getDataList()
      },
      handleCurrentChange2(val) {
        this.queryForm2.pageNo = val
        this.getDataList()
      },

      async getData(row) {
        const {data} = await getListByDeviceTypeCode({id: row.deviceType});
        this.tableData = data;
      },
      async getDataList() {
        this.loading = true
        this.queryForm2.codes = this.ids;
        const {data} = await getPageList(this.queryForm2);
        console.log("getDataList", data)
        this.tableDataList = data.records;
        this.total2 = data.total;
        this.loading = false
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
