/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import WhiteBoard from './sdk/WhiteBoardSDK_v3.4.1.js';
import ToolCollection from './sdk/ToolCollection_v3.4.1.js';
import { EnhancedEventEmitter } from '../event';
import logger from '../logger';

export interface WhiteBoardInitOptions {
  appKey: string;
  // account: string;
  // token: string;
  uid: number;
  container: HTMLDivElement;
  nickname?: string;
  checksum: string
  nonce: string;
  curTime: number;
}

export interface WhiteJoinRoomOtions {
  channel: string|number;
}

export type WhiteBoardSetEnableOtions = {
  [postion: string]: {
    visible: boolean;
    exclude?: Array<string>;
  };
};

export class NeWhiteBoard extends EnhancedEventEmitter {
  private _whiteboard: any = null;
  private _drawPlugin: any = null;
  private _container?: HTMLDivElement;
  private _toolCollection: any = null;
  static initWhiteboard: (options: WhiteBoardInitOptions) => Promise<void>;

  constructor() {
    super()
  }

  get whiteboard(): any {
    return this._whiteboard;
  }

  get drawPlugin(): any {
    return this._drawPlugin;
  }

  get toolCollection(): any {
    return this._toolCollection;
  }

  /**
   * @description: 白板初始化
   * @param {WhiteBoardInitOptions} options
   * @return {*}
   */
  public async initWhiteboard(options: WhiteBoardInitOptions): Promise<void> {
    if (this._whiteboard) {
      return this._whiteboard;
    }
    this._whiteboard = await WhiteBoard.getInstance({
      appKey: options.appKey,
      nickname: options.nickname,   //非必须
      uid: options.uid,
      container: options.container,
      platform: 'web',
      record: false,
      getAuthInfo: async () => ({
        checksum: options.checksum,       //sha1(appsecret + nonce + curTime)
        nonce: options.nonce,             //随机长度小于128位的字符串
        curTime: options.curTime,         //当前UTC时间戳，从1970年1月1日0点0分0秒开始到现在的秒数
      })
    });
    this._container = options.container;
    logger.log('初始化白板完成');
    return this._whiteboard;
  }

  /**
   * @description: 白板加入
   * @param {WhiteJoinRoomOtions} options
   * @return {*}
   */
  public async joinRoom(options: WhiteJoinRoomOtions): Promise<void> {
    if (!this._whiteboard) return;
    return new Promise((resolve, reject) => {
      this._whiteboard.joinRoom({
        channel: options.channel,
        createRoom: true,
      }, {
        ondisconnected: () => {
          WhiteBoard.hideToast();
        }
      }).then((drawPlugin) => {
        logger.log('白板加入成功');
        this._drawPlugin = drawPlugin;
        // @ts-ignore
        window.drawPlugin = drawPlugin
        resolve()
      }).catch((err) => {
        logger.error('加入白板失败', err, options.channel);
        reject(err);
      })
    })
  }

  public async setToolCollection(dom?: HTMLElement): Promise<void> {
    if (!this._drawPlugin) {
      logger.log('')
      throw new Error('not init before');
    }
    this._toolCollection = ToolCollection.getInstance({
      /**
      * 工具栏容器。应该和白板容器一致
      */
      container: dom || this._container,
      handler: this._drawPlugin,
      options: {
        platform: 'web'
      }
    })
    this._toolCollection.show();
  }

  /**
   * @description: 设置白板权限
   * @param {boolean} enable
   * @param {WhiteBoardSetEnableOtions} options
   * @return {*}
   */
  public async setEnableDraw(enable: boolean, options?: WhiteBoardSetEnableOtions): Promise<void> {
    if (!this._drawPlugin || !this._toolCollection) {
      logger.log('')
      throw new Error('not init before');
    }
    await this._drawPlugin.enableDraw(enable);
    await this._toolCollection.setVisibility({
      bottomRight: {
        visible: enable
      },
      topRight: {
        visible: enable
      },
      left: {
        visible: enable,
        exclude: enable ? ['image', 'exportImage', 'opacity'] : []
      },
      bottomLeft: {
        visible: true,
        // exclude: ["fitToContentDoc", "zoomOut", "zoomLevel", "zoomIn"]
      },
      ...options,
    });
    logger.log('设置白板权限成功', enable, options)
    return;
  }

  /**
   * @description: 动态设置渲染的dom
   * @param {HTMLElement} dom
   * @return {*}
   */
  public async setContainer(dom: HTMLElement): Promise<void> {
    if (!this._drawPlugin) {
      throw new Error('not init before');
    }
    await this._drawPlugin.setContainer(dom);
    logger.debug('设置白板dom成功')
    return;
  }

  /**
   * @description: 白板销毁
   * @param {*}
   * @return {*}
   */
  public destroy(): void {
    if (this._whiteboard) {
      this._whiteboard.destroy();
      this._whiteboard = null;
    }
    WhiteBoard.hideToast();
  }
}
