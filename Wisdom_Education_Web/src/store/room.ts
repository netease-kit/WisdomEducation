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
  changeMemberSeat,
  deleteMemberApi,
  getSeatApplyList,
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
  RoomWithSceneTypes,
  UserSeatOperation,
  HostSeatOperation,
  LiveClassMemberStatus
} from "@/config";
import intl from 'react-intl-universal';

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
      cname?: string;
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
  _liveClassSeatStudentList: Array<UserComponentData> = []; // 直播大班课操作麦位的成员信息，用作麦位和原举手逻辑中转

  // @observable
  localMemberSeatStatus: LiveClassMemberStatus = 2;

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
  roomState = intl.get("课程未开始");

  @observable
  isLiveStuJoin = false;

  @observable
  isLiveTeaJoin = false;

  @observable
  _snapRoomInfo: SnapRoomInfo = {
    states: {
      step: {},
      pause: {},
      muteChat: {},
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
  _deviceChangedCount = 0; // identifier for plugging or unplugging devices

  @observable
  _reselectDevice = false; // Identifier for device settings changes

  @observable
  incrementIng = false; // Check if the device has the requested sequence or snapshot interface

  @observable
  cachedSequenceData: Array<any> = [];

  @observable
  _studentData: Array<UserComponentData> = [];

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
  get isBigLiveClass(): boolean {
    return Number(this.roomInfo?.sceneType) ===  RoomTypes.bigClasLive
  }

  @computed
  get isBigClass(): boolean {
    return Number(this.roomInfo?.sceneType) ===  RoomTypes.bigClass
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
  get isClassMuteChat(): boolean {
    return this._snapRoomInfo.states?.muteChat?.value === 1;
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
    console.log('---从memberFullList获取的local信息', localUser)
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
    } else {
      // 直播大班课尚未加入房间的成员可以从本地维护的内存数据中获取
      if (!localUser?.userName && this.isBigLiveClass) {
        result = this._liveClassSeatStudentList.find(
          (item) => item.userUuid === this.localUserInfo.userUuid
        )
        console.log('---从_liveClassSeatStudentList获取的local信息', result)
      }
    }
    if (!result?.userName) {
      result = {
        ...this.localUserInfo,        
        rtcUid: -1,
        hasAudio: false,
        hasVideo: false,
        hasScreen: false,
        audioStream: null,
        basicStream: null,
        isLocal: true,
        showUserControl: false,
        showMoreBtn: false,
      }
    }
    logger.log("localData&stream", result, this.tempStreams);
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
    logger.log(
      "studentData&stream",
      this._studentData,
      this.tempStreams
    );
    return [...this._studentData]
  }

  @computed
  get screenData(): Array<UserComponentData> {
    const screen = this.memberFullList.reduce(
      (arr: Array<UserComponentData>, item) => {
        if (item?.streams?.subVideo?.value === 1) {
          logger.log("screenMember&hasScreenStream", JSON.stringify(item),!!this.tempStreams[item.rtcUid]?.screenStream);
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

  // 更新直播大班课当前用户的状态
  private _updateLocalLiveStatus(status: LiveClassMemberStatus) {
    this.localMemberSeatStatus = status
  }

  private _createMember(member: any): UserComponentData {
    return  {
      userName: member.userName,
      userUuid: member.userUuid,
      role: member.role,
      rtcUid: member.rtcUid,
      hasAudio: member?.streams?.audio?.value === 1,
      hasVideo: member?.streams?.video?.value === 1,
      hasScreen: false,
      audioStream: this.tempStreams[member?.rtcUid]?.audioStream,
      basicStream: this.tempStreams[member?.rtcUid]?.videoStream,
      isLocal: this.localUserInfo.userUuid === member.userUuid,
      showUserControl:
        this.localUserInfo.userUuid === member.userUuid ||
        this.localUserInfo.role === RoleTypes.host,
      showMoreBtn: this.localUserInfo.role === RoleTypes.host,
      canScreenShare:
        member?.properties?.screenShare?.value === 1 ||
        member.role === RoleTypes.host,
      wbDrawEnable:
        member?.properties?.whiteboard?.drawable === 1 ||
        member.role === RoleTypes.host,
      avHandsUp: member?.properties?.avHandsUp?.value,
      isStudent: [RoleTypes.broadcaster, RoleTypes.audience].includes(member.role)
    }
  }

  /**
   * @description: join a room
   * @param {*}
   * @return {*}
   */
  @action
  async join(joinOptions: JoinOptions): Promise<void> {
    // TODO
    // Get info
    // const localUserInfo = await login(joinOptions.userUuid);
    this.setRoomState(intl.get("课程未开始"));
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
      logger.log("Get the user profile", this.localUserInfo);
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
    logger.log("Current room data", JSON.stringify(this.appStore.roomInfo));
    const { roomUuid, roomName, sceneType } = this.appStore.roomInfo;
    const { userUuid, userName, role, imKey, imToken } = this.localUserInfo;
    this.isLiveStuJoin = Number(sceneType) === RoomTypes.bigClasLive && role !== RoleTypes.host
    this.isLiveTeaJoin = Number(sceneType) === RoomTypes.bigClasLive && role === RoleTypes.host
    const joinSettingInfo = JSON.parse(localStorage.getItem('room-setting') || '{}');
    const { chatroom, teaPlugFlow } = joinSettingInfo;
    // Whether mute is required, the teacher does not need it, and the students are subject to the settings in the classroom
    let isMuteAudio = false;
    try {
      const resource = { chatroom, live: Number(sceneType) === RoomTypes.bigClasLive || (teaPlugFlow && role === RoleTypes.host), seat: this.isLiveTeaJoin};
      if (!this.isLiveStuJoin) {
        const liveClassProperties = {
          roomSeat: {
            seatCount: 5, // 麦位数量
            mode: 1, // 管理员控制模式
            applyMode: 1, // 申请模式：1为申请上麦需要老师同意
          }
        }
        await createRoom(roomUuid, `${userName}${intl.get("的课堂")}`, Number(sceneType), resource, this.isLiveTeaJoin ? liveClassProperties : {})
          .catch(
            (data) => {
              if (data?.code !== 409) {
                logger.error("An error occurred while creating a room", data);
                if (data?.code === 1017) throw Error('1017')
                else throw Error(data?.msg || intl.get("创建异常"));
              }
              isMuteAudio = !!data?.data?.states?.muteAudio?.value && role !== RoleTypes.host
              logger.log('isMuteAudio  ',isMuteAudio)
            }
          );
        this._updateLocalLiveStatus(LiveClassMemberStatus.InRTC)
      } else {
        this._updateLocalLiveStatus(LiveClassMemberStatus.InCDN)
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
          isMuteAudio
        });
      }
    } catch (error: any) {
      console.error("An error occurred while creating or joining a meeting", error);
      runInAction(() => {
        this._joined = false;
      });
      throw Error(error?.message|| error?.msg);
    }
    // 
    // if (!this._webRtcInstance) {

    // }

    if (!this.nim?.nim) {
      await this._nimInstance.loginImServer({
        imAppkey: imKey,
        imAccid: userUuid,
        imToken,
      });
      logger.log("IM initialization", this._nimInstance);
    }

    if (!this.isLiveStuJoin) {
      await this._initializeSDK(isMuteAudio)
    }
    console.log("/snapshot join()")
    await this.getMemberList();
    runInAction(() => {
      this._joinFinish = true;
    })
    window.addEventListener("online", this.updateOnlineStatus.bind(this));
    window.addEventListener("offline", this.updateOnlineStatus.bind(this));
  }

  private async _initializeSDK(isMuteAudio?:boolean) {
    const joinSettingInfo = JSON.parse(localStorage.getItem('room-setting') || '{}');
    const {  userName, role, imKey } = this.localUserInfo;
    const { member = {}, room = {} } = this._entryData;
    const { rtcKey, rtcToken, rtcUid, wbAuth } = member;
    const { checksum, curTime, nonce } = wbAuth;
    this._webRtcInstance = isElectron
      ? new NeElertc(rtcKey)
      : new NeWebrtc(rtcKey);
    // @ts-ignore
    window._webRtcInstance = this._webRtcInstance;
    logger.log("isElectron", isElectron);
    logger.log("RTC initialization", this._webRtcInstance, this.client);
    if (!!joinSettingInfo?.teaPlugFlow && role === RoleTypes.host) {
      this._webRtcInstance.setClientChannelProfile('live')
    }
    const isBigClassStu = this.isBigClass &&
    RoleTypes.host !== this.localUserInfo.role
    if (isBigClassStu) {
      this._webRtcInstance.setClientRole(RoleTypes.audience)
    }

    this._nimInstance.on("controlNotify", (_data: any) => {
      const seatNotifications = [NIMNotifyTypes.HostAgreeSeatApply, NIMNotifyTypes.HostRejectSeatApply, NIMNotifyTypes.HostKickSeater, NIMNotifyTypes.MemberApplySeat, NIMNotifyTypes.MemberCancelApplySeat, NIMNotifyTypes.MemberLeaveSeat, NIMNotifyTypes.SeatStatesChange]
      if (!seatNotifications.includes(_data.body?.cmd)) {
        // 麦位相关通知由聊天室接收并触发处理，此处不再重复处理
        this.nimNotify(_data)
      }
    });
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

    // IM login
    // Add a listener
    this._webRtcInstance.on("client-banned", (_data: any) => {
      logger.log(`${_data.uid} was removed from the room`);
    });
    this._webRtcInstance.on("error", (error: any) => {
      logger.log("webrtc exception", error);
    });
    this._webRtcInstance.on("active-speaker", (_data: any) => {
    // logger.log('Speaking', _data);
    });
    this._webRtcInstance.on("stream-removed", (_data: any) => {
      this.updateStream(_data.uid, _data.mediaType, _data.stream);
    });
    this._webRtcInstance.on("network-quality", (_data: NetStatusItem[]) => {
    // logger.log('Network status', _data);
      runInAction(() => {
        this.networkQuality = _data;
      });
    });
    this._webRtcInstance.on("connection-state-change", (_data: any) => {
      logger.log("Network status changes", _data);
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
              // Automatically switch to a camera if the current camera is unplugged
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
      logger.log("A member joins a room", _data);
      runInAction(() => {
        this.updateMemberList(_data.uid, "add");
        // this.getMemberList().then(() => {
        //   this.syncMember();
        // });
      });
    });
    this._webRtcInstance.on("peer-leave", (_data: any) => {
      logger.log("A member leaves a room", _data);
      runInAction(() => {
        // this.updateMemberList(_data.uid, "remove");
        // this.getMemberList().then(() => {
        //   this.syncMember();
        // });
      });
    });
    this._webRtcInstance.on("stopScreenSharing", async (_data: any) => {
      logger.log("Stopping screen sharing is detected");
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
      logger.log("Notification when subscribing to streams from others succeeds", _data);
      this.updateStream(_data.uid, _data.mediaType, null);
      runInAction(() => {
        this.updateStream(_data.uid, _data.mediaType, _data.stream);
        const item = this.memberFullList.find(item=>item.rtcUid === _data.uid)
        item?.userUuid && this._updateStudentData(item.userUuid, "update", item)
      });
    });
    this._webRtcInstance.on("stream-removed", (_data: any) => {
      logger.log("Received the stream that was unpublished from the peer", _data);
    // this.updateStream(_data.uid, _data.mediaType, null);
    });
    this._webRtcInstance.on("channelClosed", (_data: any) => {
      logger.log("The RTC room is closed", _data);
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
      logger.log("Local "+_data.mediaType+" stream obtained", _data);
      this.updateStream(_data.uid, _data.mediaType, null);
      this.updateMemberList(_data.uid, "add");
      runInAction(() => {
        this.updateStream(_data.uid, _data.mediaType, _data.stream);
        const item = this.memberFullList.find(item=>item.rtcUid === _data.uid)
        item?.userUuid && this._updateStudentData(item.userUuid, "update", item)
      });
    });

    // this._webRtcInstance.on("audioTrackEnded", (_data: any) => {
    //   logger.log("The audio track ended. The device may be unplugged or disabled");
    //   this.appStore.uiStore.showToast("Audio device exception. Reselect and detect the device");
    // });

    // this._webRtcInstance.on("videoTrackEnded", (_data: any) => {
    //   logger.log("The video track ended. The device may be unplugged or disabled");
    //   this.appStore.uiStore.showToast("Video device exception. Reselect and detect the device");
    // });

    this._webRtcInstance.on("accessDenied", (_data: any) => {
      logger.log("Request to get device permissions was declined");
      // this.appStore.uiStore.showToast("Failed to get device permissions");
    })

    this._webRtcInstance.on("beOccupied", (_data: any) => {
      logger.log("The device is occupied when requesting device permissions");
      this.appStore.uiStore.showToast(intl.get("获取麦克风或摄像头权限时，设备被占用。"))
    })

    this._webRtcInstance.on("video-occupied", (_data: any) => {
      logger.log("Video open exception");
      this.appStore.uiStore.showToast(intl.get("视频输入设备异常，请重新进行设备选择和检测！"));
    })

    const mediaStatus = !this.isBigClass ||
    RoleTypes.host === this.localUserInfo.role;
    // RTC join a room
    await this._webRtcInstance.join({
      channelName: this.appStore.roomInfo?.roomUuid,
      uid: rtcUid,
      token: rtcToken,
      audio: isMuteAudio ? false : mediaStatus,
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

  /**
   * 处理直播大班课学生上台的操作
   */
  private async _handleLiveStuOnSeat() {
    const { roomUuid, sceneType } = this.appStore.roomInfo;
    const { userName, role } = this.localData
    this._entryData = await entryRoom({
      userName,
      role,
      roomUuid,
      sceneType: Number(sceneType)
    });
    await this._initializeSDK()
  }

  /**
   * @description: Set the whiteboard status
   * @param {boolean} value
   * @return {*}
   */
  @action
  public setLocalWbDrawEnable(value: boolean): void {
    this._localWbDrawEnable = value;
  }

  /**
   * @description: Set the whiteboard substream
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
   * @description: switch the speaker
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
   * @description: switch the microphone
   * @param {string} deivceId
   * @return {*}
   */
  public async selectAudio(deivceId: string): Promise<void> {
    if (!this.localData?.hasAudio) return
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
   * @description: switch the camera
   * @param {string} deivceId
   * @return {*}
   */
  public async selectVideo(deivceId: string): Promise<void> {
    if (!this.localData.hasVideo) return
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
        this.appStore.uiStore.showToast(intl.get("上台人数超过限制"));
        throw Error(intl.get("上台人数超过限制"));
      }
    });
  }

  public async handsUpActionForSeat(
    toUserUuid: string,
    action: UserSeatOperation | HostSeatOperation,
    operateByHost = false
  ): Promise<void> {
    const toUserName = toUserUuid === this.localUserInfo.userUuid ? this.localUserInfo.userName : (
      this._studentData.find((item)=>item.userUuid === toUserUuid)?.userName
    ) || ''
    await changeMemberSeat({
      roomUuid: this.roomInfo.roomUuid,
      toUserUuid,
      action,
      operateByHost,
      userName: toUserName
    }).catch((err) => {
      if(err.code === 1311) {
        this.appStore.uiStore.showToast(intl.get("上台人数超过限制"));
        throw Error(intl.get("上台人数超过限制"));
      }
    });
  }

  @action
  public async nimNotify(_data, needGetSnapShot = true): Promise<void> {
    const { roomUuid, sequence, type } = _data.body;
    if (roomUuid !== this.roomInfo.roomUuid) {
      logger.log(`The room id of the notification messages from the server does not match. Notification room ID ${roomUuid}, current room ID ${this.roomInfo.roomUuid}`)
      return
    }
    _data.body.roomUuid = roomUuid
    if (type === "RM" && typeof sequence !== 'number') {
      // 麦位相关的通知（RM）没有sequence，直接进行处理
      await this.dealSequenceData(_data.body)
      return
    }
    if (roomUuid === this.roomInfo.roomUuid && type === "R"){
      // logger.log("Notification message from the server", _data.body);
      if (sequence <= this._snapSequence) {
        // drop messages
        console.log("Invalid data, drop ",{sequence, _snapSequence:this._snapSequence})
        return
      } else if (sequence - this._snapSequence === 1) {
        // merge continuous data 
        console.log("Continuous data, merge ",{sequence, _snapSequence:this._snapSequence})
        _data.body.needGetSnapShot = needGetSnapShot;
        this.cachedSequenceData.push(_data.body)
      } else if (sequence - this._snapSequence > 10) {
        // If the data difference exceeds 10 times, get all newest data
        console.log("/snapshot sequence greater than 10 ",{sequence, _snapSequence:this._snapSequence})
        await this.getMemberList();
        return
      } else if(sequence - this._snapSequence <= 10 && !this.incrementIng) {
        this.incrementIng = true
        // If the data difference is less than 10 times, get all data after sequence
        console.log("/sequence sequence difference <=10",{sequence, _snapSequence:this._snapSequence})
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
    while(this.cachedSequenceData.length) {
      const item = this.cachedSequenceData.shift()
      const {needGetSnapShot, sequence} = item
      if (sequence === this._snapSequence + 1) {
        this._snapSequence++
        logger.log("Current notification message", JSON.stringify(item), this._snapSequence);
        await this.dealSequenceData(item, needGetSnapShot)
      }
    }
  }

  @action
  public async dealSequenceData(_data, needGetSnapShot = true): Promise<void> {
    console.log('--memberFullList--')
    console.log(JSON.stringify(this.memberFullList))
    console.log(JSON.parse(JSON.stringify(this.memberFullList)))
    console.log('--memberList--')
    console.log(JSON.stringify(this.memberList))
    console.log(JSON.parse(JSON.stringify(this.memberList)))
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
        const {operatorMember: {rtcUid, userUuid}, members} = data
        this.updateMemberList(rtcUid, "add")
        const flag = this.memberFullList.some((item) => item.rtcUid == rtcUid);
        if (flag) {
          console.log("somebody exists joined again")
        } else {
          this.memberFullList = [...this.memberFullList, ...members]
          this._updateStudentData(userUuid, 'add', members[0])
        }
        break;
      }
      case NIMNotifyTypes.RoomMemberLeave: {
        if (data.members[0]?.rtcUid !== data.operatorMember?.rtcUid) {
          console.log(`成员${data.members[0]?.rtcUid}离开，操作者是${data.operatorMember?.rtcUid}`)
        }
        const {rtcUid, userUuid} = data.members[0]
        this.updateMemberList(rtcUid, "remove").then(() => {
          this.syncMember();
          this._updateStudentData(userUuid, 'delete')
        });
        break;
      }
      case NIMNotifyTypes.StreamChange: {
        logger.log("Member stream changes");
        const { streams, member } = data;
        this.updateMemberStream(member.userUuid, streams);
        break;
      }
      case NIMNotifyTypes.StreamRemove: {
        const { streamType, member } = data;
        // 其他人停止屏幕共享时本端需要暂停播放
        // if (streamType === 'subVideo' && member.userUuid !== this.localData?.userUuid) {
        //   await this.screenData[0]?.basicStream?.stop()
        // }
        setTimeout(()=>{
          this.deleteMemberStream(member.userUuid, streamType);
        }, 100)
        break;
      }
      case NIMNotifyTypes.CustomMessage: {
        needGetSnapShot && await this.getMemberList();
        break;
      }
      case NIMNotifyTypes.MemberApplySeat: {
        const {seatUser: {userUuid, userName}, applyList} = data;
        const properties = {
          avHandsUp: {
            value: HandsUpTypes.studentHandsup
          }
        }
        const _tempMember = {
          userName,
          userUuid,
          role: RoleTypes.audience,
          rtcUid: -1,
          properties
        }
        // 申请上麦时，成员尚未在房间，需要先模拟一条学生数据
        await this._updateStudentData(userUuid, "add", _tempMember)
        this._updateLiveClasStudentData(userUuid, "add", _tempMember)
        this._synchroApplyToStudent(applyList)
        break;
      }
      case NIMNotifyTypes.HostAgreeSeatApply: {
        const {seatUser: {userUuid}, applyList} = data;
        if (this.localUserInfo.userUuid === userUuid) {
          try {
            await this._handleLiveStuOnSeat()
            this._updateLocalLiveStatus(LiveClassMemberStatus.InRTC)
            this.handsUpAction(userUuid, HandsUpTypes.teacherAgree)
          } catch(error) {
            this.deleteMember(userUuid)
            console.warn("直播大班课学生上麦失败", error)
          }
        }
        this._liveClassSeatStudentList.some((item)=>{
          if (item.userUuid === userUuid) {
            item.avHandsUp = HandsUpTypes.teacherAgree
          }
        })
        this._synchroApplyToStudent(applyList)
        break;
      }
      case NIMNotifyTypes.MemberCancelApplySeat: {
        const {seatUser: {userUuid}, applyList} = data;
        const properties = {
          avHandsUp: {
            value: HandsUpTypes.studentCancel
          } 
        }
        await this._updateStudentData(userUuid, "delete")
        this._updateLiveClasStudentData(userUuid, "delete")
        this._synchroApplyToStudent(applyList)
        this.propertiesUpdataByChange(properties, userUuid);
        break;
      }
      case NIMNotifyTypes.HostRejectSeatApply: {
        const {seatUser: {userUuid}, applyList} = data;
        const properties = {
          avHandsUp: {
            value: HandsUpTypes.teacherReject
          }
        }
        await this._updateStudentData(userUuid, "delete")
        this._updateLiveClasStudentData(userUuid, "delete")
        this._synchroApplyToStudent(applyList)
        this.propertiesUpdataByChange(properties, userUuid);
        break;
      }
      case NIMNotifyTypes.HostKickSeater: {
        const {seatUser: {userUuid}, applyList} = data;
        if (this.localUserInfo.userUuid === userUuid) {
          try {
            await this.deleteMember()
            this.appStore.uiStore.showToast(intl.get("老师结束了你的上台操作"));
          } catch(error) {
            console.warn("直播大班课学生被老师踢下台失败", error )
          }
        }
        this._synchroApplyToStudent(applyList)
        break;
      }
      case NIMNotifyTypes.MemberLeaveSeat: {
        const {seatUser: {userUuid}, applyList} = data;
        this.deleteMember(userUuid)
        this._synchroApplyToStudent(applyList)
        break;
      }
      case NIMNotifyTypes.SeatStatesChange: {
        break;
      }
      default:
        break;
    }
  }

  /**
   * @description: Properties change
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
        if (properties) {
          item.properties = Object.assign({}, item.properties, properties)
        } else {
          delete item.properties
        }
        this._updateStudentData(userUuid, "update", item)
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
                  this.appStore.uiStore.showToast(intl.get("老师授予了你白板权限"));
                });
              } else if (item?.drawable === 0) {
                runInAction(() => {
                  this._localWbDrawEnable = false;
                  this.appStore.uiStore.showToast(intl.get("老师取消了你白板权限"));
                });
              }
              break;
            case "streamAV":
              if (item?.value === 1) {
                if (item?.audio === 1) {
                  this.openAudio(userUuid, true, true);
                  this.appStore.uiStore.showToast(intl.get("老师开启了你的麦克风"));
                }
                if (item?.video === 1) {
                  this.openCamera(userUuid, true, true);
                  this.appStore.uiStore.showToast(intl.get("老师开启了你的摄像头"));
                }
                if (item?.audio === 0) {
                  this.closeAudio(userUuid, true, true);
                  this.appStore.uiStore.showToast(intl.get("老师关闭了你的麦克风"));
                }
                if (item?.video === 0) {
                  this.closeCamera(userUuid, true, true);
                  this.appStore.uiStore.showToast(intl.get("老师关闭了你的摄像头"));
                }
              } else if (item.value === 0) {
                if (item?.audio === 0) {
                  this.closeAudio(userUuid, false, false);
                  this.appStore.uiStore.showToast(intl.get("老师关闭了你的麦克风"));
                }
                if (item?.video === 0) {
                  this.closeCamera(userUuid, true, false);
                  this.appStore.uiStore.showToast(intl.get("老师关闭了你的摄像头"));
                }
              }
              break;
            case "screenShare":
              if (item?.value === 1) {
                this.appStore.uiStore.showToast(intl.get("老师授予了你屏幕共享权限"));
              } else if (item.value === 0) {
                this.appStore.uiStore.showToast(intl.get("老师取消了你屏幕共享权限"));
                if (this.screenData.length > 0) {
                  this.stopScreen(this.localData.hasScreen)
                }
              }
              break;
            case "avHandsUp":
              if (item?.value === HandsUpTypes.teacherAgree) {
                if (this.isBigClass) {
                  this._webRtcInstance?.setClientRole(RoleTypes.host)
                }
                this.appStore.uiStore.showToast(intl.get("举手申请通过"));
                // 直播大班课学生在加房时已经开启了音视频
                if (!this.isBigLiveClass) {
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
              }
              if (item?.value === HandsUpTypes.teacherReject) {
                this.setRejectModal(true);
                setTimeout(() => {
                  this.setRejectModal(false);
                }, 3000);
              }
              if (item?.value === HandsUpTypes.teacherOff) {
                this.appStore.uiStore.showToast(intl.get("老师结束了你的上台操作"));
                if (this.isBigClass) {
                  this._webRtcInstance?.setClientRole(RoleTypes.audience)
                }
                this._webRtcInstance?.unpublish();
                if (this._localWbDrawEnable) {
                  this._localWbDrawEnable = false;
                }
              }
              if (item?.value === HandsUpTypes.init) {
                if (this.isBigClass) {
                  this._webRtcInstance?.setClientRole(RoleTypes.audience)
                }
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
   * @description: Member stream changes
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
        this._updateStudentData(userUuid, "update", item)
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
                  logger.log("subvideo triggered", item.value);
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
    logger.log("Stream change complete", this.memberFullList);
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
              logger.log("subvideo triggered", item.value);
              this.tempStreams[item.rtcUid]?.screenStream &&
                this.stopScreen(false);
            } else {
              // this.closeAudioOpenAudo(userUuid);
            }
            break;
          case "audio":
            this.closeAudio(userUuid, false, isBySelf);
            delete item?.streams?.audio;
            break;
          case "video":
            this.closeCamera(userUuid, false, isBySelf);
            delete item?.streams?.video;
            break;
          default:
            break;
        }
        this._updateStudentData(userUuid, "update", item)
        return true;
      }
    });
    logger.log("Deleting stream operation completed", this.memberFullList);
  }

  /**
   * @description: Set the class duration
   * @param {number} time
   * @return {*}
   */
  @action
  public setClassDuration(time: number): void {
    this.classDuration = time || 0;
  }

  /**
   * @description: leave a room
   * @param {*}
   * @return {*}
   */
  @action
  async leave(): Promise<void> {
    logger.log("Perform the leave operation");
    // TODO
    runInAction(() => {
      this._joined = false;
      this.channelClosed = false;
      this._joinFinish = false;
    });
    this._webRtcInstance?.leave();
    this._webRtcInstance?.destroy();
    this._webRtcInstance = null;
    this.appStore?.whiteBoardStore?.destroy();
    // this.classDuration = 0;
    this.setFinishBtnShow(false);
    this.appStore.resetRoomInfo();
    this.setRoomState("");
    GlobalStorage.clear();
    await this.reset();
    await this._nimInstance.logoutImServer();
  }

  @action
  async deleteMember(userUuid=this.localUserInfo.userUuid): Promise<void> {
    const index = this.memberFullList.find((item)=>item.userUuid === userUuid)
    if (index > -1) {
      this.memberFullList.splice(index, 1)
    }
    this._updateStudentData(userUuid, 'delete')
    this._updateLiveClasStudentData(userUuid, "delete")
    if (this.localUserInfo.role === RoleTypes.host || userUuid === this.localUserInfo.userUuid) {
      // 仅支持老师踢学生或者自己离开
      deleteMemberApi({
        roomUuid: this.roomInfo.roomUuid,
        userUuid: userUuid
      })
    }
    if (userUuid === this.localUserInfo.userUuid) {
      this._webRtcInstance?.removeAllListeners();
      this._nimInstance.removeAllListeners();
      await this._webRtcInstance?.leave();
      await this._webRtcInstance?.destroy();
      this._updateLocalLiveStatus(LiveClassMemberStatus.InCDN)
      this._webRtcInstance = null;
      this.appStore.whiteBoardStore.destroy();
    }
  }

  /**
   * @description: Set screen sharing permissions
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
   * @description: Start screen sharing
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
          try {
            await this._webRtcInstance?.open("screen", displayId, id);
          } catch(error) {
            logger.error("An error occurred while sharing screen", error);
            await this.stopScreen(sendControl);
            throw error;
          }
        }, 0);
      } else {
        await this._webRtcInstance?.open("screen", displayId);
        sendControl && await this.changeSubVideoStream(this.localUserInfo.userUuid, 1)
      }
      logger.log("Screen sharing started");
    } catch (error) {
      logger.error("An error occurred while sharing screen", error);
      await this.stopScreen(sendControl);
      throw error;
    }
  }

  /**
   * @description: Stop screen sharing
   * @param {boolean} sendControl
   * @return {*}
   */
  public async stopScreen(sendControl = true): Promise<void> {
    logger.log("stopScreen-sendControl", sendControl);
    try {
      await this._webRtcInstance?.close("screen");
      logger.log("Screen sharing stopped");
      sendControl &&
        (await this.changeSubVideoStream(
          this.localUserInfo.userUuid,
          0
        ).finally(async () => {
          // await this.resetAudio();
        }));
    } catch (error) {
      logger.log("An error occurred while stopping screen sharing-msg", error);
      throw error;
    }
    //
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
   * @description: Change subvideo
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
   * @description: Mute all students
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
   * @description: Mute all students
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
   * @description: Check if the target user is included in the data
   * @param {string} userUuid
   * @return {*}
   */
  private findMember(userUuid: string) {
    return this.memberFullList.filter((item) => item.userUuid === userUuid)[0];
  }

  /**
   * @description: Set permissions
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
   * @description: Turn on the camera
   * @param {string} userUuid
   * @param {boolean} sendControl
   * @return {*}
   */
  public async openCamera(
    userUuid: string,
    sendControl = true,
    isBySelf = true
  ): Promise<void> {
    logger.log("Camera-open", userUuid, sendControl, isBySelf);
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
        if (sendControl) {
          await changeMemberStream({
            roomUuid: this.roomInfo.roomUuid,
            userUuid,
            streamType: "video",
            value: 1,
          })
        } else {
          // Turn on the camera after receiving the notification callback
          setTimeout(async()=>{
            await this._webRtcInstance?.open("video")
          }, 0)
        }
      }
    }
  }

  /**
   * @description: Turn off the camera
   * @param {string} userUuid
   * @param {boolean} sendControl
   * @return {*}
   */
  public async closeCamera(
    userUuid: string,
    sendControl = true,
    isBySelf = true
  ): Promise<void> {
    logger.log("Camera-close", userUuid, sendControl, isBySelf);
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
   * @description: Turn on the microphone
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
   * @description: Turn off the microphone
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
   * @description: Start streaming
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
   * @description: End streaming
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
   * @description: Pause streaming
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
   * @description: Resume streaming
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
   * @description: Set the whiteboard permissions
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
   * @description: Get a room snapshot
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

    if(!this.roomInfo?.roomUuid || this.incrementIng) return
    this.incrementIng = true;
    getSnapShot(this.roomInfo.roomUuid).then(
      ({ sequence, snapshot: { members = [], room = {} }, timestamp = 0 }) => {
        runInAction(() => {
          this.incrementIng = false;
          this._snapRoomInfo = Object.assign({}, this._snapRoomInfo, room);
          this.memberFullList = [...members];
          this._snapSequence = sequence;
          console.log("Update _snapSequence ", this._snapSequence);
          (window as any).memberFullList = this.memberFullList;
          const { states, roomUuid } = room;
          this.stateUpdateByChange(states, timestamp, roomUuid, false);
          const tempStudentList: Array<UserComponentData> = []
          for (const item of this.memberFullList) {
            this.updateMemberList(item.rtcUid, "add");
            const _member = this._createMember(item)
            if(_member.isStudent) {
              if (_member.userUuid === this.localUserInfo.userUuid){
                tempStudentList.unshift(_member)
              } else {
                tempStudentList.push(_member)
              }
            }
          }
          this._studentData = tempStudentList
          logger.log("memberFullList", this.memberFullList, members);
        });
      }
    );
    // 直播大班课需要获取麦位申请列表，并模拟学生数据
    if (this.isBigLiveClass) {
      getSeatApplyList(this.roomInfo.roomUuid).then(
        (seatList) => {
          seatList?.map((item)=>{
            const { userName, userUuid } = item
            const _member = {
              userName,
              userUuid,
              role: RoleTypes.audience,
              rtcUid: -1,
              properties: {
                avHandsUp: {
                  value: HandsUpTypes.studentHandsup
                }
              }
            }
            this._updateStudentData(userUuid, "add", _member)
            this._updateLiveClasStudentData(userUuid, "add", _member)
          })
        }
      )
    }
  }

  /**
   * @description: Set the member info
   * @param {SnapShotResponseMembers} data
   * @return {*}
   */
  @action
  public setBigLiveMemberFullList(data: SnapShotResponseMembers): void {
    this._bigLivememberFullList = [...data];
  }

  /**
   * @description: Room state updated
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
                this.appStore.uiStore.showToast(intl.get("老师执行了全体静音"));
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
                this.setRoomState(intl.get("课程结束"));
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
                fromNotify && this.appStore.uiStore.showToast(intl.get("聊天室已全体禁言"));
              } else {
                fromNotify && this.appStore.uiStore.showToast(intl.get("聊天室已取消禁言"));
              }
            }
            if (!this._snapRoomInfo.states?.muteChat) {
              this._snapRoomInfo.states.muteChat = this._snapRoomInfo.states.muteChat || {}
            }
            this._snapRoomInfo.states.muteChat.value = item.value;
            console.log('this._snapRoomInfo.states.muteChat.value 变化 ',this._snapRoomInfo.states.muteChat.value)
            break;
          default:
            break;
        }
      }
    }
  }

  startPushStream() {
    const { member: {rtcUid}, room: {properties: { live = {} } = {}  }} = this._entryData;
    let task = {}
    if (!isElectron) {
      task = {
        taskId: live?.cid,
        streamUrl: live?.pushUrl,
        record: false,
        config: {
          singleVideoNoTrans: false
        },
        layout: {
          canvas: {
            width: 1280,
            height: 720,
            color: 0, // background color: black
          },
          users:[{
            uid: rtcUid,
            x: 1070, 
            y: 10, // Y coordicate offset of user1 view with respect to the top left corner of the canvas layout
            width: 200, 
            height: 150, 
            adaption: 1, // Adaptive
            pushVideo: true,
            pushAudio: true
          }]
        }
      }
    } else {
      task = {
        task_id: live?.cid,
        stream_url: live?.pushUrl,
        server_record_enabled: true, // Enable Server Recording
        ls_mode: 0, // ive stream type: Video streaming
        config: {
          audio_codec_profile: 0,
          channels: 1,
          sample_rate: 1,
          audio_bitrate: 120,
          single_video_passthrough: false
        },
        layout: {
          width: 1280,
          height: 720,
          background_color: 0, // black
          user_count: 1,
          bg_image: {
            url: " ",
            x: 0,
            y: 0,
            width: 200,
            height: 150
          },
          users:[{
            uid: rtcUid,
            x: 1070,
            y: 10,
            width: 200,
            height: 150,
            adaption: 1,
            video_push: true,
            audio_push: true,
            z_order: 100
          }]
        }
      }
    }
    logger.log("plugFlowTask", task)
    this?._webRtcInstance?.addPlugFlowTask(task)
  }

  /**
   * @description: Keep the data synced
   * @param {*}
   * @return {*}
   */
  @action
  public syncMember(): void {
    runInAction(() => {
      logger.log("View syncMember", this.memberFullList, this.memberList);
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
   * @description: Reset
   * @param {*}
   * @return {*}
   */
  @action
  public async reset(): Promise<void> {
    await this.removeEvent();
    runInAction(() => {
      this.memberList = [];
      this.memberFullList = [];
      this._studentData = [];
      this._liveClassSeatStudentList = [];
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
   * @description: Remove the event listener
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
   * @description: Update memberList
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
   * update studentData
   * @param uid 
   * @param type 
   * @param memberItem 
   */
  private async _updateStudentData(uid: string, type: "add" | "delete" | "update", memberItem:any=null): Promise<void> {
    if(type!=="delete" && uid!==memberItem?.userUuid) return
    await this._dealStuDataFn(this._studentData, type==="delete" ? "delete" : "addOrUpdate", memberItem || {userUuid: uid})
  }

  private async _dealStuDataFn(list: any, type: "delete" | "addOrUpdate", memberItem: any) {
    if(type!=="delete" && !memberItem?.userUuid) return
    const index = list.findIndex((item) => item.userUuid === memberItem.userUuid);
    if (type === "delete") {
      if (index > -1) {
        list.splice(index, 1)
      }
    } else {
      const _member = this._createMember(memberItem)
      if (!_member.isStudent) return
      if (index > -1) {
        list.splice(index, 1, _member)
      } else {
        if (_member.userUuid === this.localUserInfo.userUuid){
          list.unshift(_member)
        } else {
          list.push(_member)
        }
      }
    }
  }

  private async _updateLiveClasStudentData(uid: string, type: "add" | "update" | "delete", memberItem:any=null): Promise<void> {
    if(type!=="delete" && uid!==memberItem?.userUuid) return
    await this._dealStuDataFn(this._liveClassSeatStudentList, type==="delete" ? "delete" : "addOrUpdate", memberItem|| {userUuid: uid})
  }

  // 同步麦位申请信息到学生举手属性
  private async _synchroApplyToStudent(applyList: any[]) {
    applyList.map((item)=> {
      const index = this._studentData.findIndex((ele)=>ele.userUuid === item.userUuid)
      if(index>-1) {
        const student = this._studentData[index]
        student.avHandsUp = HandsUpTypes.studentHandsup
        this._studentData.splice(index, 1, student)
      }
    })
  }

  /**
   * @description: cache stream info
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
    logger.log("stream update", this._tempStreams);
  }

  /**
   * @description: Set a chat room instance
   * @param {any} data
   * @return {*}
   */
  @action
  public setChatRoomInstance(data: any): void {
    this._chatRoom = data;
  }

  /**
   * @description: Destroy a chat room instance
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
