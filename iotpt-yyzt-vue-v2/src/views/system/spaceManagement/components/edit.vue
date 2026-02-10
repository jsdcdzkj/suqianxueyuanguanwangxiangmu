<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    width="500px"
    @close="close"
  >
    <el-form
      ref="areaForm"
      :model="areaForm"
      :rules="rules"
      label-width="116px"
    >
      <el-form-item label="区域名称" prop="areaName">
        <el-input
          v-model.trim="areaForm.areaName"
          placeholder="请输入区域名称"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="区域类型" prop="areaType">
        <el-select
          v-model="areaForm.areaType"
          placeholder="请选择区域类型"
          style="width: 100%"
        >
          <el-option
            v-for="item in areaTypeList"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属楼宇" prop="buildFloorId">
        <el-select
          v-model="areaForm.buildFloorId"
          filterable
          placeholder="请选择所属楼宇"
          class="w"
          @change="getFloor"
        >
          <el-option
            v-for="item in options2"
            :key="item.id"
            :label="item.buildName"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属楼层" prop="floorId">
        <el-select
          v-model="areaForm.floorId"
          filterable
          placeholder="请选择所属楼层"
          class="w"
        >
          <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.floorName"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="是否对gis展示" prop="isLargeScreenDisplay">
        <el-radio-group v-model="areaForm.isLargeScreenDisplay">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="区域排序" prop="sort">
        <el-input
          v-model.trim="areaForm.sort"
          placeholder="请输入区域序号"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="区域描述">
        <el-input
          v-model.trim="areaForm.memo"
          rows="4"
          type="textarea"
          placeholder="请输入区域描述"
          autocomplete="off"
        ></el-input>
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
  import {
    allBuildFloolList,
    updateBuildArea,
    addBuildArea,
    info,
  } from '@/api/area'
  import { allBuildList } from '@/api/floor'
  import { getRedisDictList } from '@/api/sysDict'

  import bumen from '@/components/bumen'

  export default {
    name: 'UserManagementEdit',
    components: { bumen },
    data() {
      return {
        loading: false,
        disabled: false,
        areaForm: {
          floorId: '',
          areaName: '',
          buildFloorId: '',
          memo: '',
          areaType: '',
          id: '',
          isLargeScreenDisplay: 0,
          sort: '',
        },
        rules: {
          areaName: [
            { required: true, trigger: 'blur', message: '请输入区域名称' },
          ],
          buildFloorId: [
            { required: true, trigger: 'blur', message: '请选择所属楼宇' },
          ],
          floorId: [
            { required: true, trigger: 'blur', message: '请选择所属楼层' },
          ],
          areaType: [
            { required: true, trigger: 'blur', message: '请选择区域类型' },
          ],
          isLargeScreenDisplay: [
            { required: true, trigger: 'blur', message: '请选择是否对gis展示' },
          ],
          sort: [
            { required: true, trigger: 'blur', message: '请输入区域排序' },
          ],
        },
        title: '',
        dialogFormVisible: false,
        options: [],
        options2: [],
        areaTypeList: [],
      }
    },
    created() {},
    methods: {
      showEdit(row, name) {
        this.getAreaTypeList()
        if (this.$refs['areaForm']) {
          this.$refs.areaForm.clearValidate()
        }

        this.options = []
        this.getBuils()
        if (name == 'add') {
          this.title = '添加'
          //楼宇，楼层数据带过来回显
          if (row.buildId != '' || row.buildId != '') {
            this.areaForm.buildFloorId = row.buildId
            this.getFloor()
            this.areaForm.floorId = row.floorId
          }
        } else if (name == 'edit') {
          this.title = '编辑'
          info(row.id).then((res) => {
            if (res.code == 0) {
              this.areaForm = res.data
              this.getFloor()
              this.areaForm.floorId = row.floorId
              if (this.areaForm.isLargeScreenDisplay == null) {
                this.areaForm.isLargeScreenDisplay = 0
              }
            }
          })
        }
        this.dialogFormVisible = true
      },
      close() {
        this.$refs['areaForm'].resetFields()
        this.areaForm = this.$options.data().areaForm
        this.dialogFormVisible = false
      },
      save() {
        var that = this
        if (this.loading) {
          return
        }
        this.$refs['areaForm'].validate(async (valid) => {
          if (valid) {
            this.loading = true
            this.disabled = true
            if (that.areaForm.id) {
              updateBuildArea(that.areaForm).then((res) => {
                if (res.code == 0) {
                  that.$baseMessage('操作成功', 'success')
                  that.$emit('fetch-data')
                  this.close()
                }
                that.loading = false
                that.disabled = false
              })
            } else {
              console.log(that.areaForm)
              addBuildArea(that.areaForm).then((res) => {
                if (res.code == 0) {
                  that.$baseMessage('操作成功', 'success')
                  that.$emit('fetch-data')
                  this.close()
                }
                that.loading = false
                that.disabled = false
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
      getFloor() {
        //楼层下拉
        this.areaForm.floorId = ''
        allBuildFloolList(this.areaForm.buildFloorId).then((res) => {
          if (res.code == 0) {
            this.options = res.data
          }
        })
      },
      getBuils() {
        //楼宇下拉
        allBuildList().then((res) => {
          if (res.code == 0) {
            this.options2 = res.data
          }
        })
      },
      async getAreaTypeList() {
        const { data } = await getRedisDictList({ dictType: 'area_type' })
        this.areaTypeList = data
      },
    },
  }
</script>
