import request from '@/utils/request'

export function allBuildFloolList(data) {
  return request({
    url: '/sys_build_area/allBuildFloorList?buildFloorId='+data,
    method: 'post',
  })
}

export function addBuildArea(data) {
  return request({
    url: '/sys_build_area/addBuildArea',
    method: 'post',
    data,
  })
}

export function selectBuildAreaList(data) {
  return request({
    url: '/sys_build_area/selectBuildAreaList',
    method: 'post',
    data,
  })
}

export function updateBuildArea(data) {
  return request({
    url: '/sys_build_area/updateBuildArea',
    method: 'post',
    data,
  })
}




export function delBuildArea(data) {
  return request({
    url: '/sys_build_area/delBuildArea?id='+data,
    method: 'post'
  })
}

export function info(data) {
  return request({
    url: '/sys_build_area/info?id='+data,
    method: 'post'
  })
}

export function getAllList(data) {
  return request({
    url: '/sys_build_area/getAllList',
    method: 'post',
    data,
  })
}

