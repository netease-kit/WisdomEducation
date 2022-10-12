import React from 'react'
import { ITrack } from './Replay'
import VideoBox from './VideoBox'

interface IProps {
    videoWidth: number
    currTime: number
    videos: Array<ITrack>
    playing: boolean
    onWait: () => any
    onCanPlay: () => any
    beginOffset: number
    /**
     * 上一次校准的播放位置
     */
    syncAt: number
    /**
     * 上一次校准时的绝对时间
     * 目前视频文件的播放位置应为 new Date().valueOf() - syncTimestamp + syncAt
     * 
     * 校准时间点为play, pause
     */
    syncTimestamp: number
    speed: number
    visibleVIds: Array<string>
    showIds: Array<string>
}

export default class VideoGroup extends React.Component<IProps> {
    waitSet: Set<string> = new Set()

    handleWait = (id: string) => {
      if (!this.waitSet.has(id)) {
        this.waitSet.add(id)
        if (this.waitSet.size === 1) {
          this.props.onWait()
        }
      }
    }

    handleCanPlay = (id: string) => {
      if (this.waitSet.has(id)) {
        this.waitSet.delete(id)
        if (this.waitSet.size === 0) {
          this.props.onCanPlay()
        }
      }
    }
    render() {
      return (
        <div className='main-video' style={{
          width: this.props.videoWidth + 20
        }}>
          {this.props.videos.filter((item)=>this.props.showIds.includes(item.id))?.map(v => {
            return (
              // <>
              //   {this.props.showIds.includes(v.id) && 
              <div
                key={v.id} 
                style={{
                  marginBottom: 10,
                }}
              >
                <VideoBox
                  playing={this.props.playing}
                  url={v.url}
                  name={v.name}
                  role={v.role}
                  hidden={!this.props.visibleVIds.includes(v.id)}
                  speed={this.props.speed}
                  syncAt={this.props.syncAt - (v.start - this.props.beginOffset)}
                  syncTimestamp={this.props.syncTimestamp}
                  onWait={() => this.handleWait(v.id)}
                  onCanPlay={() => this.handleCanPlay(v.id)}
                />
              </div>
              //   }
              // </>
            )
          })}
        </div>
      )
    }
}