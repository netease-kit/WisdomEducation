/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import EventEmit from 'events';
import Chatroom from '@/lib/chatroom/NIM_Web_Chatroom_v8.2.5'

interface InitOptions {
  nim: any;
  appKey: string;
  account: string;
  token: string;
  chatroomId: string;
  chatroomNick?: string;
  onMessage(msgs: Message[]): void,
}

interface BaseMessage {
  name: string;
  size: number;
  md5: string;
  url: string;
  ext: string;
}

interface ImageMessage extends BaseMessage {
  w: string;
  h: string;
}

interface AudioMessage extends BaseMessage {
  dur: string;
}

interface VideoMessage extends BaseMessage {
  dur: string;
  w: string;
  h: string;
}

type FileMessage = BaseMessage;

type MessageFile = ImageMessage | FileMessage | AudioMessage | VideoMessage;

export interface Progress {
  total: string;
  loaded: string;
  percentage: string;
  percentageText: string;
}

export type MessageStatus = 'success' | 'fail' | 'sending';

export type MessageType =
  | 'text'
  | 'image'
  | 'audio'
  | 'video'
  | 'file'
  | 'geo'
  | 'custom'
  | 'tip'
  | 'notification';

export interface Message {
  chatroomId: string;
  idClient: string;
  from: string;
  fromNick: string;
  fromAvatar: string;
  fromCustom: string;
  fromClientType: 'Android' | 'iOS' | 'PC' | 'Web' | 'Mac';
  type: MessageType;
  flow: 'in' | 'out';
  text: string;
  file: MessageFile;
  geo: any;
  tip: any;
  content: string;
  attach: string;
  custom: string;
  resend: boolean;
  time: number;
  isMe: boolean;
  status: MessageStatus;
  progress: Progress;
}

class ChatroomHelper extends EventEmit {
  public static instance: ChatroomHelper | undefined;
  private chatroom: any;
  private account = '';
  private isConnect = false;
  public onMessage;

  constructor({
    nim,
    appKey,
    account,
    token,
    chatroomId,
    chatroomNick,
    onMessage,
  }: InitOptions) {
    super();
    nim.getChatroomAddress({
      chatroomId,
      done: (err: any, obj: any) => {
        if (err) {
          console.log('获取聊天室地址失败: ', err);
          return;
        }
        this.chatroom = Chatroom.getInstance({
          appKey,
          account,
          token,
          chatroomId,
          chatroomNick,
          chatroomAddresses: obj.address,
          commonUpload: true,
          onconnect: () => {
            console.log('聊天室登录成功');
            this.isConnect = true;
            this.account = account;
          },
          ondisconnect: () => {
            console.log('聊天室链接断开');
            this.resetState();
          },
          onerror: (err: any) => {
            console.log('聊天室发生错误: ', err);
            this.resetState();
          },
          onmsgs: (msgs: Message[]) => {
            console.log('聊天室收到消息');
            this.parseMsgs(msgs);
          },
        });
        this.onMessage = onMessage;
      },
    });
  }

  public static getInstance(opt: InitOptions): ChatroomHelper {
    if (!this.instance) {
      this.instance = new ChatroomHelper(opt);
    }
    return this.instance;
  }

  public destroy() {
    if (this.chatroom) {
      this.chatroom.destroy({
        done: (err: any) => {
          if (err) {
            console.log('聊天室实例清除失败', err);
          } else {
            console.log('聊天室实例被清除');
          }
        },
      });
      this.chatroom = undefined;
    }
  }

  public sendText(opt: {
    idClient?: string;
    text: string;
    resend?: boolean;
    done: (err: any, msg?: Message) => void;
  }): Message {
    if (!this.chatroom || !this.isConnect) {
      console.error(this.chatroom, this.isConnect);
      throw Error('请先初始化聊天室实例并成功登录后再调用该方法');
    }
    let finalOpt: any;
    if (opt.resend) {
      finalOpt = { ...opt, flow: 'out', status: 'fail' };
    } else {
      finalOpt = opt;
    }
    return this.chatroom.sendText(finalOpt);
  }

  public sendFile(opt: {
    idClient?: string;
    type?: MessageType;
    blob?: any;
    file?: MessageFile;
    resend?: boolean;
    uploadprogress?: (progress: Progress) => void;
    uploaddone?: (err: any, obj: any) => void;
    beforesend?: (msg: Message) => void;
    done: (err: any, msg?: Message) => void;
  }): Message {
    if (!this.chatroom || !this.isConnect) {
      throw Error('请先初始化聊天室实例并成功登录后再调用该方法');
    }
    let finalOpt: any;
    if (opt.resend) {
      // sdk bug: 此处sdk文档上没写需要这些参数，应该是sdk bug
      finalOpt = { ...opt, flow: 'out', status: 'fail' };
    } else {
      finalOpt = opt;
    }
    return this.chatroom.sendFile(finalOpt);
  }

  private parseMsgs(msgs: Message[]) {
    const res: Message[] = msgs.map((item) => ({
      ...item,
      isMe: item.from === this.account,
    }));
    // this.emit('onMessage', res);
    this.onMessage(res);
  }

  private resetState() {
    this.isConnect = false;
    this.account = '';
    this.removeAllListeners();
  }
}

export default ChatroomHelper;
