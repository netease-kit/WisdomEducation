/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { useState, useMemo, useEffect } from "react";
import { observer } from "mobx-react";
import { Button, Modal, Select, Image } from "antd";
import DeviceData, { IProps as DeviceProps } from "@/component/device-data";
import "./index.less";
import logger from "@/lib/logger";
import { useRoomStore, useUIStore, useWhiteBoardStore } from "@/hooks/store";
import {
  HandsUpTypes,
  RoleTypes,
  RoomTypes,
  isElectron,
  ShareListItem,
} from "@/config";
import intl from 'react-intl-universal';

const DeviceList: React.FC = observer(() => {
  const [deviceList, setDeviceList] = useState<DeviceProps["data"]>([]);
  const [position, setPosition] = useState<DeviceProps["position"]>({});
  const [listType, setListType] = useState<"audio" | "video" | null>(null);
  const [microphoneId, setMicrophoneId] = useState("");
  const [cameraId, setCameraId] = useState("");
  const [speakerId, setSpeakerId] = useState("");
  const [microLabel, setMicroLabel] = useState("");
  const [cameraLabel, setCameraLabel] = useState("");
  const [speakLabel, setSpeakLabel] = useState("");
  const [shareList, setShareList] = useState<ShareListItem[]>([]);
  const [shareSelectVisible, setShareSelectVisible] = useState(false);
  const [shareInfo, setShareInfo] = useState<ShareListItem>();
  const [originCameras, setOriginCameras] = useState([]);
  const [originMicrophones, setOriginMicrophones] = useState([]);
  const [originSpeakers, setOriginSpeakers] = useState([]);

  const roomStore = useRoomStore();
  const uiStore = useUIStore();
  const whiteBoardStore = useWhiteBoardStore();
  const {
    joined,
    localData,
    roomInfo: { sceneType },
    hasOtherScreen,
    deviceChangedCount,
    joinFinish,
    reselectDevice
  } = roomStore;

  const handleAudioClick = () => {
    switch (localData?.hasAudio) {
      case true:
        roomStore.closeAudio(localData?.userUuid);
        break;
      case false:
        roomStore.openAudio(localData?.userUuid);
        break;
      default:
        break;
    }
  };

  const handleVideoClick = () => {
    switch (localData?.hasVideo) {
      case true:
        roomStore.closeCamera(localData?.userUuid);
        break;
      case false:
        roomStore.openCamera(localData?.userUuid);
        break;
      default:
        break;
    }
  };

  const handleShareClick = async () => {
    if (roomStore.classStep !== 1) {
      await uiStore.showToast(intl.get("请先开始上课"));
      return;
    }
    switch (localData?.hasScreen) {
      case true:
        if (roomStore.isLiveTeaJoin) {
          const res = await whiteBoardStore.getCanvasTrack()
          await roomStore.switchScreenWithCanvas('changeToCanvas', res);
        } else {
          await roomStore.stopScreen();
        }
        break;
      case false:
        if (hasOtherScreen) {
          await uiStore.showToast(intl.get("已有人在共享，您无法共享"));
          return;
        }
        if (!isElectron) {
          if (roomStore.isLiveTeaJoin) {
            await roomStore.switchScreenWithCanvas('changeToScreen')
          } else {
            await roomStore.startScreen();
          }
        } else {
          const res = await roomStore.getShareList();
          setShareList(res);
          setShareInfo(res[0]);
          setShareSelectVisible(true);
        }
        break;
      default:
        break;
    }
  };

  const deviceListData = useMemo(() => {
    if (!listType) {
      return deviceList;
    }
    if (listType === "audio") {
      return deviceList.filter((item) => item.type !== "camera");
    }
    return deviceList.filter((item) => item.type === "camera");
  }, [deviceList, listType]);

  const handleAudioDataShow = (e) => {
    const ev = e || window.Event;
    setPosition({
      left: "27%",
      bottom: 68,
    });
    setListType(listType === "audio" ? null : "audio");
    ev.stopPropagation();
  };

  const handleVideoDataShow = (e) => {
    const ev = e || window.Event;
    setPosition({
      left: "34%",
      bottom: 68,
    });
    setListType(listType === "video" ? null : "video");
    ev.stopPropagation();
  };

  const handleSelectShare = (value: ShareListItem) => {
    setShareInfo(value);
  };

  const handleShareModalClick = () => {
    roomStore.startScreen(true, shareInfo?.displayId, shareInfo?.id);
    setShareSelectVisible(false);
  };

  useEffect(() => {
    const listener = () => {
      setListType(null);
    };
    document.addEventListener("click", listener);
    return () => {
      document.removeEventListener("click", listener);
    };
  }, []);

  useEffect(() => {
    if (joined) {
      if (!listType) return;
      roomStore
        .getDeviceListData()
        .then(
          ({
            microphones = [],
            cameras = [],
            speakers = [],
            speakerIdSelect = "",
            microphoneSelect = "",
            cameraSelect = "",
          }) => {
            const speakerItem = speakers.find((item) => item.deviceId === speakerIdSelect)
            const microItem = microphones.find((item) => item.deviceId === microphoneSelect)
            const cameraItem = cameras.find((item) => item.deviceId === cameraSelect)
            if (listType === "audio") {
              if (!microItem || (microLabel && microItem.label !== microLabel)) {
                roomStore.selectAudio(microphones[0]?.deviceId).then(() => {
                  setMicrophoneId(microphones[0]?.deviceId);
                  setMicroLabel(microphones[0]?.label);
                });
              } 
              if (!speakerItem || (speakLabel && speakerItem.label !== speakLabel)) {
                roomStore.selectSpeakers(speakers[0]?.deviceId).then(() => {
                  setSpeakerId(speakers[0]?.deviceId);
                  setSpeakLabel(speakers[0]?.label);
                });
              } 
            } else if (listType === "video") {
              if (!cameraItem || (cameraLabel && cameraItem.label !== cameraLabel)) {
                roomStore.selectVideo(cameras[0]?.deviceId).then(() => {
                  setCameraId(cameras[0]?.deviceId);
                  setCameraLabel(cameras[0]?.label);
                });
              }
            }
            const data: DeviceProps["data"] = [
              {
                title: intl.get('请选择扬声器'),
                type: "speaker",
                list: speakers?.map((item) => ({
                  label: item.label,
                  value: item.deviceId,
                })),
                value: speakerIdSelect,
                onChange: ({ value }) => {
                  roomStore
                    .selectSpeakers(value)
                    .then(() => {
                      const label = speakers?.filter((item) => {
                        if (item.deviceId === value) {
                          return true;
                        }
                      })?.[0].label;
                      setSpeakLabel(label);
                      setSpeakerId(value);
                      setListType(null);
                      uiStore.showToast(`${intl.get("当前选择")} ${label}`);
                    })
                    .catch((e) => {
                      logger.log("切换扬声器失败", e);
                    });
                },
              },
              {
                title: intl.get('请选择麦克风'),
                type: "microphone",
                list: microphones?.map((item) => ({
                  label: item.label,
                  value: item.deviceId,
                })),
                value: microphoneSelect
                  ? microphoneSelect
                  : microphones[0]?.deviceId,
                onChange: ({ value }) => {
                  roomStore
                    .selectAudio(value)
                    .then(() => {
                      const label = microphones?.filter((item) => {
                        if (item.deviceId === value) {
                          return true;
                        }
                      })?.[0].label;
                      setMicroLabel(label);
                      setMicrophoneId(value);
                      setListType(null);
                      uiStore.showToast(`${intl.get("当前选择")} ${label}`);
                    })
                    .catch((e) => {
                      logger.log("切换麦克风失败", e);
                    });
                },
              },
              {
                title: intl.get('请选择摄像头'),
                type: "camera",
                list: cameras?.map((item) => ({
                  label: item.label,
                  value: item.deviceId,
                })),
                value: cameraSelect ? cameraSelect : cameras[0]?.deviceId,
                onChange: ({ value }) => {
                  roomStore
                    .selectVideo(value)
                    .then(() => {
                      const label = cameras?.filter((item) => {
                        if (item.deviceId === value) {
                          return true;
                        }
                      })?.[0].label;
                      setCameraLabel(label);
                      setCameraId(value);
                      setListType(null);
                      uiStore.showToast(`${intl.get("当前选择")} ${label}`);
                    })
                    .catch((e) => {
                      logger.log("切换摄像头失败", e);
                    });
                },
              },
            ];
            setDeviceList(data);
          }
        );
    }
  }, [joined, speakerId, microphoneId, cameraId, listType]);

  useEffect(() => {
    if(!joinFinish) return
    roomStore
      .getDeviceListData()
      .then(
        ({
          microphones = [],
          cameras = [],
          speakers = [],
          cameraSelect = "",
          microphoneSelect = "",
          speakerIdSelect = "",
        }) => {
          setOriginCameras(cameras);
          setOriginMicrophones(microphones);
          setOriginSpeakers(speakers);
          const cameraItem = cameras.find((item) => item.deviceId === cameraSelect)
          const microItem = microphones.find((item) => item.deviceId === microphoneSelect)
          const speakerItem = speakers.find((item) => item.deviceId === speakerIdSelect)
          setCameraLabel(cameraItem?.label)
          setMicroLabel(microItem?.label)
          setSpeakLabel(speakerItem?.label)
        }
      )
  }, [joinFinish, reselectDevice])

  useEffect(() => {
    if(!joined || !joinFinish) return
    roomStore
      .getDeviceListData()
      .then(
        ({
          microphones = [],
          cameras = [],
          speakers = [],
          speakerIdSelect = "",
          microphoneSelect = "",
          cameraSelect = "",
        }) => {
          const message: Array<any> = [];
          const speakerItem = speakers?.find((item) => item.deviceId === speakerIdSelect)
          const microItem = microphones?.find((item) => item.deviceId === microphoneSelect)
          const cameraItem = cameras?.find((item) => item.deviceId === cameraSelect)
          // 需要异常提示的情况：插入设备或拔掉正在使用的设备（包括拔掉后可能发生自动切换的情况）
          if (
            (cameras.length > originCameras.length) || 
            (cameraSelect && !cameraItem) || 
            (cameraLabel && cameraItem?.label !== cameraLabel)
          ) {
            message.push(intl.get("视频输入设备"));
          }
          if (
            (microphones.length > originMicrophones.length) || 
            (microphoneSelect && !microItem) || 
            (microLabel && microItem?.label !== microLabel)
          ) {
            message.push(intl.get("音频输入设备"));
            // 麦克风移出default变更时，需要手动重连一下
            if ((microLabel && microItem?.label !== microLabel)) {
              const tempMicro = speakerIdSelect || speakers[0]?.deviceId
              tempMicro && ( roomStore.selectAudio(tempMicro))
            }
          }
          if (
            speakers.length > originSpeakers.length || 
            (speakerIdSelect && !speakerItem) || 
            (speakLabel && speakerItem?.label !== speakLabel)
          ) {
            message.push(intl.get("音频输出设备"));
          }
          if (message.length > 0) {
            uiStore.showToast(message.join("、") + intl.get("异常，请重新进行设备选择和检测！"));
            logger.log("检测到有设备插拔：",message);
          }
          setListType(null);
          setOriginCameras(cameras);
          setOriginMicrophones(microphones);
          setOriginSpeakers(speakers);
          setCameraLabel(cameraItem?.label)
          setMicroLabel(microItem?.label)
          setSpeakLabel(speakerItem?.label)
        }
      )    
  }, [deviceChangedCount]);

  return (
    <div className="deviceList-wrapper">
      {(![RoomTypes.bigClass, RoomTypes.bigClasLive].includes(Number(sceneType)) ||
        localData?.avHandsUp === HandsUpTypes.teacherAgree ||
        localData?.role === RoleTypes.host ||
        (localData?.role === RoleTypes.host && RoomTypes.bigClasLive === Number(sceneType))) && (
        <>
          <div className="list-wrapper">
            <div className="list-content">
              <Button
                onClick={handleAudioClick}
                type="text"
                icon={
                  localData?.hasAudio ? (
                    <img
                      src={require("@/assets/imgs/audioOpen.png").default}
                      alt="audioOpen"
                    />
                  ) : (
                    <img
                      src={require("@/assets/imgs/audioClose.png").default}
                      alt="audioClose"
                    />
                  )
                }
              />
              <p className={localData?.hasAudio ? "gray" : "red"}>
                {localData?.hasAudio ? intl.get("静音") : intl.get("解除静音")}
              </p>
            </div>
            <div className="list-arrows">
              <Button
                onClick={handleAudioDataShow}
                type="text"
                icon={
                  <img
                    src={require("@/assets/imgs/arrows.png").default}
                    alt="arrows"
                    className="arrows-imgs"
                  />
                }
              />
            </div>
          </div>
          <div className="list-wrapper">
            <div className="list-content">
              <Button
                onClick={handleVideoClick}
                type="text"
                icon={
                  localData?.hasVideo ? (
                    <img
                      src={require("@/assets/imgs/videoOpen.png").default}
                      alt="videoOpen"
                    />
                  ) : (
                    <img
                      src={require("@/assets/imgs/videoClose.png").default}
                      alt="videoClose"
                    />
                  )
                }
              />
              <p className={localData?.hasVideo ? "gray" : "red"}>
                {localData?.hasVideo ? intl.get("关闭视频") : intl.get("开启视频")}
              </p>
            </div>
            <div className="list-arrows">
              <Button
                onClick={handleVideoDataShow}
                type="text"
                icon={
                  <img
                    src={require("@/assets/imgs/arrows.png").default}
                    alt="arrows"
                    className="arrows-imgs"
                  />
                }
              />
            </div>
          </div>
        </>
      )}
      {localData?.canScreenShare && (
        <div className="list-wrapper">
          <div className="list-content">
            <Button
              onClick={handleShareClick}
              type="text"
              icon={
                !localData?.hasScreen ? (
                  <img
                    src={require("@/assets/imgs/shareOpen.png").default}
                    alt="shareOpen"
                  />
                ) : (
                  <img
                    src={require("@/assets/imgs/shareClose.png").default}
                    alt="shareClose"
                  />
                )
              }
            />
            <p className={!localData?.hasScreen ? "gray" : "red"}>
              {!localData?.hasScreen ? intl.get("共享屏幕") : intl.get("停止共享")}
            </p>
          </div>
        </div>
      )}
      <DeviceData
        data={deviceListData}
        position={position}
        visible={!!listType}
      />
      <Modal
        visible={shareSelectVisible}
        centered
        onOk={handleShareModalClick}
        onCancel={() => setShareSelectVisible(false)}
        okText={intl.get('确认')}
        cancelText={intl.get('取消')}
        wrapClassName="modal share-modal"
        width={720}
      >
        {/* <Select onChange={handleSelectShare} value={shareInfo}>
          {shareList.map((item: ShareListItem) => (
            <Select.Option key={item.id} value={Number(item.id)}>
              {item.name}
            </Select.Option>
          ))}
        </Select> */}
        {shareList.map((item: ShareListItem) => (
          <div
            className={`share-item ${
              item.id === shareInfo?.id && "share-item-select"
            }`}
            key={item.id}
            onClick={() => handleSelectShare(item)}
          >
            <img src={item.thumbnail} alt=""></img>
            <p>{item.name}</p>
          </div>
        ))}
      </Modal>
    </div>
  );
});

export default DeviceList;
