<template>
  <el-dialog :close-on-click-modal="false" title="详情" :visible.sync="dialogFormVisible" width="808px" @close="close"
    top="10vh">
    <!--    <div class="tips-title"><i class="el-icon-s-promotion"></i>{{ '设备名称：'+ entity.name }}</div>-->

    <el-descriptions class="descriptions-self" :column="2" border label-class-name="my-label"
      content-class-name="my-content">
      <el-descriptions-item>
        <template slot="label">
          设备名称
        </template>
        <span class="contant" :title="entity.name">{{ entity.name }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备类型
        </template>
        <span class="contant" :title="entity.doorType">{{ doorTypeList[entity.doorType] }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备编码
        </template>
        <span class="contant" :title="entity.channelCode">{{ entity.channelCode }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属区域
        </template>
        <span class="contant" :title="entity.areaName">{{ entity.areaName }}</span>
      </el-descriptions-item>
      <!-- <el-descriptions-item>
        <template slot="label">
          所属楼层
        </template>
        <span class="contant" :title="entity.floorName">{{entity.floorName}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属区域
        </template>
        <span class="contant" :title="entity.areaName">{{entity.areaName}}</span>
      </el-descriptions-item> -->

      <el-descriptions-item>
        <template slot="label">
          供应商
        </template>
        <span class="contant" :title="entity.supplierName">{{ entity.supplierName }}</span>
      </el-descriptions-item>
      <el-descriptions-item :span="2">
        <template slot="label">
          设备型号
        </template>
        <span class="contant" :title="entity.modelName">{{ entity.modelNum }}</span>
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          设备描述
        </template>
        <span class="contant" :title="entity.deviceDesc">
          {{ entity.deviceDesc }}
        </span>
      </el-descriptions-item>
    </el-descriptions>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getEntity } from '@/api/mjsb'

export default {
  name: 'UserManagementEdit',
  data() {
    return {
      id: '',
      disabled: false,
      title: '',
      entity: {},
      build_title: '',
      dialogFormVisible: false,
      doorTypeList: ['','门禁', '道闸', '梯禁', '其他'],
    }
  },
  created() {

  },
  methods: {
    showEdit(row) {
      this.id = row.id
      // this.getEntity(row.id);
      this.dialogFormVisible = true
      this.entity = row
    },
    // getEntity(id) {
    //   this.build_title = '';
    //   getEntity({ id: id }).then(res => {
    //     this.entity = res.data
    //     if (this.entity.buildName && this.entity.floorName && this.entity.areaName) {
    //       this.build_title = this.entity.buildName + " / " + this.entity.buildName + " / " + this.entity.areaName;
    //     }
    //   })
    // },
    close() {
      this.dialogFormVisible = false
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
.contant {
  cursor: default;
}

.header-title {
  text-align: center;
  position: relative;

  .title-left {
    position: absolute;
    left: 0;
  }
}

.module-title {
  height: 39px;
  line-height: 39px;

  // text-align: center;
  >span {
    margin-left: 5px;
  }

  >span:after {
    content: ''; // 必须有这个属性，否则不显示
    display: inline-block; // 必须有，因为是行内元素
    background-color: #5470c6;
    width: 4px;
    height: 14px;
    position: relative;
    left: 8px;
    top: 2px;
  }

  >svg {
    position: relative;
    top: 3px;
  }
}

.tips-title {
  font-size: 14px;
  margin-bottom: 8px;
  font-weight: bold;
  color: #334c97;

  i {
    margin-right: 4px;
  }
}</style>
