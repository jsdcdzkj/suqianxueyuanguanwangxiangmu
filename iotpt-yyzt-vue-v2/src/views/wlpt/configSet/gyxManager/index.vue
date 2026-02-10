<template>
    <div class="index-container">

        <vab-query-form>
            <vab-query-form-left-panel :span="20">
                <el-form :inline="true" :model="queryForm" @submit.native.prevent>
                    <el-form-item>
                        <el-input v-model.trim="queryForm.supplierName" placeholder="请输入供应商名称" clearable @keyup.enter.native="queryData"/>
                    </el-form-item>
                    <el-form-item>
                        <el-select v-model="queryForm.supplierType" class="w" filterable placeholder="供应商类型" clearable>
                            <el-option v-for="item in supplierTypes" :key="item.id" :label="item.dictLabel" :value="item.dictValue"></el-option>
                        </el-select>
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
                  @selection-change="setSelectRows" height="calc(100vh - 240px)" border>
            <el-table-column prop="index" label="序号" width="80" align="center">
                <template #default="{ row, $index }">
                    <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
                </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="supplierName" label="供应商名称" width="200"
                             align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="supplierName" label="供应商类型" width="200" align="center">
                <template #default="{ row }">
                    <span v-text="selectDictLabel(supplierTypes, row.supplierType)"></span>
                </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="contacts" label="联系人" width="200"
                             align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="telephone" label="联系电话" width="200"
                             align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="illustrate" label="描述" align="center"></el-table-column>
            <el-table-column label="操作" width="300" align="center">
                <template #default="{ row }">
                    <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
                    <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link> -->
                    <el-button icon="el-icon-edit"  type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
                    <el-button icon="el-icon-delete" type="danger" size="mini"  @click="handleDelete(row)">删除</el-button>

                    <!-- <el-button size="mini" type="primary"> -->
                        <el-dropdown trigger="click">
                            <el-button type="primary" size="mini"> 更多菜单<i class="el-icon-arrow-down el-icon--right"></i></el-button>
                        <!-- <el-link :underline="false" type="success" style="color: #fff;text-align: center">更多<i class="el-icon-arrow-down el-icon--right"></i> -->
                        <!-- </el-link> -->
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item>
                                <el-link :underline="false" type="primary" @click="handleView(row)"
                                         style="width: 100%;">详情
                                </el-link>
                            </el-dropdown-item>
                            <el-dropdown-item divided>
                                <el-link :underline="false" type="primary" @click="handleModel(row)"
                                         style="width: 100%;">型号信息
                                </el-link>
                            </el-dropdown-item>
                            <el-dropdown-item divided>
                                <el-link :underline="false" type="primary" @click="handleDetail(row)"
                                         style="width: 100%;">设备信息
                                </el-link>
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                    <!-- </el-button> -->
                </template>
            </el-table-column>
        </el-table>
        <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
                       :total="total" @size-change="handleSizeChange"
                       @current-change="handleCurrentChange"></el-pagination>
        <edit ref="edit" @fetch-data="fetchData"></edit>
        <device ref="device" @fetch-data="fetchData"></device>
        <modelModel ref="modelModel" @fetch-data="fetchData"></modelModel>
        <managerView ref="managerView" @fetch-data="fetchData"></managerView>
    </div>
</template>

<script>
import {getPageList, onDel} from '@/api/configSupplier'
import Edit from './components/GyxManagerEdit'
import Device from './components/GyxManagerDevice'
import modelModel from './components/GyxManagerModel'
import ManagerView from './components/GyxManagerView'
import {getTypeAll} from "@/api/log";
export default {
    name: 'configSupplier',
    components: { Edit, Device, modelModel, ManagerView },
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
                pageSize: 10,
                supplierName: '',
                supplierType: '',
            },
            supplierTypes: [],
        }
    },
    created() {
        this.getDictByKey().then(res => {
            this.supplierTypes = res.data['supplierType']
        }).then(() => {
            this.fetchData()
        })
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
        handleEdit(row) {
            if (row.id) {
                this.$refs['edit'].showEdit(row)
            } else {
                this.$refs['edit'].showEdit()
            }
        },
        handleView(row) {
            this.$refs['managerView'].showEdit(row)
        },
        handleDetail(row) {
            if (row.id) {
                this.$refs['device'].showEdit(row)
            } else {
                this.$refs['device'].showEdit()
            }
        },
        handleModel(row) {
            if (row.id) {
                this.$refs['modelModel'].showEdit(row)
            }
        },
        handleDelete(row) {
            if (row.id) {
                this.$baseConfirm('你确定要删除当前项吗', null, async () => {
                    let listLength = this.list.length
                    const {msg} = await onDel({id: row.id})
                    if (listLength === 1 && this.queryForm.pageNo > 1) {
                        this.queryForm.pageNo--
                    }
                    this.$baseMessage(msg, 'success')
                    this.queryData()
                })
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
            console.log(data)
            this.list = data.records
            this.total = data.total
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
        rest() {
            this.queryForm = {
                pageNo: 1,
                pageSize: 10,
                supplierName: '',
            }
            this.fetchData()
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
