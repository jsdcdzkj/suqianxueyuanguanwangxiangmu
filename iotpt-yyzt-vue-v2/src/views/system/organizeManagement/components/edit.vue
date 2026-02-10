<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    width="500px"
    @close="close"
  >
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="部门名称" prop="deptName">
        <el-input
          v-model.trim="form.deptName"
          placeholder="请输入部门名称"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="所属区域" prop="defaultCascaderValue">
        <el-cascader
          placeholder="请选择所属区域"
          :options="options"
          :props="props"
          style="width: 100%"
          :show-all-levels="true"
          v-model="form.defaultCascaderValue"
          ref="cascaderArr"
          @change="choseTheme"
          clearable
        ></el-cascader>
      </el-form-item>
      <el-form-item label="部门描述">
        <el-input
          v-model.trim="form.memo"
          type="textarea"
          rows="4"
          placeholder="请输入部门描述"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="第三方访客区域配置" label-width="200">
        <el-select multiple style="width: 100%" v-model="form.visitorAreaIds">
          <!-- <el-option
            v-for="item in visitorList"
            :key="item.id"
            :label="item.areaName"
            :value="item.areaId"
          ></el-option> -->
          <el-option
            v-for="item in visitorList"
            :key="item.id"
            :label="item.areaName"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
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
  </el-dialog>
</template>

<script>
  import { addOrgDept, info, updateOrgDept } from '@/api/dept.js'
  import { areaTreeList } from '@/api/org.js'
  import bumen from '@/components/bumen'
  import { visitorRegionList } from '@/api/dept.js'
  export default {
    name: 'Edit',
    components: { bumen },
    data() {
      return {
        loading: false,
        disabled: false,
        checkDatas: [],
        form: {
          deptName: '',
          memo: '',
          ids: '',
          orgId: '',
          id: '',
          defaultCascaderValue: [],
          visitorAreaIds: [],
          dahuaDeptId: '',
        },
        rules: {
          deptName: [
            { required: true, trigger: 'blur', message: '请输入部门名称' },
          ],
          defaultCascaderValue: [
            {
              required: true,
              trigger: 'submit',
              message: '请选择区域',
              type: 'array',
            },
          ],
        },
        title: '',
        dialogFormVisible: false,
        props: { multiple: true },
        options: [],
        checkDatas: [],
        visitorList: [],
      }
    },
    created() {},
    methods: {
      showEdit(id, row) {
        visitorRegionList().then((res) => {
          this.visitorList = res.data
          this.form.orgId = id
          this.checkDatas = []
          if (this.$refs['form']) {
            this.$refs.form.clearValidate()
          }
          this.form.defaultCascaderValue = []
          if (!row) {
            this.treeData()
            this.title = '添加'
          } else {
            this.treeData()
            this.title = '修改'
            info(row.id).then((res) => {
              if (res.code == 0) {
                this.form.deptName = res.data.deptName
                this.form.memo = res.data.memo
                this.form.ids = res.data.ids
                this.form.orgId = res.data.orgId
                this.form.id = res.data.id
                this.form.defaultCascaderValue = res.data.showData
                this.form.visitorAreaIds = res.data.visitorAreaIds
                this.form.dahuaDeptId = res.data.dahuaDeptId
              }
            })
          }

          this.dialogFormVisible = true
        })
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
            this.checkDatas = []
            const checkedNode = this.$refs['cascaderArr'].getCheckedNodes(true)
            //获取当前点击节点的label值
            if (checkedNode != null) {
              for (let i = 0; i < checkedNode.length; i++) {
                // console.log(checkedNode[i].value)
                if (checkedNode[i].level == 3) {
                  this.checkDatas.push(checkedNode[i].value)
                }
              }
              // console.log(this.checkDatas)
              // return
            }
            if (this.checkDatas.length == 0) {
              this.$baseMessage('请选择所属区域', 'error')
              this.loading = false
              this.disabled = false
              return
            }
            this.form.ids = this.checkDatas.toString()
            if (this.form.id) {
              updateOrgDept(this.form)
                .then((res) => {
                  if (res.code == 0) {
                    this.$baseMessage('操作成功', 'success')
                    this.$emit('fetch-data')
                    this.close()
                  } else {
                    this.$baseMessage(res.msg, 'error')
                  }
                  this.loading = false
                  this.disabled = false
                })
                .catch(() => {
                  this.loading = false
                  this.disabled = false
                })
            } else {
              addOrgDept(this.form)
                .then((res) => {
                  if (res.code == 0) {
                    this.$baseMessage('操作成功', 'success')
                    this.$emit('fetch-data')
                    this.close()
                  } else {
                    this.$baseMessage(res.msg, 'error')
                  }
                  this.loading = false
                  this.disabled = false
                })
                .catch(() => {
                  this.loading = false
                  this.disabled = false
                })
            }
          } else {
            return false
          }
        })
      },
      openwin() {
        var that = this
        that.$refs['bumen'].showDia()
      },
      treeData() {
        areaTreeList().then((res) => {
          if (res.code == 0) {
            this.options = res.data
          }
        })
      },
      choseTheme(subjectValue) {
        console.log(subjectValue)

        //clearable的点击×删除事件
        this.checkDatas = []
      },
    },
  }
</script>
<style lang="scss">
  .el-cascader-panel {
    .el-scrollbar.el-cascader-menu:first-child {
      .el-checkbox {
        display: none !important;
      }
    }
    .el-scrollbar.el-cascader-menu:nth-child(2) {
      .el-checkbox {
        display: none !important;
      }
    }
  }
</style>
