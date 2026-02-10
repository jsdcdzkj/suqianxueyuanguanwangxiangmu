<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    width="768px"
    @close="close"
  >
    <!-- <div slot="title" class="header-title el-dialog__title">
            <span class="title-left">{{ title }}</span>
            <span> 设备信息  </span>
        </div> -->

    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-width="120px"
      class="fjzd-form"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备名称" prop="name">
            <el-input
              v-model.trim="form.name"
              autocomplete="off"
              placeholder="请输入设备名称"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备类型" prop="deviceType">
            <el-select
              v-model="form.deviceType"
              placeholder="请选择设备类型"
              class="w"
            >
              <el-option
                v-for="item in this.deviceTypeList"
                :key="item.id"
                :label="item.deviceTypeName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="设备编码" prop="deviceCode">
            <el-input
              v-model.trim="form.deviceCode"
              autocomplete="off"
              placeholder="请输入设备编码"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备状态" prop="status">
            <el-select
              v-model="form.status"
              placeholder="请选择设备状态"
              class="w"
            >
              <el-option
                v-for="item in this.statusList"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="所属分项" prop="subitem">
            <el-select
              v-model="form.subitem"
              placeholder="请选择设备所属分项"
              class="w"
            >
              <el-option
                v-for="item in this.subitemList"
                :key="item.id"
                :label="item.subitemName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否分项总设备" prop="isSubitemTotalDevice">
            <el-radio-group v-model="form.isSubitemTotalDevice">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="物理位置" prop="areaIds">
            <el-cascader
              :props="{
                expandTrigger: 'hover',
              }"
              :options="areaList"
              style="width: 100%"
              :show-all-levels="false"
              collapse-tags
              ref="areaId"
              v-model="form.areaIds"
              @change="choseArea"
              filterable
              clearable
            ></el-cascader>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="安装位置" prop="place">
            <el-input
              v-model.trim="form.place"
              autocomplete="off"
              placeholder="请输入安装位置"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="逻辑位置" prop="logicalAreaIds">
            <el-cascader
              :props="{
                expandTrigger: 'hover',
                value: 'value',
                label: 'label',
                children: 'children',
                checkStrictly: true,
              }"
              :options="logicalAreaList"
              style="width: 100%"
              :show-all-levels="false"
              collapse-tags
              ref="logicalAreaId"
              v-model="form.logicalAreaIds"
              @change="choseLogicalArea"
              popper-class="location"
              filterable
              clearable
            ></el-cascader>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否总设备" prop="isTotalDevice">
            <el-radio-group v-model="form.isTotalDevice">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="安装日期" prop="installationDate">
            <el-date-picker
              type="date"
              placeholder="请选择安装日期"
              v-model="form.installationDate"
              value-format="yyyy-MM-dd"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="使用日期" prop="useTime">
            <el-date-picker
              type="date"
              placeholder="请选择使用日期"
              v-model="form.useTime"
              value-format="yyyy-MM-dd"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="设备管理员" prop="userId">
            <el-select
              v-model="form.userId"
              placeholder="请选择设备管理员"
              class="w"
            >
              <el-option
                v-for="item in this.userList"
                :key="item.id"
                :label="item.realName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="管理员电话" prop="phone">
            <el-input
              v-model.trim="form.phone"
              autocomplete="off"
              placeholder="请输入管理员电话"
              @blur="form.phone = $event.target.value"
              onkeyup="value=value.replace(/[^\d]/g,'')"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="供应商名称" prop="supplierId">
            <el-select
              v-model="form.supplierId"
              placeholder="请选择供应商"
              @change="supplierChange"
              class="w"
            >
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
            <el-select
              v-model="form.modelNum"
              placeholder="请选择设备型号"
              class="w"
            >
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
          <el-form-item label="维保公司" prop="companyId">
            <el-select
              v-model="form.companyId"
              placeholder="请选择维保公司"
              class="w"
            >
              <el-option
                v-for="item in this.supplierTypeList"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="年检日期" prop="inspectionDate">
            <el-date-picker
              type="date"
              placeholder="请选择年检日期"
              v-model="form.inspectionDate"
              value-format="yyyy-MM-dd"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="采购日期" prop="procureDate">
            <el-date-picker
              type="date"
              placeholder="请选择采购日期"
              v-model="form.procureDate"
              value-format="yyyy-MM-dd"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="过保日期" prop="expirationDate">
            <el-date-picker
              type="date"
              placeholder="请选择过保日期"
              v-model="form.expirationDate"
              value-format="yyyy-MM-dd"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <!--<el-col :span="12">-->
        <!--<el-form-item label="在线状态" prop="onLineStatus">-->
        <!--<el-select v-model="form.onLineStatus" placeholder="请选择在线状态" class="w">-->
        <!--<el-option label="正常" value="1"></el-option>-->
        <!--<el-option label="离线" value="2"></el-option>-->
        <!--<el-option label="异常" value="3"></el-option>-->
        <!--</el-select>-->
        <!--</el-form-item>-->
        <!--</el-col>-->

        <el-col :span="12">
          <el-form-item label="负荷" prop="load">
            <el-input
              v-model.trim="form.load"
              autocomplete="off"
              placeholder="请输入负荷"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属网关" prop="gatewayId">
            <el-select
              v-model="form.gatewayId"
              placeholder="请选择设备所属网关"
              class="w"
            >
              <el-option
                v-for="item in this.gatewayList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="是否是租户" prop="isTenant">
            <el-radio-group v-model="form.isTenant">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <!-- <el-col :span="24">
          <el-form-item
            v-if="form.isTenant == '1'"
            label="所属租户"
            prop="orgId"
          ></el-form-item>
        </el-col> -->
        <!-- <el-col :span="12" v-if="form.isTenant == '1'">
          <el-form-item label="所属租户" prop="orgId">
            <el-select
              v-model="form.orgId"
              placeholder="请选择所属租户"
              class="w"
            >
              <el-option
                v-for="item in merchantList"
                :key="item.id"
                :label="item.orgName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col> -->
        <el-col
          v-if="form.deviceType == '10005'"
          :span="form.bindSwitch == 1 ? 12 : 24"
        >
          <el-form-item label="是否绑定空开" prop="bindSwitch">
            <el-radio-group v-model="form.bindSwitch">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            v-if="form.bindSwitch == '1' && form.deviceType == '10005'"
            label="绑定电表"
            prop="switchIdList"
          >
            <el-select
              v-model="form.switchIdList"
              placeholder="请选择绑定电表"
              class="w"
              multiple
            >
              <el-option
                v-for="item in deviceList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="设备描述" prop="deviceDesc">
        <el-input
          v-model.trim="form.deviceDesc"
          type="textarea"
          placeholder="请输入设备描述"
          :rows="3"
          autocomplete="off"
        ></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button
        type="primary"
        @click="save"
        :loading="loading"
        :disabled="disabled"
      >
        {{ loading ? '确定中 ...' : '确定' }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {
    saveOrUpdate,
    getEntity,
    getDeviceCollectList,
  } from '@/api/deviceCollect'
  import { getRedisDictList } from '@/api/sysDict'
  import { getDeviceSubitemList } from '@/api/configDeviceSubitem'
  import { getBuildAreaList } from '@/api/sysBuildArea'
  import { getDeviceGatewayList } from '@/api/deviceGateway'
  import { getBuildList } from '@/api/sysBuild'
  import { getBuildFloorList } from '@/api/sysBuildFloor'
  import { getRedisUserDictMap } from '@/api/userManagement'
  import { getConfigModelList } from '@/api/configModel'
  import { getConfigSupplierList } from '@/api/configSupplier'
  import { getDeviceTypeList, findMerchant } from '@/api/configDeviceType'
  import { areaTreeList, areaTreeList2 } from '@/api/org'

  export default {
    name: 'CjzdEdit',
    data() {
      return {
        value: '',
        loading: false,
        disabled: false,
        deviceList: [],
        form: {
          id: '',
          name: '',
          modelNum: '',
          deviceCode: '',
          supplierId: '',
          deviceType: '',
          status: '',
          buildId: '',
          floorId: '',
          areaId: '',
          logicalBuildId: '',
          logicalFloorId: '',
          logicalAreaId: '',
          place: '',
          installationDate: '',
          useTime: '',
          userId: '',
          phone: '',
          companyId: '',
          inspectionDate: '',
          procureDate: '',
          expirationDate: '',
          subitem: '',
          gatewayId: '',
          deviceDesc: '',
          isTotalDevice: 0,
          isSubitemTotalDevice: 0,
          areaIds: [],
          logicalAreaIds: [],
          onLineStatus: '',
          load: '',
          isTenant: 0,
          // orgId: '',
          bindSwitch: 0,
          switchIdList: [],
        },
        rules: {
          name: [
            { required: true, trigger: 'blur', message: '请输入设备名称' },
          ],
          modelNum: [
            { required: true, trigger: 'change', message: '请选择设备型号' },
          ],
          supplierId: [
            { required: true, trigger: 'change', message: '请选择供应商' },
          ],
          deviceCode: [
            { required: true, trigger: 'blur', message: '请输入设备编码' },
          ],
          deviceType: [
            { required: true, trigger: 'change', message: '请选择设备类型' },
          ],
          status: [
            { required: true, trigger: 'change', message: '请选择设备状态' },
          ],
          // floorId: [{required: true, trigger: 'change', message: '请选择所属楼层'}],
          areaIds: [
            { required: true, trigger: 'change', message: '请选择物理位置' },
          ],
          logicalAreaIds: [
            { required: true, trigger: 'change', message: '请选择逻辑位置' },
          ],
          // buildId: [{required: true, trigger: 'change', message: '请选择所属楼宇'}],
          subitem: [
            { required: true, trigger: 'change', message: '请选择所属分项' },
          ],
          gatewayId: [
            { required: true, trigger: 'change', message: '请选择网关设备' },
          ],
          isTotalDevice: [
            { required: true, trigger: 'change', message: '请选择是否总设备' },
          ],
          isSubitemTotalDevice: [
            {
              required: true,
              trigger: 'change',
              message: '请选择是否分项总设备',
            },
          ],
          onLineStatus: [
            { required: true, trigger: 'change', message: '请选择在线状态' },
          ],
          load: [{ required: true, trigger: 'blur', message: '请输入负荷' }],
          isTenant: [
            {
              required: true,
              trigger: 'change',
              message: '请选择是否租户',
            },
          ],
          switchIdList: [
            { required: true, trigger: 'change', message: '请绑定电表' },
          ],
          // orgId: [
          //   { required: true, trigger: 'change', message: '请选择所属租户' },
          // ],
        },
        title: '',
        dialogFormVisible: false,
        deviceTypeList: [],
        subitemList: [],
        areaList: [],
        logicalAreaList: [],
        statusList: [],
        gatewayList: [],
        buildList: [],
        buildFloorList: [],
        supplierTypeList: [],
        userList: [],
        modelList: [],
        supplierList: [],
        checkAreaDatas: [],
        checkLogicalAreaDatas: [],
        merchantList: [],
      }
    },
    created() {
      getDeviceCollectList({
        isTenant: 1,
        deviceTypes: '10007,200084',
      }).then((res) => {
        this.deviceList = res.data.map((item) => ({
          ...item,
          id: item.id + '',
        }))
      })
    },
    methods: {
      showEdit(row) {
        if (!row) {
          this.title = '添加'
        } else {
          this.title = '编辑'
          // this.form = Object.assign({}, row)
          this.getData(row)
        }

        //设备类型
        this.deviceTypeListData()
        //设备状态 字典
        this.statusListData()
        //维保公司/供应商类型【字典】
        this.supplierTypeListData()
        //所属分项
        this.subitemListData()
        //网关设备
        this.gatewayListData()
        this.getMerchantList()
        // //所属楼宇
        // this.buildListData();
        // //所属楼层
        // this.buildFloorListData();
        // //所属区域
        // this.areaListData();
        //设备管理员
        this.userListData()
        //设备型号
        // this.modelListData();
        //供应商
        this.supplierListData()

        this.areaTreeList()
        this.areaTreeList2()

        this.dialogFormVisible = true

        this.$nextTick(() => {
          if (this.$refs['form']) {
            this.$refs['form'].resetFields()
          }
        })
      },
      async getMerchantList() {
        const { data } = await findMerchant()
        this.merchantList = data
      },
      async deviceTypeListData() {
        const { data } = await getDeviceTypeList()
        console.log('设备类型', data)
        this.deviceTypeList = data
      },
      async statusListData() {
        const { data } = await getRedisDictList({ dictType: 'deviceStatus' })
        console.log('设备状态', data)
        this.statusList = data
      },
      async supplierTypeListData() {
        const { data } = await getRedisDictList({ dictType: 'supplierType' })
        console.log('维保公司', data)
        this.supplierTypeList = data
      },
      async subitemListData() {
        const { data } = await getDeviceSubitemList()
        console.log('分项', data)
        this.subitemList = data
      },
      async gatewayListData() {
        const { data } = await getDeviceGatewayList()
        console.log('网关', data)
        this.gatewayList = data
      },
      // async buildListData() {
      //   const {data} = await getBuildList();
      //   console.log("所属楼宇", data)
      //   this.buildList = data;
      // },
      // async buildChange(v) {
      //   this.buildFloorList = [];
      //   this.areaList = [];
      //   this.form.floorId = '';
      //   this.form.areaId = '';
      //   this.buildFloorListData(v);
      // },
      // async floorChange(v) {
      //   this.areaList = [];
      //   this.form.areaId = '';
      //   this.areaListData(v);
      // },
      // async buildFloorListData(buildId) {
      //   const {data} = await getBuildFloorList({'dictBuilding': buildId});
      //   console.log("所属楼层", data)
      //   this.buildFloorList = data;
      // },
      // async areaListData(floorId) {
      //   const {data} = await getBuildAreaList({'floorId': floorId});
      //   console.log("所属区域", data)
      //   this.areaList = data;
      // },
      async userListData() {
        const { data } = await getRedisUserDictMap()
        console.log('管理员', data)
        this.userList = data
      },
      async modelListData(supplierId) {
        const { data } = await getConfigModelList({ supplierId: supplierId })
        console.log('设备型号', data)
        this.modelList = data
      },
      async supplierListData(v) {
        const { data } = await getConfigSupplierList()
        console.log('供应商', data)
        this.supplierList = data
      },
      async supplierChange(v) {
        this.modelList = []
        this.form.modelNum = ''
        this.modelListData(v)
      },
      //物理位置列表数据
      areaTreeList() {
        areaTreeList().then((res) => {
          if (res.code == 0) {
            this.areaList = res.data
          }
        })
      },
      //逻辑位置列表数据
      areaTreeList2() {
        areaTreeList2().then((res) => {
          if (res.code == 0) {
            this.logicalAreaList = res.data
          }
        })
      },
      choseArea(subjectValue) {
        console.log(subjectValue)
        if (subjectValue.length == 0) {
          //clearable的点击×删除事件
          this.checkAreaDatas = []
        }
      },
      choseLogicalArea(subjectValue) {
        console.log(subjectValue)
        this.$refs.logicalAreaId.dropDownVisible = false //监听值发生变化就关闭它
        if (subjectValue.length == 0) {
          //clearable的点击×删除事件
          this.checkLogicalAreaDatas = []
        }
      },
      close() {
        this.$refs['form'].resetFields()
        this.form = this.$options.data().form
        this.dialogFormVisible = false
      },
      async getData(id) {
        const { data } = await getEntity({ id: id })
        this.form = {
          ...data,
          // orgId: data.orgId === 0 ? '' : data.orgId,
        }

        this.modelListData(data.supplierId)
        // this.buildFloorListData(data.buildId);
        // this.areaListData(data.floorId);
      },
      save() {
        if (this.loading) {
          return
        }

        const areaId = this.$refs['areaId'].getCheckedNodes()
        //获取当前点击节点的label值
        if (areaId != null && areaId != '' && areaId != undefined) {
          if (areaId.length == 1) {
            console.log(areaId[0].data.value)
            if (areaId[0].level == 3) {
              this.form.areaId = areaId[0].data.value
              this.form.floorId = areaId[0].parent.data.value
              this.form.buildId = areaId[0].parent.parent.data.value
            }
          } else {
            this.form.areaId = ''
            this.form.floorId = ''
            this.form.buildId = ''
          }
        } else {
          this.form.areaId = ''
          this.form.floorId = ''
          this.form.buildId = ''
        }

        const logicalAreaId = this.$refs['logicalAreaId'].getCheckedNodes()
        //获取当前点击节点的label值
        if (
          logicalAreaId != null &&
          logicalAreaId != '' &&
          logicalAreaId != undefined
        ) {
          if (logicalAreaId.length == 1) {
            console.log(logicalAreaId[0].data.value)
            if (logicalAreaId[0].level == 3) {
              this.form.logicalAreaId = logicalAreaId[0].data.value
              this.form.logicalFloorId = logicalAreaId[0].parent.data.value
              this.form.logicalBuildId =
                logicalAreaId[0].parent.parent.data.value
            } else if (logicalAreaId[0].level == 2) {
              this.form.logicalAreaId = 0
              this.form.logicalFloorId = logicalAreaId[0].data.value
              this.form.logicalBuildId = logicalAreaId[0].parent.data.value
            } else if (logicalAreaId[0].level == 1) {
              this.form.logicalAreaId = 0
              this.form.logicalFloorId = 0
              this.form.logicalBuildId = logicalAreaId[0].data.value
            }
          } else {
            this.form.logicalAreaId = 0
            this.form.logicalFloorId = 0
            this.form.logicalBuildId = 0
          }
        } else {
          this.form.logicalAreaId = 0
          this.form.logicalFloorId = 0
          this.form.logicalBuildId = 0
        }

        this.$refs['form'].validate(async (valid) => {
          if (valid) {
            this.loading = true
            this.disabled = true
            // setTimeout(() => {
            //   this.loading = false
            //   this.disabled = false
            // }, 1000)
            // if (this.form.isTenant == 0) {
            //   this.form.orgId = 0
            // }
            // 处理switchIdList为数组
            if (
              this.form.switchIdList &&
              typeof this.form.switchIdList === 'string'
            ) {
              this.form.switchIdList = this.form.switchIdList.split(',')
            } else if (!this.form.switchIdList) {
              this.form.switchIdList = []
            }
            const data = await saveOrUpdate(this.form)
            if (data.code == 0) {
              this.$baseMessage(data.msg, 'success')
              this.$emit('fetch-data')
              this.close()
              this.loading = false
              this.disabled = false
            } else {
              this.$baseMessage(data.msg, 'error')
              this.loading = false
              this.disabled = false
            }
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

  .location .el-checkbox,
  .location .el-radio {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 10;
  }

  .location .el-cascader-node {
    padding-left: 34px;
  }

  .location .el-checkbox__input,
  .location .el-radio__input {
    position: absolute;
    left: 20px;
    top: 10px;
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
