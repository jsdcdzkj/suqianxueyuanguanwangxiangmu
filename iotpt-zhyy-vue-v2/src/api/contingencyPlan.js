import request from '@/utils/request'



export function addContingencyPlan(data) {
  return request({
    url: '/contingency_plan/addContingencyPlan',
    method: 'post',
    data,
  })
}

export function contingencyPlanList(data) {
  return request({
    url: '/contingency_plan/contingencyPlanList',
    method: 'post',
    data,
  })
}

export function updateContingencyPlan(data) {
  return request({
    url: '/contingency_plan/updateContingencyPlan',
    method: 'post',
    data,
  })
}




export function delContingencyPlan(data) {
  return request({
    url: '/contingency_plan/delContingencyPlan?id='+data,
    method: 'post'
  })
}

export function info(data) {
  return request({
    url: '/contingency_plan/info?id='+data,
    method: 'post'
  })
}


