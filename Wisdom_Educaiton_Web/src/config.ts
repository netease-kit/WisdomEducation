/*
 * @Author: lizhaoxuan
 * @Date: 2021-05-20 14:58:34
 * @LastEditTime: 2021-06-21 20:33:07
 * @LastEditors: Please set LastEditors
 * @Description: 通用配置项
 * @FilePath: /app_wisdom_education_web/src/config.ts
 */


export enum RoomTypes {
  oneToOne = 5,
  smallClass = 6,
  bigClass = 7,
}

export enum RoleTypes {
  host = 'host',
  broadcaster = 'broadcaster',
  audience = 'audience'
}

export enum StepTypes {
  init = 0,
  isStart = 1,
  isEnd = 2,
}

export enum PauseTypes {
  underway = 0,
  isPause = 1,
}

export enum HandsUpTypes {
  init = 0,
  studentHandsup = 1,
  teacherAgree = 2,
  teacherReject = 3,
  studentCancel = 4,
  teacherOff = 5,
}

export interface UserComponentData {
  userName: string;
  userUuid: string;
  role: string;
  rtcUid: string|number;
  hasAudio: boolean,
  hasVideo: boolean,
  hasScreen: boolean,
  audioStream: any,
  basicStream: any;
  isLocal: boolean;
  showUserControl: boolean;
  showMoreBtn: boolean;
  canScreenShare?: boolean;
  wbDrawEnable?: boolean;
  avHandsUp?: number|null;
}

export enum NIMNotifyTypes {
  RoomStatesChange = 1,
  RoomStatesDelete = 2,
  RoomPropertiesChange = 10,
  RoomPropertiesDelete = 11,
  RoomMemberPropertiesChange = 20,
  RoomMemberPropertiesDelete = 21,
  RoomMemberJoin = 30,
  RoomMemberLeave = 31,
  StreamChange = 40,
  StreamRemove = 41,
  CustomMessage = 99,
}
