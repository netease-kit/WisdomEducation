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
   * @description: initialize a whiteboard instance
   * @param {WhiteBoardInitOptions} options
   * @return {*}
   */
  @action
  public async initWhiteBoard(options: WhiteBoardInitOptions): Promise<void> {
    try {
      await this.whiteboard.initWhiteboard(options)
      this.wbInstance = this.whiteboard.whiteboard;
    } catch (error) {
      logger.error('An error occurred while initializing a whiteboard instance', error)
    }
  }

  /**
   * @description:Join a whiteboard room
   * @param {WhiteJoinRoomOtions} options
   * @return {*}
   */
  @action
  public async joinRoom(options: WhiteJoinRoomOtions): Promise<void> {
    if (!this.wbInstance) {
      logger.error('The whiteboard instance is not initialized');
      return;
    }
    try {
      await this.whiteboard.joinRoom(options);
      this.drawPlugin = this.whiteboard.drawPlugin;
    } catch (error) {
      logger.error('An error occurred while joining the whiteboard room', error);
    }
  }

  /**
   * @description: Set whiteboard permissions
   * @param {boolean} enbale
   * @param {WhiteBoardSetEnableOtions} options
   * @return {*}
   */
  @action
  public async setEnableDraw(enbale: boolean, options?: WhiteBoardSetEnableOtions): Promise<void> {
    if (!this.drawPlugin) {
      logger.log('You have not logged on to the whiteboard');
      return;
    }
    logger.log('Set whiteboard-store', enbale);
    await this.whiteboard.setEnableDraw(enbale, options);
  }

  /**
   * @description: Set rendering DOM
   * @param {HTMLElement} dom
   * @return {*}
   */
  @action
  public async setContainer(dom: HTMLElement): Promise<void> {
    if (!this.drawPlugin) {
      logger.log('You have not logged on to the whiteboard');
      return;
    }
    await this.whiteboard.setContainer(dom);
    logger.log('Set the whiteboard container');
  }

  /**
   * @description: Set the toolbar DOM
   * @param {HTMLElement} dom
   * @return {*}
   */
  @action
  public async setToolCollection(dom?: HTMLElement): Promise<void> {
    if (!this.drawPlugin) {
      logger.log('You have not logged on to the whiteboard');
      return;
    }
    await this.whiteboard.setToolCollection(dom);
    this.toolCollection = this.whiteboard.toolCollection;
    logger.log('Set the whiteboard toolbar');
  }

  /**
   * @description: Get a whiteboard stream
   * @param {*}
   * @return {*}
   */
  @action
  public async getCanvasTrack(): Promise<any> {
    return await this.whiteboard.getCanvasTrack();
  }


  /**
   * @description: Update parameters of whiteboard streams
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
   * @description: Destroy a whiteboard instance
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
    logger.log('The whiteboard is destroyed');
  }
}
