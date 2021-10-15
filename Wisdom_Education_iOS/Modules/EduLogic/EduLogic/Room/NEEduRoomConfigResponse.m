//
//  NEEduRoomConfigResponse.m
//  EduLogic
//
//  Created by 郭园园 on 2021/10/13.
//

#import "NEEduRoomConfigResponse.h"
@interface NEEduRoomConfigResponse ()
@property (nonatomic, strong) NSString *sceneType;
@end

@implementation NEEduRoomConfigResponse

- (BOOL)isLiveClass {
    return [self.sceneType isEqualToString:@"EDU.LIVE_SIMPLE"];
}
- (void)setSceneType:(NSString *)sceneType {
    NSLog(@"sceneType:%@",sceneType);
    _sceneType = sceneType;
}
@end
