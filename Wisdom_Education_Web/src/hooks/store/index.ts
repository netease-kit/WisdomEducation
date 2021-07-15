/*
 * @Author: lizhaoxuan
 * @Date: 2021-05-14 14:47:05
 * @LastEditTime: 2021-05-19 16:44:50
 * @LastEditors: Please set LastEditors
 * @Description: store hook
 * @FilePath: /app_wisdom_education_web/src/hooks/store/index.ts
 */


import { MobXProviderContext } from 'mobx-react'
import { useContext } from 'react';
import { AppStore } from '@/store';
import { RoomStore } from '@/store/room';
import { WhiteBoardStore } from '@/store/whiteboard';
import { RecordStore } from '@/store/record';
import { UIStore } from '@/store/ui'

export const useAppStore = (): AppStore => {
  const context = useContext(MobXProviderContext);
  return context.store;
}

export const useRoomStore = (): RoomStore => {
  const context = useContext(MobXProviderContext);
  return context.store.roomStore;
}

export const useWhiteBoardStore = (): WhiteBoardStore => {
  const context = useContext(MobXProviderContext);
  return context.store.whiteBoardStore;
}

export const useUIStore = ():UIStore => {
  const context = useContext(MobXProviderContext);
  return context.store.uiStore;
}

export const useRecordStore = ():RecordStore => {
  const context = useContext(MobXProviderContext);
  return context.store.recordStore;
}

