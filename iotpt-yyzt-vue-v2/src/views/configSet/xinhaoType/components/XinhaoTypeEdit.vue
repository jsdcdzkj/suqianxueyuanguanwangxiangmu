<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="信号名称" prop="signalTypeName">
        <el-input v-model.trim="form.signalTypeName" autocomplete="off" placeholder="请输入信号名称"></el-input>
      </el-form-item>
      <el-form-item label="信号编码" prop="signalTypeCode">
        <el-input v-model.trim="form.signalTypeCode" autocomplete="off" placeholder="请输入信号编码"></el-input>
      </el-form-item>
      <el-form-item label="信号描述">
        <el-input type="textarea" v-model="form.signalTypeDesc"  placeholder="请输入信号描述"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { saveOrUpdate } from '@/api/sinalType'

export default {
  name: 'UserManagementEdit',
  data() {
    return {
      loading: false,
      disabled: false,
      form: {
        signalTypeName: '',
        signalTypeCode: '',
        signalTypeDesc: '',
      },
      rules: {
        signalTypeName: [
          { required: true, trigger: 'blur', message: '请输入信号名称' },
        ],
        signalTypeCode: [
          { required: true, trigger: 'blur', message: '请输入信号编码' },
        ],
      },
      title: '',
      dialogFormVisible: false,
    }
  },
  created() { },
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
            if(0 == res.code){
              this.$baseMessage(this.title + "成功", 'success')
              this.$emit('fetch-data')
              this.close()
            }else{
              this.$baseMessage(this.title + "失败", 'error')
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
