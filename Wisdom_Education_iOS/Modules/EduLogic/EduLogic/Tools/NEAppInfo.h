//
//  NEAppInfo.h
//  AFNetworking
//
//  Created by Groot on 2021/7/14.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEAppInfo : NSObject

/// App version
+ (NSString *)appVersion;

/// build version
+ (NSString *)buildVersion;

@end

NS_ASSUME_NONNULL_END
