//
//  NEEduRtcJoinChannelParam.h
//  EduLogic
//
//  Created by Groot on 2021/5/20.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// RTC 加入房间的参数
@interface NEEduRtcJoinChannelParam : NSObject
@property (nonatomic, strong) NSString *channelID;
@property (nonatomic, strong) NSString *rtcToken;
@property (nonatomic, assign) UInt64 userID;
/// default YES
@property (nonatomic, assign) BOOL subscribeVideo;
/// default YES
@property (nonatomic, assign) BOOL subscribeAudio;
@end

NS_ASSUME_NONNULL_END
