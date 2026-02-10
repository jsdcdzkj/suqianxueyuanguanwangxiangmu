<template>
  <div v-loading="loading">
    <el-row :gutter="15">
      <!-- 左侧区域 -->
      <el-col :lg="5" :xl="4">
        <el-card class="box-card" shadow="never" style="margin-bottom: 0">
          <div slot="header" class="clearfix">
            <span class="small-title">楼宇信息</span>
            <el-button
              style="float: right; margin-top: -3px"
              size="mini"
              type="success"
              icon="el-icon-plus"
              @click="buildingAdd"
            >
              新增
            </el-button>
          </div>
          <div class="block">
            <el-tree
              :data="data"
              node-key="id"
              default-expand-all
              :current-node-key="1"
              :highlight-current="true"
              @node-click="nodeClick"
              :expand-on-click-node="false"
            >
              <span class="custom-tree-node" slot-scope="{ node, data }">
                <div class="left-des-con">
                  <!-- 第一级固定一个图标 -->
                  <i
                    :class="[
                      'el-icon-office-building',
                      'icon',
                      node.isCurrent ? 'blue' : '',
                    ]"
                    v-if="node.level === 1"
                  ></i>
                  <!-- 非第一级且有子元素是个文件夹图标 -->
                  <i
                    :class="[
                      'el-icon-files',
                      'icon',
                      node.isCurrent ? 'blue' : '',
                    ]"
                    v-else
                  ></i>
                  <span
                    :class="[
                      node.childNodes.length ? 'bold' : '',
                      node.isCurrent ? 'blue' : '',
                    ]"
                  >
                    {{ node.label }}
                  </span>
                </div>

                <span class="right-edit-con">
                  <el-link
                    type="success"
                    v-if="node.data.type === '0'"
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
            <el-tab-pane name="second">
              <span slot="label">
                <i class="el-icon-map-location"></i>
                区域列表
              </span>
              <vab-query-form>
                <vab-query-form-left-panel :span="20">
                  <el-form
                    :inline="true"
                    :model="queryForm"
                    @submit.native.prevent
                  >
                    <el-form-item>
                      <el-input
                        v-model.trim="queryForm.areaName"
                        placeholder="请输入区域名称"
                        @input="tempInput"
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
                      <el-button icon="el-icon-refresh" @click="reseat2()">
                        重置
                      </el-button>
                    </el-form-item>
                  </el-form>
                </vab-query-form-left-panel>
                <vab-query-form-right-panel :span="4">
                  <el-button
                    icon="el-icon-plus"
                    type="success"
                    plain
                    @click="handleEdit"
                  >
                    添加
                  </el-button>
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
                  prop="areaName"
                  label="区域名称"
                  align="center"
                ></el-table-column>
                <el-table-column
                  show-overflow-tooltip
                  prop="areaType"
                  label="区域类型"
                  align="center"
                ></el-table-column>
                <el-table-column
                  show-overflow-tooltip
                  prop="buildName"
                  label="所属楼宇"
                  align="center"
                ></el-table-column>
                <el-table-column
                  show-overflow-tooltip
                  prop="sort"
                  label="区域排序"
                  width="100"
                  align="center"
                ></el-table-column>
                <el-table-column
                  show-overflow-tooltip
                  prop="floorName"
                  label="所属楼层"
                  width="200"
                  align="center"
                ></el-table-column>
                <el-table-column
                  show-overflow-tooltip
                  prop="memo"
                  label="区域描述"
                  minwidth="220"
                  align="center"
                ></el-table-column>
                <el-table-column
                  show-overflow-tooltip
                  prop="isLargeScreenDisplay"
                  label="是否对gis展示"
                  width="130"
                  align="center"
                >
                  <template #default="{ row }">
                    <!-- <el-tooltip :content="row.isLargeScreenDisplay == 0 ? '否':'是'" placement="top"> -->
                    <el-switch
                      v-model="row.isLargeScreenDisplay"
                      @change="gisSwitch(row)"
                      active-color="#13ce66"
                      :active-value="1"
                      :inactive-value="0"
                    ></el-switch>
                    <!-- </el-tooltip> -->
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="160" align="center">
                  <template #default="{ row }">
                    <!-- <el-link type="success" :underline="false" @click="handleEdit(row)">编辑</el-link> -->
                    <el-button
                      icon="el-icon-edit"
                      type="success"
                      size="mini"
                      @click="handleEdit(row)"
                    >
                      编辑
                    </el-button>

                    <!-- <el-link type="danger" :underline="false" @click="handleDelete(row)">删除</el-link> -->
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
            <!-- 楼宇、楼层添加信息 -->
            <el-tab-pane :label="firstName" name="first">
              <span slot="label">
                <i v-if="activeType == '0'" class="el-icon-office-building"></i>
                <i v-else class="el-icon-files"></i>
                {{ firstName }}
              </span>
              <div class="tab-second-box">
                <!-- <div v-show="activeType == '0'">
                  <el-form ref="form" :model="form" :rules="rules" label-width="80px">
                  <el-form-item label="楼宇名称" prop="buildName">
                    <el-input v-model.trim="form.buildName" :disabled="msgStatus" placeholder="请输入楼宇名称" autocomplete="off" style="width:400px;"></el-input>
                  </el-form-item>
                  <el-form-item label="楼宇位置" prop="place">
                    <el-input v-model.trim="form.place" :disabled="msgStatus" placeholder="请输入楼宇位置" autocomplete="off" style="width:400px;"></el-input>
                  </el-form-item>
                  <el-form-item label="楼宇图片">
                    <el-upload
                      action=""
                      list-type="picture-card"
                      :auto-upload = "false"
                      :on-change = "loadFile"
                      :disabled = "buildFlag"
                      :file-list="fileList"
                      accept=".jpg,.jpeg,.png"
                      :class="{hide:hideUpload}"
                      :on-preview="handlePictureCardPreview"
                      :on-remove="handleRemove">
                      <i class="el-icon-plus"></i>
                    </el-upload>
                   <el-dialog  custom-class="pic-box" :visible.sync="dialogVisible" append-to-body>
                      <img width="100%" :src="dialogImageUrl" alt="">
                    </el-dialog>
                  </el-form-item>
                  <el-form-item label="楼宇描述">
                    <el-input v-model.trim="form.memo" :disabled="msgStatus" type="textarea" placeholder="请输入楼宇描述" rows="4" autocomplete="off" style="width:400px;"></el-input>
                  </el-form-item>
                  </el-form>
                </div> -->
                <div v-show="activeType == '0'">
                  <div class="build-title-box">
                    <div class="build-tit">楼宇信息</div>
                    <div class="build-btn-box">
                      <div v-if="newsStatus" style="margin-left: 80px">
                        <el-button
                          type="primary"
                          @click="save"
                          v-show="!msgStatus"
                          :disabled="disabled"
                        >
                          {{ loading ? '确定中 ...' : '确 定' }}
                        </el-button>
                        <el-button
                          @click="resetForm('form')"
                          v-show="!msgStatus"
                        >
                          重 置
                        </el-button>
                      </div>
                      <div v-else style="margin-left: 80px">
                        <el-button
                          type="primary"
                          @click="save"
                          :disabled="msgStatus"
                          v-show="!msgStatus"
                        >
                          {{ loading ? '保存中 ...' : '保 存' }}
                        </el-button>
                        <el-button
                          @click="cancelEdit"
                          :disabled="msgStatus"
                          v-show="!msgStatus"
                        >
                          {{ loading ? '取消中 ...' : '取 消' }}
                        </el-button>
                      </div>
                    </div>
                  </div>
                  <el-form
                    ref="form"
                    :model="form"
                    :rules="rules"
                    class="build-form"
                  >
                    <table>
                      <!-- 楼宇基本信息 -->
                      <tr>
                        <td rowspan="4" class="al-center">
                          <span class="floor-label">楼宇基本信息</span>
                        </td>
                        <td colspan="2">
                          <span class="floor-label">楼宇名称</span>
                        </td>
                        <td width="50%">
                          <el-form-item prop="buildName">
                            <el-input
                              v-model.trim="form.buildName"
                              :disabled="msgStatus"
                              :placeholder="msgStatus ? '' : '请输入楼宇名称'"
                            ></el-input>
                          </el-form-item>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">楼宇位置</span>
                        </td>
                        <td>
                          <el-form-item prop="place">
                            <el-input
                              v-model.trim="form.place"
                              :disabled="msgStatus"
                              :placeholder="msgStatus ? '' : '请输入楼宇位置'"
                            ></el-input>
                          </el-form-item>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">楼宇图片</span>
                        </td>
                        <td>
                          <div class="floor-label">
                            <el-upload
                              v-if="!msgStatus"
                              action=""
                              list-type="picture-card"
                              :auto-upload="false"
                              :on-change="loadFile"
                              :disabled="buildFlag"
                              :file-list="fileList"
                              accept=".jpg,.jpeg,.png"
                              :class="{ hide: hideUpload }"
                              :on-preview="handlePictureCardPreview"
                              :on-remove="handleRemove"
                            >
                              <i class="el-icon-plus"></i>
                            </el-upload>
                            <el-image
                              v-else-if="msgStatus && fileList.length > 0"
                              :src="fileList[0].url"
                            ></el-image>
                            <el-dialog
                              custom-class="pic-box"
                              :visible.sync="dialogVisible"
                              append-to-body
                            >
                              <img width="100%" :src="dialogImageUrl" alt="" />
                            </el-dialog>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">楼宇描述</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.memo"
                            :disabled="msgStatus"
                            type="textarea"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <!-- 项目基本信息 -->
                      <tr>
                        <td rowspan="15" class="al-center">
                          <span class="floor-label">项目基本信息</span>
                        </td>
                        <td colspan="2">
                          <span class="floor-label">项目名称</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.xxmc"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">建设单位</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.jsdw"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">项目地点</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.xmdz"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td rowspan="3">
                          <span class="floor-label">建筑规模</span>
                        </td>
                        <td>
                          <span class="floor-label">总建筑面积（m²）</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.zjzmj"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td><span class="floor-label">地上层数</span></td>
                        <td>
                          <el-input
                            v-model.trim="form.dscs"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td><span class="floor-label">地下层数</span></td>
                        <td>
                          <el-input
                            v-model.trim="form.dxcs"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">建设性质</span>
                        </td>
                        <td>
                          <el-select
                            v-model.trim="form.jsxz"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请选择'"
                            class="w100"
                          >
                            <el-option
                              v-for="item in optionsJsxz"
                              :key="item.value"
                              :label="item.label"
                              :value="item.value"
                            ></el-option>
                          </el-select>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">工程立项批准文号</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.gclxpzwh"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">工程立项日期</span>
                        </td>
                        <td>
                          <el-date-picker
                            style="width: 100%"
                            :disabled="msgStatus"
                            value-format="yyyy-MM-dd"
                            v-model="form.gclxrq"
                            align="right"
                            type="date"
                            :placeholder="msgStatus ? '' : '选择日期'"
                          ></el-date-picker>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">设计单位名称</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.sjdwmc"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">大厦物业</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.propertyManagement"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>

                      <tr>
                        <td colspan="2">
                          <span class="floor-label">物业费</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.propertyManagementFee"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">空调类型</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.airConditioner"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">负责人</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.personInCharge"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">负责人电话</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.phone"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <!-- 建筑设计参数 -->
                      <tr>
                        <td rowspan="6" class="al-center">
                          <span class="floor-label">建筑设计参数</span>
                        </td>
                        <td colspan="2">
                          <span class="floor-label">建筑用途</span>
                        </td>
                        <td>
                          <el-select
                            v-model.trim="form.jsyt"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请选择'"
                            clearable
                            class="w100"
                          >
                            <el-option
                              v-for="item in buildingType"
                              :key="item.id"
                              :label="item.dictLabel"
                              :value="item.dictValue"
                              :placeholder="msgStatus ? '' : '建筑用途'"
                            ></el-option>
                          </el-select>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">结构类型</span>
                        </td>
                        <td>
                          <el-select
                            v-model.trim="form.jglx"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请选择'"
                            clearable
                            class="w100"
                          >
                            <el-option
                              v-for="item in structureType"
                              :key="item.id"
                              :label="item.dictLabel"
                              :value="item.dictValue"
                              :placeholder="msgStatus ? '' : '建筑用途'"
                            ></el-option>
                          </el-select>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">建筑高度（m）</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.jsgd"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">层高</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.cg"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">建筑耐火等级</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.jznhdj"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">使用年限</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.synx"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <!-- 能源与环境信息 -->
                      <tr>
                        <td rowspan="8" class="al-center">
                          <span class="floor-label">能源与环境信息</span>
                        </td>
                        <td colspan="2">
                          <span class="floor-label">能源种类</span>
                        </td>
                        <td>
                          <el-select
                            v-model.trim="form.rlzljxhl"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请选择'"
                            class="w100"
                          >
                            <el-option
                              v-for="(item, indexR) in optionsRlzljxhl"
                              :key="indexR"
                              :label="item.label"
                              :value="item.value"
                            ></el-option>
                          </el-select>
                        </td>
                      </tr>
                      <tr>
                        <td rowspan="2">
                          <span class="floor-label">能源效率指标</span>
                        </td>
                        <td><span class="floor-label">节能标准</span></td>
                        <td>
                          <el-input
                            v-model.trim="form.nyxlzb"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td><span class="floor-label">绿色建筑星级</span></td>
                        <td>
                          <el-input
                            v-model.trim="form.lsjzxj"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td rowspan="4">
                          <span class="floor-label">
                            供热（冷）面积及其相关能耗
                          </span>
                        </td>
                        <td><span class="floor-label">供冷面积（m²）</span></td>
                        <td>
                          <el-input
                            v-model.trim="form.glmj"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td>
                          <span class="floor-label">供冷能耗（kwh）</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.glnh"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td><span class="floor-label">供热面积（m²）</span></td>
                        <td>
                          <el-input
                            v-model.trim="form.grmj"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td>
                          <span class="floor-label">供热能耗（kwh）</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.grnh"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">环保设施配置情况</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.hbsspzqk"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <!-- 其他技术经济指标 -->
                      <tr>
                        <td rowspan="10" class="al-center">
                          <span class="floor-label">其他技术经济指标</span>
                        </td>
                        <td colspan="2">
                          <span class="floor-label">容积率</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.rjl"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">绿地率</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.ldl"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">停车位配置数量</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.tccpzsl"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">投资总额</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.tzze"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>

                      <tr>
                        <td colspan="2">
                          <span class="floor-label">车位月租金</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.parkingSpaceFee"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">空调费</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.airConditionerFee"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">空调开放时间</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.airConditionerTime"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">电梯数量</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.theNumberOfElevators"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">网络类型</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.network"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">月租金范围</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.money"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                      <!-- 竣工验收信息 -->
                      <tr>
                        <td rowspan="4" class="al-center">
                          <span class="floor-label">竣工验收信息</span>
                        </td>
                        <td colspan="2">
                          <span class="floor-label">开工日期</span>
                        </td>
                        <td>
                          <el-date-picker
                            v-model="form.kgrq"
                            style="width: 100%"
                            :disabled="msgStatus"
                            value-format="yyyy-MM-dd"
                            align="right"
                            type="date"
                            :placeholder="msgStatus ? '' : '选择日期'"
                          ></el-date-picker>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">竣工日期</span>
                        </td>
                        <td>
                          <el-date-picker
                            v-model="form.jgrq"
                            style="width: 100%"
                            :disabled="msgStatus"
                            value-format="yyyy-MM-dd"
                            align="right"
                            type="date"
                            :placeholder="msgStatus ? '' : '选择日期'"
                          ></el-date-picker>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">验收日期</span>
                        </td>
                        <td>
                          <el-date-picker
                            v-model="form.ysri"
                            style="width: 100%"
                            :disabled="msgStatus"
                            value-format="yyyy-MM-dd"
                            align="right"
                            type="date"
                            :placeholder="msgStatus ? '' : '选择日期'"
                          ></el-date-picker>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <span class="floor-label">验收结论</span>
                        </td>
                        <td>
                          <el-input
                            v-model.trim="form.ysjl"
                            :disabled="msgStatus"
                            :placeholder="msgStatus ? '' : '请输入内容'"
                          ></el-input>
                        </td>
                      </tr>
                    </table>
                  </el-form>
                </div>
                <div v-show="activeType == '1'">
                  <el-form
                    ref="floorForm"
                    :model="floorForm"
                    :rules="rules2"
                    label-width="110px"
                  >
                    <el-form-item label="楼层名称" prop="floorName">
                      <el-input
                        v-model.trim="floorForm.floorName"
                        :disabled="msgStatus"
                        :placeholder="msgStatus ? '' : '请输入楼层名称'"
                        autocomplete="off"
                        style="width: 400px"
                      ></el-input>
                    </el-form-item>
                    <el-form-item label="楼层序号" prop="sort">
                      <el-input
                        v-model.number="floorForm.sort"
                        :disabled="msgStatus"
                        :placeholder="msgStatus ? '' : '请输入楼层序号'"
                        autocomplete="off"
                        style="width: 400px"
                      ></el-input>
                    </el-form-item>
                    <el-form-item
                      label="是否对gis展示"
                      prop="isLargeScreenDisplay"
                    >
                      <el-radio-group
                        v-model="floorForm.isLargeScreenDisplay"
                        :disabled="msgStatus"
                      >
                        <el-radio :label="1">是</el-radio>
                        <el-radio :label="0">否</el-radio>
                      </el-radio-group>
                    </el-form-item>
                    <el-form-item label="楼层图片">
                      <el-upload
                        action=""
                        list-type="picture-card"
                        :auto-upload="false"
                        :on-change="loadFile"
                        :disabled="buildFlag"
                        :file-list="fileList"
                        accept=".jpg,.jpeg,.png"
                        :class="{ hide: hideUpload }"
                        :on-preview="handlePictureCardPreview"
                        :on-remove="handleRemove"
                      >
                        <i class="el-icon-plus"></i>
                      </el-upload>
                      <el-dialog
                        custom-class="pic-box"
                        :visible.sync="dialogVisible"
                        append-to-body
                      >
                        <img width="100%" :src="dialogImageUrl" alt="" />
                      </el-dialog>
                    </el-form-item>
                    <el-form-item label="楼层描述">
                      <el-input
                        v-model.trim="floorForm.memo"
                        :disabled="msgStatus"
                        type="textarea"
                        :placeholder="msgStatus ? '' : '请输入楼层描述'"
                        rows="4"
                        autocomplete="off"
                        style="width: 400px"
                      ></el-input>
                    </el-form-item>
                  </el-form>
                </div>
                <div v-show="activeType == '1'">
                  <div v-if="newsStatus" style="margin-left: 80px">
                    <el-button
                      type="primary"
                      @click="save"
                      v-show="!msgStatus"
                      :disabled="disabled"
                    >
                      {{ loading ? '确定中 ...' : '确 定' }}
                    </el-button>
                    <el-button @click="resetForm('form')" v-show="!msgStatus">
                      重 置
                    </el-button>
                  </div>
                  <div v-else style="margin-left: 80px">
                    <el-button
                      type="primary"
                      @click="save"
                      :disabled="msgStatus"
                      v-show="!msgStatus"
                    >
                      {{ loading ? '保存中 ...' : '保 存' }}
                    </el-button>
                    <el-button
                      @click="cancelEdit"
                      :disabled="msgStatus"
                      v-show="!msgStatus"
                    >
                      {{ loading ? '取消中 ...' : '取 消' }}
                    </el-button>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <edit ref="edit" @fetch-data="fetchData"></edit>
  </div>
</template>

<script>
function formDataToJson(formData) {
  const jsonData = {}
  for (let [key, value] of formData.entries()) {
    jsonData[key] = value
  }
  console.log(jsonData)// 输出 JSON 数据
  return jsonData
}
  import { importData } from '@/api/configDeviceType'
  import { doEdit } from '@/api/userManagement'
  import {
    addBuild,
    bulidTreeList,
    info,
    updateBuild,
    delBuild,
  } from '@/api/build.js'
  import {
    info as floorInfo,
    addBuildFlool,
    updateBuildFlool,
    delBuildFloor,
  } from '@/api/floor.js'
  import Edit from './components/edit'
  import {
    allBuildFloolList,
    selectBuildAreaList,
    delBuildArea,
    updateBuildArea,
  } from '@/api/area'
  import network from '@/config/net.config'
  import { getRedisDictList } from '@/api/sysDict'
  import { baseURL } from '@/config'
  export default {
    name: 'dictionary',
    components: { Edit },
    data() {
      const data = []
      return {
        loading: false,

        delPic: false, //是否删除图片
        disabled: false,
        buildFlag: true,
        form: {
          buildName: '',
          place: '',
          memo: '',
          id: '',
          xxmc: '',
          jsdw: '',
          xmdz: '',
          zjzmj: '',
          dscs: '',
          dxcs: '',
          jsxz: '',
          gclxpzwhjrq: '',
          sjdwhsgdwmc: '',
          gclxpzwh: '',
          gclxrq: '',
          sjdwmc: '',
          sgdwmc: '',
          jsyt: '',
          jglx: '',
          jsgd: '',
          cg: '',
          jznhdj: '',
          synx: '',
          rlzljxhl: '',
          nyxlzb: '',
          lsjzxj: '',
          glmj: '',
          glnh: '',
          grmj: '',
          grnh: '',
          hbsspzqk: '',
          rjl: '',
          ldl: '',
          tccpzsl: '',
          tzze: '',
          kgrq: '',
          jgrq: '',
          ysri: '',
          ysjl: '',
          propertyManagement: '',
          propertyManagementFee: '',
          parkingSpaceFee: '',
          airConditioner: '',
          airConditionerFee: '',
          airConditionerTime: '',
          theNumberOfElevators: '',
          network: '',
          money: '',
          personInCharge: '',
          phone: '',
        },
        addData: {
          //区域新增页面传参数据
          buildId: '',
          floorId: '',
        },
        floorForm: {
          floorName: '',
          sort: '',
          memo: '',
          id: '',
          dictBuilding: '',
          isLargeScreenDisplay: 0,
        },
        fileList: [],
        activeName: 'second',
        firstName: '楼宇信息',
        activeType: '0',
        newsStatus: true,
        msgStatus: true,
        rules: {
          buildName: [{ required: true, trigger: 'submit', message: ' ' }],
          place: [{ required: true, trigger: 'submit', message: ' ' }],
        },
        rules2: {
          floorName: [
            { required: true, trigger: 'submit', message: '请输入楼层名称' },
          ],
          sort: [
            { required: true, trigger: 'submit', message: '请输入楼层序号' },
            {
              required: true,
              type: 'number',
              message: '楼层序号为数字',
              trigger: 'submit',
            },
          ],
          isLargeScreenDisplay: [
            { required: true, trigger: 'blur', message: '请选择是否对gis展示' },
          ],
        },
        dialogImageUrl: '',
        dialogVisible: false,
        list: null,
        listLoading: true,
        layout: 'total, sizes, prev, pager, next, jumper',
        total: 0,
        selectRows: '',
        elementLoadingText: '正在加载...',
        queryForm: {
          pageNo: 1,
          pageSize: 10,
          permission: '',
          username: '',
          desc: '',
          buildId: '',
          floorId: '',
          areaName: '',
        },
        options: [
          {
            value: '选项1',
            label: '服务类型1',
          },
          {
            value: '选项2',
            label: '服务类型2',
          },
          {
            value: '选项3',
            label: '服务类型3',
          },
          {
            value: '选项4',
            label: '服务类型4',
          },
          {
            value: '选项5',
            label: '服务类型5',
          },
        ],
        data: JSON.parse(JSON.stringify(data)),
        data2: [
          {
            id: 1,
            label: '发起工单',
            children: [
              {
                id: 4,
                label: '待派发（11）',
                a: 1,
              },
              {
                id: 4,
                label: '已派发（25）',
                a: 1,
              },
            ],
            a: 10,
          },
          {
            id: 2,
            label: '工单情况汇总',
            children: [
              { id: 5, label: '待完成（3）', a: 1 },
              { id: 6, label: '已完成（5）', a: 1 },
              { id: 7, label: '发起人确认（1）', a: 1 },
            ],
            a: 0,
          },
          {
            id: 3,
            label: '工单确认',
            children: [
              { id: 7, label: '待确认（3）', a: 1 },
              { id: 8, label: '已确认（3）', a: 1 },
            ],
            a: 0,
          },
          {
            id: 3,
            label: '相关查询',
            children: [
              { id: 9, label: '待完成（3）', a: 1 },
              { id: 10, label: '待确认（3）', a: 1 },
              { id: 11, label: '已确认（3）', a: 1 },
              {
                id: 12,
                label: '已归档（196）',
                a: 1,
                children: [
                  {
                    id: 13,
                    label: '2022',
                    a: 2,
                    children: [
                      { id: 142, label: '01', a: 3 },
                      { id: 142, label: '02', a: 3 },
                      { id: 142, label: '03', a: 3 },
                    ],
                  },
                ],
              },
            ],
            a: 0,
          },
        ],
        hideUpload: false,
        optionsJsxz: [
          {
            value: '0',
            label: '新建',
          },
          {
            value: '1',
            label: '改建',
            disabled: true,
          },
          {
            value: '2',
            label: '扩建',
          },
        ],
        optionsRlzljxhl: [
          {
            value: '0',
            label: '水能',
          },
          {
            value: '1',
            label: '电能',
            disabled: true,
          },
          {
            value: '2',
            label: '气能',
          },
          {
            value: '2',
            label: '太阳能',
          },
          {
            value: '2',
            label: '生物能',
          },
        ],
        buildingType: [],
        structureType: [],
        value: '100',
      }
    },
    created() {
      this.queryData()
      this.treeData()

      this.getBuildingType()
      this.getStructureType()
    },
    computed: {
      //该计算属性返回要监视的对象属性值
    },
    watch: {},
    mounted() {
      this.$nextTick(() => {
        setTimeout(() => {
          this.infoLabel()
        }, 1000)
      })
    },
    methods: {
      gisSwitch(row) {
        updateBuildArea(row).then((res) => {
          if (res.code == 0) {
            this.$baseMessage('操作成功', 'success')
            this.fetchData()
          }
        })
      },
      async getBuildingType() {
        const { data } = await getRedisDictList({ dictType: 'buildingType' })
        this.buildingType = data
      },
      async getStructureType() {
        const { data } = await getRedisDictList({ dictType: 'structureType' })
        this.structureType = data
      },
      tempInput() {
        console.log('queryForm.areaName', this.queryForm.areaName)
      },
      // 这个打印出来的是node.data
      handleNodeClick(data) {
        console.log(data)
      },
      // 这个打印出来的是node
      //  getNode(node) {
      //   console.log(node)
      // },
      buildingAdd() {
        this.firstName = '楼宇信息'
        this.activeType = '0'
        this.activeName = 'first'
        this.newsStatus = true
        this.msgStatus = false
        this.buildFlag = false
        this.hideUpload = false
        this.reseat()
      },

      cancelEdit() {
        var that = this
        that.msgStatus = true
        if (that.activeType == '0') {
          //楼宇
          info(that.form.id).then((res) => {
            if (res.code == 0) {
              that.buildFlag = false
              that.form = res.data.sysBuild
              var fileList = res.data.fileList
              that.fileList = []
              if (that.form.filename) {
                this.hideUpload = true
                // fileList.forEach(function (value, index, array){
                //   var file = {};
                //   file.name = value.fileName;
                //   // file.url = network.baseURL + "/sysFile/readFile?fileId="+value.id
                //   if (value.fileUrl.includes('/minio/previewFile?fileName=')) {
                //     file.url = baseURL+value.fileUrl
                //   } else {
                //     file.url = baseURL+'/minio/previewFile?fileName='+value.fileUrl
                //   }
                //   that.fileList.push(file)
                // })
                var file = {}
                file.name = that.form.filename
                file.url =
                  baseURL + '/minio/previewFile?fileName=' + that.form.filename
                file.originalFilename = that.form.originalFilename
                that.fileList.push(file)
              } else {
                this.hideUpload = false
              }
            }
          })
        } else if (that.activeType == '1') {
          //楼层
          floorInfo(that.floorForm.id).then((res) => {
            if (res.code == 0) {
              that.floorForm = res.data.data.sysBuildFloor
              console.log(that.floorForm)
              that.buildFlag = true
              var fileList = res.data.data.fileList
              that.fileList = []
              if (that.floorForm.filename) {
                this.hideUpload = true
                // fileList.forEach(function (value, index, array){
                //   var file = {};
                //   file.name = value.fileName;
                //   // file.url = network.baseURL + "/sysFile/readFile?fileId="+value.id
                //   if (value.fileUrl.includes('/minio/previewFile?fileName=')) {
                //     file.url = baseURL+value.fileUrl
                //   } else {
                //     file.url = baseURL+'/minio/previewFile?fileName='+value.fileUrl
                //   }
                //   that.fileList.push(file)
                // })
                var file = {}
                file.name = that.floorForm.filename
                file.url =
                  baseURL +
                  '/minio/previewFile?fileName=' +
                  that.floorForm.filename
                file.originalFilename = that.floorForm.originalFilename
                that.fileList.push(file)
              } else {
                this.hideUpload = false
              }
            }
          })
        }
      },
      editTree(data) {
        var that = this
        that.fileList = []
        this.activeName = 'first'
        this.newsStatus = false
        this.msgStatus = false
        this.delPic = false

        if (data.type === '0') {
          this.activeType = '0'
          this.firstName = '楼宇信息'

          info(data.id).then((res) => {
            if (res.code == 0) {
              that.buildFlag = false
              that.form = res.data.sysBuild
              var fileList = res.data.fileList

              if (that.form.filename) {
                this.hideUpload = true
                // fileList.forEach(function (value, index, array){
                //   var file = {};
                //   file.name = value.fileName;
                //   // file.url = network.baseURL + "/sysFile/readFile?fileId="+value.id
                //   if (value.fileUrl.includes('/minio/previewFile?fileName=')) {
                //     file.url = baseURL+value.fileUrl
                //   } else {
                //     file.url = baseURL+'/minio/previewFile?fileName='+value.fileUrl
                //   }
                //   that.fileList.push(file)
                // })
                var file = {}
                file.name = that.form.filename
                file.url =
                  baseURL + '/minio/previewFile?fileName=' + that.form.filename
                file.originalFilename = that.form.originalFilename
                that.fileList.push(file)
              } else {
                this.hideUpload = false
              }
            }
            console.log(that.fileList)
          })
        } else {
          this.activeType = '1'
          this.firstName = '楼层信息'

          floorInfo(data.id).then((res) => {
            if (res.code == 0) {
              this.floorForm = res.data.data.sysBuildFloor

              that.buildFlag = false
              var { filename = '', originalFilename = '' } =
                res.data.data.sysBuildFloor
              if (filename) {
                this.hideUpload = true
                var file = {}
                file.name = filename
                file.url = baseURL + '/minio/previewFile?fileName=' + filename
                file.originalFilename = originalFilename
                that.fileList.push(file)
              } else {
                this.hideUpload = false
              }
            }
          })
        }
      },
      plus(node, data) {
        var that = this
        that.activeName = 'first'
        that.activeType = '1'
        that.newsStatus = true
        that.msgStatus = false
        that.firstName = '楼层信息'
        that.floorForm.floorName = ''
        that.floorForm.sort = ''
        that.floorForm.memo = ''
        that.floorForm.id = ''
        that.floorForm.isLargeScreenDisplay = 0

        that.buildFlag = false
        this.hideUpload = false
        info(data.id).then((res) => {
          if (res.code == 0) {
            that.buildFlag = false
            that.form = res.data.sysBuild
            var fileList = res.data.fileList
            that.fileList = []
            // if(fileList && fileList.length > 0){
            //   fileList.forEach(function (value, index, array){
            //     var file = {};
            //     file.name = value.fileName;
            //     file.url = network.baseURL + "/sysFile/readFile?fileId="+value.id
            //     that.fileList.push(file)
            //   })
            // }
          }
        })
      },
      remove(node, data) {
        if (data.type === '0') {
          this.activeType = '0'
        } else {
          this.activeType = '1'
        }
        this.$baseConfirm(
          '删除操作会一并删除下级所有数据，确定删除吗？',
          '删除警告',
          async () => {
            if (this.activeType == '0') {
              //楼宇删除
              delBuild(data.id).then((res) => {
                if (res.code == 0) {
                  this.treeData()
                }
              })
            } else if (this.activeType == '1') {
              delBuildFloor(data.id).then((res) => {
                if (res.code == 0) {
                  this.treeData()
                }
              })
            }
          }
        )
      },
      nodeClick(item, data) {
        var that = this
        that.queryForm.buildId = ''
        that.queryForm.floorId = ''
        that.queryForm.areaName = ''
        this.$refs.form.clearValidate()
        this.$refs.floorForm.clearValidate()
        that.reseat()
        that.msgStatus = true
        that.activeName = 'second'
        that.newsStatus = false
        if (item.type === '0') {
          that.activeType = '0'
          that.firstName = '楼宇信息'
          that.queryForm.buildId = item.id
          info(item.id).then((res) => {
            if (res.code == 0) {
              that.buildFlag = true
              that.form = res.data.sysBuild
              var fileList = res.data.fileList
              that.fileList = []
              if (that.form.filename) {
                this.hideUpload = true
                // fileList.forEach(function (value, index, array){
                //   var file = {};
                //   file.name = value.fileName;
                //   // file.url = network.baseURL + "/sysFile/readFile?fileId="+value.id
                //   if (value.fileUrl.includes('/minio/previewFile?fileName=')) {
                //     file.url = baseURL+value.fileUrl
                //   } else {
                //     file.url = baseURL+'/minio/previewFile?fileName='+value.fileUrl
                //   }
                //   that.fileList.push(file)
                // })
                var file = {}
                file.name = that.form.filename
                file.url =
                  baseURL + '/minio/previewFile?fileName=' + that.form.filename
                file.originalFilename = that.form.originalFilename
                that.fileList.push(file)
              } else {
                this.hideUpload = false
              }
            }
          })
          that.queryData()
        } else {
          that.activeType = '1'
          that.firstName = '楼层信息'
          that.queryForm.floorId = item.id
          floorInfo(item.id).then((res) => {
            if (res.code == 0) {
              that.floorForm = res.data.data.sysBuildFloor
              console.log(that.floorForm)
              that.buildFlag = true
              var fileList = res.data.data.fileList
              that.fileList = []
              if (that.floorForm.filename) {
                this.hideUpload = true
                // fileList.forEach(function (value, index, array){
                //   var file = {};
                //   file.name = value.fileName;
                //   file.url = network.baseURL + "/sysFile/readFile?fileId="+value.id
                //   that.fileList.push(file)
                // })
                var file = {}
                file.name = that.floorForm.filename
                file.url =
                  baseURL +
                  '/minio/previewFile?fileName=' +
                  that.floorForm.filename
                file.originalFilename = that.floorForm.originalFilename
                that.fileList.push(file)
              } else {
                this.hideUpload = false
              }
            }
          })
          that.queryData()
        }
      },
      beforeUpload(file) {
        console.log('chufa')
        // const _name = file.name;
        // const _index = _name.lastIndexOf('.');
        // const _imgType = _name.substring(_index);
        // if(_imgType == '.jpg' || _imgType == '.jpeg' || _imgType == '.png' || _imgType == '.PNG') {
        //
        // }else{
        //     this.$baseMessage("图片格式仅支持.png、.jpg、.jpeg!", 'error')
        //     return false;
        // }
      },
      async loadFile(file, fileList) {
        const _name = file.name
        const _index = _name.lastIndexOf('.')
        const _imgType = _name.substring(_index)

        // 首先进行文件大小校验，限制为2MB
        const isLt2M = file.size / 1024 / 1024 < 2
        if (!isLt2M) {
          this.fileList = [] // 清空文件列表
          this.hideUpload = false // 显示上传按钮
          this.$baseMessage('上传图片大小不能超过 2MB!', 'error')
          return false
        }

        // 然后进行文件格式校验
        if (
          _imgType == '.jpg' ||
          _imgType == '.jpeg' ||
          _imgType == '.png' ||
          _imgType == '.PNG'
        ) {
          this.fileList = []
          let fd = new FormData()
          fd.append('file', file.raw)
          var result = await importData(fd)
          this.fileList.push({
            name: result.data.data.filename,
            url:
              baseURL +
              '/minio/previewFile?fileName=' +
              result.data.data.filename,
            originalFilename: result.data.data.originalFilename,
          })
          this.hideUpload = fileList.length >= 1
        } else {
          this.fileList = [] // 清空文件列表
          this.hideUpload = false // 显示上传按钮
          this.$baseMessage('图片格式仅支持.png、.jpg、.jpeg!', 'error')
          return false
        }
      },
      handleRemove(file, fileList) {
        this.delPic = true
        this.hideUpload = fileList.length >= 1
      },
      handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url
        this.dialogVisible = true
      },

      async handleEdit(row) {
        if (row.id) {
          this.$refs['edit'].showEdit(row, 'edit')
        } else {
          this.addData.buildId = ''
          this.addData.floorId = ''
          //新增带出楼宇,楼层
          if (
            this.queryForm.buildId != '' &&
            this.queryForm.buildId != null &&
            this.queryForm.buildId != undefined
          ) {
            this.addData.buildId = parseInt(this.queryForm.buildId)
          }

          if (
            this.queryForm.floorId != '' &&
            this.queryForm.floorId != undefined &&
            this.queryForm.floorId != null
          ) {
            //只选中楼宇
            this.addData.floorId = parseInt(this.queryForm.floorId)
            await floorInfo(this.queryForm.floorId).then((res) => {
              if (res.code == 0) {
                this.addData.buildId = res.data.data.sysBuildFloor.dictBuilding
              }
            })
          }

          this.$refs['edit'].showEdit(this.addData, 'add')
          console.log(this.addData)
        }
      },
      changeHidden(row) {
        if (row.hidden == 1) {
          row.hidden = 0
        } else {
          row.hidden = 1
        }
        this.$forceUpdate()
      },
      handleDelete(row) {
        if (row.id) {
          this.$baseConfirm('你确定要删除当前项吗', null, async () => {
            delBuildArea(row.id).then((res) => {
              if (res.code == 0) {
                this.$baseMessage('删除成功', 'success')
                this.queryData()
              }
            })
          })
        } else {
          if (this.selectRows.length > 0) {
            const ids = this.selectRows.map((item) => item.id).join()
            this.$baseConfirm('你确定要删除选中项吗', null, async () => {
              const { msg } = await doDelete({ ids })
              this.$baseMessage(msg, 'success')
              this.fetchData()
            })
          } else {
            this.$baseMessage('未选中任何行', 'error')
            return false
          }
        }
      },
      save() {
        var that = this
        if (that.loading) {
          return
        }
        let formData = new FormData()
        if (that.fileList.length > 0 && that.fileList[0].raw) {
          formData.append('file', that.fileList[0].raw)
        }
        formData.append('delPic', that.delPic)
        if (that.activeType == '0') {
          //楼宇新增
          that.$refs['form'].validate(async (valid) => {
            if (valid) {
              that.loading = true
              that.disabled = true
              if (that.form.memo == null || that.form.memo == undefined) {
                that.form.memo = ''
              }
              formData.append('buildName', that.form.buildName)
              formData.append('place', that.form.place)
              formData.append('memo', that.form.memo)
              formData.append('id', that.form.id)
              formData.append(
                'xxmc',
                that.form.xxmc == null && that.form.xxmc == undefined
                  ? ''
                  : that.form.xxmc
              )
              formData.append(
                'jsdw',
                that.form.jsdw == null && that.form.jsdw == undefined
                  ? ''
                  : that.form.jsdw
              )
              formData.append(
                'xmdz',
                that.form.xmdz == null && that.form.xmdz == undefined
                  ? ''
                  : that.form.xmdz
              )
              formData.append(
                'zjzmj',
                that.form.zjzmj == null && that.form.zjzmj == undefined
                  ? ''
                  : that.form.zjzmj
              )
              formData.append(
                'dscs',
                that.form.dscs == null && that.form.dscs == undefined
                  ? ''
                  : that.form.dscs
              )
              formData.append(
                'dxcs',
                that.form.dxcs == null && that.form.dxcs == undefined
                  ? ''
                  : that.form.dxcs
              )
              formData.append(
                'jsxz',
                that.form.jsxz == null && that.form.jsxz == undefined
                  ? ''
                  : that.form.jsxz
              )
              formData.append(
                'gclxpzwh',
                that.form.gclxpzwh == null && that.form.gclxpzwh == undefined
                  ? ''
                  : that.form.gclxpzwh
              )
              formData.append(
                'gclxrq',
                that.form.gclxrq == null && that.form.gclxrq == undefined
                  ? ''
                  : that.form.gclxrq
              )
              formData.append(
                'sjdwmc',
                that.form.sjdwmc == null && that.form.sjdwmc == undefined
                  ? ''
                  : that.form.sjdwmc
              )
              formData.append(
                'sgdwmc',
                that.form.sgdwmc == null && that.form.sgdwmc == undefined
                  ? ''
                  : that.form.sgdwmc
              )
              formData.append(
                'jsyt',
                that.form.jsyt == null && that.form.jsyt == undefined
                  ? ''
                  : that.form.jsyt
              )
              formData.append(
                'jglx',
                that.form.jglx == null && that.form.jglx == undefined
                  ? ''
                  : that.form.jglx
              )
              formData.append(
                'jsgd',
                that.form.jsgd == null && that.form.jsgd == undefined
                  ? ''
                  : that.form.jsgd
              )
              formData.append(
                'cg',
                that.form.cg == null && that.form.cg == undefined
                  ? ''
                  : that.form.cg
              )
              formData.append(
                'jznhdj',
                that.form.jznhdj == null && that.form.jznhdj == undefined
                  ? ''
                  : that.form.jznhdj
              )
              formData.append(
                'synx',
                that.form.synx == null && that.form.synx == undefined
                  ? ''
                  : that.form.synx
              )
              formData.append(
                'rlzljxhl',
                that.form.rlzljxhl == null && that.form.rlzljxhl == undefined
                  ? ''
                  : that.form.rlzljxhl
              )
              formData.append(
                'nyxlzb',
                that.form.nyxlzb == null && that.form.nyxlzb == undefined
                  ? ''
                  : that.form.nyxlzb
              )
              formData.append(
                'lsjzxj',
                that.form.lsjzxj == null && that.form.lsjzxj == undefined
                  ? ''
                  : that.form.lsjzxj
              )
              formData.append(
                'glmj',
                that.form.glmj == null && that.form.glmj == undefined
                  ? ''
                  : that.form.glmj
              )
              formData.append(
                'glnh',
                that.form.glnh == null && that.form.glnh == undefined
                  ? ''
                  : that.form.glnh
              )
              formData.append(
                'grmj',
                that.form.grmj == null && that.form.grmj == undefined
                  ? ''
                  : that.form.grmj
              )
              formData.append(
                'grnh',
                that.form.grnh == null && that.form.grnh == undefined
                  ? ''
                  : that.form.grnh
              )
              formData.append(
                'hbsspzqk',
                that.form.hbsspzqk == null && that.form.hbsspzqk == undefined
                  ? ''
                  : that.form.hbsspzqk
              )
              formData.append(
                'rjl',
                that.form.rjl == null && that.form.rjl == undefined
                  ? ''
                  : that.form.rjl
              )
              formData.append(
                'ldl',
                that.form.ldl == null && that.form.ldl == undefined
                  ? ''
                  : that.form.ldl
              )
              formData.append(
                'tccpzsl',
                that.form.tccpzsl == null && that.form.tccpzsl == undefined
                  ? ''
                  : that.form.tccpzsl
              )
              formData.append(
                'tzze',
                that.form.tzze == null && that.form.tzze == undefined
                  ? ''
                  : that.form.tzze
              )
              formData.append(
                'kgrq',
                that.form.kgrq == null && that.form.kgrq == undefined
                  ? ''
                  : that.form.kgrq
              )
              formData.append(
                'jgrq',
                that.form.jgrq == null && that.form.jgrq == undefined
                  ? ''
                  : that.form.jgrq
              )
              formData.append(
                'ysri',
                that.form.ysri == null && that.form.ysri == undefined
                  ? ''
                  : that.form.ysri
              )
              formData.append(
                'ysjl',
                that.form.ysjl == null && that.form.ysjl == undefined
                  ? ''
                  : that.form.ysjl
              )
              formData.append(
                'filename',
                that.fileList.length ? that.fileList[0].name : ''
              )
              formData.append(
                'originalFilename',
                that.fileList.length ? that.fileList[0].originalFilename : ''
              )

              formData.append(
                'propertyManagement',
                that.form.propertyManagement == null &&
                  that.form.propertyManagement == undefined
                  ? ''
                  : that.form.propertyManagement
              )
              formData.append(
                'propertyManagementFee',
                that.form.propertyManagementFee == null &&
                  that.form.propertyManagementFee == undefined
                  ? ''
                  : that.form.propertyManagementFee
              )
              formData.append(
                'parkingSpaceFee',
                that.form.parkingSpaceFee == null &&
                  that.form.parkingSpaceFee == undefined
                  ? ''
                  : that.form.parkingSpaceFee
              )
              formData.append(
                'airConditioner',
                that.form.airConditioner == null &&
                  that.form.airConditioner == undefined
                  ? ''
                  : that.form.airConditioner
              )
              formData.append(
                'airConditionerFee',
                that.form.airConditionerFee == null &&
                  that.form.airConditionerFee == undefined
                  ? ''
                  : that.form.airConditionerFee
              )
              formData.append(
                'airConditionerTime',
                that.form.airConditionerTime == null &&
                  that.form.airConditionerTime == undefined
                  ? ''
                  : that.form.airConditionerTime
              )
              formData.append(
                'theNumberOfElevators',
                that.form.theNumberOfElevators == null &&
                  that.form.theNumberOfElevators == undefined
                  ? ''
                  : that.form.theNumberOfElevators
              )
              formData.append(
                'network',
                that.form.network == null && that.form.network == undefined
                  ? ''
                  : that.form.network
              )
              formData.append(
                'money',
                that.form.money == null && that.form.money == undefined
                  ? ''
                  : that.form.money
              )
              formData.append(
                'personInCharge',
                that.form.personInCharge == null &&
                  that.form.personInCharge == undefined
                  ? ''
                  : that.form.personInCharge
              )
              formData.append(
                'phone',
                that.form.phone == null && that.form.phone == undefined
                  ? ''
                  : that.form.phone
              )
              if (that.form.id) {

                updateBuild(formDataToJson(formData)).then((res) => {
                  if (res.code == 0) {
                    that.$baseMessage('操作成功', 'success')
                    that.treeData()
                    that.msgStatus = true
                    that.newsStatus = false
                    // this.hideUpload = true ;
                    that.buildFlag = true
                  }
                  that.loading = false
                  that.disabled = false
                  this.hideUpload = false
                })
              } else {
                addBuild(formDataToJson(formData)).then((res) => {
                  if (res.code == 0) {
                    that.$baseMessage('操作成功', 'success')
                    that.treeData()
                    that.msgStatus = true
                    that.newsStatus = false
                    // this.hideUpload = true ;
                    that.buildFlag = true
                  }
                  that.loading = false
                  that.disabled = false
                  this.hideUpload = false
                })
              }
            } else {
              return false
            }
          })
        } else if (that.activeType == '1') {
          that.$refs['floorForm'].validate(async (valid) => {
            if (valid) {
              if (
                that.floorForm.memo == null ||
                that.floorForm.memo == undefined
              ) {
                that.floorForm.memo = ''
              }
              that.loading = true
              that.disabled = true
              formData.append('floorName', that.floorForm.floorName)
              formData.append('sort', that.floorForm.sort)
              formData.append('memo', that.floorForm.memo)
              formData.append('dictBuilding', that.form.id)
              formData.append('id', that.floorForm.id)
              formData.append(
                'isLargeScreenDisplay',
                that.floorForm.isLargeScreenDisplay
              )
              formData.append(
                'isLargeScreenDisplay',
                that.floorForm.isLargeScreenDisplay
              )
              formData.append(
                'isLargeScreenDisplay',
                that.floorForm.isLargeScreenDisplay
              )
              if (that.fileList.length > 0) {
                formData.append('filename', that.fileList[0].name)
                formData.append(
                  'originalFilename',
                  that.fileList[0].originalFilename
                )
              }

              if (that.floorForm.id) {
                updateBuildFlool(formData).then((res) => {
                  if (res.code == 0) {
                    that.$baseMessage('操作成功', 'success')
                    that.msgStatus = true
                    that.newsStatus = false
                    that.buildFlag = true
                    that.treeData()
                  }
                  that.loading = false
                  that.disabled = false
                })
              } else {
                that.floorForm.dictBuilding = that.form.id
                addBuildFlool(formData).then((res) => {
                  if (res.code == 0) {
                    that.$baseMessage('操作成功', 'success')
                    that.treeData()
                    that.msgStatus = true
                    that.newsStatus = false
                    that.buildFlag = true
                  }
                  that.loading = false
                  that.disabled = false
                })
              }
            } else {
              return false
            }
          })
        }
      },
      resetForm(formName) {
        // console.log(formName)
        // this.$refs[formName].resetFields();
        this.form.memo = ''
        this.form.buildName = ''
        this.form.place = ''
        this.form.xxmc = ''
        this.form.jsdw = ''
        this.form.xmdz = ''
        this.form.zjzmj = ''
        this.form.dscs = ''
        this.form.dxcs = ''
        this.form.jsxz = ''
        this.form.gclxpzwh = ''
        this.form.gclxrq = ''
        this.form.sjdwmc = ''
        this.form.sgdwmc = ''
        this.form.jsyt = ''
        this.form.jglx = ''
        this.form.jsgd = ''
        this.form.jznhdj = ''
        this.form.synx = ''
        this.form.rlzljxhl = ''
        this.form.nyxlzb = ''
        this.form.lsjzxj = ''
        this.form.glmj = ''
        this.form.glnh = ''
        this.form.grmj = ''
        this.form.grnh = ''
        this.form.hbsspzqk = ''
        this.form.rjl = ''
        this.form.ldl = ''
        this.form.tccpzsl = ''
        this.form.tzze = ''
        this.form.kgrq = ''
        this.form.jgrq = ''
        this.form.ysri = ''
        this.form.ysjl = ''
        this.form.propertyManagement = ''
        this.form.propertyManagementFee = ''
        this.form.parkingSpaceFee = ''
        this.form.airConditioner = ''
        this.form.airConditionerFee = ''
        this.form.airConditionerTime = ''
        this.form.theNumberOfElevators = ''
        this.form.network = ''
        this.form.money = ''
        this.form.personInCharge = ''
        this.form.phone = ''
        this.fileList = []
        this.floorForm.floorName = ''
        this.floorForm.isLargeScreenDisplay = 0
        this.floorForm.sort = ''
        this.floorForm.memo = ''
        this.hideUpload = false
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
        let that = this
        this.listLoading = true
        selectBuildAreaList(this.queryForm).then((res) => {
          if (res.code == 0) {
            this.list = res.data.records
            this.total = res.data.total
            this.listLoading = false
            console.log('this.data', res.data)
          }
        })

        // info(this.queryForm.buildId).then((res) => {
        //     if(res.code == 0){
        //       that.buildFlag = false;
        //       that.form = res.data.sysBuild ;
        //       var fileList = res.data.fileList;

        //       if(fileList && fileList.length > 0){
        //         this.hideUpload = true
        //         fileList.forEach(function (value, index, array){
        //           var file = {};
        //           file.name = value.fileName;
        //           file.url = network.baseURL + "/sysFile/readFile?fileId="+value.id
        //           that.fileList.push(file)
        //         })
        //       }else{
        //         this.hideUpload = false
        //       }
        //     }
        //     console.log(that.fileList)
        //   })
      },
      infoLabel() {
        let that = this
        this.treeData()
        info(that.data[0].id).then((res) => {
          if (res.code == 0) {
            that.buildFlag = true
            that.form = res.data.sysBuild
            // var fileList = res.data.fileList;
            that.fileList = []
            if (that.form.filename) {
              this.hideUpload = true
              // fileList.forEach(function (value, index, array){
              //   var file = {};
              //   file.name = value.fileName;
              //   file.url = network.baseURL + "/sysFile/readFile?fileId="+value.id
              //   if (value.fileUrl.includes('/minio/previewFile?fileName=')) {
              //     file.url = baseURL+value.fileUrl
              //   } else {
              //     file.url = baseURL+'/minio/previewFile?fileName='+value.fileUrl
              //   }
              //   that.fileList.push(file)
              // })
              var file = {}
              file.name = that.form.filename
              file.url =
                baseURL + '/minio/previewFile?fileName=' + that.form.filename
              file.originalFilename = that.form.originalFilename
              that.fileList.push(file)
            } else {
              this.hideUpload = false
            }
          }
        })
      },
      treeData() {
        bulidTreeList().then((res) => {
          if (res.code == 0) {
            this.data = res.data
          }
        })
      },
      reseat() {
        if (this.form != undefined && this.form != null) {
          this.form.place = ''
          this.form.buildName = ''
          this.form.id = ''
          this.form.memo = ''
          this.form.xxmc = ''
          this.form.jsdw = ''
          this.form.xmdz = ''
          this.form.zjzmj = ''
          this.form.dscs = ''
          this.form.dxcs = ''
          this.form.jsxz = ''
          this.form.gclxpzwh = ''
          this.form.gclxrq = ''
          this.form.sjdwmc = ''
          this.form.sgdwmc = ''
          this.form.jsyt = ''
          this.form.jglx = ''
          this.form.jsgd = ''
          this.form.jznhdj = ''
          this.form.synx = ''
          this.form.rlzljxhl = ''
          this.form.nyxlzb = ''
          this.form.lsjzxj = ''
          this.form.glmj = ''
          this.form.glnh = ''
          this.form.grmj = ''
          this.form.grnh = ''
          this.form.hbsspzqk = ''
          this.form.rjl = ''
          this.form.ldl = ''
          this.form.tccpzsl = ''
          this.form.tzze = ''
          this.form.kgrq = ''
          this.form.jgrq = ''
          this.form.ysri = ''
          this.form.ysjl = ''
        }

        this.floorForm.floorName = ''
        this.floorForm.isLargeScreenDisplay = 0
        this.floorForm.sort = ''
        this.floorForm.memo = ''
        this.fileList = []
      },
      reseat2() {
        this.queryForm.areaName = ''
        this.queryData()
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
  }

  .tab-second-box {
    height: calc(100vh - 201px);
    overflow-y: auto;
  }

  ::v-deep {
    .pic-box {
      .el-dialog__body {
        border-top: none;
      }
    }

    .el-upload-list__item-thumbnail {
      object-fit: fill;
    }

    .hide .el-upload--picture-card {
      display: none;
    }
  }

  table {
    width: 100%;
  }

  table,
  th,
  td {
    border: 1px solid #dcdfe6;
    border-collapse: collapse;
    /* 移除表格内边框间的间隙 */
  }

  td .floor-label {
    display: inline-block;
    width: 100%;
    box-sizing: border-box;
    padding: 6px 12px;

    ::v-deep .el-upload-list--picture-card .el-upload-list__item {
      width: 80px;
      height: 80px;
    }

    ::v-deep .el-upload--picture-card {
      width: 80px;
      height: 80px;
      line-height: 82px;
    }
  }

  td.al-center .floor-label {
    text-align: center;
    font-weight: 600;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
  }

  .build-title-box {
    width: 100%;
    display: flex;
    justify-content: space-between;
    background-color: #f3f3f3;
    height: 38px;
    line-height: 38px;

    .build-tit {
      font-weight: 600;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.85);

      &::before {
        content: '';
        display: inline-block;
        margin-left: 14px;
        margin-right: 7px;
        width: 8px;
        height: 8px;
        background: #355bad;
        border-radius: 50%;
      }
    }

    .build-btn-box {
      margin-right: 14px;
    }
  }

  .w100 {
    width: 100%;
  }

  .build-form {
    margin-bottom: 20px;

    .el-form-item--small.el-form-item {
      margin-bottom: 0;
    }

    ::v-deep {
      .el-input__inner,
      .el-textarea__inner {
        border-color: transparent;
      }

      .el-form-item.is-error .el-input__inner {
        border-color: #f34d37;
      }
    }
  }
</style>
