import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/warningDispose/getPageList',
    method: 'post',
    params: data
  })
}

export function getList(data) {
  return request({
    url: '/warningDispose/getList',
    method: 'post',
    params: data
  })
}

