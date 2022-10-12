/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import NIM from './sdk/NIM_Web_SDK_v8.6.0.js';
import { EnhancedEventEmitter } from '../event';
import logger from '../logger';
import nim_server_conf from './nim_server_conf.json';
const needPrivate = process.env.REACT_APP_SDK_IM_PRIVATE;
needPrivate === "true" && logger.log("IM on-premises deployment configuration", nim_server_conf);

interface loginOptions {
  imAppkey: string;
  imAccid: string;
  imToken: string;
  NIMconf?: any;
}

export class NENim extends EnhancedEventEmitter {

  private _nim: any = null;
  private repeatTimes = 0;

  constructor() {
    super();
  }

  /**
   * @description: return the instance of the object
   * @param {*}
   * @return {*}
   */
  get nim(): any {
    return this._nim;
  }

  /**
   * @description: Log on to IM and get the instance
   * @param {loginOptions} options
   * @return {*}
   */
  public async loginImServer(options: loginOptions): Promise<any> {
    return new Promise((resolve, reject) => {
      // 
      if (this._nim) {
        resolve(null)
      }
      this._nim = NIM.NIM.getInstance({
        debug: false,
        db: false,
        appKey: options.imAppkey,
        account: options.imAccid,
        token: options.imToken,
        // Turn off data synchronization in IM
        syncRelations: false,
        syncFriends: false,
        syncFriendUsers: false,
        syncTeams: false,
        syncSuperTeams: false,
        syncRoamingMsgs: false,
        syncSuperTeamRoamingMsgs: false,
        privateConf: needPrivate === "true" ? nim_server_conf : {}, // On-premises deployment configuration
        ...options.NIMconf,
        onconnect: () => {
          logger.debug('IM authentication success...');
          this.emit('im-connect')
        },
        onsyncdone: () => {
          logger.debug('IM login success...')
          this.repeatTimes = 0;
          resolve(null)
        },
        ondisconnect: (error: any) => {
          logger.error('IM disconnected: ', error);
          if (this.repeatTimes <= 5) {
            setTimeout(() => {
              this.repeatTimes += 1;
              logger.error(`IM disconnected, retries: ${this.repeatTimes}`);
              this.connect();
            }, 5000);
          }
          // if (this.nim) {
          //   reporter.send({
          //     'action_name': 'login_im_failed',
          //     value: -1
          //   })
          // }
          reject(error)
        },
        onProxyMsg: this.onNotify.bind(this)
      })
    })
  }
  /**
   * @description: Notification message
   * @param {any} data
   * @return {*}
   */
  private onNotify(data: any): void{
    logger.debug('IM-receive notifications from servers')
    if (data && data.body) {
      data.body = JSON.parse(data.body)
    }
    this.emit('controlNotify', data)
  }

  /**
   * @description: request sent in pass-through mode
   * @param {string} path
   * @param {any} body
   * @return {*}
   */
  public async sendControlOrder(path: string, body?: any): Promise<any> {
    const header = {};
    return new Promise((resolve, reject) => {
      this._nim.httpRequestProxy({
        header: JSON.stringify(header),
        path,
        body: JSON.stringify(body),
        done: (e: any, a: any) => {
          logger.log('sendControlOrder, e:, a:  %t', path,  e, a)
          if (e) {
            reject(e)
          } else {
            resolve(JSON.parse(a.body))
          }
        }
      })
    })
  }

  /**
   * @description: Log out IM
   * @param {*}
   * @return {*}
   */
  public logoutImServer(): void {
    if (this._nim) {
      this._nim.disconnect()
      this._nim = null
    }
  }

  /**
   * @description: Log in
   * @param {*}
   * @return {*}
   */
  public connect(): void {
    this._nim && this._nim.connect();
  }
}
