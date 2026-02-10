<template>
    <el-dialog :title="title" :visible.sync="dialogFormVisible" width="600px" @close="close" append-to-body>
        <vab-query-form>
            <el-form :inline="true" :model="queryForm">
                <vab-query-form-left-panel :span="24">                   
                    <el-form-item label="机构名称">
                        <el-input v-model.trim="queryForm.medins_name" clearable />
                    </el-form-item>
                    <el-form-item>
                        <el-button icon="el-icon-search" type="primary" @click="queryData">查 询</el-button>
                        <el-button icon="el-icon-refresh" @click="reseat()">重 置</el-button>                        
                    </el-form-item>

                </vab-query-form-left-panel>
                <!-- <vab-query-form-right-panel :span="4">
                    
                </vab-query-form-right-panel> -->
            </el-form>
        </vab-query-form>
        <el-table ref="listTable" :data="list" :element-loading-text="elementLoadingText" height="260px" v-loading="listLoading" border>
            <template slot="empty">
                <el-empty :image-size="150"></el-empty>
            </template>
            <el-table-column label="选择" width="80" align="center">
                <template slot-scope="scope">
                    <el-radio :label="scope.row.id" v-model="id"><span></span></el-radio>
                </template>
            </el-table-column>
            <el-table-column label="序号" width="80" align="center" show-overflow-tooltip prop="id"><template
                    slot-scope="scope">
                    <span v-text="getIndex(scope.$index)"> </span>
                </template></el-table-column>           
            <el-table-column label="部门名称" align="center" show-overflow-tooltip prop="username"></el-table-column>           
        </el-table>

        <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
            :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange2"></el-pagination>

        <div slot="footer" class="dialog-footer">
            <el-button @click="close">关 闭</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import { getList, doDelete } from '@/api/userManagement'
export default {
    name: 'bumen',
    components: {},
    data() {
        return {
            currentRow: null,
            id: '',
            tableData: [],
            title: '',
            dialogFormVisible: false,
            value1: '',
            checked: false,
            isShow: false,
            list: [],
            listLoading: true,
            layout: 'total, sizes, prev, pager, next, jumper',
            total: 0,
            selectRows: '',
            elementLoadingText: '正在加载...',
            queryForm: {
                pageNo: 1,
                pageSize: 10,
                medins_code: '',
                medins_name: '',
                fixmedins_type: '',
            },
        }
    },
    created() {
        this.fetchData()
    },
    porps: {
        //父组件传递过来的初始选中值，根据自己项目需求设置
        chooseData: {
            type: Object,
        },
    },
    watch: {
        //观察是否有父组件传递的初始值或者变化，重新选中
        chooseData(val) {
            if (val) {
                this.radio = false
                this.getInitChoose()
            }
        },
    },
    beforeDestroy() { },
    mounted() { },
    methods: {
        handleCurrentChange(val) {
            this.id = val.medins_code;
            this.selectRows = val
        },
        handleSizeChange(val) {
            this.queryForm.pageSize = val
            this.fetchData()
        },
        getCurrentRow(index) {
            this.radio = index
        },
        handleCurrentChange2(val) {
            this.queryForm.pageNo = val
            this.fetchData()
        },
        queryData() {
            this.queryForm.pageNo = 1
            this.fetchData()
        },
        moreQuery() {
            this.isShow = !this.isShow
        },
        //设置单选框选择状态
        getInitChoose() {
            if (this.chooseData) {
                let index = this.tableData.findIndex(
                    (item) => item.userUuid == this.chooseData.id
                )
            }
            if (index > -1) {
                this.radio = index
            }
        },
        async fetchData() {
            this.listLoading = true
            const { data, totalCount } = await getList(this.queryForm)
            this.list = data
            this.total = totalCount
            setTimeout(() => {
                this.listLoading = false
            }, 300)
        },
        showDia(row) {
            this.list = [];
            this.total = 0;
            this.queryForm.fixmedins_type = row;
            this.queryForm.medins_code = "";
            this.queryForm.medins_name = "";
            if (!row) {
                this.title = '组织机构查询'

            } else {
                this.title = '查询'
                this.form = Object.assign({}, row)
            }
            this.fetchData()
            this.dialogFormVisible = true
        },
        close() {
            // this.$refs['form'].resetFields()
            // this.form = this.$options.data().form
            this.dialogFormVisible = false
        },
        save() {
            var that = this;
            that.$emit('fetch-data', that.selectRows)
            that.close()
        },
        //获取表格序号
        getIndex($index) {
            //表格序号
            return (this.queryForm.pageNo - 1) * this.queryForm.pageSize + $index + 1
        },
        reseat() {
            this.queryForm.medins_code = "";
            this.queryForm.medins_name = "";
        }
    },
}
</script>
<style lang="scss" scoped>
::v-deep {
    .el-radio__label {
        display: none;
    }
}
</style>