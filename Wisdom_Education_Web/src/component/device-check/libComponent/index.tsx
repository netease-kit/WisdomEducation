import React, { useState, useEffect, useRef } from 'react'
import { Select, Button, Slider, Progress } from 'antd'
import { useRoomStore } from '@/hooks/store';
import logger from '@/lib/logger';

const styles = require('./index.module.less');

let timer: any = null

export type DeviceType = 'camera' | 'microphone' | 'speaker'
export interface Device {
  deviceId: string
  label: string
}

export interface IProps {
  appkey?: string
  debug?: boolean
  header?: React.ReactChild
  cameraId: string
  microphoneId: string
  speakerId: string
  okText?: string
  onOk?: () => void
  auidoUrl?: string
  onCameraChange?: (deviceId: string) => void
  onMicrophoneChange?: (deviceId: string) => void
  onSpeakerChange?: (deviceId: string) => void
  onMicroCaptureVolumeChange?: (volume: number) => void
  onAudioVolumeChange?: (volume: number) => void
  theme?: {
    footerStyle?: React.CSSProperties
  },
  needVideoPic: boolean
}

const DeviceCheck: React.FC<IProps> = ({
  header = null,
  cameraId,
  microphoneId,
  speakerId,
  okText = '确定',
  onOk,
  auidoUrl = 'https://app.yunxin.163.com/webdemo/audio/rain.mp3',
  onCameraChange,
  onMicrophoneChange,
  onSpeakerChange,
  onMicroCaptureVolumeChange,
  onAudioVolumeChange,
  theme,
  needVideoPic=true,
}) => {
  const roomStore = useRoomStore();
  const {
    joinFinish, 
    deviceChangedCount,
  } = roomStore;
  const [originCameras, setOriginCameras] = useState<Device[]>([])
  const [originMicrophones, setOriginMicrophones] = useState<Device[]>([])
  const [originSpeakers, setOriginSpeakers] = useState<Device[]>([])
  const [testMic, setTestMic] = useState(false)
  const [testSpeaker, setTestSpeaker] = useState(false)
  const [audioLevel, setAudioLevel] = useState(0)
  const [speakerLevel, setSpeakerLevel] = useState(0)
  const [speakerTestLoading, setSpeakerTestLoading] = useState(false)
  const [micTestLoading, setMicTestLoading] = useState(false)
  const [audioTimer, setAudioTimer] = useState(0)

  const videoRef = useRef<HTMLDivElement>(null)
  const audioRef = useRef<HTMLAudioElement>(null)

  useEffect(() => {
    if (joinFinish) {
      roomStore
        .getDeviceListData()
        .then(
          ({
            microphones = [],
            cameras = [],
            speakers = []
          }) => {
            setOriginCameras(cameras)
            setOriginMicrophones(microphones)
            setOriginSpeakers(speakers)      
          }
        )
    }
    // if (needVideoPic && videoRef.current) {
    //   rtc.setupLocalView(videoRef.current)
    // }

    return () => {
      cancelAnimationFrame(audioTimer)
      setAudioTimer(0)
      // rtc.destroy()
    }
  }, [joinFinish])

  useEffect(() => {
    if(!deviceChangedCount) return
    let _cameras: Device[] = []
    let _microphones: Device[] = []
    let _speakers: Device[] = []
    roomStore
      .getDeviceListData()
      .then(
        ({
          microphones = [],
          cameras = [],
          speakers = [],
        }) => {
          _cameras = cameras
          _microphones = microphones
          _speakers = speakers
          if (!shallowEqual(_cameras, originCameras)) {
            setOriginCameras(_cameras)
          } 
          if (!shallowEqual(_microphones, originMicrophones)) {
            console.log("testMic停止")
            testMic && handleMicTest()
            setOriginMicrophones(_microphones)
          } 
          if (!shallowEqual(_speakers, originSpeakers)) {
            console.log("testSpeaker停止")
            testSpeaker && handleSpeakerTest()
            setOriginSpeakers(_speakers)
          }      
        }
      )
  }, [deviceChangedCount])

  const shallowEqual = (arr1: Device[], arr2: Device[]): boolean => {
    if (arr1.length !== arr2.length) {
      return false
    }
    return (
      arr1.every((i) => arr2.some((j) => j.deviceId === i.deviceId)) &&
      arr2.every((i) => arr1.some((j) => j.deviceId === i.deviceId))
    )
  }

  const switchDevice = async ({
    type,
    deviceId,
  }: {
    type: DeviceType
    deviceId: string
  }): Promise<{
    type: DeviceType
    deviceId: string
  }> => {
    logger.log(
      'switchDevice',
      JSON.stringify({
        type,
        deviceId,
      })
    )
    try {
      // this.localStream.mediaHelper.isLocal = true
      switch (type) {
        case 'camera':
          roomStore.selectVideo(deviceId)
          // if (this.view) {
          //   if (!this.isPlaying) {
          //     await this.localStream.open({ type: 'video', deviceId })
          //     await this.playVideo()
          //   } else if (deviceId) {
          //     await this.localStream.switchDevice('video', deviceId)
          //   } else if (this.isPlaying) {
          //     await this.localStream.close({ type: 'video' })
          //     this.isPlaying = false
          //   }
          // }
          break
        case 'microphone':
          roomStore.selectAudio(deviceId)
          break
        case 'speaker':
          roomStore.selectSpeakers(deviceId)
          break
        default:
          break
      }
      return { type, deviceId }
    } catch (error) {
      logger.error('switchDevice failed: ', error)
      throw error
    }
  }

  const handleChangeDevice = (type: DeviceType, deviceId: string) => {
    if(!deviceId) return
    try {
      switchDevice({
        type,
        deviceId,
      })
        .then((res) => {
          if (res.type === 'camera') {
            onCameraChange?.(res.deviceId)
          } else if (res.type === 'microphone') {
            onMicrophoneChange?.(res.deviceId)
          } else if (res.type === 'speaker') {
            // @ts-ignore
            audioRef.current?.setSinkId(res.deviceId)
            onSpeakerChange?.(res.deviceId)
          }
        })
    } catch (error) {
      console.log("switchDevice failed ",error)
    }
  }

  const handleMicTest = () => {
    const newValue = !testMic
    setMicTestLoading(true)
    try {
      if (newValue) {
        roomStore.enableVolumeIndicationInElectron(true,100)
        const handler = () => {
          const timer = requestAnimationFrame(() => {
            roomStore.getAudioLevel().then((level) => {
              setAudioLevel(Number(level))
            })
            handler()
          })
          setAudioTimer(timer)
        }
        handler()
        setTestMic(newValue)
      } else {
        roomStore.enableVolumeIndicationInElectron(false,100)
        cancelAnimationFrame(audioTimer)
        setAudioTimer(0)
        setTestMic(newValue)
        setAudioLevel(0)
      }
    } catch(error) {
      console.log("handleMicTest error",error)
    } finally {
      setMicTestLoading(false)
    }
  }

  const handleSpeakerTest = () => {
    const newValue = !testSpeaker
    if (newValue) {
      const handler = () => {
        timer = requestAnimationFrame(() => {
          const random = Math.floor(Math.random() * (95 - 50 + 1) + 50)
          setSpeakerLevel(random)
          handler()
        })
      }
      setSpeakerTestLoading(true)
      audioRef.current?.play().finally(() => {
        setSpeakerTestLoading(false)
        setTestSpeaker(newValue)
        handler()
      })
    } else {
      audioRef.current?.pause()
      cancelAnimationFrame(timer)
      timer = null
      setSpeakerLevel(0)
      setTestSpeaker(newValue)
    }
  }

  const handleConfirm = () => {
    onOk?.()
  }

  return (
    <div className={styles.deviceCheckWrapper}>
      {header}
      <div className={styles.deviceCheckRow}>
        <span className={styles.deviceCheckLabelText}>摄像头：</span>
        <Select
          className={styles.deviceCheckSelect}
          placeholder='请选择'
          disabled={testMic || testSpeaker}
          value={cameraId}
          onChange={(value) => {
            handleChangeDevice('camera', value)
          }}
          options={originCameras.map((item) => ({
            value: item.deviceId,
            label: item.label,
          }))}
        />
        { needVideoPic && <div className={styles.deviceCheckVideoContainer} ref={videoRef}></div> }
      </div>
      <div className={styles.deviceCheckRow}>
        <span className={styles.deviceCheckLabelText}>麦克风：</span>
        <div>
          <div className={styles.deviceCheckTL}>
            <Select
              className={styles.deviceCheckSelect}
              placeholder='请选择'
              disabled={testMic || testSpeaker}
              value={microphoneId}
              onChange={(value) => {
                handleChangeDevice('microphone', value)
              }}
              options={originMicrophones.map((item) => ({
                value: item.deviceId,
                label: item.label,
              }))}
            />
            <Button
              disabled={testSpeaker || !microphoneId || originMicrophones.length===0}
              onClick={handleMicTest}
              loading={micTestLoading}
              shape="round"
              type="default"
            >
              {testMic ? '停止检测' : '检测麦克风'}
            </Button>
          </div>
          <div className={styles.deviceCheckInlineLabel}>
            <span className={styles.deviceCheckInlineLabelText}>输入级别</span>
            <Progress
              className={styles.deviceCheckSlider}
              percent={audioLevel}
              showInfo={false}
            />
          </div>
          <div className={styles.deviceCheckInlineLabel}>
            <span className={styles.deviceCheckInlineLabelText}>输入音量</span>
            <Slider
              className={styles.deviceCheckSlider}
              min={0}
              max={100}
              defaultValue={100}
              onChange={(volume) => {
                roomStore.setMicrophoneCaptureVolume(volume)
                onMicroCaptureVolumeChange?.(volume)
              }}
              disabled={!microphoneId || originMicrophones.length===0}
            />
          </div>
        </div>
      </div>
      <div className={styles.deviceCheckRow}>
        <span className={styles.deviceCheckLabelText}>扬声器：</span>
        <div>
          <div className={styles.deviceCheckTL}>
            <Select
              className={styles.deviceCheckSelect}
              placeholder='请选择'
              disabled={testMic || testSpeaker}
              value={speakerId}
              onChange={(value) => {
                handleChangeDevice('speaker', value)
              }}
              options={originSpeakers.map((item) => ({
                value: item.deviceId,
                label: item.label,
              }))}
            />
            <Button
              disabled={testMic || !speakerId || originSpeakers.length===0}
              onClick={handleSpeakerTest}
              loading={speakerTestLoading}
              shape="round"
              type="default"
            >
              {testSpeaker ? '停止检测' : '检测扬声器'}
            </Button>
          </div>
          <div className={styles.deviceCheckInlineLabel}>
            <span className={styles.deviceCheckInlineLabelText}>输出级别</span>
            <Progress
              className={styles.deviceCheckSlider}
              percent={speakerLevel}
              showInfo={false}
            />
          </div>
          <div className={styles.deviceCheckInlineLabel}>
            <span className={styles.deviceCheckInlineLabelText}>输出音量</span>
            <Slider
              className={styles.deviceCheckSlider}
              min={0}
              max={100}
              defaultValue={100}
              onChange={(volume) => {
                if (audioRef.current) {
                  audioRef.current.volume = volume / 100
                }
                console.log("设置扬声器输出音量",volume)
                onAudioVolumeChange?.(volume)
              }}
              disabled={!speakerId || originSpeakers.length===0}
            />
          </div>
        </div>
      </div>
      <div style={theme?.footerStyle} className={styles.deviceCheckFooter}>
        <Button onClick={handleConfirm} type="primary">
          {okText}
        </Button>
      </div>
      <audio ref={audioRef} src={auidoUrl} loop={true} autoPlay={false}></audio>
    </div>
  )
}

export default DeviceCheck