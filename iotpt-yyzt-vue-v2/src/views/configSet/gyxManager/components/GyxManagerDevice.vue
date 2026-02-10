<template>
   <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="1200px" @close="close">
        <span class="drawer-title"><i class="el-icon-menu"></i><b>{{ titleName }}</b></span>
        <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
            <el-tab-pane label="采集终端" name="first">
                <el-table v-loading="listLoading1" :data="list1" :element-loading-text="elementLoadingText"
                          height="calc(100vh - 700px)" border>
                    <el-table-column prop="index" label="序号" width="80" align="center">
                        <template #default="{ row, $index }">
                            <span>{{ ($index + 1) + (queryForm1.pageNo - 1) * queryForm1.pageSize }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column show-overflow-tooltip prop="name" label="设备名称" min-width="100"  align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="deviceTypeName" label="类型" width="100" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="subitemName" label="所属分项" min-width="120" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="areaNames" label="物理位置" align="center" width="400"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="logicalAreaNames" label="逻辑位置" align="center">
                    </el-table-column>
                    <el-table-column label="状态" width="100" align="center" prop="status">
                        <template slot-scope="{row}">
                            <el-tag size="mini" type="info" v-if="row.status==1">未使用</el-tag>
                            <el-tag size="mini" type="success" v-if="row.status==2">使用中</el-tag>
                            <el-tag size="mini" type="danger" v-if="row.status==3">损坏</el-tag>
                            <el-tag size="mini" type="warning" v-if="row.status==4">维修中</el-tag>
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination background :current-page="queryForm1.pageNo" :page-size="queryForm1.pageSize" :layout="layout"
                               :total="total1" @size-change="handleSizeChange1"
                               @current-change="handleCurrentChange1"></el-pagination>
            </el-tab-pane>
            <el-tab-pane label="门禁设备" name="second">

                <el-table v-loading="listLoading2" :data="list2" :element-loading-text="elementLoadingText"
                          height="calc(100vh - 700px)" border>
                    <el-table-column prop="index" label="序号" width="80" align="center">
                        <template #default="{ row, $index }">
                            <span>{{ ($index + 1) + (queryForm2.pageNo - 1) * queryForm2.pageSize }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column show-overflow-tooltip prop="name" label="设备名称" width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="deviceCode" label="设备编码" width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="areaName" label="所属区域" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="supplierName" label="供应商" width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="modelName" label="设备型号" width="200" align="center"></el-table-column>
                </el-table>
                <el-pagination background :current-page="queryForm2.pageNo" :page-size="queryForm2.pageSize" :layout="layout"
                               :total="total2" @size-change="handleSizeChange2"
                               @current-change="handleCurrentChange2"></el-pagination>
            </el-tab-pane>
            <el-tab-pane label="视频设备" name="third">
                <el-table v-loading="listLoading3" :data="list3" :element-loading-text="elementLoadingText"
                          height="calc(100vh - 700px)" border>
                    <el-table-column prop="index" label="序号" width="80" align="center">
                        <template #default="{ row, $index }">
                            <span>{{ ($index + 1) + (queryForm3.pageNo - 1) * queryForm3.pageSize }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column show-overflow-tooltip prop="name" label="设备名称" width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="deviceCode" label="设备编码" width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="areaName" label="所属区域" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="supplierName" label="供应商"  width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="modelName" label="设备型号" width="200" align="center"></el-table-column>
                </el-table>
                <el-pagination background :current-page="queryForm3.pageNo" :page-size="queryForm3.pageSize" :layout="layout"
                               :total="total3" @size-change="handleSizeChange3"
                               @current-change="handleCurrentChange3"></el-pagination>
            </el-tab-pane>
            <el-tab-pane label="网关设备" name="fourth">
                <el-table v-loading="listLoading4" :data="list4" :element-loading-text="elementLoadingText"
                          height="calc(100vh - 700px)" border>
                    <el-table-column prop="index" label="序号" width="80" align="center">
                        <template #default="{ row, $index }">
                            <span>{{ ($index + 1) + (queryForm4.pageNo - 1) * queryForm4.pageSize }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column show-overflow-tooltip prop="name" label="设备名称" width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="areaName" label="所属区域" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="supplierName" label="供应商" width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="modelName" label="设备型号" width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="brand" label="设备品牌" width="200" align="center"></el-table-column>
                    <el-table-column show-overflow-tooltip prop="deviceDesc" label="设备描述" align="center"></el-table-column>
                </el-table>
                <el-pagination background :current-page="queryForm4.pageNo" :page-size="queryForm4.pageSize" :layout="layout"
                               :total="total4" @size-change="handleSizeChange4"
                               @current-change="handleCurrentChange4"></el-pagination>
            </el-tab-pane>
        </el-tabs>

        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>

        </div>
    </el-dialog>
</template>

<script>
import {getPageList as getList1} from "@/api/deviceCollect";
import {getPage as getList2} from "@/api/mjsb";
import { getPage as getList3 } from '@/api/spsb'
import { getPageList as getList4 } from '@/api/deviceGateway'

export default {
    name: 'shebeiList',
    data() {
        return {
            loading: false,
            disabled: false,
            activeName: 'first',
            title: '',
            dialogFormVisible: false,
            layout: 'total, sizes, prev, pager, next, jumper',
            total1: 0,
            total2: 0,
            total3: 0,
            total4: 0,
            elementLoadingText: '正在加载...',
            list1: null,
            list2: null,
            list3: null,
            list4: null,
            listLoading1: true,
            listLoading2: true,
            listLoading3: true,
            listLoading4: true,
            queryForm1: {pageNo: 1, pageSize: 10, supplierId: ''},
            queryForm2: {pageNo: 1, pageSize: 10, supplierId: ''},
            queryForm3: {pageNo: 1, pageSize: 10, supplierId: ''},
            queryForm4: {pageNo: 1, pageSize: 10, supplierId: ''},
            titleName: '供应商'
        }
    },
    created() {

    },
    methods: {
        handleClick(tab, event) {
            if (this.activeName == 'first') {
                this.cjzdData();
            } else if (this.activeName == 'second') {
                this.mjsbData();
            } else if (this.activeName == 'third') {
                this.spsbData();
            } else if (this.activeName == 'fourth') {
                this.wgsbData();
            }
        },
        showEdit(row) {
            this.title = '设备信息'
            this.queryForm1.supplierId = row.id
            this.queryForm2.supplierId = row.id
            this.queryForm3.supplierId = row.id
            this.queryForm4.supplierId = row.id
            this.titleName = row.supplierName

            this.cjzdData();
            this.mjsbData();
            this.spsbData();
            this.wgsbData();

            this.dialogFormVisible = true
        },
        close() {
            this.dialogFormVisible = false
        },
        async fetchData() {
            // this.listLoading = true
            // const { data, totalCount } = await getList(this.queryForm)
            // this.list = data
            // this.total = totalCount
            // setTimeout(() => {
            //   this.listLoading = false
            // }, 300)
        },
        handleSizeChange1(val) {this.queryForm1.pageSize = val;this.cjzdData();},
        handleSizeChange2(val) {this.queryForm2.pageSize = val;this.mjsbData();},
        handleSizeChange3(val) {this.queryForm3.pageSize = val;this.spsbData();},
        handleSizeChange4(val) {this.queryForm4.pageSize = val;this.wgsbData();},
        handleCurrentChange1(val) {this.queryForm1.pageNo = val;this.cjzdData();},
        handleCurrentChange2(val) {this.queryForm2.pageNo = val;this.mjsbData();},
        handleCurrentChange3(val) {this.queryForm3.pageNo = val;this.spsbData();},
        handleCurrentChange4(val) {this.queryForm4.pageNo = val;this.wgsbData();},
        queryData() {
            this.queryForm1.pageNo = 1
            this.fetchData()
        },
        // 采集终端
        async cjzdData(){
            this.listLoading1 = true
            const {data} = await getList1(this.queryForm1)
            this.list1 = data.records;
            this.total1 = data.total;
            setTimeout(() => {
                this.listLoading1 = false
            }, 300)
        },
        // 门禁设备
        async mjsbData(){
            this.listLoading2 = true
            getList2(this.queryForm2).then(res => {
                if(0 == res.code){
                    this.list2 = res.data.records;
                    this.total2 = res.data.total
                }else{
                    this.$$baseMessage('查询失败', 'error');
                }
            })
            setTimeout(() => {
                this.listLoading2 = false
            }, 300)
        },
        // 视频设备
        async spsbData(){
            this.listLoading3 = true
            getList3(this.queryForm3).then(res => {
                if(0 == res.code){
                    this.list3 = res.data.records
                    this.total3 = res.data.total
                }
            })

            setTimeout(() => {
                this.listLoading3 = false
            }, 300)
        },
        // 网关设备
        async wgsbData(){
            this.listLoading4 = true
            const { data } = await getList4(this.queryForm4)
            this.list4 =  data.records;
            this.total4 = data.total;
            setTimeout(() => {
                this.listLoading4 = false
            }, 300)
        }


    },
}
</script>
<style scoped lang="scss">
.drawer-title {
    font-size: 16px;
    line-height: 16px;
    color: #334c97;
    display: flex;
    align-items: center;
    margin-bottom: 12px;

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
</style>
