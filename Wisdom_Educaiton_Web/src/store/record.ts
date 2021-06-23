/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import { observable, computed, action, makeObservable, toJS } from 'mobx';
import { AppStore } from './index';
import logger from '@/lib/logger';
import { IEvent, ITrack } from '@/pages/record/index';
import { getRecordInfo } from '@/services/api';

interface IpStore {
  videoTracks: Array<ITrack>
  wbTracks: Array<ITrack>
  events: Array<IEvent>
}

export class RecordStore {
  public appStore: AppStore;

  @observable
  _store: IpStore = { videoTracks: [], wbTracks: [], events: [] };
  /**
   * uid到name，video, event的查询方法
   * 传递给Replay时，uname要拼接到video tracks，wb tracks中,
   */
  @observable
  uidToMember: Map<string, any> = new Map();

  // 转码是否生成
  @observable
  isValid = true;

  // 回放进度条初始化的位置
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
   * @description: 初始化
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
      logger.error('初始化错误', error)
      uiStore.setLoading(false)
    }
  }

  /**
  * @description: 将数据转换成组件需要的数据
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
    const wbTracks: Array<ITrack> = []
    const events: Array<IEvent> = []
    for (const record of rawTracks) {
      const member = this.uidToMember.get(record.rtcUid)
      if (record.type === 'gz') {
        wbTracks.push({
          id: record.id, // ?
          userId: record.roomUid,
          // name: member ? member.userName : record.roomUid, // ?
          name: record.userName || record.roomUid, // ?
          role: record.role || '未知身份',
          // url: record.url.replace(/^(https|http)?\:/i,""),
          url: record.url,
          type: 'whiteboard',
          start: record.timestamp,
          end: record.timestamp + (record.duration || 11) * 1000
        })
      } else {
        videoTracks.push({
          id: record.id, // ?
          userId: record.roomUid,
          // name: member ? member.userName : record.roomUid, // ?
          name: record.userName || record.roomUid, // ?
          role: record.role || '未知身份',
          // url: record.url.replace(/^(https|http)?\:/i,""),
          url: record.url,
          type: 'video',
          start: record.timestamp,
          end: record.timestamp + (record.duration || 1) * 1000
        })
      }
    }

    for (const event of rawEvents) {
      events.push({
        userId: event.roomUid,
        action: (event.type === 1 || event.type === 3) ? 'show' : 'hide',
        timestamp: event.timestamp
      })
    }

    this._store = {
      videoTracks, wbTracks, events
    }

  }
}
