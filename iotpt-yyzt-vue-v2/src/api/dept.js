import request from '@/utils/request'

export function addOrgDept(data) {
  return request({
    url: '/sys_org_dept/addOrgDept',
    method: 'post',
    data,
  })
}

export function selectOrgDeptList(data) {
  return request({
    url: '/sys_org_dept/selectOrgDeptList',
    method: 'post',
    data,
  })
}

export function updateOrgDept(data) {
  return request({
    url: '/sys_org_dept/updateOrgDept',
    method: 'post',
    data,
  })
}

export function delOrgDept(data) {
  return request({
    url: '/sys_org_dept/delOrgDept?id=' + data,
    method: 'post',
  })
}

export function info(data) {
  return request({
    url: '/sys_org_dept/info?id=' + data,
    method: 'post',
  })
}

export function findDept(data) {
  return request({
    url: '/sys_org_dept/findDept',
    method: 'post',
    data,
  })
}

// 门禁设备列表
export function getDoorList(data) {
  return request({
    url: '/sysuser/getDoorList',
    method: 'post',
    data,
  })
}

export function SaveAi(data) {
  return request({
    url: '/sysuser/aiAuthority',
    method: 'post',
    data,
  })
}

export function getCarList(data) {
  return request({
    url: 'sysuser/getCarList?userId=' + data,
    method: 'post',
  })
}

export function visitorRegionList(data) {
  return request({
    url: '/sys_org_dept/visitorRegionList',
    method: 'post',
    data,
  })
}

// 同步大华数据
export function visitorRegion(data) {
  return request({
    url: '/sys_org_dept/visitorRegion',
    method: 'post',
    data,
  })
}

export function visitorRegion2(data) {
  return request({
    url: `/sys_org_dept/synchronization?iccId=${data.iccId}&orgId=${data.orgId}`,
    method: 'get',
  })
}
