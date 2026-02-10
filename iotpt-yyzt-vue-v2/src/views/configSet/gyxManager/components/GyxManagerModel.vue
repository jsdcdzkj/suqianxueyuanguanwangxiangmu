<template>
    <el-drawer :wrapperClosable="false"  :visible.sync="dialogFormVisible" :before-close="close" append-to-body size="70%"
               custom-class="self-class">
        <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>型号管理</b></span>
        <div style="padding:0 20px">
            <vab-query-form>
                <el-button icon="el-icon-plus" type="success" @click="handleEdit" style="float: right;">添加</el-button>
            </vab-query-form>

            <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
                      @selection-change="setSelectRows" height="calc(100vh - 240px)" border>
<!--                <el-table-column show-overflow-tooltip type="selection" width="60" align="center"></el-table-column>-->
                <el-table-column prop="index" label="序号" width="80" align="center">
                    <template #default="{ row, $index }">
                        <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
                    </template>
                </el-table-column>
                <el-table-column show-overflow-tooltip prop="modelName" label="型号名称" width="120"
                                 align="center"></el-table-column>
                <el-table-column show-overflow-tooltip prop="email" label="所属供应商" align="center" width="200">
                    <template #default="{ row }">
                        <span>{{ row.supplier.supplierName }}</span>
                    </template>
                </el-table-column>
                <el-table-column show-overflow-tooltip prop="illustrate" label="型号说明" align="center"></el-table-column>
                <el-table-column label="操作" width="160" align="center">
                    <template #default="{ row }">
                        <!-- <el-link type="success" @click="handleEdit(row)" :underline="false">编辑</el-link> -->
                        <!-- <el-link type="danger" @click="handleDelete(row)" :underline="false">删除</el-link> -->
                        <el-button icon="el-icon-edit"  type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
                        <el-button icon="el-icon-delete" type="danger" size="mini"  @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
                           :total="total" @size-change="handleSizeChange"
                           @current-change="handleCurrentChange"></el-pagination>

            <edit ref="edit" @fetch-data="fetchData"></edit>
        </div>
        <div class="footer-btn">
            <el-button @click="close">取 消</el-button>
<!--            <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{-->
<!--                    loading ? '确定中 ...' : '确定'-->
<!--                }}-->
<!--            </el-button>-->
        </div>
    </el-drawer>
</template>

<script>
import {getModelList} from '@/api/configSupplier'
import {onDel} from '@/api/configModel'
import Edit from './XinghaoManagerEdit'

export default {
    name: 'UserManagementEdit',
    components: {Edit},
    data() {
        return {
            loading: false,
            disabled: false,
            form: {
                username: '',
                password: '',
                email: '',
                permissions: [],
                org_code: '',
                org_name: '',
            },
            rules: {
                username: [
                    {required: true, trigger: 'blur', message: '请输入用户名'},
                ],
                password: [
                    {required: true, trigger: 'blur', message: '请输入密码'},
                ],
                org_code: [
                    {required: true, trigger: 'submit', message: '请选择所属机构'},
                ],
                checkedRoles: [
                    {required: true, trigger: 'blur', message: '请选择所属机构'},
                ],
            },
            title: '',
            dialogFormVisible: false,
            searchShow: false,
            list: [],
            listLoading: true,
            layout: 'total, sizes, prev, pager, next, jumper',
            total: 0,
            selectRows: '',
            elementLoadingText: '正在加载...',
            queryForm: {
                pageNo: 1,
                pageSize: 10,
                configSupplierId: '',
            },
        }
    },
    mounted() {
        // this.fetchData()
    },
    methods: {
        showEdit(row) {
            if (!row) {
                this.title = '添加'
            } else {
                this.title = '编辑'
                this.form = Object.assign({}, row)
                this.queryForm.configSupplierId = row.id;
                this.fetchData()
            }
            this.dialogFormVisible = true
        },
        close() {
            // this.$refs['form'].resetFields()
            // this.form = this.$options.data().form
            this.dialogFormVisible = false
        },
        // save() {
        //     if (this.loading) {
        //         return
        //     }
        //     this.$refs['form'].validate(async (valid) => {
        //         if (valid) {
        //             this.loading = true
        //             this.disabled = true
        //             setTimeout(() => {
        //                 this.loading = false
        //                 this.disabled = false
        //             }, 1000)
        //
        //             const {msg} = await doEdit(this.form)
        //             this.$baseMessage(msg, 'success')
        //             this.$emit('fetch-data')
        //             this.close()
        //         } else {
        //             return false
        //         }
        //     })
        // },
        handleEdit(row) {
            row.supplierId = this.form.id
            this.$refs['edit'].showEdit(row)
        },
        ShowSearch() {
            this.searchShow = !this.searchShow
        },
        refresh() {
            // location.reload();
        },
        setSelectRows(val) {
            this.selectRows = val
        },
        handleDelete(row) {
            if (row.id) {
                this.$baseConfirm('你确定要删除当前项吗', null, async () => {
                    const {msg} = await onDel({id: row.id})
                    this.$baseMessage(msg, 'success')
                    this.fetchData()
                })
            } else {
                this.$baseMessage('当前项不存在', 'error')
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
            const {data, totalCount} = await getModelList(this.queryForm)
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
    },
}
</script>
<style scoped lang="scss">
::v-deep {
    .el-drawer__header {
        padding: 10px !important;
        margin-bottom: 14px;
        border-bottom: 1px solid #d1d9e1;

        .drawer-title {
            font-size: 16px;
            line-height: 16px;
            color: #334c97;
            display: flex;
            align-items: center;

            i {
                display: block;
                font-size: 18px;
                line-height: 18px;
                margin-right: 4px;
            }

            b {
                display: block;
                font-size: 16px;
                line-height: 16px;
                margin-top: 2px;
                margin-right: 4px;
            }
        }
    }
}

.footer-btn {
    padding: 12px 20px;
    border-top: 1px solid #efefef;
    position: absolute;
    bottom: 0;
    text-align: right;
    width: 100%;
    background-color: #fff;
    z-index: 999;
}
</style>
