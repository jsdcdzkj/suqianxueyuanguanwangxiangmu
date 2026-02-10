import request from '@/utils/request'

export function getPage(data) {
  return request({
    url: '/nameList/getPage',
    method: 'post',
    params: data
  })
}

export function getFile(data) {
  return request({
    url: '/nameList/getFile',
    method: 'post',
    params: data
  })
}

export function insertOrUpdate(data) {
  return request({
    url: '/nameList/insertOrUpdate',
    method: 'post',
    responseType: "blob",
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data,
  })
}

export function saveOrUpdStranger(data) {
  return request({
    url: '/nameList/saveOrUpdStranger',
    method: 'post',
    responseType: "blob",
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data,
  })
}

export function del(data) {
  return request({
    url: '/nameList/del',
    method: 'post',
    params: data
  })
}

export function enableDisable(data) {
  return request({
    url: '/nameList/enableDisable',
    method: 'post',
    params: data
  })
}

export function getDoorList(data) {
  return request({
    url: '/nameList/getDoorList',
    method: 'post',
    params: data
  })
}
