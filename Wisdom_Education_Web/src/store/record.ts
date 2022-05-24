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
          const userLeaveEvent = rawEvents.find(ele => ele.roomUid == item.roomUid && ele.type == 2)
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
      events.push({
        userId: event.roomUid,
        action: this.checkEventType(event.type, sceneType),
        timestamp: event.timestamp
      })
    }
    console.log('events', events);

    this._store = {
      videoTracks, wbTracks, events, record, screenTracks, sceneType
    }

  }

  private checkEventType = (type: number, sceneType: SceneTypes) => {
    let result: "show" | "hide" | "showScreen" | "remove";
    switch (true) {
      case SceneTypes.BIG === sceneType && [3, 5, 9].includes(type):
        result = 'show';
        break;
      case SceneTypes.BIG === sceneType && [1].includes(type):
        result = 'remove';
        break;
      case SceneTypes.BIG !== sceneType && [1, 3, 5, 9].includes(type):
        result = 'show';
        break;
      case [7].includes(type):
        result = 'showScreen';
        break;
      case [2, 10].includes(type):
        result = 'remove';
        break;
      default:
        if (SceneTypes.BIG === sceneType && ![4, 6].includes(type)) {
          result = 'remove';
        } else {
          result = 'hide';
        }
        break;
    }
    return result;
  }
}
