import request from '@/utils/request'

export function getDeviceTypeTop(data) {
  return request({
    url: '/smartEnergyReport/getDeviceTypeTop.do',
    method: 'post',
    data,
  })
}

export function getAreaDeviceTop(data) {
  return request({
    url: '/smartEnergyReport/getDeviceFloorTop.do',
    method: 'post',
    data,
  })
}
