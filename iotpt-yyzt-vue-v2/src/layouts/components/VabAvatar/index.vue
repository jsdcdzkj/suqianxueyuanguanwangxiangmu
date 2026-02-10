<template>
  <el-dropdown @command="handleCommand">
    <span class="avatar-dropdown">
      <!--<el-avatar class="user-avatar" :src="avatar"></el-avatar>-->
      <!-- <img class="user-avatar" :src="avatar" alt="" /> -->
      <img class="user-avatar" src="~@/assets/avatar.gif" alt="" />

      <div class="user-name">
        {{ username }}
        <i class="el-icon-arrow-down el-icon--right"></i>
      </div>
    </span>

    <el-dropdown-menu slot="dropdown">
      <!-- <el-dropdown-item command="github">github地址</el-dropdown-item>
      <el-dropdown-item command="gitee" divided>码云地址</el-dropdown-item>
      <el-dropdown-item command="pro" divided>pro付费版地址</el-dropdown-item> -->
      <!-- <el-dropdown-item command="personalCenter">个人设置</el-dropdown-item> -->
      <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
  import { mapGetters } from 'vuex'
  import { recordRoute } from '@/config'

  import { getWarning, upeWarning } from '@/api/user'

  export default {
    name: 'VabAvatar',
    computed: {
      ...mapGetters({
        avatar: 'user/avatar',
        username: 'user/username',
      }),
    },
    data() {
      return {
        warningId: -1,
        notify: null,
      }
    },
    created() {
      //this.openWarning()
    },
    beforeDestroy() {
      if (this.notify) {
        this.notify.close()
        this.notify = null
      }
    },
    methods: {
      async openWarning() {
        const that = this
        const res = await getWarning()
        if (res.data && res.data.id) {
          if (that.warningId != res.data.id) {
            let msg = '<b style="color: #F56C6C">告警中</b></br>'
            msg += '<strong><i>' + res.data.warnTypeName + '</i></strong></br>'
            msg += res.data.point ? res.data.point + '</br>' : ''
            msg +=
              res.data.warnContent == null ? '' : res.data.warnContent + '</br>'
            that.notify = this.$notify({
              title: res.data.deviceName,
              message: msg,
              dangerouslyUseHTMLString: true,
              duration: 0,
              position: 'bottom-right',
              onClose: function () {
                that.$nextTick(() => {
                  if (that.notify) {
                    upeWarning({ id: res.data.id, yyztRead: '1' })
                    that.warningId = res.data.id
                    that.openWarning()
                  }
                })
                // that.$confirm('此操作将永久删除该信息, 是否继续?', '提示', {
                //   confirmButtonText: '确定',
                //   cancelButtonText: '取消',
                //   type: 'warning'
                // }).then(() => {
                //   upeWarning({id:res.data.id,zhyyRead:"1"})
                //   that.warningId = res.data.id
                //   that.openWarning()
                //   that.$message({
                //     type: 'success',
                //     message: '关闭信息成功!'
                //   });
                // }).catch(() => {
                //   that.$message({
                //     type: 'info',
                //     message: '已取消操作'
                //   });
                // });
              },
            })
          }
        }
      },
      handleCommand(command) {
        switch (command) {
          case 'logout':
            this.logout()
            break
          case 'personalCenter':
            this.personalCenter()
            break
        }
      },
      personalCenter() {
        // this.$router.push('/personalCenter')
      },
      logout() {
        this.$baseConfirm(
          '您确定要退出' + this.$baseTitle + '吗?',
          null,
          async () => {
            await this.$store.dispatch('user/logout')
            if (recordRoute) {
              const fullPath = this.$route.fullPath
              this.$router.replace(`/login`)
              // window.location.href = '/';
            } else {
              console.log('🚀 ~ file: index.vue:57 ~ :')
              this.$router.replace('/login')
              // window.location.href = '/'
            }
          }
        )
      },
    },
  }
</script>
<style lang="scss" scoped>
  .avatar-dropdown {
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: center;
    justify-items: center;
    height: 50px;
    padding: 0;

    .user-avatar {
      width: 40px;
      height: 40px;
      cursor: pointer;
      border-radius: 50%;
    }

    .user-name {
      position: relative;
      margin-left: 5px;
      margin-left: 5px;
      cursor: pointer;
    }
  }
</style>
