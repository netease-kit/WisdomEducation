/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import WhiteBoard from 'WhiteBoard';
import ToolCollection from 'ToolCollection';
import { EnhancedEventEmitter } from '../event';
import logger from '../logger';
import wb_server_conf from "./wb_server_conf.json";
import intl from 'react-intl-universal';

const needPrivate = process.env.REACT_APP_SDK_WB_PRIVATE;
needPrivate === "true" && logger.log("WB on-premises deployment configuration", wb_server_conf);

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
  private hasTransDoc = false;
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
   * @description: Whiteboard initialization
   * @param {WhiteBoardInitOptions} options
   * @return {*}
   */
  public async initWhiteboard(options: WhiteBoardInitOptions): Promise<void> {
    if (this._whiteboard) {
      return this._whiteboard;
    }
    this._whiteboard = await WhiteBoard.getInstance({
      appKey: options.appKey,
      nickname: options.nickname,   //Optional
      uid: options.uid,
      container: options.container,
      platform: 'web',
      record: false,
      privateConf: needPrivate === "true" ? wb_server_conf : {}, // On-premises deployment configuration
      getAuthInfo: async () => ({
        checksum: options.checksum,       //SHA1(appsecret + nonce + curTime)
        nonce: options.nonce,             //A string in a random length less than 128 characters
        curTime: options.curTime,         //The current timestamp in UTC represent the number of seconds since January 1, 1970, 00:00:00
      })
    });
    this._container = options.container;
    logger.log('Whiteboard initialization is completed');
    return this._whiteboard;
  }

  /**
   * @description: Join a whiteboard
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
        logger.log('Joined a whiteboard');
        this._drawPlugin = drawPlugin;
        // @ts-ignore
        window.drawPlugin = drawPlugin
        resolve()
      }).catch((err) => {
        logger.error('Failed to join a whiteboard', err, options.channel);
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
      * Toolbar container, consistent with the whiteboard container
      */
      container: dom || this._container,
      handler: this._drawPlugin,
      options: {
        platform: 'web'
      }
    })
    this._toolCollection.show();
    this._drawPlugin.on('event:appState:change', (name, value) => {
      if (name === 'board') {
        if (!this.hasTransDoc && this._drawPlugin.hasTransDoc()) {
          this.hasTransDoc = true
          this.toolCollection?.addOrSetContainer({
            position: 'bottomRight',
            items: [
              {
                tool: 'prevPage',
                hint: intl.get('上一页')
              },
              {
                tool: 'prevAnim',
                hint: intl.get('上一步')
              },
              {
                tool: 'pageInfo'
              },
              {
                tool: 'nextAnim',
                hint: intl.get('下一步')
              },
              {
                tool: 'nextPage',
                hint: intl.get('下一页')
              },
              {
                tool: 'preview',
                hint: intl.get('预览'),
                previewSliderPosition: 'right'
              }
            ]
          })
        } else if (this.hasTransDoc && !this._drawPlugin.hasTransDoc()) {
          this.hasTransDoc = false
          this.toolCollection?.addOrSetContainer({
            position: 'bottomRight',
            items: [
              {
                tool: 'firstPage',
                hint: intl.get('第一页')
              },
              {
                tool: 'prevPage',
                hint: intl.get('上一页')
              },
              {
                tool: 'pageInfo'
              },
              {
                tool: 'nextPage',
                hint: intl.get('下一页')
              },
              {
                tool: 'lastPage',
                hint: intl.get('最后一页')
              },
              {
                tool: 'preview',
                hint: intl.get('预览'),
                previewSliderPosition: 'right'
              }
            ]
          })
        }
      }
    })
  }

  /**
   * @description: Set whiteboard permissions
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
    logger.log('Whiteboard permissions enabled', enable, options)
    return;
  }

  /**
   * @description: Dynamically applied rendering DOM
   * @param {HTMLElement} dom
   * @return {*}
   */
  public async setContainer(dom: HTMLElement): Promise<void> {
    if (!this._drawPlugin) {
      throw new Error('not init before');
    }
    await this._drawPlugin.setContainer(dom);
    logger.debug('Set the whiteboard DOM')
    return;
  }

  /**
   * @description: Get the whiteboard stream
   * @param {*}
   * @return {*}
   */
  public async getCanvasTrack(): Promise<void> {
    if (this.drawPlugin) {
      const stream = this.drawPlugin.getStream()
      const tracks = stream.getVideoTracks()
      logger.log('Get the whiteboard substream', tracks);
      return tracks[0]
    }
  }

  /**
   * @description: Update the whiteboard stream
   * @param {opt}
   * @return {*}
   */
  public async updateCanvasStream(opt: {
    width?: number,
    keepDPI?: boolean
  }): Promise<void> {
    if (this.drawPlugin) {
      logger.log('Update the whiteboard stream', opt);
      this.drawPlugin.getStream(opt)
    }
  }

  /**
   * @description: Destroy a whiteboard
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
