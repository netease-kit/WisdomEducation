/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import React, {
  useRef,
  useCallback,
  useEffect,
  useState,
  useMemo,
} from "react";
import "./index.less";
import { observer } from "mobx-react";
import { useRoomStore, useWhiteBoardStore, useUIStore } from "@/hooks/store";
import { RoleTypes, RoomTypes, HandsUpTypes, isElectron, HostSeatOperation } from "@/config";
import { Button, Modal, Dropdown, Menu } from "antd";
import logger from "@/lib/logger";
import intl from 'react-intl-universal';

interface VideoPlayerProps {
  showUserControl?: boolean;
  showMediaStatus?: boolean;
  showMoreBtn?: boolean;
  hasAudio?: boolean;
  hasVideo?: boolean;
  hasScreen?: boolean;
  applyWhiteBoardDraw?: boolean;
  needDefaultAvatar?: boolean;
  basicStream?: any;
  audioStream?: any;
  isLocal?: boolean;
  userName?: string;
  role?: string;
  rtcUid?: string | number;
  userUuid?: string;
  wbDrawEnable?: boolean;
  canScreenShare?: boolean;
  avHandsUp?: number | null;
}

const VideoPlayer: React.FC<VideoPlayerProps> = observer(
  ({
    showUserControl = false,
    showMediaStatus = true,
    showMoreBtn = false,
    needDefaultAvatar = false,
    hasAudio = false,
    hasVideo = false,
    hasScreen = false,
    applyWhiteBoardDraw = false,
    basicStream,
    audioStream,
    isLocal = false,
    userName,
    role,
    rtcUid = "",
    userUuid = "",
    wbDrawEnable = false,
    canScreenShare = false,
    avHandsUp = 0,
  }) => {
    let timer;
    const playDOM = useRef<HTMLDivElement>(null);
    const roomStore = useRoomStore();
    const wbStore = useWhiteBoardStore();
    const uiStore = useUIStore();
    const [modalVisible, setModalVisible] = useState(false);
    const [reloadTime, setReloadTime] = useState(0);

    const isBySelf = useMemo(
      () => userUuid === roomStore?.localData?.userUuid,
      [roomStore?.localData, userUuid]
    );

    const handleAudio = async () => {
      logger.log("Operated by the current user", isBySelf);
      if (hasAudio) {
        await roomStore.closeAudio(userUuid, true, isBySelf);
        await uiStore.showToast(intl.get("操作成功"));
      } else {
        await roomStore.openAudio(userUuid, true, isBySelf);
        await uiStore.showToast(intl.get("操作成功"));
      }
    };

    const handleVideo = async () => {
      if (hasVideo) {
        await roomStore.closeCamera(userUuid, true, isBySelf);
        await uiStore.showToast(intl.get("操作成功"));
      } else {
        await roomStore.openCamera(userUuid, true, isBySelf);
        await uiStore.showToast(intl.get("操作成功"));
      }
    };

    const handleSetWbEnableDraw = async (userUuid: string, value: number) => {
      if (roomStore.classStep !== 1) {
        await uiStore.showToast(intl.get("请先开始上课"));
        return;
      }
      await roomStore.setWbEnableDraw(userUuid, value);
      await uiStore.showToast(intl.get("操作成功"));
    };

    const handleSetAllowScreen = async (userUuid: string, value: number) => {
      if (roomStore.classStep !== 1) {
        await uiStore.showToast(intl.get("请先开始上课"));
        return;
      }
      // if(value === 0) await roomStore.changeSubVideoStream(userUuid, value);
      await roomStore.setAllowScreenShare(userUuid, value);
      await uiStore.showToast(intl.get("操作成功"));
    };

    const handleModalOk = async (userUuid: string, value: HandsUpTypes) => { 
      try {
        switch (value) {
          case HandsUpTypes.teacherAgree:
            break;
          case HandsUpTypes.teacherOff:
            if (roomStore.isBigLiveClass) {
              await roomStore.handsUpActionForSeat(userUuid, HostSeatOperation.teacherOff, true)
            } else {
              await roomStore.handsUpAction(userUuid, value);
            }
            await roomStore.changeSubVideoStream(userUuid, 0);
            break;
          default:
            break;
        }
        await uiStore.showToast(intl.get("操作成功"));
      } catch (error) {
        console.error('error', error);
      } finally {
        setModalVisible(false);
      }
    };

    const handleModalCancel = () => {
      setModalVisible(false);
    };

    const handsUpAction = (userUuid: string, value: HandsUpTypes) => {
      setModalVisible(true);
    };

    const MoreContent = useCallback(() => {
      return (
        <Menu>
          {wbDrawEnable ? (
            <Menu.Item onClick={() => handleSetWbEnableDraw(userUuid, 0)}>
              {intl.get('取消白板权限')}
            </Menu.Item>
          ) : (
            <Menu.Item onClick={() => handleSetWbEnableDraw(userUuid, 1)}>
              {intl.get('授予白板权限')}
            </Menu.Item>
          )}
          {canScreenShare ? (
            <Menu.Item onClick={() => handleSetAllowScreen(userUuid, 0)}>
              {intl.get('取消共享权限')}
            </Menu.Item>
          ) : (
            <Menu.Item onClick={() => handleSetAllowScreen(userUuid, 1)}>
              {intl.get('授予共享权限')}
            </Menu.Item>
          )}
          {[RoomTypes.bigClass, RoomTypes.bigClasLive].includes(Number(roomStore?.roomInfo?.sceneType)) &&
            avHandsUp === HandsUpTypes.teacherAgree && (
            <Menu.Item onClick={() =>
              handsUpAction(userUuid, HandsUpTypes.teacherOff)
            }>
              {intl.get('请他下台')}
            </Menu.Item>
          )}
        </Menu>
      );
    }, [
      userUuid,
      roomStore?.roomInfo?.sceneType,
      wbDrawEnable,
      canScreenShare,
      avHandsUp,
    ]);

    const videoParams = useMemo(() => {
      const param: {
        audio?: boolean;
        video?: boolean;
        screen?: boolean;
      } = {
        audio: true,
        video: true,
        screen: false,
      };
      if (hasScreen) {
        param.screen = true;
        param.video = false;
      }
      if (isLocal) {
        param.audio = false;
      }
      return param;
    }, [hasScreen, isLocal]);

    const playVideo = async () => {
      if (
        playDOM?.current?.clientWidth &&
        ( hasScreen || hasVideo )
      ) {
        logger.log("playback success", userName, playDOM?.current);
        setReloadTime(0);
        clearTimeout(timer);
        const modeOptions = {
          options: {
            width: playDOM.current.clientWidth,
            height: playDOM.current.clientHeight,
            cut: false,
          },
          mediaType: hasScreen ? "screen" : "video",
        };
        logger.debug('Screen sharing', hasScreen);
        logger.debug('Configure', modeOptions);
        try {
          if (isLocal) {
            logger.log(
              "Set the local view",
              playDOM.current,
              modeOptions,
              basicStream
            );
            await basicStream.setLocalRenderMode(
              modeOptions.options,
              modeOptions.mediaType
            );
            setTimeout(async() => {
              // param.audio = false;
              await basicStream
                .play(playDOM.current, videoParams)
                .catch((error) => {
                  logger.error("playback failure 1", error);
                });
            }, 2000);
          } else {
            // setTimeout(async () => {
            logger.log(
              "Set the remote view after playback",
              playDOM.current,
              modeOptions,
              basicStream
            );
            await basicStream
              .play(playDOM.current, videoParams)
              .catch((error) => {
                logger.error("playback failure 2", error);
              });
            await basicStream.setRemoteRenderMode(
              modeOptions.options,
              modeOptions.mediaType
            );
            // }, 2000);
          }
          logger.log(
            "Current playback state",
            rtcUid,
            await basicStream.isPlaying("video")
          );
        } catch (error) {
          logger.error("play failure 4", error);
        }
      } else if (reloadTime < 10) {
        setReloadTime(reloadTime + 1);
        logger.error(
          "play failure-rerendering",
          `${reloadTime}`,
          playDOM?.current?.clientWidth
        );
        timer = setTimeout(() => {
          playVideo();
        }, 2000);
      }
    };

    const playVideoInEle = async () => {
      if (
        playDOM?.current?.clientWidth &&
        roomStore.client &&
        playDOM.current.childElementCount <= 0
      ) {
        try {
          let res;
          console.log("playDOM.current", playDOM.current);
          if (isLocal) {
            if (hasScreen) {
              logger.log("ele-setupLocalSubStreamVideoCanvas");
              res = roomStore.client.setupLocalSubStreamVideoCanvas({
                view: playDOM.current,
                mode: 0,
              });
              roomStore.client.setSubStreamRenderMode("local", 0);
            } else {
              logger.log("ele-setupLocalVideoCanvas");
              res = roomStore.client.setupLocalVideoCanvas({
                view: playDOM.current,
                mode: 0,
              });
              hasVideo && roomStore.openCamera(userUuid, false, true);
              // roomStore.rtc.setVideoProfile();
            }
          } else {
            if (hasScreen) {
              logger.log("ele-setupRemoteSubStreamVideoCanvas", rtcUid);
              res = roomStore.client.setupRemoteSubStreamVideoCanvas(rtcUid, {
                view: playDOM.current,
                mode: 0,
              });
            } else {
              logger.log("ele-setupRemoteVideoCanvas", rtcUid);
              res = roomStore.client.setupRemoteVideoCanvas(rtcUid, {
                view: playDOM.current,
                mode: 0,
              });
              roomStore.client.setSubStreamRenderMode(rtcUid, 0);
            }
          }
          if (res !== 0) {
            logger.error("playback failure", res);
          } else {
            logger.log("playback success", res);
          }
        } catch (error) {
          logger.error("ele playback failure", error);
        }
      }
    };

    const stopVideo = () => {
      if (basicStream) {
        for (const key in videoParams) {
          if (Object.prototype.hasOwnProperty.call(videoParams, key)) {
            const item = videoParams[key];
            if (item) {
              basicStream.stop(key);
            }
          }
        }
      }
    };

    const playAudio = () => {
      if (playDOM?.current?.clientWidth) {
        audioStream.play(playDOM?.current, {
          audio: true,
          video: false,
          screen: false
        });
      }
    };

    // const stopAudio = () => {
    //   if (audioStream) {
    //     audioStream.stop('audio')
    //   }
    // }

    useEffect(() => {
      logger.log("basicStream||hasVideo||hasScreen changed --> playVideo");
      if (!isElectron) {
        if (
          (basicStream && hasVideo && basicStream.hasVideo) ||
          (basicStream && hasScreen)
        ) {
          playVideo();
          // 大班课学生上台有视频时，需要多次尝试播放，否则会出现黑屏
          if (
            [RoomTypes.bigClass].includes(
              Number(roomStore?.roomInfo?.sceneType)
            ) &&
            isLocal
          ) {
            let count = 0
            const timer = setInterval(() => {
              count++
              if (count < 3) {
                if ((hasVideo && !basicStream?.isPlaying('video')) || (hasScreen && !basicStream?.isPlaying('screen'))) {
                  console.log('再次尝试播放次数 ', count, {hasVideo, hasScreen})
                  playVideo()
                }
              } else {
                clearInterval(timer)
              }
            }, 1000)
          }
        }
      } else {
        playVideoInEle();
      }
      return () => {
        if (basicStream) {
          // stopVideo();
        }
      };
    }, [
      basicStream,
      basicStream?.hasVideo,
      hasVideo,
      hasScreen
    ]);

    /* electron-sdk clear the canvas before turning off the camera */
    useEffect(() => {
      if (!isElectron || !roomStore.client || hasScreen || hasVideo) return 
      try {
        let res;
        if (isLocal) {
          logger.log("ele-setupLocalVideoCanvas null");
          if(roomStore?.localData?.hasScreen) {
            // Avoid sdk error reporting, repair after upgrading sdk
            res = 0
            logger.log("skipped Electron canvas clear when screening");
          } else {
            res = roomStore.client.setupLocalVideoCanvas({
              view: null,
              mode: 0,
            });
          }
        } else {
          if (rtcUid) {
            logger.log("ele-setupRemoteVideoCanvas null", rtcUid);
            res = roomStore.client.setupRemoteVideoCanvas(rtcUid, {
              view: null,
              mode: 0,
            });
          }
        }
        if (res !== 0) {
          (isLocal || rtcUid) && (logger.error("Failed to clear the Electron canvas", res));
        } else {
          logger.log("Electron canvas cleared", res);
        }
      } catch(error) {
        logger.error("Failed to clear the Electron canvas", error);
      }
    }, [hasVideo])

    useEffect(() => {
      if (audioStream && !isLocal) {
        playAudio();
      }
    }, [audioStream]);

    return (
      <div className="video-player-component">
        <div
          className={`video-contaniner video-contaniner-${rtcUid}-${
            hasScreen ? "screen" : "video"
          } ${hasScreen ? "" : hasVideo ? "" : "video-hide"}`}
          ref={playDOM}
        />
        {!hasVideo && !needDefaultAvatar && (
          <div className="default-video-avatar" />
        )}
        {showMediaStatus && (
          <div className="video-media-control">
            {userName ? (
              <div className="username">
                {userName}
                {`${role === RoleTypes.host ? `（${intl.get("老师")}）` : `（${intl.get("学生")}）`}`}
              </div>
            ) : (
              <div />
            )}
            {userName ? (
              <div>
                {role !== RoleTypes.host && wbDrawEnable && (
                  <i className="media-wb-open" />
                )}
                {role !== RoleTypes.host && canScreenShare && (
                  <i className="media-screen-open" />
                )}
                {hasAudio ? (
                  <i className="media-audio-open" />
                ) : (
                  <i className="media-audio-close" />
                )}
                {hasVideo ? (
                  <i className="media-video-open" />
                ) : (
                  <i className="media-video-close" />
                )}
              </div>
            ) : (
              <div />
            )}
          </div>
        )}
        {showUserControl && (
          <div className="video-user-control">
            <div className="menu-item">
              <Button
                className="control-button"
                shape="circle"
                onClick={handleAudio}
              >
                {hasAudio ? (
                  <i className="contrl-audio-open" />
                ) : (
                  <i className="contrl-audio-close" />
                )}
              </Button>
              <p>{hasAudio ? intl.get("静音") : intl.get("解除静音")}</p>
            </div>
            <div className="menu-item">
              <Button
                className="control-button"
                shape="circle"
                onClick={handleVideo}
              >
                {hasVideo ? (
                  <i className="contrl-video-open" />
                ) : (
                  <i className="contrl-video-close" />
                )}
              </Button>
              <p>{hasVideo ? intl.get("关闭视频") : intl.get("开启视频")}</p>
            </div>
            {showMoreBtn && (
              <div className="menu-item">
                <Dropdown
                  arrow
                  overlayClassName='video-more-outer'
                  placement="bottomRight"
                  getPopupContainer={(triggerNode) => {
                    return (triggerNode.parentNode ||
                      document.body) as HTMLElement
                  }}
                  overlay={<MoreContent />}
                >
                  <Button
                    onClick={(e) => {
                      e.stopPropagation();
                    }}
                    className="control-button"
                    shape="circle"
                  >
                    <i className="control-more-btn">···</i>
                  </Button>
                </Dropdown>
                <p>{intl.get('更多')}</p>
              </div>
            )}
            <Modal
              visible={modalVisible}
              centered
              onOk={() => handleModalOk(userUuid, HandsUpTypes.teacherOff)}
              onCancel={handleModalCancel}
              okText={intl.get('确认')}
              cancelText={intl.get('取消')}
              wrapClassName="modal"
            >
              <p className="title">{intl.get('请他下台')}</p>
              <p className="desc">
                {intl.get('结束该学生的上台动作，同时收回他的屏幕共享、白板权限')}
              </p>
            </Modal>
          </div>
        )}
      </div>
    );
  }
);

export default VideoPlayer;
