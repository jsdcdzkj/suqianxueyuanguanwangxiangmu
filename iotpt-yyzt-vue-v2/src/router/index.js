/**
 * @author https://gitee.com/chu1204505056/vue-admin-better （不想保留author可删除）
 * @description router全局配置，如有必要可分文件抽离，其中asyncRoutes只有在intelligence模式下才会用到，vip文档中已提供路由的基础图标与小清新图标的配置方案，请仔细阅读
 */

import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layouts'
import EmptyLayout from '@/layouts/EmptyLayout'
import { publicPath, routerMode } from '@/config'

Vue.use(VueRouter)
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true,
  },
  {
    path: '/register',
    component: () => import('@/views/register/index'),
    hidden: true,
  },
  {
    path: '/personalCenter',
    component: () => import('@/views/personalCenter/index'),
    hidden: true,
  },
  {
    path: '/loading',
    component: Layout,
    hidden: true,
    redirect: 'noRedirect',
    children: [
      {
        path: 'index',
        name: 'Loading',
        component: () => import('@/views/loading/index'),
        meta: {
          title: '请联系管理员',
          icon: 'chess-king',
          affix: true,
        },
      },
    ],
  },
  {
    path: '/401',
    name: '401',
    component: () => import('@/views/401'),
    hidden: true,
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/404'),
    hidden: true,
  },
]

export const asyncRoutes = [
  {
    path: '/',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: 'index',
        name: 'Index',
        component: () => import('@/views/index/index'),
        meta: {
          title: '首页',
          icon: 'chess-king',
          affix: true,
        },
      },
    ],
  },
  /* {
    path: "/test",
    component: Layout,
    redirect: "noRedirect",
    children: [
      {
        path: "test",
        name: "Test",
        component: () => import("@/views/test/index"),
        meta: {
          title: "test",
          icon: "marker",
          permissions: ["admin"],
        },
      },
    ],
  }, */

  {
    path: '/systemmanager',
    component: Layout,
    redirect: 'noRedirect',
    name: 'systemmanager',
    alwaysShow: true,
    meta: { title: '系统管理', icon: 'cog' },
    children: [
      {
        path: 'systemset',
        name: 'systemset',
        component: () => import('@/views/vab/permissions/index'),
        meta: {
          title: '系统配置',
          icon: 'cogs',
          permissions: ['admin', 'editor'],
        },
      },
      // {
      //   path: 'space',
      //   component: EmptyLayout,
      //   redirect: 'noRedirect',
      //   name: 'space',
      //   meta: {
      //     title: '空间管理',
      //     icon: 'building',
      //     permissions: ['admin', 'editor'],
      //   },
      //   children: [
      //     {
      //       path: 'buildingSpace',
      //       name: 'buildingSpace',
      //       component: () => import('@/views/system/space/building/index'),
      //       meta: { title: '楼宇管理',icon:'door-open' },
      //     },
      //     {
      //       path: 'floorSpace',
      //       name: 'floorSpace',
      //       component: () => import('@/views/system/space/floor/index'),
      //       meta: { title: '楼层管理',icon:'layer-group' },
      //     },
      //     {
      //       path: 'areaSpace',
      //       name: 'areaSpace',
      //       component: () => import('@/views/system/space/area/index'),
      //       meta: { title: '区域管理',icon:'map-marked-alt' },
      //     },
      //   ],
      // },
      // {
      //   path: 'orgin',
      //   name: 'orgin',
      //   component: EmptyLayout,
      //   redirect: 'noRedirect',
      //   meta: {
      //     title: '组织机构',
      //     icon: 'network-wired',
      //     permissions: ['admin', 'editor'],
      //   },
      //   children: [
      //     {
      //       path: 'unit',
      //       name: 'unit',
      //       component: () => import('@/views/system/organization/unit/index'),
      //       meta: { title: '单位管理',icon:'puzzle-piece' },
      //     },
      //     {
      //       path: 'department',
      //       name: 'department',
      //       component: () => import('@/views/system/organization/department/index'),
      //       meta: { title: '部门管理',icon:'dice-d20' },
      //     },
      //   ],
      // },
      {
        path: 'spacemanagement',
        name: 'spacemanagement',
        component: () => import('@/views/system/spaceManagement/index'),
        meta: {
          title: '空间管理',
          icon: 'building',
          permissions: ['admin', 'editor'],
        },
      },
      {
        path: 'organizemanagement',
        name: 'organizemanagement',
        component: () => import('@/views/system/organizeManagement/index'),
        meta: {
          title: '组织机构',
          icon: 'network-wired',
          permissions: ['admin', 'editor'],
        },
      },
      {
        path: 'menumanger',
        name: 'menumanger',
        component: () => import('@/views/system/menuManagement/index'),
        meta: {
          title: '菜单管理',
          icon: 'list',
          permissions: ['admin', 'editor'],
        },
      },
      {
        path: 'rolemanger',
        name: 'rolemanger',
        component: () => import('@/views/system/roleManagement/index'),
        meta: {
          title: '角色管理',
          icon: 'users',
          permissions: ['admin', 'editor'],
        },
      },
      {
        path: 'usermanger',
        name: 'usermanger',
        component: () => import('@/views/system/userManagement/index'),
        meta: {
          title: '用户管理',
          icon: 'user',
          permissions: ['admin', 'editor'],
        },
      },
      {
        path: 'zidianmanger',
        name: 'zidianmanger',
        component: () => import('@/views/system/dictionary/index'),
        meta: {
          title: '字典管理',
          icon: 'book',
          permissions: ['admin', 'editor'],
        },
      },
      {
        path: 'rizhimanger',
        name: 'rizhimanger',
        component: () => import('@/views/system/log/index'),
        meta: {
          title: '日志管理',
          icon: 'bookmark',
          permissions: ['admin', 'editor'],
        },
      },
      // {
      //   path: 'icon',
      //   component: EmptyLayout,
      //   redirect: 'noRedirect',
      //   name: 'Icon',
      //   meta: {
      //     title: '图标',
      //     permissions: ['admin'],
      //   },
      //   children: [
      //     {
      //       path: 'awesomeIcon',
      //       name: 'AwesomeIcon',
      //       component: () => import('@/views/vab/icon/index'),
      //       meta: { title: '常规图标' },
      //     },
      //     {
      //       path: 'colorfulIcon',
      //       name: 'ColorfulIcon',
      //       component: () => import('@/views/vab/icon/colorfulIcon'),
      //       meta: { title: '多彩图标' },
      //     },
      //   ],
      // },
    ],
  },
  {
    path: '/configset',
    component: Layout,
    redirect: 'noRedirect',
    name: 'configset',
    meta: { title: '配置管理', icon: 'clipboard-list' },
    children: [
      {
        path: 'titlemanager',
        name: 'titlemanager',
        component: () => import('@/views/configSet/titleManager/index'),
        meta: { title: '主题管理', icon: 'border-all' },
      },
      // {
      //   path: 'xieyimanager',
      //   name: 'xieyimanager',
      //   component: () =>
      //     import('@/views/configSet/xieyiManager/index'),
      //   meta: { title: '协议配置',icon: 'bars' },
      // },
      {
        path: 'lianjiemanager',
        name: 'lianjiemanager',
        component: () => import('@/views/configSet/lianjieManager/index'),
        meta: { title: '连接配置', icon: 'link' },
      },
      {
        path: 'datamanager',
        name: 'datamanager',
        component: () => import('@/views/configSet/dataManager/index'),
        meta: { title: '数据模板', icon: 'layer-group' },
      },
      {
        path: 'equipmenttype',
        name: 'equipmenttype',
        component: () => import('@/views/configSet/equipMenttype/index'),
        meta: { title: '设备类型', icon: 'hdd' },
      },
      {
        path: 'xinhaotype',
        name: 'xinhaotype',
        component: () => import('@/views/configSet/xinhaoType/index'),
        meta: { title: '信号类型', icon: 'wifi' },
      },
      {
        path: 'equipmentFx',
        name: 'equipmentFx',
        component: () => import('@/views/configSet/equipmentFx/index'),
        meta: { title: '设备分项', icon: 'book' },
      },
      {
        path: 'gyxmanager',
        name: 'gyxmanager',
        component: () => import('@/views/configSet/gyxManager/index'),
        meta: { title: '供应商管理', icon: 'window-maximize' },
      },
      // {
      //   path: 'xinghaomanager',
      //   name: 'xinghaomanager',
      //   component: () =>
      //     import('@/views/configSet/xinghaoManager/index'),
      //   meta: { title: '型号管理',icon: 'book' },
      // },
    ],
  },
  {
    path: '/equipmentCenter',
    component: Layout,
    redirect: 'noRedirect',
    name: 'equipmentCenter',
    meta: {
      title: '设备中心',
      icon: 'record-vinyl',
      permissions: ['admin'],
    },
    children: [
      {
        path: 'cjzd',
        name: 'cjzd',
        component: () => import('@/views/equipmentCenter/cjzd/index'),
        meta: {
          title: '采集终端',
          icon: 'allergies',
        },
      },
      {
        path: 'mjsb',
        name: 'mjsb',
        component: () => import('@/views/equipmentCenter/mjsb/index'),
        meta: {
          title: '门禁设备',
          icon: 'door-closed',
        },
      },
      {
        path: 'spsb',
        name: 'spsb',
        component: () => import('@/views/equipmentCenter/spsb/index'),
        meta: {
          title: '视频设备',
          icon: 'file-video',
        },
      },
      {
        path: 'wgsb',
        name: 'wgsb',
        component: () => import('@/views/equipmentCenter/wgsb/index'),
        meta: {
          title: '网关设备',
          icon: 'barcode',
        },
      },
    ],
  },
  {
    path: '/warningCenter',
    component: Layout,
    redirect: 'noRedirect',
    name: 'warningCenter',
    meta: { title: '告警中心', icon: 'bug' },
    children: [
      {
        path: 'warningnow',
        name: 'warningnow',
        component: () => import('@/views/warningCenter/realtime/index'),
        meta: { title: '实时告警', icon: 'flag' },
      },
      {
        path: 'warningset',
        name: 'warningset',
        component: () => import('@/views/warningCenter/configuration/index'),
        meta: { title: '告警配置', icon: 'file-medical-alt' },
      },
      {
        path: 'warninghistory',
        name: 'warninghistory',
        component: () => import('@/views/warningCenter/history/index'),
        meta: { title: '历史告警', icon: 'calendar-alt' },
      },
    ],
  },
  {
    path: '/dataserver',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: 'index',
        name: 'Index',
        component: () => import('@/views/index/index'),
        meta: {
          title: '数据服务',
          icon: 'coins',
        },
      },
    ],
  },
  {
    path: '/infomationServer',
    component: Layout,
    redirect: 'noRedirect',
    name: 'infomationServer',
    meta: { title: '消息服务', icon: 'info-circle' },
    children: [
      {
        path: 'service',
        name: 'service',
        component: () => import('@/views/infomationServer/service/index'),
        meta: { title: '服务管理', icon: 'chess-queen' },
      },
      {
        path: 'blacklist',
        name: 'blacklist',
        component: () => import('@/views/infomationServer/blacklist/index'),
        meta: { title: '黑名单', icon: 'angry' },
      },
    ],
  },
  {
    path: '*',
    redirect: '/404',
    hidden: true,
  },
]

const router = new VueRouter({
  base: publicPath,
  mode: routerMode,
  scrollBehavior: () => ({
    y: 0,
  }),
  routes: constantRoutes,
})

export function resetRouter() {
  location.reload()
}

export default router
