//
//  File.swift
//  NERecordPlay
//
//  Created by 郭园园 on 2021/8/11.
//

import Foundation
public struct Response: Codable {
    var code: Int
    var msg: String
    var requestId: String
    var ts: Int
    var data: RecordData?
}

@objc public class RecordData:NSObject, Codable {
    var sceneType: String?
    var record: Record
    var eventList: Array<Event>
    var recordItemList: Array<RecordItem>
    public var snapshotDto: SnapshotDto
}

public struct Record: Codable {
    var classBeginTimestamp: Int
    var recordId: String
    var roomUuid: String
    var roomCid: String
    var startTime: Int
    var stopTime: Int
}
struct Event: Codable {
    var roomUid: String
    var timestamp: Int
    var type: Int
}
public struct RecordItem: Codable {
    public var role: String?
    public var userName: String?
    public var url: String
    public var duration: Int
    public var filename: String
    public var md5: String
    public var mix: Int
    var pieceIndex: Int
    var recordId: String
    public var roomUid: Int
    var size: Int
    public var subStream: Bool
    public var timestamp: Int
    public var type: FormatType
    
    public func isTeacher() -> Bool {
        return role == "host"
    }
}

public enum FormatType:String, Codable {
    case mp4,gz
}

public struct SnapshotDto: Codable {
    var sequence: Int
    public var snapshot: Snapshot
}

public struct Snapshot: Codable {
    public var room: Room
    public var members: Array<Member>
    
}
// mark - Member
public struct Member: Codable {
    public var userName: String
    public var userUuid: String
    public var role: String
    var rtcUid: Int
    var streams: Streams
    var properties: Properties
    var time: Int
    public var isTeacher: Bool {
        return role == "host"
    }
    
}
struct Streams: Codable {
    var video: Item?
    var audio: Item?
}
struct Properties: Codable {
    var screenShare: Item?
}
struct Item: Codable {
    var value: Int
    var time: Int?
}

// mark - Room
public struct Room: Codable {
    public var roomName: String
    public var roomUuid: String
    public var rtcCid: String
    var properties: RoomProperties
}
struct RoomProperties: Codable {
    var chatRoom: ChatRoom?
    var whiteboard: Whiteboard?
}
struct ChatRoom: Codable {
    var chatRoomId: Int
    var roomCreatorId: String
    var time: Int
}
struct Whiteboard: Codable {
    var channelName: String
}
