<template>
    <el-dialog :close-on-click-modal="false" title="地锁" :visible.sync="dialogFormVisible" width="768px" @close="close">
        <el-form ref="form" :model="form" :rules="rules" label-width="127px" class="fjzd-form">
            <el-descriptions title="左车位地锁"></el-descriptions>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="设备状态" prop="isEnable">
                        <el-radio-group v-model="form.isEnable">
                            <el-radio :label="1" value="1">启用</el-radio>
                            <el-radio :label="2" value="2">禁用</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="地锁编号" prop="lockCode">
                        <el-input v-model.trim="form.lockCode" autocomplete="off" placeholder="请输入地锁编号" clearable></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="车位号" prop="parkingCode">
                        <el-input v-model.trim="form.parkingCode" autocomplete="off" placeholder="请输入车位号" clearable></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="车位类型" prop="stallType">
                        <el-select v-model="form.stallType" placeholder="请选择车位类型" clearable class="w">
                            <el-option v-for="item in this.typeSelectLists" :key="item.dictValue" :label="item.dictLabel"
                                :value="item.dictValue"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <el-descriptions title="右车位地锁"></el-descriptions>
        <el-form ref="form1" :model="form1" :rules="rules" label-width="127px" class="fjzd-form">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="设备状态" prop="isEnable">
                        <el-radio-group v-model="form1.isEnable">
                            <el-radio :label="1" value="1">启用</el-radio>
                            <el-radio :label="2" value="2">禁用</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="地锁编号" prop="lockCode">
                        <el-input v-model.trim="form1.lockCode" autocomplete="off" placeholder="请输入地锁编号" clearable></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="车位号" prop="parkingCode">
                        <el-input v-model.trim="form1.parkingCode" autocomplete="off" placeholder="请输入车位号" clearable></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="车位类型" prop="stallType">
                        <el-select v-model="form1.stallType" placeholder="请选择车位类型" clearable class="w">
                            <el-option v-for="item in this.typeSelectLists" :key="item.dictValue" :label="item.dictLabel"
                                :value="item.dictValue"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button type="primary" @click="save" :loading="loading">{{ loading ? '提交中 ...' :
        '提交'
                }}</el-button>
        </div>
    </el-dialog>
</template>

<script>
import { saveGroundLock, getGroundLockParkingDict,getGroundLockByDeviceId } from '@/api/spsb'
export default {
    name: 'dsEdit',
    data() {
        return {
            loading: false,
            form: {
                parkingFlag:0,
                isEnable: 1,
                lockCode:'',
                parkingCode:'',
                stallType:''
            },
            form1: {
                parkingFlag:1,
                isEnable: 1,
                lockCode:'',
                parkingCode:'',
                stallType:''
            },
            typeSelectLists: [],
            rules: {
                lockCode: [
                    { required: true, trigger: 'blur', message: '请输入地锁编号' },
                ],
                parkingCode: [{ required: true, trigger: 'blur', message: '请输入车位号' }],
                stallType: [
                    { required: true, trigger: 'change', message: '请选择车位类型' },
                ],
            },
            dialogFormVisible: false,
        }
    },
    mounted() {
        this.typeSelectList();
    },
    methods: {
        showEdit(row) {
            this.loading = false;
            getGroundLockByDeviceId({id:row.id}).then(({data})=>{
                if(data.length){
                    this.form = data.find(el=>{return el.parkingFlag==0});
                    this.form1 = data.find(el=>{return el.parkingFlag==1});
                }else{
                    this.form.videoId = row.id;
                    this.form1.videoId = row.id;
                }
            })
            this.dialogFormVisible = true;
        },
        // 车位类型
        typeSelectList() {
            getGroundLockParkingDict().then((res) => {
                if (res.code == 0) {
                    this.typeSelectLists = res.data;
                }
            })
        },
        close() {
            this.$refs['form'].resetFields();
            this.$refs['form1'].resetFields();
            this.dialogFormVisible = false;
        },
        save() {
            this.$refs['form'].validate((valid) => {
                if (valid) {
                    this.$refs['form1'].validate((valid1) => {
                        if(valid1){
                            this.loading = true
                            setTimeout(() => {
                                this.loading = false;
                            }, 1000)
                            saveGroundLock(JSON.stringify({param1:this.form,param2:this.form1})).then(res => {
                                if (0 == res.code) {
                                    this.$baseMessage("操作成功", 'success')
                                    this.close()
                                } else {
                                    if(res.msg != ""){
                                        this.$baseMessage(res.msg, 'error')
                                    }else{
                                        this.$baseMessage("操作失败:", 'error')
                                    }
                                    this.loading = false;
                                }
                            }).catch(()=>{
                                this.loading = false;
                            })
                        }
                    })
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