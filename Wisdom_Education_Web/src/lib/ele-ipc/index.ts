import { EnhancedEventEmitter } from '@/lib/event';

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
    }
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
