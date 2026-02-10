import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/configModel/getPageList',
    method: 'post',
    params: data
  })
}

export function getConfigModelList(data) {
  return request({
    url: '/configModel/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/configModel/saveOrUpdate',
    method: 'post',
    data
  })
}

export function getEntity(data) {
  return request({
    url: '/configModel/getById',
    method: 'post',
    params: data
  })
}

export function onDel(data) {
  return request({
    url: '/configModel/delete',
    method: 'post',
    params: data
  })
}

