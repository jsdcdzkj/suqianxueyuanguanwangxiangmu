import request from '@/utils/request'

export function getList(data) {
  return request({
    url: '/deviceLift/getPageList',
    method: 'post',
    data,
  })
}

export function add(data) {
    return request({
      url: '/deviceLift/toAdd.do',
      method: 'post',
      data,
    })
}

export function edit(data) {
    return request({
      url: '/deviceLift/toEdit.do',
      method: 'post',
      data,
    })
}

export function del(data) {
  return request({
    url: '/deviceLift/toDel.do',
    method: 'post',
    data,
  })
}

export function detail(data) {
  return request({
    url: '/deviceLift/toView.do',
    method: 'post',
    data,
  })
}