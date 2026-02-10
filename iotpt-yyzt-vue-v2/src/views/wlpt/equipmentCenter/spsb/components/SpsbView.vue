<template>
 <el-dialog :close-on-click-modal="false" title="详情" :visible.sync="dialogFormVisible" append-to-body modal-append-to-body width="808px" @close="close" top="10vh">
<!--    <div class="tips-title"><i class="el-icon-s-promotion"></i>{{ '设备名称：' + entity.name }}</div>-->
    <el-descriptions class="descriptions-self" :column="2" border label-class-name="my-label" content-class-name="my-content">
      <el-descriptions-item >
        <template slot="label">
          设备名称
        </template>
        <span class="contant" :title="entity.name">{{entity.name}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备编码
        </template>
        <span class="contant" :title="entity.deviceCode">{{entity.deviceCode}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属楼宇
        </template>
        <span class="contant" :title="entity.buildName">{{entity.buildName}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
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
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          安装位置
        </template>
        <span class="contant" :title="entity.installPosition">{{entity.installPosition}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          供应商
        </template>
        <span class="contant" :title="entity.supplierName">{{entity.supplierName}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备型号
        </template>
        <span class="contant" :title="entity.modelName">{{entity.modelName}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备通道号
        </template>
        <span class="contant" :title="entity.channel">{{entity.channel}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          NVR通道号
        </template>
        <span class="contant" :title="entity.nvrChannel">{{entity.nvrChannel}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          窗口号
        </template>
        <span class="contant" :title="entity.windowNum">{{entity.windowNum}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          硬盘机id
        </template>
        <span class="contant" :title="entity.nvrId">{{entity.nvrId}}</span>
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          ip地址
        </template>
        <span class="contant" :title="entity.ip">{{entity.ip}}</span>
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          设备端口
        </template>
        <span class="contant" :title="entity.port">{{entity.port}}</span>
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          设备经度
        </template>
        <span class="contant" :title="entity.pointX">{{entity.pointX}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备纬度
        </template>
        <span class="contant" :title="entity.pointY">{{entity.pointY}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          拍摄区域横坐标
        </template>
        <span class="contant" :title="entity.catchX">{{entity.catchX}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          拍摄区域纵坐标
        </template>
        <span class="contant" :title="entity.catchY">{{entity.catchY}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          h5token
        </template>
        <span class="contant" :title="entity.token">{{entity.token}}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          是否视频流
        </template>
        <span class="contant" v-if="entity.isVideoStream == 0" title="否">否</span>
        <span class="contant" v-else-if="entity.isVideoStream == 1" title="是">是</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备状态
        </template>
        <span class="contant" v-if="entity.status == 0" title="禁用">禁用</span>
        <span class="contant" v-else-if="entity.status == 1" title="启用">启用</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备描述
        </template>
        <span class="contant" :title="entity.deviceDesc">{{entity.deviceDesc}}</span>
      </el-descriptions-item>
    </el-descriptions>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getEntity } from '@/api/spsb'

export default {
  name: 'WgsbView',
  data() {
    return {
      disabled: false,
      title: '',
      entity: {},
      dialogFormVisible: false,
    }
  },
  created() {},
  methods: {
    showEdit(row) {
      this.title = row.id
      this.getEntity(row.id);
      this.dialogFormVisible = true
    },
    getEntity(id) {
      getEntity({id: id}).then(res => {
        this.entity = res.data
      })
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
