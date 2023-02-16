// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import "NEEduSeatUserAction.h"

@implementation NEEduSeatUserAction
- (instancetype)init {
    self = [super init];
    if (self) {
        self.lockIndex = NO;
        self.seatIndex = -1;
    }
    return self;
}
+ (instancetype)action:(NEEduSeatUserActionType)actionType {
    NEEduSeatUserAction *action = [self new];
    action.action = actionType;
    return action;
}
@end
