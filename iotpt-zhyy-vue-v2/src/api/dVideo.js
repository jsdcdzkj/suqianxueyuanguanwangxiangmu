import request from '@/utils/request'

export function getById(data) {
  return request({
    url: '/dVideo/getById',
    method: 'post',
    params: data
  })
}

export function dVideoList(data) {
  return request({
    url: '/dVideo/getPageList',
    method: 'post',
    params: data
  })
}

export function deviceOnlineStatus() {
    return request({
        url: '/dVideo/getDeviceOnlineStatus',
        method: 'get'
    })
}
