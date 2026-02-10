import request from '@/utils/request'

export function getPage(data) {
  return request({
    url: '/stallCar/getPage',
    method: 'post',
    params:data
  })
}

export function getTime(data) {
  return request({
    url: '/stallCar/getTime',
    method: 'post',
    params:data
  })
}


export function sevenProportion(data) {
  return request({
    url: '/stallCar/sevenProportion',
    method: 'post',
    params:data
  })
}

export function sevenTop(data) {
  return request({
    url: '/stallCar/sevenTop',
    method: 'post',
    params:data
  })
}



