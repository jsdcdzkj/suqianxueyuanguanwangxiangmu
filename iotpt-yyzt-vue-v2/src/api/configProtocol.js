import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/configProtocol/getPageList',
    method: 'post',
    params: data
  })
}

export function getList(data) {
  return request({
    url: '/configProtocol/getList',
    method: 'post',
    params: data
  })
}
export function getConfigProtocolList(data) {
  return request({
    url: '/configProtocol/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/configProtocol/saveOrUpdate',
    method: 'post',
    data
  })
}

export function getEntity(data) {
  return request({
    url: '/configProtocol/getEntity',
    method: 'post',
    params: data
  })
}
