import request from '@/utils/request'


export function userList(data) {
  return request({
    url: '/car/userList',
    method: 'post',
    data
  })
}

export function addCar(data) {
  return request({
    url: '/car/addCar',
    method: 'post',
    data
  })
}

export function updateCar(data) {
  return request({
    url: '/car/updateCar',
    method: 'post',
    data
  })
}

export function selectCarList(data) {
  return request({
    url: '/car/selectCarList',
    method: 'post',
    data
  })
}

export function delCar(data) {
  return request({
    url: '/car/delCar?id='+data,
    method: 'post'
  })
}

export function info(data) {
  return request({
    url: '/car/info?id='+data,
    method: 'post'
  })
}

//车辆布控 -- 列表
export function getPage(data) {
  return request({
    url: '/vehicle/getPage',
    method: 'post',
    params:data
  })
}

//车辆布控 -- 新增 修改
export function insertOrUpdate(data) {
  return request({
    url: '/vehicle/insertOrUpdate',
    method: 'post',
    params:data
  })
}

//车辆布控 -- 删除
export function vehicleDel(data) {
  return request({
    url: '/vehicle/del?id='+data,
    method: 'post'
  })
}

// 模板导入
export function importData(data) {
  return request({
    url: '/car/importData',
    method: 'post',
    params:data
  })
}
// 模板下载
export function exportData(data) {
  return request({
    url: '/car/export',
    method: 'post',
    data,
    responseType: 'blob'
  })
}
// 统计表格
export function carStatistics(data) {
  return request({
    url: '/car/carStatistics',
    method: 'post',
    params:data
  })
}





