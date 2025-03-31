/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */


import React, { useEffect } from 'react';
import { useRoomStore, useWhiteBoardStore } from '@/hooks/store';
import { observer } from 'mobx-react';
import BeforeOrInClass, { classStatus } from '@/component/beforeorin-class';
import WhiteBoard from '@/component/white-board';
import VidepPlayer from '@/component/video-player';
import StudentList from '@/component/student-list';
import ScreenSharing from '@/component/screen-sharing';
import './big-class-live-tea.less';
import { Layout } from 'antd';
import logger from '@/lib/logger';
import { RoleTypes, StepTypes, PauseTypes, HandsUpTypes } from '@/config';
import VideoPlayer from '@/component/video-player';
const { Header, Footer, Sider, Content } = Layout;

const BigClassLiveTea: React.FC = observer(() => {
  const roomStore = useRoomStore();
  const whiteBoardStore = useWhiteBoardStore();
  const { joinFinish, snapRoomInfo } = roomStore;
  const { wbSetFinish } = whiteBoardStore;

  useEffect(() => {
    if (joinFinish && wbSetFinish) {
      whiteBoardStore.getCanvasTrack().then((res) => {
        roomStore.switchScreenWithCanvas('open', res);
      });
    }
  }, [joinFinish, wbSetFinish, roomStore, whiteBoardStore])

  const { teacherData, studentData, screenData, snapRoomInfo: { states: { step, pause } = {} }, localData } = roomStore;
  return (
    <div className="big-class-live-tea">
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
    </div>
  )
})

export default BigClassLiveTea;
