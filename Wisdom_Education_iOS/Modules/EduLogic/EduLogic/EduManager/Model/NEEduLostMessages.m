//
//  NEEduLostMessages.m
//  EduLogic
//
//  Created by Groot on 2021/6/17.
//

#import "NEEduLostMessages.h"

@implementation NEEduLostMessages

+ (NSDictionary *)modelContainerPropertyGenericClass {
    return @{@"list" : [NEEduSignalMessage class]};
}
@end
