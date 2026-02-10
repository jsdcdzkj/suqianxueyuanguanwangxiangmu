import request from '@/utils/request'

export function getList(data) {
  return request({
    url: '/warningInfoDetails/getList',
    method: 'post',
    params: data
  })
}
