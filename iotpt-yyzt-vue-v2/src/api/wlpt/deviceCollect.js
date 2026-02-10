import request from '@/utils/request'
import axios from "axios";
import network from "@/config/net.config.js";

// 采集终端模板下载
export function downloadTemplate(data) {
    return request({
        url: '/deviceCollect/downloadTemplate',
        method: 'get',
        responseType: "blob",
        headers: {
            'Content-Type': 'application/x-download'
        },
        params:data,
    })
}

//采集终端导入
export async function importUploadData(form) {
    var result = {};
    if (null != form.get("file") && "" != form.get("file") && undefined != form.get("file")) {
        await axios.post(network.baseURL + "/deviceCollect/importUploadData", form, {
            headers: {
              "Content-Type": "multipart/form-data", "accessToken": localStorage.getItem("vue-admin-beautiful-2021")
            }
        }).then((res) => {
            result = res;
        });
    }
    return result;
}

// 视频设备模板下载
export function downloadVideoTemplate(data) {
  return request({
      url: '/DeviceVideo/downloadTemplate',
      method: 'get',
      responseType: "blob",
      headers: {
          'Content-Type': 'application/x-download'
      },
      params:data,
  })
}

//视频设备导入
export async function importUploadVideoData(form) {
  var result = {};
  if (null != form.get("file") && "" != form.get("file") && undefined != form.get("file")) {
      await axios.post(network.baseURL + "/DeviceVideo/importUploadData", form, {
          headers: {
            "Content-Type": "multipart/form-data", "accessToken": localStorage.getItem("vue-admin-beautiful-2021")
          }
      }).then((res) => {
          result = res;
      });
  }
  return result;
}