import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/sys_build_area/getPageList',
    method: 'post',
    params: data
  })
}

export function getBuildAreaList(data) {
  return request({
    url: '/sys_build_area/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/sys_build_area/saveOrUpdate',
    method: 'post',
    data
  })
}

export function getEntity(data) {
  return request({
    url: '/sys_build_area/getById',
    method: 'post',
    params: data
  })
}

export function onDel(data) {
  return request({
    url: '/sys_build_area/delete',
    method: 'post',
    params: data
  })
}


export function areaTreeList(data) {
  return request({
      url: '/sys_org_manage/areaTreeList',
      method: 'post',
      data,
  })
}
