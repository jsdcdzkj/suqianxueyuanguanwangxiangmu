<template>
  <div v-loading="loading">
    <el-row :gutter="15">
      <!-- 左侧区域 -->
      <el-col :lg="5" :xl="4">
        <el-card class="box-card" shadow="never" style="margin-bottom: 0">
          <el-input
            placeholder="输入关键字进行过滤"
            v-model="filterText"
          ></el-input>
          <div class="block">
            <el-tree
              class="filter-tree"
              :data="data"
              :props="defaultProps"
              default-expand-all
              :highlight-current="true"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              @node-click="handleNodeClick"
              ref="tree"
            ></el-tree>
          </div>
        </el-card>
      </el-col>
      <!-- 右侧区域 -->
      <el-col :lg="19" :xl="20">
        <el-card v-if="sceneList.length > 0" class="scen-box" shadow="never">
          <div
            v-for="(item, index) in sceneList"
            :key="item.id"
            class="scen-item"
            :class="{ active: activeIndex === `${index}` }"
            @click="handleSearch(index, item.id)"
          >
            <div class="item">
              <div class="title">
                <span>{{ item.sceneName }}</span>
                <i
                  class="el-icon-close close-icon"
                  @click.stop="handleClose(item.id)"
                ></i>
              </div>
              <span class="info">
                否认场景： {{ item.isCheck == 0 ? '否' : '是' }}
              </span>
              <hr />
              <span class="tip">
                {{ item.memo }}
              </span>
            </div>
          </div>
        </el-card>
        <el-card
          class="box-card type-wrap"
          shadow="never"
          style="margin-bottom: 0"
        >
          <div slot="header" class="clearfix">
            <el-button
              style="
                float: right;
                margin-left: 10px;
                background: #5462c0;
                color: #fff;
              "
              @click="handleAdd('add')"
            >
              新增场景
            </el-button>
            <el-button
              v-if="this.activeIndex.length > 0"
              style="float: right"
              @click="handleAdd"
            >
              更新场景
            </el-button>
          </div>
          <div
            :style="{
              height:
                sceneList.length > 0
                  ? 'calc(100vh - 470px)'
                  : 'calc(100vh - 232px)',
              overflowY: 'auto',
              overflowX: 'hidden',
            }"
          >
            <div
              v-for="(value, key, index) in deviceList"
              :key="index"
              class="scen-type"
            >
              <div class="type-name">
                <i></i>
                <span>{{ key }}</span>
              </div>
              <el-form
                ref="form"
                label-position="left"
                :model="deviceList"
                label-width="70px"
              >
                <el-row :gutter="20">
                  <el-col :span="6" v-for="item in value" :key="item.id">
                    <div class="card">
                      <div class="card-flex card-title">
                        <div>{{ item.name }}</div>
                        <el-form-item>
                          <div class="lock">
                            <i
                              @click="handleSwicth(item)"
                              :class="
                                !!(item.isLock - 0)
                                  ? 'el-icon-lock'
                                  : 'el-icon-unlock'
                              "
                            ></i>
                          </div>
                        </el-form-item>
                      </div>
                      <el-form-item label="开关状态">
                        <div class="rightW">
                          <el-switch
                            v-model="item.switchStatus"
                            active-color="#5462C0"
                            inactive-color="#E9EBF4"
                            active-value="1"
                            inactive-value="0"
                            :disabled="!!(item.isLock - 0)"
                          ></el-switch>
                        </div>
                      </el-form-item>
                      <div v-if="key === '空调'">
                        <el-form-item label="模式">
                          <div class="rightW">
                            <el-radio-group v-model="item.pattern">
                              <el-radio
                                :disabled="!!(item.isLock - 0)"
                                v-for="types in item.configSignalTypeItems"
                                :label="types.itemKey"
                                :key="types.id"
                              >
                                {{ types.itemVal }}
                              </el-radio>
                            </el-radio-group>
                          </div>
                        </el-form-item>
                        <el-form-item label="温度">
                          <div class="rightW temp">
                            <el-slider
                              v-model="item.temperature"
                              :show-tooltip="false"
                              :disabled="!!(item.isLock - 0)"
                            ></el-slider>
                            <span class="tip">{{ item.temperature }}℃</span>
                          </div>
                        </el-form-item>
                        <el-form-item label="风速">
                          <div class="rightW">
                            <el-radio-group v-model="item.windSpeed">
                              <el-radio
                                :disabled="!!(item.isLock - 0)"
                                v-for="types in item.configSignalTypeItems2"
                                :label="types.itemKey"
                                :key="types.id"
                              >
                                {{ types.itemVal }}
                              </el-radio>
                            </el-radio-group>
                          </div>
                        </el-form-item>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </el-form>
            </div>
          </div>
        </el-card>
      </el-col>
      <Add
        ref="add"
        :sceneDeviceList="deviceList"
        :logicalInfo="logicalInfo"
        :scene-info="sceneInfo"
        @fetch-data="fetchData"
        @handle-search="handleSearch"
      ></Add>
    </el-row>
  </div>
</template>
  
  <script>
import { areaTreeList, getList, info, delScene } from '@/api/scene.js'
import Add from './components/add'
export default {
  name: 'SceneManagement',
  components: { Add },
  data() {
    const data = []
    return {
      data: [],
      loading: false,
      defaultProps: {
        children: 'children',
        label: 'label',
      },
      filterText: '',
      isLock: true,
      deviceList: {},
      sceneDeviceList: [],
      sceneList: [],
      logicalInfo: {
        logicalAreaId: 0,
        logicalFloorId: 0,
        logicalBuildId: '',
      },
      activeIndex: '',
      sceneInfo: {},
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    },
  },
  created() {
    this.treeData()
  },
  methods: {
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    treeData() {
      areaTreeList().then((res) => {
        if (res.code == 0) {
          this.data = res.data
          this.logicalInfo.logicalBuildId = this.data[0].value
          this.fetchData()
        }
      })
    },
    handleNodeClick(data) {
      this.activeIndex = 0
      //选一级，二级时logicalAreaId传0 选3级时logicalAreaId传区域value值，选一级时logicalFloorId值为0 选二级或者三级时logicalFloorId传楼层value
      const node = this.$refs['tree'].currentNode.node
      this.logicalInfo.logicalBuildId = this.data[0].value
      if (node.level === 3) {
        this.logicalInfo.logicalFloorId = data.value
        this.logicalInfo.logicalAreaId = data.value
      } else if (node.level === 2) {
        this.logicalInfo.logicalFloorId = data.value
        this.logicalInfo.logicalAreaId = 0
      } else {
        this.logicalInfo.logicalFloorId = 0
        this.logicalInfo.logicalAreaId = 0
      }
      this.fetchData()
    },
    handleSwicth(data) {
      data.isLock = data.isLock == 0 ? 1 : 0
    },
    handleSearch(index, id) {
      const idTemp = id || this.sceneInfo.id
      this.activeIndex = String (index || this.sceneList.findIndex(val => val.id == idTemp))
      console.log(this.activeIndex, this.sceneInfo, this.sceneInfo.id, this.sceneList)
      
      info({ id: idTemp }).then((res) => {
        const { sceneDeviceList } = res.data
        this.sceneInfo = res.data
        const obj = {}
        sceneDeviceList.forEach((val) => {
          this.$set(val, 'switchStatus', val.status)
          if (obj[val.deviceTypeName]) {
            val.temperature = +val.temperature || 0
            obj[val.deviceTypeName].push(val)
          } else {
            val.temperature = +val.temperature || 0
            obj[val.deviceTypeName] = [val]
          }
        })
        this.deviceList = obj
        console.log('更新场景拿到的数据', obj, this.deviceList)
      })
    },
    handleClose(id) {
      this.$baseConfirm('你确定要删除当前场景吗', null, async () => {
          delScene({ id }).then((res) => {
            if (res.code == 0) {
              this.sceneList = this.sceneList.filter((val) => val.id !== id)
              this.$baseMessage(res.msg, 'success')
              this.fetchData()
              this.activeIndex = ''
            }
          })
        })
    },
    async fetchData(params, id) {
      this.listLoading = true
      await getList({ ...this.logicalInfo }).then((res) => {
        if (res.code == 0) {
          const { deviceList, sceneList } = res.data || {}
          this.sceneList = sceneList
          this.sceneDeviceList = deviceList.map((val) => {
            return {
              sceneId: '',
              deviceId: val.id,
              name: val.name,
              deviceTypeName: val.deviceTypeName,
              switchStatus: '0',
              pattern: val.pattern,
              temperature: val.temperature,
              windSpeed: val.windSpeed,
              isLock: 1,
              configSignalTypeItems: val.configSignalTypeItems,
              configSignalTypeItems2: val.configSignalTypeItems2,
            }
          })
          const obj = {}
          this.sceneDeviceList.forEach((val) => {
            if (obj[val.deviceTypeName]) {
              obj[val.deviceTypeName].push(val)
            } else {
              obj[val.deviceTypeName] = [val]
            }
          })
          this.deviceList = obj
          console.log(this.deviceList, this.sceneList, this.deviceList)
        }
      })

      if (params === 'add') {
        const index = this.sceneList.findIndex(val => val.id == id)
        console.log('1111111111111111', index, id)
        this.handleSearch(index, id)
      }

      setTimeout(() => {
        this.listLoading = false
      }, 300)
    },
    handleAdd(data) {
      if (data === 'add') {
        this.$refs['add'].showEdit(true)
      } else {
        this.$refs['add'].showEdit()
      }
    },
  },
}
</script>
<style scoped lang="scss">
.block {
  height: calc(100vh - 195px);
  overflow-y: auto;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: '';
}
.clearfix:after {
  clear: both;
}
.scen-box {
  padding: 24px;

  .scen-item {
    flex-basis: 24%;
    flex-shrink: 0;
    width: 24%;
    background: #ffffff;
    border-radius: 4px;
    border: 1px solid #e9ebf4;
    height: 171px;

    & ~ .scen-item {
      margin-left: 1%;
    }

    .item {
      padding: 24px;
    }

    .title {
      font-size: 16px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.85);
      line-height: 24px;
      display: flex;
      justify-content: space-between;
    }
    .close-icon {
      color: #5462c0;
      background: #e9ebf4;
      padding: 4px;
    }

    .info {
      color: rgba(0, 0, 0, 0.65);
      margin: 12px 0;
      display: inline-block;
    }

    .tip {
      color: rgba(0, 0, 0, 0.45);
      font-size: 12px;
      margin-top: 12px;
    }
  }

  .active {
    border: 1px solid #5462c0;
    background: #e9ebf4;
  }
}

.scen-type {
  .type-name {
    padding-bottom: 20px;
    i {
      width: 4px;
      height: 24px;
      background: #5462c0;
      float: left;
    }
    span {
      font-size: 18px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.85);
      line-height: 26px;
      margin-left: 16px;
    }
  }

  .card {
    padding: 24px;
    border: 1px solid #e9ebf4;
    background: #fff;
    margin-bottom: 36px;

    .lock i {
      background: #e9ebf4;
      color: #5462c0;
      border-radius: 4px;
      padding: 4px;
    }

    .card-flex {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      color: rgba(0, 0, 0, 0.65);
    }

    .temp {
      display: flex;
      align-items: center;
      .tip {
        margin-left: 8px;
      }
    }

    .rightW {
      width: 100%;
      text-align: right;
    }

    .card-title {
      font-size: 16px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.85);
    }
  }
}
::v-deep {
  .scen-box {
    .el-card__body {
      display: flex;
      padding: 0;
      width: 100%;
      overflow: auto;
    }
  }

  .type-wrap {
    .el-card__body {
      overflow-y: auto;
    }
  }

  .el-slider {
    flex: 1;
  }

  .card-flex {
    .el-form-item__content {
      font-size: 18px;
    }
    .el-form-item {
      margin-bottom: 0;
    }

    .el-form-item__content {
      margin-left: 0 !important;
    }
  }

  .card {
    .el-radio {
      margin: 4px;
      .el-radio__input {
        display: none;
      }

      .el-radio__label {
        background: #f3f3f3;
        line-height: 24px;
        padding: 4px 8px;
        height: 24px;
        border-radius: 4px;
        border: 1px solid rgba(0, 0, 0, 0.15);
        margin-bottom: 8px;
      }

      .el-radio__input.is-checked + .el-radio__label {
        color: #fff;
        background: #5462c0;
      }
    }
  }
}
</style>
  