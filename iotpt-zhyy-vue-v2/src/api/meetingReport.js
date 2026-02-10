import request from '@/utils/request'

/**
 * 会议统计分析
 */

/**
 * 会议室总量、小会议室、大会议室、多功能会议室
 */
export function getInfo(data) {
  return request({
    url: '/meetingReport/getInfo',
    method: 'post',
    params: data
  })
}

export function orgTreeList(data) {
  return request({
    url: '/meetingReport/orgTreeList',
    method: 'post',
    data,
  })
}

/**
 * 会议室楼层分布统计
 */
export function floorReport(data) {
  return request({
    url: '/meetingReport/floorReport',
    method: 'post',
    params: data
  })
}

/**
 * 使用频率
 */
export function useCountReport(data) {
  return request({
    url: '/meetingReport/useCountReport',
    method: 'post',
    params: data
  })
}

/**
 * 会议室使用趋势分析
 */
export function useTrendsReport(data) {
  return request({
    url: '/meetingReport/useTrendsReport',
    method: 'post',
    params: data
  })
}

