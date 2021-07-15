//
//  NEEduRemoveProperty.h
//  EduLogic
//
//  Created by Groot on 2021/6/21.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduHttpUser.h"
NS_ASSUME_NONNULL_BEGIN

@interface NEEduRemoveProperty : NSObject
@property (nonatomic, strong) NSString *key;
@property (nonatomic, strong) NEEduHttpUser *member;

@end

NS_ASSUME_NONNULL_END
