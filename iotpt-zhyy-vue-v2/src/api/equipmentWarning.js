import request from '@/utils/request'

// 大额用电设备
export function highValueElectricalEquipment(data) {
  return request({
    url: '/smartEnergyReport/highValueElectricalEquipment',
    method: 'post',
    data,
  })
}
// 近7日用电趋势
export function consumptionTrend(data) {
  return request({
    url: '/smartEnergyReport/consumptionTrend',
    method: 'post',
    params:data,
  })
}
// 用电规划列表
export function getList(data) {
  return request({
    url: '/smartEnergyReport/getList',
    method: 'post',
    params: data,
  })
}
// 调整定额
export function updateVal(data) {
  return request({
    url: '/smartEnergyReport/updateVal',
    method: 'get',
    params: data,
  })
}
// 去年同期定额值
export function theSamePeriodLastYear(data) {
  return request({
    url: '/smartEnergyReport/theSamePeriodLastYear',
    method: 'get',
    params: data,
  })
}
// 用能异常数据展示
export function info(data) {
  return request({
    url: '/smartEnergyReport/info',
    method: 'get',
    params: data,
  })
}
// 空载电流平均值
export function noLoadCurrent(data) {
  return request({
    url: '/smartEnergyReport/noLoadCurrent',
    method: 'get',
    params: data,
  })
}
// 每月平均电流
export function averageMonthlyCurrent(data) {
  return request({
    url: '/smartEnergyReport/averageMonthlyCurrent',
    method: 'get',
    params: data,
  })
}

// 分项月定额列表
export function getSubValue(data) {
  return request({
    url: '/smartEnergyReport/getSubValue',
    method: 'get',
    params: data,
  })
}

// 分项调整定额
export function updateSubmiteValue(data) {
  return request({
    url: '/smartEnergyReport/updateSubmiteValue',
    method: 'get',
    params: data,
  })
}

// 分项月数据
export function getSubmitMonth(data) {
  return request({
    url: '/smartEnergyReport/getSubmitMonth',
    method: 'get',
    params: data,
  })
}

// 分项月调整定额
export function setSubmitMonthValue(data) {
  return request({
    url: '/smartEnergyReport/setSubmitMonthValue',
    method: 'get',
    params: data,
  })
}

// 能耗分项监测列表
export function itemizedMonitoringList(data) {
  return request({
    url: '/smartEnergyReport/itemizedMonitoringList',
    method: 'get',
    params: data,
  })
}

// 实时负荷监测
export function getLoad(data) {
  return request({
    url: '/smartEnergyReport/getLoad',
    method: 'get',
    params: data,
  })
}

// 整体指标
export function overall(data) {
  return request({
    url: '/smartEnergyReport/overall.do',
    method: 'get',
    params: data,
  })
}