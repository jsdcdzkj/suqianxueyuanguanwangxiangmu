<template>
 <el-dialog :close-on-click-modal="false" title="详情" :visible.sync="dialogFormVisible" width="808px" @close="close" top="10vh">
    <!--<div class="tips-title"><i class="el-icon-s-promotion"></i>{{ 'ID：'+form.id }}</div>-->
    <el-descriptions class="descriptions-self" :column="2" border label-class-name="my-label"
                     content-class-name="my-content">
      <el-descriptions-item>
        <template slot="label">
          模板名称
        </template>
        <span class="contant" :title="form.modelName">{{ form.modelName }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          模板编码
        </template>
        <span class="contant" :title="form.modelCode">{{ form.modelCode }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          模板类型
        </template>
        <span class="contant" :title="form.modelTypeName">{{ form.modelTypeName }}</span>
      </el-descriptions-item>
      <el-descriptions-item v-if="form.modelType == 2">
        <template slot="label">
          下发指令格式
        </template>
        <span class="contant" :title="form.polymerization">{{ form.polymerization }}</span>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          模板描述
        </template>
        <span class="contant" :title="form.modelDesc">{{ form.modelDesc }}</span>
      </el-descriptions-item>
    </el-descriptions>
    <div v-if="form.modelType == 3">
      <div class="tips-title" style=" margin-top: 30px;"><i class="el-icon-s-promotion"></i>解析数据示例</div>
      <json-viewer :value="form.polymerization" :expand-depth=3 copyable boxed expanded></json-viewer>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="close">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {getEntity} from '@/api/configDataTransfer'

  export default {
    name: 'DataManagerView',
    data() {
      return {
        disabled: false,
        title: '',
        dialogFormVisible: false,
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
        form: {
          id: '',
          modelName: '',
          modelCode: '',
          polymerization: '',
          modelDesc: '',
        },
      }
    },
    created() {
    },
    methods: {
      showEdit(row) {
        // this.title = row.id
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
