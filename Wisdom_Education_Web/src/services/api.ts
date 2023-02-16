/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import { post, put, get, reqDelete } from './index';
import { GlobalStorage, trimStr } from '@/utils';
import { RoomTypes, RoleTypes, Authorization, UserSeatOperation, HostSeatOperation } from '@/config';

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
  sequence: number,
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
    members: SnapShotResponseMembers
  },
  timestamp?: number;
}

export interface SequenceListResponse {
  sequence: number,
  data: {
    stream: {

    },
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
    members: SequenceListResponseMembers
  },
  cmd: number
}

export type SequenceListResponseMembers = Array<{
  properties: {
    screenShare?: {
      value: number
    },
    streamAV?: {
      value: number,
      video?: number,
      audio?: number
    },
    avHandsUp?: {
      value: number
    },
    whiteboard?: {
      drawable: number
    }
  };
  streams: {
    audio?: {
      audio: number
    },
    video?: {
      video: number
    }
  };
  userName: string;
  userUuid: string;
  role: string;
  rtcUid: number;
}>

export type SnapShotResponseMembers = Array<{
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
  'whiteboard': 'drawable', // Enable whiteboard
  'screenShare': 'value', // Screen sharing
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

export interface Resources {
  live?: boolean;
  rtc?: boolean;
  chatroom?: boolean;
  whiteboard?: boolean;
}

export interface SeatIndexInfo {
  roomUuid: string;
  userUuid: string;
  userName: string;
}

/**
 * @description: login request
 * @param {string} userUuid
 * @return {*}
 */
export async function login(user = '', token = ''): Promise<LoginResponse>  {
  const res = await post(`/v1/login`, {} ,{
    headers: {
      user: trimStr(user),
      token: trimStr(token),
    }
  });
  return res;
}

export async function anonymousLogin(): Promise<LoginResponse>  {
  const res = await post(`/v1/anonymous/login`);
  return res;
}

/**
 * @description: Create a room
 * @param {string} roomUuid
 * @return {*}
 */
export async function createRoom(roomUuid: string|number, roomName: string, roomType: RoomTypes, resource: Resources, properties: any): Promise<any> {
  const reqConfig = {
    roomName: roomName,
    configId: roomType,
    config: {
      resource: {
        ...resource,
        rtc: true,
        whiteboard: true,
      },
    },
    properties: properties
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
 * @description: Get the room info
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
 * @description: Join a room
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

export async function deleteMemberApi(options) {
  const res = await reqDelete(`/v1/rooms/${options.roomUuid}/members/${options.userUuid}`, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    },
    data: {}
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

export async function getSequence(options):Promise<Array<SequenceListResponse>> {
  const res = await get(`/v1/rooms/${options.roomUuid}/sequence?nextId=${options.nextId}`, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  })
  return res.list;
}

/**
 * @description: Update the stream info
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
 * @description: Edit the profile of a member
 * @param {ChangeMemberPropertiesOptions} options
 * @return {*}
 */
export async function changeMemberProperties(options: ChangeMemberPropertiesOptions): Promise<any> {
  const key = propertyKey[options.propertyType];
  if (!key) throw new Error(`Invalid request parameter, ${options.propertyType}`);
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
 * @description: Edit the room profile
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
 * @description: Delete a room attribute
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
 * @description: Delete a member property
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
 * @description: Get playback info
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

export async function changeMemberSeat(options: {
  roomUuid: string|number, 
  toUserUuid: string, 
  action: UserSeatOperation | HostSeatOperation,
  operateByHost: boolean
  userName?: string
}): Promise<any> {
  const {roomUuid, toUserUuid, action, operateByHost, userName} = options
  const param = {
    action,
    toUserUuid,
    userName
  }
  const res = await post(`/v1/rooms/${roomUuid}/seat/${operateByHost ? 'host' : 'user'}/action`, param, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  });

  return res;
}

export async function getSeatApplyList(roomUuid: number|string): Promise<SeatIndexInfo[]> {
  const res = await get(`/v1/rooms/${roomUuid}/seat/applyList`, {
    headers: {
      user: GlobalStorage.read('user')?.userUuid,
      token: GlobalStorage.read('user')?.userToken
    }
  })
  return res
}