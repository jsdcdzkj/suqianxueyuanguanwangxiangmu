import request from '@/utils/request'




//任务类型、状态、数量趋势
export function lineChart(data) {
    return request({
        url: '/statistics/lineChart',
        method: 'post',
        data
    })
}

export function getListByGroup(data) {
  return request({
    url: '/statistics/getListByGroup',
    method: 'post',
    data
  })
}

export function getList(data) {
  return request({
    url: '/statistics/getList',
    method: 'post',
    data
  })
}


//任务类型、状态、数量趋势
export function getMission_type(data) {
  return request({
    url: '/statistics/getMission_type',
    method: 'post',
    data
  })
}

//任务类型、状态、数量趋势
export function getMission_state(data) {
  return request({
    url: '/statistics/getMission_state',
    method: 'post',
    data
  })
}

//任务类型、状态、数量趋势
export function getMission_type_team(data) {
  return request({
    url: '/statistics/getMission_type_team',
    method: 'post',
    data
  })
}

//Excel导出
export function getListExport(data) {
  return request({
    url: '/statistics/getListExport',
    method: 'post',
    responseType: "blob",
    headers: {
      'Content-Type': 'application/x-download'
    },
    params:data,
  })
}

export function statistics(data) {
  return request({
    url: '/statistics/statistics',
    method: 'post',
    data
  })
}

export function numberOfDenominations(data) {
  return request({
    url: '/statistics/numberOfDenominations',
    method: 'post',
    data
  })
}

export function energyEvaluate(data) {
  return request({
    url: '/statistics/energyEvaluate.do',
    method: 'post',
    data
  })
}


export function numberGjOfDays(data) {
    return request({
        url: '/statistics/numberGjOfDays',
        method: 'post',
        data
    })
}

export function monthAlarm(data){
    return request({
        url: '/statistics/monthAlarm',
        method: 'post',
        data
    })
}

export function alarmStatistics(data){
    return request({
        url: '/statistics/alarmStatistics',
        method: 'post',
        data
    })
}


