/*
 * @Author: lizhaoxuan
 * @Date: 2021-05-14 14:38:46
 * @LastEditTime: 2021-07-13 10:53:18
 * @LastEditors: Please set LastEditors
 * @Description: 房间相关操作以及
 * @FilePath: /app_wisdom_education_web/src/store/room.ts
 */

import { observable, action, computed, runInAction, makeObservable } from 'mobx';
import { login, createRoom, getRoomInfo, entryRoom, EntryRoomResponse, getSnapShot, changeMemberStream, changeRoomState, deleteRoomState, changeMemberProperties, deleteMemberProperties, deleteMemberStream, anonymousLogin } from '@/services/api';
import { AppStore } from './index';
import { NeWebrtc } from '@/lib/rtc';
import { NENim } from '@/lib/im';
import logger from '@/lib/logger';
import { EnhancedEventEmitter } from '@/lib/event';
import { GlobalStorage, debounce, history } from '@/utils';
import { RoleTypes, RoomTypes, UserComponentData, NIMNotifyTypes, HandsUpTypes } from '@/config';


interface JoinOptions {
  roomName: string;
  roomUuid: string;
  sceneType: number;
  role: string;
  userName: string;
  userUuid: string;
}

export interface Streams {
  video: {
    value: number;
    time: number;
  },
  audio: {
    value: number;
    time: number;
  },
  subVideo: {
    value: number;
    time: number;
  }
}

export interface SnapRoomInfo {
  roomName?: string;
  roomUuid?: string;
  rtcCid?: number | string;
  properties?: {
    chatRoom?: {
      chatRoomId?: number | string,
    }
    whiteboard: {
      channelName: number | string,
    }
  },
  states?: {
    step?: {
      value?: number;
    },
    muteChat?: {
      value?: number;
    },
    muteVideo?: {
      value?: number;
    },
    muteAudio?: {
      value?: number;
    },
    pause?: {
      value?: number;
    }
  }
}

export type NetStatusItem = {
  uid: number;
  uplinkNetworkQuality: 0 | 1 | 2 | 3 | 4 | 5;
  downlinkNetworkQuality: 0 | 1 | 2 | 3 | 4 | 5;
}

export class RoomStore extends EnhancedEventEmitter {

  public appStore: AppStore;

  private _webRtcInstance: NeWebrtc | null;
  private _nimInstance: NENim;

  @observable
  _tempStreams: {
    [uid: number]: {
      videoStream?: any;
      screenStream?: any;
      audioStream?: any;
    }
  } = {};

  @observable
  memberList: Array<any> = [];

  @observable
  memberFullList: Array<any> = [];

  @observable
  networkQuality: Array<any> = []

  @observable
  localUserInfo = GlobalStorage.read('user');

  @observable
  _entryData: any;

  @observable
  _joined = false;

  @observable
  _localWbDrawEnable = false;

  @observable
  prevToNowTime = '';

  @observable
  roomState = '课堂未开始';

  @observable
  _snapRoomInfo: SnapRoomInfo = {
    states: {
      step: {},
      pause: {},
    },
    properties: {
      whiteboard: {
        channelName: 0
      },
    }
  };

  @observable
  classDuration = 0;

  @observable
  finishBtnShow = false;

  @observable
  rejectModal = false;

  @observable
  connectionStateChange;

  @observable
  channelClosed = false;

  @observable
  beforeOnlineType = true;

  constructor(appStore: AppStore) {
    super();
    makeObservable(this);
    this.appStore = appStore;
    this._nimInstance = new NENim();
    this._webRtcInstance = null;
  }

  get roomInfo(): Record<string, string> {
    return this.appStore.roomInfo;
  }

  @computed
  get snapRoomInfo(): SnapRoomInfo {
    return this._snapRoomInfo;
  }

  @computed
  get classStep(): number | undefined {
    return this._snapRoomInfo.states?.step?.value;
  }

  @computed
  get nim(): any {
    return this._nimInstance;
  }

  @computed
  get client(): any {
    return this._webRtcInstance?.client;
  }

  @computed
  get joined(): boolean {
    return this._joined;
  }

  @computed
  get entryData(): EntryRoomResponse {
    return this._entryData;
  }

  @computed
  get localWbDrawEnable(): boolean {
    return this._localWbDrawEnable;
  }

  @computed
  get tempStreams(): {
    [uid: number]: {
      videoStream?: any;
      screenStream?: any;
      audioStream?: any;
    }
    } {
    return this._tempStreams;
  }

  @computed
  get localData(): UserComponentData {
    const localUser = this.memberFullList.find((item) => (item.userUuid === this.localUserInfo.userUuid));
    let result;
    if (localUser?.userName) {
      result = {
        userName: localUser.userName,
        userUuid: localUser.userUuid,
        role: localUser.role,
        rtcUid: localUser.rtcUid,
        hasAudio: localUser?.streams?.audio?.value === 1,
        hasVideo: localUser?.streams?.video?.value === 1,
        hasScreen: localUser?.streams?.subVideo?.value === 1,
        audioStream: this.tempStreams[localUser?.rtcUid]?.audioStream,
        basicStream: this.tempStreams[localUser?.rtcUid]?.videoStream,
        isLocal: this.localUserInfo.userUuid === localUser.userUuid,
        showUserControl: this.localUserInfo.userUuid === localUser.userUuid || localUser.role === RoleTypes.host,
        showMoreBtn: this.localUserInfo.role === RoleTypes.host,
        canScreenShare: localUser?.properties?.screenShare?.value === 1 || localUser.role === RoleTypes.host,
        wbDrawEnable: localUser?.properties?.whiteboard?.drawable === 1 || localUser.role === RoleTypes.host,
        avHandsUp: localUser?.properties?.avHandsUp?.value,
      };
    }
    logger.log('localData', result, this.tempStreams);

    return result;
  }

  @computed
  get teacherData(): UserComponentData {
    const teacher = this.memberFullList.find((item) => (item.role === RoleTypes.host));
    let result;
    if (teacher?.userName) {
      result = {
        userName: teacher.userName,
        userUuid: teacher.userUuid,
        role: teacher.role,
        rtcUid: teacher.rtcUid,
        hasAudio: teacher.streams?.audio?.value === 1,
        hasVideo: teacher.streams?.video?.value === 1,
        hasScreen: false,
        audioStream: this.tempStreams[teacher?.rtcUid]?.audioStream,
        basicStream: this.tempStreams[teacher?.rtcUid]?.videoStream,
        isLocal: this.localUserInfo.userUuid === teacher.userUuid,
        showUserControl: this.localUserInfo.userUuid === teacher.userUuid || this.localUserInfo.role === RoleTypes.host,
        showMoreBtn: this.localUserInfo.role === RoleTypes.host,
        canScreenShare: teacher?.properties?.screenShare?.value === 1 || teacher.role === RoleTypes.host,
        wbDrawEnable: teacher?.properties?.whiteboard?.drawable === 1 || teacher.role === RoleTypes.host,
      };
    }
    logger.log('teacherData', result, this.memberFullList, this.memberList);

    return result;
  }

  @computed
  get studentData(): Array<UserComponentData> {
    const student = this.memberFullList.reduce((arr: Array<UserComponentData>, item) => {
      if ((item.role === RoleTypes.broadcaster || item.role === RoleTypes.audience)) {
        const { userUuid } = this.localUserInfo;
        const studentInfo = {
          userName: item.userName,
          userUuid: item.userUuid,
          role: item.role,
          rtcUid: item.rtcUid,
          hasAudio: item?.streams?.audio?.value === 1,
          hasVideo: item?.streams?.video?.value === 1,
          hasScreen: false,
          audioStream: this.tempStreams[item?.rtcUid]?.audioStream,
          basicStream: this.tempStreams[item?.rtcUid]?.videoStream,
          isLocal: this.localUserInfo.userUuid === item.userUuid,
          showUserControl: this.localUserInfo.userUuid === item.userUuid || this.localUserInfo.role === RoleTypes.host,
          showMoreBtn: this.localUserInfo.role === RoleTypes.host,
          canScreenShare: item?.properties?.screenShare?.value === 1 || item.role === RoleTypes.host,
          wbDrawEnable: item?.properties?.whiteboard?.drawable === 1 || item.role === RoleTypes.host,
          avHandsUp: item?.properties?.avHandsUp?.value,
        };
        if (userUuid === item.userUuid) {
          arr.unshift(studentInfo);
        } else {
          arr.push(studentInfo);
        }
      }
      return arr;
    }, []);
    logger.log('studentData', student, this.memberFullList, this.memberList, this.tempStreams);
    return student;
  }

  @computed
  get screenData(): Array<UserComponentData> {
    const screen = this.memberFullList.reduce((arr: Array<UserComponentData>, item) => {
      if (item?.streams?.subVideo?.value === 1 && this.tempStreams[item.rtcUid]?.screenStream) {
        logger.log('screenData1', item);
        arr.push({
          userName: item.userName,
          userUuid: item.userUuid,
          role: item.role,
          rtcUid: item.rtcUid,
          hasAudio: item.streams?.audio?.value === 1,
          // hasVideo: item.streams?.video?.value === 1,
          hasVideo: false,
          hasScreen: item.streams.subVideo?.value === 1,
          audioStream: null,
          basicStream: this.tempStreams[item.rtcUid]?.screenStream,
          isLocal: this.localUserInfo.userUuid === item.userUuid,
          showUserControl: false,
          showMoreBtn: false,
        });
      }
      return arr;
    }, []);
    logger.log('screenData', screen, this.memberFullList, this.tempStreams);
    return screen;
  }

  @computed
  get hasOtherScreen(): boolean {
    return this.memberFullList.some(item => item.streams?.subVideo?.value === 1);
  }

  /**
   * @description: 加入房间
   * @param {*}
   * @return {*}
   */
  @action
  async join(joinOptions: JoinOptions): Promise<void> {
    // TODO
    // 获取信息
    // const localUserInfo = await login(joinOptions.userUuid);
    this.setRoomState('课程未开始');
    runInAction(() => {
      this.classDuration = 0;
      this.setPrevToNowTime('');
    });
    const localUserInfo = await anonymousLogin();
    this.localUserInfo = {
      ...joinOptions,
      ...localUserInfo,
    };
    GlobalStorage.save('user', this.localUserInfo);
    logger.log('获取个人信息', this.localUserInfo);
    if (!this.appStore.roomInfo.roomUuid) {
      this.appStore.setRoomInfo(joinOptions);
    }
    logger.log('会议信息', this.appStore.roomInfo);
    const { roomUuid, roomName, sceneType } = this.appStore.roomInfo;
    const { userUuid, userName, role, imKey, imToken } = this.localUserInfo;
    try {
      await createRoom(roomUuid, `${userName}的课堂`, Number(sceneType)).catch((data) => {
        if (data?.code !== 409) {
          logger.error('创建异常', data);
          throw Error(data?.msg || '创建异常');
        }
      });
      await getRoomInfo(roomUuid);
      this._entryData = await entryRoom({
        userName, role, roomUuid, sceneType: Number(sceneType),
      });

    } catch (error: any) {
      console.error('创建或加入会议异常', error);
      throw Error(error?.code);
    }
    const { member = {} } = this._entryData;
    const { rtcKey, rtcToken, rtcUid } = member;
    // 每次加入房间重新实例化webRtc 否则在第二次登录进来，会导致共享有问题
    // if (!this._webRtcInstance) {

    // }
    this._webRtcInstance = new NeWebrtc(rtcKey);
    logger.log('rtc初始化', this._webRtcInstance);

    if (!this.nim?.nim) {
      await this._nimInstance.loginImServer({
        imAppkey: imKey,
        imAccid: userUuid,
        imToken
      });
      logger.log('im初始化', this._nimInstance);
    }

    this._nimInstance.on('controlNotify', (_data: any) => this.nimNotify(_data));
    this._nimInstance.on('im-connect', () => {
      if (this.joined) {
        this.getMemberList().then(() => {
          const result: any = this.memberList.filter(item => item.userUuid === this.localData.userUuid);
          this.updateMemberStream(this.localData.userUuid, result?.streams);
          this.propertiesUpdataByChange(result?.properties, this.localData.userUuid);
        });
      }
    });



    // IM登录
    // 增加监听
    this._webRtcInstance.on('client-banned', (_data: any) => {
      logger.log(`${_data.uid} 被踢出房间`);
    });
    this._webRtcInstance.on('error', (error: any) => {
      logger.log('webrtc异常', error);
    });
    this._webRtcInstance.on('active-speaker', (_data: any) => {
      // logger.log('正在说话', _data);
    });
    this._webRtcInstance.on('stream-removed', (_data: any) => {
      this.updateStream(_data.uid, _data.mediaType, _data.stream);
    });
    this._webRtcInstance.on('network-quality', (_data: NetStatusItem[]) => {
      // logger.log('网络状态', _data);
      runInAction(() => {
        this.networkQuality = _data;
      });
    });
    this._webRtcInstance.on('connection-state-change', (_data: any) => {
      logger.log('网络状态变更', _data);
      runInAction(() => {
        this.connectionStateChange = _data;
      });
    });
    this._webRtcInstance.on('peer-online', (_data: any) => {
      logger.log('成员加入', _data);
      runInAction(() => {
        this.updateMemberList(_data.uid, 'add');
        this.getMemberList().then(() => {
          this.syncMember();
        });
      });
    });
    this._webRtcInstance.on('peer-leave', (_data: any) => {
      logger.log('成员离开', _data);
      runInAction(() => {
        this.updateMemberList(_data.uid, 'remove');
        this.getMemberList().then(() => {
          this.syncMember();
        });
      });
    });
    this._webRtcInstance.on('stopScreenSharing', (_data: any) => {
      logger.log('检测到停止屏幕共享');
      this.stopScreen();
    });
    this._webRtcInstance.on('stream-subscribed', (_data: any) => {
      logger.log('订阅别人的流成功的通知', _data);
      this.updateStream(_data.uid, _data.mediaType, null);
      runInAction(() => {
        this.updateStream(_data.uid, _data.mediaType, _data.stream);
      });
    });
    this._webRtcInstance.on('stream-removed', (_data: any) => {
      logger.log('收到别人停止发布的消息', _data);
      // this.updateStream(_data.uid, _data.mediaType, null);
    });
    this._webRtcInstance.on('channelClosed', (_data: any) => {
      logger.log('RTC房间被关闭', _data);
      runInAction(() => {
        this.channelClosed = true;
      });
      // this.updateStream(_data.uid, _data.mediaType, null);
    });
    this._webRtcInstance.on('play-local-stream', (_data: any) => {
      logger.log('本地视频获取', _data);
      this.updateStream(_data.uid, _data.mediaType, null);
      this.updateMemberList(_data.uid, 'add');
      runInAction(() => {
        this.updateStream(_data.uid, _data.mediaType, _data.stream);
      });
    });
    const mediaStatus = RoomTypes.bigClass !== Number(this.appStore.roomInfo.sceneType) || RoleTypes.host === this.localUserInfo.role;
    // RTC加入
    this._webRtcInstance.join({
      channelName: roomUuid,
      uid: rtcUid,
      token: rtcToken,
      audio: mediaStatus,
      video: mediaStatus,
      needPublish: mediaStatus
    });
    await this.getMemberList();
    window.addEventListener('online',  this.updateOnlineStatus.bind(this));
    window.addEventListener('offline', this.updateOnlineStatus.bind(this));
    runInAction(() => {
      this._joined = true;
    });
  }

  /**
   * @description: 设置白板状态
   * @param {boolean} value
   * @return {*}
   */
  @action
  public setLocalWbDrawEnable(value: boolean): void {
    this._localWbDrawEnable = value;
  }

  @action
  public async getDeviceListData(): Promise<any> {
    try {
      const microphones = await this._webRtcInstance?.getMicrophones();
      const cameras = await this._webRtcInstance?.getCameras();
      const speakers = await this._webRtcInstance?.getSpeakers();
      const speakerIdSelect = this._webRtcInstance?.speakerId;
      const microphoneSelect = this._webRtcInstance?.microphoneId;
      const cameraSelect = this._webRtcInstance?.cameraId;
      return { microphones, cameras, speakers, speakerIdSelect, microphoneSelect, cameraSelect };
    } catch (error) {
      logger.log("getDeviceListData failed", error);
    }
  }

  public async selectSpeakers(sinkId: string): Promise<void> {
    try {
      const changeSpeaker = await this._webRtcInstance?.selectSpeakers(sinkId);
      return changeSpeaker;
    } catch (error) {
      logger.log("selectSpeakers failed", error);
    }
  }

  /**
   * @description: 切换麦克风
   * @param {string} deivceId
   * @return {*}
   */
  public async selectAudio(deivceId: string): Promise<void> {
    await this._webRtcInstance?.selectAudio(deivceId);
    return;
  }

  /**
   * @description: 切换摄像头
   * @param {string} deivceId
   * @return {*}
   */
  public async selectVideo(deivceId: string): Promise<void> {
    await this._webRtcInstance?.selectVideo(deivceId);
    return;
  }

  public async handsUpAction(userUuid: string, value: HandsUpTypes): Promise<void> {
    const onlineData = this.studentData.filter((item) => item?.avHandsUp === HandsUpTypes.teacherAgree);
    if(value === HandsUpTypes.teacherAgree && onlineData?.length >= 6) {
      this.appStore.uiStore.showToast("上台人数超过限制");
      throw Error('上台人数超过限制');
    }
    await changeMemberProperties({
      roomUuid: this.roomInfo.roomUuid,
      userUuid,
      propertyType: 'avHandsUp',
      value
    });
  }

  @action
  private async nimNotify(_data) {
    logger.log('服务器通知消息', _data);
    logger.log('当前房间数据', this.roomInfo);
    const { cmd, data, timestamp, type } = _data.body;
    const { states, roomUuid } = data;
    if (roomUuid === this.roomInfo.roomUuid && type === 'R') {
      switch (cmd) {
        case (NIMNotifyTypes.RoomStatesChange): {
          await this.getMemberList();
          this.stateUpdateByChange(states, timestamp, roomUuid);
          break;
        }
        case (NIMNotifyTypes.RoomStatesDelete): {
          await this.getMemberList();
          break;
        }
        case (NIMNotifyTypes.RoomPropertiesChange): {
          await this.getMemberList();
          break;
        }
        case (NIMNotifyTypes.RoomPropertiesDelete): {
          await this.getMemberList();
          break;
        }
        case (NIMNotifyTypes.RoomMemberPropertiesChange): {
          await this.getMemberList();
          const { properties, member: { userUuid } } = data;
          this.propertiesUpdataByChange(properties, userUuid);
          break;
        }
        case (NIMNotifyTypes.RoomMemberPropertiesDelete): {
          await this.getMemberList();
          break;
        }
        case (NIMNotifyTypes.RoomMemberJoin): {
          this.getMemberList();
          // this.syncMember();
          break;
        }
        case (NIMNotifyTypes.RoomMemberLeave): {
          this.getMemberList();
          // this.syncMember();
          break;
        }
        case (NIMNotifyTypes.StreamChange): {
          logger.log('成员流变更');
          const { streams, member } = data;
          this.updateMemberStream(member.userUuid, streams);
          break;
        }
        case (NIMNotifyTypes.StreamRemove): {
          // await this.getMemberList();
          const { streamType, member } = data;
          this.deleteMemberStream(member.userUuid, streamType);
          break;
        }
        case (NIMNotifyTypes.CustomMessage): {
          await this.getMemberList();
          break;
        }
        default:
          break;
      }
    }
  }


  /**
   * @description: 属性变更
   * @param {object} properties
   * @param {string} userUuid
   * @return {*}
   */
  @action
  public async propertiesUpdataByChange(properties: { [key: string]: any }, userUuid: string): Promise<void> {
    if (this.localUserInfo.userUuid === userUuid) {
      for (const key in properties) {
        if (Object.prototype.hasOwnProperty.call(properties, key)) {
          const item = properties[key];
          switch (key) {
            case 'whiteboard':
              if (item?.drawable === 1) {
                runInAction(() => {
                  this._localWbDrawEnable = true;
                  this.appStore.uiStore.showToast('老师授予了你白板权限');
                });
              } else if (item?.drawable === 0) {
                runInAction(() => {
                  this._localWbDrawEnable = false;
                  this.appStore.uiStore.showToast('老师取消了你白板权限');
                });
              }
              break;
            case 'streamAV':
              if (item?.value === 1) {
                if (item?.audio === 1) {
                  this.openAudio(userUuid, true, true);
                  this.appStore.uiStore.showToast('老师开启了你的麦克风');
                }
                if (item?.video === 1) {
                  this.openCamera(userUuid, true, true);
                  this.appStore.uiStore.showToast('老师开启了你的摄像头');
                }
                if (item?.audio === 0) {
                  this.closeAudio(userUuid, true, true);
                  this.appStore.uiStore.showToast('老师关闭了你的麦克风');
                }
                if (item?.video === 0) {
                  this.closeCamera(userUuid, true, true);
                  this.appStore.uiStore.showToast('老师关闭了你的摄像头');
                }
              } else if (item.value === 0) {
                if (item?.audio === 0) {
                  this.closeAudio(userUuid, false, false);
                  this.appStore.uiStore.showToast('老师关闭了你的麦克风');
                }
                if (item?.video === 0) {
                  this.closeCamera(userUuid, true, false);
                  this.appStore.uiStore.showToast('老师关闭了你的摄像头');
                }
              }
              break;
            case 'screenShare':
              if (item?.value === 1) {
                this.appStore.uiStore.showToast('老师授予了你屏幕共享权限');
              } else if (item.value === 0) {
                this.appStore.uiStore.showToast('老师取消了你屏幕共享权限');
              }
              break;
            case 'avHandsUp':
              if (item?.value === HandsUpTypes.teacherAgree) {
                this.appStore.uiStore.showToast('举手申请通过');
                await changeMemberStream({
                  roomUuid: this.roomInfo.roomUuid,
                  userUuid: userUuid,
                  streamType: 'audio',
                  value: 1
                });
                await changeMemberStream({
                  roomUuid: this.roomInfo.roomUuid,
                  userUuid: userUuid,
                  streamType: 'video',
                  value: 1
                });
                this._webRtcInstance?.publish()
              }
              if (item?.value === HandsUpTypes.teacherReject) {
                this.setRejectModal(true);
                setTimeout(() => { this.setRejectModal(false); }, 3000);
              }
              if (item?.value === HandsUpTypes.teacherOff) {
                this.appStore.uiStore.showToast('老师结束了你的上台操作');
                this._webRtcInstance?.unpublish()
              }
              if (item?.value === HandsUpTypes.init) {
                this._webRtcInstance?.unpublish()
              }
              break;
            default:
              break;
          }
        }
      }
    }
  }

  /**
   * @description: 成员流变更
   * @param {string} userUuid
   * @param {Streams} streams
   * @return {*}
   */
  @action
  public async updateMemberStream(userUuid: string, streams: Streams): Promise<void> {
    const isBySelf = userUuid === this.localData.userUuid;
    this.memberFullList.some(item => {
      if (item.userUuid === userUuid) {
        item.streams = Object.assign({}, item.streams, streams);
        for (const key in streams) {
          if (Object.prototype.hasOwnProperty.call(streams, key)) {
            const item = streams[key];
            switch (key) {
              case 'audio':
                if (item.value === 1) {
                  this.openAudio(userUuid, false, isBySelf);
                } else if (item.value === 0) {
                  this.closeAudio(userUuid, false, isBySelf);
                }
                break;
              case 'video':
                if (item.value === 1) {
                  this.openCamera(userUuid, false, isBySelf);
                } else if (item.value === 0) {
                  this.closeCamera(userUuid, false, isBySelf);
                }
                break;
              case 'subVideo':
                if (this.localData.userUuid === item.userUuid) {
                  logger.log('触发了subvideo', item.value);
                  if (item.value === 1) {
                    this.startScreen(false);
                  } else {
                    this.stopScreen(false);
                  }
                }
                break;
              default:
                break;
            }
          }
        }
        return true;
      }
    });
    logger.log('流变更执行完成', this.memberFullList);
  }

  // @action
  // public async closeAudioOpenAudo (userUuid: string) {
  //   await this.closeAudio(userUuid, true, false);
  //   await this.openAudio(userUuid, true, false);
  // }

  @action
  public async deleteMemberStream(userUuid: string, streamType: string): Promise<void> {
    const isBySelf = userUuid === this.localData.userUuid;
    this.memberFullList.some(item => {
      if (item.userUuid === userUuid) {
        switch (streamType) {
          case 'subVideo':
            delete item.streams?.subVideo;
            console.log('this.localData.userUuid', this.localData.userUuid);
            console.log('titem.userUuid', item.userUuid);
            if (this.localData.userUuid === item.userUuid) {
              logger.log('触发了subvideo', item.value);
              this.tempStreams[item.rtcUid]?.screenStream && this.stopScreen(false);
            } else {
              // this.closeAudioOpenAudo(userUuid);
            }
            break;
          case 'audio':
            this.closeAudio(userUuid, false, isBySelf);
            delete item.streams.audio;
            break;
          case 'video':
            this.closeCamera(userUuid, false, isBySelf);
            delete item.streams.video;
            break;
          default:
            break;
        }
        return true;
      }
    });
    logger.log('流删除执行完成', this.memberFullList);
  }

  /**
   * @description: 离开房间
   * @param {*}
   * @return {*}
   */
  @action
  async leave(): Promise<void> {
    logger.log('执行leave');
    // TODO
    runInAction(() => {
      this._joined = false;
    });
    this._webRtcInstance?.leave();
    // this.classDuration = 0;
    this.setFinishBtnShow(false);
    this.appStore.whiteBoardStore.destroy();
    this.appStore.resetRoomInfo();
    this.setRoomState('');
    this.reset();
    GlobalStorage.clear();
  }

  /**
   * @description: 设置屏幕共享权限
   * @param {string} userUuid
   * @param {number} value
   * @return {*}
   */
  public async setAllowScreenShare(userUuid: string, value: number): Promise<void> {
    await changeMemberProperties({
      roomUuid: this.roomInfo.roomUuid,
      userUuid,
      propertyType: 'screenShare',
      value,
    });
  }


  /**
   * @description: 开启屏幕共享
   * @param {boolean} sendControl
   * @return {*}
   */
  public async startScreen(sendControl = true): Promise<void> {
    logger.log('sendControl', sendControl);
    // TODO
    try {
      await this._webRtcInstance?.open('screen');
      logger.log('共享开启成功');
      sendControl && await this.changeSubVideoStream(this.localUserInfo.userUuid, 1);
    } catch (error) {
      logger.error('共享开启异常', error);
      await this.stopScreen(sendControl);
      throw error;
    }
  }

  /**
   * @description: 停止屏幕共享
   * @param {boolean} sendControl
   * @return {*}
   */
  public async stopScreen(sendControl = true): Promise<void> {
    try {
      await this._webRtcInstance?.close('screen');
      logger.log('共享关闭成功');
      sendControl && await this.changeSubVideoStream(this.localUserInfo.userUuid, 0).finally(async () => {
        await this.resetAudio();
      });
    } catch (error) {
      logger.log('共享关闭异常-msg', error);
      throw error;
    }
    // 不知道什么原因，会关闭屏幕共享会出现对端听不到的情况，暂时先使用当前成员信息做一次处理
  }

  private async resetAudio() {
    await this.closeAudio(this.localData.userUuid, false);
    if (this.localData.hasAudio) {
      await this.openAudio(this.localData.userUuid, false);
    } else {
      await this.closeAudio(this.localData.userUuid, false);
    }
  }

  /**
   * @description: 改变subvideo
   * @param {string} userUuid
   * @param {number} value
   * @return {*}
   */
  public async changeSubVideoStream(userUuid: string, value: number): Promise<void> {
    if (value !== 0) {
      await changeMemberStream({
        roomUuid: this.roomInfo.roomUuid,
        userUuid,
        streamType: 'subVideo',
        value,
      });
    } else {
      await deleteMemberStream({
        roomUuid: this.roomInfo.roomUuid,
        userUuid,
        streamType: 'subVideo',
      });
    }
  }

  /**
   * @description: 静音全部学生
   * @param {*}
   * @return {*}
   */
  public async muteAllStudent(): Promise<void> {
    await changeRoomState({
      roomUuid: this.roomInfo.roomUuid,
      userUuid: this.teacherData.userUuid,
      state: 'muteAudio',
      value: 1
    });
  }

  /**
   * @description: 静音全部学生
   * @param {*}
   * @return {*}
   */
  public async muteChatroom(value: number): Promise<void> {
    await changeRoomState({
      roomUuid: this.roomInfo.roomUuid,
      userUuid: this.teacherData.userUuid,
      state: 'muteChat',
      value,
    });
  }

  /**
   * @description: 查找当前数据中是否包含备操作人
   * @param {string} userUuid
   * @return {*}
   */
  private findMember(userUuid: string) {
    return this.memberFullList.filter(item => item.userUuid === userUuid)[0];
  }


  /**
   * @description: 设置开启权限
   * @param {string} userUuid
   * @param {number} value
   * @param {number} audio
   * @param {number} video
   * @return {*}
   */
  public async changeMemberStreamProperties(userUuid: string, value: number, audio: number, video: number): Promise<void> {
    await changeMemberProperties({
      roomUuid: this.roomInfo.roomUuid,
      userUuid,
      propertyType: 'streamAV',
      value,
      audio,
      video,
    });
  }

  /**
   * @description: 开启视频
   * @param {string} userUuid
   * @param {boolean} sendControl
   * @return {*}
   */
  public async openCamera(userUuid: string, sendControl = true, isBySelf = true): Promise<void> {
    logger.log('音视频开关-open', userUuid, sendControl, isBySelf);
    // TODO
    const operatedUser = this.findMember(userUuid);
    if (operatedUser) {
      if (!isBySelf) {
        sendControl && await changeMemberProperties({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          propertyType: 'streamAV',
          value: 1,
          video: 1
        });
      } else {
        sendControl && await changeMemberStream({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          streamType: 'video',
          value: 1
        });
      }

      // await this.tempStreams[operatedUser?.rtcUid]?.videoStream?.unmuteVideo().catch(() => {
      isBySelf && await this._webRtcInstance?.open('video').catch(() => {
        // this.closeCamera(userUuid, sendControl, isBySelf);
      });
      await this.resetAudio();
    }
  }

  /**
   * @description: 关闭视频
   * @param {string} userUuid
   * @param {boolean} sendControl
   * @return {*}
   */
  public async closeCamera(userUuid: string, sendControl = true, isBySelf = true): Promise<void> {
    logger.log('音视频开关-close', userUuid, sendControl, isBySelf);
    // TODO
    const operatedUser = this.findMember(userUuid);
    if (operatedUser) {
      if (!isBySelf) {
        sendControl && await changeMemberProperties({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          propertyType: 'streamAV',
          value: 1,
          video: 0
        });
      } else {
        sendControl && await deleteMemberStream({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          streamType: 'video',
        });
      }

      // await this.tempStreams[operatedUser?.rtcUid]?.videoStream?.muteVideo().catch(() => {
      isBySelf && await this._webRtcInstance?.close('video').catch(() => {
        // this.openCamera(userUuid, sendControl, isBySelf);
      });
      await this.resetAudio();
    }
  }

  /**
   * @description: 开启音频
   * @param {string} userUuid
   * @param {boolean} sendControl
   * @return {*}
   */
  public async openAudio(userUuid: string, sendControl = true, isBySelf = true): Promise<void> {
    // TODO
    const operatedUser = this.findMember(userUuid);
    if (operatedUser) {
      if (!isBySelf) {
        sendControl && await changeMemberProperties({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          propertyType: 'streamAV',
          value: 1,
          audio: 1
        });
      } else {
        sendControl && await changeMemberStream({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          streamType: 'audio',
          value: 1
        });
      }
      // await this.tempStreams[operatedUser?.rtcUid]?.videoStream?.unmuteAudio().catch(() => {
      isBySelf && await this._webRtcInstance?.open('audio').catch(() => {
        this.closeAudio(userUuid, sendControl, isBySelf);
      });
    }
  }

  /**
   * @description: 关闭音频
   * @param {string} userUuid
   * @param {boolean} sendControl
   * @return {*}
   */
  public async closeAudio(userUuid: string, sendControl = true, isBySelf = true): Promise<void> {
    // TODO
    const operatedUser = this.findMember(userUuid);
    if (operatedUser) {
      if (!isBySelf) {
        sendControl && await changeMemberProperties({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          propertyType: 'streamAV',
          value: 1,
          audio: 0
        });
      } else {
        sendControl && await deleteMemberStream({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          streamType: 'audio',
        });
      }
      isBySelf && await this._webRtcInstance?.close('audio').catch(() => {
        this.openAudio(userUuid, sendControl, isBySelf);
      });
    }
  }

  @action
  public updateOnlineStatus(): void {
    if (navigator.onLine && !this.beforeOnlineType) {
      this.getMemberList()
    }
    this.beforeOnlineType = navigator.onLine
  }

  // @action
  // public async deleteMember() {
  //   // TODO
  // }

  // @action
  // public async updateMember() {
  //   // TODO
  // }

  /**
   * @description: 开始上课
   * @param {*}
   * @return {*}
   */
  public async startClassRoom(): Promise<void> {
    await changeRoomState({
      roomUuid: this.roomInfo.roomUuid,
      userUuid: this.teacherData?.userUuid,
      state: 'step',
      value: 1
    });
  }

  /**
   * @description: 结束上课
   * @param {*}
   * @return {*}
   */
  public async endClassRoom(): Promise<void> {
    await changeRoomState({
      roomUuid: this.roomInfo.roomUuid,
      userUuid: this.teacherData.userUuid,
      state: 'step',
      value: 2
    });
  }

  /**
   * @description: 暂停课堂
   * @param {*}
   * @return {*}
   */
  public async pauseClassRoom(): Promise<void> {
    await changeRoomState({
      roomUuid: this.roomInfo.roomUuid,
      userUuid: this.teacherData.userUuid,
      state: 'pause',
      value: 1
    });
    return;
  }

  /**
   * @description: 取消暂停课堂
   * @param {*}
   * @return {*}
   */
  public async canclePauseClassRoom(): Promise<void> {
    await deleteRoomState({
      roomUuid: this.roomInfo.roomUuid,
      state: 'pause',
    });
    return;
  }

  /**
   * @description: 设置白板权限
   * @param {string} userUuid
   * @param {number} value
   * @return {*}
   */
  public async setWbEnableDraw(userUuid: string, value: number): Promise<void> {
    switch (value) {
      case 1:
        await changeMemberProperties({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          value,
          propertyType: 'whiteboard'
        });
        break;
      case 0:
        await changeMemberProperties({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          propertyType: 'whiteboard',
          value: 0,
        });
        break;
      default:
        break;
    }
  }

  /**
   * @description: 获取房间快照
   * @param {*}
   * @return {*}
   */
  @action
  public async getMemberList(): Promise<void> {
    // TODO
    // const { snapshot: { members = [] } } = await getSnapShot(this.roomInfo.roomUuid);
    // runInAction(() => {
    //   this.memberFullList = [...members];
    //   logger.log('memberFullList', this.memberFullList, members);
    // })
    getSnapShot(this.roomInfo.roomUuid).then(({ snapshot: { members = [], room = {} }, timestamp = 0 }) => {
      runInAction(() => {
        this._snapRoomInfo = Object.assign({}, room);
        this.memberFullList = [...members];
        (window as any).memberFullList = this.memberFullList;
        const { states, roomUuid } = room;
        this.stateUpdateByChange(states, timestamp, roomUuid, false);
        logger.log('memberFullList', this.memberFullList, members);
      });
    });
  }

  /**
   * @description: 房间状态变更
   * @param {any} states
   * @param {number} time
   * @return {*}
   */
  @action
  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public stateUpdateByChange(states: any, time?: number, operatorRoomUuid?: string, fromNotify = true): void {
    for (const key in states) {
      if (Object.prototype.hasOwnProperty.call(states, key)) {
        const item = states[key];
        switch (key) {
          case 'muteAudio':
            if (item.value === 1 && this.localData?.role !== RoleTypes.host && fromNotify) {
              this.closeAudio(this.localUserInfo.userUuid);
              fromNotify && this.appStore.uiStore.showToast('老师执行了全体静音');
            }
            break;
          case 'step':
            if (item.value === 1 && time) {
              this.classDuration = time - item.time;
            } else if (item.value === 2) {
              const { roomUuid } = this.snapRoomInfo;
              const { room: { rtcCid } } = this._entryData;
              if (operatorRoomUuid === roomUuid) {
                this.leave();
                this.setRoomState('课程结束');
                history.push(`/endCourse?roomUuid=${roomUuid}&rtcCid=${rtcCid}`);
              }
            }
            break;
          case 'muteChat':
            if (item.value === 1 && this.localData?.role !== RoleTypes.host) {
              fromNotify && this.appStore.uiStore.showToast('聊天室已全体禁言');
            } else {
              fromNotify && this.appStore.uiStore.showToast('聊天室已取消禁言');
            }
            break;
          default:
            break;
        }
      }
    }
  }

  /**
   * @description: 保持数据同步
   * @param {*}
   * @return {*}
   */
  @action
  public syncMember(): void {
    runInAction(() => {
      logger.log('查看syncMember', this.memberFullList, this.memberList);
      for (let i = 0; i < this.memberFullList.length; i++) {
        const item = this.memberFullList[i];
        if (!this.memberList.includes(Number(item.rtcUid))) {
          this.memberFullList.splice(i, 1);
          i--;
        }
      }
    });
  }

  /**
   * @description: 重置
   * @param {*}
   * @return {*}
   */
  @action
  public async reset(): Promise<void> {
    // TODO
    runInAction(() => {
      this.memberList = [];
      this.memberFullList = [];
      this._tempStreams = {};
      this._snapRoomInfo = {
        states: {
          step: {},
          pause: {}
        }
      };
      this.localUserInfo = {};
    });
    await this.removeEvent();
  }

  /**
   * @description: 移除时间监听
   * @param {*}
   * @return {*}
   */
  private async removeEvent(): Promise<void> {
    this._webRtcInstance?.removeAllListeners();
    this._nimInstance.removeAllListeners();
    window.removeEventListener('online',  this.updateOnlineStatus.bind(this));
    window.removeEventListener('offline', this.updateOnlineStatus.bind(this));
  }

  @action
  public setPrevToNowTime(value: string): void {
    this.prevToNowTime = value;
  }

  @action
  public setFinishBtnShow(value: boolean): void {
    this.finishBtnShow = value;
  }

  @action
  public setRejectModal(value: boolean): void {
    this.rejectModal = value;
  }

  @action
  public setRoomState(value: string): void {
    this.roomState = value;
  }

  /**
   * @description: 更新memberList
   * @param {number} uid
   * @return {*}
   */
  @action
  public updateMemberList(uid: number, type: 'add' | 'remove'): void {
    // TODO
    const index = this.memberList.findIndex((item: number) => item === uid);
    switch (type) {
      case 'add':
        if (!this.memberList.includes(uid)) {
          this.memberList.push(uid);
        }
        break;
      case 'remove':
        if (index >= 0) {
          this.memberList.splice(index, 1);
          delete this._tempStreams[uid];
        }
        break;
      default:
        break;
    }
  }

  /**
   * @description: 缓存流信息
   * @param {number} uid
   * @param {any} stream
   * @return {*}
   */
  @action
  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public updateStream(uid: number, mediaType: 'audio' | 'video' | 'screen', stream: any): void {
    // TODO
    if (!this._tempStreams[uid]) {
      this._tempStreams[uid] = {};
    }
    switch (mediaType) {
      case 'audio':
        this._tempStreams[uid].audioStream = stream || this._tempStreams[uid].audioStream;
        break;
      case 'video':
        this._tempStreams[uid].videoStream = stream;

        break;
      case 'screen':
        this._tempStreams[uid].screenStream = stream;

        break;
      default:
        break;
    }
    logger.log('stream更新', this._tempStreams);
  }
}
