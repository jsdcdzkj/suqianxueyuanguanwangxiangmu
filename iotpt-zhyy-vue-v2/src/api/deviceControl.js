import request from '@/utils/request'

export function getList(data) {
  return request({
    url: '/deviceControl/getList',
    method: 'post',
    params:data
  })
}

export function isControl(data) {
  return request({
    url: '/deviceControl/isControl',
    method: 'post',
    params:data
  })
}




