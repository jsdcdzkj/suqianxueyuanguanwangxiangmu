<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="类型名称" prop="deviceTypeName">
        <el-input v-model.trim="form.deviceTypeName" autocomplete="off" placeholder="请输入设备类型名称"></el-input>
      </el-form-item>
      <el-form-item label="类型编码" prop="deviceTypeCode">
        <el-input v-model.trim="form.deviceTypeCode" autocomplete="off" placeholder="请输入设备类型编码"></el-input>
      </el-form-item>
     
      <el-form-item label="类型描述" prop="deviceTypeDesc">
        <el-input type="textarea" v-model="form.deviceTypeDesc"  placeholder="请输入设备类型描述"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
      }}</el-button>
    </div>
    <bumen ref="bumen" @fetch-data=""></bumen>
  </el-dialog>
</template>

<script>
import { saveOrUpdate } from '@/api/configDeviceType'
import bumen from '@/components/bumen'
export default {
  name: 'UserManagementEdit',
  components: { bumen },
  data() {
    return {
      loading: false,
      disabled: false,
      form: {
        deviceTypeName: '',
        deviceTypeCode: '',
        deviceTypeDesc: '',
      },
      rules: {
        deviceTypeName: [
          { required: true, trigger: 'blur', message: '请输入设备类型名称' },
        ],
        deviceTypeCode: [
          { required: true, trigger: 'blur', message: '请输入设备类型编码' },
        ],
        deviceTypeDesc: [
          { required: true, trigger: 'blur', message: '请输入设备类型描述' },
        ],
        // checkedRoles: [
        //   { required: true, trigger: 'blur', message: '请选择所属机构' },
        // ],
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

          const { msg } = await saveOrUpdate(this.form)
          this.$baseMessage(msg, 'success')
          this.$emit('fetch-data')
          this.close()
        } else {
          return false
        }
      })
    },
    openwin() {
      var that = this      
      that.$refs['bumen'].showDia()      
    },
  },
}
</script>
