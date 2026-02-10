import request from '@/utils/request'

export function getPageListApi(data) {
  return request({
    url: '/abnormalPatrol/getPageList',
    method: 'post',
    params: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
  })
}

export function getTeamGroupListApi() {
  return request({
    url: '/abnormalPatrol/getTeamGroupList',
    method: 'post',
  })
}

export function saveOperationApi(data) {
  return request({
    url: '/abnormalPatrol/saveOperation',
    method: 'post',
    data
  })
}
