/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react';
import { useRoomStore } from '@/hooks/store';
import { useHistory } from 'react-router-dom';
import './index.less';
import { DeviceCheck, getDefaultDevices } from 'kit-devicecheck-web';




const DeviceCheckPage: React.FC = observer(() => {
  const [cameraId, setCameraId] = useState('');
  const [microphoneId, setMicrophoneId] = useState('');
  const [speakerId, setSpeakerId] = useState('');
  const history = useHistory();


  useEffect(() => {
    getDefaultDevices().then((res: any) => {
      setCameraId(res.cameraId)
      setMicrophoneId(res.microphoneId)
      setSpeakerId(res.speakerId)
    })
  }, [])

  return (
    <div className="deivce-check-page">
      <DeviceCheck
        cameraId={cameraId}
        microphoneId={microphoneId}
        speakerId={speakerId}
        okText="检测完成，回到首页"
        onCameraChange={(deviceId: string) => setCameraId(deviceId)}
        onMicrophoneChange={(deviceId: string) => setMicrophoneId(deviceId)}
        onSpeakerChange={(deviceId: string) => setSpeakerId(deviceId)}
        onOk={() => {
          history.push('/')
        }}
      />
    </div>
  )
});

export default DeviceCheckPage;
