<template>
  <el-drawer :title="title" :visible.sync="dialogFormVisible" size="1200px" @close="close">
    <!-- <div slot="title" class="header-title el-dialog__title">
        <span class="title-left">{{ title }}</span>
        <span> 设备信息  </span>
    </div> -->
    <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>批量编辑</b></span>
    <div style="padding: 0 20px;">

      <div class="tips-title"><i class="el-icon-s-promotion"></i>批量编辑项</div>
      <!-- <div class="module-title" style="margin-top: -15px;">
      <svg t="1683618700542" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8350" width="20" height="20">
        <path d="M571.52 136.832a32 32 0 0 1 44.8 0.64l174.4 175.904a32 32 0 0 1-1.632 46.624L368.864 728.064a32 32 0 0 1-21.76 7.936l-155.776-3.328a32 32 0 0 1-31.328-32v-158.208a32 32 0 0 1 9.92-23.168z m42.464 514.496l239.84 4.672a32 32 0 1 1-1.248 64l-239.84-4.672a32 32 0 1 1 1.28-64zM592.96 204.8L224 556.16v113.184l112 2.4 385.312-337.472L592.96 204.8z m259.296 606.528a32 32 0 0 1 0.48 64l-628.48 4.672a32 32 0 0 1-0.48-64l628.48-4.672z" fill="#aaaaaa" p-id="8351"></path>
      </svg>
      <span>批量编辑项</span>
    </div> -->
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="fjzd-form">
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="username">
              <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备类型" prop="checkedRoles">
              <el-select v-model="form.value" multiple placeholder="请选择" class="w">
                <el-option label="管理员" value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备编码" prop="username">
              <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备编码"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备状态" prop="checkedRoles">
              <el-select v-model="form.value" multiple placeholder="请选择" class="w">
                <el-option label="管理员" value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属分项" prop="checkedRoles">
              <el-select v-model="form.value" multiple placeholder="请选择" class="w">
                <el-option label="管理员" value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属网关" prop="checkedRoles">
              <el-select v-model="form.value" multiple placeholder="请选择" class="w">
                <el-option label="管理员" value="1"></el-option>
              </el-select>
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
            <el-form-item label="安装时期" prop="username">
              <el-date-picker type="date" placeholder="请选择日期" v-model="form.date1" style="width: 100%;"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用日期" prop="username">
              <el-date-picker type="date" placeholder="请选择日期" v-model="form.date1" style="width: 100%;"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="设备管理员" prop="checkedRoles">
              <el-select v-model="form.value" multiple placeholder="请选择" class="w">
                <el-option label="管理员" value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="管理员电话" prop="username">
              <el-input v-model.trim="form.username" autocomplete="off" placeholder="请输入设备名称"></el-input>
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
            <el-form-item label="维保公司" prop="checkedRoles">
              <el-select v-model="form.value" multiple placeholder="请选择" class="w">
                <el-option label="管理员" value="s"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年检日期" prop="username">
              <el-date-picker type="date" placeholder="请选择日期" v-model="form.date1" style="width: 100%;"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="采购日期" prop="username">
              <el-date-picker type="date" placeholder="请选择日期" v-model="form.date1" style="width: 100%;"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="过保日期" prop="username">
              <el-date-picker type="date" placeholder="请选择日期" v-model="form.date1" style="width: 100%;"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="设备描述" prop="org_code">
          <el-input v-model.trim="form.username" type="textarea" placeholder="请输入解析数据示例" :rows="3" autocomplete="off"></el-input>
        </el-form-item>

      </el-form>
      <div style="height: 270px;">
        <!-- <div class="module-title">
        <svg t="1683618338470" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6828" width="15" height="15">
          <path d="M485.052632 943.157895L80.842105 538.947368 512 80.842105h431.157895v431.157895L485.052632 943.157895zM889.263158 134.736842H538.947368L161.684211 538.947368l323.368421 323.368421 404.210526-377.263157V134.736842z m-256 134.736842a121.263158 121.263158 0 1 1 0 242.526316 121.263158 121.263158 0 0 1 0-242.526316z m0 188.631579a67.368421 67.368421 0 1 0 0-134.736842 67.368421 67.368421 0 0 0 0 134.736842z" fill="#AAAAAA" p-id="6829"></path>
        </svg>
        <span>已选设备</span>
      </div> -->

        <div class="tips-title"><i class="el-icon-s-promotion"></i>已选设备</div>
        <CjzdTable ref="CjzdTable" @fetch-data=""></CjzdTable>
      </div>
    </div>

    <!-- <div slot="footer" class="footer-btn">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
      }}</el-button>
    </div> -->
    
    <div class="footer-btn">
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">立即提交</el-button>
      <el-button @click="close">取消</el-button>
    </div>
  </el-drawer>
</template>

<script>
import { doEdit } from '@/api/userManagement'
import CjzdTable from './cjzdTable'

export default {
  name: 'UserManagementEdit',
  components: { CjzdTable },
  data() {
    return {
      value: '',
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
        value: '',
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
      this.title = '批量编辑'
      this.form = Object.assign({}, row)
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
</style>

<style scoped lang="scss">
.jiexi {
  position: absolute;
  right: 21px;
  top: 100px;
  z-index: 1;
  padding: 5px 15px;
}
::v-deep {
  .el-drawer__header {
    padding: 10px !important;
    margin-bottom: 14px;
    border-bottom: 1px solid #d1d9e1;

    .drawer-title {
      font-size: 16px;
      line-height: 16px;
      color: #334c97;
      display: flex;
      align-items: center;

      i {
        display: block;
        font-size: 18px;
        line-height: 18px;
        margin-right: 4px;
      }

      b {
        display: block;
        font-size: 16px;
        line-height: 16px;
        margin-top: 2px;
        margin-right: 4px;
      }
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

  .el-timeline {
    padding-left: 0 !important;
  }

  .timeline-con {
    padding: 10px 4px;
    height: calc(100vh - 598px);
    overflow-y: auto;
  }

  .time-con {
    margin-left: -6px;
    color: #333;
    // box-shadow: 0 0px 4px 0 rgba(0, 0, 0, 0.1);
    border-top: 4px solid #ccd2e6;
    background-color: #f5f6fb;

    .time-top {
      padding: 10px;

      span.time-name {
        margin-right: 10px;
        color: #666;
        font-weight: bold;

        i {
          color: #ccd2e6;
          margin-right: 4px;
        }

        b {
          font-weight: normal;
          color: #999;
          margin-left: 12px;
          font-size: 13px;
        }
      }

      .el-tag {
        float: right;
        margin-top: -2px;
      }
    }

    .time-bot {
      display: flex;
      padding: 0px 10px 6px;
      align-items: flex-start;
      line-height: 24px;
      font-size: 14px;

      i {
        display: inline-block;
        width: 16px;
        height: 24px;
        margin-right: 4px;
        line-height: 24px;
        color: #ccd2e6;
      }

      p {
        margin: 0;
        color: #666;
      }
    }
  }

  .el-radio {
    padding: 6px 12px;
    background-color: #f5fbff;
    margin-bottom: 6px;
    margin-right: 6px;
  }

  .my-label {
    background: #e1f3d8;
  }

  .my-content {
    width: 280px;
  }
}
.footer-btn {
  padding: 12px 20px;
  border-top: 1px solid #efefef;
  position: absolute;
  bottom: 0;
  text-align: right;
  width: 100%;
  background-color: #fff;
  z-index: 999;
}
</style>