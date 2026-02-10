import request from "@/utils/request";

export function getList(data) {
  return request({
    url: "/deviceCollect/getZNXZ",
    method: "post",
    params: data
  });
}

export function totalInfo(params) {
  return request({
    url: "/chint/device/point/statistics",
    method: "post",
    params
  });
}

export function oneControl(data) {
  return request({
    url: "/deviceCollect/remoteControlZNXZ",
    method: "post",
    data
  });
}

export function areaControl(params) {
  return request({
    url: "/chint/device/point/switchBySpace",
    method: "get",
    params
  });
}

export function allControl(params) {
  return request({
    url: "/chint/device/point/master",
    method: "get",
    params
  });
}

export function radarControl(params) {
  return request({
    url: "/chint/device/point/radar",
    method: "get",
    params
  });
}

export function airLog(data) {
  return request({
    url: "/deviceCollect/air17Log",
    method: "get",
    params: data
  });
}

export function energyTrendAVG(data) {
  return request({
    url: "/deviceCollect/energyTrendAVG",
    method: "post",
    params: data
  });
}

export function getReportingListAll(data) {
  return request({
    url: "/deviceCollect/getReportingListAll",
    method: "post",
    params: data
  });
}

export function energyTrend(data) {
  return request({
    url: "/deviceCollect/energyTrend_ZHCZ",
    method: "post",
    params: data
  });
}
export function getReportingListPage(data) {
  return request({
    url: "/deviceCollect/getReportingListPage",
    method: "post",
    params: data
  });
}
export function getEnetgyAll(data) {
  return request({
    url: "/deviceCollect/getEnetgyAll",
    method: "post",
    params: data
  });
}
