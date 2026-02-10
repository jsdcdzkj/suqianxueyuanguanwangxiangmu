import request from '@/utils/request'

export function addOrgManage(data) {
  return request({
    url: '/sys_org_manage/addOrgManage',
    method: 'post',
    data,
  })
}

export function selectBuildList(data) {
  return request({
    url: '/sys_org_manage/selectBuildList',
    method: 'post',
    data,
  })
}

export function updateOrgManage(data) {
  return request({
    url: '/sys_org_manage/updateOrgManage',
    method: 'post',
    data,
  })
}

export function orgTreeList(data) {
  return request({
    url: '/sys_org_manage/orgTreeList',
    method: 'post',
    data,
  })
}

export function areaTreeList(data) {
  return request({
    url: '/sys_org_manage/areaTreeList',
    method: 'post',
    data,
  })
}

export function areaTreeList2(data) {
  return request({
    url: '/sys_org_manage/areaTreeList2',
    method: 'post',
    data,
  })
}

export function delOrgManage(data) {
  return request({
    url: '/sys_org_manage/delOrgManage?id=' + data,
    method: 'post',
  })
}

export function info(data) {
  return request({
    url: '/sys_org_manage/info?id=' + data,
    method: 'post',
  })
}

export function findOrg(data) {
  return request({
    url: '/sys_org_manage/findOrg',
    method: 'post',
    data,
  })
}

export function findAllDept(data) {
  return request({
    url: '/sys_org_dept/findAllDept',
    method: 'post',
    data,
  })
}

// 根据单位查询部门
export function findAllDeptByUnit(data) {
  return request({
    url: '/sys_org_dept/findAllDeptByUnit',
    method: 'post',
    data,
  })
}

