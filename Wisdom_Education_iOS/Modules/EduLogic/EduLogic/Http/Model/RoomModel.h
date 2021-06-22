//
//  RoomModel.h
//  EduSDK
//
//  Created by Netease on 2020/7/21.
//  Copyright © 2021 Netease. All rights reserved.
//

#import "BaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface ChatRoomInfoModel : NSObject
/// IM 聊天室ID
@property (nonatomic, strong) NSString *chatRoomId;
/// IM 聊天室创建者
@property (nonatomic, strong) NSString *roomCreatorId;
@end

@interface RoomProperties : NSObject
/// IM 聊天室
@property (nonatomic, strong) ChatRoomInfoModel *chatRoom;
@end

@interface RoomInfoModel : NSObject
/// NERtcSDK 频道名字（channelName）
@property (nonatomic, strong) NSString *roomUuid;
/// App 展示房间名称
@property (nonatomic, strong) NSString *roomName;

@end

@interface RoomMuteStateModel : NSObject
@property (nonatomic, assign) NSInteger administrator;
@property (nonatomic, assign) NSInteger host;
@property (nonatomic, assign) NSInteger assistant;
@property (nonatomic, assign) NSInteger broadcaster;
@property (nonatomic, assign) NSInteger audience;
@end

@interface RoomStateModel : NSObject
@property (nonatomic, assign) NSInteger state;//0未开始 1开始 2结束
@property (nonatomic, assign) NSInteger startTime;
@property (nonatomic, strong) RoomMuteStateModel *muteChat;
@property (nonatomic, strong) RoomMuteStateModel *muteAudio;
@property (nonatomic, strong) RoomMuteStateModel *muteVideo;
@end

@interface RoomDataModel : NSObject
@property (nonatomic, strong) RoomInfoModel *roomInfo;
@property (nonatomic, strong) RoomProperties *roomProperties;
@property (nonatomic, strong) RoomStateModel *roomState;
@end

@interface RoomModel : NSObject <BaseModel>
@property (nonatomic, strong) RoomDataModel *data;
@end

NS_ASSUME_NONNULL_END

