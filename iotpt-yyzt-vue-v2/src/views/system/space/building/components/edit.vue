<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="楼宇名称" prop="floolName">
        <el-input v-model.trim="form.floolName" placeholder="请输入" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="所属楼宇" prop="dictBuilding">
        <el-select v-model="form.dictBuilding" filterable placeholder="请选择" class="w">
          <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.floolName"
          :value="item.id">
        </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="楼宇描述">
        <el-input v-model.trim="form.memo" type="textarea" placeholder="请输入" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
      }}</el-button>
    </div>
    <bumen ref="bumen" @fetch-data=""></bumen>
  </el-dialog>
</template>

<script>
import { allBuildList,addBuild,updateBuild } from '@/api/build.js'

export default {
  name: 'UserManagementEdit',
  data() {
    return {
      loading: false,
      disabled: false,
      form: {
        id: '',
        floolName: '',
        dictBuilding: '',
        memo: '',
      },
      rules: {
        floolName: [
          { required: true, trigger: 'blur', message: '请输入楼宇名称' },
        ],

      },
      title: '',
      dialogFormVisible: false,
      options: [],
    }
  },
  created() {

  },
  methods: {
    showEdit(row) {
      this.getBuils() ;
      if (!row) {
        this.title = '添加'
      } else {
        this.title = '编辑'
        this.form = Object.assign({}, row)

      }
      this.dialogFormVisible = true
    },
    close() {
      this.$refs['form'].resetFields()
      this.form = this.$options.data().form
      this.dialogFormVisible = false
    },
    save() {
      var that = this ;
      if (that.loading) {
        return
      }
      that.$refs['form'].validate(async (valid) => {
        if (valid) {
          that.loading = true
          that.disabled = true
          if(that.form.id){
            updateBuild(that.form).then((res) => {
              if(res.code == 0){
                that.$baseMessage("操作成功", 'success')
                that.$emit('fetch-data')
                that.close()
              }
              that.loading = false
              that.disabled = false
            })
          }else{
            addBuild(that.form).then((res) => {
              if(res.code == 0){
                that.$baseMessage("操作成功", 'success')
                that.$emit('fetch-data')
                that.close()
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
    getBuils(){
      //楼宇下拉
      allBuildList().then((res) => {
        if(res.code == 0){
          this.options = res.data ;
        }
      })
    }
  },
}
</script>
