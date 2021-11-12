//
//  NERtcCallKitLog.m
//  NERtcCallKit
//
//  Created by Wenchao Ding on 2021/4/26.
//  Copyright © 2021 Wenchao Ding. All rights reserved.
//

#import "NERtcCallKitLog.h"

@implementation NERtcCallKitLog

+ (void)load {
    YXAlogOptions *options = [[YXAlogOptions alloc] init];
    options.path = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).lastObject stringByAppendingPathComponent:@"NEEdu/Log"];
    options.moduleName = @"CallKit";
    options.level = YXAlogLevelInfo;
    options.filePrefix = @"Log";
    [YXAlog.shared setupWithOptions:options];
}

@end
