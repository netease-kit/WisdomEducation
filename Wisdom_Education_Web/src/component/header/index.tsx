/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React from 'react';
import { observer } from 'mobx-react';
import { useRoomStore, useUIStore } from '@/hooks/store';
import logger from '@/lib/logger';
import { Tooltip, message } from 'antd';
import copy from 'copy-to-clipboard';
import { NetStatusItem } from '@/store/room';
import './index.less';
import copyImg from '@/assets/imgs/copy.png';
import { LeftOutlined } from '@ant-design/icons';
import { history } from '@/utils';

interface HeaderShowProps {
  isHave?: boolean;
  title?: string;
  hasBack?: boolean;
  backUrl?: string;
  backMsg?: string;
}

const netInfoImgMap = {
  0: require('@/assets/imgs/info1.png').default,
  1: require('@/assets/imgs/info4.png').default,
  2: require('@/assets/imgs/info3.png').default,
  3: require('@/assets/imgs/info3.png').default,
  4: require('@/assets/imgs/info2.png').default,
  5: require('@/assets/imgs/info1.png').default,
  6: require('@/assets/imgs/info1.png').default,
};

const netXinhaoImgMap = {
  0: require('@/assets/imgs/xinhao1.png').default,
  1: require('@/assets/imgs/xinhao4.png').default,
  2: require('@/assets/imgs/xinhao3.png').default,
  3: require('@/assets/imgs/xinhao3.png').default,
  4: require('@/assets/imgs/xinhao2.png').default,
  5: require('@/assets/imgs/xinhao1.png').default,
  6: require('@/assets/imgs/xinhao1.png').default,
};

const Header: React.FC<HeaderShowProps> = observer((props) => {
  const roomStore = useRoomStore();
  const uiStore = useUIStore();
  const userInfo = roomStore?.localUserInfo || {};
  const { snapRoomInfo: { roomUuid = '', roomName = '' } } = roomStore;
  const networkQuality = roomStore?.networkQuality || [];
  const memberFullList = roomStore?.memberFullList || [];

  const hostName = memberFullList.filter((item) => {
    return item.role === "host"
  }).map((item) => {
    return item.userName
  }).join('')

  const handleCopyClick = () => {
    copy(roomUuid);
    uiStore.showToast("复制成功")
  }

  const content =
    <div className="head-infoCard">
      <div className="info-num">
        <span className="title">课堂号</span>
        {roomUuid?.length > 0 && <span className="desc">{roomUuid}</span>}
        <img src={copyImg} alt="" className="copyImg" onClick={handleCopyClick}/>
      </div>
      <div className="info-name">
        <span className="title">课堂名称</span>
        <span className="desc">{roomName}</span>
      </div>
      <div className="info-role">
        <span className="title">老师</span>
        <span className="desc">{hostName}</span>
      </div>
    </div>

  const downlinkNetworkQuality = (networkQuality.find(item => item.uid === memberFullList[0]?.rtcUid) as NetStatusItem)?.downlinkNetworkQuality || 3;
  const uplinkNetworkQuality = (networkQuality.find(item => item.uid === memberFullList[0]?.rtcUid) as NetStatusItem)?.uplinkNetworkQuality || 3;
  const maxNetworkQuality = Math.max(uplinkNetworkQuality, downlinkNetworkQuality)

  return (
    <div className="head-component">
      {props.hasBack && <div className="head-back" onClick={() => history.push(`${props.backUrl}`)}>
        <LeftOutlined className="back-icon"/>
        {props?.backMsg}
      </div>}
      <div className="head-courseState">{props.title || roomStore.roomState}&nbsp;
        <span>{roomStore.prevToNowTime}</span>
      </div>
      {
        props.isHave ? null :
          <div className="head-infoBar">
            <span className="info-title">{roomName}</span>
            <Tooltip placement="bottomRight" title={content} overlayClassName="head-info-tooltip">
              <div className="infoDiv">
                <img src={netInfoImgMap[maxNetworkQuality.toString()] || require('@/assets/imgs/info1.png').default} alt="info" className="infoImg"/>
              </div>
            </Tooltip>
            <div>
              <img src={netXinhaoImgMap[maxNetworkQuality.toString()] || require('@/assets/imgs/xinhao1.png').default} alt="sign" className="signImg"/>
            </div>
          </div>
      }

    </div>
  )
})

export default Header;
