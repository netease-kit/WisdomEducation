//
//  NEAppInfo.m
//  AFNetworking
//
//  Created by Groot on 2021/7/14.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEAppInfo.h"

@implementation NEAppInfo
/// App version
+ (NSString *)appVersion {
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    return [infoDictionary objectForKey:@"CFBundleShortVersionString"];
}

/// build version
+ (NSString *)buildVersion {
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    return [infoDictionary objectForKey:@"CFBundleVersion"];
}
@end
