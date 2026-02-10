<template>
  <el-drawer :visible.sync="dialogFormVisible" :wrapperClosable="false" size="1200px" @close="close" :before-close="handleClose">
    <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>{{ title }}</b></span>
    <div style="padding: 0 20px 80px 20px;">
      <div class="tips-title"><vab-icon :icon="['fas', 'file-alt']"></vab-icon>&nbsp;基本信息</div>
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="90px" class="demo-ruleForm">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="配置名称" prop="name">
              <el-input v-model="ruleForm.configName" placeholder="请输入配置名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="配置范围">
              <el-select v-model="ruleForm.configType" placeholder="配置范围" disabled style="width:100%;">
                <el-option
                  v-for="item in configTypeLists"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="告警类型" prop="warnType">
              <el-select v-model="ruleForm.warnType" placeholder="请选择告警类型" style="width:100%;">
                <el-option
                  v-for="item in warnTypeLists"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所属分组" prop="subset">
              <el-select v-model="ruleForm.subset" placeholder="请选择所属分组" style="width:100%;">
                <el-option
                  v-for="item in warnGroupLists"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备类型" prop="deviceType">
              <el-select v-model="ruleForm.deviceType" placeholder="请选择设备类型" style="width:100%;"  @change="changeDeviceType">
                <el-option
                  v-for="item in options"
                  :key="item.id"
                  :label="item.deviceTypeName"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="楼宇" prop="buildId">
              <el-select v-model="ruleForm.buildId" placeholder="请选择楼宇" style="width:100%;" @change="changeBuild">
                <el-option
                  v-for="item in buildList"
                  :key="item.id"
                  :label="item.buildName"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="楼层" prop="floor">
              <el-select v-model="ruleForm.floor" placeholder="请选择楼层" style="width:100%;"  @change="changefloor">
                <el-option
                  v-for="item in floorsList"
                  :key="item.id"
                  :label="item.floorName"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备" prop="deviceId">
              <el-select v-model="ruleForm.deviceId" placeholder="请选择设备" clearable style="width:100%;" @change="changeDevice">
                <el-option
                  v-for="item in deviceIdList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="tips-title" style="margin-bottom: 20px;"><vab-icon :icon="['fas', 'tools']"></vab-icon>&nbsp;配置<span class="count">（共{{ ruleForm.warnDetailsList.length }}项）</span>
          <div class="select-title">
              <el-select v-model="ruleForm.sType" placeholder="请选择信号类型" clearable style="width:100%;margin-right:12px;">
                <el-option
                  v-for="item in signalTypeLists"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
              <vab-icon :icon="['fas', 'plus-circle']" @click="add" style="cursor: pointer;"></vab-icon>
          </div>
        </div>

        <div class="config-type-con" v-for="(item,index) in ruleForm.warnDetailsList" :key="index">
          <p>
            {{item.signName}}
            <vab-icon :icon="['fas', 'times-circle']" @click="circle(index)" ></vab-icon>
          </p>
          <el-form-item label="值类型" >
            <el-radio-group v-model="item.valueType">
              <el-radio :label="typeDes.id" :key="typeDes.id" v-for="typeDes in item.typeDes">{{ typeDes.name }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <!-- 值类型：为Int时显示此块 -->
          <div class="int-box" v-if="item.valueType == 0">
            <div class="config-item" v-for="(domain, index2) in ruleForm.warnDetailsList[index].domains" :key="domain.key">
              <el-row :gutter="20">
                <el-col :span="6">
                  <div class="area-box">
                    <el-form-item label="阈值范围" :prop="'warnDetailsList.'+ index +'.domains.' + index2 + '.valueBegin'" :rules="[{ required: true, message: '请填写', trigger: 'blur'},{ type: 'number', message: '须为数字值'}]">
                        <el-input v-model.number="domain.valueBegin" placeholder="数值"></el-input>
                    </el-form-item>
                    <span>至</span>
                    <el-form-item :prop="'warnDetailsList.'+ index +'.domains.' + index2 + '.valueEnd'" :rules="[{ required: true, message: '请填写', trigger: 'blur'},{ type: 'number', message: '须为数字值'}]" class="no-label">
                        <el-input v-model.number="domain.valueEnd" placeholder="数值" style="margin-left: 0;"></el-input>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="告警级别">
                    <el-select v-model="domain.warnLevel" placeholder="请选择">
                      <el-option
                            v-for="item in warnLevelOption"
                            :key="item.dictValue"
                            :label="item.dictLabel"
                            :value="item.dictValue">
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="联动事件">
                    <el-select v-model="domain.linkageId" placeholder="请选择">
                      <el-option label="事件一" :value="1"></el-option>
                      <el-option label="事件二" :value="2"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="状态">
                    <el-radio-group v-model="domain.isEnable">
                      <el-radio :label="0">启用</el-radio>
                      <el-radio :label="1">停用</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
              </el-row>

            </div>
            <!--<el-form-item class="form-edit-btn">
              <el-button type="success" plain size="mini" icon="el-icon-circle-plus-outline" @click="addDomain(index)">新增</el-button>
              <el-button type="danger" plain size="mini" icon="el-icon-remove-outline" @click="removeDomain(index)">删除</el-button>
            </el-form-item>-->
          </div>
          <!-- 值类型：为布尔时显示此块 -->
          <div class="boolean-box" v-else>
            <div class="config-item" v-for="(bool, index2) in ruleForm.warnDetailsList[index].booleans" :key="bool.key">
              <el-row :gutter="20">
                <el-col :span="6">

                  <el-form-item label="阈值范围">
                    <el-radio-group v-model="bool.numberBool">
                      <el-radio :label="0" >是</el-radio>
                      <el-radio :label="1">否</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="告警级别">
                    <el-select v-model="bool.warnLevel" placeholder="请选择">
                      <el-option label="级别一" value="1"></el-option>
                      <el-option label="级别二" value="2"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="联动事件">
                    <el-select v-model="bool.linkageId" placeholder="请选择">
                      <el-option label="事件一" :value="1"></el-option>
                      <el-option label="事件二" :value="2"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="状态">
                    <el-radio-group v-model="bool.isEnable">
                      <el-radio :label="0">启用</el-radio>
                      <el-radio :label="1">停用</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>





        <!-- <div class="config-type-con">
          <p>湿度</p>
          <el-form-item label="值类型">
            <el-radio-group v-model="ruleForm.radio">
              <el-radio :label="0">int</el-radio>
              <el-radio :label="1">Boolean</el-radio>
            </el-radio-group>
          </el-form-item>
          <div class="config-item">
            <el-row :gutter="20">
              <el-col :span="6" v-if="ruleForm.radio == 0">
                <el-form-item label="阈值范围">
                  <div class="area-box">
                    <el-input v-model="ruleForm.name" placeholder="数值"></el-input>
                    <span>至</span>
                    <el-input v-model="ruleForm.name" placeholder="数值"></el-input>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="6" v-if="ruleForm.radio == 1">
                <el-form-item label="阈值范围">
                  <el-radio-group v-model="ruleForm.radio2">
                    <el-radio :label="0">是</el-radio>
                    <el-radio :label="1">否</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="告警级别" prop="warnLevel">
                  <el-select v-model="ruleForm.warnLevel" placeholder="请选择">
                    <el-option label="区域一" value="shanghai"></el-option>
                    <el-option label="区域二" value="beijing"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="联动事件">
                  <el-select v-model="ruleForm.region" placeholder="请选择">
                    <el-option label="区域一" value="shanghai"></el-option>
                    <el-option label="区域二" value="beijing"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="状态" prop="isEnable">
                  <el-radio-group v-model="ruleForm.isEnable">
                    <el-radio :label="0">启用</el-radio>
                    <el-radio :label="1">停用</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <el-form-item v-if="ruleForm.radio == 0" class="form-edit-btn">
            <el-button type="success" plain size="mini" icon="el-icon-circle-plus-outline" @click="">新增</el-button>
            <el-button type="danger" plain size="mini" icon="el-icon-remove-outline" @click="">删除</el-button>
          </el-form-item>
        </div>  -->
        <el-empty description="请设置基础信息" v-show="ruleForm.warnDetailsList.length == 0"></el-empty>
      </el-form>
    </div>
    <div class="footer-btn">
      <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">立即提交</el-button>
      <el-button @click="close">取消</el-button>
    </div>
  </el-drawer>
</template>

<script>
import { doEdit } from '@/api/userManagement'
import { getRedisDictList } from '@/api/sysDict'
import { saveOrUpdate ,getWarningConfig,delEntity} from '@/api/warnConfig'
import { allBuildFloolList} from "@/api/area";
import { getPageList as getconfigDeviceType } from '@/api/configDeviceType'
import { getBuildList } from '@/api/build'
import { getAllList} from '@/api/floor'
import { getList as getDeviceCollectList} from '@/api/deviceCollect'
import { getListByDeviceTypeCode} from '@/api/configSignalType'
import { getEntityByTId} from '@/api/configDeviceSignalMap'
export default {
  name: 'configEdit',
  components: { },
  data() {
    return {
      SS:0,
      loading: false,
      disabled: false,
      options: [],//设备类型
      buildList: [],//
      floorsList: [],//指定楼宇楼层
      deviceIdList: [],//指定楼宇楼层
      configTypeLists: [],//配置范围
      warnTypeLists: [],//告警类型
      warnGroupLists : [],//所属分组
      signalTypeLists : [],//信号类型
      ruleForm: {
          warnType:'',
          subset:'',
          sType:'', //选择信号类型
          configName: '',//配置名称
          configType: '',// 配置范围
          deviceType: '',// 设备类型
          floor: '',//楼层
          buildId: '',//楼层
          deviceId: '',//设备
          warnDetailsList:[],
         /* warnDetailsList:[
            {
              name:'温度',
              type:0,
              typeDes:[{
                  id:0,
                  name:"Int",
              },{
                  id:1,
                  name:"Boolean",
              }],
              domains: [{
                valueBegin:'',
                valueEnd:'',
                warnLevel:'',
                linkageId:'',
                isEnable:0,
              }],
              booleans:[
                {
                  numberBool:0,
                  warnLevel:'',
                  linkageId:'',
                  isEnable:0,
                }
              ]
            },
            {
              name:'湿度',
              type:0,
              typeDes:[{
                  id:0,
                  name:"Int",
              },{
                  id:1,
                  name:"Boolean",
              }],
              domains: [{
                valueBegin:'',
                valueEnd:'',
                warnLevel:'',
                linkageId:'',
                isEnable:0,
              }],
              booleans:[
                {
                  numberBool:0,
                  warnLevel:'',
                  linkageId:'',
                  isEnable:0,
                }
              ]
            },
            {
              name:'通讯状态',
              type:1,
              typeDes:[{
                  id:0,
                  name:"Int",
              },{
                  id:1,
                  name:"Boolean",
              }],
              domains: [{
                valueBegin:'',
                valueEnd:'',
                warnLevel:'',
                linkageId:'',
                isEnable:0,
              }],
              booleans:[
                {
                  numberBool:0,
                  warnLevel:'',
                  linkageId:'',
                  isEnable:0,
                }
              ]
            },
          ],*/


        },
        configTemp: {
        signName:'温度',
        valueType:0,
        typeDes:[{
          id:0,
          name:"Int",
        },{
          id:1,
          name:"Boolean",
        }],
        domains: [{
          valueBegin:'',
          valueEnd:'',
          warnLevel:'',
          linkageId:'',
          isEnable:0,
        }],
        booleans:[
          {
            numberBool:0,
            warnLevel:'',
            linkageId:'',
            isEnable:0,
          }
        ]
      },
        rules: {
          configName: [
            { required: true, trigger: 'blur', message: '请填写配置名称' }
          ],
         /* one: [
            { required: true, trigger: 'blur', message: '请选择告警类型' },
          ],
          two: [
            { required: true, trigger: 'blur', message: '请选择所属分组' },
          ],*/
          deviceType: [
            { required: true, trigger: 'blur', message: '请选择设备类型' },
          ],
        },
      title: '',
      dialogFormVisible: false,

      warnLevelOption: [],
      linkageIdOption: [],
    }
  },
  created() {
    this.getWarnLevelData(); //告警等级
    this.getLinkageIdOptionData(); //
    this.getOptionData(); //设备类型
    this.getAllBuildList() ;//获取所有楼宇
    this.getDeviceCollectList(); //获取设备信息
    this.getConfigTypeListsData() ;//配置范围
    this.getWarnTypeListsData(); //配置范围
    this.getWarnGroupListsData();//配置范围
  },
  methods: {
    add() {
        var sType = this.ruleForm.sType;
        var sTypeName = ''
        for (let i = 0; i < this.signalTypeLists.length; i++) {
            if( sType == this.signalTypeLists[i].id){
                sTypeName = this.signalTypeLists[i].name
            }
        }
        var cTemp = {}
        cTemp = JSON.parse(JSON.stringify(this.configTemp));
        cTemp.signType = sType;
        cTemp.signName = sTypeName;
        this.ruleForm.warnDetailsList.push(cTemp);
    },
    circle(index) {
      console.log('chufa');
      this.ruleForm.warnDetailsList.splice(index, 1);
    },
    addDomain(index) {
        this.ruleForm.warnDetailsList[index].domains.push({
          valueBegin:'',
          valueEnd:'',
          warnLevel:'',
          linkageId:'',
          isEnable:0,
          key: Date.now()
        });
      },
      removeDomain(index) {

        console.log(index)

      },
    showEdit(row) {

      if (!row) {
        this.title = '添加'
        this.dialogFormVisible = true
      } else {
        this.title = '编辑'
        this.getWarningConfig(row);
      }

    },
    close() {
      this.$refs['ruleForm'].resetFields()
      this.ruleForm = this.$options.data().ruleForm
      this.dialogFormVisible = false
    },
    handleClose(done) {
      this.$refs['ruleForm'].resetFields()
      this.ruleForm = this.$options.data().ruleForm
      this.dialogFormVisible = false
    },
    save() {
      if (this.loading) {
        return
      }
      this.$refs['ruleForm'].validate(async (valid) => {
        if (valid) {
          this.loading = true
          this.disabled = true
          setTimeout(() => {
            this.loading = false
            this.disabled = false
          }, 1000)
          const { msg } = await saveOrUpdate(JSON.stringify(this.ruleForm))
          console.log(msg);
          this.$baseMessage(msg, 'success')
          this.$emit('fetch-data')
          this.close()
        } else {
          return false
        }
      })
    },

    async getWarningConfig(row){

      const { data } = await getWarningConfig({id: row.id})
      console.log("====" + data)
      this.ruleForm=data.data;

      if(this.ruleForm.deviceId == 0 || this.ruleForm.deviceId == undefined){
        this.ruleForm.deviceId = "";
      }else{
        this.ruleForm.deviceId = this.ruleForm.deviceId*1
      }

      if(this.ruleForm.buildId != 0){
         this.changeBuild();
      }
      // this.ruleForm.floor =this.ruleForm.floor.toString()
      this.ruleForm.floor = Number(this.ruleForm.floor) ==0 ? undefined:Number(this.ruleForm.floor)

      this.ruleForm.deviceType = this.ruleForm.deviceType*1

      this.setSignalTypeLists();


      for (let i = 0; i < this.ruleForm.warnDetailsList.length; i++) {
        this.ruleForm.warnDetailsList[i].typeDes = [{id:0,name:"Int",},{id:1,name:"Boolean",}];
        this.ruleForm.warnDetailsList[i].valueType = Number(this.ruleForm.warnDetailsList[i].valueType);

        let arr = this.ruleForm.warnDetailsList[i].booleans
        if(arr&&arr.length>0 ) {
          arr.forEach(element => {
            element.numberBool = element.numberBool*1
          });
        }
      }

      console.log("-----"+JSON.stringify( this.ruleForm))
      this.dialogFormVisible = true
      this.$forceUpdate()
    },
    async setSignalTypeLists(){

        const {data} = await getEntityByTId({tId: this.ruleForm.deviceType})

        this.signalTypeLists = [];
        for (let i = 0; i < data.length; i++) {
            var dTypeCode = data[i];
            var stList = {}
            stList.id = dTypeCode.signalTypeCode
            stList.name = dTypeCode.signalTypeName
            this.signalTypeLists.push(stList);
        }
    },

    async getOptionData() {
      const {data} = await getconfigDeviceType()
      this.options = data.records;
    },
    async getAllBuildList() {
      //楼宇
      const {data} = await getBuildList()
      this.buildList = data;
    },
    async getFloorLists(value) {
      //指定楼宇的所有楼层
       const {data} = await getAllList({dictBuilding: value})
       this.floorsList = data;
    },
    async getDeviceCollectList() {
      //所有设备信息
      const {data} = await getDeviceCollectList()
      this.deviceIdList = data;
    },
    //配置范围
    async getConfigTypeListsData() {
      const {data} = await getRedisDictList({dictType: "warnConfigType"})
      console.log(data)
      this.configTypeLists = data;
    },

    //告警类型
    async getWarnTypeListsData() {
      const {data} = await getRedisDictList({dictType: "warnType"})
      console.log(data)
      this.warnTypeLists = data;
    },

    //所属分组
    async getWarnGroupListsData() {
      const {data} = await getRedisDictList({dictType: "warnGroup"})
      console.log(data)
      this.warnGroupLists = data;
    },
    async getWarnLevelData() {
        const {data} = await getRedisDictList({dictType: "warnLevel"})
        console.log(data)
        this.warnLevelOption = data;
    },
    async getLinkageIdOptionData() {
        const {data} = await getRedisDictList({dictType: "linkageId"})
        console.log(data)
        this.linkageIdOption = data;
    },

    async changeDeviceType(){

      this.ruleForm.sType ='';
      //给配置范围赋值
      this.setConfigType()

      //温度 湿度 展示不同的配置内容  查询各个设备 设备类型 展示不同的
      var dType = this.ruleForm.deviceType; //有可能是多个
      if( dType != null && dType != "" && dType != undefined){


        this.ruleForm.warnDetailsList=[];

       // const {data} = await getListByDeviceTypeCode(dType)
        const {data} = await getEntityByTId({tId: dType})


        this.signalTypeLists=[];
          for (let i = 0; i < data.length; i++) {
              var dTypeCode = data[i];
              var cTemp = {}
              cTemp = JSON.parse(JSON.stringify(this.configTemp));
              cTemp.signType = dTypeCode.signalTypeCode
              cTemp.signName = dTypeCode.signalTypeName
              // this.ruleForm.warnDetailsList.push(cTemp);



              var stList = {}
              stList.id = dTypeCode.signalTypeCode
              stList.name = dTypeCode.signalTypeName
              this.signalTypeLists.push(stList);
          }

      } else {
        this.ruleForm.warnDetailsList=[];
      }
      this.ruleForm.buildId =""
      this.ruleForm.floor =""
      this.ruleForm.deviceId =""
      this.changeCollectData()
    },

    openwin() {
      var that = this
      that.$refs['bumen'].showDia()
    },
    async changeBuild() {
      this.floorsList = [];
     // const {data} = await getFloorList({dictBuilding: this.ruleForm.buildId})
      const {data} = await getAllList({dictBuilding: this.ruleForm.buildId})

      this.floorsList = data;
      //给配置范围赋值
      this.setConfigType()
      this.changeCollectData()
    },
    async changefloor() {
      //给配置范围赋值
      this.setConfigType()
      this.changeCollectData()
    },
    async changeDevice(value) {
      this.setConfigType()
      this.changeCollectData()
    },
    async changeCollectData() {

      var deviceType = this.ruleForm.deviceType

      var buildId = this.ruleForm.buildId
      var floor = this.ruleForm.floor

      const {data} = await getDeviceCollectList({"deviceType": deviceType,"buildId":buildId,"floorId":floor})
      this.deviceIdList = data;
    },
    setConfigType(){
      //给配置范围赋值
      var deviceType = this.ruleForm.deviceType
      var buildId = this.ruleForm.buildId
      var floor = this.ruleForm.floor
      var deviceId = this.ruleForm.deviceId+""
      if(deviceId != ''){
        this.ruleForm.configType = "2";
      }else if(deviceType !='' || buildId != '' || floor != '' ){
        this.ruleForm.configType = "1";
      }

    }
  },


}
</script>
<style scoped lang="scss">
::v-deep {
  .el-drawer__header {
    padding: 10px !important;
    margin-bottom: 14px;
    border-bottom: 1px solid #d1d9e1;

    .drawer-title {
      font-size: 16px;
      line-height: 16px;
      color: #334c97;
      display: flex;
      align-items: center;

      i {
        display: block;
        font-size: 18px;
        line-height: 18px;
        margin-right: 4px;
      }

      b {
        display: block;
        font-size: 16px;
        line-height: 16px;
        margin-top: 2px;
        margin-right: 4px;
      }
    }
  }

  .tips-title {
    font-size: 14px;
    margin-bottom: 10px;
    font-weight: bold;
    color: #334c97;
    position: relative;
    // border: 1px solid red;

    .select-title {
      position: absolute;
      right: 0;
      top: 50%;
      transform: translateY(-50%);
      display: flex;
      align-items: center;
      v-dedp .el-input__inner {
        height: 20px;
        line-height: 20px;
      }
    }

    i {
      margin-right: 4px;
    }
    .count{
      color: #999;
      font-weight: normal;
      margin-left: 10px;
    }
  }
  .area-box{
    display: flex;
    justify-content: center;
    align-items: center;
    .el-input__inner{
      width: 80px;
    }
    .no-label{
      .el-form-item__content{
        margin-left: 0 !important;
      }
    }
    span{
      margin: 0 6px;
    }
  }
  .config-type-con{
    margin-bottom: 14px;
    background-color: #fafcff;
    border-top-right-radius: 8px;
    padding-top: 8px;
    border: 1px solid #efefef;
    position: relative;
    &::before{
      content: "";
      display: inline-block;
      width: 4px;
      height: 46px;
      background-color: #5470c6;
      position: absolute;
      left: 0;
      top: 0px;
      border-top-right-radius: 8px;
      border-bottom-right-radius: 8px;
    }
    .el-form-item--small.el-form-item{
      margin-bottom: 6px;
    }
    p{
      position: absolute;
      right: 0;
      top: 0;
      height: 28px;
      line-height: 28px;
      text-align: center;
      display: inline-block;
      padding: 0 20px 0 30px;
      margin: 0 0 6px;
      background-color: #5470c6;
      border-top-right-radius: 8px;
      border-bottom-left-radius: 16px;
      color: #fff;
      font-size: 12px;
      box-shadow: 0 1px 2px #677bb7;

      >svg {
        position: relative;
        right: -11px;
        z-index: 1;
        cursor: pointer;
      }
    }
    .config-item{
      padding: 10px 0;
      background-color: #fbf2de;
      .el-form-item--small.el-form-item{
        margin-bottom: 0;
      }
    }
    .form-edit-btn{
      margin-top: 6px;
      margin-bottom: 12px !important;
    }
  }
  .footer-btn{
    padding: 12px 20px;
    border-top: 1px solid #efefef;
    position: absolute;
    bottom: 0;
    text-align: right;
    width: 100%;
    background-color: #fff;
    z-index: 999;
  }

}
</style>
