<template>
    <el-dialog
        :close-on-click-modal="false"
        :title="title"
        :visible.sync="dialogFormVisible"
        width="500px"
        @close="close"
    >
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="工作流名称" prop="name">
                <el-input
                    v-model.trim="form.name"
                    autocomplete="off"
                    placeholder="请输入"
                ></el-input>
            </el-form-item>
            <el-form-item label="工作流密钥" prop="secretKey">
                <el-input
                    v-model.trim="form.secretKey"
                    autocomplete="off"
                    placeholder="请输入"
                ></el-input>
            </el-form-item>

            <el-form-item label="工作流地址" prop="url">
                <el-input
                    v-model.trim="form.url"
                    autocomplete="off"
                    placeholder="请输入"
                ></el-input>
            </el-form-item>

            <el-form-item label="类型" prop="type">
                <el-select
                    v-model="form.type"
                    placeholder="请选择"
                    clearable
                    filterable
                    style="width:100%;"
                >
                    <el-option label="DIFY" value="1"/>
                    <el-option label="毕昇" value="2"/>
                </el-select>
            </el-form-item>

            <el-form-item label="icon图标">
                <el-input
                    v-model.trim="form.icon"
                    autocomplete="off"
                    placeholder="请输入"
                ></el-input>
            </el-form-item>
            <el-form-item label="排序">
                <el-input
                    v-model.trim="form.sort"
                    type="number"
                    autocomplete="off"
                    placeholder="请输入"
                ></el-input>
            </el-form-item>
            <el-form-item label="描述" prop="description">
                <el-input
                    type="textarea"
                    v-model="form.description"
                    placeholder="请输入描述"
                ></el-input>
            </el-form-item>
            <!--      <el-form-item label="字典状态">-->
            <!--        <el-radio-group v-model="radio">-->
            <!--          <el-radio :label="0">启用</el-radio>-->
            <!--          <el-radio :label="1">禁用</el-radio>-->
            <!--        </el-radio-group>-->
            <!--      </el-form-item>-->
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button
                :disabled="disabled"
                :loading="loading"
                type="primary"
                @click="save"
            >
                {{ loading ? '确定中 ...' : '确定' }}
            </el-button>
        </div>
        <!--    <bumen ref="bumen" @fetch-data=""></bumen>-->
    </el-dialog>
</template>

<script>
import {saveOrUpdate} from '@/api/worklist'
// import bumen from '@/components/bumen'

export default {
    name: 'dictEdit',
    // components: {bumen},
    data() {
        return {
            loading: false,
            disabled: false,
            form: {
                id: '',
                name: '',
                secretKey: '',
                url: '',
                type: '',
                icon: '',
                description: '',
                sort: '',
            },
            radio: 0,
            rules: {
                name: [
                    {required: true, trigger: 'blur', message: '请输入工作流名称'},
                ],
                secretKey: [
                    {required: true, trigger: 'blur', message: '请输入工作流密钥'},
                ],
                // parentId: [
                //     {required: true, trigger: 'blur', message: '请选择所属父级'},
                // ],
            },
            title: '',
            dialogFormVisible: false,
            options: [],
        }
    },
    created() {
    },
    methods: {
        showEdit(row) {
            if (!row) {
                this.title = '添加'
            } else {
                this.title = '编辑'
                this.form = Object.assign({}, row)
                // 转string
                // this.form.parentId = row.parentId.toString()
                if (row.parentId === -1) {
                    this.form.parentId = this.form.parentId.toString()
                }
            }
            this.dialogFormVisible = true
        },
        close() {
            this.$refs['form'].resetFields()
            this.form = this.$options.data().form
            this.dialogFormVisible = false
        },
        save() {
            if (this.loading) {
                return
            }
            this.$refs['form'].validate(async (valid) => {
                if (this.form.sort !== '' && this.form.sort != null) {
                    if (this.form.sort < 0) {
                        this.$baseMessage('排序不能小于0', 'warning')
                        return false
                    }
                }
                if (valid) {
                    this.loading = true
                    this.disabled = true
                    setTimeout(() => {
                        this.loading = false
                        this.disabled = false
                    }, 1000)

                    saveOrUpdate(this.form).then((res) => {
                        if (-1 == res.code) {
                            this.$baseMessage(res.msg, 'error')
                        } else {
                            this.$baseMessage('保存' + res.msg, 'success')
                            this.$emit('fetch-data')
                            this.close()
                        }
                    })
                } else {
                    return false
                }
            })
        },

    },
}
</script>
