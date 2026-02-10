<template>
    <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px"
               @close="close">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="主题名称" prop="topicCode">
                <el-input v-model.trim="form.topicCode" autocomplete="off" placeholder="请输入主题名称"></el-input>
            </el-form-item>
            <el-form-item label="主题Topic" prop="topicName">
                <el-input v-model.trim="form.topicName" autocomplete="off" placeholder="请输入主题Topic"></el-input>
            </el-form-item>
            <el-form-item label="主题类型" prop="topicType">
                <el-select v-model="form.topicType" placeholder="请选择主题类型" class="w" @change="typeChange">
                    <el-option label="订阅主题" :value="1"></el-option>
                    <el-option label="发布主题" :value="2"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="连接地址" prop="linkId">
                <el-select v-model="form.linkId" placeholder="请选择连接地址" class="w">
                    <el-option
                        v-for="item in this.linkList"
                        :key="item.id"
                        :label="item.linkName"
                        :value="item.id"
                    ></el-option>
                </el-select>
            </el-form-item>
            <!--<el-form-item label="主题状态" prop="topicStatus">-->
            <!--<el-radio-group v-model="form.topicStatus">-->
            <!--<el-radio :label="0">禁用</el-radio>-->
            <!--<el-radio :label="1">启用</el-radio>-->
            <!--</el-radio-group>-->
            <!--</el-form-item>-->
            <el-form-item label="数据识别码" prop="templateKey" v-model="isShow" v-if="isShow==true">
                <el-input v-model.trim="form.templateKey" autocomplete="off" placeholder="请输入数据识别码"></el-input>
            </el-form-item>
            <el-form-item label="数据码值" prop="dataVal" v-model="isShow" v-if="isShow==true">
                <el-input v-model.trim="form.dataVal" autocomplete="off" placeholder="请输入数据码值"></el-input>
            </el-form-item>
            <el-form-item label="告警码值" prop="alarmVal" v-model="isShow" v-if="isShow==true">
                <el-input v-model.trim="form.alarmVal" autocomplete="off" placeholder="请输入告警码值"></el-input>
            </el-form-item>
            <el-form-item label="心跳码值" prop="heartVal" v-model="isShow" v-if="isShow==true">
                <el-input v-model.trim="form.heartVal" autocomplete="off" placeholder="请输入心跳码值"></el-input>
            </el-form-item>
            <el-form-item label="变更码值" prop="changeVal" v-model="isShow" v-if="isShow==true">
                <el-input v-model.trim="form.changeVal" autocomplete="off" placeholder="请输入变更码值"></el-input>
            </el-form-item>
            <el-form-item label="控制码值" prop="controlVal" v-model="isShow" v-if="isShow==true">
                <el-input v-model.trim="form.controlVal" autocomplete="off" placeholder="请输入控制码值"></el-input>
            </el-form-item>
            <el-form-item label="主题描述" prop="topicDesc">
                <el-input type="textarea" v-model="form.topicDesc" placeholder="请输入主题描述"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">
                {{ loading ? '确定中 ...' : '确定' }}
            </el-button>
        </div>
    </el-dialog>
</template>

<script>
    import {getEntity, saveOrUpdate} from '@/api/configTopic'
    import {getList} from '@/api/configLink'

    export default {
        name: 'titleManagerEdit',
        components: {},
        data() {
            return {
                loading: false,
                disabled: false,
                isShow: true,
                form: {
                    id: '',
                    topicName: '',
                    topicCode: '',
                    linkId: '',
                    topicType: 1,
                    // topicStatus: 1,
                    topicDesc: '',
                    templateKey: '',
                    dataVal: '',
                    alarmVal: '',
                    heartVal: '',
                    controlVal:'',
                    changeVal:''
                },
                rules: {
                    topicName: [
                        {required: true, trigger: 'blur', message: '请输入主题Topic'},
                    ],
                    topicCode: [
                        {required: true, trigger: 'blur', message: '请输入主题名称'},
                    ],
                    topicType: [
                        {required: true, trigger: 'change', message: '请选择主题类型'},
                    ],
                    linkId: [
                        {required: true, trigger: 'change', message: '请选择连接地址'},
                    ],
                    // topicStatus: [
                    //   {required: true, trigger: 'blur', message: '请选择主题状态'},
                    // ],
                    // topicDesc: [
                    //   {required: true, trigger: 'submit', message: '请输入主题描述'},
                    // ],
                },
                title: '',
                dialogFormVisible: false,
                linkList: [],
            }
        },
        created() {
        },
        methods: {
            async typeChange(v) {
                if (v == 1) {
                    this.isShow = true;
                } else {
                    this.isShow = false;
                }
            },
            showEdit(row) {
                if (!row) {
                    this.title = '添加'
                    this.isShow = true;
                } else {
                    this.title = '编辑';
                    // this.form = Object.assign({}, row)
                    this.getData(row)
                }
                //连接列表
                this.linkListData();
                this.dialogFormVisible = true

                this.$nextTick(() => {
                    if (this.$refs['form']) {
                        this.$refs['form'].resetFields()
                    }
                });
            },
            close() {
                this.$refs['form'].resetFields();
                this.form = this.$options.data().form;
                this.dialogFormVisible = false
            },
            async linkListData() {
                const {data} = await getList()
                console.log(data)
                this.linkList = data;
            },
            async getData(id) {
                const {data} = await getEntity({id: id});
                this.form = data;
                if (this.form.topicType == 1) {
                    this.isShow = true;
                } else {
                    this.isShow = false;
                }
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

                        const {msg} = await saveOrUpdate(this.form)
                        this.$baseMessage(msg, 'success')
                        this.$emit('fetch-data')
                        this.close()
                    } else {
                        return false
                    }
                })
            },
        },
    }
</script>
