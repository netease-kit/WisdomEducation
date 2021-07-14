/*
 * @Author: penghao
 * @Date: 2021-06-13 15:02:24
 * @LastEditTime: 2021-06-13 15:02:24
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 */
import React, { useEffect, useRef } from 'react';
import { toJS } from 'mobx';
import { Spin } from "antd";
import Header from '@/component/header';
import { useRecordStore, useUIStore } from '@/hooks/store';
import { useLocation } from "react-router-dom";
import { observer } from 'mobx-react';
import Replay from '@/component/web-record';
import { getQueryString } from '@/utils/index';
import Empty from './empty';
import './index.less';
export interface IEvent {
  userId: string
  action: 'show' | 'hide',
  timestamp: number,
  payload?: any
}

export interface ITrack {
  id: string
  userId: string,
  name: string,
  role: 'student' | 'teacher'
  url: string,
  type: 'video' | 'whiteboard'
  start: number,
  end: number,
  payload?: any
}

const Record: React.FC = observer(() => {

  const recordStore = useRecordStore();

  const uiStore = useUIStore();

  const location = useLocation();

  const replayRef = useRef<any>();

  const { store, isValid, seekToTime } = recordStore;

  useEffect(() => {
    const roomUuid = getQueryString('roomUuid')
    const rtcCid = getQueryString('rtcCid')
    recordStore.init(roomUuid, rtcCid)
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
      <Header title="课程回放" isHave={true} />
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
