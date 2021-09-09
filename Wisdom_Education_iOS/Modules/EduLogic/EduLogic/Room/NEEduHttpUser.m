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

@implementation NEWebAuth

@end

@implementation NEEduHttpUser
- (instancetype)initWithRole:(NSString *)role
{
    self = [super init];
    if (self) {
        self.role = role;
    }
    return self;
}

+ (instancetype)teacher {
    return [[NEEduHttpUser alloc] initWithRole:NEEduRoleHost];
}

+ (instancetype)student {
    return [[NEEduHttpUser alloc] initWithRole:NEEduRoleBroadcaster];
}

- (BOOL)isTeacher {
    if ([self.role isEqualToString:NEEduRoleHost]) {
        return YES;
    }
    return NO;
}
@end
