/*
 * @Author: your name
 * @Date: 2021-05-12 17:26:35
 * @LastEditTime: 2021-05-12 17:46:23
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /app_wisdom_education_web/src/lib/event/index.ts
 */
import { EventEmitter } from 'events';
import logger from '../logger';


export class EnhancedEventEmitter extends EventEmitter {
  constructor() {
    super();
    this.setMaxListeners(Infinity);
  }

  safeEmit(event: string, ...args: any[]): boolean {
    const numListeners = this.listenerCount(event);

    try {
      return this.emit(event, ...args);
    }
    catch (error) {
      logger.log(
        'safeEmit() | event listener threw an error [event:%s]:',
        event, error);

      return Boolean(numListeners);
    }
  }

  async safeEmitAsPromise(event: string, ...args: any[]): Promise<any> {
    return new Promise((resolve, reject) => (
      this.safeEmit(event, ...args, resolve, reject)
    ));
  }
}
