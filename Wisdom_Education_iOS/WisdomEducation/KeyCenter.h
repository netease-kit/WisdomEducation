//
//  KeyCenter.h
//  WisdomEducation
//
//  Created by Groot on 2021/5/17.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface KeyCenter : NSObject
/// 用户申请的appKey（必填）
+ (NSString *)appKey;

/// 用户申请的鉴权码（必填）
+ (NSString *)authorization;

/// 服务器地址、私有化部署需要替换为私有化配置（可选）
+ (NSString *)baseURL;

@end

NS_ASSUME_NONNULL_END
