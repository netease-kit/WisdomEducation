import React, { createRef } from 'react'
import PAUSE_BASE_64 from '../../assets/svg/pause.svg'
import PLAY_BASE_64 from '../../assets/svg/play.svg'
import './Control.less'

import {getTime} from '../../utils'

interface IProps {
  currTime: number,
  playing: boolean,
  start: number,
  speed: number
  end: number
  onPlay: () => any
  onPause: () => any
  onSetSpeed: (speed: number) => any
  onSeekTo: (time: number) => any
}

interface IStates {
  duration: number// 起止时间
  showLabel: boolean
  labelLeft: number
  labelText: string
  drag: boolean
  speedOptionShow: boolean
}

export default class Control extends React.Component<IProps, IStates> {
  toolUIEl = createRef()
  speedOptionHideTimer: any

  constructor(props: IProps) {
    super(props)
    const { start, end } = props
    this.state = {
      duration: end - start,
      showLabel: false,
      labelLeft: 0,
      labelText: '',
      speedOptionShow: false,
      drag: false
    }
  }

  onMouseMove = (e: React.MouseEvent | React.TouchEvent) => {
    const {currentX, currentTime} = this.getCurrentXAndTime(e)
    const [labelText] = getTime(currentTime)

    this.setState({
      labelLeft: currentX,
      labelText: labelText,
    })
  }

  pauseOrPlay = (e: React.MouseEvent) => {
    if (this.props.playing) {
      this.props.onPause()
    } else {
      this.props.onPlay()
    }
  }

  onMouseDown = (e: React.MouseEvent | React.TouchEvent) => {
    const role = (e.target! as HTMLDivElement).getAttribute('role')
    if (role === 'slider') {
      this.setState({
        showLabel: true,
        drag: true
      })
      this.onMouseMove(e)
    }
  }

  onMouseUp = (e: React.MouseEvent | React.TouchEvent) => {
    if (this.state.drag) {
      this.seekTo(e)
    }
    this.hidePosition(e)
  }

  handleClickSpeed = (e: React.MouseEvent) => {
    this.setState({
      speedOptionShow: !this.state.speedOptionShow
    })
  }

  handleClickSpeedOption = (ev: React.MouseEvent, speed) => {
    ev.stopPropagation()
    this.setState({
      speedOptionShow: false
    })
    this.props.onSetSpeed(speed)
  }

  showPosition = (e: React.MouseEvent) => {
    this.setState({
      showLabel: true
    })
    this.onMouseMove(e)
  }
  
  hidePosition = (e: React.MouseEvent | React.TouchEvent) => {
    this.setState({
      showLabel: false
    })
  }

  seekTo = (e: React.MouseEvent | React.TouchEvent) => {
    const { currentX, currentTime } = this.getCurrentXAndTime(e)
    this.props.onSeekTo(currentTime)
    // 如果是播放状态则先暂停后播放
    const lastPlayingStatus = this.props.playing
    if (lastPlayingStatus) {
      this.props.onPause()
    }
    const updator: any = {
      labelLeft: currentX,
      time: currentTime,
      drag: false,
    }
    // 如果是鼠标事件，不隐藏光标
    this.setState(updator)
    setTimeout(()=>{
      if(lastPlayingStatus) {
        this.props.onPlay()
      }
    })
  }

  getCurrentXAndTime = (e: React.MouseEvent | React.TouchEvent) => {
    let currentX = 0
    const left = (this.toolUIEl.current as HTMLElement).getBoundingClientRect().left
    if (e.type.search(/mouse|click/) > -1) {
      currentX = (e as React.MouseEvent).clientX - left
    } else {
      currentX = (e as React.TouchEvent).touches[0].clientX - left
    }

    const fullWidth = (this.toolUIEl.current as HTMLDivElement).offsetWidth
    // 防止越界
    if (currentX < 0) {
      currentX = 0
    }
    if (currentX > fullWidth) {
      currentX = fullWidth
    }

    const currentTime = this.state.duration * (currentX / fullWidth)

    return {
      currentX,
      currentTime
    }
  }

  renderSpeed() {
    const speedText = this.props.speed === 1 ? '倍数' : `${this.props.speed}x`
    const speedArr = [2, 1.5, 1.25, 1, 0.75, 0.5]
    return (
      <div 
        className='player-speed'
        onClick={this.handleClickSpeed}
      >
        <div className='player-speed-text'>{speedText}</div>
        {this.state.speedOptionShow && (
          <div className='player-speed-options'>
            {speedArr.map(speed => {
              return (
                <div className='option' key={speed} onClick={(ev) => this.handleClickSpeedOption(ev, speed)}>{speed}x</div>
              )
            })}
          </div>
        )}
      </div>
    )
  }

  renderTimeline = (widthPercent) => {
    const { showLabel, labelLeft, labelText, drag } = this.state

    return (
      <div
        ref={this.toolUIEl as React.RefObject<HTMLDivElement>}
        className='player-timeline'
        onClick={this.seekTo}
        onMouseEnter={this.showPosition}
        onMouseLeave={this.hidePosition}
        onMouseDown={this.onMouseDown}
        onMouseMove={this.onMouseMove}
        onTouchStart={this.onMouseDown}
        onTouchMove={this.onMouseMove}
        onTouchEnd={this.onMouseUp}
        role="timeline"
      >
        <span 
          role="currentLabel" 
          className='player-timeline-label'
          style={{
            display: showLabel ? 'inline-block' : 'none',
            left: `${labelLeft}px`
          }}
        >
          {labelText}
        </span>
        <span 
          role="label-background" 
          className='player-timeline-bg' 
          style={{
            display: showLabel ? 'inline-block' : 'none',
            height: 20,
            left: `${labelLeft}px`
          }}
        />
        <div 
          role="blue-line" 
          className='player-timeline-blueline' 
          style={{
            width: `${widthPercent + '%'}`
          }}
        />
        <div 
          role="slider" 
          className='player-timeline-slider'
          style={{
            left: `${drag ? labelLeft + 'px' : widthPercent + '%'}`,
            backgroundClip: `${drag ? 'border-box' : 'content-box'}`
          }}
        />
      </div>
    )
  }

  renderPlayOrPause = () => {
    return (
      <div
        className='player-play-icon-wrapper'
        role="icon"
        onClick={this.pauseOrPlay}
      >
        <span
          className='player-play-icon'
          style={{
            backgroundImage: `url(${(this.props.playing ? PAUSE_BASE_64 : PLAY_BASE_64)})`
          }}
        />
      </div>
    )
  }

  render() {
    const { duration } = this.state
    const time = this.props.currTime

    let widthPercent = 0
    if (time >= duration) {
      widthPercent = 100
    } else if (time <= 0) {
      widthPercent = 0
    } else {
      widthPercent = time / duration * 100
    }

    
    let timeDuration: string
    if (time > duration) {
      const [durationStr] = getTime(duration)
      timeDuration = durationStr + '/' + durationStr
    } else {
      const [durationStr, durationHour] = getTime(duration)
      const [currentStr] = getTime(time, durationHour > 0)
      timeDuration = currentStr + '/' + durationStr
    }

    return (
      <div className="player-container">
        {this.renderPlayOrPause()}
        {this.renderTimeline(widthPercent)}
        {this.renderSpeed()}
        <div role="duration" className='player-duration'>
          {timeDuration}
        </div>
      </div >
    )
  }
}
