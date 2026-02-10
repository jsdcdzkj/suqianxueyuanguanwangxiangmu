import request from '@/utils/request'


export function areaTreeListVideoH(data) {
    return request({
        url: '/deviceCollect/areaTreeListVideoH',
        method: 'post',
        data,
    })
}

export function areaTreeList(data) {
    return request({
        url: '/dVideo/getPageListAll',
        method: 'post',
        params:data,
    })
}

export function getPageListAllTree(data) {
    return request({
        url: '/dVideo/getPageListAllTree',
        method: 'post',
        params:data,
    })
}

export function areaTreeListVideo(data) {
    return request({
        url: '/deviceCollect/areaTreeListVideo',
        method: 'post',
        data,
    })
}

export function getVideoPageList(data) {
    return request({
        url: '/videoPull/getVideoPageList',
        method: 'post',
        data,
    })
}

export function saveOrUpdateVideo(data) {
    return request({
        url: '/videoPull/saveOrUpdate',
        method: 'post',
        data,
    })
}

export function  getRtspAddrApi(data) {
  return request({
    url: '/videoPull/getRTSPAddr',
    method: 'post',
    data,
  })
}
export function getEntity(data) {
    return request({
        url: '/videoPull/getEntity',
        method: 'post',
        data,
    })
}

export function delVideoPull(data) {
    return request({
        url: '/videoPull/delVideoPull',
        method: 'post',
        data,
    })
}

export function startUp(data) {
    return request({
        url: '/videoPull/startUp',
        method: 'post',
        params:{id:data},
    })
}
export function startDow(data) {
    return request({
        url: '/videoPull/startDow',
        method: 'post',
        params:{id:data},
    })
}
export function startLeft(data) {
    return request({
        url: '/videoPull/startLeft',
        method: 'post',
        params:{id:data},
    })
}
export function startRight(data) {
    return request({
        url: '/videoPull/startRight',
        method: 'post',
        params:{id:data},
    })
}
export function endUp(data) {
    return request({
        url: '/videoPull/endUp',
        method: 'post',
        params:{id:data},
    })
}
export function endDow(data) {
    return request({
        url: '/videoPull/endDow',
        method: 'post',
        params:{id:data},
    })
}
export function endLeft(data) {
    return request({
        url: '/videoPull/endLeft',
        method: 'post',
        params:{id:data},
    })
}
export function endRight(data) {
    return request({
        url: '/videoPull/endRight',
        method: 'post',
        params:{id:data},
    })
}
export function playerPull(data) {
    return request({
        url: '/videoPull/playerPull',
        method: 'post',
        data,
    })
}

export function receive(data) {
  return request({
    url: '/receive',
    method: 'post',
    data,
  })
}

export function  getAddr() {
    return request({
      url: '/videoPull/getAddr',
      method: 'post',
    })
}

export function playbackURLs(data) {
    return request({
      url: '/dVideo/replay_HLS',
      method: 'post',
      data,
    })
}

// 重点区域接口
// 新增
export function saveKeyAreas(data) {
    return request({
      url: '/keyAreas/saveKeyAreas',
      method: 'post',
      data,
    })
}
// 删除重点区域
export function delKeyAreas(data) {
    return request({
      url: '/keyAreas/delKeyAreas',
      method: "get",
      params: data,
    })
}
// 删除重点区域摄像头
export function delKeyAreasMap(data) {
    return request({
      url: '/keyAreas/delKeyAreasMap',
      method: "get",
      params: data,
    })
}

// 查询重点区域列表
export function getKeyAreas(data) {
    return request({
      url: '/keyAreas/getKeyAreas',
      method: 'post',
      data,
    })
}