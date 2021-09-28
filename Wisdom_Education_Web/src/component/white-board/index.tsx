/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import React, { useEffect, useRef } from 'react';
import { useWhiteBoardStore, useRoomStore } from '@/hooks/store';
import { observer } from 'mobx-react';
import './index.less'
import logger from '@/lib/logger';
import { RoleTypes, RoomTypes } from '@/config';

const WhiteBoard:React.FC = observer(() => {
  const whiteBoardStore = useWhiteBoardStore();
  const roomStore = useRoomStore();
  const wbRef = useRef<HTMLDivElement>(null);
  const { localWbDrawEnable, joinFinish, snapRoomInfo } = roomStore;

  useEffect(() => {
    if (joinFinish && wbRef.current) {
      const { localUserInfo: { role }, roomInfo: { sceneType } } = roomStore;
      const enbaleDraw = role === RoleTypes.host || Number(sceneType) === RoomTypes.oneToOne;
      whiteBoardStore.setContainer(wbRef.current);
      whiteBoardStore.setToolCollection(wbRef.current)
      roomStore.setLocalWbDrawEnable(enbaleDraw);
      whiteBoardStore.setEnableDraw(enbaleDraw);
      whiteBoardStore.setWbSetFinish(true);
      // if (snapRoomInfo?.properties?.whiteboard?.channelName) {
      //   if (!whiteBoardStore.wbInstance) {
      //     whiteBoardStore.initWhiteBoard({
      //       appKey: imKey,
      //       account: userUuid,
      //       token: imToken,
      //       container: wbRef.current,
      //       nickname: userName,
      //     }).then(async () => {
      //       await whiteBoardStore.joinRoom({
      //         channel: (snapRoomInfo?.properties?.whiteboard?.channelName as number),
      //       })
      //       whiteBoardStore.setEnableDraw(enbaleDraw);
      //     }).catch((e) => {
      //       logger.log('白板加入异常', e)
      //     });
      //   } else {
      //     whiteBoardStore.joinRoom({
      //       channel: chatRoomId,
      //     }).then(() => {
      //       whiteBoardStore.setEnableDraw(enbaleDraw);
      //     }).catch((e) => {
      //       logger.log('白板加入异常', e)
      //     });
      //   }
      // }
    }
  }, [roomStore, joinFinish, whiteBoardStore, wbRef, snapRoomInfo?.properties?.whiteboard?.channelName]);


  useEffect(() => {
    return () => {
      whiteBoardStore.destroy();
    }
  }, [whiteBoardStore])

  useEffect(() => {
    logger.log('设置白板-index.tsx', localWbDrawEnable, whiteBoardStore.wbInstance);
    if (whiteBoardStore.wbInstance) {
      whiteBoardStore.setEnableDraw(localWbDrawEnable);
    }
  }, [whiteBoardStore.wbInstance, localWbDrawEnable]);

  return (
    <div className="white-board-component">
      <div className="white-board-container" ref={wbRef} />
    </div>
  )
})

export default WhiteBoard;
