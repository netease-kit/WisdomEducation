/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
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

