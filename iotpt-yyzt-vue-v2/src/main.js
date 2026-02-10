import Vue from 'vue'
import App from './App'
import store from './store'
import router from './router'
import './plugins'
import '@/layouts/export'
import {parseTime, getDictByKey, selectDictLabel, formatDate} from '@/utils/tools'
import Treeselect from "@riophae/vue-treeselect";
import EbsTreeSelect from '@/components/EbsTreeSelect'

// 全局组件
Vue.component( 'Treeselect', Treeselect )
Vue.component( 'EbsTreeSelect', EbsTreeSelect )


// 公共方法
Vue.prototype.parseTime = parseTime
Vue.prototype.getDictByKey = getDictByKey
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.formatDate = formatDate
/**
 * @description 生产环境默认都使用mock，如果正式用于生产环境时，记得去掉
 */

// if (process.env.NODE_ENV === 'production') {
//   const { mockXHR } = require('@/utils/static')
//   mockXHR()
// }

Vue.config.productionTip = false



new Vue({
  el: '#vue-admin-beautiful',
  router,
  store,
  render: (h) => h(App),
})
