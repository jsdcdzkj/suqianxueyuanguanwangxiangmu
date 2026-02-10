import api from '@/api/index.js'

const weixin = {
	login: function() {
		// uni.clearStorageSync(); //启动时清空本地缓存
		uni.removeStorageSync('openid');
		uni.removeStorageSync('accessToken');
		uni.removeStorageSync('userInfo');

		uni.login({
			provider: 'weixin', //使用微信登录
			success: function(res) {
				api.user.wxLogin({
					code: res.code
				}).then(result => {
					
					if (result.code === 0) {
						getApp().loginUserInfo(result.data)
						if (getApp().loginCallback && typeof getApp().loginCallback ==
							'function') {
							getApp().loginCallback();
							
						}
					}
				})
			}
		});
	},
	autoUpdate: function() {
		if (uni.canIUse('getUpdateManager')) {
			const updateManager = uni.getUpdateManager();
			//检查updateManager的更新
			updateManager.onCheckForUpdate(function(res) {
				//hasUpdate是否有新版本
				if (res.hasUpdate) {
					//下载版本完成时的回调
					updateManager.onUpdateReady(function() {
						uni.showModal({
							title: "更新提示",
							content: "新版本已更新，是否重启应用？",
							success(res) {
								if (res.confirm) {
									updateManager.applyUpdate();
								}
							}
						});
					});
					//下载版本失败时的回调
					updateManager.onUpdateFailed(function() {
						uni.showModal({
							title: "新版本已更新！",
							content: "新版本已上线，请重启小程序完成更新！"
						});
					})
				}
			})
		} else {
			uni.showModal({
				title: "提示",
				content: "当前版本无法使用该功能，请更新微信版本！"
			});
		}
	}
}

export default weixin