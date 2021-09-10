//
//  CYXHttpRequest.h
//  TenMinDemo
//
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "BaseModel.h"
#import "NEAppInfo.h"

#define APIVersion1 @"v1"
#define APIVersion2 @"v2"

NS_ASSUME_NONNULL_BEGIN

@interface HttpManagerConfig : NSObject
@property(nonatomic, strong) NSString *baseURL;
@property(nonatomic, strong) NSString *version;
@property(nonatomic, strong) NSString *appKey;

//header
@property(nonatomic, strong) NSString *authorization;
@property(nonatomic, strong) NSString *deviceId;

@end

@interface HttpManager : NSObject

+ (HttpManagerConfig *)getHttpManagerConfig;
+ (void)setupHttpManagerConfig:(HttpManagerConfig *)httpConfig;
+ (void)setErrorBlock:(void(^)(NSInteger))errorBlock;
/// 添加请求头内容
/// @param dictionary 添加内容
+ (void)addHeaderFromDictionary:(NSDictionary *)dictionary;

/// 匿名登录，已设备ID作为唯一表示
/// @param classType 返回参数类型
/// @param successBlock 成功回调
/// @param failureBlock 失败回调
+ (void)loginWithAnalysisClass:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

/// 用户登录，已用户ID作为唯一表示
/// @param userId    用户ID
/// @param token     用户token
/// @param classType 返回参数类型
/// @param successBlock 成功回调
/// @param failureBlock 失败回调
+ (void)loginWithUserId:(NSString *)userId token:(NSString *)token analysisClass:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)createRoom:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)enterRoom:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)getRoomProfile:(NSString *)roomUuid classType:(Class)classType success:(void (^ _Nullable) (id objModel ,NSInteger ts))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;
+ (void)updateStreamStateWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType streamType:(NSString *)streamType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;
//修改用户属性，教师通知学生打开音视频
+ (void)updateMemberPropertyWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType property:(NSString *)property success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)deleteMemberPropertyWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType property:(NSString *)property success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;


+ (void)startLessonWithRoomUuid:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)muteAllWithRoomUuid:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)muteAllTextWithRoomUuid:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

/// 获取丢失的长链接消息
/// @param roomUuid 房间ID
/// @param nextId 消息senquence
/// @param classType 返回数据类型
/// @param successBlock 成功回调
/// @param failureBlock 失败回调
+ (void)getMessageWithRoomUuid:(NSString *)roomUuid nextId:(NSInteger)nextId classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

/// 获取回放地址
/// @param roomUuid 房间ID
/// @param rtcCid Rtc Channel Id
/// @param classType 返回数据类型
/// @param successBlock 成功回调
/// @param failureBlock 失败回调
+ (void)getRecords:(NSString *)roomUuid rtcCid:(NSInteger)rtcCid classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;
@end

NS_ASSUME_NONNULL_END
