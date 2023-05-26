/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { useEffect, useState } from 'react';
import { observer } from 'mobx-react';
import './index.less';
import { Layout, Modal, Spin } from 'antd';
import { useRoomStore, useUIStore, useAppStore } from '@/hooks/store';
import { GlobalStorage } from '@/utils';
import { useHistory, useLocation } from 'react-router-dom';
import Header from '@/component/header';
import Footer from '@/component/footer';
import { isElectron, RoleTypes } from '@/config';
import logger from '@/lib/logger';
import intl from 'react-intl-universal';

// const { Header, Footer } = Layout;
const RoomControl: React.FC = observer(({ children }) => {
  const appStore = useAppStore();
  const roomStore = useRoomStore();
  const history = useHistory();
  const location= useLocation();
  const uiStore = useUIStore();
  const { loading } = uiStore;
  const userInfo = roomStore?.localUserInfo || {};
  const [isHost, setIsHost] = useState<boolean>(false);
  const [modalVisible, setModalVisible] = useState<boolean>(false)

  useEffect(() => {
    if (typeof userInfo.role === 'string') {
      setIsHost(userInfo.role === RoleTypes.host)
    }
  }, [userInfo.role]);

  const listenCloseTab = (e) => {
    // chrome no longer supports custom return content(returnValue) since version 51
    (e || window.event).returnValue = intl.get('确定离开当前页面吗？');
  }

  useEffect(() => {
    const joinParams = GlobalStorage.read('user');
    if (!joinParams?.role) {
      history.push('/');
      return;
    }
    window.history.pushState(null, document.title, window.location.href);

    const handlePopState = (evt: any) => {
      window.history.pushState(null, document.title, null);
      window.addEventListener('popstate', handlePopState, false)
    }
    if (!roomStore.joined) {
      uiStore.setLoading(true);
      roomStore.join(joinParams).then(() => {
        uiStore.showToast(intl.get('加入房间成功'));
      }).catch((err) => {
        console.error('failed to join the room', err);
        const userInfo = GlobalStorage.read('user');
        switch (Number(err?.message)) {
          case 1002:
            if (userInfo && userInfo.role === RoleTypes.host) {
              uiStore.showToast(intl.get('老师数量超过限制'));
            } else {
              uiStore.showToast(intl.get('学生数量超过限制'));
            }
            break;
          case 400:
            uiStore.showToast(intl.get('参数错误'));
            break;
          case 401:
            break;
          case 1017:
            uiStore.showToast(intl.get("创建房间时房间已经存在且房间类型冲突"));
            break;
          case 1004:
            break;  
          default:
            uiStore.showToast(err?.message || intl.get('加入房间失败'));
            break;
        }
        setTimeout(() => {
          roomStore.leave()
          history.push('/');
        }, 1500);
        // uiStore.showToast(`${err?.message || 'Failed to join the room'}`, 'error');
      }).finally(() => {
        uiStore.setLoading(false);
      });
    }
    window.addEventListener('beforeunload', listenCloseTab);
    return () => {
      window.removeEventListener('popstate', handlePopState, false)
      window.removeEventListener('beforeunload', listenCloseTab);
    }
  }, [history, location.pathname, roomStore, uiStore]);

  // Prevents the space bar and enter key from triggering the last click event
  useEffect(()=>{
    function handleKeyDown(event) {
      if ([32, 13].includes(event.keyCode) && event.target.nodeName === 'BUTTON') {
        event.preventDefault();
      }
    }
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [])

  const handleModalOk = async () => {
    setModalVisible(false);
    history.push('/')
  }

  const handleModalCancel = () => {
    setModalVisible(false);
  }

  return (
    <div className="room-control">
      {uiStore.loading ? <div className="room-loading"><Spin className="room-loading-spin" /></div> : null}
      <Layout className="layout-outer">
        {/* It is only used in the scene where the teacher on the electronic side leaves the classroom temporarily */}
        <Header hasBack={isElectron && isHost} onBackClick={()=>{
          isElectron && isHost && setModalVisible(true)
        }}/>
        <Layout>
          {children}
        </Layout>
        <Footer />
      </Layout>
      <Modal visible={modalVisible} centered
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        okText={intl.get('确认')}
        cancelText={intl.get('取消')}
        wrapClassName="modal"
      >
        <p className="title">
          { intl.get('确认离开') }
        </p>
        <p className="desc">
          { intl.get('离开教室后将暂停教学，学生需要等待您再次进入课堂后方可继续上课。') }
        </p>
      </Modal>
    </div>
  )
});
const RoomPage:React.FC = observer(({ children }) => {
  return (
    <RoomControl>
      {children}
    </RoomControl>
  )
})

export default RoomPage
