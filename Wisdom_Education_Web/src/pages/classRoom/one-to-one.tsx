/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import React, { useEffect } from 'react';
import { useRoomStore } from '@/hooks/store';
import { observer } from 'mobx-react';
import BeforeOrInClass, { classStatus } from '@/component/beforeorin-class';
import WhiteBoard from '@/component/white-board';
import VideoPlayer from '@/component/video-player';
import StudentList from '@/component/student-list';
import ScreenSharing from '@/component/screen-sharing';
import './one-to-one.less';
import { Layout } from 'antd';
import logger from '@/lib/logger';
import { RoleTypes, StepTypes, PauseTypes } from '@/config';
const { Header, Footer, Sider, Content } = Layout;


const OneToOne: React.FC = observer(() => {

  const roomStore = useRoomStore();

  const { teacherData, studentData, screenData, snapRoomInfo: { states: { step, pause } = {} }, localData } = roomStore;

  // const renderContent = () => {
  //   let result: React.ReactElement | null = null;
  //   result = (<BeforeOrInClass status={classStatus.notStart}/>);
  //   return result;
  // }

  return (
    <div className="one-to-one-class">
      {/* <Layout> */}
      {localData?.hasScreen && <ScreenSharing />}
      <Content className="layout-content">
        <WhiteBoard />
        {screenData[0] && <VideoPlayer {...screenData[0]} showUserControl={false} showMediaStatus={false} />}
        {(step?.value === StepTypes.init || !step?.value) && localData?.role !== RoleTypes.host && <BeforeOrInClass status={classStatus.notStart}/>}
        {pause?.value === PauseTypes.isPause && <BeforeOrInClass status={classStatus.pause}/>}
      </Content>
      <Sider width={230} className="layout-sider">
        <StudentList>
          {!teacherData ? (
            <VideoPlayer />) : (
            <VideoPlayer
              {...teacherData}
              showMoreBtn={false}
            />
          )}
          {
            studentData[0] ? (
              <VideoPlayer {...studentData[0]} />
            ) : (
              <VideoPlayer />
            )
          }
        </StudentList>
      </Sider>
      {/* </Layout> */}
    </div>
  )
})

export default OneToOne;
