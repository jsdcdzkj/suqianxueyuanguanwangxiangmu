import "normalize.css/normalize.css"; // A modern alternative to CSS resets
import Vue from "vue";
import "quill/dist/quill.core.css"; // import styles
import "quill/dist/quill.snow.css"; // for snow theme
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";

import "../jsmpeg.min";

//日期格式化工具
import dayjs from "dayjs";
import "flex.css";

//图表工具
import * as echarts from "echarts";
import "echarts-liquidfill";

import "@/styles/index.scss"; // global css
import "@/remixIcon/remixicon.css"; // remixicon css

// 视频插件
import "video.js/dist/video-js.css"; // 引入video.js的css
import hls from "videojs-contrib-hls"; // 播放hls流需要的插件
//import flash from "videojs-flash"; // 播放rtmp流需要的插件
Vue.use(hls);
//Vue.use(flash);

import store from "./store";
import { parseTime, getDictByKey, selectDictLabel, formatDate, throttle } from "@/utils/tools";

//import { ioTSupervisionRoutes, intelligentSecurityRoutes, iTOperationManageRoutes } from "./appHeader/route";

// 全局组件
import BoTreeSelect from "@/components/BoTreeSelect";
import Ele from "@/utils/ele";
import VabIcon from "vab-icon";
import EbsTreeSelect from "@/components/EbsTreeSelect";
import registerReportTemplate from "@/components/ReportTemplate/index";
// 全局组件
Vue.component("EbsTreeSelect", EbsTreeSelect);
// 全局组件
Vue.component("BoTreeSelect", BoTreeSelect);
Vue.component("VabIcon", VabIcon);

registerReportTemplate(Vue);
// 公共方法
Vue.prototype.parseTime = parseTime;
Vue.prototype.getDictByKey = getDictByKey;
Vue.prototype.selectDictLabel = selectDictLabel;
Vue.prototype.formatDate = formatDate;
Vue.prototype.throttle = throttle;
Vue.prototype.$echarts = echarts;
Vue.prototype.dayjs = dayjs;
Vue.prototype.baseUrl = process.env.VUE_APP_BASE_API;
Vue.prototype.uploadUrl = process.env.VUE_APP_BASE_API + "/minio/importFile";
Vue.prototype.commonImgUrl = process.env.VUE_APP_BASE_API + "/minio/previewFile?fileName=";
Vue.use(Ele);

import "@/assets/font/font.css";
import "@/icons"; // icon
import "@/permission"; // permission control

// 将自动注册所有组件为全局组件
import dataV from "@jiaminghi/data-view";

Vue.use(dataV);
/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
// if (process.env.NODE_ENV === 'production') {
//   const { mockXHR } = require('../mock')
//   mockXHR()
// }

// set ElementUI lang to EN
// Vue.use(ElementUI, { locale, size: 'small' })
// 如果想要中文版 element-ui，按如下方式声明
Vue.use(ElementUI, { size: "small" });

Vue.config.productionTip = false;

// 对路由的处理
// const templateRoutesIndex = localStorage.getItem('alreadyRoutes__template__index') || 0
// var linkJson = localStorage.getItem('links') || ''
// if (linkJson != '') {
//     var links = JSON.parse(linkJson);
//     var route = links[templateRoutesIndex].routes;
//     if (templateRoutesIndex !== 0) {
//         router.options.routes = links[templateRoutesIndex].routes;
//         router.addRoutes(links[templateRoutesIndex].routes)
//         store.dispatch('app/toggleRoutes', route)
//     } else {
//         store.dispatch('app/toggleRoutes', links[0].routes)
//     }
//
// }
// const routesArr = [constantRoutes, ioTSupervisionRoutes, intelligentSecurityRoutes, iTOperationManageRoutes]
// const templateRoutes = routesArr[templateRoutesIndex]
//
// if (templateRoutesIndex !== 0) {
//   router.options.routes = templateRoutes;
//   router.addRoutes(templateRoutes)
//   store.dispatch('app/toggleRoutes', templateRoutes)
// } else {
//   store.dispatch('app/toggleRoutes', constantRoutes)
// }
import App from "./App";
import router, { constantRoutes } from "./router";
new Vue({
    el: "#app",
    router,
    store,
    render: (h) => h(App)
});
