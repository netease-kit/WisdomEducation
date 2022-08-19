import React from 'react'
import RecordPlayer from 'RecordPlayer'
import TickTick from '../../utils/TickTick'

import Control from './Control'

import './Replay.less'
import VideoGroup from './VideoGroup'
import VideoBox from './VideoBox'

enum SceneTypes {
    ONE_TO_ONE = "EDU.1V1",
    SMALL = "EDU.SMALL",
    BIG = "EDU.BIG",
}
interface IProps {
    config: {
        videoWidth: number
    }
    store : {
        videoTracks: Array<ITrack>
        wbTracks: Array<ITrack>
        events: Array<IEvent>
        screenTracks: Array<ITrack>
        record: {
            classBeginTimestamp: number
            classEndTimestamp: number
            recordId: string
            roomCid: string
            roomUuid: string
            startTime: number
        }
        sceneType: SceneTypes
    }
}

interface IState {
    playing: boolean,
    /**
     * 单位ms，对应控制条上时间。起始为0，对应的为tracks中最小的start。
     */
    currTime: number,
    /**
     * 等于tracks中最小的start
     */
    beginAt: number,
    endAt: number,
    speed: number,
    wait: boolean,
    /**
     * 和currTime单位量纲相同。videobox根据syncAt, syncTimestamp, 以及当前时间确定是否同步
     */
    syncAt: number,
    syncTimestamp: number
}

export interface ITrack {
    id: string
    userId: string,
    name: string,
    role: 'student' | 'teacher' | 'host' | 'audience'
    url: string,
    type: 'video' | 'whiteboard' | 'screen'
    start: number,
    end: number,
    payload?: any,
    subStream?: boolean
}

export interface IEvent {
    userId: string
    action: 'show' | 'hide' | 'showScreen' | 'remove',
    timestamp: number,
    payload?: any
}

export default class Replay extends React.Component<IProps, IState> {
    player: any
    ticktick: TickTick | undefined
    /**
     * 白板的起始时间，seekTo的时候要减去这个时间。因为白板seekTo 0对应的位置为整体播放时的wbBeginOffset的位置
     */
    wbBegin = 0
    wbEnd = 0

    constructor(props) {
      super(props)

      const store = props.store
      // const beginAt = store.wbTracks.concat(store.videoTracks).reduce((prev, track) => {
      //     return Math.min(prev, track.start)
      // }, Number.POSITIVE_INFINITY)
      const beginAt = store.record.startTime;
      const endAt = store.videoTracks.reduce((prev, track) => {
        return Math.max(prev, track.end)
      }, Number.NEGATIVE_INFINITY)

      if (store.wbTracks.length > 0) {
        // this.wbBegin = store.wbTracks[0].start - beginAt
        // this.wbEnd = store.wbTracks[0].end - beginAt
        this.wbBegin = beginAt;
        this.wbEnd = endAt;
      }

      store.videoTracks.forEach((t, index) => {
        console.log('视频开始时间', index, t.start - beginAt)
        console.log('视频结束时间', index, t.end - beginAt)
      })
      console.log('白板开始时间', this.wbBegin)
      console.log('白板结束时间', this.wbEnd)

      console.log('开始和结束时间', beginAt, endAt);
        

      this.state = {
        playing: false,
        currTime: 0,
        beginAt: beginAt,
        endAt: endAt,
        syncAt: 0,
        syncTimestamp: new Date().valueOf(),
        speed: 1,
        wait: false
      }
    }

    componentDidMount() {
      const store = this.props.store
      const wbUrls = store.wbTracks.map(t => {
        return t.url
      })

      console.log("RecordPlayer", RecordPlayer);
      RecordPlayer.getInstance({
        whiteboardParams: {
          urlArr: wbUrls,
          container: document.getElementById('whiteboard-container')
        }
      }).then(({player}) => {
        this.player = player
        this.ticktick = new TickTick()
        this.ticktick.on('tick', (time) => {
          this.player.seekTo(time)
          this.setState({
            currTime: time
          })
          if (time > this.state.endAt - this.state.beginAt) {
            this.handlePause();
          }
        })
        player.setTimeRange(this.state.beginAt)
      })
    }

    componentWillUnmount() {
      this.ticktick?.destory()
    }

    /**
     * 获取时间time时，正在播放，且根据各种事件，应该被播放的视频文件
     * @param time 
     */
    getVideosOfMoment(time: number, isScreen = false) {
      const store = this.props.store
      const arr = isScreen ? store.screenTracks : store.videoTracks;
      const visibleVIds = arr.filter(t => {
        const inRange = (time >= t.start) && (time <= t.end)
        if (!inRange) {
          return false
        } else {
          //看看是否因为进出事件，导致视频不应该播放
          const events = store.events.filter(ev => ev.userId == t.userId && ev.timestamp <= time && (isScreen ? t.subStream : !t.subStream))
          if (events.length > 0 && isScreen) {
            return events[events.length - 1].action === (isScreen ? 'showScreen' :'show')
          } else {
            return isScreen ? false : true;
          }
        }
      }).map(t => t.id)
      return visibleVIds
    }

    /**
     * 获取时间time时，正在播放，且根据各种事件，应该被播放的视频文件
     * @param time 
     */
    getShowVideosOfMoment(time: number) {
      const store = this.props.store
      const arr = store.videoTracks;
      const visibleVIds = arr.filter(t => {
        const inRange = (time >= t.start) && (time <= t.end)
        if (!inRange) {
          return SceneTypes.BIG === store.sceneType && t.role === 'host'
        } else {
          //看看是否因为进出事件，导致视频不应该播放
          const events = store.events.filter(ev => ev.userId == t.userId && ev.timestamp <= time)
          if (SceneTypes.BIG === store.sceneType && t.role === 'host') {
            return true
          } else if (SceneTypes.BIG !== store.sceneType) {
            return true;
          } else if (events.length > 0) {
            // 判断最近的操作是展示还是离开
            const tempEvents = events.concat([])
            tempEvents.reverse()
            const item = tempEvents.find((item)=>["show", "remove"].includes(item.action))
            return item?.action !== 'remove'
          } else {
            return false;
          }
        }
      }).map(t => t.id)
      return visibleVIds
    }

    handlePlay = () => {
      this.setState({
        playing: true,
        syncAt: this.state.currTime,
        syncTimestamp: new Date().valueOf()
      }, () => {
        if (this.checkSecond(this.state.currTime) >= this.checkSecond(this.state.endAt - this.state.beginAt)) {
          this.ticktick?.setCurrentTime(0)
          this.setState({
            currTime: 0,
            syncAt: 0,
            syncTimestamp: new Date().valueOf()
          })
        }
      })
      this.ticktick?.play()
    }

    handlePause = () => {
      this.setState({
        playing: false,
        syncAt: this.state.currTime,
        syncTimestamp: new Date().valueOf()
      })
      console.log('handlePause time', this.state.currTime);
        
      this.ticktick?.pause()
    }

    handleSetSpeed = (speed: number) => {
      this.setState({
        speed: speed
      })
      if (this.ticktick) {
        this.ticktick.rate = speed
      }
    }

    handleSeekTo = (time: number) => {
      if (this.ticktick) {
        this.ticktick.setCurrentTime(time)
      }
      this.setState({
        currTime: time,
        syncAt: time,
        syncTimestamp: new Date().valueOf()
      })
    }

    /**
     * 视频文件缓冲中
     */
    handleWait = () => {
      this.setState({
        wait: true
      })
      this.ticktick?.pause()
    }

    /**
     * 缓冲数据加载了一部分。如果目前正在等待缓冲数据，且处于播放状态，则改为播放
     */
    handleCanPlay = () => {
      if (this.state.wait === true) {
        this.setState({
          wait: false
        })
        if (this.state.playing) {
          this.ticktick?.play()
        }
      }
    }

    checkSecond(num) {
      return Math.floor(num / 1000);
    }

    render() {
      return (
        <div className='replay-div'>
          <div className='main'>
            {this.renderWhiteboard()}
            {this.renderScreenGroup()}
            {this.renderVideoGroup()}
          </div>
          {this.renderControls()}
        </div>
      )
    }

    renderWhiteboard() {
      return (
        <div className='main-wb' id='whiteboard-container' style={{
          width: `calc(100% - ${this.props.config.videoWidth + 20}px)`
        }}/>
      )
    }

    renderVideoGroup() {
      const visibleVIds = this.getVideosOfMoment(this.state.currTime + this.state.beginAt)
      const showIds = this.getShowVideosOfMoment(this.state.currTime + this.state.beginAt)
      return <VideoGroup
        beginOffset={this.state.beginAt}
        videoWidth={this.props.config.videoWidth}
        videos={this.props.store.videoTracks}
        currTime={this.state.currTime}
        playing={this.state.playing}
        onWait={this.handleWait}
        onCanPlay={this.handleCanPlay}
        syncAt={this.state.syncAt}
        syncTimestamp={this.state.syncTimestamp}
        speed={this.state.speed}
        visibleVIds={visibleVIds}
        showIds={showIds}
      />
    }

    renderScreenGroup() {
      const visibleVIds = this.getVideosOfMoment(this.state.currTime + this.state.beginAt, true)
      // 播放第一个匹配共享
      const screenItem = this.props.store.screenTracks.find((item)=>visibleVIds.includes(item.id))
      // 学生屏幕共享流是在上台后才开始录制的，需要加上这段时间
      const timestamp = screenItem?.start ? (this.state.syncTimestamp + (screenItem?.start - this.state.beginAt)) : this.state.syncTimestamp
      return screenItem?.url && <div className="screen-share" style={{
        width: `calc(100% - ${this.props.config.videoWidth + 20}px)`
      }}>
        <VideoBox
          playing={this.state.playing}
          url={screenItem.url}
          name={screenItem.name}
          role={screenItem.role}
          hidden={false}
          speed={this.state.speed}
          syncAt={this.state.syncAt}
          syncTimestamp={timestamp}
          onWait={this.handleWait}
          onCanPlay={this.handleCanPlay}
          needControl={false}
        />
      </div>
    }

    renderControls() {
      return (
        <div className='footer'>
          <Control 
            playing={this.state.playing}
            currTime={this.state.currTime}
            speed={this.state.speed}
            start={this.state.beginAt}
            end={this.state.endAt}
            onPlay={this.handlePlay}
            onPause={this.handlePause}
            onSetSpeed={this.handleSetSpeed}
            onSeekTo={this.handleSeekTo}
          />
        </div>
      )
    }
}