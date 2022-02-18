/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react';
import { useRoomStore, useUIStore } from '@/hooks/store';
import logger from '@/lib/logger';
import { history, debounce } from '@/utils';
import './index.less';
import DeviceCheckLib from './libComponent';
import intl from 'react-intl-universal';

interface DeviceCheckComOptions {
  onOk?: () => void
}

const DeviceCheckCom: React.FC<DeviceCheckComOptions> = ({
  onOk
}) => {
  const roomStore = useRoomStore();
  const { joinFinish, deviceChangedCount } = roomStore;
  const [reselectDeviceFlag, setReselectDeviceFlag] = useState(false);
  const [cameraId, setCameraId] = useState('');
  const [microphoneId, setMicrophoneId] = useState('');
  const [speakerId, setSpeakerId] = useState('');
  const appkey = process.env.REACT_APP_SDK_APPKEY

  const setDevice = (deviceId, type) => {
    if (!deviceId) return 
    if (type === "camera" && cameraId !== deviceId) {
      setCameraId(deviceId);
    } else if (type === "microphone" && microphoneId !== deviceId) {
      setMicrophoneId(deviceId);
    } else if (type === "speaker" && speakerId !== deviceId) {
      setSpeakerId(deviceId);
    }
    setReselectDeviceFlag(!reselectDeviceFlag)
  }

  const microCaptureVolumeChange = async (volume) => {
    if (typeof volume !== "number") return
    const result = await roomStore.setMicrophoneCaptureVolume(volume)
    logger.log("microCaptureVolumeChange  ",result)
  }

  const audioVolumeChange = async (volume) => {
    if (typeof volume !== "number") return
    // todo 没有result返回
    const result = await roomStore.setAudioVolume(volume)
    logger.log("audioVolumeChange  ",result)
  }

  useEffect(() => {
    debounce(() => {
      roomStore.setReselectDevice(!roomStore.reselectDevice)
    }, 1200)
  }, [reselectDeviceFlag])

  useEffect(() => {
    if (!joinFinish) return
    roomStore
      .getDeviceListData()
      .then(
        ({
          speakerIdSelect = "",
          microphoneSelect = "",
          cameraSelect = "",
          microphones = [],
          cameras = [],
          speakers = [],
        }) => {
          setCameraId(cameraSelect || cameras[0]?.devicedId)
          setMicrophoneId(microphoneSelect || microphones[0]?.devicedId)
          setSpeakerId(speakerIdSelect || speakers[0]?.devicedId)
          // 设备全部拔出时显示内容优化
          if (microphones.length === 0) {
            setMicrophoneId(intl.get("请选择"))
          }
          if (speakers.length === 0) {
            setSpeakerId(intl.get("请选择"))
          }
          if (cameras.length === 0) {
            setCameraId(intl.get("请选择"))
          }         
        }
      )
  }, [joinFinish, deviceChangedCount])

  return (
    <DeviceCheckLib
      appkey={appkey}
      cameraId={cameraId}
      microphoneId={microphoneId}
      speakerId={speakerId}
      okText={intl.get('完成')}
      onCameraChange={(deviceId: string) => setDevice(deviceId, "camera")}
      onMicrophoneChange={(deviceId: string) => setDevice(deviceId, "microphone")}
      onSpeakerChange={(deviceId: string) => setDevice(deviceId, "speaker")}
      onMicroCaptureVolumeChange={(volume: number) => microCaptureVolumeChange(volume)}
      onAudioVolumeChange={(volume: number) => audioVolumeChange(volume)}
      onOk={() => 
        onOk?.()
      }
      needVideoPic={false}
    />
  )
};

export default DeviceCheckCom;