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

#define LocalErrorDomain @"com.netease.needuhttpdomain"
#define LocalError(errCode,reason) ([NSError errorWithDomain:LocalErrorDomain \
code:(errCode) \
userInfo:@{NSLocalizedDescriptionKey:(reason)}])

#define HTTP_STATUE_OK 200

static HttpManagerConfig *config;

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

+ (void)loginWithParam:(NSDictionary * _Nullable)param analysisClass:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock {
    NSString *urlStr = [NSString stringWithFormat:HTTP_EASY_LOGIN, config.baseURL,config.appKey,config.version];
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
            }
        }
    } failure:failure];
}

+ (void)put:(NSString *)url token:(NSString * _Nullable)token params:(NSDictionary *)params headers:(NSDictionary<NSString*, NSString*> *)headers success:(void (^)(id responseObj))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {

    [HttpClient put:url params:params headers:[HttpManager httpHeader] success:^(id  _Nonnull responseObj) {
        NSInteger code = [[responseObj objectForKey:@"code"] integerValue];
        if (code == 0 || code == 409) {
            NSDictionary *data = [responseObj objectForKey:@"data"];
            if (success) {
                success(data);
            }
        }else {
            if (failure) {
                NSError *error = [self errorWithErrorCode:code];
                failure(error,HTTP_STATUE_OK);
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
            }
        }
    } failure:failure];
}

+ (NSDictionary *)httpHeader {
    NSMutableDictionary *headers = [NSMutableDictionary dictionary];
    if(config.authorization) {
        NSString *auth = [NSString stringWithFormat:@"Basic %@", config.authorization];
        headers[@"Authorization"] = auth;
    }
    if (config.deviceId) {
        headers[@"deviceId"] = config.deviceId;
    }
    if (config.userUuid) {
        headers[@"user"] = config.userUuid;
    }
    if (config.userToken) {
        headers[@"token"] = config.userToken;
    }
    headers[@"clientType"] = @"ios";
    
    NSString *versionCode = [NSString stringWithFormat:@"%@%@",[NEAppInfo appVersion],[NEAppInfo buildVersion]];
    versionCode = [versionCode stringByReplacingOccurrencesOfString:@"." withString:@""];
    headers[@"versionCode"] = versionCode;
    return [headers copy];
}

+ (NSError *)errorWithErrorCode:(NSInteger)code {
    NSString *message = @"未知错误";
    switch (code) {
        case 304:
            message = @"未修改";
            break;
        case 400:
            message = @"参数错误";
            break;
        case 401:
            message = @"鉴权失败";
            break;
        case 403:
            message = @"没有操作权限";
            break;
        case 404:
            message = @"没有该操作";
            break;
        case 405:
            message = @"方法不支持";
            break;
        case 409:
            message = @"房间已存在";
            break;
        case 415:
            message = @"数据格式不支持";
            break;
        case 500:
            message = @"服务器内部异常";
            break;
        case 503:
            message = @"服务繁忙";
            break;
        case 1001:
            message = @"服务出错了";
            break;
        case 1002:
            message = @"该角色人数超出限制";
            break;
        case 1003:
            message = @"该角色未定义";
            break;
        case 1004:
            message = @"房间不存在";
            break;
        case 1005:
            message = @"房间配置不存在";
            break;
        case 1006:
            message = @"房间属性已存在";
            break;
        case 1007:
            message = @"成员属性已存在";
            break;
        case 1008:
            message = @"超大房间设置的座位号已经存在";
            break;
        case 1009:
            message = @"超大房间座位已满";
            break;
        case 1010:
            message = @"超大房间用户已入座";
            break;
        case 1011:
            message = @"超大房间座位号不存在";
            break;
        case 1012:
            message = @"人数超过限制";
            break;
        case 1014:
            message = @"坐席配置不正确";
            break;
        case 1015:
            message = @"成员不存在";
            break;
        case 700:
            message = @"创建IM账户失败";
            break;
        case 701:
            message = @"IM账户不存在";
            break;
        case 702:
            message = @"IM服务异常";
            break;
        case 703:
            message = @"IM账户已存在";
            break;
            
        default:
            break;
    }
    return LocalError(code, message);
}
@end
