<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    width="600px"
    @close="close"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="证书名称" prop="name">
        <el-input
          v-model.trim="form.name"
          autocomplete="off"
          placeholder="请输入"
        ></el-input>
      </el-form-item>
      <el-form-item label="有效时间" prop="time">
        <el-date-picker
          v-model="form.time"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd HH:mm:ss"
          format="yyyy-MM-dd HH:mm:ss"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input
          v-model.trim="form.phones"
          autocomplete="off"
          placeholder="请输入手机号，多个手机号以逗号隔开"
        ></el-input>
      </el-form-item>
      <el-form-item label="域名">
        <el-input
          v-model.trim="form.domainName"
          autocomplete="off"
          placeholder="请输入域名"
        ></el-input>
      </el-form-item>
      <el-form-item label="是否启用" prop="status">
        <el-switch
          v-model="form.status"
          active-text="启用"
          inactive-text="禁用"
        ></el-switch>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button
        :disabled="disabled"
        :loading="loading"
        type="primary"
        @click="save"
      >
        {{ loading ? '确定中 ...' : '确定' }}
      </el-button>
    </div>
    <!--    <bumen ref="bumen" @fetch-data=""></bumen>-->
  </el-dialog>
</template>

<script>
  import { saveOrUpCredential } from '@/api/build'
  // import bumen from '@/components/bumen'

  export default {
    name: 'dictEdit',
    // components: {bumen},
    data() {
      return {
        loading: false,
        disabled: false,
        form: {
          id: '',
          time: [],
          status: false,
          name: '',
          phones: '',
          domainName: '',
        },
        radio: 0,
        rules: {
          time: [
            { required: true, trigger: 'blur', message: '请选择有效时间' },
          ],
          name: [
            { required: true, trigger: 'blur', message: '请输入字典类型' },
          ],
          // parentId: [
          //     {required: true, trigger: 'blur', message: '请选择所属父级'},
          // ],
        },
        title: '',
        dialogFormVisible: false,
        options: [],
      }
    },
    methods: {
      showEdit(row) {
        if (!row) {
          this.title = '添加'
        } else {
          this.title = '编辑'
          this.form = Object.assign({}, row)
          this.form = {
            name: row.name,
            time: [row.startTime, row.endTime],
            status: row.status == 1 ? false : true,
            id: row.id,
            domainName: row.domainName,
            phones: row.phones,
          }
          // 转string
          // this.form.parentId = row.parentId.toString()
          if (row.parentId === -1) {
            this.form.parentId = this.form.parentId.toString()
          }
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
            const data = {
              name: this.form.name,
              startTime: this.form.time[0],
              endTime: this.form.time[1],
              status: this.form.status ? 0 : 1,
              id: this.form.id,
              phones: this.form.phones,
              domainName: this.form.domainName,
            }

            if (!this.form.id) {
              delete data.id
            }

            saveOrUpCredential(data).then((res) => {
              if (-1 == res.code) {
                this.$baseMessage(res.msg, 'error')
              } else {
                this.$baseMessage('保存' + res.msg, 'success')
                this.$emit('fetch-data')
                this.close()
              }
            })
          } else {
            return false
          }
        })
      },
      async getOptions() {
        const { data } = await getList()
        this.options = data
      },
      // openwin() {
      //   var that = this
      //   that.$refs['bumen'].showDia()
      // },
    },
  }
</script>
