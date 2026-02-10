<template>
  <el-dialog :visible.sync="dialogFormVisible" width="400px" @close="close" title="上传配置模板">
    <el-upload class="upload-demo" drag action="https://jsonplaceholder.typicode.com/posts/" multiple>
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过500kb</div>
    </el-upload>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'uploadView',
  data() {
    return {
      dialogFormVisible: false,
      loading: false,
      disabled: false,
    }
  },
  created() {},
  methods: {
    showEdit(row) {
      this.dialogFormVisible = true
    },
    close() {
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
  },
}
</script>
<style scoped lang="scss">
.title {
  display: flex;
  margin-top: 20px;

  div:first-child {
    margin-right: 40px;
  }
}
.upload-demo {
  width: 370px;
}
</style>