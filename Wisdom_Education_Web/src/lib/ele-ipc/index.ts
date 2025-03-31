import { EnhancedEventEmitter } from '@/lib/event';
import logger from '../logger';

// @ts-ignore
const { ipcRenderer, eleRemote, platform } = window;

let eleInstance: EleUIUseEvent | null;
class EleUIUseEvent extends EnhancedEventEmitter {
  constructor() {
    super();
    this.init();
  }
  public init(): void {
    if (ipcRenderer) {
      ipcRenderer.on('main-close-before', () => {
        this.emit('main-close-before')
      })
      // 注册 sendMessage 事件监听器
      ipcRenderer.on('sendMessage', async (event: any, channel: string, args: any) => {
        const result = await this.sendMessage(channel, args); // 调用实例的 sendMessage 方法
        event.returnValue = result; // 将返回值发送回主进程
      });
    }
  }
  public sendMessage(channel: string, args?: any): Promise<any> {
    return new Promise((resolve, reject) => {
      if (ipcRenderer) {
        ipcRenderer.once(`${channel}-reply`, (event: any, result: any) => {
          resolve(result);
        });
        logger.log('ipcRenderer发送消息 ',channel)
        ipcRenderer.send(channel, args);
      } else {
        reject(new Error('ipcRenderer not found'));
      }
    });
  }
  public destroy(): void {
    if (ipcRenderer) {
      ipcRenderer.removeAllListeners();
    }
  }
}

export default {
  getInstance(): EleUIUseEvent {
    if (!eleInstance) {
      eleInstance = new EleUIUseEvent();
    }
    return eleInstance;
  },
  destroy(): void {
    eleInstance?.destroy();
    eleInstance = null;
  }
}
