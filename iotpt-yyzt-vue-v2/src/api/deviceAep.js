import request from '@/utils/request'
import axios from "axios";

export function getPageList(data) {
  return request({
    url: '/deviceAep/getPageList',
    method: 'post',
    params: data
  })
}

export function bindDevice(data) {
  return request({
    url: '/deviceAep/bindDevice',
    method: 'post',
    params: data
  })
}

export function delBindDevice(data) {
  return request({
    url: '/deviceAep/delBindDevice',
    method: 'post',
    params: data,
    headers:{
      'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'      //改这里就好了
    },
  })
}

export function getDeviceCollectList(data) {
  return request({
    url: '/deviceAep/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/deviceAep/saveOrUpdate',
    method: 'post',
    data
  })
}

export function getEntity(data) {
  return request({
    url: '/deviceAep/getById',
    method: 'post',
    params: data
  })
}

export function onDel(data) {
  return request({
    url: '/deviceAep/delete',
    method: 'post',
    params: data
  })
}

/**
 * 批量编辑
 */
export function batchUpdate(data) {
  return request({
    url: '/deviceAep/batchUpdate',
    method: 'post',
    data
  })
}

/**
 * 导出
 */
export function toExportTemplate(data) {
  return request({
    url: '/deviceAep/toExportTemplate',
    method: 'get',
    params: data,
    responseType: 'blob'
  })
}

export function getList(data) {
  return request({
    url: '/deviceAep/getList',
    method: 'post',
    data,
    headers:{
      'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'      //改这里就好了
    },
  })
}

// 网关模板下载
export function downloadTemplate(data) {
    return request({
        url: '/deviceAep/downloadTemplate',
        method: 'post',
        responseType: "blob",
        headers: {
            'Content-Type': 'application/x-download'
        },
        params:data,
    })
}

//上传文件
import network from "@/config/net.config.js";
export async function importUploadData(form) {
    var result = {};
    if (null != form.get("file") && "" != form.get("file") && undefined != form.get("file")) {
        await axios.post(network.baseURL + "/deviceAep/importUploadData", form, {
            headers: {
                "Content-Type": "multipart/form-data", "accessToken": localStorage.getItem("vue-admin-beautiful-2021")
            }
        }).then((res) => {
            result = res;
        });
    }
    return result;
}
