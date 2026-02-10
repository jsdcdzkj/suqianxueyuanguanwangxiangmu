<template>
   <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" append-to-body @close="close">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="型号名称" prop="modelName">
                <el-input v-model.trim="form.modelName" autocomplete="off" placeholder="请输入型号名称"></el-input>
            </el-form-item>
            <el-form-item label="型号说明" prop="illustrate">
                <el-input type="textarea" v-model="form.illustrate" placeholder="请输入型号说明"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{
                    loading ? '确定中 ...' : '确定'
                }}
            </el-button>
        </div>
    </el-dialog>
</template>

<script>
import {saveOrUpdate} from '@/api/configModel'

export default {
    name: 'configModelEdit',
    data() {
        return {
            loading: false,
            disabled: false,
            form: {
                id: '',
                supplierId: '',
                modelName: '',
                illustrate: '',
            },
            rules: {
                modelName: [
                    {required: true, trigger: 'blur', message: '请输入型号名称'},
                ],
                illustrate: [
                    {required: true, trigger: 'blur', message: '请输入型号说明'},
                ],
            },
            title: '',
            dialogFormVisible: false,
        }
    },
    created() {
    },
    methods: {
        showEdit(row) {
            if (!row.id) {
                this.title = '添加'
                this.form.supplierId = row.supplierId
            } else {
                this.title = '编辑'
                this.form = Object.assign({}, row)
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
                if (valid) {
                    this.loading = true
                    this.disabled = true
                    setTimeout(() => {
                        this.loading = false
                        this.disabled = false
                    }, 1000)

                    saveOrUpdate(this.form).then(res => {
                        if (res.code === 0) {
                            this.$baseMessage(res.msg, 'success')
                            this.$emit('fetch-data')
                            this.close()
                        } else {
                            this.$baseMessage(res.msg, 'error')
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
