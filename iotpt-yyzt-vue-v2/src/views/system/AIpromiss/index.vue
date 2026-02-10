<template>
    <div class="menuManagement-container">
        <!-- <el-divider content-position="left">
              演示环境仅做基础功能展示，若想实现不同角色的真实菜单配置，需将settings.js路由加载模式改为all模式，由后端全面接管路由渲染与权限控制
            </el-divider> -->
        <el-row>
            <!-- <el-col :xs="24" :sm="24" :md="8" :lg="4" :xl="4">
                    <el-tree
                      :data="data"
                      :props="defaultProps"
                      node-key="id"
                      :default-expanded-keys="['root']"
                      @node-click="handleNodeClick"
                    ></el-tree>
                  </el-col> -->
            <el-col :lg="24" :md="24" :sm="24" :xl="24" :xs="24">
                <vab-query-form>
                    <vab-query-form-left-panel :span="20">
                        <el-form :inline="true" :model="queryForm" @submit.native.prevent>
                            <el-form-item>
                                <el-input v-model.trim="queryForm.title" clearable placeholder="菜单名称"
                                          @keyup.enter.native="queryData"/>
                            </el-form-item>
                            <el-form-item>
                                <el-button icon="el-icon-search" type="primary" @click="queryData">查询</el-button>
                                <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
                            </el-form-item>
                        </el-form>

                    </vab-query-form-left-panel>
                    <vab-query-form-right-panel :span="4">
                        <el-button icon="el-icon-plus" plain type="success" @click="handleEdit()">添加</el-button>
                        <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
                                    <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
                        <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
                                    <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
                    </vab-query-form-right-panel>
                </vab-query-form>

<!--                <el-tabs v-model="activeName" @tab-click="handleClick">-->
<!--                    <el-tab-pane v-for="item in systematicType" :key="item.id" :label="item.dictLabel"-->
<!--                                 :name="item.dictValue"></el-tab-pane>-->
<!--                </el-tabs>-->
                <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
                          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" border default-expand-all
                          row-key="id">
                    <el-table-column label="菜单名称" prop="title" show-overflow-tooltip></el-table-column>


                    <el-table-column :key="Math.random()" align="center" label="是否显示" prop="hidden" width="100">
                        <template slot-scope="scope">
                            <el-link :icon="scope.row.isShow == 0 ? 'el-icon-warning' : 'el-icon-success'"
                                     :type="scope.row.isShow == 0 ? 'danger' : 'success'"
                                     @click="changeHidden(scope.row)">{{ scope.row.isShow == 0 ? "隐藏" : "显示" }}
                            </el-link>
                        </template>
                    </el-table-column>

                    <el-table-column align="center" label="排序" prop="sort" show-overflow-tooltip width="80"></el-table-column>
                    <!-- <el-table-column show-overflow-tooltip label="是否一直显示当前节点">
                            <template #default="{ row }">
                              <span>
                                {{ row.alwaysShow ? '是' : '否' }}
                              </span>
                            </template>
                          </el-table-column>
                          <el-table-column show-overflow-tooltip label="是否固定">
                            <template #default="{ row }">
                              <span v-if="row.meta">
                                {{ row.meta.affix ? '是' : '否' }}
                              </span>
                            </template>
                          </el-table-column>
                          <el-table-column show-overflow-tooltip label="是否无缓存">
                            <template #default="{ row }">
                              <span v-if="row.meta">
                                {{ row.meta.noKeepAlive ? '是' : '否' }}
                              </span>
                            </template>
                          </el-table-column>
                          <el-table-column show-overflow-tooltip label="badge">
                            <template #default="{ row }">
                              <span v-if="row.meta">
                                {{ row.meta.badge }}
                              </span>
                            </template>
                          </el-table-column> -->
                    <el-table-column align="center" label="操作" show-overflow-tooltip width="160">
                        <template #default="{ row }">
                            <!-- <el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>
                                      <el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link> -->
                            <el-button icon="el-icon-edit" type="success" size="mini" @click="handleEdit(row)">编辑
                            </el-button>
                            <el-button icon="el-icon-delete" type="danger" size="mini" @click="handleDelete(row)">删除
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-col>
        </el-row>

        <edit ref="edit" @fetch-data="fetchData"></edit>
    </div>
</template>

<script>
import {doDelete, getMenuTree, doChangeStatus} from '@/api/aiPromiss'
import Edit from './components/MenuManagementEdit'
import {selectDictLabel} from '@/utils/tools'

export default {
    name: 'MenuManagement',
    components: {Edit},
    inject: ['reload'],
    data() {
        return {
            activeName: '',
            data: [],
            defaultProps: {
                children: 'children',
                label: 'label',
            },
            list: [],
            listLoading: true,
            elementLoadingText: '正在加载...',
            systematicType: [],
            queryForm: {
                title: '',
                systemId:''
            },
            options: [
                {value: '选项1', label: '应用名称1'},
                {value: '选项2', label: '应用名称2'},
                {value: '选项3', label: '应用名称3'},
                {value: '选项4', label: '应用名称4'},
                {value: '选项5', label: '应用名称5'},
            ],
        }
    },
    async created() {
        this.fetchData()
        // const roleData = await getTree()
        // this.data = roleData.data
        // this.getDictByKey()
        //     .then((res) => {
        //         this.systematicType = res.data['systematic_type']
        //         console.log('systematicType', this.systematicType)
        //         this.activeName = this.systematicType[1].dictValue
        //         this.queryForm.systemId = this.activeName
        //     })
        //     .then(() => {
        //         this.fetchData()
        //     })
    },
    methods: {
        // selectDictLabel,
        // handleClick(tab, event) {
        //     console.log(tab, event);
        //     console.log('tab', this.activeName);
        //     this.queryForm.systemId = this.activeName
        //     this.fetchData()
        // },
        handleEdit(row) {
            if (row) {
                this.$refs['edit'].showEdit(row, this.activeName)
            } else {
                this.$refs['edit'].showEdit('', this.activeName)
            }
        },
        changeHidden(row) {
            var isShow = row.isShow
            // 弹窗是否确定求改显示
            this.$baseConfirm(
                '你确定要' + (isShow == 1 ? '隐藏' : '显示') + '当前菜单吗',
                null,
                async () => {
                    const {msg} = await doChangeStatus({
                        id: row.id,
                        isShow: isShow == 1 ? 0 : 1,
                    })
                    this.$baseMessage('已' + (isShow == 1 ? '隐藏' : '显示'), 'success')
                    const permissions = await this.$store.dispatch('user/getUserInfo')
                    this.$store.dispatch('routes/setRoutes', permissions)
                    this.fetchData()
                }
            )
        },
        handleDelete(row) {
            if (row.id) {
                this.$baseConfirm('你确定要删除当前项吗', null, async () => {
                    const {msg} = await doDelete({id: row.id})
                    setTimeout(async () => {
                        this.$baseMessage('删除成功', 'success')
                        const permissions = await this.$store.dispatch('user/getUserInfo')
                        this.$store.dispatch('routes/setRoutes', permissions)
                        this.fetchData()
                    }, 500)
                    // loading 正在重新加载菜单

                    // this.fetchData()
                })
            }
        },
        queryData() {
            this.queryForm.pageNo = 1
            this.fetchData()
        },
        async fetchData() {
            this.listLoading = true

            const {data} = await getMenuTree(this.queryForm)
            this.list = data
            setTimeout(() => {
                this.listLoading = false
            }, 300)
        },
        handleNodeClick(data) {
            this.fetchData()
        },
        // 重置
        reset() {
            this.queryForm = {
                title: '',
                menuType: '',
            }
            this.fetchData()
        },
    },
}
</script>
<style scoped lang="scss">
.app-main-container .app-main-height.menuManagement-container {
    min-height: calc(100vh - 140px);
}
</style>
