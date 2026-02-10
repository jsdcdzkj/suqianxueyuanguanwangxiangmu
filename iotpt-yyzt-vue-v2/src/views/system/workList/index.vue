<template>
    <div class="roleManagement-container">
        <!-- <el-divider content-position="left">
          演示环境仅做基础功能展示，若想实现不同角色的真实菜单配置，需将settings.js路由加载模式改为all模式，由后端全面接管路由渲染与权限控制
        </el-divider> -->
        <vab-query-form>
            <vab-query-form-left-panel :span="20">
                <el-form :inline="true" :model="queryForm" @submit.native.prevent>
                    <el-form-item>
                        <el-input v-model.trim="queryForm.name" clearable placeholder="工作流名称"
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
                <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
                <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
                <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
                <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
            </vab-query-form-right-panel>
        </vab-query-form>

        <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
                  border height="calc(100vh - 240px)">
            <el-table-column prop="index" label="序号" width="80" align="center">
                <template #default="{ row, $index }">
                    <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
                </template>
            </el-table-column>
            <el-table-column align="center" label="工作流名称" minwidth="160" prop="name"
                             show-overflow-tooltip></el-table-column>

            <el-table-column align="center" label="地址" minwidth="160" prop="url"
                             show-overflow-tooltip></el-table-column>
            <el-table-column align="center" label="类型" width="100"
                             show-overflow-tooltip>
                <template slot-scope="scope">
                    <span v-if="scope.row.type==='1'" style="color:#4caf50">DIFY</span>
                    <span v-else style="color:#4caf50">毕昇</span>
                </template>
            </el-table-column>
            <el-table-column align="center" label="密钥" minwidth="160" prop="secretKey"
                             show-overflow-tooltip></el-table-column>
            <el-table-column align="center" label="图标" width="100" prop="icon"
                             show-overflow-tooltip></el-table-column>

            <el-table-column align="center" label="排序" width="80" prop="sort" ></el-table-column>

            <el-table-column align="center" label="操作" show-overflow-tooltip width="160">
                <template #default="{ row }">
                    <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
                    <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link> -->
                    <el-button icon="el-icon-edit"  type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
                    <el-button icon="el-icon-delete" type="danger" size="mini"  @click="handleDelete(row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            :current-page="queryForm.pageNo"
            :layout="layout"
            :page-size="queryForm.pageSize"
            :total="total"
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        ></el-pagination>
        <edit ref="edit" @fetch-data="fetchData"></edit>
    </div>
</template>

<script>
import {saveOrUpdate, getList} from '@/api/worklist'
import Edit from './components/edit'

export default {
    name: 'dictionary',
    components: {Edit},
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
                type: '',
                name: ''
            },
            options: [],
        }
    },
    created() {
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
                    let listLength = this.list.length
                    const {msg} = await saveOrUpdate({id: row.id,isDel:1})
                    this.$baseMessage("删除成功", 'success')
                    if (listLength == 1 && this.queryForm.pageNo > 1) {
                        this.queryForm.pageNo--
                    }
                    this.fetchData()
                })
            } else {
                this.$baseMessage('请选择要删除的项', 'warning')
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
        // 重置
        rest() {
            this.queryForm = {
                pageNo: 1,
                pageSize: 20,
                dictLabel: '',
                dictType: '',
            }
            this.fetchData()
        },
        async fetchData() {
            this.listLoading = true
            const {data} = await getList(this.queryForm)
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
.app-main-container .app-main-height.roleManagement-container{
    min-height: calc(100vh - 140px);
}
</style>
