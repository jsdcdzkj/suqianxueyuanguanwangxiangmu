import request from '@/utils/request'

// 能耗预测分析图
export function energyForecast(data) {
  return request({
    url: '/statistics/energyForecast',
    method: 'post',
    data,
  })
}

// 湿度温度查询
export function tempReport(data) {
  return request({
    url: '/energyPrediction/tempReport',
    method: 'post',
    data,
  })
}

// 活动参数配置详情
export function configView(data) {
  return request({
    url: '/energyPrediction/configView',
    method: 'post',
    data,
  })
}

// 活动参数配置保存
export function configSaveOrUpd(data) {
  return request({
    url: '/energyPrediction/configSaveOrUpd',
    method: 'post',
    data,
  })
}

// 人员参数详情
export function getFloorList(data) {
  return request({
    url: '/energyPrediction/getFloorList',
    method: 'post',
    data,
  })
}

// 人员参数保存
export function updFloorList(data) {
  return request({
    url: '/energyPrediction/updFloorList',
    method: 'post',
    data,
  })
}

// 室内温度统计
export function getTempData(data) {
  return request({
    url: '/energyPrediction/getTempData',
    method: 'post',
    data,
  })
}

// 室内温度统计
export function getRhData(data) {
  return request({
    url: '/energyPrediction/getRhData',
    method: 'post',
    data,
  })
}