//
//  NEEduInitOption.h
//  EduLogic
//
//  Created by Groot on 2021/5/13.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduKitOptions : NSObject
@property (nonatomic, copy) NSString *authorization;
@property (nullable, nonatomic,copy) NSString *baseURL;

@end

NS_ASSUME_NONNULL_END
