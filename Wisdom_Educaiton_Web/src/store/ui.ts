/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import { EnhancedEventEmitter } from '@/lib/event';
import { observable, action, computed, makeObservable } from 'mobx'
import { message } from 'antd';
import { AppStore } from './index';

export class UIStore extends EnhancedEventEmitter {

  @observable
  _loading = false;

  appStore: AppStore;

  constructor(appStore: AppStore) {
    super();
    makeObservable(this);
    this.appStore = appStore;
  }

  @computed
  get loading(): boolean {
    return this._loading;
  }

  /**
   * @description: message提示
   * @param {string} msg
   * @param {*} type
   * @param {*} delay
   * @return {*}
   */
  public async showToast(msg: string, type: 'info'|'success'|'warn'|'error' = 'info', delay = 3): Promise<void> {
    message[type](msg, delay);
  }

  /**
   * @description: 设置页面loading是否展示
   * @param {boolean} enable
   * @return {*}
   */
  @action
  public setLoading(enable: boolean): void {
    this._loading = enable;
  }
}
