//
//  NEEduRecorderPlayer.swift
//  NERecordPlay
//
//  Created by 郭园园 on 2021/8/9.
//

import Foundation
import NELivePlayerFramework
import CoreVideo

enum ScrollDirection {
    case right
    case left
}

public class NEEduRecorderPlayerManager: NSObject, NEEduRecordPlayerProtocol, NEEduRecordPlayerDelegate {

    public var state: PlayState {
        get {
            return firstPlayerItem?.state ?? .idle
        }
    }
    
    public weak var delegate: NEEduRecordPlayerDelegate?
    public var duration: Double = 0
    var data: RecordData
    var wbContentView: UIView
    var firstPlayerItem: NEEduWBPlayer?
    
    /// 用户视频数据视频数据列表，不包含辅流
    public var recordList: Array<RecordItem> = []
    
    /// 白板地址列表
    var wbUrlList: Array<String> = []
    
    /// 辅流列表
    var subStreamList: Array<RecordItem> = []
    
    /// 初始化的总的播放器列表，包含主流和辅流播放器。
    public var playerList = [NEEduRecordPlayer]()
    
    /// 当前正在播放的播放器列表
    var playingPlayers = [NEEduRecordPlayer]()
    
    /// 当前正在播放的播放器列表
    var preparePlayPlayers = [NEEduRecordPlayer]()
    
    /// 当前正在播放的RecordItem
    public var playingRecordItems = [RecordItem]()
    public var teacher: Member?
    
    public var playerDic: [String:NEEduRecordPlayer] = [:]
    var autoPlay: Bool = false
    var wbPlayer:NEEduWBPlayer?
    var preparedNumber = 0
    var fininshedNum = 0
    var curTime = 0;
    
    /// 时间：事件，时间单位秒
    var timeEvent: [Int:Event] = [:]
    var subPlayer: NEEduRecordPlayer?
    let eventPair = [2:1,8:7]
    var currentTime: Double = 0
    
    public init(data: RecordData, view:UIView, autoPlay:Bool) {
        self.duration = Double(data.record.stopTime - data.record.startTime) / 1000
        self.autoPlay = autoPlay
        self.data = data
        self.wbContentView = view;
        super.init()
        self.handleData(data: data)
        // 创建视频播放器列表
        self.playerList = createVideoPlayer(recordList: recordList)
        // 创建白板播放器
        self.wbPlayer = createWBPlayer(wbUrlList: wbUrlList)
    }
    /// 对元数据分类处理
    /// - Parameter data: 元数据
    func handleData(data:RecordData) {
        for item in data.recordItemList {
            if item.type == .gz {
                wbUrlList.append(item.url)
            }else {
                if item.type == .mp4 || item.type == .aac{
                    if item.subStream {
                        subStreamList.append(item)
                    }else {
                        item.isTeacher() ? recordList.insert(item, at: 0) : recordList.append(item)
                    }
                }
            }
        }
        print("recordList:\(recordList)\n subStreamList:\(subStreamList)\n wbUrlList:\(wbUrlList)")
        for event in data.eventList {
            if event.type == 4 || event.type == 5 || event.type == 6 || event.type == 3 {
                continue
            }
            let timeKey = (event.timestamp - data.record.startTime)/1000
            timeEvent[timeKey] = event
        }
        print("timeEvent:\(timeEvent)");
        
        for member in data.snapshotDto.snapshot.members {
            if member.isTeacher {
                teacher = member
                break
            }
        }
    }
    
    /// 创建播放器
    /// - Parameter recordList: 视频地址列表
    /// - Returns: 播放器列表
    func createVideoPlayer(recordList:[RecordItem]) -> [NEEduRecordPlayer] {
        var itemList = [NEEduRecordPlayer]()
        for recordItem in recordList {
            do {
                let player = try NEEduRecordPlayer(url: recordItem.url)
                player.startOffSet = Double(recordItem.timestamp - data.record.startTime) / 1000
                player.delegate = self
                player.autoPlay = self.autoPlay;
                itemList.append(player)
                playerDic[recordItem.url] = player
                //统计刚进入回放就需要播放的播放器
//                let offset = (recordItem.timestamp - data.record.startTime)/1000
                if showVideoView(record: recordItem) {
//                    player.autoPlay = self.autoPlay;
                    if recordItem.isTeacher() {
                        playingPlayers.insert(player, at: 0)
                        playingRecordItems.insert(recordItem, at: 0)
                    }else {
                        playingPlayers.append(player)
                        playingRecordItems.append(recordItem)
                    }
                }else {
//                    player.autoPlay = false;
                    preparePlayPlayers.append(player)
                }
            } catch  {
                print("error: initPlayer:\(error)")
            }
        }
        return itemList
    }
    
    func showVideoView(record:RecordItem) -> Bool {
        if self.data.sceneType == "EDU.BIG" {
            for member in self.data.snapshotDto.snapshot.members {
                if member.rtcUid == record.roomUid, record.isTeacher() {
                    return true
                }
            }
        }else {
            for member in self.data.snapshotDto.snapshot.members {
                if member.rtcUid == record.roomUid {
                    return true
                }
            }
        }
        return false;
    }
    
    func resetPlayers(recordList:[RecordItem], seekToZero: Bool) {
        var players = [NEEduRecordPlayer]()
        var items = [RecordItem]()
        for item in recordList {
            guard let player = playerDic[item.url] else {
                print("error:playerDic中获取player为空url:\(item.url)")
                continue
            }
            player.pause()
            player.startOffSet = Double(item.timestamp - data.record.startTime) / 1000
            if seekToZero {
                player.seekTo(time: 0)
            }
            if showVideoView(record: item) {
                if item.isTeacher() {
                    players.insert(player, at: 0)
                    items.insert(item, at: 0)
                }else {
                    players.append(player)
                    items.append(item)
                }
            }else {
//                preparePlayPlayers.append(player)
            }
        }
        playingPlayers = players
        playingRecordItems = items
        // 初始化辅流播放器
        self.subPlayer?.pause()
        print("resetPlayers\(playingPlayers.count) playingRecordItems:\(playingRecordItems.count)")
        self.delegate?.onResetPlayer(player: self)
    }
    
    /*
     func createVideoPlayer(recordList:[RecordItem]) -> [NEEduRecordPlayer] {
         var itemList = [NEEduRecordPlayer]()
         for recordItem in recordList {
             do {
                 let player = try NEEduRecordPlayer(url: recordItem.url)
                 player.startOffSet = Double(recordItem.timestamp - data.record.startTime) / 1000
                 player.delegate = self
                 itemList.append(player)
                 playerDic[recordItem.url] = player
                 //统计刚进入回放就需要播放的播放器
 //                let offset = (recordItem.timestamp - data.record.startTime)/1000
                 if showVideoView(record: recordItem) {
                     player.autoPlay = self.autoPlay;
                     if recordItem.isTeacher() {
                         playingPlayers.insert(player, at: 0)
                         playingRecordItems.insert(recordItem, at: 0)
                     }else {
                         playingPlayers.append(player)
                         playingRecordItems.append(recordItem)
                     }
                 }else {
                     player.autoPlay = false;
                     preparePlayPlayers.append(player)
                 }
             } catch  {
                 print("error: initPlayer:\(error)")
             }
         }
         return itemList
     }
     */
  
    /// 创建白板播放器
    /// - Returns: 播放器
    func createWBPlayer(wbUrlList: Array<String>) -> NEEduWBPlayer {
        let wbPlayerItem = NEEduWBPlayer.init(urls: wbUrlList,contentView: self.wbContentView)
        wbPlayerItem.delegate = self
        wbPlayerItem.autoPlay = self.autoPlay
        wbPlayerItem.asTimeline = true
        self.firstPlayerItem = wbPlayerItem
        return wbPlayerItem
    }
    
    func findRecordWithEvent(event: Event) -> RecordItem? {
        var recordItem: RecordItem?
        for item in subStreamList {
            if Int(event.roomUid) == item.roomUid {
                recordItem = item
                break
            }
        }
        guard let record = recordItem else {
            return nil
        }
        return record
    }
    
    func createSubVideoPlayer(recordItem: RecordItem) -> NEEduRecordPlayer? {
        if recordItem.url.count <= 0 {
            return nil
        }
        do {
            try subPlayer = NEEduRecordPlayer(url: recordItem.url)
            subPlayer?.autoPlay = true
            subPlayer?.delegate = self
            subPlayer?.startOffSet = Double(recordItem.timestamp - data.record.startTime) / 1000
            
        } catch  {
            print("error: initPlayer:\(error)")
        }
        return subPlayer
        
    }
    func resetSubPlayer() {
        if self.subPlayer != nil {
            self.subPlayer!.pause()
        }
    }

// MARK: - NEEduRecordPlayProtocol
    public func prepareToPlay() {
        for item in playingPlayers {
            item.prepareToPlay()
        }
        
        for prepareItem in preparePlayPlayers {
            prepareItem.prepareToPlay()
        }
    }
    public func play() {
        for item in playingPlayers {
            item.play()
        }
        wbPlayer?.play()
    }
    
    public func pause() {
        for item in playingPlayers {
            item.pause()
        }
        wbPlayer?.pause()
    }

    public func seekTo(time: Double) {
        print("seekCurrentTime:\(currentTime) toTime:\(time)")
        //  事件处理
        if currentTime < time {
            //right
            for item in playingPlayers {
                item.seekTo(time: time)
                item.play()
            }
            wbPlayer?.seekTo(time: time)
            wbPlayer?.play()
            handleSeekToRightEvent(fromTime: currentTime, toTime: time)
        }else {
            //left
            resetPlayers(recordList: data.recordItemList,seekToZero: false)
            for item in playingPlayers {
                item.seekTo(time: time)
                item.play()
            }
            wbPlayer?.seekTo(time: time)
            wbPlayer?.play()
            handleSeekToLeftEvent(fromTime: currentTime, toTime: time)
        }
        delegate?.onSeeked(player: self, time: time, errorCode: 0)
    }
    
    public func stop() {
        for item in playingPlayers {
            item.stop()
        }
        for item in preparePlayPlayers {
            item.stop()
        }
        subPlayer?.stop()
        wbPlayer?.stop()
    }
    
    public func muteAudio(mute: Bool) {
        for item in playingPlayers {
            item.muteAudio(mute: mute)
        }
    }

// MARK:
    
    // 向右拖拽事件处理
    private func handleSeekToRightEvent(fromTime: Double, toTime: Double) {
        filterEvent(fromTime: fromTime, toTime: toTime, direction: .right)
    }
    // 向左拖拽事件处理
    private func handleSeekToLeftEvent(fromTime: Double, toTime: Double) {
        filterEvent(fromTime: fromTime, toTime: toTime, direction: .left)
    }
    // 成对事件消除处理
    private func filterEvent(fromTime: Double, toTime: Double, direction: ScrollDirection) {
//         折叠时间点之前事件，执行剩下的事件
        var dic = [String:Event]()
        var array = [Event]()
        print("eventList:\(data.eventList) count:\(data.eventList.count)")
        for event in data.eventList {
            print("eventType:\(event.type)\n 事件时间：\(event.timestamp) 时长：\((event.timestamp - data.record.startTime)/1000)")
            let point = (event.timestamp - data.record.startTime)/1000
            if direction == .right {
                if point > Int(toTime) || point < Int(fromTime) {
                    continue
                }
            }else {
                if point > Int(toTime) {
                    continue
                }
            }
            let key = String("\(event.type)\(event.roomUid)")
            let pairType = self.eventPair[event.type]
            let pairKey = String("\(pairType)\(event.roomUid)")
            //先看下配对事件是否已存在，不存在则加入数组，存在则消消乐
            let pairEvent = dic[pairKey]
            if pairEvent == nil {
                dic[key] = event
                array.append(event)
            }else {
                dic.removeValue(forKey: pairKey)
                for (index, event) in array.enumerated() {
                    if event.roomUid == pairEvent?.roomUid,event.type == pairEvent?.type {
                        print("will remove at\(index)")
                        array.remove(at: index)
                        break
                    }
                }
            }
        }
        print("最终的事件：\(array)个数：\(array.count)")
        for event in array {
            handleEvent(event: event, to: toTime)
        }
    }

//    MARK:NEEduRecordPlayEvent
    public func onPrepared(playerItem: Any) {
        if let wbPlayer = playerItem as? NEEduWBPlayer {
            wbPlayer.setViewer(viewer:teacher?.rtcUid ?? 0)
            wbPlayer.setDuration(startTime: data.record.startTime, endTime: data.record.stopTime)
        }
        if let player = playerItem as? NEEduRecordPlayer {
            preparedNumber += 1
        }
        if (preparedNumber == playingPlayers.count + preparePlayPlayers.count) {
            delegate?.onPrepared(playerItem: playerItem)
        }
    }

    public func onFirstVideoDisplay(player: NEEduRecordPlayerProtocol) {
        if let playerItem = player as? NEEduRecordPlayer {
            if playerItem.url == subPlayer?.url {
                print("onFirstVideoDisplay1：\(playerItem.url)")
                //辅流第一帧
                if playerItem.seekToTime > 0 {
                    playerItem.pause();
                    playerItem.seekTo(time: playerItem.seekToTime)
                    playerItem.play()
                    playerItem.seekToTime = 0
                    print("onFirstVideoDisplay2")
                }
            }else {
//                主流播放器第一帧
                if playingPlayers.contains(where: { player in
                    return player.url == playerItem.url
                }) {
                    
                }else {
                    print("onFirstVideoDisplay3:\(playerItem.url)加载第一帧完成并暂停")
                    playerItem.pause()
                    
                }
            }
        }
//        if let playerItem = player as? NEEduRecordPlayer, playerItem.url == subPlayer?.url {
//            print("onFirstVideoDisplay1：\(playerItem.url)")
//            //辅流第一帧
//            if playerItem.seekToTime > 0 {
//                playerItem.pause();
//                playerItem.seekTo(time: playerItem.seekToTime)
//                playerItem.play()
//                playerItem.seekToTime = 0
//                print("onFirstVideoDisplay2")
//            }
//        }else {
//            //主流播放器第一帧
//            print("onFirstVideoDisplay3")
//        }
        
    }
    
    public func onPlay(player: Any) {
//        if let playerItem = player as? NEEduWBPlayer, playerItem.asTimeline {
//            delegate?.onPlay(player: self)
//        }
    }
    
    public func onPause(player: Any) {
//        if let playerItem = player as? NEEduWBPlayer, playerItem.asTimeline {
//            delegate?.onPause(player: self)
//        }
        
    }
    
    public func onSeeked(player: Any, time: Double, errorCode: Int) {
//        if let playerItem = player as? NEEduWBPlayer, playerItem.asTimeline {
//            delegate?.onSeeked(player: self, time: time, errorCode: errorCode)
//        }
    }
    
    public func onFinished(player: Any) {
        //所有播放器播放完成才更新
        if let playerItem = player as? NEEduWBPlayer, playerItem.asTimeline {
            print("播放完成 白板播放器：\(playerItem.asTimeline)")
            resetPlayers(recordList: data.recordItemList,seekToZero: true)
            delegate?.onFinished(player: self)
        }
    }
    
    public func onError(player: Any, errorCode: Int) {
        delegate?.onError(player: self, errorCode: errorCode)
    }
    
    public func onPlayTime(player: NEEduRecordPlayerProtocol, time: Double) {
        if let player = player as? NEEduWBPlayer, player.asTimeline, player.state != .finished {
            currentTime = time;
            if(curTime == Int(time)) {
                return
            }
            delegate?.onPlayTime(player: self, time: time)
            // 查找是否有需要执行的事件
            curTime = Int(time);
//            print("播放时间：\(curTime) in timeEvent:\(timeEvent)");
            guard let event = timeEvent[curTime] else {
                return
            }
            print("播放器:event:\(event.type) curTime:\(curTime)")
            let to  =  Double((event.timestamp - data.record.startTime) / 1000)
            handleEvent(event: event, to: to)
        }
    }
    public func onSubStreamStart(player: NEEduRecordPlayerProtocol, videoView: UIView?) {
        
    }
    
    public func onSubStreamStop(player: NEEduRecordPlayerProtocol, videoView: UIView?) {
        
    }
    
    public func userEnter(item: RecordItem) {
    }
    
    public func userLeave(item: RecordItem) {
        
    }
    public func onResetPlayer(player: NEEduRecordPlayerProtocol) {
        
    }
    
//    MARK: 回放事件
    /*
     事件类型
     1：成员进入房间
     2：成员离开房间
     3：成员打开音频
     4：成员关闭音频
     5：成员打开视频
     6：成员关闭视频
     7：成员打开辅流
     8：成员关闭辅流
     9:成员上台（大班课）
     10:成员下台（大班课）
     */
    func handleEvent(event: Event, to:Double) {
        
        switch event.type {
        case 1:
            userEnter(event: event,to: to)
        case 2:
            userLeave(event: event)
        case 7:
            startShareSceen(event: event, to:to)
        case 8:
            stopShareSceen(event: event)
        case 9:
            userEnter(event: event,to: to)
        case 10:
            userLeave(event: event)
        default:
            break
        }
    }
    
    func userEnter(event: Event, to: Double) {
        let record = findUrlOfUser(roomUid: Int(event.roomUid) ?? 0)
        guard let recordItem = record else {
            return
        }
        
        // 大班课忽略学生的进出
        if data.sceneType == "EDU.BIG", !recordItem.isTeacher() {
            return
        }
        
        guard let player = playerDic[recordItem.url]  else {
            return
        }
        print("事件用户进入时间偏移：\(to) url:\(recordItem.url) userName:\(recordItem.userName)")
        player.seekTo(time: to)
        player.play()
        if playingRecordItems.contains(where: { item in
            return item.url == recordItem.url
        }) { return }
        
        if recordItem.isTeacher() {
            playingRecordItems.insert(recordItem, at: 0)
            playingPlayers.insert(player, at: 0)
        }else {
            playingRecordItems.append(recordItem)
            playingPlayers.append(player)
        }
        delegate?.userEnter(item: recordItem)
    }
    
    func userLeave(event: Event) {
        let record = findUrlOfUser(roomUid: Int(event.roomUid) ?? 0)
        guard let recordItem = record else {
            return
        }
        
        // 大班课忽略学生的进出
        if data.sceneType == "EDU.BIG", !recordItem.isTeacher() {
            return
        }
        
        guard let player = playerDic[recordItem.url]  else {
            return
        }
        print("事件用户离开 userName:\(recordItem.userName) state:\(player.state)")
        player.pause()
        for (index,item) in playingRecordItems.enumerated() {
            if item.url == recordItem.url {
                playingRecordItems.remove(at: index)
                playingPlayers.remove(at: index)
            }
        }
        delegate?.userLeave(item: recordItem)
//        if player.state == .playing || player.state == .finished {
//            player.pause()
//            for (index,item) in playingRecordItems.enumerated() {
//                if item.url == recordItem.url {
//                    playingRecordItems.remove(at: index)
//                    playingPlayers.remove(at: index)
//                }
//            }
//            delegate?.userLeave(item: recordItem)
//        }
        
    }
    
    func startShareSceen(event: Event, to:Double) {
        guard let record = findRecordWithEvent(event: event) else {
            return
        }
        if self.subPlayer == nil {
            self.subPlayer = createSubVideoPlayer(recordItem: record)
            guard let subPlayer = self.subPlayer else {
                return
            }
            subPlayer.seekToTime = Double(event.timestamp - data.record.startTime) / 1000
            subPlayer.prepareToPlay()
            playingPlayers.append(subPlayer)
            //在收到第一帧回调中pause seek play
        }else {
            self.subPlayer!.startOffSet = Double(record.timestamp - data.record.startTime) / 1000
            self.subPlayer!.seekToTime = Double(event.timestamp - data.record.startTime) / 1000
            if self.subPlayer!.url == record.url {
                self.subPlayer!.pause()
                self.subPlayer!.seekTo(time: self.subPlayer!.seekToTime)
                self.subPlayer!.play()
            }else {
                print("原URL\(self.subPlayer!.url)切换URL\(record.url)")
                self.subPlayer!.updateUrl(url: record.url)
            }
        }

        delegate?.onSubStreamStart(player: self.subPlayer!, videoView: self.subPlayer!.view)
    }
    
    func stopShareSceen(event: Event?) {
        print("屏幕共享结束\(event?.roomUid)")
        guard let player = subPlayer else {
            return
        }
        subPlayer?.pause()
        delegate?.onSubStreamStop(player: player, videoView: player.view)
    }

    private func findUrlOfUser(roomUid: Int) -> (RecordItem?) {
        for item in recordList {
            if roomUid == item.roomUid {
                return item
            }
        }
        return nil
    }
    
    deinit {
        print("deinit NEEduRecorderPlayer")
    }
}


