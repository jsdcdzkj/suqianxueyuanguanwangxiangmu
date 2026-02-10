<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    width="500px"
    @close="close"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="场景名称" prop="sceneName">
        <el-input
          v-model.trim="form.sceneName"
          placeholder="请输入场景名称"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="默认场景" prop="isCheck">
        <el-radio v-model="form.isCheck" label="0">否</el-radio>
        <el-radio v-model="form.isCheck" label="1">是</el-radio>
      </el-form-item>
      <el-form-item label="场景描述" props="memo">
        <el-input
          v-model.trim="form.memo"
          type="textarea"
          placeholder="请输入场景描述"
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
  </el-dialog>
</template>

<script>
import { updateScene, addScene } from '@/api/scene.js'

export default {
  name: 'Add',
  props: {
    logicalInfo: {
      type: Object,
      default: {}
    },
    sceneDeviceList: {
      type: [Array, Object],
      default: []
    },
    sceneInfo: {
      type: Object,
      default: {}
    },
  },
  data() {
    return {
      loading: false,
      disabled: false,
      form: {
        sceneName: '',
        isCheck: '',
        memo: '',
      },
      rules: {
        sceneName: [
          { required: true, trigger: 'blur', message: '请输入楼宇名称' },
        ],
        isCheck: [
          { required: true, trigger: 'blur', message: '请选择所属部门' },
        ],
      },
      title: '',
      dialogFormVisible: false,
    }
  },
  created() {},
  methods: {
    showEdit(data) {
      if (data) {
        this.title = '新增场景'
      } else {
        this.title = '更新场景'
        this.form = Object.assign({}, this.sceneInfo)
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
          const arr = Object.values(this.sceneDeviceList).flat()
          this.form.sceneDeviceList = arr.map(item => {
            item.status = item.switchStatus
            return item
          })
          this.form.logicalAreaId = this.logicalInfo.logicalAreaId
          this.form.logicalFloorId = this.logicalInfo.logicalFloorId
          this.form.logicalBuildId = this.logicalInfo.logicalBuildId
          let res = ''
          if (this.title === '新增场景') {
            res = await addScene(this.form)
            this.$emit('fetch-data', 'add', res.data)
          } else {
            res = await updateScene(this.form)
            this.$emit('handle-search')
          }
          this.$baseMessage(res.msg, 'success')
          
          this.close()
        } else {
          return false
        }
      })
    },
  },
}
</script>
