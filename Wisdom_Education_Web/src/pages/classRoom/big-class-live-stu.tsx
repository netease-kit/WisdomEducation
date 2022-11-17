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
import VideoPlayer from '@/component/video-player';
import './big-class-live-stu.less';
import { Layout } from 'antd';
import logger from '@/lib/logger';
import { RoleTypes, StepTypes, PauseTypes, HandsUpTypes, LiveClassMemberStatus } from '@/config';
import intl from 'react-intl-universal';

const { Header, Footer, Sider, Content } = Layout;

const BigClassLiveStu: React.FC = observer(() => {
  const roomStore = useRoomStore();
  const whiteBoardStore = useWhiteBoardStore();
  const { snapRoomInfo: { states: { step, pause } = {}, properties: { live } = {} }, localData, joinFinish, localMemberSeatStatus, screenData, studentData, teacherData } = roomStore;
  const { wbSetFinish } = whiteBoardStore;
  // @ts-ignore
  const neplayer = window.neplayer
  const videoRef = useRef<HTMLVideoElement>(null)
  let myPlay;

  const checkUrl = (url) => {
    return url.replace(/(http)/g, 'https');
  }

  const setDataSource = () => {
    const supportLowDelay = true
    const joinSettingInfo = JSON.parse(localStorage.getItem('room-setting') || '{}');
    const { nertsLive } = joinSettingInfo;
    if (nertsLive && supportLowDelay) {
      myPlay?.setDataSource([
        {
          type: 'nertc',
          src: live?.pullRtsUrl,
        }
      ]);
    } else {
      myPlay?.setDataSource([
        // {
        //   type: "application/x-mpegURL",
        //   src: checkUrl(live?.pullHlsUrl)
        // },
        {
          type: "video/x-flv",
          src: checkUrl(live?.pullHttpUrl),
        },
      ]);
    }
  }

  const initNEPlayer = () => {
    try {
      if (joinFinish && step?.value !== StepTypes.init && videoRef?.current && live?.cname) {
        //TODO
        if (myPlay) return
        myPlay = neplayer('big-class-live-container', {
          width: videoRef.current.clientWidth,
          height: videoRef.current.clientHeight,
          techOrder: ['html5','flvjs'],
          errMsg7: intl.get('拉流超时'),
          streamTimeoutTime: 30 * 1000,
          autoplay: true,
          controlBar: {
            playToggle: false,
            progressControl: false
          }
        }, () => {
          let retryCount = 0
          let retryTimer
          const retryInterval = [2000, 2000, 3000, 5000, 5000]

          myPlay.onError(function(err){
            if (retryCount >= 5) {
              console.error('The number of retries exceeds 5, No retry attempts will be made')
            } else {
              clearTimeout(retryTimer)
              retryTimer = setTimeout(() => {
                myPlay.refresh()
                retryCount += 1
              }, retryInterval[retryCount]);
            }
          });
          setDataSource()
          
          // 点击播放按钮的时候设置源并播放
          myPlay.corePlayer.bigPlayButton.on('click', ()=>{
            myPlay?.refresh()
          })

        })
      }
    } catch (error) {
      logger.debug('Failed to rendering the live stream', error)
    }
  }

  useEffect(() => {
    if (localMemberSeatStatus === LiveClassMemberStatus.InRTC) {
      myPlay && myPlay.release();
      myPlay = null;
    } else {
      initNEPlayer()
    }
    return () => {
      myPlay && myPlay.release();
      myPlay = null;
    }
  }, [joinFinish, roomStore, step?.value, videoRef, localMemberSeatStatus])

  return (
    <div className="big-class-live-stu">
      {(step?.value === StepTypes.init || !step?.value) && localData?.role !== RoleTypes.host && <BeforeOrInClass status={classStatus.notStart}/>}
      {
        localMemberSeatStatus === LiveClassMemberStatus.InRTC ?
          <>
            {localData?.hasScreen && <ScreenSharing />}
            <Content className="layout-content">
              <WhiteBoard />
              {screenData[0] && <VideoPlayer {...screenData[0]} showUserControl={false} showMediaStatus={false}  />}
              {(step?.value === StepTypes.init || !step?.value) && localData?.role !== RoleTypes.host && <BeforeOrInClass status={classStatus.notStart}/>}
              {pause?.value === PauseTypes.isPause && <BeforeOrInClass status={classStatus.pause}/>}
            </Content>
            <Sider width={230} className="layout-sider">
              <StudentList>
                {!teacherData ? (
                  <VidepPlayer />) : (
                  <VidepPlayer
                    {...teacherData}
                    showMoreBtn={false}
                  />
                )}
                {studentData.map((item) => (
                  item.avHandsUp === HandsUpTypes.teacherAgree && <VidepPlayer key={item.rtcUid} {...item} />
                ))}
              </StudentList>
            </Sider>
          </>
          :
          <div className="big-class-live-stu-content">
            <video ref={videoRef} id="big-class-live-container" className="video-js vjs-fluid" x-webkit-airplay="allow" webkit-playsinline="true" controls preload="auto" width="640" height="360">
            </video>
          </div>
      }
    </div>
  )
})

export default BigClassLiveStu;
