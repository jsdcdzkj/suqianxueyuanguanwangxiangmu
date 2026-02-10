<template>
    <el-drawer :title="title" :visible.sync="dialogFormVisible" size="1200px" @close="close"
               :before-close="handleClose">
        <!-- <div slot="title" class="header-title el-dialog__title">
            <span class="title-left">{{ title }}</span>
            <span> 设备信息  </span>
        </div> -->
        <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>批量编辑</b></span>
        <div style="padding: 0 20px;">

            <div class="tips-title"><i class="el-icon-s-promotion"></i>批量编辑项</div>
            <!-- <div class="module-title" style="margin-top: -15px;">
            <svg t="1683618700542" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8350" width="20" height="20">
              <path d="M571.52 136.832a32 32 0 0 1 44.8 0.64l174.4 175.904a32 32 0 0 1-1.632 46.624L368.864 728.064a32 32 0 0 1-21.76 7.936l-155.776-3.328a32 32 0 0 1-31.328-32v-158.208a32 32 0 0 1 9.92-23.168z m42.464 514.496l239.84 4.672a32 32 0 1 1-1.248 64l-239.84-4.672a32 32 0 1 1 1.28-64zM592.96 204.8L224 556.16v113.184l112 2.4 385.312-337.472L592.96 204.8z m259.296 606.528a32 32 0 0 1 0.48 64l-628.48 4.672a32 32 0 0 1-0.48-64l628.48-4.672z" fill="#aaaaaa" p-id="8351"></path>
            </svg>
            <span>批量编辑项</span>
          </div> -->
            <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="fjzd-form">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="设备类型" prop="deviceType">
                            <el-select v-model="form.deviceType" placeholder="请选择设备类型" class="w">
                                <el-option
                                    v-for="item in this.deviceTypeList"
                                    :key="item.id"
                                    :label="item.deviceTypeName"
                                    :value="item.id"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="设备状态" prop="status">
                            <el-select v-model="form.status" placeholder="请选择设备状态" class="w">
                                <el-option
                                    v-for="item in this.statusList"
                                    :key="item.dictValue"
                                    :label="item.dictLabel"
                                    :value="item.dictValue"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="物理位置" prop="areaIds">
                            <el-cascader
                                :props="{
                                  expandTrigger: 'hover',
                                }"
                                :options="areaList"
                                style="width: 100%;"
                                :show-all-levels="false"
                                collapse-tags
                                ref="areaId"
                                v-model="form.areaIds"
                                @change="choseArea"
                                filterable
                                clearable></el-cascader>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="安装位置" prop="place">
                            <el-input v-model.trim="form.place" autocomplete="off" placeholder="请输入安装位置"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="12">
                        <el-form-item label="逻辑位置" prop="logicalAreaIds">
                            <el-cascader
                                :props="{
                                  expandTrigger: 'hover',
                                  value: 'value',
                                  label: 'label',
                                  children: 'children',
                                  checkStrictly: true,
                                }"
                                :options="logicalAreaList"
                                style="width: 100%;"
                                :show-all-levels="false"
                                collapse-tags
                                ref="logicalAreaId"
                                v-model="form.logicalAreaIds"
                                @change="choseLogicalArea"
                                filterable
                                clearable></el-cascader>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属分项" prop="subitem">
                            <el-select v-model="form.subitem" placeholder="请选择设备所属分项" class="w">
                                <el-option
                                    v-for="item in this.subitemList"
                                    :key="item.id"
                                    :label="item.subitemName"
                                    :value="item.id"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="12">
                        <el-form-item label="安装日期" prop="installationDate">
                            <el-date-picker type="date" placeholder="请选择安装日期" v-model="form.installationDate"
                                            value-format="yyyy-MM-dd" style="width: 100%;"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="使用日期" prop="useTime">
                            <el-date-picker type="date" placeholder="请选择使用日期" v-model="form.useTime"
                                            value-format="yyyy-MM-dd" style="width: 100%;"></el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="12">
                        <el-form-item label="设备管理员" prop="userId">
                            <el-select v-model="form.userId" placeholder="请选择设备管理员" class="w">
                                <el-option
                                    v-for="item in this.userList"
                                    :key="item.id"
                                    :label="item.realName"
                                    :value="item.id"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="管理员电话" prop="phone">
                            <el-input v-model.trim="form.phone" autocomplete="off" placeholder="请输入管理员电话"
                                      @blur="form.phone = $event.target.value"
                                      onkeyup="value=value.replace(/[^\d]/g,'')"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="供应商名称" prop="supplierId">
                            <el-select v-model="form.supplierId" placeholder="请选择供应商" @change="supplierChange"
                                       class="w">
                                <el-option
                                    v-for="item in this.supplierList"
                                    :key="item.id"
                                    :label="item.supplierName"
                                    :value="item.id"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="设备型号" prop="modelNum">
                            <el-select v-model="form.modelNum" placeholder="请选择设备型号" class="w">
                                <el-option
                                    v-for="item in this.modelList"
                                    :key="item.id"
                                    :label="item.modelName"
                                    :value="item.id"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="12">
                        <el-form-item label="维保公司" prop="companyId">
                            <el-select v-model="form.companyId" placeholder="请选择维保公司" class="w">
                                <el-option
                                    v-for="item in this.supplierTypeList"
                                    :key="item.dictValue"
                                    :label="item.dictLabel"
                                    :value="item.dictValue"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="年检日期" prop="inspectionDate">
                            <el-date-picker type="date" placeholder="请选择年检日期" v-model="form.inspectionDate"
                                            value-format="yyyy-MM-dd" style="width: 100%;"></el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="12">
                        <el-form-item label="采购日期" prop="procureDate">
                            <el-date-picker type="date" placeholder="请选择采购日期" v-model="form.procureDate"
                                            value-format="yyyy-MM-dd" style="width: 100%;"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="过保日期" prop="expirationDate">
                            <el-date-picker type="date" placeholder="请选择过保日期" v-model="form.expirationDate"
                                            value-format="yyyy-MM-dd" style="width: 100%;"></el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>

            </el-form>
            <div style="height: 270px;">
                <!-- <div class="module-title">
                <svg t="1683618338470" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6828" width="15" height="15">
                  <path d="M485.052632 943.157895L80.842105 538.947368 512 80.842105h431.157895v431.157895L485.052632 943.157895zM889.263158 134.736842H538.947368L161.684211 538.947368l323.368421 323.368421 404.210526-377.263157V134.736842z m-256 134.736842a121.263158 121.263158 0 1 1 0 242.526316 121.263158 121.263158 0 0 1 0-242.526316z m0 188.631579a67.368421 67.368421 0 1 0 0-134.736842 67.368421 67.368421 0 0 0 0 134.736842z" fill="#AAAAAA" p-id="6829"></path>
                </svg>
                <span>已选设备</span>
              </div> -->

                <div class="tips-title"><i class="el-icon-s-promotion"></i>已选设备</div>
                <CjzdTable ref="CjzdTable" :tableData="tableData" @fetch-data=""></CjzdTable>
            </div>
        </div>

        <!-- <div slot="footer" class="footer-btn">
          <el-button @click="close">取 消</el-button>
          <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{ loading ? '确定中 ...' : '确定'
          }}</el-button>
        </div> -->

        <div class="footer-btn">
            <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">立即提交</el-button>
            <el-button @click="close">取消</el-button>
        </div>
    </el-drawer>
</template>

<script>
    import {getDeviceCollectList, batchUpdate} from '@/api/deviceCollect'

    import {getRedisDictList} from '@/api/sysDict'
    import {getDeviceSubitemList} from '@/api/configDeviceSubitem'
    import {getBuildAreaList} from '@/api/sysBuildArea'
    import {getDeviceGatewayList} from '@/api/deviceGateway'
    import {getBuildList} from '@/api/sysBuild'
    import {getBuildFloorList} from '@/api/sysBuildFloor'
    import {getRedisUserDictMap} from '@/api/userManagement'
    import {getConfigModelList} from '@/api/configModel'
    import {getConfigSupplierList} from '@/api/configSupplier'
    import {getDeviceTypeList} from '@/api/configDeviceType'
    import {areaTreeList, areaTreeList2} from '@/api/org'

    import CjzdTable from './cjzdTable'

    export default {
        name: 'CjzdPlEdit',
        components: {CjzdTable},
        data() {
            return {
                value: '',
                loading: false,
                disabled: false,
                form: {
                    id: '',
                    modelNum: '',
                    supplierId: '',
                    deviceType: '',
                    status: '',
                    areaId: '',
                    logicalAreaId: '',
                    logicalType: '',
                    place: '',
                    installationDate: '',
                    useTime: '',
                    userId: '',
                    phone: '',
                    companyId: '',
                    inspectionDate: '',
                    procureDate: '',
                    expirationDate: '',
                    subitem: '',
                    gatewayId: '',
                    voList: [],
                    areaIds: [],
                    logicalAreaIds: [],
                },
                rules: {
                    // username: [
                    //   {required: true, trigger: 'blur', message: '请输入用户名'},
                    // ],
                    // password: [{required: true, trigger: 'blur', message: '请输入密码'}],
                    // org_code: [
                    //   {required: true, trigger: 'submit', message: '请选择所属机构'},
                    // ],
                    // checkedRoles: [
                    //   {required: true, trigger: 'blur', message: '请选择所属机构'},
                    // ],
                },
                title: '',
                dialogFormVisible: false,
                deviceTypeList: [],
                subitemList: [],
                areaList: [],
                logicalAreaList: [],
                statusList: [],
                gatewayList: [],
                buildList: [],
                buildFloorList: [],
                supplierTypeList: [],
                userList: [],
                modelList: [],
                supplierList: [],
                tableData: [],
                checkAreaDatas: [],
                checkLogicalAreaDatas: [],
            }
        },
        created() {
        },
        methods: {
            showEdit(row) {
                this.title = '批量编辑';
                this.tableData = row;
                this.form.voList = this.tableData;

                console.log("tableData");
                console.log(row);

                //设备类型
                this.deviceTypeListData();
                //设备状态 字典
                this.statusListData();
                //维保公司/供应商类型【字典】
                this.supplierTypeListData();
                //所属分项
                this.subitemListData();
                //网关设备
                this.gatewayListData();
                // //所属楼宇
                // this.buildListData();
                // //所属楼层
                // this.buildFloorListData();
                // //所属区域
                // this.areaListData();
                //设备管理员
                this.userListData();
                // //设备型号
                // this.modelListData();
                //供应商
                this.supplierListData();

                this.areaTreeList()
                this.areaTreeList2()

                this.dialogFormVisible = true;
            },

            async deviceTypeListData() {
                const {data} = await getDeviceTypeList();
                console.log("设备类型", data)
                this.deviceTypeList = data;
            },
            async statusListData() {
                const {data} = await getRedisDictList({dictType: "deviceStatus"});
                console.log("设备状态", data)
                this.statusList = data;
            },
            async supplierTypeListData() {
                const {data} = await getRedisDictList({dictType: "supplierType"});
                console.log("维保公司", data)
                this.supplierTypeList = data;
            },
            async subitemListData() {
                const {data} = await getDeviceSubitemList();
                console.log("分项", data)
                this.subitemList = data;
            },
            async gatewayListData() {
                const {data} = await getDeviceGatewayList();
                console.log("网关", data)
                this.gatewayList = data;
            },
            // async buildListData() {
            //     const {data} = await getBuildList();
            //     console.log("所属楼宇", data)
            //     this.buildList = data;
            // },
            // async buildChange(v) {
            //     this.buildFloorList = [];
            //     this.areaList = [];
            //     this.form.floorId = '';
            //     this.form.areaId = '';
            //     this.buildFloorListData(v);
            // },
            // async floorChange(v) {
            //     this.areaList = [];
            //     this.form.areaId = '';
            //     this.areaListData(v);
            // },
            // async buildFloorListData(buildId) {
            //     const {data} = await getBuildFloorList({'dictBuilding': buildId});
            //     console.log("所属楼层", data)
            //     this.buildFloorList = data;
            // },
            // async areaListData(floorId) {
            //     const {data} = await getBuildAreaList({'floorId': floorId});
            //     console.log("所属区域", data)
            //     this.areaList = data;
            // },
            async userListData() {
                const {data} = await getRedisUserDictMap();
                console.log("管理员", data)
                this.userList = data;
            },
            async modelListData(supplierId) {
                const {data} = await getConfigModelList({supplierId: supplierId});
                console.log("设备型号", data)
                this.modelList = data;
            },
            async supplierListData() {
                const {data} = await getConfigSupplierList();
                console.log("供应商", data)
                this.supplierList = data;
            },
            async supplierChange(v) {
                this.modelList = [];
                this.form.modelNum = '';
                this.modelListData(v);
            },
            //物理位置列表数据
            areaTreeList() {
                areaTreeList().then((res) => {
                    if (res.code == 0) {
                        this.areaList = res.data;
                    }
                })
            },
            //逻辑位置列表数据
            areaTreeList2() {
                areaTreeList2().then((res) => {
                    if (res.code == 0) {
                        this.logicalAreaList = res.data;
                    }
                })
            },
            choseArea(subjectValue) {
                console.log(subjectValue)
                if (subjectValue.length == 0) {
                    //clearable的点击×删除事件
                    this.checkAreaDatas = [];
                }
            },
            choseLogicalArea(subjectValue) {
                console.log(subjectValue)
                if (subjectValue.length == 0) {
                    //clearable的点击×删除事件
                    this.checkLogicalAreaDatas = [];
                }
            },
            close() {
                this.$refs['form'].resetFields();
                this.form = this.$options.data().form;
                this.dialogFormVisible = false
            },
            handleClose(done) {
                if (this.loading) {
                    return
                }

                this.loading = true;
                this.timer = setTimeout(() => {
                    done();
                    // 动画关闭需要一定的时间
                    setTimeout(() => {
                        this.loading = false
                    }, 400)
                }, 500)

                // this.$confirm('确定要提交表单吗？')
                //   .then((_) => {
                //     this.loading = true;
                //     this.timer = setTimeout(() => {
                //       done();
                //       // 动画关闭需要一定的时间
                //       setTimeout(() => {
                //         this.loading = false
                //       }, 400)
                //     }, 2000)
                //   })
                //   .catch((_) => {
                //   })
            },
            /**
             * 验证表单要提交的对象的某些属性是否为空
             * @param obj 需要提交的对象
             * @param args 不需要判断的属性
             * @returns boolean
             */
            isForm(obj, ...args) {
                var count = 0;
                const t = Object.assign({}, obj)
                for (const i in args) {
                    delete t[args[i]]
                }
                console.log(t)
                for (const key in t) {
                    if (t.hasOwnProperty(key)) {
                        console.log(typeof t[key])
                        if (typeof t[key] == "string") {
                            if (t[key] === '') {
                                count++;
                            }
                        } else {
                            if (t[key].length == 0) {
                                count++;
                            }
                        }
                    }
                }
                return count
            },
            save() {
                if (this.loading) {
                    return
                }
                console.log(this.form.voList);
                if (this.form.voList.length == 0) {
                    this.$baseMessage("已选设备不能数量不能为0", 'error');
                    return
                }

                if (this.form.supplierId) {
                    if (this.form.modelNum == 'undefined' || this.form.modelNum == null || this.form.modelNum == '') {
                        this.$baseMessage("请选择设备型号", 'error');
                        return
                    }
                }

                // if (this.form.buildId) {
                //     if (this.form.floorId) {
                //         if (this.form.areaId == 'undefined' || this.form.areaId == null || this.form.areaId == '') {
                //             this.$baseMessage("请选择设备所属区域", 'error');
                //             return
                //         }
                //     } else {
                //         this.$baseMessage("请选择设备所属楼层", 'error');
                //         return
                //     }
                // }

                var count = this.isForm(this.form, "voList", "logicalType");
                if (count >= 20) {
                    this.$baseMessage("请至少修改一项", 'error');
                    return
                }

                const areaId = this.$refs["areaId"].getCheckedNodes();
                const logicalAreaId = this.$refs["logicalAreaId"].getCheckedNodes();
                //获取当前点击节点的label值
                if (areaId != null && areaId != "" && areaId != undefined) {
                    for (var i = 0; i < areaId.length; i++) {
                        console.log(areaId[i].data.value)
                        if (areaId[i].level == 3) {
                            this.form.areaId = areaId[i].data.value;
                        }
                    }
                } else {
                    this.form.areaId = '';
                }
                //获取当前点击节点的label值
                if (logicalAreaId != null && logicalAreaId != "" && logicalAreaId != undefined) {
                    for (var i = 0; i < logicalAreaId.length; i++) {
                        console.log(logicalAreaId[i].data.value)
                        if (logicalAreaId[i].level == 3) {
                            this.form.logicalAreaId = logicalAreaId[i].data.value;
                            this.form.logicalType = "3";
                        } else if (logicalAreaId[i].level == 2) {
                            this.form.logicalAreaId = logicalAreaId[i].data.value;
                            this.form.logicalType = "2";
                        } else if (logicalAreaId[i].level == 1) {
                            this.form.logicalAreaId = logicalAreaId[i].data.value;
                            this.form.logicalType = "1";
                        }
                    }
                } else {
                    this.form.logicalAreaId = '';
                    this.form.logicalType = '';
                }
                this.$refs['form'].validate(async (valid) => {
                    if (valid) {
                        this.loading = true;
                        this.disabled = true;
                        setTimeout(() => {
                            this.loading = false;
                            this.disabled = false;
                        }, 1000);

                        const {msg} = await batchUpdate(this.form)
                        this.$baseMessage(msg, 'success')
                        this.$emit('fetch-data');
                        this.close();
                    } else {
                        return false
                    }
                })
            },
            openwin() {
                var that = this
                that.$refs['bumen'].showDia()
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
</style>

<style scoped lang="scss">
    .jiexi {
        position: absolute;
        right: 21px;
        top: 100px;
        z-index: 1;
        padding: 5px 15px;
    }

    ::v-deep {
        .el-drawer__header {
            padding: 10px !important;
            margin-bottom: 14px;
            border-bottom: 1px solid #d1d9e1;

            .drawer-title {
                font-size: 16px;
                line-height: 16px;
                color: #334c97;
                display: flex;
                align-items: center;

                i {
                    display: block;
                    font-size: 18px;
                    line-height: 18px;
                    margin-right: 4px;
                }

                b {
                    display: block;
                    font-size: 16px;
                    line-height: 16px;
                    margin-top: 2px;
                    margin-right: 4px;
                }
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

        .el-timeline {
            padding-left: 0 !important;
        }

        .timeline-con {
            padding: 10px 4px;
            height: calc(100vh - 598px);
            overflow-y: auto;
        }

        .time-con {
            margin-left: -6px;
            color: #333;
            // box-shadow: 0 0px 4px 0 rgba(0, 0, 0, 0.1);
            border-top: 4px solid #ccd2e6;
            background-color: #f5f6fb;

            .time-top {
                padding: 10px;

                span.time-name {
                    margin-right: 10px;
                    color: #666;
                    font-weight: bold;

                    i {
                        color: #ccd2e6;
                        margin-right: 4px;
                    }

                    b {
                        font-weight: normal;
                        color: #999;
                        margin-left: 12px;
                        font-size: 13px;
                    }
                }

                .el-tag {
                    float: right;
                    margin-top: -2px;
                }
            }

            .time-bot {
                display: flex;
                padding: 0px 10px 6px;
                align-items: flex-start;
                line-height: 24px;
                font-size: 14px;

                i {
                    display: inline-block;
                    width: 16px;
                    height: 24px;
                    margin-right: 4px;
                    line-height: 24px;
                    color: #ccd2e6;
                }

                p {
                    margin: 0;
                    color: #666;
                }
            }
        }

        .el-radio {
            padding: 6px 12px;
            background-color: #f5fbff;
            margin-bottom: 6px;
            margin-right: 6px;
        }

        .my-label {
            background: #e1f3d8;
        }

        .my-content {
            width: 280px;
        }
    }

    .footer-btn {
        padding: 12px 20px;
        border-top: 1px solid #efefef;
        position: absolute;
        bottom: 0;
        text-align: right;
        width: 100%;
        background-color: #fff;
        z-index: 999;
    }
</style>
