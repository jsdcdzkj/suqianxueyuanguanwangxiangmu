import request from '@/utils/request'

/**
 * 配置相关请求
 */
var baseUrl = 'sysConfig'

//获取列表
export function getPageList(data) {
  return request({
    url: baseUrl + '/getPage',
    method: 'post',
    data,
  })
}
//新增编辑
export function saveOrUpdate(data) {
  return request({
    url: baseUrl + '/saveOrUpdate',
    method: 'post',
    data,
  })
}
//删除
export function deleteConfig(data) {
  return request({
    url: baseUrl + '/deleteConfig',
    method: 'post',
    data,
  })
}

//获取水费价格
export function getWaterPrice(data) {
  return request({
    url: '/waterPrice/getWaterPrice',
    method: 'post',
    data,
  })
}

// 修改水费价格
export function setWaterPrice(data) {
  return request({
    url: '/waterPrice/addWaterPrice',
    method: 'post',
    data,
  })
}
