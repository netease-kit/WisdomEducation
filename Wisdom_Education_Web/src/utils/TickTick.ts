/**
 * 滴答滴答报时器。
 * 
 * 处于play状态时，每隔一段时间会向外发送tick事件。
 */

import { EventEmitter } from "eventemitter3";

interface TickTickEvent {
  tick: [number]
  pause: [],
  play: []
}

export const DEFAULT_INTERVAL = 10

export default class TickTick extends EventEmitter<TickTickEvent> {
  timer?: ReturnType<typeof setInterval>;
  state: 'pause' | 'play' = 'pause'
  //currTimeStamp是相对于最早Track的起始时间偏移量
  currTimeStamp = 0;
  //lastRealTime是上一次记录的真实时间
  lastRealTime: number | undefined = undefined
  rate = 1

  destory() {
    this.pause()
    this.eventNames().forEach(name => this.off(name))
  }

  microTask = () => {
    if (this.state === 'play') {
      this.tick()
    }
  }

  private tick() {
    const now = Date.now();
    if (this.lastRealTime === undefined) {
      this.lastRealTime = now
      return
    } else {
      const passedGap = ((now - this.lastRealTime) * this.rate)
      this.currTimeStamp = this.currTimeStamp + passedGap;
      this.lastRealTime = now
      this.emit('tick', this.currTimeStamp)
    }
  }

  pause() {
    if (this.timer) {
      window.clearInterval(this.timer)
    }
    this.timer = undefined
    this.lastRealTime = undefined

    this.state = 'pause';
    this.emit('pause')
  }

  play() {
    if (!this.timer) {
      this.timer = setInterval(this.microTask, DEFAULT_INTERVAL)
    }
    this.state = 'play'
    this.emit('play')
  }

  setCurrentTime(timeStamp = 0) {
    this.currTimeStamp = timeStamp
    this.lastRealTime = undefined
    this.emit('tick', this.currTimeStamp)
  }
}