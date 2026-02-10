<template>
  <div class="roleManagement-container">
    <!-- <el-divider content-position="left">
      演示环境仅做基础功能展示，若想实现不同角色的真实菜单配置，需将settings.js路由加载模式改为all模式，由后端全面接管路由渲染与权限控制
    </el-divider> -->
    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-select v-model="queryForm.warnLevel" @keyup.enter.native="queryData"  clearable filterable placeholder="告警等级" class="w">
              <el-option v-for="item in warnLevelList" :key="item.dictValue" :label="item.dictLabel" :value="item.dictLabel">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryForm.areaId" @keyup.enter.native="queryData" clearable filterable placeholder="告警区域" class="w">
              <el-option v-for="item in areaList" :key="item.id" :label="item.areaName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-date-picker
            v-model="value1"
            @change='printValue1'
            clearable
            type="daterange"
            value-format="yyyy-MM-dd"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期">
          </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="primary" @click="queryData">
              查询
            </el-button>
            <el-button icon="el-icon-refresh" @click="clearData">
              重置
            </el-button>
          </el-form-item>
        </el-form>

      </vab-query-form-left-panel>

    </vab-query-form>

    <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText" height="calc(100vh - 240px)"
      border>
      <el-table-column prop="index" label="序号" width="80" align="center">
        <template #default="{ row, $index }">
          <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip label="告警等级" prop="warnLevelName" align="center" width="120">
<!--        <template slot-scope="scope">-->
<!--          &lt;!&ndash; <span style="color: #67C23A;">一般告警</span> &ndash;&gt;-->
<!--          &lt;!&ndash; <span style="color: #E6A23C;">紧急告警</span> &ndash;&gt;-->
<!--          <span style="color: #F56C6C;">严重告警</span>-->
<!--        </template>-->
      </el-table-column>
      <el-table-column show-overflow-tooltip prop="deviceName" label="告警对象" align="center" width="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="areaName" label="告警区域" align="center"
        minwidth="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="content" label="告警内容" align="center"
        minwidth="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="warnTime" label="告警开始时间" align="center" width="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="warnTime" label="告警结束时间" align="center" width="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="statusName" label="告警状态" align="center" width="120">
        <template slot-scope="{row}">
          <!-- <el-tag size="mini">已恢复</el-tag> -->
          <el-tag size="mini" type="danger" v-if="row.warnStatus==0">告警中</el-tag>
          <el-tag size="mini" type="success" v-if="row.warnStatus==1">已处理</el-tag>
<!--          <el-tag size="mini" type="danger" >{{}}</el-tag>-->
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip prop="statusName" label="处理状态" align="center" width="120">
<!--        <template slot-scope="scope">-->
<!--          &lt;!&ndash; <el-tag size="mini" type="success">处理中</el-tag> &ndash;&gt;-->
<!--          &lt;!&ndash; <el-tag size="mini" type="info">已处理</el-tag> &ndash;&gt;-->
<!--          <el-tag size="mini" type="warning">待响应</el-tag>-->
<!--        </template>-->
      </el-table-column>

      <el-table-column show-overflow-tooltip prop="warnSource" label="告警来源" align="center" width="120"></el-table-column>

      <el-table-column show-overflow-tooltip label="操作" width="130" align="center">
        <template #default="{ row }">
          <!-- <el-link type="text" :underline="false" @click="handleEdit(row)">查看</el-link> -->
          <el-button icon="el-icon-info" type="primary" size="mini"  @click="handleEdit(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
      :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
  </div>
</template>

<script>
import { getPageList } from '@/api/warnInfo'
import Edit from './components/edit'
import {getRedisDictList} from '@/api/sysDict'
import {getBuildAreaList} from "@/api/sysBuildArea";

export default {
  name: 'dictionary',
  components: { Edit },
  data() {
    return {
      list: null,
      warnLevelList:null,
      areaList:null,
      listLoading: true,
      layout: 'total, sizes, prev, pager, next, jumper',
      total: 0,
      selectRows: '',
      elementLoadingText: '正在加载...',
      value1: '',
      queryForm: {
        pageNo: 1,
        pageSize: 10,
        status: '3',
        startTime: '',
        endTime: '',
        warnLevel: '',
        areaId:''
      },
      options: [{
        value: '选项1',
        label: '选项1'
      }, {
        value: '选项2',
        label: '选项2'
      }, {
        value: '选项3',
        label: '选项3'
      }, {
        value: '选项4',
        label: '选项4'
      }, {
        value: '选项5',
        label: '选项5'
      }],
    }
  },
  created() {
    this.getWarnLevels();
    // 区域
    this.getBuildAreaList();
    this.fetchData()
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
    clearData(){

      this.value1=null;
      this.queryForm.pageNo=1;
      this.queryForm.pageSize=10;
      this.queryForm.startTime="";
      this.queryForm.areaId="";
      this.queryForm.endTime="";
      this.queryForm.warnLevel="";
      this.queryData();
    },
    changeHidden(row) {
      if (row.hidden == 1) {
        row.hidden = 0
      } else {
        row.hidden = 1
      }
      this.$forceUpdate()
    },
    async getBuildAreaList(){
      const {data} = await getBuildAreaList()
      this.areaList=data;
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
    handleSizeChange(val) {
      this.queryForm.pageSize = val
      this.fetchData()
    },
    printValue1() {
      if (null != this.value1) {
        this.queryForm.startTime = this.value1[0];
        this.queryForm.endTime = this.value1[1];
      } else {
        this.queryForm.startTime = "";
        this.queryForm.endTime = "";
      }
    },
    handleCurrentChange(val) {
      this.queryForm.pageNo = val
      this.fetchData()
    },
    queryData() {
      this.queryForm.pageNo = 1
      this.fetchData()
    },
    async getWarnLevels() {

      const {data} = await getRedisDictList({dictType: "warnType"});
      console.log(data);
      this.warnLevelList=data;

    },
    async fetchData() {
      this.listLoading = true
      const {data} = await getPageList(this.queryForm);
      console.log(data);
      this.list = data.records;
      this.total =data.total;
      setTimeout(() => {
        this.listLoading = false
      }, 300)
    },
  },
}
</script>
