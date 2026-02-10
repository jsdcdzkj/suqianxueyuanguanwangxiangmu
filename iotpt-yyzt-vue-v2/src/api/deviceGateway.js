import request from '@/utils/request'
import axios from 'axios'
export function getPageList(data) {
  return request({
    url: '/deviceGateway/getPageList',
    method: 'post',
    params: data,
  })
}

export function getDeviceGatewayList(data) {
  return request({
    url: '/deviceGateway/getList',
    method: 'post',
    params: data,
  })
}

export function saveOrUpdate(data) {
  if (data.id) {
    return request({
      url: '/deviceGateway/edit',
      method: 'post',
      data,
    })
  } else {
    return request({
      url: '/deviceGateway/add',
      method: 'post',
      data,
    })
  }
}

export function getEntity(data) {
  return request({
    url: '/deviceGateway/getEntity',
    method: 'post',
    params: data,
  })
}

export function onDel(data) {
  return request({
    url: '/deviceGateway/delDeviceGateway',
    method: 'post',
    params: data,
  })
}
//上传文件
let urlUpload = 'http://192.168.0.40:8103/deviceGateway/uploadFile'
export async function importData(form) {
  var result = {}
  if (
    null != form.get('file') &&
    '' != form.get('file') &&
    undefined != form.get('file')
  ) {
    await axios
      .post(urlUpload, form, {
        headers: {
          'Content-Type': 'multipart/form-data',
          accessToken: localStorage.getItem('vue-admin-beautiful-2021'),
        },
      })
      .then((res) => {
        result = res
      })
  }
  return result
}

export function exportFile(data) {
  return request({
    url: '/deviceGateway/exportFile',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/x-download',
    },
    params: data,
  })
}

// 网关模板下载
export function downloadTemplate(data) {
  return request({
    url: '/deviceGateway/downloadTemplate',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/x-download',
    },
    params: data,
  })
}

//上传文件
let urlUpload1 = 'http://192.168.0.193:8080/deviceGateway/importUploadData'
// 引用配置文件
import network from '@/config/net.config.js'
export async function importUploadData(form) {
  var result = {}
  if (
    null != form.get('file') &&
    '' != form.get('file') &&
    undefined != form.get('file')
  ) {
    await axios
      .post(network.baseURL + '/deviceGateway/importUploadData', form, {
        headers: {
          'Content-Type': 'multipart/form-data',
          accessToken: localStorage.getItem('vue-admin-beautiful-2021'),
        },
      })
      .then((res) => {
        result = res
      })
  }
  return result
}
