//
//  NEEduSignalStreamAV.h
//  EduLogic
//
//  Created by Groot on 2021/6/2.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduSignalStreamAV : NSObject
/// 表示授权
@property (nonatomic , strong) NSNumber *            value;
/// 授权关闭/打开音频消息
@property (nonatomic , strong) NSNumber *            audio;
/// 授权关闭/打开音频消息
@property (nonatomic , strong) NSNumber *            video;

@end

NS_ASSUME_NONNULL_END
