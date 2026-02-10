<template>
 <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="768px" @close="close">
    <!-- <div slot="title" class="header-title el-dialog__title">
        <span class="title-left">{{ title }}</span>
        <span> 设备信息  </span>
    </div> -->

    <el-form ref="form" :model="form" :rules="rules" label-width="90px" class="fjzd-form">
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
          <el-form-item label="所属区域" prop="areaId">
            <el-cascader
              placeholder="请选择所属区域"
              :options="areaList"
              style="width: 100%;"
              :show-all-levels="false"
              collapse-tags
              ref="areaId"
              v-model="areaIds"
              @change="choseArea"
              clearable></el-cascader>
          </el-form-item>
        </el-col>
        <!-- <el-col :span="12"> -->
          <!-- <el-form-item label="所属楼层" prop="floorId">
            <el-select v-model="form.floorId" placeholder="请选择" class="w" clearable @change="getArea()">
              <el-option v-for="item in floorList" :key="item.id" :label="item.floorName" :value="item.id"></el-option>
            </el-select>
          </el-form-item> -->
        <!-- </el-col> -->
        <el-col :span="12">
          <el-form-item label="供应商" prop="supplierId">
            <el-select v-model="form.supplierId" placeholder="请选择供应商" class="w" clearable @change="getModel()">
              <el-option v-for="item in supplierList" :key="item.id" :label="item.supplierName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>

        <el-col :span="12">
          <el-form-item label="品牌" prop="brand">
            <el-input v-model.trim="form.brand" autocomplete="off" placeholder="请输入品牌名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备型号" prop="modelId">
            <el-select v-model="form.modelId" placeholder="请选择设备型号" class="w" clearable>
              <el-option v-for="item in modelList" :key="item.id" :label="item.modelName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>

        <el-col :span="12">
          <el-form-item label="出厂编号" prop="serialNum">
            <el-input v-model.trim="form.serialNum" autocomplete="off" placeholder="请输入出厂编号"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="传输协议" prop="configProtocolId">
            <el-select v-model="form.configProtocolId" placeholder="请选择传输协议" class="w" clearable>
              <el-option v-for="item in configProtocolList" :key="item.id" :label="item.protocolName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>

        <el-col :span="12">
          <el-form-item label="服务连接" prop="configLinkId">
            <el-select v-model="form.configLinkId" placeholder="请选择服务连接" class="w" clearable>
              <el-option v-for="item in configLinkList" :key="item.id" :label="item.linkName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="发布主题" prop="publishTopicId">
            <el-select v-model="form.publishTopicId" placeholder="请选择发布主题" class="w" clearable>
              <el-option v-for="item in publishTopicList" :key="item.id" :label="item.topicCode" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>

        <el-col :span="12">
          <el-form-item label="订阅主题" prop="subscribeTopicId">
            <el-select v-model="form.subscribeTopicId" placeholder="请选择订阅主题" class="w" clearable>
              <el-option v-for="item in subscribeTopicList" :key="item.id" :label="item.topicCode" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <!-- <el-col :span="12">
          <el-form-item label="转换模板" prop="transferId">
            <el-select v-model="form.transferId" placeholder="请选择转换模板" class="w" clearable>
              <el-option v-for="item in transferList" :key="item.id" :label="item.modelName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col> -->
      </el-row>
      <el-row>

        <el-col :span="12">
          <el-form-item label="告警主题" prop="alarmTopicId">
            <el-select v-model="form.alarmTopicId" placeholder="请选择告警主题" class="w" clearable>
              <el-option v-for="item in alarmTopicList" :key="item.id" :label="item.topicCode" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="心跳主题" prop="heartTopicId">
            <el-select v-model="form.heartTopicId" placeholder="请选择心跳主题" class="w" clearable>
              <el-option v-for="item in heartTopicList" :key="item.id" :label="item.topicCode" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>

        <el-col :span="24">
          <el-form-item label="设备描述" prop="deviceDesc">
            <el-input v-model.trim="form.deviceDesc" type="textarea" placeholder="请输入设备描述" :rows="2" autocomplete="off"></el-input>
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
import { getTransferList } from '@/api/configDataTransfer'
import { getConfigLinkList } from '@/api/configLink'
import { getConfigModelList } from '@/api/configModel'
import { getConfigProtocolList } from '@/api/configProtocol'
import { getConfigSupplierList } from '@/api/configSupplier'
import { getList } from '@/api/configTopic'
import { saveOrUpdate } from '@/api/deviceGateway'
import { areaTreeList } from '@/api/org'



export default {
  name: 'WgsbEdit',
  data() {
    return {
      value:'',
      loading: false,
      disabled: false,
      buildList:null,
      floorList:null,
      areaList:null,
      supplierList:null,
      modelList:null,
      configProtocolList:null,
      configLinkList:null,
      publishTopicList:null,
      subscribeTopicList:null,
      transferList: null,
      alarmTopicList: null,
      heartTopicList:null,
      areaList: null,
      areaIds: [],
      checkAreaDatas:[],
      form: {
        name:'',
        deviceCode:'',
        buildId:'',
        floorId:'',
        areaId:'',
        place:'',
        supplierId:'',
        brand:'',
        modelId:'',
        serialNum:'',
        configProtocolId:'',
        configLinkId:'',
        publishTopicId:'',
        subscribeTopicId:'',
        transferId:'',
        deviceDesc: '',
        alarmTopicId: '',
        heartTopicId:'',
      },
      rules: {
        name: [
          { required: true, trigger: 'blur', message: '请输入设备名称' },
        ],
        deviceCode: [{ required: true, trigger: 'blur', message: '请输入设备编码' }],
        buildId: [
          { required: true, trigger: 'change', message: '请选择所属楼宇' },
        ],
        floorId: [
          { required: true, trigger: 'change', message: '请选择所属楼层' },
        ],
        areaId: [
          { required: true, trigger: 'change', message: '请选择所属区域' },
        ],
        place: [
          { required: true, trigger: 'blur', message: '请输入安装位置' },
        ],
        supplierId: [
          { required: true, trigger: 'change', message: '请选择所属供应商' },
        ],
        brand: [
          { required: true, trigger: 'blur', message: '请输入品牌' },
        ],
        modelId: [
          { required: true, trigger: 'change', message: '请选择设备型号' },
        ],
        serialNum: [
          { required: true, trigger: 'blur', message: '请输入出厂编号' },
        ],
        configProtocolId: [
          { required: true, trigger: 'change', message: '请选择传输协议' },
        ],
        configLinkId: [
          { required: true, trigger: 'change', message: '请选择服务连接' },
        ]
        // ,
        // publishTopicId: [
        //   { required: true, trigger: 'change', message: '请选择发布主题' },
        // ],
        // subscribeTopicId: [
        //   { required: true, trigger: 'change', message: '请选择订阅主题' },
        // ]
        // ,
        // alarmTopicId: [
        //   { required: true, trigger: 'change', message: '请选择报警主题' },
        // ],
        // heartTopicId: [
        //   { required: true, trigger: 'change', message: '请选择心跳主题' },
        // ]
        // ,
        // transferId: [
        //   { required: true, trigger: 'change', message: '请选择转换模板' },
        // ]
        ,
        deviceDesc: [
          { required: true, trigger: 'blur', message: '请输入设备描述' },
        ],
      },
      title: '',
      dialogFormVisible: false,
    }
  },
  created() {},
  methods: {
    showEdit(row) {
      if (!row) {
        this.title = '添加'
      } else {
        this.title = '编辑'
        this.form = Object.assign({}, row)
        this.areaIds.push(this.form.buildId+"")
        this.areaIds.push(this.form.floorId+"")
        this.areaIds.push(this.form.areaId+"")
        console.log(this.areaIds)
        this.getConfigModelList();
      }

      // 获取楼宇集合
      // this.getBuildList();
      // 获取楼层集合
      this.areaTreeList();
      // 区域
      // this.getBuildAreaList();
      // 供应商
      this.getConfigSupplierList();
      // 设备型号
      // this.getConfigModelList();
      // 发布主题订阅
      this.getPublishTopic();
      // 订阅主题
      this.getSubscribeTopic();
       // 报警主题
      this.getAlarmTopic();
        // 心跳主题
      this.getHeartTopic();
      //传输协议
      this.getConfigProtocolList();
      // 服务连接
      this.getConfigLinkList();
      // 转换模板
      this.getTransferList();

      this.dialogFormVisible = true
    },
    close() {
      this.areaIds = [];
      this.$refs['form'].resetFields()
      this.form = this.$options.data().form
      this.dialogFormVisible = false
    },
    // async getBuildList(){
    //   const {data} = await getBuildList()
    //   this.buildList=data;
    // },
    // async getBuildFloorList() {
    //   let a = {dictBuilding: this.form.buildId}
    //   const {data} = await getBuildFloorList(a)
    //   this.floorList=data;
    // },
    // async getBuildAreaList() {
    //   const {data} = await getBuildAreaList()
    //   this.areaList=data;
    // },
    async getConfigSupplierList(){
      const {data} = await getConfigSupplierList()
      this.supplierList=data;
    },
    async getConfigModelList() {
      let a = {supplierId: this.form.supplierId}
      const {data} = await getConfigModelList(a)
      this.modelList=data;
    },
    async getPublishTopic(){
      const {data} = await getList({topicType:2})
      this.publishTopicList=data;
    },
    async getSubscribeTopic(){
      const {data} = await getList({topicType:1})
      this.subscribeTopicList=data;
    },
    async getHeartTopic(){
      const {data} = await getList({topicType:1})
      this.heartTopicList=data;
    },
    async getAlarmTopic(){
      const {data} = await getList({topicType:1})
      this.alarmTopicList=data;
    },
    async getConfigProtocolList(){
      const {data} = await getConfigProtocolList()
      this.configProtocolList=data;
    },
    async getConfigLinkList(){
      const {data} = await getConfigLinkList()
      this.configLinkList=data;
    },
    async getTransferList(){
      const {data} = await getTransferList()
      this.transferList=data;
    },
    getModel() {
      this.form.modelId = ''
      this.modelList = []
      if (this.form.supplierId != '') {
        this.getConfigModelList();
      }
    },
        //区域列表数据
        areaTreeList() {
          areaTreeList().then((res) => {
          if (res.code == 0) {
            this.areaList = res.data;
          }
        })
    },
    choseArea(subjectValue) {
      console.log(subjectValue)
      if (subjectValue.length == 0) {
        //clearable的点击×删除事件
        this.checkAreaDatas = [];
        this.form.buildId = ''
        this.form.floorId = ''
        this.form.areaId=''
      } else {
        this.form.buildId = subjectValue[0]
        this.form.floorId = subjectValue[1]
        this.form.areaId=subjectValue[2]
      }
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
          console.log(this.form);
          const { msg,code } = await saveOrUpdate(this.form)
          if (code == 0) {
            this.$baseMessage(msg, 'success')
          } else {
            this.$baseMessage(msg, 'error')
          }
          this.$emit('fetch-data')
          this.close()
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
