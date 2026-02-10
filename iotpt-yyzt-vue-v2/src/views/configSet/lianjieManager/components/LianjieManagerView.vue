<template>
  <el-dialog :close-on-click-modal="false" title="详情" :visible.sync="dialogFormVisible" width="808px" @close="close"
             top="10vh">
    <!--<div class="tips-title"><i class="el-icon-s-promotion"></i>{{ 'ID：'+form.id }}</div>-->
    <el-descriptions class="descriptions-self" :column="2" border label-class-name="my-label"
                     content-class-name="my-content">
      <el-descriptions-item>
        <template slot="label">
          连接名称
        </template>
        <span class="contant" :title="form.linkName">{{ form.linkName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          传输协议
        </template>
        <span class="contant" :title="form.protocolName">{{ form.protocolName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          服务地址
        </template>
        <span class="contant" :title="form.serviceAddress">{{ form.serviceAddress }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          客户端主键
        </template>
        <span class="contant" :title="form.clientId">{{ form.clientId }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          用户名
        </template>
        <span class="contant" :title="form.username">{{ form.username }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          密码
        </template>
        <span class="contant" :title="form.password">{{ form.password }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          清除会话
        </template>
        <span class="contant" title="是" v-if="form.cleanSession == 1"> <el-tag size="small"
                                                                               type="success">是</el-tag></span>
        <span class="contant" title="否" v-else> <el-tag size="small" type="warning">否</el-tag></span>
        <!-- <el-tag size="small" type="warning">不是</el-tag> -->
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          自动重连
        </template>
        <span class="contant" title="是" v-if="form.reconnect == 1"> <el-tag size="small"
                                                                            type="success">是</el-tag></span>
        <span class="contant" title="否" v-else> <el-tag size="small" type="warning">否</el-tag></span>
        <!-- <el-tag size="small" type="warning">不是</el-tag> -->
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          超时时间
        </template>
        <span class="contant" :title="form.connectTimeOut">{{ form.connectTimeOut }}</span>
      </el-descriptions-item>
      <!--<el-descriptions-item>-->
      <!--<template slot="label">-->
      <!--使用日期-->
      <!--</template>-->
      <!--<span class="contant" title=" 2020-09-09"> 2020-09-09</span>-->
      <!--</el-descriptions-item>-->
      <el-descriptions-item>
        <template slot="label">
          心跳间隔
        </template>
        <span class="contant" :title="form.heartbeatTime">{{ form.heartbeatTime }}</span>
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          API Key
        </template>
        <span class="contant" :title="form.apiKey">{{ form.apiKey }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          Secret Key
        </template>
        <span class="contant" :title="form.secretKey">{{ form.secretKey }}</span>
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          连接描述
        </template>
        <span class="contant" :title="form.linkDesc">
         {{ form.linkDesc }}
        </span>
      </el-descriptions-item>
    </el-descriptions>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {getEntity} from '@/api/configLink'

  export default {
    name: 'LianjieManagerView',
    data() {
      return {
        disabled: false,
        title: '',
        dialogFormVisible: false,
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
        },
      }
    },
    created() {
    },
    methods: {
      showEdit(row) {
        // this.title = row
        this.getData(row);
        this.dialogFormVisible = true
      },
      async getData(id) {
        const {data} = await getEntity({id: id});
        this.form = data;
      },
      close() {
        this.dialogFormVisible = false
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
  .contant {
    cursor: default;
  }

  .header-title {
    text-align: center;
    position: relative;

    .title-left {
      position: absolute;
      left: 0;
    }
  }

  .module-title {
    height: 39px;
    line-height: 39px;
    // text-align: center;
    > span {
      margin-left: 5px;
    }

    > span:after {
      content: ''; // 必须有这个属性，否则不显示
      display: inline-block; // 必须有，因为是行内元素
      background-color: #5470c6;
      width: 4px;
      height: 14px;
      position: relative;
      left: 8px;
      top: 2px;
    }

    > svg {
      position: relative;
      top: 3px;
    }
  }

  .tips-title {
    font-size: 14px;
    margin-bottom: 8px;
    font-weight: bold;
    color: #334c97;

    i {
      margin-right: 4px;
    }
  }
</style>
