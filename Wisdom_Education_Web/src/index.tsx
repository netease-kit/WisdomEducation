/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { useMemo, useEffect, useState } from 'react';
import ReactDOM from 'react-dom';
import './index.less';
import { Modal } from 'antd';
import 'antd/dist/antd.less';
// import App from '@/pages/App';
import RoomPage from '@/pages/classRoom';
import Join from '@/pages/join';
import OneToOne from '@/pages/classRoom/one-to-one';
import SmallClass from './pages/classRoom/small-class';
import BigClass from '@/pages/classRoom/big-class';
import BigClassLiveTea from '@/pages/classRoom/big-class-live-tea';
import BigClassLiveStu from '@/pages/classRoom/big-class-live-stu';
import EndCourse from '@/pages/endCourse';
import Record from '@/pages/record';
import DeviceCheck from '@/pages/deviceCheck';
import { Provider } from 'mobx-react';
import { AppStore } from '@/store';
import { history } from '@/utils';
import { initLocales } from '@/utils/universal';
import eleIpc from '@/lib/ele-ipc';
import { isElectron } from '@/config';
import intl from 'react-intl-universal';

import {
  HashRouter as Router,
  Switch,
  Route,
} from "react-router-dom";
import logger from './lib/logger';

export const defaultStore = new AppStore()
initLocales()
const { confirm } = Modal

const ReactApp = () => {
  const eleIpcIns = useMemo(() => (isElectron ? eleIpc.getInstance() : null), []);
  const [modalVisible, setModalVisible] = useState<boolean>(false)

  useEffect(() => {
    if (eleIpcIns) {
      eleIpcIns.on('main-close-before', () => {
        logger.debug('main-close-before')
        if (location.hash.includes('/classroom')) {
          setModalVisible(true)
        } else {
          eleIpcIns?.sendMessage('allow-to-close')
        }
      });
    }
    return () => {
      eleIpcIns?.removeAllListeners();
    }
  }, [eleIpcIns])

  return (
    <Provider store={defaultStore}>
      <Router>
        <Switch>
          <Route exact path="/" component={Join} />
          <Route exact path="/classroom/one-to-one" component={()=>(
            <RoomPage>
              <OneToOne />
            </RoomPage>
          )} />
          <Route exact path="/classroom/small-class" component={()=>(
            <RoomPage>
              <SmallClass />
            </RoomPage>)} />
          <Route exact path="/classroom/big-class" component={()=>(
            <RoomPage>
              <BigClass />
            </RoomPage>)}/>
          <Route exact path="/classroom/big-class-live-tea" component={()=>(
            <RoomPage>
              <BigClassLiveTea />
            </RoomPage>
          )}/>
          <Route exact path="/classroom/big-class-live-stu" component={()=>(
            <RoomPage>
              <BigClassLiveStu />
            </RoomPage>
          )}/>
          <Route exact path="/record" component={Record} />
          <Route exact path="/endCourse" component={EndCourse} />
        </Switch>
        <Modal 
          visible={modalVisible} centered
          onOk={()=>{
            logger.debug('点击了确认 回到首页')
            history.push('/')
            setTimeout(()=>{
              eleIpcIns?.sendMessage('allow-to-close')
            }, 100)
          }}
          onCancel={()=>{
            setModalVisible(false)
          }}
          okText={intl.get('确认')}
          cancelText={intl.get('取消')}
          wrapClassName="modal"
        >
          <p>
            {intl.get('确认离开？系统可能不会保存您的更改。')}
          </p>
        </Modal>
      </Router>
    </Provider>
  )
}

ReactDOM.render(
  <ReactApp/>
  ,document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
