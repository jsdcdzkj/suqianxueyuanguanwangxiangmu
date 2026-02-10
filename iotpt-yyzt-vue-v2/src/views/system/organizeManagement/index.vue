<template>
  <div v-loading="loading">
    <el-row :gutter="15">
      <!-- 左侧区域 -->
      <el-col :lg="5" :xl="4">
        <el-card class="box-card" shadow="never" style="margin-bottom: 0">
          <div slot="header" class="clearfix">
            <span class="small-title">单位信息</span>
            <el-button
              style="float: right; margin-top: -3px"
              size="mini"
              type="success"
              icon="el-icon-plus"
              @click="plus2"
            >
              新增
            </el-button>
          </div>
          <div class="block">
            <el-tree
              :data="data"
              :props="treeProps"
              node-key="id"
              default-expand-all
              :current-node-key="4"
              :highlight-current="true"
              @node-click="nodeClick"
              :expand-on-click-node="false"
            >
              <span class="custom-tree-node" slot-scope="{ node, data }">
                <div class="left-des-con">
                  <!-- 第一级固定一个图标 -->
                  <i class="icon" v-if="node.level === 1">
                    <svg
                      v-show="!node.isCurrent"
                      t="1689305765399"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="17458"
                      width="14"
                      height="14"
                    >
                      <path
                        d="M931.781 870.036h-27.708V580.229c0-15.531-8.386-36.494-28.219-44.26L624.146 382.442V141.963l-2.771-5.396C610 110.39 585.062 97.228 554.292 102.442l-394.771 109.63c-22.458 7.766-39.083 28.693-39.083 52.172v605.792H92.219C75.083 870.036 64 883.198 64 896.359c0 16.005 11.375 26.323 28.219 26.323h839.563c17.136 0 28.219-10.609 28.219-26.323-0.147-16.005-11.522-26.323-28.22-26.323z m-84-292.651v292.651H624.146V442.234l223.635 135.151z m-672.073-313.14l391.635-109.594v715.385H175.708V264.245z m0 0"
                        fill="#222222"
                        p-id="17459"
                      ></path>
                      <path
                        d="M455.635 345.146H287.927c-17.136 0-28.219 10.317-28.219 26.359 0 16.005 11.375 26.323 28.219 26.323h167.708c17.136 0 28.219-10.318 28.219-26.323 0-16.188-11.521-26.359-28.219-26.359z m0 156.26H287.927c-17.136 0-28.219 10.609-28.219 26.322v0.328c0 16.006 11.375 26.323 28.219 26.323h167.708c17.136 0 28.219-10.646 28.219-26.323v-0.328c0-16.004-11.521-26.322-28.219-26.322z m0 156.88H287.927c-17.136 0-28.219 10.281-28.219 26.323v0.292c0 16.041 11.375 26.359 28.219 26.359h167.708c17.136 0 28.219-10.318 28.219-26.359v-0.292c0-16.042-11.521-26.323-28.219-26.323z m223.928 26.615c0 16.041 11.375 26.359 28.292 26.359h55.854c17.209 0 28.292-10.318 28.292-26.359v-0.292c0-16.042-11.375-26.323-28.292-26.323h-55.854c-17.209 0-28.292 10.281-28.292 26.323v0.292z m0 0"
                        fill="#222222"
                        p-id="17460"
                      ></path>
                    </svg>
                    <svg
                      v-show="node.isCurrent"
                      t="1689306005892"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="17573"
                      id="mx_n_1689306005896"
                      width="14"
                      height="14"
                    >
                      <path
                        d="M931.781 870.036h-27.708V580.229c0-15.531-8.386-36.494-28.219-44.26L624.146 382.442V141.963l-2.771-5.396C610 110.39 585.062 97.228 554.292 102.442l-394.771 109.63c-22.458 7.766-39.083 28.693-39.083 52.172v605.792H92.219C75.083 870.036 64 883.198 64 896.359c0 16.005 11.375 26.323 28.219 26.323h839.563c17.136 0 28.219-10.609 28.219-26.323-0.147-16.005-11.522-26.323-28.22-26.323z m-84-292.651v292.651H624.146V442.234l223.635 135.151z m-672.073-313.14l391.635-109.594v715.385H175.708V264.245z m0 0"
                        fill="#334c97"
                        p-id="17574"
                      ></path>
                      <path
                        d="M455.635 345.146H287.927c-17.136 0-28.219 10.317-28.219 26.359 0 16.005 11.375 26.323 28.219 26.323h167.708c17.136 0 28.219-10.318 28.219-26.323 0-16.188-11.521-26.359-28.219-26.359z m0 156.26H287.927c-17.136 0-28.219 10.609-28.219 26.322v0.328c0 16.006 11.375 26.323 28.219 26.323h167.708c17.136 0 28.219-10.646 28.219-26.323v-0.328c0-16.004-11.521-26.322-28.219-26.322z m0 156.88H287.927c-17.136 0-28.219 10.281-28.219 26.323v0.292c0 16.041 11.375 26.359 28.219 26.359h167.708c17.136 0 28.219-10.318 28.219-26.359v-0.292c0-16.042-11.521-26.323-28.219-26.323z m223.928 26.615c0 16.041 11.375 26.359 28.292 26.359h55.854c17.209 0 28.292-10.318 28.292-26.359v-0.292c0-16.042-11.375-26.323-28.292-26.323h-55.854c-17.209 0-28.292 10.281-28.292 26.323v0.292z m0 0"
                        fill="#334c97"
                        p-id="17575"
                      ></path>
                    </svg>
                  </i>
                  <!-- 非第一级且有子元素是个文件夹图标 -->
                  <i class="icon" v-else>
                    <svg
                      v-show="!node.isCurrent"
                      t="1689227089690"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="6393"
                      width="14"
                      height="14"
                    >
                      <path
                        d="M896 912H128a16 16 0 0 1 0-32h768a16 16 0 0 1 0 32z"
                        p-id="6394"
                        fill="#333333"
                      ></path>
                      <path
                        d="M756 912H268a28 28 0 0 1-28-28V140a28 28 0 0 1 28-28h488a28 28 0 0 1 28 28v744a28 28 0 0 1-28 28z m-484-32h480V144H272z"
                        p-id="6395"
                        fill="#333333"
                      ></path>
                      <path
                        d="M452 400H348a28 28 0 0 1-28-28V268a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                        p-id="6396"
                        fill="#333333"
                      ></path>
                      <path
                        d="M676 400H572a28 28 0 0 1-28-28V268a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96zM452 592H348a28 28 0 0 1-28-28V460a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                        fill="#333333"
                        p-id="6397"
                      ></path>
                      <path
                        d="M676 592H572a28 28 0 0 1-28-28V460a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96zM452 784H348a28 28 0 0 1-28-28V652a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                        p-id="6398"
                        fill="#333333"
                      ></path>
                      <path
                        d="M676 784H572a28 28 0 0 1-28-28V652a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                        fill="#333333"
                        p-id="6399"
                      ></path>
                    </svg>
                    <svg
                      v-show="node.isCurrent"
                      t="1689227089690"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="6393"
                      width="14"
                      height="14"
                    >
                      <path
                        d="M896 912H128a16 16 0 0 1 0-32h768a16 16 0 0 1 0 32z"
                        p-id="6394"
                        fill="#334c97"
                      ></path>
                      <path
                        d="M756 912H268a28 28 0 0 1-28-28V140a28 28 0 0 1 28-28h488a28 28 0 0 1 28 28v744a28 28 0 0 1-28 28z m-484-32h480V144H272z"
                        p-id="6395"
                        fill="#334c97"
                      ></path>
                      <path
                        d="M452 400H348a28 28 0 0 1-28-28V268a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                        p-id="6396"
                        fill="#334c97"
                      ></path>
                      <path
                        d="M676 400H572a28 28 0 0 1-28-28V268a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96zM452 592H348a28 28 0 0 1-28-28V460a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                        fill="#334c97"
                        p-id="6397"
                      ></path>
                      <path
                        d="M676 592H572a28 28 0 0 1-28-28V460a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96zM452 784H348a28 28 0 0 1-28-28V652a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                        p-id="6398"
                        fill="#334c97"
                      ></path>
                      <path
                        d="M676 784H572a28 28 0 0 1-28-28V652a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                        fill="#334c97"
                        p-id="6399"
                      ></path>
                    </svg>
                  </i>

                  <span
                    :class="[
                      node.childNodes.length ? 'bold' : '',
                      node.isCurrent ? 'blue' : '',
                    ]"
                  >
                    {{ node.label }}
                  </span>
                </div>
                <span class="right-edit-con" v-if="node.data.type === '0'">
                  <el-link
                    type="success"
                    :underline="false"
                    icon="el-icon-plus"
                    @click.stop="() => plus(node, data)"
                  ></el-link>
                  <el-link
                    type="primary"
                    :underline="false"
                    icon="el-icon-edit"
                    @click.stop="() => editTree(data)"
                  ></el-link>
                  <el-link
                    type="danger"
                    :underline="false"
                    icon="el-icon-delete"
                    @click.stop="() => remove(node, data)"
                  ></el-link>
                </span>
                <span class="right-edit-con" v-else>
                  <el-link
                    type="success"
                    :underline="false"
                    icon="el-icon-plus"
                    @click.stop="() => plus(node, data)"
                  ></el-link>
                  <el-link
                    type="primary"
                    :underline="false"
                    icon="el-icon-edit"
                    @click.stop="() => editTree(data)"
                  ></el-link>
                  <el-link
                    type="danger"
                    :underline="false"
                    icon="el-icon-delete"
                    @click.stop="() => remove(node, data)"
                  ></el-link>
                </span>
              </span>
            </el-tree>
          </div>
        </el-card>
      </el-col>
      <!-- 右侧区域 -->
      <el-col :lg="19" :xl="20">
        <el-card
          class="box-card right-box-card"
          shadow="never"
          style="margin-bottom: 0"
        >
          <el-tabs v-model="activeName">
            <!-- 区域信息添加 -->
            <el-tab-pane name="first">
              <span slot="label">
                <i class="el-icon-collection"></i>
                部门信息
              </span>
              <vab-query-form>
                <vab-query-form-left-panel :span="20">
                  <el-form
                    :inline="true"
                    :model="queryForm"
                    @submit.native.prevent
                  >
                    <el-form-item>
                      <el-cascader
                        :options="options"
                        style="width: 100%"
                        :show-all-levels="false"
                        placeholder="请选择楼层区域"
                        collapse-tags
                        ref="cascaderArr"
                        v-model="as"
                        @change="choseTheme"
                        clearable
                      ></el-cascader>
                    </el-form-item>
                    <el-form-item>
                      <el-input
                        v-model.trim="queryForm.deptName"
                        placeholder="部门名称"
                        clearable
                        @keyup.enter.native="queryData"
                      />
                    </el-form-item>
                    <el-form-item>
                      <el-button
                        icon="el-icon-search"
                        type="primary"
                        @click="queryData"
                      >
                        查询
                      </el-button>
                      <el-button icon="el-icon-refresh" @click="reseat3">
                        重置
                      </el-button>
                    </el-form-item>
                  </el-form>
                </vab-query-form-left-panel>
                <vab-query-form-right-panel :span="4">
                  <div style="display: flex">
                    <el-button
                      type="primary"
                      @click="handlerAsyncData2"
                      :loading="asyncDataing"
                    >
                      同步大华部门
                    </el-button>
                    <el-button
                      type="primary"
                      @click="handlerAsyncData"
                      :loading="asyncDataing"
                    >
                      同步大华访客区域
                    </el-button>
                    <el-button
                      icon="el-icon-plus"
                      type="success"
                      plain
                      @click="handleEdit"
                    >
                      添加
                    </el-button>
                  </div>
                  <!-- <el-button icon="el-icon-edit" type="success" plain @click="handleEdit">修改</el-button>
                                    <el-button icon="el-icon-delete" type="danger" plain @click="handleDelete">删除</el-button> -->
                  <!-- <el-button icon="el-icon-search" circle @click="ShowSearch()"></el-button>
                                    <el-button icon="el-icon-refresh" circle @click="refreshRoute()"></el-button> -->
                </vab-query-form-right-panel>
              </vab-query-form>

              <el-table
                v-loading="listLoading"
                :data="list"
                :element-loading-text="elementLoadingText"
                height="calc(100vh - 296px)"
                border
              >
                <el-table-column
                  show-overflow-tooltip
                  prop="deptName"
                  label="部门名称"
                  width="300"
                  align="center"
                ></el-table-column>
                <el-table-column
                  show-overflow-tooltip
                  prop="orgName"
                  label="包含区域"
                  minwidth="120"
                  align="center"
                ></el-table-column>
                <el-table-column
                  show-overflow-tooltip
                  prop="memo"
                  label="备注"
                  minwidth="120"
                  align="center"
                ></el-table-column>
                <el-table-column label="操作" width="160" align="center">
                  <template #default="{ row }">
                    <!-- <el-link type="success" :underline="false" @click="handleEdit(row)">编辑</el-link>
                                        <el-link type="danger" :underline="false" @click="handleDelete(row)">删除</el-link>
                                        -->

                    <el-button
                      icon="el-icon-edit"
                      type="success"
                      size="mini"
                      @click="handleEdit(row)"
                    >
                      编辑
                    </el-button>
                    <el-button
                      icon="el-icon-delete"
                      type="danger"
                      size="mini"
                      @click="handleDelete(row)"
                    >
                      删除
                    </el-button>
                    <!-- <el-button type="text" @click="handleView(row)">查看</el-button> -->
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination
                background
                :current-page="queryForm.pageNo"
                :page-size="queryForm.pageSize"
                :layout="layout"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              ></el-pagination>
            </el-tab-pane>
            <el-tab-pane name="second">
              <span slot="label">
                <i style="vertical-align: middle" v-if="iconType == '0'">
                  <svg
                    v-show="activeName == 'first'"
                    t="1689305765399"
                    viewBox="0 0 1024 1024"
                    version="1.1"
                    xmlns="http://www.w3.org/2000/svg"
                    p-id="17458"
                    width="14"
                    height="14"
                  >
                    <path
                      d="M931.781 870.036h-27.708V580.229c0-15.531-8.386-36.494-28.219-44.26L624.146 382.442V141.963l-2.771-5.396C610 110.39 585.062 97.228 554.292 102.442l-394.771 109.63c-22.458 7.766-39.083 28.693-39.083 52.172v605.792H92.219C75.083 870.036 64 883.198 64 896.359c0 16.005 11.375 26.323 28.219 26.323h839.563c17.136 0 28.219-10.609 28.219-26.323-0.147-16.005-11.522-26.323-28.22-26.323z m-84-292.651v292.651H624.146V442.234l223.635 135.151z m-672.073-313.14l391.635-109.594v715.385H175.708V264.245z m0 0"
                      fill="#222222"
                      p-id="17459"
                    ></path>
                    <path
                      d="M455.635 345.146H287.927c-17.136 0-28.219 10.317-28.219 26.359 0 16.005 11.375 26.323 28.219 26.323h167.708c17.136 0 28.219-10.318 28.219-26.323 0-16.188-11.521-26.359-28.219-26.359z m0 156.26H287.927c-17.136 0-28.219 10.609-28.219 26.322v0.328c0 16.006 11.375 26.323 28.219 26.323h167.708c17.136 0 28.219-10.646 28.219-26.323v-0.328c0-16.004-11.521-26.322-28.219-26.322z m0 156.88H287.927c-17.136 0-28.219 10.281-28.219 26.323v0.292c0 16.041 11.375 26.359 28.219 26.359h167.708c17.136 0 28.219-10.318 28.219-26.359v-0.292c0-16.042-11.521-26.323-28.219-26.323z m223.928 26.615c0 16.041 11.375 26.359 28.292 26.359h55.854c17.209 0 28.292-10.318 28.292-26.359v-0.292c0-16.042-11.375-26.323-28.292-26.323h-55.854c-17.209 0-28.292 10.281-28.292 26.323v0.292z m0 0"
                      fill="#222222"
                      p-id="17460"
                    ></path>
                  </svg>
                  <svg
                    v-show="activeName == 'second'"
                    t="1689306005892"
                    viewBox="0 0 1024 1024"
                    version="1.1"
                    xmlns="http://www.w3.org/2000/svg"
                    p-id="17573"
                    id="mx_n_1689306005896"
                    width="14"
                    height="14"
                  >
                    <path
                      d="M931.781 870.036h-27.708V580.229c0-15.531-8.386-36.494-28.219-44.26L624.146 382.442V141.963l-2.771-5.396C610 110.39 585.062 97.228 554.292 102.442l-394.771 109.63c-22.458 7.766-39.083 28.693-39.083 52.172v605.792H92.219C75.083 870.036 64 883.198 64 896.359c0 16.005 11.375 26.323 28.219 26.323h839.563c17.136 0 28.219-10.609 28.219-26.323-0.147-16.005-11.522-26.323-28.22-26.323z m-84-292.651v292.651H624.146V442.234l223.635 135.151z m-672.073-313.14l391.635-109.594v715.385H175.708V264.245z m0 0"
                      fill="#334c97"
                      p-id="17574"
                    ></path>
                    <path
                      d="M455.635 345.146H287.927c-17.136 0-28.219 10.317-28.219 26.359 0 16.005 11.375 26.323 28.219 26.323h167.708c17.136 0 28.219-10.318 28.219-26.323 0-16.188-11.521-26.359-28.219-26.359z m0 156.26H287.927c-17.136 0-28.219 10.609-28.219 26.322v0.328c0 16.006 11.375 26.323 28.219 26.323h167.708c17.136 0 28.219-10.646 28.219-26.323v-0.328c0-16.004-11.521-26.322-28.219-26.322z m0 156.88H287.927c-17.136 0-28.219 10.281-28.219 26.323v0.292c0 16.041 11.375 26.359 28.219 26.359h167.708c17.136 0 28.219-10.318 28.219-26.359v-0.292c0-16.042-11.521-26.323-28.219-26.323z m223.928 26.615c0 16.041 11.375 26.359 28.292 26.359h55.854c17.209 0 28.292-10.318 28.292-26.359v-0.292c0-16.042-11.375-26.323-28.292-26.323h-55.854c-17.209 0-28.292 10.281-28.292 26.323v0.292z m0 0"
                      fill="#334c97"
                      p-id="17575"
                    ></path>
                  </svg>
                </i>
                <i style="vertical-align: middle" v-else>
                  <svg
                    v-show="activeName == 'first'"
                    t="1689227089690"
                    viewBox="0 0 1024 1024"
                    version="1.1"
                    xmlns="http://www.w3.org/2000/svg"
                    p-id="6393"
                    width="14"
                    height="14"
                  >
                    <path
                      d="M896 912H128a16 16 0 0 1 0-32h768a16 16 0 0 1 0 32z"
                      p-id="6394"
                      fill="#333333"
                    ></path>
                    <path
                      d="M756 912H268a28 28 0 0 1-28-28V140a28 28 0 0 1 28-28h488a28 28 0 0 1 28 28v744a28 28 0 0 1-28 28z m-484-32h480V144H272z"
                      p-id="6395"
                      fill="#333333"
                    ></path>
                    <path
                      d="M452 400H348a28 28 0 0 1-28-28V268a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                      p-id="6396"
                      fill="#333333"
                    ></path>
                    <path
                      d="M676 400H572a28 28 0 0 1-28-28V268a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96zM452 592H348a28 28 0 0 1-28-28V460a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                      fill="#333333"
                      p-id="6397"
                    ></path>
                    <path
                      d="M676 592H572a28 28 0 0 1-28-28V460a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96zM452 784H348a28 28 0 0 1-28-28V652a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                      p-id="6398"
                      fill="#333333"
                    ></path>
                    <path
                      d="M676 784H572a28 28 0 0 1-28-28V652a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                      fill="#333333"
                      p-id="6399"
                    ></path>
                  </svg>
                  <svg
                    v-show="activeName == 'second'"
                    t="1689227089690"
                    viewBox="0 0 1024 1024"
                    version="1.1"
                    xmlns="http://www.w3.org/2000/svg"
                    p-id="6393"
                    width="14"
                    height="14"
                  >
                    <path
                      d="M896 912H128a16 16 0 0 1 0-32h768a16 16 0 0 1 0 32z"
                      p-id="6394"
                      fill="#334c97"
                    ></path>
                    <path
                      d="M756 912H268a28 28 0 0 1-28-28V140a28 28 0 0 1 28-28h488a28 28 0 0 1 28 28v744a28 28 0 0 1-28 28z m-484-32h480V144H272z"
                      p-id="6395"
                      fill="#334c97"
                    ></path>
                    <path
                      d="M452 400H348a28 28 0 0 1-28-28V268a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                      p-id="6396"
                      fill="#334c97"
                    ></path>
                    <path
                      d="M676 400H572a28 28 0 0 1-28-28V268a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96zM452 592H348a28 28 0 0 1-28-28V460a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                      fill="#334c97"
                      p-id="6397"
                    ></path>
                    <path
                      d="M676 592H572a28 28 0 0 1-28-28V460a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96zM452 784H348a28 28 0 0 1-28-28V652a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                      p-id="6398"
                      fill="#334c97"
                    ></path>
                    <path
                      d="M676 784H572a28 28 0 0 1-28-28V652a28 28 0 0 1 28-28h104a28 28 0 0 1 28 28v104a28 28 0 0 1-28 28z m-100-32h96v-96h-96z"
                      fill="#334c97"
                      p-id="6399"
                    ></path>
                  </svg>
                </i>
                单位信息
              </span>
              <div class="tab-second-box">
                <el-form
                  ref="form"
                  :model="form"
                  :rules="rules"
                  label-width="88px"
                >
                  <el-form-item label="单位名称" prop="orgName">
                    <el-input
                      v-model.trim="form.orgName"
                      :disabled="msgStatus"
                      :placeholder="msgStatus ? '' : '请输入单位名称'"
                      autocomplete="off"
                      style="width: 400px"
                    ></el-input>
                  </el-form-item>
                  <el-form-item label="是否是租户" prop="isItATenant">
                    <el-radio-group
                      v-model="form.isItATenant"
                      :disabled="msgStatus"
                    >
                      <el-radio label="1">是</el-radio>
                      <el-radio label="0">否</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="地址" prop="address">
                    <el-input
                      v-model.trim="form.address"
                      :disabled="msgStatus"
                      :placeholder="msgStatus ? '' : '请输入地址'"
                      autocomplete="off"
                      style="width: 400px"
                    ></el-input>
                  </el-form-item>
                  <el-form-item label="联系人" prop="manager">
                    <el-input
                      v-model.trim="form.manager"
                      :disabled="msgStatus"
                      :placeholder="msgStatus ? '' : '请输入联系人'"
                      autocomplete="off"
                      style="width: 400px"
                    ></el-input>
                  </el-form-item>
                  <el-form-item label="电话" prop="phone">
                    <el-input
                      v-model.trim="form.phone"
                      :disabled="msgStatus"
                      :placeholder="msgStatus ? '' : '请输入电话'"
                      autocomplete="off"
                      style="width: 400px"
                    ></el-input>
                  </el-form-item>

                  <el-form-item label="备注信息">
                    <el-input
                      v-model.trim="form.memo"
                      :disabled="msgStatus"
                      type="textarea"
                      :placeholder="msgStatus ? '' : '请输入备注信息'"
                      autocomplete="off"
                      style="width: 400px"
                    ></el-input>
                  </el-form-item>
                  <div v-if="activeType == '0'" style="margin-left: 80px">
                    <el-button
                      type="primary"
                      style="margin-left: 10px"
                      @click="save"
                      :loading="loading"
                      v-show="!msgStatus"
                      :disabled="msgStatus"
                    >
                      {{ loading ? '保存中 ...' : '保 存' }}
                    </el-button>
                    <el-button
                      @click="cancelEdit"
                      :loading="loading"
                      :disabled="msgStatus"
                      v-show="!msgStatus"
                    >
                      {{ loading ? '取消中 ...' : '取 消' }}
                    </el-button>
                  </div>
                  <div v-else style="margin-left: 80px">
                    <el-button
                      type="primary"
                      style="margin-left: 10px"
                      @click="save"
                      :loading="loading"
                      :disabled="disabled"
                    >
                      {{ loading ? '确定中 ...' : '添 加' }}
                    </el-button>
                    <el-button @click="reseat">重 置</el-button>
                  </div>
                </el-form>
              </div>
            </el-tab-pane>
          </el-tabs>
          <!-- <div class="tips-title"><i class="el-icon-s-promotion"></i>单位信息</div> -->
        </el-card>
      </el-col>
    </el-row>
    <edit ref="edit" @fetch-data="fetchData"></edit>
  </div>
</template>

<script>
  import { doEdit } from '@/api/userManagement'
  import {
    addOrgManage,
    orgTreeList,
    info,
    updateOrgManage,
    delOrgManage,
    findOrg,
  } from '@/api/org.js'
  import {
    selectOrgDeptList,
    delOrgDept,
    visitorRegion,
    visitorRegion2,
  } from '@/api/dept.js'
  import { areaTreeList } from '@/api/org.js'
  import Edit from './components/edit'

  export default {
    name: 'department',
    components: { Edit },
    data() {
      const data = []
      return {
        treeProps: {
          label: 'orgName',
          value: 'id',
        },
        data: JSON.parse(JSON.stringify(data)),
        loading: false,
        disabled: false,
        form: {
          orgName: '',
          isItATenant: '0',
          address: '',
          manager: '',
          phone: '',
          memo: '',
          parentId: '',
          id: '',
        },
        activeType: '0',
        msgStatus: true,
        rules: {
          orgName: [
            { required: true, trigger: 'blur', message: '请输入单位名称' },
          ],
        },
        searchShow: false,
        list: null,
        listLoading: false,
        layout: 'total, sizes, prev, pager, next, jumper',
        total: 0,
        selectRows: '',
        as: '',
        elementLoadingText: '正在加载...',
        queryForm: {
          pageNo: 1,
          pageSize: 10,
          deptName: '',
          ids: '',
        },
        options: [],
        checkDatas: [],
        heightTable: 'calc(100vh - 522px)',
        activeName: 'first',
        iconType: '0',
        asyncDataing: false,
        asyncObj: {
          iccId: '',
          orgId: '',
        },
      }
    },
    created() {
      this.treeData()
      this.treeData2()
      setTimeout(() => {
        if (
          this.data != null &&
          this.data != undefined &&
          this.data != '' &&
          this.data.length > 0
        ) {
          this.form.id = this.data[0].id
          info(this.form.id).then((res) => {
            if (res.code == 0) {
              this.form.orgName = res.data.orgName
              this.form.isItATenant = res.data.isItATenant || '0'
              this.form.address = res.data.address
              this.form.manager = res.data.manager
              this.form.phone = res.data.phone
              this.form.memo = res.data.memo
              this.form.parentId = res.data.parentId
              this.form.id = res.data.id
              this.asyncObj.orgId = res.data.id
              this.asyncObj.iccId = res.data.iccId
            }
          })
        }
        this.queryData()
      }, 500)
    },
    methods: {
      handlerAsyncData() {
        this.asyncDataing = true

        visitorRegion(this.asyncObj).then((res) => {
          this.asyncDataing = false
          this.$message.success('同步成功')
        })
      },
      handlerAsyncData2() {
        this.asyncDataing = true
        if (!this.asyncObj.orgId && !this.asyncObj.iccId) {
          this.$message.success('请选择单位')
          return
        }
        visitorRegion2(this.asyncObj).then((res) => {
          this.asyncDataing = false
          this.$message.success('同步成功')
          // 刷新页面
          // this.$router.replace(this.$route.fullPath)
          this.fetchData()
        })
      },
      editTree(data) {
        this.$refs.form.clearValidate()
        this.activeType = '0'
        this.activeName = 'second'
        this.msgStatus = false

        info(data.id).then((res) => {
          if (res.code == 0) {
            this.form.orgName = res.data.orgName
            this.form.isItATenant = res.data.isItATenant || '0'
            this.form.address = res.data.address
            this.form.manager = res.data.manager
            this.form.phone = res.data.phone
            this.form.memo = res.data.memo
            this.form.parentId = res.data.parentId
            this.form.id = res.data.id
          }
        })
      },
      plus(node, data) {
        this.reseat()
        this.form.parentId = ''
        if (data != undefined) {
          this.form.parentId = data.id
        }
        this.activeType = '1'
        this.activeName = 'second'
        this.iconType = '1'
        this.msgStatus = false
      },
      plus2(node, data) {
        this.reseat()
        this.form.parentId = ''
        console.log(this.form.parentId)
        this.activeType = '1'
        this.activeName = 'second'
        this.iconType = '0'
        this.msgStatus = false
      },
      remove(node, data) {
        this.$baseConfirm(
          '删除操作会一并删除下级所有数据，确定删除吗？',
          '删除警告',
          async () => {
            delOrgManage(data.id).then((res) => {
              if (res.code == 0) {
                this.$baseMessage('删除成功！', 'success')
                this.treeData()
              }
            })
          }
        )
      },
      nodeClick(item, data) {
        this.queryForm.deptName = ''
        this.reseat3()
        this.heightTable = 'calc(100vh - 522px)'
        this.$refs.form.clearValidate()
        this.msgStatus = true
        this.activeType = '0'
        this.activeName = 'first'
        this.form.parentId = item.id

        this.asyncObj.orgId = item.id
        this.asyncObj.iccId = item.iccId
        if (item.parentId != 0) {
          this.iconType = '1'
        } else {
          this.iconType = '0'
        }
        info(item.id).then((res) => {
          if (res.code == 0) {
            this.form.orgName = res.data.orgName
            this.form.isItATenant = res.data.isItATenant || '0'
            this.form.address = res.data.address
            this.form.manager = res.data.manager
            this.form.phone = res.data.phone
            this.form.memo = res.data.memo
            this.form.parentId = res.data.parentId
            this.form.id = res.data.id
            this.queryData()
          }
        })
      },
      ShowSearch() {
        this.searchShow = !this.searchShow
      },
      refresh() {
        location.reload()
      },
      setSelectRows(val) {
        this.selectRows = val
      },
      handleEdit(row) {
        if (this.form.id) {
          if (row.id) {
            this.$refs['edit'].showEdit(this.form.id, row)
          } else {
            this.$refs['edit'].showEdit(this.form.id)
          }
        } else {
          this.$baseMessage('请先在左侧选中单位！', 'error')
        }
      },
      handleDelete(row) {
        if (row.id) {
          this.$baseConfirm('你确定要删除当前项吗', null, async () => {
            delOrgDept(row.id).then((res) => {
              if (res.code == 0) {
                this.$baseMessage('删除成功', 'success')
                this.queryData()
              } else {
                this.$baseMessage(res.msg, 'error')
              }
            })
          })
        } else {
          if (this.selectRows.length > 0) {
            const ids = this.selectRows.map((item) => item.id).join()
            this.$baseConfirm('你确定要删除选中项吗', null, async () => {
              const { msg, code } = await doDelete({ ids })
              if (code == 0) {
                this.$baseMessage(msg, 'success')
                this.fetchData()
              } else {
                this.$baseMessage(res.msg, 'error')
              }
            })
          } else {
            this.$baseMessage('未选中任何行', 'error')
            return false
          }
        }
      },
      cancelEdit() {
        var that = this
        that.msgStatus = true
        this.heightTable = 'calc(100vh - 522px)'
        info(that.form.id).then((res) => {
          if (res.code == 0) {
            this.form.orgName = res.data.orgName
            this.form.isItATenant = res.data.isItATenant || '0'
            this.form.address = res.data.address
            this.form.manager = res.data.manager
            this.form.phone = res.data.phone
            this.form.memo = res.data.memo
            this.form.parentId = res.data.parentId
            this.form.id = res.data.id
          }
        })
      },
      save() {
        if (this.loading) {
          return
        }
        this.$refs['form'].validate(async (valid) => {
          if (valid) {
            this.loading = true
            this.disabled = true
            //单位父级新增
            if ((this.activeType = '1')) {
              if (this.form.id == '') {
                addOrgManage(this.form).then((res) => {
                  if (res.code == 0) {
                    this.$baseMessage('操作成功', 'success')
                    this.treeData()
                    console.log(res)
                    // this.reseat() ;
                    this.form.id = res.data.id
                    this.msgStatus = true
                    this.activeType = '0'
                  }
                  this.queryData()
                  this.loading = false
                  this.disabled = false
                })
                this.loading = false
                this.disabled = false
              } else {
                updateOrgManage(this.form).then((res) => {
                  //修改
                  if (res.code == 0) {
                    this.treeData()
                    this.msgStatus = true
                    this.activeType = '0'
                  }
                  this.loading = false
                  this.disabled = false
                })
                this.loading = false
                this.disabled = false
              }
            }
          } else {
            return false
          }
        })
      },
      resetForm(formName) {
        this.$refs[formName].resetFields()
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
        if (this.form.id) {
          this.listLoading = true
          this.queryForm.orgId = this.form.id
          this.checkDatas = []
          const checkedNode = this.$refs['cascaderArr'].getCheckedNodes()
          //获取当前点击节点的label值
          if (checkedNode != null) {
            for (var i = 0; i < checkedNode.length; i++) {
              console.log(checkedNode[i].data.value)
              if (checkedNode[i].level == 3) {
                this.checkDatas.push(checkedNode[i].data.value)
              }
            }
          }
          this.queryForm.ids = this.checkDatas.toString()
          selectOrgDeptList(this.queryForm).then((res) => {
            if (res.code == 0) {
              this.list = res.data.records
              this.total = res.data.total
              this.listLoading = false
            }
          })
        }
      },
      async refreshRoute() {
        this.$baseEventBus.$emit('reload-router-view')
        this.pulse = true
        setTimeout(() => {
          this.pulse = false
        }, 1000)
      },
      treeData() {
        findOrg().then((res) => {
          if (res.code == 0) {
            this.data = res.data
          }
        })
      },
      reseat() {
        this.$refs['form'].resetFields()
        this.form.memo = ''
        this.form.id = ''
      },
      treeData2() {
        areaTreeList().then((res) => {
          if (res.code == 0) {
            this.options = res.data
          }
        })
      },
      choseTheme(subjectValue) {
        console.log(subjectValue)
        if (subjectValue.length == 0) {
          //clearable的点击×删除事件
          this.checkDatas = []
          this.queryData()
        } else {
          this.queryData()
        }
      },
      reseat3() {
        this.queryForm.deptName = ''

        this.as = ''
        this.checkDatas = []
        this.queryForm.ids = ''

        setTimeout(() => {
          this.queryData()
        }, 100)
      },
    },
  }
</script>
<style scoped lang="scss">
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 20px;
    max-width: 100%;
    box-sizing: border-box;
    &:hover {
      .right-edit-con {
        display: flex;
      }
    }
    .left-des-con {
      display: inline-block;
      width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    .right-edit-con {
      display: none;
      position: absolute;
      right: 0;
      top: 0;
      min-width: 40px;
      padding: 6px 10px;
      justify-content: flex-end;
      background-color: #f5f7fac4;
      .el-link {
        font-size: 14px;
      }
    }
    .icon {
      margin-right: 4px;
      vertical-align: middle;
    }
    .bold {
      font-weight: normal;
    }
    .blue {
      color: #334c97;
    }
  }

  .block {
    height: calc(100vh - 195px);
    overflow-y: auto;
  }
  .tips-title {
    font-size: 14px;
    margin-bottom: 14px;
    font-weight: bold;
    color: #334c97;

    i {
      margin-right: 4px;
    }
  }
  .small-title {
    margin: 0 0 14px;
    line-height: 16px;
    color: #334c97;
    font-weight: bold;
    &::before {
      content: '';
      display: inline-block;
      width: 4px;
      height: 22px;
      background-color: #334c97;
      margin-right: 14px;
      margin-top: -3px;
      margin-left: -20px;
      position: absolute;
      border-radius: 1px;
    }
  }
  ::v-deep {
    .el-tree-node__content {
      position: relative;
      overflow: hidden;
    }
    .right-box-card {
      .el-card__body {
        padding-top: 6px;
        .el-tabs__item {
          height: 46px;
        }
        .el-tabs__content {
          padding-top: 12px;
        }
      }
    }
    .el-textarea__inner {
      height: 84px !important;
    }
  }
  .tab-second-box {
    height: calc(100vh - 201px);
    overflow-y: auto;
  }
</style>
