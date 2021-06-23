//
//  NEEduStreams.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduPropertyItem.h"
NS_ASSUME_NONNULL_BEGIN

@interface NEEduStreams : NSObject
@property (nonatomic , strong) NEEduPropertyItem              * audio;
@property (nonatomic , strong) NEEduPropertyItem              * video;
@property (nonatomic , strong) NEEduPropertyItem              * subVideo;

@end

NS_ASSUME_NONNULL_END
