/*
 * @Author: lizhaoxuan
 * @Date: 2021-05-17 10:48:15
 * @LastEditTime: 2021-06-11 18:21:35
 * @LastEditors: Please set LastEditors
 * @Description: 白板相关
 * @FilePath: /app_wisdom_education_web/src/component/white-board/index.tsx
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
  const { localWbDrawEnable, joined, snapRoomInfo } = roomStore;

  useEffect(() => {
    if (joined && wbRef.current) {
      const { localUserInfo: { imKey, userUuid, imToken, userName, role }, entryData, roomInfo: { sceneType } } = roomStore;
      const { room: { properties: { chatRoom: { chatRoomId } } } } = entryData;
      const enbaleDraw = role === RoleTypes.host || Number(sceneType) === RoomTypes.oneToOne;
      roomStore.setLocalWbDrawEnable(enbaleDraw);
      if (snapRoomInfo?.properties?.whiteboard?.channelName) {
        if (!whiteBoardStore.wbInstance) {
          whiteBoardStore.initWhiteBoard({
            appKey: imKey,
            account: userUuid,
            token: imToken,
            container: wbRef.current,
            nickname: userName,
          }).then(async () => {
            await whiteBoardStore.joinRoom({
              channel: (snapRoomInfo?.properties?.whiteboard?.channelName as number),
            })
            whiteBoardStore.setEnableDraw(enbaleDraw);
          }).catch((e) => {
            logger.log('白板加入异常', e)
          });
        } else {
          whiteBoardStore.joinRoom({
            channel: chatRoomId,
          }).then(() => {
            whiteBoardStore.setEnableDraw(enbaleDraw);
          }).catch((e) => {
            logger.log('白板加入异常', e)
          });
        }
      }
    }
  }, [roomStore, joined, whiteBoardStore, wbRef, snapRoomInfo?.properties?.whiteboard?.channelName]);


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
