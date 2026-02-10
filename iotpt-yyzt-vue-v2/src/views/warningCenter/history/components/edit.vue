<template>
  <el-drawer :visible.sync="dialogFormVisible" size="1200px" @close="close">
    <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>{{ title }}</b></span>
    <div style="padding: 0 20px;">
      <div class="tips-title"><vab-icon :icon="['fas', 'bell']"></vab-icon>&nbsp;告警信息</div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="告警等级" content-class-name="my-content">{{form.warnLevelName}}</el-descriptions-item>
        <el-descriptions-item label="告警设备" content-class-name="my-content">{{form.deviceName}}</el-descriptions-item>
        <el-descriptions-item label="告警区域" content-class-name="my-content">{{form.areaName}}</el-descriptions-item>
        <el-descriptions-item label="告警来源" content-class-name="my-content">{{form.warnSource}}</el-descriptions-item>
        <el-descriptions-item label="告警开始时间" content-class-name="my-content">{{form.warnTime}}</el-descriptions-item>
        <el-descriptions-item label="告警结束时间" content-class-name="my-content">{{form.warnTime}}</el-descriptions-item>
        <el-descriptions-item label="告警状态" content-class-name="my-content">{{form.warnStatus==0?'告警中':'已处理'}}</el-descriptions-item>
        <el-descriptions-item label="告警内容" content-class-name="my-content">{{form.content}}</el-descriptions-item>
      </el-descriptions>
      <div class="tips-title mt2"><vab-icon :icon="['fas', 'clock']"></vab-icon>&nbsp;告警记录</div>
      <el-table v-loading="loading" :data="list" :element-loading-text="elementLoadingText" height="calc(100vh - 540px)"
      border>
      <el-table-column show-overflow-tooltip label="告警等级" prop="warnLevelName" align="center" width="120">
<!--        <template slot-scope="scope">-->
<!--          &lt;!&ndash; <span style="color: #67C23A;">一般告警</span> &ndash;&gt;-->
<!--          &lt;!&ndash; <span style="color: #E6A23C;">紧急告警</span> &ndash;&gt;-->
<!--          <span style="color: #F56C6C;">严重告警</span>-->
<!--        </template>-->
      </el-table-column>
      <el-table-column show-overflow-tooltip prop="deviceName" label="告警设备" align="center" width="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="areaName" label="告警区域" align="center"
        minwidth="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="content" label="告警内容" align="center"
        minwidth="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="warnTime" label="告警时间" align="center" width="160"></el-table-column>
<!--      <el-table-column show-overflow-tooltip prop="permission" label="告警结束时间" align="center" width="160"></el-table-column>-->
      <el-table-column show-overflow-tooltip prop="warnSource" label="告警来源" align="center" width="120"></el-table-column>
    </el-table>
    </div>
    <div class="footer-btn">
      <el-button @click="close">取消</el-button>
    </div>
  </el-drawer>
</template>

<script>
import { getList } from '@/api/warnInfoDetail'
import bumen from '@/components/bumen'
import {getPageList} from "@/api/warnInfo";

export default {
  name: 'UserManagementEdit',
  components: { bumen },
  data() {
    return {
      loading: false,
      disabled: false,
      title: '',
      queryForm:{
        id:''
      },
      form:null,
      dialogFormVisible: false,
      list:null
    }
  },
  created() { },
  methods: {
    showEdit(row) {
      this.title = '查看详情'
      this.form = Object.assign({}, row)

      this.queryForm.id = this.form.id;
      this.queryData();
      this.dialogFormVisible = true
    },
    close() {
      this.dialogFormVisible = false
    },
    async queryData(){
      this.loading=true;

      const {data} = await getList(this.queryForm);
      console.log(data);
      this.list = data;
      // this.total =data.total;
      setTimeout(() => {
        this.loading = false
      }, 300)

    },
    elementLoadingText(){

    },
    openwin() {
      var that = this
      that.$refs['bumen'].showDia()
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

  .tips-title {
    font-size: 14px;
    margin-bottom: 8px;
    font-weight: bold;
    color: #334c97;

    i {
      margin-right: 4px;
    }
  }
  .area-box{
    display: flex;
    justify-content: center;
    span{
      margin: 0 8px;
    }
  }
  .config-type-con{
    margin-bottom: 14px;
    background-color: #fafcff;
    padding-top: 8px;
    border: 1px solid #efefef;
    position: relative;
    .el-form-item--small.el-form-item{
      margin-bottom: 6px;
    }
    p{
      position: absolute;
      right:0;
      height: 26px;
      line-height: 26px;
      text-align: center;
      display: inline-block;
      padding: 0 20px;
      margin: 0 0 6px;
      background-color: #5470c6;
      border-top-left-radius: 22px;
      border-bottom-left-radius: 22px;
      color: #fff;
      font-size: 12px;
    }
    .config-item{
      padding: 10px 0;
      background-color: #fbf2de;
      .el-form-item--small.el-form-item{
        margin-bottom: 0;
      }
    }
    .form-edit-btn{
      margin-top: 6px;
      margin-bottom: 12px !important;
    }
  }
  .footer-btn{
    padding: 12px 20px;
    border-top: 1px solid #efefef;
    position: absolute;
    bottom: 0;
    text-align: right;
    width: 100%;
  }
  .my-content {
    width: 460px;
  }
  .el-descriptions-item__label{
    text-align: right !important;
  }

}
.mt2{
  margin-top: 20px;
}
</style>
