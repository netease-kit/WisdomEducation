//
//  NEEduRoomStates.h
//  EduLogic
//
//  Created by Groot on 2021/6/6.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduLessonStep.h"
#import "NEEduPropertyItem.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduRoomStates : NSObject
@property (nonatomic , strong) NEEduPropertyItem              * muteChat;
@property (nonatomic , strong) NEEduLessonStep              * step;
@property (nonatomic , strong) NEEduPropertyItem              * muteAll;

@property (nonatomic , strong) NEEduPropertyItem              * pause;
@property (nonatomic , strong) NEEduPropertyItem              * muteVideo;
@property (nonatomic , strong) NEEduPropertyItem              * muteAudio;

@end

NS_ASSUME_NONNULL_END
