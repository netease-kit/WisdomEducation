//
//  NEDeviceAuth.m
//  YXEducation
//
//  Created by Groot on 2021/5/7.
//  Copyright © 2021 Netease. All rights reserved.
//

#import "NEDeviceAuth.h"
#import <UIKit/UIKit.h>
#import <Photos/Photos.h>
#import <AssetsLibrary/AssetsLibrary.h>
#ifdef NSFoundationVersionNumber_iOS_9_x_Max
#import <UserNotifications/UserNotifications.h>
#endif
@implementation NEDeviceAuth
+ (BOOL)hasAudioAuthoriztion {
    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeAudio];
    if (authStatus == AVAuthorizationStatusDenied || authStatus == AVAuthorizationStatusRestricted) {
        return NO;
    }
    return YES;
}
+ (BOOL)hasCameraAuthorization {
    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if (authStatus == AVAuthorizationStatusDenied || authStatus == AVAuthorizationStatusRestricted) {
        return NO;
    }
    return YES;
}
+ (void)requestAudioAuthorization:(void(^)(BOOL granted))completion {
    AVAudioSession *session = [AVAudioSession sharedInstance];
    if ([session respondsToSelector:@selector(requestRecordPermission:)]){
        [session performSelector:@selector(requestRecordPermission:) withObject:^(BOOL granted) {
            if (completion) {
                completion(granted);
            }
        }];
    }
}
+ (BOOL)hasPhotoAuthorization {
    PHAuthorizationStatus authStatus = [PHPhotoLibrary authorizationStatus];
    if(authStatus == PHAuthorizationStatusDenied || authStatus == PHAuthorizationStatusRestricted) {
        return NO;
    }
    return YES;
}
+ (void)requestPhotoAuthorization:(void(^)(BOOL granted))completion {
    BOOL auth = [UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary];
    if (!auth) {
        if (completion) {
            completion(auth);
        }
    }else {
        [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) { //弹出访问权限提示框
            dispatch_async(dispatch_get_main_queue(),^{ // 无权限
                if (completion) {
                    completion(status == PHAuthorizationStatusAuthorized);
                }
            });
        }];
    }
}
/**
 判断相机权限开关,会弹出是否允许弹出权限
 (需要在info中配置)Privacy - Camera Usage Description 允许**访问您的相机,来用于**功能
 */
+ (void)requestCameraAuthorization:(void(^)(BOOL granted))completion {
    BOOL auth = [UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera];
    if (!auth){
        if (completion) {
            completion(NO);
        }
    }else {
        NSString *mediaType = AVMediaTypeVideo;//读取媒体类型
        [AVCaptureDevice requestAccessForMediaType:mediaType completionHandler:^(BOOL granted) {
            dispatch_async(dispatch_get_main_queue(),^{
                if (completion) {
                    completion(granted);
                }
            });
        }];
    }
}

+ (void)hasNotificationAuthorization:(void(^)(BOOL granted))completion {
    if (@available(iOS 10.0, *)) {
        [[UNUserNotificationCenter currentNotificationCenter] getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
            if (completion) {
                completion(settings.authorizationStatus == UNAuthorizationStatusAuthorized);
            }
        }];
    } else {
        UIUserNotificationSettings *setting = [[UIApplication sharedApplication] currentUserNotificationSettings];
        if (completion) {
            completion(UIUserNotificationTypeNone != setting.types);
        }
    }
}

/**
 判断通知权限开关,会弹出是否允许弹出权限(远程、本地)
 */
+ (void)requestNotificationAuthorization:(void(^)(BOOL granted,NSError *error))completion {
    [[UNUserNotificationCenter   currentNotificationCenter] requestAuthorizationWithOptions:(UNAuthorizationOptionBadge | UNAuthorizationOptionSound | UNAuthorizationOptionAlert) completionHandler:^(BOOL granted, NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (completion) {
                completion(granted,error);
            }
        });
    }];
}


@end
