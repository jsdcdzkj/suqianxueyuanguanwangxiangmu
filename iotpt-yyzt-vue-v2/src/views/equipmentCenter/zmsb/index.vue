<template>
  <div class="index-container">
    <vab-query-form>
      <vab-query-form-left-panel :span="20">
        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
          <el-form-item>
            <el-input
              v-model.trim="queryForm.pointName"
              placeholder="请输入设备名称"
              clearable
              @keyup.enter.native="queryData"
            />
          </el-form-item>
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
              v-model="areaIdList"
              filterable
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
                v-for="item in this.onlineDict"
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
            <el-button icon="el-icon-refresh" @click="resetQuery">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </vab-query-form-left-panel>
      <vab-query-form-right-panel :span="4" v-if="username == 'admin'">
        <el-button type="primary" @click="refresh" :loading="listLoading">
          同步数据
        </el-button>
      </vab-query-form-right-panel>
    </vab-query-form>
    <el-table
      v-loading="listLoading"
      :data="list"
      :element-loading-text="elementLoadingText"
      height="calc(100vh - 240px)"
      border
    >
      <el-table-column prop="index" label="序号" width="80" align="center">
        <template #default="{ $index }">
          <span>
            {{ $index + 1 + (queryForm.pageIndex - 1) * queryForm.pageSize }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="pointName"
        label="设备名称"
        width="250"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="deviceId"
        label="设备编码"
        width="320"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="storagePoint"
        label="点位号"
        width="330"
        align="center"
      ></el-table-column>
      <el-table-column
        show-overflow-tooltip
        label="所属区域"
        align="center"
        width="350"
      >
        <template #default="{ row }">
          <span v-if="row.areaName">
            {{ row.buildName }}/{{ row.floorName }}/{{ row.areaName }}
          </span>
          <span v-else-if="row.floorName">
            {{ row.buildName }}/{{ row.floorName }}
          </span>
          <span v-else>{{ row.buildName }}</span>
        </template>
      </el-table-column>
      <el-table-column
        show-overflow-tooltip
        prop="remark"
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
      <el-table-column label="操作" width="180" align="center">
        <template #default="{ row }">
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
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      :current-page="queryForm.pageIndex"
      :page-size="queryForm.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    ></el-pagination>
    <edit ref="edit" :areaList="areaList" @fetch-data="fetchData"></edit>
  </div>
</template>

<script>
  import { getPage, refresh, del } from '@/api/zmsb'
  import { areaTreeList } from '@/api/sysBuildArea'
  import Edit from './components/SpsbEdit'
  import { mapGetters } from 'vuex'
  import online from '@/mixins/online'
  export default {
    name: 'zmsb',
    mixins: [online],
    components: { Edit },
    data() {
      return {
        list: null,
        listLoading: true,
        total: 0,
        elementLoadingText: '正在加载...',
        queryForm: {
          pageIndex: 1,
          pageSize: 20,
          pointName: '',
          areaId: '',
        },
        areaList: [],
        areaIdList: []
      }
    },
    computed: {
      ...mapGetters({
        username: 'user/username',
      }),
    },
    created() {
      this.fetchData()
      this.areaListData()
    },
    methods: {
      areaListData() {
        //区域列表数据
        areaTreeList().then((res) => {
          if (res.code == 0) {
            this.areaList = res.data
          }
        })
      },
      refresh() {
        this.listLoading = true
        refresh()
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
      handleEdit(row) {
        let data = JSON.parse(JSON.stringify(row))
        if (data.areaId) {
          data.areaIds = [
            String(data.buildId),
            String(data.floorId),
            String(data.areaId),
          ]
        } else {
          data.areaIds = []
        }
        this.$refs['edit'].showEdit(data)
      },
      handleDelete(row) {
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
      },
      handleSizeChange(val) {
        this.queryForm.pageSize = val
        this.fetchData()
      },
      handleCurrentChange(val) {
        this.queryForm.pageIndex = val
        this.fetchData()
      },
      queryData() {
        this.queryForm.pageIndex = 1
        this.fetchData()
      },
      async fetchData() {
        this.listLoading = true
        this.queryForm.buildId = this.areaIdList?.[0]
        this.queryForm.floorId = this.areaIdList?.[1]
        this.queryForm.areaId = this.areaIdList?.[2]
        getPage(this.queryForm)
          .then((res) => {
            if (0 == res.code) {
              this.list = res.data.records
              this.total = res.data.total
              if (
                res.data.records.length == 0 &&
                this.queryForm.pageIndex > 1
              ) {
                this.queryForm.pageIndex = this.queryForm.pageIndex - 1
                this.fetchData()
              }
            }
          })
          .finally(() => {
            this.listLoading = false
          })
      },
      resetQuery() {
        this.queryForm = this.$options.data().queryForm
        this.areaIdList = []
        this.queryData()
      },
    },
  }
</script>
