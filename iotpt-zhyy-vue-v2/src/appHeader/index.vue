<template>
    <div class="app-header--container">
        <div class="header-container--left">
            <div class="logo">
                <img :src="logoImg" alt="" srcset="" />
            </div>
<!--            <div class="content">-->
<!--                 <div class="name">智慧鼎驰大厦</div>-->
<!--                <div class="content-nav__link">-->
<!--                    <div v-for="(item, index) in permissions" :key="index">-->
<!--                        <div-->
<!--                            class="link"-->
<!--                            v-if="!item.hidden"-->
<!--                            @click="changeActiveIndex(index, item)"-->
<!--                            :class="{ active: activeIndex === index }"-->
<!--                        >-->
<!--                            <div class="link-sub">-->
<!--                                <vab-icon :icon="['fas', item.iconName]" />-->
<!--                                &lt;!&ndash; <img :src="item.icon" alt="" srcset="" /> &ndash;&gt;-->
<!--                            </div>-->
<!--                            <div class="link-text">{{ item.name }}</div>-->
<!--                        </div>-->
<!--                    </div>-->

<!--                </div>-->
<!--            </div>-->
        </div>

        <div class="header-container--right">
            <!--            <div class="right-menu tabMenu" @click="gourl('http://smartpark.dincher.cn/jsc/')">-->
            <!--                <i class="el-icon-sort"></i>-->
            <!--                驾驶舱-->
            <!--            </div>-->
            <div class="time">{{ currentTime }}</div>
            <div class="weather">
                <Weather v-if="isProduct"></Weather>
            </div>

            <div class="right-menu">
                <el-dropdown class="avatar-container" trigger="click">
                    <div class="avatar-wrapper">
                        <div class="user-avatar"></div>
                        <div class="user-name">{{ name || "" }}</div>
                        <i class="el-icon-caret-bottom" />
                    </div>
                    <el-dropdown-menu slot="dropdown" class="user-dropdown">
                        <!-- 密码修改 -->
                        <el-dropdown-item @click.native="dialogVisible = true">密码修改</el-dropdown-item>
                        <el-dropdown-item divided @click.native="logout">
                            <span style="display: block">退出登录</span>
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </div>
        <!-- 密码修改 弹窗 -->
        <el-dialog title="修改密码" :visible.sync="dialogVisible" append-to-body width="500px">
            <el-form :model="form" :rules="rules" ref="form" label-width="80px" class="demo-ruleForm">
                <el-form-item label="原密码" prop="oldPassWord">
                    <el-input
                        type="password"
                        v-model="form.oldPassWord"
                        autocomplete="off"
                        placeholder="请输入原密码"
                        show-password
                    ></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="password">
                    <el-input
                        type="password"
                        v-model="form.password"
                        autocomplete="off"
                        placeholder="密码需包含大小写字母特殊符号并且长度大于等于8位"
                        show-password
                    ></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input
                        type="password"
                        v-model="form.confirmPassword"
                        autocomplete="off"
                        placeholder="密码需包含大小写字母特殊符号并且长度大于等于8位"
                        show-password
                    ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm('form')">确 定</el-button>
            </div>
        </el-dialog>

        <!-- 告警弹窗 -->
        <div
            v-if="windowInfo.mode == 1 && windowInfo.times > 0"
            class="alertWindow"
            @click="toWarnList(windowInfo.warnCategory, windowInfo.id)"
        >
            <div class="window-header">
                <div style="display: flex; align-items: center">
                    <el-badge
                        :value="windowInfo.times"
                        :max="99"
                        @click.native.stop="toWarnList(windowInfo.warnCategory)"
                    >
                        <i class="el-icon-warning" />
                    </el-badge>
                    <span style="font-size: 16px; font-weight: bold; margin-left: 10px; color: rgba(0, 0, 0, 0.85)">
                        {{ windowInfo.warnTypeName }}
                    </span>
                </div>
                <div>
                    <el-tag style="margin-right: 16px" effect="plain" type="warning">等待处理</el-tag>
                    <i class="el-icon-close" @click.stop="closeWindow(2)" />
                </div>
            </div>

            <div class="window_right">
                <!-- <div class="title">告警区域：{{ windowInfo.areaName }}</div> -->
                <div class="window_content">
                    <div v-if="windowInfo.warnContent">告警内容：{{ windowInfo.warnContent }}</div>
                    <div>上报时间：{{ windowInfo.alertTime }}</div>
                    <div>告警位置：{{ windowInfo.areaName }}</div>
                    <!-- <div>设备名称：{{ windowInfo.deviceName }}</div>
                    <div>告警类型：{{ windowInfo.warnTypeName }}</div>
                    <div>告警时间：{{ windowInfo.alertTime }}</div> -->
                </div>
                <div class="window-warn-img">
                    <template v-if="windowInfo.warnLevel == 4">
                        <img src="@/assets/img/alarmEvent/warn-red.png" alt="" />
                        <span class="red">紧急</span>
                    </template>
                    <template v-else-if="windowInfo.warnLevel == 1">
                        <img src="@/assets/img/alarmEvent/warn-blue.png" alt="" />
                        <span class="blue">警告</span>
                    </template>
                    <template v-else-if="windowInfo.warnLevel == 2">
                        <img src="@/assets/img/alarmEvent/warn-green.png" alt="" />
                        <span class="green">次要</span>
                    </template>
                    <template v-else-if="windowInfo.warnLevel == 3">
                        <img src="@/assets/img/alarmEvent/warn-orange.png" alt="" />
                        <span class="orange">重要</span>
                    </template>
                </div>
            </div>
        </div>
        <div v-if="windowInfo.mode == 2 && windowInfo.times > 0" class="dotWindow" @click="closeWindow(1)">
            <el-badge :value="windowInfo.times" :max="99">
                <i class="el-icon-warning" />
            </el-badge>
        </div>
    </div>
</template>

<script>
/* eslint-disable */
import store from "@/store";
import { time } from "@/utils/index";
import Weather from "./tianqi.vue";
import { mapGetters, mapState } from "vuex";
import logoImg from "@/assets/img/logo.png";

import { getWarning, upeWarning, changePwd } from "@/api/user";
const VUE_WEBSOCKET_URL = process.env.VUE_APP_WEBSOCKET_URL;
let ws, timer;
export default {
    components: { Weather },
    data() {
        return {
            isProduct: process.env.NODE_ENV == "production",
            logoImg,
            activeIndex: Number(localStorage.getItem("alreadyRoutes__template__index") || 0),
            links: [],
            currentTime: "",
            interval: null,
            dialogVisible: false,
            warningId: -1,
            notify: null,
            form: {
                oldPassWord: "",
                password: "",
                confirmPassword: ""
            },
            rules: {
                oldPassWord: [{ required: true, message: "请输入原密码", trigger: "blur" }],
                password: [
                    {
                        required: true,

                        validator: (rule, value, callback) => {
                            if (!value) {
                                return callback(new Error("请输入密码"));
                            }
                            if (
                                /^(?![A-Z]+$)(?![a-z]+$)(?!\d+$)(?![\W_]+$)\S{8,16}$/.test(value) &&
                                value.length >= 8
                            ) {
                                callback();
                            } else {
                                callback(new Error("密码需包含大小写字母特殊符号并且长度大于等于8位"));
                            }
                        },
                        trigger: "blur"
                    }
                ],
                confirmPassword: [
                    {
                        required: true,
                        validator: (rule, value, callback) => {
                            if (!value) {
                                return callback(new Error("请输入确认密码"));
                            }
                            if (
                                /^(?![A-Z]+$)(?![a-z]+$)(?!\d+$)(?![\W_]+$)\S{8,16}$/.test(value) &&
                                value.length >= 8
                            ) {
                                callback();
                            } else {
                                callback(new Error("密码需包含大小写字母特殊符号并且长度大于等于8位"));
                            }
                        },
                        trigger: "blur"
                    }
                ]
            },
            windowInfo: {
                times: 0,
                mode: 1
            },
            closeReason: "",
            againTimes: 0
        };
    },
    computed: {
        ...mapGetters(["avatar", "name", "userId"]),
        ...mapState({
            permissions: (state) => state.user.permissions,
            isNeedUpdatePwd: (state) => state.user.isNeedUpdatePwd
        })
    },
    mounted() {
        this.interval = setInterval(() => {
            this.currentTime = time();
        }, 1000);
    },
    created() {
        //this.openWarning();
        this.initWebscoket();
    },
    watch: {
        dialogVisible(val) {
            if (!val) {
                this.form = this.$options.data().form;
            }
        },
        isNeedUpdatePwd: {
            handler(val) {
                console.log("@132423i523yi", val);
                this.dialogVisible = val;
            },
            immediate: true
        }
    },
    methods: {
        async openWarning() {
            const that = this;
            const res = await getWarning();
            if (res.data && res.data.id) {
                if (that.warningId != res.data.id) {
                    let msg = '<b style="color: #F56C6C">告警中</b></br>';
                    msg += "<strong><i>" + res.data.warnTypeName + "</i></strong></br>";
                    msg += res.data.point ? res.data.point + '"</br>"' : "";
                    msg += res.data.warnContent == null ? "" : res.data.warnContent + "</br>";
                    that.notify = this.$notify({
                        title: res.data.deviceName,
                        message: msg,
                        dangerouslyUseHTMLString: true,
                        duration: 0,
                        position: "bottom-right",
                        onClose: function () {
                            that.$nextTick(() => {
                                if (that.notify) {
                                    upeWarning({ id: res.data.id, zhyyRead: "1" });
                                    that.warningId = res.data.id;
                                    that.openWarning();
                                }
                            });
                        }
                    });
                }
            }
        },
        initWebscoket() {
            if (!VUE_WEBSOCKET_URL) {
                setTimeout(this.initWebscoket, 500);
                return;
            }
            this.againTimes++;
            var that = this;
            ws = new WebSocket(VUE_WEBSOCKET_URL + "/" + btoa(that.userId));
            ws.onopen = function () {
                console.log("websocket连接成功");
                that.closeReason = "";
                that.againTimes = 0;
                timer = setInterval(() => {
                    ws.send(btoa(that.userId));
                }, 5000);
            };
            ws.onmessage = function (e) {
                let _data = "";
                try {
                    _data = JSON.parse(e.data);
                } catch (e) {}
                if (!_data) return;
                console.log("接收数据", _data);
                that.windowInfo = Object.assign(_data, {
                    times: that.windowInfo.times + 1,
                    mode: that.windowInfo.mode
                });
            };
            //当客户端收到服务端发送的关闭连接请求时，触发onclose事件
            ws.onclose = function () {
                ws = null;
                timer && clearInterval(timer);
                timer = null;
                if (!["leave", "close"].includes(that.closeReason)) {
                    if (that.againTimes < 6) {
                        console.log("重新连接websocket");
                        that.initWebscoket();
                    }
                }
            };
        },
        toWarnList(type, id) {
            // if ([1, 2, 3].includes(Number(type))) {
            //     this.$router.push("/warningInfo");
            // } else {
            //     this.$router.push("/intelligentSecurity/realTime");
            // }
            // 告警事件
            this.windowInfo = { times: 0, mode: 1 };
            this.changeActiveIndex(2, {
                routes: this.permissions[2].routes,
                homeRoutePath: "/intelligentSecurity"
            });
            this.$router.push("/intelligentSecurity/alarmEvent");
            /* if (id) {
                this.$router.push('/intelligentSecurity/realTime?id=' + id)
            } else {
                this.$router.push('/intelligentSecurity/realTime')
            } */
        },
        closeWindow(val) {
            this.windowInfo.mode = val;
        },
        gourl(url) {
            // let path = window.location.protocol + "//" + url;
            // window.open(path,'_blank')
            window.location.href = url;
        },

        changeActiveIndex(index, item) {
            this.activeIndex = index;
            store.dispatch("app/toggleRoutes", item.routes);
            localStorage.setItem("alreadyRoutes__template__index", index);
            this.$router.push({
                path: item.homeRoutePath
            });
        },
        changeActiveIndex2(index) {
            this.$router.push({
                path: index == 1 ? "/prospectiveclient" : "/investmentProperties"
            });
        },
        async logout() {
            this.$baseConfirm("您确定要退出智慧应用平台吗?", null, async () => {
                await this.$store.dispatch("user/logout");

                this.$router.push(`/login`);
                // this.$router.push(`/login?redirect=${this.$route.fullPath}`);
            });
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    if (this.form.password != this.form.confirmPassword) {
                        this.$message.error("两次密码输入不一致");
                        return;
                    }
                    changePwd(this.form).then((res) => {
                        if (res.code === 0) {
                            this.$message.success("密码修改成功");
                            this.$store.dispatch("user/resetToken").then(() => {
                                this.$router.push(`/login`);
                            });
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                }
            });
        }
    },
    beforeDestroy() {
        clearInterval(this.interval);
        this.closeReason = "leave";
        ws && ws.close();
        timer && clearInterval(timer);
        timer = null;
    }
};
</script>

<style lang="scss" scoped>
.alertWindow {
    position: fixed;
    right: 16px;
    bottom: 16px;
    background: #fff;
    // display: flex;
    width: 364px;
    padding: 14px 26px 14px 13px;
    border-radius: 8px;
    box-sizing: border-box;
    border: 1px solid #ebeef5;
    z-index: 2000;
    overflow: hidden;
    cursor: pointer;

    &::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        width: 100%;
        height: 4px;
        background: #faab0c;
    }

    .el-icon-warning {
        color: #e6a23c;
        font-size: 24px;
    }

    .el-icon-close {
        position: absolute;
        top: 18px;
        right: 15px;
        cursor: pointer;
        color: #909399;
        font-size: 16px;
    }

    .window-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding-bottom: 16px;
        margin-bottom: 16px;
        border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    }

    .window_right {
        flex: 1;
        margin-left: 16px;
        margin-right: 8px;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title {
            font-weight: 700;
            font-size: 16px;
            color: #303133;
            margin: 0;
        }

        .window_content {
            font-size: 14px;
            line-height: 32px;
            margin: 6px 0 0;
            color: rgba(0, 0, 0, 0.85);
            text-align: justify;
        }

        .window-warn-img {
            width: 74px;
            height: 74px;
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            color: #e6a23c;

            .blue {
                color: #1e95f4;
            }

            .red {
                color: #f56c6c;
            }

            .green {
                color: #67c23a;
            }

            img {
                position: absolute;
                width: 100%;
                height: 100%;
                top: 0;
                left: 0;
                z-index: -1;
            }
        }
    }
}

.dotWindow {
    position: fixed;
    bottom: 10px;
    right: 12px;
    z-index: 2000;
    color: #fff;
    cursor: pointer;

    i {
        color: #f56c6c;
        font-size: 32px;
    }
}

.app-header--container {
    display: flex;
    justify-content: space-between;
    width: 100%;
    height: 100%;
    background-color: #18223d;
    color: rgba(255, 255, 255, 0.9);
    padding-right: 0px;
    font-size: 14px;

    .header-container--left {
        display: flex;
        width: 100%;
        height: 100%;

        .logo {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 140px;
            height: 100%;
            cursor: pointer;
        }

        .content {
            display: flex;
            align-items: center;

            .name {
                padding: 0 10px;
                font-size: 20px;
                font-weight: 700;
            }

            .content-nav__link {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-left: 20px;

                .link {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    padding: 0 20px;

                    height: 35px;
                    margin-right: 0px;
                    cursor: pointer;

                    &:hover {
                        background-color: rgba(255, 255, 255, 0.1);
                        border-radius: 20px;
                    }

                    &.active {
                        background-color: rgba(255, 255, 255, 0.1);
                        border-radius: 20px;
                    }

                    .link-sub {
                        margin-right: 5px;
                    }

                    .link-text {
                        font-size: 16px;
                    }
                }
            }
        }
    }

    .header-container--right {
        display: flex;
        align-items: center;

        .tabMenu {
            width: 120px;
            line-height: 50px;
            cursor: pointer;
            font-size: 16px;
            position: relative;
            margin-right: 20px;

            i {
                margin-right: 5px;
                transform: rotate(90deg);
            }

            &::after {
                content: "";
                position: absolute;
                right: 0;
                top: 50%;
                transform: translateY(-50%);
                height: 20px;
                width: 2px;
                background-color: rgba(255, 255, 255, 0.2);
            }
        }

        .time {
            flex-shrink: 0;
            padding-right: 20px;
        }

        .weather {
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 316px;
            height: 30px;
            padding: 0 20px;
            margin-right: 20px;

            &::after {
                content: "";
                position: absolute;
                left: 0;
                top: 50%;
                transform: translateY(-50%);
                height: 20px;
                width: 2px;
                background-color: rgba(255, 255, 255, 0.2);
            }

            &::before {
                content: "";
                position: absolute;
                right: 0;
                top: 50%;
                transform: translateY(-50%);
                height: 20px;
                width: 2px;
                background-color: rgba(255, 255, 255, 0.2);
            }
        }
    }

    .right-menu {
        height: 100%;
        margin-left: 20px;

        ::v-deep .el-dropdown {
            color: #fff;
        }

        &:focus {
            outline: none;
        }

        .right-menu-item {
            display: inline-block;
            padding: 0 8px;
            height: 100%;
            font-size: 18px;
            color: #5a5e66;
            vertical-align: text-bottom;

            &.hover-effect {
                cursor: pointer;
                transition: background 0.3s;

                &:hover {
                    background: rgba(0, 0, 0, 0.025);
                }
            }
        }

        .avatar-container {
            margin-right: 30px;

            .avatar-wrapper {
                display: flex;
                align-items: center;
                margin-top: 5px;
                position: relative;
                cursor: pointer;

                .user-name {
                    margin: 0 10px;
                }

                .user-avatar {
                    cursor: pointer;
                    width: 0px;
                    height: 40px;
                    border-radius: 10px;
                }

                .el-icon-caret-bottom {
                    cursor: pointer;
                    font-size: 12px;
                }
            }
        }
    }
}
</style>
