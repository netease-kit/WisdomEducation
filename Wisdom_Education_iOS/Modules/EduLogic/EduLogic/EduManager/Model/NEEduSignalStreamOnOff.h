//
//  NEEduSignalStreamOnOff.h
//  EduLogic
//
//  Created by Groot on 2021/5/27.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduPropertyItem.h"
#import "NEEduSignalBaseModel.h"

NS_ASSUME_NONNULL_BEGIN


@interface NEEduSignalStreamOnOff : NEEduSignalBaseModel
@property (nonatomic , strong) NEEduStreams              * streams;

@end

NS_ASSUME_NONNULL_END
