// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import "NEEduSeatItemChangeEvent.h"
@implementation NEEduSeatItemDetail
+ (NSDictionary<NSString *,id> *)modelContainerPropertyGenericClass {
    return @{
        @"seatList": NEEduSeatItem.class
    };
}
@end

@implementation NEEduSeatItemChangeEvent

@end
