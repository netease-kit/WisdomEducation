/*
* @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
* Use of this source code is governed by a MIT license that can be found in the LICENSE file
*/
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import * as WebRTC2 from './sdk/NIM_Web_WebRTC2_v4.2.1.js';

import { EnhancedEventEmitter } from '../event';
import logger from '../logger';
import { ShareListItem } from '@/config'


// 测试要求加版本信息提示
logger.log('当前g2版本：4.2.1');
export class NeWebrtc extends EnhancedEventEmitter {
  private readonly _appkey: string|undefined;
  private _client: any;
  private _localStream: any;
  private _pubConf: any = {
    audio: true,
    microphoneId: '',
    video: true,
    cameraId: '',
    speakerId: ''
  }
  private _mapRemoteStreams: Map<string, any> = new Map();

  constructor(appKey: string){
    super()
    this._appkey = appKey;
    this._client = WebRTC2.createClient({
      appkey: this._appkey,
      debug: true
    })
    this._localStream = null
    this.initEvents()
  }

  get speakerId(): string {
    return this._pubConf.speakerId
  }

  get microphoneId(): string {
    return this._pubConf.microphoneId
  }

  get cameraId(): string {
    return this._pubConf.cameraId
  }

  get client(): any {
    return this._client;
  }

  private initEvents(): void{
    /*this._client.on('active-speaker', (_data: any) => {
      logger.log('当前在讲话的人: ', _data.uid)
    })*/

    // this._client.on('sync-finish', () => {
    logger.log('登录同步信息完成')
    //   this.emit('@syncFinish')
    // })

    this._client.on('channel-closed', () => {
      logger.log('房间被关闭')
      this._localStream = null
      this._mapRemoteStreams.clear()
      this.emit('channelClosed')
    })

    this._client.on('client-banned', (_data: any) => {
      logger.log(`${_data.uid} 被提出房间`)
      if (_data.uid == this._localStream.streamID) {
        logger.log('自己被移除')
        this._localStream = null
        this._mapRemoteStreams.clear()
        this.emit('client-banned')
      }
    })

    this._client.on('error', (e: any) => {
      logger.log('error:', e)
      this.emit('error')
    })

    this._client.on('aexception', (_data: any) => {
      logger.log('===== exception事件:  %s', JSON.stringify(_data, null, ''))
    })

    this._client.on('active-speaker', (_data: any) => {
      this.emit('active-speaker', _data)
    })

    this._client.on('volume-indicator', (_data: any) => {
      this.emit('volume-indicator', _data);
    });

    this._client.on('network-quality', (_data: any) => {
      this.emit('network-quality', _data);
    });

    this._client.on('deviceAdd', (_data: any) => {
      this.emit('deviceAdd', _data)
    })

    this._client.on('deviceRemove', (_data: any) => {
      this.emit('deviceRemove', _data)
    })

    /*this._client.on('network-quality', (_data: any) => {
      logger.log('房间里所有成员的网络状况:', _data)
    })*/

    this._client.on('connection-state-change', (_data: any) => {
      this.emit('connection-state-change', _data)
    })

    this._client.on('peer-online', (_data: any) => {
      logger.log(`${_data.uid} 加入房间`)
      this.emit('peer-online', _data)
    })

    this._client.on('peer-leave', (_data: any) => {
      logger.log(`${_data.uid} 离开房间`)
      this.emit('peer-leave', _data)
    })

    this._client.on('stream-added', (_data: any) => {
      logger.log('收到别人的发布消息:', _data)
      const uid = _data.stream.streamID
      this._mapRemoteStreams.set(uid, _data.stream)
      this.subscribe(_data.stream)
      this.emit('stream-added', {
        uid,
        basicStream: _data.stream,
      })
    })

    this._client.on('stream-removed', (_data: any) => {
      logger.log('收到别人停止发布的消息:', _data);
      const uid = _data.stream.streamID
      this.emit('stream-removed', {
        uid,
        mediaType: _data.mediaType,
        stream: null,
      })
      if (!_data.stream.video) {
        // reporter.send({
        //   'action_name': 'member_video_state',
        //   'member_uid': _data.stream.streamID,
        //   'room_uid': this._client.getChannelInfo().channelId,
        //   value: 0
        // })
      }
    })

    this._client.on('stream-subscribed', (_data: any) => {
      logger.log('订阅别人的流成功的通知:', _data)
      const uid = _data.stream.streamID
      const stream = _data.stream
      logger.log('uid: %s', uid)
      // let audioStream: any;
      // let videoStream: any;
      // let screenStream: any;
      switch (_data.mediaType) {
        case 'audio':
          stream.play().then().catch((e: any)=>{
            logger.log('播放对方的声音失败:', e)
          })
          // audioStream = stream;
          break;
        case 'video':
          // videoStream = stream;
          break;
        case 'screen':
          // screenStream = stream;
          break;
        default:
          break;
      }
      this.emit('stream-subscribed', {
        uid,
        mediaType: _data.mediaType,
        stream,
      })
      const audioEl = stream._play.audioDom
      this._selectSpeakers(audioEl, this._pubConf.speakerId)
      // if (videoStream) {
      // reporter.send({
      //   'action_name': 'member_video_state',
      //   'member_uid': stream.streamID,
      //   'room_uid': this._client.getChannelInfo().channelId,
      //   value: 1
      // })
      // }
    })

    this._client.on('audioTrackEnded', (_data: any) => {
      this.emit('audioTrackEnded', _data)
    })

    this._client.on('videoTrackEnded', (_data: any) => {
      this.emit('videoTrackEnded', _data)
    })

    this._client.on('stopScreenSharing', (_data: any) => {
      logger.log('屏幕共享被关闭')
      this.emit('stopScreenSharing', _data)
    })
    // @ts-ignore
    window._client = this._client;
  }

  _bindStreamEvents (): void{
    logger.log('_bindStreamEvents()')
  }

  public async join(options: any): Promise<void>{
    logger.log('join()')
    // reporter.send({
    //   'action_name': 'join_channel'
    // })
    /*this._pubConf.audio = options.audio
    this._pubConf.video = options.video*/
    try {
      await this._client.join({
        ...options,
        joinChannelRecordConfig: {
          recordAudio: true,
          recordVideo: true,
          recordType: 0,
        }
      })
      logger.log('join() successed')
      // reporter.send({
      //   'action_name': 'join_channel_success'
      // })
      // const speakers = await this.getSpeakers()
      // this._pubConf.speakerId = speakers[0] && speakers[0].deviceId
      // if (this._pubConf.audio || this._pubConf.video) {
      // logger.log('join() initLocalStream')
      //   await this.initLocalStream(options.uid)
      // logger.log('join() initLocalStream completed')
      // } else {
      // logger.log('do not publish')
      // }
    } catch(e: any) {
      logger.log('join() failed:', e)
      // reporter.send({
      //   'action_name': 'join_channel_failed',
      //   value: e
      // })
      throw new Error(e);
    }
    const speakers: any = await this.getSpeakers()
    this._pubConf.speakerId = speakers[0] && speakers[0].deviceId
    // if (this._pubConf.audio || this._pubConf.video) {
    logger.log('join() initLocalStream')
    await this.initLocalStream(options.uid, options.audio, options.video, options.needPublish).catch(() => logger.log('initLocalStream error'))
    logger.log('join() initLocalStream completed')
    // } else {
    logger.log('do not publish')
    // }
  }

  async leave(): Promise<void> {
    logger.log('leave()', this._client)
    this._localStream && this._localStream.destroy();
    this._localStream = null;
    this._mapRemoteStreams.clear();
    try{
      await this._client?.leave()
      this._client = null;
      WebRTC2.destroy();
    }catch(e: any){
      logger.log('leave failed:', e)
      throw new Error(e);
    }
  }

  async open(type: string, deviceId?: string): Promise<void> {
    logger.log('open()', type)
    if (!this._localStream) {
      return
    }
    try {
      await this._localStream.open({
        type,
        deviceId
      })
      if (type === 'video') {
        this._pubConf.cameraId = deviceId
        // const videoStream = this._localStream.mediaHelper.videoStream
        this.emit('play-local-stream', {
          uid: this._localStream.streamID,
          mediaType: 'video',
          stream: this._localStream,
        })
      } else if (type === 'screen') {
        // await this._localStream.unmuteScreen();
        // const videoStream = this._localStream.mediaHelper.screenStream
        this.emit('play-local-stream', {
          uid: this._localStream.streamID,
          mediaType: 'screen',
          stream: this._localStream,
        })
      } else if (type === 'audio') {
        this._pubConf.microphoneId = deviceId
        this.emit('play-local-stream', {
          uid: this._localStream.streamID,
          mediaType: 'audio',
          stream: this._localStream,
        })
      }
      logger.log('open() localstream', this._localStream)
    } catch(e: any) {
      logger.log('open() failed:', e)
      throw new Error(e);
    }
  }

  async close(type: string): Promise<void> {
    logger.log('close() %s', type)
    if (!this._localStream) {
      return
    }
    try {
      if (this._localStream[type]) {
        await this._localStream.close({
          type
        });
      }
      if (type === 'screen') {
        // await this._localStream.muteScreen();
        const videoStream = this._localStream.mediaHelper.videoStream
        if (videoStream) {
          this.emit('play-local-stream', {
            uid: this._localStream.streamID,
            mediaType: 'screen',
            stream: null,
          })
        }
      } else if (type === 'video') {
        this.emit('play-local-stream', {
          uid: this._localStream.streamID,
          mediaType: 'video',
          stream: null,
        })
      } else if (type === 'audio') {
        this.emit('play-local-stream', {
          uid: this._localStream.streamID,
          mediaType: 'audio',
          stream: null,
        })
      }
    } catch(e: any) {
      logger.log('close() failed:', e)
      throw new Error(e);
    }
  }

  async setVideoProfile(resolution: number, frameRate: number): Promise<void> {
    try {
      await this.close('video');
      await this._localStream.setVideoProfile({
        // 调整视频帧率与分辨率
        resolution,
        frameRate,
      });
      await this.open('video', this._pubConf.cameraId);
      logger.log('setVideoProfile success');
    } catch (e: any) {
      logger.log('setVideoProfile fail:', e);
      throw new Error(e);
    }
  }

  async initLocalStream(uid: number, audio: boolean, video: boolean, needPublish: boolean): Promise<void>{
    logger.log('initLocalStream()')
    this._pubConf.audio = audio;
    this._pubConf.video = video;
    if (!this._localStream) {
      this._localStream = WebRTC2.createStream({
        uid,
        audio: this._pubConf.audio,
        microphoneId: this._pubConf.microphoneId,
        video: this._pubConf.video,
        cameraId: this._pubConf.cameraId,
        client: this._client
      })

      this._bindStreamEvents()
    }
    try{
      await this._localStream.setVideoProfile({ // 调整视频帧率与分辨率
        resolution: WebRTC2.VIDEO_QUALITY_720p,
        frameRate: WebRTC2.CHAT_VIDEO_FRAME_RATE_25
      })
      await this._localStream.init()
      logger.log('initLocalStream() successed')
      if (this._localStream.audio) {
        logger.log('initLocalStream() play local audio stream')
        //this._localStream.play()
        //this._localStream.setLocalRenderMode()
      }

      // const videoStream = this._localStream.mediaHelper.videoStream
      this.emit('play-local-stream', {
        uid: this._localStream.streamID,
        mediaType: 'video',
        stream: this._localStream,
      })
      this.emit('play-local-stream', {
        uid: this._localStream.streamID,
        mediaType: 'audio',
        stream: this._localStream,
      })
      needPublish && await this.publish()
      logger.log(`initLocalStream() publish local stream ${needPublish}`)
      const microphones: any = await this.getMicrophones()
      const cameras: any = await this.getCameras()
      this._pubConf.microphoneId = microphones[0] && microphones[0].deviceId
      this._pubConf.cameraId = cameras[0] && cameras[0].deviceId
    } catch(e: any) {
      logger.log('initLocalStream() failed:', e)
      throw new Error(e);
    }
  }

  async publish(): Promise<void> {
    logger.log('publish()', this._localStream)
    try {
      await this._client.publish(this._localStream)
      logger.log('publish() successed')
    } catch(e: any) {
      logger.log('publish() failed:', e)
      throw new Error(e);
    }
  }

  async unpublish(): Promise<void> {
    logger.log('unpublish()', this._localStream)
    try {
      await this._client.unpublish(this._localStream)
      logger.log('unpublish() successed')
    } catch(e: any) {
      logger.log('unpublish() failed:', e)
      throw new Error(e);
    }
  }

  async subscribe(stream: any, audio = true, video = true, highOrLow = 1): Promise<void> {
    logger.log('subscribe()')
    // reporter.send({
    //   'action_name': 'sub_member_video',
    //   'member_uid': stream.streamID,
    //   'room_uid': this._client.getChannelInfo().channelId
    // })
    try {
      stream.setSubscribeConfig({
        audio,
        video,
        highOrLow,
        screen: true,
      })
      await this._client.subscribe(stream)
      logger.log('subscribe() successed')
    } catch(e: any) {
      logger.log('subscribe() failed:', e)
      throw new Error(e);
    }
  }

  async unsubscribe(stream: any) {
    logger.log('unsubscribe()')
    // reporter.send({
    //   'action_name': 'unsub_member_video',
    //   'member_uid': stream.streamID,
    //   'room_uid': this._client.getChannelInfo().channelId
    // });
    try {
      await this._client.unsubscribe(stream)
      logger.log('unsubscribe() successed')
    } catch(e: any) {
      logger.log('unsubscribe() failed:', e)
      throw new Error(e);
    }
  }

  async selectSpeakers(sinkId: string): Promise<void> {
    if(!sinkId) return
    this._pubConf.speakerId = sinkId
    this._mapRemoteStreams.forEach(stream => {
      const audioDom = stream._play && stream._play.audioDom
      if(audioDom){
        this._selectSpeakers(audioDom, sinkId)
      }
    })
  }

  async _selectSpeakers(element: any, sinkId: string): Promise<void> {
    logger.log('_selectSpeakers(), sinkId: ', sinkId)

    if (element && typeof element.sinkId === 'undefined') {
      logger.log('Browser does not support output device selection.');
      return
    }
    if (!element) return

    try {
      this._pubConf.speakerId = sinkId
      await element.setSinkId(sinkId)
      logger.log('selectSpeakers() successed')
      return
    } catch(e: any) {
      logger.log('selectSpeakers() failed:', e)
      // throw new Error(e);
    }
  }

  /**
   * @description: 选择麦克风
   * @param {string} deviceId
   * @return {*}
   */
  async selectAudio(deviceId: string): Promise<void> {
    if (!deviceId) return;
    await this.close('audio');
    await this.open('audio', deviceId);
  }

  /**
   * @description: 选择摄像头
   * @param {string} deviceId
   * @return {*}
   */
  async selectVideo(deviceId: string): Promise<void> {
    if (!deviceId) return;
    await this.close('video');
    await this.open('video', deviceId);
  }

  async getMicrophones(): Promise<void> {
    logger.log('getMicrophones()')
    try {
      const microphones = await WebRTC2.getMicrophones()
      logger.log('getMicrophones() successed:', microphones)
      return microphones
    } catch(e: any) {
      logger.log('getMicrophones() failed:', e)
      throw new Error(e);
    }
  }

  async getCameras(): Promise<void> {
    logger.log('getCameras()')
    try {
      const cameras = await WebRTC2.getCameras()
      logger.log('getCameras() successed:', cameras)
      return cameras
    } catch(e: any) {
      logger.log('getCameras() failed:', e)
      throw new Error(e);
    }
  }

  async getSpeakers(): Promise<void> {
    logger.log('getSpeakers()')
    try {
      const speakers = await WebRTC2.getSpeakers()
      logger.log('getSpeakers() successed:', speakers)
      return speakers
    } catch(e: any) {
      logger.log('getSpeakers() failed:', e)
      throw new Error(e);
    }
  }

  //设置mic采集音量 0-100
  setCaptureVolume(volume: number): void {
    this._localStream.setCaptureVolume(volume)
  }
  //获取mic的采集音量 0-1
  getAudioLevel(): void {
    return this._localStream && this._localStream.getAudioLevel()
  }

  // 获取网络相关数据
  async getTransportStats(): Promise<any> {
    try {
      const data = await this._client.getTransportStats();
      logger.log('获取网络相关数据', data);
      return data;
    } catch (e: any) {
      logger.log('getTransportStats() failed:', e);
      throw new Error(e);
    }
  }
  // 获取当前会话数据
  async getSessionStats(): Promise<any> {
    try {
      const data = await this._client.getSessionStats();
      logger.log('获取当前会话数据', data);
      return data;
    } catch (e: any) {
      logger.log('getSessionStats() fail:', e);
      throw new Error(e);
    }
  }

  // 获取当前音频流数据
  async getLocalAudioStats(): Promise<any> {
    try {
      const data = await this._client.getLocalAudioStats();
      logger.log('getLocalAudioStats success', data);
      return data;
    } catch (e: any) {
      logger.log('getLocalAudioStats() fail:', e);
      throw new Error(e);
    }
  }

  async getShareList(): Promise<ShareListItem[]> {
    return []
  }

  // 获取远端音频流数据
  async getRemoteAudioStats(): Promise<any> {
    try {
      const data = await this._client.getRemoteAudioStats();
      logger.log('getRemoteAudioStats success', data);
      return data;
    } catch (e: any) {
      logger.log('getRemoteAudioStats() fail:', e);
      throw new Error(e);
    }
  }

  // 获取当前视频流数据
  async getLocalVideoStats(): Promise<any> {
    try {
      const data = await this._client.getLocalVideoStats();
      logger.log('getLocalVideoStats success', data);
      return data;
    } catch (e: any) {
      logger.log('getLocalVideoStats() fail:', e);
      throw new Error(e);
    }
  }

  // 获取远端视频流数据
  async getRemoteVideoStats(): Promise<any> {
    try {
      const data = await this._client.getRemoteVideoStats();
      logger.log('getRemoteVideoStats success', data);
      return data;
    } catch (e: any) {
      logger.log('getRemoteVideoStats() fail:', e);
      throw new Error(e);
    }
  }

  async destroy(): Promise<void> {
    logger.log('destroy()');
    // this._localStream.destroy();
    try {
      this._localStream = null
      this._client.destroy();
      this._client = null;
      WebRTC2.destroy();
      this._mapRemoteStreams.clear()
    } catch (error) {
      logger.log('destroy() error', error);
    }
  }
}
