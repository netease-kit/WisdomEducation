//
//  NEEduScreenShareHandler.m
//  WisdomEducationBroadcast
//
//  Created by Groot on 2021/5/31.
//

#import "NEEduScreenShareHandler.h"

@interface NEEduScreenShareHandler()

@end

static NSString *kAppGroup = @"group.com.netease.yunxin.app.wisdom.education";

@implementation NEEduScreenShareHandler

- (void)setupWithOptions:(NEScreenShareBroadcasterOptions *)options {
    options.appGroup = kAppGroup;
    options.frameRate = 10;
    options.targetFrameSize = CGSizeMake(720, 0);
}

//- (void)processSampleBuffer:(CMSampleBufferRef)sampleBuffer withType:(RPSampleBufferType)sampleBufferType {
//    NSLog(@"EDU: %s", __FUNCTION__);
//    [super processSampleBuffer:sampleBuffer withType:sampleBufferType];
//}

@end
