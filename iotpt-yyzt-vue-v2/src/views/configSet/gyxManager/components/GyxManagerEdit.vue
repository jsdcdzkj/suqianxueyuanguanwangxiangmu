<template>
   <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="供应商名称" prop="supplierName">
                <el-input v-model.trim="form.supplierName" autocomplete="off" placeholder="请输入供应商名称"></el-input>
            </el-form-item>
            <el-form-item label="供应商类型" prop="supplierType">
                <el-select v-model="form.supplierType" placeholder="请选择供应商类型" class="w" clearable>
                    <el-option v-for="item in systematicType" :key="item.id" :label="item.dictLabel"
                               :value="item.dictValue"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="联系人" prop="contacts">
                <el-input v-model.trim="form.contacts" autocomplete="off" placeholder="请输入联系人"></el-input>
            </el-form-item>
            <el-form-item label="联系电话" prop="telephone">
                <el-input v-model.trim="form.telephone" autocomplete="off" placeholder="请输入联系电话"></el-input>
            </el-form-item>
            <el-form-item label="地址" prop="address">
                <el-input v-model.trim="form.address" autocomplete="off" placeholder="请输入地址"></el-input>
            </el-form-item>
            <el-form-item label="描述">
                <el-input v-model.trim="form.illustrate" type="textarea" autocomplete="off" placeholder="请输入描述"></el-input>
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
import {saveOrUpdate} from '@/api/configSupplier'

export default {
    name: 'configSupplierEdit',
    data() {
        return {
            loading: false,
            disabled: false,
            form: {
                id: '',
                supplierName: '',
                supplierType: '',
                contacts: '',
                telephone: '',
                address: '',
                illustrate: '',
            },
            rules: {
                supplierName: [
                    {required: true, trigger: 'blur', message: '请输入供应商名称'},
                ],
                supplierType: [
                    {required: true, trigger: 'change', message: '请选择供应商类型'},
                ],
                contacts: [
                    {required: true, trigger: 'blur', message: '请输入联系人'},
                ],
                telephone: [
                    {required: true, trigger: 'blur', message: '请输入联系电话'},
                ],
                address: [
                    {required: true, trigger: 'blur', message: '请输入地址'},
                ],
            },
            title: '',
            dialogFormVisible: false,
            systematicType: [],
        }
    },
    created() {
        this.getDictByKey().then(res => {
            this.systematicType = res.data['supplierType']
        })
    },
    methods: {
        showEdit(row) {
            if (!row) {
                this.title = '添加'
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
