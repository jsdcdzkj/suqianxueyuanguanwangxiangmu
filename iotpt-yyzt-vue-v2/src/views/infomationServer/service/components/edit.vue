<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="110px">
      <el-form-item label="通知方式" prop="msgType">
        <el-select v-model="form.msgType" filterable placeholder="请选择通知方式" class="w">
            <el-option v-for="item in msgTypes" :key="item.id" :label="item.dictLabel" :value="item.dictValue"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="名称" prop="msgName">
          <el-input v-model.trim="form.msgName" placeholder="请输入名称" autocomplete="off"></el-input>
        </el-form-item>
      <div v-if="form.msgType === '2'">
        <el-form-item label="RegionId" prop="regionId">
          <el-input v-model.trim="form.regionId" placeholder="请输入RegionId" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="AccessKeyId" prop="accessKeyId">
          <el-input v-model.trim="form.accessKeyId" placeholder="请输入AccessKeyId" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Secret" prop="secret">
          <el-input v-model.trim="form.secret" placeholder="请输入Secret" autocomplete="off"></el-input>
        </el-form-item>
      </div>
      <div v-else>
        <el-form-item label="服务器地址" prop="server">
          <el-input v-model.trim="form.server" placeholder="请输入服务器地址" autocomplete="off" style="width:100%"></el-input>
          <el-input-number v-model="form.port" @change="handleChange" :min="1" label="描述文字" style="width: 50%;padding: 0 4px;"></el-input-number>
          <el-checkbox v-model="form.isSSL">开启SSL</el-checkbox>
        </el-form-item>
        <el-form-item label="发件人" prop="sender">
          <el-input v-model.trim="form.sender" placeholder="请输入发件人" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="name">
          <el-input v-model.trim="form.name" placeholder="请输入用户名" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="pwd">
          <el-input v-model.trim="form.pwd" type="password" placeholder="请输入密码" autocomplete="off"></el-input>
        </el-form-item>
      </div>
      <el-form-item label="说明">
        <el-input v-model.trim="form.remarks" type="textarea" placeholder="请输入说明" autocomplete="off"></el-input>
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
import { doEdit } from '@/api/msgInfo'
import bumen from '@/components/bumen'

export default {
  name: 'UserManagementEdit',
  components: { bumen },
  data() {
    return {
      loading: false,
      disabled: false,
      form: {
          id: '',
          msgName: '',
          msgType: '',
          emailType: '',
          protocol: '',
          server: '',
          port: '',
          name: '',
          pwd: '',
      },
      radio:0,
      rules: {
          msgName: [{ required: true, trigger: 'blur', message: '请输入服务名称' },],
          msgType: [{ required: true, trigger: 'blur', message: '请输入' },],
          protocol: [{ required: true, trigger: 'blur', message: '请输入' },],
          server: [{ required: true, trigger: 'blur', message: '请输入' },],
          sender: [{ required: true, trigger: 'blur', message: '请输入' },],
          name: [{ required: true, trigger: 'blur', message: '请输入' },],
          pwd: [{ required: true, trigger: 'blur', message: '请输入' },],
          regionId: [{ required: true, trigger: 'blur', message: '请输入' },],
          accessKeyId: [{ required: true, trigger: 'blur', message: '请输入' },],
          secret: [{ required: true, trigger: 'blur', message: '请输入' },],
      },
      title: '',
      dialogFormVisible: false,
        msgTypes: [],
        protocols: [],
        emails: [],


    }
  },
  created() { },
  methods: {
    showEdit(row) {

        this.getDictByKey().then(res => {
            this.msgTypes = res.data['msg_type']
            this.protocols = res.data['msg_protocol']
            this.emails = res.data['msg_email']
        })


      if (!row) {
        this.title = '添加'
      } else {
        this.title = '编辑'
        row.isSSL = row.isSSL == 1 ? true : false
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
            var x = {};
            x.id = this.form.id;
            x.msgName = this.form.msgName;
            x.msgType = this.form.msgType;
            x.emailType = this.form.emailType;
            x.protocol = this.form.protocol;
            x.server = this.form.server;
            x.port = this.form.port;
            x.name = this.form.name;
            x.pwd = this.form.pwd;
            x.isSSL = this.form.isSSL ? 1 : 0;
            x.sender= this.form.sender;
            x.regionId = this.form.regionId;
            x.accessKeyId = this.form.accessKeyId;
            x.secret = this.form.secret;
            x.remarks = this.form.remarks;

            doEdit(x).then((res) => {
                if (res.code === 0){
                    if (!x.id){
                        this.$baseMessage("新增成功", 'success')
                    }else {
                        this.$baseMessage("编辑成功", 'success')
                    }
                    this.$emit('fetch-data')
                    this.close()
                }else {
                    this.$baseMessage(res.msg, 'error')
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
