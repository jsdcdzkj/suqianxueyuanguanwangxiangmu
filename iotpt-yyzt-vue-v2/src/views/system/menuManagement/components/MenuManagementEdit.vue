<template>
   <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="菜单名称" prop="title">
                <el-input v-model="form.title" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
            <el-form-item label="菜单类型" prop="menuType">
                <el-select v-model="form.menuType" class="w" filterable placeholder="请选择">
<!--                    <el-option value="0" label="目录"></el-option>-->
                    <el-option value="1" label="菜单"></el-option>
                    <el-option value="2" label="按钮"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="所属系统" prop="systemId">
                <el-select v-model="form.systemId" class="w" @change="changeSystem" filterable placeholder="请选择">
                    <el-option value="" >请选择</el-option>
                    <el-option v-for="item in systematicType" :key="item.id" :label="item.dictLabel"
                               :value="item.dictValue"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="父级菜单" prop="checkedRoles">
                <ebs-tree-select :options="treeData" v-model="form.parentId" :props="{ value: 'id', label: 'title',  children: 'children' }" :accordion="true" placeholder="选择父级菜单" />
                <!-- <treeselect v-model="form.parentId" :normalizer="normalizer" :options="treeData" placeholder="选择父级菜单" /> -->
            </el-form-item>
            <el-form-item label="路由名称">
                <el-input v-model="form.routerName" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
            <el-form-item label="路由链接">
                <el-input v-model="form.routerUrl" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
            <el-form-item label="vue文件路径">
                <el-input v-model="form.vueUrl" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
            <el-form-item label="重定向类型">
                <el-input v-model="form.redirectType" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
            <el-form-item label="图标">
                <el-input v-model="form.icon" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
<!--            <el-form-item label="是否显示">-->
<!--                <el-radio-group v-model="form.isShow">-->
<!--                    <el-radio :label="0">否</el-radio>-->
<!--                    <el-radio :label="1">是</el-radio>-->
<!--                </el-radio-group>-->
<!--            </el-form-item>-->

            <el-form-item label="排序">
                <el-input v-model="form.sort" autocomplete="off" placeholder="请输入" type="number"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button type="primary" @click="save" :disabled="disabledBtn">确 定</el-button>
        </div>
    </el-dialog>
</template>

<script>
import {getMenuList, doEdit, getMenuTree} from '@/api/menuManagement'
import { orgTreeList} from "@/api/org";

export default {
    name: 'MenuManagementEdit',
    inject: ['reload'],
    data() {
        return {
            form: {
                id: '',
                title: '',
                parentId: '',
                routerName: '',
                routerUrl: '',
                vueUrl: '',
                redirectType: '',
                icon: '',
                isShow: '1',
                systemId: '',
                sort: '',
                menuType: '1',
            },
            rules: {
                title: [{required: true, trigger: 'blur', message: '请输入菜单名称'}],
                path: [{required: true, trigger: 'blur', message: '请输入角色标志'}],
                menuType: [{required: true, trigger: 'change', message: '请选择菜单类型'}],
                systemId: [{required: true, trigger: 'change', message: '请选择所属系统'}],
                // org_code: [{required: true, trigger: 'blur', message: '请选择所属系统'}]
            },
            title: '',
            dialogFormVisible: false,
            options: [{value: '选项1', label: '选项1'}, {value: '选项2', label: '选项2'}, {
                value: '选项3',
                label: '选项3'
            }, {value: '选项4', label: '选项4'}, {value: '选项5', label: '选项5'}],
            menuOptions: [],
            optionRedirect: [],
            systematicType: [],
            treeData: [],
            disabledBtn: false,
            queryForm: {
                systemId: '',
            }
        }
    },
    mounted() {
        this.getMenuSelect()
        this.getDictByKey().then(res => {
            this.optionRedirect = res.data['redirect_type']
            this.systematicType = res.data['systematic_type']
        })
        this.getTreeData()
    },
    methods: {
        changeSystem(){
            this.queryForm.systemId = this.form.systemId
            this.form.parentId = ''
            this.fetchData();
        },
        async fetchData() {
            const {data} = await getMenuTree(this.queryForm)
            this.treeData = data
        },
        // 自定义参数键值名称
        normalizer(node){
            //去掉children=[]的children属性
            if(node.children && !node.children.length){
                delete node.children;
            }
            return {
                id: node.id,
                label: node.title,
                children: node.children,
                level: node.isShow
            }
        },
        // 单位数据
        getTreeData() {
            // orgTreeList().then((res) => {
            //     if (res.code == 0) {
            //         // this.treeData = res.data;
            //     }
            // })
        },
        showEdit(row, type) {
            if (type){
                this.form.systemId = type
            }
            this.changeSystem()
            if (!row) {
                this.title = '添加'
            } else {
                this.title = '编辑'
                this.form = Object.assign({}, row)
                this.form.parentId = String(this.form.parentId || '')
                this.form.parentId = this.form.parentId === '0' ? '' : this.form.parentId
                this.form.menuType = String(this.form.menuType || '')
                this.form.systemId = String(this.form.systemId || '')
            }

            this.getMenuSelect()
            this.getDictByKey().then(res => {
                this.optionRedirect = res.data['redirect_type']
                this.systematicType = res.data['systematic_type']
            })

            // this.getTreeData()
            this.disabledBtn = false
            this.dialogFormVisible = true
        },
        close() {
            this.$refs['form'].resetFields()
            this.form = this.$options.data().form
            this.dialogFormVisible = false
        },
        save() {
            this.disabledBtn = true
            this.$refs['form'].validate(async (valid) => {
                if (valid) {

                    const {msg} = await doEdit(this.form)
                    this.$baseMessage("操作成功", 'success')
                    const permissions = await this.$store.dispatch('user/getUserInfo')
                    this.$store.dispatch('routes/setRoutes', permissions)
                    this.$emit('fetch-data')
                    this.getMenuSelect()
                    //更新左侧菜单栏样式    重新加载路由    重新塞入路由信息
                    let hasToken = this.$store.getters['user/accessToken']
                    this.$store.dispatch('user/getUserInfo', hasToken)
                    // window.location.reload()
                    // this.reload()
                    // 重新塞入路由信息
                    this.close()
                } else {
                    this.disabledBtn = false
                    return false
                }
            })
        },
        getMenuSelect() {
            getMenuList().then(res => {
                this.menuOptions = res.data
            })
        }
    },
}
</script>
