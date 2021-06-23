//
//  NEEduUserProperty.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduPropertyItem.h"
#import "NEEduSignalStreamAV.h"
#import "NEEduHandsupProperty.h"
#import "NEEduWhiteboardProperty.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduUserProperty : NSObject
@property (nonatomic , strong) NEEduPropertyItem              * screenShare;
@property (nonatomic , strong) NEEduWhiteboardProperty              * whiteboard;
@property (nonatomic , strong) NEEduSignalStreamAV            *streamAV;
@property (nonatomic, strong) NEEduHandsupProperty            *avHandsUp;

@end

NS_ASSUME_NONNULL_END
