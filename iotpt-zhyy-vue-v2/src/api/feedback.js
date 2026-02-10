import request from '@/utils/request'


export function getPageList(data) {
  return request({
    url: '/feedback/getPageList',
    method: 'post',
    data
  })
}

export function feedbackCountAndTrend(data) {
  return request({
    url: '/feedback/feedbackCountAndTrend',
    method: 'post',
    data
  })
}

export function reply(data) {
  return request({
    url: '/feedback/reply',
    method: 'post',
    data
  })
}

export function getEntity(data) {
  return request({
    url: '/feedback/getEntity',
    method: 'post',
    data
  })
}

// 详情明细导出
export function exportData(data) {
  return request({
      url: "/feedback/export",
      method: "post",
      data,
      responseType: "blob"
  });
}


