<template>
  <el-dialog :close-on-click-modal="false" :title="title" top="6vh" :visible.sync="dialogFormVisible" width="500px"
             @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="110px">
      <el-form-item label="连接名称" prop="linkName">
        <el-input v-model.trim="form.linkName" placeholder="请输入连接名称" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="传输协议" prop="protocolId">
        <el-select v-model="form.protocolId" placeholder="请选择传输协议" class="w" @change="typeChange">
          <el-option
            v-for="item in this.protocolList"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          ></el-option>
        </el-select>
        <!--<el-input v-model.trim="form.protocolId" placeholder="请输入传输协议" autocomplete="off"></el-input>-->
      </el-form-item>
      <el-form-item label="服务地址" prop="serviceAddress">
        <el-input v-model.trim="form.serviceAddress" placeholder="请输入服务地址(示例:127.0.0.1:8080)" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="客户端主键" prop="clientId">
        <el-input v-model.trim="form.clientId" placeholder="请输入客户端主键" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model.trim="form.username" placeholder="请输入用户名" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model.trim="form.password" placeholder="请输入密码" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="清除会话" prop="cleanSession">
        <el-radio-group v-model="form.cleanSession">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="自动重连" prop="reconnect">
        <el-radio-group v-model="form.reconnect">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="超时时间(秒)" prop="connectTimeOut">
        <el-input v-model.trim="form.connectTimeOut" placeholder="请输入超时时间"
                  @blur="form.connectTimeOut = $event.target.value"
                  onkeyup="value=value.replace(/[^\d]/g,'')"></el-input>
      </el-form-item>
      <el-form-item label="心跳间隔(秒)" prop="heartbeatTime">
        <el-input v-model.trim="form.heartbeatTime" placeholder="请输入心跳间隔"
                  @blur="form.heartbeatTime = $event.target.value"
                  onkeyup="value=value.replace(/[^\d]/g,'')"></el-input>
      </el-form-item>
      <el-form-item label="连接描述" prop="linkDesc">
        <el-input v-model.trim="form.linkDesc" placeholder="请输入连接描述" type="textarea" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="API Key" prop="apiKey" v-model="isShow" v-if="isShow==true">
        <el-input v-model.trim="form.apiKey" placeholder="请输入API Key" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="Secret Key" prop="secretKey" v-model="isShow" v-if="isShow==true">
        <el-input v-model.trim="form.secretKey" placeholder="请输入API Key" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button @click="test" type="success">测试连接</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">
        {{ loading ? '确定中 ...' : '确定' }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {getRedisDictList} from '@/api/sysDict'
  import {getEntity, saveOrUpdate, testMqttConnect} from '@/api/configLink'

  export default {
    name: 'titleManagerEdit',
    components: {},
    data() {
      return {
        loading: false,
        disabled: false,
        isShow: true,
        form: {
          id: '',
          linkName: '',
          protocolId: '',
          serviceAddress: '',
          clientId: '',
          username: '',
          password: '',
          cleanSession: 1,
          reconnect: 1,
          connectTimeOut: '',
          heartbeatTime: '',
          linkDesc: '',
          apiKey: '',
          secretKey: '',
        },
        rules: {
          linkName: [
            {required: true, trigger: 'blur', message: '请输入连接名称'},
          ],
          protocolId: [
            {required: true, trigger: 'change', message: '请选择传输协议'},
          ],
          serviceAddress: [
            {required: true, trigger: 'blur', message: '请输入服务地址'},
          ],
          clientId: [
            {required: true, trigger: 'blur', message: '请输入客户端主键'},
          ],
          username: [
            { required: true, trigger: 'blur', message: '请输入用户名' },
          ],
          password: [
            { required: true, trigger: 'blur', message: '请输入密码' },
          ],
          connectTimeOut: [
            {required: true, trigger: 'blur', message: '请输入超时时间'},
          ],
          heartbeatTime: [
            {required: true, trigger: 'blur', message: '请输入心跳间隔'},
          ],
          apiKey: [
            {required: true, trigger: 'blur', message: '请输入API Key'},
          ],
          secretKey: [
            {required: true, trigger: 'blur', message: '请输入Secret Key'},
          ],
        },
        title: '',
        dialogFormVisible: false,
        protocolList: [],
      }
    },
    created() {
    },
    methods: {
      showEdit(row) {
        if (!row) {
          this.title = '添加'
          this.isShow = true;
        } else {
          this.title = '编辑';
          // this.form = Object.assign({}, row);
          this.getData(row);
        }
        //传输协议列表
        this.protocolListData();
        this.dialogFormVisible = true

        this.$nextTick(() => {
          if (this.$refs['form']) {
            this.$refs['form'].resetFields()
          }
        });
      },
      async protocolListData() {
        const {data} = await getRedisDictList({dictType: "config_protocol"});
        this.protocolList = data;
      },
      async typeChange(v) {
        console.log(v)
        if (v == 1) {
          this.isShow = true;
        } else {
          this.isShow = false;
        }
      },
      close() {
        this.$refs['form'].resetFields()
        this.form = this.$options.data().form
        this.dialogFormVisible = false
      },
      async getData(id) {
        const {data} = await getEntity({id: id});
        this.form = data;
        if (this.form.protocolId == 1) {
          this.isShow = true;
        } else {
          this.isShow = false;
        }
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

            const {msg} = await saveOrUpdate(this.form)
            this.$baseMessage(msg, 'success')
            this.$emit('fetch-data')
            this.close()
          } else {
            return false
          }
        })
      },
      async test() {
        if (this.form.protocolId == "" || this.form.protocolId == null || this.form.protocolId == undefined) {
          this.$baseMessage('请选择传输协议', 'error')
          return
        }

        if (this.form.serviceAddress == "" || this.form.serviceAddress == null || this.form.serviceAddress == undefined) {
          this.$baseMessage('请输入服务地址', 'error')
          return
        }

        //MQTT传输协议
        if (this.form.protocolId == "1") {
          if (this.form.clientId == "" || this.form.clientId == null || this.form.clientId == undefined) {
            this.$baseMessage('请输入客户端主键', 'error')
            return
          }
          if (this.form.username == "" || this.form.username == null || this.form.username == undefined) {
            this.$baseMessage('请输入用户名', 'error')
            return
          }
          if (this.form.password == "" || this.form.password == null || this.form.password == undefined) {
            this.$baseMessage('请输入密码', 'error')
            return
          }

          const {msg} = await testMqttConnect(this.form);
          console.log(msg)
          this.$baseMessage(msg, 'success')
        } else {

          this.$baseMessage("暂未开通http协议连接测试", 'success')
        }

      }
    },
  }
</script>
