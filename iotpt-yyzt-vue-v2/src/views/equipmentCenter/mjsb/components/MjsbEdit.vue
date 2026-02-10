<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    width="500px"
    @close="close"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="110px">
      <el-form-item label="设备类型" prop="doorType">
        <el-radio-group v-model="form.doorType">
          <el-radio label="1" value="1">门禁</el-radio>
          <el-radio label="2" value="2">道闸</el-radio>
          <el-radio label="3" value="3">梯禁</el-radio>
          <el-radio label="4" value="4">其他</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="设备名称" prop="name">
        <el-input
          v-model.trim="form.name"
          autocomplete="off"
          placeholder="请输入设备名称"
        ></el-input>
      </el-form-item>
      <!-- <el-form-item label="设备编码" prop="deviceCode">
        <el-input v-model.trim="form.deviceCode" autocomplete="off" placeholder="请输入设备编码"></el-input>
      </el-form-item> -->
      <el-form-item label="所属区域" prop="areaId">
        <el-cascader
          placeholder="请选择所属区域"
          :props="{ expandTrigger: 'hover' }"
          :options="areaList"
          style="width: 100%"
          :show-all-levels="false"
          collapse-tags
          ref="areaId"
          v-model="form.areaIds"
          @change="choseArea"
          clearable
        ></el-cascader>
      </el-form-item>
      <!-- <el-form-item label="所属楼宇" prop="buildId">
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
      </el-form-item> -->
      <el-form-item label="设备编码" prop="channelCode">
        <el-input
          v-model.trim="form.channelCode"
          placeholder="请输入通道编码"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="进出设备" prop="accessEquipment">
        <el-select
          v-model="form.accessEquipment"
          placeholder="请选择进出设备"
          clearable
          @change="supplierChange"
          class="w"
        >
          <el-option label="普通门禁" value="0"></el-option>
          <el-option label="进大楼设备" value="1"></el-option>
          <el-option label="出大楼设备" value="2"></el-option>
          <el-option label="17F健身房门禁设备" value="3"></el-option>
          <el-option label="普通电梯设备" value="4"></el-option>
          <el-option label="专用电梯设备" value="5"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="供应商" prop="supplierId">
        <el-select
          v-model="form.supplierId"
          placeholder="请选择供应商"
          clearable
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
      <el-form-item label="设备型号" prop="modelNum">
        <el-select
          v-model="form.modelNum"
          placeholder="请选择设备型号"
          clearable
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
      <el-form-item label="设备描述" prop="deviceDesc">
        <el-input
          v-model.trim="form.deviceDesc"
          type="textarea"
          placeholder="请输入设备描述"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="设备状态">
        <el-radio-group v-model="form.status">
          <el-radio :label="1" value="1">启用</el-radio>
          <el-radio :label="0" value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否是租户" prop="isTenant">
        <el-radio-group v-model="form.isTenant">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="form.isTenant == '1'" label="所属租户" prop="orgId">
        <el-select v-model="form.orgId" placeholder="请选择所属租户" class="w">
          <el-option
            v-for="item in this.merchantList"
            :key="item.id"
            :label="item.orgName"
            :value="item.id"
          ></el-option>
        </el-select>
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
  import { addDoor, updateDoor } from '@/api/mjsb'
  import { getBuildAreaList } from '@/api/sysBuildArea'
  import { getBuildList } from '@/api/sysBuild'
  import { getBuildFloorList } from '@/api/sysBuildFloor'
  import { getConfigModelList } from '@/api/configModel'
  import { getConfigSupplierList } from '@/api/configSupplier'
  import { findMerchant } from '@/api/configDeviceType'
  import { areaTreeList } from '@/api/org'
  export default {
    name: 'MjsbEdit',
    components: {},
    data() {
      return {
        loading: false,
        disabled: false,
        form: {
          id: '',
          name: '',
          // deviceCode: '',
          doorType: '',
          areaId: '',
          floorId: '',
          buildId: '',
          modelNum: '',
          deviceDesc: '',
          supplierId: '',
          accessEquipment: '0',
          status: 1,
          areaIds: [],
          isTenant: 0,
          orgId: '',
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
          doorType: [
            { required: true, trigger: 'blur', message: '请选择设备类型' },
          ],
          areaId: [
            { required: true, trigger: 'change', message: '请选择所属区域' },
          ],
          channelCode: [
            { required: true, trigger: 'change', message: '请选择通道编码' },
          ],
          accessEquipment: [
            { required: true, trigger: 'change', message: '请选择进出设备' },
          ],
          // floorId: [
          //   { required: true, trigger: 'change', message: '请选择所属楼层', type: 'number' },
          // ],
          // buildId: [
          //   { required: true, trigger: 'change', message: '请选择所属楼宇', type: 'number' },
          // ],
          isTenant: [
            {
              required: true,
              trigger: 'change',
              message: '请选择是否租户',
            },
          ],
          orgId: [
            { required: true, trigger: 'change', message: '请选择所属租户' },
          ],
        },
        title: '',
        dialogFormVisible: false,
        merchantList: [],
      }
    },
    created() {},
    methods: {
      showEdit(row) {
        if (!row) {
          this.title = '添加'
        } else {
          this.title = '编辑'
          // this.buildFloorListData(row.buildId);
          // this.areaListData(row.floorId);
          this.form = Object.assign({}, { doorType: '' }, row, {
            orgId: row.orgId === 0 ? '' : row.orgId,
          })

          var array = []
          array.push(this.form.buildId + '')
          array.push(this.form.floorId + '')
          array.push(this.form.areaId + '')
          this.form.areaIds = array
        }
        this.areaTreeList()
        this.modelListData()
        this.supplierListData()
        this.getMerchantList()
        this.dialogFormVisible = true
        // this.buildListData();
        //this.buildFloorListData();
        //this.areaListData();
      },
      areaTreeList() {
        //所属区域
        areaTreeList().then((res) => {
          if (res.code == 0) {
            this.areaList = res.data
          }
        })
      },
      choseArea(subjectValue) {
        //所属区域选中值
        if (subjectValue.length == 0) {
          this.checkAreaDatas = []
        } else {
          const checkedNodes = this.$refs['areaId'].getCheckedNodes()
          this.form.buildId = checkedNodes[0].path[0]
          this.form.floorId = checkedNodes[0].path[1]
          this.form.areaId = checkedNodes[0].data.value
        }
      },
      async getMerchantList() {
        const { data } = await findMerchant()
        this.merchantList = data
      },
      async buildListData() {
        const { data } = await getBuildList()
        console.log('所属楼宇', data)
        this.buildList = data
      },
      async buildChange(v) {
        this.buildFloorList = []
        this.areaList = []
        this.form.floorId = ''
        this.form.areaId = ''
        if (v) {
          this.buildFloorListData(v)
        }
      },
      async floorChange(v) {
        this.areaList = []
        this.form.areaId = ''
        if (v) {
          this.areaListData(v)
        }
      },
      async buildFloorListData(buildId) {
        const { data } = await getBuildFloorList({ dictBuilding: buildId })
        console.log('所属楼层', data)
        this.buildFloorList = data
      },
      async areaListData(floorId) {
        const { data } = await getBuildAreaList({ floorId: floorId })
        console.log('所属区域', data)
        this.areaList = data
      },
      async modelListData(supplierId) {
        const { data } = await getConfigModelList({ supplierId: supplierId })
        console.log('设备型号', data)
        this.modelList = data
      },
      async supplierListData() {
        const { data } = await getConfigSupplierList()
        console.log('供应商', data)
        this.supplierList = data
      },
      supplierChange(e) {
        if (null == e || '' == e) {
          this.form.modelNum = ''
          this.modelList = []
        } else {
          this.form.modelNum = ''
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
            if (this.form.isTenant == 0) {
              this.form.orgId = 0
            }
            if (this.form.id) {
              updateDoor(this.form).then((res) => {
                if (0 == res.code) {
                  this.$baseMessage(this.title + '成功', 'success')
                  this.$emit('fetch-data')
                  this.close()
                } else {
                  this.$baseMessage(this.title + '失败', 'error')
                }
              })
            } else {
              delete this.form.id
              addDoor(this.form).then((res) => {
                if (0 == res.code) {
                  this.$baseMessage(this.title + '成功', 'success')
                  this.$emit('fetch-data')
                  this.close()
                } else {
                  this.$baseMessage(this.title + '失败', 'error')
                }
              })
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
