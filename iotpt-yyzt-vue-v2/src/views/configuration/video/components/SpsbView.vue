<template>
  <el-dialog title="查看" :visible.sync="dialogFormVisible" append-to-body modal-append-to-body width="808px" @close="close" top="10vh">
    <div class="tips-title"><i class="el-icon-s-promotion"></i>{{ '设备名称：'+title }}</div>
    <el-descriptions class="descriptions-self" :column="2" border label-class-name="my-label" content-class-name="my-content">
      <el-descriptions-item >
        <template slot="label">
          设备名称
        </template>
        <span class="contant" title="上边">上边</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备编码
        </template>
        <span class="contant" title="18100000000181000000001810000000018100000000">18100000000181000000001810000000018100000000</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          设备编码
        </template>
        <span class="contant" title="123123123">123123123</span>
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
          供应商
        </template>
        <span class="contant" title="供应商">供应商</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          品牌
        </template>
        <span class="contant" title="xx品牌">xx品牌</span>
      </el-descriptions-item> 
      <el-descriptions-item>
        <template slot="label">
          设备型号
        </template>
        <span class="contant" title="xx型号">xx型号</span>
      </el-descriptions-item> 
      <el-descriptions-item>
        <template slot="label">
          出厂编号
        </template>
        <span class="contant" title="1010102">1010102</span>
      </el-descriptions-item> 
      <el-descriptions-item>
        <template slot="label">
          传输协议
        </template>
        <span class="contant" title="MQTT">MQTT</span>
      </el-descriptions-item> 
      <el-descriptions-item>
        <template slot="label">
          服务连接
        </template>
        <span class="contant" title="MQTT1">MQTT1</span>
      </el-descriptions-item> 
      <el-descriptions-item>
        <template slot="label">
          发布主题
        </template>
        <span class="contant" title="主题">主题</span>
      </el-descriptions-item> 

      <el-descriptions-item>
        <template slot="label">
          订阅主题
        </template>
        <span class="contant" title="主题">主题</span>
      </el-descriptions-item> 

      <el-descriptions-item>
        <template slot="label">
          转换模板
        </template>
        <span class="contant" title="模板1">模板1</span>
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

export default {
  name: 'WgsbView',
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