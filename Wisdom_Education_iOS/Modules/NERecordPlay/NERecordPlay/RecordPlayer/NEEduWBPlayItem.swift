//
//  NEEduWBPlayItem.swift
//  NERecordPlay
//
//  Created by 郭园园 on 2021/8/16.
//

import Foundation
import NEWhiteBoard

public class NEEduWBPlayItem:NSObject, NEEduRecordPlayerProtocol,NEWBRecordPlayerDelegate {
    public var state: PlayState = .idle
    public weak var delegate: NEEduRecordPlayerDelegate?
    public var duration: Double = 0
    
    var player: NEWBRecordPlayer?
    init(urls: [String], contentView: UIView) {
        super.init()
        let param = NEWBRecordPlayerParam()
        param.urls = urls
//        param.controlContainerId = "toolbar"
        player = NEWBRecordPlayer(playerWithContentView: contentView, param: param);
        player?.delegate = self
    }

    public func prepareToPlay() {
        player?.prepareToPlay()
    }
    
    public func play() {
        player?.play()
    }
    
    public func pause() {
        player?.pause()
    }
    
    public func seekTo(time: Double) {
        player?.seek(toTimeInterval: Int(time * 1000))
    }
    
    public func stop() {
        
    }
    
    public func muteAudio(mute: Bool) {
        
    }
    
    public func setDuration(startTime:Int, endTime:Int) {
        player?.setTimeRangeStartTime(startTime, endTime: endTime)
    }
    
    public func onPrepared(with info: NEWBRecordInfo) {
        state = .prepared
        self.delegate?.onPrepared(playerItem: self)
    }
    deinit {
        print("deinit NEEduWBPlayItem")
    }
}

