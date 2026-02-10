import request from '@/utils/request'

export function allBuildList(params) {
  return request({
    url: '/credential/getEntityList',
    method: 'get',
    params,
  })
}

export function getBuildList(params) {
  return request({
    url: '/sys_build/allBuildList',
    method: 'post',
  })
}

export function saveOrUpCredential(params) {
  return request({
    url: '/credential/saveOrUpCredential',
    method: 'post',
    data: params,
  })
}

export function delCredential(params) {
  return request({
    url: '/credential/delCredential',
    method: 'get',
    params,
  })
}

export function addBuild(data) {
  return request({
    url: '/sys_build/addBuild',
    method: 'post',
    data: data,
  })
}

export function selectBuildList(data) {
  return request({
    url: '/sys_build/selectBuildList',
    method: 'post',
    data,
  })
}

export function updateBuild(data) {
  return request({
    url: '/sys_build/updateBuild',
    method: 'post',
    data: data,
  })
}

export function delBuild(data) {
  return request({
    url: '/sys_build/delBuild?id=' + data,
    method: 'post',
  })
}

export function info(data) {
  return request({
    url: '/sys_build/info?id=' + data,
    method: 'post',
  })
}

export function bulidTreeList() {
  return request({
    url: '/sys_build/bulidTreeList',
    method: 'post',
  })
}
