import request from '@/utils/request'

/**
 * 分页查询检查项模板
 * @param data
 * @returns {AxiosPromise}
 */
export function getList(data) {
  return request({
    url: '/checkTemlate/getCheckPage.do',
    method: 'post',
    params: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
  })
}


/**
 * 查询所有模板类型
 * @param data
 * @returns {AxiosPromise}
 */
export function getTypeList(data) {
  return request({
    url: '/checkTemlate/getAllTypeList.do',
    method: 'post',
    params: data,
  })
}


/**
 * 删除模板
 * @param data
 * @returns {AxiosPromise}
 */
export function delCheckTem(data) {
  return request({
    url: '/checkTemlate/delCheckTem.do',
    method: 'post',
    params: data,
  })
}


/**
 * 修改状态
 * @param data
 * @returns {AxiosPromise}
 */
export function setIsAble(data) {
  return request({
    url: '/checkTemlate/setIsAble.do',
    method: 'post',
    params: data,
  })
}


/**
 * 复制功能
 * @param data
 * @returns {AxiosPromise}
 */
export function copyTemplate(data) {
  return request({
    url: '/checkTemlate/copyTemplate.do',
    method: 'post',
    params: data,
  })
}

/**
 * 新增 编辑模板
 * @param data
 * @returns {AxiosPromise}
 */
export function addCheck(data) {
  return request({
    url: '/checkTemlate/addCheck.do',
    method: 'post',
    data,
  })
}


/**
 * 查询模板
 * @param data
 * @returns {AxiosPromise}
 */
export function getOneDetail(data) {
  return request({
    url: '/checkTemlate/getOneDetail.do',
    method: 'post',
    params: data,
  })
}


/**
 * 查询所有设备类型
 * @param data
 * @returns {AxiosPromise}
 */
export function getAllDeviceType() {
  return request({
    url: '/checkTemlate/getAllDeviceType.do',
    method: 'post',
  })
}



