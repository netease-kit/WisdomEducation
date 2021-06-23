//
//  NEEduSignalBaseModel.h
//  EduLogic
//
//  Created by Groot on 2021/6/2.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduHttpUser.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduSignalBaseModel : NSObject
@property (nonatomic , copy) NSString              * appId;
@property (nonatomic , strong) NEEduHttpUser              * member;
@property (nonatomic , copy) NSString              * roomUuid;
@property (nonatomic , strong) NEEduHttpUser              * operatorMember;
@property (nonatomic, strong) NSString *streamType;
@end

NS_ASSUME_NONNULL_END
