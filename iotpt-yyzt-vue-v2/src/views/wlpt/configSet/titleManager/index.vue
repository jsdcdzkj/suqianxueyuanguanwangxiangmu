<template>
    <div class="index-container title-manager-class">

        <vab-query-form>
            <vab-query-form-left-panel :span="16">
                <el-form :inline="true" :model="queryForm" @submit.native.prevent ref="queryForm">
                    <el-form-item prop="topicCode">
                        <el-input v-model.trim="queryForm.topicCode" placeholder="请输入主题名称" clearable
                                  @keyup.enter.native="queryData"/>
                    </el-form-item>
                    <el-form-item prop="topicType">
                        <el-select v-model="queryForm.topicType" placeholder="请选择主题类型" clearable>
                            <el-option key="1" label="订阅主题" value="1"></el-option>
                            <el-option key="2" label="发布主题" value="2"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item prop="linkId">
                        <el-select v-model="queryForm.linkId" placeholder="请选择连接地址" clearable>
                            <el-option
                                v-for="item in this.linkList"
                                :key="item.id"
                                :label="item.linkName"
                                :value="item.id"
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
            <vab-query-form-right-panel :span="8">
                <el-button icon="el-icon-caret-right" type="primary" plain @click="runStarts">批量启用</el-button>
                <el-button icon="el-icon-circle-close" type="danger" plain @click="runStops">批量停用</el-button>
                <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">添加</el-button>
                <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
                <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
                <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
                <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
            </vab-query-form-right-panel>
        </vab-query-form>

        <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
                  highlight-current-row stripe
                  @selection-change="setSelectRows" height="calc(100vh - 240px)" border>
            <el-table-column
                show-overflow-tooltip
                type="selection"
                width="55">
            </el-table-column>
            <el-table-column prop="index" label="序号" width="80" align="center">
                <template #default="{ row, $index }">
                    <span>{{ ($index + 1) + (queryForm.pageNo - 1) * queryForm.pageSize }}</span>
                </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="topicCode" label="主题名称" width="200"
                             align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="topicName" label="主题Topic" width="200"
                             align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="topicType" label="主题类型" width="200" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.topicType === 1">订阅主题</span>
                    <span v-else-if="scope.row.topicType === 2">发布主题</span>
                </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="linkName" label="连接地址" width="200"
                             align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="topicDesc" label="主题描述" align="center"></el-table-column>
            <el-table-column :key="Math.random()" align="center" label="主题状态" prop="isEnable" width="100">
                <template slot-scope="{row}">
                    <el-link
                        :icon="row.topicStatus == 0 ? 'el-icon-warning' : 'el-icon-success'"
                        :type="row.topicStatus == 0 ? 'danger' : 'success'" @click="changeHidden(row)">
                        {{ row.topicStatus == 0 ? "停用" : "启用" }}
                    </el-link>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="300" align="center">
                <template #default="{ row }">
                    <!-- <el-link :icon="row.topicStatus == 1 ? 'el-icon-warning' : 'el-icon-success'"
                             :type="row.topicStatus == 1 ? 'danger' : 'success'" @click="changeHidden(row)">
                      {{ row.topicStatus == 1 ? "停用" : "启用" }}
                    </el-link>
                    <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
                    <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>
                    <el-link :underline="false" type="primary" @click="handleView(row)">数据转换配置</el-link> -->


                    <el-button icon="el-icon-edit" type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
                    <el-button icon="el-icon-delete" type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
                    <el-button icon="el-icon-info" type="primary" size="mini" @click="handleView(row)">数据转换配置
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
                       :total="total" @size-change="handleSizeChange"
                       @current-change="handleCurrentChange"></el-pagination>
        <edit ref="edit" @fetch-data="fetchData"></edit>
        <titleManagerView ref="titleManagerView" @fetch-data="fetchData"></titleManagerView>
        <ConfigurationTemplate ref="ConfigurationTemplate" @fetch-data="fetchData"></ConfigurationTemplate>
        <fbEdit ref="fbEdit" @fetch-data="fetchData"></fbEdit>
    </div>
</template>

<script>
    // 主题管理

    import {getPageList, saveOrUpdate, addTopic, removeTopic, runStops, runStarts} from '@/api/configTopic'
    import {getList} from '@/api/configLink'
    import Edit from './components/titleManagerEdit'
    import TitleManagerView from '../dataManager/components/DataManagerEdit/index'

    import fbEdit from '../dataManager/components/DataManagerFbEdit'

    import ConfigurationTemplate from './components/ConfigurationTemplate'

    export default {
        name: 'titleManager',
        components: {Edit, TitleManagerView, ConfigurationTemplate, fbEdit},
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
                    topicName: '',
                    topicCode: '',
                    topicType: '',
                },
                linkList: [],
            }
        },
        created() {
            //连接列表
            this.linkListData();
            this.fetchData()
        },
        methods: {
            changeHidden(row) {
                var topicStatus = row.topicStatus;
                if (topicStatus == 0) {
                    // if (row.transferId == '' || row.transferId == null || row.transferId == undefined) {
                    //   this.$baseMessage('请先关联数据转换配置', 'error');
                    //   return
                    // }
                    // 弹窗是否确定求改显示
                    this.$baseConfirm('你确定要启用', null, async () => {
                        const data = await addTopic({id: row.id, linkId: row.linkId});
                        if (data.code == 0) {
                            this.$baseMessage('已启用', 'success');
                        } else {
                            this.$baseMessage(data.msg, 'error');
                        }

                        this.fetchData()
                    })
                } else {
                    // 弹窗是否确定求改显示
                    this.$baseConfirm('你确定要停用', null, async () => {
                        const data = await removeTopic({id: row.id, linkId: row.linkId});
                        if (data.code == 0) {
                            this.$baseMessage('已停用', 'success');
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
                    this.$baseConfirm('你确定要删除当前项吗', null, async () => {
                        const data = await saveOrUpdate({id: row.id, isDel: 1})
                        if (data.code == 0) {
                            this.$baseMessage("删除成功", 'success')
                        } else {
                            this.$baseMessage(data.msg, 'error')
                        }
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
            runStops() {
                if (this.selectRows.length > 0) {
                    const ids = this.selectRows.map((item) => item.id).join()
                    const linkIds = this.selectRows.map((item) => item.linkId).join()
                    this.$baseConfirm('你确定要停用选中项吗', null, async () => {
                        const {msg} = await runStops({idList: ids, linkIdList: linkIds})
                        this.$baseMessage(msg, 'success')
                        this.fetchData()
                    })
                } else {
                    this.$baseMessage('未选中任何行', 'error')
                    return false
                }
            },
            runStarts() {
                if (this.selectRows.length > 0) {

                    // var msg = ""
                    // for (var i = 0; i < this.selectRows.length; i++) {
                    //   if (this.selectRows[i].transferId == '' || this.selectRows[i].transferId == null || this.selectRows[i].transferId == undefined) {
                    //     msg += this.selectRows[i].topicName + ",";
                    //   }
                    // }
                    // if (msg != "") {
                    //   msg = msg.substring(0, msg.lastIndexOf(","));
                    //   this.$baseMessage(msg + ' 未关联数据转换配置，请先关联数据转换配置', 'error');
                    //   return
                    // }

                    const ids = this.selectRows.map((item) => item.id).join()
                    const linkIds = this.selectRows.map((item) => item.linkId).join()
                    this.$baseConfirm('你确定要启用选中项吗', null, async () => {
                        const {msg} = await runStarts({idList: ids, linkIdList: linkIds})
                        this.$baseMessage(msg, 'success')
                        this.fetchData()
                    })
                } else {
                    this.$baseMessage('未选中任何行', 'error')
                    return false
                }
            },
            handleView(row) {
                this.$refs['ConfigurationTemplate'].showEdit(row)
            },
            //新增模板弹窗
            openManagerView(id) {
                console.log("id", id)
                this.$refs['titleManagerView'].showEdit2(id)
            },
            openDataManagerFbEdit(id) {
                console.log("id", id)
                this.$refs['fbEdit'].showEdit2(id)
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
            async linkListData() {
                const {data} = await getList()
                console.log(data)
                this.linkList = data;
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
<style lang="scss">
    .title-manager-class tbody .el-table__cell:first-child .cell {
        padding-left: 15px !important;
    }
</style>
