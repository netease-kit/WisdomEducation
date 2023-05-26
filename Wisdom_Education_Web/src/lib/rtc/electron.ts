import { PlatForms, RoleTypes, ShareListItem } from "@/config";
import { EnhancedEventEmitter } from "../event";
import logger from "../logger";
import rtc_server_conf from './rtc_server_conf.json';

// @ts-ignore
const { NERtcSDK, ipcRenderer, eleRemote, platform } = window;
const needPrivate = process.env.REACT_APP_SDK_RTC_PRIVATE;
needPrivate === "true" && logger.log("eleactron-RTC on-premises deployment configuration", rtc_server_conf);
ipcRenderer && ipcRenderer.send("hasRender");
ipcRenderer &&
  ipcRenderer.on("onWindowRender", (event, data) => {
    // @ts-ignore
    window.electronLogPath = data.logPath;
    // @ts-ignore
    logger.debug("electronLogPath", window.electronLogPath);
  });

interface DeviceId {
  label: string;
  deviceId: string;
  isDefault?: boolean;
}
interface RemoteUsers {
  id: number;
}
type NetworkQuality = 0 | 1 | 2 | 3 | 4 | 5 | 6;
type MediaType = "audio" | "video" | "screen";
interface IElecStats {
  uid: number;
  tx_quality: NetworkQuality;
  rx_quality: NetworkQuality;
}
interface RtcDevice {
  device_id: string;
  device_name: string;
  transport_type: number;
  suspected_unavailable: boolean;
  system_default_device: boolean;
}
interface JoinOptions {
  channelName: string | number;
  uid: number;
  token: string;
  audio: boolean;
  video: boolean;
  needPublish?: boolean;
}

type VideoLayers = {
  layer_type: 1 | 2; // 1: mainstream; 2: substream
  width: number;
  height: number;
  capture_frame_rate: number;
  render_frame_rate: number;
  encoder_frame_rate: number;
  sent_frame_rate: number;
  sent_bitrate: number;
  target_bitrate: number;
  encoder_bitrate: number;
  codec_name: string;
};
interface LocalStats {
  video_layers_count: number;
  video_layers_list: Array<VideoLayers>;
}

interface RemoteStats {
  uid: number;
  video_layers_count: number;
  video_layers_list: Array<VideoLayers>;
}

interface StatsOpen {
  [key: string]: {
    CaptureResolutionWidth?: number;
    CaptureResolutionHeight?: number;
    RecvResolutionWidth?: number;
    RecvResolutionHeight?: number;
  };
}
type LocalStatsOpen = StatsOpen;

interface RemoteStatsOpen {
  [uid: string]: StatsOpen;
}

interface ScreenRectRegion {
  x: number;
  y: number;
  width: number;
  height: number;
}

interface NERtcParams {
  record_host_enabled?: boolean;
  record_audio_enabled: boolean;
  record_video_enabled: boolean;
  record_type: 0|1|2;
}

export class NeElertc extends EnhancedEventEmitter {
  private _nertcEngine: any;
  private _appKey: string;
  private _remoteStreams: Map<string, any> = new Map();
  private _remoteUsers: Array<RemoteUsers> = [];
  private _pubConf = {
    audio: true,
    microphoneId: "",
    video: true,
    cameraId: "",
    speakerId: "",
  };
  private localStatsOpen: LocalStatsOpen = {};
  private remoteStatsOpen: RemoteStatsOpen = {};
  // private _screen: any;
  private _windowsList: ShareListItem[] = [];
  private _localStream = null;
  private _localVolumeLevel: any;

  constructor(appKey: string) {
    super();
    this._appKey = appKey;
    this._nertcEngine = new NERtcSDK.NERtcEngine();
    // this._nertcEngine.app_key = this._appKey;
    this._nertcEngine.log_dir_path = "";
    const server_config = {
      channel_server: rtc_server_conf.channelServer,
      statistics_server: rtc_server_conf.statisticsServer,
      room_server: rtc_server_conf.roomServer,
      compat_server: rtc_server_conf.compatServer,
      nos_lbs_server: rtc_server_conf.nosLbsServer,
      nos_upload_sever: rtc_server_conf.nosUploadSever,
      nos_token_server: rtc_server_conf.nosTokenServer,
      use_ipv6: rtc_server_conf.useIPv6
    }
    const res = this._nertcEngine.initialize({
      app_key: this._appKey,
      log_file_max_size_KBytes: 0, // Set the upper limit of the log file size in KB. If the value is set to 0, the size is 20MB by default
      // @ts-ignore
      log_dir_path: window.electronLogPath,
      log_level: 3,
      server_config: needPrivate === "true" ? server_config : {} // On-premises deployment configuration
    });
    if (res !== 0) {
      logger.error("initialize fail", this._appKey);
      return;
    } else {
      logger.log("initialize success", this._appKey);
    }
    logger.log("Current G2-Electron version", this._nertcEngine.getVersion());
    this.initEvent();
    // @ts-ignore
    window._nertcEngine = this._nertcEngine;
  }

  get speakerId(): string {
    return this._pubConf.speakerId;
  }

  get microphoneId(): string {
    return this._pubConf.microphoneId;
  }

  get cameraId(): string {
    return this._pubConf.cameraId;
  }

  get client(): any {
    return this._nertcEngine;
  }

  // get screen(): any {
  //   return this._screen;
  // }

  get windowsList(): ShareListItem[] {
    return this._windowsList;
  }

  get localStream(): any {
    return this._localStream;
  }

  public initEvent(): void {
    logger.log("addEleRtcListener");
    // Revoked when a local client joins a room
    this._nertcEngine.on("onJoinChannel", this._onJoinChannel.bind(this));
    // Revoked when a local client leaves a room, releasing resources
    this._nertcEngine.on("onLeaveChannel", this._onLeaveChannel.bind(this));
    // Revoked when a remote client joins a room
    this._nertcEngine.on("onUserJoined", this._onUserJoined.bind(this));
    // Revoked when a remote client leaves a room
    this._nertcEngine.on("onUserLeft", this._onUserLeft.bind(this));
    // Revoked when a remote client turns on the microphone
    this._nertcEngine.on("onUserAudioStart", this._onUserAudioStart.bind(this));
    // Revoked when a remote client turns off the microphone
    this._nertcEngine.on("onUserAudioStop", this._onUserAudioStop.bind(this));
    // Revoked when a remote user turns on the camera
    this._nertcEngine.on("onUserVideoStart", this._onUserVideoStart.bind(this));
    // Revoked when a remote user turns off the camera
    this._nertcEngine.on("onUserVideoStop", this._onUserVideoStop.bind(this));
    // Monitor upstream and downstream network quality
    this._nertcEngine.on("onNetworkQuality", this._onNetworkQuality.bind(this));
    // Revoked when the client gets disconnected from server
    this._nertcEngine.on("onDisconnect", this._onDisconnect.bind(this));
    // Revoked when the network state changes
    this._nertcEngine.on(
      "onConnectionStateChange",
      this._onConnectionStateChange.bind(this)
    );
    window.navigator.mediaDevices.ondevicechange = (() => {
      logger.log('Device changes detected')
      this.emit('device-change')
    })
    this._nertcEngine.on(
      "onLocalVideoStats",
      this._onLocalVideoStats.bind(this)
    );
    this._nertcEngine.on(
      "onRemoteVideoStats",
      this._onRemoteVideoStats.bind(this)
    );
    this._nertcEngine.on(
      "onUserSubStreamVideoStart",
      this._onUserSubStreamVideoStart.bind(this)
    );
    this._nertcEngine.on(
      "onUserSubStreamVideoStop",
      this._onUserSubStreamVideoStop.bind(this)
    );
    ipcRenderer.on("onWindowCreate", this._onWindowCreate.bind(this));
    ipcRenderer.send("hasJoinClass");
    // Call statistics callback
    // this._nertcEngine.on('onRtcStats', this._onRtcStats.bind(this));
    // Callback for the local instantaneous volume in the channel
    this._nertcEngine.on(
      "onLocalAudioVolumeIndication",
      this._onLocalAudioVolumeIndication.bind(this)
    );
  }

  /**
   * @description: Enable a device
   * @param {MediaType} type
   * @param {string} deviceId
   * @return {*}
   */
  public async open(
    type: MediaType,
    deviceId?: string,
    id?: string
  ): Promise<void> {
    logger.log("electron-open", type, deviceId, id);
    let result;
    switch (type) {
      case "audio":
        result = !deviceId
          ? this._nertcEngine.enableLocalAudio(true)
          : this._nertcEngine.setRecordDevice(deviceId);
        this._pubConf.microphoneId = deviceId || this._pubConf.microphoneId;
        break;
      case "video":
        if (deviceId) {
          this._nertcEngine.setVideoDevice(deviceId)
        }
        result = this._nertcEngine.enableLocalVideo(true)
        this._pubConf.cameraId = deviceId || this._pubConf.cameraId;
        break;
      case "screen":
        // eslint-disable-next-line no-case-declarations
        // const rectRegion = await this.getShareRect();
        // eslint-disable-next-line no-case-declarations
        const rectRegion = {
          x: 0,
          y: 0,
          width: 0,
          height: 0,
        };
        // if (platform === PlatForms.mac) {
        if (deviceId) {
          result = this._nertcEngine.startScreenCaptureByDisplayId(
            Number(deviceId || 0),
            { ...rectRegion },
            {
              prefer: 1,
              profile: 2,
              dimensions: { ...rectRegion }
            }
          );
        } else {
          result = this._nertcEngine.startScreenCaptureByWindowId(
            Number(id || 0),
            { ...rectRegion },
            {
              prefer: 1,
              profile: 2,
              dimensions: { ...rectRegion },
              window_focus: true,
              capture_mouse_cursor: true
            }
          );
        }
        // } else if (platform === PlatForms.win) {
        //   result = this._nertcEngine.startScreenCaptureByWindowId(
        //     Number(id || 0),
        //     { ...rectRegion },
        //     {
        //       prefer: 1,
        //       profile: 2,
        //       dimensions: { ...rectRegion }
        //     }
        //   );
        // }
        break;
      default:
        break;
    }
    if (result === 0) {
      logger.log("open success", type);
    } else {
      logger.error("open fail", type, result);
      throw "open fail";
    }
  }
  /**
   * @description: Disable a device
   * @param {MediaType} type
   * @return {*}
   */
  public async close(type: MediaType): Promise<void> {
    logger.log("electron-close", type);
    let result;
    switch (type) {
      case "audio":
        result = this._nertcEngine.enableLocalAudio(false);
        break;
      case "video":
        result = this._nertcEngine.enableLocalVideo(false);
        break;
      case "screen":
        result = this._nertcEngine.stopScreenCapture();
        break;
      default:
        break;
    }
    if (result === 0) {
      logger.log("close success");
    } else {
      logger.error("close fail");
      throw "close fail";
    }
  }

  public async setVideoProfile(): Promise<void> {
    const res = this._nertcEngine.setVideoConfig({
      max_profile: 1,
      width: 320,
      height: 240,
      crop_mode: 0,
      framerate: 15,
      min_framerate: 0,
      bitrate: 0,
      min_bitrate: 0,
      degradation_preference: 1,
    });
    if (res === 0) {
      logger.log("setVideoConfig success");
    } else {
      logger.error("setVideoConfig fail");
      throw "setVideoConfig fail";
    }
  }

  public async setSubStreamRenderMode(
    uid: string | number,
    mode: number
  ): Promise<void> {
    const res = this._nertcEngine.setSubStreamRenderMode(uid, mode);
    if (res === 0) {
      logger.log("setSubStreamRenderMode success");
    } else {
      logger.error("setSubStreamRenderMode fail");
      throw "setSubStreamRenderMode fail";
    }
  }

  public async initLocalStream(): Promise<void> {
    //TODO
  }

  public switchScreenWithCanvas(): void {
    //TODO
  }

  public async enableAudioVolumeIndication(enable: boolean, interval=100): Promise<void> {
    const result = this._nertcEngine.enableAudioVolumeIndication(enable, interval)
    if (result === 0) {
      logger.log("enableVolumeIndication success: ", enable)
      return
    }
    logger.log("enableVolumeIndication fail: ", enable)
  }

  /**
   * @description: Switch a speaker
   * @param {string} deviceId
   * @return {*}
   */
  public async selectSpeakers(deviceId: string): Promise<void> {
    const res = this._nertcEngine.setPlayoutDevice(deviceId);
    if (res === 0) {
      this._pubConf.speakerId = deviceId;
      logger.log("setAudioOutDevice success: ", deviceId);
      return;
    }
    logger.error("setAudioOutDevice fail: ", deviceId);
  }

  /**
   * @description: Switch an audio input device
   * @param {string} deviceId
   * @return {*}
   */
  public async selectAudio(deviceId: string): Promise<void> {
    logger.log("selectAudio", deviceId);
    this.open("audio", deviceId);
  }

  /**
   * @description: Switch a video device
   * @param {string} deviceId
   * @return {*}
   */
  public async selectVideo(deviceId: string): Promise<void> {
    logger.log("selectVideo", deviceId);
    this.open("video", deviceId);
  }

  /**
   * @description: Get a microphone
   * @param {*}
   * @return {*}
   */
  public async getMicrophones(): Promise<DeviceId[]> {
    const mics: RtcDevice[] = this._nertcEngine.enumerateRecordDevices();
    const result = mics.map((item: RtcDevice) => ({
      label: item.device_name,
      deviceId: item.device_id,
      isDefault: item.system_default_device,
    }));
    logger.log("micphones", result);
    return result;
  }

  /**
   * @description: Get a camera
   * @param {*}
   * @return {*}
   */
  public async getCameras(): Promise<DeviceId[]> {
    const videos: RtcDevice[] =
      this._nertcEngine.enumerateVideoCaptureDevices();
    const result = videos.map((item: RtcDevice) => ({
      label: item.device_name,
      deviceId: item.device_id,
      isDefault: item.system_default_device,
    }));
    logger.log("cameras", result);
    return result;
  }

  /**
   * @description: Get a speaker
   * @param {*}
   * @return {*}
   */
  public async getSpeakers(): Promise<DeviceId[]> {
    const speakers: RtcDevice[] = this._nertcEngine.enumeratePlayoutDevices();
    const result = speakers.map((item: RtcDevice) => ({
      label: item.device_name,
      deviceId: item.device_id,
      isDefault: item.system_default_device,
    }));
    logger.log("speakers", result);
    return result;
  }
  
  // Adjust the recording volume 0-400
  setMicrophoneCaptureVolume(volume: number): void {
    const result = this._nertcEngine.adjustRecordingSignalVolume(volume/100*400)
    console.log("adjustRecordingSignalVolume ", volume/100*400, result)
    return result
  }

  //Get the current audio capture volume 0-255
  getAudioLevel(): number {
    logger.log("getAudioLevel ", this._localVolumeLevel)
    return this._localVolumeLevel
  }

  // Set the playback volume 0-400
  setAudioVolume(volume: number): void {
    const result = this._nertcEngine.adjustPlaybackSignalVolume(volume/100*400)
    return result
  }
  

  public async publish(): Promise<void> {
    //
    this.open("video");
  }

  public async unpublish(): Promise<void> {
    this.close("video");
  }

  /**
   * @description: Join a room
   * @param {JoinOptions} options
   * @return {*}
   */
  public async join(options: JoinOptions): Promise<void> {
    logger.log("start join");
    try {
      const mics = await this.getMicrophones();
      const speakers = await this.getSpeakers();
      const videos = await this.getCameras();
      if (!mics.length) {
        // throw { msg: 'The list of microphones is empty' };
      }
      if (!speakers.length) {
        // throw { msg: 'The list of speakers is empty' };
      }
      if (!videos.length) {
        // throw { msg: 'The list of cameras is empty' };
      }
      // Initialize the device ID
      this._pubConf.microphoneId =
        mics.find((item) => item.isDefault)?.deviceId ||
        this._nertcEngine.getRecordDevice();
      this._pubConf.speakerId =
        speakers.find((item) => item.isDefault)?.deviceId ||
        this._nertcEngine.getPlayoutDevice();
      this._pubConf.cameraId =
        videos.find((item) => item.isDefault)?.deviceId ||
        this._nertcEngine.getVideoDevice() ||
        videos[0]?.deviceId ||
        "";
      // options.audio ? this.open('audio', this._pubConf.microphoneId) : this.close('audio')
      // options.video ? this.open('video', this._pubConf.cameraId) : this.close('video');
      await this.setVideoProfile();
      await this.setParameters();
      this.setLocalVideoMirrorMode();
      // 修复互动大班课学生加入后未关音频的问题
      const result = await this._nertcEngine.enableLocalAudio(options.audio)
      console.log("-------静音", !options.audio, result)
      const joinRes = this._nertcEngine.joinChannel(
        options.token,
        options.channelName,
        options.uid
      );
      if (joinRes === 0) {
        logger.log("joinChannel success");
      } else {
        logger.error("joinChannel fail");
        throw "joinChannel fail";
      }
    } catch (error) {
      logger.error("join fail: ", error);
      return Promise.reject(error);
    }
  }

  public async getShareList(): Promise<ShareListItem[]> {
    // const screenDisplay = pcUtil
    //   .enumerateDisplay()
    //   .map((item: string, index: number) => ({
    //     title: `Screen${index + 1}`,
    //     id: parseInt(item, 10),
    //   }));
    // const windowDisplay = pcUtil.enumerateWindows() || [];
    // return [...screenDisplay, ...windowDisplay];
    // if (platform === PlatForms.mac) {
    const sources = await eleRemote.desktopCapturer.getSources({ types: ['window', 'screen'], thumbnailSize: { width: 320, height: 180 }, fetchWindowIcons: true })
    if (sources.length > 0) {
      this._windowsList = sources.map((item) => ({
        id: item.id.split(':')[1],
        displayId: item.display_id,
        name: item.name,
        thumbnail: item.thumbnail.toDataURL(),
        appIcon: item.appIcon?.toDataURL()
      }))
    }
    // } else if (platform === PlatForms.win) {
    //   this._windowsList = [];
    //   const source = await this._nertcEngine.enumerateScreenCaptureSourceInfo(206, 206, 206, 206);
    //   const canvasDom = document.createElement('canvas');
    //   const ctx = canvasDom.getContext('2d')
    //   for (let i = 0; i < source.length; i++) {
    //     const srcinfo = source[i]
    //     if (srcinfo.thumbBGRA !== undefined && srcinfo.thumbBGRA.length !== 0) {
    //       canvasDom.width = srcinfo.thumbBGRA.width
    //       canvasDom.height = srcinfo.thumbBGRA.height
    //       const imgData = new ImageData(new Uint8ClampedArray(srcinfo.thumbBGRA.buffer), srcinfo.thumbBGRA.width, srcinfo.thumbBGRA.height)
    //       ctx?.putImageData(imgData, 0, 0)
    //       srcinfo.captureThumbnail = canvasDom.toDataURL()
    //       ctx?.clearRect(0, 0, canvasDom.width, canvasDom.height)
    //     }
    //     if (srcinfo.iconBGRA !== undefined && srcinfo.iconBGRA.length !== 0) {
    //       canvasDom.width = srcinfo.iconBGRA.width
    //       canvasDom.height = srcinfo.iconBGRA.height
    //       const iconData = new ImageData(new Uint8ClampedArray(srcinfo.iconBGRA.buffer), srcinfo.iconBGRA.width, srcinfo.iconBGRA.height)
    //       ctx?.putImageData(iconData, 0, 0)
    //       srcinfo.captureIcon = canvasDom.toDataURL()
    //       ctx?.clearRect(0, 0, canvasDom.width, canvasDom.height)
    //     }
    //     const sid = srcinfo.displayId || srcinfo.sourceId.toString()
    //     this.windowsList.push({
    //       id: sid,
    //       name: srcinfo.sourceName,
    //       displayId: srcinfo.displayId,
    //       thumbnail: srcinfo.captureThumbnail,
    //       appIcon: srcinfo.captureIcon
    //     })
    //   }
    // }
    logger.log('shareList', this._windowsList);
    return this._windowsList;
  }

  public async getLocalVideoStats() {
    //TODO
  }
  public async getRemoteVideoStats() {
    //TODO
  }

  public async setParameters(options:NERtcParams = {
    record_audio_enabled: false,
    record_video_enabled: false,
    record_type: 0,
  }): Promise<void> {
    // setParameters is represented in JSON format
    const res = await this._nertcEngine.setParameters(JSON.stringify(options));
  }

  /**
   * Set local video mirror mode
   * @param mode 0|1|2 
   */
  public async setLocalVideoMirrorMode(mode=2) {
    const res = await this._nertcEngine.setLocalVideoMirrorMode(mode)
  }

  /**
   * Room Type: call or live streaming
   * @param type 'rtc'|'live'
   */
  async setClientChannelProfile(type='live') {
    const profile = type === 'live' ? 1 : 0 // call:0;streaming:1
    const res = await this._nertcEngine?.setChannelProfile(profile);
    if (res === 0) {
      logger.log('setClientChannelProfile success');
      return
    }
    logger.error('setClientChannelProfile fail:', res);
  }

  /**
   * Setting user roles
   * @param type RoleTypes
   */
  async setClientRole(type=RoleTypes.host) {
    logger.log('setClientRole ', type);
    const res = await this._nertcEngine?.setClientRole(type===RoleTypes.host ? 0 : 1);
    if(res === 0) {
      logger.log('setClientRole success', res);
      return;
    }
    logger.error('setClientRole fail:', res);
  }

  /**
   * Add streaming task
   * @param tasks task
   * @returns 
   */
  addPlugFlowTask(tasks) {
    const res = this._nertcEngine?.addLiveStreamTask(tasks);
    if (res === 0) {
      logger.log('addPlugFlowTask success');
      return
    }
    logger.error('addPlugFlowTask fail:', res);
  }


  /**
   * @description: leave a channel
   * @param {*}
   * @return {*}
   */
  public async leave(): Promise<void> {
    if (!this._nertcEngine) {
      logger.log("no _nertcEngine");
      return;
    }
    const res = this._nertcEngine.leaveChannel();
    if (res === 0) {
      logger.log("leaveChannel success");
    } else {
      logger.error("leaveChannel fail", res);
    }
    ipcRenderer.removeAllListeners("onWindowCreate");
  }

  /**
   * @description: Destroy the instance
   * @param {*}
   * @return {*}
   */
  public async destroy(): Promise<void> {
    logger.log("destroy()");
    try {
      this._nertcEngine.release();
      this._nertcEngine = null;
      window.navigator.mediaDevices.ondevicechange = null;
    } catch (error) {
      logger.error("Destroying failed: ", error);
    }
  }

  // private async getShareRect(): Promise<ScreenRectRegion> {
  //   return await eleRemote.screen.getAllDisplays()[0].bounds;
  // }

  private _onJoinChannel() {
    logger.log("_onJoinChannel", this);
  }

  private _onLeaveChannel() {
    this.destroy();
    logger.log("_onLeaveChannel");
  }

  private async _onUserJoined(uid: number, userName: string) {
    logger.log("_onUserJoined: ", uid, userName);
    this.emit("peer-online", { uid, userName });
  }

  private async _onUserLeft(uid: number, reason: number) {
    logger.log("_onUserLeft: ", uid, reason);
    this.emit("peer-leave", { uid, reason });
  }

  private async _onNetworkQuality(uc: number, stats: IElecStats[]) {
    // logger.log('_onNetworkQuality: ', uc, stats);

    const result = stats.map((item) => ({
      uid: item.uid,
      uplinkNetworkQuality: item.tx_quality,
      downlinkNetworkQuality: item.rx_quality,
    }));
    this.emit("network-quality", result);
  }

  private async _onDisconnect(errorCode: number) {
    logger.log("_onDisconnect: ", errorCode);
    this.emit("onDisconnect", errorCode);
  }

  private async _onConnectionStateChange(state: number, reason: number) {
    logger.log("_onConnectionStateChange: ", state, reason);
    this.emit("connection-state-change", {
      state,
      reason,
    });
  }

  private async _onUserVideoStart(uid: number) {
    const res = this._nertcEngine.subscribeRemoteVideoStream(uid, 0, true);
    if (res === 0) {
      logger.log("subscribeRemoteVideoStream success", uid, true);
    } else {
      logger.error("subscribeRemoteVideoStream fail", uid, true);
    }
  }

  private async _onUserVideoStop(uid: number) {
    const res = this._nertcEngine.subscribeRemoteVideoStream(uid, 0, false);
    if (res === 0) {
      logger.log("subscribeRemoteVideoStream success", uid, true);
    } else {
      logger.error("subscribeRemoteVideoStream fail", uid, true);
    }
  }

  private async _onUserAudioStart(uid: number) {
    //TODO
  }

  private async _onUserAudioStop(uid: number) {
    //TODO
  }

  private async _onLocalVideoStats(data: LocalStats) {
    logger.log("localVideoStats", data);
    const arr = data.video_layers_list || [];
    for (const ele of arr) {
      switch (ele.layer_type) {
        case 1:
          this.localStatsOpen.video = {
            CaptureResolutionWidth: ele.width,
            CaptureResolutionHeight: ele.height,
          };
          break;
        case 2:
          this.localStatsOpen.screen = {
            CaptureResolutionWidth: ele.width,
            CaptureResolutionHeight: ele.height,
          };
          break;
        default:
          break;
      }
    }
  }

  private async _onRemoteVideoStats(data: number, stats: RemoteStats[] = []) {
    for (const item of stats) {
      const arr = item.video_layers_list || [];
      for (const ele of arr) {
        this.remoteStatsOpen[item.uid] = {
          video: {},
          screen: {},
        };
        switch (ele.layer_type) {
          case 1:
            this.remoteStatsOpen[item.uid].video = {
              RecvResolutionWidth: ele.width,
              RecvResolutionHeight: ele.height,
            };
            break;
          case 2:
            this.remoteStatsOpen[item.uid].screen = {
              RecvResolutionWidth: ele.width,
              RecvResolutionHeight: ele.height,
            };
            break;
          default:
            break;
        }
      }
    }
  }

  private async _onUserSubStreamVideoStart(uid: number) {
    logger.log("_onUserSubStreamVideoStart", uid);
    this._nertcEngine.subscribeRemoteVideoSubStream(uid, true);
    this.emit("startScreenSharing", uid);
  }
  private async _onUserSubStreamVideoStop(uid: number) {
    logger.log("_onUserSubStreamVideoStop", uid);
    this._nertcEngine.subscribeRemoteVideoSubStream(uid, false);
    this.emit("stopScreenSharing", uid);
  }

  private async _onWindowCreate(event, data) {
    // logger.log("_onWindowCreate", eleRemote, data);
    // this._screen = eleRemote?.screen;
    // this._windowsList = data.windowsList;
  }

  private async _onLocalAudioVolumeIndication(volume: number) {
    logger.log("_onLocalAudioVolumeIndication", volume)
    this._localVolumeLevel = volume
  }
}
