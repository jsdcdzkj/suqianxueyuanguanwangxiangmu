<template>
  <div class="index-container">
    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item prop="logicalAreaIds">
            <el-cascader
              :props="{
                expandTrigger: 'hover',
                value: 'value',
                label: 'label',
                children: 'children',
                checkStrictly: true
              }"
              placeholder="选择所属区域"
              :options="areaList"
              style="width: 100%"
              :show-all-levels="false"
              collapse-tags
              ref="areaId"
              v-model="queryForm.areaIdList"
              @change="choseArea"
              filterable
              clearable
            ></el-cascader>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model.trim="queryForm.name"
              placeholder="请输入设备名称"
              clearable
              @keyup.enter.native="queryData"
            />
          </el-form-item>
          <el-form-item prop="onLineStatus">
            <el-select
              v-model="queryForm.onLineStatus"
              placeholder="请选择在线状态"
              clearable
            >
              <el-option
                v-for="item in onlineDict2"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model.trim="queryForm.channel"
              placeholder="请输入设备编码"
              clearable
              @keyup.enter.native="queryData"
            />
          </el-form-item>
          <el-form-item prop="deviceVideoType">
            <el-select
              v-model="queryForm.deviceVideoType"
              placeholder="请选择设备类型"
              clearable
            >
              <el-option
                v-for="item in typeList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="primary" @click="queryData">
              查询
            </el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </vab-query-form-left-panel>
      <vab-query-form-right-panel :span="4">
        <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">
          添加
        </el-button>
        <el-button
          icon="el-icon-plus"
          plain
          type="success"
          @click="handleImport"
        >
          导入
        </el-button>
        <el-button
          type="primary"
          @click="refresh"
          :loading="listLoading"
          v-if="username == 'admin'"
        >
          同步数据
        </el-button>
        <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
        <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
        <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
        <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
      </vab-query-form-right-panel>
    </vab-query-form>

    <el-table
      v-loading="listLoading"
      :data="list"
      :element-loading-text="elementLoadingText"
      @selection-change="setSelectRows"
      height="calc(100vh - 240px)"
      border
    >
      <el-table-column prop="index" label="序号" width="70" align="center">
        <template #default="{ $index }">
          <span>
            {{ $index + 1 + (queryForm.pageNo - 1) * queryForm.pageSize }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="name"
        label="设备名称"
        width="200"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="channel"
        label="设备编码"
        width="200"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="typeName"
        label="视频设备类型"
        width="180"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="areaName"
        label="所属区域"
        align="center"
        width="250"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="supplierName"
        label="供应商"
        width=""
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="modelName"
        label="设备型号"
        width="180"
        align="center"
      ></el-table-column>
      <el-table-column
        label="设备状态"
        width="100"
        align="center"
        :key="Math.random()"
        prop="status"
      >
        <template slot-scope="scope">
          <el-link
            :icon="
              scope.row.status == 0 ? 'el-icon-warning' : 'el-icon-success'
            "
            :type="scope.row.status == 0 ? 'danger' : 'success'"
            @click="changeHidden(scope.row)"
          >
            {{ scope.row.status == 0 ? '禁用' : '启用' }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="deviceDesc"
        label="设备描述"
        align="center"
      ></el-table-column>
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
            v-if="row.onLineStatus == 0"
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
      <el-table-column label="操作" width="240" align="right">
        <template #default="{ row }">
          <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
          <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>
          <el-link :underline="false" type="primary" @click="handleView(row)">详情</el-link> -->
          <el-button
            icon="el-icon-edit"
            type="success"
            size="mini"
            v-if="row.deviceVideoType == 7 && row.isGroundLock == 1"
            @click="handleDsEdit(row)"
          >
            地锁
          </el-button>
            <el-button
            icon="el-icon-edit"
            type="success"
            size="mini"
            v-if="row.deviceVideoType == 7 && row.isGroundLock == 0"
            @click="handleCWEdit(row)"
          >
            车位
          </el-button>
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
    <spsbView ref="spsbView" @fetch-data="fetchData"></spsbView>
    <dsEdit ref="dsEdit"></dsEdit>
    <cwEdit ref="cwEdit"></cwEdit>
    <uploadFile ref="uploadFile" @fetch-data="fetchData"></uploadFile>
  </div>
</template>

<script>
  import { getPage, saveOrUpdate, del, syncDahua, typeSelectLists } from '@/api/spsb'
  import { areaTreeList } from '@/api/sysBuildArea'
  import Edit from './components/SpsbEdit'
  import SpsbView from './components/SpsbView'
  import DsEdit from './components/dsEdit'
  import CwEdit from './components/cwEdit'
  import uploadFile from './components/uploadFile'
  import { mapGetters } from 'vuex'
  import online from '@/mixins/online'
  export default {
    name: 'UserManagement',
    mixins: [online],
    components: { Edit, SpsbView, DsEdit, uploadFile, CwEdit },
    data() {
      return {
        searchShow: false,
        list: null,
        listLoading: true,
        layout: 'total, sizes, prev, pager, next, jumper',
        total: 0,
        selectRows: '',
        elementLoadingText: '正在加载...',
        queryForm: {
          pageNo: 1,
          pageSize: 20,
          name: '',
          areaId: '',
          areaIdList: [],
          channel: '',
          deviceVideoType: '',
        },
        areaList: [],
        typeList: []
      }
    },
    watch: {
      'queryForm.areaIdList': {
        handler(value) {
          if (value.length == 0) {
            this.queryForm.areaId = ''
          }
        },
      },
    },
    created() {
      this.fetchData()
      this.areaListData()
      this.typeSelectList()
    },
    computed: {
      ...mapGetters({
        username: 'user/username',
      }),
    },
    methods: {
      // 视频设备类型
    typeSelectList() {
      typeSelectLists().then((res) => {
        if (res.code == 0) {
          this.typeList = res.data;
        }
      })
    },
      ShowSearch() {
        this.searchShow = !this.searchShow
      },
      handleImport() {
        this.$refs['uploadFile'].showEdit()
      },
      choseArea(subjectValue) {
        //选择区域
        if (subjectValue.length == 0) {
          //clearable的点击×删除事件
          this.checkAreaDatas = []
        }
        // if (subjectValue.length == 0) {
        //   this.checkAreaDatas = [];
        // } else {
        //   this.queryForm.areaId = this.$refs['areaId'].getCheckedNodes()[0].data.value;
        // }
      },
      async areaListData() {
        //区域列表数据
        areaTreeList().then((res) => {
          if (res.code == 0) {
            this.areaList = res.data
          }
        })
      },
      refresh() {
        this.listLoading = true
        syncDahua()
          .then((res) => {
            if (res.code === 0) {
              this.$baseMessage('同步成功', 'success')
              this.queryData()
            }
          })
          .catch(() => {
            this.listLoading = false
          })
      },
      setSelectRows(val) {
        this.selectRows = val
      },
      changeHidden(row) {
        var status = row.status
        // 弹窗是否确定求改显示
        this.$baseConfirm(
          '确定' + (status == 1 ? '禁用' : '启用') + '当前视频设备吗',
          null,
          async () => {
            row.status = row.status ^ 1
            saveOrUpdate(row).then((res) => {
              if (0 == res.code) {
                this.$baseMessage(
                  '已' + (status == 1 ? '禁用' : '启用'),
                  'success'
                )
                this.fetchData()
              } else {
                this.$baseMessage(
                  (status == 1 ? '禁用' : '启用') + '失败',
                  'error'
                )
              }
            })
          }
        )
      },
      handleEdit(row) {
        if (row.id) {
          this.$refs['edit'].showEdit(row)
        } else {
          this.$refs['edit'].showEdit()
        }
      },
      handleDsEdit(row) {
        this.$refs['dsEdit'].showEdit(row)
      },
      handleCWEdit(row) {
        this.$refs['cwEdit'].showEdit(row)
      },
      handleView(row) {
        this.$refs['spsbView'].showEdit(row)
      },
      handleDelete(row) {
        if (row.id) {
          if (row.status == 0) {
            //禁用状态可删除
            this.$baseConfirm('确定删除当前项吗', null, async () => {
              del({ id: row.id }).then((res) => {
                if (0 == res.code) {
                  this.$baseMessage('删除成功', 'success')
                  this.fetchData()
                } else {
                  this.$baseMessage(res.msg, 'success')
                }
              })
            })
          } else {
            this.$baseMessage('启用状态不可删除', 'error')
          }
        } else {
          if (this.selectRows.length > 0) {
            const ids = this.selectRows.map((item) => item.id).join()
            this.$baseConfirm('确定删除选中项吗', null, async () => {
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
        // if (this.queryForm.areaIdList.length > 0) {
        //   this.queryForm.areaId =
        //     this.queryForm.areaIdList[this.queryForm.areaIdList.length - 1]
        // }
        this.listLoading = true
        let param = {
          pageNo: this.queryForm.pageNo,
          pageSize: this.queryForm.pageSize,
          name: this.queryForm.name,
          onLineStatus: this.queryForm.onLineStatus,
          channel: this.queryForm.channel,
          deviceVideoType: this.queryForm.deviceVideoType,
          buildId: this.queryForm.areaIdList?.[0],
          floorId:  this.queryForm.areaIdList?.[1],
          areaId: this.queryForm.areaIdList?.[2],
        }
        getPage(param).then((res) => {
          if (0 == res.code) {
            this.list = res.data.records
            this.total = res.data.total
            if (res.data.records.length == 0 && this.queryForm.pageNo > 1) {
              this.queryForm.pageNo = this.queryForm.pageNo - 1
              this.fetchData()
            }
          }
        })

        setTimeout(() => {
          this.listLoading = false
        }, 300)
      },
      resetQuery() {
        this.queryForm.name = ''
        this.queryForm.areaId = ''
        this.queryForm.pageNo = 1
        this.queryForm.pageSize = 20
        this.queryForm.areaIdList = []
        this.queryForm.onLineStatus = ''
        this.queryForm.channel = ''
        this.queryForm.deviceVideoType = ''
        this.queryData()
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
