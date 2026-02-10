<template>
  <el-dialog :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="110px">
      <el-form-item label="设备名称" prop="username">
        <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入模板名称"></el-input>
      </el-form-item>
      <el-form-item label="设备编码" prop="username">
        <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入模板名称"></el-input>
      </el-form-item>
      <el-form-item label="所属楼宇" prop="org_code">
        <el-select v-model="form.username" multiple placeholder="请选择模板类型" class="w">
          <el-option label="管理员" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属楼层" prop="org_code">
        <el-select v-model="form.username" multiple placeholder="请选择模板类型" class="w">
          <el-option label="管理员" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属区域" prop="org_code">
        <el-select v-model="form.username" multiple placeholder="请选择模板类型" class="w">
          <el-option label="管理员" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="供应商" prop="org_code">
        <el-select v-model="form.username" multiple placeholder="请选择模板类型" class="w">
          <el-option label="管理员" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备型号" prop="org_code">
        <el-select v-model="form.username" multiple placeholder="请选择模板类型" class="w">
          <el-option label="管理员" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备描述" prop="org_code">
        <el-input v-model.trim="form.username" type="textarea" placeholder="请输入模板描述" autocomplete="off"></el-input>
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
import { doEdit } from '@/api/userManagement'

export default {
  name: 'MjsbEdit',
  components: {  },
  data() {
    return {
      loading: false,
      disabled: false,
      form: {
        username: '',
        password: '',
        email: '',
        permissions: [],
        org_code: '',
        org_name: '',
      },
      rules: {
        username: [
          { required: true, trigger: 'blur', message: '请输入用户名' },
        ],
        password: [
          { required: true, trigger: 'blur', message: '请输入密码' },
        ],
        org_code: [
          { required: true, trigger: 'submit', message: '请选择所属机构' },
        ],
        checkedRoles: [
          { required: true, trigger: 'blur', message: '请选择所属机构' },
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

          const { msg } = await doEdit(this.form)
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
