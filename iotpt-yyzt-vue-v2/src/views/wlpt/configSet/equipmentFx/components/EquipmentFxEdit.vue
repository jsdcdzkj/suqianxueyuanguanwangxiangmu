<template>
  <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="分项名称" prop="subitemName">
        <el-input v-model.trim="form.subitemName" autocomplete="off" placeholder="请输入分项名称"></el-input>
      </el-form-item>
      <el-form-item label="分项编码" prop="subitemCode">
        <el-input v-model.trim="form.subitemCode" autocomplete="off" placeholder="请输入分项编码"></el-input>
      </el-form-item>
      <el-form-item label="能源类型" prop="energy_type">
        <el-select v-model="form.energy_type" placeholder="请选择能源类型" clearable class="w">
          <el-option v-for="item in this.typeList" :key="item.dictValue" :label="item.dictLabel"
            :value="item.dictValue"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="分项描述" prop="illustrate">
        <el-input type="textarea" v-model="form.illustrate" placeholder="请输入分项描述"></el-input>
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
import { saveOrUpdate } from '@/api/deviceSubitem'
import { selectDictList } from '@/api/sysDict'
export default {
  name: 'EquipmentFxEdit',
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
        energy_type: ''
      },
      typeList: [],
      rules: {
        subitemName: [
          { required: true, trigger: 'blur', message: '请输入分项名称' },
        ],
        subitemCode: [
          { required: true, trigger: 'blur', message: '请输入分项编码' },
        ],
        energy_type: [
          { required: true, trigger: 'blur', message: '请选择能源类型' },
        ],
      },
      title: '',
      dialogFormVisible: false,
    }
  },
  created() {
    this.typeListData();
  },
  methods: {
    async typeListData() {
      const { data } = await selectDictList({ dictType: "device_energy_type" })
      console.log(data)
      var type = [];
      if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
          type.push({ dictLabel: data[i].dictLabel, dictValue: parseInt(data[i].dictValue) })
        }
        this.typeList = type;
      }
    },
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
            if (0 == res.code) {
              this.$baseMessage(this.title + "成功", 'success')
              this.$emit('fetch-data')
              this.close()
            } else {
              this.$baseMessage(this.title + "失败", 'error')
            }
          })
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
