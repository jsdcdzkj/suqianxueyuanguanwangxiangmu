import request from '@/utils/request'


export function getTreeBuild() {
  return request({
    url: '/planArea/getTreeBuild.do',
    method: 'get',
  })
}

export function getTreeBuildDisable() {
  return request({
    url: '/planArea/getTreeBuildDisable.do',
    method: 'get',
  })
}

export function getGroupList() {
  return request({
    url: '/teamGroups/getAllList.do',
    method: 'post',
  })
}


export function getTemplateList() {
  return request({
    url: '/checkTemlate/getAllList.do',
    method: 'post',
  })
}

export function getPageList(data) {
  return request({
    url: '/jobPlan/getPageList.do',
    method: 'post',
    params: data,
  })
}

export function getRedisDictList(data) {
  return request({
    url: '/sysDict/getRedisDictList',
    method: 'post',
    params: data,
  })
}

export function getJobPlanById(data) {
  return request({
    url: '/jobPlan/getJobPlanById.do',
    method: 'post',
    params: data,
  })
}

export function toAdd(data) {
  return request({
    url: '/jobPlan/toAdd.do',
    method: 'post',
    data: data,
  })
}

export function toEdit(data) {
  return request({
    url: '/jobPlan/toEdit.do',
    method: 'post',
    data: data,
  })
}

export function isEnablePlan(data) {
  return request({
    url: '/jobPlan/isEnablePlan.do',
    method: 'post',
    params: data,
  })
}

export function toDel(data) {
  return request({
    url: '/jobPlan/toDel.do',
    method: 'post',
    params: data,
  })
}
