//
//  NERtcCallKitLog.h
//  NERtcCallKit
//
//  Created by Wenchao Ding on 2021/4/26.
//  Copyright © 2021 Wenchao Ding. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <YXAlog_iOS/YXAlog.h>

#ifndef NCKLogInfo
#define NCKLogInfo YXAlogInfo
#define NCKLogError YXAlogError
#define NCKLogFlush() YXAlogFlushAsync()
#endif

NS_ASSUME_NONNULL_BEGIN

@interface NERtcCallKitLog : NSObject


@end

NS_ASSUME_NONNULL_END
