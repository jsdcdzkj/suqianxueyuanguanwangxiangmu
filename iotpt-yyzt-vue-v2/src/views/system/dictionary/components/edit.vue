<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    width="500px"
    @close="close"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="字典名称" prop="dictLabel">
        <el-input
          v-model.trim="form.dictLabel"
          autocomplete="off"
          placeholder="请输入"
        ></el-input>
      </el-form-item>
      <el-form-item label="字典值" prop="dictValue">
        <el-input
          v-model.trim="form.dictValue"
          autocomplete="off"
          placeholder="请输入"
        ></el-input>
      </el-form-item>
      <el-form-item label="字典标识" prop="dictType">
        <el-input
          v-model.trim="form.dictType"
          autocomplete="off"
          placeholder="请输入"
        ></el-input>
      </el-form-item>
      <el-form-item label="字典类型名称" prop="dictTypeName">
        <el-input
          v-model.trim="form.dictTypeName"
          autocomplete="off"
          placeholder="请输入"
        ></el-input>
      </el-form-item>
      <el-form-item label="icon">
        <el-input
          v-model.trim="form.colour"
          autocomplete="off"
          placeholder="请输入"
        ></el-input>
      </el-form-item>
      <!--            <el-form-item label="所属父级" prop="checkedRoles">-->
      <!--                <el-select v-model="form.parentId" class="w" filterable placeholder="请选择">-->
      <!--                    <el-option value="-1" label="请选择">请选择</el-option>-->
      <!--                    <el-option v-for="item in options" :key="item.id" :label="item.dictLabel" :value="item.id"-->
      <!--                               :disabled="item.id === form.id"></el-option>-->
      <!--                </el-select>-->
      <!--            </el-form-item>-->
      <el-form-item label="排序">
        <el-input
          v-model.trim="form.sort"
          type="number"
          autocomplete="off"
          placeholder="请输入"
        ></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="memo">
        <el-input
          type="textarea"
          v-model="form.memo"
          placeholder="请输入描述"
        ></el-input>
      </el-form-item>
      <!--      <el-form-item label="字典状态">-->
      <!--        <el-radio-group v-model="radio">-->
      <!--          <el-radio :label="0">启用</el-radio>-->
      <!--          <el-radio :label="1">禁用</el-radio>-->
      <!--        </el-radio-group>-->
      <!--      </el-form-item>-->
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
  import { saveOrUpdate, getList } from '@/api/dict'
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
          dictLabel: '',
          dictValue: '',
          dictType: '',
          parentId: '',
          dictTypeName: '',
          colour: '',
          memo: '',
          sort: '',
        },
        radio: 0,
        rules: {
          dictLabel: [
            { required: true, trigger: 'blur', message: '请输入字典名称' },
          ],
          dictValue: [
            { required: true, trigger: 'blur', message: '请输入字典值' },
          ],
          dictType: [
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
    created() {
      this.getOptions()
    },
    methods: {
      showEdit(row) {
        if (!row) {
          this.title = '添加'
        } else {
          this.title = '编辑'
          this.form = Object.assign({}, row)
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
          if (this.form.sort !== '' && this.form.sort != null) {
            if (this.form.sort < 0) {
              this.$baseMessage('排序不能小于0', 'warning')
              return false
            }
          }
          if (valid) {
            this.loading = true
            this.disabled = true
            setTimeout(() => {
              this.loading = false
              this.disabled = false
            }, 1000)

            saveOrUpdate(this.form).then((res) => {
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
