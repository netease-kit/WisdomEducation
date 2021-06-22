//
//  EduManager.h
//  EduLogic
//
//  Created by Groot on 2021/5/13.
//

#import <Foundation/Foundation.h>
#import "NEEduKitOptions.h"
#import "NEEduUser.h"
#import "NEEduRoomProfile.h"
#import "NEEduEnterRoomParam.h"
#import "NEEduRoomService.h"
#import "NEEduMessageService.h"
#import "NEEduIMService.h"
#import "NEEduVideoService.h"
#import "NEEduUserService.h"

NS_ASSUME_NONNULL_BEGIN

@interface EduManager : NSObject
@property (nonatomic, strong) NEEduIMService *imService;
@property (nonatomic, strong) NEEduVideoService *videoService;

@property (nonatomic, strong) NEEduRoomService *roomService;
@property (nonatomic, strong) NEEduMessageService *messageService;
@property (nonatomic, strong) NEEduUserService *userService;

@property (nonatomic, strong) NEEduRoomProfile *profile;
@property (nonatomic, strong ,readonly) NEEduUser *localUser;
@property (nonatomic, strong ,readonly) NSString *appKey;

+ (instancetype)shared;
- (void)setupAppkey:(NSString * _Nonnull)appKey options:(NEEduKitOptions * )options;
/// 登录
/// @param userID 用户ID，为nil则匿名登录
/// @param success 成功
/// @param failure 失败
- (void)login:(NSString * _Nullable)userID success:(void(^)(NEEduUser *user))success failure:(void(^)(NSError *error))failure;

/// 加入房间
/// @param roomOption 参数
/// @param success 成功
/// @param failure 失败
- (void)enterClassroom:(NEEduEnterRoomParam *)roomOption success:(void(^)(NEEduRoomProfile *roomProfile))success failure:(void(^)(NSError *error))failure;

/// 设置画布
/// @param view 视频渲染View
/// @param member 用户信息
- (void)setCanvasView:(UIView *)view forMember:(NEEduHttpUser *)member;

- (void)leaveClassroom;
- (void)destoryClassroom;

@end

NS_ASSUME_NONNULL_END

