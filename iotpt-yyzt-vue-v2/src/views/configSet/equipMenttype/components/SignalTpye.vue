<template>
 <el-dialog :close-on-click-modal="false" title="信号名称" :visible.sync="dialogFormVisible" width="1102px" @close="close">

    <el-row :gutter="20" class="signal-type">
      <el-col :span="11">
        <el-form :inline="true" :model="staffTemp">
          <el-form-item label="类型名称">
            <el-input v-model="staffTemp.signalTypeName"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getStaffList">查找</el-button>
          </el-form-item>
        </el-form>
        <el-table
          ref="staffTable"
          v-loading="listLoading"
          :key="tableKey"
          :data="staffList"
          border
          height="495"
          fit
          :row-key="row => row.id"
          highlight-current-row
          @selection-change="handleStaffChange"
          @row-click="handleClickTableRow"
        >
          <el-table-column type="selection" :reserve-selection="true" width="55"></el-table-column>
          <el-table-column label="信号名称" align="center">
            <template slot-scope="{row}">
              <span>{{ row.signalTypeName }}</span>
            </template>
          </el-table-column>

          <el-table-column label="信号编码" align="center">
            <template slot-scope="{row}">
              <span>{{ row.signalTypeCode }}</span>
            </template>
          </el-table-column>
          <el-table-column label="描述" align="center">
            <template slot-scope="{row}">
              <span>{{ row.signalTypeDesc }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <el-col :span="2" style="text-align:center;" class="move-button">
        <el-button
          @click="addStaff"
          type="primary"
          :disabled="!staffData.length"
          icon="el-icon-arrow-right"
          circle
        ></el-button>
        <br/>
        <el-button
          @click="removeStaff"
          type="primary"
          :disabled="!selectedStaffData.length"
          icon="el-icon-arrow-left"
          circle
          style="margin-left: 0;margin-top: 10px;"
        ></el-button>
      </el-col>
      <el-col :span="11">
        <div class="name">
          <svg t="1683600455505" class="icon" viewBox="0 0 1072 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
               p-id="3736" width="20" height="20">
            <path
              d="M198.119619 97.52381h725.333333C952.027429 97.52381 975.238095 119.954286 975.238095 147.504762V293.546667c0 27.599238-23.210667 49.980952-51.833905 49.980952H198.119619C169.545143 343.527619 146.285714 321.145905 146.285714 293.546667V147.504762C146.285714 119.905524 169.496381 97.52381 198.119619 97.52381z m77.287619 98.401523a25.063619 25.063619 0 0 0-25.502476 24.576c0 13.604571 11.410286 24.624762 25.502476 24.624762h156.233143a25.063619 25.063619 0 0 0 25.502476-24.624762 25.063619 25.063619 0 0 0-25.502476-24.576H275.407238z m-77.287619 193.097143h725.333333c28.623238 0 51.785143 22.430476 51.785143 49.980953v146.041904c0 27.599238-23.210667 49.93219-51.833905 49.932191H198.119619c-28.574476 0-51.785143-22.332952-51.785143-49.932191V439.003429c0-27.599238 23.210667-49.980952 51.833905-49.980953z m0 291.498667h725.333333c28.623238 0 51.785143 22.381714 51.785143 49.93219v146.090667C975.238095 904.045714 952.027429 926.47619 923.40419 926.47619H198.119619C169.545143 926.47619 146.285714 904.094476 146.285714 876.495238V730.453333c0-27.599238 23.210667-49.980952 51.833905-49.980952z m77.287619-181.930667a25.063619 25.063619 0 0 0-25.502476 24.576c0 13.604571 11.410286 24.624762 25.502476 24.624762h156.233143a25.063619 25.063619 0 0 0 25.502476-24.624762 25.063619 25.063619 0 0 0-25.502476-24.576H275.407238z"
              fill="#5470c6" p-id="3737"></path>
          </svg>
          设备类型名称 {{form.deviceTypeName}}
        </div>
        <el-table
          ref="selectedStaffTable"
          v-loading="listLoading"
          :key="tableKey"
          :data="selectedStaffList"
          border
          height="495"
          :row-key="row => row.id"
          fit
          highlight-current-row
          @selection-change="handleSelectedStaffChange"
          @row-click="handleClickRightTableRow"
        >
          <el-table-column type="selection" :reserve-selection="true" width="55"></el-table-column>
          <el-table-column label="信号名称" align="center">
            <template slot-scope="{row}">
              <span>{{ row.signalTypeName }}</span>
            </template>
          </el-table-column>

          <el-table-column label="信号编码" align="center">
            <template slot-scope="{row}">
              <span>{{ row.signalTypeCode }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script>
  import { getEntityByTId, getSignalType, saveOrUpdate } from '@/api/configDeviceSignalMap'
import bumen from '@/components/bumen'

  export default {
    name: 'UserManagementEdit',
    components: {bumen},
    data() {
      return {
        form: {},
        listLoading: true,
        staffTemp: {
          signalTypeName: "",
          id: '',
          nickName: "",
          staffTypeId: ""
        },
        staffList: [],
        selectedStaffList: [],
        staffData: [],
        selectedStaffData: [],
        tableKey: 0,
        rowKey: "rowKey",
        staffOptions: [
          {key: 28, display_name: "补货员"},
          {key: 29, display_name: "测试员"}
        ],

        loading: false,
        disabled: false,
        title: '',
        dialogFormVisible: false,
        signalList: [],
      }
    },
    created() {
    },
    methods: {
      showEdit(row) {
        this.form = Object.assign({}, row)
        this.staffTemp.id = this.form.id
        this.fetchData1()
        this.getStaffList()
        this.dialogFormVisible = true
      },
      close() {
        // this.$refs['form'].resetFields()
        // this.form = this.$options.data().form
        this.dialogFormVisible = false
      },
      save() {
        if (this.loading) {
          return
        }
        // this.$refs['form'].validate(async (valid) => {
        //   if (valid) {
        //     this.loading = true
        //     this.disabled = true
        //     setTimeout(() => {
        //       this.loading = false
        //       this.disabled = false
        //     }, 1000)

        //     const { msg } = await doEdit(this.form)
        //     this.$baseMessage(msg, 'success')
        //     this.$emit('fetch-data')
        //     this.close()
        //   } else {
        //     return false
        //   }
        // })
      },
      openwin() {
        var that = this
        that.$refs['bumen'].showDia()
      },
      // 从后台获取左边表格的数据
      getStaffList() {
        // fetchStaffList(this.staffTemp).then(res => {
        //   if (res.value.staff.length === 0) {
        //     alert("查无此人");
        //   }
        //   this.staffList = res.value.staff;
        // });
        this.fetchData()

        this.listLoading = false
      },
      // 将左边表格选择项存入staffData中
      handleStaffChange(rows) {
        this.staffData = [];
        if (rows) {
          rows.forEach(row => {
            if (row) {
              this.staffData.push(row);
            }
          });
        }
      },
      handleClickTableRow(row, event, column) {
        console.log(row);
        console.log(column);
        this.$refs.staffTable.toggleRowSelection(row);
      },
      handleClickRightTableRow(row, event, column) {
        console.log(row);
        console.log(column);
        this.$refs.selectedStaffTable.toggleRowSelection(row);
      },
      // 左边表格选择项移到右边
      addStaff() {
        setTimeout(() => {
          this.$refs["staffTable"].clearSelection();
          this.$refs["selectedStaffTable"].clearSelection();
        }, 0);
        let repeat = false;
        this.selectedStaffList.forEach(item => {
          if (this.staffData[0] && item.id === this.staffData[0].id) {
            repeat = true;
            alert("已添加");
            return;
          }
        });
        if (repeat === false) {
          this.staffData.forEach(item => {
            this.selectedStaffList.push(item);
          });
          for (let i = 0; i < this.staffList.length; i++) {
            for (let j = 0; j < this.staffData.length; j++) {
              if (
                this.staffList[i] &&
                this.staffData[j] &&
                this.staffList[i].id === this.staffData[j].id
              ) {
                this.staffList.splice(i, 1);
              }
            }
          }
        }
        this.save()
      },
      // 右边表格选择项移到左边
      removeStaff() {
        setTimeout(() => {
          this.$refs["staffTable"].clearSelection();
          this.$refs["selectedStaffTable"].clearSelection();
        }, 0);
        this.selectedStaffData.forEach(item => {
          this.staffList.push(item);
        });
        for (let i = 0; i < this.selectedStaffList.length; i++) {
          for (let j = 0; j < this.selectedStaffData.length; j++) {
            if (
              this.selectedStaffList[i] &&
              this.selectedStaffData[j] &&
              this.selectedStaffList[i].id === this.selectedStaffData[j].id
            ) {
              this.selectedStaffList.splice(i, 1);
            }
          }
        }
        this.save()
      },
      // 将右边表格选择项存入selectedStaffData中
      handleSelectedStaffChange(rows) {
        this.selectedStaffData = [];
        if (rows) {
          rows.forEach(row => {
            if (row) {
              this.selectedStaffData.push(row);
            }
          });
        }
      },
      // 提交
      modifyStaff() {
        let isEmpty = false;
        this.selectedStaffList.forEach(item => {
          if (!item.staffTypeId) {
            alert("请选择类型");
            isEmpty = true;
            return;
          }
        });
        if (isEmpty === false) {
          editStaff(this.selectedStaffList, this.deviceQuery.id).then(res => {
            this.staffListVisible = false;
            this.$notify({
              title: "成功",
              message: "修改成功",
              type: "success",
              duration: 2000
            });
          });
        }
      },
      async fetchData() {
        // this.listLoading = true
        const {data} = await getSignalType(this.staffTemp)
        this.signalList = data;
        this.staffList = []
        for (let index = 0; index < this.signalList.length; index++) {
          this.staffList.push({
            id: this.signalList[index].id,
            signalTypeName: this.signalList[index].signalTypeName,
            signalTypeCode: this.signalList[index].signalTypeCode,
            signalTypeDesc: this.signalList[index].signalTypeDesc,
          })
        }
        // setTimeout(() => {
        //   this.listLoading = false
        // }, 300)
      },
      async fetchData1() {
        // this.listLoading = true
        const {data} = await getEntityByTId({tId: this.form.id})
        this.selectedStaffList = []
        for (let index = 0; index < data.length; index++) {
          console.log(data[index])
          this.selectedStaffList.push({
            id: data[index].id,
            signalTypeName: data[index].signalTypeName,
            signalTypeCode: data[index].signalTypeCode,
            signalTypeDesc: data[index].signalTypeDesc,
          })
        }
        // setTimeout(() => {
        //   this.listLoading = false
        // }, 300)
      },
      async save() {
        if (this.loading) {
          return
        }
        this.loading = true
        this.disabled = true
        setTimeout(() => {
          this.loading = false
          this.disabled = false
        }, 1000)
        let ids = []
        for (let i = 0; i < this.selectedStaffList.length; i++) {
          ids.push(this.selectedStaffList[i].id)
        }
  
        // for (let i = 0; i < this.staffData.length; i++) {
        //   ids.push(this.staffData[i].id)
        // }
        let saveForm = {
          sIds: ids.join(','),
          deviceTypeId: this.form.id,

        }
        const {msg} = await saveOrUpdate(saveForm)
        this.$baseMessage(msg, 'success')
        this.fetchData()
        this.fetchData1()
        // this.close()
      },
    },
  }
</script>
<style lang="scss" scoped>
  .name {
    height: 51px;
    line-height: 35px;
    font-size: 16px;

    > svg {
      position: relative;
      top: 5px;
    }
  }

  .move-button {
    margin-top: 221px;
    // height: 500px;
  }
</style>

<style lang="scss">
  .signal-type {
    tbody .el-table__cell:first-child .cell {
      padding-left: 15px;
    }
  }
</style>
