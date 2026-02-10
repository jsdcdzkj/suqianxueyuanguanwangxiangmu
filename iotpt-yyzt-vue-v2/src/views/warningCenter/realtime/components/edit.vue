<template>
  <el-drawer :visible.sync="dialogFormVisible" size="1200px" @close="close" :before-close="handleClose">
    <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>实时告警</b></span>
    <div style="padding: 0 20px;">
      <el-tabs>
        <el-tab-pane label="告警处理">
          <el-card class="box-card" shadow="never" style="background-color: #fbfbfb;">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="tips-title"><vab-icon :icon="['fas', 'bell']"></vab-icon>&nbsp;告警信息</div>
                <el-form :model="form" label-width="90px" size="mini">
                  <el-form-item label="告警等级">
                    <el-input v-model="form.warnLevelName" :readonly="true"></el-input>
                  </el-form-item>
                  <el-form-item label="告警对象">
                    <el-input v-model="form.deviceName" :readonly="true"></el-input>
                  </el-form-item>
                  <el-form-item label="告警位置">
                    <el-input v-model="sizeForm.name" :readonly="true"></el-input>
                  </el-form-item>
                  <el-form-item label="告警来源">
                    <el-input v-model="form.warnSource" :readonly="true"></el-input>
                  </el-form-item>
                  <el-form-item label="告警时间">
                    <el-input v-model="form.warnTime" :readonly="true"></el-input>
                  </el-form-item>
                  <el-form-item label="告警状态">
                    <el-input v-model="form.statusName" :readonly="true"></el-input>
                  </el-form-item>
                  <el-form-item label="告警内容">
                    <el-input v-model="form.content" type="textarea" :rows="2" :readonly="true"></el-input>
                  </el-form-item>
                </el-form>
              </el-col>
              <el-col :span="12">
                <div class="tips-title"><vab-icon :icon="['fas', 'clock']"></vab-icon>&nbsp;告警记录</div>
                <el-table v-loading="listLoading" :data="tableData" :element-loading-text="elementLoadingText" height="288"
                  border>
                  <el-table-column label="告警等级" prop="warnLevelName" align="center" width="120">
                    <template slot-scope="scope">
                    <!-- <span style="color: #67C23A;">一般告警</span> -->
                    <!-- <span style="color: #E6A23C;">紧急告警</span> -->
                    <span style="color: #F56C6C;">严重告警</span>
                  </template>
                  </el-table-column>
                  <el-table-column show-overflow-tooltip  prop="content" label="告警内容" align="center" minwidth="180">
                  </el-table-column>
                  <el-table-column show-overflow-tooltip prop="warnTime" align="center" width="160" label="告警时间">
                  </el-table-column>
                  <el-table-column prop="warnStatus" align="center" width="80" label="状态">
                  </el-table-column>
                </el-table>
              </el-col>
            </el-row>
          </el-card>
          <el-card class="box-card" shadow="never">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="tips-title"><vab-icon :icon="['fas', 'clipboard-list']"></vab-icon>&nbsp;处理</div>
                <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="90px" class="demo-ruleForm">
                  <el-form-item label="状态" prop="radio">
                    <el-radio-group v-model="ruleForm.radio">
                      <el-radio :label="0">待响应</el-radio>
                      <el-radio :label="1">处理中</el-radio>
                      <el-radio :label="2">已处理</el-radio>
                      <el-radio :label="3">自动恢复</el-radio>
                      <el-radio :label="4">处理中</el-radio>
                      <el-radio :label="5">忽略</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item v-if="ruleForm.radio == 5 " label="忽略类型" prop="radio2">
                    <el-radio-group v-model="ruleForm.radio2">
                      <el-radio :label="0">当前信号</el-radio>
                      <el-radio :label="1">当前设备</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item v-if="ruleForm.radio == 5 " label="状态" prop="radio3">
                    <el-radio-group v-model="ruleForm.radio3">
                      <el-radio :label="0">30分钟</el-radio>
                      <el-radio :label="1">12小时</el-radio>
                      <el-radio :label="2">24小时</el-radio>
                      <el-radio :label="3">7天</el-radio>
                      <el-radio :label="4">1月</el-radio>
                      <el-radio :label="5">自定义</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item v-if="ruleForm.radio3 == 5 " label="自定义时间">
                    <el-date-picker
                    v-model="ruleForm.time"
                    type="datetimerange"
                    range-separator="至"
                    style="width: 100%"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期">
                  </el-date-picker>
                  </el-form-item>
                  <el-form-item label="说明">
                    <el-input v-model.trim="ruleForm.desc" type="textarea" placeholder="请输入"
                      autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">立即提交</el-button>
                    <el-button @click="close">取消</el-button>
                  </el-form-item>
                </el-form>

              </el-col>
              <el-col :span="12">
                <div class="tips-title"><vab-icon :icon="['fas', 'calendar-check']"></vab-icon>&nbsp;处理记录</div>
                <div class="timeline-con">
                  <el-timeline>
                    <el-timeline-item  color="#0bbd87" v-for="(item, index) in warningDisposeList" :key="index">
                      <div class="time-con">
                        <div class="time-top">
                          <span class="time-name"><i class="el-icon-user-solid"></i>王文文<b>2023-04-23 10:29:56</b></span>
                          <!-- 共有5中状态 -->
                          <!-- <el-tag type="danger" plain size="mini">待响应</el-tag>
                        <el-tag plain size="mini">处理中</el-tag>
                        <el-tag type="success" plain size="mini">已处理</el-tag>
                        <el-tag type="warning" plain size="mini">自动恢复</el-tag> -->
                          <el-tag type="info" plain size="mini">忽略</el-tag>
                        </div>
                        <div class="time-bot">
                          <i class="el-icon-s-comment"></i>
                          <p>已派专业人员处理！</p>
                        </div>
                      </div>
                    </el-timeline-item>

                  </el-timeline>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>
        <el-tab-pane label="设备详情">
          <el-card class="box-card" shadow="never">
            <el-row :gutter="20">
              <el-col :span="16">
                <div class="tips-title"><vab-icon :icon="['fas', 'hdd']"></vab-icon>&nbsp;基本信息</div>
                <el-descriptions :column="2" border>
                <el-descriptions-item label="设备名称" content-class-name="my-content">{{ baseData.name }}</el-descriptions-item>
                <el-descriptions-item label="设备类型" content-class-name="my-content">{{ baseData.deviceTypeName }}</el-descriptions-item>
                <el-descriptions-item label="设备编码" content-class-name="my-content">{{ baseData.deviceCode }}</el-descriptions-item>
                <el-descriptions-item label="设备状态" content-class-name="my-content">{{ baseData.status }}</el-descriptions-item>
                <el-descriptions-item label="所属分项" content-class-name="my-content">{{ baseData.subitem}}</el-descriptions-item>
                <el-descriptions-item label="所属网关" content-class-name="my-content">{{ baseData.gatewayName }}</el-descriptions-item>
                <el-descriptions-item label="所属楼宇" content-class-name="my-content">{{ baseData.buildName }}</el-descriptions-item>
                <el-descriptions-item label="所属楼层" content-class-name="my-content">{{ baseData.floorName }}</el-descriptions-item>
                <el-descriptions-item label="所属区域" content-class-name="my-content">{{ baseData.areaName  }}</el-descriptions-item>
                <el-descriptions-item label="安装位置" content-class-name="my-content">{{ baseData.place }}</el-descriptions-item>
                <el-descriptions-item label="安装日期" content-class-name="my-content">{{ baseData.installationDate }}</el-descriptions-item>
                <el-descriptions-item label="使用日期" content-class-name="my-content">{{ baseData.useTime }}</el-descriptions-item>
                <el-descriptions-item label="设备管理员" content-class-name="my-content">{{ baseData.userName }}</el-descriptions-item>
                <el-descriptions-item label="管理员电话" content-class-name="my-content">{{ baseData.phone }}</el-descriptions-item>
                <el-descriptions-item label="供应商名称" content-class-name="my-content">{{ baseData.supplierName }}</el-descriptions-item>
                <el-descriptions-item label="设备型号" content-class-name="my-content">{{ baseData.modelName }}</el-descriptions-item>
                <el-descriptions-item label="供应商联系人" content-class-name="my-content"></el-descriptions-item>
                <el-descriptions-item label="供应商电话" content-class-name="my-content"></el-descriptions-item>
                <el-descriptions-item label="维保公司" content-class-name="my-content">{{ baseData.companyName }}</el-descriptions-item>
                <el-descriptions-item label="维保联系人" content-class-name="my-content"></el-descriptions-item>
                <el-descriptions-item label="维保电话" content-class-name="my-content">{{ baseData.phone }}</el-descriptions-item>
                <el-descriptions-item label="年检日期" content-class-name="my-content">{{ baseData.inspectionDate }}</el-descriptions-item>
                <el-descriptions-item label="采购日期" content-class-name="my-content">{{ baseData.procureDate }}</el-descriptions-item>
                <el-descriptions-item label="过保日期" content-class-name="my-content">{{ baseData.expirationDate }}</el-descriptions-item>
                <el-descriptions-item label="设备描述">{{ baseData.deviceDesc }}</el-descriptions-item>
              </el-descriptions>

              </el-col>
              <el-col :span="8">
                <div class="tips-title"><vab-icon :icon="['fas', 'tachometer-alt']"></vab-icon>&nbsp;实时信息</div>
                <el-table :data="tableData2" border style="width: 100%">
                  <el-table-column prop="name" label="信号名称" align="center" width="100">
                  </el-table-column>
                  <el-table-column prop="number" label="信号值" align="center" minwidth="180">
                  </el-table-column>
                  <el-table-column prop="unit" align="center" width="100" label="单位">
                  </el-table-column>
                </el-table>
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </div>

  </el-drawer>
</template>

<script>
import { doEdit } from '@/api/userManagement'
import bumen from '@/components/bumen'
import {getRedisDictList} from "@/api/sysDict";
import {getEntity} from "@/api/deviceCollect";
import {getPageList as getWarnConfig} from "@/api/warnConfig";
import {getPageList} from "@/api/warnInfo";
import {getList as getWarnDisposeList } from "@/api/warnDispose";

export default {
  name: 'UserManagementEdit',
  components: { bumen },
  data() {
    return {
      loading: false,
      disabled: false,
      sizeForm: {
        name: '默认文字',
      },
      form:{},
      baseData:{},
      warningDisposeList:[],
      ruleForm: {
          desc: '',
          time: '',
          radio: 1,
          radio2: 1,
          radio3: 1,
        },
        rules: {
          radio: [
            { required: true, message: '请选择', trigger: 'change' }
          ],
          radio2: [
            { required: true, message: '请选择', trigger: 'change' }
          ],
          radio3: [
            { required: true, message: '请选择', trigger: 'change' }
          ],
        },
      title: '',
      dialogFormVisible: false,
      tableData: [{
        grade: '一般告警',
        date: '2016-05-03 12:12:00',
        state: '已恢复',
        con: '设备故障报警'
      }, {
        grade: '一般告警',
        date: '2016-05-03 12:12:00',
        state: '已恢复',
        con: '设备故障报警'
      }, {
        grade: '一般告警',
        date: '2016-05-03 12:12:00',
        state: '已恢复',
        con: '设备故障报警'
      }, {
        grade: '一般告警',
        date: '2016-05-03 12:12:00',
        state: '已恢复',
        con: '设备故障报警'
      }, {
        grade: '一般告警',
        date: '2016-05-03 12:12:00',
        state: '已恢复',
        con: '设备故障报警'
      }, {
        grade: '一般告警',
        date: '2016-05-03 12:12:00',
        state: '已恢复',
        con: '设备故障报警'
      }],
      tableData2: [{
        name: '温度',
        number: '25',
        unit: '℃',
      }, {
        name: '温度',
        number: '25',
        unit: '℃',
      }, {
        name: '温度',
        number: '25',
        unit: '℃',
      }, {
        name: '温度',
        number: '25',
        unit: '℃',
      }, {
        name: '温度',
        number: '25',
        unit: '℃',
      }, {
        name: '温度',
        number: '25',
        unit: '℃',
      }]
    }
  },
  created() {

  },
  methods: {
    showEdit(row) {
      if (!row) {
        this.title = '添加'
      } else {
        this.title = '编辑'
        this.form = Object.assign({}, row)

        this.getBaseData(row); //设备类型
        this.getWarnRecordsData(row); //设备类型
        this.getWarnDisposeList(row); //设备类型
      }
      this.dialogFormVisible = true
    },
    close() {
      this.$refs['ruleForm'].resetFields()
      this.form = this.$options.data().form
      this.dialogFormVisible = false
    },
    handleClose(done) {
      this.$refs['ruleForm'].resetFields()
      this.form = this.$options.data().form
      this.dialogFormVisible = false
    },
    save() {
      if (this.loading) {
        return
      }
      this.$refs['ruleForm'].validate(async (valid) => {
        if (valid) {
          this.loading = true
          this.disabled = true
          setTimeout(() => {
            this.loading = false
            this.disabled = false
          }, 1000)

          const { msg } = await doEdit(this.form)
          this.$baseMessage(msg, 'success')
          this.$emit('fetch-data')
          this.close()
        } else {
          return false
        }
      })
    },
    openwin() {
      var that = this
      that.$refs['bumen'].showDia()
    },
    async getBaseData(row) {

      const {data} = await getEntity({id:row.deviceId})
      console.log(data)
      this.baseData = data;
    },
    async getWarnRecordsData(row) {
      const {data} = await getPageList({deviceId:row.deviceId});
      this.tableData = data.records;
    },
    async getWarnDisposeList(row) {
      const {data} = await getWarnDisposeList({warningInfoId:row.id});
      this.warningDisposeList = data;
    },
  },
}
</script>
<style scoped lang="scss">
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

  .el-input__inner[readonly='readonly'] {
    background-color: #f7f8fb;
  }

  .el-textarea__inner[readonly='readonly'] {
    background-color: #f7f8fb;
  }

  .el-form-item--mini.el-form-item {
    margin-bottom: 8px;
  }

  .tips-title {
    font-size: 14px;
    margin-bottom: 10px;
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
    background-color: #f6f8ff;

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

  // .el-tabs__header{
  //   margin-bottom: 0;
  // }
  // .el-tabs__content{
  //   border: 1px solid #e4e7ed;
  //   border-top: none;
  //   padding-top: 10px;
  //   padding-bottom: 10px;
  //   height: 360px;
  //   overflow-y: auto;
  // }
  // .el-tabs__item{
  //   height: 32px;
  //   line-height: 32px;
  //   position: relative;
  //   &.is-active{
  //     background-color: #d8dff4;
  //   }
  //   &.is-active::after{
  //     content: '';
  //     display: inline-block;
  //     width: 100%;
  //     height: 1px;
  //     background-color: #d8dff4;
  //     position: absolute;
  //     bottom: 0px;
  //     left: 0;
  //   }
  // }
  // .el-tabs--card > .el-tabs__header .el-tabs__item {
  //   border-bottom: aliceblue;
  // }
  .my-label {
    background: #E1F3D8;
  }
  .el-descriptions-item__label{
    text-align: right !important;
  }

  .my-content {
    width: 280px;
  }
}
</style>
