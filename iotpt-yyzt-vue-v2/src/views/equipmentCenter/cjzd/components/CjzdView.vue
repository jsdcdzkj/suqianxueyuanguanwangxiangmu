<template>
 <el-dialog :close-on-click-modal="false" title="详情" :visible.sync="dialogFormVisible" width="808px" @close="close" top="10vh">
    <!--<div class="tips-title"><i class="el-icon-s-promotion"></i>{{ '设备名称：'+form.name }}</div>-->

    <el-descriptions class="descriptions-self" :column="2" border label-class-name="my-label"
                     content-class-name="my-content">
      <el-descriptions-item>
        <template slot="label">
          设备名称
        </template>
        <span class="contant" :title="form.name">{{ form.name }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备类型
        </template>
        <span class="contant" :title="form.deviceTypeName">{{ form.deviceTypeName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备编码
        </template>
        <span class="contant" :title="form.deviceCode">{{ form.deviceCode }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备状态
        </template>
        <span class="contant" :title="form.statusName">{{ form.statusName }}</span>
        <!--<span class="contant" :title="在线"> <el-tag size="small" type="success">在线</el-tag></span>-->
        <!-- <el-tag size="small" type="warning">离线</el-tag> -->
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属分项
        </template>
        <span class="contant" :title="form.subitemName">{{ form.subitemName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属网关
        </template>
        <span class="contant" :title="form.gatewayName">{{ form.gatewayName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          物理位置
        </template>
        <span class="contant" :title="form.areaNames">{{ form.areaNames }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          安装位置
        </template>
        <span class="contant" :title="form.place">{{ form.place }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          逻辑位置
        </template>
        <span class="contant" :title="form.logicalAreaNames">{{ form.logicalAreaNames }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          是否总设备
        </template>
        <span class="contant" title="是" v-if="form.isTotalDevice == 1">是</span>
        <span class="contant" title="否" v-else>否</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          安装时期
        </template>
        <span class="contant" :title="form.installationDate">{{ form.installationDate }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          使用日期
        </template>
        <span class="contant" :title=" form.useTime"> {{ form.useTime }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备管理员
        </template>
        <span class="contant" :title="form.userName">{{ form.userName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          管理员电话
        </template>
        <span class="contant" :title="form.phone">{{ form.phone }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          供应商名称
        </template>
        <span class="contant" :title="form.supplierName">{{ form.supplierName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备型号
        </template>
        <span class="contant" :title="form.modelName">{{ form.modelName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          维保公司
        </template>
        <span class="contant" :title="form.companyName">{{ form.companyName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          年检日期
        </template>
        <span class="contant" :title="form.inspectionDate"> {{ form.inspectionDate }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          采购日期
        </template>
        <span class="contant" :title="form.procureDate"> {{ form.procureDate }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          过保日期
        </template>
        <span class="contant" :title="form.expirationDate"> {{ form.expirationDate }}</span>
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          设备描述
        </template>
        <span class="contant" :title="form.deviceDesc">
        {{ form.deviceDesc }}
        </span>
      </el-descriptions-item>
    </el-descriptions>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {getEntity} from '@/api/deviceCollect'
  import CjzdTable from './cjzdTable'

  export default {
    name: 'CjzdView',
    components: {CjzdTable},
    data() {
      return {
        disabled: false,
        title: '',
        dialogFormVisible: false,
        form: {},
      }
    },
    created() {
    },
    methods: {
      showEdit(row) {
        // this.title = row
        this.getData(row);
        this.dialogFormVisible = true
      },
      async getData(id) {
        const {data} = await getEntity({id: id});
        this.form = data;
      },
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
    > span {
      margin-left: 5px;
    }

    > span:after {
      content: ''; // 必须有这个属性，否则不显示
      display: inline-block; // 必须有，因为是行内元素
      background-color: #5470c6;
      width: 4px;
      height: 14px;
      position: relative;
      left: 8px;
      top: 2px;
    }

    > svg {
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
  }
</style>
