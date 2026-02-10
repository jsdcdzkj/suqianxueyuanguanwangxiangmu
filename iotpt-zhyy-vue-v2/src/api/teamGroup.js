import request from '@/utils/request'

/**
 * 分页查询班组
 * @param data
 * @returns {AxiosPromise}
 */
export function getList(data) {
  return request({
    url: '/teamGroups/getTeamPage.do',
    method: 'post',
    params: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
  })
}

/**
 * 删除班组
 * @param data
 * @returns {AxiosPromise}
 */
export function delTeam(data) {
  return request({
    url: '/teamGroups/delTeam.do',
    method: 'post',
    params: data,
  })
}


/**
 * 班组详情
 * @param data
 * @returns {AxiosPromise}
 */
export function detailTeam(data) {
  return request({
    url: '/teamGroups/detailTeam.do',
    method: 'post',
    params: data,
  })
}

/**
 * 用户分页
 * @param data
 * @returns {AxiosPromise}
 */
export function getUserPage(data) {
  return request({
    url: '/teamGroups/getUserPage.do',
    method: 'post',
    params: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
  })
}

/**’
 * 单位树
 * @param data
 * @returns {AxiosPromise}
 */
export function orgTreeList(data) {
  return request({
    url: '/teamGroups/orgTreeList',
    method: 'post',
    data,
  })
}

// 获取角色列表
export function getRoleList(data) {
  return request({
    url: '/teamGroups/getRoleList.do',
    method: 'post',
    params: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
  })

}


/**’
 * 单位树
 * @param data
 * @returns {AxiosPromise}
 */
export function getroleDicts(data) {
  return request({
    url: '/teamGroups/getroleDicts.do',
    method: 'post',
    data,
  })
}



/**’
 * 单位树
 * @param data
 * @returns {AxiosPromise}
 */
export function editTeam(data) {
  return request({
    url: '/teamGroups/addTeam.do',
    method: 'post',
    data,
  })
}
