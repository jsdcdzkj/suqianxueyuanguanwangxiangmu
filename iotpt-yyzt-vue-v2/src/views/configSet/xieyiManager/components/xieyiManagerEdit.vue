<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="协议名称" prop="protocolName">
        <el-input v-model.trim="form.protocolName" autocomplete="off" placeholder="请输入协议名称"></el-input>
      </el-form-item>
      <el-form-item label="协议说明" prop="protocolDesc">
        <el-input type="textarea" v-model="form.protocolDesc" placeholder="请输入协议说明"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">
        {{ loading ? '确定中 ...' : '确定' }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {getEntity, saveOrUpdate} from '@/api/configProtocol'

  export default {
    name: 'xieyiManagerEdit',
    components: {},
    data() {
      return {
        loading: false,
        disabled: false,
        form: {
          id: '',
          protocolName: '',
          protocolDesc: '',
          isDel: 0,
        },
        rules: {
          protocolName: [
            {required: true, trigger: 'blur', message: '请输入协议名称'},
          ],
          // protocolDesc: [
          //   { required: true, trigger: 'blur', message: '请输入协议说明' },
          // ],
        },
        title: '',
        dialogFormVisible: false,
      }
    },
    created() {
    },
    methods: {
      showEdit(row) {
        if (!row) {
          this.title = '添加'
        } else {
          this.title = '编辑'
          // this.form = Object.assign({}, row)
          this.getData(row)
        }
        this.dialogFormVisible = true
      },
      close() {
        this.$refs['form'].resetFields()
        this.form = this.$options.data().form
        this.dialogFormVisible = false
      },
      async getData(id) {
        const {data} = await getEntity({id: id});
        this.form = data;
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
    },
  }
</script>
