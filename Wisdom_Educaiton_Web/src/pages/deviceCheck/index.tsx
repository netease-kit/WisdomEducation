/*
 * @Author: lizhaoxuan
 * @Date: 2021-06-29 14:18:42
 * @LastEditTime: 2021-06-29 15:00:32
 * @LastEditors: Please set LastEditors
 * @Description: 会前设备检查
 * @FilePath: /app_wisdom_education_web/src/pages/deviceCheck/index.tsx
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
        theme={{
          iconColor: "#5174F6"
        }}
      />
    </div>
  )
});

export default DeviceCheckPage;
