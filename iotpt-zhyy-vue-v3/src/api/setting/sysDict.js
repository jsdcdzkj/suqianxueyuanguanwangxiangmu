import request from '@/utils/request'

/**
 * redis
 * 数据字典列表
 */
export function getRedisDictList(data) {
  return request({
    url: '/sysDict/getRedisDictList',
    method: 'post',
    params: data,
  })
}

export function selectDictList(data) {
  return request({
    url: '/sysDict/selectDictList',
    method: 'post',
    params: data,
  })
}
