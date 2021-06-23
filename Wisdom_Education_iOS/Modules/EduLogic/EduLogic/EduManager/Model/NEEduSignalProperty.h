//
//  NEEduSignalProperty.h
//  EduLogic
//
//  Created by Groot on 2021/6/2.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduSignalBaseModel.h"
#import "NEEduSignalStreamAV.h"
#import "NEEduWhiteboardProperty.h"
#import "NEEduHandsupProperty.h"

NS_ASSUME_NONNULL_BEGIN

@interface Properties :NSObject
@property (nonatomic, strong) NEEduSignalStreamAV *streamAV;
@property (nonatomic, strong) NEEduPropertyItem *screenShare;
@property (nonatomic, strong) NEEduWhiteboardProperty *whiteboard;
@property (nonatomic, strong) NEEduHandsupProperty *avHandsUp;

@end

@interface NEEduSignalProperty : NEEduSignalBaseModel
@property (nonatomic, strong) Properties *properties;
@end

NS_ASSUME_NONNULL_END
