/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { useState, useCallback, useEffect, useMemo, useRef, Fragment } from 'react';
import { observer } from 'mobx-react';
import { Button, Tooltip, Modal, Checkbox, Tabs, Input, message } from 'antd';
import VirtualList from 'rc-virtual-list'
import './index.less';
import logger from '@/lib/logger';
import { HandsUpTypes, UserSeatOperation, RoleTypes, RoomTypes, UserComponentData, HostSeatOperation } from '@/config';
import { useRoomStore, useUIStore } from '@/hooks/store';
import { Message } from '../chatroom/chatroomHelper';
import ChatRoom from '@/component/chatroom';
import intl from 'react-intl-universal';
import { debounce } from '@/utils';

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
  const [lastMemNum, setLastMemNum] = useState(0)
  // const count = useRef(0);

  const roomStore = useRoomStore();
  const uiStore = useUIStore();
  const { roomInfo, studentData, nim, localUserInfo, snapRoomInfo, bigLivememberFullList, isBigLiveClass, isClassMuteChat } = roomStore
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
      await uiStore.showToast(intl.get("操作成功"));
    } else {
      await roomStore.openAudio(item.userUuid, true, isBySelf(item.userUuid));
      await uiStore.showToast(intl.get("操作成功"));
    }
  }

  const handleVideoClick = async (item) => {
    if (item.hasVideo) {
      await roomStore.closeCamera(item.userUuid, true, isBySelf(item.userUuid));
      await uiStore.showToast(intl.get("操作成功"));
    } else {
      await roomStore.openCamera(item.userUuid, true, isBySelf(item.userUuid));
      await uiStore.showToast(intl.get("操作成功"));
    }
  }

  const handleChatModal = () => {
    setChatVisible(true);
  }

  const handleChatCancel = () => {
    setChatVisible(false);
    setMessageCount(0)
  }

  // Every time the chat window is opened, the message is positioned to the end
  useEffect(()=>{
    if(chatVisible) {
      const contentDom = document.getElementById('chatroomContent')
      if(!contentDom) return
      setTimeout(()=>{
        contentDom.scrollTo({
          top: contentDom?.scrollHeight,
          behavior: 'smooth'
        })
      }, 0)
    }
  }, [chatVisible])

  const handleHandsModalOk = async () => {
    setHandsVisible(false);
    switch (userInfo?.avHandsUp) {
      case HandsUpTypes.teacherAgree:
        if (userInfo.hasScreen) {
          await roomStore.stopScreen();
        }
        if (isBigLiveClass) {
          roomStore.deleteMember(userInfo?.userUuid)
        } else {
          await roomStore.handsUpAction(userInfo?.userUuid, HandsUpTypes.init);
        }
        break;
      case HandsUpTypes.studentHandsup:
        if (isBigLiveClass) {
          await roomStore.handsUpActionForSeat(userInfo?.userUuid, UserSeatOperation.studentCancel);
        } else {
          await roomStore.handsUpAction(userInfo?.userUuid, HandsUpTypes.studentCancel);
        }
        break;
      default:
        if (isBigLiveClass) {
          await roomStore.handsUpActionForSeat(userInfo?.userUuid, UserSeatOperation.studentHandsup);
        } else {
          await roomStore.handsUpAction(userInfo?.userUuid, HandsUpTypes.studentHandsup);
        }
        break;
    }
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

  const handsUpActionByTea = async (userUuid: string, value: HandsUpTypes) => {
    try {
      !isBigLiveClass && await roomStore.handsUpAction(userUuid, value)
      switch (value) {
        case HandsUpTypes.teacherAgree:
          isBigLiveClass && await roomStore.handsUpActionForSeat(userUuid, HostSeatOperation.teacherAgree, true)
          break;
        case HandsUpTypes.teacherOff:
          isBigLiveClass && await roomStore.handsUpActionForSeat(userUuid, HostSeatOperation.teacherOff, true)
          await roomStore.changeSubVideoStream(userUuid, 0);
          break;
        case HandsUpTypes.teacherReject:
          isBigLiveClass && await roomStore.handsUpActionForSeat(userUuid, HostSeatOperation.teacherReject, true)
          break;
        default:
          break;
      }
      await uiStore.showToast(intl.get("操作成功"));
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
      logger.log("All muted")
    }
  }

  const onBanChange = (e) => {
    const value = Number(e.target.checked);
    roomStore.muteChatroom(value);
    logger.log("All muted", value);
  }

  const handleSetWbEnableDraw = async (userUuid: string, value: number) => {
    if (roomStore.classStep !== 1) {
      await uiStore.showToast(intl.get("请先开始上课"));
      return;
    }
    await roomStore.setWbEnableDraw(userUuid, value);
    await uiStore.showToast(intl.get("操作成功"));
  }

  const handleSetAllowScreen = async (userUuid: string, value: number) => {
    if (roomStore.classStep !== 1) {
      await uiStore.showToast(intl.get("请先开始上课"));
      return;
    }
    if (value === 0) await roomStore.changeSubVideoStream(userUuid, value);
    await roomStore.setAllowScreenShare(userUuid, value);
    await uiStore.showToast(intl.get("操作成功"));
  }

  const MoreContent = useCallback((item) => {
    return (
      <ul onClick={() => setMoreVisible(false)}>
        {item.wbDrawEnable ?
          <li><Button onClick={() => handleSetWbEnableDraw(item.userUuid, 0)} type="text">{intl.get('取消白板权限')}</Button></li> :
          <li><Button onClick={() => handleSetWbEnableDraw(item.userUuid, 1)} type="text">{intl.get('授予白板权限')}</Button></li>}
        {item.canScreenShare ?
          <li><Button onClick={() => handleSetAllowScreen(item.userUuid, 0)} type="text">{intl.get('取消共享权限')}</Button></li> :
          <li><Button onClick={() => handleSetAllowScreen(item.userUuid, 1)} type="text">{intl.get('授予共享权限')}</Button></li>}
        {
          RoomTypes.bigClass === Number(roomStore?.roomInfo?.sceneType) && item.avHandsUp === HandsUpTypes.teacherAgree &&
          <li><Button onClick={() => handsUpActionByTea(item.userUuid, HandsUpTypes.teacherOff)} type="text">{intl.get('请他下台')}</Button></li>
        }
      </ul>
    );
  }, [roomStore]);

  const handleChangeSearch = (e) => {
    setSearchValue(e.target.value)
    handleSearchMember(e.target.value)
  }

  const handleSearchMember = useCallback(
    (_value=searchValue) => {
      const arr = Number(localUserInfo?.sceneType) === RoomTypes.bigClasLive ? bigLivememberFullList : studentData;
      if (!_value) {
        setAllMember(arr)
      } else {
        const newArray = arr.filter((item) => item?.userName.includes(_value));
        setAllMember(newArray)
      }
      logger.log('Search info', _value, arr, Number(localUserInfo?.sceneType))
    },
    [searchValue, studentData, localUserInfo, bigLivememberFullList],
  )

  const handleMsg = useCallback(() => {
    let result = intl.get('举手');
    roomStore.setFinishBtnShow(false);
    if (userInfo?.role === RoleTypes.host) {
      result = intl.get('举手申请');
      roomStore.setFinishBtnShow(false);
    } else {
      switch (userInfo?.avHandsUp) {
        case HandsUpTypes.studentHandsup:
          result = intl.get('举手中');
          roomStore.setFinishBtnShow(false);
          break;
        case HandsUpTypes.teacherAgree:
          result = intl.get('下讲台');
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
  }, [studentData, bigLivememberFullList]);

  useEffect(() => {
    // 
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

  useEffect(() => {
    if (RoomTypes.bigClasLive === Number(roomInfo.sceneType)) {
      setInputVisible(true)
    }
  }, [roomInfo])

  const memberContent = () => {
    return (
      <>
        {inputVisible ?
          <div className="search">
            <Input placeholder={intl.get('请输入关键词搜索')} onChange={handleChangeSearch} allowClear />
            <Button onClick={()=>{handleSearchMember()}} >{intl.get('搜索')}</Button>
          </div> : null
        }
        <ul>
          {
            ![RoomTypes.bigClass, RoomTypes.bigClasLive].includes(Number(roomInfo.sceneType)) &&
            studentData.map((item) => (
              <li key={item.rtcUid}>
                <span>{item.userName}</span>
                <div className="buttons">
                  {item.wbDrawEnable && <img src={require('@/assets/imgs/whiteBoard.png').default} alt="whiteBoard" />}
                  {/*Whiteboard permissions state*/}
                  {item.canScreenShare && <img src={require('@/assets/imgs/screenShare.png').default} alt="screenShare" />}
                  {/*Screen sharing permissions state*/}
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
              <Fragment key={item.rtcUid}>
                <li>
                  <span>{item.userName}</span>
                  <div className="buttons">
                    {item.wbDrawEnable && <img src={require('@/assets/imgs/whiteBoard.png').default} alt="whiteBoard" />}
                    {/*Whiteboard permissions state*/}
                    {item.canScreenShare && <img src={require('@/assets/imgs/screenShare.png').default} alt="screenShare" />}
                    {/*Screen sharing permission state*/}
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
              </Fragment>
            ))
          }
          {
            ([RoomTypes.bigClass, RoomTypes.bigClasLive].includes(Number(roomInfo.sceneType)) && inputVisible) &&
            <VirtualList
              data={allMember}
              height={288}
              itemHeight={54}
              itemKey="userUuid" 
              component="ul" 
            >
              {item=> <li className='all-member'><span>{item.userName}</span></li>}
            </VirtualList>
          }
        </ul>
      </>
    )
  }

  const memberTabs = () => (
    <Tabs defaultActiveKey="online" onChange={handleTabsChange}>
      {[RoomTypes.bigClass].includes(Number(roomInfo.sceneType)) && <Tabs.TabPane tab={`${intl.get("连线成员")} (${onlineMemberLength})`} key="online">
        {/* {memberTabs} */}
      </Tabs.TabPane>}
      <Tabs.TabPane tab={`${intl.get("全部成员")} (${studentData?.length})`} key="all">
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
    <div className="mute-desc">{intl.get('全体静音为默认关闭所有成员的麦克风，但成员依旧能主动开启')}</div>
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
    if (userInfo.role === RoleTypes.host) {
      if (memberAllLength > 0 && memberAllLength > lastMemNum) {
        message.info(intl.get('有新的举手申请'))
      }
      setLastMemNum(memberAllLength)
    }
  }, [memberAllLength, userInfo.role])

  const muteChatContent = useMemo(()=>{
    return <Checkbox onChange={onBanChange} checked={isClassMuteChat}>{intl.get('聊天室全体禁言')}</Checkbox>
  }, [isClassMuteChat])

  return (
    <div className="member-list">
      <div className="list-wrapper">
        <div className="list-content">
          <Button
            onClick={handleMemberClick}
            type="text"
            icon={<img src={require('@/assets/imgs/member.png').default} alt="member" />}
          />
          <p className="gray">{intl.get('课堂成员')}</p>
        </div>
      </div>
      {([RoomTypes.bigClass, RoomTypes.bigClasLive].includes(Number(roomInfo.sceneType))) && (userInfo.role === RoleTypes.host || roomStore.classStep === 1) &&
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
      {snapRoomInfo?.properties?.chatRoom?.chatRoomId && <div className="list-wrapper">
        <div className="list-content">
          <Button
            onClick={handleChatModal}
            type="text"
            icon={<img className="chat-icon" src={require('@/assets/imgs/chat.png').default} alt="chat" />}
          />
          {
            !chatVisible && messageCount > 0 && <span className="circle message-count"></span>
          }
          <p className="gray">{intl.get('聊天室')}</p>
        </div>
      </div>}
      <Modal
        title={Number(roomInfo.sceneType) === RoomTypes.bigClass ? memberTabs() : `${intl.get("课堂成员")} (${Number(roomInfo.sceneType) === RoomTypes.bigClasLive ? bigLivememberFullList?.length : studentData?.length})`}
        wrapClassName="memberModal"
        visible={handleMemberVisible}
        onCancel={handleMemberCancel}
        keyboard={false}
        centered
        footer={
          userInfo.role === RoleTypes.host && <div className="footers">
            {/* <Button type="primary" onClick={handleMuteAll}>Mute all</Button> */}
            {userInfo.role === RoleTypes.host && RoomTypes.bigClasLive !== Number(roomStore?.roomInfo?.sceneType) && <><Button
              type="text"
              className={`member-mute-all ${muteAllBtnHover ? 'active-mute' : ''}`}
              icon={muteAllBtnHover ?
                <img src={require('@/assets/imgs/audioClose.png').default} alt="audioClose" /> :
                <img src={require('@/assets/close-audio.png').default} alt="audioOpen" />
              }
              onMouseEnter={() => setMuteAllBtnHover(true)}
              onMouseLeave={() => setMuteAllBtnHover(false)}
              onClick={handleMuteAll}
            >{intl.get('全体静音')}</Button>
            <Tooltip title={content} overlayClassName="mute-tooltip" placement="topLeft">
              <img src={require('@/assets/imgs/info5.png').default} alt="info" className="infoImg" />
            </Tooltip></>}
            {muteChatContent}
          </div>
        }
      >
        {memberContent()}
      </Modal>
      <Modal
        title={`${intl.get('举手申请')} (${memberAllLength})`}
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
                  <Button onClick={() => {
                    debounce(()=>{
                      handsUpActionByTea(item.userUuid, HandsUpTypes.teacherAgree)
                    }, 1000)
                  }} >{intl.get('同意')}</Button>
                  <Button onClick={() => {
                    debounce(()=>{
                      handsUpActionByTea(item.userUuid, HandsUpTypes.teacherReject)
                    }, 1000)
                  }} >{intl.get('拒绝')}</Button>
                </div>
              </li>
            ))
          }
        </ul>
      </Modal>
      <Modal visible={handsVisible} centered
        onOk={handleHandsModalOk}
        onCancel={handleHandsModalCancel}
        okText={intl.get('确认')}
        cancelText={intl.get('取消')}
        wrapClassName="modal"
      >
        <p className="title">
          {userInfo?.avHandsUp === HandsUpTypes.teacherAgree && intl.get('下讲台')}
          {userInfo?.avHandsUp === HandsUpTypes.studentHandsup && intl.get('取消举手')}
          {(!userInfo?.avHandsUp || [HandsUpTypes.init, HandsUpTypes.studentCancel, HandsUpTypes.teacherOff, HandsUpTypes.teacherReject].includes(userInfo?.avHandsUp)) && intl.get('举手申请')}
        </p>
        <p className="desc">
          {userInfo?.avHandsUp === HandsUpTypes.teacherAgree && intl.get('下讲台后，你的视频画面将不再显示在屏幕上，不能继续与老师语音交流。')}
          {userInfo?.avHandsUp === HandsUpTypes.studentHandsup && intl.get('是否确认取消举手？')}
          {(!userInfo?.avHandsUp || [HandsUpTypes.init, HandsUpTypes.studentCancel, HandsUpTypes.teacherOff, HandsUpTypes.teacherReject].includes(userInfo?.avHandsUp)) && intl.get('申请上台与老师沟通，通过后你的视频画面将出现在屏幕上并能与老师语音')}
        </p>
      </Modal>
      <Modal
        title={intl.get('聊天室')}
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
            nickName={`${localUserInfo?.userName}${localUserInfo?.role === RoleTypes.host ? `（${intl.get("老师")}）` : `（${intl.get("学生")}）`}`}
            chatroomId={snapRoomInfo?.properties?.chatRoom?.chatRoomId.toString()}
            token={localUserInfo?.imToken}
            canSendMsg={userInfo.role === RoleTypes.host ? true : !(snapRoomInfo?.states?.muteChat?.value === 1)}
            receiveMessage={(msgs) => {
              console.log('messageCount', messageCount, msgs);
              for (const item of msgs) {
                if (['text', 'image', 'file'].includes(item.type)) {
                  setMessageCount(messageCount + 1)
                }
              }
            }}
          />
        }
      </Modal>
      <Modal visible={roomStore.rejectModal} centered
        wrapClassName="reject-modal"
        footer={null}
      >
        <p className="title">{intl.get('举手申请被拒绝')}</p>
        <p className="desc">{intl.get('您的举手申请被拒绝，请稍后再尝试。')}</p>
      </Modal>
    </div>
  )
})

export default MemberList;
