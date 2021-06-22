//
//  NEEduRtcJoinChannelParam.m
//  EduLogic
//
//  Created by Groot on 2021/5/20.
//

#import "NEEduRtcJoinChannelParam.h"

@implementation NEEduRtcJoinChannelParam
- (instancetype)init
{
    self = [super init];
    if (self) {
        self.subscribeAudio = YES;
        self.subscribeVideo = YES;
    }
    return self;
}
@end
