/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */


import { observable, computed, action, makeObservable } from 'mobx';
import { AppStore } from './index';
import { NeWhiteBoard, WhiteBoardInitOptions, WhiteBoardSetEnableOtions, WhiteJoinRoomOtions } from '@/lib/whiteboard';
import logger from '@/lib/logger';



export class WhiteBoardStore {
  public appStore: AppStore;
  public whiteboard: NeWhiteBoard;

  @observable
  drawPlugin: any = null;

  @observable
  toolCollection: any = null;

  @observable
  wbInstance: any = null;

  @observable
  _wbSetFinish = false;

  constructor(appStore: AppStore) {
    makeObservable(this);
    this.appStore = appStore;
    this.whiteboard = new NeWhiteBoard();
  }

  @computed
  get wbSetFinish(): boolean {
    return this._wbSetFinish;
  }

  @action
  public setWbSetFinish(val: boolean): void {
    this._wbSetFinish = val;
  }

  /**
   * @description: 初始化登录白板
   * @param {WhiteBoardInitOptions} options
   * @return {*}
   */
  @action
  public async initWhiteBoard(options: WhiteBoardInitOptions): Promise<void> {
    try {
      await this.whiteboard.initWhiteboard(options)
      this.wbInstance = this.whiteboard.whiteboard;
    } catch (error) {
      logger.error('白板初始化错误', error)
    }
  }

  /**
   * @description: 加入白板
   * @param {WhiteJoinRoomOtions} options
   * @return {*}
   */
  @action
  public async joinRoom(options: WhiteJoinRoomOtions): Promise<void> {
    if (!this.wbInstance) {
      logger.error('白板尚未初始化');
      return;
    }
    try {
      await this.whiteboard.joinRoom(options);
      this.drawPlugin = this.whiteboard.drawPlugin;
    } catch (error) {
      logger.error('加入白板错误', error);
    }
  }

  /**
   * @description: 设置白板操作权限
   * @param {boolean} enbale
   * @param {WhiteBoardSetEnableOtions} options
   * @return {*}
   */
  @action
  public async setEnableDraw(enbale: boolean, options?: WhiteBoardSetEnableOtions): Promise<void> {
    if (!this.drawPlugin) {
      logger.log('白板尚未登录');
      return;
    }
    logger.log('设置白板-store', enbale);
    await this.whiteboard.setEnableDraw(enbale, options);
  }

  /**
   * @description: 设置渲染dom
   * @param {HTMLElement} dom
   * @return {*}
   */
  @action
  public async setContainer(dom: HTMLElement): Promise<void> {
    if (!this.drawPlugin) {
      logger.log('白板尚未登录');
      return;
    }
    await this.whiteboard.setContainer(dom);
    logger.log('设置白板Container');
  }

  /**
   * @description: 设置工具栏dom
   * @param {HTMLElement} dom
   * @return {*}
   */
  @action
  public async setToolCollection(dom?: HTMLElement): Promise<void> {
    if (!this.drawPlugin) {
      logger.log('白板尚未登录');
      return;
    }
    await this.whiteboard.setToolCollection(dom);
    this.toolCollection = this.whiteboard.toolCollection;
    logger.log('设置白板工具栏');
  }

  /**
   * @description: 获取白板流
   * @param {*}
   * @return {*}
   */
  @action
  public async getCanvasTrack(): Promise<any> {
    return await this.whiteboard.getCanvasTrack();
  }


  /**
   * @description: 更新白板流参数
   * @param {*}
   * @return {*}
   */
  @action
  public updateCanvasStream(opt: {
    width?: number,
    keepDPI?: boolean
  }): void {
    this.whiteboard.updateCanvasStream(opt);
  }

  /**
   * @description: 白板销毁
   * @param {*}
   * @return {*}
   */
  @action
  public destroy(): void {
    this.whiteboard && this.whiteboard.destroy();
    this.toolCollection && this.toolCollection.destroy();
    this.toolCollection = null;
    this.drawPlugin = null;
    this.wbInstance = null;
    this.setWbSetFinish(false)
    logger.log('白板销毁');
  }
}
