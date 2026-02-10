import request from '@/utils/request'
import { encryptedData } from '@/utils/encrypt'
import { loginRSA, tokenName } from '@/config'

export async function login(data) {
  // if (loginRSA) {
  //   data = await encryptedData(data)
  // }

  return request({
    url: '/login.do',
    method: 'post',
    data,
  })
}

export function getUserInfo(accessToken) {
  return request({
    url: '/userInfo.do',
    method: 'post',
    data: {
      [tokenName]: accessToken,
    },
    // 重写请求头 content-type 为 application/x-www-form-urlencoded
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
  })
}

export function userInfoPermissions() {
  return request({
    url: '/userInfoPermissions.do',
    method: 'post',
  })
}

export function logout() {
  return request({
    url: '/logout.do',
    method: 'post',
  })
}

export function register() {
  return request({
    url: '/register',
    method: 'post',
  })
}

export function getWarning() {
  return request({
    url: '/getWarning.do',
    method: 'post',
  })
}

export function upeWarning(data) {
  return request({
    url: '/upeWarning.do',
    method: 'post',
    params: data,
  })
}

export function addCar(data) {
  return request({
    url: '/sysuser/addCar',
    method: 'post',
    data: data,
  })
}

export function accessAuthority(data) {
  return request({
    url: '/sysuser/accessAuthority',
    method: 'post',
    data: data,
  })
}

// sysuser/syncAccountInfo
