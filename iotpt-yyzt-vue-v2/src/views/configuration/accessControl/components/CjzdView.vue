<template>
  <el-dialog title="查看" :visible.sync="dialogFormVisible" width="808px" @close="close" top="10vh">
    <div class="tips-title"><i class="el-icon-s-promotion"></i>{{ '设备名称：'+title }}</div>

    <el-descriptions class="descriptions-self" :column="2" border label-class-name="my-label" content-class-name="my-content">
      <el-descriptions-item>
        <template slot="label">
          设备名称
        </template>
        <span class="contant" title="kooriookami">kooriookami</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备类型
        </template>
        <span class="contant" title="18100000000">18100000000</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备编码
        </template>
        <span class="contant" title="18100000000181000000001810000000018100000000">18100000000181000000001810000000018100000000</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备状态
        </template>
        <span class="contant" title="在线"> <el-tag size="small" type="success">在线</el-tag></span>
        <!-- <el-tag size="small" type="warning">离线</el-tag> -->
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属分项
        </template>
        <span class="contant" title="123123123">123123123</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属网关
        </template>
        <span class="contant" title="第一个网关">第一个网关</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属楼宇
        </template>
        <span class="contant" title="a座">a座</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属楼层
        </template>
        <span class="contant" title="二层">二层</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          所属区域
        </template>
        <span class="contant" title="A区域得西北角">A区域得西北角</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          安装位置
        </template>
        <span class="contant" title="二层">二层</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          安装时期
        </template>
        <span class="contant" title="第一个十七十七">第一个十七十七</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          使用日期
        </template>
        <span class="contant" title=" 2020-09-09"> 2020-09-09</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备管理员
        </template>
        <span class="contant" title="张三">张三</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          管理员电话
        </template>
        <span class="contant" title="15999999999">15999999999</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          供应商名称
        </template>
        <span class="contant" title="B供应商">B供应商</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备型号
        </template>
        <span class="contant" title="ABCDEFG">ABCDEFG</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          维保公司
        </template>
        <span class="contant" title="C维保公司">C维保公司</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          年检日期
        </template>
        <span class="contant" title=" 2020-09-09"> 2020-09-09</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          采购日期
        </template>
        <span class="contant" title=" 2020-09-09"> 2020-09-09</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          过保日期
        </template>
        <span class="contant" title=" 2020-09-09"> 2020-09-09</span>
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          设备描述
        </template>
        <span class="contant" title="江苏省苏州市吴中区吴中大道 1188 号">
          江苏省苏州市吴中区吴中大道 1188 号
          江苏省苏州市吴中区吴中大道 1188 号
          江苏省苏州市吴中区吴中大道 1188 号
        </span>
      </el-descriptions-item>
    </el-descriptions>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { doEdit } from '@/api/userManagement'
import CjzdTable from './cjzdTable'

export default {
  name: 'UserManagementEdit',
  components: { CjzdTable },
  data() {
    return {
      disabled: false,
      title: '',
      dialogFormVisible: false,
    }
  },
  created() {},
  methods: {
    showEdit(row) {
      this.title = row.id
      this.dialogFormVisible = true
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