import { del } from 'vue'

/**
 * @author https://gitee.com/chu1204505056/vue-admin-better （不想保留author可删除）
 * @description all模式渲染后端返回路由
 * @param constantRoutes
 * @returns {*}
 */
export function convertRouter(asyncRoutes) {
  // console.log(JSON.parse(JSON.stringify(asyncRoutes)))
  return asyncRoutes.map((route) => {
    // debugger
    // console.log(route.component, route, "route.component")
    if (route.component) {
      if (route.component === 'Layout') {
        route.component = (resolve) => require(['@/layouts'], resolve)
      } else if (route.component === 'EmptyLayout') {
        route.component = (resolve) =>
          require(['@/layouts/EmptyLayout'], resolve)
      } else if (route.component === 'path') {
        console.log(
          '🚀 ~ file: handleRoutes.js:21 ~ returnasyncRoutes.map ~ component:',
          route
        )
        ;(route.meta.target = '_blank'), delete route.component
      } else {
        const index = route.component.indexOf('views')
        const path =
          index > 0 ? route.component.slice(index) : `views/${route.component}`
        route.component = (resolve) => require([`@/${path}`], resolve)
      }

      // if (route.children && route.children.length)
      //     route.children = convertRouter(route.children)
      // if (route.children && route.children.length === 0) delete route.children
    }
    if (route.children && route.children.length)
      route.children = convertRouter(route.children)
    if (route.children && route.children.length === 0) delete route.children
    return route
  })
}

/**
 * @author https://gitee.com/chu1204505056/vue-admin-better （不想保留author可删除）
 * @description 判断当前路由是否包含权限
 * @param permissions
 * @param route
 * @returns {boolean|*}
 */
function hasPermission(permissions, route) {
  if (route.meta && route.meta.permissions) {
    return permissions.some((role) => route.meta.permissions.includes(role))
  } else {
    return true
  }
}

/**
 * @author https://gitee.com/chu1204505056/vue-admin-better （不想保留author可删除）
 * @description intelligence模式根据permissions数组拦截路由
 * @param routes
 * @param permissions
 * @returns {[]}
 */
export function filterAsyncRoutes(routes, permissions) {
  const finallyRoutes = []
  routes.forEach((route) => {
    const item = { ...route }
    if (hasPermission(permissions, item)) {
      // if (item.children) {
      //   item.children = filterAsyncRoutes(item.children, permissions)
      // }

      finallyRoutes.push(item)
    }
  })
  return finallyRoutes
}

// 组装路由
export function filterAsyncRoutes2(routes, permissions) {
  const finallyRoutes = []
  /**
   * [{"path":"/","component":{"name":"Layout","computed":{},"beforeDestroy":[null,null],"methods":{},"staticRenderFns":[],"_compiled":true,"_scopeId":"data-v-699834c3","beforeCreate":[null],"__file":"src/layouts/index.vue"},"redirect":"/index","children":[{"path":"index","name":"Index","meta":{"title":"首页","icon":"chess-king","affix":true}}]},{"path":"/systemmanager","component":{"name":"Layout","computed":{},"beforeDestroy":[null,null],"methods":{},"staticRenderFns":[],"_compiled":true,"_scopeId":"data-v-699834c3","beforeCreate":[null],"__file":"src/layouts/index.vue"},"redirect":"noRedirect","name":"systemmanager","alwaysShow":true,"meta":{"title":"系统管理","icon":"cog"},"children":[{"path":"systemset","name":"systemset","meta":{"title":"系统配置","icon":"cogs","permissions":["admin","editor"]}},{"path":"space","component":{"staticRenderFns":[],"_compiled":true,"beforeCreate":[null],"beforeDestroy":[null],"__file":"src/layouts/EmptyLayout.vue"},"redirect":"noRedirect","name":"space","meta":{"title":"空间管理","icon":"building","permissions":["admin","editor"]},"children":[{"path":"buildingSpace","name":"buildingSpace","meta":{"title":"楼宇管理","icon":"door-open"}},{"path":"floorSpace","name":"floorSpace","meta":{"title":"楼层管理","icon":"layer-group"}},{"path":"areaSpace","name":"areaSpace","meta":{"title":"区域管理","icon":"map-marked-alt"}}]},{"path":"orgin","name":"orgin","component":{"staticRenderFns":[],"_compiled":true,"beforeCreate":[null],"beforeDestroy":[null],"__file":"src/layouts/EmptyLayout.vue"},"redirect":"noRedirect","meta":{"title":"组织机构","icon":"network-wired","permissions":["admin","editor"]},"children":[{"path":"unit","name":"unit","meta":{"title":"单位管理","icon":"puzzle-piece"}},{"path":"department","name":"department","meta":{"title":"部门管理","icon":"dice-d20"}}]},{"path":"menumanger","name":"menumanger","meta":{"title":"菜单管理","icon":"list","permissions":["admin","editor"]}},{"path":"rolemanger","name":"rolemanger","meta":{"title":"角色管理","icon":"users","permissions":["admin","editor"]}},{"path":"usermanger","name":"usermanger","meta":{"title":"用户管理","icon":"user","permissions":["admin","editor"]}},{"path":"zidianmanger","name":"zidianmanger","meta":{"title":"字典管理","icon":"book","permissions":["admin","editor"]}},{"path":"rizhimanger","name":"rizhimanger","meta":{"title":"日志管理","icon":"bookmark","permissions":["admin","editor"]}}]},{"path":"/warningCenter","component":{"name":"Layout","computed":{},"beforeDestroy":[null,null],"methods":{},"staticRenderFns":[],"_compiled":true,"_scopeId":"data-v-699834c3","beforeCreate":[null],"__file":"src/layouts/index.vue"},"redirect":"noRedirect","name":"warningCenter","meta":{"title":"告警中心","icon":"bug"},"children":[{"path":"warningnow","name":"warningnow","meta":{"title":"实时告警","icon":"flag"}},{"path":"warningset","name":"warningset","meta":{"title":"告警配置","icon":"file-medical-alt"}},{"path":"warninghistory","name":"warninghistory","meta":{"title":"历史告警","icon":"calendar-alt"}}]},{"path":"/dataserver","component":{"name":"Layout","computed":{},"beforeDestroy":[null,null],"methods":{},"staticRenderFns":[],"_compiled":true,"_scopeId":"data-v-699834c3","beforeCreate":[null],"__file":"src/layouts/index.vue"},"redirect":"/index","children":[{"path":"index","name":"Index","meta":{"title":"数据服务","icon":"coins"}}]},{"path":"/infomationServer","component":{"name":"Layout","computed":{},"beforeDestroy":[null,null],"methods":{},"staticRenderFns":[],"_compiled":true,"_scopeId":"data-v-699834c3","beforeCreate":[null],"__file":"src/layouts/index.vue"},"redirect":"noRedirect","name":"infomationServer","meta":{"title":"消息服务","icon":"info-circle"},"children":[{"path":"service","name":"service","meta":{"title":"服务管理","icon":"chess-queen"}},{"path":"blacklist","name":"blacklist","meta":{"title":"黑名单","icon":"angry"}}]},{"path":"*","redirect":"/404","hidden":true}]
   */
  //把permissions 组装成路由
  console.log('finallyRoutes', JSON.stringify(finallyRoutes))
  return permissions
}
