import App from './App'
import tool from './utils/tool'
import config from '@/config'
import uView from './uni_modules/vk-uview-ui';

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
	...App
})
app.$mount()
// #endif

// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
export const publicImage = 'https://smartpark.dincher.cn/yqimg'
export function createApp() {
	const app = createSSRApp(App)
	app.config.globalProperties.$TOOL = tool;
	app.config.globalProperties.$publicImage = publicImage;
	app.use(uView)
	return {
		app
	}
}
// #endif