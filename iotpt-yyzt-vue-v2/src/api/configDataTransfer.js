import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/configDataTransfer/getPageList',
    method: 'post',
    params: data
  })
}

export function getList(data) {
  return request({
    url: '/configDataTransfer/getList',
    method: 'post',
    params: data
  })
}
export function getTransferList(data) {
  return request({
    url: '/configDataTransfer/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/configDataTransfer/saveOrUpdate',
    method: 'post',
    data
  })
}
export function del(data) {
  return request({
    url: '/configDataTransfer/del',
    method: 'post',
    params: data
  })
}

export function getEntity(data) {
  return request({
    url: '/configDataTransfer/getEntity',
    method: 'post',
    params: data
  })
}

//获取mqtt数据
export function getDatas(data) {
  return request({
    url: '/configDataTransfer/getDatas',
    method: 'post',
    data
  })
}

//获取mqtt数据
export function parseTest(data) {
  return request({
    url: '/configDataTransfer/parseTest',
    method: 'post',
    data
  })
}
