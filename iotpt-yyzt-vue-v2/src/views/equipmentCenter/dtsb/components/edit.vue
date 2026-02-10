<template>
    <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="768px"
               @close="close">
        <el-form ref="form" :model="form" :rules="isSee?{}:rules" label-width="100px" class="fjzd-form" :disabled="isSee">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="设备名称" prop="liftName">
                        <el-input v-model.trim="form.liftName" autocomplete="off" placeholder="请输入设备名称"></el-input>
                    </el-form-item>
                </el-col>
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
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="设备编码" prop="deviceCode">
                        <el-input v-model.trim="form.deviceCode" autocomplete="off" placeholder="请输入设备编码"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="设备SN" prop="deviceSn">
                        <el-input v-model.trim="form.deviceSn" autocomplete="off" placeholder="请输入设备SN"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="设备状态" prop="liftStatus">
                        <el-select v-model="form.liftStatus" placeholder="请选择设备状态" class="w">
                            <el-option
                                v-for="item in this.statusList"
                                :key="item.dictValue"
                                :label="item.dictLabel"
                                :value="item.dictValue"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="电梯相机" prop="videoId">
                        <el-select v-model="form.videoId" placeholder="请选择该轿厢的监控相机" filterable clearable class="w">
                            <el-option
                                v-for="item in videoList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="供应商名称" prop="maintenanceSupplier">
                        <el-select v-model="form.maintenanceSupplier" clearable placeholder="请选择供应商" @change="supplierChange" class="w">
                            <el-option
                                v-for="item in supplierList"
                                :key="item.id"
                                :label="item.supplierName"
                                :value="item.id"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="设备型号" prop="modelId">
                        <el-select v-model="form.modelId" clearable placeholder="请选择设备型号" class="w">
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
                            popper-class="location"
                            filterable
                            clearable></el-cascader>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="所属区域" prop="areaIds">
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
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="设备描述" prop="deviceDesc">
                        <el-input v-model.trim="form.deviceDesc" type="textarea" placeholder="请输入设备描述" :rows="3"
                                autocomplete="off"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="维保人姓名" prop="maintenanceUser">
                        <el-input v-model.trim="form.maintenanceUser" autocomplete="off" placeholder="请输入维保人姓名"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="维保人电话" prop="maintenancePhone">
                        <el-input v-model.trim="form.maintenancePhone" autocomplete="off" placeholder="请输入维保人电话"
                                  @blur="form.maintenancePhone = $event.target.value"
                                  onkeyup="value=value.replace(/[^\d]/g,'')"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="维保周期" prop="maintenanceCycle">
                        <el-input v-model.trim="form.maintenanceCycle" autocomplete="off" placeholder="请输入维保周期"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="维保次数" prop="maintenanceNum">
                        <el-input v-model.trim="form.maintenanceNum" autocomplete="off" placeholder="请输入维保次数"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="维保公司" prop="maintenanceCompany">
                        <el-input v-model.trim="form.maintenanceCompany" autocomplete="off" placeholder="请输入维保公司"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="最大载重" prop="bigLoad">
                        <el-input v-model.trim="form.bigLoad" autocomplete="off" placeholder="请输入最大载重"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="最大荷载人数" prop="crowdLoad">
                        <el-input type="number" v-model.trim="form.crowdLoad" autocomplete="off" placeholder="请输入最大荷载人数"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button type="primary" @click="save" :loading="loading" v-if="!isSee">
                {{ loading ? '确定中 ...' : '确定' }}
            </el-button>
        </div>
    </el-dialog>
</template>

<script>
    import {add,edit,detail} from '@/api/dtsb'
    import {getRedisDictList} from '@/api/sysDict'
    import {getConfigModelList} from '@/api/configModel'
    import {getConfigSupplierList} from '@/api/configSupplier'
    import {getDeviceTypeList} from '@/api/configDeviceType'
    import {areaTreeList, areaTreeList2} from '@/api/org'
    import { getPage } from '@/api/spsb'

    export default {
        name: 'edit',
        data() {
            return {
                isSee:false,
                loading: false,
                form: {
                    id: '',
                    liftName: '',
                    deviceCode: '',
                    deviceSn:'',
                    modelId:'',
                    maintenanceSupplier: '',
                    deviceType: '',
                    buildId: '',
                    floorId: '',
                    areaId: '',
                    logicalBuildId: '',
                    logicalFloorId: '',
                    logicalAreaId: '',
                    deviceDesc:'',
                    videoId:'',
                    maintenanceUser:'',
                    maintenancePhone:'',
                    maintenanceCycle:'',
                    maintenanceNum:'',
                    maintenanceCompany: '',
                    bigLoad:'',
                    crowdLoad:'',
                    liftStatus:'',
                    areaIds: [],
                    logicalAreaIds: []
                },
                rules: {
                    liftName: [{required: true, trigger: 'blur', message: '请输入设备名称'},],
                    deviceType: [{required: true, trigger: 'change', message: '请选择设备类型'}],
                    deviceCode: [{required: true, trigger: 'blur', message: '请输入设备编码'}],
                    liftStatus: [{required: true, trigger: 'change', message: '请选择设备状态'}],
                    areaIds: [{required: true, trigger: 'change', message: '请选择所属区域'}]
                },
                title: '',
                dialogFormVisible: false,
                deviceTypeList: [],
                areaList: [],
                logicalAreaList: [],
                statusList: [],
                supplierTypeList: [],
                modelList: [],
                supplierList: [],
                checkAreaDatas: [],
                checkLogicalAreaDatas: [],
                videoList:[]
            }
        },
        created() {
            //视频相机列表
            getPage({pageNo:1,pageSize:999}).then(res=>{
                this.videoList = res.data.records;
            })
            //设备类型
            this.deviceTypeListData();
            //设备状态 字典
            this.statusListData();
            //维保公司/供应商类型【字典】
            this.supplierTypeListData();
            //供应商
            this.supplierListData();
            this.areaTreeList();
            this.areaTreeList2();
        },
        methods: {
            showEdit(id,isSee) {
                if(isSee){
                    this.isSee = true;
                    this.title = '查看详情';
                    this.getData(id);
                }else{
                    this.isSee = false;
                    if (!id) {
                        this.title = '添加';
                    } else {
                        this.title = '编辑';
                        this.getData(id);
                    }
                }
                this.dialogFormVisible = true;
                this.$nextTick(() => {
                    if (this.$refs['form']) {
                        this.$refs['form'].resetFields();
                    }
                });
            },
            async deviceTypeListData() {
                const {data} = await getDeviceTypeList();
                this.deviceTypeList = data;
            },
            async statusListData() {
                const {data} = await getRedisDictList({dictType: "deviceStatus"});
                this.statusList = data;
            },
            async supplierTypeListData() {
                const {data} = await getRedisDictList({dictType: "supplierType"});
                this.supplierTypeList = data;
            },
            async modelListData(supplierId) {
                const {data} = await getConfigModelList({supplierId: supplierId});
                this.modelList = data;
            },
            async supplierListData() {
                const {data} = await getConfigSupplierList();
                this.supplierList = data;
            },
            async supplierChange(v) {
                this.modelList = [];
                this.form.modelId = '';
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
                if (subjectValue.length == 0) {
                    //clearable的点击×删除事件
                    this.checkAreaDatas = [];
                }
            },
            choseLogicalArea(subjectValue) {
                this.$refs.logicalAreaId.dropDownVisible = false; //监听值发生变化就关闭它
                if (subjectValue.length == 0) {
                    //clearable的点击×删除事件
                    this.checkLogicalAreaDatas = [];
                }
            },
            close() {
                this.$refs['form'].resetFields()
                this.form = this.$options.data().form
                this.dialogFormVisible = false
            },
            getData(id) {
                detail({id}).then(({data})=>{
                    data.areaIds = [String(data.buildId),String(data.floorId),String(data.areaId)];
                    var arr = [];
                    if(data.logicalBuildId){
                        arr.push(String(data.logicalBuildId));
                    }
                    if(data.logicalFloorId){
                        arr.push(String(data.logicalFloorId));
                    }
                    if(data.logicalAreaId){
                        arr.push(String(data.logicalAreaId));
                    }
                    data.logicalAreaIds = arr;
                    if(data.maintenanceSupplier){
                        data.maintenanceSupplier = Number(data.maintenanceSupplier);
                        this.modelListData(data.maintenanceSupplier);
                    }
                    this.form = data;
                })
            },
            save() {
                if (this.loading) {
                    return
                }
                if(this.form.areaIds){
                    this.form.areaId = this.form.areaIds[2]||'';
                    this.form.floorId = this.form.areaIds[1]||'';
                    this.form.buildId = this.form.areaIds[0]||'';
                }
                if(this.form.logicalAreaIds){
                    this.form.logicalAreaId = this.form.logicalAreaIds[2]||'';
                    this.form.logicalFloorId = this.form.logicalAreaIds[1]||'';
                    this.form.logicalBuildId = this.form.logicalAreaIds[0]||'';
                }
                this.$refs['form'].validate(async (valid) => {
                    if (valid) {
                        this.loading = true;
                        let formData = JSON.parse(JSON.stringify(this.form));
                        delete formData.areaIds;
                        delete formData.logicalAreaIds;
                        if(!formData.id){
                            add(formData).then((data)=>{
                                if (data.code == 0) {
                                    this.$baseMessage(data.msg, 'success');
                                    this.$emit('fetch-data')
                                    this.close();
                                } else {
                                    this.$baseMessage(data.msg, 'error')
                                }
                            }).finally(()=>{
                                this.loading = false;
                            })
                        }else{
                            edit(formData).then((data)=>{
                                if (data.code == 0) {
                                    this.$baseMessage(data.msg, 'success');
                                    this.$emit('fetch-data')
                                    this.close();
                                } else {
                                    this.$baseMessage(data.msg, 'error')
                                }
                            }).finally(()=>{
                                this.loading = false;
                            })
                        }
                    } else {
                        return false
                    }
                })
            }
        },
    }
</script>
<style lang="scss">
    .fjzd-form {
        padding-right: 12px !important;
    }


    .location .el-checkbox, .location .el-radio {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 10;
    }

    .location .el-cascader-node {
        padding-left: 34px;
    }

    .location .el-checkbox__input, .location .el-radio__input {
        position: absolute;
        left: 20px;
        top: 10px;
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


</style>
