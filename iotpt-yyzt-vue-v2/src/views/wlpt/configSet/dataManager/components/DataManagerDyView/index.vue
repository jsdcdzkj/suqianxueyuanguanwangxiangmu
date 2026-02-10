<template>
    <el-drawer :visible.sync="dialogFormVisible" size="1200px" @close="close" :before-close="handleClose">
        <span slot="title" class="drawer-title"><i class="el-icon-menu"></i><b>{{ title }}</b></span>
        <div style="padding: 0 20px;position: relative;" class="title-manager-view">
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
                <el-row :gutter="20">
                    <el-col :span="8">
                        <el-form-item label="模板名称" prop="modelName">
                            <el-input v-model.trim="form.modelName" placeholder="请输入模板名称" :disabled="true"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="模板编码" prop="modelCode">
                            <el-input v-model.trim="form.modelCode" placeholder="请输入模板编码" :disabled="true"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="聚合标识" prop="polymerization">
                            <el-input v-model.trim="form.polymerization" placeholder="请输入聚合标识" :disabled="true"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="模板描述" prop="modelDesc">
                            <el-input v-model.trim="form.modelDesc" type="textarea" placeholder="请输入模板描述"
                                      :disabled="true"/>
                        </el-form-item>
                    </el-col>
                </el-row>

            </el-form>
            <!--<el-button type="primary" size="mini" class="jiexi" @click="parseTest()">解析测试</el-button>-->
            <el-tabs v-model="activeName" @tab-click="handleClick" style="position: relative;">
                <!--<div class="tabs-self-top">-->
                <!--<div class="tips-title"><i class="el-icon-s-promotion"></i>转换数据</div>-->
                <!--<el-row :gutter="20" style="margin-bottom: 12px;">-->
                <!--<el-col :span="12">-->
                <!--<el-select v-model="configLinkId" placeholder="请选择连接" @change="linkChange">-->
                <!--<el-option-->
                <!--v-for="item in configLinkList"-->
                <!--:key="item.id"-->
                <!--:label="item.linkName"-->
                <!--:value="item.id">-->
                <!--</el-option>-->
                <!--</el-select>-->
                <!--</el-col>-->
                <!--<el-col :span="12">-->
                <!--<el-select v-model="configTopicId" placeholder="请选择主题" @change="topicChange">-->
                <!--<el-option-->
                <!--v-for="item in configTopicList"-->
                <!--:key="item.id"-->
                <!--:label="item.topicName"-->
                <!--:value="item.topicName">-->
                <!--</el-option>-->
                <!--</el-select>-->
                <!--</el-col>-->
                <!--</el-row>-->
                <!--<div style="margin-bottom: 12px;">-->
                <!--<el-button type="primary" size="mini" :loading="loading" @click="getDatas()">获取数据</el-button>-->
                <!--<el-button size="mini" @click="checkJson">格式校验</el-button>-->
                <!--</div>-->

                <!--<div>-->
                <!--<json-viewer :value="jsonData" :expand-depth=5 copyable boxed expanded></json-viewer>-->
                <!--</div>-->
                <!--</div>-->

                <el-tab-pane label="数据" name="shuju">
                    <shuju ref="shuju" :dataList="form.dataList" :templateField1="form.templateField1"
                           :configLinkList="configLinkList" :configTopicList="configTopicList"
                           @fetch-data="fetchData"></shuju>
                </el-tab-pane>
                <el-tab-pane label="告警" name="gaojing">
                    <gaojing ref="gaojing" :alarmList="form.alarmList" :templateField2="form.templateField2"
                             :configLinkList="configLinkList" :configTopicList="configTopicList"
                             @fetch-data="fetchData"></gaojing>
                </el-tab-pane>
                <el-tab-pane label="心跳" name="xintiao">
                    <xintiao ref="xintiao" :heartList="form.heartList" :templateField3="form.templateField3"
                             :configLinkList="configLinkList" :configTopicList="configTopicList"
                             @fetch-data="fetchData"></xintiao>
                </el-tab-pane>
                <el-tab-pane label="控制" name="kongzhi">
                    <kongzhi ref="kongzhi" :controlList="form.controlList" :templateField4="form.templateField4"
                             :configLinkList="configLinkList" :configTopicList="configTopicList"
                             @fetch-data="fetchData"></kongzhi>
                </el-tab-pane>
            </el-tabs>
        </div>
        <div class="footer-btn">
            <!--<el-button type="primary" @click="save" :loading="loading" :disabled="disabled">立即提交</el-button>-->
            <el-button @click="close">关闭</el-button>
        </div>
    </el-drawer>
</template>

<script>
// 订阅主题模板

import {getEntity, saveOrUpdate, getDatas, parseTest} from '@/api/configDataTransfer'

import {getConfigLinkList} from '@/api/configLink'
import {getConfigTopicList} from '@/api/configTopic'

import shuju from './shuju'
import gaojing from './gaojing'
import xintiao from './xintiao'
import kongzhi from './kongzhi'

export default {
    name: 'DataManagerView',
    components: {shuju, gaojing, xintiao,kongzhi},
    data() {
        return {
            activeName: 'shuju',
            configTopicId: '',
            configLinkId: '',
            form: {
                id: '',
                modelName: '',
                modelCode: '',
                modelType: '1',
                polymerization: '',
                modelDesc: '',
                configTopicId: '',
                dataList: [],
                alarmList: [],
                heartList: [],
                controlList: [],
                templateField1: {
                    id: null,
                    modelId: '',
                    type: '1',
                    templateKey: '',
                    alarmVal: '',
                    heartVal: '',
                    dataVal: '',
                },
                templateField2: {
                    id: null,
                    modelId: '',
                    type: '2',
                    templateKey: '',
                    alarmVal: '',
                    heartVal: '',
                    dataVal: '',
                },
                templateField3: {
                    id: null,
                    modelId: '',
                    type: '3',
                    templateKey: '',
                    alarmVal: '',
                    heartVal: '',
                    dataVal: '',
                },
                templateField4: {
                    id: null,
                    modelId: '',
                    type: '4',
                    templateKey: '',
                    alarmVal: '',
                    heartVal: '',
                    dataVal: '',
                },
                jsonData: '',
                dataType: ''
            },
            loading: false,
            disabled: false,
            sizeForm: {
                name: '默认文字',
            },
            ruleForm: {
                desc: '',
                time: '',
                radio: 1,
                radio2: 1,
                radio3: 1,
            },
            rules: {
                modelName: [
                    {required: true, trigger: 'blur', message: '请输入模板名称'},
                ],
                modelCode: [
                    {required: true, trigger: 'blur', message: '请输入模板编码'},
                ],
            },
            title: '',
            dialogFormVisible: false,
            systemKeyList1: ["gateWayId", "deviceId", "channelId", "val", "dataTime"],//1数据 系统字段
            systemKeyList2: ["gateWayId", "deviceId", "signalId", "alertTime", "val"],//2告警 系统字段
            systemKeyList3: ["device", "heartState", "heartTime"],//3心跳 系统字段
            systemKeyList4: ["gateWayId", "deviceId", "channelId", "val", "controlResult", "controlTime"],//4.控制 系统字段
            configLinkList: [],
            configTopicList: [],
            jsonData: {},
            // jsonData: {
            //   "list": [{
            //     "dev_addr": "58",
            //     "dev_name": "设备1",
            //     "tag_name": "测点0",
            //     "v": 60,
            //     "l": 10,
            //     "t": 0,
            //     "s": 0,
            //     "m": "低低报警"
            //   }, {
            //     "dev_addr": "58",
            //     "dev_name": "设备1",
            //     "tag_name": "测点0",
            //     "v": 2115,
            //     "l": 100,
            //     "t": 1,
            //     "s": 1,
            //     "m": "高高报警"
            //   }],
            //   "sn": "1021702119",
            //   "time": "2018-10-10 16:01:47",
            //   "type": "alarm"
            // },
            socket: '',
        }
    },
    created() {
    },
    methods: {
        async linkChange(v) {
            console.log(v);
            this.configTopicListData(v);
        },
        async topicChange(v) {
            console.log(v);
            this.linkWebSocket(window.btoa(v));
        },
        linkWebSocket(clientId) {
            if (typeof (WebSocket) == "undefined") {
                this.$baseMessage('您的浏览器不支持', 'error');
            } else {
                // var hostport = "ws://192.168.0.40:8104/websocket/" + clientId;
                var hostport = "ws://localhost:8086/websocket/" + clientId;
                // if (this.socket != '') {
                //   this.socket.onclose = function () {
                //     console.log("socket关闭事件222");
                //   };
                // }
                this.socket = new WebSocket(hostport);

                this.socket.onopen = this.setOnopenMessage;
                //获得消息事件
                this.socket.onmessage = this.setOnmessageMessage;
                this.socket.onclose = this.setOncloseMessage;
                this.socket.onerror = this.setErrorMessage
            }
        },
        setErrorMessage() {
            // 如果报错的话，在这里就可以重新初始化websocket，这就是断线重连

            console.log('WebSocket连接发生错误   状态码：')
        },
        setOnopenMessage() {
            console.log('WebSocket连接成功    状态码：')
        },
        setOnmessageMessage(event) {
            this.jsonData = event.data;
            console.log('服务端返回：' + event.data)

        },
        setOncloseMessage() {
            console.log('WebSocket连接关闭    状态码：')
        },
        async getDatas() {
            console.log(this.activeName)
            var type = "";
            if (this.activeName == "shuju") {
                type = "1";
            } else if (this.activeName == "gaojing") {
                type = "2";
            } else if (this.activeName == "xintiao") {
                type = "3";
            }else if (this.activeName=="kongzhi"){
                type = "4";
            }
            console.log(this.configLinkId)
            if (this.configLinkId == "" || this.configLinkId == null || this.configLinkId == undefined) {
                this.$baseMessage('请选择连接', 'error')
                return
            }
            if (this.configTopicId == "" || this.configTopicId == null || this.configTopicId == undefined) {
                this.$baseMessage('请选择主题', 'error')
                return
            }
            this.jsonData = {};
            const {data} = await getDatas({
                linkId: this.configLinkId,
                type: type,
                topic: this.configTopicId,
                online: 0
            });
            console.log(data)
            this.$baseMessage('请求成功', 'success')
        },
        checkJson() {
            this.jsonData = JSON.parse(this.jsonData);
        },
        handleClose(done) {
            if (this.loading) {
                return
            }
            // this.loading = true;
            this.timer = setTimeout(() => {
                done();
                // 动画关闭需要一定的时间
                // setTimeout(() => {
                //   this.loading = false
                // }, 400)
            }, 500)

            // this.$confirm('确定要提交表单吗？')
            //   .then((_) => {
            //     this.loading = true
            //     this.timer = setTimeout(() => {
            //       done()
            //       // 动画关闭需要一定的时间
            //       setTimeout(() => {
            //         this.loading = false
            //       }, 400)
            //     }, 2000)
            //   })
            //   .catch((_) => {})
        },
        showEdit(row) {
            if (!row) {
                this.title = '详情'
            } else {
                this.title = '详情';
                // this.form = Object.assign({}, row)
                this.getData(row);
            }

            if (this.form.dataList.length == 0) {
                for (var i = 0; i < 5; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList1[i];
                    this.form.dataList.push(obj);
                }
            }
            if (this.form.alarmList.length == 0) {
                for (var i = 0; i < 5; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList2[i];
                    this.form.alarmList.push(obj);
                }
            }
            if (this.form.heartList.length == 0) {
                for (var i = 0; i < 3; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList3[i];
                    this.form.heartList.push(obj);
                }
            }

            if (this.form.controlList.length == 0) {
                for (var i = 0; i < 6; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList4[i];
                    this.form.controlList.push(obj);
                }
            }

            this.configLinkListData();
            // this.configTopicListData();

            this.dialogFormVisible = true
        },
        //主题管理关联模板新增模板弹窗
        showEdit2(configTopicId) {
            this.title = '添加'
            this.form.configTopicId = configTopicId;
            console.log(configTopicId)
            console.log(this.form.configTopicId)
            if (this.form.dataList.length == 0) {
                for (var i = 0; i < 5; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList1[i];
                    this.form.dataList.push(obj);
                }
            }
            if (this.form.alarmList.length == 0) {
                for (var i = 0; i < 5; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList2[i];
                    this.form.alarmList.push(obj);
                }
            }
            if (this.form.heartList.length == 0) {
                for (var i = 0; i < 3; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList3[i];
                    this.form.heartList.push(obj);
                }
            }

            if (this.form.controlList.length == 0) {
                for (var i = 0; i < 6; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList4[i];
                    this.form.controlList.push(obj);
                }
            }

            this.configLinkListData();
            this.configTopicListData();

            this.dialogFormVisible = true
        },
        handleClick(tab, event) {
            this.jsonData = {};
            console.log(tab, event)
        },
        async parseTest() {
            this.jsonData = {
                "gateWayId": "666888",
                "sendTime": "2023-07-03 14:13:35.531",
                "data": [{
                    "channelId": "tongxunmozu",
                    "deviceId": "kongkai",
                    "point": "switch_on",
                    "type": "1",
                    "value": 0,
                    "timestamp": "2023-07-03 14:13:35.531"
                }, {
                    "channelId": "tongxunmozu",
                    "deviceId": "kongkai",
                    "point": "switch_off",
                    "type": "1",
                    "value": 0,
                    "timestamp": "2023-07-03 14:13:35.531"
                }, {
                    "channelId": "tongxunmozu",
                    "deviceId": "kongkai",
                    "point": "temp_A",
                    "type": "1",
                    "value": 0,
                    "timestamp": "2023-07-03 14:13:35.531"
                }, {
                    "channelId": "tongxunmozu",
                    "deviceId": "kongkai",
                    "point": "temp_B",
                    "type": "1",
                    "value": 0,
                    "timestamp": "2023-07-03 14:13:35.531"
                }, {
                    "channelId": "tongxunmozu",
                    "deviceId": "kongkai",
                    "point": "temp_C",
                    "type": "1",
                    "value": 0,
                    "timestamp": "2023-07-03 14:13:35.531"
                }, {
                    "channelId": "tongxunmozu",
                    "deviceId": "kongkai",
                    "point": "Electric_current_A",
                    "type": "1",
                    "value": 0,
                    "timestamp": "2023-07-03 14:13:35.531"
                }, {
                    "channelId": "tongxunmozu",
                    "deviceId": "kongkai",
                    "point": "Electric_current_B",
                    "type": "1",
                    "value": 0,
                    "timestamp": "2023-07-03 14:13:35.531"
                }, {
                    "channelId": "tongxunmozu",
                    "deviceId": "kongkai",
                    "point": "Electric_current_C",
                    "type": "1",
                    "value": 0,
                    "timestamp": "2023-07-03 14:13:35.531"
                }, {
                    "channelId": "tongxunmozu",
                    "deviceId": "kongkai",
                    "point": "rate",
                    "type": "1",
                    "value": 0,
                    "timestamp": "2023-07-03 14:13:35.531"
                }]
            }
            if (JSON.stringify(this.jsonData) == '{}' || this.jsonData == '{}') {
                this.$baseMessage('请先获取数据', 'error')
                return
            }

            var type = "";
            if (this.activeName == "shuju") {
                type = "1";
            } else if (this.activeName == "gaojing") {
                type = "2";
            } else if (this.activeName == "xintiao") {
                type = "3";
            }else if (this.activeName == "kongzhi") {
                type = "4";
            }
            this.form.dataType = type
            if (typeof this.jsonData == "string") {
                this.form.jsonData = this.jsonData;
            } else {
                this.form.jsonData = JSON.stringify(this.jsonData)
            }

            const {msg} = await parseTest(this.form)
            this.$baseMessage(msg, 'success')
        },
        async configLinkListData() {
            const {data} = await getConfigLinkList();
            this.configLinkList = data;
        },
        async configTopicListData(linkId) {
            const {data} = await getConfigTopicList({linkId: linkId});
            this.configTopicList = data;
        },
        close() {
            this.$refs['form'].resetFields()
            this.form = this.$options.data().form

            if (this.form.dataList.length == 0) {
                for (var i = 0; i < 5; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList1[i];
                    this.form.dataList.push(obj);
                }
            }
            if (this.form.alarmList.length == 0) {
                for (var i = 0; i < 5; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList2[i];
                    this.form.alarmList.push(obj);
                }
            }
            if (this.form.heartList.length == 0) {
                for (var i = 0; i < 3; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList3[i];
                    this.form.heartList.push(obj);
                }
            }


            if (this.form.controlList.length == 0) {
                for (var i = 0; i < 6; i++) {
                    var obj = {
                        id: null,
                        systemKey: '',
                        gatewayKey: '',
                    };
                    obj.systemKey = this.systemKeyList4[i];
                    this.form.controlList.push(obj);
                }
            }

            this.dialogFormVisible = false
        },
        async getData(id) {
            const {data} = await getEntity({id: id});
            console.log(data)
            this.form = data;
        },
        save() {
            console.log(this.form.dataList)

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
            // this.$refs[this.activeName].save()
        },
        async fetchData() {
        },
    },
}
</script>
<style scoped lang="scss">
.jiexi {
    position: absolute;
    right: 21px;
    top: 123px;
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
<style lang="scss">
// .el-form  {
//   margin-top: -23px !important;
// }

// .el-form-item  {
//   margin-bottom: 0px!important;
//   position: relative;
//   top: 12px;
// }
</style>
