<template>
    <div class="index-container">

        <vab-query-form>
            <vab-query-form-left-panel :span="20">
                <el-form :inline="true" :model="queryForm" @submit.native.prevent ref="queryForm">
                    <el-form-item prop="linkName">
                        <el-input v-model.trim="queryForm.linkName" placeholder="请输入连接名称" clearable
                                  @keyup.enter.native="queryData"/>
                    </el-form-item>
                    <el-form-item prop="protocolId">
                        <el-select v-model="queryForm.protocolId" placeholder="请选择传输协议" clearable>
                            <el-option
                                v-for="item in this.protocolList"
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
            <el-table-column show-overflow-tooltip prop="linkName" label="连接名称" width="200"
                             align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="protocolName" label="连接协议" width="200"
                             align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="linkDesc" label="连接描述" align="center"></el-table-column>
            <el-table-column label="操作" width="500" align="center">
                <template #default="{ row }">
                    <el-button
                        size="mini"
                        :icon="row.connectionStatus == 2 ? 'el-icon-warning' : 'el-icon-success'"
                        :type="row.connectionStatus == 2 ? 'danger' : 'success'" @click="changeHidden(row)">
                        {{ row.connectionStatus == 2 ? "断开连接" : "建立连接" }}
                    </el-button>
                    <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
                    <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>
                    <el-link :underline="false" type="primary" @click="handleView(row)">详情</el-link>
                    <el-link :underline="false" type="primary" @click="titleManagerView(row)">主题信息</el-link> -->

                    <el-button icon="el-icon-edit" type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
                    <el-button icon="el-icon-delete" type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
                    <el-button icon="el-icon-info" type="primary" size="mini" @click="handleView(row)">详情</el-button>
                    <el-button icon="el-icon-view" type="primary" size="mini" @click="titleManagerView(row)">主题信息
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
                       :total="total" @size-change="handleSizeChange"
                       @current-change="handleCurrentChange"></el-pagination>
        <edit ref="edit" @fetch-data="fetchData"></edit>
        <managerView ref="managerView" @fetch-data="fetchData"></managerView>
        <titleManager ref="titleManager" @fetch-data="fetchData"></titleManager>
    </div>
</template>

<script>
    // 连接管理

    import {getRedisDictList} from '@/api/sysDict'
    import {getPageList, saveOrUpdate, addMqttConnect, removeMqttConnect} from '@/api/configLink'
    import Edit from './components/LianjieManagerEdit'
    import ManagerView from './components/LianjieManagerView'
    import titleManager from './components/titleManagerView'

    export default {
        name: 'lianjieManager',
        components: {Edit, ManagerView, titleManager},
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
                    linkName: '',
                    protocolId: '',
                },
                protocolList: [],
            }
        },
        created() {
            //传输协议列表
            this.protocolListData();
            this.fetchData()
        },
        methods: {
            changeHidden(row) {
                if (row.connectionStatus == 1) {
                    // 弹窗是否确定求改显示
                    this.$baseConfirm('你确定要建立连接', null, async () => {
                        const data = await addMqttConnect({id: row.id});
                        if (data.code == 0) {
                            this.$baseMessage('已建立连接', 'success');
                        } else {
                            this.$baseMessage(data.msg, 'error');
                        }
                        this.fetchData()
                    })
                } else {
                    // 弹窗是否确定求改显示
                    this.$baseConfirm('你确定要断开连接', null, async () => {
                        const data = await removeMqttConnect({id: row.id});
                        if (data.code == 0) {
                            this.$baseMessage('已断开连接', 'success');
                        } else {
                            this.$baseMessage(data.msg, 'error');
                        }

                        this.fetchData()
                    })
                }
            },
            ShowSearch() {
                this.searchShow = !this.searchShow
            },
            refresh() {
                location.reload();
            },
            /** 重置按钮操作 */
            resetQuery() {
                this.$refs.queryForm.resetFields();
                this.queryData();
            },
            setSelectRows(val) {
                this.selectRows = val
            },
            handleEdit(row) {
                if (row.id) {
                    this.$refs['edit'].showEdit(row.id)
                } else {
                    this.$refs['edit'].showEdit()
                }
            },
            handleDelete(row) {
                if (row.id) {
                    if (row.connectionStatus == 2) {
                        this.$baseMessage("请先断开连接再删除", 'error')
                        return
                    }
                    this.$baseConfirm('你确定要删除当前项吗', null, async () => {
                        const {msg} = await saveOrUpdate({id: row.id, isDel: 1})
                        this.$baseMessage("删除成功", 'success')
                        this.fetchData()
                    })
                } else {
                    // if (this.selectRows.length > 0) {
                    //   const ids = this.selectRows.map((item) => item.id).join()
                    //   this.$baseConfirm('你确定要删除选中项吗', null, async () => {
                    //     const {msg} = await doDelete({ids})
                    //     this.$baseMessage(msg, 'success')
                    //     this.fetchData()
                    //   })
                    // } else {
                    //   this.$baseMessage('未选中任何行', 'error')
                    //   return false
                    // }
                }
            },
            handleView(row) {
                this.$refs['managerView'].showEdit(row.id)
            },
            titleManagerView(row) {
                this.$refs['titleManager'].showEdit(row.id)
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
            async protocolListData() {
                const {data} = await getRedisDictList({dictType: "config_protocol"})
                console.log(data)
                this.protocolList = data;
            },
            async fetchData() {
                this.listLoading = true
                const {data} = await getPageList(this.queryForm)
                this.list = data.records;
                this.total = data.total;
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
