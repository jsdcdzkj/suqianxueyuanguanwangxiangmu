<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="单位名称" prop="username">
        <el-input v-model.trim="form.username" placeholder="请输入" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="所属单位" prop="checkedRoles">
        <el-select v-model="form.checkedRoles" filterable placeholder="请选择" class="w">
          <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="地址" prop="username2">
        <el-input v-model.trim="form.username2" placeholder="请输入" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="负责人" prop="username3">
        <el-input v-model.trim="form.username3" placeholder="请输入" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="联系电话" prop="username4">
        <el-input v-model.trim="form.username4" placeholder="请输入" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model.trim="form.org_desc" type="textarea" placeholder="请输入" autocomplete="off"></el-input>
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
import { doEdit } from '@/api/userManagement'
import bumen from '@/components/bumen'

export default {
  name: 'Edit',
  components: { bumen },
  data() {
    return {
      loading: false,
      disabled: false,
      form: {
        username: '',
        username2: '',
        username3: '',
        username4: '',
        org_desc: '',
        checkedRoles:'',
      },
      rules: {
        username: [
          { required: true, trigger: 'blur', message: '请输入单位名称' },
        ],
        username2: [
          { required: true, trigger: 'blur', message: '请输入地址' },
        ],
        username3: [
          { required: true, trigger: 'blur', message: '请输入负责人' },
        ],
        username4: [
          { required: true, trigger: 'blur', message: '请输入联系电话' },
        ],
        checkedRoles: [
          { required: true, trigger: 'blur', message: '请选择所属单位' },
        ],
      },
      title: '',
      dialogFormVisible: false,
      options: [{
          value: '选项1',
          label: '单位名称1'
        }, {
          value: '选项2',
          label: '单位名称2'
        }, {
          value: '选项3',
          label: '单位名称3'
        }, {
          value: '选项4',
          label: '单位名称4'
        }, {
          value: '选项5',
          label: '单位名称5'
        }],
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
