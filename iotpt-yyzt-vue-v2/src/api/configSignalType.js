import request from '@/utils/request'

export function getListByDeviceTypeCode(data) {
  return request({
    url: '/configSignalType/getListByDeviceTypeCode',
    method: 'post',
    params: data
  })
}
