/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { useState, useCallback, useEffect, useMemo, useRef } from 'react';
import { observer } from 'mobx-react';
import { Button, Tooltip, Modal, Checkbox, Tabs, Input, message } from 'antd';
import './index.less';
import logger from '@/lib/logger';
import { HandsUpTypes, RoleTypes, RoomTypes, UserComponentData } from '@/config';
import { useRoomStore, useUIStore } from '@/hooks/store';
import { Message } from '../chatroom/chatroomHelper';
import ChatRoom from '@/component/chatroom'

const MemberList = observer(() => {
  const [handSpeakVisible, setHandSpeakVisible] = useState(false);
  const [handleHandsVisible, setHandleHandsVisible] = useState(false);
  const [handleMemberVisible, setHandleMemberVisible] = useState(false);
  const [chatVisible, setChatVisible] = useState(false);
  const [inputVisible, setInputVisible] = useState(false);
  const [handsVisible, setHandsVisible] = useState(false);
  const [chatIndex, setChatIndex] = useState<number>(-1);
  const [moreVisible, setMoreVisible] = useState(false)

  const [onlineMember, setOnlineMember] = useState<Array<UserComponentData>>([]);
  const [allMember, setAllMember] = useState<Array<UserComponentData>>([]);
  const [searchValue, setSearchValue] = useState('');
  const [messageCount, setMessageCount] = useState<number>(0);
  const [muteAllBtnHover, setMuteAllBtnHover] = useState(false);
  // const count = useRef(0);

  const roomStore = useRoomStore();
  const uiStore = useUIStore();
  const { roomInfo, studentData, nim, localUserInfo, snapRoomInfo } = roomStore
  const userInfo = roomStore?.localData || {};

  const isBySelf = useCallback((userUuid) => userUuid === roomStore?.localData?.userUuid, [roomStore?.localData])


  const handleMemberClick = () => {
    setHandleMemberVisible(true);
  }

  const handleMemberCancel = () => {
    setHandleMemberVisible(false);
  }

  const handleHandSpeakClick = () => {
    if (userInfo.role === RoleTypes.host) {
      setHandleHandsVisible(true);
    } else {
      setHandsVisible(true);
    }
  }

  const handleHandsCancel = () => {
    setHandleHandsVisible(false);
  }

  const handleAudioClick = async (item) => {
    if (item.hasAudio) {
      await roomStore.closeAudio(item.userUuid, true, isBySelf(item.userUuid));
      await uiStore.showToast("操作成功");
    } else {
      await roomStore.openAudio(item.userUuid, true, isBySelf(item.userUuid));
      await uiStore.showToast("操作成功");
    }
  }

  const handleVideoClick = async (item) => {
    if (item.hasVideo) {
      await roomStore.closeCamera(item.userUuid, true, isBySelf(item.userUuid));
      await uiStore.showToast("操作成功");
    } else {
      await roomStore.openCamera(item.userUuid, true, isBySelf(item.userUuid));
      await uiStore.showToast("操作成功");
    }
  }

  const handleChatModal = () => {
    setChatVisible(true);
  }

  const handleChatCancel = () => {
    setChatVisible(false);
    setMessageCount(0)
  }

  const handleHandsModalOk = async () => {
    switch (userInfo?.avHandsUp) {
      case HandsUpTypes.teacherAgree:
        if (userInfo.hasScreen) {
          await roomStore.stopScreen();
        }
        await roomStore.handsUpAction(userInfo?.userUuid, HandsUpTypes.init);
        break;
      case HandsUpTypes.studentHandsup:
        await roomStore.handsUpAction(userInfo?.userUuid, HandsUpTypes.studentCancel);
        break;
      default:
        await roomStore.handsUpAction(userInfo?.userUuid, HandsUpTypes.studentHandsup);
        break;
    }
    setHandsVisible(false);
  }

  const handleHandsModalCancel = () => {
    setHandsVisible(false);
  }

  useEffect(() => {
    setHandSpeakVisible(userInfo?.role !== RoleTypes.host &&
      (userInfo?.avHandsUp === HandsUpTypes.teacherAgree || userInfo?.avHandsUp === HandsUpTypes.studentHandsup))
  }, [userInfo?.avHandsUp, userInfo?.role])

  const handleTabsChange = (key) => {
    if (key === 'all') {
      setInputVisible(true);
    } else {
      setInputVisible(false);
    }
  }

  const handsUpAction = async (userUuid: string, value: HandsUpTypes) => {
    try {
      const res = await roomStore.handsUpAction(userUuid, value)
      console.log('res', res);
      await uiStore.showToast("操作成功");
      switch (value) {
        case HandsUpTypes.teacherAgree:
          // await roomStore.changeMemberStreamProperties(userUuid, 1, 1, 1)
          break;
        case HandsUpTypes.teacherOff:
          // await roomStore.changeMemberStreamProperties(userUuid, 0, 0, 0);
          await roomStore.changeSubVideoStream(userUuid, 0);
          break;
        default:
          // await roomStore.changeMemberStreamProperties(userUuid, 0, 0, 0)
          break;
      }
    } catch (error) {
      console.error('error', error);
    }
  }


  const isMemberMuteAll = useMemo(() => {
    const isMute = studentData.some(el => {
      return el.hasAudio
    })
    return isMute;
  }, [studentData])

  const handleMuteAll = () => {
    if (isMemberMuteAll) {
      roomStore.muteAllStudent();
      logger.log("全体静音")
    }
  }

  const onBanChange = (e) => {
    const value = Number(e.target.checked);
    roomStore.muteChatroom(value);
    logger.log("全体禁言", value);
  }

  const handleSetWbEnableDraw = async (userUuid: string, value: number) => {
    if (roomStore.classStep !== 1) {
      await uiStore.showToast("请先开始上课");
      return;
    }
    roomStore.setWbEnableDraw(userUuid, value);
    await uiStore.showToast("操作成功");
  }

  const handleSetAllowScreen = async (userUuid: string, value: number) => {
    if (roomStore.classStep !== 1) {
      await uiStore.showToast("请先开始上课");
      return;
    }
    if (value === 0) await roomStore.changeSubVideoStream(userUuid, value);
    await roomStore.setAllowScreenShare(userUuid, value);
    await uiStore.showToast("操作成功");
  }

  const MoreContent = useCallback((item) => {
    return (
      <ul onClick={() => setMoreVisible(false)}>
        {item.wbDrawEnable ?
          <li><Button onClick={() => handleSetWbEnableDraw(item.userUuid, 0)} type="text">取消白板权限</Button></li> :
          <li><Button onClick={() => handleSetWbEnableDraw(item.userUuid, 1)} type="text">授予白板权限</Button></li>}
        {item.canScreenShare ?
          <li><Button onClick={() => handleSetAllowScreen(item.userUuid, 0)} type="text">取消共享权限</Button></li> :
          <li><Button onClick={() => handleSetAllowScreen(item.userUuid, 1)} type="text">授予共享权限</Button></li>}
        {
          RoomTypes.bigClass === Number(roomStore?.roomInfo?.sceneType) && item.avHandsUp === HandsUpTypes.teacherAgree &&
          <li><Button onClick={() => handsUpAction(item.userUuid, HandsUpTypes.teacherOff)} type="text">请他下台</Button></li>
        }
      </ul>
    );
  }, [roomStore]);

  const handleChangeSearch = (e) => {
    setSearchValue(e.target.value)
  }

  const handleSearchMember = useCallback(
    () => {
      if (!searchValue) {
        setAllMember(studentData)
        logger.log('搜索信息', searchValue, studentData)

      } else {
        const newArray = studentData.filter((item) => item?.userName.includes(searchValue));
        setAllMember(newArray)
      }
      logger.log('搜索信息', searchValue, studentData)
    },
    [searchValue, studentData],
  )

  const handleMsg = useCallback(() => {
    let result = '举手';
    roomStore.setFinishBtnShow(false);
    if (userInfo?.role === RoleTypes.host) {
      result = '举手申请';
      roomStore.setFinishBtnShow(false);
    } else {
      switch (userInfo?.avHandsUp) {
        case HandsUpTypes.studentHandsup:
          result = '举手中';
          roomStore.setFinishBtnShow(false);
          break;
        case HandsUpTypes.teacherAgree:
          result = '下讲台';
          roomStore.setFinishBtnShow(true);
          break;
        default:
          break;
      }
    }
    return result;
  }, [userInfo?.avHandsUp, userInfo?.role])

  useEffect(() => {
    const newArray = studentData.filter((item) => item?.avHandsUp === HandsUpTypes.teacherAgree);
    setOnlineMember(newArray);
    handleSearchMember();
  }, [studentData]);

  useEffect(() => {
    // 临时的一个方案，不然modal内的聊天室无法先获取消息
    if (nim?.nim && snapRoomInfo?.properties?.chatRoom?.chatRoomId) {
      setChatVisible(true);
      Promise.resolve().then(() => {
        setChatVisible(false);
      })
      setTimeout(() => {
        setChatIndex(1000);
      }, 300);
    }
  }, [nim?.nim && snapRoomInfo?.properties?.chatRoom?.chatRoomId]);

  const memberContent = () => {
    return (
      <>
        {inputVisible ?
          <div className="search">
            <Input placeholder="请输入关键词搜索" onChange={handleChangeSearch} allowClear />
            <Button onClick={handleSearchMember} >搜索</Button>
          </div> : null
        }
        <ul>
          {
            RoomTypes.bigClass !== Number(roomInfo.sceneType) &&
            studentData.map((item) => (
              <li key={item.rtcUid}>
                <span>{item.userName}</span>
                <div className="buttons">
                  {item.wbDrawEnable && <img src={require('@/assets/imgs/whiteBoard.png').default} alt="whiteBoard" />}
                  {/*白板授权状态*/}
                  {item.canScreenShare && <img src={require('@/assets/imgs/screenShare.png').default} alt="screenShare" />}
                  {/*屏幕共享状态*/}
                </div>
                <div className="icons">
                  <Button
                    type="text"
                    onClick={() => handleAudioClick(item)}
                    icon={item.hasAudio ? <img src={require('@/assets/imgs/audioOpen.png').default} alt="audioOpen" /> : <img src={require('@/assets/imgs/audioClose.png').default} alt="audioClose" />}
                  />
                  <Button
                    type="text"
                    onClick={() => handleVideoClick(item)}
                    icon={item.hasVideo ? <img src={require('@/assets/imgs/videoOpen.png').default} alt="videoOpen" /> : <img src={require('@/assets/imgs/videoClose.png').default} alt="videoClose" />}
                  />
                  {userInfo.role === RoleTypes.host &&
                    <Tooltip placement="bottomRight" title={MoreContent(item)} overlayClassName="tooltip" trigger="click">
                      <Button
                        // onClick={(e) => {e.stopPropagation();setMoreVisible(true)}}
                        type="text"
                        icon={<div className="more">···</div>}
                      />
                    </Tooltip>
                  }
                </div>
              </li>
            ))
          }
          {
            (RoomTypes.bigClass === Number(roomInfo.sceneType) && !inputVisible) &&
            onlineMember.map((item) => (
              <>
                <li key={item.rtcUid}>
                  <span>{item.userName}</span>
                  <div className="buttons">
                    {item.wbDrawEnable && <img src={require('@/assets/imgs/whiteBoard.png').default} alt="whiteBoard" />}
                    {/*白板授权状态*/}
                    {item.canScreenShare && <img src={require('@/assets/imgs/screenShare.png').default} alt="screenShare" />}
                    {/*屏幕共享状态*/}
                  </div>
                  <div className="icons">
                    <Button
                      type="text"
                      onClick={() => handleAudioClick(item)}
                      icon={item.hasAudio ? <img src={require('@/assets/imgs/audioOpen.png').default} alt="audioOpen" /> : <img src={require('@/assets/imgs/audioClose.png').default} alt="audioClose" />}
                    />
                    <Button
                      type="text"
                      onClick={() => handleVideoClick(item)}
                      icon={item.hasVideo ? <img src={require('@/assets/imgs/videoOpen.png').default} alt="videoOpen" /> : <img src={require('@/assets/imgs/videoClose.png').default} alt="videoClose" />}
                    />
                    {userInfo.role === RoleTypes.host &&
                      <Tooltip placement="bottomRight" title={MoreContent(item)} overlayClassName="tooltip" trigger="click">
                        <Button
                          type="text"
                          icon={<div className="more">···</div>}
                        />
                      </Tooltip>
                    }
                  </div>
                </li>
              </>
            ))
          }
          {
            (RoomTypes.bigClass === Number(roomInfo.sceneType) && inputVisible) &&
            allMember.map((item) => (
              // <li key={item.rtcUid}>
              //   <span>{item.userName}</span>
              //   <div className="buttons">
              //     {item.wbDrawEnable && <img src={require('@/assets/imgs/whiteBoard.png').default} alt="whiteBoard" />}
              //     {/*白板授权状态*/}
              //     {item.canScreenShare && <img src={require('@/assets/imgs/screenShare.png').default} alt="screenShare" />}
              //     {/*屏幕共享状态*/}
              //   </div>
              //   <div className="icons">
              //     <Button
              //       type="text"
              //       onClick={() => handleAudioClick(item)}
              //       icon={item.hasAudio ? <img src={require('@/assets/imgs/audioOpen.png').default} alt="audioOpen" /> : <img src={require('@/assets/imgs/audioClose.png').default} alt="audioClose" />}
              //     />
              //     <Button
              //       type="text"
              //       onClick={() => handleVideoClick(item)}
              //       icon={item.hasVideo ? <img src={require('@/assets/imgs/videoOpen.png').default} alt="videoOpen" /> : <img src={require('@/assets/imgs/videoClose.png').default} alt="videoClose" />}
              //     />
              //     {userInfo.role === RoleTypes.host &&
              //       <Tooltip placement="bottomRight" title={MoreContent(item)} overlayClassName="tooltip" trigger="click">
              //         <Button
              //           type="text"
              //           icon={<div className="more">···</div>}
              //         />
              //       </Tooltip>
              //     }
              //   </div>
              // </li>
              <>
                <li className="all-member" key={item.rtcUid}>
                  <span>{item.userName}</span>
                </li>
              </>
            ))
          }
        </ul>
      </>
    )
  }

  const memberTabs = () => (
    <Tabs defaultActiveKey="online" onChange={handleTabsChange}>
      <Tabs.TabPane tab={`连线成员 (${onlineMemberLength})`} key="online">
        {/* {memberTabs} */}
      </Tabs.TabPane>
      <Tabs.TabPane tab={`全部成员 (${studentData?.length})`} key="all">
        {/* {memberTabs} */}
      </Tabs.TabPane>
    </Tabs>
  );

  const memberAllLength = studentData?.filter((item) => {
    return item?.avHandsUp === HandsUpTypes.studentHandsup
  })?.length

  const onlineMemberLength = studentData?.filter((item) => {
    return item?.avHandsUp === HandsUpTypes.teacherAgree
  })?.length

  const content = () => (
    <div className="mute-desc">全体静音为默认关闭所有成员的麦克风，但成员依旧能主动开启</div>
  )
  const popoverListener = () => {
    if (moreVisible) {
      setMoreVisible(false);
    }
  }

  useEffect(() => {
    document.addEventListener("click", popoverListener);
    return () => {
      document.removeEventListener('click', popoverListener);
    }
  }, [moreVisible])

  useEffect( () => {
    if (userInfo.role === RoleTypes.host && memberAllLength > 0) {
      message.info('有新的举手申请')
    }
  }, [memberAllLength, userInfo.role])


  return (
    <div className="member-list">
      <div className="list-wrapper">
        <div className="list-content">
          <Button
            onClick={handleMemberClick}
            type="text"
            icon={<img src={require('@/assets/imgs/member.png').default} alt="member" />}
          />
          <p className="gray">课堂成员</p>
        </div>
      </div>
      {Number(roomInfo.sceneType) === RoomTypes.bigClass && (userInfo.role === RoleTypes.host || roomStore.classStep === 1) &&
        <div className="list-wrapper">
          <div className="list-content">
            <Button
              onClick={handleHandSpeakClick}
              type="text"
              icon={handSpeakVisible ?
                <img src={require('@/assets/imgs/handSpeaking.png').default} alt="handSpeaking" className="hands" /> :
                <img src={require('@/assets/imgs/handSpeak.png').default} alt="handSpeak" className="hands" />}
            />
            {(userInfo.role === RoleTypes.host && memberAllLength > 0) && <span className="circle">{memberAllLength}</span>}
            <p className={handSpeakVisible ? "blue" : "gray"}>{(handleMsg())}</p>
          </div>
        </div>
      }
      <div className="list-wrapper">
        <div className="list-content">
          <Button
            onClick={handleChatModal}
            type="text"
            icon={<img className="chat-icon" src={require('@/assets/imgs/chat.png').default} alt="chat" />}
          />
          {
            !chatVisible && messageCount > 0 && <span className="circle message-count"></span>
          }
          <p className="gray">聊天室</p>
        </div>
      </div>
      <Modal
        title={Number(roomInfo.sceneType) === RoomTypes.bigClass ? memberTabs() : `课堂成员 (${studentData?.length})`}
        wrapClassName="memberModal"
        visible={handleMemberVisible}
        onCancel={handleMemberCancel}
        centered
        footer={
          userInfo.role === RoleTypes.host && <div className="footers">
            {/* <Button type="primary" onClick={handleMuteAll}>全体静音</Button> */}
            <Button
              type="text"
              className={`member-mute-all ${muteAllBtnHover ? 'active-mute' : ''}`}
              icon={muteAllBtnHover ?
                <img src={require('@/assets/imgs/audioClose.png').default} alt="audioClose" /> :
                <img src={require('@/assets/close-audio.png').default} alt="audioOpen" />
              }
              onMouseEnter={() => setMuteAllBtnHover(true)}
              onMouseLeave={() => setMuteAllBtnHover(false)}
              onClick={handleMuteAll}
            >全体静音</Button>
            <Tooltip title={content} overlayClassName="mute-tooltip" placement="topLeft">
              <img src={require('@/assets/imgs/info5.png').default} alt="info" className="infoImg" />
            </Tooltip>
            <Checkbox onChange={onBanChange}>聊天室全体禁言</Checkbox>
          </div>
        }
      >
        {memberContent()}
      </Modal>
      <Modal
        title={`举手申请 (${memberAllLength})`}
        wrapClassName="handsModal"
        visible={handleHandsVisible}
        onCancel={handleHandsCancel}
        centered
        footer={null}
      >
        <ul>
          {
            studentData.map((item) => (
              item?.avHandsUp === HandsUpTypes.studentHandsup && <li key={item.userUuid}>
                <span>{item.userName}</span>
                <div>
                  <Button onClick={() => handsUpAction(item.userUuid, HandsUpTypes.teacherAgree)} >同意</Button>
                  <Button onClick={() => handsUpAction(item.userUuid, HandsUpTypes.teacherReject)} >拒绝</Button>
                </div>
              </li>
            ))
          }
        </ul>
      </Modal>
      <Modal visible={handsVisible} centered
        onOk={handleHandsModalOk}
        onCancel={handleHandsModalCancel}
        okText="确认"
        cancelText="取消"
        wrapClassName="modal"
      >
        <p className="title">
          {userInfo?.avHandsUp === HandsUpTypes.teacherAgree && '下讲台'}
          {userInfo?.avHandsUp === HandsUpTypes.studentHandsup && '取消举手'}
          {(!userInfo?.avHandsUp || [HandsUpTypes.init, HandsUpTypes.studentCancel, HandsUpTypes.teacherOff, HandsUpTypes.teacherReject].includes(userInfo?.avHandsUp)) && '举手申请'}
        </p>
        <p className="desc">
          {userInfo?.avHandsUp === HandsUpTypes.teacherAgree && '下讲台后，你的视频画面将不再显示在屏幕上，不能继续与老师语音交流。'}
          {userInfo?.avHandsUp === HandsUpTypes.studentHandsup && '是否确认取消举手？'}
          {(!userInfo?.avHandsUp || [HandsUpTypes.init, HandsUpTypes.studentCancel, HandsUpTypes.teacherOff, HandsUpTypes.teacherReject].includes(userInfo?.avHandsUp)) && '申请上台与老师沟通，通过后你的视频画面将出现在屏幕上并能与老师语音'}
        </p>
      </Modal>
      <Modal
        title="聊天室"
        wrapClassName="chatModal"
        visible={chatVisible}
        onCancel={handleChatCancel}
        centered
        footer={null}
        zIndex={chatIndex}
      >
        {
          nim?.nim && snapRoomInfo?.properties?.chatRoom?.chatRoomId &&
          <ChatRoom
            nim={nim?.nim}
            appKey={localUserInfo?.imKey}
            account={localUserInfo?.userUuid}
            nickName={`${localUserInfo?.userName}${localUserInfo?.role === RoleTypes.host ? '（老师）' : '（学生）'}`}
            chatroomId={snapRoomInfo?.properties?.chatRoom?.chatRoomId.toString()}
            token={localUserInfo?.imToken}
            canSendMsg={userInfo.role === RoleTypes.host ? true : !(snapRoomInfo?.states?.muteChat?.value === 1)}
            receiveMessage={() => {
              console.log('messageCount', messageCount);
              setMessageCount(messageCount + 1)
            }}
          />
        }
      </Modal>
      <Modal visible={roomStore.rejectModal} centered
        wrapClassName="reject-modal"
        footer={null}
      >
        <p className="title">举手申请被拒绝</p>
        <p className="desc">您的举手申请被拒绝，请稍后再尝试。</p>
      </Modal>
    </div>
  )
})

export default MemberList;
