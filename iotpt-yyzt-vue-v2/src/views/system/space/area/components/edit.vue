<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="区域名称" prop="areaName">
        <el-input v-model.trim="form.areaName" placeholder="请输入" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="所属楼宇" prop="buildFloorId">
        <el-select v-model="form.buildFloorId" filterable placeholder="请选择" class="w" @change="getFloor">
          <el-option
            v-for="item in options2"
            :key="item.id"
            :label="item.floolName"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属楼层" prop="floorId" v-if="isShow">
        <el-select v-model="form.floorId" filterable placeholder="请选择" class="w">
          <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.floolName"
          :value="item.id">
        </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="区域编码" prop="code">
        <el-input v-model.trim="form.code" placeholder="请输入" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="区域说明">
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
import { allBuildFloolList,updateBuildArea,addBuildArea,info } from '@/api/area'
import { allBuildList } from '@/api/floor'

export default {
  name: 'UserManagementEdit',
  data() {
    return {
      loading: false,
      disabled: false,
      isShow: false,
      form: {
        areaName: '',
        code: '',
        floorId: '',
        memo:'',
        buildFloorId:'',
      },
      rules: {
        areaName: [
          { required: true, trigger: 'blur', message: '请输入区域名称' },
        ],
        floorId: [
          { required: true, trigger: 'blur', message: '请选择所属楼层' },
        ],
        code: [
          { required: true, trigger: 'blur', message: '请输入区域编码' },
        ],
        buildFloorId: [
          { required: true, trigger: 'blur', message: '请选择所属楼宇' },
        ],

      },
      title: '',
      dialogFormVisible: false,
      options: [],
      options2: [],
    }
  },
  created() { },
  methods: {
    showEdit(row) {
      this.getBuils() ;
      if (!row) {
        this.title = '添加'
        this.isShow = false ;
      } else {
        this.title = '编辑'
        // this.form = Object.assign({}, row)
        this.getInfo(row.id)
        this.isShow = true ;
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
        var that  = this ;
        if (valid) {
          that.loading = true
          that.disabled = true
          if(that.form.id){
            updateBuildArea(that.form).then((res) => {
              if(res.code == 0){
                that.$baseMessage("操作成功", 'success')
                that.$emit('fetch-data')
                that.close()
              }
              that.loading = false
              that.disabled = false
            })
          }else{
            addBuildArea(that.form).then((res) => {
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
    getFloor(){
      //楼层下拉
      this.form.floorId = ""
      allBuildFloolList(this.form.buildFloorId).then((res) => {
        if(res.code == 0){
          this.options = res.data ;
          this.isShow=true ;
        }
      })
    },
    getBuils(){
      //楼宇下拉
      allBuildList().then((res) => {
        if(res.code == 0){
          this.options2 = res.data ;
        }
      })
    },
    getInfo(id){
      //楼宇下拉
      var that = this ;
      info(id).then((res) => {
        if(res.code == 0){

          this.options =res.data.sysBuildFloorList ;
          that.form = res.data ;
        }
      })
    },

  },
}
</script>
