//
//  NEEduSignalUserIn.m
//  EduLogic
//
//  Created by Groot on 2021/5/27.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduSignalUserIn.h"

@implementation NEEduSignalUserIn
+ (nullable NSDictionary<NSString *, id> *)modelContainerPropertyGenericClass {
    return @{@"members":[NEEduHttpUser class]};
}
@end
