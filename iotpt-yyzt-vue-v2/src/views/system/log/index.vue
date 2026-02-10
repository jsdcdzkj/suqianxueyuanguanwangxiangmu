<template>
    <div class="roleManagement-container">
        <!-- <el-divider content-position="left">
          演示环境仅做基础功能展示，若想实现不同角色的真实菜单配置，需将settings.js路由加载模式改为all模式，由后端全面接管路由渲染与权限控制
        </el-divider> -->
        <vab-query-form>
            <vab-query-form-left-panel :span="20">
                <el-form :inline="true" :model="queryForm" @submit.native.prevent>
                    <el-form-item>
                        <el-select v-model="queryForm.logType" filterable placeholder="日志类型" class="w" clearable>
                            <el-option v-for="item in sys_logType" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-date-picker
                            clearable
                            v-model="values"
                            @change="changeDate"
                            value-format="yyyy-MM-dd"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-input v-model.trim="queryForm.content" placeholder="内容描述" clearable @keyup.enter.native="queryData"/>
                    </el-form-item>
                    <el-form-item>
                        <el-input v-model.trim="queryForm.operatorName" placeholder="操作人" clearable @keyup.enter.native="queryData"/>
                    </el-form-item>
                    <el-form-item>
                        <el-button icon="el-icon-search" type="primary" @click="queryData">查询</el-button>
                        <el-button icon="el-icon-refresh" @click="rest">重置</el-button>
                    </el-form-item>
                </el-form>

            </vab-query-form-left-panel>
        </vab-query-form>

        <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText" height="calc(100vh - 240px)" border>
            <el-table-column prop="index" label="序号" width="80" align="center">
                <template #default="{ row, $index }">
                    <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
                </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="logTypeName" label="日志类型" align="center" width="180"></el-table-column>
            <el-table-column show-overflow-tooltip prop="content" label="内容描述" align="center" minwidth="200"></el-table-column>
            <el-table-column show-overflow-tooltip prop="operatorName" label="操作人" align="center" width="180"></el-table-column>
            <el-table-column show-overflow-tooltip prop="operateTime" label="操作时间" align="center" width="180"></el-table-column>

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
    </div>
</template>

<script>
import {getPageList, getTypeAll} from '@/api/log'
import {selectDictLabel} from "../../../utils/tools";

export default {
    name: 'log',
    components: {},
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
                pageSize: 20,
                operatorName: '',
                content: '',
                logType: '',
                startTime: '',
                endTime: '',
            },
            options: [],
            sys_logType: [],
            values: []
        }
    },
    async created() {
        const {data} = await getTypeAll()
        this.sys_logType = data
        await this.fetchData()
    },
    methods: {
        selectDictLabel,
        changeDate(val) {
            if (val === null) {
                this.queryForm.startTime = ''
                this.queryForm.endTime = ''
                return
            }
            if (val.length === 0) {
                this.queryForm.startTime = ''
                this.queryForm.endTime = ''
            }else{
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
            this.queryForm.operatorName = ''
            this.queryForm.content = ''
            this.queryForm.logType = ''
            this.queryForm.startTime = ''
            this.queryForm.endTime = ''
            this.values = []
            this.queryData()
        },
    },
}
</script>
