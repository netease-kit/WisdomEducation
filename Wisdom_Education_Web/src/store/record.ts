/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import { observable, computed, action, makeObservable, toJS } from 'mobx';
import { AppStore } from './index';
import logger from '@/lib/logger';
import { IEvent, ITrack } from '@/pages/record/index';
import { getRecordInfo } from '@/services/api';
import { SceneTypes, RoleTypes } from '@/config';
import intl from 'react-intl-universal';

interface IpStore {
  videoTracks: Array<ITrack>
  wbTracks: Array<ITrack>
  events: Array<IEvent>
  record: Array<any>;
  screenTracks: Array<ITrack>
  sceneType: SceneTypes
}

export class RecordStore {
  public appStore: AppStore;

  @observable
  _store: IpStore = { videoTracks: [], wbTracks: [], events: [], record: [], screenTracks: [], sceneType: SceneTypes.ONE_TO_ONE };
  /**
   * Query method using uid, name，video, and event
   * To pass in Replay, uname must be concatenated with video tracks and wb tracks.
   */
  @observable
  uidToMember: Map<string, any> = new Map();

  // Check if transcoding is completed
  @observable
  isValid = true;

  // The initial position for playback progress
  @observable
  seekToTime = 0;

  constructor(appStore: AppStore) {
    makeObservable(this);
    this.appStore = appStore;
  }

  @computed
  get store() {
    return this._store;
  }

  /**
   * @description: Initialization
   * @param {string | number} roomUuid
   * @param {string | number} rtcCid
   * @return {*}
   */
  @action
  public async init(roomUuid: string | number, rtcCid: string | number): Promise<void> {
    const { uiStore } = this.appStore;
    try {
      uiStore.setLoading(true)
      const res = await getRecordInfo(roomUuid, rtcCid);
      console.log('resresresresresres', res);
      const { recordItemList } = res;
      if (recordItemList && recordItemList.length > 0) {
        const { recordItemList, record, snapshotDto: { snapshot } } = res;
        this.isValid = !!(recordItemList?.length);
        this.convertData(res);
        this.seekToTime = (record.classBeginTimestamp - record.createTime) || 0;
      } else {
        this.isValid = false;
      }

      uiStore.setLoading(false)

    } catch (error) {
      logger.error('An error occurred while initializing', error)
      uiStore.setLoading(false)
    }
  }

  /**
  * @description: Convert data to a format required by the component
  * @param {object} data
  * @return {*}
  */
  @action
  private convertData(data: any): void {
    const entry = data
    const members: Array<any> = [];
    const rawEvents: Array<any> = []
    const rawTracks: Array<any> = []

    for (const event of entry.snapshotDto.snapshot.members) {
      members.push(event)
    }
    for (const event of entry.eventList) {
      rawEvents.push(event)
    }
    for (const record of entry.recordItemList) {
      rawTracks.push(record)
    }

    for (const member of members) {
      this.uidToMember.set(member.rtcUid, member)
    }

    const videoTracks: Array<ITrack> = []
    const screenTracks: Array<ITrack> = []
    const wbTracks: Array<ITrack> = []
    const events: Array<IEvent> = []
    const { record, sceneType } = entry;
    for (const item of rawTracks) {
      const member = this.uidToMember.get(item.rtcUid)
      if (item.type === 'gz') {
        wbTracks.push({
          id: item.roomUid, // ?
          userId: item.roomUid,
          name: item.userName || item.roomUid, // ?
          role: item.role || intl.get('未知身份'),
          url: item.url,
          type: 'whiteboard',
          start: record.startTime,
          end: record.startTime + (item.duration || 11) * 1000
        })
      } else if (!item.subStream) {
        if (item.role === RoleTypes.host) {
          videoTracks.unshift({
            id: item.roomUid, // ?
            userId: item.roomUid,
            name: `${item.userName}(${intl.get("老师")})` || item.roomUid, // ?
            role: item.role || intl.get('未知身份'),
            url: item.url,
            type: 'video',
            start: record.startTime,
            subStream: item.subStream,
            end: record.startTime + (item.duration || 1) * 1000
          })
        } else {
          // fix: 学生上台后下台，再次上台，由于中间有remove事件，后续的流未正常播放
          const tempEvents = rawEvents.concat([])
          tempEvents.reverse()
          const userLeaveEvent = tempEvents.find(ele => ele.roomUid == item.roomUid && ele.type == 2)
          videoTracks.push({
            id: item.roomUid, // ?
            userId: item.roomUid,
            name: `${item.userName}(${intl.get("学生")})` || item.roomUid, // ?
            role: item.role || intl.get('未知身份'),
            url: item.url,
            type: 'video',
            start: item.timestamp, // Start time on student clients must be calibrated with the system time
            subStream: item.subStream,
            end: userLeaveEvent ? userLeaveEvent.timestamp : record.stopTime,
            // end: item.timestamp + (item.duration || 1) * 1000
          })
        }
      } else {
        screenTracks.push({
          id: item.roomUid, // ?
          userId: item.roomUid,
          name: item.userName || item.roomUid, // ?
          role: item.role || intl.get('未知身份'),
          url: item.url,
          type: 'screen',
          start: item.timestamp,
          end: item.timestamp + (item.duration || 1) * 1000,
          subStream: item.subStream
        })
      }
    }

    for (const event of rawEvents) {
      // 只处理课堂开始到结束的事件
      if (event.timestamp >= record.startTime && event.timestamp <= record.stopTime) {
        events.push({
          userId: event.roomUid,
          action: this.checkEventType(event.type, sceneType),
          type: event.type,
          timestamp: event.timestamp
        })
      }
    }
    console.log('events', events);

    this._store = {
      videoTracks, wbTracks, events, record, screenTracks, sceneType
    }

  }

  /**
   * 
   * @param type 成员操作事件类型
   * 1:成员进入房间
   * 2:成员离开房间
   * 3:成员打开音频
   * 4:成员关闭音频
   * 5:成员打开视频
   * 6:成员关闭视频
   * 7:成员打开辅流
   * 8:成员关闭辅流
   * 9:互动大班课学生上台
   * 10:互动大班课学生下台
   * @param sceneType 课程类型
   * @returns 
   */
  private checkEventType = (type: number, sceneType: SceneTypes) => {
    let result: "show" | "hide" | "showScreen" | "remove";
    switch (true) {
      case [SceneTypes.BIG, SceneTypes.BiGLIVE].includes(sceneType) && [3, 5, 9].includes(type):
        result = 'show';
        break;
      case [SceneTypes.BIG, SceneTypes.BiGLIVE].includes(sceneType) && [1].includes(type):
        result = 'remove';
        break;
      case ![SceneTypes.BIG, SceneTypes.BiGLIVE].includes(sceneType) && [1, 3, 5, 9].includes(type):
        result = 'show';
        break;
      case [7].includes(type):
        result = 'showScreen';
        break;
      case [2, 10].includes(type):
        result = 'remove';
        break;
      default:
        // fix: 互动大班课停止共享type为8，应该解析为'hide'
        if ([SceneTypes.BIG, SceneTypes.BiGLIVE].includes(sceneType) && ![4, 6, 8].includes(type)) {
          result = 'remove';
        } else {
          result = 'hide';
        }
        break;
    }
    return result;
  }
}
