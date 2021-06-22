//
//  NERtcCallKitLog.m
//  NERtcCallKit
//
//  Created by Wenchao Ding on 2021/4/26.
//  Copyright © 2021 Wenchao Ding. All rights reserved.
//

#import "NEEduLog.h"

@implementation NEEduLog

+ (void)load {
    YXAlogOptions *options = [[YXAlogOptions alloc] init];
    options.path = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).lastObject stringByAppendingPathComponent:@"EduKit/Log"];
    options.moduleName = @"EduKit";
    options.level = YXAlogLevelInfo;
    options.filePrefix = @"Log";
    [YXAlog.shared setupWithOptions:options];
}

@end
