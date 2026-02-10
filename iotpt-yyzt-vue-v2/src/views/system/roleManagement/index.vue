<template>
    <div class="roleManagement-container">
        <!-- <el-divider content-position="left">
          演示环境仅做基础功能展示，若想实现不同角色的真实菜单配置，需将settings.js路由加载模式改为all模式，由后端全面接管路由渲染与权限控制
        </el-divider> -->
        <vab-query-form>
            <vab-query-form-left-panel :span="20">
                <el-form :inline="true" :model="queryForm" @submit.native.prevent>
                    <el-form-item>
                        <el-select v-model="queryForm.systemId" filterable placeholder="所属系统" class="w" clearable>
                            <el-option v-for="item in systematicType" :key="item.id" :label="item.dictLabel" :value="item.dictValue"></el-option>
                        </el-select>
                    </el-form-item>
<!--                    <el-form-item>-->
<!--                        <el-select v-model="queryForm.desc" filterable placeholder="所属楼层" clearable class="w">-->
<!--                            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>-->
<!--                        </el-select>-->
<!--                    </el-form-item>-->
                    <el-form-item>
                        <el-input v-model.trim="queryForm.roleName" placeholder="角色名称" clearable @keyup.enter.native="queryData"/>
                    </el-form-item>
                    <el-form-item>
                        <el-input v-model.trim="queryForm.roleFlag" placeholder="角色标志" clearable @keyup.enter.native="queryData"/>
                    </el-form-item>
                    <el-form-item>
                        <el-button icon="el-icon-search" type="primary" @click="queryData">查询</el-button>
                        <el-button icon="el-icon-refresh" @click="rest">重置</el-button>
                    </el-form-item>
                </el-form>

            </vab-query-form-left-panel>
            <vab-query-form-right-panel :span="4">
                <el-button icon="el-icon-plus" type="success" plain @click="handleEdit">添加</el-button>
                <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
                <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
                <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
                <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
            </vab-query-form-right-panel>
        </vab-query-form>

        <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText" height="calc(100vh - 240px)" border>
            <el-table-column prop="index" label="序号" width="80" align="center">
                <template #default="{ row, $index }">
                    <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
                </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="roleName" label="角色名称" align="center" width="200"></el-table-column>
            <el-table-column show-overflow-tooltip prop="roleFlag" label="角色标志" align="center" width="120"></el-table-column>
            <el-table-column show-overflow-tooltip prop="systemId" label="所属系统" align="center" width="200">
                <template #default="{ row }">
                    <span v-text="selectDictLabel(systematicType, row.systemId)"></span>
                </template>
            </el-table-column>
<!--            <el-table-column show-overflow-tooltip prop="createUserName" label="创建人" width="120" align="center"></el-table-column>-->
<!--            <el-table-column show-overflow-tooltip prop="createTime" label="创建时间" align="center" width="180"></el-table-column>-->
            <el-table-column show-overflow-tooltip prop="updateUserName" label="修改人" width="120" align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="updateTime" label="修改时间" align="center" width="180"></el-table-column>

            <el-table-column show-overflow-tooltip prop="memo" label="备注" align="center" minwidth="160"></el-table-column>
            <el-table-column show-overflow-tooltip label="操作" width="160" align="left" header-align="center">
                 <template #default="{ row }">
                    <!-- <el-link type="success" :underline="false" @click="handleEdit(row)">编辑</el-link>
                    <el-link type="danger" :underline="false" @click="handleDelete(row)">删除</el-link> -->

                    <el-button icon="el-icon-edit"  type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
                    <el-button icon="el-icon-delete" type="danger" size="mini" v-if="!['zhyyadmin','yyadmin'].includes(row.roleFlag)"  @click="handleDelete(row)">删除</el-button>

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
import {getPageList, doDelete, getRoleUser} from '@/api/roleManagement'
import Edit from './components/RoleManagementEdit'
import {selectDictLabel} from "@/utils/tools";

export default {
    name: 'RoleManagement',
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
                roleName: '',
                roleFlag: '',
                systemId: '',
            },
            options: [],
            systematicType: [],
        }
    },
    created() {
        this.getDictByKey().then(res => {
            this.systematicType = res.data['systematic_type']
        }).then(() => {
            this.fetchData()
        })
        // this.fetchData()
    },
    methods: {
        selectDictLabel,
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
        handleDelete(row) {
            if (row.id) {
                // 查询角色下面的用户
                getRoleUser({role_id: row.id}).then(res => {
                    if (res.data.length > 0) {
                        let msg = '';
                        res.data.forEach(item => {
                            msg += item.realName + '、'
                        })
                        msg = msg.substring(0, msg.length - 1)
                        this.$baseMessage('该角色下面有用户(' + msg + ')，不能删除 ! ', 'error')
                    } else {
                        this.$baseConfirm('你确定要删除当前项吗', null, async () => {
                            let listLength = this.list.length
                            const {msg} = await doDelete({id: row.id})
                            this.$baseMessage("删除成功", 'success')
                            if (listLength === 1 && this.queryForm.pageNo > 1) {
                                this.queryForm.pageNo--
                            }
                            this.fetchData()
                        })
                    }
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
            this.queryForm = {
                pageNo: 1,
                pageSize: 20,
                roleName: '',
                roleFlag: '',
                systemId: '',
            }
            this.queryData()
        },
    },
}
</script>
