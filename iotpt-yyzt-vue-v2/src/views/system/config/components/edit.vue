<template>
    <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px"
               @close="close">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="配置名称" prop="configName">
                <el-input v-model.trim="form.configName" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
<!--            <el-form-item label="配置值" prop="configValue">-->
<!--                <el-input v-model.trim="form.configValue" autocomplete="off" placeholder="请输入"></el-input>-->
<!--            </el-form-item>-->
            <el-form-item label="配置标识" prop="configSign">
                <el-input v-model.trim="form.configSign" autocomplete="off" placeholder="请输入"></el-input>
            </el-form-item>
            <el-form-item label="设备类型" prop="deviceTypeId">
                <el-select v-model="form.deviceTypeId" placeholder="请选择" class="w"
                           @change="changeCollectData">
                    <el-option
                        v-for="item in this.deviceTypeList"
                        :key="item.id"
                        :label="item.deviceTypeName"
                        :value="item.id"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="设备" prop="deviceIdList">
                <el-select v-model="form.deviceIdList" placeholder="请选择" class="w"
                           multiple
                           filterable
                           default-first-option>
                    <el-option
                        v-for="item in this.deviceIdList"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="描述" prop="memo">
                <el-input type="textarea" v-model="form.memo" placeholder="请输入描述"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button :disabled="disabled" :loading="loading" type="primary" @click="save">
                {{ loading ? '确定中 ...' : '确定' }}
            </el-button>
        </div>
    </el-dialog>
</template>

<script>
import {saveOrUpdate} from '@/api/config'
import {getDeviceTypeList} from '@/api/configDeviceType'
import {getList as getDeviceCollectList} from "@/api/deviceCollect";

export default {
    name: 'dictEdit',
    data() {
        return {
            loading: false,
            disabled: false,
            deviceTypeList: null,
            deviceIdList: null,
            form: {
                id: '',
                deviceTypeId: '',
                deviceIdList: [],
                configName: '',
                configValue: '',
                configSign: '',
                memo: '',
            },
            radio: 0,
            rules: {
                deviceTypeId:[
                    {required: true, trigger: 'change', message: '请选择设备类型'},
                ],
                deviceIdList: [
                    {required: true, trigger: 'change', message: '请选择设备'},
                ],
                configName: [
                    {required: true, trigger: 'blur', message: '请输入配置名称'},
                ],
                // configValue: [
                //     {required: true, trigger: 'blur', message: '请输入配置值'},
                // ],
                configSign: [
                    {required: true, trigger: 'blur', message: '请输入配置标识'},
                ],
            },
            title: '',
            dialogFormVisible: false,
            options: [],
        }
    },
    created() {
        this.deviceTypeListData()
        this.getDeviceCollectList();
    },
    methods: {
        showEdit(row) {
            console.log(row)
            if (!row) {
                this.title = '添加'
            } else {
                this.title = '编辑'
                this.form = Object.assign({}, row)
            }
            this.dialogFormVisible = true
        },
        close() {
            this.$refs['form'].resetFields()
            this.form = this.$options.data().form
            this.dialogFormVisible = false
        },
        save() {
            if (this.loading) {
                return
            }
            this.$refs['form'].validate(async (valid) => {
                if (valid) {
                    this.loading = true
                    this.disabled = true
                    setTimeout(() => {
                        this.loading = false
                        this.disabled = false
                    }, 1000)
                    console.log(this.form)
                    const {msg} = await saveOrUpdate(this.form)
                    this.$baseMessage(msg, 'success')
                    this.$emit('fetch-data')
                    this.close()
                } else {
                    return false
                }
            })
        },
        async deviceTypeListData() {
            //所有设备类型信息
            const {data} = await getDeviceTypeList()
            this.deviceTypeList = data
        },
        async getDeviceCollectList() {
            //所有设备信息
            const {data} = await getDeviceCollectList()
            this.deviceIdList = data;
        },
        async changeCollectData() {
            this.form.deviceIdList = []
            var deviceType = this.form.deviceTypeId
            var buildId = this.form.buildId
            var floor = this.form.floor
            const {data} = await getDeviceCollectList({"deviceType": deviceType}, {"buildId": floor}, {"floorId": buildId})
            this.deviceIdList = data;
        },
    },
}
</script>
