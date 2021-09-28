/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */


import React, { useEffect, useRef } from 'react';
import { useRoomStore, useWhiteBoardStore } from '@/hooks/store';
import { observer } from 'mobx-react';
import BeforeOrInClass, { classStatus } from '@/component/beforeorin-class';
import WhiteBoard from '@/component/white-board';
import VidepPlayer from '@/component/video-player';
import StudentList from '@/component/student-list';
import ScreenSharing from '@/component/screen-sharing';
import './big-class-live-stu.less';
import { Layout } from 'antd';
import logger from '@/lib/logger';
import { RoleTypes, StepTypes, PauseTypes, HandsUpTypes } from '@/config';

const { Header, Footer, Sider, Content } = Layout;

const BigClassLiveStu: React.FC = observer(() => {
  const roomStore = useRoomStore();
  const whiteBoardStore = useWhiteBoardStore();
  const { snapRoomInfo: { states: { step } = {}, properties: { live } = {} }, localData, joinFinish } = roomStore;
  const { wbSetFinish } = whiteBoardStore;
  // @ts-ignore
  const neplayer = window.neplayer
  const videoRef = useRef<HTMLVideoElement>(null)
  let myPlay;

  const checkUrl = (url) => {
    return url.replace(/(http)/g, 'https');
  }

  useEffect(() => {
    try {
      const joinSettingInfo = JSON.parse(localStorage.getItem('room-setting') || '{}');
      const { nertsLive } = joinSettingInfo;
      if (joinFinish && step?.value !== StepTypes.init && videoRef?.current && live?.cid) {
        //TODO
        myPlay = neplayer('big-class-live-container', {
          width: videoRef.current.clientWidth,
          height: videoRef.current.clientHeight,
          techOrder: ['html5','flvjs'],
          errMsg7: '拉流超时',
          streamTimeoutTime: 30 * 1000
        }, () => {
          let retryCount = 0
          let retryTimer
          const retryInterval = [2000, 2000, 3000, 5000, 5000]

          myPlay.onError(function(err){
            if (retryCount >= 5) {
              console.error('播放器重试次数超过5次，不再重试')
            } else {
              clearTimeout(retryTimer)
              retryTimer = setTimeout(() => {
                setDataSource()
                retryCount += 1
              }, retryInterval[retryCount]);
            }
          });

          function setDataSource() {
            /**
             * _acc2opus-RTS后缀是由于阿里低延时的一些问题，导致需要这样设置才行
             */
            const supportLowDelay = true
            if (nertsLive && supportLowDelay) {
              myPlay.setDataSource([
                {
                  type: 'rts',
                  src: live?.pullRtsUrl + '_acc2opus-RTS',
                },
                // {
                //   type: "application/x-mpegURL",
                //   src: checkUrl(live?.pullHlsUrl)
                // },
                // {
                //   type: "video/x-flv",
                //   src: checkUrl(live?.pullHttpUrl),
                // },
              ]);
            } else {
              myPlay.setDataSource([
                {
                  type: "application/x-mpegURL",
                  src: checkUrl(live?.pullHlsUrl)
                },
                {
                  type: "video/x-flv",
                  src: checkUrl(live?.pullHttpUrl),
                },
              ]);
            }
          }

          setDataSource()
        })
      }
    } catch (error) {
      logger.debug('渲染直播失败', error)
    }
    return () => {
      myPlay && myPlay.release();
      myPlay = null;
    }
  }, [joinFinish, roomStore, step?.value, videoRef])

  return (
    <div className="big-class-live-stu">
      {(step?.value === StepTypes.init || !step?.value) && localData?.role !== RoleTypes.host && <BeforeOrInClass status={classStatus.notStart}/>}
      <div className="big-class-live-stu-content">
        <video ref={videoRef} id="big-class-live-container" className="video-js" x-webkit-airplay="allow" webkit-playsinline="true" controls preload="auto" width="640" height="360" data-setup="{}">
        </video>
      </div>
    </div>
  )
})

export default BigClassLiveStu;
