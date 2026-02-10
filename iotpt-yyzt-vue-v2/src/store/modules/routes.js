/**
 * @author https://gitee.com/chu1204505056/vue-admin-better （不想保留author可删除）
 * @description 路由拦截状态管理，目前两种模式：all模式与intelligence模式，其中partialRoutes是菜单暂未使用
 */
import { asyncRoutes, constantRoutes } from '@/router'
import { getRouterList } from '@/api/router'
import { convertRouter, filterAsyncRoutes } from '@/utils/handleRoutes'

const state = () => ({
  routes: [],
  partialRoutes: [],
})
const getters = {
  routes: (state) => state.routes,
  partialRoutes: (state) => state.partialRoutes,
}
const mutations = {
  setRoutes(state, routes) {
    state.routes = constantRoutes.concat(routes)
  },
  setAllRoutes(state, routes) {
    state.routes = constantRoutes.concat(routes)
  },
  setPartialRoutes(state, routes) {
    state.partialRoutes = constantRoutes.concat(routes)
  },
}
const actions = {
  async setRoutes({ commit }, permissions) {
    let path = ''

    for (let index = 0; index < permissions.length; index++) {
      const element = permissions[index]
      if (element && element.children && element.children.length == 0) {
        element.children = [JSON.parse(JSON.stringify(element))]
        element.component = 'Layout'
        element.path = element.path
        element.redirect = 'noRedirect'
        element.name = element.name
        // console.log(element,'permissions[index]');
      }
    }

    if (
      permissions &&
      permissions[0] &&
      permissions[0].children &&
      permissions[0].children.length > 0
    ) {
      path = permissions[0].path + '/' + permissions[0].children[0].path
    } else {
      path = '/loading/index'
    }
    // console.log(path,permissions,'path=========');

    //开源版只过滤动态路由permissions，admin不再默认拥有全部权限
    permissions.push({ path: '/', redirect: path, hidden: true })
    permissions.push({ path: '*', redirect: '/404', hidden: true })

    let accessRoutes = convertRouter(permissions)
    const finallyAsyncRoutes = await filterAsyncRoutes(
      [...asyncRoutes],
      permissions
    )
    // commit('setRoutes', accessRoutes)
    commit('setRoutes', accessRoutes)
    return accessRoutes
  },
  async setAllRoutes({ commit }) {
    let { data } = await getRouterList()
    console.log(data)
    data.push({ path: '*', redirect: '/404', hidden: true })
    let accessRoutes = convertRouter(data)
    commit('setAllRoutes', accessRoutes)
    return accessRoutes
  },
  setPartialRoutes({ commit }, accessRoutes) {
    commit('setPartialRoutes', accessRoutes)
    return accessRoutes
  },
}
export default { state, getters, mutations, actions }
