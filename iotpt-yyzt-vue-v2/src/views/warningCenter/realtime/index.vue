<template>
  <div class="roleManagement-container">
    <!-- <el-divider content-position="left">
      演示环境仅做基础功能展示，若想实现不同角色的真实菜单配置，需将settings.js路由加载模式改为all模式，由后端全面接管路由渲染与权限控制
    </el-divider> -->
    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-select v-model="queryForm.warnLevel" filterable placeholder="告警等级" class="w" clearable>
              <el-option v-for="item in warnLevelList" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item style="display: none">
            <el-input v-model.trim="queryForm.warnStatus" placeholder="status" clearable />
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryForm.areaId" filterable placeholder="告警区域" class="w" clearable>
              <el-option v-for="item in allBuildListList" :key="item.id" :label="item.areaName" :value="item.id">
              </el-option>
            </el-select>
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

      </el-table-column>
      <el-table-column show-overflow-tooltip prop="deviceName" label="告警对象" align="center" width="120"></el-table-column>
      <el-table-column show-overflow-tooltip prop="areaName" label="告警区域" align="center"
        minwidth="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="content" label="告警内容" align="center"
        minwidth="160"></el-table-column>
      <el-table-column show-overflow-tooltip prop="warnTime" label="告警时间" align="center" width="180"></el-table-column>
      <el-table-column show-overflow-tooltip prop="warnStatus" label="告警状态" align="center" width="120">
        <template slot-scope="scope">
          <!-- 0：告警中 1.已处理-->
          <el-tag size="mini" type="warning" v-if="scope.row.warnStatus == 0">告警中</el-tag>
          <el-tag size="mini" type="warning" v-if="scope.row.warnStatus == 1">已处理</el-tag>
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip  prop="status" label="处理状态" align="center" width="120">
        <template slot-scope="scope">
          <el-tag size="mini" type="warning" v-if="scope.row.status == 1">待理中</el-tag>
          <el-tag size="mini" type="warning" v-if="scope.row.status == 2">处理中</el-tag>
          <el-tag size="mini" type="warning" v-if="scope.row.status == 3">已处理</el-tag>
          <el-tag size="mini" type="warning" v-if="scope.row.status == 4">自动恢复</el-tag>
          <el-tag size="mini" type="warning" v-if="scope.row.status == 5">忽略</el-tag>
        </template>
      </el-table-column>

      <el-table-column show-overflow-tooltip prop="warnSource" label="告警来源" align="center" width="120"></el-table-column>

      <el-table-column show-overflow-tooltip label="操作" width="130" align="center">
        <template #default="{ row }">
          <!-- <el-link type="success" :underline="false" @click="handleEdit(row)">告警处理</el-link> -->

           <el-button icon="el-icon-edit"  type="success" size="mini" @click="handleEdit(row)">告警处理</el-button>

        </template>
      </el-table-column>
    </el-table>
    <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
      :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
  </div>
</template>

<script>
import { getList, doDelete } from '@/api/roleManagement'
import Edit from './components/edit'
import {getRedisDictList} from "@/api/sysDict";
import {getPageList, getRealtimePageList} from "@/api/warnInfo";
import {getAllList} from "@/api/area";

export default {
  name: 'dictionary',
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
        pageSize: 10,
        warnLevel: '',
        areaId: ''
      },
      warnLevelList: [],
      allBuildListList: [],
      options: [],
    }
  },
  created() {
    this.fetchData()
    this.getWarnLevels();
    this.getAllArea();
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
    changeHidden(row) {
      if (row.hidden == 1) {
        row.hidden = 0
      } else {
        row.hidden = 1
      }
      this.$forceUpdate()
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
      const {data} = await getRealtimePageList(this.queryForm);
      this.list = data.records;
      this.total =data.total;

      setTimeout(() => {
        this.listLoading = false
      }, 300)
    },

    async getWarnLevels() {
      const {data} = await getRedisDictList({dictType: "warnType"});
      this.warnLevelList=data;
    },
    async getAllArea() {
      const {data} = await getAllList({});
      this.allBuildListList=data;
    },

    clearData(){
      this.value1=null;
      this.queryForm.pageNo=1;
      this.queryForm.pageSize=10;
      this.queryForm.warnLevel="";
      this.queryForm.areaId="";
      this.queryData();
    },
  },
}
</script>
