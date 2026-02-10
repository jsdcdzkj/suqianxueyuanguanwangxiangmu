<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    width="500px"
    @close="close"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="物模型名称" prop="signalTypeName">
        <el-input
          v-model.trim="form.signalTypeName"
          autocomplete="off"
          placeholder="请输入物模型名称"
        ></el-input>
      </el-form-item>
      <el-form-item label="物模型编码" prop="signalTypeCode">
        <el-input
          v-model.trim="form.signalTypeCode"
          autocomplete="off"
          placeholder="请输入物模型编码"
        ></el-input>
      </el-form-item>
      <el-form-item label="物模型描述">
        <el-input
          type="textarea"
          v-model="form.signalTypeDesc"
          placeholder="请输入物模型描述"
        ></el-input>
      </el-form-item>
      <el-form-item label="物模型类型" prop="signalType">
        <el-radio-group v-model="form.signalType" clearable>
          <el-radio-button
            v-for="item in XinHaoList"
            :key="item.dictValue"
            :label="item.dictValue"
            :value="item.dictValue"
          >
            {{ item.dictLabel }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="数据类型" prop="dataType">
        <el-select
          v-model="form.dataType"
          placeholder="请选择"
          class="w"
          clearable
        >
          <el-option
            v-for="item in dataTypeList"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        v-if="form.dataType === '1'"
        label="单位"
        prop="unitInt"
        :rules="rules.unit"
      >
        <el-input
          v-model.trim="form.unitInt"
          autocomplete="off"
          placeholder="单位符号，例如：A"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-else-if="form.dataType === '2'"
        label="单位"
        prop="unitNum"
        :rules="rules.unit"
      >
        <el-input
          v-model.trim="form.unitNum"
          autocomplete="off"
          placeholder="单位符号，例如：℃"
        ></el-input>
      </el-form-item>
      <div class="unitItem" v-else-if="form.dataType === '3'">
        <span class="unit-title">
          <i>*</i>
          枚举项
        </span>
        <div
          class="unit-enum-wrap"
          v-for="(item, index) in form.enum"
          :key="index"
        >
          <el-form-item
            :prop="'enum.' + index + '.itemKey'"
            :rules="rules.enum"
          >
            <el-input
              v-model.trim="item.itemKey"
              autocomplete="off"
              clearable
              placeholder="参数值，例如：2"
            ></el-input>
          </el-form-item>
          <span>:</span>
          <el-form-item
            :prop="'enum.' + index + '.itemVal'"
            :rules="rules.enumDes"
          >
            <el-input
              v-model.trim="item.itemVal"
              autocomplete="off"
              clearable
              placeholder="参数描述，例如：制冷"
            ></el-input>
          </el-form-item>
          <span class="deleteBtn" v-if="index > 0" @click="deleteUnit(index)">
            删除
          </span>
        </div>
        <span class="unit-btn" @click="addUnit">
          <i class="el-icon-plus"></i>
          添加枚举项
        </span>
      </div>

      <div v-else class="unit-bool-wrap">
        <span class="unit-title">
          <i>*</i>
          布尔值
        </span>
        <el-form-item prop="bool.itemKey" :rules="rules.boolDes">
          <el-input
            v-model.trim="form.bool.itemKey"
            autocomplete="off"
            placeholder="例如：关闭"
          ></el-input>
          （0值对应的文本）
        </el-form-item>
        <el-form-item prop="bool.itemVal" :rules="rules.boolDes">
          <el-input
            v-model.trim="form.bool.itemVal"
            autocomplete="off"
            placeholder="例如：开启"
          ></el-input>
          （1值对应的文本）
        </el-form-item>
      </div>
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
  import { saveOrUpdate, getEntity } from '@/api/sinalType'

  import { getRedisDictList } from '@/api/sysDict'

  export default {
    name: 'UserManagementEdit',
    data() {
      return {
        loading: false,
        disabled: false,
        XinHaoList: [],
        dataTypeList: [],
        form: {
          signalTypeName: '',
          signalTypeCode: '',
          signalTypeDesc: '',
          signalType: '',
          dataType: '1',
          unitInt: '',
          unitNum: '',
          bool: {},
          enum: [{}],
        },
        rules: {
          signalTypeName: [
            { required: true, trigger: 'blur', message: '请输入物模型名称' },
          ],
          signalTypeCode: [
            { required: true, trigger: 'blur', message: '请输入物模型编码' },
          ],
          signalType: [
            { required: true, trigger: 'change', message: '请选择物模型类型' },
          ],
          dataType: [
            { required: true, trigger: 'change', message: '请选择数据类型' },
          ],
          unit: [{ required: true, trigger: 'blur', message: '请输入单位' }],
          enum: [{ required: true, trigger: 'blur', message: '请输入参数值' }],
          enumDes: [
            { required: true, trigger: 'blur', message: '请输入参数描述' },
          ],
          boolDes: [
            { required: true, trigger: 'blur', message: '请输入文本对应的值' },
          ],
        },
        title: '',
        dialogFormVisible: false,
        signalStatisticalTypes: [],
      }
    },
    methods: {
      addUnit() {
        this.form.enum.push({})
      },
      deleteUnit(index) {
        let newArr = []
        if (index == this.form.enum.length - 1) {
          //为尾
          newArr = this.form.enum.slice(0, this.form.enum.length - 1)
        } else {
          //为中
          let arr1 = this.form.enum.slice(0, index)
          let arr2 = this.form.enum.slice(index + 1)
          newArr = arr1.concat(arr2)
        }
        this.form.enum = [].concat(newArr)
      },
      showEdit(row) {
        this.getXinHaoType()
        this.getDictByKey().then((res) => {
          this.signalStatisticalTypes = res.data['signalStatisticalType']
        })
        if (!row) {
          this.title = '添加'
        } else {
          this.title = '编辑'

          getEntity({ id: row.id }).then((res) => {
            console.log(res.data)
            // 编辑状态下form表单数据
            const { items, dataType } = res.data || []
            res.data.enum = [{}]
            res.data.bool = {}
            switch (dataType) {
              case '2':
                res.data.unitNum = items[0].itemVal
                break
              case '3':
                res.data.enum = items
                break
              case '4':
                res.data.bool = items[0]
                break
              default:
                res.data.unitInt = items[0]?.itemVal
            }
            this.form = res.data
          })
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
          if (valid) {
            this.loading = true
            this.disabled = true
            setTimeout(() => {
              this.loading = false
              this.disabled = false
            }, 1000)
            if (this.form.dataType === '1') {
              this.form.items = [{ itemVal: this.form.unitInt, isDel: '0' }]
            } else if (this.form.dataType === '2') {
              this.form.items = [{ itemVal: this.form.unitNum, isDel: '0' }]
            } else if (this.form.dataType === '3') {
              this.form.items = this.form.enum.map((val) => {
                val.isDel = '0'
                return val
              })
            } else if (this.form.dataType === '4') {
              this.form.items = [
                {
                  ...this.form.bool,
                  isDel: '0',
                },
              ]
            }

            saveOrUpdate(this.form).then((res) => {
              if (0 == res.code) {
                this.$baseMessage(this.title + '成功', 'success')
                this.$emit('fetch-data')
                this.close()
              } else {
                this.$baseMessage(res.msg, 'error')
              }
            })
          } else {
            return false
          }
        })
      },
      async getXinHaoType() {
        const { data } = await getRedisDictList({ dictType: 'wlSignalType' })
        this.XinHaoList = data

        const res = await getRedisDictList({ dictType: 'wlDataType' })
        this.dataTypeList = res.data
      },
    },
  }
</script>
<style lang="scss" scoped>
  .unit-title {
    width: 86px;
    text-align: right;
    font-size: 14px;
    line-height: 32px;
    float: left;

    i {
      color: red;
    }
  }
  .unitItem {
    .el-form-item {
      display: inline-block;
      width: 40%;

      ::v-deep .el-form-item__content {
        margin-left: 0 !important;
      }
    }
    .deleteBtn {
      color: #f00;
      padding-left: 10px;
    }

    .unit-enum-wrap {
      margin-left: 100px;
    }

    .unit-btn {
      margin-left: 100px;
    }
  }
  .unit-bool-wrap {
    .el-input {
      width: 40%;
    }
  }
</style>
