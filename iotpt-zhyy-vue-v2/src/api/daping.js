import request from "@/utils/request";


export function saveOrUpdate(data) {
  return request({
    url: "/daping/config/saveOrUpdate",
    method: "post",
    data
  });
}

export function dapingInfo(data) {
  return request({
    url: "/daping/config/latest",
    method: "get",
    data
  });
}
