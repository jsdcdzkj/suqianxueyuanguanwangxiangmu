export default {

  created() {
    //#ifdef MP-WEIXIN
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    });
    //#endif
  },
  onLoad(e) {
    const pages = getCurrentPages()
    const currentPageUrl = pages[pages.length - 1].route;

    let islogin = uni.getStorageSync('userInfo')
    if (currentPageUrl.indexOf('login/person') == -1) {

      if (!islogin.id && currentPageUrl.indexOf('login/index') == -1 && currentPageUrl.indexOf('pages/index/index') == -1 && currentPageUrl.indexOf('login/register') == -1 && currentPageUrl.indexOf('usercenter/privacy') == -1 && currentPageUrl.indexOf('login/forgotpassword') == -1 && currentPageUrl.indexOf('usercenter/statement') == -1 && currentPageUrl.indexOf('login/resetpassword') == -1 && currentPageUrl.indexOf('visitor/visitor') == -1 && currentPageUrl.indexOf('visitor/code_detail') == -1 && currentPageUrl.indexOf('login/vaildphone') == -1) {
        uni.reLaunch({
          url: '/pages/login/index'
        });
        return false
      }
    }
  }
}
