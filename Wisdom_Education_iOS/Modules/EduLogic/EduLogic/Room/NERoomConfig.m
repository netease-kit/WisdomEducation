//
//  NERoomConfig.m
//  EduLogic
//
//  Created by 郭园园 on 2021/9/2.
//

#import "NERoomConfig.h"
@implementation Resource

@end

@implementation NERoomConfig

- (Resource *)resource {
    if (!_resource) {
        _resource = [[Resource alloc] init];
        _resource.live = NO;
        _resource.rtc = YES;
        _resource.chatroom = YES;
        _resource.whiteboard = YES;
    }
    return _resource;
}
@end
