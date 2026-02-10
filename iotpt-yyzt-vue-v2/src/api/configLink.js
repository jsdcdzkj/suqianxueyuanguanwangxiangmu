import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/configLink/getPageList',
    method: 'post',
    params: data
  })
}

export function getList(data) {
  return request({
    url: '/configLink/getList',
    method: 'post',
    params: data
  })
}

export function getConfigLinkList(data) {
  return request({
    url: '/configLink/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/configLink/saveOrUpdate',
    method: 'post',
    data
  })
}

export function getEntity(data) {
  return request({
    url: '/configLink/getEntity',
    method: 'post',
    params: data
  })
}

export function addMqttConnect(data) {
  return request({
    url: '/configLink/addMqttConnect',
    method: 'post',
    params: data
  })
}

export function removeMqttConnect(data) {
  return request({
    url: '/configLink/removeMqttConnect',
    method: 'post',
    params: data
  })
}

export function testMqttConnect(data) {
  return request({
    url: '/configLink/testMqttConnect',
    method: 'post',
    data
  })
}
