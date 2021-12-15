//
//  NEEduWBPlayItem.swift
//  NERecordPlay
//
//  Created by 郭园园 on 2021/8/16.
//

import Foundation
import NEWhiteBoard

public class NEEduWBPlayer:NSObject, NEEduRecordPlayerProtocol,NEWBRecordPlayerDelegate {
    public var asTimeline = false
    public var state: PlayState = .idle
    public weak var delegate: NEEduRecordPlayerDelegate?
    public var duration: Double = 0
    public var autoPlay: Bool = false;

    var player: NEWBRecordPlayer?
    init(urls: [String], contentView: UIView) {
        super.init()
        let param = NEWBRecordPlayerParam()
        param.urls = urls
        player = NEWBRecordPlayer(playerWithContentView: contentView, param: param);
        player?.delegate = self
    }

    public func prepareToPlay() {
        player?.prepareToPlay()
    }
    
    public func play() {
        player?.play()
        state = .playing
    }
    
    public func pause() {
        player?.pause()
        state = .pause
    }
    
    public func seekTo(time: Double) {
        player?.seek(toTimeInterval: Int(time * 1000))
    }
    
    public func stop() {
        state = .finished
        player?.stop()
    }
    
    public func muteAudio(mute: Bool) {
        
    }
    
    public func setDuration(startTime:Int, endTime:Int) {
        player?.setTimeRangeStartTime(startTime, endTime: endTime)
    }
    
    public func onPrepared(with info: NEWBRecordInfo) {
        state = .prepared
        self.delegate?.onPrepared(playerItem: self)
        if autoPlay == true {
            play()
        }
    }
    
    public func onPlayTime(_ time: TimeInterval) {
        self.delegate?.onPlayTime(player: self, time: time)
    }
    
    public func onPlayFinished() {
        state = .finished
        self.delegate?.onFinished(player: self);
    }

    deinit {
        print("deinit NEEduWBPlayItem")
    }
}

