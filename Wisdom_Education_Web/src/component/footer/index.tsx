/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { useState, useRef, useEffect, useMemo } from 'react';
import { useHistory } from 'react-router-dom';
import { observer } from 'mobx-react';
import { useRoomStore } from '@/hooks/store';
import { RoomTypes, StepTypes, PauseTypes, RoleTypes, HandsUpTypes, isElectron } from '@/config';
import { Button, Modal } from 'antd';
import DeviceList from '@/component/device-list';
import MemberList from '@/component/member-list';
import './index.less';
import eleIpc from '@/lib/ele-ipc';
import logger from '@/lib/logger';

let startInterval;

const Footer: React.FC = observer(() => {
  const [modalVisible, setModalVisible] = useState(false);
  const [finishVisible, setFinishVisible] = useState(false);
  const [leavemodalVisible, setLeaveModalVisible] = useState(false);
  const [roomStep, setRoomStep] = useState<StepTypes>(0);
  const [roomPause, setRoomPause] = useState<PauseTypes>(0);
  const [isHost, setIsHost] = useState<boolean>(false);
  const startTime = useRef(0);
  const history = useHistory();
  const roomStore = useRoomStore();
  const { snapRoomInfo: { states: { step, pause } = {} }, roomInfo, classDuration } = roomStore;
  const userInfo = roomStore?.localUserInfo || {};
  const [isEleClose, setIsEleClose] = useState(false);

  const eleIpcIns = useMemo(() => (isElectron ? eleIpc.getInstance() : null), []);

  const handleEndModal = async () => {
    // setModalVisible(true);
    switch (roomPause) {
      case PauseTypes.isPause:
        await roomStore.canclePauseClassRoom();
        break;
      case PauseTypes.underway:
        await roomStore.pauseClassRoom();
        break;
      default:
        break;
    }
  }

  const handleStartModal = async () => {
    setModalVisible(true);
  }

  const handleLeaveModal = async () => {
    setModalVisible(true);
    setLeaveModalVisible(true);
  }

  useEffect(() => {
    if (typeof step?.value === 'number') {
      setRoomStep(step?.value);
    } else {
      setRoomStep(0);
    }
  }, [step?.value]);

  useEffect(() => {
    if (typeof pause?.value === 'number') {
      setRoomPause(pause?.value)
    } else {
      setRoomPause(0);
    }
  }, [pause?.value]);

  useEffect(() => {
    if (typeof userInfo.role === 'string') {
      setIsHost(userInfo.role === RoleTypes.host)
    }
  }, [userInfo.role]);

  useEffect(() => {
    switch (roomStep) {
      case StepTypes.isStart:
        classTimeStart();
        break;
      case StepTypes.isEnd:
        classTimeStop();
        break;
      default:
        break;
    }
  }, [roomStep])
  useEffect(() => {
    switch (roomPause) {
      case PauseTypes.isPause:
        // pauseClass();
        break;
      case PauseTypes.underway:
        // classTimeStart();
        break;
      default:
        break;
    }
  }, [roomPause])

  useEffect(() => {
    startTime.current = classDuration;
  }, [classDuration])

  useEffect(() => {
    return () => {
      classTimeStop();
      // roomStore.setRoomState('课程未开始');
    }
  }, [])

  useEffect(() => {
    if (eleIpcIns) {
      eleIpcIns.on('main-close-before', () => {
        logger.debug('main-close-before');
        setIsEleClose(true);
        handleStartModal();
      });
    }
    return () => {
      eleIpcIns?.removeAllListeners();
    }
  }, [eleIpcIns])



  const classTimeStart = async () => {
    startInterval = setInterval(() => {
      // const now = new Date().getTime();
      const prevToNow = startTime.current + 1000;
      startTime.current = prevToNow;
      const seconds = Math.floor(prevToNow / 1000);
      const minute = Math.floor(seconds / 60);
      const second = seconds - minute * 60;
      const time = `${minute < 10 ? `(0${minute}` : `(${minute}`} : ${second < 10 ? `0${second}` : second})`;
      roomStore.setPrevToNowTime(time);
    }, 1000);
    await roomStore.setRoomState('正在上课');
  }

  const classTimeStop = async () => {
    // roomStore.setPrevToNowTime('');
    clearInterval(startInterval);
    // roomStore.setPrevToNowTime('');
    roomStore.setRoomState('课程结束');

  }

  const pauseClass = async () => {
    clearInterval(startInterval);
    roomStore.setRoomState('课程暂停');
  }


  const handleModalOk = async () => {
    const { roomUuid } = roomInfo;
    if (isHost && !leavemodalVisible) {
      switch (true) {
        case roomStep === StepTypes.init && !isEleClose:
          await roomStore.startClassRoom();
          setRoomStep(StepTypes.isStart)
          setModalVisible(false);
          break;
        case roomStep === StepTypes.isStart || isEleClose:
          await roomStore.endClassRoom();
          await classTimeStop();
          await roomStore.leave();
          // history.push(`/endCourse?roomUuid=${roomUuid}`);
          break;
        case roomStep === StepTypes.isEnd:
          setModalVisible(false);
          break;
        default:
          await roomStore.endClassRoom();
          await classTimeStop();
          await roomStore.leave();
          // history.push(`/endCourse?roomUuid=${roomUuid}`);
          break;
      }
    } else {
      clearInterval(startInterval);
      setModalVisible(false);
      await roomStore.leave();
      history.push('/');
    }
    // if (isEnd && !isHost) {
    //   startTime.current = new Date().getTime();
    //   GlobalStorage.save('startTime', startTime.current)
    //   startInterval = setInterval(() => {
    //     const now = new Date().getTime();
    //     const prevToNow = now - startTime.current;
    //     const seconds = Math.floor(prevToNow/1000);
    //     const minute =  Math.floor(seconds/60);
    //     const second = seconds - minute * 60;
    //     const time = `${minute < 10 ? `(0${minute}` : `(${minute}`} : ${second < 10 ? `0${second}` : second})`;
    //     roomStore.setPrevToNowTime(time);
    //   }, 1000);
    //   await roomStore.startClassRoom();
    //   await roomStore.setRoomState('正在上课');
    // }
    // if (isEnd && isHost) {
    //   await roomStore.endClassRoom();
    //   await roomStore.leave();
    //   history.push('/endCourse');
    //   clearInterval(startInterval);
    //   roomStore.setPrevToNowTime('');
    //   roomStore.setRoomState('课程结束');
    // }
    // if (!isEnd && isHost) {
    //   clearInterval(startInterval);
    //   await roomStore.leave();
    // }
  }

  const handleModalCancel = () => {
    setModalVisible(false);
    setLeaveModalVisible(false);
    setIsEleClose(false);
  }

  const handleFinishModal = async () => {
    setFinishVisible(true);
  }

  const handleFinishModalOk = () => {
    roomStore.handsUpAction(userInfo?.userUuid, HandsUpTypes.studentCancel);
    setFinishVisible(false);
  }

  const handleFinishModalCancel = () => {
    setFinishVisible(false);
  }

  const btnType = !isHost ? "primary" : (roomStep === StepTypes.init ? 'primary' : 'ghost');

  console.log('roomStore.channelClosed', roomStore.channelClosed);

  // RTC受到消息需要结束
  const channelClosed = async () => {
    await roomStore.leave();
    history.push('/');
    setTimeout(() => {
      location.reload();
    });
  }

  useEffect(() => {
    if (roomStore.channelClosed) {
      channelClosed();
    }
  }, [roomStore.channelClosed])

  return (
    <div className="foot-component">
      <div className="footLists">
        <DeviceList />
        {Number(roomInfo.sceneType) !== RoomTypes.oneToOne && <MemberList />}
      </div>
      <div className="roomBtns">
        {/* {isHost && <Button type="ghost" onClick={handleEndModal}>
          {
            roomPause === PauseTypes.isPause? '继续上课' : '暂离课堂'
          }
        </Button>} */}
        {/* {
          isHost && <Button type="ghost" onClick={handleEndModal}>离开课堂</Button>
        } */}
        {/* {(roomStore.finishBtnShow && !isHost) && <Button type="ghost" onClick={handleFinishModal}>下讲台</Button>} */}
        {/* {
          isHost && <Button type="ghost" onClick={handleLeaveModal}>离开课堂</Button>
        } */}
        {

        }
        <Button type={btnType} onClick={handleStartModal}>
          {
            !isHost ?
              '离开课堂' :
              roomStep === StepTypes.init ? '开始上课' : '结束课堂'
          }
        </Button>
        <Modal visible={modalVisible} centered
          onOk={handleModalOk}
          onCancel={handleModalCancel}
          okText="确认"
          cancelText="取消"
          wrapClassName="modal"
        >
          <p className="title">
            {
              !isHost || leavemodalVisible ?
                '确认离开' :
                roomStep === StepTypes.init && !isEleClose ? '确认开始上课' : '结束课堂'
            }
          </p>
          <p className="desc">
            {
              !isHost ? '离开教室后将暂停教学，学生需等待您再次进入课堂方可继续上课' :
                (roomStep === StepTypes.init && !isEleClose ? '开课后教学内容将同步至学生端，并正式开始课堂录制' : '结束课堂后老师和学生均会跳转课堂结束画面，支持查看课程回放')
            }
          </p>
        </Modal>
        <Modal visible={finishVisible} centered
          onOk={handleFinishModalOk}
          onCancel={handleFinishModalCancel}
          okText="确认"
          cancelText="取消"
          wrapClassName="modal"
        >
          <p className="title">下讲台</p>
          <p className="desc">下讲台后，你的视频画面将不再显示在屏幕上，不能继续与老师语音交流。</p>
        </Modal>
      </div>
    </div>
  )
})

export default Footer;
