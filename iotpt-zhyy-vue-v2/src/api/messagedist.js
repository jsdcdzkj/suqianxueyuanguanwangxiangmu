import request from '@/utils/request'

// 分页列表
export function page(data) {
  return request({
    url: '/app/message/config/page',
    method: 'post',
    data
  })
}

// 列表不分页
export function list(data) {
  return request({
    url: '/app/message/config/list',
    method: 'post',
    data
  })
}
// 新增保存
export function save(data) {
  return request({
    url: '/app/message/config/save',
    method: 'post',
    data
  })
}

// 分类查询
export function getType(data) {
  return request({
    url: '/app/message/config/type',
    method: "get",
    params: data,
  })
}
// 删除
export function del(data) {
  return request({
    url: '/app/message/config/delete',
    method: "get",
    params: data,
  })
}
// 根据id查询
export function byId(data) {
  return request({
    url: '/app/message/config/byId',
    method: "get",
    params: data,
  })
}