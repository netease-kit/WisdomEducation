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
      dealWhiteBoard()
    }
  }, [roomStore, joinFinish, whiteBoardStore, wbRef, snapRoomInfo?.properties?.whiteboard?.channelName]);

  const dealWhiteBoard = async() => {
    if (wbRef.current) {
      const { localUserInfo: { role }, roomInfo: { sceneType } } = roomStore;
      const enbaleDraw = role === RoleTypes.host || Number(sceneType) === RoomTypes.oneToOne;
      const enableUploadMedia = Number(sceneType) !== RoomTypes.bigClasLive;
      await whiteBoardStore.setContainer(wbRef.current);
      await whiteBoardStore.setToolCollection(wbRef.current, enableUploadMedia)
      roomStore.setLocalWbDrawEnable(enbaleDraw);
      whiteBoardStore.setEnableDraw(enbaleDraw);
      whiteBoardStore.setWbSetFinish(true);
    }
  }

  useEffect(() => {
    return () => {
      whiteBoardStore.destroy();
    }
  }, [whiteBoardStore])

  useEffect(() => {
    logger.log('Set whiteboard-index.tsx', localWbDrawEnable, whiteBoardStore.wbInstance);
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
