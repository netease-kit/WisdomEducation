/* eslint-disable no-case-declarations */
/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { useEffect, useState, useMemo, useReducer } from 'react';
import { message } from 'antd';
import ChatroomHelper, {
  Message,
  MessageType,
  Progress,
  AttachType,
} from './chatroomHelper';
import { initialState, reducer } from './reducer';
import { default as ChatroomUI } from './Chatroom';
import { useUIStore, useRoomStore } from '@/hooks/store';
import logger from '@/lib/logger';
import { RoleTypes } from '@/config';
import { useHistory } from 'react-router-dom';
import intl from 'react-intl-universal';

interface IProps {
  nim: any;
  appKey: string;
  account: string;
  nickName: string;
  token: string;
  chatroomId: string;
  canSendMsg: boolean;
  receiveMessage(msgs: Message[]): void,
}


const Chatroom: React.FC<IProps> = ({
  nim,
  appKey,
  account,
  nickName,
  token,
  chatroomId,
  canSendMsg,
  receiveMessage
}) => {
  const uiStore = useUIStore();
  const roomStore = useRoomStore();
  const history = useHistory();
  const { localUserInfo, localData } = roomStore;

  const [state, dispatch] = useReducer(reducer, initialState);

  const onMessage = (msgs: Message[]) => {
    dispatch({ type: 'addMessage', payload: msgs });
    receiveMessage(msgs)
  }

  const chatroomHelper = useMemo(
    () =>
      new ChatroomHelper({
        nim,
        appKey,
        account,
        token,
        chatroomId,
        chatroomNick: nickName,
        onMessage,
      }),
    [nim, appKey, account, token, chatroomId, nickName],
  );


  const [imageProgress, setImageProgress] = useState<Progress | undefined>(
    undefined,
  );
  const [fileProgress, setFileProgress] = useState<Progress | undefined>(
    undefined,
  );


  useEffect(() => {
    if(chatroomHelper) {
      // chatroomHelper.on('onMessage', (msgs: Message[]) => {
      //   dispatch({ type: 'addMessage', payload: msgs });
      // });
      roomStore.setChatRoomInstance(chatroomHelper.chatRoom);
      chatroomHelper.on('chat-onmsgs', (msgs: Message[]) => {
        for (const item of msgs) {
          switch (item.type) {
            case "notification":
              const type: AttachType = item.attach.type;
              if (['memberEnter', 'memberExit'].includes(type)) {
                chatroomHelper.chatRoom.getChatroomMembers({
                  guest: true,
                  limit: 200,
                  done: (error, obj) => {
                    if (error) {
                      logger.error('获取聊天室成员失败', error);
                      return;
                    }
                    const members = obj.members.map((item) => {
                      if (!item.nick.includes(intl.get('老师'))) {
                        if (localUserInfo.userUuid === item.account) {
                          return {
                            userName: item.nick,
                            userUuid: item.account,
                            role: RoleTypes.broadcaster,
                          }
                        } else {
                          return {
                            userName: item.nick,
                            userUuid: item.account,
                            role: RoleTypes.broadcaster,
                          }
                        }
                      }
                    });
                    // members.unshift({
                    //   userName: localUserInfo.userName,
                    //   userUuid: localUserInfo.userUuid,
                    //   role: localUserInfo.role,
                    //   rtcUid: localUserInfo.userUuid,
                    //   streams: {
                    //     audio: {
                    //       value: localData?.hasAudio ? 1 : 0,
                    //     },
                    //     video: {
                    //       value: localData?.hasAudio ? 1 : 0,
                    //     }
                    //   }
                    // })
                    roomStore.setBigLiveMemberFullList(members);
                    logger.debug('聊天室成员', obj.members, members)
                  }
                })
              }
              break;
            case "custom":
              try {
                const res: {
                  [key: string]: any
                } = {};
                // 保持格式统一
                const data = JSON.parse(item.content);
                res.body = data.data;
                roomStore.nimNotify(res, false)
              } catch (err) {
                logger.error('解析自定义消息失败', err)
              }
              break;
            default:
              break;
          }
        }
      });

      chatroomHelper.on('chat-onkicked', async (reason) => {
        let msg = reason || intl.get('该账号被踢出房间')
        if (reason === 'samePlatformKick') {
          msg = intl.get('该账号在其他地方重复登录')

          const userInfo = roomStore?.localUserInfo || {};
          if (userInfo.role === RoleTypes.host) {
            await roomStore.endClassRoom();
          }
        } else if (reason === 'managerKick') {
          msg = intl.get('被管理员踢出房间')
        }

        //被踢出，需要回到首页
        roomStore.leave()
        history.push('/');
        uiStore.showToast(msg);
        uiStore.setLoading(false);
      })
      return () => {
        chatroomHelper.removeAllListeners();
        chatroomHelper.destroy();
        roomStore.removeChatRoomInstance();
      };
    }
  }, [history, chatroomHelper, roomStore]);

  const setProgress = (type: MessageType, progress?: Progress) => {
    if (type === 'image') {
      setImageProgress(progress);
    } else if (type === 'file') {
      setFileProgress(progress);
    }
  };

  const doneHandler = (err: any, msg: Message) => {
    if (err) {
      console.log(
        `发送聊天室${msg.type === 'text'
          ? '文本'
          : msg.type === 'image'
            ? '图片'
            : msg.type === 'file'
              ? '文件'
              : '未知'
        }消息失败: `,
        err,
      );
      uiStore.showToast(`${intl.get("发送聊天室")}${msg.type === 'text'
        ? intl.get('文本')
        : msg.type === 'image'
          ? intl.get('图片')
          : msg.type === 'file'
            ? intl.get('文件')
            : intl.get('未知')
      }${intl.get("消息失败")}`, 'error')
      dispatch({ type: 'updateMessage', payload: { ...msg, status: 'fail' } });
      return;
    }
    dispatch({ type: 'updateMessage', payload: { ...msg, status: 'success' } });
  };

  const sendHandler = (data: string | any, type: MessageType) => {
    if (type === 'text') {
      const newMsg = chatroomHelper.sendText({
        text: data,
        done: (err) => {
          doneHandler(err, newMsg);
        },
      });
      dispatch({
        type: 'addMessage',
        payload: { ...newMsg, isMe: true, fromNick: nickName },
      });
    } else {
      let newMsg: Message;
      setProgress(type, {
        total: '-',
        percentage: '-',
        percentageText: '-',
        loaded: '-',
      });
      // 超时重置发送按钮
      const timeout = setTimeout(() => {
        message.error(intl.get('上传超时，请重试'));
        setProgress(type);
      }, 30000);
      chatroomHelper.sendFile({
        type,
        blob: data,
        uploadprogress: (progress) => {
          setProgress(type, progress);
        },
        uploaddone: (err) => {
          if (err) {
            const errMsg = `${intl.get("上传")}${type === 'image' ? intl.get('图片') : type === 'file' ? intl.get('文件') : intl.get('未知')
            }${intl.get("失败")}`;
            message.error(errMsg);
            console.log(errMsg, err);
          }
          clearTimeout(timeout);
          setProgress(type);
        },
        beforesend: (msg) => {
          newMsg = msg;
          dispatch({
            type: 'addMessage',
            payload: { ...msg, isMe: true, fromNick: nickName },
          });
        },
        done: (err) => {
          if (newMsg) {
            doneHandler(err, newMsg);
          }
        },
      });
    }
  };

  const resendHandler = (msg: Message, type: MessageType) => {
    if (type === 'text') {
      const newMsg = chatroomHelper.sendText({
        resend: true,
        idClient: msg.idClient,
        text: msg.text,
        done: (err) => {
          doneHandler(err, newMsg);
        },
      });
      dispatch({ type: 'updateMessage', payload: newMsg });
    } else {
      dispatch({
        type: 'updateMessage',
        payload: { ...msg, status: 'sending' },
      });
      chatroomHelper.sendFile({
        type,
        file: msg.file,
        resend: true,
        idClient: msg.idClient,
        done: (err) => {
          doneHandler(err, msg);
        },
      });
    }
  };

  return (
    <ChatroomUI
      messages={state.messages}
      onSend={sendHandler}
      onResend={resendHandler}
      imageProgress={imageProgress}
      fileProgress={fileProgress}
      canSendMsg={canSendMsg}
    />
  );
};

export default Chatroom;
