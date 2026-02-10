<template>
   <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="角色名称" prop="roleName">
                <el-input v-model="form.roleName" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
            <el-form-item label="角色标志" prop="roleFlag">
                <el-input v-model="form.roleFlag" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
            <el-form-item label="所属系统" prop="systemId">
                <el-select v-model="form.systemId" filterable placeholder="请选择" class="w" @change="changeSystem">
                    <el-option v-for="item in systematicType" :key="item.id" :label="item.dictLabel"
                               :value="item.dictValue"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="菜单权限" prop="menuIds">
                <div class="tree-boxs" >
                    <el-tree
                        :data="data2"
                        show-checkbox
                        node-key="id"
                        :default-checked-keys="oneArr"
                        ref="treeOne"
                        :default-expand-all="true"
                        :props="defaultProps">
                    </el-tree>
                </div>
            </el-form-item>

            <el-form-item label="备注">
                <el-input v-model="form.memo" type="textarea" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button type="primary" @click="save" :disabled="saveDisable">确 定</el-button>
        </div>
    </el-dialog>
</template>

<script>
import {doEdit} from '@/api/roleManagement'
import {getMenuTypeTree} from '@/api/menuManagement'

export default {
    name: 'roleEdit',
    data() {
        return {
            form: {
                id: '',
                roleName: '',
                roleFlag: '',
                systemId: '',
                memo: '',
                menuIds: []
            },
            rules: {
                roleName: [{required: true, trigger: 'blur', message: '请输入角色名称'}],
                roleFlag: [{required: true, trigger: 'blur', message: '请输入角色标志'}],
                systemId: [{required: true, trigger: 'blur', message: '请选择所属系统'}],
                menuIds: [{required: true, trigger: 'blur', message: '请选择菜单权限'}],
            },
            title: '',
            dialogFormVisible: false,
            options: [],
            systematicType: [],
            data2: [],
            defaultProps: {
                children: 'children',
                label: 'label'
            },
            data: [],
            oneArr: [],
            twoArr: [],
            activeName: 'first',
            saveDisable: false,
            parentArr: []
        }
    },
    created() {
        this.getDictByKey().then(res => {
            this.systematicType = res.data['systematic_type']
        })
        this.getMenuTypeTree2(1);
    },
    methods: {
        showEdit(row) {
            if (!row) {
                this.title = '添加'
            } else {
                this.title = '编辑'
                this.form = Object.assign({}, row)
                this.getMenuTypeTree2(this.form.systemId).then(res => {
                    let filterArr = row.menuIds;
                    filterArr = filterArr.map(item => {
                        return item.toString()
                    })
                    // 如果 filterArr 里面 包含 parentArr 里面的值,则删除
                    filterArr = filterArr.filter(item => {
                        return !this.parentArr.includes(item)
                    })

                    this.oneArr = filterArr;
                    this.form.systemId = row.systemId.toString()
                    this.$nextTick(() => {
                        this.$refs.treeOne.setCheckedKeys(this.oneArr);
                        this.$forceUpdate()
                    })
                })
            }

            this.saveDisable = false
            this.dialogFormVisible = true
        },
        close() {
            this.$refs['form'].resetFields()
            this.$refs.treeOne.setCheckedKeys([]);
            this.form = this.$options.data().form
            this.dialogFormVisible = false
        },
        save() {
            this.saveDisable = true
            this.form.menuIds = this.$refs.treeOne.getCheckedKeys()
            this.$refs['form'].validate(async (valid) => {
                if (valid) {

                    // if (this.activeName === 'first') {

                    // } else {
                    //     this.form.menuIds = this.$refs.treeTwo.getCheckedKeys()
                    // }
                    const {code, msg} = await doEdit(this.form)
                    if(0 == code){
                        this.$baseMessage("操作成功", 'success')
                        this.$emit('fetch-data')
                        const permissions = await this.$store.dispatch('user/getUserInfo')
                        this.$store.dispatch('routes/setRoutes', permissions)
                        // window.location.reload()
                        this.close()
                    }else{
                        this.saveDisable = false
                        this.$baseMessage(msg, 'error')
                    }
                } else {
                    this.saveDisable = false
                    return false
                }
            })

        },
        async getMenuTypeTree2(systemId) {
            const {data} = await getMenuTypeTree({systemId: systemId})
            this.data2 = data
            // 循环遍历数据,判断自己是否有children,如果有,则记录parentArr数组中.
            data.forEach(item => {
                if (item.children && item.children.length > 0) {
                    this.parentArr.push(item.id)
                    item.children.forEach(item2 => {
                        if (item2.children && item2.children.length > 0) {
                            this.parentArr.push(item2.id)
                            item2.children.forEach(item3 => {
                                if (item3.children && item3.children.length > 0) {
                                    this.parentArr.push(item3.id)
                                    item3.children.forEach(item4 => {
                                        if (item4.children && item4.children.length > 0) {
                                            this.parentArr.push(item4.id)
                                        }
                                    })
                                }
                            })
                        }
                    })
                }
            })
            console.log("菜单数据", data)
        },
        changeSystem() {
            this.getMenuTypeTree2(this.form.systemId)
        },
    },
}
</script>
<style scoped lang="scss">
::v-deep {
    .el-tabs__header {
        margin-bottom: 0;
    }

    .el-tabs__content {
        border: 1px solid #e4e7ed;
        border-top: none;
        padding-top: 10px;
        padding-bottom: 10px;
        height: 260px;
        overflow-y: auto;
    }

    .el-tabs__item {
        height: 32px;
        line-height: 32px;
        position: relative;

        &.is-active {
            background-color: #d8dff4;
        }

        &.is-active::after {
            content: '';
            display: inline-block;
            width: 100%;
            height: 1px;
            background-color: #d8dff4;
            position: absolute;
            bottom: 0px;
            left: 0;
        }
    }

    .el-tabs--card > .el-tabs__header .el-tabs__item {
        border-bottom: aliceblue;
    }

    .tree-boxs{
        border: 1px solid #e4e7ed;
        padding-top: 10px;
        padding-bottom: 10px;
        height: 260px;
        overflow-y: auto;
        border-radius: 4px;
    }

}
</style>
