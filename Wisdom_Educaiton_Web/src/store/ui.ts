/*
 * @Author: your name
 * @Date: 2021-05-19 16:31:17
 * @LastEditTime: 2021-06-07 11:25:52
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /app_wisdom_education_web/src/store/ui.ts
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
