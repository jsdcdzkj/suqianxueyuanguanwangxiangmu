<template>
    <div class="index-container">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="140px" class="demo-ruleForm">
            <el-row :gutter="20">
                <el-col :span="4">
                    <el-form-item label="是否启用峰谷平电价" prop="delivery">
                        <el-switch v-model="ruleForm.delivery" :disabled="status"></el-switch>
                    </el-form-item>
                </el-col>
                <el-col :span="20">
                    <el-form-item>
                        <el-button type="primary" :disabled="saveStatus" @click="submitForm('ruleForm')">保 存</el-button>
                        <el-button @click="editForm('ruleForm')" :disabled="status2">修 改</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
            <div v-if="!ruleForm.delivery">
                <div class="nav-group__title">用电基础配置项</div>

                <el-form-item label="基础电价" prop="num">
                    <el-input-number v-model="ruleForm.num" :disabled="saveStatus" :precision="2" :step="0.1"
                                    :max="10"></el-input-number>
                </el-form-item>
            </div>
            

            <div v-else>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-card class="box-card" shadow="hover">
                            <!-- <div slot="header" class="clearfix">
                                <span>高峰时段</span>
                                <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
                            </div> -->
                            <div class="title mb2">
                                <div class="tips-title">
                                    <vab-icon :icon="['fas', 'sort-amount-down']"></vab-icon>&nbsp;高峰时段
                                    <el-button style="float: right;" icon="el-icon-plus" :disabled="saveStatus" type="success" @click="addTime('',1)" size="mini">新增</el-button>
                                </div>
                                
                            </div>
                            <el-table :data="peakTimeHigh" border stripe>
                                <el-table-column label="起时点" align="center" prop="startTime">
                                </el-table-column>
                                <el-table-column label="止时点" align="center" prop="endTime">
                                </el-table-column>
                                <el-table-column label="操作" align="center">
                                <template #default="{ row }">
                                    <el-button icon="el-icon-edit" plain type="success" :disabled="saveStatus" size="mini" @click="addTime(row,1)">编辑</el-button>
                                    <el-button icon="el-icon-delete" plain type="danger" :disabled="saveStatus" size="mini" @click="delTime(row)">删除</el-button>
                                </template>
                                </el-table-column>
                            </el-table>
                        </el-card>
                        
                    </el-col>
                    <el-col :span="12">
                        <el-card class="box-card" shadow="hover">
                            <!-- <div slot="header" class="clearfix">
                                <span>低谷时段</span>
                                <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
                            </div> -->

                            <div class="title mb2">
                                <div class="tips-title">
                                    <vab-icon :icon="['fas', 'sort-amount-up-alt']"></vab-icon>&nbsp;低谷时段
                                    <el-button style="float: right;" icon="el-icon-plus" :disabled="saveStatus" type="success" @click="addTime('',2)" size="mini">新增</el-button>
                                </div>
                                
                            </div>
                            <el-table :data="peakTimeLow" border stripe>
                                <el-table-column label="起时点" align="center" prop="startTime">
                                </el-table-column>
                                <el-table-column label="止时点" align="center" prop="endTime">
                                </el-table-column>
                                <el-table-column label="操作" align="center">
                                <template #default="{ row }">
                                    <el-button icon="el-icon-edit" plain type="success" :disabled="saveStatus" size="mini" @click="addTime(row,2)">编辑</el-button>
                                    <el-button icon="el-icon-delete" plain type="danger" :disabled="saveStatus" size="mini" @click="delTime(row)">删除</el-button>
                                </template>
                                </el-table-column>
                            </el-table>
                        </el-card>
                    </el-col>
                </el-row>
            </div>
        </el-form>

        <el-dialog :title="title" :visible.sync="dialogFormVisible" width="500px">
            <el-form ref="dialogForm" :model="dialogForm" :rules="dialogRules"  label-width="140px" class="demo-ruleForm">
                <el-form-item label="起时点" prop="startTime">
                    <!--<el-time-picker v-model="dialogForm.startTime" placeholder="选择时间" value-format="HH" format="HH">-->
                    <!--</el-time-picker>-->
                    <el-select v-model="dialogForm.startTime" placeholder="请选择起时点">
                        <el-option label="00" value="00"></el-option>
                        <el-option label="01" value="01"></el-option>
                        <el-option label="02" value="02"></el-option>
                        <el-option label="03" value="03"></el-option>
                        <el-option label="04" value="04"></el-option>
                        <el-option label="05" value="05"></el-option>
                        <el-option label="06" value="06"></el-option>
                        <el-option label="07" value="07"></el-option>
                        <el-option label="08" value="08"></el-option>
                        <el-option label="09" value="09"></el-option>
                        <el-option label="10" value="10"></el-option>
                        <el-option label="11" value="11"></el-option>
                        <el-option label="12" value="12"></el-option>
                        <el-option label="13" value="13"></el-option>
                        <el-option label="14" value="14"></el-option>
                        <el-option label="15" value="15"></el-option>
                        <el-option label="16" value="16"></el-option>
                        <el-option label="17" value="17"></el-option>
                        <el-option label="18" value="18"></el-option>
                        <el-option label="19" value="19"></el-option>
                        <el-option label="20" value="20"></el-option>
                        <el-option label="21" value="21"></el-option>
                        <el-option label="22" value="22"></el-option>
                        <el-option label="23" value="23"></el-option>
                        <el-option label="24" value="24"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="止时点" prop="endTime">
                    <!--<el-time-picker v-model="dialogForm.endTime" placeholder="选择时间" value-format="HH" format="HH">-->
                    <!--</el-time-picker>-->
                    <el-select v-model="dialogForm.endTime" placeholder="请选择止时点">
                        <el-option label="00" value="00"></el-option>
                        <el-option label="01" value="01"></el-option>
                        <el-option label="02" value="02"></el-option>
                        <el-option label="03" value="03"></el-option>
                        <el-option label="04" value="04"></el-option>
                        <el-option label="05" value="05"></el-option>
                        <el-option label="06" value="06"></el-option>
                        <el-option label="07" value="07"></el-option>
                        <el-option label="08" value="08"></el-option>
                        <el-option label="09" value="09"></el-option>
                        <el-option label="10" value="10"></el-option>
                        <el-option label="11" value="11"></el-option>
                        <el-option label="12" value="12"></el-option>
                        <el-option label="13" value="13"></el-option>
                        <el-option label="14" value="14"></el-option>
                        <el-option label="15" value="15"></el-option>
                        <el-option label="16" value="16"></el-option>
                        <el-option label="17" value="17"></el-option>
                        <el-option label="18" value="18"></el-option>
                        <el-option label="19" value="19"></el-option>
                        <el-option label="20" value="20"></el-option>
                        <el-option label="21" value="21"></el-option>
                        <el-option label="22" value="22"></el-option>
                        <el-option label="23" value="23"></el-option>
                        <el-option label="24" value="24"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="sub">确 定</el-button>
            </div>
        </el-dialog>

        
        
    </div>
</template>

<script>

import {getAll, saveOrUpdate} from "@/api/peakGuPing";
import {getList,addPeakGuPingTime,updatePeakGuPingTime,delPeakGuPingTime} from "@/api/peakGuPingTime.js";

export default {
    name: 'electricity',
    components: {},
    data() {
        return {
            status: true,
            status2: false,
            saveStatus: true,
            ruleForm: {
                delivery: false,
                num: 1,
            },
            rules: {
                num: [
                    {required: true, message: '价格不能为空'},
                ]
            },
            querry:{
                "type":""
            },
            peakTimeHigh: [],
            peakTimeLow: [],
            dialogFormVisible: false,
            dialogForm: {
                startTime: '',
                endTime: '',
                type: '',
            },
            dialogRules: {
                startTime: [
                    {required: true, message: '起时点不能为空'},
                ],
                endTime: [
                    {required: true, message: '止时点不能为空'},
                ],
            },
            
            title: '',
            type: '',
            id: '',
        }
    },
    created() {
        this.init();
        this.getListData();
    },
    methods: {
        async init() {
            const res = await getAll()
            if (res.data.length > 0) {
                this.ruleForm.id = res.data[0].id
                this.ruleForm.delivery = res.data[0].delivery == "1" ? true : false
                this.ruleForm.num = res.data[0].num
            }
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.status = true;
                    this.status2 = false;
                    this.saveStatus = true;
                    if(this.ruleForm.delivery == true){
                        this.ruleForm.delivery = 1
                        this.ruleForm.num = 1
                    }else if(this.ruleForm.delivery == false){
                        this.ruleForm.delivery = 0
                    }
                    saveOrUpdate(this.ruleForm)
                    this.ruleForm.delivery = this.ruleForm.delivery == 1 ? true : false
                    this.$baseMessage("保存成功", 'success')
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        editForm(formName) {
            this.status = false;
            this.status2 = true;
            this.saveStatus = false;
        },
        addTime(row,type){
            var that = this ;
            that.id = "" ;
            that.dialogFormVisible = true
            if(type == 1){
                that.title = '高峰时段'
                that.type = '0'
            }else{
                that.title = '低谷时段'
                that.type = '1'
            }
            if(row != ''){
                that.title += '编辑'
                that.dialogForm = row
                that.id = row.id ;
            }else{
                that.title += '新增'
                that.dialogForm.startTime ="" ;
                that.dialogForm.endTime ="" ;
                setTimeout(() => {
                    that.$refs.dialogForm.clearValidate();
                }, 100)

            }

            that.$refs.dialogForm.clearValidate();
        },
        delTime(row){
            this.$confirm('此操作将永久删除该时段, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
                }).then(() => {
                    this.del(row.id) ;

                })
        },
        getListData() {
            this.querry.type = "0" ;
            getList(this.querry).then((res) => {
                if (res.code == 0) {
                    this.peakTimeHigh = res.data ;
                }
            });

            this.querry.type = "1" ;
            getList(this.querry).then((res) => {
                if (res.code == 0) {
                    this.peakTimeLow = res.data ;
                }
            });
        },
        sub(){
            this.$refs['dialogForm'].validate((valid) => {
                if (valid) {
                    this.dialogForm.type = this.type

                    if(this.dialogForm.endTime<this.dialogForm.startTime){

                        this.$baseMessage("结束时间小于开始时间", 'error')
                        return ;
                    }
                    if(this.id){
                        updatePeakGuPingTime(this.dialogForm).then((res) => {
                            if (res.code == 0) {
                                this.dialogFormVisible = false
                                this.getListData() ;
                                this.$baseMessage("操作成功", 'success')
                            }
                        });
                    }else {
                        addPeakGuPingTime(this.dialogForm).then((res) => {
                            if (res.code == 0) {
                                this.dialogFormVisible = false
                                this.getListData() ;
                                this.$baseMessage("操作成功", 'success')
                            }
                        });
                    }
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });



        },
        del(id){
            delPeakGuPingTime(id).then((res) => {
                if (res.code == 0) {
                    this.$baseMessage("删除成功", 'success')
                    this.getListData() ;
                }
            });
        }

    },
}
</script>
<style scoped lang="scss">
.mb2 {
    margin-bottom: 14px;
}
.index-container {
    height: calc(100vh - 144px);
}

.nav-group__title {
    font-size: 12px;
    color: #999;
    line-height: 26px;
    padding-left: 44px;
}
</style>
