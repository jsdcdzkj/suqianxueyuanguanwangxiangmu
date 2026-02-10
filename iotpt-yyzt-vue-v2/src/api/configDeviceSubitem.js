import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/configDeviceSubitem/getPageList',
    method: 'post',
    params: data
  })
}

export function getDeviceSubitemList(data) {
  return request({
    url: '/configDeviceSubitem/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/configDeviceSubitem/saveOrUpdate',
    method: 'post',
    data
  })
}

export function getEntity(data) {
  return request({
    url: '/configDeviceSubitem/getById',
    method: 'post',
    params: data
  })
}

export function onDel(data) {
  return request({
    url: '/configDeviceSubitem/delete',
    method: 'post',
    params: data
  })
}

