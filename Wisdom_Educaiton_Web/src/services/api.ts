/*
 * @Author: lizhaoxuan
 * @Date: 2021-05-13 19:19:23
 * @LastEditTime: 2021-06-08 16:06:22
 * @LastEditors: Please set LastEditors
 * @Description: 请求接口
 * @FilePath: /app_wisdom_education_web/src/services/login.ts
 */

import { post, put, get, reqDelete } from './index';
import { GlobalStorage } from '@/utils';
import { RoomTypes, RoleTypes } from '@/config';

interface LoginResponse {
  imKey: string;
  imToken: string;
  rtcKey: string;
  userToken: string;
  userUuid: string;
}
export interface EntryRoomOptions {
  userName: string;
  role: string;
  roomUuid: number|string;
  sceneType: number;
}

export interface EntryRoomResponse {
  room: {
    roomName: string|number;
    roomUuid: string|number;
    rtcCid: string|number;
    properties: {
      chatRoom: {
        chatRoomId: string|number;
        roomCreatorId: string|number;
      }
    },
  },
  member: {
    rtcKey: string;
    rtcToken: string;
    role: string;
    userName: string;
    userUuid: string;
    rtcUid: number|string
  }
}

export interface SnapShotResponse {
  snapshot: {
    room: {
      roomName: string;
      roomUuid: string;
      rtcCid: string|number
      properties: {
        chatRoom: {
          chatRoomId: string|number;
          roomCreatorId: string|number;
        }
      },
      states?: {
        [key: string]: {
          value?: number;
          time?: number;
        }
      }
    },
    members: Array<{
      userName: string;
      userUuid: string;
      role: string;
      rtcUid: number,
      streams: {
        audio: {
          value: number;
        },
        video: {
          value: number;
        }
      },
    }>
  },
  timestamp?: number;
}

export interface ChangeMemberStreamOptions{
  roomUuid: string|number;
  userUuid: string;
  streamType: 'audio'|'video'|'subVideo';
  value: number;
}

export interface DeleteMemberStreamOptions{
  roomUuid: string|number;
  userUuid: string;
  streamType: 'audio'|'video'|'subVideo';
}

export interface ChangeMemberPropertiesOptions {
  roomUuid: string|number;
  userUuid: string;
  propertyType: 'whiteboard'|'screenShare'|'streamAV'|'avHandsUp';
  value: number;
  audio?: number;
  video?: number;
}

export interface DeleteMemberPropertiesOptions {
  roomUuid: string|number;
  userUuid: string;
  propertyType: 'whiteboard'|'screenShare';
}

const propertyKey = {
  'whiteboard': 'drawable', // 白板开启
  'screenShare': 'value', // 屏幕共享
  'streamAV': 'value',
  'avHandsUp': 'value',
}

export interface changeMemberPropertiesOptions {
  roomUuid: string|number;
  userUuid: string;
  propertyType: 'whiteboard'| 'screenShare';
  value: number;
}

export interface ChangeRoomStateOptions {
  roomUuid: string|number;
  userUuid: string;
  state: 'step'|'pause'|'muteAudio'|'muteChat';
  value: number;
}

export interface DeleteRoomStateOptions {
  roomUuid: string|number;
  state: 'step'|'pause'|'muteAudio';
}


/**
 * @description: 登录请求
 * @param {string} userUuid
 * @return {*}
 */
export async function login(userUuid: string|number): Promise<LoginResponse>  {
  const res = await post(`/v1/users/${userUuid}/login`);
  return res;
}

export async function anonymousLogin(): Promise<LoginResponse>  {
  const res = await post(`/v1/anonymous/login`);
  return res;
}

/**
 * @description: 创建房间
 * @param {string} roomUuid
 * @return {*}
 */
export async function createRoom(roomUuid: string|number, roomName: string, roomType: RoomTypes): Promise<any> {
  const reqConfig = {
    roomName: roomName,
    configId: roomType,
  };
  const res = await put(`/v1/rooms/${roomUuid}`, reqConfig,
    {
      headers: {
        user: GlobalStorage.read('user')?.userUuid,
        token: GlobalStorage.read('user')?.userToken
      }
    }
  )
  return res;
}

/**
 * @description: 获取房间信息
 * @param {string} roomUuid
 * @return {*}
 */
export async function getRoomInfo(roomUuid: string|number): Promise<any> {
  const res = await get(`/v1/rooms/${roomUuid}/config`,{
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  });
  return res;
}

/**
 * @description: 加入房间
 * @param {EntryRoomOptions} options
 * @return {*}
 */
export async function entryRoom(options: EntryRoomOptions): Promise<EntryRoomResponse> {
  const param = {
    userName: options.userName,
    role: options.role,
    streams: RoomTypes.bigClass === options.sceneType && options.role !== RoleTypes.host? {} : {
      audio: {
        value: 1
      },
      video: {
        value: 1
      }
    },
    properties: options.role === RoleTypes.host ? {
      screenShare: {
        value: 0
      }
    } : {}
  }
  const res = await post(`/v1/rooms/${options.roomUuid}/entry`, param, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  })
  return res;
}

export async function deleteMember(options) {
  const res = await reqDelete(`/v1/rooms/${options.roomUuid}`, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    },
    params: {
      // TODO
    }
  })
  return res;
}

export async function getSnapShot(roomUuid: number|string): Promise<SnapShotResponse> {
  const res = await get(`/v1/rooms/${roomUuid}/snapshot`, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  })
  return res;
}

/**
 * @description: 更新流信息
 * @param {ChangeMemberStreamOptions} options
 * @return {*}
 */
export async function changeMemberStream(options: ChangeMemberStreamOptions): Promise<any> {
  const res = await put(`/v1/rooms/${options.roomUuid}/members/${options.userUuid}/streams/${options.streamType}`, {
    value: options.value
  }, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  })
  return res;
}

export async function deleteMemberStream(options: DeleteMemberStreamOptions): Promise<any> {
  const res = await reqDelete(`/v1/rooms/${options.roomUuid}/members/${options.userUuid}/streams/${options.streamType}`, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  })
  return res;
}

// export async function roomStates(roomUuid: number|string, key): Promise<any> {
//   const param = {
//     value: 1
//   }
//   const res = put(`/v1/rooms/${roomUuid}/states/${key}`, param, {
//     headers: {
//       user: GlobalStorage.read('user')?.userUuid,
//       token: GlobalStorage.read('user')?.userToken
//     }
//   })
//   return res;
// }

/**
 * @description: 修改成员房间属性
 * @param {ChangeMemberPropertiesOptions} options
 * @return {*}
 */
export async function changeMemberProperties(options: ChangeMemberPropertiesOptions): Promise<any> {
  const key = propertyKey[options.propertyType];
  if (!key) throw new Error(`修改房间参数，请求参数异常, ${options.propertyType}`);
  const param = {
    [key]: options.value,
    audio: options.audio,
    video: options.video,
  }
  for (const key in param) {
    if (Object.prototype.hasOwnProperty.call(param, key)) {
      if (typeof param[key] !== 'number') {
        delete param[key]
      }
    }
  }
  const res = await put(`/v1/rooms/${options.roomUuid}/members/${options.userUuid}/properties/${options.propertyType}`, param, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  })
  return res;
}

/**
 * @description: 修改房间属性
 * @param {ChangeRoomStateOptions} options
 * @return {*}
 */
export async function changeRoomState(options: ChangeRoomStateOptions): Promise<any> {
  // TODO
  const res = await put(`/v1/rooms/${options.roomUuid}/states/${options.state}`, {
    value: options.value,
  }, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  });
  return res;
}

/**
 * @description: 删除房间属性
 * @param {DeleteRoomStateOptions} options
 * @return {*}
 */
export async function deleteRoomState(options: DeleteRoomStateOptions): Promise<any> {
  const res = await reqDelete(`/v1/rooms/${options.roomUuid}/states/${options.state}`, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  });
  return res;
}

/**
 * @description: 删除成员属性
 * @param {DeleteMemberPropertiesOptions} options
 * @return {*}
 */
export async function deleteMemberProperties(options: DeleteMemberPropertiesOptions): Promise<any> {
  const res = await reqDelete(`/v1/rooms/${options.roomUuid}/members/${options.userUuid}/properties/${options.propertyType}`, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  });
  return res;
}

/**
 * @description: 获取回放信息
 * @param {string} roomUuid
 * @return {*}
 */
export async function getRecordInfo(roomUuid: string|number, rtcCid: string|number): Promise<any> {
  const res = await get(`/v1/rooms/${roomUuid}/${rtcCid}/record/playback`,{
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  });
  return res;
}