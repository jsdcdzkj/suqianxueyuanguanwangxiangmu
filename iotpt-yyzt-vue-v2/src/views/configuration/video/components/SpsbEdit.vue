<template>
  <el-dialog :title="title" :visible.sync="dialogFormVisible" width="768px" @close="close">
    <!-- <div slot="title" class="header-title el-dialog__title">
        <span class="title-left">{{ title }}</span>
        <span> 设备信息  </span>
    </div> -->
    
    <el-form ref="form" :model="form" :rules="rules" label-width="127px" class="fjzd-form">
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备名称" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备编码" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备编码"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="所属楼宇" prop="checkedRoles">
            <el-select v-model="form.value" multiple placeholder="请选择" class="w">
              <el-option label="管理员" value="1"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属楼层" prop="checkedRoles">
            <el-select v-model="form.value" multiple placeholder="请选择" class="w">
              <el-option label="管理员" value="1"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="所属区域" prop="checkedRoles">
            <el-select v-model="form.value" multiple placeholder="请选择" class="w">
              <el-option label="管理员" value="1"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="安装位置" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入安装位置"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="供应商名称" prop="checkedRoles">
            <el-select v-model="form.value" multiple placeholder="请选择" class="w">
              <el-option label="管理员" value="1"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备型号" prop="checkedRoles">
            <el-select v-model="form.value" multiple placeholder="请选择" class="w">
              <el-option label="管理员" value="1"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备通道号" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="NVR通道号" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备编码"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="窗口号" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="硬盘机id" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备编码"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="ip地址" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备端口" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备编码"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备经度" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备纬度" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备编码"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="拍摄区域横坐标" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="拍摄区域纵坐标" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备编码"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="h5token" prop="username">
            <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否视频流" prop="username">
            <el-radio-group v-model="form.username">
              <el-radio label="是"></el-radio>
              <el-radio label="否"></el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="设备状态" prop="checkedRoles">
            <el-select v-model="form.value" multiple placeholder="请选择" class="w">
              <el-option label="管理员" value="1"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备描述" prop="org_code">
            <el-input v-model.trim="form.username" type="textarea" placeholder="请输入解析数据示例" :rows="3" autocomplete="off"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
     


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
  name: 'UserManagementEdit',
  data() {
    return {
      value:'',
      loading: false,
      disabled: false,
      form: {
        username: '',
        password: '',
        email: '',
        permissions: [],
        org_code: '',
        org_name: '',
        date1: '',
        date2: '',
        value:''
      },
      rules: {
        username: [
          { required: true, trigger: 'blur', message: '请输入用户名' },
        ],
        password: [{ required: true, trigger: 'blur', message: '请输入密码' }],
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
  created() {},
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
<style lang="scss">
.fjzd-form {
  padding-right: 12px !important;
}
</style>
<style lang="scss" scoped>
.header-title {
  text-align: center;
  position: relative;

  .title-left {
    position: absolute;
    left: 0;
  }
}
</style>