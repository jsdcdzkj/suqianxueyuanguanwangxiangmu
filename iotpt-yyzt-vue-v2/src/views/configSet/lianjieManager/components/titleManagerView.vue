<template>
 <el-dialog :close-on-click-modal="false" title="主题信息" :visible.sync="dialogFormVisible" width="808px" @close="close" top="10vh">
    <!--<div class="tips-title"><i class="el-icon-s-promotion"></i>{{ 'ID：'+form.id }}</div>-->

    <el-table v-loading="listLoading" :data="list" :element-loading-text="elementLoadingText"
              @selection-change="setSelectRows" height="800" border>
      <el-table-column show-overflow-tooltip prop="topicName" label="主题名称" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="topicType" label="主题类型" width="200" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.topicType === 1">订阅主题</span>
          <span v-else-if="scope.row.topicType === 2">发布主题</span>
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip prop="linkName" label="连接名称" width="200" align="center"></el-table-column>
      <el-table-column show-overflow-tooltip prop="topicDesc" label="主题描述"></el-table-column>
      <!--<el-table-column label="操作" width="200" align="center">-->
        <!--<template #default="{ row }">-->
          <!--<el-link :underline="false" type="success" @click="handleEdit(row)">编辑</el-link>-->
          <!--<el-link :underline="false" type="danger" @click="handleDelete(row)">删除</el-link>-->
          <!--<el-link :underline="false" type="primary" @click="handleView(row)">数据转换配置</el-link>-->
        <!--</template>-->
      <!--</el-table-column>-->
    </el-table>
    <el-pagination background :current-page="queryForm.pageNo" :page-size="queryForm.pageSize" :layout="layout"
                   :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>

    <div slot="footer" class="dialog-footer">
      <el-button @click="close">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  // 主题信息
  import {getPageList, saveOrUpdate} from '@/api/configTopic'

  export default {
    name: 'titleManagerView',
    data() {
      return {
        disabled: false,
        title: '',
        dialogFormVisible: false,
        searchShow: false,
        list: null,
        listLoading: true,
        layout: 'total, sizes, prev, pager, next, jumper',
        total: 0,
        selectRows: '',
        elementLoadingText: '正在加载...',
        queryForm: {
          pageNo: 1,
          pageSize: 10,
          linkId: '',
        },
      }
    },
    created() {
    },
    methods: {
      showEdit(row) {
        // this.title = row

        this.queryForm.linkId = row;
        this.fetchData();
        this.dialogFormVisible = true
      },
      setSelectRows(val) {
        this.selectRows = val
      },
      handleSizeChange(val) {
        this.queryForm.pageSize = val
        this.fetchData()
      },
      handleCurrentChange(val) {
        this.queryForm.pageNo = val
        this.fetchData()
      },
      queryData() {
        this.queryForm.pageNo = 1
        this.fetchData()
      },
      async fetchData() {
        this.listLoading = true
        const {data} = await getPageList(this.queryForm)
        this.list = data.records;
        this.total = data.total;
        setTimeout(() => {
          this.listLoading = false
        }, 300)
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
