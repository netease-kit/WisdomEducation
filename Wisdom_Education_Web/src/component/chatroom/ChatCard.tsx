/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React from 'react';
import { Button, Image } from 'antd';
import { ExclamationCircleFilled, LoadingOutlined } from '@ant-design/icons';
import moment from 'moment';
import { Message, MessageType } from './chatroomHelper';
import { parseFileSize, addUrlSearch, matchExt } from '@/utils';
import Icon from '@/component/icon';
import { useRoomStore } from '@/hooks/store';
import { RoleTypes } from '@/config';

import './index.less';

const fileIconMap = {
  pdf: 'iconPDF',
  word: 'iconWord',
  excel: 'iconExcel',
  ppt: 'iconPPT',
  zip: 'iconyasuobao',
};

interface IProps {
  content: Message;
  onResend: (msg: Message, type: MessageType) => void;
}

const ChatCard: React.FC<IProps> = ({ content, onResend }) => {
  const roomStore = useRoomStore();
  const { localData } = roomStore;
  const { isMe, fromNick, time, status } = content;
  const textBgColor: React.CSSProperties = {
    backgroundColor: isMe ? 'rgba(69, 116, 252, 1)' : '#333B45',
    color: isMe ? '#fff' : '#B4BFD0',
  };

  const renderSendStatus = (resend: () => void) => {
    if (!isMe || status === 'success') {
      return null;
    }
    return (
      <div style={{ marginRight: 12 }}>
        {status === 'sending' ? (
          <LoadingOutlined style={{ color: '#14a9fb' }} />
        ) : status === 'fail' ? (
          <Button
            type="ghost"
            danger
            style={{ border: 'none', width: 'auto', height: 'auto' }}
            onClick={resend}
            icon={<ExclamationCircleFilled />}
          ></Button>
        ) : null}
      </div>
    );
  };

  const renderText = (text: string) => {
    return (
      <div className='chatCardContent'>
        {renderSendStatus(() => {
          onResend(content, 'text');
        })}
        <div className='chatCardText' style={textBgColor}>
          {text.split(/\s/).map((item, index) => (
            <React.Fragment key={item + index}>
              {index !== 0 && <span>&nbsp;</span>}
              {/^https?:\/\//.test(item) ? (
                <a
                  className='chatCardTextItem'
                  href={item}
                  target="_blank" rel="noreferrer"
                >
                  {item}
                </a>
              ) : (
                <span className='chatCardTextItem'>{item}</span>
              )}
            </React.Fragment>
          ))}
        </div>
      </div>
    );
  };

  const renderImage = (data: Message['file']) => {
    return (
      <div className='chatCardContent'>
        {renderSendStatus(() => {
          onResend(content, 'image');
        })}
        <Image className='chatCardImg' src={data.url} />
      </div>
    );
  };

  const renderFile = (data: Message['file']) => {
    return (
      <div className='chatCardFillContent'>
        {renderSendStatus(() => {
          onResend(content, 'file');
        })}
        <a
          className='chatCardFile'
          href={addUrlSearch(data.url, `download=${data.name}`)}
          target="_blank" rel="noreferrer"
        >
          <Icon
            type={fileIconMap[matchExt(data.ext)] || 'iconqitawenjian1'}
            width="32"
            height="32"
          />
          <div className='chatCardFileContent'>
            <div className='chatCardFileName'>{data.name}</div>
            <div className='chatCardFileSize'>
              {parseFileSize(data.size)}
            </div>
          </div>
          {status === 'sending' ? (
            <LoadingOutlined />
          ) : (
            <Icon type="iconxiazai" width="16" height="16" />
          )}
        </a>
      </div>
    );
  };

  const renderNoSupport = () => {
    return (
      // <div className='chatCardText' style={textBgColor}>
      //   不支持该消息
      // </div>
      <></>
    );
  };

  return (
    <div
      className='chatCardWrapper'
      style={{ alignItems: isMe ? 'flex-end' : 'flex-start' }}
    >
      <div>
        {isMe ? (
          <>
            {/* <span
              className='chatCardSendTime'
              style={{ marginRight: 8 }}
            >
              {moment(time).format('HH:mm:ss')}
            </span> */}
            <span className='chatCardNickName'>{fromNick}</span>
          </>
        ) : (
          <>
            <span
              className='chatCardNickName'
              style={{ marginRight: 8 }}
            >
              {fromNick}
            </span>
            {/* <span className='chatCardSendTime'>
              {moment(time).format('HH:mm:ss')}
            </span> */}
          </>
        )}
      </div>
      {content.type === 'text'
        ? renderText(content.text)
        : content.type === 'image'
          ? renderImage(content.file)
          : content.type === 'file'
            ? renderFile(content.file)
            : renderNoSupport()}
    </div>
  );
};

export default ChatCard;
