<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="部门名称" prop="username">
        <el-input v-model.trim="form.username" placeholder="请输入" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="所属部门" prop="checkedRoles">
        <el-select v-model="form.checkedRoles" filterable placeholder="请选择" class="w">
          <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属单位" prop="checkedRoles2">
        <el-select v-model="form.checkedRoles2" filterable placeholder="请选择" class="w">
          <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属区域" prop="checkedRoles3">
        <el-select v-model="form.checkedRoles3" filterable placeholder="请选择" class="w">
          <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
        </el-select>
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
        org_code: '',
        org_desc: '',
        checkedRoles:'',
        checkedRoles2:'',
        checkedRoles3:'',
      },
      rules: {
        username: [
          { required: true, trigger: 'blur', message: '请输入楼宇名称' },
        ],
        checkedRoles: [
          { required: true, trigger: 'blur', message: '请选择所属部门' },
        ],
        checkedRoles2: [
          { required: true, trigger: 'blur', message: '请选择所属单位' },
        ],
        checkedRoles3: [
          { required: true, trigger: 'blur', message: '请选择所属区域' },
        ],
      },
      title: '',
      dialogFormVisible: false,
      options: [{
          value: '选项1',
          label: '选项1'
        }, {
          value: '选项2',
          label: '选项2'
        }, {
          value: '选项3',
          label: '选项3'
        }, {
          value: '选项4',
          label: '选项4'
        }, {
          value: '选项5',
          label: '选项5'
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
