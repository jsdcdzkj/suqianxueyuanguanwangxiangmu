import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/configDeviceSignalMap/getPageList',
    method: 'get',
    params: data
  })
}

export function getSignalType(data) {
  return request({
    url: '/configDeviceSignalMap/getSignalType',
    method: 'get',
    params: data
  })
}

export function getConfigLinkList(data) {
  return request({
    url: '/configDeviceSignalMap/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/configDeviceSignalMap/saveOrUpdate',
    method: 'post',
    params:data
  })
}

export function getEntityByTId(data) {
  return request({
    url: '/configDeviceSignalMap/getEntityByTId',
    method: 'get',
    params: data
  })
}

export function delConfigDeviceType(data) {
  return request({
    url: '/configDeviceType/delConfigDeviceType',
    method: 'post',
    params: data
  })
}
