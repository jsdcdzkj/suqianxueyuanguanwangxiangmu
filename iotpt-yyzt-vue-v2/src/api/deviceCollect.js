import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/deviceCollect/getPageList',
    method: 'post',
    params: data
  })
}

export function bindDevice(data) {
  return request({
    url: '/deviceCollect/bindDevice',
    method: 'post',
    params: data
  })
}

export function delBindDevice(data) {
  return request({
    url: '/deviceCollect/delBindDevice',
    method: 'post',
    params: data,
    headers:{
      'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'      //改这里就好了
    },
  })
}

export function getDeviceCollectList(data) {
  return request({
    url: '/deviceCollect/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/deviceCollect/saveOrUpdate',
    method: 'post',
    data
  })
}

export function getEntity(data) {
  return request({
    url: '/deviceCollect/getById',
    method: 'post',
    params: data
  })
}

export function onDel(data) {
  return request({
    url: '/deviceCollect/delete',
    method: 'post',
    params: data
  })
}

/**
 * 批量编辑
 */
export function batchUpdate(data) {
  return request({
    url: '/deviceCollect/batchUpdate',
    method: 'post',
    data
  })
}

/**
 * 导出
 */
export function toExportTemplate(data) {
  return request({
    url: '/deviceCollect/toExportTemplate',
    method: 'get',
    params: data,
    responseType: 'blob'
  })
}

export function getList(data) {
  return request({
    url: '/deviceCollect/getList',
    method: 'post',
    data,
    headers:{
      'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'      //改这里就好了
    },
  })
}
