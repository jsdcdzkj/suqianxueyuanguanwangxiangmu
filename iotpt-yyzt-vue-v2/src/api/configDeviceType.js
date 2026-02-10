import { baseURL } from '@/config'
import request from '@/utils/request'
import axios from 'axios'

export function getPageList(data) {
  return request({
    url: '/configDeviceType/getPageList',
    method: 'get',
    params: data,
  })
}

export function getDeviceTypeList(data) {
  return request({
    url: '/configDeviceType/getList',
    method: 'post',
    params: data,
  })
}

export function getConfigLinkList(data) {
  return request({
    url: '/configDeviceType/getList',
    method: 'post',
    params: data,
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/configDeviceType/saveOrUpdate',
    method: 'post',
    data,
  })
}

export function getEntity(data) {
  return request({
    url: '/configDeviceType/getEntity',
    method: 'post',
    params: data,
  })
}

export function delConfigDeviceType(data) {
  return request({
    url: '/configDeviceType/delConfigDeviceType',
    method: 'post',
    params: data,
  })
}
export function findMerchant(data) {
  return request({
    url: '/sys_org_manage/findMerchant',
    method: 'get',
    params: data,
  })
}
//上传文件
let urlUpload = baseURL + '/minio/importFile'

export async function importData(form) {
  console.log('222222222222221111111111112', form)
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
        console.log('5555555555555', res)
        result = res
      })
  }
  return result
}
