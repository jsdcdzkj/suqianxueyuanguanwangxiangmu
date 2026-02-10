import request from '@/utils/request'

export function selectBuildFloorList(data) {
  return request({
    url: '/sys_build_floor/selectBuildFloorList',
    method: 'post',
    params: data
  })
}

export function getBuildFloorList(data) {
  return request({
    url: '/sys_build_floor/getList',
    method: 'post',
    params: data
  })
}
