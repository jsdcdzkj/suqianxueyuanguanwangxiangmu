import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/dataSheet/getPageList',
    method: 'post',
    data
  })
}

export function getDataSheetList(data) {
  return request({
    url: '/dataSheet/getList',
    method: 'post',
    data
  })
}
