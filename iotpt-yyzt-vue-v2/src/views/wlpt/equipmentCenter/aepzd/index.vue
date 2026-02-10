<template>
  <div class="index-container">
    <vab-query-form>
      <vab-query-form-left-panel :span="19" class="left-panel-self">
        <el-form
          :inline="true"
          :model="queryForm"
          @submit.native.prevent
          ref="queryForm"
        >
          <!-- <vab-query-form-left-panel :span="20" class="left-panel-self">  -->
          <el-form-item prop="name">
            <el-input
              v-model.trim="queryForm.name"
              placeholder="请输入设备名称"
              clearable
              @keyup.enter.native="queryData"
            />
          </el-form-item>
          <el-form-item prop="deviceType">
            <el-select
              v-model="queryForm.deviceType"
              placeholder="请选择设备类型"
              clearable
            >
              <el-option
                v-for="item in this.deviceTypeList"
                :key="item.id"
                :label="item.deviceTypeName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <!-- <el-form-item prop="subitem">
                        <el-select v-model="queryForm.subitem" placeholder="请选择设备所属分项" clearable>
                            <el-option
                                v-for="item in this.subitemList"
                                :key="item.id"
                                :label="item.subitemName"
                                :value="item.id"
                            ></el-option>
                        </el-select>
                    </el-form-item> -->

          <el-form-item prop="areaIds">
            <el-cascader
              :props="{
                expandTrigger: 'hover',
                value: 'value',
                label: 'label',
                children: 'children',
                checkStrictly: true,
              }"
              placeholder="请选择物理位置"
              :options="areaList"
              style="width: 100%"
              :show-all-levels="false"
              collapse-tags
              ref="areaId"
              v-model="areaIds"
              @change="choseArea"
              filterable
              clearable
            ></el-cascader>
          </el-form-item>
          <!-- <el-form-item prop="logicalAreaIds">
                        <el-cascader
                            :props="{
                              expandTrigger: 'hover',
                              value: 'value',
                              label: 'label',
                              children: 'children',
                              checkStrictly: true,
                            }"
                            placeholder="请选择逻辑位置"
                            :options="logicalAreaList"
                            style="width: 100%;"
                            :show-all-levels="false"
                            collapse-tags
                            ref="logicalAreaId"
                            v-model="logicalAreaIds"
                            @change="choseLogicalArea"
                            popper-class="location"
                            filterable
                            clearable></el-cascader>
                    </el-form-item> -->
          <el-form-item prop="status">
            <el-select
              v-model="queryForm.status"
              placeholder="请选择设备状态"
              clearable
            >
              <el-option
                v-for="item in this.statusList"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="onLineStatus">
            <el-select
              v-model="queryForm.onLineStatus"
              placeholder="请选择在线状态"
              clearable
            >
              <el-option
                v-for="item in this.onlineDict"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>
          <!-- </vab-query-form-left-panel> -->
          <!-- <vab-query-form-right-panel :span="4"> -->
          <el-form-item>
            <el-button icon="el-icon-search" type="primary" @click="queryData">
              查询
            </el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">
              重置
            </el-button>
          </el-form-item>
          <!-- </vab-query-form-right-panel> -->
        </el-form>
      </vab-query-form-left-panel>
      <vab-query-form-right-panel :span="5">
        <el-button
          icon="el-icon-edit"
          plain
          type="warning"
          @click="handlePlEdit"
        >
          批量编辑
        </el-button>
        <el-button
          icon="el-icon-plus"
          plain
          type="success"
          @click="handleImport"
        >
          导入
        </el-button>
        <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">
          添加
        </el-button>
        <el-button
          icon="el-icon-download"
          plain
          @click="downXls"
          :loading="dowmLoading"
          :disabled="dowmLoading"
        >
          {{ dowmLoading ? '导出中' : '导出' }}
        </el-button>
        <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
                <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
      </vab-query-form-right-panel>
    </vab-query-form>

    <el-table
      v-loading="listLoading"
      :data="list"
      :element-loading-text="elementLoadingText"
      stripe
      @selection-change="setSelectRows"
      height="calc(100vh - 240px)"
      border
    >
      <el-table-column
        show-overflow-tooltip
        type="selection"
        width="60"
        align="center"
      ></el-table-column>
      <el-table-column prop="index" label="序号" width="80" align="center">
        <template #default="{ row, $index }">
          <span>
            {{ $index + 1 + (queryForm.pageNo - 1) * queryForm.pageSize }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="name"
        label="设备名称"
        width="150"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="deviceTypeName"
        label="类型"
        width="150"
        align="center"
      ></el-table-column>
      <!--            <el-table-column show-overflow-tooltip prop="subitemName" label="所属分项" width="150" align="center"></el-table-column>-->
      <el-table-column
        show-overflow-tooltip
        prop="areaNames"
        label="物理位置"
        align="center"
      ></el-table-column>
      <!--            <el-table-column show-overflow-tooltip prop="logicalAreaNames" label="逻辑位置" align="center">-->
      <!--            </el-table-column>-->
      <el-table-column label="状态" width="100" align="center" prop="status">
        <template slot-scope="{ row }">
          <el-link
            :underline="false"
            icon="el-icon-info"
            type="info"
            v-if="row.status == 1"
          >
            未使用
          </el-link>
          <el-link
            :underline="false"
            icon="el-icon-success"
            type="success"
            v-if="row.status == 2"
          >
            使用中
          </el-link>
          <el-link
            :underline="false"
            icon="el-icon-error"
            type="danger"
            v-if="row.status == 3"
          >
            损坏
          </el-link>
          <el-link
            :underline="false"
            icon="el-icon-warning"
            type="warning"
            v-if="row.status == 4"
          >
            维修中
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        label="在线状态"
        width="100"
        align="center"
        prop="onLineStatus"
      >
        <template slot-scope="{ row }">
          <el-link
            :underline="false"
            icon="el-icon-success"
            type="success"
            v-if="row.onLineStatus == 1"
          >
            正常
          </el-link>
          <el-link
            :underline="false"
            icon="el-icon-error"
            type="danger"
            v-if="row.onLineStatus == 2"
          >
            离线
          </el-link>
          <el-link
            :underline="false"
            icon="el-icon-warning"
            type="warning"
            v-if="row.onLineStatus == 3"
          >
            异常
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="350" align="center">
        <template #default="{ row }">
          <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
                    <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>
                    <el-link :underline="false" type="primary" @click="handleView(row)">详情</el-link>
                    <el-link :underline="false" type="primary" @click="handleSignal(row)">信号信息</el-link> -->

          <el-button
            icon="el-icon-edit"
            type="success"
            size="mini"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            icon="el-icon-delete"
            type="danger"
            size="mini"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
          <el-button
            icon="el-icon-info"
            type="primary"
            size="mini"
            @click="handleView(row)"
          >
            详情
          </el-button>
          <el-button
            icon="el-icon-view"
            type="primary"
            size="mini"
            @click="handleSignal(row)"
          >
            信号信息
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      :current-page="queryForm.pageNo"
      :page-size="queryForm.pageSize"
      :layout="layout"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    ></el-pagination>
    <edit ref="edit" @fetch-data="fetchData"></edit>
    <plEdit ref="plEdit" @fetch-data="fetchData"></plEdit>
    <cjzdView ref="cjzdView" @fetch-data="fetchData"></cjzdView>
    <cjzdSignalView
      ref="cjzdSignalView"
      @fetch-data="fetchData"
    ></cjzdSignalView>
    <uploadFile ref="uploadFile" @fetch-data="fetchData"></uploadFile>
  </div>
</template>

<script>
  // aep采集终端

  import {
    getPageList,
    saveOrUpdate,
    onDel,
    batchUpdate,
    toExportTemplate,
  } from '@/api/deviceAep'
  import { getRedisDictList } from '@/api/sysDict'
  import { getDeviceSubitemList } from '@/api/configDeviceSubitem'
  import { getBuildAreaList } from '@/api/sysBuildArea'
  import { getDeviceTypeList } from '@/api/configDeviceType'
  import { areaTreeList, areaTreeList2 } from '@/api/org'

  import Edit from './components/CjzdEdit'
  import PlEdit from './components/CjzdPlEdit'
  import CjzdView from './components/CjzdView'
  import CjzdSignalView from './components/CjzdSignalView'
  import uploadFile from './components/uploadFile'
  import online from '@/mixins/online'
  export default {
    name: 'cjzdIndex',
    mixins: [online],
    components: { Edit, PlEdit, CjzdView, CjzdSignalView, uploadFile },
    data() {
      return {
        searchShow: false,
        list: null,
        listLoading: true,
        dowmLoading: false,
        layout: 'total, sizes, prev, pager, next, jumper',
        total: 0,
        selectRows: '',
        elementLoadingText: '正在加载...',
        queryForm: {
          pageNo: 1,
          pageSize: 20,
          name: '',
          deviceType: '',
          subitem: '',
          areaId: '',
          status: '',
          // logicalAreaId: '',
          // logicalFloorId: '',
          // logicalBuildId: '',
        },
        deviceTypeList: [],
        subitemList: [],
        areaList: [],
        logicalAreaList: [],
        statusList: [],
        multiple: true, // 多选
        ids: [], // 选中数组
        checkAreaDatas: [],
        areaIds: [],
        logicalAreaIds: [],
        checkLogicalAreaDatas: [],
      }
    },
    created() {
      this.deviceTypeListData()
      this.statusListData()
      this.areaTreeList()
      this.areaTreeList2()
      this.subitemListData()
      this.fetchData2()
    },
    methods: {
      handleImport() {
        this.$refs['uploadFile'].showEdit()
      },
      ShowSearch() {
        this.searchShow = !this.searchShow
      },
      refresh() {
        location.reload()
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.$refs.queryForm.resetFields()
        this.areaIds = []
        this.logicalAreaIds = []
        this.queryData()
      },
      setSelectRows(selection) {
        console.log('selection')
        console.log(selection)
        this.selectRows = selection
        console.log(this.selectRows)
        this.ids = selection.map((item) => item.id)
        console.log('ids')
        console.log(this.ids)
        // this.multiple = !selection.length;
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
          this.$refs['edit'].showEdit(row.id)
        } else {
          this.$refs['edit'].showEdit()
        }
      },
      handlePlEdit() {
        console.log('this.selectRows')
        console.log(this.selectRows)
        if (this.selectRows.length > 0) {
          this.$refs['plEdit'].showEdit(this.selectRows)
        } else {
          this.$baseMessage('请选择至少一条数据', 'error')
        }
      },
      handleView(row) {
        this.$refs['cjzdView'].showEdit(row.id)
      },
      handleSignal(row) {
        this.$refs['cjzdSignalView'].showEdit(row)
      },
      handleDelete(row) {
        if (row.id) {
          if (row.status == 2) {
            this.$baseMessage('该设备状态为使用中暂无法删除', 'error')
            return
          }

          this.$baseConfirm('你确定要删除当前项吗', null, async () => {
            const { msg } = await onDel({ id: row.id, isDel: 1 })
            this.$baseMessage('删除成功', 'success')
            this.fetchData()
          })
        }
      },
      /**导出xls**/
      async downXls() {
        this.dowmLoading = true

        toExportTemplate(this.queryForm).then((res) => {
          console.log(res)
          const objectUrl = URL.createObjectURL(new Blob([res])) // 获取文件名
          const link = document.createElement('a') // 文件地址
          link.download = '采集终端导出.xlsx'
          link.href = objectUrl
          link.click()
          this.dowmLoading = false
        })
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
      queryData2() {
        this.queryForm.pageNo = 1
        this.fetchData2()
      },
      async deviceTypeListData() {
        const { data } = await getDeviceTypeList()
        console.log(data)
        this.deviceTypeList = data
      },
      async statusListData() {
        const { data } = await getRedisDictList({ dictType: 'deviceStatus' })
        console.log(data)
        this.statusList = data
      },
      //区域列表数据
      areaTreeList() {
        areaTreeList().then((res) => {
          if (res.code == 0) {
            this.areaList = res.data
          }
        })
      },
      //区域列表数据
      areaTreeList2() {
        areaTreeList2().then((res) => {
          if (res.code == 0) {
            this.logicalAreaList = res.data
          }
        })
      },
      choseArea(subjectValue) {
        console.log(subjectValue)
        if (subjectValue.length == 0) {
          //clearable的点击×删除事件
          this.checkAreaDatas = []
        }
      },
      choseLogicalArea(subjectValue) {
        console.log(subjectValue)
        // this.$refs.logicalAreaId.dropDownVisible = false; //监听值发生变化就关闭它
        if (subjectValue.length == 0) {
          //clearable的点击×删除事件
          this.checkLogicalAreaDatas = []
        }
      },
      async subitemListData() {
        const { data } = await getDeviceSubitemList()
        console.log(data)
        this.subitemList = data
      },
      async fetchData() {
        this.listLoading = true

        // setTimeout(async () => {
        // const areaId = this.$refs['areaId'].getCheckedNodes()
        // //获取当前点击节点的label值
        // if (areaId != null && areaId != '' && areaId != undefined) {
        //   if (areaId.length == 1) {
        //     console.log(areaId[0].data.value)
        //     if (areaId[0].level == 3) {
        //       this.queryForm.areaId = areaId[0].data.value
        //     }
        //   } else {
        //     this.queryForm.areaId = ''
        //   }
        // } else {
        //   this.queryForm.areaId = ''
        // }

        // const logicalAreaId = this.$refs["logicalAreaId"].getCheckedNodes();
        // //获取当前点击节点的label值
        // if (logicalAreaId != null && logicalAreaId != "" && logicalAreaId != undefined) {
        //     if (logicalAreaId.length == 1) {
        //         console.log(logicalAreaId[0].data.value)
        //         if (logicalAreaId[0].level == 3) {
        //             this.queryForm.logicalAreaId = logicalAreaId[0].data.value;
        //             this.queryForm.logicalFloorId = logicalAreaId[0].parent.data.value;
        //             this.queryForm.logicalBuildId = logicalAreaId[0].parent.parent.data.value;
        //         } else if (logicalAreaId[0].level == 2) {
        //             this.queryForm.logicalAreaId = 0;
        //             this.queryForm.logicalFloorId = logicalAreaId[0].data.value;
        //             this.queryForm.logicalBuildId = logicalAreaId[0].parent.data.value;
        //         } else if (logicalAreaId[0].level == 1) {
        //             this.queryForm.logicalAreaId = 0;
        //             this.queryForm.logicalFloorId = 0;
        //             this.queryForm.logicalBuildId = logicalAreaId[0].data.value;
        //         }
        //     } else {
        //         this.queryForm.logicalAreaId = 0;
        //         this.queryForm.logicalFloorId = 0;
        //         this.queryForm.logicalBuildId = 0;
        //     }
        // } else {
        //     this.queryForm.logicalAreaId = 0;
        //     this.queryForm.logicalFloorId = 0;
        //     this.queryForm.logicalBuildId = 0;
        // }
        this.queryForm.buildId = this.areaIds?.[0]
        this.queryForm.floorId = this.areaIds?.[1]
        this.queryForm.areaId = this.areaIds?.[2]
        const { data } = await getPageList(this.queryForm)
        this.list = data.records
        this.total = data.total
        // setTimeout(() => {
        this.listLoading = false
        // }, 300)

        // }, 200)
      },
      async fetchData2() {
        this.listLoading = true
        this.queryForm.areaId = ''
        // this.queryForm.logicalAreaId = 0;
        // this.queryForm.logicalFloorId = 0;
        // this.queryForm.logicalBuildId = 0;
        const { data } = await getPageList(this.queryForm)
        this.list = data.records
        this.total = data.total
        // setTimeout(() => {
        this.listLoading = false
        // }, 300)
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
<style>
  .left-panel-self .left-panel .el-form,
  .left-panel-self .left-panel .el-form-item__content {
    display: flex;
    flex-wrap: nowrap !important;
  }

  .location .el-checkbox,
  .location .el-radio {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 10;
  }

  .location .el-cascader-node {
    padding-left: 34px;
  }

  .location .el-checkbox__input,
  .location .el-radio__input {
    position: absolute;
    left: 20px;
    top: 10px;
  }
</style>
