<template>
  <div class="index-container">
    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-input
              v-model.trim="queryForm.name"
              placeholder="请输入设备名称"
              @keyup.enter.native="queryData"
              clearable
            />
          </el-form-item>
          <el-form-item prop="areaIds">
            <el-cascader
            :props="{
                expandTrigger: 'hover',
                value: 'value',
                label: 'label',
                children: 'children',
                checkStrictly: true
              }"
              placeholder="请选择所属区域"
              :options="areaList"
              style="width: 100%"
              :show-all-levels="false"
              collapse-tags
              ref="areaId"
              v-model="areaIds"
              @change="choseArea"
              clearable
            ></el-cascader>
          </el-form-item>
          <el-form-item prop="onLineStatus">
            <el-select
              v-model="queryForm.onLineStatus"
              placeholder="请选择在线状态"
              clearable
            >
              <el-option
                v-for="item in this.onlineDict2"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              ></el-option>
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
      <vab-query-form-right-panel :span="4">
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
        width="200"
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
        width="200"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="modelName"
        label="设备型号"
        width="200"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="brand"
        label="设备品牌"
        width="200"
        align="center"
      ></el-table-column>
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
      <el-table-column label="操作" width="300" align="center">
        <template #default="{ row }">
          <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
                    <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link> -->
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
          <el-dropdown trigger="click">
            <el-button type="primary" size="mini">
              更多菜单
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <!-- <el-link :underline="false" type="text" style="color: #fff; text-align: ceter">更多<i class="el-icon-arrow-down el-icon--right"></i></el-link> -->
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <el-link
                  :underline="false"
                  type="primary"
                  @click="handleView(row)"
                  style="width: 100%"
                >
                  详情
                </el-link>
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-link
                  :underline="false"
                  type="primary"
                  @click="handleUpload(row)"
                  style="width: 100%"
                >
                  上传配置模板
                </el-link>
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-link
                  :underline="false"
                  type="primary"
                  @click="handleDown(row)"
                  style="width: 100%"
                >
                  生成配置文件
                </el-link>
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-link
                  :underline="false"
                  type="primary"
                  @click="handleDeviceView(row)"
                  style="width: 100%"
                >
                  设备信息
                </el-link>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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
    <wgsbView ref="wgsbView" @fetch-data="fetchData"></wgsbView>
    <glView ref="glView" @fetch-data="fetchData"></glView>
    <uploadView ref="uploadView" @fetch-data="fetchData"></uploadView>
    <uploadFile ref="uploadFile" @fetch-data="fetchData"></uploadFile>
  </div>
</template>

<script>
  import { exportFile, getPageList, onDel } from '@/api/deviceGateway'
  import { areaTreeList } from '@/api/org'
  import { getBuildAreaList } from '@/api/sysBuildArea'
  import uploadView from './components/uploadView'
  import uploadFile from './components/uploadFile'
  import Edit from './components/WgsbEdit'
  import glView from './components/WgsbGlView'
  import WgsbView from './components/WgsbView'
  import online from '@/mixins/online'
  export default {
    name: 'UserManagement',
    mixins: [online],
    components: { Edit, WgsbView, glView, uploadView, uploadFile },
    data() {
      return {
        searchShow: false,
        list: null,
        // areaList: null,
        listLoading: true,
        layout: 'total, sizes, prev, pager, next, jumper',
        total: 0,
        selectRows: '',
        areaList: [],
        areaIds: [],
        checkAreaDatas: [],
        elementLoadingText: '正在加载...',
        queryForm: {
          pageNo: 1,
          pageSize: 10,
          name: '',
          areaId: '',
        },
      }
    },
    created() {
      // 区域
      // this.getBuildAreaList()
      this.fetchData()
      this.areaTreeList()
    },
    methods: {
      ShowSearch() {
        this.searchShow = !this.searchShow
      },
      refresh() {
        location.reload()
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
      clearData() {
        this.queryForm.pageNo = 1
        this.queryForm.pageSize = 10
        this.queryForm.name = ''
        this.queryForm.onLineStatus = ''
        this.areaIds = []
        this.fetchData()
      },
      handleImport() {
        this.$refs['uploadFile'].showEdit()
      },
      handleEdit(row) {
        if (row.id) {
          this.$refs['edit'].showEdit(row)
        } else {
          this.$refs['edit'].showEdit()
        }
      },
      handleView(row) {
        this.$refs['wgsbView'].showEdit(row)
      },
      async getBuildAreaList() {
        const { data } = await getBuildAreaList()
        this.areaList = data
      },
      handleDelete(row) {
        if (row.id) {
          this.$baseConfirm('你确定要删除当前项吗', null, async () => {
            const { msg } = await onDel({ id: row.id })
            this.$baseMessage(msg, 'success')
            this.fetchData()
          })
        } else {
          if (this.selectRows.length > 0) {
            const ids = this.selectRows.map((item) => item.id).join()
            this.$baseConfirm('你确定要删除选中项吗', null, async () => {
              const { msg } = await onDel({ ids })
              this.$baseMessage(msg, 'success')
              this.fetchData()
            })
          } else {
            this.$baseMessage('未选中任何行', 'error')
            return false
          }
        }
      },
      handleUpload(row) {
        this.$refs['uploadView'].showEdit(row)
      },
      handleDown(row) {
        this.exportFile(row)
      },
      handleDeviceView(row) {
        this.$refs['glView'].showEdit(row)
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
      //区域列表数据
      areaTreeList() {
        areaTreeList().then((res) => {
          if (res.code == 0) {
            this.areaList = res.data
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
      async fetchData() {
        this.listLoading = true
        setTimeout(async () => {
          const areaId = this.$refs['areaId'].getCheckedNodes()
        //   //获取当前点击节点的label值
        //   if (areaId != null && areaId != '' && areaId != undefined) {
        //     for (var i = 0; i < areaId.length; i++) {
        //       console.log(areaId[i].data.value)
        //       if (areaId[i].level == 3) {
        //         this.queryForm.areaId = areaId[i].data.value
        //       }
        //     }
        //   } else {
        //     this.queryForm.areaId = ''
        //   }
        this.queryForm.buildId = this.areaIds?.[0]
        this.queryForm.floorId = this.areaIds?.[1]
        this.queryForm.areaId = this.areaIds?.[2]
          const { data } = await getPageList(this.queryForm)
          this.list = data.records
          this.total = data.total
          this.listLoading = false
        }, 400)
      },
      async refreshRoute() {
        this.$baseEventBus.$emit('reload-router-view')
        this.pulse = true
        setTimeout(() => {
          this.pulse = false
        }, 1000)
      },
      async exportFile(row) {
        this.listLoading = true
        await exportFile(row).then((res) => {
          console.log(res)
          let fileName = '数据模板.xlsx'
          let objectUrl = URL.createObjectURL(new Blob([res]))
          const link = document.createElement('a')
          link.download = decodeURI(fileName)
          link.href = objectUrl
          link.click()
          this.listLoading = false
          this.$baseMessage('导出成功！', 'success')
        })
        setTimeout(() => {
          this.listLoading = false
        }, 300)
      },
    },
  }
</script>
<style scoped lang="scss">
  .el-dropdown {
    margin-left: 10px;

    ::v-deep {
      .el-button--text {
        width: 100% !important;
      }
    }
  }
</style>
