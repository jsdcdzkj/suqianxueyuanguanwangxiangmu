import request from '@/utils/request'

// 获取窗帘列表
export function selectByMeetingId(data) {
  return request({
    url: '/curtain/selectByMeetingId',
    method: 'get',
    params: data
  })
}

// 控制窗帘升降
export function curtainControl(data) {
  return request({
    url: '/curtain/curtainControl',
    method: 'post',
    data
  })
}
// 获取窗帘列表
export function getMeetingRoomPage(data) {
  return request({
      url: "/meetingRoom/getMeetingRoomPage",
      method: "post",
      params: data,
      headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
      }
  });
}
