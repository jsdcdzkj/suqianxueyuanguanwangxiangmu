<template>
    <div class="roleManagement-container">
        <!-- <el-divider content-position="left">
          演示环境仅做基础功能展示，若想实现不同角色的真实菜单配置，需将settings.js路由加载模式改为all模式，由后端全面接管路由渲染与权限控制
        </el-divider> -->
        <vab-query-form>
            <vab-query-form-left-panel :span="20">
                <el-form :inline="true" :model="queryForm" @submit.native.prevent>
                    <el-form-item>
                        <el-select v-model="queryForm.deviceTypeId" placeholder="请选择设备类型" class="w"
                                   clearable
                                   @change="changeCollectData">
                            <el-option
                                v-for="item in this.deviceTypeList"
                                :key="item.id"
                                :label="item.deviceTypeName"
                                :value="item.id"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-input v-model.trim="queryForm.configName" placeholder="配置名称" clearable
                                  @keyup.enter.native="queryData"/>
                    </el-form-item>
<!--                    <el-form-item>-->
<!--                        <el-input v-model.trim="queryForm.config_value" placeholder="配置值" clearable-->
<!--                                  @keyup.enter.native="queryData"/>-->
<!--                    </el-form-item>-->
                    <el-form-item>
                        <el-input v-model.trim="queryForm.configSign" placeholder="配置标识" clearable
                                  @keyup.enter.native="queryData"/>
                    </el-form-item>
                    <el-form-item>
                        <el-button icon="el-icon-search" type="primary" @click="queryData">查询</el-button>
                        <el-button icon="el-icon-refresh" @click="rest">重置</el-button>
                    </el-form-item>
                </el-form>

            </vab-query-form-left-panel>
            <vab-query-form-right-panel :span="4">
                <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">添加</el-button>
            </vab-query-form-right-panel>
        </vab-query-form>

        <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
                  height="calc(100vh - 240px)" border>
            <el-table-column prop="index" label="序号" width="80" align="center">
                <template #default="{ row, $index }">
                    <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
                </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="configName" label="配置名称" align="center"
                             minwidth="200"></el-table-column>
<!--            <el-table-column show-overflow-tooltip prop="config_value" label="配置值" align="center"-->
<!--                             minwidth="200"></el-table-column>-->
            <el-table-column show-overflow-tooltip prop="configSign" label="配置标识" align="center"
                             minwidth="200"></el-table-column>
            <el-table-column show-overflow-tooltip prop="deviceTypeName" label="设备类型" align="center"
                             minwidth="200"></el-table-column>
            <el-table-column show-overflow-tooltip prop="deviceName" label="设备" align="center"
                             minwidth="200"></el-table-column>
            <el-table-column show-overflow-tooltip prop="memo" label="描述" align="center"
                             minwidth="200"></el-table-column>
            <el-table-column show-overflow-tooltip prop="createUserName" label="创建人" align="center"
                             width="180"></el-table-column>
            <el-table-column show-overflow-tooltip prop="createTime" label="创建时间" align="center"
                             width="180"></el-table-column>
            <el-table-column align="center" label="操作" show-overflow-tooltip width="160">
                <template #default="{ row }">
                    <el-button icon="el-icon-edit" type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
                    <el-button icon="el-icon-delete" type="danger" size="mini" @click="handleDelete(row)">删除
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
    </div>
</template>

<script>
import {deleteConfig, getPageList} from '@/api/config'
import {selectDictLabel} from "../../../utils/tools"
import {getDeviceTypeList} from '@/api/configDeviceType'
import {getList as getDeviceCollectList} from '@/api/deviceCollect'
import Edit from './components/edit'

export default {
    name: 'log',
    components: {Edit},
    data() {
        return {
            list: null,
            listLoading: true,
            layout: 'total, sizes, prev, pager, next, jumper',
            total: 0,
            selectRows: '',
            elementLoadingText: '正在加载...',
            deviceTypeList: null,
            deviceIdList: null,
            queryForm: {
                pageNo: 1,
                pageSize: 10,
                deviceTypeId: '',
                deviceIdList: [],
                configName: '',
                config_value: '',
                configSign: '',
                memo: '',
                startTime: '',
                endTime: '',
            },
            options: [],
            sys_logType: [],
            values: []
        }
    },
    created() {
        this.deviceTypeListData();
        this.getDeviceCollectList();
        this.fetchData()
    },
    methods: {
        selectDictLabel,
        async deviceTypeListData() {
            //所有设备类型信息
            const {data} = await getDeviceTypeList();
            this.deviceTypeList = data;
        },
        async getDeviceCollectList() {
            //所有设备信息
            const {data} = await getDeviceCollectList()
            this.deviceIdList = data;
        },
        async changeCollectData() {
            var deviceType = this.queryForm.deviceTypeId
            var buildId = this.queryForm.buildId
            var floor = this.queryForm.floor
            const {data} = await getDeviceCollectList({"deviceType": deviceType}, {"buildId": floor}, {"floorId": buildId})
            this.deviceIdList = data;
        },
        changeDate(val) {
            if (val === null) {
                this.queryForm.startTime = ''
                this.queryForm.endTime = ''
                return
            }
            if (val.length === 0) {
                this.queryForm.startTime = ''
                this.queryForm.endTime = ''
            } else {
                this.queryForm.timeList = []
                this.queryForm.startTime = val[0]
                this.queryForm.endTime = val[1]
            }
        },
        setSelectRows(val) {
            this.selectRows = val
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
            const {data, totalCount} = await getPageList(this.queryForm)
            this.list = data.records
            this.total = data.total
            setTimeout(() => {
                this.listLoading = false
            }, 300)
        },
        rest() {
            this.queryForm.deviceTypeId = ''
            this.queryForm.device_id = ''
            this.queryForm.configName = ''
            this.queryForm.config_value = ''
            this.queryForm.configSign = ''
            this.queryForm.memo = ''
            this.queryForm.startTime = ''
            this.queryForm.endTime = ''
            this.values = []
            this.queryData()
        },
        handleEdit(row) {
            if (row.id) {
                this.$refs['edit'].showEdit(row)
            } else {
                this.$refs['edit'].showEdit()
            }
        },
        handleDelete(row) {
            if (row.id) {
                this.$baseConfirm('你确定要删除当前项吗', null, async () => {
                    let listLength = this.list.length
                    const {msg} = await deleteConfig({isDel: 1, id: row.id, configName: row.configName})
                    this.$baseMessage("删除成功", 'success')
                    if (listLength == 1 && this.queryForm.pageNo > 1) {
                        this.queryForm.pageNo--
                    }
                    await this.fetchData()
                })
            } else {
                this.$baseMessage('请选择要删除的项', 'warning')
            }
        },
    },
}
</script>
