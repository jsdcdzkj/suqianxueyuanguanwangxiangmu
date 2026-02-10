<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="主题名称" prop="topicName">
        <el-row :gutter="20">
          <el-col :span="20">
            <el-input v-model.trim="form.topicName" :disabled="true" autocomplete="off" placeholder=""></el-input>
          </el-col>

        </el-row>
      </el-form-item>
      <el-form-item label="模板名称" prop="transferId">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-select v-model="form.transferId" placeholder="请选择模板" class="w">
              <el-option
                v-for="item in this.templateList"
                :key="item.id"
                :label="item.modelName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="4">
            <el-button @click="openView(form)" type="primary">新增模板</el-button>
          </el-col>
        </el-row>
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
  import {getEntity, saveOrUpdate} from '@/api/configTopic'
  import {getList} from '@/api/configDataTransfer'

  export default {
    name: 'ConfigurationTemplate',
    components: {},
    data() {
      return {
        loading: false,
        disabled: false,
        form: {
          id: '',
          topicName: '',
          transferId: '',
        },
        rules: {
          transferId: [
            {required: true, trigger: 'blur', message: '请选择模板'},
          ],
        },
        title: '',
        dialogFormVisible: false,
        templateList: [],
        topicType: '',
      }
    },
    created() {
    },
    methods: {
      showEdit(row) {
        this.title = '配置模板';
        this.dialogFormVisible = true
        this.topicType = row.topicType

        //模板列表
        this.templateListData(row.topicType);

        // this.form = Object.assign({}, row)
        this.getData(row.id)
      },
      openView(row) {
        this.close()
        //订阅主题
        if (this.topicType == 1) {
          this.$parent.openManagerView(row.id)
        } else {
          this.$parent.openDataManagerFbEdit(row.id)
        }
      },
      close() {
        this.$refs['form'].resetFields();
        this.form = this.$options.data().form;
        this.dialogFormVisible = false
      },
      async templateListData(type) {
        const {data} = await getList({modelType: type})
        console.log(data)
        this.templateList = data;
      },
      async getData(id) {
        const {data} = await getEntity({id: id});
        console.log(data)
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
