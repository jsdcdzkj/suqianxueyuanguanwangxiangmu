<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">

        <div>&nbsp;</div>
        <!-- <div class="tips-title"><i class="el-icon-s-promotion"></i>转换数据</div>
        <el-row :gutter="20" style="margin-bottom: 12px;">
          <el-col :span="12">
            <el-select v-model="configLinkId" placeholder="请选择连接">
              <el-option
                v-for="item in configLinkList"
                :key="item.id"
                :label="item.linkName"
                :value="item.id">
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="12">
            <el-select v-model="configTopicId" placeholder="请选择主题" @change="topicChange">
              <el-option
                v-for="item in configTopicList"
                :key="item.id"
                :label="item.topicName"
                :value="item.topicName">
              </el-option>
            </el-select>
          </el-col>
        </el-row>
        <div style="margin-bottom: 12px;">
          <el-button type="primary" size="mini" @click="getDatas()" :loading="loading">获取数据</el-button>
          <el-button size="mini" @click="checkJson()">格式校验</el-button>
        </div>
        <div>
          <json-viewer :value="jsonData" :expand-depth=5 copyable boxed expanded></json-viewer>
        </div> -->
      </el-col>
      <el-col :span="16">
        <div class="tips-title"><vab-icon :icon="['fas', 'map-signs']"></vab-icon>&nbsp;解析规则配置</div>

        <el-form label-width="0px" :model="formLabelAlign" :rules="rules" ref="ruleForm">
          <table class="custom-table">
            <tr>
              <td colspan="2" class="title"><span>系统对象字段</span></td>
              <td colspan="1" class="title"><span>解析对应字段</span></td>
            </tr>
            <!-- 循环 -->
            <template>
              <tr>
                <td rowspan="6">
                  <div class="big-title">控制信息</div>
                </td>
                <td>网关主键（{{controlList[0].systemKey}}）</td>
                <td>
                  <el-input placeholder="请输入网关主键" v-model="controlList[0].gatewayKey"></el-input>
                </td>
              </tr>
              <tr>
                <td>设备主键（{{controlList[1].systemKey}}）</td>
                <td>
                  <el-input placeholder="请输入设备主键" v-model="controlList[1].gatewayKey"></el-input>
                </td>
              </tr>
              <tr>
                <td>信号主键（{{controlList[2].systemKey}}）</td>
                <td>
                  <el-input placeholder="请输入信号主键" v-model="controlList[2].gatewayKey"></el-input>
                </td>
              </tr>
              <tr>
                <td>信号值（{{controlList[3].systemKey}}）</td>
                <td>
                  <el-input placeholder="请输入信号值" v-model="controlList[3].gatewayKey"></el-input>
                </td>
              </tr>
              <tr>
                <td>控制结果（{{controlList[4].systemKey}}）</td>
                <td>
                  <el-input placeholder="请输入控制结果" v-model="controlList[4].gatewayKey"></el-input>
                </td>
              </tr>
                <tr>
                    <td>控制时间（{{controlList[5].systemKey}}）</td>
                    <td>
                        <el-input placeholder="请输入控制时间" v-model="controlList[5].gatewayKey"></el-input>
                    </td>
                </tr>
            </template>
          </table>
        </el-form>
      </el-col>
    </el-row>
  </div>

</template>

<script>
  import {getDatas} from '@/api/configDataTransfer'

  export default {
    props: {
      configLinkList: {
        type: Array,
        default: []
      },
      configTopicList: {
        type: Array,
        default: []
      },
        controlList: {
        type: Array,
        default: []
      },
      templateField4: {
        type: Object,
        default: {}
      }
    },
    data() {
      return {
        loading: false,
        // 临时内容
        input: '临时内容',
        jsonData: {
          code: 200,
          msg: 'success',
          totalCount: 3,
          data: [
            {
              id: '120000202006264860',
              username: 'admin',
              password: 'admin',
              email: 'u.tobcl@nndnwxjt.sd',
              permissions: ['admin'],
              datatime: '1993-06-03 05:16:54',
              hidden: 1,
            },
            {
              id: '13000019871117234X',
              username: 'editor',
              password: 'editor',
              email: 'j.kbri@szdb.at',
              permissions: ['editor'],
              datatime: '1995-11-03 15:02:20',
              hidden: 1,
            },
            {
              id: '620000197006181111',
              username: 'test',
              password: 'test',
              email: 'y.ksvnky@sssntudr.cv',
              permissions: ['admin', 'editor'],
              datatime: '2005-10-08 02:17:06',
              hidden: 1,
            },
          ],
        },
        formLabelAlign: {
          name: '',
          region: '',
          type: '',
        },
        rules: {
          name: [
            {required: true, message: '请输入活动名称', trigger: 'blur'},
            {min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur'},
          ],
        },
        configLinkId: '',
        configTopicId: '',
        socket: '',
      }
    },
    created() {
      // this.linkWebSocket();
    },
    methods: {
      // async topicChange(v) {
      //   console.log(v);
      //   this.linkWebSocket(window.btoa(v));
      // },
      // linkWebSocket(clientId) {
      //   if (typeof (WebSocket) == "undefined") {
      //     this.$baseMessage('您的浏览器不支持', 'error');
      //   } else {
      //     var hostport = "ws://localhost:8086/websocket/" + clientId;
      //     if (this.socket != '') {
      //       this.socket.onclose = function () {
      //         console.log("socket关闭事件222");
      //       };
      //     }
      //     this.socket = new WebSocket(hostport);
      //     var socket = this.socket;
      //     socket.onopen = function () {
      //       console.log("Socket 已打开");
      //       //alert("websocket已经打开");
      //       socket.send("");
      //     };
      //     //获得消息事件
      //     socket.onmessage = function (msg) {
      //       console.log("socket获得消息事件");
      //       console.log(msg);
      //       // var json = JSON.parse(msg.data);
      //
      //     };
      //     socket.onclose = function () {
      //       console.log("socket关闭事件");
      //     };
      //     socket.onerror = function () {
      //       console.log("Socket发生了错误");
      //     }
      //   }
      // },
      async getDatas() {
        const {data} = await getDatas({topic: "/Topic/likong", online: 0});
        console.log(data)
        this.$baseMessage('请求成功', 'success')
      },
      close() {
        this.$refs['ruleForm'].resetFields()
        this.form = this.$options.data().form
        this.dialogFormVisible = false
      },
      save() {
        if (this.loading) {
          return
        }
        this.$refs['ruleForm'].validate(async (valid) => {
          if (valid) {
            this.$baseMessage('成功', 'success')
            // this.$emit('fetch-data')
            // this.close()
          } else {
            return false
          }
        })
      },
    },
  }
</script>
<style lang="scss" scoped>
  .title {
    background: #f5f5f5 !important;
    padding: 5px 5px !important;
  }

  .big-title {
    // background: #f5f5f5;
    width: 100%;
    height: 100%;
    font-weight: bold;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .small-title {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
</style>
<style lang="scss">
  .shuju-table {
    .cell {
      width: 100%;
      height: 100%;
      padding: 0px !important;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .el-input__inner {
      width: 97%;
      margin: 2px 10px 2px 3px;
    }

    .el-form-item {
      width: 100%;
    }

    .el-form-item--small.el-form-item {
      margin-bottom: 0px !important;
    }
  }
</style>
