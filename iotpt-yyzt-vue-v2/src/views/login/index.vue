<template>
  <div class="login-container">
    <sbanner></sbanner>
    <div class="logo">
      <el-image :src="require('@/assets/login_images/logo.png')"></el-image>
    </div>
    <div class="login-wrap">
      <div class="formbox">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          class="login-form"
          label-position="left"
        >
          <!-- <div class="title">hello !</div> -->
          <div class="title-tips">欢迎来到{{ title }}！</div>
          <el-form-item style="margin-top: 40px" prop="username">
            <span class="svg-container svg-container-admin">
              <vab-icon :icon="['fas', 'user']" />
            </span>
            <el-input
              v-model.trim="form.username"
              v-focus
              placeholder="请输入用户名"
              tabindex="1"
              type="text"
              autocomplete="off"
            />
          </el-form-item>
          <el-form-item prop="password">
            <span class="svg-container">
              <vab-icon :icon="['fas', 'lock']" />
            </span>
            <el-input
              :key="passwordType"
              ref="password"
              v-model.trim="form.password"
              :type="passwordType"
              tabindex="2"
              placeholder="请输入密码"
              @keyup.enter.native="handleLogin"
              autocomplete="off"
            />
            <span
              v-if="passwordType === 'password'"
              class="show-password"
              @click="handlePassword"
            >
              <vab-icon :icon="['fas', 'eye-slash']"></vab-icon>
            </span>
            <span v-else class="show-password" @click="handlePassword">
              <vab-icon :icon="['fas', 'eye']"></vab-icon>
            </span>
          </el-form-item>
          <el-button
            :loading="loading"
            class="login-btn"
            type="primary"
            @click="handleLogin"
          >
            登录
          </el-button>
          <!-- <router-link to="/register">
                      <div style="margin-top: 20px">注册</div>
                    </router-link> -->
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
  import { isPassword } from '@/utils/validate'
  import { userInfoPermissions } from '@/api/user'
  import sbanner from '@/components/bgBanner'

  export default {
    name: 'Login',
    components: { sbanner },
    directives: {
      focus: {
        inserted(el) {
          el.querySelector('input').focus()
        },
      },
    },
    data() {
      const validateusername = (rule, value, callback) => {
        if ('' == value) {
          callback(new Error('用户名不能为空'))
        } else {
          callback()
        }
      }
      const validatePassword = (rule, value, callback) => {
        if (!isPassword(value)) {
          callback(new Error('密码不能少于6位'))
        } else {
          callback()
        }
      }
      return {
        nodeEnv: process.env.NODE_ENV,
        title: this.$baseTitle,
        count: 0,
        form: {
          username: '',
          password: '',
          cardinfo: '',
          type: '1'
        },
        rules: {
          username: [
            {
              required: true,
              trigger: 'blur',
              validator: validateusername,
            },
          ],
          password: [
            {
              required: true,
              trigger: 'blur',
              validator: validatePassword,
            },
          ],
        },
        loading: false,
        passwordType: 'password',
        redirect: undefined,
      }
    },
    watch: {
      $route: {
        handler(route) {
          this.redirect = (route.query && route.query.redirect) || '/'
        },
        immediate: true,
      },
    },
    created() {
      document.body.style.overflow = 'hidden'
    },
    beforeDestroy() {
      document.body.style.overflow = 'auto'
    },
    mounted() {
      // this.form.username = 'admin'
      // this.form.password = '123456'
      // setTimeout(() => {
      //   this.handleLogin()
      // }, 3000)
      window.addEventListener("message", this.handleMessage);
    },
    methods: {
      handlePassword() {
        this.passwordType === 'password'
          ? (this.passwordType = '')
          : (this.passwordType = 'password')
        this.$nextTick(() => {
          this.$refs.password.focus()
        })
      },
      handleMessage(e) {
            var that = this;
            if ("portal" == e.data.type) {
                var cardinfo = encodeURIComponent(e.data.cardInfo);
                if (cardinfo) {
                    that.form.type = "2";
                    that.form.cardinfo = cardinfo;
                    that.handleLogin_auto();
                } else {
                    that.form.type = "1";
                }
            }
        },
      handleLogin_auto() {
            this.loading = true
            var that = this;
            if (this.count != 0) {
                return;
            }
            this.count++;
            this.$store
                .dispatch('user/login', this.form)
                .then(res => {
                    // const routerPath =
                    //     this.redirect === '/404' || this.redirect === '/401'
                    //         ? '/'
                    //         : this.redirect
                    that.$router.push('/index').catch(() => {
                    })
                    this.loading = false
                    window.removeEventListener('message', that.handleMessage);
                })
                .catch(() => {
                    this.loading = false
                })
        },
      handleLogin() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.loading = true
            this.$store
              .dispatch('user/login', this.form)
              .then(() => {
                const routerPath =
                  this.redirect === '/404' || this.redirect === '/401'
                    ? '/'
                    : this.redirect
                this.$router.push(routerPath).catch(() => {})
                setTimeout(() => {
                  this.loading = false
                }, 1000)

                // // 从state中获取用户信息
                // userInfoPermissions().then(res => {
                //     let routerPath = this.redirect === '/404' || this.redirect === '/401' ? '/' : this.redirect
                //     if (res.code == 0) {
                //         let permissions = res.data.permissions
                //         var path = '';
                //         while (permissions && permissions[0] && permissions[0].children) {
                //             path += permissions[0].path + "/";
                //             permissions = permissions[0].children;
                //         }
                //         if (path.length > 0) {
                //             path = path.substring(1, path.length - 1);
                //             console.log("🚀 ~ file: index.vue:155 ~ userInfoPermissions ~ path:", path)
                //             this.$router.push(path).catch(() => {})
                //             setTimeout(() => {
                //                 this.loading = false
                //             }, 1000)
                //         }else {
                //             this.$router.push('/loading/index').catch(() => {})
                //         }
                //     }
                // })
              })
              .catch(() => {
                this.loading = false
              })
              .finally(() => {
                setTimeout(() => {
                  this.loading = false
                }, 1000)
              })
          } else {
            return false
          }
        })
      },
    },
  }
</script>

<style lang="scss" scoped>
  .login-container {
    height: 100vh;

    // background: url('~@/assets/login_images/b1.jpg') center center fixed no-repeat;
    // background-size: cover;
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
      width: 100%;
      height: calc(100% - 100px);
      position: relative;
      z-index: 2;

      .formbox {
        width: 400px;
        height: auto;
        background: rgba(14, 18, 26, 0.8);
        padding: 40px 80px 80px;
        border-radius: 10px;
        box-sizing: border-box;
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
      font-size: $base-font-size-default;
      color: $base-color-white;

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
        color: $base-color-blue;
        text-align: center;
      }
    }

    .svg-container {
      position: absolute;
      top: 6px;
      left: 15px;
      z-index: $base-z-index;
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
          min-height: $base-input-height;
          line-height: $base-input-height;
        }

        &__error {
          position: absolute;
          top: 100%;
          left: 18px;
          font-size: $base-font-size-small;
          line-height: 18px;
          color: $base-color-red;
        }
      }

      .el-input {
        box-sizing: border-box;
        background: transparent;

        input {
          height: 40px;
          padding-left: 45px;
          font-size: $base-font-size-default;
          line-height: 40px;
          color: #fff;
          background: transparent;
          border: 1px solid #fff;
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
