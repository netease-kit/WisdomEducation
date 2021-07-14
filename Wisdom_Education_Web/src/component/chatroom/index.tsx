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
} from './chatroomHelper';
import { initialState, reducer } from './reducer';
import { default as ChatroomUI } from './Chatroom';
import { useUIStore } from '@/hooks/store';




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
      return () => {
        chatroomHelper.destroy();
      };
    }
  }, [chatroomHelper]);

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
      uiStore.showToast(`发送聊天室${msg.type === 'text'
        ? '文本'
        : msg.type === 'image'
          ? '图片'
          : msg.type === 'file'
            ? '文件'
            : '未知'
      }消息失败`, 'error')
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
        message.error('上传超时，请重试');
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
            const errMsg = `上传${type === 'image' ? '图片' : type === 'file' ? '文件' : '未知'
            }失败`;
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
