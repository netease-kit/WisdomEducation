//
//  NEEduEnterRoomParam.m
//  EduLogic
//
//  Created by Groot on 2021/5/20.
//

#import "NEEduEnterRoomParam.h"

@implementation NEEduEnterRoomParam
- (instancetype)init
{
    self = [super init];
    if (self) {
        self.role = NEEduRoleTypeStudent;
        self.userName = @"yuanyuan";
    }
    return self;
}
@end
