//
//  NEEduUserService.m
//  EduLogic
//
//  Created by Groot on 2021/5/26.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduUserService.h"
#import "HttpManager.h"
#import "NEEduPropertyItem.h"
#import "NEEduManager.h"
#import "NEEduErrorType.h"

@interface NEEduUserService ()
@property (nonatomic, strong) NEEduRoomProfile *profile;
@property (nonatomic, strong) NEEduHttpUser *localUser;

@end

@implementation NEEduUserService
- (instancetype)initLocalUser:(NEEduHttpUser *)localUser
{
    self = [super init];
    if (self) {
        self.localUser = localUser;
    }
    return self;
}
- (void)setupProfile:(NEEduRoomProfile *)profile {
    self.profile = profile;
}

- (void)localUserVideoEnable:(BOOL)enable result:(void(^)(NSError *error))result {
    if (!self.profile || !self.localUser) {
        NSError *error = [NSError errorWithDomain:NEEduErrorDomain code:NEEduErrorTypeUnsupportOperation userInfo:@{NSLocalizedDescriptionKey:@"视频开关失败,房间或用户信息不存在"}];
        if (result) {
            result(error);
        }
        return;
    }
    int intEnable = enable;
    NSDictionary *param = @{@"value":@(intEnable)};
    //http
    [HttpManager updateStreamStateWithRoomUuid:self.profile.snapshot.room.roomUuid userUuid:self.localUser.userUuid param:param classType:[NEEduPropertyItem class] streamType:@"video" success:^(id  _Nonnull objModel) {
        NEEduPropertyItem *item = objModel;
        //RTC
        int code = [[NEEduManager shared].rtcService enableLocalVideo:enable];
        NSError *error = code == 0 ? nil : [NSError errorWithDomain:NEEduErrorDomain code:code userInfo:@{NSLocalizedDescriptionKey:[NSString stringWithFormat:@"视频开关失败,code:%d",code]}];
        if (!error) {
            for (NEEduHttpUser *user in self.profile.snapshot.members) {
                if ([user.userUuid isEqualToString:self.localUser.userUuid]) {
                    user.streams.video = item;
                }
            }
        }
        if (result) {
            result(error);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (result) {
            result(error);
        }
    }];
}
- (void)localUserAudioEnable:(BOOL)enable result:(void(^)(NSError *error))result {
    if (!self.profile || !self.localUser) {
        NSError *error = [NSError errorWithDomain:NEEduErrorDomain code:NEEduErrorTypeUnsupportOperation userInfo:@{NSLocalizedDescriptionKey:@"音频开关失败,房间或用户信息不存在"}];
        if (result) {
            result(error);
        }
        return;
    }
    int intEnable = enable;
    NSDictionary *param = @{@"value":@(intEnable)};
    //http
    [HttpManager updateStreamStateWithRoomUuid:self.profile.snapshot.room.roomUuid userUuid:self.localUser.userUuid param:param classType:[NEEduPropertyItem class] streamType:@"audio" success:^(id  _Nonnull objModel) {
        NEEduPropertyItem *item = objModel;
        //RTC
        int code = [[NEEduManager shared].rtcService enableLocalAudio:enable];
        NSError *error = code == 0 ? nil : [NSError errorWithDomain:NEEduErrorDomain code:code userInfo:@{NSLocalizedDescriptionKey:@"音频开关失败"}];
        if (!error) {//更新profile
            for (NEEduHttpUser *user in self.profile.snapshot.members) {
                if ([user.userUuid isEqualToString:self.localUser.userUuid]) {
                    user.streams.audio = item;
                }
            }
        }
        if (result) {
            result(error);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (result) {
            result(error);
        }
    }];
}

- (void)remoteUserVideoEnable:(BOOL)enable userID:(NSString *)userID result:(void(^)(NSError *error))result {
    int intEnable = enable;
    NSDictionary *param = @{@"value":@(1),@"video":@(intEnable)};
    
    [HttpManager updateMemberPropertyWithRoomUuid:self.profile.snapshot.room.roomUuid userUuid:userID param:param classType:[NEEduPropertyItem class] property:@"streamAV" success:^(id  _Nonnull objModel) {
        if (result) {
            result(nil);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (result) {
            result(error);
        }
    }];
}

- (void)remoteUserAudioEnable:(BOOL)enable userID:(NSString *)userID result:(void(^)(NSError *error))result {
    int intEnable = enable;
    NSDictionary *param = @{@"value":@(1),@"audio":@(intEnable)};
    [HttpManager updateMemberPropertyWithRoomUuid:self.profile.snapshot.room.roomUuid userUuid:userID param:param classType:[NEEduPropertyItem class] property:@"streamAV" success:^(id  _Nonnull objModel) {
        if (result) {
            result(nil);
        }
        
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (result) {
            result(error);
        }
    }];
}
//老师开启学生版本可编辑权限
- (void)whiteboardDrawable:(BOOL)drawable userID:(NSString *)userID result:(void(^)(NSError *error))result {
    int intEnable = drawable;
    NSDictionary *param = @{@"drawable":@(intEnable)};
    [HttpManager updateMemberPropertyWithRoomUuid:self.profile.snapshot.room.roomUuid userUuid:userID param:param classType:[NEEduPropertyItem class] property:@"whiteboard" success:^(id  _Nonnull objModel) {
        if (result) {
            result(nil);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (result) {
            result(error);
        }
    }];

}
- (void)screenShareAuthorization:(BOOL)enable userID:(NSString *)userID result:(void(^)(NSError *error))result {
    int intEnable = enable;
    NSDictionary *param = @{@"value":@(intEnable)};
    [HttpManager updateMemberPropertyWithRoomUuid:self.profile.snapshot.room.roomUuid userUuid:userID param:param classType:[NEEduPropertyItem class] property:@"screenShare" success:^(id  _Nonnull objModel) {
        if (result) {
            result(nil);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (result) {
            result(error);
        }
    }];
 
}
- (void)localShareScreenEnable:(BOOL)enable result:(void(^)(NSError *error))result {
    if (!self.profile || !self.localUser) {
        NSError *error = [NSError errorWithDomain:NEEduErrorDomain code:NEEduErrorTypeUnsupportOperation userInfo:@{NSLocalizedDescriptionKey:@"屏幕共享失败,房间或用户信息不存在"}];
        if (result) {
            result(error);
        }
        return;
    }
    int intEnable = enable;
    NSDictionary *param = @{@"value":@(intEnable)};
    //http
    [HttpManager updateStreamStateWithRoomUuid:self.profile.snapshot.room.roomUuid userUuid:self.localUser.userUuid param:param classType:[NEEduPropertyItem class] streamType:@"subVideo" success:^(id  _Nonnull objModel) {
        NEEduPropertyItem *item = objModel;
        //RTC
        int code = [[NEEduManager shared].rtcService enableLocalAudio:enable];
        NSError *error = code == 0 ? nil : [NSError errorWithDomain:NEEduErrorDomain code:code userInfo:@{NSLocalizedDescriptionKey:@"屏幕共享失败"}];
        if (!error) {//更新profile
            for (NEEduHttpUser *user in self.profile.snapshot.members) {
                if ([user.userUuid isEqualToString:self.localUser.userUuid]) {
                    user.streams.subVideo = item;
                }
            }
        }
        if (result) {
            result(error);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (result) {
            result(error);
        }
    }];
}
- (void)handsupStateChange:(NEEduHandsupState)state userID:(NSString *)userID result:(void(^)(NSError *error))result {
    int intEnable = state;
    NSDictionary *param = @{@"value":@(intEnable)};
    [HttpManager updateMemberPropertyWithRoomUuid:self.profile.snapshot.room.roomUuid userUuid:userID param:param classType:[NEEduPropertyItem class] property:@"avHandsUp" success:^(NEEduPropertyItem *objModel) {
        for (NEEduHttpUser *user in self.profile.snapshot.members) {
            if ([user.userUuid isEqualToString:self.localUser.userUuid]) {
                if (user.properties.avHandsUp) {
                    user.properties.avHandsUp.value = objModel.value;
                }else {
                    NEEduHandsupProperty *item = [[NEEduHandsupProperty alloc] init];
                    item.value = objModel.value;
                    user.properties.avHandsUp = item;
                }
            }
        }
        if (result) {
            result(nil);
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (result) {
            result(error);
        }
    }];
}
- (NEEduHttpUser *)userIsShareScreen {
    for (NEEduHttpUser *user in self.profile.snapshot.members) {
        if (user.streams.subVideo.value == 1) {
            return user;
        }
    }
    return nil;
}
@end
