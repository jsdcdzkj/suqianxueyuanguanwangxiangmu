<template>
  <el-dialog :close-on-click-modal="false" title="详情" :visible.sync="dialogFormVisible" width="808px" @close="close"
    top="10vh">
    <!-- <div class="tips-title"><vab-icon :icon="['fas', 'hdd']"></vab-icon>&nbsp;{{ '设备名称：'+form.name }}</div> -->
    <el-descriptions class="descriptions-self" :column="2" border label-class-name="my-label"
      content-class-name="my-content">
      <el-descriptions-item>
        <template slot="label">
          设备名称
        </template>
        <span title="kooriookami">{{ form.name }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备编码
        </template>
        <span class="contant" title="">{{ form.deviceCode }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属区域
        </template>
        <span class="contant" :title="build_title">{{build_title}}</span>
      </el-descriptions-item>
      <!-- <el-descriptions-item>
        <template slot="label">
          所属楼层
        </template>
        <span class="contant" title="二层">{{form.floorName}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属区域
        </template>
        <span class="contant" title="A区域得西北角">{{form.areaName}}</span>
      </el-descriptions-item> -->
      <el-descriptions-item>
        <template slot="label">
          安装位置
        </template>
        <span class="contant" title="二层">{{ form.place }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          供应商
        </template>
        <span class="contant" title="供应商">{{ form.supplierName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          品牌
        </template>
        <span class="contant" title="xx品牌">{{ form.brand }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备型号
        </template>
        <span class="contant" title="xx型号">{{ form.modelName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          出厂编号
        </template>
        <span class="contant" title="1010102">{{ form.serialNum }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          传输协议
        </template>
        <span class="contant" title="MQTT">{{ form.protocolName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          服务连接
        </template>
        <span title="MQTT1">{{ form.linkName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          发布主题
        </template>
        <span title="主题">{{ form.topicNameP }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          订阅主题
        </template>
        <span title="主题">{{ form.topicNameS }}</span>
      </el-descriptions-item>
      <el-descriptions-item :span="2">
        <template slot="label">
          转换模板
        </template>
        <span title="模板1">{{ form.transferName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备描述
        </template>
        <span class="contant" title="江苏省苏州市吴中区吴中大道 1188 号">
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
import { getEntity } from '@/api/deviceGateway'

export default {
  name: 'WgsbView',
  data() {
    return {
      disabled: false,
      title: '',
      form: {
        id: ''
      },
      build_title: '',
      dialogFormVisible: false,
    }
  },
  created() { },
  methods: {
    showEdit(row) {
      this.form.id = row.id
      this.fetchData();
      this.dialogFormVisible = true
    },
    close() {
      this.dialogFormVisible = false
    },
    async fetchData() {
      this.build_title = '';
      const { data } = await getEntity({ id: this.form.id });
      this.form = data;
      if (this.form.buildName && this.form.floorName && this.form.areaName) {
        this.build_title = this.form.buildName + " / " + this.form.buildName + " / " + this.form.areaName;
      }
    }

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
