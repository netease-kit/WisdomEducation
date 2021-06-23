/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { useState, useRef, useEffect } from 'react';
import { Input, Upload, message } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import CharCard from './ChatCard';
import { Message, MessageType, Progress } from './chatroomHelper';
import Icon from '@/component/icon';
import { useUIStore } from '@/hooks/store';
import moment from 'moment';



import './index.less';


interface IProps {
  messages: Message[];
  onSend: (data: string | any, type: MessageType) => void;
  onResend: (msg: Message, type: MessageType) => void;
  imageProgress?: Progress;
  fileProgress?: Progress;
  canSendMsg: boolean
}

const Chatroom: React.FC<IProps> = ({
  messages,
  onSend,
  onResend,
  imageProgress,
  fileProgress,
  canSendMsg,
}) => {
  const [curText, setCurText] = useState('');
  const contentRef = useRef(null);
  const inputWrapperRef = useRef(null);
  const uiStore = useUIStore();

  useEffect(() => {
    if (inputWrapperRef.current) {
      // @ts-ignore
      inputWrapperRef.current.firstChild.scrollTop =
        // @ts-ignore
        inputWrapperRef.current.firstChild.scrollHeight;
    }
  }, [curText]);

  useEffect(() => {
    if (contentRef.current) {
      // @ts-ignore
      contentRef.current.scrollTop = contentRef.current.scrollHeight;
    }
  }, [messages]);

  const pressEnterHandler = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    e.preventDefault();
    const finalCurText = curText.trim();
    if (e.shiftKey) {
      setCurText(curText + '\n');
    } else if (!finalCurText) {
      uiStore.showToast('无法发送空消息')
    } else if (finalCurText && !e.shiftKey) {
      onSend(finalCurText, 'text');
      setCurText('');
    }
  };

  const beforeUploadHandler = (
    file: any,
    FileList: any[],
  ): boolean | Promise<void | Blob | File> => {
    const isLt100M = file.size / 1024 / 1024 < 100;
    if (!isLt100M) {
      message.error('图片或文件大小最大支持100M');
    }
    return isLt100M;
  };

  const uploadImgHandler = (file: any): any => {
    onSend(file, 'image');
  };

  const uploadFileHandler = (file: any): any => {
    onSend(file, 'file');
  };

  return (
    <div className='chatroomWrapper chat-board'>
      {/* <div className='chatroomTitle'>消息</div> */}
      <div ref={contentRef} className='chatroomContent'>
        {messages.map((item, index, array) => (
          <React.Fragment  key={item.idClient}>
            {((index - 1 >= 0 && item.time - array[index - 1].time > 5 * 60 * 100) || index === 0) && <p className='chatroomCardTime'>{moment(item.time).format('yyyy-MM-DD HH:mm:ss')}</p>}
            <CharCard content={item} onResend={onResend} />
          </React.Fragment>
        ))}
      </div>
      <div className='chatroomTools'>
        {/* <div className='chatroomToolsButton'>
          {!imageProgress ? (
            <Upload
              beforeUpload={beforeUploadHandler}
              showUploadList={false}
              accept=".jpg,.png,.jpeg"
              action={uploadImgHandler}
            >
              <Icon
                type="iconshangchuantupian"
                color="#666"
                width="20"
                height="20"
              />
            </Upload>
          ) : (
            <LoadingOutlined />
          )}
        </div>
        <div className='chatroomToolsButton'>
          {!fileProgress ? (
            <Upload
              beforeUpload={beforeUploadHandler}
              showUploadList={false}
              action={uploadFileHandler}
            >
              <Icon
                type="iconshangchuanfujian"
                color="#666"
                width="20"
                height="20"
              />
            </Upload>
          ) : (
            <LoadingOutlined />
          )}
        </div> */}
      </div>
      <div className='chatroomEditor' ref={inputWrapperRef}>
        <Input.TextArea
          style={{ resize: 'none' }}
          disabled={!canSendMsg}
          placeholder={canSendMsg ? '请输入信息，并按enter键发送' : '聊天室已禁言'}
          bordered={false}
          value={curText}
          onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => {
            setCurText(e.target.value);
          }}
          onPressEnter={pressEnterHandler}
        />
      </div>
    </div>
  );
};

export default Chatroom;
