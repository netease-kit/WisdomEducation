//
//  NEEduRecordPlayItem.swift
//  NERecordPlay
//
//  Created by 郭园园 on 2021/8/9.
//

import Foundation
import NELivePlayerFramework

public class NEEduRecordPlayer: NEEduRecordPlayerProtocol {
    
//    public var asTimeline = false
    public weak var delegate: NEEduRecordPlayerDelegate?
    public var currentTime: Double {
        get {
            return player.currentPlaybackTime()
        }
    }
    public var autoPlay: Bool {
        get {
            player.shouldAutoplay
        }
        set {
            player.shouldAutoplay = newValue
        }
    }
    var player: NELivePlayerController
    var first = true
    var url: String
    public var view: UIView? {
        get {
            return player.view
        }
    }
    public var duration: Double {
        return player.duration
    }
    public var state: PlayState = .idle
    
    /// startOffSet = 该视频开始起始时间 - 课堂开始时间，单位:秒
    public var startOffSet: Double = 0.0
    public var seekToTime: Double = 0
    
    init(url: String)throws {
        self.url = url
//        player = try NELivePlayerController.init(contentURL: URL(string: url))
        player = NELivePlayerController(contentURL: URL(string: url)!, error: nil)
        addNotification()
    }
    
    func addNotification() {
        NotificationCenter.default.addObserver(self, selector: #selector(onPreparedToPlay), name: NSNotification.Name.NELivePlayerDidPreparedToPlay, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(onFirstVideoDisplay), name: NSNotification.Name.NELivePlayerFirstVideoDisplayed, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(onPlayStateChange), name: NSNotification.Name.NELivePlayerPlaybackStateChanged, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(onSeekCompleted), name: NSNotification.Name.NELivePlayerMoviePlayerSeekCompleted, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(onPlayFinished), name: NSNotification.Name.NELivePlayerPlaybackFinished, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(onReleased), name: NSNotification.Name.NELivePlayerReleaseSueecss, object: nil)
    }
    
    func removeNotification() {
        NotificationCenter.default.removeObserver(self)
    }
    
//    func addProgressListen() {
//        player.setPlaybackTimeListenerWithIntervalMS(1000) { [unowned self] interval in
//            self.delegate?.onPlayTime(player: self, time: interval)
//            print("11播放器:\(self.player) asTimeline:\(self.asTimeline) 播放时间：\(self.player.currentPlaybackTime())")
//        }
//    }
//    func removeProgressListen() {
//        player.setPlaybackTimeListenerWithIntervalMS(0, callback: nil)
//    }
    
    public func prepareToPlay() {
        player.prepareToPlay()
    }
    
    public func play() {
        player.play()
    }
    
    public func pause() {
        player.pause()
    }
    
    public func seekTo(time: Double) {
        let interval = time - startOffSet < 0 ? 0 : time - startOffSet
        print("用户进入时间偏移1：timeto1:\(interval) url:\(self.url)")
        player.setCurrentPlaybackTime(interval)
    }
    
    public func stop() {
        player.shutdown()
        removeNotification()
//        removeProgressListen()
    }
    
    public func muteAudio(mute: Bool) {
        player.setMute(mute)
    }
    
    public func updateUrl(url: String) {
        self.url = url
//        player.switchContentUrl(URL.init(string: url))
        if let aUrl = URL(string: url) {
            player.switchContentUrl(aUrl)
        }
    }
    
    @objc public func onPreparedToPlay(noti:Notification) {
        guard let player = noti.object as? NELivePlayerController else {
            return
        }
        if self.player.hashValue == player.hashValue {
            print("player通知 onPrepared url:\(self.url)")
            self.delegate?.onPrepared(playerItem: self)
        }
    }
    
    @objc public func onFirstVideoDisplay(noti:Notification) {
        guard let player = noti.object as? NELivePlayerController else {
            return
        }
        if self.player.hashValue == player.hashValue {
            print("player通知 onFirstVideoDisplay:\(self.player) 通知：\(noti.object)")
            self.delegate?.onFirstVideoDisplay(player: self)
        }
    }
    
    @objc func onPlayStateChange(noti:Notification) {
        guard let player = noti.object as? NELivePlayerController else {
            return
        }
        if self.player.hashValue == player.hashValue {
            switch player.playbackState {
            case .playing:
                state = .playing
                print("play state change:playing");
                self.delegate?.onPlay(player: self)
            case .paused:
                state = .pause
                self.delegate?.onPause(player: self)
                print("play state change:pause");

            case .seeking:
                state = .seeking
                print("play state change:seeking");
            case .stopped:
                state = .finished
                print("play state change:stopped");
            default:
                print("[player]:isFirst:\(first) default")
            }
        }
    }
    
    @objc func onSeekCompleted(noti:Notification) {
        guard let player = noti.object as? NELivePlayerController else {
            return
        }
        if self.player.hashValue == player.hashValue {
            print("player通知 onSeekCompleted:\(noti.object)")
            let time = noti.userInfo?[NELivePlayerMoviePlayerSeekCompletedTargetKey] as! NSNumber
            let errorCode = noti.userInfo?[NELivePlayerMoviePlayerSeekCompletedErrorKey] as! NSNumber
            state = .playing
            self.delegate?.onSeeked(player: self, time: Double(time)/1000, errorCode: errorCode.intValue)
        }
    }
    
    @objc func onPlayFinished(noti:Notification) {
        guard let player = noti.object as? NELivePlayerController else {
            return
        }
        if self.player.hashValue == player.hashValue {
            print("player通知 onPlayFinished:\(noti.object)")
            state = .finished
            if let code = noti.userInfo?[NELivePlayerPlaybackDidFinishErrorKey] as? Int, code != 0 {
                self.delegate?.onError(player: self, errorCode: code)
            }else {
                self.delegate?.onFinished(player: self)
            }
        }
    }
    
    @objc func onReleased(noti:Notification) {
        guard let player = noti.object as? NELivePlayerController else {
            return
        }
        if self.player.hashValue == player.hashValue {
            print("[player] onReleased noti:\(noti)")
        }
    }
    
    deinit {
        print("deinit NEEduRecordPlayItem")
        removeNotification()
    }
}
