import request from '@/utils/request'

export function userCount() {
    return request({
      url: '/chargeOrder/userCount',
      method: 'post',
    })
}


export function getChargeOrder() {
    return request({
      url: '/chargeOrder/getChargeOrder',
      method: 'post',
    })
}

export function getPage(data) {
  return request({
    url: '/chargeOrder/getPageList',
    method: 'post',
    params:data
  })
}
