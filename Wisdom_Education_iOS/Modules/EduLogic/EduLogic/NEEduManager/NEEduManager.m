//
//  NEEduManager.m
//  EduLogic
//
//  Created by Groot on 2021/5/13.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduManager.h"

#import "HttpManager.h"
#import "NEEduUser.h"
#import "NERtcVideoCanvasExtention.h"


@interface NEEduManager ()
@property (nonatomic, strong, readwrite) NEEduHttpUser *localUser;
@property (nonatomic, copy, readwrite) NSString *imKey;
@property (nonatomic, copy, readwrite) NSString *imToken;
@end

@implementation NEEduManager
+ (instancetype)shared {
    static dispatch_once_t onceToken;
    static NEEduManager *manager;
    dispatch_once(&onceToken, ^{
        manager = [[NEEduManager alloc] init];
    });
    return manager;
}

- (void)setupAppKey:(NSString * _Nonnull)appKey options:(NEEduKitOptions *)options {
    HttpManagerConfig *config = [HttpManager getHttpManagerConfig];
    config.appKey = appKey;
    config.authorization = options.authorization;
    config.baseURL = options.baseURL;
}

- (void)login:(NSString * _Nullable)userID success:(void(^)(NEEduUser *user))success failure:(void(^)(NSError *error))failure {
    // 1.login http
    __weak typeof(self)weakSelf = self;
    [HttpManager loginWithParam:nil analysisClass:[NEEduUser class] success:^(NEEduUser * _Nonnull objModel) {
        weakSelf.imKey = objModel.imKey;
        weakSelf.imToken = objModel.imToken;
        //登录成功，更新HttpManager token
        HttpManagerConfig *config = [HttpManager getHttpManagerConfig];
        config.userUuid = objModel.userUuid;
        config.userToken = objModel.userToken;
        [self setupSDKWithImKey:objModel.imKey rtcKey:objModel.rtcKey];
        [self.imService login:objModel.userUuid token:objModel.imToken completion:^(NSError * _Nullable error) {
            if (error) {
                //IM登录失败，更新HttpManager token
                HttpManagerConfig *config = [HttpManager getHttpManagerConfig];
                config.userUuid = nil;
                config.userToken = nil;
                if (failure) {
                    failure(error);
                }
            }else {
                weakSelf.localUser.userUuid = objModel.userUuid;
                if (success) {
                    success(objModel);
                }
            }
        }];
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (failure) {
            failure(error);
        }
    }];
}
- (void)enterClassroom:(NEEduEnterRoomParam *)roomOption success:(void(^)(NEEduRoomProfile *roomProfile))success failure:(void(^)(NSError *error))failure {
    __weak typeof(self)weakSelf = self;
    // 1.http entry
    [self.roomService enterRoom:roomOption completion:^(NSError * _Nonnull error, NEEduEnterRoomResponse * _Nonnull response) {
        __strong typeof(self)strongSelf = weakSelf;
        if (error) {
            if (failure) {
                failure(error);
            }
        }else {
            //2.Rtc join
            weakSelf.localUser = response.member;
            weakSelf.userService = [[NEEduUserService alloc] initLocalUser:response.member];
            weakSelf.messageService.localUser = response.member;
            NEEduRtcJoinChannelParam *param = [[NEEduRtcJoinChannelParam alloc] init];
            param.channelID = roomOption.roomUuid;
            param.rtcToken = response.member.rtcToken;
            param.userID = response.member.rtcUid;
            param.subscribeAudio = roomOption.autoSubscribeAudio;
            param.subscribeVideo = roomOption.autoSubscribeVideo;
            __strong typeof(self)weakSelf = strongSelf;
            [strongSelf.rtcService joinChannel:(NEEduRtcJoinChannelParam *)param completion:^(NSError * _Nonnull error, uint64_t channelID) {
                __strong typeof(self)strongSelf = weakSelf;
                if (error) {
                    if (failure) {
                        failure(error);
                    }
                }else {
                    //4.http snapshot
                    __strong typeof(self)weakSelf = strongSelf;
                    [strongSelf.roomService getRoomProfile:roomOption.roomUuid completion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
                        __strong typeof(self)strongSelf = weakSelf;
                        if (error) {
                            strongSelf.profile = nil;
                            if (failure) {
                                failure(error);
                            }
                        }else {
                            //推流
                            [strongSelf.rtcService enableLocalAudio:roomOption.autoPublish];
                            [strongSelf.rtcService enableLocalVideo:roomOption.autoPublish];
                            if (success) {
                                success(profile);
                            }
                        }
                    }];
                }
            }];
//            3.IM enter chatRoom
            NEEduChatRoomParam *chatparam = [[NEEduChatRoomParam alloc] init];
            chatparam.chatRoomID = response.room.properties.chatRoom.chatRoomId;
            if (roomOption.role == NEEduRoleTypeTeacher) {
                chatparam.nickname = [NSString stringWithFormat:@"%@(老师)",response.member.userName];
            }else {
                chatparam.nickname = [NSString stringWithFormat:@"%@(学生)",response.member.userName];
            }
            [strongSelf.imService enterChatRoomWithParam:chatparam success:^(NEEduChatRoomResponse * _Nonnull response) {
            } failed:^(NSError * _Nonnull error) {
            }];
        }
    }];
}
- (void)setCanvasView:(UIView *)view forMember:(NEEduHttpUser *)member {
    NERtcVideoCanvasExtention *canvas = [[NERtcVideoCanvasExtention alloc] init];
    canvas.uid = member.rtcUid;
    canvas.container = view;
    canvas.renderMode = kNERtcVideoRenderScaleFit;
    if ([self myselfWithUserID:member.userUuid]) {
        [self.rtcService setupLocalVideo:canvas];
    }else {
        [self.rtcService setupRemoteVideo:canvas];
    }
}

- (void)leaveClassroom {
    [self.rtcService leaveChannel];
    [self.imService destroy];
}

- (void)destoryClassroom {
    [self.rtcService destroy];
    self.rtcService = nil;
    self.imService = nil;
    self.roomService = nil;
}

#pragma mark - private
/// 初始化IMSDK & RtcSDK
/// @param imKey IMSDK的Key
/// @param rtcKey RtcSDK的Key
- (void)setupSDKWithImKey:(NSString *)imKey rtcKey:(NSString *)rtcKey {
    [self.imService setupAppkey:imKey];
    [self.rtcService setupAppkey:rtcKey];
    self.imService.delegate = self.messageService;
}

- (BOOL)myselfWithUserID:(NSString *)userID {
    if (!self.localUser) {
        return NO;
    }
    if ([self.localUser.userUuid isEqualToString:userID]) {
        return YES;
    }
    return NO;
}



#pragma mark - get
- (NEEduIMService *)imService {
    if (!_imService) {
        _imService = [[NEEduIMService alloc] init];
    }
    return _imService;
}
- (NEEduRtcService *)rtcService {
    if (!_rtcService) {
        _rtcService = [[NEEduRtcService alloc] init];
    }
    return _rtcService;
}
- (NEEduRoomService *)roomService {
    if (!_roomService) {
        _roomService = [[NEEduRoomService alloc] init];
    }
    return _roomService;
}
- (NEEduMessageService *)messageService {
    if (!_messageService) {
        _messageService = [[NEEduMessageService alloc] init];
    }
    return _messageService;
}

- (NEEduUserService *)userService {
    if (!_userService) {
        _userService = [[NEEduUserService alloc] init];
    }
    return _userService;
}

- (NEEduHttpUser *)localUser {
    if (!_localUser) {
        _localUser = [[NEEduHttpUser alloc] init];
    }
    return _localUser;
}
@end
