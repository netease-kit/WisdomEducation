/*
 * @Author: lizhaoxuan
 * @Date: 2021-05-28 14:12:34
 * @LastEditTime: 2021-06-11 16:25:11
 * @LastEditors: Please set LastEditors
 * @Description: lizhaoxuan
 * @FilePath: /app_wisdom_education_web/src/pages/classRoom/big-class.tsx
 */


import React, { useEffect } from 'react';
import { useRoomStore } from '@/hooks/store';
import { observer } from 'mobx-react';
import BeforeOrInClass, { classStatus } from '@/component/beforeorin-class';
import WhiteBoard from '@/component/white-board';
import VidepPlayer from '@/component/video-player';
import StudentList from '@/component/student-list';
import './big-class.less';
import { Layout } from 'antd';
import logger from '@/lib/logger';
import { RoleTypes, StepTypes, PauseTypes, HandsUpTypes } from '@/config';
import VideoPlayer from '@/component/video-player';
const { Header, Footer, Sider, Content } = Layout;

const BigClass: React.FC = observer(() => {
  const roomStore = useRoomStore();

  const { teacherData, studentData, screenData, snapRoomInfo: { states: { step, pause } = {} }, localData } = roomStore;
  return (
    <div className="big-class">
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

export default BigClass;
