<template>
 <el-dialog :close-on-click-modal="false" :visible.sync="dialogFormVisible" append-to-body width="1300px" @close="close" title="设备列表">
    <div class="index-container">

      <vab-query-form>
        <vab-query-form-left-panel :span="20">
          <el-form :inline="true" :model="queryForm" @submit.native.prevent>
            <el-form-item prop="name">
              <el-input v-model.trim="queryForm.name" placeholder="请输入设备名称" clearable
                        @keyup.enter.native="queryData"/>
            </el-form-item>
            <el-form-item prop="deviceType">
              <el-select v-model="queryForm.deviceType" placeholder="请选择设备类型" clearable>
                <el-option
                  v-for="item in this.deviceTypeList"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                ></el-option>
              </el-select>
            </el-form-item>

            <el-form-item prop="subitem">
              <el-select v-model="queryForm.subitem" placeholder="请选择设备所属分项" clearable>
                <el-option
                  v-for="item in this.subitemList"
                  :key="item.id"
                  :label="item.subitemName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item prop="areaId">
              <el-select v-model="queryForm.areaId" placeholder="请选择设备所属区域" clearable>
                <el-option
                  v-for="item in this.areaList"
                  :key="item.id"
                  :label="item.areaName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" type="primary" @click="queryData">
                查询
              </el-button>
              <!-- <el-button icon="el-icon-refresh">
              重置
            </el-button> -->
            </el-form-item>
          </el-form>

        </vab-query-form-left-panel>
        <!-- <vab-query-form-right-panel :span="4">
          <el-button icon="el-icon-plus" type="success" @click="handleEdit">关联</el-button>
        </vab-query-form-right-panel> -->
      </vab-query-form>

      <el-table v-loading="listLoading" :data="list"  :element-loading-text="elementLoadingText" @selection-change="setSelectRows" height="600" border>
        <el-table-column show-overflow-tooltip type="selection" width="60" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="name" label="设备名称" width="120" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="deviceTypeName" label="设备类型" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="deviceCode" label="设备编码" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="floorName" label="所属楼层/区域" align="center"></el-table-column>
        <el-table-column show-overflow-tooltip prop="areaName" label="所属区域" align="center"></el-table-column>
      </el-table>
      <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>

    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getPageList,bindDevice } from '@/api/deviceCollect'
import {getRedisDictList} from "@/api/sysDict";
import {getBuildAreaList} from "@/api/sysBuildArea";
import {getDeviceSubitemList} from "@/api/configDeviceSubitem";

export default {
  name: 'UserManagement',
  data() {
    return {
      dialogFormVisible: false,
      searchShow: false,
      list: null,
      listLoading: true,
      layout: 'total, sizes, prev, pager, next, jumper',
      total: 0,
      selectRows: '',
      disabled:false,
      loading:false,
      deviceTypeList:null,
      statusList:null,
      areaList:null,

      subitemList:null,
      elementLoadingText: '正在加载...',
      form:null,
      queryForm: {
        pageNo: 1,
        pageSize: 10,
        id:'',
        isGateway:'0',
        deviceType:'',
        name:'',
        subitem:'',
        areaId:'',
        checkIds:"",
      },
    }
  },
  created() {
    this.deviceTypeListData();
    this.statusListData();
    this.areaListData();
    this.subitemListData();
  },
  methods: {
    async deviceTypeListData() {
      const {data} = await getRedisDictList({dictType: "deviceType"});
      this.deviceTypeList = data;
    },
    async statusListData() {
      const {data} = await getRedisDictList({dictType: "deviceStatus"});
      this.statusList = data;
    },
    async areaListData() {
      const {data} = await getBuildAreaList();
      this.areaList = data;
    },
    async subitemListData() {
      const {data} = await getDeviceSubitemList();
      this.subitemList = data;
    },
    showEdit(row) {
      this.form = Object.assign({}, row)
      this.queryForm={
        pageNo: 1,
          pageSize: 10,
          gatewayId:'',
          isGateway:'0',
          deviceType:'',
          name:'',
          subitem:'',
          checkIds: '',
          areaId:''
      };

      this.fetchData()
      this.queryForm.gatewayId = this.form.id;
      this.dialogFormVisible = true
    },
    close() {
      this.dialogFormVisible = false
    },
    ShowSearch() {
      this.searchShow = !this.searchShow
    },
    refresh() {
      location.reload()
    },
    async save(){
      var checkIds="";
      this.selectRows.map(e => {
        checkIds=checkIds+e.id+",";
      });
      this.queryForm.checkIds=checkIds;

      const { data } = await bindDevice(this.queryForm)


      this.dialogFormVisible = false

      this.$parent.fetchData();

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
      const { data } = await getPageList(this.queryForm)
      this.list = data.records;
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
    handleEdit() {}
  },
}
</script>
