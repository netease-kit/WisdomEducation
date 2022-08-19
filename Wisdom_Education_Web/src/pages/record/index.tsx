/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { useEffect, useRef } from 'react';
import { toJS } from 'mobx';
import { Spin } from "antd";
import Header from '@/component/header';
import { useRecordStore, useUIStore, useRoomStore } from '@/hooks/store';
import { useLocation } from "react-router-dom";
import { observer } from 'mobx-react';
import Replay from '@/component/web-record/Replay';
import { getQueryString } from '@/utils/index';
import Empty from './empty';
import './index.less';
import { isElectron } from '@/config';
import intl from 'react-intl-universal';

export interface IEvent {
  userId: string
  action: 'show' | 'hide' | 'showScreen' | 'remove',
  timestamp: number,
  type?: number,
  payload?: any
}

export interface ITrack {
  id: string
  userId: string,
  name: string,
  role: 'student' | 'teacher'
  url: string,
  type: 'video' | 'whiteboard' | 'screen'
  start: number,
  end: number,
  payload?: any,
  subStream?: boolean
}

const Record: React.FC = observer(() => {

  const recordStore = useRecordStore();

  const uiStore = useUIStore();

  const location = useLocation();

  const roomStore = useRoomStore();

  const replayRef = useRef<any>();

  const { store, isValid, seekToTime } = recordStore;

  useEffect(() => {
    const roomUuid = getQueryString('roomUuid')
    const rtcCid = getQueryString('rtcCid')
    recordStore.init(roomUuid, rtcCid)
    roomStore.setClassDuration(0);
    roomStore.setPrevToNowTime('')
  }, [])

  useEffect(() => {
    if (replayRef && replayRef.current && !uiStore.loading) {
      console.log('seekToTime', seekToTime);
      replayRef.current.handleSeekTo(seekToTime);
    }
  }, [replayRef.current, uiStore.loading])

  const dataStore = toJS(store);

  return (
    <div className="record-wrap">
      <Header title={intl.get('课程回放')} isHave={true} hasBack={true} backMsg={intl.get('返回登录页')} backUrl="/" />
      {uiStore.loading ? <div className="room-loading"><Spin className="room-loading-spin" /></div> : null}
      {
        isValid ? (
          !uiStore.loading && (
            // @ts-ignore
            <Replay ref={replayRef} store={dataStore} config={{ videoWidth: 200 }} />
          )
        ) : (
          <Empty />
        )
      }
    </div>
  )
})

export default Record;
