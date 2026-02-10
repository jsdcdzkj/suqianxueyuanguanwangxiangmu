import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/warningInfo/getPageList',
    method: 'post',
    params: data
  })
}

export function getRealtimePageList(data) {
  return request({
    url: '/warningInfo/getRealtimePageList',
    method: 'post',
    params: data
  })
}

