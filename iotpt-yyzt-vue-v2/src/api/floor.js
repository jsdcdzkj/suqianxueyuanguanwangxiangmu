import request from '@/utils/request'

export function allBuildList() {
  return request({
    url: '/sys_build_floor/allBuildList',
    method: 'post',
  })
}

export function addBuildFlool(data) {
  return request({
    url: '/sys_build_floor/addBuildFloor',
    method: 'post',
    data,
  })
}

export function selectBuildFloolList(data) {
  return request({
    url: '/sys_build_floor/selectBuildFloorList',
    method: 'post',
    data,
  })
}

export function getList(data) {
  return request({
    url: '/sys_build_floor/getList',
    method: 'post',
    data,
  })
}
export function getAllList(data) {
  return request({
    url: '/sys_build_floor/getAllList',
    method: 'post',
    data,
  })
}

export function updateBuildFlool(data) {
  return request({
    url: '/sys_build_floor/updateBuildFloor',
    method: 'post',
    contentType:'multipart/form-data',
    data,
  })
}

export function info(data) {
  return request({
    url: '/sys_build_floor/info?id='+data,
    method: 'post'
  })
}

export function delBuildFloor(data) {
  return request({
    url: '/sys_build_floor/delBuildFloor?id='+data,
    method: 'post'
  })
}


