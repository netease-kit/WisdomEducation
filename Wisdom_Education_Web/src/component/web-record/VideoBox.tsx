import React, { createRef, RefObject } from 'react'

import './VideoBox.less'

interface IProps {
    url: string
    name: string
    role: 'student' | 'teacher' | 'host' | 'audience'
    /**
     * 回放页面是否处于播放状态
     */
    playing: boolean
    /**
     * 是否因为events，或者当前时间不处于视频时间范围内，需要隐藏该视频
     */
    hidden: boolean
    speed: number
    onWait: () => any
    onCanPlay: () => any
    /**
     * 上一次校准的播放位置。位置为0时，对应着视频的起点位置
     */
    syncAt: number
    /**
     * 上一次校准时的绝对时间
     * 目前视频文件的播放位置应为 new Date().valueOf() - syncTimestamp + syncAt
     * 
     * 校准时间点为play, pause
     */
    syncTimestamp: number
    needControl?: boolean
}

const observeAt = '李四'

export default class VideoBox extends React.Component<IProps> {
    videoRef: RefObject<HTMLVideoElement> = createRef()
    state = {
      videoOn: true,
      audioOn: true,
      playing: false,
      speed: 1
    }

    componentDidMount() {
      if (this.props.playing) {
        this.play()
      }
      console.log(this.props)
      this.setPlaySpeed(this.props.speed)
    }

    componentDidUpdate(nextProps) {
      if (nextProps.playing && !this.state.playing) {
        this.play()
      } else if (!nextProps.playing && this.state.playing) {
        this.pause()
      } else if (nextProps.syncTimestamp !== this.props.syncTimestamp) {
        this.calibrate(nextProps)
      }

      if (nextProps.speed !== this.state.speed) {
        this.setPlaySpeed(nextProps.speed)
      }
    }

    play() {
      if (this.props.name.toString() === observeAt) {
        console.log('李四 play', this.props)
      }

      if (this.videoRef.current) {
        this.calibrate(this.props)
        this.videoRef.current.play()
        this.setState({
          playing: true
        })
      }
    }

    pause() {
      if (this.props.name.toString() === observeAt) {
        console.log('李四 pause', this.props)
      }

      if (this.videoRef.current) {
        this.videoRef.current.pause()
        this.setState({
          playing: false
        })
      }
    }

    /**
     * 调整播放器时间。
     * 在播放，暂停，以及syncTimestamp发生变化时调整播放器时间
     * @param props 
     */
    calibrate(props) {
      const playAt = new Date().valueOf() - props.syncTimestamp + props.syncAt

      if (this.props.name.toString() === observeAt) {
        console.log('李四 calibrate at', playAt/ 1000, this.props)
        this.printVideoState()
      }

      if (this.videoRef.current) {
        if (props.playing) {
          this.videoRef.current.play()
        }
        this.videoRef.current.currentTime = playAt / 1000
      }
    }

    setPlaySpeed(speed) {
      if (this.state.speed !== speed) {
        this.setState({
          speed
        })
        if (this.videoRef.current) {
          this.videoRef.current.playbackRate = speed
        }
      }
    }

    handleToggleVideo = () => {
      this.setState({
        videoOn: !this.state.videoOn
      })
    }

    handleToggleAudio = () => {
      this.setState({
        audioOn: !this.state.audioOn
      }, () => {
        if (this.videoRef.current) {
          this.videoRef.current.muted = this.state.audioOn ? false : true
        }
      })
    }

    handleWait = () => {
      if (this.props.name.toString() === observeAt) {
        console.log('李四 wait')
      }

      this.props.onWait()
    }

    handleCanPlay = () => {
      if (this.props.name.toString() === observeAt) {
        console.log('李四 play')
      }
      this.props.onCanPlay()
    }

    printVideoState = () => {
      if (this.videoRef.current) {
        console.log('李四 currTime', this.videoRef.current.currentTime)
        console.log('李四 paused', this.videoRef.current.paused)
        console.log('李四 ended', this.videoRef.current.ended)
        console.log('李四 readyState', this.videoRef.current.readyState)
      }
    }

    render() {
      if (this.props.name.toString() === observeAt) {
        console.log('李四 props')
      }
      const hidden = !this.state.videoOn
      const { needControl = true} = this.props;
      return (
        <div className='videobox'>
          <video
            ref={this.videoRef as RefObject<HTMLVideoElement>}
            onWaiting={this.handleWait}
            onCanPlay={this.handleCanPlay}
            src={this.props.url}
            muted={!this.state.audioOn || this.props.hidden}
            style={{
              width: '100%',
              visibility: hidden ? 'hidden' : 'visible'
            }}
          />
          {hidden && 
                    <div className='video-black' />
          }
          {needControl && this.renderControls()}
        </div>
      )
    }

    renderControls() {
      const name = this.props.name + (this.props.role  === 'teacher' ? '(教师)' : '')
      const videoClassNameSuffix = this.state.videoOn ? '' : ' off'
      const audioClassNameSuffix = this.state.audioOn ? '' : ' off'
      return (
        <div className='controls'>
          <div className='name'>{name}</div>
          <div className='ctrl'>
            <div className={'video icon' + videoClassNameSuffix} onClick={this.handleToggleVideo}></div>
            <div className={'audio icon' + audioClassNameSuffix} onClick={this.handleToggleAudio}></div>
          </div>
        </div>
      )
    }
}