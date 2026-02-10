import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/windowCollect/getWindowPageList',
    method: 'post',
    data,
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/windowCollect/saveOrUpdate',
    method: 'post',
    data,
  })
}

export function getEntity1(data) {
  return request({
    url: '/windowCollect/getEntity',
    method: 'post',
    data,
  })
}

export function delwindow(data) {
  return request({
    url: '/windowCollect/delwindow',
    method: 'post',
    params:data,
  })
}