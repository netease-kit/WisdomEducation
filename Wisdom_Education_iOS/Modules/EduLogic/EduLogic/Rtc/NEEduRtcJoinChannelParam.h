//
//  NEEduRtcJoinChannelParam.h
//  EduLogic
//
//  Created by Groot on 2021/5/20.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

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
