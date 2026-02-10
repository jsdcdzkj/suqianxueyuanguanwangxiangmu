import request from '@/utils/request'

export function getPageList(data) {
  return request({
    url: '/configTopic/getPageList',
    method: 'post',
    params: data
  })
}

export function getList(data) {
  return request({
    url: '/configTopic/getList',
    method: 'post',
    params: data
  })
}

export function getConfigTopicList(data) {
  return request({
    url: '/configTopic/getList',
    method: 'post',
    params: data
  })
}

export function saveOrUpdate(data) {
  return request({
    url: '/configTopic/saveOrUpdate',
    method: 'post',
    params: data
  })
}

export function getEntity(data) {
  return request({
    url: '/configTopic/getEntity',
    method: 'post',
    params: data
  })
}

export function addTopic(data) {
  return request({
    url: '/configTopic/addTopic',
    method: 'post',
    params: data
  })
}

export function removeTopic(data) {
  return request({
    url: '/configTopic/removeTopic',
    method: 'post',
    params: data
  })
}


export function runStops(data) {
  return request({
    url: '/configTopic/runStops',
    method: 'post',
    params: data
  })
}


export function runStarts(data) {
  return request({
    url: '/configTopic/runStarts',
    method: 'post',
    params: data
  })
}
