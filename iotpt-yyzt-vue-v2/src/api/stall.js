import request from '@/utils/request'

export function addStall(data) {
  return request({
    url: '/stall/addStall',
    method: 'post',
    data
  })
}

export function selectStallList(data) {
  return request({
    url: '/stall/selectStallList',
    method: 'post',
    data
  })
}

export function updateStall(data) {
  return request({
    url: '/stall/updateStall',
    method: 'post',
    data
  })
}

export function updateIsUse(data) {
  return request({
    url: '/stall/updateIsUse',
    method: 'post',
    data
  })
}


export function delStall(data) {
  return request({
    url: '/stall/delStall?id='+data,
    method: 'post'
  })
}

export function stallCount(data) {
  return request({
    url: '/stall/stallCount',
    method: 'post'
  })
}

// 图片查询
export function selectStallImgList(data) {
  return request({
    url: '/stall/selectStallImgList',
    method: 'post',
    data
  })
}
// 查看下载文件
export function selectAreaFile(url, name) {
  const a = document.createElement("a");
  a.style.display = "none";
  a.download = name;
  a.href = url;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
}

// 查看详情
// export function info(data) {
//   return request({
//     url: '/stall/info?id='+data,
//     method: 'post',
//   })
// }

export function info(data) {
  return request({
    url: '/stall/info',
    method: 'post',
    data: {id: data},
  })
}

export function controlGroundLock(data) {
  return request({
    url: '/stall/controlGroundLock',
    method: 'post',
    data
  })
}

export function stallList(data) {
  return request({
    url: '/stall/stallList?id='+data,
    method: 'post'
  })
}

export function stallStatistics(data) {
  return request({
    url: '/stall/stallStatistics',
    method: 'post',
    data
  })
}


export function userList(data) {
  return request({
    url: '/car/userList',
    method: 'post',
    data
  })
}

export function treeData(data) {
  return request({
    url: '/stall/treeData',
    method: 'post',
    data
  })
}

export function parkingUtilizationRate(data) {
  return request({
    url: '/stall/parkingUtilizationRate',
    method: 'post'
  })
}

// 摄像头下拉接口
export function deviceList(data) {
  return request({
    url: '/stall/deviceList',
    method: 'post',
    data
  })
}

// 地锁下拉接口
export function parkingLockList(data) {
  return request({
    url: '/stall/parkingLockList',
    method: 'post',
    data
  })
}






