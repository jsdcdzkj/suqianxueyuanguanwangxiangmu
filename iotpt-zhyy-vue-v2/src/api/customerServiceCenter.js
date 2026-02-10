import request from '@/utils/request'

export function infomationList(data) {
  return request({
    url: '/informationBulletin/getPage',
    method: 'post',
    params: data
  })
}

export function infomationDetail(data) {
  return request({
    url: '/informationBulletin/getDetails',
    method: 'post',
    data
  })
}

export function infomationSave(data) {
  return request({
    url: '/informationBulletin/saveOrUpdate',
    method: 'post',
    data
  })
}

// 查看公告人群分类
export function releaseUnit(data) {
  return request({
    url: '/informationBulletin/releaseUnit',
    method: 'post',
    data
  })
}