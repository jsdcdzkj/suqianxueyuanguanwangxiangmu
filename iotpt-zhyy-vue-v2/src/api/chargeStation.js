import request from '@/utils/request'

export function chargeStatistics(data) {
  return request({
    url: '/station/chargeStatistics',
    method: 'post',
    data
  })
}

export function getList(data) {
  return request({
    url: '/station/getList.do',
    method: 'post',
    data
  })
}

export function stationStatistics(data) {
  return request({
    url: '/station/stationStatistics.do',
    method: 'post',
    data
  })
}

export function getView(data) {
  return request({
    url: '/station/getView.do',
    method: 'post',
    params:data
  })
}
