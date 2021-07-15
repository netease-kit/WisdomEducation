//
//  NEEduHttpUser.m
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduHttpUser.h"

NSString * const NEEduRoleHost              = @"host";
NSString * const NEEduRoleBroadcaster       = @"broadcaster";
NSString * const NEEduRoleAudience          = @"audience";

@implementation NEEduHttpUser
- (void)setRole:(NSString *)role {
    _role = role;
    NSLog(@"%s  role:%@",__func__,role);
}
- (BOOL)isTeacher {
    if ([self.role isEqualToString:@"host"]) {
        return YES;
    }
    return NO;
}
@end
