/* 公共引入,勿随意修改,修改时需经过确认 */
import Vue from 'vue'
import './element'
import './support'
import '@/styles/vab.scss'
import '@/styles/element-ui.scss'
import '@/remixIcon'
import '@/colorfulIcon'
import '@/config/permission'
import '@/utils/errorLog'
import './vabIcon'
import VabPermissions from 'zx-layouts/Permissions'
import Vab from '@/utils/vab'
import VabCount from 'zx-count'
import JsonViewer from 'vue-json-viewer'
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

Vue.use(Vab)
Vue.use(VabPermissions)
Vue.use(VabCount)
Vue.use(JsonViewer)
