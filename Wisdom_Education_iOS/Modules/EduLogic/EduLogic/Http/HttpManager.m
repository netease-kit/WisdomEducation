//
//  CYXHttpRequest.m
//  TenMinDemo
//
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "HttpManager.h"
#import "HttpClient.h"
#import "URL.h"
#import <YYModel/YYModel.h>
#import "NEAppInfo.h"
#import "NEEduErrorType.h"

#define LocalErrorDomain @"com.netease.needuhttpdomain"
#define LocalError(errCode,reason) ([NSError errorWithDomain:LocalErrorDomain \
code:(errCode) \
userInfo:@{NSLocalizedDescriptionKey:(reason)}])

#define HTTP_STATUE_OK 200

static HttpManagerConfig *config;
static NSMutableDictionary *header;
static void(^_errorBlock)(NSInteger);

@implementation HttpManagerConfig
@end

@implementation HttpManager
+ (HttpManagerConfig *)getHttpManagerConfig {
    if(config == nil) {
        config = [HttpManagerConfig new];
        config.version = @"v1";
        config.baseURL = @"https://yiyong-xedu-v2.netease.im";
        NSString *UUIDStr = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
        config.deviceId = UUIDStr;
    }
    return config;
}
+ (void)setupHttpManagerConfig:(HttpManagerConfig *)httpConfig {
    config = httpConfig;
}
+ (void)setErrorBlock:(void(^)(NSInteger))errorBlock {
    _errorBlock = errorBlock;
}

+ (void)loginWithAnalysisClass:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_EASY_LOGIN, config.baseURL,config.appKey,config.version];
    [HttpManager post:urlStr token:nil params:nil headers:nil success:^(id responseObj) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model);
        }
    } failure:^(NSError *error, NSInteger statusCode) {
        if(failureBlock) {
            failureBlock(error, statusCode);
        }
    }];
}

+ (void)loginWithUserId:(NSString *)userId token:(NSString *)token analysisClass:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_LOGIN, config.baseURL,config.appKey,config.version];
    if (!userId || !token) {
        if(failureBlock) {
            failureBlock(LocalError(NEEduErrorTypeInvalidParemeter, @"参数错误"), HTTP_STATUE_OK);
        }
        return;
    }
    [self addHeaderFromDictionary:@{@"user":userId,@"token":token}];
    [HttpManager post:urlStr token:nil params:nil headers:nil success:^(id responseObj) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model);
        }
    } failure:^(NSError *error, NSInteger statusCode) {
        if(failureBlock) {
            failureBlock(error, statusCode);
        }
    }];
}

+ (void)createRoom:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_CREATE_ROOM, config.baseURL, config.appKey, config.version,roomUuid];
    [HttpManager put:urlStr token:nil params:param headers:nil success:^(id responseObj) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model);
        }
    } failure:^(NSError *error, NSInteger statusCode) {
        if(failureBlock) {
            failureBlock(error, statusCode);
        }
    }];
}

+ (void)getRoom:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_GET_ROOM, config.baseURL, config.appKey, config.version,roomUuid];
    [HttpManager get:urlStr token:nil params:param headers:nil success:^(id data, NSInteger ts) {
        id model = [classType yy_modelWithDictionary:data];
        if(successBlock){
            successBlock(model);
        }
    } failure:^(NSError *error, NSInteger statusCode) {
        if(failureBlock) {
            failureBlock(error, statusCode);
        }
    }];
}

+ (void)enterRoom:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_ENTER_ROOM, config.baseURL, config.appKey, config.version, roomUuid];
    [HttpManager post:urlStr token:nil params:param headers:nil success:^(id responseObj) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model);
        }
    } failure:^(NSError *error, NSInteger statusCode) {
        if(failureBlock) {
            failureBlock(error, statusCode);
        }
    }];
}

+ (void)getRoomProfile:(NSString *)roomUuid classType:(Class)classType success:(void (^ _Nullable) (id objModel ,NSInteger ts))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_ROOM_profile, config.baseURL, config.appKey, config.version, roomUuid];
    [HttpManager get:urlStr token:nil params:nil headers:nil success:^(id responseObj,NSInteger ts) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model,ts);
        }
    } failure:failureBlock];
}

+ (void)updateStreamStateWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType streamType:(NSString *)streamType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_STREAM_STATE, config.baseURL, config.appKey, config.version, roomUuid,userUuid,streamType];
    NSNumber *value = [param objectForKey:@"value"];
    if (value.intValue == 0) {
        [HttpManager delete:urlStr token:nil params:param headers:nil success:^(id responseObj) {
            id model = [classType yy_modelWithDictionary:responseObj];
            if(successBlock){
                successBlock(model);
            }
        } failure:failureBlock];
    }else {
        [HttpManager put:urlStr token:nil params:param headers:nil success:^(id responseObj) {
            id model = [classType yy_modelWithDictionary:responseObj];
            if(successBlock){
                successBlock(model);
            }
        } failure:failureBlock];
    }
}

+ (void)updateMemberPropertyWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType property:(NSString *)property success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
//    @"%@/scene/apps/%@/%@/rooms/%@/members/%@/properties/%@"
    NSString *urlStr = [NSString stringWithFormat:HTTP_MEMBER_PROPERTY, config.baseURL, config.appKey, config.version, roomUuid,userUuid,property];
    [HttpManager put:urlStr token:nil params:param headers:nil success:^(id responseObj) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model);
        }
    } failure:failureBlock];
}

+ (void)deleteMemberPropertyWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType property:(NSString *)property success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
//    @"%@/scene/apps/%@/%@/rooms/%@/members/%@/properties/%@"
    NSString *urlStr = [NSString stringWithFormat:HTTP_MEMBER_PROPERTY, config.baseURL, config.appKey, config.version, roomUuid,userUuid,property];
    [HttpManager delete:urlStr token:nil params:param headers:nil success:^(id responseObj) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model);
        }
    } failure:failureBlock];
}

//+ (void)deleteStreamWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType streamType:(NSString *)streamType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
//    NSString *urlStr = [NSString stringWithFormat:HTTP_STREAM_STATE, config.baseURL, config.appId, config.version, roomUuid,userUuid,streamType];
//    [HttpManager delete:urlStr token:nil params:param headers:nil success:^(id responseObj) {
//        id model = [classType yy_modelWithDictionary:responseObj];
//        if(successBlock){
//            successBlock(model);
//        }
//    } failure:failureBlock];
//    
//}
//PUT: /scene/apps/{appId}/v1/rooms/{roomId}/states/step
+ (void)startLessonWithRoomUuid:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_LESSON_START, config.baseURL, config.appKey, config.version, roomUuid];
    [HttpManager put:urlStr token:nil params:param headers:nil success:^(id responseObj) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model);
        }
    } failure:failureBlock];
}

+ (void)muteAllWithRoomUuid:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_LESSON_MUTE, config.baseURL, config.appKey, config.version, roomUuid];
    [HttpManager put:urlStr token:nil params:param headers:nil success:^(id responseObj) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model);
        }
    } failure:failureBlock];
}

+ (void)muteAllTextWithRoomUuid:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_LESSON_MUTE_CHAT, config.baseURL, config.appKey, config.version, roomUuid];
    [HttpManager put:urlStr token:nil params:param headers:nil success:^(id responseObj) {
        id model = [classType yy_modelWithDictionary:responseObj];
        if(successBlock){
            successBlock(model);
        }
    } failure:failureBlock];
}

+ (void)getMessageWithRoomUuid:(NSString *)roomUuid nextId:(NSInteger)nextId classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_MESSAGE_GET, config.baseURL, config.appKey, config.version, roomUuid,nextId];
    [HttpManager get:urlStr token:nil params:nil headers:nil success:^(id data, NSInteger ts) {
        id model = [classType yy_modelWithDictionary:data];
        if(successBlock){
            successBlock(model);
        }
    } failure:failureBlock];
}

+ (void)getRecords:(NSString *)roomUuid rtcCid:(NSInteger)rtcCid classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_RECORD_GET, config.baseURL, config.appKey, config.version, roomUuid,rtcCid];
    [HttpManager get:urlStr token:nil params:nil headers:nil success:^(id data, NSInteger ts) {
        id model = [classType yy_modelWithDictionary:data];
        if(successBlock){
            successBlock(model);
        }
    } failure:failureBlock];
}

#pragma mark private

+ (void)get:(NSString *)url token:(NSString * _Nullable)token params:(NSDictionary *)params headers:(NSDictionary<NSString*, NSString*> *)headers success:(void (^)(id data,NSInteger ts))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {
    if (params != nil) {
        NSArray<NSString *> *keys = params.allKeys;
        
        if (![url containsString:@"?"] && keys.count > 0) {
            url = [url stringByAppendingString:@"?"];
        }
        
        for (NSInteger index = 0; index < keys.count; index ++) {
            NSString *key = keys[index];
            url = [url stringByAppendingFormat:@"%@=%@", key, params[key]];
            if (index < keys.count - 1) {
                url = [url stringByAppendingString:@"&"];
            }
        }
    }
    [HttpClient get:url params:nil headers:[HttpManager httpHeader] success:^(id  _Nonnull responseObj) {
        NSInteger code = [[responseObj objectForKey:@"code"] integerValue];
        if (code == 0) {
            NSDictionary *data = [responseObj objectForKey:@"data"];
            NSNumber *ts = [responseObj objectForKey:@"ts"];
            if (success) {
                success(data,ts.integerValue);
            }
        }else {
            if (failure) {
                NSError *error = [self errorWithErrorCode:code];
                failure(error,HTTP_STATUE_OK);
                [self handleErrorCode:code];
            }
        }
    } failure:failure];
}

+ (void)post:(NSString *)url token:(NSString * _Nullable)token params:(NSDictionary *)params headers:(NSDictionary<NSString *, NSString*> *)headers success:(void (^)(id responseObj))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {
    [HttpClient post:url params:params headers:[HttpManager httpHeader] success:^(id  _Nonnull responseObj) {
        NSInteger code = [[responseObj objectForKey:@"code"] integerValue];
        if (code == 0) {
            NSDictionary *data = [responseObj objectForKey:@"data"];
            if (success) {
                success(data);
            }
        }else {
            if (failure) {
                NSError *error = [self errorWithErrorCode:code];
                failure(error,HTTP_STATUE_OK);
                [self handleErrorCode:code];
            }
        }
    } failure:failure];
}

+ (void)put:(NSString *)url token:(NSString * _Nullable)token params:(NSDictionary *)params headers:(NSDictionary<NSString*, NSString*> *)headers success:(void (^)(id responseObj))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {

    [HttpClient put:url params:params headers:[HttpManager httpHeader] success:^(id  _Nonnull responseObj) {
        NSInteger code = [[responseObj objectForKey:@"code"] integerValue];
        if (code == EduErrorTypeNone || code == NEEduErrorTypeRoomAlreadyExists) {
            NSDictionary *data = [responseObj objectForKey:@"data"];
            if (success) {
                success(data);
            }
        }else {
            if (failure) {
                NSError *error = [self errorWithErrorCode:code];
                failure(error,HTTP_STATUE_OK);
                [self handleErrorCode:code];
            }
        }
    } failure:failure];
}

+ (void)delete:(NSString *)url token:(NSString * _Nullable)token params:(NSDictionary *)params headers:(NSDictionary<NSString*, NSString*> *)headers success:(void (^)(id responseObj))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {
    [HttpClient del:url params:params headers:[HttpManager httpHeader] success:^(id  _Nonnull responseObj) {
        NSInteger code = [[responseObj objectForKey:@"code"] integerValue];
        if (code == 0) {
            NSDictionary *data = [responseObj objectForKey:@"data"];
            if (success) {
                success(data);
            }
        }else {
            if (failure) {
                NSError *error = [self errorWithErrorCode:code];
                failure(error,HTTP_STATUE_OK);
                [self handleErrorCode:code];
            }
        }
    } failure:failure];
}
+ (NSMutableDictionary *)httpHeader {
    if (!header) {
        header = [NSMutableDictionary dictionary];
        header[@"clientType"] = @"ios";
        NSString *versionCode = [NSString stringWithFormat:@"%@%@",[NEAppInfo appVersion],[NEAppInfo buildVersion]];
        versionCode = [versionCode stringByReplacingOccurrencesOfString:@"." withString:@""];
        header[@"versionCode"] = versionCode;
        if(config.authorization) {
            NSString *auth = [NSString stringWithFormat:@"Basic %@", config.authorization];
            header[@"Authorization"] = auth;
        }
        if (config.deviceId) {
            header[@"deviceId"] = config.deviceId;
        }
    }
    return header;
}
 
+ (void)addHeaderFromDictionary:(NSDictionary *)dictionary {
    [[self httpHeader] addEntriesFromDictionary:dictionary];
}

+ (NSError *)errorWithErrorCode:(NSInteger)code {
    NSString *message = @"未知错误";
    switch (code) {
        case NEEduErrorTypeNotModified:
            message = @"未修改";
            break;
        case NEEduErrorTypeInvalidParemeter:
            message = @"参数错误";
            break;
        case NEEduErrorTypeUnauthorized:
            message = @"鉴权失败";
            break;
        case NEEduErrorTypeForbidden:
            message = @"没有操作权限";
            break;
        case NEEduErrorTypeNotFound:
            message = @"没有该操作";
            break;
        case NEEduErrorTypeMethodNotAllowed:
            message = @"方法不支持";
            break;
        case NEEduErrorTypeRoomAlreadyExists:
            message = @"房间已存在";
            break;
        case NEEduErrorTypeUnsurpportedType:
            message = @"数据格式不支持";
            break;
        case NEEduErrorTypeInternalServerError:
            message = @"服务器内部异常";
            break;
        case NEEduErrorTypeServiceBusy:
            message = @"服务繁忙";
            break;
        case NEEduErrorTypeConfigError:
            message = @"服务出错了";
            break;
        case NEEduErrorTypeRoleNumberOutOflimit:
            message = @"该角色人数超出限制";
            break;
        case NEEduErrorTypeRoleUndefined:
            message = @"该角色未定义";
            break;
        case NEEduErrorTypeRoomNotFound:
            message = @"房间不存在";
            break;
        case NEEduErrorTypeBadRoomConfig:
            message = @"房间配置不存在";
            break;
        case NEEduErrorTypeRoomPropertyExists:
            message = @"房间属性已存在";
            break;
        case NEEduErrorTypeMemberPropertyExists:
            message = @"成员属性已存在";
            break;
        case NEEduErrorTypeSeatConflict:
            message = @"超大房间设置的座位号已经存在";
            break;
        case NEEduErrorTypeSeatIsFull:
            message = @"超大房间座位已满";
            break;
        case NEEduErrorTypeUserIsSeated:
            message = @"超大房间用户已入座";
            break;
        case NEEduErrorTypeSeatNotExist:
            message = @"超大房间座位号不存在";
            break;
        case NEEduErrorTypeOutOfConcurrentLimit:
            message = @"人数超过限制";
            break;
        case NEEduErrorTypeInvalidSeatConfig:
            message = @"坐席配置不正确";
            break;
        case NEEduErrorTypeUserNotFound:
            message = @"成员不存在";
            break;
        case NEEduErrorTypeUserIsAlreadyInRoom:
            message = @"用户已在房间中";
            break;
        case NEEduErrorTypeCreateIMUserFailed:
            message = @"创建IM账户失败";
            break;
        case NEEduErrorTypeIMUserNotExist:
            message = @"IM账户不存在";
            break;
        case NEEduErrorTypeBadImService:
            message = @"IM服务异常";
            break;
        case NEEduErrorTypeNimUserExist:
            message = @"IM账户已存在";
            break;
        case NEEduErrorTypeRoomConfigConflict:
            message = @"房间已经存在且房间类型不匹配";
            break;
        default:
            break;
    }
    return LocalError(code, message);
}

+ (void)handleErrorCode:(NSInteger)code {
    if (code == 401) {
        _errorBlock(code);
    }
}
@end
