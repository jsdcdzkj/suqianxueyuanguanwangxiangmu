import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/warningConfig/getPageList',
    method: 'post',
    params: data
  })
}

export function getList(data) {
  return request({
    url: '/warningConfig/getList',
    method: 'post',
    params: data
  })
}

export function getWarningConfigList(data) {
  return request({
    url: '/warningConfig/getList',
    method: 'post',
    params: data
  })
}

export function getWarningConfig(data) {
  return request({
    url: '/warningConfig/getWarningConfig',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/warningConfig/saveOrUpdate',
    method: 'post',
    data
  })
}

export function getEntity(data) {
  return request({
    url: '/warningConfig/getEntity',
    method: 'post',
    params: data
  })
}

export function delEntity(data) {
  return request({
    url: '/warningConfig/delEntity',
    method: 'post',
    params: data
  })
}
