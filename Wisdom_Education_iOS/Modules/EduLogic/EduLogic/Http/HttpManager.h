//
//  CYXHttpRequest.h
//  TenMinDemo
//
//  Created by apple开发 on 16/5/31.
//  Copyright © 2016年 CYXiang. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BaseModel.h"

#define APIVersion1 @"v1"
#define APIVersion2 @"v2"

NS_ASSUME_NONNULL_BEGIN

@interface HttpManagerConfig : NSObject
@property(nonatomic, strong) NSString *baseURL;
@property(nonatomic, strong) NSString *version;

//header
@property(nonatomic, strong) NSString *authorization;
@property(nonatomic, strong) NSString *deviceId;
@property(nonatomic, strong) NSString *userUuid;
@property(nonatomic, strong) NSString *userToken;
@property(nonatomic, strong) NSString *appId;


@end

@interface HttpManager : NSObject

+ (HttpManagerConfig *)getHttpManagerConfig;
+ (void)setupHttpManagerConfig:(HttpManagerConfig *)httpConfig;

+ (void)loginWithParam:(NSDictionary * _Nullable)param analysisClass:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)createRoom:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)enterRoom:(NSString *)roomUuid param:(NSDictionary *)param classType:(Class)classType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)getRoomProfile:(NSString *)roomUuid classType:(Class)classType success:(void (^ _Nullable) (id objModel ,NSInteger ts))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;
+ (void)updateStreamStateWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType streamType:(NSString *)streamType success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;
//修改用户属性，教师通知学生打开音视频
+ (void)updateMemberPropertyWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType property:(NSString *)property success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

+ (void)deleteMemberPropertyWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)userUuid param:(NSDictionary *)param classType:(Class)classType property:(NSString *)property success:(void (^ _Nullable) (id objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;


//+ (void)removeStreamWithRoomUuid:(NSString *)roomUuid userUuid:(NSString *)tagetUserUuid userToken:(NSString *)userToken streamUuid:(NSString *)streamUuid param:(NSDictionary *)param apiVersion:(NSString *)apiVersion analysisClass:(Class)classType success:(void (^ _Nullable) (id<BaseModel> objModel))successBlock failure:(void (^ _Nullable) (NSError * _Nullable error, NSInteger statusCode))failureBlock;

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
@end

NS_ASSUME_NONNULL_END
