import request from '@/utils/request'

export function areaTreeList(data) {
  return request({
    url: '/scene/areaTreeList2',
    method: 'post',
    data,
  })
}

export function getList(data) {
  return request({
    url: '/scene/getList',
    method: 'post',
    data,
  })
}

export function addScene(data) {
  return request({
    url: '/scene/addScene',
    method: 'post',
    data,
  })
}

export function updateScene(data) {
  return request({
    url: '/scene/updateScene',
    method: 'post',
    data,
  })
}

export function delScene(data) {
  return request({
    url: '/scene/delScene',
    method: 'post',
    params: data
  })
}

export function info(data) {
  return request({
    url: '/scene/info',
    method: 'post',
    params: data
  })
}
