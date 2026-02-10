<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="110px">
      <el-form-item label="模板名称" prop="modelName">
        <el-input v-model.trim="form.modelName" autocomplete="off" placeholder="请输入模板名称"></el-input>
      </el-form-item>
      <el-form-item label="模板编码" prop="modelCode">
        <el-input v-model.trim="form.modelCode" autocomplete="off" placeholder="请输入模板编码"></el-input>
      </el-form-item>
      <el-form-item label="下发指令格式" prop="polymerization">
        <el-input v-model.trim="form.polymerization" type="textarea" placeholder="请输入下发指令格式" :rows="3"
                  autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="模板描述" prop="modelDesc">
        <el-input v-model.trim="form.modelDesc" type="textarea" placeholder="请输入模板描述" autocomplete="off"></el-input>
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
  // 系统对接模板

  import {getEntity, saveOrUpdate} from '@/api/configDataTransfer'

  export default {
    name: 'configDataTransferSysEdit',
    components: {},
    data() {
      return {
        loading: false,
        disabled: false,
        form: {
          id: '',
          modelName: '',
          modelCode: '',
          modelType: '2',
          polymerization: '',
          modelDesc: '',
        },
        rules: {
          modelName: [
            {required: true, trigger: 'blur', message: '请输入模板名称'},
          ],
          modelCode: [
            {required: true, trigger: 'blur', message: '请输入模板编码'},
          ],
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
          this.getData(row);
        }
        this.dialogFormVisible = true

        this.$nextTick(() => {
          if (this.$refs['form']) {
            this.$refs['form'].resetFields()
          }
        });
      },
      showEdit2(configTopicId) {
        this.title = '添加'
        this.form.configTopicId = configTopicId;
        console.log(configTopicId)
        console.log(this.form.configTopicId)
        this.dialogFormVisible = true

        this.$nextTick(() => {
          if (this.$refs['form']) {
            this.$refs['form'].resetFields()
          }
        });
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

            const data = await saveOrUpdate(this.form)
            if (data.code == 0) {
              this.$baseMessage(data.msg, 'success')
              this.$emit('fetch-data')
              this.close()
            } else {
              this.$baseMessage(data.msg, 'error')
            }
          } else {
            return false
          }
        })
      },
    },
  }
</script>
