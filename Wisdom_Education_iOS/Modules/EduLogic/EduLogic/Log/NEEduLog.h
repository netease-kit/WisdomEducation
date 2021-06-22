//
//  NERtcCallKitLog.h
//  NERtcCallKit
//
//  Created by Wenchao Ding on 2021/4/26.
//  Copyright © 2021 Wenchao Ding. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <YXAlog_iOS/YXAlog.h>

#ifndef NEduLogInfo
#define NEduLogInfo YXAlogInfo
#define NEduLogError YXAlogError
#define NEduLogFlush() YXAlogFlushAsync()
#endif

NS_ASSUME_NONNULL_BEGIN

@interface NEEduLog : NSObject


@end

NS_ASSUME_NONNULL_END
