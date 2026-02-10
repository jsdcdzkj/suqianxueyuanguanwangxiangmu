<template>
    <div class="index-container">

        <vab-query-form>
            <vab-query-form-left-panel :span="20">
                <el-form :inline="true" :model="queryForm" @submit.native.prevent>
                    <el-form-item>
                        <el-input v-model.trim="queryForm.groupName" clearable placeholder="分组名称" @keyup.enter.native="queryData"/>
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
                        <el-button icon="el-icon-search" type="primary" @click="queryData">查询</el-button>
                        <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
                    </el-form-item>
                </el-form>

            </vab-query-form-left-panel>
            <vab-query-form-right-panel :span="4">
                <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">添加</el-button>
            </vab-query-form-right-panel>
        </vab-query-form>

        <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
                  border height="calc(100vh - 240px)">
            <el-table-column prop="index" label="序号" width="80" align="center">
                <template #default="{ row, $index }">

                    <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
                </template>
            </el-table-column>
            <el-table-column align="center" label="分组名称" prop="groupName" show-overflow-tooltip min-width="120"></el-table-column>
            <el-table-column align="center" label="创建时间" prop="createTime" show-overflow-tooltip ></el-table-column>

            <el-table-column align="center" label="备注" minwidth="120" prop="remark" show-overflow-tooltip></el-table-column>
            <el-table-column label="操作" width="160" align="center">
                <template #default="{ row }">
                    <el-button icon="el-icon-edit"  type="success" size="mini" v-if="row.loginName != 'admin'" @click="handleEdit(row)">编辑</el-button>
                    <el-button icon="el-icon-delete" type="danger" size="mini" v-if="row.loginName != 'admin'"  @click="handleDelete(row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination :current-page="queryForm.pageNo" :layout="layout" :page-size="queryForm.pageSize" :total="total"
                       background @size-change="handleSizeChange"
                       @current-change="handleCurrentChange"></el-pagination>
        <edit ref="edit" @fetch-data="fetchData"></edit>
    </div>
</template>

<script>
import {doDelete, getList} from '@/api/groupManagement'
import Edit from './components/groupManagementEdit'

export default {
    name: 'groupManagement',
    components: {Edit},
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
                groupName: '',
                startTime: '',
                endTime: '',
            },
            roleList: [],
            values: [],
        }
    },
    created() {
        this.fetchData()
    },
    methods: {
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
                this.queryForm.startTime = val[0]
                this.queryForm.endTime = val[1]
            }
        },
        
        reset() {
            this.queryForm = {
                pageNo: 1,
                pageSize: 20,
                groupName: '',
                startTime: '',
                endTime: '',
            }
            this.values = []
            this.fetchData()
        },

        handleEdit(row) {
            if (row.id) {
                this.$refs['edit'].showEdit(row)
            } else {
                this.$refs['edit'].showEdit()
            }
        },
        handleDelete(row) {
            this.$baseConfirm('你确定要删除当前项吗', null, async () => {
                let listLength = this.list.length
                const {msg} = await doDelete({id: row.id,isDel:1})
                this.$baseMessage("删除成功", 'success')
                if (listLength == 1 && this.queryForm.pageNo > 1) {
                    this.queryForm.pageNo--
                }
                this.fetchData()
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
        
        async fetchData() {
            this.listLoading = true
            const {data, totalCount} = await getList(this.queryForm)
            this.list = data.records
            this.total = data.total
            setTimeout(() => {
                this.listLoading = false
            }, 300)
        },
        
    },
}
</script>
<style scoped lang="scss">
    .tag-box{
        display: flex;
        flex-wrap: wrap;
        margin-top: 10px;
        .el-tag{
            margin-bottom: 10px;
            margin-right: 10px;
        }
        .el-tag + .el-tag{
            margin-left: 0;
        }
    }
</style>
