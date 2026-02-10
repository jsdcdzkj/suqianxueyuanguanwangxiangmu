<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px" @close="close">
    <el-form ref="form" :model="form" :rules="rules" label-width="110px">
      <el-form-item label="设备名称" prop="name">
        <el-input v-model.trim="form.name" autocomplete="off" placeholder="请输入设备名称" ></el-input>
      </el-form-item>
      <el-form-item label="设备编码" prop="deviceCode">
        <el-input v-model.trim="form.deviceCode" autocomplete="off" placeholder="请输入设备编码"></el-input>
      </el-form-item>
      <el-form-item label="所属楼宇" prop="buildId">
        <el-select v-model="form.buildId" placeholder="请选择所属楼宇" clearable @change="buildChange" class="w">
          <el-option
                v-for="item in this.buildList"
                :key="item.id"
                :label="item.buildName"
                :value="item.id"
              ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属楼层" prop="floorId">
        <el-select v-model="form.floorId" placeholder="请选择所属楼层" clearable @change="floorChange" class="w">
          <el-option
                v-for="item in this.buildFloorList"
                :key="item.id"
                :label="item.floorName"
                :value="item.id"
              ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属区域" prop="areaId">
        <el-select v-model="form.areaId" placeholder="请选择所属区域" clearable class="w">
          <el-option
                v-for="item in this.areaList"
                :key="item.id"
                :label="item.areaName"
                :value="item.id"
              ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="供应商" prop="supplierId">
        <el-select v-model="form.supplierId" placeholder="请选择供应商" clearable @change="supplierChange" class="w">
          <el-option
                v-for="item in this.supplierList"
                :key="item.id"
                :label="item.supplierName"
                :value="item.id"
              ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备型号" prop="modelNum">
        <el-select v-model="form.modelNum" placeholder="请选择设备型号" clearable class="w">
          <el-option
                v-for="item in this.modelList"
                :key="item.id"
                :label="item.modelName"
                :value="item.id"
              ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备描述" prop="deviceDesc">
        <el-input v-model.trim="form.deviceDesc" type="textarea" placeholder="请输入设备描述" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="设备状态">
                <el-radio-group v-model="form.status">
                    <el-radio :label="1" value="1">启用</el-radio>
                    <el-radio :label="0" value="0">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { saveOrUpdate } from '@/api/mjsb'
import {getBuildAreaList} from '@/api/sysBuildArea'
import {getBuildList} from '@/api/sysBuild'
import {getBuildFloorList} from '@/api/sysBuildFloor'
import {getConfigModelList} from '@/api/configModel'
import {getConfigSupplierList} from '@/api/configSupplier'

export default {
  name: 'MjsbEdit',
  components: {  },
  data() {
    return {
      loading: false,
      disabled: false,
      form: {
        id: '',
        name: '',
        deviceCode: '',
        areaId: '',
        floorId: '',
        buildId: '',
        modelNum: '',
        deviceDesc: '',
        supplierId: '',
        status: 1,
      },
      areaList: [],
      buildList: [],
      buildFloorList: [],
      userList: [],
      modelList: [],
      supplierList: [],
      rules: {
        name: [
          { required: true, trigger: 'blur', message: '请输入设备名称' },
        ],
        deviceCode: [
          { required: true, trigger: 'blur', message: '请输入设备编码' },
        ],
        areaId: [
          { required: true, trigger: 'change', message: '请选择所属区域', type: 'number' },
        ],
        floorId: [
          { required: true, trigger: 'change', message: '请选择所属楼层', type: 'number' },
        ],
        buildId: [
          { required: true, trigger: 'change', message: '请选择所属楼宇', type: 'number' },
        ],
      },
      title: '',
      dialogFormVisible: false,
    }
  },
  created() { },
  methods: {
    showEdit(row) {
      if (!row) {
        this.title = '添加'
      } else {
        this.title = '编辑'
        this.buildFloorListData(row.buildId);
        this.areaListData(row.floorId);
        this.form = Object.assign({}, row)
      }
      this.buildListData();
      //this.buildFloorListData();
      //this.areaListData();
      //this.modelListData();
      this.supplierListData();
      this.dialogFormVisible = true
    },
    async buildListData() {
      const {data} = await getBuildList();
      console.log("所属楼宇", data)
      this.buildList = data;
    },
    async buildChange(v){
      this.buildFloorList = []
      this.areaList = []
      this.form.floorId = ''
      this.form.areaId = ''
      if(v){
this.buildFloorListData(v)
      }
    },
    async floorChange(v){
      this.areaList = []
      this.form.areaId = ''
      if(v){
this.areaListData(v)
      }

    },
    async buildFloorListData(buildId) {
      const {data} = await getBuildFloorList({'dictBuilding': buildId});
      console.log("所属楼层", data)
      this.buildFloorList = data;
    },
    async areaListData(floorId) {
      const {data} = await getBuildAreaList({'floorId': floorId});
      console.log("所属区域", data)
      this.areaList = data;
    },
    async modelListData(supplierId) {
      const {data} = await getConfigModelList({"supplierId": supplierId});
      console.log("设备型号", data)
      this.modelList = data;
    },
    async supplierListData() {
      const {data} = await getConfigSupplierList();
      console.log("供应商", data)
      this.supplierList = data;
    },
    supplierChange(e){
      if(null == e || '' == e){
        this.form.modelNum = '';
        this.modelList = [];
      }else{
        this.form.modelNum = '';
        this.modelListData(e)
      }
      console.log(e)
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

          saveOrUpdate(this.form).then(res => {
            if(0 == res.code){

              this.$baseMessage(this.title + "成功", 'success')
              this.$emit('fetch-data')
              this.close()
            }else{
              this.$baseMessage(this.title + "失败", 'error')
            }

          })
        } else {
          return false
        }
      })
    },
    openwin() {
      var that = this
      that.$refs['bumen'].showDia()
    },
  },
}
</script>
