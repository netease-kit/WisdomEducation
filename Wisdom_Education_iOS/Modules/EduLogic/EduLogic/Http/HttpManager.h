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
#import "NEEduSeatUserAction.h"


NS_ASSUME_NONNULL_BEGIN

@interface HttpManagerConfig : NSObject
@property(nonatomic, strong) NSString *baseURL;
/// 版本号
@property(nonatomic, strong) NSString *version;
@property(nonatomic, strong) NSString *appKey;
/// 授权
@property(nonatomic, strong) NSString *authorization;
/// 设备ID
@property(nonatomic, strong) NSString *deviceId;

@end

/// 成功回调
typedef void(^NEEduSuccessBlock)(id _Nullable objModel);
/// 失败回调
typedef void(^NEEduFailureBlock)(NSError * _Nullable error, NSInteger statusCode);

@interface HttpManager : NSObject

+ (HttpManagerConfig *)getHttpManagerConfig;
+ (void)setupHttpManagerConfig:(HttpManagerConfig *)httpConfig;
+ (void)setErrorBlock:(void(^)(NSInteger))errorBlock;
+ (NSMutableDictionary *)httpHeader;
/// 添加请求头内容
/// @param dictionary 添加内容
+ (void)addHeaderFromDictionary:(NSDictionary *)dictionary;
+ (NSError *)errorWithErrorCode:(NSInteger)code;
/// 匿名登录，已设备ID作为唯一表示
/// @param classType 返回参数类型
/// @param successBlock 成功回调
/// @param failureBlock 失败回调
+ (void)loginWithAnalysisClass:(Class)classType
                       success:(_Nullable NEEduSuccessBlock)successBlock
                       failure:(_Nullable NEEduFailureBlock)failureBlock;

/// 用户登录，已用户ID作为唯一表示
/// @param userId    用户ID
/// @param token     用户token
/// @param classType 返回参数类型
/// @param successBlock 成功回调
/// @param failureBlock 失败回调
+ (void)loginWithUserId:(NSString *)userId
                  token:(NSString *)token
          analysisClass:(Class)classType
                success:(_Nullable NEEduSuccessBlock)successBlock
                failure:(_Nullable NEEduFailureBlock)failureBlock;

+ (void)createRoom:(NSString *)roomUuid
             param:(NSDictionary *)param
         classType:(Class)classType
           success:(_Nullable NEEduSuccessBlock)successBlock
           failure:(_Nullable NEEduFailureBlock)failureBlock;

/// 获取房间信息
/// @param roomUuid 房间ID
/// @param param 参数
/// @param classType 返回数据类型
/// @param successBlock 成功回调
/// @param failureBlock 失败回调
+ (void)getRoom:(NSString *)roomUuid
          param:(NSDictionary * _Nullable)param
      classType:(Class)classType
        success:(_Nullable NEEduSuccessBlock)successBlock
        failure:(_Nullable NEEduFailureBlock)failureBlock;

+ (void)enterRoom:(NSString *)roomUuid
            param:(NSDictionary *)param
        classType:(Class)classType
          success:(_Nullable NEEduSuccessBlock)successBlock
          failure:(_Nullable NEEduFailureBlock)failureBlock;

+ (void)getRoomProfile:(NSString *)roomUuid
             classType:(Class)classType
               success:(void (^ _Nullable) (id objModel ,NSInteger ts))successBlock
               failure:(_Nullable NEEduFailureBlock)failureBlock;

+ (void)updateStreamStateWithRoomUuid:(NSString *)roomUuid
                             userUuid:(NSString *)userUuid
                                param:(NSDictionary *)param
                            classType:(Class)classType
                           streamType:(NSString *)streamType
                              success:(_Nullable NEEduSuccessBlock)successBlock
                              failure:(_Nullable NEEduFailureBlock)failureBlock;
//修改用户属性，教师通知学生打开音视频
+ (void)updateMemberPropertyWithRoomUuid:(NSString *)roomUuid
                                userUuid:(NSString *)userUuid
                                   param:(NSDictionary *)param
                               classType:(Class)classType
                                property:(NSString *)property
                                 success:(_Nullable NEEduSuccessBlock)successBlock
                                 failure:(_Nullable NEEduFailureBlock)failureBlock;

+ (void)deleteMemberPropertyWithRoomUuid:(NSString *)roomUuid
                                userUuid:(NSString *)userUuid
                                   param:(NSDictionary *)param
                               classType:(Class)classType
                                property:(NSString *)property
                                 success:(_Nullable NEEduSuccessBlock)successBlock
                                 failure:(_Nullable NEEduFailureBlock)failureBlock;


+ (void)startLessonWithRoomUuid:(NSString *)roomUuid
                          param:(NSDictionary *)param
                      classType:(Class)classType
                        success:(_Nullable NEEduSuccessBlock)successBlock
                        failure:(_Nullable NEEduFailureBlock)failureBlock;

+ (void)muteAllWithRoomUuid:(NSString *)roomUuid
                      param:(NSDictionary *)param
                  classType:(Class)classType
                    success:(_Nullable NEEduSuccessBlock)successBlock
                    failure:(_Nullable NEEduFailureBlock)failureBlock;

+ (void)muteAllTextWithRoomUuid:(NSString *)roomUuid
                          param:(NSDictionary *)param
                      classType:(Class)classType
                        success:(_Nullable NEEduSuccessBlock)successBlock
                        failure:(_Nullable NEEduFailureBlock)failureBlock;

/// 离开房间
+ (void)leaveRoomWithRoomUuid:(NSString *)roomUuid
                    userUuid:(NSString *)userUuid
            success:(void (^ _Nullable)(void))successBlock
            failure:(_Nullable NEEduFailureBlock)failureBlock;

/// 获取丢失的长链接消息
/// @param roomUuid 房间ID
/// @param nextId 消息senquence
/// @param classType 返回数据类型
/// @param successBlock 成功回调
/// @param failureBlock 失败回调
+ (void)getMessageWithRoomUuid:(NSString *)roomUuid
                        nextId:(NSInteger)nextId
                     classType:(Class)classType
                       success:(_Nullable NEEduSuccessBlock)successBlock
                       failure:(_Nullable NEEduFailureBlock)failureBlock;

/// 获取回放地址
/// @param roomUuid 房间ID
/// @param rtcCid Rtc Channel Id
/// @param classType 返回数据类型
/// @param successBlock 成功回调
/// @param failureBlock 失败回调
+ (void)getRecords:(NSString *)roomUuid
            rtcCid:(NSInteger)rtcCid
         classType:(Class)classType
           success:(_Nullable NEEduSuccessBlock)successBlock
           failure:(_Nullable NEEduFailureBlock)failureBlock;

/// 用户麦位操作
+ (void)userSeatOperation:(NSString *)roomUuid
                 userName:(NSString *)userName
               action:(NEEduSeatUserAction *)action
                  success:(void (^ _Nullable)(void))success
                  failure:(_Nullable NEEduFailureBlock)failure;

/// 获取麦位信息
+ (void)getSeatInfo:(NSString *)roomUuid
          classType:(Class)classType
            success:(_Nonnull NEEduSuccessBlock)success
            failure:(_Nullable NEEduFailureBlock)failure;
/// 获取麦位请求列表
+ (void)getSeatRequestList:(NSString *)roomUuid
            classType:(Class)classType
            success:(_Nullable NEEduSuccessBlock)success
            failure:(_Nullable NEEduFailureBlock)failure;
@end

NS_ASSUME_NONNULL_END
