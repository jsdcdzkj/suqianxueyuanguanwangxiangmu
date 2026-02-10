<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    @close="close"
    append-to-body
    width="600px"
  >
    <span slot="title" class="drawer-title">
      <b>{{ title }}</b>
    </span>
    <div style="padding: 0 20px">
      <el-form ref="car" :rules="rules" :model="car" label-width="108px">
        <el-form-item label="车牌号码" prop="busNumber">
          <el-input
            v-model="car.busNumber"
            placeholder="请输入车牌号码"
          ></el-input>
        </el-form-item>
        <el-form-item label="车牌类型" prop="carType">
          <el-select
            v-model="car.carType"
            placeholder="请选择车牌类型"
            style="width: 100%"
            clearable
          >
            <el-option label="蓝牌" value="0"></el-option>
            <el-option label="绿牌" value="1"></el-option>
            <el-option label="黄牌" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="车辆类型" prop="vehicleBrand">
          <el-select
            v-model="car.vehicleBrand"
            placeholder="请选择车辆类型"
            style="width: 100%"
            clearable
            @change="changeCarType"
          >
            <el-option label="员工车辆" value="0"></el-option>
            <el-option label="公司车辆" value="1"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="权限状态" prop="state">
          <el-radio-group v-model="car.state">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="车位权限类型" prop="stallLimit">
          <el-radio-group v-model="car.stallLimit" @change="getStallLimit()">
            <el-radio :label="3">新能源车位</el-radio>
            <el-radio :label="1">普通车位</el-radio>
            <el-radio :label="2">指定车位</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="车位编码"
          prop="stallId"
          v-if="car.stallLimit == 2"
        >
          <el-select
            v-model="car.stallId"
            placeholder="请选择"
            style="width: 100%"
          >
            <el-option
              v-for="item in stalls"
              :key="item.value"
              :label="item.stallCode"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="有效截止时间" prop="endOfEffectiveTime">
          <el-date-picker
            v-model="car.endOfEffectiveTime"
            style="width: 100%"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择时间"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="desc">
          <el-input
            type="textarea"
            placeholder="请输入备注"
            v-model="car.remarke"
          ></el-input>
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取消</el-button>
      <el-button
        type="primary"
        :disabled="disabled"
        :loading="disabled"
        @click="saveCar('car')"
      >
        保存
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
  import { stallList } from '@/api/stall'
  import { addCar, updateCar } from '@/api/car'
  export default {
    name: 'add',
    components: {},
    data() {
      return {
        loading: false,
        disabled: false,
        title: '',
        stalls: [],
        listLoading: false,
        elementLoadingText: '正在加载...',
        dialogFormVisible: false,
        car: {
          user: '',
          region: '',
          desc: '',
          busNumber: '',
          vehicleBrand: '',
          carType: '',
          stallId: '',
          realName: '',
          id: '',
          remarke: '',
          stallLimit: 3,
          endOfEffectiveTime: '',
          phone: '',
          state: 0,
        },
        stallLimit: 3,

        state: 0,
        rules: {
          busNumber: [{ required: true, message: '请输入', trigger: 'blur' }],
          stallId: [{ required: true, message: '请选择', trigger: 'blur' }],
          realName: [{ required: true, message: '请选择', trigger: 'sub' }],
          carType: [{ required: true, message: '请选择', trigger: 'blur' }],
          vehicleBrand: [
            { required: true, message: '请选择', trigger: 'blur' },
          ],
          endOfEffectiveTime: [
            { required: true, message: '请选择', trigger: 'blur' },
          ],
          phone: [{ required: true, message: '请输入', trigger: 'blur' }],
          state: [{ required: true, message: '请选择', trigger: 'blur' }],
          stallLimit: [{ required: true, message: '请选择', trigger: 'blur' }],
        },
        options: [],
      }
    },
    created() {},
    methods: {
      showEdit(row) {
        var that = this

        if (row) {
          console.log(row)
          // that.stallLimit = row.stallLimit
          that.getStallLimit(row.stallId)
          that.title = '编辑'
          that.car = Object.assign({}, row)
          setTimeout(() => {
            that.car.state = parseInt(row.state)
            if (
              row.stallCode != '' &&
              row.stallCode != undefined &&
              row.stallCode != null
            ) {
              that.car.stallLimit = 2
            } else {
              that.car.stallLimit = parseInt(row.stallLimit)
            }
            that.car.stallId = parseInt(row.stallId)
            console.log('that.car.stallId', that.car.stallId)
          }, 100)
        } else {
          that.title = '新增'

          that.car = {
            user: '',
            region: '',
            desc: '',
            busNumber: '',
            vehicleBrand: '',
            carType: '',
            stallId: '',
            realName: '',
            id: '',
            remarke: '',
            stallLimit: 3,
            endOfEffectiveTime: '',
            phone: '',
            state: 0,
          }
          that.state = 0
          that.stallLimit = 3
          that.getStallLimit()
        }
        that.dialogFormVisible = true

        setTimeout(() => {
          that.$refs.car.clearValidate()
        }, 300)
      },
      close() {
        this.car = {
          user: '',
          region: '',
          desc: '',
          busNumber: '',
          vehicleBrand: '',
          carType: '',
          stallId: '',
          realName: '',
          id: '',
          remarke: '',
          stallLimit: 3,
          endOfEffectiveTime: '',
          phone: '',
          state: 0,
        }
        this.dialogFormVisible = false
      },

      saveCar(formName) {
        var that = this
        that.disabled = true
        that.$refs[formName].validate((valid) => {
          if (valid) {
            this.$emit('saveCar', this.car)
            that.disabled = false
            this.close()
          } else {
            that.disabled = false
            return false
          }
        })
        // this.$emit('saveCar', this.car);
        // this.close()
        // var that = this ;
        // that.$refs[formName].validate((valid) => {
        //   if (valid) {
        //     that.disabled = true;
        //     that.car.state = that.state ;
        //     that.car.stallLimit = that.stallLimit ;
        //     if(that.car.id){
        //       updateCar(that.car).then((res) => {
        //         if(res.code == 0){
        //           that.$baseMessage("操作成功", 'success')
        //           that.$emit('fetch-data')
        //           that.close() ;
        //         }
        //         that.disabled = false;
        //         that.dialogFormVisible = false;
        //       })
        //     }else {

        //       addCar(that.car).then((res) => {
        //         if(res.code == 0){
        //           that.$baseMessage("操作成功", 'success')
        //           that.$emit('fetch-data')
        //           that.close() ;
        //         }
        //         that.disabled = false;
        //         that.dialogFormVisible = false;
        //       })
        //     }
        //   } else {
        //     return false;
        //   }
        // });
      },
      changeCarType(value) {
        this.car.realName = ''
        this.car.phone = ''
        if (value === '2' || value === '3') {
          this.car.userId = ''
        }
      },

      getStallLimit(data) {
        this.car.stallId = ''
        if (this.car.stallLimit == 2) {
          if (data == undefined) {
            data = ''
          }
          stallList(data).then((res) => {
            if (res.code == 0) {
              console.log(res)
              this.stalls = res.data
            }
          })
        }
      },
      getWork(data) {
        var that = this
        that.car.realName = data.realName
        that.car.userId = data.id
      },
    },
  }
</script>
<style scoped lang="scss">
  ::v-deep {
    .avatar-uploader .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
    }
    .avatar-uploader .el-upload:hover {
      border-color: #409eff;
    }
    .avatar-uploader-icon {
      font-size: 24px;
      color: #8c939d;
      width: 78px;
      height: 78px;
      line-height: 78px;
      text-align: center;
    }
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
    }
  }
</style>
