<template>
    <div class="login-container">
        <sbanner></sbanner>
        <div class="logo">
            <el-image :src="require('@/assets/img/logo.png')"></el-image>
        </div>
        <div class="text-wrap">
            <div class="word">
                <span>聚力前行</span>
                <span>同心致远</span>
            </div>
        </div>
        <div class="login-wrap">
            <div class="formbox">
                <dv-border-box-1>
                    <div class="mainform">
                        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form"
                            auto-complete="on" label-position="left">
                            <div class="title-tips">欢迎来到{{ title }}！</div>
                            <el-form-item style="margin-top: 40px" prop="username">
                                <span class="svg-container svg-container-admin">
                                    <svg-icon icon-class="user" />
                                </span>
                                <el-input ref="username" v-model="loginForm.username" placeholder="Username"
                                    name="username" type="text" tabindex="1" auto-complete="on" />
                            </el-form-item>
                            <el-form-item prop="password">
                                <span class="svg-container">
                                    <svg-icon icon-class="password" />
                                </span>
                                <el-input :key="passwordType" ref="password" v-model="loginForm.password"
                                    :type="passwordType" placeholder="Password" name="password" tabindex="2"
                                    auto-complete="on" @keyup.enter.native="handleLogin" />
                                <span class="show-password" @click="showPwd">
                                    <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
                                </span>
                            </el-form-item>
                            <el-button class="login-btn" :loading="loading" type="primary"
                                style="width: 100%; margin-bottom: 30px" @click.native.prevent="handleLogin">
                                登录
                            </el-button>
                        </el-form>
                    </div>
                </dv-border-box-1>
            </div>
        </div>
        <!-- <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on" label-position="left">

      <div class="title-container">
        <h3 class="title">Login Form</h3>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="username"
          v-model="loginForm.username"
          placeholder="Username"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
      </el-form-item>

      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="passwordType"
          ref="password"
          v-model="loginForm.password"
          :type="passwordType"
          placeholder="Password"
          name="password"
          tabindex="2"
          auto-complete="on"
          @keyup.enter.native="handleLogin"
        />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">Login</el-button>

      <div class="tips">
        <span style="margin-right:20px;">username: admin</span>
        <span> password: any</span>
      </div>

    </el-form> -->
    </div>
</template>

<script>
import { validUsername } from "@/utils/validate";
import sbanner from "@/components/bgBanner";
import store from "@/store";
export default {
    name: "Login",
    components: { sbanner },
    data() {
        const validateUsername = (rule, value, callback) => {
            // if (!validUsername(value)) {
            //   callback(new Error('请输入正确的用户名'))
            // } else {
            //   callback()
            // }
            if ("" == value) {
                callback(new Error("用户名不能为空"));
            } else {
                callback();
            }
        };
        const validatePassword = (rule, value, callback) => {
            if (value.length < 6) {
                callback(new Error("密码不能少于6位"));
            } else {
                callback();
            }
        };
        return {
            loginForm: {
                username: "",
                password: "",
                cardinfo: '',
                type: '1'
            },
            loginRules: {
                username: [{ required: true, trigger: "blur", validator: validateUsername }],
                password: [{ required: true, trigger: "blur", validator: validatePassword }]
            },
            loading: false,
            passwordType: "password",
            redirect: undefined,
            title: "园区智慧应用平台",
            count: 0
        };
    },
    watch: {
        $route: {
            handler: function (route) {
                this.redirect = route.query && route.query.redirect;
            },
            immediate: true
        }
    },
    mounted() {
        console.log("11111====")
        window.addEventListener("message", this.handleMessage);
    },
    methods: {
        showPwd() {
            if (this.passwordType === "password") {
                this.passwordType = "";
            } else {
                this.passwordType = "password";
            }
            this.$nextTick(() => {
                this.$refs.password.focus();
            });
        },
        handleMessage(e) {
            var that = this;
            if ("portal" == e.data.type) {
                var cardinfo = encodeURIComponent(e.data.cardInfo);
                if (cardinfo) {
                    that.loginForm.type = "2";
                    that.loginForm.cardinfo = cardinfo;
                    console.log("33333====")
                    that.handleLogin_auto();
                } else {
                    that.loginForm.type = "1";
                    console.log("4444====")
                }
            }
        },
        handleLogin_auto() {
            this.loading = true
            var that = this;

            if (this.count != 0) {
                return;
            }
            console.log("555====")
            this.count++;
            this.$store
                .dispatch("user/login", this.loginForm)
                .then((res) => {
                    this.$store.dispatch("user/getInfo").then((currentRouter) => {
                        if (currentRouter) {
                            this.$store.dispatch("app/toggleRoutes", currentRouter.routes);
                            this.$router.replace({ path: currentRouter.homeRoutePath });
                        } else {
                            this.$router.replace({ path: "/noAnyPath" });
                            // this.$store.dispatch("")
                        }
                    });
                    window.removeEventListener('message', that.handleMessage);
                })
                .catch(() => {
                    this.loading = false;
                });
        },
        handleLogin() {
            this.$refs.loginForm.validate((valid) => {
                if (valid) {
                    this.loading = true;
                    setTimeout(() => {
                        this.loading = false;
                    }, 800);
                    this.$store
                        .dispatch("user/login", this.loginForm)
                        .then((res) => {
                            this.$store.dispatch("user/getInfo").then((currentRouter) => {
                                if (currentRouter) {
                                    this.$store.dispatch("app/toggleRoutes", currentRouter.routes);
                                    this.$router.replace({ path: currentRouter.homeRoutePath });
                                } else {
                                    this.$router.replace({ path: "/noAnyPath" });
                                    // this.$store.dispatch("")
                                }
                            });
                        })
                        .catch(() => {
                            this.loading = false;
                        });
                } else {
                    return false;
                }
            });
        }
    }
};
</script>

<style lang="scss" scoped>
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
    // min-height: 100%;
    // width: 100%;
    // background-color: $bg;
    height: 100vh;
    overflow: hidden;
    display: flex;

    .text-wrap {
        display: flex;
        justify-content: center;
        align-items: center;
        flex: 1;
        position: relative;
        z-index: 1;
        height: calc(100% - 80px);

        .word {
            font-size: 80px;
            color: #fff;
            text-align: center;

            span {
                margin: 0 50px;
            }

            // box-shadow: 0 0 20px rgba(0,0,0,.6);
        }
    }

    .logo {
        position: absolute;
        left: 40px;
        top: 20px;
        z-index: 2;
        width: 484px;
        height: 74px;
    }

    .login-wrap {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        width: 30%;
        height: 100%;
        position: relative;
        z-index: 1;
        background: rgba(14, 28, 39, 0.8);
        // background: rgba(255, 255, 255, .8);
        // background-image:linear-gradient(90deg, transparent,rgba(14, 28, 39, .8));

        .formbox {
            width: 400px;
            height: auto;
            // background: rgba(14, 18, 26, .8);

            border-radius: 0px;
            box-sizing: border-box;

            .mainform {
                padding: 40px 60px 40px;
                box-sizing: border-box;
            }
        }
    }

    .title {
        font-size: 54px;
        font-weight: 500;
        color: rgba(14, 18, 26, 1);
    }

    .title-tips {
        margin-top: 29px;
        font-size: 22px;
        font-weight: 400;
        color: rgba(255, 255, 255, 1);
        text-overflow: ellipsis;
        white-space: nowrap;
        text-align: center;
    }

    .login-btn {
        display: inherit;
        width: 100%;
        height: 40px;
        margin-top: 5px;
        border: 0;

        &:hover {
            opacity: 0.9;
        }
    }

    .login-form {
        position: relative;
        max-width: 100%;
        // margin: calc((100vh - 425px) / 2) 10% 10%;
        overflow: hidden;

        .forget-password {
            width: 100%;
            margin-top: 40px;
            text-align: left;

            .forget-pass {
                width: 129px;
                height: 19px;
                font-size: 20px;
                font-weight: 400;
                color: rgba(92, 102, 240, 1);
            }
        }
    }

    .tips {
        margin-bottom: 10px;
        font-size: 14px;
        color: #fff;

        span {
            &:first-of-type {
                margin-right: 16px;
            }
        }
    }

    .title-container {
        position: relative;

        .title {
            margin: 0 auto 40px auto;
            font-size: 34px;
            font-weight: bold;
            color: blue;
            text-align: center;
        }
    }

    .svg-container {
        position: absolute;
        top: 6px;
        left: 15px;
        z-index: 2;
        font-size: 16px;
        color: #d7dee3;
        cursor: pointer;
        user-select: none;
    }

    .show-password {
        position: absolute;
        top: 6px;
        right: 25px;
        font-size: 16px;
        color: #d7dee3;
        cursor: pointer;
        user-select: none;
    }

    ::v-deep {
        .el-form-item {
            padding-right: 0;
            margin: 20px 0;
            color: #fff;
            background: transparent;
            border: 1px solid transparent;
            border-radius: 2px;

            &__content {
                min-height: 32px;
                line-height: 32px;
            }

            &__error {
                position: absolute;
                top: 100%;
                left: 18px;
                font-size: 12px;
                line-height: 18px;
                color: red;
            }
        }

        .el-input {
            box-sizing: border-box;
            background: transparent;

            input {
                height: 40px;
                padding-left: 45px;
                font-size: 14px;
                line-height: 40px;
                color: #fff;
                background: transparent;
                border: 1px solid #235fa7;
                border-radius: 5px;
                caret-color: #fff;
            }

            input:-webkit-autofill {
                transition: background-color 5000s ease-in-out 0s;
                -webkit-text-fill-color: white !important;
            }
        }
    }
}
</style>
