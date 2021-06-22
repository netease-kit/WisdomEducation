//
//  JoinRoomModel.h
//  EduSDK
//
//  Created by Netease on 2020/7/20.
//  Copyright © 2021 Netease. All rights reserved.
//

#import "BaseModel.h"
#import "RoomModel.h"
#import "EduUser.h"

NS_ASSUME_NONNULL_BEGIN

@interface JoinUserModel : EduUser

/// IM 用户名
@property (nonatomic, strong) NSString *userName;
/// IM 用户Id
@property (nonatomic, strong) NSString *userUuid;

@property (nonatomic, strong) NSString *userToken;
@property (nonatomic, strong) NSString *imToken;
@property (nonatomic, strong) NSString *rtcToken;
@property (nonatomic, strong) NSArray *streams;

@end

@interface JoinRoomInfoModel : NSObject
@property (nonatomic, strong) JoinUserModel *user;
@property (nonatomic, strong) RoomDataModel *room;
@end

@interface JoinRoomModel : NSObject <BaseModel>
@property (nonatomic, strong) JoinRoomInfoModel *data;
@end

NS_ASSUME_NONNULL_END
