//
//  NEDeviceAuth.h
//  YXEducation
//
//  Created by Groot on 2021/5/7.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEDeviceAuth : NSObject

/// 查询麦克风授权
+ (BOOL)hasAudioAuthoriztion;

/// 请求麦克风权限
/// @param completion 结果
+ (void)requestAudioAuthorization:(void(^)(BOOL granted))completion;

/// 查询相机授权
+ (BOOL)hasCameraAuthorization;

/// 请求相机权限
/// @param completion 结果
+ (void)requestCameraAuthorization:(void(^)(BOOL granted))completion;

/// 查询相册授权
+ (BOOL)hasPhotoAuthorization;

/// 请求相册权限
/// @param completion 结果
+ (void)requestPhotoAuthorization:(void(^)(BOOL granted))completion;

@end

NS_ASSUME_NONNULL_END
