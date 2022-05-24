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
import { RoleTypes, RoomTypes, HandsUpTypes, isElectron } from "@/config";
import { Popover, Button, Modal } from "antd";
import logger from "@/lib/logger";
import { setInterval } from "timers";
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
    isLocal = true,
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
    const [moreVisible, setMoreVisible] = useState(false);
    const [userStats, setUserStats] = useState<{
      CaptureResolutionWidth?: number;
      CaptureResolutionHeight?: number;
      RecvResolutionWidth?: number;
      RecvResolutionHeight?: number;
    }>({});
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
      roomStore.setWbEnableDraw(userUuid, value);
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
      await roomStore.handsUpAction(userUuid, value);
      switch (value) {
        case HandsUpTypes.teacherAgree:
          // await roomStore.changeMemberStreamProperties(userUuid, 1, 1, 1)
          break;
        case HandsUpTypes.teacherOff:
          // await roomStore.changeMemberStreamProperties(userUuid, 0, 0, 0)
          await roomStore.changeSubVideoStream(userUuid, 0);
          break;
        default:
          // await roomStore.changeMemberStreamProperties(userUuid, 0, 0, 0)
          break;
      }
      await uiStore.showToast(intl.get("操作成功"));
      setModalVisible(false);
    };

    const handleModalCancel = () => {
      setModalVisible(false);
    };

    const handsUpAction = (userUuid: string, value: HandsUpTypes) => {
      setModalVisible(true);
    };

    const MoreContent = useCallback(() => {
      return (
        <ul onClick={() => setMoreVisible(false)}>
          {wbDrawEnable ? (
            <li>
              <Button
                onClick={() => handleSetWbEnableDraw(userUuid, 0)}
                type="text"
              >
                {intl.get('取消白板权限')}
              </Button>
            </li>
          ) : (
            <li>
              <Button
                onClick={() => handleSetWbEnableDraw(userUuid, 1)}
                type="text"
              >
                {intl.get('授予白板权限')}
              </Button>
            </li>
          )}
          {canScreenShare ? (
            <li>
              <Button
                onClick={() => handleSetAllowScreen(userUuid, 0)}
                type="text"
              >
                {intl.get('取消共享权限')}
              </Button>
            </li>
          ) : (
            <li>
              <Button
                onClick={() => handleSetAllowScreen(userUuid, 1)}
                type="text"
              >
                {intl.get('授予共享权限')}
              </Button>
            </li>
          )}
          {RoomTypes.bigClass === Number(roomStore?.roomInfo?.sceneType) &&
            avHandsUp === HandsUpTypes.teacherAgree && (
            <li>
              <Button
                onClick={() =>
                  handsUpAction(userUuid, HandsUpTypes.teacherOff)
                }
                type="text"
              >
                {intl.get('请他下台')}
              </Button>
            </li>
          )}
        </ul>
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

    const getUserStats = async () => {
      let stats;
      if (isLocal) {
        const result =
          (await roomStore.client?.getLocalVideoStats("screen")) || [];
        stats = result[0];
      } else {
        const result =
          (await roomStore.client?.getRemoteVideoStats("screen")) || {};
        stats = result[rtcUid];
      }
      // logger.log('video-screen', stats)
      if ((stats?.CaptureResolutionWidth || stats?.RecvResolutionWidth) > 0) {
        setUserStats(stats);
      }
    };

    const playVideo = async (
      CaptureResolutionWidth = 0,
      CaptureResolutionHeight = 0,
      RecvResolutionWidth = 0,
      RecvResolutionHeight = 0
    ) => {
      if (
        playDOM?.current?.clientWidth &&
        (((CaptureResolutionWidth || RecvResolutionWidth) > 0 && hasScreen) ||
          hasVideo)
      ) {
        logger.log("playback success", userName, playDOM, playDOM.current);
        setReloadTime(0);
        clearTimeout(timer);
        const scale = hasScreen
          ? CaptureResolutionWidth
            ? CaptureResolutionWidth / CaptureResolutionHeight
            : RecvResolutionWidth / RecvResolutionHeight
          : 1;
        let width, height;
        if (scale >= 1) {
          width = playDOM.current.clientWidth;
          height = playDOM.current.clientHeight;
        } else {
          width = playDOM.current.clientWidth * scale;
          height = playDOM.current.clientHeight;
        }
        const modeOptions = {
          options: {
            width: hasScreen ? width : playDOM.current.clientWidth,
            height: hasScreen ? height : playDOM.current.clientHeight,
            cut: false,
          },
          mediaType: hasScreen ? "screen" : "video",
        };
        logger.debug('Screen sharing', hasScreen);
        logger.debug('Configure', modeOptions);
        logger.debug('Pass the aspect ratio', CaptureResolutionWidth, CaptureResolutionHeight, RecvResolutionWidth, RecvResolutionHeight)
        try {
          if (isLocal) {
            logger.log(
              "Set the remote view",
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
          playDOM?.current?.clientWidth,
          CaptureResolutionWidth,
          RecvResolutionWidth
        );
        timer = setTimeout(() => {
          playVideo(
            CaptureResolutionWidth,
            CaptureResolutionHeight,
            RecvResolutionWidth,
            RecvResolutionHeight
          );
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
        audioStream.play(playDOM?.current);
      }
    };

    // const stopAudio = () => {
    //   if (audioStream) {
    //     audioStream.stop('audio')
    //   }
    // }

    const popoverListener = () => {
      if (moreVisible) {
        setMoreVisible(false);
      }
    };

    useEffect(() => {
      document.addEventListener("click", popoverListener);
      return () => {
        document.removeEventListener("click", popoverListener);
      };
    }, [moreVisible]);

    useEffect(() => {
      logger.log(
        "CaptureResolutionWidth",
        userStats?.CaptureResolutionWidth,
        userStats?.CaptureResolutionHeight,
        userStats?.RecvResolutionWidth,
        userStats?.RecvResolutionHeight
      );
      if (!isElectron) {
        if (
          (basicStream && hasVideo) ||
          (basicStream &&
            hasScreen &&
            (userStats?.CaptureResolutionWidth ||
              userStats?.RecvResolutionWidth))
        ) {
          playVideo(
            userStats?.CaptureResolutionWidth,
            userStats?.CaptureResolutionHeight,
            userStats?.RecvResolutionWidth,
            userStats?.RecvResolutionHeight
          );
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
      hasVideo,
      hasScreen,
      userStats?.CaptureResolutionWidth,
      userStats?.CaptureResolutionHeight,
      userStats?.RecvResolutionWidth,
      userStats?.RecvResolutionHeight,
    ]);

    /* electron-sdk clear the canvas before turning off the camera */
    useEffect(() => {
      if (!isElectron || !roomStore.client || hasScreen || hasVideo) return 
      try {
        let res;
        if (isLocal) {
          logger.log("ele-setupLocalVideoCanvas null");
          res = roomStore.client.setupLocalVideoCanvas({
            view: null,
            mode: 0,
          });
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
      if (audioStream) {
        playAudio();
      }
    }, [audioStream]);

    useEffect(() => {
      if (!isElectron) {
        timer = setInterval(() => {
          getUserStats();
        }, 2000);
      }
      return () => {
        clearInterval(timer);
      };
    }, []);

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
                {/* {
                (role !== RoleTypes.host && canScreenShare) && <i className="media-screen-open" />
              } */}
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
                <Popover
                  placement="bottomRight"
                  visible={moreVisible}
                  overlayClassName="video-more-outer"
                  content={<MoreContent />}
                  trigger="click"
                >
                  <Button
                    onClick={(e) => {
                      e.stopPropagation();
                      setMoreVisible(true);
                    }}
                    className="control-button"
                    shape="circle"
                  >
                    <i className="control-more-btn">···</i>
                  </Button>
                </Popover>
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
