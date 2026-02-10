<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <div class="tips-title"><i class="el-icon-s-promotion"></i>转换数据</div>
        <el-row :gutter="20" style="margin-bottom: 12px;">
          <el-col :span="12">
            <el-select v-model="value" placeholder="请选择连接">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="12">
            <el-select v-model="value" placeholder="请选择主题">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-col>
        </el-row>
        <div style="margin-bottom: 12px;">
          <el-button type="primary" size="mini" :loading="loading">获取数据</el-button>
          <el-button size="mini" >格式校验</el-button>
        </div>
        <div>
          <json-viewer :value="jsonData" :expand-depth=5 copyable boxed expanded></json-viewer>
        </div>
      </el-col>
      <el-col :span="16">
        <div class="tips-title"><i class="el-icon-s-promotion"></i>解析规则配置</div>

        <el-form label-width="0px"  :model="formLabelAlign" :rules="rules"  ref="ruleForm">
          <el-table :data="tableData" border style="width: 100%" class="shuju-table" :header-cell-style="arraySpanMethod" :span-method="spanMethod">
            <el-table-column prop="1" key='entrustStatus'>
              <template slot-scope="scope">
                <div v-if="scope.$index==0" class="big-title">网关信息</div>
                <div v-if="scope.$index==2" class="big-title">设备信息</div>
                <div v-if="scope.$index==4" class="big-title">信号信息</div>
                <div v-if="scope.$index==8" class="big-title">数据信息</div>
              </template>
            </el-table-column>
            <el-table-column prop="2" label="系统对象字段">
              <template slot-scope="scope">
                <div class="small-title" v-if="scope.$index <= 8">{{ propArr[scope.$index].name }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="3">
              <template slot-scope="scope">
                <div class="small-title" v-if="scope.$index <= 7">
                  <el-input placeholder="请输入内容" v-model="propArr[scope.$index].data" :disabled="true"> </el-input>
                </div>
                <div v-if="scope.$index==8" class="small-title">数据</div>
                <div v-if="scope.$index==10" class="small-title">告警</div>
                <div v-if="scope.$index==12" class="small-title">心跳</div>
              </template>
            </el-table-column>
            <el-table-column prop="4">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.$index }}， {{ scope.column.property}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="5">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.$index }}， {{ scope.column.property}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="6" label="解析对应字段">
              <template slot-scope="scope">
                <div class="small-title" v-if="scope.$index <= 7">{{ propArr[scope.$index].name }}</div>
                <div class="small-title" v-if="scope.$index >= 8">{{ propArr2[scope.$index-8].name }}</div>
                <!-- <span style="margin-left: 10px">{{ scope.$index }}， {{ scope.column.property}}</span> -->
              </template>
            </el-table-column>
            <el-table-column prop="7">
              <template slot-scope="scope">
                <el-form-item label="" prop="name">
                  <el-input v-model="formLabelAlign.name" :disabled="true"></el-input>
                </el-form-item>
                <!-- <span style="margin-left: 10px">{{ scope.$index }}， {{ scope.column.property}}</span> -->
              </template>
            </el-table-column>
            <el-table-column prop="8">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.$index }}， {{ scope.column.property}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="9">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.$index }}， {{ scope.column.property}}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-form>
      </el-col>
    </el-row>
  </div>

</template>

<script>
export default {
  data() {
    return {
      // 临时内容
      input: '临时内容',
      jsonData: {
        code: 200,
        msg: 'success',
        totalCount: 3,
        data: [
          {
            id: '120000202006264860',
            username: 'admin',
            password: 'admin',
            email: 'u.tobcl@nndnwxjt.sd',
            permissions: ['admin'],
            datatime: '1993-06-03 05:16:54',
            hidden: 1,
          },
          {
            id: '13000019871117234X',
            username: 'editor',
            password: 'editor',
            email: 'j.kbri@szdb.at',
            permissions: ['editor'],
            datatime: '1995-11-03 15:02:20',
            hidden: 1,
          },
          {
            id: '620000197006181111',
            username: 'test',
            password: 'test',
            email: 'y.ksvnky@sssntudr.cv',
            permissions: ['admin', 'editor'],
            datatime: '2005-10-08 02:17:06',
            hidden: 1,
          },
        ],
      },
      tableData: new Array(14).fill({}),
      propArr: [
        { name: '网关主键', data: '临时数据' },
        { name: '网关名称', data: '临时数据' },
        { name: '设备主键', data: '临时数据' },
        { name: '设备名称', data: '临时数据' },
        { name: '信号主键', data: '临时数据' },
        { name: '信号名称', data: '临时数据' },
        { name: '信号值', data: '临时数据' },
        { name: '采集时间', data: '临时数据' },
        { name: '数据类型', data: '数据类型' },
      ],
      propArr2: [
        { name: '数据key', data: '临时数据' },
        { name: 'value', data: '临时数据' },
        { name: '数据key', data: '临时数据' },
        { name: 'value', data: '临时数据' },
        { name: '数据key', data: '临时数据' },
        { name: 'value', data: '临时数据' },
      ],
      formLabelAlign: {
        name: '',
        region: '',
        type: '',
      },
      rules: {
        name: [
          { required: true, message: '请输入活动名称', trigger: 'blur' },
          { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' },
        ]
      },
    }
  },
  methods: {
    
    close() {
      this.$refs['ruleForm'].resetFields()
      this.form = this.$options.data().form
      this.dialogFormVisible = false
    },
    save() {
      if (this.loading) {
        return
      }
      this.$refs['ruleForm'].validate(async (valid) => {
        if (valid) {
          this.$baseMessage('成功', 'success')
          // this.$emit('fetch-data')
          // this.close()
        } else {
          return false
        }
      })
    },
    arraySpanMethod({ row, column, rowIndex, columnIndex }) {
      // table不能让第0个表头合并，所以第0个表头的colSpan设置为0，合并前4个表头
      row[1].colSpan = 5
      row[0].colSpan = 0
      row[2].colSpan = 0
      row[3].colSpan = 0
      row[4].colSpan = 0
      // 隐藏后2、0、3表头
      if (
        columnIndex === 2 ||
        columnIndex === 0 ||
        columnIndex === 3 ||
        columnIndex === 4
      ) {
        return 'display: none'
      }
      // 合并后三个表头
      for (let index = 5; index <= 8; index++) {
        if (index === 5) {
          row[index].colSpan = 4
        } else {
          row[index].colSpan = 0
        }
      }
      // 隐藏5、6表头
      if (columnIndex > 5) {
        return 'display: none'
      }
    },
    spanMethod({ row, column, rowIndex, columnIndex }) {
      // 第一列，12、34、合并行
      if (columnIndex === 0 && rowIndex <= 3) {
        if (rowIndex % 2 === 0) {
          return {
            rowspan: 2,
            colspan: 1,
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          }
        }
      }
      // 第一列，4、5、6、7、合并行
      if (columnIndex === 0 && 3 <= rowIndex && rowIndex <= 7) {
        if (rowIndex % 4 === 0) {
          return {
            rowspan: 4,
            colspan: 1,
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          }
        }
      }
      // 第一、二列，4、5、6、7、合并行
      if ((columnIndex === 0 || columnIndex === 1) && 8 <= rowIndex) {
        if (rowIndex === 8) {
          return {
            rowspan: 8,
            colspan: 1,
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          }
        }
      }

      // 第九行，4、5、6、7、合并行
      if (columnIndex === 2 && 8 <= rowIndex) {
        if ((rowIndex - 7) % 2) {
          return {
            rowspan: 2,
            colspan: 3,
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          }
        }
      }

      // 第一行，3、4、5合并列
      if (2 <= columnIndex && columnIndex <= 4) {
        if (columnIndex == 2) {
          return {
            rowspan: 1,
            colspan: 3,
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          }
        }
      }
      if (6 <= columnIndex && columnIndex <= 9) {
        if (columnIndex == 6) {
          return {
            rowspan: 1,
            colspan: 3,
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          }
        }
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.big-title {
  // background: #f5f5f5;
  width: 100%;
  height: 100%;
  font-weight: bold;
  display: flex;
  justify-content: center;
  align-items: center;
}
.small-title {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
<style lang="scss">
.shuju-table {
  .cell {
    width: 100%;
    height: 100%;
    padding: 0px !important;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .el-input__inner {
    width: 97%;
    margin: 2px 10px 2px 3px;
  }
  .el-form-item {
    width: 100%;
  }
  

  .el-form-item--small.el-form-item {
    margin-bottom: 0px!important;

  }
}
</style>