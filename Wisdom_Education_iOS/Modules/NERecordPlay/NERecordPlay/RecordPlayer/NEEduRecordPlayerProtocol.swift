//
//  NEEduRecordPlayProtocol.swift
//  NERecordPlay
//
//  Created by 郭园园 on 2021/8/9.
//

import Foundation

public enum PlayState {
    case idle
    case prepared
    case playing
    case pause
    case seeking
    case finished
}

public protocol NEEduRecordPlayerDelegate: NSObjectProtocol {
    func onPrepared(playerItem:Any)
    func onPlay(player: Any)
    func onPause(player: Any)
    func onSeeked(player: Any ,time:Double, errorCode:Int)
    func onFinished(player: Any)
    func onError(player: Any, errorCode: Int)
    func onPlayTime(player: NEEduRecordPlayerProtocol, time:Double)
    func onSubStreamStart(player:NEEduRecordPlayerProtocol, videoView: UIView?)
    func onSubStreamStop(player:NEEduRecordPlayerProtocol, videoView: UIView?)
    func userEnter(item: RecordItem)
    func userLeave(item: RecordItem)
    func onResetPlayer(player: NEEduRecordPlayerProtocol)
}

public protocol NEEduRecordPlayerProtocol {
    var delegate: NEEduRecordPlayerDelegate? { get set }
    var state: PlayState { get }    
    /// 资源总时长，单位秒
    var duration: Double { get }
    
    func prepareToPlay()
    
    func play()
    
    func pause()
    /// 跳转指定时间点
    /// - Parameter time: 单位：秒
    func seekTo(time: Double)
    
    func stop()
    
    /// 静音
    func muteAudio(mute: Bool)
    
//    func replay()
}
