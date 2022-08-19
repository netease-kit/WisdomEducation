/*
* @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
* Use of this source code is governed by a MIT license that can be found in the LICENSE file
*/
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import * as WebRTC2 from './sdk/NIM_Web_NERTC_v4.5.500.js';

import { EnhancedEventEmitter } from '../event';
import logger from '../logger';
import { ShareListItem } from '@/config';
import rtc_server_conf from './rtc_server_conf.json';
const needPrivate = process.env.REACT_APP_SDK_RTC_PRIVATE;
needPrivate === "true" && logger.log("web-RTC on-premises deployment configuration", rtc_server_conf);


// Version information required for testing
logger.log('Current G2 version: 4.5.500');
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

  get localStream(): any {
    return this._localStream;
  }

  private initEvents(): void{
    /*this._client.on('active-speaker', (_data: any) => {
      logger.log('Current speaker: ', _data.uid)
    })*/

    // this._client.on('sync-finish', () => {
    logger.log('Login sync is complete')
    //   this.emit('@syncFinish')
    // })

    this._client.on('channel-closed', () => {
      logger.log('The room was closed')
      this._localStream = null
      this._mapRemoteStreams.clear()
      this.emit('channelClosed')
    })

    this._client.on('client-banned', (_data: any) => {
      logger.log(`${_data.uid} was removed from the room`)
      if (_data.uid == this._localStream.streamID) {
        logger.log('You were removed')
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
      logger.log('===== exception event:  %s', JSON.stringify(_data, null, ''))
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
      logger.log('Network status of all members in the room:', _data)
    })*/

    this._client.on('connection-state-change', (_data: any) => {
      this.emit('connection-state-change', _data)
    })

    window.navigator.mediaDevices.ondevicechange = (() => {
      logger.log('Device changes monitored')
      this.emit('device-change')
    })

    this._client.on('peer-online', (_data: any) => {
      logger.log(`${_data.uid} joins the room`)
      this.emit('peer-online', _data)
    })

    this._client.on('peer-leave', (_data: any) => {
      logger.log(`${_data.uid} leaves the room`)
      this.emit('peer-leave', _data)
    })

    this._client.on('stream-added', (_data: any) => {
      logger.log('receive messages:', _data)
      const uid = _data.stream.streamID
      this._mapRemoteStreams.set(uid, _data.stream)
      this.subscribe(_data.stream)
      this.emit('stream-added', {
        uid,
        basicStream: _data.stream,
      })
    })

    this._client.on('stream-removed', (_data: any) => {
      logger.log('Received the stream that was unpublished from the peer:', _data);
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
      logger.log('Notification for success of subcription to streams from others:', _data)
      const uid = _data.stream.streamID
      const stream = _data.stream
      logger.log('uid: %s', uid)
      // let audioStream: any;
      // let videoStream: any;
      // let screenStream: any;
      switch (_data.mediaType) {
        case 'audio':
          stream.play().then().catch((e: any)=>{
            logger.log('Failed to play the audio stream sent from the peer:', e)
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
      logger.log('Screen sharing is disabled')
      this.emit('stopScreenSharing', _data)
    })

    this._client.on('accessDenied', (_data: any) => {
      this.emit('accessDenied', _data);
    })

    this._client.on('beOccupied', (_data: any) => {
      this.emit('beOccupied', _data);
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
          recordAudio: false,
          recordVideo: false,
          recordType: 0,
        },
        neRtcServerAddresses: needPrivate === "true" ? rtc_server_conf : {}, // On-premises deployment configuration
      })
      logger.log('join() successed', options)
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
    logger.log('open()', type, deviceId, this._pubConf)
    if (!this._localStream) {
      return
    }
    try {
      type === 'video' && (deviceId = deviceId || this._pubConf.cameraId)
      await this._localStream.open({
        type,
        deviceId
      })
      if (type === 'video') {
        deviceId && (this._pubConf.cameraId = deviceId)
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
        deviceId && (this._pubConf.microphoneId = deviceId)
        this.emit('play-local-stream', {
          uid: this._localStream.streamID,
          mediaType: 'audio',
          stream: this._localStream,
        })
      }
      logger.log('open() localstream', this._localStream)
    } catch(e: any) {
      logger.log('open() failed:', e)
      if((e+'').includes("Could not start video source")) {
        this.emit('video-occupied')
      }
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

  /**
   * @description: switch the whiteboard stream
   * @param {string} type
   * @param {any} stream
   * @return {*}
   */
  async switchScreenWithCanvas(type: string, track?: any): Promise<void> {
    if (!this._localStream) {
      throw new Error("this._localStream is not defined");
    }
    logger.log('Whiteboard substream settings', type, track);
    switch (type) {
      case 'open':
        await this._localStream.open({
          type: 'screen',
          screenVideoSource: track
        })
        break;
      case 'changeToCanvas':
        await this._localStream.switchScreenStream({
          screenVideoSource: track
        })
        break;
      case 'close':
        await this._localStream.close({
          type: 'screen',
        })
        break;
      case 'changeToScreen':
        await this._localStream.switchScreenStream({ screenAudio: false })
        break;
      default:
        break;
    }
  }

  async setVideoProfile(resolution: number, frameRate: number): Promise<void> {
    try {
      await this.close('video');
      await this._localStream.setVideoProfile({
        // Adjust the video frame rate and resolution
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
      await this._localStream.setVideoProfile({ // Adjust the video frame rate and resolution
        resolution: WebRTC2.VIDEO_QUALITY_480p,
        frameRate: WebRTC2.CHAT_VIDEO_FRAME_RATE_15
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
      throw new Error(e);
    }
  }

  /**
   * @description: Select the microphone
   * @param {string} deviceId
   * @return {*}
   */
  async selectAudio(deviceId: string): Promise<void> {
    if (!deviceId) return;
    await this.close('audio');
    await this.open('audio', deviceId);
  }

  /**
   * @description: Select the camera
   * @param {string} deviceId
   * @return {*}
   */
  async selectVideo(deviceId: string): Promise<void> {
    if (!deviceId) return;
    await this.close('video');
    await this.open('video', deviceId);
  }

  async getMicrophones(): Promise<void> {
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
    try {
      const speakers = await WebRTC2.getSpeakers()
      logger.log('getSpeakers() successed:', speakers)
      return speakers
    } catch(e: any) {
      logger.log('getSpeakers() failed:', e)
      throw new Error(e);
    }
  }

  //Set the capture volume of the microphone 0-100
  setMicrophoneCaptureVolume(volume: number): void {
    this._localStream.setCaptureVolume(volume)
  }
  
  //Get the capture volume of the microphone 0-1
  getAudioLevel(): number {
    const result =  this._localStream && this._localStream.getAudioLevel()*100
    logger.log("getAudioLevel ", result)
    return result
  }

  // Set the playback volume 0-100
  setAudioVolume(volume: number): void {
    this._mapRemoteStreams.forEach(stream => {
      if(stream){
        stream.setAudioVolume(volume)
      }
    })
  }

  // Get the network-related data
  async getTransportStats(): Promise<any> {
    try {
      const data = await this._client.getTransportStats();
      logger.log('Get the network-related data', data);
      return data;
    } catch (e: any) {
      logger.log('getTransportStats() failed:', e);
      throw new Error(e);
    }
  }
  // Get the data of thecurrent session
  async getSessionStats(): Promise<any> {
    try {
      const data = await this._client.getSessionStats();
      logger.log('Get the data of the current session', data);
      return data;
    } catch (e: any) {
      logger.log('getSessionStats() fail:', e);
      throw new Error(e);
    }
  }

  // Get the current audio stream data
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

  // Get the stats of the remote audio stream
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

  // Get the stats of the current video stream
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

  // Get the stats of a remote video stream
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

  /**
   * Room Type: call or live streaming
   * @param type 'rtc'|'live'
   */
  async setClientChannelProfile(type='live') {
    try {
      const data = await this._client?.setChannelProfile({mode: type});
      logger.log('setClientChannelProfile success', data);
      return data;
    } catch (e: any) {
      logger.log('setClientChannelProfile() fail:', e);
      throw new Error(e);
    }
  }

  /**
   * Add streaming task
   * @param tasks task
   */
  async addPlugFlowTask(tasks) {
    try {
      const data = await this._client?.addTasks({rtmpTasks: [tasks]});
      logger.log('addPlugFlowTask success', data);
      return data;
    } catch (e: any) {
      logger.log('addPlugFlowTask() fail:', e);
      throw new Error(e);
    }
  }

  async destroy(): Promise<void> {
    logger.log('destroy()');
    // this._localStream.destroy();
    try {
      this._localStream = null
      this._client?.destroy();
      this._client = null;
      WebRTC2.destroy();
      this._mapRemoteStreams.clear()
      window.navigator.mediaDevices.ondevicechange = null;
    } catch (error) {
      logger.log('destroy() error', error);
    }
  }

  async enableAudioVolumeIndication(enable: boolean, interval: number):Promise<void> {
    console.log("")
  }
}
