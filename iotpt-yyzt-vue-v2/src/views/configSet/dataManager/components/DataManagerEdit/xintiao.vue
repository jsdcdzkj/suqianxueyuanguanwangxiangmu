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
            <el-select v-model="configTopicId" placeholder="请选择主题">
              <el-option
                v-for="item in configTopicList"
                :key="item.id"
                :label="item.topicName"
                :value="item.id">
              </el-option>
            </el-select>
          </el-col>
        </el-row>
        <div style="margin-bottom: 12px;">
          <el-button type="primary" size="mini" :loading="loading">获取数据</el-button>
          <el-button size="mini">格式校验</el-button>
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
                <td rowspan="3">
                  <div class="big-title">心跳信息</div>
                </td>
                <td>设备（{{heartList[0].systemKey}}）</td>
                <td>
                  <el-input placeholder="请输入设备" v-model="heartList[0].gatewayKey"></el-input>
                </td>
              </tr>
              <tr>
                <td>心跳状态（{{heartList[1].systemKey}}）</td>
                <td>
                  <el-input placeholder="请输入心跳状态" v-model="heartList[1].gatewayKey"></el-input>
                </td>
              </tr>
              <tr>
                <td>心跳时间（{{heartList[2].systemKey}}）</td>
                <td>
                  <el-input placeholder="请输入心跳时间" v-model="heartList[2].gatewayKey"></el-input>
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
      heartList: {
        type: Array,
        default: []
      },
      templateField3: {
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
      }
    },
    methods: {
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
