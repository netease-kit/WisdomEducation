//
//  NEEduMember.h
//  AFNetworking
//
//  Created by Groot on 2021/5/27.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduMember : NSObject
@property (nonatomic, strong) NSString *name;
@property (nonatomic, strong) NSString *userID;
// 是否有白板编辑权限
@property (nonatomic, assign) BOOL whiteboardEnable;

// 是否有屏幕共享权限
@property (nonatomic, assign) BOOL shareScreenEnable;
/// 视频按钮是否可用
@property (nonatomic, assign) BOOL videoEnable;
/// 音频按钮是否可用
@property (nonatomic, assign) BOOL audioEnable;
/// 视频是否开启
@property (nonatomic, assign) BOOL hasVideo;
/// 音频是否开启
@property (nonatomic, assign) BOOL hasAudio;

@property (nonatomic, assign) BOOL online;
@property (nonatomic, assign) BOOL showMoreButton;
//大班课仅全部成员仅显示名字
@property (nonatomic, assign) BOOL isBigClass;
/// 是否在全部成员列表
@property (nonatomic, assign) BOOL isInAllList;



@end

NS_ASSUME_NONNULL_END
