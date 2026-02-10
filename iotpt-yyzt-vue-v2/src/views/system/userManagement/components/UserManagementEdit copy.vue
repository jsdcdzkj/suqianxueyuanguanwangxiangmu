<template>
  <el-drawer
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    size="1280px"
    @close="close"
  >
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>卡片名称</span>
        <el-button style="float: right; padding: 3px 0" type="text">
          操作按钮
        </el-button>
      </div>
      <el-form>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="用户头像" prop="avatar">
              <el-upload
                class="avatar-uploader"
                :action="actionUrl"
                :show-file-list="false"
                accept=".jpg,.jpeg,.png"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img
                  v-if="form.avatar || form.avatarUrl"
                  :src="getImgUrl(form.avatar || form.avatarUrl)"
                  class="avatar"
                />
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名称" prop="realName">
              <el-input
                v-model.trim="form.realName"
                autocomplete="off"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录名称" prop="loginName">
              <el-input
                v-model="form.loginName"
                autocomplete="off"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户密码" prop="password">
              <el-input
                v-model.trim="form.password"
                type="password"
                placeholder="请输入"
                autocomplete="off"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属单位" prop="unitId">
              <ebs-tree-select
                :options="treeData"
                v-model="form.unitId"
                :accordion="true"
                @node-click="nodeClick"
                placeholder="选择所属单位"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门" prop="deptId">
              <!-- <el-input v-model.trim="form.deptId" @click.native="openwin" placeholder="请选择">
                              <el-button slot="append" icon="el-icon-search" @click="openwin"></el-button>
                          </el-input> -->

              <el-select
                v-model="form.deptId"
                filterable
                placeholder="请选择"
                class="w"
              >
                <el-option
                  v-for="item in options"
                  :key="item.id"
                  :label="item.deptName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="phone">
              <el-input
                v-model="form.phone"
                autocomplete="off"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="cardId">
              <el-input
                v-model="form.cardId"
                autocomplete="off"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户角色" prop="roleIds">
              <el-select
                v-model="form.roleIds"
                multiple
                placeholder="请选择"
                class="w"
              >
                <el-option
                  v-for="item in roleList"
                  :key="item.id"
                  :label="item.roleName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户状态">
              <el-radio-group v-model="form.isEnable">
                <el-radio :label="1" value="1">启用</el-radio>
                <el-radio :label="0" value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model.trim="form.memo"
                type="textarea"
                :rows="3"
                placeholder="请输入"
                autocomplete="off"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>卡片名称</span>
        <el-button style="float: right; padding: 3px 0" type="text">
          操作按钮
        </el-button>
      </div>
      <el-form>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="通行权限" prop="doorIds">
              <el-select
                v-model="form.doorIds"
                multiple
                placeholder="请选择"
                class="w"
              >
                <el-option
                  v-for="item in doorOption"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>卡片名称</span>
        <el-button style="float: right; padding: 3px 0" type="text">
          操作按钮
        </el-button>
      </div>
      <div v-for="o in 4" :key="o" class="text item">
        {{ '列表内容 ' + o }}
      </div>
    </el-card>

    <span slot="title" class="drawer-title">
      <i class="el-icon-menu"></i>
      <b>{{ title }}</b>
    </span>
    <div
      style="padding: 0 30px 80px 30px; position: relative"
      class="title-manager-view"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <div class="title">
          <div class="tips-title">基础信息</div>
        </div>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="用户头像" prop="avatar">
              <el-upload
                class="avatar-uploader"
                :action="actionUrl"
                :show-file-list="false"
                accept=".jpg,.jpeg,.png"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img
                  v-if="form.avatar || form.avatarUrl"
                  :src="getImgUrl(form.avatar || form.avatarUrl)"
                  class="avatar"
                />
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名称" prop="realName">
              <el-input
                v-model.trim="form.realName"
                autocomplete="off"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录名称" prop="loginName">
              <el-input
                v-model="form.loginName"
                autocomplete="off"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户密码" prop="password">
              <el-input
                v-model.trim="form.password"
                type="password"
                placeholder="请输入"
                autocomplete="off"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属单位" prop="unitId">
              <ebs-tree-select
                :options="treeData"
                v-model="form.unitId"
                :accordion="true"
                @node-click="nodeClick"
                placeholder="选择所属单位"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门" prop="deptId">
              <!-- <el-input v-model.trim="form.deptId" @click.native="openwin" placeholder="请选择">
                            <el-button slot="append" icon="el-icon-search" @click="openwin"></el-button>
                        </el-input> -->

              <el-select
                v-model="form.deptId"
                filterable
                placeholder="请选择"
                class="w"
              >
                <el-option
                  v-for="item in options"
                  :key="item.id"
                  :label="item.deptName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="phone">
              <el-input
                v-model="form.phone"
                autocomplete="off"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="cardId">
              <el-input
                v-model="form.cardId"
                autocomplete="off"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户角色" prop="roleIds">
              <el-select
                v-model="form.roleIds"
                multiple
                placeholder="请选择"
                class="w"
              >
                <el-option
                  v-for="item in roleList"
                  :key="item.id"
                  :label="item.roleName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户状态">
              <el-radio-group v-model="form.isEnable">
                <el-radio :label="1" value="1">启用</el-radio>
                <el-radio :label="0" value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model.trim="form.memo"
                type="textarea"
                :rows="3"
                placeholder="请输入"
                autocomplete="off"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="title">
          <div class="tips-title">通行权限</div>
        </div>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="通行权限" prop="doorIds">
              <el-select
                v-model="form.doorIds"
                multiple
                placeholder="请选择"
                class="w"
              >
                <el-option
                  v-for="item in doorOption"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="title">
          <div class="tips-title">车辆信息</div>
        </div>
        <el-row :gutter="20">
          <el-col
            :span="12"
            v-for="(car, index) in form.car"
            :key="index"
            style="margin-bottom: 14px"
          >
            <div class="car-card">
              <div class="card-left">
                <h4>{{ car.busNumber }}</h4>
                <p>有效期至{{ car.endOfEffectiveTime }}</p>
              </div>
              <div class="card-right">
                <div class="card-edit" @click="handelAdd(index)">编辑</div>
                <div class="card-del" @click="deleteCar(index)">删除</div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="car-card">
              <div class="car-add" @click="handelAdd()">
                <i class="el-icon-plus"></i>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- <el-form-item label="所属区域" prop="areaId">
                <el-select v-model="form.areaId" filterable placeholder="请选择" class="w">
                    <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item> -->
      </el-form>
    </div>
    <div class="footer-btn">
      <el-button @click="close">取 消</el-button>
      <el-button
        type="primary"
        @click="save"
        :loading="loading"
        :disabled="disabled"
      >
        {{ loading ? '确定中 ...' : '确定' }}
      </el-button>
    </div>
    <bumen ref="bumen" @fetch-data=""></bumen>
    <add ref="add" @saveCar="saveCar" @fetch-data=""></add>
  </el-drawer>
</template>

<script>
  import { doEdit } from '@/api/userManagement'
  import bumen from '@/components/bumen'
  import add from './add'
  import { getList as getRoleList } from '@/api/roleManagement'
  import { info, orgTreeList } from '@/api/org'
  import { selectOrgDeptList, getDoorList, getCarList } from '@/api/dept'
  import { baseURL } from '@/config'
  export default {
    name: 'UserManagementEdit',
    components: { bumen, add },
    data() {
      // 身份证号验证规则
      var checkIdCard = (rule, value, callback) => {
        // 简单的身份证校验规则，可以根据实际需求扩展
        const reg =
          /^[1-9]\d{5}(18|19|20|21|22)?\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}(\d|[Xx])$/
        if (reg.test(value) === false) {
          callback(new Error('请输入正确的身份证号码'))
        } else {
          callback()
        }
      }
      // 验证手机号的正则表达式
      const checkPhone = (rule, value, callback) => {
        const reg = /^1[3-9]\d{9}$/
        if (value === '') {
          callback(new Error('手机号不能为空'))
        } else if (!reg.test(value)) {
          callback(new Error('请输入正确的手机号'))
        } else {
          callback()
        }
      }
      return {
        actionUrl: baseURL + '/minio/importFile',
        loading: false,
        disabled: false,
        form: {
          id: '',
          realName: '',
          loginName: '',
          password: '',
          areaId: '',
          deptId: undefined,
          phone: '',
          roleIds: [],
          doorIds: [],
          cardId: '',
          isEnable: 1,
          memo: '',
          unitId: '',
          avatar: '',
          car: [],
        },
        radio: 0,
        rules: {
          avatar: [{ required: true, trigger: 'blur', message: '请上传头像' }],
          realName: [
            { required: true, trigger: 'blur', message: '请输入用户名' },
          ],
          loginName: [
            { required: true, trigger: 'blur', message: '请输入登录名' },
          ],
          password: [
            { required: true, trigger: 'blur', message: '请输入密码' },
          ],
          // unitId: [
          //   {
          //     validator: (rule, value, callback) => {
          //       console.log(value,'value');
          //       if (!value || value=='-99' ) {
          //         return callback(new Error('请选择所属单位'))
          //       }
          //       callback()
          //     },
          //     trigger: 'change'
          //   },
          // ],
          unitId: [
            { required: true, trigger: 'change', message: '请选择所属单位' },
          ],
          deptId: [
            {
              required: true,
              trigger: 'change',
              message: '请选择所属部门',
              type: 'number',
            },
          ],
          // areaId: [
          //     {required: true, trigger: 'submit', message: '请选择所属区域'},
          // ],
          phone: [
            { required: true, message: '请输入手机号', trigger: 'blur' },
            { validator: checkPhone, trigger: 'blur' },
          ],
          roleIds: [
            { required: true, trigger: 'change', message: '请选择用户角色' },
          ],
          doorIds: [
            { required: true, trigger: 'change', message: '请选择通行权限' },
          ],
          cardId: [
            { required: true, message: '请输入身份证号', trigger: 'blur' },
            // { validator: checkIdCard, trigger: 'blur' }
          ],
        },
        title: '',
        dialogFormVisible: false,
        options: [],
        roleList: [],
        treeData: [],
        doorOption: [],
        valueLimits: [],
        showModal: false,
        editStatus: null,
        carList: [],
      }
    },
    created() {
      this.getRoleList()
      this.getTreeData()
      this.getDoorList()
    },
    methods: {
      saveCar(car) {
        if (this.editStatus != undefined && this.editStatus != null) {
          Object.assign(this.form.car[this.editStatus], car)
        } else {
          this.form.car.push(car)
        }
      },
      deleteCar(index) {
        this.form.car.splice(index, 1)
      },
      handelAdd(index) {
        if (index != undefined) {
          this.editStatus = index
          const info = this.form.car[index]
          this.$refs['add'].showEdit(info)
        } else {
          this.editStatus = null
          this.$refs['add'].showEdit()
        }
      },
      getImgUrl(attr) {
        if (!attr) return
        if (attr.includes('/minio/previewFile?fileName=')) {
          return baseURL + attr
        } else {
          return baseURL + '/minio/previewFile?fileName=' + attr
        }
      },
      //限制图片大小
      beforeAvatarUpload(file) {
        const isLt2M = file.size / 1024 / 1024 < 8
        if (!isLt2M) {
          this.$message.error('上传头像图片大小不能超过 8MB!')
        }
        return isLt2M
      },
      // 上传头像回调
      handleAvatarSuccess({ code, data, msg }) {
        if (code === 0) {
          this.form.avatar = data.filename
        } else {
          this.$message.error(msg)
        }
      },
      // 单位数据
      getTreeData() {
        orgTreeList().then((res) => {
          if (res.code == 0) {
            this.treeData = res.data
          }
        })
      },
      // 门禁设备列表
      async getDoorList() {
        const { data } = await getDoorList()
        this.doorOption = data
      },
      /** 部门数据 */
      nodeClick(node) {
        this.options = []
        this.form.unitId = node.data.id
        this.form.deptId = ''
        var queryForm = {
          ids: '',
          username: '',
          orgId: node.data.id,
          pageNo: 1,
          pageSize: 10000000,
        }
        selectOrgDeptList(queryForm)
          .then((res) => {
            if (res.code == 0) {
              this.options = res.data.records
            } else {
              this.$message.error(
                '获取部门数据失败, 请检查单位部门数据是否完整'
              )
            }
          })
          .catch((err) => {
            console.log('error', err)
          })
      },
      showEdit(row) {
        if (!row) {
          this.title = '添加'
        } else {
          this.title = '编辑'
          if (row.unitId) {
            this.nodeClick({ data: { id: row.unitId } })
          }
          this.form = Object.assign({}, row)
          getCarList(row.id)
            .then((res) => {
              console.log(res)
              this.form.car = res.data.carList
              this.form.doorIds = res.data.doorPurviewList.map(
                (item) => item.doorId
              )
            })
            .catch((err) => {
              console.log('error', err)
            })
        }
        this.dialogFormVisible = true
      },
      close() {
        this.$refs['form'].resetFields()
        this.form = this.$options.data().form
        this.dialogFormVisible = false
        this.loading = false
        this.disabled = false
      },
      save() {
        if (this.loading) {
          return
        }
        console.log(this.form.roleIds)
        console.log(this.form.doorIds)
        this.$refs['form'].validate(async (valid) => {
          if (valid) {
            this.loading = true
            this.disabled = true

            const { code, msg } = await doEdit(this.form)
            if (0 == code) {
              this.$baseMessage(msg, 'success')
              this.$emit('fetch-data')
              const permissions = await this.$store.dispatch('user/getUserInfo')
              this.$store.dispatch('routes/setRoutes', permissions)
              this.close()
            } else {
              this.loading = false
              this.disabled = false
              this.$baseMessage(msg, 'error')
            }
          } else {
            this.loading = false
            this.disabled = false
            return false
          }
        })
      },
      openwin() {
        var that = this
        that.$refs['bumen'].showDia()
      },

      async getRoleList() {
        const { data } = await getRoleList()
        this.roleList = data
      },
    },
  }
</script>
<style lang="scss" scoped>
  ::v-deep .avatar-uploader {
    height: 60px;
  }
  ::v-deep .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  ::v-deep .avatar-uploader .el-upload:hover {
    border-color: #409eff;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 60px;
    height: 60px;
    line-height: 60px;
    text-align: center;
  }
  .avatar {
    width: 60px;
    height: 60px;
    display: block;
    object-fit: cover;
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
  .tips-title {
    margin-bottom: 14px;
    font-weight: bold;
  }
  .car-card {
    display: flex;
    width: 100%;
    height: 96px;
    background: #ffffff;
    box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
    border-radius: 8px 8px 8px 8px;
    border: 1px solid rgba(0, 0, 0, 0.06);
    overflow: hidden;
    .car-add {
      width: 100%;
      height: 100%;
      line-height: 96px;
      text-align: center;
      font-size: 18px;
      color: #999;
      cursor: pointer;
    }
    .card-left {
      padding: 23px 16px;
      flex: 1;
      border-right: 1px solid rgba(0, 0, 0, 0.06);
      h4 {
        margin: 0;
        padding: 0;
        font-size: 16px;
        font-weight: bold;
        color: rgba(0, 0, 0, 0.85);
        line-height: 24px;
      }
      p {
        margin: 0;
        padding: 0;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.65);
        line-height: 22px;
      }
    }
    .card-right {
      font-size: 14px;
      color: rgba(0, 0, 0, 0.45);
      div {
        width: 60px;
        height: 48px;
        line-height: 48px;
        text-align: center;
        cursor: pointer;
      }
      .card-edit {
        border-bottom: 1px solid rgba(0, 0, 0, 0.06);
        box-sizing: border-box;
        color: rgba(53, 91, 173, 1);
      }
    }
  }
</style>
