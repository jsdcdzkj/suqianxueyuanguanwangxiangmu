<template>
  <el-table :data="tableData" style="width: 100%" height="100vh-250">
    <el-table-column prop="name" label="设备名称" align="center" width="200" >
    </el-table-column>
    <el-table-column prop="deviceTypeName" label="设备类型" align="center" width="200" >
    </el-table-column>
    <el-table-column prop="areaNames" label="物理位置" align="center" >
    </el-table-column>
    <el-table-column prop="subitemName" label="设备分项" align="center" width="200" >
    </el-table-column>
    <el-table-column prop="supplierName" label="供应商" align="center" width="200" >
    </el-table-column>
    <el-table-column fixed="right" label="操作" width="120" align="center">
      <template slot-scope="scope">
        <el-button icon="el-icon-delete" type="danger" size="mini" @click.native.prevent="deleteRow(scope.$index, tableData)">移除</el-button>
        <!--<el-button @click.native.prevent="deleteRow(scope.$index, tableData)" type="text" size="small">-->
          <!--移除-->
        <!--</el-button>-->
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
  export default {
    props: {
      tableData: {
        type: Array,
        default: []
      },
    },
    methods: {
      deleteRow(index, rows) {
        rows.splice(index, 1)
      },
    },
    data() {
      return {
        // tableData: [
        //   {
        //     date: '2016-05-03',
        //     name: '王小虎',
        //     province: '上海',
        //     city: '普陀区',
        //     address: '上海市普陀区金沙江路 1518 弄',
        //     zip: 200333,
        //   },
        //   {
        //     date: '2016-05-02',
        //     name: '王小虎',
        //     province: '上海',
        //     city: '普陀区',
        //     address: '上海市普陀区金沙江路 1518 弄',
        //     zip: 200333,
        //   },
        //   {
        //     date: '2016-05-04',
        //     name: '王小虎',
        //     province: '上海',
        //     city: '普陀区',
        //     address: '上海市普陀区金沙江路 1518 弄',
        //     zip: 200333,
        //   },
        //   {
        //     date: '2016-05-01',
        //     name: '王小虎',
        //     province: '上海',
        //     city: '普陀区',
        //     address: '上海市普陀区金沙江路 1518 弄',
        //     zip: 200333,
        //   },
        //   {
        //     date: '2016-05-08',
        //     name: '王小虎',
        //     province: '上海',
        //     city: '普陀区',
        //     address: '上海市普陀区金沙江路 1518 弄',
        //     zip: 200333,
        //   },
        //   {
        //     date: '2016-05-06',
        //     name: '王小虎',
        //     province: '上海',
        //     city: '普陀区',
        //     address: '上海市普陀区金沙江路 1518 弄',
        //     zip: 200333,
        //   },
        //   {
        //     date: '2016-05-07',
        //     name: '王小虎',
        //     province: '上海',
        //     city: '普陀区',
        //     address: '上海市普陀区金沙江路 1518 弄',
        //     zip: 200333,
        //   },
        // ],
      }
    },
  }
</script>
