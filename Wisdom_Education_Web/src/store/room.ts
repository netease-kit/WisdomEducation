/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import {
  observable,
  action,
  computed,
  runInAction,
  makeObservable,
} from "mobx";
import {
  login,
  createRoom,
  getRoomInfo,
  entryRoom,
  EntryRoomResponse,
  getSnapShot,
  getSequence,
  changeMemberStream,
  changeRoomState,
  deleteRoomState,
  changeMemberProperties,
  deleteMemberProperties,
  deleteMemberStream,
  anonymousLogin,
  SnapShotResponseMembers,
} from "@/services/api";
import { AppStore } from "./index";
import { NeWebrtc } from "@/lib/rtc";
import { NeElertc } from "@/lib/rtc/electron";
import { NENim } from "@/lib/im";
import logger from "@/lib/logger";
import { EnhancedEventEmitter } from "@/lib/event";
import { GlobalStorage, debounce, history, trimStr } from "@/utils";
import {
  RoleTypes,
  RoomTypes,
  UserComponentData,
  NIMNotifyTypes,
  HandsUpTypes,
  isElectron,
  ShareListItem,
  RoomWithSceneTypes
} from "@/config";

interface JoinOptions {
  roomName: string;
  roomUuid: string;
  sceneType: number;
  role: string;
  userName: string;
  userUuid: string;
  uuid?: string;
  token?: string;
}

export interface Streams {
  video: {
    value: number;
    time: number;
  };
  audio: {
    value: number;
    time: number;
  };
  subVideo: {
    value: number;
    time: number;
  };
}

export interface SnapRoomInfo {
  roomName?: string;
  roomUuid?: string;
  rtcCid?: number | string;
  properties?: {
    chatRoom?: {
      chatRoomId?: number | string;
    };
    whiteboard?: {
      channelName: number | string;
    };
    live?: {
      cid?: string;
      pullHlsUrl?: string;
      pullHttpUrl?: string;
      pullRtmpUrl?: string;
      pullRtsUrl?: string;
      pushUrl?:string;
    };
  };
  states: {
    step?: {
      value?: number;
    };
    muteChat?: {
      value?: number;
    };
    muteVideo?: {
      value?: number;
    };
    muteAudio?: {
      value?: number;
    };
    pause?: {
      value?: number;
    };
  };
}

export type NetStatusItem = {
  uid: number;
  uplinkNetworkQuality: 0 | 1 | 2 | 3 | 4 | 5;
  downlinkNetworkQuality: 0 | 1 | 2 | 3 | 4 | 5;
};

interface TempStream {
  [uid: number]: {
    videoStream?: any;
    screenStream?: any;
    audioStream?: any;
  };
}

export class RoomStore extends EnhancedEventEmitter {
  public appStore: AppStore;

  private _webRtcInstance: NeWebrtc | NeElertc | null;
  private _nimInstance: NENim;

  @observable
  _tempStreams: {
    [uid: number]: {
      videoStream?: any;
      screenStream?: any;
      audioStream?: any;
    };
  } = {};

  @observable
  memberList: Array<any> = [];

  @observable
  memberFullList: Array<any> = [];

  @observable
  _bigLivememberFullList: Array<any> = [];

  @observable
  _joined = false;

  @observable
  _joinFinish = false;

  @observable
  _entryData: any;

  @observable
  localUserInfo = GlobalStorage.read("user");

  @observable
  _localWbDrawEnable = false;

  @observable
  prevToNowTime = "";

  @observable
  roomState = "课堂未开始";

  @observable
  isLiveStuJoin = false;

  @observable
  isLiveTeaJoin = false;

  @observable
  _snapRoomInfo: SnapRoomInfo = {
    states: {
      step: {},
      pause: {},
    },
    properties: {
      whiteboard: {
        channelName: 0,
      },
    },
  };

  @observable
  _snapSequence = 0;

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

  @observable
  _chatRoom: any;

  @observable
  networkQuality: Array<any> = [];

  @observable
  _deviceChangedCount = 0; // 设备插拔标识

  @observable
  _reselectDevice = false; // 设备设置变更标识 

  @observable
  incrementIng = false; // 标识是否有正在请求的sequence或snapshot接口

  @observable
  cachedSequenceData: Array<any> = [];

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
  get rtc(): any {
    return this._webRtcInstance;
  }

  @computed
  get joined(): boolean {
    return this._joined;
  }

  @computed
  get joinFinish(): boolean {
    return this._joinFinish;
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
  get tempStreams(): TempStream {
    return this._tempStreams;
  }

  @computed
  get bigLivememberFullList(): Array<any> {
    return this._bigLivememberFullList;
  }

  @computed
  get localData(): UserComponentData {
    const localUser = this.memberFullList.find(
      (item) => item.userUuid === this.localUserInfo.userUuid
    );
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
        showUserControl:
          this.localUserInfo.userUuid === localUser.userUuid ||
          localUser.role === RoleTypes.host,
        showMoreBtn: this.localUserInfo.role === RoleTypes.host,
        canScreenShare:
          localUser?.properties?.screenShare?.value === 1 ||
          localUser.role === RoleTypes.host,
        wbDrawEnable:
          localUser?.properties?.whiteboard?.drawable === 1 ||
          localUser.role === RoleTypes.host,
        avHandsUp: localUser?.properties?.avHandsUp?.value,
      };
    }
    logger.log("localData", result, this.tempStreams);

    return result;
  }

  @computed
  get teacherData(): UserComponentData {
    const teacher = this.memberFullList.find(
      (item) => item.role === RoleTypes.host
    );
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
        showUserControl:
          this.localUserInfo.userUuid === teacher.userUuid ||
          this.localUserInfo.role === RoleTypes.host,
        showMoreBtn: this.localUserInfo.role === RoleTypes.host,
        canScreenShare:
          teacher?.properties?.screenShare?.value === 1 ||
          teacher.role === RoleTypes.host,
        wbDrawEnable:
          teacher?.properties?.whiteboard?.drawable === 1 ||
          teacher.role === RoleTypes.host,
      };
    }
    logger.log("teacherData", result);

    return result;
  }

  @computed
  get studentData(): Array<UserComponentData> {
    const student = this.memberFullList.reduce(
      (arr: Array<UserComponentData>, item) => {
        if (
          item.role === RoleTypes.broadcaster ||
          item.role === RoleTypes.audience
        ) {
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
            showUserControl:
              this.localUserInfo.userUuid === item.userUuid ||
              this.localUserInfo.role === RoleTypes.host,
            showMoreBtn: this.localUserInfo.role === RoleTypes.host,
            canScreenShare:
              item?.properties?.screenShare?.value === 1 ||
              item.role === RoleTypes.host,
            wbDrawEnable:
              item?.properties?.whiteboard?.drawable === 1 ||
              item.role === RoleTypes.host,
            avHandsUp: item?.properties?.avHandsUp?.value,
          };
          if (userUuid === item.userUuid) {
            arr.unshift(studentInfo);
          } else {
            arr.push(studentInfo);
          }
        }
        return arr;
      },
      []
    );
    logger.log(
      "studentData&stream",
      student,
      this.tempStreams
    );
    return student;
  }

  @computed
  get screenData(): Array<UserComponentData> {
    const screen = this.memberFullList.reduce(
      (arr: Array<UserComponentData>, item) => {
        if (item?.streams?.subVideo?.value === 1) {
          logger.log("screenData1", item);
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
      },
      []
    );
    logger.log("screenData&stream", screen, this.tempStreams);
    return screen;
  }

  @computed
  get hasOtherScreen(): boolean {
    return this.memberFullList.some(
      (item) => item.streams?.subVideo?.value === 1
    );
  }

  @computed
  get deviceChangedCount(): number {
    return this._deviceChangedCount;
  }

  @computed
  get reselectDevice(): boolean {
    return this._reselectDevice;
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
    this.setRoomState("课程未开始");
    runInAction(() => {
      this.setClassDuration(0)
      this.setPrevToNowTime("");
      this._joined = true;
    });
    let { uuid = "", token = "" } = joinOptions;
    [uuid, token] = [trimStr(uuid), trimStr(token)];
    const isParamError = (uuid && !token) || (!uuid && token);
    if (isParamError) {
      throw new Error("400");
    }
    try {
      const userToken = uuid && token;
      const localUserInfo = userToken
        ? await login(uuid, token)
        : await anonymousLogin();
      this.localUserInfo = {
        ...joinOptions,
        ...localUserInfo,
      };
      GlobalStorage.save("user", this.localUserInfo);
      logger.log("获取个人信息", this.localUserInfo);
      // throw Error('400');
      if (!this.appStore.roomInfo.roomUuid) {
        this.appStore.setRoomInfo(joinOptions);
      }
    } catch (error: any) {
      runInAction(() => {
        this._joined = false;
      });
      throw Error(error?.code);
    }
    logger.log("当前房间数据", JSON.stringify(this.appStore.roomInfo));
    const { roomUuid, roomName, sceneType } = this.appStore.roomInfo;
    const { userUuid, userName, role, imKey, imToken } = this.localUserInfo;
    this.isLiveStuJoin = Number(sceneType) === RoomTypes.bigClasLive && role !== RoleTypes.host
    this.isLiveTeaJoin = Number(sceneType) === RoomTypes.bigClasLive && role === RoleTypes.host
    try {
      const joinSettingInfo = JSON.parse(localStorage.getItem('room-setting') || '{}');
      const { chatroom } = joinSettingInfo;
      const resource = { chatroom, live: Number(sceneType) === RoomTypes.bigClasLive };
      if (!this.isLiveStuJoin) {
        await createRoom(roomUuid, `${userName}的课堂`, Number(sceneType), resource).catch(
          (data) => {
            if (data?.code !== 409) {
              logger.error("创建异常", data);
              throw Error(data?.msg || "创建异常");
            }
          }
        );
      }
      const roomConfig = await getRoomInfo(roomUuid);
      if (roomConfig.sceneType !== RoomWithSceneTypes[sceneType]) {
        runInAction(() => {
          this._joined = false;
        });
        throw Error('1017');
      }
      if (!this.isLiveStuJoin) {
        this._entryData = await entryRoom({
          userName,
          role,
          roomUuid,
          sceneType: Number(sceneType),
        });
      }
    } catch (error: any) {
      console.error("创建或加入会议异常", error);
      runInAction(() => {
        this._joined = false;
      });
      throw Error(error?.message);
    }
    // 每次加入房间重新实例化webRtc 否则在第二次登录进来，会导致共享有问题
    // if (!this._webRtcInstance) {

    // }

    if (!this.nim?.nim) {
      await this._nimInstance.loginImServer({
        imAppkey: imKey,
        imAccid: userUuid,
        imToken,
      });
      logger.log("im初始化", this._nimInstance);
    }

    if (!this.isLiveStuJoin) {
      const { member = {}, room = {} } = this._entryData;
      const { rtcKey, rtcToken, rtcUid, wbAuth } = member;
      const { checksum, curTime, nonce } = wbAuth;
      this._webRtcInstance = isElectron
        ? new NeElertc(rtcKey)
        : new NeWebrtc(rtcKey);
      // @ts-ignore
      window._webRtcInstance = this._webRtcInstance;
      logger.log("isElectron", isElectron);
      logger.log("rtc初始化", this._webRtcInstance, this.client);

      this._nimInstance.on("controlNotify", (_data: any) =>
        this.nimNotify(_data)
      );
      this._nimInstance.on("im-connect", () => {
        if (this.joined) {
          console.log("/snapshot im-connect")
          this.getMemberList().then(() => {
            const result: any = this.memberList.filter(
              (item) => item.userUuid === this.localData?.userUuid
            );
            this.updateMemberStream(this.localData?.userUuid, result?.streams);
            this.propertiesUpdataByChange(
              result?.properties,
              this.localData?.userUuid
            );
          });
        }
      });

      // IM登录
      // 增加监听
      this._webRtcInstance.on("client-banned", (_data: any) => {
        logger.log(`${_data.uid} 被踢出房间`);
      });
      this._webRtcInstance.on("error", (error: any) => {
        logger.log("webrtc异常", error);
      });
      this._webRtcInstance.on("active-speaker", (_data: any) => {
      // logger.log('正在说话', _data);
      });
      this._webRtcInstance.on("stream-removed", (_data: any) => {
        this.updateStream(_data.uid, _data.mediaType, _data.stream);
      });
      this._webRtcInstance.on("network-quality", (_data: NetStatusItem[]) => {
      // logger.log('网络状态', _data);
        runInAction(() => {
          this.networkQuality = _data;
        });
      });
      this._webRtcInstance.on("connection-state-change", (_data: any) => {
        logger.log("网络状态变更", _data);
        if (isElectron) {
          if ([1, 2, 3].includes(_data.reason)) {
            this.channelClosed = true;
          }
        } else {
          if (_data.curState === "DISCONNECTED" && this.joined) {
            this.channelClosed = true;
          }
        }
        runInAction(() => {
          this.connectionStateChange = _data;
        });
      });
      this._webRtcInstance.on("device-change", () => {
        debounce(() => {
          this.getDeviceListData()
            .then(
              async({
                microphones = [],
                cameras = [],
                speakers = [],
                speakerIdSelect = "",
                microphoneSelect = "",
                cameraSelect = "",
              }) => {
                const cameraItem = cameras?.find((item) => item.deviceId === cameraSelect)
                // 使用摄像头被拔出时，自动切换
                if (cameraSelect && !cameraItem && cameras[0]?.deviceId) {
                  await this.selectVideo(cameras[0]?.deviceId)
                }
                this._deviceChangedCount++;
                console.log("this._deviceChangedCount ",this._deviceChangedCount)     
              }
            )
          
        }, 1200)
      })
      this._webRtcInstance.on("peer-online", (_data: any) => {
        logger.log("成员加入", _data);
        runInAction(() => {
          this.updateMemberList(_data.uid, "add");
          // this.getMemberList().then(() => {
          //   this.syncMember();
          // });
        });
      });
      this._webRtcInstance.on("peer-leave", (_data: any) => {
        logger.log("成员离开", _data);
        runInAction(() => {
          // this.updateMemberList(_data.uid, "remove");
          // this.getMemberList().then(() => {
          //   this.syncMember();
          // });
        });
      });
      this._webRtcInstance.on("stopScreenSharing", async (_data: any) => {
        logger.log("检测到停止屏幕共享");
        if (isElectron) {
          this.stopScreen();
        } else if (!isElectron) {
          if (this.isLiveTeaJoin) {
            await this.stopScreen(false);
            this.appStore.whiteBoardStore.getCanvasTrack().then((res) => {
              this.switchScreenWithCanvas('open', res);
            });
          } else {
            this.stopScreen();
          }
        }
      });
      this._webRtcInstance.on("stream-subscribed", (_data: any) => {
        logger.log("订阅别人的流成功的通知", _data);
        this.updateStream(_data.uid, _data.mediaType, null);
        runInAction(() => {
          this.updateStream(_data.uid, _data.mediaType, _data.stream);
        });
      });
      this._webRtcInstance.on("stream-removed", (_data: any) => {
        logger.log("收到别人停止发布的消息", _data);
      // this.updateStream(_data.uid, _data.mediaType, null);
      });
      this._webRtcInstance.on("channelClosed", (_data: any) => {
        logger.log("RTC房间被关闭", _data);
        runInAction(() => {
          this.channelClosed = true;
        });
      // this.updateStream(_data.uid, _data.mediaType, null);
      });

      this._webRtcInstance.on("onDisconnect", (_data: number) => {
        runInAction(() => {
          if (_data !== 0) {
            this.channelClosed = true;
          }
        });
      // this.updateStream(_data.uid, _data.mediaType, null);
      });
      this._webRtcInstance.on("play-local-stream", (_data: any) => {
        logger.log("本地"+_data.mediaType+"流获取", _data);
        this.updateStream(_data.uid, _data.mediaType, null);
        this.updateMemberList(_data.uid, "add");
        runInAction(() => {
          this.updateStream(_data.uid, _data.mediaType, _data.stream);
        });
      });

      // this._webRtcInstance.on("audioTrackEnded", (_data: any) => {
      //   logger.log("检测到音频轨道结束。造成的原因可能是设备被拔出（或禁用）。");
      //   this.appStore.uiStore.showToast("音频设备异常，请重新进行设备选择和检测！");
      // });

      // this._webRtcInstance.on("videoTrackEnded", (_data: any) => {
      //   logger.log("检测到视频轨道结束。造成的原因可能是设备被拔出（或禁用）。");
      //   this.appStore.uiStore.showToast("视频输入设备异常，请重新进行设备选择和检测！");
      // });

      this._webRtcInstance.on("accessDenied", (_data: any) => {
        logger.log("检测到设备权限被拒绝");
        // this.appStore.uiStore.showToast("获取设备权限被拒绝");
      })

      this._webRtcInstance.on("beOccupied", (_data: any) => {
        logger.log("检测到获取设备权限时，设备被占用");
        this.appStore.uiStore.showToast("获取麦克风或摄像头权限时，设备被占用。")
      })

      this._webRtcInstance.on("video-occupied", (_data: any) => {
        logger.log("视频开启异常");
        this.appStore.uiStore.showToast("视频输入设备异常，请重新进行设备选择和检测！");
      })

      const mediaStatus =
      RoomTypes.bigClass !== Number(this.appStore.roomInfo.sceneType) ||
      RoleTypes.host === this.localUserInfo.role;
      // RTC加入
      await this._webRtcInstance.join({
        channelName: roomUuid,
        uid: rtcUid,
        token: rtcToken,
        audio: mediaStatus,
        video: mediaStatus,
        needPublish: mediaStatus,
      });
      // const enbaleDraw = role === RoleTypes.host || Number(sceneType) === RoomTypes.oneToOne;
      if (!this.appStore.whiteBoardStore.wbInstance) {
        await this.appStore.whiteBoardStore.initWhiteBoard({
          appKey: imKey,
          uid: rtcUid,
          container: document.createElement('div'),
          nickname: userName,
          checksum,
          nonce,
          curTime: Number(curTime),
        });
        await this.appStore.whiteBoardStore.joinRoom({
          channel: (room.properties?.whiteboard?.channelName as number),
        })
      // await this.appStore.whiteBoardStore.setEnableDraw(enbaleDraw);
      }
    }
    console.log("/snapshot join()")
    await this.getMemberList();
    runInAction(() => {
      this._joinFinish = true;
    })
    window.addEventListener("online", this.updateOnlineStatus.bind(this));
    window.addEventListener("offline", this.updateOnlineStatus.bind(this));
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

  /**
   * @description: 设置白板辅流
   * @param {string} type
   * @param {any} stream
   * @return {*}
   */
  @action
  public async switchScreenWithCanvas(type: string, stream?: any): Promise<void> {
    await this._webRtcInstance?.switchScreenWithCanvas(type, stream);
    ['changeToScreen'].includes(type) ?
      (await this.changeSubVideoStream(this.localUserInfo.userUuid, 1)) :
      (await this.changeSubVideoStream(this.localUserInfo.userUuid, 0));
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
      logger.log("getDeviceListData",{
        microphones,
        cameras,
        speakers,
        speakerIdSelect,
        microphoneSelect,
        cameraSelect,
      })
      return {
        microphones,
        cameras,
        speakers,
        speakerIdSelect,
        microphoneSelect,
        cameraSelect,
      };
    } catch (error) {
      logger.log("getDeviceListData failed", error);
    }
  }

  /**
   * @description: 切换扬声器
   * @param {string} deivceId
   * @return {*}
   */
  @action
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

  public async setMicrophoneCaptureVolume(volume: number): Promise<void> {
    try {
      await this._webRtcInstance?.setMicrophoneCaptureVolume(volume)
    } catch (error) {
      logger.log("setMicrophoneCaptureVolume failed", error);
    }
  }

  public async setAudioVolume(volume: number): Promise<void> {
    try {
      await this._webRtcInstance?.setAudioVolume(volume)
    } catch (error) {
      logger.log("setAudioVolume failed", error);
    }
  }

  public async getAudioLevel(): Promise<any> {
    try {
      const result = await this._webRtcInstance?.getAudioLevel();
      return result
    } catch (error) {
      logger.log("setAudioVolume failed", error);
    }
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

  public async handsUpAction(
    userUuid: string,
    value: HandsUpTypes
  ): Promise<void> {
    const onlineData = this.studentData.filter(
      (item) => item?.avHandsUp === HandsUpTypes.teacherAgree
    );
    await changeMemberProperties({
      roomUuid: this.roomInfo.roomUuid,
      userUuid,
      propertyType: "avHandsUp",
      value,
    }).catch((err) => {
      if(err.code === 1012) {
        this.appStore.uiStore.showToast("上台人数超过限制");
        throw Error("上台人数超过限制");
      }
    });
  }

  @action
  public async nimNotify(_data, needGetSnapShot = true): Promise<void> {
    const { data: {roomUuid}, sequence, type } = _data.body;
    if (roomUuid !== this.roomInfo.roomUuid) {
      logger.log(`服务器通知消息与房间不符，通知为${roomUuid}，当前为${this.roomInfo.roomUuid}`)
      return
    }
    if (roomUuid === this.roomInfo.roomUuid && type === "R"){
      // logger.log("服务器通知消息", _data.body);
      if (sequence <= this._snapSequence) {
        // 丢弃消息
        console.log("异常数据 丢弃 ",{sequence, _snapSequence:this._snapSequence})
        return
      } else if (sequence - this._snapSequence === 1) {
        // 连续数据，直接进行合并
        console.log("连续数据 合并 ",{sequence, _snapSequence:this._snapSequence})
        _data.body.needGetSnapShot = needGetSnapShot;
        this.cachedSequenceData.push(_data.body)
      } else if (sequence - this._snapSequence > 10) {
        // 数据差超过10次，获取全量最新数据
        console.log("/snapshot sequence差大于10 ",{sequence, _snapSequence:this._snapSequence})
        await this.getMemberList();
        return
      } else if(sequence - this._snapSequence <= 10 && !this.incrementIng) {
        this.incrementIng = true
        // 数据差在10次以内，获取该sequence之后的所有数据
        console.log("/sequence sequence差<=10",{sequence, _snapSequence:this._snapSequence})
        await getSequence({
          roomUuid,
          nextId: this._snapSequence + 1
        }).then(async (res) => {
          this.incrementIng = false
          if (res.length > 0) {
            this.cachedSequenceData = [...this.cachedSequenceData, ...res]
          }
        })
      }
      this.dealCachedData()
    }
  }

  @action
  public roomPropertyUpdataByChange(properties, type="change"):void {
    if (!this._snapRoomInfo.properties) this._snapRoomInfo.properties = {}
    for (const key in properties) {
      if (Object.prototype.hasOwnProperty.call(properties, key)) {
        const snapItem = this._snapRoomInfo.properties[key]
        if (type === "change") {
          this._snapRoomInfo.properties[key] = Object.assign({}, snapItem, properties[key])
        }
      }
    }
    if (type === "delete") {
      this._snapRoomInfo.properties = {}
    }
  }

  @action 
  public async dealCachedData() {
    this.cachedSequenceData.sort()
    while(this.cachedSequenceData.length) {
      const item = this.cachedSequenceData.shift()
      const {needGetSnapShot, sequence} = item
      if (sequence === this._snapSequence + 1) {
        logger.log("当前处理的通知消息", JSON.stringify(item));
        this._snapSequence++
        await this.dealSequenceData(item, needGetSnapShot)
      }
    }
  }

  @action
  public async dealSequenceData(_data, needGetSnapShot = true): Promise<void> {
    const { cmd, data, timestamp } = _data;
    const { states, properties, roomUuid } = data;
    switch (cmd) {
      case NIMNotifyTypes.RoomStatesChange: {
        this.stateUpdateByChange(states, timestamp, roomUuid);
        break;
      }
      case NIMNotifyTypes.RoomStatesDelete: {
        this.stateUpdateByChange(states, timestamp, roomUuid);
        break;
      }
      case NIMNotifyTypes.RoomPropertiesChange: {
        this.roomPropertyUpdataByChange(properties);
        break;
      }
      case NIMNotifyTypes.RoomPropertiesDelete: {
        this.roomPropertyUpdataByChange(properties, 'delete');
        break;
      }
      case NIMNotifyTypes.RoomMemberPropertiesChange: {
        const {
          properties,
          member: { userUuid },
        } = data;
        this.propertiesUpdataByChange(properties, userUuid);
        break;
      }
      case NIMNotifyTypes.RoomMemberPropertiesDelete: {
        const {
          properties,
          member: { userUuid },
        } = data;
        this.propertiesUpdataByChange(properties, userUuid);
        break;
      }
      case NIMNotifyTypes.RoomMemberJoin: {
        const {operatorMember: {rtcUid}, members} = data
        this.updateMemberList(rtcUid, "add")
        const flag = this.memberFullList.some((item) => item.rtcUid == rtcUid);
        flag && (console.log("somebody exists joined again"))
        !flag  && (this.memberFullList = [...this.memberFullList, ...members])
        break;
      }
      case NIMNotifyTypes.RoomMemberLeave: {
        const {operatorMember: {rtcUid}} = data
        this.updateMemberList(rtcUid, "remove").then(() => {
          this.syncMember();
        });
        // this.syncMember();
        // const { members } = data;
        // for (const ele of members) {
        //   if (ele.userUuid === this.localData.userUuid) {
        //     this.appStore.uiStore.showToast('该账号在其他设备登录', 'error')
        //     this.leave();
        //     history.push(`/`);
        //   }
        // }
        break;
      }
      case NIMNotifyTypes.StreamChange: {
        logger.log("成员流变更");
        const { streams, member } = data;
        this.updateMemberStream(member.userUuid, streams);
        break;
      }
      case NIMNotifyTypes.StreamRemove: {
        const { streamType, member } = data;
        this.deleteMemberStream(member.userUuid, streamType);
        break;
      }
      case NIMNotifyTypes.CustomMessage: {
        needGetSnapShot && await this.getMemberList();
        break;
      }
      default:
        break;
    }
  }

  /**
   * @description: 属性变更
   * @param {object} properties
   * @param {string} userUuid
   * @return {*}
   */
  @action
  public async propertiesUpdataByChange(
    properties: { [key: string]: any },
    userUuid: string
  ): Promise<void> {
    this.memberFullList.some((item) => {
      if (item.userUuid === userUuid) {
        if (properties) item.properties = Object.assign({}, item.properties, properties)
        else delete item.properties
      }
    })
    if (!properties) return
    if (this.localUserInfo.userUuid === userUuid) {
      for (const key in properties) {
        if (Object.prototype.hasOwnProperty.call(properties, key)) {
          const item = properties[key];
          switch (key) {
            case "whiteboard":
              if (item?.drawable === 1) {
                runInAction(() => {
                  this._localWbDrawEnable = true;
                  this.appStore.uiStore.showToast("老师授予了你白板权限");
                });
              } else if (item?.drawable === 0) {
                runInAction(() => {
                  this._localWbDrawEnable = false;
                  this.appStore.uiStore.showToast("老师取消了你白板权限");
                });
              }
              break;
            case "streamAV":
              if (item?.value === 1) {
                if (item?.audio === 1) {
                  this.openAudio(userUuid, true, true);
                  this.appStore.uiStore.showToast("老师开启了你的麦克风");
                }
                if (item?.video === 1) {
                  this.openCamera(userUuid, true, true);
                  this.appStore.uiStore.showToast("老师开启了你的摄像头");
                }
                if (item?.audio === 0) {
                  this.closeAudio(userUuid, true, true);
                  this.appStore.uiStore.showToast("老师关闭了你的麦克风");
                }
                if (item?.video === 0) {
                  this.closeCamera(userUuid, true, true);
                  this.appStore.uiStore.showToast("老师关闭了你的摄像头");
                }
              } else if (item.value === 0) {
                if (item?.audio === 0) {
                  this.closeAudio(userUuid, false, false);
                  this.appStore.uiStore.showToast("老师关闭了你的麦克风");
                }
                if (item?.video === 0) {
                  this.closeCamera(userUuid, true, false);
                  this.appStore.uiStore.showToast("老师关闭了你的摄像头");
                }
              }
              break;
            case "screenShare":
              if (item?.value === 1) {
                this.appStore.uiStore.showToast("老师授予了你屏幕共享权限");
              } else if (item.value === 0) {
                this.appStore.uiStore.showToast("老师取消了你屏幕共享权限");
                if (this.screenData.length > 0) {
                  this.stopScreen(false)
                }
              }
              break;
            case "avHandsUp":
              if (item?.value === HandsUpTypes.teacherAgree) {
                this.appStore.uiStore.showToast("举手申请通过");
                await changeMemberStream({
                  roomUuid: this.roomInfo.roomUuid,
                  userUuid: userUuid,
                  streamType: "audio",
                  value: 1,
                });
                await changeMemberStream({
                  roomUuid: this.roomInfo.roomUuid,
                  userUuid: userUuid,
                  streamType: "video",
                  value: 1,
                });
                this._webRtcInstance?.publish();
              }
              if (item?.value === HandsUpTypes.teacherReject) {
                this.setRejectModal(true);
                setTimeout(() => {
                  this.setRejectModal(false);
                }, 3000);
              }
              if (item?.value === HandsUpTypes.teacherOff) {
                this.appStore.uiStore.showToast("老师结束了你的上台操作");
                this._webRtcInstance?.unpublish();
                if (this._localWbDrawEnable) {
                  this._localWbDrawEnable = false;
                }
              }
              if (item?.value === HandsUpTypes.init) {
                this._webRtcInstance?.unpublish();
                if (this._localWbDrawEnable) {
                  this._localWbDrawEnable = false;
                }
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
  public async updateMemberStream(
    userUuid: string,
    streams: Streams
  ): Promise<void> {
    const isBySelf = userUuid === this.localData?.userUuid;
    this.memberFullList.some((item) => {
      if (item.userUuid === userUuid) {
        item.streams = Object.assign({}, item.streams, streams);
        for (const key in streams) {
          if (Object.prototype.hasOwnProperty.call(streams, key)) {
            const item = streams[key];
            switch (key) {
              case "audio":
                if (item.value === 1) {
                  this.openAudio(userUuid, false, isBySelf);
                } else if (item.value === 0) {
                  this.closeAudio(userUuid, false, isBySelf);
                }
                break;
              case "video":
                if (item.value === 1) {
                  this.openCamera(userUuid, false, isBySelf);
                } else if (item.value === 0) {
                  this.closeCamera(userUuid, false, isBySelf);
                }
                break;
              case "subVideo":
                if (this.localData?.userUuid === item.userUuid) {
                  logger.log("触发了subvideo", item.value);
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
    logger.log("流变更执行完成", this.memberFullList);
  }

  // @action
  // public async closeAudioOpenAudo (userUuid: string) {
  //   await this.closeAudio(userUuid, true, false);
  //   await this.openAudio(userUuid, true, false);
  // }

  @action
  public async deleteMemberStream(
    userUuid: string,
    streamType: string
  ): Promise<void> {
    const isBySelf = userUuid === this.localData?.userUuid;
    this.memberFullList.some((item) => {
      if (item.userUuid === userUuid) {
        switch (streamType) {
          case "subVideo":
            delete item.streams?.subVideo;
            console.log("this.localData.userUuid", this.localData?.userUuid);
            console.log("titem.userUuid", item.userUuid);
            if (this.localData?.userUuid === item.userUuid) {
              logger.log("触发了subvideo", item.value);
              this.tempStreams[item.rtcUid]?.screenStream &&
                this.stopScreen(false);
            } else {
              // this.closeAudioOpenAudo(userUuid);
            }
            break;
          case "audio":
            this.closeAudio(userUuid, false, isBySelf);
            delete item.streams.audio;
            break;
          case "video":
            this.closeCamera(userUuid, false, isBySelf);
            delete item.streams.video;
            break;
          default:
            break;
        }
        return true;
      }
    });
    logger.log("流删除执行完成", this.memberFullList);
  }

  /**
   * @description: 设置课堂时间
   * @param {number} time
   * @return {*}
   */
  @action
  public setClassDuration(time: number): void {
    this.classDuration = time || 0;
  }

  /**
   * @description: 离开房间
   * @param {*}
   * @return {*}
   */
  @action
  async leave(): Promise<void> {
    logger.log("执行leave");
    // TODO
    runInAction(() => {
      this._joined = false;
      this.channelClosed = false;
      this._joinFinish = false;
    });
    if (!this.isLiveStuJoin) {
      this._webRtcInstance?.leave();
      this._webRtcInstance?.destroy();
      this._webRtcInstance = null;
      this.appStore.whiteBoardStore.destroy();

    }
    // this.classDuration = 0;
    this.setFinishBtnShow(false);
    this.appStore.resetRoomInfo();
    this.setRoomState("");
    GlobalStorage.clear();
    await this.reset();
    await this._nimInstance.logoutImServer();
  }

  /**
   * @description: 设置屏幕共享权限
   * @param {string} userUuid
   * @param {number} value
   * @return {*}
   */
  public async setAllowScreenShare(
    userUuid: string,
    value: number
  ): Promise<void> {
    await changeMemberProperties({
      roomUuid: this.roomInfo.roomUuid,
      userUuid,
      propertyType: "screenShare",
      value,
    });
  }

  public async getShareList(): Promise<ShareListItem[]> {
    const res = await this._webRtcInstance?.getShareList();
    if (res && res?.length > 0) {
      return res;
    }
    return [];
  }

  /**
   * @description: 开启屏幕共享
   * @param {boolean} sendControl
   * @return {*}
   */
  public async startScreen(
    sendControl = true,
    displayId?: string,
    id?: string
  ): Promise<void> {
    logger.log("startScreen-sendControl", sendControl);
    // TODO
    try {
      if (isElectron) {
        sendControl &&
          (await this.changeSubVideoStream(this.localUserInfo.userUuid, 1));
        setTimeout(async () => {
          await this._webRtcInstance?.open("screen", displayId, id);
        }, 0);
      } else {
        await this._webRtcInstance?.open("screen", displayId);
        sendControl &&
          (await this.changeSubVideoStream(this.localUserInfo.userUuid, 1));
      }
      logger.log("共享开启成功");
    } catch (error) {
      logger.error("共享开启异常", error);
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
    logger.log("stopScreen-sendControl", sendControl);
    try {
      await this._webRtcInstance?.close("screen");
      logger.log("共享关闭成功");
      sendControl &&
        (await this.changeSubVideoStream(
          this.localUserInfo.userUuid,
          0
        ).finally(async () => {
          // await this.resetAudio();
        }));
    } catch (error) {
      logger.log("共享关闭异常-msg", error);
      throw error;
    }
    // 不知道什么原因，会关闭屏幕共享会出现对端听不到的情况，暂时先使用当前成员信息做一次处理
  }

  // private async resetAudio() {
  //   await this.closeAudio(this.localData.userUuid, false);
  //   if (this.localData.hasAudio) {
  //     await this.openAudio(this.localData.userUuid, false);
  //   } else {
  //     await this.closeAudio(this.localData.userUuid, false);
  //   }
  // }

  /**
   * @description: 改变subvideo
   * @param {string} userUuid
   * @param {number} value
   * @return {*}
   */
  public async changeSubVideoStream(
    userUuid: string,
    value: number
  ): Promise<void> {
    if (value !== 0) {
      await changeMemberStream({
        roomUuid: this.roomInfo.roomUuid,
        userUuid,
        streamType: "subVideo",
        value,
      });
    } else {
      await deleteMemberStream({
        roomUuid: this.roomInfo.roomUuid,
        userUuid,
        streamType: "subVideo",
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
      state: "muteAudio",
      value: 1,
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
      state: "muteChat",
      value,
    });
  }

  /**
   * @description: 查找当前数据中是否包含备操作人
   * @param {string} userUuid
   * @return {*}
   */
  private findMember(userUuid: string) {
    return this.memberFullList.filter((item) => item.userUuid === userUuid)[0];
  }

  /**
   * @description: 设置开启权限
   * @param {string} userUuid
   * @param {number} value
   * @param {number} audio
   * @param {number} video
   * @return {*}
   */
  public async changeMemberStreamProperties(
    userUuid: string,
    value: number,
    audio: number,
    video: number
  ): Promise<void> {
    await changeMemberProperties({
      roomUuid: this.roomInfo.roomUuid,
      userUuid,
      propertyType: "streamAV",
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
  public async openCamera(
    userUuid: string,
    sendControl = true,
    isBySelf = true
  ): Promise<void> {
    logger.log("视频开关-open", userUuid, sendControl, isBySelf);
    // TODO
    const operatedUser = this.findMember(userUuid);
    if (operatedUser) {
      if (!isBySelf) {
        sendControl &&
          (await changeMemberProperties({
            roomUuid: this.roomInfo.roomUuid,
            userUuid,
            propertyType: "streamAV",
            value: 1,
            video: 1,
          }));
      } else {
        sendControl &&
          (await changeMemberStream({
            roomUuid: this.roomInfo.roomUuid,
            userUuid,
            streamType: "video",
            value: 1,
          }));
        setTimeout(async()=>{
          await this._webRtcInstance?.open("video")
        }, 0)
      }
    }
  }

  /**
   * @description: 关闭视频
   * @param {string} userUuid
   * @param {boolean} sendControl
   * @return {*}
   */
  public async closeCamera(
    userUuid: string,
    sendControl = true,
    isBySelf = true
  ): Promise<void> {
    logger.log("视频开关-close", userUuid, sendControl, isBySelf);
    // TODO
    const operatedUser = this.findMember(userUuid);
    if (operatedUser) {
      if (!isBySelf) {
        sendControl &&
          (await changeMemberProperties({
            roomUuid: this.roomInfo.roomUuid,
            userUuid,
            propertyType: "streamAV",
            value: 1,
            video: 0,
          }));
      } else {
        sendControl &&
          (await deleteMemberStream({
            roomUuid: this.roomInfo.roomUuid,
            userUuid,
            streamType: "video",
          }));
      }

      // await this.tempStreams[operatedUser?.rtcUid]?.videoStream?.muteVideo().catch(() => {
      isBySelf &&
        (await this._webRtcInstance?.close("video").catch(() => {
          // this.openCamera(userUuid, sendControl, isBySelf);
        }));
      // await this.resetAudio();
    }
  }

  /**
   * @description: 开启音频
   * @param {string} userUuid
   * @param {boolean} sendControl
   * @return {*}
   */
  public async openAudio(
    userUuid: string,
    sendControl = true,
    isBySelf = true
  ): Promise<void> {
    // TODO
    const operatedUser = this.findMember(userUuid);
    if (operatedUser) {
      if (!isBySelf) {
        sendControl &&
          (await changeMemberProperties({
            roomUuid: this.roomInfo.roomUuid,
            userUuid,
            propertyType: "streamAV",
            value: 1,
            audio: 1,
          }));
      } else {
        sendControl &&
          (await changeMemberStream({
            roomUuid: this.roomInfo.roomUuid,
            userUuid,
            streamType: "audio",
            value: 1,
          }));
      }
      // await this.tempStreams[operatedUser?.rtcUid]?.videoStream?.unmuteAudio().catch(() => {
      isBySelf &&
        (await this._webRtcInstance?.open("audio").catch(() => {
          this.closeAudio(userUuid, sendControl, isBySelf);
        }));
    }
  }

  /**
   * @description: 关闭音频
   * @param {string} userUuid
   * @param {boolean} sendControl
   * @return {*}
   */
  public async closeAudio(
    userUuid: string,
    sendControl = true,
    isBySelf = true
  ): Promise<void> {
    // TODO
    const operatedUser = this.findMember(userUuid);
    if (operatedUser) {
      if (!isBySelf) {
        sendControl &&
          (await changeMemberProperties({
            roomUuid: this.roomInfo.roomUuid,
            userUuid,
            propertyType: "streamAV",
            value: 1,
            audio: 0,
          }));
      } else {
        sendControl &&
          (await deleteMemberStream({
            roomUuid: this.roomInfo.roomUuid,
            userUuid,
            streamType: "audio",
          }));
      }
      isBySelf &&
        (await this._webRtcInstance?.close("audio").catch(() => {
          this.openAudio(userUuid, sendControl, isBySelf);
        }));
    }
  }

  @action
  public updateOnlineStatus(): void {
    if (navigator.onLine && !this.beforeOnlineType) {
      console.log("/snapshot onLine")
      this.getMemberList();
    }
    this.beforeOnlineType = navigator.onLine;
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
      state: "step",
      value: 1,
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
      state: "step",
      value: 2,
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
      state: "pause",
      value: 1,
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
      state: "pause",
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
          propertyType: "whiteboard",
        });
        break;
      case 0:
        await changeMemberProperties({
          roomUuid: this.roomInfo.roomUuid,
          userUuid,
          propertyType: "whiteboard",
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

    /**
     * http://jira.netease.com/browse/YYTX-3640
     * 解决房间关闭后请求传参为空导致的鉴权异常
     */
    if(!this.roomInfo?.roomUuid || this.incrementIng) return
    this.incrementIng = true;
    getSnapShot(this.roomInfo.roomUuid).then(
      ({ sequence, snapshot: { members = [], room = {} }, timestamp = 0 }) => {
        runInAction(() => {
          this.incrementIng = false;
          this._snapRoomInfo = Object.assign({}, this._snapRoomInfo, room);
          this.memberFullList = [...members];
          this._snapSequence = sequence;
          console.log("更新_snapSequence ", this._snapSequence);
          (window as any).memberFullList = this.memberFullList;
          const { states, roomUuid } = room;
          this.stateUpdateByChange(states, timestamp, roomUuid, false);
          for (const item of this.memberFullList) {
            this.updateMemberList(item.rtcUid, "add");
          }
          logger.log("memberFullList", this.memberFullList, members);
        });
      }
    );
  }

  /**
   * @description: 设置成员信息
   * @param {SnapShotResponseMembers} data
   * @return {*}
   */
  @action
  public setBigLiveMemberFullList(data: SnapShotResponseMembers): void {
    this._bigLivememberFullList = [...data];
  }

  /**
   * @description: 房间状态变更
   * @param {any} states
   * @param {number} time
   * @return {*}
   */
  @action
  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public stateUpdateByChange(
    states: any,
    time?: number,
    operatorRoomUuid?: string,
    fromNotify = true
  ): void {
    if (!states && this._snapRoomInfo.states) {
      this._snapRoomInfo.states = {}
      return
    }
    for (const key in states) {
      if (Object.prototype.hasOwnProperty.call(states, key)) {
        const item = states[key];
        switch (key) {
          case "muteAudio":
            if (
              item.value === 1 &&
              this.localData?.role !== RoleTypes.host &&
              fromNotify
            ) {
              this.closeAudio(this.localUserInfo.userUuid);
              fromNotify &&
                this.appStore.uiStore.showToast("老师执行了全体静音");
            }
            if (!this._snapRoomInfo.states?.muteAudio) {
              this._snapRoomInfo.states.muteAudio = this._snapRoomInfo.states.muteAudio || {}
            }
            this._snapRoomInfo.states.muteAudio.value = item.value;
            break;
          case "step":
            if (item.value === 1 && time) {
              this.setClassDuration(time - item.time);
              localStorage.setItem('record-url', `/record?roomUuid=${this.snapRoomInfo.roomUuid}&rtcCid=${this.snapRoomInfo.rtcCid}`)
            } else if (item.value === 2) {
              const { roomUuid, rtcCid } = this.snapRoomInfo;
              if (operatorRoomUuid === roomUuid) {
                this.leave();
                this.setRoomState("课程结束");
                history.push(
                  `/endCourse?roomUuid=${roomUuid}&rtcCid=${rtcCid}`
                );
              }
            }
            if (!this._snapRoomInfo.states?.step) {
              this._snapRoomInfo.states.step = this._snapRoomInfo.states.step || {}
            }
            this._snapRoomInfo.states.step.value = item.value;
            break;
          case "muteChat":
            if (this.localData?.role !== RoleTypes.host) {
              if (item.value === 1) {
                fromNotify && this.appStore.uiStore.showToast("聊天室已全体禁言");
              } else {
                fromNotify && this.appStore.uiStore.showToast("聊天室已取消禁言");
              }
              if (!this._snapRoomInfo.states?.muteChat) {
                this._snapRoomInfo.states.muteChat = this._snapRoomInfo.states.muteChat || {}
              }
              this._snapRoomInfo.states.muteChat.value = item.value;
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
      logger.log("查看syncMember", this.memberFullList, this.memberList);
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
    await this.removeEvent();
    runInAction(() => {
      this.memberList = [];
      this.memberFullList = [];
      this._tempStreams = {};
      this._snapRoomInfo = {
        states: {
          step: {},
          pause: {},
        },
        properties: {
          whiteboard: {
            channelName: 0,
          },
          live: {
            cid: '',
          },
        },
      };
      this.localUserInfo = {};
      this.isLiveTeaJoin = false;
      this.isLiveStuJoin = false;
    });
  }

  /**
   * @description: 移除时间监听
   * @param {*}
   * @return {*}
   */
  private async removeEvent(): Promise<void> {
    if (!this.isLiveStuJoin) {
      this._webRtcInstance?.removeAllListeners();
      this._nimInstance.removeAllListeners();
    }
    window.removeEventListener("online", this.updateOnlineStatus.bind(this));
    window.removeEventListener("offline", this.updateOnlineStatus.bind(this));
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
  public async updateMemberList(uid: number, type: "add" | "remove"): Promise<void> {
    // TODO
    const index = this.memberList.findIndex((item: number) => item === uid);
    switch (type) {
      case "add":
        if (!this.memberList.includes(uid)) {
          this.memberList.push(uid);
        }
        break;
      case "remove":
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
  public updateStream(
    uid: number,
    mediaType: "audio" | "video" | "screen",
    stream: any
  ): void {
    // TODO
    if (!this._tempStreams[uid]) {
      this._tempStreams[uid] = {};
    }
    switch (mediaType) {
      case "audio":
        this._tempStreams[uid].audioStream =
          stream || this._tempStreams[uid].audioStream;
        break;
      case "video":
        this._tempStreams[uid].videoStream = stream;

        break;
      case "screen":
        this._tempStreams[uid].screenStream = stream;

        break;
      default:
        break;
    }
    logger.log("stream更新", this._tempStreams);
  }

  /**
   * @description: 外部获取聊天室实例，暂时兼容
   * @param {any} data
   * @return {*}
   */
  @action
  public setChatRoomInstance(data: any): void {
    this._chatRoom = data;
  }

  /**
   * @description: 清除聊天室实例
   * @param {*}
   * @return {*}
   */
  @action
  public removeChatRoomInstance(): void {
    this._chatRoom = null;
  }

  @action
  public setReselectDevice(value: boolean): void {
    this._reselectDevice = value;
  }

  @action 
  public enableVolumeIndicationInElectron(enable: boolean, interval: number ):void {
    if (isElectron) {
      this._webRtcInstance?.enableAudioVolumeIndication(enable, interval)
    }
  }
}
