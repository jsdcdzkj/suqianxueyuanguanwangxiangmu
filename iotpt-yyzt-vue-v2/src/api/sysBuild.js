import request from '@/utils/request'

export function selectBuildList(data) {
  return request({
    url: '/sys_build/selectBuildList',
    method: 'post',
    params: data
  })
}

export function getBuildList(data) {
  return request({
    url: '/sys_build/getList',
    method: 'post',
    params: data
  })
}
