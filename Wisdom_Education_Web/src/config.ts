/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

export enum RoomTypes {
  oneToOne = 5,
  smallClass = 6,
  bigClass = 7,
  bigClasLive = 20,
}

export enum RoleTypes {
  host = "host",
  broadcaster = "broadcaster",
  audience = "audience",
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

export enum UserSeatOperation {
  studentHandsup = 1,
  studentCancel = 2,
  studentOff = 5
}

export enum HostSeatOperation {
  teacherAgree = 1,
  teacherReject = 4,
  teacherOff = 5
}

export enum LiveClassMemberStatus {
  InRTC = 1,
  InCDN = 2
}

export interface UserComponentData {
  userName: string;
  userUuid: string;
  role: string;
  rtcUid: string | number;
  hasAudio: boolean;
  hasVideo: boolean;
  hasScreen: boolean;
  audioStream: any;
  basicStream: any;
  isLocal: boolean;
  showUserControl: boolean;
  showMoreBtn: boolean;
  canScreenShare?: boolean;
  wbDrawEnable?: boolean;
  avHandsUp?: number | null;
  isStudent?: boolean;
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
  HostAgreeSeatApply = 110, // 管理员同意上麦
  HostRejectSeatApply = 113, // 管理员拒绝上麦
  HostKickSeater = 114, // 管理员踢麦
  MemberApplySeat = 115, // 观众申请上麦
  MemberCancelApplySeat = 116, // 观众取消申请上麦
  MemberLeaveSeat = 119, // 观众主动下麦
  SeatStatesChange = 120, // 麦位状态变更
}

export const Authorization = `Basic ${process.env.REACT_APP_SDK_AUTHORIZATION}`;

export const isDev = process.env.REACT_APP_ENV === "development";
export const isElectron = process.env.REACT_APP_PLATFORM === "electron";

export enum PlatForms {
  mac = "darwin",
  linux = "linux",
  win = "win32",
}

export interface ShareListItem {
  id: string;
  displayId: string;
  thumbnail: string;
  appIcon: string;
  name: string;
}

export enum SceneTypes {
  ONE_TO_ONE = "EDU.1V1",
  SMALL = "EDU.SMALL",
  BIG = "EDU.BIG",
  BiGLIVE = "EDU.LIVE_SIMPLE"
}

export const RoomWithSceneTypes  = {
  [RoomTypes.oneToOne]: SceneTypes.ONE_TO_ONE,
  [RoomTypes.smallClass]: SceneTypes.SMALL,
  [RoomTypes.bigClass]: SceneTypes.BIG,
  [RoomTypes.bigClasLive]: SceneTypes.BiGLIVE,
}
