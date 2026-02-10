<template>
  <el-dialog :close-on-click-modal="false" title="编辑" :visible.sync="dialogFormVisible" width="768px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="fjzd-form">
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备名称" prop="pointName">
            <el-input v-model.trim="form.pointName" disabled placeholder="请输入设备名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择设备类型" clearable class="w">
              <el-option
                  v-for="item in this.typeSelectLists"
                  :key="item.id"
                  :label="item.deviceTypeName"
                  :value="String(item.id)"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备编码" prop="deviceId">
            <el-input v-model.trim="form.deviceId" disabled placeholder="请输入设备编码"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="点位号" prop="storagePoint">
            <el-input v-model.trim="form.storagePoint" disabled placeholder="请输入点位号"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="所属区域" prop="areaIds">
            <el-cascader placeholder="请选择所属区域" :props="{ expandTrigger: 'hover' }" :options="areaList" style="width: 100%;" collapse-tags v-model="form.areaIds" @change="choseArea" clearable></el-cascader>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否总开关" prop="isMaster">
            <el-select v-model="form.isMaster" placeholder="请选择是否总开关" clearable class="w">
              <el-option label="是" :value="1"></el-option>
              <el-option label="否" :value="0"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="设备描述" prop="remark">
            <el-input v-model.trim="form.remark" type="textarea" placeholder="请输入设备描述" :rows="3"
              autocomplete="off"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading">{{ loading ? '保存中 ...' : '保存'
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { saveOrUpdate } from '@/api/zmsb'
import {getDeviceTypeList} from '@/api/configDeviceType'
export default {
  name: 'UserManagementEdit',
  props:['detail','areaList'],
  data() {
    return {
      value: '',
      loading: false,
      disabled: false,
      form: {
        pointName:'',
        deviceId:'',
        storagePoint:'',
        buildId:'',
        floorId:'',
        areaId:'',
        areaIds:[],
        isMaster:'',
        remark:'',
        type:''
      },
      rules: {
        type:[
          { required: true, trigger: 'change', message: '请选择设备类型'},
        ],
        areaIds: [
          { required: true, trigger: 'change', message: '请选择所属区域'},
        ],
      },
      dialogFormVisible: false,
      typeSelectLists:[],
    }
  },
  created(){
    getDeviceTypeList().then(res=>{
      this.typeSelectLists = res.data;
    })
  },
  methods: {
    showEdit(data) {
      this.form = Object.assign({},data);
      this.dialogFormVisible = true;
      this.$nextTick(()=>{
        this.$refs['form'].clearValidate();
      })
    },
    choseArea(val){
      if(val.length===0){
        this.form.buildId = '';
        this.form.floorId = '';
        this.form.areaId = '';
      }else{
        this.form.buildId = val[0];
        this.form.floorId = val[1];
        this.form.areaId = val[2];
      }
    },
    close() {
      this.dialogFormVisible = false;
      this.form = this.$options.data().form;
    },
    save() {
      this.$refs['form'].validate(async (valid) => {
        if (valid) {
          this.loading = true;
          let formData = Object.assign({}, this.form);
          saveOrUpdate(formData).then(res => {
            if (0 == res.code) {
              this.$baseMessage("修改成功", 'success');
              this.$emit('fetch-data');
              this.close();
            } else {
              if(res.msg != ""){
                this.$baseMessage(res.msg, 'error');
              }else{
                this.$baseMessage("修改失败:", 'error');
              }
            }
          }).finally(()=>{
            this.loading = false;
          })
        } else {
          return false
        }
      })
    }
  },
}
</script>
<style lang="scss">
.fjzd-form {
  padding-right: 12px !important;
}
</style>
