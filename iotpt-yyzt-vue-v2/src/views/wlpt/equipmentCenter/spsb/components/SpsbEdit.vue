<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="768px" @close="close">
    <!-- <div slot="title" class="header-title el-dialog__title">
        <span class="title-left">{{ title }}</span>
        <span> 设备信息  </span>
    </div> -->

    <el-form ref="form" :model="form" :rules="rules" label-width="127px" class="fjzd-form">
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备名称" prop="name">
            <el-input v-model.trim="form.name" autocomplete="off" placeholder="请输入设备名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备编码" prop="deviceCode">
            <el-input v-model.trim="form.deviceCode" autocomplete="off" placeholder="请输入设备编码"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
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
        </el-col>
        <el-col :span="12">
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
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
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
        </el-col>
        <el-col :span="12">
          <el-form-item label="安装位置" prop="installPosition">
            <el-input v-model.trim="form.installPosition" autocomplete="off" placeholder="请输入安装位置"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="供应商名称" prop="supplierId">
            <el-select v-model="form.supplierId" placeholder="请选择供应商" clearable @change="supplierChange" class="w">
          <el-option
                v-for="item in this.supplierList"
                :key="item.id"
                :label="item.supplierName"
                :value="item.id"
              ></el-option>
        </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
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
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备通道号" prop="username">
            <el-input v-model.trim="form.channel" autocomplete="off" placeholder="请输入设备通道号"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="NVR通道号" prop="nvrChannel">
            <el-input v-model.trim="form.nvrChannel" autocomplete="off" placeholder="请输入NVR通道号"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="窗口号" prop="windowNum">
            <el-input v-model.trim="form.windowNum" autocomplete="off" placeholder="请输入窗口号"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="硬盘机id" prop="nvrId">
            <el-input v-model.trim="form.nvrId" autocomplete="off" placeholder="请输入硬盘机id"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="ip地址" prop="ip">
            <el-input v-model.trim="form.ip" autocomplete="off" placeholder="请输入ip地址"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备端口" prop="port">
            <el-input v-model.trim="form.port" autocomplete="off" placeholder="请输入设备端口"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备经度" prop="pointX">
            <el-input v-model.trim="form.pointX" autocomplete="off" placeholder="请输入设备经度"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备纬度" prop="pointY">
            <el-input v-model.trim="form.pointY" autocomplete="off" placeholder="请输入设备纬度"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="拍摄区域横坐标" prop="catchX">
            <el-input v-model.trim="form.catchX" autocomplete="off" placeholder="请输入拍摄区域横坐标"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="拍摄区域纵坐标" prop="catchY">
            <el-input v-model.trim="form.catchY" autocomplete="off" placeholder="请输入拍摄区域纵坐标"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="h5token" prop="token">
            <el-input v-model.trim="form.token" autocomplete="off" placeholder="请输入h5token"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否视频流" prop="isVideoStream">
            <el-radio-group v-model="form.isVideoStream">
              <el-radio :label="1" value="1">是</el-radio>
              <el-radio :label="0" value="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="设备状态" prop="status">
            <el-radio-group v-model="form.status">
                    <el-radio :label="1" value="1">启用</el-radio>
                    <el-radio :label="0" value="0">禁用</el-radio>
                </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备描述" prop="deviceDesc">
            <el-input v-model.trim="form.deviceDesc" type="textarea" placeholder="请输入设备描述" :rows="3" autocomplete="off"></el-input>
          </el-form-item>
        </el-col>
      </el-row>



    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { saveOrUpdate } from '@/api/spsb'
import {getBuildAreaList} from '@/api/sysBuildArea'
import {getBuildList} from '@/api/sysBuild'
import {getBuildFloorList} from '@/api/sysBuildFloor'
import {getConfigModelList} from '@/api/configModel'
import {getConfigSupplierList} from '@/api/configSupplier'
export default {
  name: 'UserManagementEdit',
  data() {
    return {
      value:'',
      loading: false,
      disabled: false,
      form: {
        status: 1,
        isVideoStream: 1,
        modelNum: ''
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
        deviceCode: [{ required: true, trigger: 'blur', message: '请输入设备编码' }],
      },
      title: '',
      dialogFormVisible: false,
    }
  },
  created() {},
  methods: {
    showEdit(row) {
      this.buildListData();
      //this.modelListData();
      this.supplierListData();
      if (!row) {
        this.title = '添加'
      } else {
        this.title = '编辑'
        this.buildFloorListData(row.buildId);
        this.areaListData(row.floorId);
        this.form = Object.assign({}, row)
      }

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
      this.buildFloorList = data;
    },
    async areaListData(floorId) {
      const {data} = await getBuildAreaList({'floorId': floorId});
      this.areaList = data;
    },
    async modelListData(supplierId) {
      const {data} = await getConfigModelList({"supplierId": supplierId});
      this.modelList = data;
    },
    async supplierListData() {
      const {data} = await getConfigSupplierList();
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
<style lang="scss">
.fjzd-form {
  padding-right: 12px !important;
}
</style>
<style lang="scss" scoped>
.header-title {
  text-align: center;
  position: relative;

  .title-left {
    position: absolute;
    left: 0;
  }
}
</style>
