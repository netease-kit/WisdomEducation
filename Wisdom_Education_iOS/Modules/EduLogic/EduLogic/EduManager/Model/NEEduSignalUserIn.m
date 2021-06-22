//
//  NEEduSignalUserIn.m
//  EduLogic
//
//  Created by Groot on 2021/5/27.
//

#import "NEEduSignalUserIn.h"

@implementation NEEduSignalUserIn
+ (nullable NSDictionary<NSString *, id> *)modelContainerPropertyGenericClass {
    return @{@"members":[NEEduHttpUser class]};
}
@end
