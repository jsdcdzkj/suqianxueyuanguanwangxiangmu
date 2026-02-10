import request from '@/utils/request'

/**
 * 用户管理
 */
var baseUrl = '/sysuser'

// 获取用户列表分页
export function getList(data) {
  return request({
    url: baseUrl + '/getUserPage.do',
    method: 'post',
    params: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
  })
}

// 启用禁用状态
export function doEnable(data) {
  return request({
    url: baseUrl + '/doEnable.do',
    method: 'post',
    params: data,
  })
}

// 新增或者修改用户
export function doEdit(data) {
  if (data.id) {
    return request({
      url: baseUrl + '/editUser.do',
      method: 'post',
      data,
    })
  } else {
    return request({
      url: baseUrl + '/addUser.do',
      method: 'post',
      data,
    })
  }
}

// 删除用户
export function doDelete(data) {
  return request({
    url: baseUrl + '/delUser.do',
    method: 'post',
    params: data,
  })
}

// 用户列表
export function getRedisUserDictMap(data) {
  return request({
    url: baseUrl + '/getRedisUserDictMap',
    method: 'post',
    data,
  })
}

// 用户同步
export function personnelSynchronization(data) {
  return request({
    url: '/sysuser/personnelSynchronization',
    method: 'post',
    data,
  })
}

export function syncAccountInfo() {
  return request({
    url: '/sysuser/syncAccountInfo',
    method: 'post',
  })
}
