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
#import "NEEduErrorType.h"

@interface NEEduManager ()
@property (nonatomic, strong, readwrite) NEEduHttpUser *localUser;
@property (nonatomic, copy, readwrite) NSString *imKey;
@property (nonatomic, strong) NEEduEnterRoomParam *roomParam;
@property (nonatomic, strong) NEEduHttpRoom *room;
@property (nonatomic, assign) BOOL configRead;
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
    self.configRead = options.isConfigRead;
    [self handleHttpRequestError];
}
- (void)login:(NSString *)userID token:(NSString *)token success:(void(^)(NEEduUser *user))success failure:(void(^)(NSError *error))failure {
    [HttpManager loginWithUserId:userID token:token analysisClass:[NEEduUser class] success:^(NEEduUser * _Nonnull objModel) {
        self.imKey = objModel.imKey;
//        NEEduManager.shared.configRead = YES;
        [self.rtcService setupAppkey:objModel.rtcKey isConfigRead:self.configRead];
        
        [self.imService addIMDelegate];
        self.imService.delegate = self.messageService;
        
        if (self.reuseIM) {
            if (self.imService.isLogined) {
                if (success) {
                    success(objModel);
                }
            }else {
                NSError *error = [NSError errorWithDomain:NEEduErrorDomain code:NEEduErrorTypeUnsupportOperation userInfo:@{NSLocalizedDescriptionKey:@"复用IM时，请先登录IM"}];
                if (failure) {
                    failure(error);
                }
            }
            return;
        }
        //不复用的情况下，初始化IM并登录
        [self.imService setupAppkey:objModel.imKey];
        __weak typeof(self)weakSelf = self;
        [self.imService login:objModel.userUuid token:objModel.imToken completion:^(NSError * _Nullable error) {
            if (error) {
                if (failure) {
                    failure(error);
                }
            }else {
                __strong typeof(self)strongSelf = weakSelf;
                strongSelf.localUser.userUuid = objModel.userUuid;
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
// 匿名登录
- (void)easyLoginWithSuccess:(void(^)(NEEduUser *user))success failure:(void(^)(NSError *error))failure {
    [HttpManager loginWithAnalysisClass:[NEEduUser class] success:^(NEEduUser * _Nonnull objModel) {
        self.imKey = objModel.imKey;
        if (objModel.userUuid && objModel.userToken) {
            [HttpManager addHeaderFromDictionary:@{@"user":objModel.userUuid,@"token":objModel.userToken}];
        }
        // 读取文件开关
//        NEEduManager.shared.configRead = YES;
        [self.rtcService setupAppkey:objModel.rtcKey isConfigRead:self.configRead];
        [self.imService setupAppkey:objModel.imKey];
        [self.imService addIMDelegate];
        self.imService.delegate = self.messageService;
        
        if (self.reuseIM) {
            if (self.imService.isLogined) {
                if (success) {
                    success(objModel);
                }
            }else {
                NSError *error = [NSError errorWithDomain:NEEduErrorDomain code:NEEduErrorTypeUnsupportOperation userInfo:@{NSLocalizedDescriptionKey:@"复用IM的情况下，请先登录IM"}];
                if (failure) {
                    failure(error);
                }
            }
            return;
        }
        
        __weak typeof(self)weakSelf = self;
        [self.imService login:objModel.userUuid token:objModel.imToken completion:^(NSError * _Nullable error) {
            if (error) {
                if (failure) {
                    failure(error);
                }
            }else {
                __strong typeof(self)strongSelf = weakSelf;
                strongSelf.localUser.userUuid = objModel.userUuid;
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
- (void)enterClassroom:(NEEduEnterRoomParam *)roomOption completion:(void(^)(NSError *error,NEEduEnterRoomResponse * response))completion {
    self.roomParam = roomOption;
    __weak typeof(self)weakSelf = self;
    // 1.http entry
    [self.roomService enterRoom:roomOption completion:^(NSError * _Nonnull error, NEEduEnterRoomResponse * _Nonnull response) {
        __strong typeof(self)strongSelf = weakSelf;
        if (error) {
            strongSelf.localUser = nil;
            strongSelf.room = nil;
            strongSelf.userService = nil;
            strongSelf.messageService = nil;
            if (completion) {
                completion(error,nil);
            }
        }else {
            //2.Rtc join
            strongSelf.localUser = response.member;
            strongSelf.profile.snapshot.room = response.room;
            strongSelf.room = response.room;
            strongSelf.userService = [[NEEduUserService alloc] initLocalUser:response.member];
            strongSelf.messageService.localUser = response.member;
            if (completion) {
                completion(nil,response);
            }
        }
    }];
}
- (void)joinRtcAndGetProfileCompletion:(void(^)(NSError *error,NEEduRoomProfile *profile))completion {
    dispatch_queue_t queue = dispatch_queue_create("NEEdu.logic.join", DISPATCH_QUEUE_SERIAL);
    __weak typeof(self)weakSelf = self;
    dispatch_async(queue, ^{
        [weakSelf joinRtcRoomCompletion:^(NSError *error, uint64_t channelID) {
            if (error) {
                if (completion) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        completion(error,nil);
                    });
                }
            }else {
                [weakSelf getProfileCompletion:^(NSError *error, NEEduRoomProfile *profile) {
                    if (error) {
                        if (completion) {
                            dispatch_async(dispatch_get_main_queue(), ^{
                                completion(error,nil);
                            });
                        }
                    }else {
                        if (completion) {
                            dispatch_async(dispatch_get_main_queue(), ^{
                                completion(nil,profile);
                            });
                        }
                    }
                }];
            }
        }];
    });
}
- (void)joinRtcRoomCompletion:(void(^)(NSError *error,uint64_t channelID))completion {
    NEEduRtcJoinChannelParam *param = [[NEEduRtcJoinChannelParam alloc] init];
    param.channelID = self.roomParam.roomUuid;
    param.rtcToken = self.localUser.rtcToken;
    param.userID = self.localUser.rtcUid;
    param.subscribeAudio = self.roomParam.autoSubscribeAudio;
    param.subscribeVideo = self.roomParam.autoSubscribeVideo;
    [self.rtcService joinChannel:param completion:completion];
}
- (void)joinChatRoomSuccess:(void(^)(NEEduChatRoomResponse *response))success failed:(void(^)(NSError *error))failed {
    NEEduChatRoomParam *chatparam = [[NEEduChatRoomParam alloc] init];
    chatparam.chatRoomID = self.room.properties.chatRoom.chatRoomId;
    if (self.roomParam.role == NEEduRoleTypeTeacher) {
        chatparam.nickname = [NSString stringWithFormat:@"%@(老师)",self.localUser.userName];
    }else {
        chatparam.nickname = [NSString stringWithFormat:@"%@(学生)",self.localUser.userName];
    }
    [self.imService enterChatRoomWithParam:chatparam success:success failed:failed];
}

- (void)getProfileCompletion:(void(^)(NSError *error,NEEduRoomProfile *profile))completion {
    __weak typeof(self)weakSelf = self;
    [self.roomService getRoomProfile:self.roomParam.roomUuid completion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
        __strong typeof(self)strongSelf = weakSelf;
        if (error) {
            strongSelf.profile = nil;
            if (completion) {
                completion(error,nil);
            }
        }else {
            strongSelf.profile = profile;
            //推流
            [strongSelf.rtcService enableLocalAudio:self.roomParam.autoPublish];
            [strongSelf.rtcService enableLocalVideo:self.roomParam.autoPublish];
            if (completion) {
                completion(nil,profile);
            }
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
    self.profile = nil;
    [self.rtcService leaveChannel];
    [self.imService leaveChatRoom];
    if (!self.reuseIM) {
        [self.imService logout];
    }
}

- (void)destoryClassroom {
    [self.rtcService destroy];
}

#pragma mark - private

- (BOOL)myselfWithUserID:(NSString *)userID {
    if (!self.localUser) {
        return NO;
    }
    if ([self.localUser.userUuid isEqualToString:userID]) {
        return YES;
    }
    return NO;
}
- (void)handleHttpRequestError {
    [HttpManager setErrorBlock:^(NSInteger code) {
        if (code == 401) {
            if (self.messageService.delegate && [self.messageService.delegate respondsToSelector:@selector(onUserTokenExpired:)]) {
                [self.messageService.delegate onUserTokenExpired:self.localUser];
            }
        }
    }];
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
